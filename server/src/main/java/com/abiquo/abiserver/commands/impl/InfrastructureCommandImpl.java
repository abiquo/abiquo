/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.abiserver.commands.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.abicloudws.IInfrastructureWS;
import com.abiquo.abiserver.abicloudws.InfrastructureWS;
import com.abiquo.abiserver.abicloudws.RemoteServiceUtils;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.InfrastructureCommand;
import com.abiquo.abiserver.commands.RemoteServicesCommand;
import com.abiquo.abiserver.eventing.EventingException;
import com.abiquo.abiserver.eventing.EventingSupport;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.InvalidIPAddressException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.DatastoreDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.HyperVisorDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.RackDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.dao.workload.FitPolicyRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.MachineLoadRuleDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.infrastructure.InfrastructureElement;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.networking.VlanNetworkParameters;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.util.AbiCloudError;
import com.abiquo.util.resources.ResourceManager;

/**
 * This command collects all actions related to Infrastructure
 * 
 * @author Oliver
 */

public class InfrastructureCommandImpl extends BasicCommand implements InfrastructureCommand
{

    /**
     * DAOFactory to create DAOs
     */
    private DAOFactory factory;

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(InfrastructureCommandImpl.class);

    protected static ResourceManager resourceManager =
        new ResourceManager(InfrastructureCommandImpl.class);

    private IInfrastructureWS infrastructureWS;

    // TODO autowire
    private RemoteServicesCommand rsCommand = new RemoteServicesCommandImpl();

    public InfrastructureCommandImpl()
    {
        factory = HibernateDAOFactory.instance();

        try
        {
            infrastructureWS =
                (IInfrastructureWS) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.abicloudws.InfrastructureWSPremium").newInstance();
        }
        catch (Exception e)
        {
            infrastructureWS = new InfrastructureWS();
        }
    }

    /*
     * ______________________________ DATA CENTER _______________________________
     */

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#getInfrastructureByDataCenter(com.abiquo
     * .abiserver.pojo.infrastructure.DataCenter)
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public DataResult<ArrayList<InfrastructureElement>> getInfrastructureByDataCenter(
        final DataCenter dataCenter)
    {

        DataResult<ArrayList<InfrastructureElement>> dataResult =
            new DataResult<ArrayList<InfrastructureElement>>();
        ArrayList<InfrastructureElement> infrastructures = null;
        DatacenterHB datacenterPojo = null;

        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try
        {
            transaction = session.beginTransaction();
            infrastructures = new ArrayList<InfrastructureElement>();
            datacenterPojo = (DatacenterHB) session.get(DatacenterHB.class, dataCenter.getId());

            // Adding the racks
            Set<RackHB> racks = datacenterPojo.getRacks();
            for (RackHB rackPojo : racks)
            {
                Rack rack = rackPojo.toPojo();
                rack.setDataCenter(dataCenter);
                // Adding to the infrastructure list
                infrastructures.add(rack);

                // Adding the physicalMachines
                Set<PhysicalmachineHB> phyMachines = rackPojo.getPhysicalmachines();
                for (PhysicalmachineHB phyMachinePojo : phyMachines)
                {
                    PhysicalMachine phyMachine = phyMachinePojo.toPojo();
                    phyMachine.setAssignedTo(rack);
                    infrastructures.add(phyMachine);

                    // Adding the HyperVisor
                    HypervisorHB hypervisorPojo = phyMachinePojo.getHypervisor();
                    HyperVisor hypervisor = hypervisorPojo.toPojo();
                    hypervisor.setAssignedTo(phyMachine);
                    infrastructures.add(hypervisor);

                    // Adding the VirtualMachines
                    Set<VirtualmachineHB> virtualMachines = hypervisorPojo.getVirtualmachines();
                    for (VirtualmachineHB virtualMachinePojo : virtualMachines)
                    {
                        VirtualMachine virtualMachine = virtualMachinePojo.toPojo();
                        virtualMachine.setAssignedTo(hypervisor);
                        infrastructures.add(virtualMachine);
                    }

                }
            }

            // Adding the physical machines in this Data Center, without a rack
            Conjunction conjunction = Restrictions.conjunction();
            conjunction.add(Restrictions.isNull("rack"));
            conjunction.add(Restrictions.eq("dataCenter", datacenterPojo));
            ArrayList<PhysicalmachineHB> physicalMachinesWORack =
                (ArrayList<PhysicalmachineHB>) session.createCriteria(PhysicalmachineHB.class).add(
                    conjunction).list();
            for (PhysicalmachineHB physicalMachineHB : physicalMachinesWORack)
            {
                infrastructures.add(physicalMachineHB.toPojo());
            }

            // We are done!
            transaction.commit();

            dataResult.setSuccess(true);
            dataResult.setData(infrastructures);
        }
        catch (HibernateException e)
        {
            if (transaction != null)
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "getInfrastructureByDataCenter", e);
        }

        return dataResult;
    }

    @Override
    public ArrayList<RackHB> getRacksByDatacenter(final UserSession userSession,
        final Integer datacenterId, final String filters)
    {
        ArrayList<RackHB> racks = new ArrayList<RackHB>();

        factory.beginConnection();

        DataCenterDAO dcDAO = factory.getDataCenterDAO();
        racks = dcDAO.getRacks(datacenterId, filters);

        factory.endConnection();
        return racks;
    }

    /**
     * Gets the physical machine list by rack
     * 
     * @param rackId the rack identifier
     * @return the list of physical machine
     * @throws PersistenceException
     * @throws InfrastructureCommandException
     * @see com.abiquo.abiserver.commands.InfrastructureCommand#getPhysicalMachinesByRack(java.lang.Integer)
     */
    public List<PhysicalMachine> getPhysicalMachinesByRack(UserSession userSession,
        Integer rackId, String filters)
    {
        factory.beginConnection();

        PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
        List<PhysicalmachineHB> availablePms = pmDAO.getPhysicalMachineByRack(rackId, filters);
        List<PhysicalMachine> result = new ArrayList<PhysicalMachine>();
        for (PhysicalmachineHB singleResult : availablePms)
        {
            result.add(singleResult.toPojo());
        }
        
        factory.endConnection();

        return result;
    }

    @Override
    public List<VirtualmachineHB> getVirtualMachinesByPhysicalMachine(
        final UserSession userSession, final Integer physicalMachineId)
    {
        factory.beginConnection();

        PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
        List<VirtualmachineHB> vms = pmDAO.getDeployedVirtualMachines(physicalMachineId);

        factory.endConnection();

        return vms;
    }

    @Override
    public HypervisorHB getHypervisorByPhysicalMachine(final UserSession userSession,
        final Integer physicalMachineId)
    {
        factory.beginConnection();

        HyperVisorDAO hyperDAO = factory.getHyperVisorDAO();
        HypervisorHB hyperHB = hyperDAO.getHypervisorFromPhysicalMachine(physicalMachineId);

        factory.endConnection();

        return hyperHB;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#getAvailablePhysicalMachinesByRack(java
     * .lang.Integer)
     */
    public DataResult<ArrayList<PhysicalMachine>> getAvailablePhysicalMachinesByRack(
        Integer rackId, Integer enterpriseId)
    {
        DataResult<ArrayList<PhysicalMachine>> dataResult =
            new DataResult<ArrayList<PhysicalMachine>>();

        ArrayList<PhysicalMachine> infrastructures = null;

        factory.beginConnection();

        infrastructures = new ArrayList<PhysicalMachine>();

        PhysicalMachineDAO pmDao = factory.getPhysicalMachineDAO();

        List<PhysicalmachineHB> availablePms = pmDao.getPhysicalMachineByRack(rackId, null);

        for (PhysicalmachineHB physicalmachineHB : availablePms)
        {
            // If physical machine has no deployed machines and it has no enterprise assigned

            // If idEnterprise equals 0, its an new creation enterprise if not is editing the
            // enterprise so it should not include PM's with VM's from other enterprises
            Long numberOfVM;

            if (enterpriseId.intValue() == 0)
            {
                numberOfVM = pmDao.getNumberOfDeployedVirtualMachines(physicalmachineHB);
            }
            else
            {
                numberOfVM =
                    pmDao.getNumberOfDeployedVirtualMachinesOwnedByOtherEnterprise(
                        physicalmachineHB, enterpriseId);
            }

            if ((numberOfVM.equals(new Long(0)))
                && ((physicalmachineHB.getIdEnterprise() == null) || (physicalmachineHB
                    .getIdEnterprise() == 0)))
            {
                infrastructures.add(physicalmachineHB.toPojo());
            }
        }

        factory.endConnection();

        dataResult.setSuccess(true);
        dataResult.setData(infrastructures);

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#getDataCenters(com.abiquo.abiserver.pojo
     * .authentication.UserSession)
     */
    @SuppressWarnings("unchecked")
    public DataResult<ArrayList<DataCenter>> getDataCenters(final UserSession userSession)
    {

        DataResult<ArrayList<DataCenter>> dataResult = new DataResult<ArrayList<DataCenter>>();

        Session session = HibernateUtil.getSession();
        Transaction transaction = null;

        try
        {
            transaction = session.beginTransaction();

            ArrayList<DataCenter> dataCentersPojo = new ArrayList<DataCenter>();
            ArrayList<DatacenterHB> dataCenters =
                (ArrayList<DatacenterHB>) HibernateUtil.getSession().createCriteria(
                    DatacenterHB.class).addOrder(Order.asc("name")).list();
            for (DatacenterHB datacenterHB : dataCenters)
            {
                DataCenter dataCenter = datacenterHB.toPojo();
                dataCentersPojo.add(dataCenter);
            }

            dataResult.setData(dataCentersPojo);
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("getDataCenters.success"));

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "getDataCenters", e);
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#getAllowedDataCenters(com.abiquo.abiserver
     * .pojo.authentication.UserSession)
     */
    public DataResult<ArrayList<DataCenter>> getAllowedDataCenters(final UserSession userSession)
    {
        return getDataCenters(userSession);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#createDataCenter(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    public DataResult<DataCenter> createDataCenter(final UserSession userSession,
        final DataCenter dataCenter)
    {
        DataResult<DataCenter> dataResult;
        dataResult = new DataResult<DataCenter>();
        dataResult.setSuccess(true);

        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Checks for existing DataCenters with the same name
            try
            {
                if (checkExistingDataCenterNames(dataCenter.getName()))
                {
                    dataResult.setSuccess(false);
                    // dataResult.setMessage("Another datacenter with the name '"
                    // + dataCenter.getName()
                    // + "' already exists. Please choose a different name.");

                    errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                        "createDataCenter_existingname");

                    // Log the event
                    traceLog(SeverityType.MINOR, ComponentType.DATACENTER, EventType.DC_CREATE,
                        userSession, dataCenter, null, "Another datacenter with the name '"
                            + dataCenter.getName()
                            + "' already exists. Please choose a different name.", null, null,
                        null, null, userSession.getEnterpriseName());

                    return dataResult;
                }
            }
            catch (PersistenceException e1)
            {
                throw new HibernateException(e1.getMessage(), e1);
            }

            NetworkHB network = new NetworkHB();
            network.setUuid(UUID.randomUUID().toString());
            session.save(network);

            DatacenterHB datacenterPojo = dataCenter.toPojoHB();
            datacenterPojo.setIdDataCenter(null); // Force a creation operation
            datacenterPojo.setNetwork(network);
            final Integer idDatacenter = (Integer) session.save(datacenterPojo);

            // Creating remote Services

            ArrayList<RemoteService> remoteServices = dataCenter.getRemoteServices();
            ArrayList<RemoteService> updatedRemoteServices = new ArrayList<RemoteService>();

            if (remoteServices == null)
            {
                updatedRemoteServices = new ArrayList<RemoteService>();
            }

            boolean errorsInRemoteService = false;

            transaction.commit();

            for (RemoteService remoteService : remoteServices)
            {
                RemoteServiceHB remoteServiceHB = remoteService.toPojoHB();
                remoteServiceHB.setIdDataCenter(idDatacenter);
                remoteService.setIdDataCenter(idDatacenter);

                if (!validateRemoteService(remoteServiceHB))
                {
                    errorsInRemoteService = true;

                    traceLog(SeverityType.MINOR, ComponentType.DATACENTER, EventType.DC_CREATE,
                        userSession, dataCenter, null, remoteServiceHB.getRemoteServiceType()
                            .getName()
                            + " was not properly configured", null, null, null, null, null);
                }
                else
                {
                    DataResult<RemoteService> savedRS =
                        rsCommand.addRemoteService(userSession, remoteServiceHB.toPojo());
                    if (savedRS.getSuccess())
                    {
                        updatedRemoteServices.add(savedRS.getData());
                    }
                    else
                    {
                        errorsInRemoteService = true;

                        traceLog(SeverityType.MAJOR, ComponentType.DATACENTER, EventType.DC_CREATE,
                            userSession, dataCenter, null, remoteServiceHB.getRemoteServiceType()
                                .getName()
                                + " was not properly configured", null, null, null, null, null);
                    }
                }
            }

            dataCenter.setRemoteServices(updatedRemoteServices);
            dataCenter.setId(idDatacenter);
            dataResult.setData(dataCenter);

            if (errorsInRemoteService)
            {
                dataResult.setSuccess(false);
                dataResult
                    .setMessage("Datacenter '"
                        + dataCenter.getName()
                        + "' has been created but some Remote Services had configuration errors. Please check the events to fix the problems.");

                // Log the event
                traceLog(
                    SeverityType.MAJOR,
                    ComponentType.DATACENTER,
                    EventType.DC_CREATE,
                    userSession,
                    dataCenter,
                    null,
                    "Datacenter '"
                        + dataCenter.getName()
                        + "' has been created but some Remote Services had configuration errors. Please check the events to fix the problems.",
                    null, null, null, null, null);
            }
            else
            {
                // Log the event
                traceLog(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_CREATE,
                    userSession, dataCenter, null, "Datacenter '" + dataCenter.getName()
                        + "' has been created in " + dataCenter.getSituation(), null, null, null,
                    null, null);
            }

        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "createDataCenter", e);

            // Log the event
            traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_CREATE,
                userSession, dataCenter, null, e.getMessage(), null, null, null, null, null);
        }
        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#editDataCenter(com.abiquo.abiserver.pojo
     * .authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    public BasicResult editDataCenter(final UserSession userSession, final DataCenter dataCenter)
    {
        BasicResult basicResult;
        basicResult = new BasicResult();
        basicResult.setSuccess(true);

        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            DatacenterHB datacenterPojo =
                (DatacenterHB) session.get(DatacenterHB.class, dataCenter.getId());

            // Auxiliar Datacenter object, used to keep a copy of the object
            // before the modification
            DataCenter datacenterAux = datacenterPojo.toPojo();

            datacenterPojo.setName(dataCenter.getName());
            datacenterPojo.setSituation(dataCenter.getSituation());

            session.update(datacenterPojo);

            transaction.commit();

            // Log the event
            traceLog(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_MODIFY, userSession,
                datacenterAux, null, "Datacenter '" + datacenterAux.getName()
                    + "' has been modified [Name: " + dataCenter.getName() + ", Situation: "
                    + dataCenter.getSituation() + "]", null, null, null, null, null);

        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "editDataCenter", e);

            DatacenterHB datacenterPojo =
                (DatacenterHB) session.get(DatacenterHB.class, dataCenter.getId());

            traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_MODIFY,
                userSession, datacenterPojo.toPojo(), null, e.getMessage(), null, null, null, null,
                null);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#deleteDataCenter(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    public BasicResult deleteDataCenter(final UserSession userSession, final DataCenter dataCenter)
    {

        BasicResult basicResult;
        basicResult = new BasicResult();

        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();
        RackDAO rackDAO = factory.getRackDAO();
        PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
        MachineLoadRuleDAO machineLoadRuleDAO = factory.getMachineLoadRuleDAO();
        FitPolicyRuleDAO fitPolicyRuleDAO = factory.getFitPolicyRuleDAO();

        try
        {
            factory.beginConnection();
            DatacenterHB datacenterPojo = datacenterDAO.findById(dataCenter.getId());

            // only delete the datacenter if it doesn't have any virtual
            // datacenter associated
            if (datacenterDAO.getNumberVirtualDatacentersByDatacenter(datacenterPojo
                .getIdDataCenter()) == 0)
            {
                // Delete datacenter allocation rules
                fitPolicyRuleDAO.deleteRulesForDatacenter(dataCenter.getId());
                machineLoadRuleDAO.deleteRulesForDatacenter(dataCenter.getId());

                for (RackHB currentRack : datacenterPojo.getRacks())
                {
                    for (PhysicalmachineHB pmToDelete : currentRack.getPhysicalmachines())
                    {
                        deleteNotManagedVMachines(pmToDelete.getIdPhysicalMachine());

                        pmDAO.makeTransient(pmToDelete);
                    }

                    // Once all physical machines are deleted, delete the rack
                    rackDAO.makeTransient(currentRack);
                }

                datacenterPojo.setEntLimits(new HashSet<DatacenterLimitHB>());

                datacenterDAO.makeTransient(datacenterPojo);

                // make the changes persistent
                factory.endConnection();

                traceLog(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_DELETE,
                    userSession, dataCenter, null, null, null, null, null, null, null);

                basicResult.setSuccess(true);
            }
            else
            {
                traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                    userSession, dataCenter, null, "there are virtual datacenters associated",
                    null, null, null, null, null);

                // This exception will be catch if you try to delete
                // PhysicalDatacenters with
                // AssociatedDatacenters
                factory.rollbackConnection();

                errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                    "deleteDataCenterConstraint");

            }

        }
        catch (Exception e)
        {
            factory.rollbackConnection();

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "deleteDataCenter", e);

            traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                userSession, dataCenter, null, e.getMessage(), null, null, null, null, null);

        }

        return basicResult;
    }

    /* ______________________________ RACKS _______________________________ */

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#createRack(com.abiquo.abiserver.pojo.
     * authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.Rack)
     */
    public DataResult<Rack> createRack(final UserSession userSession, final Rack rack)
    {
        DataResult<Rack> dataResult;
        dataResult = new DataResult<Rack>();
        dataResult.setSuccess(true);

        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            
            VlanNetworkParameters vlanNetParameters = rack.getVlanNetworkParameters();
            if(vlanNetParameters == null)
            {
            	vlanNetParameters = new VlanNetworkParameters(2, 4094, 
            			"", 80, 8);
            	rack.setVlanNetworkParameters(vlanNetParameters);
            }
            
            RackHB rackHB = rack.toPojoHB();
            session.save(rackHB);

            rack.setId(rackHB.getIdRack());
            dataResult.setData(rack);

            transaction.commit();

            traceLog(SeverityType.INFO, ComponentType.RACK, EventType.RACK_CREATE, userSession,
                rack.getDataCenter(), null, "Rack '" + rack.getName() + "' has been created", null,
                rack, null, null, null);
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "createRack", e);

            traceLog(SeverityType.CRITICAL, ComponentType.RACK, EventType.RACK_CREATE, userSession,
                rack.getDataCenter(), null, e.getMessage(), null, rack, null, null, null);

        }
        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#deleteRack(com.abiquo.abiserver.pojo.
     * authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.Rack)
     */
    public BasicResult deleteRack(final UserSession userSession, final Rack rack)
    {
        BasicResult basicResult;
        basicResult = new BasicResult();

        PhysicalMachineDAO physicalmachineDAO = factory.getPhysicalMachineDAO();
        RackDAO rackDAO = factory.getRackDAO();

        try
        {
            factory.beginConnection();

            RackHB rackHB = rackDAO.findById(rack.getId());

            // Delete rack allocation rules
            MachineLoadRuleDAO ruleDAO = factory.getMachineLoadRuleDAO();
            ruleDAO.deleteRulesForRack(rack.getId());

            for (PhysicalmachineHB pmToDelete : rackHB.getPhysicalmachines())
            {
                if (physicalmachineDAO.getNumberOfDeployedVirtualMachines(pmToDelete) == 0)
                {
                    // VMs not managed must be deleted too
                    deleteNotManagedVMachines(pmToDelete.getIdPhysicalMachine());

                    physicalmachineDAO.makeTransient(pmToDelete);
                }
                else
                {
                    basicResult.setSuccess(false);

                    errorManager.reportError(resourceManager, basicResult,
                        "deleterack_deployedmachines");

                    factory.rollbackConnection();

                    return basicResult;

                }
            }

            // once the physical machines are deleted, delete the rack
            rackDAO.makeTransient(rackHB);

            basicResult.setSuccess(true);

            factory.endConnection();

            traceLog(SeverityType.INFO, ComponentType.RACK, EventType.RACK_DELETE, userSession,
                rack.getDataCenter(), null, null, null, rack, null, null, null);

        }
        catch (Exception e)
        {
            factory.rollbackConnection();

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "deleteRack", e);

            traceLog(SeverityType.CRITICAL, ComponentType.RACK, EventType.RACK_DELETE, userSession,
                rack.getDataCenter(), null, e.getMessage(), null, rack, null, null, null);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.InfrastructureCommand#editRack(com.abiquo.abiserver.pojo.
     * authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.Rack)
     */
    public BasicResult editRack(final UserSession userSession, final Rack rack)
    {

        BasicResult basicResult;
        basicResult = new BasicResult();
        basicResult.setSuccess(true);

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            RackHB rackPojo =
                (RackHB) session.get(
                    com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB.class,
                    rack.getId());

            // Auxiliar rack object, used to keep a copy of the object before
            // the modification
            Rack rackAux = rackPojo.toPojo();

            rackPojo.setName(rack.getName());
            rackPojo.setShortDescription(rack.getShortDescription());
            rackPojo.setLargeDescription(rack.getLargeDescription());

            VlanNetworkParameters vlanNetworkParameters = rack.getVlanNetworkParameters();
            rackPojo.setVlan_id_max(vlanNetworkParameters.getVlan_id_max());
            rackPojo.setVlan_id_min(vlanNetworkParameters.getVlan_id_min());
            rackPojo.setVlan_per_vdc_expected(vlanNetworkParameters.getVlan_per_vdc_expected());
            rackPojo.setNRSQ(vlanNetworkParameters.getNRSQ());
            rackPojo.setVlans_id_avoided(vlanNetworkParameters.getVlans_id_avoided());

            session.update(rackPojo);

            transaction.commit();

            traceLog(SeverityType.INFO, ComponentType.RACK, EventType.RACK_MODIFY, userSession,
                rackAux.getDataCenter(), null, "Rack '" + rackAux.getName()
                    + "' has been modified [Name: " + rack.getName() + ", Short description: "
                    + rack.getShortDescription() + ", Large description: "
                    + rack.getLargeDescription() + "]", null, rackAux, null, null, null);
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "editRack", e);

            RackHB rackPojo =
                (RackHB) session.get(
                    com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB.class,
                    rack.getId());

            traceLog(SeverityType.CRITICAL, ComponentType.RACK, EventType.RACK_MODIFY, userSession,
                rackPojo.toPojo().getDataCenter(), null, e.getMessage(), null, rackPojo.toPojo(),
                null, null, null);
        }
        return basicResult;
    }

    /*
     * ______________________________ PHYSICAL MACHINES _______________________________
     */
    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#createPhysicalMachine(com.abiquo.abiserver
     * .pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation)
     */
    public DataResult<PhysicalMachineCreation> createPhysicalMachine(final UserSession userSession,
        final PhysicalMachineCreation physicalMachineCreation)
        throws InfrastructureCommandException
    {
        DataResult<PhysicalMachineCreation> dataResult;
        dataResult = new DataResult<PhysicalMachineCreation>();
        Session session = null;
        Transaction transaction = null;
        PhysicalMachine pm = physicalMachineCreation.getPhysicalMachine();

        String virtualSystemMonitorAddress = null;
        try
        {
            virtualSystemMonitorAddress =
                RemoteServiceUtils.getVirtualSystemMonitorFromPhysicalMachine(pm);
        }
        catch (Exception e)
        {
            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "createPhysicalMachine", e);

            PhysicalMachine physicalMachine = physicalMachineCreation.getPhysicalMachine();

            // Log the event
            traceLog(SeverityType.CRITICAL, ComponentType.MACHINE, EventType.MACHINE_CREATE,
                userSession, physicalMachine.getDataCenter(), null, e.getMessage(), null,
                (Rack) physicalMachine.getAssignedTo(), physicalMachine, null, null);
        }

        HyperVisor hypervisor = physicalMachineCreation.getHypervisors().get(0);

        String user = hypervisor.getUser();
        String password = hypervisor.getPassword();
        String virtualSystemAddress =
            "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";
        
        HypervisorType hypervisorType = hypervisor.toPojoHB().getType();
        try
        {
        	EventingSupport.monitorPhysicalMachine(virtualSystemAddress, 
        			hypervisorType, virtualSystemMonitorAddress, user, password);
        } catch (EventingException e)
        {
        	errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                    "createPhysicalMachine", e);
        	return dataResult;
        }
        
        try
        {            
            PhysicalMachine physicalMachine = physicalMachineCreation.getPhysicalMachine();
            // Checks non-zero values in PhysicalMachine data
            checkPhysicalMachineData(physicalMachine);

            session = HibernateUtil.getSessionFactory().getCurrentSession();
            transaction = session.beginTransaction();
            // Creating the PhysicalMachineHB object to save the PhysicalMachine
            // in the Data Base
            PhysicalmachineHB physicalMachineHB = physicalMachine.toPojoHB();
            physicalMachineHB.setHypervisor(null);

            // Save the datastores of the physicalmachine first
            for (DatastoreHB datastore : physicalMachineHB.getDatastoresHB())
            {
                session.save(datastore);
            }

            session.save(physicalMachineHB);

            // Creating the hypervisors, if there is any
            ArrayList<HyperVisor> hypervisorList = physicalMachineCreation.getHypervisors();
            ArrayList<HyperVisor> createdHypervisorList = new ArrayList<HyperVisor>();
            HypervisorHB hypervisorHBToCreate;
            for (HyperVisor hypervisorToCreate : hypervisorList)
            {
                hypervisorHBToCreate = hypervisorToCreate.toPojoHB();
                hypervisorHBToCreate.setPhysicalMachine(physicalMachineHB);
                session.save(hypervisorHBToCreate);
                createdHypervisorList.add(hypervisorHBToCreate.toPojo());
            }

            // Returning the PhysicalMachine and the Hypervisors created to the
            // client
            PhysicalmachineHB physicalMachineHBCreated =
                (PhysicalmachineHB) session.get(PhysicalmachineHB.class, physicalMachineHB
                    .getIdPhysicalMachine());
            PhysicalMachine physicalMachineCreated = physicalMachineHBCreated.toPojo();

            transaction.commit();
            physicalMachineCreation.setPhysicalMachine(physicalMachineCreated);
            physicalMachineCreation.setHypervisors(createdHypervisorList);
            dataResult.setData(physicalMachineCreation);
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("createPhysicalMachine.success"));

            // Log the event
            String hyperName = "NULL";
            if (physicalMachineCreation.getHypervisors().get(0) != null)
            {
                hyperName = physicalMachineCreation.getHypervisors().get(0).getName();
            }
            traceLog(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_CREATE,
                userSession, physicalMachine.getDataCenter(), null, "Physical machine '"
                    + physicalMachine.getName() + "' has been created [" + physicalMachine.getCpu()
                    + "CPUs, " + physicalMachine.getRam() + " RAM, " + physicalMachine.getHd()
                    + " HD, " + hyperName + " hypervisor]", null, (Rack) physicalMachine
                    .getAssignedTo(), physicalMachine, null, null);

        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "createPhysicalMachine", e);

            PhysicalMachine physicalMachine = physicalMachineCreation.getPhysicalMachine();

            // Log the event
            traceLog(SeverityType.CRITICAL, ComponentType.MACHINE, EventType.MACHINE_CREATE,
                userSession, physicalMachine.getDataCenter(), null, e.getMessage(), null,
                (Rack) physicalMachine.getAssignedTo(), physicalMachine, null, null);
            try
            {
                EventingSupport.unMonitorPhysicalMachine(virtualSystemAddress, hypervisorType,
                    virtualSystemMonitorAddress, user, password);
            }
            catch (EventingException e1)
            {
                errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                    "createPhysicalMachine", e1);
            }
        }
        return dataResult;

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#deletePhysicalMachine(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    public BasicResult deletePhysicalMachine(final UserSession userSession,
        final PhysicalMachine physicalMachine)
    {
        BasicResult basicResult = new BasicResult();

        try
        {
            factory.beginConnection();

            PhysicalMachineDAO machineDao = factory.getPhysicalMachineDAO();
            PhysicalmachineHB machine = machineDao.findById(physicalMachine.getId());
            List<VirtualmachineHB> vms =
                machineDao.getDeployedVirtualMachines(machine.getIdPhysicalMachine());

            HypervisorHB hypervisor = machine.getHypervisor();

            deletePhysicalMachineFromDatabase(physicalMachine.getId(), userSession);

            String user = hypervisor.getUser();
            String password = hypervisor.getPassword();

            String virtualSystemAddress =
                "http://" + hypervisor.getIp() + ":" + hypervisor.getPort() + "/";

            String virtualSystemMonitorAddress =
                RemoteServiceUtils.getVirtualSystemMonitorFromPhysicalMachine(physicalMachine);

            for (VirtualmachineHB vm : vms)
            {
                try
                {
                    EventingSupport.unsubscribe(vm.getName(), virtualSystemMonitorAddress);
                }
                catch (EventingException e)
                {
                    logger.debug(e.getMessage());
                }
            }

            EventingSupport.unMonitorPhysicalMachine(virtualSystemAddress, hypervisor.getType(),
                virtualSystemMonitorAddress, user, password);

            factory.endConnection();

            basicResult.setSuccess(true);

            traceLog(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_DELETE,
                userSession, physicalMachine.getDataCenter(), null, null, null,
                (Rack) physicalMachine.getAssignedTo(), physicalMachine, null, null);

        }
        catch (Exception e)
        {
            factory.rollbackConnection();

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "deletePhysicalMachine", e);

            traceLog(SeverityType.CRITICAL, ComponentType.MACHINE, EventType.MACHINE_DELETE,
                userSession, physicalMachine.getDataCenter(), null, e.getMessage(), null,
                (Rack) physicalMachine.getAssignedTo(), physicalMachine, null, null);
        }

        return basicResult;
    }

    protected void deletePhysicalMachineFromDatabase(final int machineId, final UserSession session)
    {
        PhysicalMachineDAO machineDao = factory.getPhysicalMachineDAO();

        PhysicalmachineHB machine = machineDao.findById(machineId);
        List<VirtualmachineHB> vms = machineDao.getDeployedAbiquoVirtualMachines(machineId);

        // Now, always delete the physicalMachine and the abiquoVM are updated to delete
        // hypevisor and move apps to not_deployed or apply changes

        // First of all virtualMachines on the pMachine we change references
        for (int i = 0; vms.size() > i; i++)
        {
            VirtualmachineHB vm = vms.get(i);
            deletePhysicalMachineReference(vm, session);
        }

        // Delete all of its related datastores.
        DatastoreDAO datastoreDao = factory.getDatastoreDAO();

        for (DatastoreHB datastoreHB : machine.getDatastoresHB())
        {
            datastoreDao.makeTransient(datastoreHB);
        }

        machineDao.makeTransient(machine);
    }

    @Override
    public void deletePhysicalMachineReference(VirtualmachineHB vMachine, UserSession user)
    {
        factory = HibernateDAOFactory.instance();

        VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();
        VirtualApplianceDAO vAppDAO = factory.getVirtualApplianceDAO();

        PhysicalmachineHB physicalMachine = vMachine.getHypervisor().getPhysicalMachine();

        // Update information to delete PhysicalMachine reference and update state
        vMachine.setDatastore(null);
        vMachine.setState(StateEnum.NOT_DEPLOYED);
        vMachine.setHypervisor(null);

        vmDAO.makePersistent(vMachine);

        VirtualappHB vApp = vmDAO.findVirtualAppFromVM(vMachine.getIdVm());

        StateEnum newState = StateEnum.NOT_DEPLOYED;

        Collection<NodeHB< ? >> nodes = vApp.getNodesHB();

        if (nodes != null && nodes.size() > 0)
        {
            for (NodeHB< ? > node : nodes)
            {
                NodeVirtualImageHB nVI = (NodeVirtualImageHB) node;
                if (nVI.getVirtualMachineHB().getState() != StateEnum.NOT_DEPLOYED)
                {
                    newState = StateEnum.APPLY_CHANGES_NEEDED;
                    break;
                }
            }
        }

        vApp.setState(newState);
        vApp.setSubState(newState);
        vAppDAO.makePersistent(vApp);

        // Finally we update the userResources
        updateUsedResourcesByPhysicalMachine(physicalMachine.getIdPhysicalMachine());

        traceLog(SeverityType.WARNING, ComponentType.VIRTUAL_MACHINE,
            com.abiquo.tracer.EventType.VM_UNDEPLOY_FORCED, user, null, vApp
                .getVirtualDataCenterHB().getName(), "FORCED UNDEPLOY of the VM" + " ["
                + vMachine.getName() + "] of the enterprise " + "["
                + vMachine.getEnterpriseHB().getName() + "], Virtual Appliance [" + vApp.getName()
                + "] on VirtualDataCenter [" + vApp.getVirtualDataCenterHB().getName() + "]"
                + " updated, please force re-deploy", vApp.toPojo(), null, null, null, null);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#editPhysicalMachine(com.abiquo.abiserver
     * .pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation)
     */
    public DataResult<ArrayList<HyperVisor>> editPhysicalMachine(final UserSession userSession,
        final PhysicalMachineCreation physicalMachineCreation)
        throws InfrastructureCommandException
    {

        DataResult<ArrayList<HyperVisor>> dataResult = new DataResult<ArrayList<HyperVisor>>();

        Session session = null;
        Transaction transaction = null;
        try
        {
        	PhysicalMachine pm = physicalMachineCreation.getPhysicalMachine();
            checkPhysicalMachineData(pm);

            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            PhysicalmachineHB physicalMachineHb =
                (PhysicalmachineHB) session.get(PhysicalmachineHB.class, pm.getId());

            PhysicalMachine physicalMachineAux = physicalMachineHb.toPojo();

            // Updating the other attributes
            physicalMachineHb.setName(pm.getName());
            physicalMachineHb.setDescription(pm.getDescription());
            physicalMachineHb.setCpu(pm.getCpu());
            physicalMachineHb.setRam(pm.getRam());
            physicalMachineHb.setHd(pm.getHd());
            physicalMachineHb.setRealCpu(pm.getRealCpu());
            physicalMachineHb.setRealRam(pm.getRealRam());
            physicalMachineHb.setIdState(pm.getIdState());         
            physicalMachineHb.getHypervisor().setIpService(pm.getHypervisor().getIpService());
            physicalMachineHb.setVswitchName(pm.getVswitchName());
                        
            session.update(physicalMachineHb);            

            dataResult.setSuccess(true);
            dataResult.setMessage("Physical Machine edited successfully");
            dataResult.setData(physicalMachineCreation.getHypervisors());

            transaction.commit();

            traceLog(SeverityType.INFO, ComponentType.MACHINE, EventType.MACHINE_MODIFY,
                userSession, physicalMachineAux.getDataCenter(), null, "Physical machine '"
                    + physicalMachineAux.getName() + "' has been modified [Name: "
                    + physicalMachineHb.getName() + ", " + +physicalMachineHb.getCpu() + "CPUs, "
                    + physicalMachineHb.getRam() + " RAM, " + physicalMachineHb.getHd() + " HD, "
                    + physicalMachineHb.getHypervisor().getType().getValue() + " hypervisor]", null, (Rack) physicalMachineAux.getAssignedTo(),
                physicalMachineAux, null, null);
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editPhysicalMachine", e);

            PhysicalmachineHB physicalMachineHb =
                (PhysicalmachineHB) session.get(PhysicalmachineHB.class, physicalMachineCreation
                    .getPhysicalMachine().getId());

            // Log the event
            traceLog(SeverityType.CRITICAL, ComponentType.MACHINE, EventType.MACHINE_MODIFY,
                userSession, physicalMachineCreation.getPhysicalMachine().getDataCenter(), null, e
                    .getMessage(), null, (Rack) physicalMachineCreation.getPhysicalMachine()
                    .getAssignedTo(), physicalMachineHb.toPojo(), null, null);

        }

        return dataResult;
    }

    /*
     * ______________________________ HYPERVISORS _______________________________
     */

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#createHypervisor(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.HyperVisor)
     */
    public DataResult<HyperVisor> createHypervisor(final UserSession userSession,
        final HyperVisor hypervisor)
    {
        DataResult<HyperVisor> dataResult = new DataResult<HyperVisor>();
        dataResult.setSuccess(true);

        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            HypervisorHB hypervisorHB = hypervisor.toPojoHB();

            session.save(hypervisorHB);

            transaction.commit();

            dataResult.setData(hypervisorHB.toPojo());
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "createHypervisor", e);
        }
        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#editHypervisor(com.abiquo.abiserver.pojo
     * .authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.HyperVisor)
     */
    public BasicResult editHypervisor(final UserSession userSession, final HyperVisor hypervisor)
    {

        BasicResult basicResult;
        basicResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            HypervisorHB hypervisorHB =
                (HypervisorHB) session.get(HypervisorHB.class, hypervisor.getId());
            PhysicalmachineHB physicalMachineHB =
                (PhysicalmachineHB) session.get(PhysicalmachineHB.class, hypervisor.getAssignedTo()
                    .getId());

            // Updating the Hypervisor
            hypervisorHB.setType(HypervisorType.fromValue(hypervisor.getType().getName()));
            hypervisorHB.setIp(hypervisor.getIp());
            hypervisorHB.setIpService(hypervisor.getIpService());
            hypervisorHB.setPort(hypervisor.getPort());
            hypervisorHB.setPhysicalMachine(physicalMachineHB);

            session.update(hypervisorHB);

            transaction.commit();
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, basicResult, "editHypervisor", e);
        }

        basicResult.setMessage(InfrastructureCommandImpl.resourceManager
            .getMessage("editHypervisor.success"));
        basicResult.setSuccess(true);

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#deleteHypervisor(com.abiquo.abiserver
     * .pojo.infrastructure.HyperVisor)
     */
    public BasicResult deleteHypervisor(final HyperVisor hypervisor)
    {
        BasicResult basicResult;
        basicResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            HypervisorHB hypervisorHB =
                (HypervisorHB) session.get(HypervisorHB.class, hypervisor.getId());
            session.delete(hypervisorHB);

            // TODO Do we have to delete Virtual Infrastructure when an
            // Hypervisor has been deleted?
            transaction.commit();

        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, basicResult, "deleteHypervisor", e);
        }

        basicResult.setSuccess(true);
        return basicResult;
    }

    /*
     * ______________________________ VIRTUAL MACHINES _______________________________
     */

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#createVirtualMachine(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    @Deprecated
    public DataResult<VirtualMachine> createVirtualMachine(final VirtualMachine virtualMachine)
    {
        DataResult<VirtualMachine> dataResult = new DataResult<VirtualMachine>();
        BasicResult wsResult = new BasicResult();
        VirtualMachine createdVirtualMachine;

        Session session = null;
        Transaction transaction = null;

        try
        {
            // Starting the hibernate session
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Generate the Virtual Machine that will be created
            VirtualmachineHB virtualMachineHB = virtualMachine.toPojoHB();
            virtualMachineHB.setState(StateEnum.NOT_DEPLOYED);
            virtualMachineHB.setUuid(UUID.randomUUID().toString());

            session.save(virtualMachineHB);

            // Recovering the Virtual Machine created, that will be returned to
            // the user
            createdVirtualMachine = virtualMachineHB.toPojo();

            // TODO Call the WebService to create the VirtualMachine
            /*
             * InfrastructureWS infrWS = new InfrastructureWS(); wsResult =
             * infrWS.createVirtualMachine(virtualMachine); if(! wsResult.getSuccess()) { Exception
             * e = new Exception(wsResult.getMessage()); throw e; }
             */

            // If everything went fine, we can save the hibernate session and
            // return the result
            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "createVirtualMachine", e);

            return dataResult;
        }

        dataResult.setData(createdVirtualMachine);
        dataResult.setMessage(wsResult.getMessage());
        dataResult.setSuccess(true);

        return dataResult;

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#deleteVirtualMachine(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.VirtualMachine)
     */
    public BasicResult deleteVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine)
    {
        // TODO Connect with database
        BasicResult basicResult = null;
        VirtualmachineHB virtualMachinePojo = null;
        Transaction transaction = null;
        try
        {
            Session session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            virtualMachinePojo =
                (VirtualmachineHB) session.get(VirtualmachineHB.class, virtualMachine.getId());
            session.delete(virtualMachinePojo);
            basicResult = getInfrastructureWS().deleteVirtualMachine(virtualMachine);
        }
        catch (Exception e)
        {
            errorManager.reportError(resourceManager, basicResult, "deleteVirtualMachine", e);

            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DESTROY,
                userSession, null, null, e.getMessage(), null, null, null, null, null);
        }
        if (basicResult.getSuccess())
        {
            transaction.commit();

            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DESTROY,
                userSession, null, null, null, null, null, null, null, null);
        }
        return basicResult;
    }

    /**
     * deletes the current virtual machines not managed by abicloud for this physicalmachine
     * 
     * @param idPhysicalMachine
     * @return
     * @throws PersistenceException
     */
    private boolean deleteNotManagedVMachines(final Integer idPhysicalMachine)
        throws PersistenceException
    {

        PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
        VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();

        for (VirtualmachineHB currentVM : pmDAO.getNotDeployedVirtualMachines(idPhysicalMachine))
        {
            vmDAO.makeTransient(currentVM);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#editVirtualMachine(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.VirtualMachine)
     */
    public BasicResult editVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine)
    {
        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);
        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            VirtualmachineHB virtualMachineHB =
                (VirtualmachineHB) session.get(VirtualmachineHB.class, virtualMachine.getId());

            if (virtualMachineHB.getState() != StateEnum.NOT_DEPLOYED)
            {
                basicResult = getInfrastructureWS().editVirtualMachine(virtualMachine);
            }

            if (basicResult.getSuccess())
            {
                session = HibernateUtil.getSession();
                transaction = session.beginTransaction();
                virtualMachineHB =
                    (VirtualmachineHB) session.get(VirtualmachineHB.class, virtualMachine.getId());
                // Updating the other attributes
                virtualMachineHB.setName(virtualMachine.getName());
                virtualMachineHB.setDescription(virtualMachine.getDescription());
                virtualMachineHB.setCpu(virtualMachine.getCpu());
                virtualMachineHB.setRam(virtualMachine.getRam());
                virtualMachineHB.setHd(virtualMachine.getHd());
                virtualMachineHB
                    .setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
                session.update(virtualMachineHB);
                transaction.commit();
            }
            else
            {
                errorManager.reportError(resourceManager, basicResult, "editVirtualMachine",
                    basicResult.getMessage());
                return basicResult;
            }

        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, basicResult, "editVirtualMachine", e);
        }

        basicResult.setSuccess(true);
        basicResult.setMessage(InfrastructureCommandImpl.resourceManager
            .getMessage("editVirtualMachine.success"));

        return basicResult;
    }

    /**
     * Performs the action in Abicloud associated with the attribute "state" in the virtual machine
     * Connects with AbiCloud WS to save the new virtual machine's state in Data Base
     * 
     * @param virtualMachine
     * @param actionState the action state to perform
     * @return
     */
    private BasicResult setVirtualMachineState(final VirtualMachine virtualMachine,
        final String actionState)
    {
        BasicResult basicResult = new BasicResult();
        try
        {
            basicResult = getInfrastructureWS().setVirtualMachineState(virtualMachine, actionState);
        }
        catch (Exception e)
        {
            errorManager.reportError(resourceManager, basicResult, "setVirtualMachineState", e,
                actionState);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#startVirtualMachine(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    public DataResult<State> startVirtualMachine(final VirtualMachine virtualMachine)
    {
        DataResult<State> dataResult = new DataResult<State>();
        BasicResult basicResult = new BasicResult();

        // Saving the state of the virtual machine sent by the user
        State oldState = virtualMachine.getState();

        // Checking the current state of the virtual machine
        DataResult<State> currentStateAndAllow;
        try
        {
            currentStateAndAllow = checkVirtualMachineState(virtualMachine);
        }
        catch (Exception e)
        {
            // There was a problem checking the state of the virtual machine. We
            // can not
            // manipulate it
            errorManager.reportError(resourceManager, dataResult, "startVirtualMachine", e);
            dataResult.setData(oldState);
            return dataResult;
        }

        if (currentStateAndAllow.getSuccess())
        {
            // The Virtual Machine is now blocked to other users, and we can
            // manipulate it
            switch (virtualMachine.getState().toEnum())
            {
                case PAUSED:
                    basicResult =
                        setVirtualMachineState(virtualMachine, AbiCloudConstants.RESUME_ACTION);
                    break;
                case POWERED_OFF:
                    basicResult =
                        setVirtualMachineState(virtualMachine, AbiCloudConstants.POWERUP_ACTION);
                    break;
            }

            if (!basicResult.getSuccess())
            {
                // There was a problem shuting down the virtual machine
                // Leaving the virtual machine with its old state
                // updateStateInDB(virtualMachine, oldState);

                // Generating the result
                dataResult.setMessage(basicResult.getMessage());
                dataResult.setSuccess(basicResult.getSuccess());
                dataResult.setData(new State(StateEnum.UNKNOWN));
                return dataResult;
            }

            dataResult.setMessage(basicResult.getMessage());
            dataResult.setSuccess(basicResult.getSuccess());
            // Decomment this to enable the state changes through eventing
            State inProgressState = new State(StateEnum.IN_PROGRESS);
            dataResult.setData(inProgressState);

        }
        else
        {
            // The Virtual Machine is being used by other user, or it is not up
            // to date.
            // We inform the new state
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("startVirtualMachine.success"));
            dataResult.setData(currentStateAndAllow.getData());

        }

        return dataResult;

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#pauseVirtualMachine(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    public DataResult<State> pauseVirtualMachine(final VirtualMachine virtualMachine)
    {
        DataResult<State> dataResult = new DataResult<State>();
        BasicResult basicResult = new BasicResult();

        // Saving the state of the virtual machine sent by the user
        State oldState = virtualMachine.getState();

        // Checking the current state of the virtual machine
        DataResult<State> currentStateAndAllow;
        try
        {
            currentStateAndAllow = checkVirtualMachineState(virtualMachine);
        }
        catch (Exception e)
        {
            // There was a problem checking the state of the virtual machine. We
            // can not
            // manipulate it
            errorManager.reportError(resourceManager, dataResult, "pauseVirtualMachine", e);
            dataResult.setData(oldState);
            return dataResult;
        }

        if (currentStateAndAllow.getSuccess())
        {
            // The Virtual Machine is now blocked to other users, and we can
            // manipulate it
            basicResult = setVirtualMachineState(virtualMachine, AbiCloudConstants.PAUSE_ACTION);

            if (!basicResult.getSuccess())
            {
                // There was a problem shuting down the virtual machine
                // Leaving the virtual machine with its old state
                // updateStateInDB(virtualMachine, oldState);

                // Generating the result
                dataResult.setMessage(basicResult.getMessage());
                dataResult.setSuccess(basicResult.getSuccess());
                dataResult.setData(new State(StateEnum.UNKNOWN));
                return dataResult;
            }

            dataResult.setMessage(basicResult.getMessage());
            dataResult.setSuccess(basicResult.getSuccess());
            State inProgressState = new State(StateEnum.IN_PROGRESS);
            dataResult.setData(inProgressState);

        }
        else
        {
            // The Virtual Machine is being used by other user, or it is not up
            // to date.
            // We inform the new state
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("pauseVirtualMachine.success"));
            dataResult.setData(currentStateAndAllow.getData());

        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#rebootVirtualMachine(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    public DataResult<State> rebootVirtualMachine(final VirtualMachine virtualMachine)
    {
        // Rebooting the machine implies powering off and powering up
        DataResult<State> dataResult = new DataResult<State>();
        BasicResult basicResult = new BasicResult();

        // Saving the state of the virtual machine sent by the user
        State oldState = virtualMachine.getState();

        // Checking the current state of the virtual machine
        DataResult<State> currentStateAndAllow;
        try
        {
            currentStateAndAllow = checkVirtualMachineState(virtualMachine);
        }
        catch (Exception e)
        {
            // There was a problem checking the state of the virtual machine. We
            // can not
            // manipulate it
            errorManager.reportError(resourceManager, dataResult, "rebootVirtualMachine", e);
            dataResult.setData(oldState);
            return dataResult;
        }

        if (currentStateAndAllow.getSuccess())
        {
            // The Virtual Machine is now blocked to other users, and we can
            // manipulate it

            // First we have to shut down the virtual machine
            basicResult =
                setVirtualMachineState(virtualMachine, AbiCloudConstants.POWERDOWN_ACTION);
            if (!basicResult.getSuccess())
            {
                // There was a problem shuting down the virtual machine
                // Leaving the virtual machine with its old state
                // updateStateInDB(virtualMachine, oldState);

                // Generating the result
                dataResult.setMessage(basicResult.getMessage());
                dataResult.setSuccess(basicResult.getSuccess());
                dataResult.setData(new State(StateEnum.UNKNOWN));
                return dataResult;
            }

            else
            {
                // The shutting down had success. Powering on the virtual
                // machine again
                BasicResult basicResultPowerUP =
                    setVirtualMachineState(virtualMachine, AbiCloudConstants.POWERUP_ACTION);

                dataResult.setMessage(basicResultPowerUP.getMessage());
                dataResult.setSuccess(basicResultPowerUP.getSuccess());
                State inProgressState = new State(StateEnum.IN_PROGRESS);
                dataResult.setData(inProgressState);
            }
        }
        else
        {
            // The Virtual Machine is being used by other user, or it is not up
            // to date.
            // We inform the new state
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("rebootVirtualMachine.success"));
            dataResult.setData(currentStateAndAllow.getData());

        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#shutdownVirtualMachine(com.abiquo.abiserver
     * .pojo.infrastructure.VirtualMachine)
     */
    public DataResult<State> shutdownVirtualMachine(final VirtualMachine virtualMachine)
    {
        DataResult<State> dataResult = new DataResult<State>();
        BasicResult basicResult = new BasicResult();

        // Saving the state of the virtual machine sent by the user
        State oldState = virtualMachine.getState();

        // Checking the current state of the virtual machine
        DataResult<State> currentStateAndAllow;
        try
        {
            currentStateAndAllow = checkVirtualMachineState(virtualMachine);
        }
        catch (Exception e)
        {
            // There was a problem checking the state of the virtual machine. We
            // can not
            // manipulate it
            errorManager.reportError(resourceManager, dataResult, "shutdownVirtualMachine", e);
            dataResult.setData(oldState);
            return dataResult;
        }

        if (currentStateAndAllow.getSuccess())
        {
            // The Virtual Machine is now blocked to other users, and we can
            // manipulate it
            basicResult =
                setVirtualMachineState(virtualMachine, AbiCloudConstants.POWERDOWN_ACTION);

            if (!basicResult.getSuccess())
            {
                // There was a problem shuting down the virtual machine
                // Leaving the virtual machine with unknown state

                // Generating the result
                dataResult.setMessage(basicResult.getMessage());
                dataResult.setSuccess(basicResult.getSuccess());
                dataResult.setData(new State(StateEnum.UNKNOWN));
                return dataResult;
            }

            dataResult.setMessage(basicResult.getMessage());
            dataResult.setSuccess(basicResult.getSuccess());
            State inProgressState = new State(StateEnum.IN_PROGRESS);
            dataResult.setData(inProgressState);

        }
        else
        {
            // The Virtual Machine is being used by another user, or it is not
            // up to date.
            // We inform of the new state
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("shutdownVirtualMachine.success"));
            dataResult.setData(currentStateAndAllow.getData());

        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#moveVirtualMachine(com.abiquo.abiserver
     * .pojo.authentication.UserSession, com.abiquo.abiserver.pojo.infrastructure.VirtualMachine)
     */
    public BasicResult moveVirtualMachine(final UserSession userSession,
        final VirtualMachine virtualMachine)
    {

        return editVirtualMachine(userSession, virtualMachine);
    }

    /**
     * Checks if the state of a given virtual machine, is actually the last valid state in the Data
     * Base If it is the same, the state of the virtual machine will be updated to
     * State.IN_PROGRESS, and a boolean will be returned to true, to indicate that the virtual
     * machine can be manipulated Otherwise, the current state will be returned, and the boolean
     * will be set to false, indicating that the virtual machine can not be manipulated
     * 
     * @param virtualMachine The virtual machine that will be checked
     * @return A DataResult object, containing a boolean that indicates if the virtual machine can
     *         be manipulated and, in any case, it will contain the last valid state of the virtual
     *         machine
     * @throws Exception An Exception is thrown if there was a problem connecting to the Data base
     */
    private DataResult<State> checkVirtualMachineState(final VirtualMachine virtualMachine)
        throws Exception
    {
        Session session = null;
        Transaction transaction = null;

        DataResult<State> currentStateAndAllow = new DataResult<State>();

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Getting the last state of the virtual machine
            VirtualmachineHB virtualMachineHB =
                (VirtualmachineHB) session.get(VirtualmachineHB.class, virtualMachine.getId());

            if (virtualMachine.getState().toEnum() == virtualMachineHB.getState()
                && virtualMachineHB.getState() != StateEnum.IN_PROGRESS)
            {
                // The given virtual machine is up to date, and is not in
                // progress.
                // We set it now to IN_PROGRESS, and return that it is allowed
                // to manipulate it
                virtualMachineHB.setState(StateEnum.IN_PROGRESS);

                session.update(virtualMachineHB);

                // Generating the result
                currentStateAndAllow.setSuccess(true);
                currentStateAndAllow.setData(new State(StateEnum.IN_PROGRESS));
            }
            else
            {
                // The given virtual machine is not up to date, or the virtual
                // machine
                // is already in the state State.IN_PROGRESS. Manipulating it is
                // not allowed

                // Generating the result
                currentStateAndAllow.setSuccess(false);
                currentStateAndAllow.setData(new State(virtualMachineHB.getState()));
            }

            transaction.commit();
        }

        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }
            throw e;
        }

        return currentStateAndAllow;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#checkVirtualMachinesState(java.util.ArrayList
     * )
     */
    @SuppressWarnings("unchecked")
    public DataResult<ArrayList<VirtualMachine>> checkVirtualMachinesState(
        final ArrayList<VirtualMachine> virtualMachinesToCheck)
    {
        DataResult<ArrayList<VirtualMachine>> dataResult =
            new DataResult<ArrayList<VirtualMachine>>();
        ArrayList<VirtualMachine> virtualMachinesChecked = new ArrayList<VirtualMachine>();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Generating the list of id's of the virtual machines requested
            ArrayList<Integer> virtualMachinesToCheckIds = new ArrayList<Integer>();
            for (VirtualMachine virtualMachine : virtualMachinesToCheck)
            {
                virtualMachinesToCheckIds.add(virtualMachine.getId());
            }

            // Getting the virtual machines updated from the data base
            ArrayList<VirtualmachineHB> virtualMachinesHBChecked =
                (ArrayList<VirtualmachineHB>) session.createCriteria(VirtualmachineHB.class).add(
                    Restrictions.in("idVm", virtualMachinesToCheckIds)).list();

            // Returning the result
            for (VirtualmachineHB virtualMachineHB : virtualMachinesHBChecked)
            {
                virtualMachinesChecked.add(virtualMachineHB.toPojo());
            }

            transaction.commit();

            dataResult.setSuccess(true);
            dataResult.setMessage(resourceManager.getMessage("checkVirtualMachinesState.success"));
            dataResult.setData(virtualMachinesChecked);
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "checkVirtualMachinesState", e);

        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#getHypervisorsTypeByDataCenter(com.abiquo
     * .abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB)
     */
    public DataResult<ArrayList<HyperVisorType>> getHypervisorsTypeByDataCenter(
        final DatacenterHB dataCenter)
    {
        DataResult<ArrayList<HyperVisorType>> dataResult =
            new DataResult<ArrayList<HyperVisorType>>();
        ArrayList<HypervisorType> hyperTypePojo = new ArrayList<HypervisorType>();

        HyperVisorDAO hyperDAO = factory.getHyperVisorDAO();

        try
        {
            // initialize the transaction
            factory.beginConnection();

            hyperTypePojo =
                new ArrayList<HypervisorType>(hyperDAO.getHypervisorsTypeByDataCenter(dataCenter));

            ArrayList<HyperVisorType> HyperTypeResult = new ArrayList<HyperVisorType>();
            for (HypervisorType type : hyperTypePojo)
            {
                HyperTypeResult.add(new HyperVisorType(type));
            }
            dataResult.setData(HyperTypeResult);
            dataResult.setSuccess(true);
            dataResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("getHypervisorsTypeByDataCenter.success"));

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            errorManager.reportError(InfrastructureCommandImpl.resourceManager, dataResult,
                "getHypervisorsTypeByDataCenter", e);
        }

        return dataResult;

    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.InfrastructureCommand#getFactory()
     */
    public DAOFactory getFactory()
    {
        return factory;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#setFactory(com.abiquo.abiserver.persistence
     * .DAOFactory)
     */
    public void setFactory(final DAOFactory factory)
    {
        this.factory = factory;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#updateUsedResourcesByDatacenter(com.abiquo
     * .abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB)
     */
    public BasicResult updateUsedResourcesByDatacenter(final DatacenterHB dataCenter)
    {
        BasicResult basicResult = new BasicResult();

        try
        {

            // initialize the transaction
            factory.beginConnection();
            DataCenterDAO datacenterDAO = factory.getDataCenterDAO();

            datacenterDAO.updateUsedResourcesByDatacenter(dataCenter.getIdDataCenter());
            basicResult.setSuccess(true);
            basicResult.setMessage(InfrastructureCommandImpl.resourceManager
                .getMessage("updateUsedResourcesByDatacenter.success"));

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "updateUsedResourcesByDatacenter", e);
        }

        return basicResult;

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#updateUsedResourcesByDatacenter(com.abiquo
     * .abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB)
     */
    public void updateUsedResourcesByPhysicalMachine(final Integer pmID)
    {
        factory = HibernateDAOFactory.instance();

        try
        {
            PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();

            pmDAO.updateUsedResourcesByPhysicalMachine(pmID);

        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
        }

    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.InfrastructureCommand#checkIPAddress(java.lang.String)
     */
    public void checkIPAddress(final String ip) throws InfrastructureCommandException
    {

        // IP not filed
        if (ip == null || ip.equals(""))
        {
            throw new InfrastructureCommandException(resourceManager
                .getMessage("assignPublicIPDatacenter.NOIP.extraMsg"),
                AbiCloudError.INFRASTRUCTURE_ERROR);
        }

        // IP not well formed
        try
        {
            IPAddress.newIPAddress(ip);
        }
        catch (InvalidIPAddressException e)
        {
            throw new InfrastructureCommandException(resourceManager
                .getMessage("assignPublicIPDatacenter.IPNOWELLFORMED.extraMsg"),
                AbiCloudError.INFRASTRUCTURE_ERROR);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#checkPhysicalMachineData(com.abiquo.abiserver
     * .pojo.infrastructure.PhysicalMachine)
     */
    public void checkPhysicalMachineData(final PhysicalMachine physicalMachine)
        throws InfrastructureCommandException
    {
        // Checks non-zero values in PhysicalMachine data
        if (physicalMachine.getCpu() <= 0 || physicalMachine.getRam() <= 0
            || physicalMachine.getHd() <= 0)
        {
            throw new InfrastructureCommandException(resourceManager
                .getMessage("checkPhysicalMachine.zerovalues.extraMsg"),
                AbiCloudError.INFRASTRUCTURE_ERROR);
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#forceRefreshVirtualMachineState(com.abiquo
     * .abiserver.pojo.infrastructure.VirtualMachine)
     */
    public BasicResult forceRefreshVirtualMachineState(final VirtualMachine virtualMachine)
    {
        BasicResult basicResult = null;
        // Calling the webservice
        basicResult = getInfrastructureWS().forceRefreshVirtualMachineState(virtualMachine);
        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#validateRemoteService(com.abiquo.abiserver
     * .business.hibernate.pojohb.service.RemoteServiceHB)
     */
    public boolean validateRemoteService(final RemoteServiceHB remoteService)
    {
        return remoteService.getURI() != null;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.InfrastructureCommand#checkExistingDataCenterNames(java.lang
     * .String)
     */
    public boolean checkExistingDataCenterNames(final String name) throws PersistenceException
    {
        DataCenterDAO dao = factory.getDataCenterDAO();

        DatacenterHB dc;
        dc = dao.findByName(name);

        return dc != null;

    }

    /**
     * @param infrastructureWS the infrastructureWS to set
     */
    public void setInfrastructureWS(IInfrastructureWS infrastructureWS)
    {
        this.infrastructureWS = infrastructureWS;
    }

    /**
     * @return the infrastructureWS
     */
    public IInfrastructureWS getInfrastructureWS()
    {
        return infrastructureWS;
    }

    /**
     * Checks the virtual infrastructure related to the physical machine
     * 
     * @param physicalMachineId the physical machine identifier to check the virtual infrastructure
     * @return
     */
    public BasicResult checkVirtualInfrastructureState(final Integer physicalMachineId,
        final UserSession userSession, Boolean isAutomaticCheck)
    {
        factory.beginConnection();
        PhysicalMachineDAO pmDao = factory.getPhysicalMachineDAO();
        PhysicalmachineHB pm = pmDao.findById(physicalMachineId);
        DatacenterHB dataCenter = pm.getDataCenter();
        factory.endConnection();

        return updateUsedResourcesByDatacenter(dataCenter);
    }

}
