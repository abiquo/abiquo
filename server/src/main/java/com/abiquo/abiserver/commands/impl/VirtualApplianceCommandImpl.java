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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.abicloudws.IVirtualApplianceWS;
import com.abiquo.abiserver.abicloudws.RemoteServiceUtils;
import com.abiquo.abiserver.abicloudws.VirtualApplianceWS;
import com.abiquo.abiserver.appslibrary.VirtualImageException;
import com.abiquo.abiserver.business.authentication.SessionUtil;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.LogHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeStorageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeTypeEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.NetworkCommand;
import com.abiquo.abiserver.commands.VirtualApplianceCommand;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.VirtualDatacenterResourceStub;
import com.abiquo.abiserver.commands.stub.VirtualMachineResourceStub;
import com.abiquo.abiserver.commands.stub.impl.VirtualDatacenterResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.VirtualMachineResourceStubImpl;
import com.abiquo.abiserver.eventing.EventingException;
import com.abiquo.abiserver.eventing.EventingSupport;
import com.abiquo.abiserver.exception.HardLimitExceededException;
import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.exception.NotEnoughResourcesException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.exception.SchedulerException;
import com.abiquo.abiserver.exception.SoftLimitExceededException;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.abiserver.exception.VirtualFactoryHealthException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.Log;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeNetwork;
import com.abiquo.abiserver.pojo.virtualappliance.NodeStorage;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;
import com.sun.ws.management.client.exceptions.FaultException;

/**
 * This command collects all actions related to Virtual Appliances
 * 
 * @author abiquo
 */
public class VirtualApplianceCommandImpl extends BasicCommand implements VirtualApplianceCommand
{

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(VirtualApplianceCommandImpl.class);

    private IVirtualApplianceWS virtualApplianceWs;

    public VirtualApplianceCommandImpl()
    {
        try
        {
            virtualApplianceWs =
                (IVirtualApplianceWS) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.abicloudws.VirtualApplianceWSPremium")
                    .newInstance();
        }
        catch (Exception e)
        {
            virtualApplianceWs = new VirtualApplianceWS();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#checkVirtualAppliance(com.abiquo.abiserver
     * .pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public DataResult<VirtualAppliance> checkVirtualAppliance(
        final VirtualAppliance virtualAppliance)
    {

        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Getting the given VirtualAppliance from the DataBase
            VirtualappHB updatedVirtualAppHB =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualAppliance.getId());

            // Generating the result
            if (updatedVirtualAppHB != null)
            {
                dataResult.setSuccess(true);
                dataResult.setData(updatedVirtualAppHB.toPojo());
                dataResult.setMessage(resourceManager.getMessage("checkVirtualAppliance.success"));
            }
            else
            {
                errorManager.reportError(resourceManager, dataResult, "checkVirtualAppliance");
            }

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "checkVirtualAppliance", e);
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.VirtualApplianceCommand#
     * checkVirtualDatacentersAndAppliancesByEnterprise(com.abiquo.abiserver.pojo.user.Enterprise)
     */
    @Override
    @SuppressWarnings("unchecked")
    public DataResult<ArrayList<Collection>> checkVirtualDatacentersAndAppliancesByEnterprise(
        UserSession userSession, final Enterprise enterprise)
    {
        DataResult<ArrayList<Collection>> dataResult = new DataResult<ArrayList<Collection>>();

        ArrayList<Collection> updatedVirtualDatacentersAndAppliances = new ArrayList<Collection>();

        // Retrieving the updated VirtualDatacenters list
        DataResult<Collection<VirtualDataCenter>> updatedVirtualDatacenters =
            getVirtualDataCentersByEnterprise(userSession, enterprise);

        // Retrieving the updated VirtualAppliances list
        DataResult<Collection<VirtualAppliance>> updatedVirtualAppliances =
            getVirtualAppliancesByEnterprise(userSession, enterprise);

        // If both were successful
        if (updatedVirtualDatacenters.getSuccess() && updatedVirtualAppliances.getSuccess())
        {
            // Creating the result to return
            updatedVirtualDatacentersAndAppliances.add(updatedVirtualDatacenters.getData());
            updatedVirtualDatacentersAndAppliances.add(updatedVirtualAppliances.getData());

            dataResult.setSuccess(true);
            dataResult.setData(updatedVirtualDatacentersAndAppliances);
            dataResult.setMessage(resourceManager
                .getMessage("checkVirtualDatacentersAndAppliancesByEnterprise.success"));
        }
        else
        {
            // Creating the error result to return
            dataResult.setSuccess(false);
            dataResult.setMessage(resourceManager
                .getMessage("checkVirtualDatacentersAndAppliancesByEnterprise.error"));
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.VirtualApplianceCommand#
     * checkVirtualDatacentersAndAppliancesByEnterpriseAndDatacenter
     * (com.abiquo.abiserver.pojo.user.Enterprise,
     * com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    @SuppressWarnings("unchecked")
    public DataResult<ArrayList<Collection>> checkVirtualDatacentersAndAppliancesByEnterpriseAndDatacenter(
        UserSession userSession, final Enterprise enterprise, final DataCenter datacenter)
    {
        DataResult<ArrayList<Collection>> dataResult = new DataResult<ArrayList<Collection>>();

        ArrayList<Collection> updatedVirtualDatacentersAndAppliances = new ArrayList<Collection>();

        // Retrieving the updated VirtualDatacenters list
        DataResult<Collection<VirtualDataCenter>> updatedVirtualDatacenters =
            getVirtualDataCentersByEnterpriseAndDatacenter(userSession, enterprise, datacenter);

        // Retrieving the updated VirtualAppliances list
        DataResult<Collection<VirtualAppliance>> updatedVirtualAppliances =
            getVirtualAppliancesByEnterpriseAndDatacenter(userSession, enterprise, datacenter);

        // If both were successful
        if (updatedVirtualDatacenters.getSuccess() && updatedVirtualAppliances.getSuccess())
        {
            // Creating the result to return
            updatedVirtualDatacentersAndAppliances.add(updatedVirtualDatacenters.getData());
            updatedVirtualDatacentersAndAppliances.add(updatedVirtualAppliances.getData());

            dataResult.setSuccess(true);
            dataResult.setData(updatedVirtualDatacentersAndAppliances);
            dataResult.setMessage(resourceManager
                .getMessage("checkVirtualDatacentersAndAppliancesByEnterprise.success"));
        }
        else
        {
            // Creating the error result to return
            dataResult.setSuccess(false);
            dataResult.setMessage(resourceManager
                .getMessage("checkVirtualDatacentersAndAppliancesByEnterprise.error"));
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)s
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#createVirtualAppliance(com.abiquo.abiserver
     * .pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public DataResult<VirtualAppliance> createVirtualAppliance(final UserSession userSession,
        VirtualAppliance virtualAppliance)
    {

        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();

        VirtualappHB virtualAppHBPojo = null;
        Session session = null;
        Transaction transaction = null;

        // Variable to hold the id of the virtual appliance - this will be used
        // to report an error
        // to the database
        Integer virtualApplianceId = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            virtualAppHBPojo = virtualAppliance.toPojoHB();

            virtualAppHBPojo.setState(StateEnum.NOT_DEPLOYED);
            virtualAppHBPojo.setSubState(StateEnum.NOT_DEPLOYED);

            // Saving the data
            session.save("VirtualappHB", virtualAppHBPojo);

            // Set the virtualApplianceId
            virtualApplianceId = virtualAppHBPojo.getIdVirtualApp();

            // Recovering the best information
            virtualAppliance =
                ((VirtualappHB) session.get("VirtualappExtendedHB", virtualApplianceId)).toPojo();

            // Updating dataResult information
            dataResult.setData(virtualAppliance);
            dataResult.setMessage(resourceManager.getMessage("createVirtualAppliance.success"));
            dataResult.setSuccess(true);

            transaction.commit();

            // Log the event
            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_CREATE,
                userSession, null, virtualAppliance.getVirtualDataCenter().getName(),
                "Virtual Appliance '" + virtualAppliance.getName() + "' has been created",
                virtualAppliance, null, null, null, null);
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "createVirtualAppliance", e,
                virtualApplianceId);

            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_CREATE,
                userSession, null, virtualAppliance.getVirtualDataCenter().getName(),
                e.getMessage(), null, null, null, null, null);

            return dataResult;
        }

        return dataResult;

    }

    /**
     * FIXME in order to check the private VLAN limits when creating a virtual datacenter
     */
    private NetworkCommand instantiateNetworkCommand()
    {
        NetworkCommand netComm;
        try
        {
            netComm =
                (NetworkCommand) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.commands.impl.NetworkingCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            netComm = new NetworkCommandImpl();
        }

        return netComm;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#createVirtualDataCenter(com.abiquo.
     * abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter, java.lang.String,
     * com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB)
     */
    @Override
    public DataResult<VirtualDataCenter> createVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter, final String networkName,
        final NetworkConfigurationHB configuration)
    {
        // Check the private VLAN limits.
        VirtualDataCenterHB vdc = virtualDataCenter.toPojoHB();
        EnterpriseHB enter = vdc.getEnterpriseHB();

        DAOFactory daoF = HibernateDAOFactory.instance();

        try
        {
            daoF.beginConnection();

            instantiateNetworkCommand().checkPrivateVlan(vdc, vdc.getIdDataCenter(), enter);
        }
        catch (Exception e)
        {
            DataResult<VirtualDataCenter> result = new DataResult<VirtualDataCenter>();
            result.setSuccess(false);
            result.setMessage(e.getMessage());

            return result;
        }
        finally
        {
            daoF.endConnection();
        }

        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        DataResult<VirtualDataCenter> result =
            proxy.createVirtualDatacenter(virtualDataCenter, networkName, configuration,
                resourceManager);

        if (!result.getSuccess())
        {
            BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_CREATE, userSession, null, virtualDataCenter.getName(),
                result.getMessage(), null, null, null, null, null);
        }
        else
        {
            BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_CREATE, userSession, null, result.getData().getName(),
                "Virtual datacenter '" + result.getData().getName() + "' with a "
                    + result.getData().getHyperType().getName() + " hypervisor has been created",
                null, null, null, null, null);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#deleteVirtualAppliance(com.abiquo.abiserver
     * .pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public BasicResult deleteVirtualAppliance(final UserSession userSession,
        final VirtualAppliance virtualAppliance)
    {
        BasicResult basicResult = new BasicResult();

        basicResult.setSuccess(true);

        VirtualappHB virtualappHBPojo = null;
        Session session = null;
        Transaction transaction = null;

        Integer virtualApplianceId = virtualAppliance.getId();

        try
        {
            basicResult = shutdownVirtualAppliance(userSession, virtualAppliance);

            if (basicResult.getSuccess())
            {
                session = HibernateUtil.getSession();
                transaction = session.beginTransaction();
                virtualappHBPojo =
                    (VirtualappHB) session.get("VirtualappExtendedHB", virtualApplianceId);

                // Delete Rasds for each node
                Collection<NodeHB< ? >> nodes = virtualappHBPojo.getNodesHB();

                if (nodes != null && nodes.size() > 0)
                {
                    for (NodeHB< ? > node : nodes)
                    {
                        NodeVirtualImageHB nVI = (NodeVirtualImageHB) node;
                        deleteRasdFromNode(session, nVI);
                    }
                }

                // Deleting the virtual appliance
                session.delete("VirtualappHB", virtualappHBPojo);

                transaction.commit();

                traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_DELETE,
                    userSession, null, virtualAppliance.getVirtualDataCenter().getName(), null,
                    virtualAppliance, null, null, null, null);

            }
        }
        catch (Exception e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_DELETE,
                userSession, null, virtualAppliance.getVirtualDataCenter().getName(),
                e.getMessage(), virtualAppliance, null, null, null, null);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, basicResult, "deleteVirtualAppliance", e,
                virtualApplianceId);
        }

        return basicResult;
    }

    private boolean mustUndeploy(final StateEnum state)
    {
        switch (state)
        {
            case PAUSED:
            case POWERED_OFF:
            case REBOOTED:
            case RUNNING:
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#deleteVirtualDataCenter(com.abiquo.
     * abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter)
     */
    @Override
    public BasicResult deleteVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter)
    {
        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        BasicResult result = proxy.deleteVirtualDatacenter(virtualDataCenter, resourceManager);

        if (!result.getSuccess())
        {
            BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_DELETE, userSession, null, virtualDataCenter.getName(),
                result.getMessage(), null, null, null, null, null);
        }
        else
        {
            BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_DELETE, userSession, null, virtualDataCenter.getName(), null, null,
                null, null, null, null);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#editVirtualAppliance(com.abiquo.abiserver
     * .pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public DataResult<VirtualAppliance> editVirtualAppliance(final UserSession userSession,
        VirtualAppliance virtualAppliance)
    {

        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        dataResult.setSuccess(true);
        VirtualappHB virtualappHBPojo = null;
        VirtualappHB virtualappHBPojoOld = null;
        VirtualAppliance virtualappOld = null;
        Session session = null;
        Transaction transaction = null;
        // Determines if the transaction has to be started again
        List<Node> updatedNodes = null;

        State originalVirtualApplianceState = virtualAppliance.getState();

        try
        {
            UserHB owner = SessionUtil.findUserHBByName(userSession.getUser());

            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Recover the virtualApp information and store a backup copy
            // as well in case we
            // have to revert to the it's state as it was before
            virtualappHBPojo =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualAppliance.getId());
            virtualappHBPojoOld =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualAppliance.getId());
            virtualappOld = virtualappHBPojoOld.toPojo();
            virtualappHBPojo.setName(virtualAppliance.getName());
            virtualappHBPojo.setPublic_(virtualAppliance.getIsPublic() ? 1 : 0);
            virtualappHBPojo.setVirtualDataCenterHB(virtualAppliance.getVirtualDataCenter()
                .toPojoHB());
            virtualappHBPojo.setNodeConnections(virtualAppliance.getNodeConnections());

            if (virtualAppliance.getNodes().size() > 0)
            {
                updatedNodes =
                    updateVirtualAppliancePojo(userSession, session, virtualappHBPojo,
                        virtualAppliance, owner);

                // Update the virtual appliance with the present nodes
                ArrayList<Node> newNodes = new ArrayList<Node>(updatedNodes);
                virtualAppliance.setNodes(newNodes);
                // Close the transaction before calling the webservice
                if (transaction != null && transaction.isActive())
                {
                    logger
                        .debug("Virtual Appliance has nodes to be edited so the transaction will be commited ...");
                    transaction.commit();
                    logger.debug("VA nodes saved: Transaction committed!");

                }

                try
                {
                    updateNetworkResources(owner, updatedNodes, virtualappHBPojo.getIdVirtualApp());
                }
                catch (NetworkCommandException e)
                {
                    DAOFactory factory = HibernateDAOFactory.instance();
                    factory.beginConnection();
                    IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
                    NodeVirtualImageDAO nviDAO = factory.getNodeVirtualImageDAO();
                    for (Node node : updatedNodes)
                    {
                        NodeVirtualImage nvi = (NodeVirtualImage) node;
                        List<IpPoolManagementHB> listOfNICs =
                            ipPoolDAO.getPrivateNICsByVirtualMachine(nvi.getVirtualMachine()
                                .getId());
                        if (listOfNICs == null || listOfNICs.isEmpty())
                        {
                            nviDAO.makeTransient(nvi.toPojoHB());
                        }
                    }
                    factory.endConnection();
                    throw e;
                }
            }
            else
            {
                StateEnum state = originalVirtualApplianceState.toEnum();
                virtualappHBPojo.setState(state);
                virtualappHBPojo.setSubState(state);
                session.update("VirtualappHB", virtualappHBPojo);
                transaction.commit();
                dataResult.setSuccess(true);
            }
        }
        catch (HardLimitExceededException hl)
        {
            traceLog(SeverityType.MINOR, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_MODIFY,
                userSession, null, virtualappHBPojoOld.toPojo().getVirtualDataCenter().getName(),
                "Hard limit exceeded exception\nCaused by:" + hl.getMessage(),
                virtualappHBPojoOld.toPojo(), null, null, null, null);

            dataResult.setResultCode(BasicResult.HARD_LIMT_EXCEEDED);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editVirtualAppliance", hl,
                virtualAppliance.getId());
            virtualAppliance =
                updateStateInDB(virtualappOld, originalVirtualApplianceState.toEnum()).getData();

            dataResult.setData(virtualappOld);
            dataResult.setMessage(hl.getMessage());

            return dataResult;
        }
        catch (SoftLimitExceededException sl)
        {
            traceLog(SeverityType.MINOR, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_MODIFY,
                userSession, null, virtualappHBPojoOld.toPojo().getVirtualDataCenter().getName(),
                "Soft limit exceeded exception\nCaused by: " + sl.getMessage(),
                virtualappHBPojoOld.toPojo(), null, null, null, null);

            dataResult.setResultCode(BasicResult.SOFT_LIMT_EXCEEDED);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editVirtualAppliance", sl,
                virtualAppliance.getId());
            virtualAppliance =
                updateStateInDB(virtualappOld, originalVirtualApplianceState.toEnum()).getData();

            dataResult.setData(virtualappOld);
            dataResult.setMessage(sl.getMessage());

            return dataResult;
        }
        catch (NotEnoughResourcesException nl)
        {
            traceLog(SeverityType.MINOR, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_MODIFY,
                userSession, null, virtualappHBPojoOld.toPojo().getVirtualDataCenter().getName(),
                "Not enough resource on the datacenter : " + nl.getMessage(),
                virtualappHBPojoOld.toPojo(), null, null, null, null);

            dataResult.setResultCode(BasicResult.CLOUD_LIMT_EXCEEDED);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editVirtualAppliance", nl,
                virtualAppliance.getId());
            virtualAppliance =
                updateStateInDB(virtualappOld, originalVirtualApplianceState.toEnum()).getData();

            dataResult.setData(virtualappOld);
            dataResult.setMessage(nl.getMessage());

            return dataResult;
        }
        catch (SchedulerException e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_MODIFY,
                userSession, null, virtualappHBPojoOld.toPojo().getVirtualDataCenter().getName(),
                e.getMessage(), virtualappHBPojoOld.toPojo(), null, null, null, null);

            // TODO other BasicResult id
            dataResult.setResultCode(BasicResult.CLOUD_LIMT_EXCEEDED);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editVirtualAppliance", e,
                virtualAppliance.getId());

            dataResult.setMessage(e.getMessage());
            virtualAppliance =
                updateStateInDB(virtualappOld, originalVirtualApplianceState.toEnum()).getData();

            dataResult.setData(virtualappOld);
            dataResult.setMessage(e.getMessage());

            return dataResult;
        }
        catch (NetworkCommandException e)
        {
            traceLog(SeverityType.MAJOR, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_MODIFY,
                userSession, null, null, e.getMessage(), null, null, null, null, null);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editVirtualAppliance", e,
                virtualAppliance.getId());
            // virtualAppliance =
            // updateStateInDB(virtualappOld, originalVirtualApplianceState).getData();

            dataResult.setMessage(e.getMessage());
            dataResult.setData(virtualappOld);
            return dataResult;
        }
        catch (Exception e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_MODIFY,
                userSession, null, virtualappHBPojoOld.toPojo().getVirtualDataCenter().getName(),
                e.getMessage(), null, null, null, null, null);

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "editVirtualAppliance", e,
                virtualAppliance.getId());
            virtualAppliance =
                updateStateInDB(virtualappOld, originalVirtualApplianceState.toEnum()).getData();
            dataResult.setData(virtualappOld);
            return dataResult;
        }
        dataResult.setData(virtualAppliance);
        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#applyChangesVirtualAppliance(com.abiquo
     * .abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance, java.lang.Boolean)
     */
    @Override
    public BasicResult applyChangesVirtualAppliance(final UserSession userSession,
        VirtualAppliance virtualAppliance, final Boolean force)
    {
        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        dataResult.setSuccess(true);
        Session session = null;
        Transaction transaction = null;
        VirtualAppliance virtualappOld = null;
        // The VirtualAppliance's state that user sent
        State originalVirtualApplianceState = virtualAppliance.getState();
        DAOFactory factory = HibernateDAOFactory.instance();

        // Before start editing a VirtualAppliance, we must check that it is not
        // already being edited by another user
        DataResult<VirtualAppliance> currentStateAndAllow;
        try
        {

            factory.beginConnection();
            VirtualApplianceDAO dao = factory.getVirtualApplianceDAO();

            currentStateAndAllow =
                dao.checkVirtualApplianceState(virtualAppliance, StateEnum.IN_PROGRESS);

            factory.endConnection();
        }
        catch (Exception e)
        {
            // There was an error while checking the current state of the
            // VirtualAppliance. We can
            // not edit it
            errorManager.reportError(resourceManager, dataResult, "applyChangesVirtualAppliance",
                e, virtualAppliance.getId());
            dataResult.setData(virtualAppliance);
            return dataResult;
        }
        // Now the VirtualAppliance is locked for other users, and we can
        // safely edit it
        if (currentStateAndAllow.getSuccess())
        {
            try
            {
                session = HibernateUtil.getSession();
                transaction = session.beginTransaction();
                VirtualappHB virtualappHBPojo =
                    (VirtualappHB) session.get("VirtualappExtendedHB", virtualAppliance.getId());
                virtualappOld = virtualappHBPojo.toPojo();
                transaction.commit();

                createVirtualMachines(userSession, virtualAppliance, force, dataResult);
                // Getting the oldNodes for refreshing the virtual factory before applying new
                // changes
                List<Node> oldNodes = getOldNodes(virtualappOld, userSession);
                // Update the virtual appliance with the present nodes
                ArrayList<Node> nodesforRefreshing = new ArrayList<Node>(oldNodes);
                virtualappOld.setNodes(nodesforRefreshing);
                // Getting the nodes to be deployed (erasing the marked as ERASED)
                ArrayList<Node> newNodes = deleteErasedNodes(virtualAppliance);
                // Injecting possible conversions
                factory = HibernateDAOFactory.instance();
                factory.beginConnection();
                for (Node node : newNodes)
                {
                    if (node.isNodeTypeVirtualImage())
                    {
                        NodeVirtualImage nodevi = (NodeVirtualImage) node;
                        VirtualimageHB viHb = nodevi.getVirtualImage().toPojoHB();
                        VirtualmachineHB vmHb = nodevi.getVirtualMachine().toPojoHB();
                        prepareVirtualImage(viHb, vmHb);
                        nodevi.setVirtualImage(viHb.toPojo());
                        nodevi.setVirtualMachine(vmHb.toPojo());
                    }
                }
                factory.endConnection();
                virtualAppliance.setNodes(newNodes);
                if (transaction != null && transaction.isActive())
                {
                    logger
                        .debug("Virtual Appliance has nodes to be edited so the transaction will be commited ...");
                    transaction.commit();
                    logger.debug("VA nodes saved: Transaction committed!");

                }
                // Launching the refreshing operation for the virtual appliance

                // Check health of NOT_MANAGED virtual system
                // checkVMNotManagedHealth(virtualappHBPojo.toPojo(), getVirtualApplianceWs());

                BasicResult basicResult = new BasicResult();
                basicResult.setSuccess(true);

                beforeCallingVirtualFactory(virtualAppliance);

                basicResult = getVirtualApplianceWs().editVirtualAppliance(virtualAppliance);
                if (basicResult.getSuccess())
                {
                    if (!mustWaitForEvents(virtualAppliance, virtualappOld))
                    {
                        // Returning the present state of the
                        // virtualappliance from DB
                        session = HibernateUtil.getSession();
                        transaction = session.beginTransaction();

                        StateEnum currentStateEnum = StateEnum.NOT_DEPLOYED;
                        if (virtualAppliance.getNodes().size() > 0)
                        {
                            currentStateEnum = StateEnum.RUNNING;
                        }
                        State currentState = new State(currentStateEnum);

                        virtualAppliance.setState(currentState);
                        virtualAppliance.setSubState(currentState);
                        virtualappHBPojo.setState(currentStateEnum);
                        virtualappHBPojo.setSubState(currentStateEnum);
                        session.update("VirtualappHB", virtualappHBPojo);
                        transaction.commit();
                    }
                    else
                    {
                        // Forcing the state to IN PROGRESS for visual purposes
                        State state = new State(StateEnum.IN_PROGRESS);
                        virtualAppliance.setState(state);
                        State applychangesState = new State(StateEnum.APPLY_CHANGES_NEEDED);
                        virtualAppliance.setState(state);
                        virtualAppliance.setSubState(applychangesState);
                    }

                    // Log the event
                    traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
                        EventType.VAPP_MODIFY, userSession, null, virtualappOld
                            .getVirtualDataCenter().getName(), "Virtual Appliance '"
                            + virtualappOld.getName() + "' has been modified [Name: "
                            + virtualAppliance.getName() + "]", virtualappOld, null, null, null,
                        null);
                    dataResult.setMessage(basicResult.getMessage());
                    dataResult.setSuccess(basicResult.getSuccess());
                }
                else
                {
                    // Transaction failed so we restore the old values of the
                    // Virtual Appliance
                    // stored in virtualappHBPojoOld
                    // TODO A else case should be here, to properly inform user
                    // that an error
                    // happened
                    // and make an transaction.commit()

                    // Log the event
                    traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                        EventType.VAPP_MODIFY, userSession, null, virtualappOld
                            .getVirtualDataCenter().getName(), "Transaction failed", virtualappOld,
                        null, null, null, null);
                    dataResult.setMessage(basicResult.getMessage());
                    dataResult.setSuccess(basicResult.getSuccess());
                    // Perform an unlock of the VirtualAppliance ????
                    session = HibernateUtil.getSession();
                    transaction = session.beginTransaction();
                    session.update("VirtualappHB", virtualAppliance.toPojoHB());
                    transaction.commit();
                    // The error
                    errorManager.reportError(resourceManager, dataResult,
                        "applyChangesVirtualAppliance", virtualAppliance.getId());
                }
            }
            catch (VirtualFactoryHealthException e)
            {

                // Log the event
                traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_MODIFY, userSession, null, virtualAppliance
                        .getVirtualDataCenter().getName(), e.getMessage(), virtualAppliance, null,
                    null, null, null);
                virtualappOld.setSubState(new State(StateEnum.UNKNOWN));
                virtualAppliance =
                    updateOnlyStateInDB(virtualappOld, originalVirtualApplianceState.toEnum())
                        .getData();
                dataResult.setData(virtualappOld);
                dataResult.setSuccess(Boolean.FALSE);
                dataResult.setMessage(e.getMessage());

            }
            catch (Exception e)
            {
                if (transaction != null && transaction.isActive())
                {
                    transaction.rollback();
                }

                // Log the event
                traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_MODIFY, userSession, null, virtualAppliance
                        .getVirtualDataCenter().getName(), e.getMessage(), virtualAppliance, null,
                    null, null, null);
                virtualAppliance =
                    updateStateInDB(virtualappOld, originalVirtualApplianceState.toEnum())
                        .getData();
                dataResult.setData(virtualappOld);
                dataResult.setSuccess(Boolean.FALSE);
                dataResult.setMessage(e.getMessage());
                return dataResult;
            }
        }
        else
        {
            // This VirtualAppliance is being used and can not be edited now
            // We inform user of this and return the virtualAppliance with the
            // current state
            dataResult.setSuccess(false);
            dataResult.setMessage(resourceManager
                .getMessage("editVirtualAppliance.modifiedByOther"));
        }
        dataResult.setData(virtualAppliance);
        return dataResult;

    }

    /**
     * Private helper for deleting the nodes marked ERASED
     * 
     * @param virtualAppliance the virtual appliance to get the nodes marked as erased
     * @return the nodes to be deployed
     */
    private ArrayList<Node> deleteErasedNodes(final VirtualAppliance virtualAppliance)
    {
        Collection<Node> nodes = virtualAppliance.getNodes();

        ArrayList<Node> updatenodesList = new ArrayList<Node>();
        for (Node node : nodes)
        {
            NodeVirtualImage vi = (NodeVirtualImage) node;
            if (vi.getModified() != Node.NODE_ERASED)
            {
                updatenodesList.add(node);
            }
        }

        return updatenodesList;
    }

    /**
     * Updates the virtual appliance pojo deleting the nodes marked to be erased and just taking
     * into account the new ones to be deployed. This method is for refreshing purposes
     * 
     * @param session
     * @param virtualappOld
     * @param virtualappHBPojo
     * @throws RemoteServiceException
     * @throws PersistenceException
     * @throws ParserConfigurationException
     * @throws JAXBException
     * @throws DatatypeConfigurationException
     * @throws FaultException
     * @throws IOException
     * @throws SOAPException
     * @throws VirtualFactoryHealthException
     * @throws VirtualApplianceCommandException
     */
    private List<Node> getOldNodes(final VirtualAppliance virtualappOld, UserSession userSession)
        throws PersistenceException, RemoteServiceException, JAXBException,
        ParserConfigurationException, SOAPException, IOException, FaultException,
        DatatypeConfigurationException, VirtualFactoryHealthException,
        VirtualApplianceCommandException
    {
        Collection<Node> nodesList = virtualappOld.getNodes();
        List<Node> updatenodesList = new ArrayList<Node>();
        List<Node> nodesToDelete = new ArrayList<Node>();

        Integer virtualDatacenterId = virtualappOld.getVirtualDataCenter().getId();
        Integer virtualApplianceId = virtualappOld.getId();

        // Checking removed nodes

        virtualApplianceWs.checkRemovedNodes(virtualappOld);

        // Recovering virtualSystemMonitor address
        String virtualSystemMonitor =
            RemoteServiceUtils.getVirtualSystemMonitorFromVA(virtualappOld);

        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        VirtualappHB virtualappHBPojo =
            (VirtualappHB) session.get("VirtualappExtendedHB", virtualappOld.getId());

        Collection<NodeHB< ? >> nodesPojoList = virtualappHBPojo.getNodesHB();
        List<VirtualimageHB> listOfImagesToDelete = new ArrayList<VirtualimageHB>();
        for (Node node : nodesList)
        {

            session = checkOpenTransaction(session);

            // Only should arrive here NodeVirtualImage nodes
            NodeVirtualImage nodevi = (NodeVirtualImage) node;
            if (nodevi.getModified() == Node.NODE_ERASED)
            {
                NodeHB nodePojo = (NodeHB) session.get(NodeHB.class, node.getId());

                beforeDeletingNode(session, nodevi.toPojoHB());

                nodesPojoList.remove(nodePojo);
                // XXX session.delete(nodePojo);

                // For non-managed virtual images, we have to alse delete them.
                if (nodevi.toPojoHB().getVirtualImageHB().getRepository() == null)
                {
                    listOfImagesToDelete.add(nodevi.toPojoHB().getVirtualImageHB());
                }

                if (nodePojo.getType() == NodeTypeEnum.VIRTUAL_IMAGE)
                {
                    NodeVirtualImageHB nodeVi = (NodeVirtualImageHB) nodePojo;

                    if (nodeVi.getVirtualMachineHB() != null
                        && nodeVi.getVirtualMachineHB().getState() != StateEnum.NOT_DEPLOYED)
                    {
                        nodesToDelete.add(node);
                        // Delete Rasds
                        deleteRasdFromNode(session, nodeVi);
                        // Rolling back physical machine resources
                        VirtualmachineHB virtualMachineHB = nodeVi.getVirtualMachineHB();
                        VirtualMachine virtualMachine = virtualMachineHB.toPojo();
                        HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
                        logger.debug("Restoring the physical machine resources");
                        HypervisorHB hypervisorHB = virtualMachineHB.getHypervisor();
                        PhysicalmachineHB physicalMachineHB = hypervisorHB.getPhysicalMachine();
                        PhysicalMachine physicalMachine = physicalMachineHB.toPojo();
                        logger.debug("cpu used: " + virtualMachine.getCpu() + "ram used: "
                            + virtualMachine.getRam() + "hd used: " + virtualMachine.getHd());

                        VirtualMachineResourceStub vmachineResource =
                            APIStubFactory.getInstance(userSession,
                                new VirtualMachineResourceStubImpl(),
                                VirtualMachineResourceStub.class);

                        try
                        {
                            vmachineResource.deallocate(userSession, virtualDatacenterId,
                                virtualApplianceId, virtualMachine.getId());
                        }
                        catch (Exception e1)
                        {
                            throw new VirtualApplianceCommandException(e1);
                        }

                        // Don't unsuscribe from imported virtual images, because it never has
                        // suscribed.
                        if (nodeVi.getVirtualImageHB().getRepository() != null)
                        {
                            try
                            {
                                // Unsubscribing the deleted virtual machine
                                EventingSupport.unsubscribeEvent(nodeVi.getVirtualMachineHB()
                                    .toPojo(), virtualSystemMonitor);
                            }
                            catch (EventingException e)
                            {
                                logger
                                    .info(
                                        "As in the unsubscription message we are not following the WS-eventing standard this exception could be thrown:{}",
                                        e);
                            }
                        }
                    }
                    updatenodesList.add(node);
                }

                session = checkOpenTransaction(session);
                session.delete(nodePojo);
            }
            else if (nodevi.getVirtualMachine().getState().toEnum() != StateEnum.NOT_DEPLOYED)
            {
                updatenodesList.add(node);
            }

            session = checkOpenTransaction(session);
            session.getTransaction().commit();
        }

        // session = checkOpenTransaction(session);
        // session.update("VirtualappExtendedHB", virtualappHBPojo);
        // session.getTransaction().commit();

        // after all, clean all non-managed images
        deleteNonManagedImages(listOfImagesToDelete);

        virtualApplianceWs.removeNodes(nodesToDelete);

        return updatenodesList;
    }

    /**
     * Private helper to know if the VA must wait to new events
     * 
     * @param virtualAppliance the new virtual appliance
     * @param virtualappOld the old virtual appliance
     * @return
     */
    private boolean mustWaitForEvents(final VirtualAppliance virtualAppliance,
        final VirtualAppliance virtualappOld)
    {
        // Just checks if a node is added or modified
        if (virtualAppliance.getNodes().size() >= virtualappOld.getNodes().size())
        {
            // The VA must wait for events when the VA is running and a node is added
            // if (virtualAppliance.getState().toEnum() == StateEnum.RUNNING
            // && !compareNodes(virtualappOld.getNodes(), virtualAppliance.getNodes()))

            if (!compareNodes(virtualappOld.getNodes(), virtualAppliance.getNodes()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * It compares the ID of the nodes to check if a is new node
     * 
     * @param oldNodes the old nodes
     * @param newNodes the new nodes
     * @return
     */
    private boolean compareNodes(final Collection<Node> oldNodes, final Collection<Node> newNodes)
    {
        ListIterator e1 = ((List) oldNodes).listIterator();
        ListIterator e2 = ((List) newNodes).listIterator();
        while (e1.hasNext() && e2.hasNext())
        {
            Node o1 = (Node) e1.next();
            Node o2 = (Node) e2.next();
            if (!(o1 == null ? o2 == null : o1.getId() == o2.getId()))
            {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#editVirtualDataCenter(com.abiquo.abiserver
     * .pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter)
     */
    @Override
    public BasicResult editVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter)
    {
        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        BasicResult result = proxy.updateVirtualDatacenter(virtualDataCenter, resourceManager);

        if (!result.getSuccess())
        {
            BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_MODIFY, userSession, null, virtualDataCenter.getName(),
                result.getMessage(), null, null, null, null, null);
        }
        else
        {
            // Log the event
            BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_MODIFY, userSession, null, virtualDataCenter.getName(),
                "Virtual datacenter '" + virtualDataCenter.getName() + "' with a "
                    + virtualDataCenter.getHyperType() + " hypervisor has been modified [Name: "
                    + virtualDataCenter.getName() + "]", null, null, null, null, null);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualApplianceNodes(com.abiquo
     * .abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public DataResult<Collection<Node>> getVirtualApplianceNodes(
        final VirtualAppliance virtualAppliance)
    {
        DataResult<Collection<Node>> dataResult = new DataResult<Collection<Node>>();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            VirtualappHB virtualAppHB =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualAppliance.getId());

            Collection<Node> nodeList = virtualAppHB.toPojo().getNodes();

            // Building result
            dataResult.setSuccess(true);
            dataResult.setData(nodeList);
            dataResult.setMessage("VirtualAppliance nodes successfully retrieved");

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "getVirtualApplianceNodes", e,
                virtualAppliance.getId());
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualAppliancesByEnterprise(com
     * .abiquo.abiserver.pojo.user.Enterprise)
     */
    @Override
    public DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterprise(
        UserSession userSession, final Enterprise enterprise)
    {
        return new GetVirtualAppliancesList(userSession, enterprise.getId(), null)
        {

            @Override
            protected Collection<VirtualappHB> get(UserHB user, VirtualApplianceDAO dao)
            {
                return dao.getVirtualAppliancesByEnterprise(user, enterpriseId);
            }
        }.getVirtualAppliances(errorManager, resourceManager);
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.VirtualApplianceCommand#
     * getVirtualAppliancesByEnterpriseAndDatacenter(com.abiquo.abiserver.pojo.user.Enterprise,
     * com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    public DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterpriseAndDatacenter(
        UserSession userSession, final Enterprise enterprise, final DataCenter datacenter)
    {
        return new GetVirtualAppliancesList(userSession, enterprise.getId(), datacenter.getId())
        {

            @Override
            protected Collection<VirtualappHB> get(UserHB user, VirtualApplianceDAO dao)
            {
                return dao.getVirtualAppliancesByEnterpriseAndDatacenter(user, enterpriseId,
                    datacenterId);
            }
        }.getVirtualAppliances(errorManager, resourceManager);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualApplianceUpdatedLogs(com.
     * abiquo.abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public DataResult<ArrayList<Log>> getVirtualApplianceUpdatedLogs(
        final VirtualAppliance virtualAppliance)
    {
        Session session = null;
        Transaction transaction = null;
        DataResult<ArrayList<Log>> dataResult = new DataResult<ArrayList<Log>>();

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Getting the VirtualAppliance
            VirtualappHB virtualappHB =
                (VirtualappHB) session.get("VirtualappHB", virtualAppliance.getId());
            VirtualAppliance virtualApplianceUpdated = virtualappHB.toPojo();

            // Building result
            dataResult.setData(virtualApplianceUpdated.getLogs());
            dataResult.setSuccess(true);
            dataResult.setMessage(resourceManager
                .getMessage("getVirtualApplianceUpdatedLogs.success"));

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "getVirtualApplianceUpdatedLogs",
                e, virtualAppliance.getId());
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#markLogAsDeleted(com.abiquo.abiserver
     * .pojo.virtualappliance.Log)
     */
    @Override
    public BasicResult markLogAsDeleted(final Log log)
    {
        BasicResult basicResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Getting the Log to be marked as deleted
            LogHB logHB = (LogHB) session.get(LogHB.class, log.getIdLog());
            if (logHB != null)
            {
                logHB.setDeleted(1);
                session.update(logHB);

                basicResult.setSuccess(true);
                basicResult.setMessage(resourceManager.getMessage("markLogAsDeleted.success"));
            }
            else
            {
                basicResult.setMessage("This log entry no longer exists.");
            }

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, basicResult, "markLogAsDeleted", e);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualDataCentersByEnterprise(com
     * .abiquo.abiserver.pojo.user.Enterprise)
     */
    @Override
    public DataResult<Collection<VirtualDataCenter>> getVirtualDataCentersByEnterprise(
        UserSession userSession, final Enterprise enterprise)
    {
        return getVirtualDataCentersByEnterpriseAndDatacenter(userSession, enterprise, null);
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.VirtualApplianceCommand#
     * getVirtualDataCentersByEnterpriseAndDatacenter(com.abiquo.abiserver.pojo.user.Enterprise,
     * com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    public DataResult<Collection<VirtualDataCenter>> getVirtualDataCentersByEnterpriseAndDatacenter(
        UserSession userSession, final Enterprise enterprise, final DataCenter datacenter)
    {
        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        DataResult<Collection<VirtualDataCenter>> dataResult =
            proxy.getVirtualDatacenters(enterprise, datacenter);

        if (dataResult.getSuccess())
        {
            dataResult.setMessage(resourceManager.getMessage("getVirtualDataCenters.success"));
        }

        return dataResult;
    }

    private static abstract class GetVirtualAppliancesList
    {
        protected final UserSession userSession;

        protected final Integer enterpriseId;

        protected final Integer datacenterId;

        public GetVirtualAppliancesList(UserSession userSession, Integer enterpriseId,
            Integer datacenterId)
        {
            this.userSession = userSession;
            this.enterpriseId = enterpriseId;
            this.datacenterId = datacenterId;
        }

        protected abstract Collection<VirtualappHB> get(UserHB user, VirtualApplianceDAO dao);

        public DataResult<Collection<VirtualAppliance>> getVirtualAppliances(
            ErrorManager errorManager, ResourceManager resourceManager)
        {
            DataResult<Collection<VirtualAppliance>> dataResult =
                new DataResult<Collection<VirtualAppliance>>();

            DAOFactory factory = HibernateDAOFactory.instance();
            factory.beginConnection();

            VirtualApplianceDAO dao = factory.getVirtualApplianceDAO();
            UserDAO userDAO = factory.getUserDAO();

            UserHB user = userDAO.getUserByUserName(userSession.getUser());
            try
            {
                Collection<VirtualappHB> vAppsHB = get(user, dao);
                Collection<VirtualAppliance> vapps = new ArrayList<VirtualAppliance>();
                for (VirtualappHB vd : vAppsHB)
                {
                    vapps.add(vd.toPojo());
                }

                // Building result
                dataResult.setSuccess(true);
                dataResult.setData(vapps);
                dataResult.setMessage(resourceManager
                    .getMessage("getVirtualAppliancesByEnterprise.success"));
            }
            catch (Exception e)
            {
                errorManager.reportError(resourceManager, dataResult,
                    "getVirtualAppliancesByEnterprise", e);
            }
            finally
            {
                factory.endConnection();
            }

            return dataResult;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#shutdownVirtualAppliance(com.abiquo
     * .abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public DataResult<VirtualAppliance> shutdownVirtualAppliance(final UserSession userSession,
        VirtualAppliance virtualAppliance)
    {
        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        BasicResult basicResult = new BasicResult();
        basicResult.setSuccess(true);

        if (!mustUndeploy(virtualAppliance.getState().toEnum()))
        {
            errorManager.reportError(resourceManager, dataResult,
                "shutdownVirtualApplianceInvalidState", virtualAppliance.getId(), "Invalid state "
                    + virtualAppliance.getState());
            dataResult.setData(virtualAppliance);

            return dataResult;
        }

        // getting the nodes from the virtualappliance, because it comes without
        // them
        Collection<Node> nodes = getVirtualApplianceNodes(virtualAppliance).getData();
        virtualAppliance.setNodes(deleteUndeployedNodes(nodes));

        // Saving the state of the virtual appliance sent by the user
        State oldState = virtualAppliance.getState();

        // Checking the current state of the virtual appliance
        DataResult<VirtualAppliance> currentStateAndAllow;

        Integer virtualApplianceId = virtualAppliance.getId();

        try
        {
            DAOFactory factory = HibernateDAOFactory.instance();

            if (!factory.isTransactionActive())
            {
                factory.beginConnection();
                VirtualApplianceDAO dao = factory.getVirtualApplianceDAO();

                currentStateAndAllow =
                    dao.checkVirtualApplianceState(virtualAppliance, StateEnum.RUNNING);

                factory.endConnection();
            }
            else
            {
                VirtualApplianceDAO dao = factory.getVirtualApplianceDAO();

                currentStateAndAllow =
                    dao.checkVirtualApplianceState(virtualAppliance, StateEnum.RUNNING);
            }

        }
        catch (Exception e)
        {
            // There was a problem checking the state of the virtual appliance.
            // We can not
            // manipulate it
            errorManager.reportError(resourceManager, dataResult, "shutdownVirtualAppliance", e,
                virtualApplianceId);
            dataResult.setData(virtualAppliance); // dataResult.setData(oldState);
            return dataResult;
        }

        if (currentStateAndAllow.getSuccess())
        {
            try
            {
                // Check health of NOT_MANAGED virtual system and marks the nodes as unhealthy
                // checkVMNotManagedHealth(virtualAppliance, virtualApplianceWs);

                // Delete the network resource for the non-managed virtual images
                deleteNonManagedNetworkResources(virtualAppliance);

                basicResult = virtualApplianceWs.shutdownVirtualAppliance(virtualAppliance);

                if (basicResult.getSuccess())
                {
                    basicResult = virtualApplianceWs.deleteVirtualAppliance(virtualAppliance);
                    undeployVirtualMachines(userSession, virtualAppliance, dataResult);
                }
                else
                {
                    traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                        EventType.VAPP_POWEROFF, userSession, null, virtualAppliance
                            .getVirtualDataCenter().getName(), basicResult.getMessage(),
                        virtualAppliance, null, null, null, null);
                    virtualAppliance =
                        updateOnlyStateInDB(virtualAppliance, oldState.toEnum()).getData();
                    dataResult.setData(virtualAppliance);
                    dataResult.setMessage(basicResult.getMessage());
                    dataResult.setSuccess(basicResult.getSuccess());
                    return dataResult;
                }
            }
            catch (Exception e)
            {
                // Leaving the virtual appliance with its original state
                virtualAppliance = updateStateInDB(virtualAppliance, oldState.toEnum()).getData();
                errorManager.reportError(resourceManager, dataResult, "shutdownVirtualAppliance",
                    e, virtualApplianceId);
                dataResult.setData(virtualAppliance);

                traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_POWEROFF, userSession, null, virtualAppliance
                        .getVirtualDataCenter().getName(), e.getMessage(), virtualAppliance, null,
                    null, null, null);

                return dataResult;
            }

            dataResult.setMessage(basicResult.getMessage());
            dataResult.setSuccess(basicResult.getSuccess());
            if (basicResult.getSuccess())
            {
                // Everything went fine
                // Setting the new State of the Virtual Appliance
                virtualAppliance =
                    updateStateInDB(virtualAppliance, StateEnum.NOT_DEPLOYED).getData();
                dataResult.setData(virtualAppliance);

                traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_POWEROFF, userSession, null, virtualAppliance
                        .getVirtualDataCenter().getName(), null, virtualAppliance, null, null,
                    null, null);

            }
            else
            {
                traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                    EventType.VAPP_POWEROFF, userSession, null, virtualAppliance
                        .getVirtualDataCenter().getName(),
                    "There was a problem changing the state of the virtual appliance",
                    virtualAppliance, null, null, null, null);

                // There was a problem changing the state of the virtual
                // appliance
                // Leaving it with its original state
                virtualAppliance = updateStateInDB(virtualAppliance, oldState.toEnum()).getData();
                dataResult.setData(virtualAppliance);
            }

            return dataResult;
        }
        else
        {
            // The Virtual Appliance is being used by other user, or it is not
            // up to date.
            // We inform the new state
            dataResult.setSuccess(true);
            dataResult.setMessage(resourceManager
                .getMessage("shutdownVirtualAppliance.modifiedByOther"));
            dataResult.setData(currentStateAndAllow.getData());

            return dataResult;
        }
    }

    /**
     * This method deletes all the network resources of the non-managed nodes.
     * 
     * @param virtualAppliance the virtual appliance that contains all the nodes to check.
     */
    private void deleteNonManagedNetworkResources(VirtualAppliance virtualAppliance)
    {
        Collection<Node> nodes = virtualAppliance.getNodes();
        Session session = HibernateUtil.getSession();
        Transaction t = session.beginTransaction();

        for (Node node : nodes)
        {
            // For each node not managed, delete its IPs.
            NodeVirtualImage nodeVi = (NodeVirtualImage) node;
            if (!nodeVi.isManaged())
            {
                deleteRasdFromNode(session,
                    (NodeVirtualImageHB) session.get(NodeVirtualImageHB.class, nodeVi.getId()));

            }
        }

        t.commit();
    }

    /**
     * Private helper to delete the erased nodes
     * 
     * @param nodes the virtual appliances nodes
     * @return the nodes without the erased nodes
     */
    private ArrayList<Node> deleteUndeployedNodes(final Collection<Node> nodes)
    {
        ArrayList<Node> updatenodesList = new ArrayList<Node>();
        for (Node node : nodes)
        {
            if (node.isNodeTypeVirtualImage())
            {
                NodeVirtualImage nodevi = (NodeVirtualImage) node;
                if (nodevi.getVirtualMachine().getState().toEnum() != StateEnum.NOT_DEPLOYED)
                {
                    updatenodesList.add(node);
                }

            }
        }
        return updatenodesList;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#blockVirtualAppliance(com.abiquo.abiserver
     * .pojo.virtualappliance.VirtualAppliance,
     * com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum)
     */
    @Override
    public boolean blockVirtualAppliance(final VirtualAppliance virtualAppliance,
        final StateEnum subState) throws PersistenceException
    {
        DAOFactory daoFactory = HibernateDAOFactory.instance();
        boolean blocked = false;

        daoFactory.beginConnection();

        VirtualApplianceDAO applianceDAO = daoFactory.getVirtualApplianceDAO();
        VirtualappHB current = applianceDAO.findByIdNamedExtended(virtualAppliance.getId());

        if (current.getState() != StateEnum.IN_PROGRESS)
        {
            current.setState(StateEnum.IN_PROGRESS);
            current.setSubState(subState);

            applianceDAO.makePersistent(current);

            blocked = true;
        }
        else
        {
            logger.debug("Unable to block the virtual appliance.");
        }

        daoFactory.endConnection();

        return blocked;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#beforeStartVirtualAppliance(com.abiquo
     * .abiserver.pojo.authentication.UserSession,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance, java.lang.Boolean)
     */
    @Override
    public DataResult<VirtualAppliance> beforeStartVirtualAppliance(final UserSession userSession,
        final VirtualAppliance vApp, final Boolean force)
    {
        int userId = 0;

        DAOFactory daoFactory = HibernateDAOFactory.instance();

        State sourceState = vApp.getState();
        State sourceSubState = vApp.getSubState();

        try
        {
            if (!blockVirtualAppliance(vApp, StateEnum.CHECKING))
            {
                DataResult<VirtualAppliance> result = new DataResult<VirtualAppliance>();

                result.setSuccess(true);
                result.setMessage(resourceManager
                    .getMessage("startVirtualAppliance.modifiedByOther"));
                result.setData(vApp);

                return result;
            }

            daoFactory.beginConnection();

            UserDAO userDAO = daoFactory.getUserDAO();
            UserHB userHB = userDAO.findUserHBByName(userSession.getUser());
            userId = userHB.getIdUser();

            daoFactory.endConnection();
        }
        catch (Exception e)
        {
            daoFactory.rollbackConnection();

            DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();

            errorManager.reportError(resourceManager, dataResult, "startVirtualAppliance", e,
                vApp.getId());

            vApp.setState(sourceState);
            dataResult.setSuccess(false);
            dataResult.setData(vApp);
            return dataResult;
        }

        logger.debug("Starting virtual appliance " + vApp.getId());

        return startVirtualAppliance(userSession, userId, vApp, sourceState, sourceSubState, force);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#traceErrorStartingVirtualAppliance(
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance,
     * com.abiquo.abiserver.pojo.infrastructure.State,
     * com.abiquo.abiserver.pojo.infrastructure.State,
     * com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB, com.abiquo.tracer.ComponentType,
     * java.lang.String, java.lang.String, boolean, int)
     */
    // @Override
    protected DataResult<VirtualAppliance> traceErrorStartingVirtualAppliance(
        UserSession userSession, VirtualAppliance vApp, final State state, final State subState,
        final UserHB userHB, final ComponentType componentType, final String message,
        final String reportErrorKey, Exception exception, final int... resultCode)
    {
        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();

        traceLog(SeverityType.CRITICAL, componentType, EventType.VAPP_POWERON, userSession, null,
            vApp.getVirtualDataCenter().getName(), message, vApp, null, null, null, null);

        errorManager.reportError(resourceManager, dataResult, reportErrorKey, exception,
            vApp.getId());

        vApp = updateStateAndSubStateInDB(vApp, state, subState).getData();
        dataResult.setData(vApp);
        dataResult.setMessage(message);

        if (resultCode != null && resultCode.length > 0)
        {
            dataResult.setResultCode(resultCode[0]);
        }

        return dataResult;
    }

    /**
     * Private helper to update the state and the sub-state of a virtual appliance in the database.
     * 
     * @param virtualappliance The Virtual appliance to update.
     * @param state The new state to update.
     * @param subState the new SubState to update.
     * @return A basic Result with the operation result.
     */
    private DataResult<VirtualAppliance> updateStateAndSubStateInDB(
        VirtualAppliance virtualappliance, final State state, final State subState)
    {
        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = checkOpenTransaction(session).getTransaction();// session.beginTransaction();

            VirtualappHB virtualAppliance =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualappliance.getId());

            virtualAppliance.setState(state.toEnum());
            virtualAppliance.setSubState(subState.toEnum());

            session.update("VirtualappHB", virtualAppliance);

            virtualappliance = virtualAppliance.toPojo();

            transaction.commit();

            dataResult.setData(virtualappliance);
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "updateStateInDB", e);
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.VirtualApplianceCommand#startVirtualAppliance(int,
     * com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance,
     * com.abiquo.abiserver.pojo.infrastructure.State,
     * com.abiquo.abiserver.pojo.infrastructure.State, java.lang.Boolean)
     */

    public DataResult<VirtualAppliance> startVirtualAppliance(UserSession userSession,
        final int idUser, VirtualAppliance virtualAppliance, final State sourceState,
        final State sourceSubState, final Boolean force)
    {

        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();
        State inProgress = new State(StateEnum.IN_PROGRESS);
        State notDeployed = new State(StateEnum.NOT_DEPLOYED);
        // Get the user POJO from the DB
        UserHB userHB = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        factory.beginConnection();

        userHB = factory.getUserDAO().findById(idUser);
        factory.endConnection();
        
        // if the virtual appliance doesn't have nodes, return back the message
        if (virtualAppliance.getNodes().size() == 0)
        {
            updateStateAndSubStateInDB(virtualAppliance, notDeployed, notDeployed);
            traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState, sourceSubState,
                userHB, ComponentType.VIRTUAL_APPLIANCE, "The Virtual Appliance doesn't have virtual machines to deploy",
                "startVirtualAppliance", null, BasicResult.ZERO_NODES);
                
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setResultCode(BasicResult.ZERO_NODES);
            dataResult.setMessage("The Virtual Appliance doesn't have virtual machines to deploy");
            return dataResult;
        }
        
        BasicResult basicResult = null;



        updateStateAndSubStateInDB(virtualAppliance, inProgress, notDeployed);

        // Check the remote services
        try
        {
            RemoteServiceUtils.checkRemoteServicesFromVA(virtualAppliance);
        }
        catch (RemoteServiceException e)
        {
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE,
                "An error occured while checking remote service", "startVirtualAppliance", e);
        }

        try
        {
            createVirtualMachines(userSession, virtualAppliance, force, dataResult);
        }
        catch (HardLimitExceededException hl)
        {        	
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE, hl.getMessage(),
                "createVirtualMachines", hl, BasicResult.HARD_LIMT_EXCEEDED);
        }
        catch (SoftLimitExceededException sl)
        {
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE, sl.getMessage(),
                "createVirtualMachines", sl, BasicResult.SOFT_LIMT_EXCEEDED);
        }
        catch (NotEnoughResourcesException nl)
        {
            final String cause =
                String.format("There is not enough resources in datacenter "
                    + "for deploying the Virtual Appliance:%s", virtualAppliance.getName());

            dataResult =
                traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                    sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE, cause,
                    "startVirtualAppliance", nl); // , BasicResult..CLOUD_LIMT_EXCEEDED

            String message =
                String.format("%s\n%s", "The virtual appliance can not be deployed. "
                    + "Please contact your Cloud Administrator.", cause);

            dataResult.setMessage(message);
            return dataResult;
        }
        catch (VirtualImageException e)
        {
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.IMAGE_CONVERTER, e.getMessage(),
                "createVirtualMachines", e);
        }
        catch (Exception e1)
        {
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE, e1.getMessage(),
                "createVirtualMachines", e1);
        }
        finally
        {
        	undeployVirtualMachines(userSession, virtualAppliance, dataResult);
        }

        try
        {
            beforeCallingVirtualFactory(virtualAppliance);

            checkAllVirtualImagesExistOnTheRepository(virtualAppliance);
        }
        catch (Exception e)
        {
        	undeployVirtualMachines(userSession, virtualAppliance, dataResult);
        	
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE, e.getMessage(),
                "createVirtualMachines", e);
        }

        try
        {
            // Calling the webservice
            basicResult = virtualApplianceWs.startVirtualAppliance(virtualAppliance);
        }
        catch (EventingException e)
        {
        	undeployVirtualMachines(userSession, virtualAppliance, dataResult);

            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE,
                "The datacenter is not well configured. Contact your Cloud Administrator",
                "eventingError", e);
        }
        catch (Exception e)
        {
        	undeployVirtualMachines(userSession, virtualAppliance, dataResult);

            virtualApplianceWs.rollbackEventSubscription(virtualAppliance);
            return traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE,
                "It's not possible to deploy the virtual application", "startVirtualAppliance", e);

        }

        dataResult.setMessage(basicResult.getMessage());
        dataResult.setSuccess(basicResult.getSuccess());

        if (basicResult.getSuccess())
        {
            // As the event sink will update the virtual appliance state the
            // state will be in
            // progress
            State newState = new State(StateEnum.IN_PROGRESS);
            virtualAppliance.setState(newState);
            dataResult.setData(virtualAppliance);

            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_POWERON,
                userSession, null, virtualAppliance.getVirtualDataCenter().getName(), null,
                virtualAppliance, null, null, null, null);
        }
        else
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.VAPP_POWERON, userSession, null, virtualAppliance.getVirtualDataCenter()
                    .getName(), dataResult.getMessage(), virtualAppliance, null, null, null, null);

            undeployVirtualMachines(userSession, virtualAppliance, dataResult);
            
            virtualApplianceWs.rollbackEventSubscription(virtualAppliance);
            virtualAppliance = updateStateInDB(virtualAppliance, sourceState.toEnum()).getData();
            dataResult.setData(virtualAppliance);
            traceErrorStartingVirtualAppliance(userSession, virtualAppliance, sourceState,
                sourceSubState, userHB, ComponentType.VIRTUAL_APPLIANCE,
                "It's not possible to deploy the virtual application", "startVirtualAppliance",
                new Exception(dataResult.getMessage()));
        }

        return dataResult;
    }

    /**
     * use the Appliance Manager *StaticRepositoryServlet* to check if all the disk files used by
     * the virtual appliance exist on the expected repository path.
     */
    private void checkAllVirtualImagesExistOnTheRepository(VirtualAppliance vapp) throws Exception
    {
        DAOFactory factory = HibernateDAOFactory.instance();
        factory.beginConnection();
        String amLocation =
            factory.getRemoteServiceDAO().getRemoteServiceUriByType(
                vapp.getVirtualDataCenter().getIdDataCenter(), RemoteServiceType.APPLIANCE_MANAGER);
        factory.endConnection();

        RestClient client = new RestClient();

        for (Node< ? > node : vapp.getNodes())
        {
            if (node instanceof NodeVirtualImage)
            {
                NodeVirtualImage nvi = (NodeVirtualImage) node;

                // Stateful images have a specific path of the form: ip|iqn; ignore them
                if (!nvi.getVirtualImage().isImageStateful())
                {
                    String path = nvi.getVirtualImage().getPath();

                    Resource diskFileResource =
                        client.resource(String.format("%s/files/%s", amLocation, path));

                    ClientResponse response = diskFileResource.head();

                    if (response.getStatusCode() / 200 != 1)
                    {
                        String cause = "Disk file not found on the datacenter repository : " + path;
                        throw new Exception(cause);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#beforeCallingVirtualFactory(com.abiquo
     * .abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public void beforeCallingVirtualFactory(final VirtualAppliance virtualAppliance)
        throws Exception
    {
        // Do nothing by default.
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#forceRefreshVirtualApplianceState(com
     * .abiquo.abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    public BasicResult forceRefreshVirtualApplianceState(final VirtualAppliance virtualAppliance)
    {
        BasicResult basicResult = null;
        // Calling the webservice
        basicResult = getVirtualApplianceWs().forceRefreshVirtualApplianceState(virtualAppliance);
        return basicResult;
    }

    /**
     * This method update the information of a Network node This method runs extra process to call
     * when user add new nodes.
     * 
     * @param session of the transaction
     * @param nodeNPojo the node object
     * @param virtualAppliance
     * @return the node object
     * @throws SchedulerException if it happens something
     */
    private NodeHB createNodeNetworkPojo(final Session session, final NodeNetworkHB nodeNPojo,
        final VirtualAppliance virtualAppliance) throws SchedulerException
    {
        // Now... Nothing to do

        session.save("NodeNetworkHB", nodeNPojo);

        return nodeNPojo;
    }

    /**
     * Private helper to convert a infrastructure pojo node to hibernate pojo and save it to the DB
     * 
     * @param session the hibernate session
     * @param node the node created from the pojo
     * @param virtualAppliance virtual appliance of the note
     * @throws HardLimitExceededException , it the configured hard limit is exceeded
     * @throws SoftLimitExceededException , on force = false and the soft limit is exceeded.
     * @throws NotEnoughResourcesException, if doesn't exists resources on Physical DataCenter
     */
    private NodeHB createNodePojo(final Session session, final Node node,
        final VirtualAppliance virtualAppliance, final UserHB owner) throws SchedulerException,
        SoftLimitExceededException, HardLimitExceededException, NotEnoughResourcesException
    {
        NodeHB nodePojo = new NodeHB();

        // For each kind of node...
        NodeTypeEnum nodeType = node.getNodeType().toEnum();
        switch (nodeType)
        {
            case VIRTUAL_IMAGE:
                nodePojo =
                    createNodeVirtualImagePojo(session, ((NodeVirtualImage) node).toPojoHB(),
                        virtualAppliance, owner);
                break;
            case STORAGE:
                nodePojo =
                    createNodeStoragePojo(session, ((NodeStorage) node).toPojoHB(),
                        virtualAppliance);
                break;
            case NETWORK:
                nodePojo =
                    createNodeNetworkPojo(session, ((NodeNetwork) node).toPojoHB(),
                        virtualAppliance);
                break;
        }

        // Updating the visual nodes
        node.setId(nodePojo.getIdNode());

        return nodePojo;
    }

    /**
     * This method update information of a Storage node. This method runs extra process to call when
     * user add new nodes.
     * 
     * @param session of the transaction
     * @param nodeStoragePojo the node object
     * @param virtualAppliance the virtual appliance of the node
     * @return the node object
     * @throws SchedulerException if it happens something
     */
    private NodeHB createNodeStoragePojo(final Session session,
        final NodeStorageHB nodeStoragePojo, final VirtualAppliance virtualAppliance)
        throws SchedulerException
    {
        // Now... Nothing to do

        session.save("NodeStorageHB", nodeStoragePojo);

        return nodeStoragePojo;
    }

    /**
     * This method update information of a NodeVirtualImage This method runs extra process to call
     * when user add new nodes.
     * 
     * @param session of the transaction
     * @param nodeVIPojo the VirtualImage Node
     * @param virtualAppliance the virtualApp object
     * @param owner the virtualMachine's owner
     * @return the node updated
     * @throws SchedulerException if it happens something
     * @throws HardLimitExceededException , it the configured hard limit is exceeded
     * @throws SoftLimitExceededException , on force = false and the soft limit is exceeded.
     * @throws NotEnoughResourcesException, if doesn't exists resources on Physic)al DataCenter
     */
    private NodeHB<NodeVirtualImage> createNodeVirtualImagePojo(final Session session,
        final NodeVirtualImageHB nodeVIPojo, final VirtualAppliance virtualAppliance,
        final UserHB owner) throws SchedulerException, SoftLimitExceededException,
        HardLimitExceededException, NotEnoughResourcesException
    {
        VirtualimageHB virtualImage = nodeVIPojo.getVirtualImageHB();

        // Creating virtual image pojo
        VirtualimageHB vi =
            (VirtualimageHB) session.get(VirtualimageHB.class, virtualImage.getIdImage());

        // If virtual appliance has been started - create virtual machine for
        // the current node
        if (virtualAppliance.getState().toEnum() == StateEnum.RUNNING
            || virtualAppliance.getState().toEnum() == StateEnum.APPLY_CHANGES_NEEDED)
        {
            VirtualmachineHB virtualMachineHB = createEmptyVirtualMachine(nodeVIPojo, owner);
            nodeVIPojo.setVirtualMachineHB(virtualMachineHB);
            session.save(virtualMachineHB);
            // TODO Change this IF sinces it's the same code
        }
        else if (virtualAppliance.getState().toEnum() == StateEnum.NOT_DEPLOYED)
        {
            VirtualmachineHB virtualMachineHB = createEmptyVirtualMachine(nodeVIPojo, owner);
            nodeVIPojo.setVirtualMachineHB(virtualMachineHB);
            session.save(virtualMachineHB);
        }

        nodeVIPojo.setVirtualImageHB(vi);

        session.save("NodeVirtualImageHB", nodeVIPojo);

        return nodeVIPojo;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#createEmptyVirtualMachine(com.abiquo
     * .abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB,
     * com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB)
     */
    @Override
    public VirtualmachineHB createEmptyVirtualMachine(final NodeVirtualImageHB nodeVIPojo,
        final UserHB owner)
    {
        VirtualmachineHB virtualMachineHB = new VirtualmachineHB();
        VirtualimageHB virtualImageHB = nodeVIPojo.getVirtualImageHB();
        // virtualMachineHB.setIdVm(0);
        HypervisorHB hyper = null;
        virtualMachineHB.setHypervisor(hyper);
        virtualMachineHB.setImage(nodeVIPojo.getVirtualImageHB());
        virtualMachineHB.setState(StateEnum.NOT_DEPLOYED);
        String uuid = UUID.randomUUID().toString();
        virtualMachineHB.setUuid(uuid);
        virtualMachineHB.setName(uuid);
        virtualMachineHB.setDescription(virtualImageHB.getDescription());
        virtualMachineHB.setRam(virtualImageHB.getRamRequired());
        virtualMachineHB.setCpu(virtualImageHB.getCpuRequired());
        virtualMachineHB.setHd(virtualImageHB.getHdRequired());
        virtualMachineHB.setVdrpPort(0);
        virtualMachineHB.setHighDisponibility(0);
        virtualMachineHB.setIdType(VirtualmachineHB.MANAGED_VM);
        virtualMachineHB.setUserHB(owner);
        virtualMachineHB.setEnterpriseHB(owner.getEnterpriseHB());
        virtualMachineHB.setConversion(null);
        return virtualMachineHB;
    }

    // TODO javadoc
    /**
     * Create a virtual for a particular node
     * 
     * @param session the hibernate session
     * @param virtualimageHB
     * @param nodePojo
     * @param force , indicating if the SoftLimitException should be reported (on force = false)
     * @param allocatedMachines, list of machines actually allocated (if it already have hypervisor
     *            doesnt add to this list)
     * @throws HardLimitExceededException , it the configured hard limit is exceeded
     * @throws SoftLimitExceededException , on force = false and the soft limit is exceeded.
     * @throws SchedulerException if there is a problem in the process (usually not enough resources
     *             to create the virtual machine)
     * @throws NotEnoughResourcesException, if doesn't exists resources on Physical DataCenter
     */
    private void createNodeVirtualMachine(UserSession userSession, Session session,
        VirtualAppliance vapp, final VirtualimageHB virtualimageHB,
        final NodeVirtualImageHB nodePojo, List<VirtualmachineHB> allocatedMachines,
        final boolean force) throws HardLimitExceededException, SoftLimitExceededException,
        SchedulerException, NotEnoughResourcesException
    {
        VirtualmachineHB virtualMachineHB = nodePojo.getVirtualMachineHB();
        VirtualMachine virtualMachine = virtualMachineHB.toPojo();

        Integer virtualDatacenterId = vapp.getVirtualDataCenter().getId();
        Integer virtualApplianceId = vapp.getId();
        Integer virtualMachineId = virtualMachineHB.getIdVm();

        if (virtualMachineHB.getHypervisor() == null)
        {
            VirtualMachineResourceStub vmachineResource =
                APIStubFactory.getInstance(userSession, new VirtualMachineResourceStubImpl(),
                    VirtualMachineResourceStub.class);

            vmachineResource.allocate(userSession, virtualDatacenterId, virtualApplianceId,
                virtualMachineId, force);
        }
        else
        {
            updateVirtualMachineHB(virtualMachine, virtualMachineHB);
        }
        // As the API call can close the hibernate session, restoring the session
        session = checkOpenTransaction(session);

        virtualMachineHB = (VirtualmachineHB) session.get(VirtualmachineHB.class, virtualMachineId);
        nodePojo.setVirtualMachineHB(virtualMachineHB);

        allocatedMachines.add(virtualMachineHB);

        PhysicalMachine pm = virtualMachineHB.getHypervisor().getPhysicalMachine().toPojo();

        String msg =
            "Allocated virtual machine " + virtualMachineHB.getName()
                + " to be deployed in physical machine " + pm.getName();

        // Log must be traced as system. Users don't care where their machines are deployed
        traceSystemLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE, EventType.VAPP_CREATE,
            pm.getDataCenter(), vapp.getVirtualDataCenter().getName(), msg, vapp, pm.getRack(), pm);
    }

    /**
     * Updates the virtual machine HB with the virtual machine parameters got from the scheduler
     * 
     * @param virtualMachine the virtual machine
     * @param virtualMachineHB the virtual machine HB
     */
    private void updateVirtualMachineHB(final VirtualMachine virtualMachine,
        final VirtualmachineHB virtualMachineHB)
    {
        virtualMachineHB.setIdVm(virtualMachine.getId());
        HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
        if (hypervisor == null)
        {
            virtualMachineHB.setHypervisor(null);
        }
        else
        {
            virtualMachineHB.setHypervisor(hypervisor.toPojoHB());
        }
        virtualMachineHB.setState(virtualMachine.getState().toEnum());
        virtualMachineHB.setImage(virtualMachine.getVirtualImage().toPojoHB());

        virtualMachineHB.setUuid(virtualMachine.getUUID());
        virtualMachineHB.setName(virtualMachine.getName());
        virtualMachineHB.setDescription(virtualMachine.getDescription());
        virtualMachineHB.setRam(virtualMachine.getRam());
        virtualMachineHB.setCpu(virtualMachine.getCpu());
        virtualMachineHB.setHd(virtualMachine.getHd());
        virtualMachineHB.setVdrpIp(virtualMachine.getVdrpIP());
        virtualMachineHB.setVdrpPort(virtualMachine.getVdrpPort());
        virtualMachineHB.setHighDisponibility(virtualMachine.getHighDisponibility() ? 1 : 0);
        virtualMachineHB.setIdType(virtualMachine.getIdType());
        virtualMachineHB.setDatastore(virtualMachine.getDatastore().toPojoHB());
    }

    // Creates the virtual machine - helper method to startVirtualAppliance
    /**
     * This method creates the virtual machine. Is the helper method to startVirtualAppliance It the
     * hard resource allocation limit is exceeded the BasicResult result code is set to
     * HARD_LIMT_EXCEEDED.
     * 
     * @param virtualAppliance virtualApp where the virtual machines has to run
     * @param force , indicating if the virtual appliance should be started even when the soft limit
     *            is exceeded. if false and the soft limit is reached the BasicResult result code is
     *            set to SOFT_LIMT_EXCEEDED.
     * @param dataResult the result XXX unused
     * @throws HardLimitExceededException , it the configured hard limit is exceeded
     * @throws SoftLimitExceededException , on force = false and the soft limit is exceeded.
     * @throws SchedulerException if there is a problem in the process (usually not enough resources
     *             to create the virtual machine)
     * @throws NotEnoughResourcesException, if doesn't exists resources on Physical DataCenter
     */
    private void createVirtualMachines(UserSession userSession,
        final VirtualAppliance virtualAppliance, final boolean force, final DataResult dataResult)
        throws HardLimitExceededException, SoftLimitExceededException, SchedulerException,
        HibernateException, NotEnoughResourcesException, VirtualImageException
    {

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        // transaction = session.beginTransaction();
        session = checkOpenTransaction(session);

        boolean isActive = session.getTransaction().isActive();

        NodeVirtualImageHB nodeHBPojo;
        VirtualImage virtualImage;
        VirtualimageHB virtualImagePojo;

        // Recovering Nodes
        Collection<Node> nodes = getNodesFromVirtualApp(session, virtualAppliance);
        Collection<Node> nodesUpdated = new ArrayList<Node>();

        // Traverse all the nodes in "virtualAppliance

        SoftLimitExceededException softlimit = null;
        HardLimitExceededException errorAndCleanupHard = null;
        NotEnoughResourcesException errorAndCleanupEnough = null;
        List<VirtualmachineHB> deployedvms = new LinkedList<VirtualmachineHB>();

        try
        {
            for (Node node : nodes)
            {
                if (node.isNodeTypeVirtualImage())
                {
                    session = checkOpenTransaction(session);

                    nodeHBPojo =
                        (NodeVirtualImageHB) session.get(NodeVirtualImageHB.class, node.getId());

                    // Convert to virtualImage node
                    NodeVirtualImage nodeVI = (NodeVirtualImage) node;

                    virtualImage = nodeVI.getVirtualImage();

                    virtualImagePojo =
                        (VirtualimageHB) session.get(VirtualimageHB.class, virtualImage.getId());

                    try
                    {
                        createNodeVirtualMachine(userSession, session, virtualAppliance,
                            virtualImagePojo, nodeHBPojo, deployedvms, force);

                        virtualImagePojo =
                            prepareVirtualImage(virtualImagePojo, nodeHBPojo.getVirtualMachineHB());

                        node = nodeHBPojo.toPojo();

                        nodesUpdated.add(node);
                    }
                    catch (SoftLimitExceededException e)
                    {
                        softlimit = e;
                    }

                }
            }
        }
        catch (HardLimitExceededException e)
        {
            errorAndCleanupHard = e;
        }
        catch (NotEnoughResourcesException e)
        {
            errorAndCleanupEnough = e;
        }

        if (errorAndCleanupHard != null || errorAndCleanupEnough != null)
        {

            VirtualMachineResourceStub vmachineResource =
                APIStubFactory.getInstance(userSession, new VirtualMachineResourceStubImpl(),
                    VirtualMachineResourceStub.class);

            Integer virtualDatacenterId = virtualAppliance.getVirtualDataCenter().getId();
            Integer virtualApplianceId = virtualAppliance.getId();

            for (VirtualmachineHB vmDeployed : deployedvms)
            {

                if (vmDeployed.getState() == StateEnum.IN_PROGRESS)
                {
                    try
                    {
                        vmachineResource.deallocate(userSession, virtualDatacenterId,
                            virtualApplianceId, vmDeployed.getIdVm());

                        session = checkOpenTransaction(session);

                        VirtualmachineHB virtualMachineHB =
                            (VirtualmachineHB) session.get(VirtualmachineHB.class,
                                vmDeployed.getIdVm());

                        virtualMachineHB.setHypervisor(null);

                        session.getTransaction().commit();
                    }
                    catch (Exception e)
                    {
                        logger.warn("Can't deallocate a deployed machine on this transction : ", e);
                    }
                }
            }

            throw errorAndCleanupEnough != null ? errorAndCleanupEnough : errorAndCleanupHard;
        }

        if (softlimit != null)
        {
            throw softlimit;
        }

        virtualAppliance.setNodes(nodesUpdated);

        if (!isActive)
        {
            HibernateDAOFactory.getSessionFactory().getCurrentSession().getTransaction().commit();
        }

    }

    private Session checkOpenTransaction(Session session)
    {
        if (!session.isOpen())
        {
            session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        }

        Transaction tx = session.getTransaction();
        boolean isActive = tx.isActive();
        if (!isActive)
        {
            tx = session.beginTransaction();
        }
        return session;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#prepareVirtualImage(com.abiquo.abiserver
     * .business.hibernate.pojohb.virtualimage.VirtualimageHB,
     * com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB)
     */
    @Override
    public VirtualimageHB prepareVirtualImage(final VirtualimageHB virtualImage,
        final VirtualmachineHB virtualMachine)
    {
        return virtualImage;
    }

    /**
     * Private helper to undeploy the virtual machines instantiated in the virtual machine
     * 
     * @param virtualAppliance the virtual appliance to delete the virtual machines
     * @param dataResult the data result for populating the errors
     */
    private List<VirtualmachineHB> undeployVirtualMachines(UserSession userSession,
        final VirtualAppliance virtualAppliance, final DataResult<VirtualAppliance> dataResult)
    {
        VirtualMachineResourceStub vmachineResource =
            APIStubFactory.getInstance(userSession, new VirtualMachineResourceStubImpl(),
                VirtualMachineResourceStub.class);

        Transaction transaction = null;
        List<VirtualmachineHB> undeployedVMs = new ArrayList<VirtualmachineHB>();
        try
        {
            Session session = HibernateUtil.getSession();
            transaction = session.beginTransaction();
            // Recovering Nodes
            Collection<Node> nodes = getNodesFromVirtualApp(session, virtualAppliance);
            transaction.commit();

            Integer virtualDatacenterId = virtualAppliance.getVirtualDataCenter().getId();
            Integer virtualApplianceId = virtualAppliance.getId();

            for (Node node : nodes)
            {
                // Assure that the node is Virtual Image Type
                if (node.isNodeTypeVirtualImage())
                {
                    // Convert to virtualImage node
                    NodeVirtualImage nodeVI = (NodeVirtualImage) node;
                    VirtualMachine virtualMachine = nodeVI.getVirtualMachine();
                    if (virtualMachine != null)
                    {
                        logger.debug("HD used: " + virtualMachine.getHd());
                        HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
                        logger.debug("Restoring the physical machine resources");

                        session = HibernateUtil.getSession();
                        transaction = session.beginTransaction();
                        HypervisorHB hypervisorHB =
                            (HypervisorHB) session.get(HypervisorHB.class, hypervisor.getId());
                        transaction.commit();

                        PhysicalmachineHB physicalMachineHB = hypervisorHB.getPhysicalMachine();
                        PhysicalMachine physicalMachine = physicalMachineHB.toPojo();
                        logger.debug("cpu used: " + virtualMachine.getCpu() + "ram used: "
                            + virtualMachine.getRam() + "hd used: " + virtualMachine.getHd());

                        // scheduler.rollback(virtualMachine, physicalMachine);

                        try
                        {
                            vmachineResource.deallocate(userSession, virtualDatacenterId,
                                virtualApplianceId, virtualMachine.getId());
                        }
                        catch (Exception e)
                        {
                            throw new VirtualApplianceCommandException(e);
                        }

                        session = HibernateUtil.getSession();
                        transaction = session.beginTransaction();

                        // Deleting the virtual machine instance from DB
                        VirtualmachineHB vmachineHB =
                            (VirtualmachineHB) session.get(VirtualmachineHB.class,
                                virtualMachine.getId());
                        if (vmachineHB != null)
                        {
                            vmachineHB.setState(StateEnum.NOT_DEPLOYED);
                            // Hypervisor == null in order to delete the relation between
                            // virtualMachine
                            // and physicalMachine
                            vmachineHB.setHypervisor(null);
                            // Datastore == null in order to delete the relation virtualmachine
                            // datastore
                            vmachineHB.setDatastore(null);
                            if (nodeVI.getModified() == Node.NODE_ERASED)
                            {
                                NodeHB nodePojo = (NodeHB) session.get(NodeHB.class, node.getId());
                                beforeDeletingNode(session, nodeVI.toPojoHB());
                                session.delete(nodePojo);
                            }
                            else
                            {
                                session.update(vmachineHB);
                                undeployedVMs.add(vmachineHB);
                            }
                        }
                        else
                        {
                            session.update(vmachineHB);
                            undeployedVMs.add(vmachineHB);
                        }

                        transaction.commit();
                    }

                }
            }
            // transaction.commit();

            // Get the user POJO from the DB
            // UserHB userHB = SessionUtil.findUserHBByName(session.getUser());

            for (Object element : undeployedVMs)
            {
                VirtualmachineHB vm = (VirtualmachineHB) element;

                // Log the event
                traceLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DESTROY,
                    userSession, null, virtualAppliance.getVirtualDataCenter().getName(), "VM "
                        + vm.getName() + " was Undeployed", virtualAppliance, null, null, null,
                    null);

            }

        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "deleteVirtualMachines", e);
        }
        catch (VirtualApplianceCommandException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "deleteVirtualMachines", e);
        }
        return undeployedVMs;
    }

    /**
     * Private helper to delete the virtual machines instantiated in the virtual machine
     * 
     * @param virtualAppliance the virtual appliance to delete the virtual machines
     * @param basicResult the data result for populating the errors
     */
    private void deleteVirtualMachines(final VirtualAppliance virtualAppliance,
        final BasicResult basicResult)
    {

        Transaction transaction = null;
        try
        {
            Session session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Recovering Nodes
            Collection<Node> nodes = getNodesFromVirtualApp(session, virtualAppliance);

            for (Node node : nodes)
            {
                // Assure that the node is Virtual Image Type
                if (node.isNodeTypeVirtualImage())
                {
                    // Convert to virtualImage node
                    NodeVirtualImage nodeVI = (NodeVirtualImage) node;
                    VirtualMachine virtualMachine = nodeVI.getVirtualMachine();
                    if (virtualMachine != null)
                    {
                        // Deleting the virtual machine instance from DB
                        VirtualmachineHB vmachineHB =
                            (VirtualmachineHB) session.get(VirtualmachineHB.class,
                                virtualMachine.getId());
                        Set<ResourceManagementHB> resmanSet = vmachineHB.getResman();
                        for (ResourceManagementHB resourceManagementHB : resmanSet)
                        {
                            resourceManagementHB.deallocateResource();
                            session.update(resourceManagementHB);
                        }
                        session.delete(vmachineHB);
                    }

                }
            }
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, basicResult, "deleteVirtualMachines", e);
        }
        // catch (SchedulerException e)
        // {
        // if (transaction != null && transaction.isActive())
        // {
        // transaction.rollback();
        // }
        //
        // errorManager.reportError(resourceManager, basicResult, "deleteVirtualMachines", e);
        // }
    }

    /**
     * This method recover the node list of a virtualAppliance IT's used to support third party
     * methods (It doesn't create a session)
     * 
     * @throws SchedulerException when something occurs
     */
    private Collection<Node> getNodesFromVirtualApp(final Session session,
        final VirtualAppliance vApp) // throws SchedulerException
    {
        // Maybe vApp has the nodes information
        Collection<Node> nodes = vApp.getNodes();

        // If not... recover the information
        if (nodes == null)
        {
            nodes = new ArrayList<Node>();
            vApp.toPojoHB();
            Disjunction virtualAppDisjuction = Restrictions.disjunction();
            virtualAppDisjuction.add(Restrictions.eq("idVirtualApp", vApp.getId()));
            ArrayList<NodeHB> nodesHb =
                (ArrayList<NodeHB>) session.createCriteria(NodeHB.class).add(virtualAppDisjuction)
                    .list();
            for (NodeHB node : nodesHb)
            {
                nodes.add(node.toPojo());
            }
        }

        return nodes;

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#updateStateInDB(com.abiquo.abiserver
     * .pojo.virtualappliance.VirtualAppliance,
     * com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum)
     */
    @Override
    public DataResult<VirtualAppliance> updateStateInDB(VirtualAppliance virtualappliance,
        final StateEnum newState)
    {
        DataResult<VirtualAppliance> dataResult;
        dataResult = new DataResult<VirtualAppliance>();
        dataResult.setSuccess(true);
        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            VirtualappHB virtualAppPojo =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualappliance.getId());
            virtualAppPojo.setState(newState);
            virtualAppPojo.setSubState(newState);
            session.update("VirtualappHB", virtualAppPojo);
            virtualappliance = virtualAppPojo.toPojo();
            transaction.commit();

            dataResult.setData(virtualappliance);
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "updateStateInDB", e);
        }
        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#updateOnlyStateInDB(com.abiquo.abiserver
     * .pojo.virtualappliance.VirtualAppliance,
     * com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum)
     */
    @Override
    public DataResult<VirtualAppliance> updateOnlyStateInDB(VirtualAppliance virtualappliance,
        final StateEnum newState)
    {
        DataResult<VirtualAppliance> dataResult;
        dataResult = new DataResult<VirtualAppliance>();
        dataResult.setSuccess(true);
        Session session = null;
        Transaction transaction = null;
        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            VirtualappHB virtualAppPojo =
                (VirtualappHB) session.get("VirtualappExtendedHB", virtualappliance.getId());
            virtualAppPojo.setState(newState);
            session.update("VirtualappHB", virtualAppPojo);
            virtualappliance = virtualAppPojo.toPojo();
            transaction.commit();

            dataResult.setData(virtualappliance);
        }
        catch (HibernateException e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            errorManager.reportError(resourceManager, dataResult, "updateStateInDB", e);
        }
        return dataResult;
    }

    /**
     * Private helper to update the virtualAppliances
     * 
     * @param session the hibernate session
     * @param virtualappHBPojo the virtual appliance pojo to update
     * @param virtualAppliance virtualApp to update
     * @param owner virtual machine's owner
     * @return a Node list of the virtual appliance
     * @throws SchedulerException XXX
     */
    private List<Node> updateVirtualAppliancePojo(UserSession userSession, final Session session,
        final VirtualappHB virtualappHBPojo, final VirtualAppliance virtualAppliance,
        final UserHB owner) throws SchedulerException, SoftLimitExceededException,
        HardLimitExceededException, NotEnoughResourcesException, VirtualApplianceCommandException
    {
        VirtualMachineResourceStub vmachineResource =
            APIStubFactory.getInstance(userSession, new VirtualMachineResourceStubImpl(),
                VirtualMachineResourceStub.class);

        Collection<NodeHB< ? >> nodesPojoList = virtualappHBPojo.getNodesHB();

        Collection<Node> nodesList = virtualAppliance.getNodes();
        List<Node> updatenodesList = new ArrayList<Node>();

        Integer virtualDatacenterId = virtualAppliance.getVirtualDataCenter().getId();
        Integer virtualApplianceId = virtualAppliance.getId();

        for (Node node : nodesList)
        {
            switch (node.getModified())
            {
                case Node.NODE_ERASED:
                    NodeHB nodePojo = (NodeHB) session.get(NodeHB.class, node.getId());
                    nodePojo.setModified(Node.NODE_ERASED);
                    if (nodePojo.getType() == NodeTypeEnum.VIRTUAL_IMAGE)
                    {
                        NodeVirtualImageHB nodeVi = (NodeVirtualImageHB) nodePojo;

                        if (virtualAppliance.getSubState().toEnum() == StateEnum.CRASHED)
                        {
                            // Before deleting logic
                            beforeDeletingNode(session, nodeVi);
                            // Delete Rasds
                            deleteRasdFromNode(session, nodeVi);
                            // Deleting from database
                            session.delete(nodePojo);
                            // Deleting from nodes list
                            nodesPojoList.remove(nodePojo);
                            // Rolling back physical machine resources
                            VirtualmachineHB virtualMachineHB = nodeVi.getVirtualMachineHB();
                            VirtualMachine virtualMachine = virtualMachineHB.toPojo();
                            HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
                            // Restores the physical resources just if an hypervisor has been
                            // assigned to the virtual machine
                            if (hypervisor != null)
                            {
                                logger.debug("Restoring the physical machine resources");
                                HypervisorHB hypervisorHB = virtualMachineHB.getHypervisor();
                                PhysicalmachineHB physicalMachineHB =
                                    hypervisorHB.getPhysicalMachine();
                                PhysicalMachine physicalMachine = physicalMachineHB.toPojo();
                                logger.debug("cpu used: " + virtualMachine.getCpu() + "ram used: "
                                    + virtualMachine.getRam() + "hd used: "
                                    + virtualMachine.getHd());

                                Integer virtualMachineId = virtualMachine.getId();

                                vmachineResource.deallocate(userSession, virtualDatacenterId,
                                    virtualApplianceId, virtualMachineId);

                            }
                            // If there is no other virtual machine CRASHED in this virtual
                            // appliance
                            if (!isAnotherVMCrashed(virtualAppliance, virtualMachine.getName()))
                            {
                                State changesNeededSubState =
                                    new State(virtualAppliance.getState().toEnum());
                                virtualAppliance.setSubState(changesNeededSubState);
                                virtualappHBPojo.setSubState(changesNeededSubState.toEnum());
                            }

                        }
                        else if (virtualAppliance.getState().toEnum() == StateEnum.NOT_DEPLOYED)
                        {
                            // Before deleting logic
                            beforeDeletingNode(session, nodeVi);
                            // Delete Rasds
                            deleteRasdFromNode(session, nodeVi);
                            // Deleting from database
                            session.delete(nodePojo);
                            // Deleting from nodes list
                            nodesPojoList.remove(nodePojo);
                        }
                        else
                        {
                            if (virtualAppliance.getState().toEnum() == StateEnum.APPLY_CHANGES_NEEDED
                                && nodeVi.getVirtualMachineHB().getState() == StateEnum.NOT_DEPLOYED)
                            {
                                // Before deleting logic
                                beforeDeletingNode(session, nodeVi);
                                // Delete Rasds
                                deleteRasdFromNode(session, nodeVi);
                                // Deleting from database
                                session.delete(nodePojo);
                                // Deleting from nodes list
                                nodesPojoList.remove(nodePojo);
                                // Rolling back physical machine resources
                                VirtualmachineHB virtualMachineHB = nodeVi.getVirtualMachineHB();
                                VirtualMachine virtualMachine = virtualMachineHB.toPojo();
                                HyperVisor hypervisor = (HyperVisor) virtualMachine.getAssignedTo();
                                // Restores the physical resources just if an hypervisor has been
                                // assigned to the virtual machine
                                if (hypervisor != null)
                                {
                                    logger.debug("Restoring the physical machine resources");
                                    HypervisorHB hypervisorHB = virtualMachineHB.getHypervisor();
                                    PhysicalmachineHB physicalMachineHB =
                                        hypervisorHB.getPhysicalMachine();
                                    PhysicalMachine physicalMachine = physicalMachineHB.toPojo();
                                    logger.debug("cpu used: " + virtualMachine.getCpu()
                                        + "ram used: " + virtualMachine.getRam() + "hd used: "
                                        + virtualMachine.getHd());

                                    Integer virtualMachineId = virtualMachine.getId();

                                    vmachineResource.deallocate(userSession, virtualDatacenterId,
                                        virtualApplianceId, virtualMachineId);
                                }
                                State changesNeededState =
                                    new State(StateEnum.APPLY_CHANGES_NEEDED);
                                virtualAppliance.setState(changesNeededState);
                                virtualAppliance.setSubState(changesNeededState);
                                virtualappHBPojo.setState(changesNeededState.toEnum());
                                virtualappHBPojo.setSubState(changesNeededState.toEnum());
                            }
                            else
                            {
                                session.update(nodePojo);
                                updatenodesList.add(node);
                                State changesNeededState =
                                    new State(StateEnum.APPLY_CHANGES_NEEDED);
                                virtualAppliance.setState(changesNeededState);
                                virtualAppliance.setSubState(changesNeededState);
                                virtualappHBPojo.setState(changesNeededState.toEnum());
                                virtualappHBPojo.setSubState(changesNeededState.toEnum());
                            }

                        }
                    }

                    break;
                case Node.NODE_MODIFIED:
                    NodeHB nodeModified = (NodeHB) session.get(NodeHB.class, node.getId());
                    nodeModified.setModified(Node.NODE_MODIFIED);
                    nodeModified.setPosX(node.getPosX());
                    nodeModified.setPosY(node.getPosY());
                    nodeModified.setName(node.getName());
                    session.update(nodeModified);
                    updatenodesList.add(node);
                    break;
                case Node.NODE_NEW:
                    NodeHB newNode = createNodePojo(session, node, virtualAppliance, owner);
                    newNode.setModified(Node.NODE_NOT_MODIFIED);
                    nodesPojoList.add(newNode);
                    // Setting the ID for the new ID
                    node = newNode.toPojo();
                    if (virtualAppliance.getState().toEnum() != StateEnum.NOT_DEPLOYED)
                    {
                        State changesNeededState = new State(StateEnum.APPLY_CHANGES_NEEDED);
                        virtualAppliance.setState(changesNeededState);
                        virtualAppliance.setSubState(changesNeededState);
                        virtualappHBPojo.setState(changesNeededState.toEnum());
                        virtualappHBPojo.setSubState(changesNeededState.toEnum());
                    }
                    updatenodesList.add(node);
                    afterCreatingNode(session, virtualAppliance, newNode);
                    break;
                case Node.NODE_NOT_MODIFIED:
                    updatenodesList.add(node);
                    break;
                default:
                    break;
            }
        }
        session.update("VirtualappExtendedHB", virtualappHBPojo);

        return updatenodesList;

    }

    /**
     * Checks if there is another VM Crashed different from the machine name as parameter
     * 
     * @param virtualAppliance the virtual appliance to checj
     * @param vmachineName the virtual machine name
     * @return true if there is another VM crashed, false if contrary
     */
    private boolean isAnotherVMCrashed(VirtualAppliance virtualAppliance, String vmachineName)
    {
        boolean isAnotherVMCrashed = false;
        Collection<Node> nodesList = virtualAppliance.getNodes();
        for (Node node : nodesList)
        {
            if (node.isNodeTypeVirtualImage())
            {
                NodeVirtualImage nodevi = (NodeVirtualImage) node;
                if (nodevi.getVirtualMachine().getState().toEnum().compareTo(StateEnum.CRASHED) == 0)
                {
                    if (!nodevi.getVirtualMachine().getName().equals(vmachineName))
                    {
                        isAnotherVMCrashed = true;
                    }
                }
            }
        }
        return isAnotherVMCrashed;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#updateNetworkResources(com.abiquo.abiserver
     * .business.hibernate.pojohb.user.UserHB, java.util.List, java.lang.Integer)
     */
    @Override
    public void updateNetworkResources(final UserHB user, final List<Node> updatedNodes,
        final Integer vappId) throws NetworkCommandException
    {

        DAOFactory factory = HibernateDAOFactory.instance();
        try
        {

            factory.beginConnection();
            NetworkCommand netcommand = new NetworkCommandImpl();

            for (Node currentNode : updatedNodes)
            {
                if (currentNode.isNodeTypeVirtualImage())
                {
                    NodeVirtualImage nodevi = (NodeVirtualImage) currentNode;
                    if (nodevi.getVirtualMachine().getState().toEnum() == StateEnum.NOT_DEPLOYED)
                    {
                        // check if there is any private IP related to this node
                        IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
                        List<IpPoolManagementHB> listPools =
                            ipPoolDAO.getPrivateNICsByVirtualMachine(nodevi.getVirtualMachine()
                                .getId());

                        if (listPools.size() == 0)
                        {
                            VirtualDataCenterDAO vdcDAO = factory.getVirtualDataCenterDAO();
                            VirtualDataCenterHB vdcHB =
                                vdcDAO.getVirtualDatacenterFromVirtualAppliance(vappId);
                            netcommand.assignDefaultNICResource(user, vdcHB.getNetwork()
                                .getNetworkId(), nodevi.getVirtualMachine().getId());
                        }

                    }
                }
            }
            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#deleteRasdFromNode(org.hibernate.Session
     * , com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB)
     */
    @Override
    public void deleteRasdFromNode(final Session session, final NodeVirtualImageHB node)
    {
        Set<ResourceManagementHB> resmans = node.getVirtualMachineHB().getResman();

        if (resmans != null && resmans.size() > 0)
        {
            for (ResourceManagementHB resm : resmans)
            {
                if (resm.getIdResourceType().equals("10"))
                {
                    if (resm instanceof IpPoolManagementHB)
                    {
                        IpPoolManagementHB netMan = (IpPoolManagementHB) resm;

                        ResourceAllocationSettingData rasd = netMan.getRasd();
                        session.delete(rasd);
                        netMan.setRasd(null);
                        netMan.setVirtualMachine(null);
                        netMan.setVirtualApp(null);
                        netMan.setConfigureGateway(Boolean.FALSE);

                        session.saveOrUpdate(netMan);
                    }

                }
                else if (resm.getIdResourceType().equals("8"))
                {
                    resm.setVirtualApp(null);
                    resm.setVirtualMachine(null);
                    session.saveOrUpdate(resm);
                }
            }
            node.getVirtualMachineHB().getResman().clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#deleteRasdFromNode(com.abiquo.abiserver
     * .business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB)
     */
    @Override
    public void deleteRasdFromNode(final NodeVirtualImageHB node)
    {
        Session session = null;
        Transaction transaction = null;
        session = HibernateUtil.getSession();
        transaction = session.beginTransaction();

        deleteRasdFromNode(session, node);

        session.close();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getConversionByNodeVirtualImage(int)
     */
    @Override
    public VirtualImageConversions getConversionByNodeVirtualImage(final int id)
    {
        DAOFactory factory = HibernateDAOFactory.instance();

        try
        {
            factory.beginConnection();

            NodeVirtualImageHB node = factory.getNodeVirtualImageDAO().findById(id);

            factory.endConnection();
            VirtualImageConversionsHB conversion = node.getVirtualMachineHB().getConversion();
            if (conversion != null)
            {
                return conversion.toPojo();
            }
            return null;
        }
        catch (PersistenceException e)
        {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualAppliance(java.lang.Integer)
     */
    @Override
    public VirtualappHB getVirtualAppliance(final Integer virtualApplianceId)
        throws VirtualApplianceCommandException
    {
        DAOFactory factory = HibernateDAOFactory.instance();

        try
        {
            factory.beginConnection();
            VirtualApplianceDAO vappDAO = factory.getVirtualApplianceDAO();
            VirtualappHB vapp = vappDAO.findByIdNamedExtended(virtualApplianceId);
            factory.endConnection();

            return vapp;
        }
        catch (PersistenceException e)
        {
            throw new VirtualApplianceCommandException(e.getMessage(), e);
        }

    }

    private void deleteNonManagedImages(final List<VirtualimageHB> listOfImages)
        throws PersistenceException
    {
        DAOFactory factory = HibernateDAOFactory.instance();

        factory.beginConnection();
        VirtualImageDAO viDAO = factory.getVirtualImageDAO();

        for (VirtualimageHB viHB : listOfImages)
        {
            viDAO.makeTransient(viHB);
        }

        factory.endConnection();

    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#beforeDeletingNode(org.hibernate.Session
     * , com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB)
     */
    @Override
    public void beforeDeletingNode(final Session session, final NodeVirtualImageHB nodeVi)
    {
        // Override to customize behavior
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#afterCreatingNode(org.hibernate.Session
     * , com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance,
     * com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB)
     */
    @Override
    public void afterCreatingNode(final Session session, final VirtualAppliance virtualAppliance,
        final NodeHB newNode)
    {
        // Override to customize behavior
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#setVirtualApplianceWs(com.abiquo.abiserver
     * .abicloudws.IVirtualApplianceWS)
     */
    @Override
    public void setVirtualApplianceWs(IVirtualApplianceWS virtualApplianceWs)
    {
        this.virtualApplianceWs = virtualApplianceWs;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualApplianceWs()
     */
    @Override
    public IVirtualApplianceWS getVirtualApplianceWs()
    {
        return virtualApplianceWs;
    }

}
