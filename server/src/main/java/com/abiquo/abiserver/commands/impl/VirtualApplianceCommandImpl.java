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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.VirtualApplianceCommand;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub;
import com.abiquo.abiserver.commands.stub.VirtualDatacenterResourceStub;
import com.abiquo.abiserver.commands.stub.impl.VirtualApplianceResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.VirtualDatacenterResourceStubImpl;
import com.abiquo.abiserver.exception.HardLimitExceededException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.abiserver.exception.VirtualApplianceTimeoutException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDatacentersListResult;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions;
import com.abiquo.commons.amqp.impl.vsm.VSMProducer;
import com.abiquo.commons.amqp.impl.vsm.domain.VirtualSystemEvent;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;
import com.abiquo.vsm.events.VMEventType;

/**
 * This command collects all actions related to Virtual Appliances
 * 
 * @author abiquo
 */
public class VirtualApplianceCommandImpl extends BasicCommand implements VirtualApplianceCommand
{

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(VirtualApplianceCommandImpl.class);

    public VirtualApplianceCommandImpl()
    {

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
            session = HibernateUtil.getSession(true);
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
                logger
                    .trace("Unexpected error when refreshing the information of the virtual appliance");
            }

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            logger.trace(
                "Unexpected error when refreshing the information of the virtual appliance", e);
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

            virtualAppHBPojo.setState(StateEnum.NOT_ALLOCATED);
            // virtualAppHBPojo.setSubState(StateEnum.NOT_ALLOCATED);

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
                userSession, null, virtualAppliance.getVirtualDataCenter().getName(), e
                    .getMessage(), null, null, null, null, null);

            return dataResult;
        }

        return dataResult;

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

        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        DataResult<VirtualDataCenter> result =
            proxy.createVirtualDatacenter(virtualDataCenter, networkName, configuration,
                resourceManager);

        if (!result.getSuccess())
        {
            BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_CREATE, userSession, null, virtualDataCenter.getName(), result
                    .getMessage(), null, null, null, null, null);
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
            BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_DELETE, userSession, null, virtualDataCenter.getName(),
                "Error deleting Virtual datacenter '" + virtualDataCenter.getName() + "'", null,
                null, null, null, null);
        }
        else
        {

            BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_DELETE, userSession, null, virtualDataCenter.getName(),
                "Virtual datacenter '" + virtualDataCenter.getName() + "' with a "
                    + virtualDataCenter.getHyperType().getName() + " hypervisor has been deleted",
                null, null, null, null, null);
        }

        return result;
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

        // Checked en api
        // Session session = HibernateUtil.getSession();
        // Transaction tx = session.beginTransaction();
        //
        // try
        // {
        // VirtualDataCenterHB vdcHb = virtualDataCenter.toPojoHB();
        // checkLimits(vdcHb, userSession);
        // }
        // catch (HardLimitExceededException e)
        // {
        // BasicResult basicResult = new BasicResult();
        // basicResult.setSuccess(false);
        // basicResult.setMessage(resourceManager
        // .getMessage("editVirtualDataCenter.limitExceeded"));
        //
        // return basicResult;
        // }
        // finally
        // {
        // tx.commit();
        // }

        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        DataResult<VirtualDataCenter> result =
            proxy.updateVirtualDatacenter(virtualDataCenter, resourceManager);

        if (!result.getSuccess())
        {
            BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_MODIFY, userSession, null, virtualDataCenter.getName(), result
                    .getMessage(), null, null, null, null, null);
        }
        else
        {
            VirtualDataCenter vdc = result.getData();
            // Log the event
            BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.VDC_MODIFY, userSession, null, vdc.getName(), "Virtual datacenter '"
                    + vdc.getName() + "' with a " + vdc.getHyperType().getFriendlyName()
                    + " hypervisor type has been modified [Name: " + vdc.getName() + "]", null,
                null, null, null, null);
        }
        return result;
    }

    protected void checkLimits(final VirtualDataCenterHB vdc, final UserSession userSession)
        throws HardLimitExceededException
    {
        // community impl (no limits at all)
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.VirtualApplianceCommand#getVirtualApplianceNodes(com.abiquo
     * .abiserver.pojo.virtualappliance.VirtualAppliance)
     */
    @Override
    @SuppressWarnings("unchecked")
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

            String query = "from NodeHB nodes where nodes.idVirtualApp = :vappId";

            Query namedQuery =
                session.createQuery(query).setParameter("vappId", virtualAppliance.getId());

            List<NodeHB<Node< ? >>> nodes = namedQuery.list();
            Collection<Node> nodeList = new ArrayList<Node>();

            for (NodeHB<Node< ? >> n : nodes)
            {
                nodeList.add(n.toPojo());
            }

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
        final UserSession userSession, final Enterprise enterprise)
    {

        VirtualApplianceResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualApplianceResourceStubImpl(),
                VirtualApplianceResourceStub.class);
        DataResult<Collection<VirtualAppliance>> result =
            proxy.getVirtualAppliancesByEnterprise(userSession, enterprise);
        if (result.getSuccess())
        {
            result.setMessage(resourceManager
                .getMessage("getVirtualAppliancesByEnterprise.success"));
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.VirtualApplianceCommand#
     * getVirtualAppliancesByEnterpriseAndDatacenter(com.abiquo.abiserver.pojo.user.Enterprise,
     * com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    public DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterpriseAndDatacenter(
        final UserSession userSession, final Enterprise enterprise, final DataCenter datacenter)
    {
        return new GetVirtualAppliancesList(userSession, enterprise.getId(), datacenter.getId())
        {

            @Override
            protected Collection<VirtualappHB> get(final UserHB user, final VirtualApplianceDAO dao)
            {
                return dao.getVirtualAppliancesByEnterpriseAndDatacenter(user, enterpriseId,
                    datacenterId);
            }
        }.getVirtualAppliances(errorManager, resourceManager);
    }

    @Override
    public DataResult<VirtualDatacentersListResult> getVirtualDataCentersByEnterprise(
        final UserSession userSession, final Enterprise enterprise, final ListRequest listRequest)
    {
        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        DataResult<VirtualDatacentersListResult> dataResult =
            proxy.getVirtualDatacentersByEnterprise(enterprise, listRequest);

        if (dataResult.getSuccess())
        {
            dataResult.setMessage(resourceManager.getMessage("getVirtualDataCenters.success"));
        }

        return dataResult;
    }

    @Override
    public DataResult<Collection<VirtualDataCenter>> getVirtualDataCentersByEnterpriseFaster(
        final UserSession userSession, final Enterprise enterprise)
    {
        VirtualDatacenterResourceStub proxy =
            APIStubFactory.getInstance(userSession, new VirtualDatacenterResourceStubImpl(),
                VirtualDatacenterResourceStub.class);

        DataResult<Collection<VirtualDataCenter>> dataResult =
            proxy.getVirtualDatacentersByEnterprise(enterprise);

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

        public GetVirtualAppliancesList(final UserSession userSession, final Integer enterpriseId,
            final Integer datacenterId)
        {
            this.userSession = userSession;
            this.enterpriseId = enterpriseId;
            this.datacenterId = datacenterId;
        }

        protected abstract Collection<VirtualappHB> get(UserHB user, VirtualApplianceDAO dao);

        public DataResult<Collection<VirtualAppliance>> getVirtualAppliances(
            final ErrorManager errorManager, final ResourceManager resourceManager)
        {
            DataResult<Collection<VirtualAppliance>> dataResult =
                new DataResult<Collection<VirtualAppliance>>();

            DAOFactory factory = HibernateDAOFactory.instance();
            factory.beginConnection(true);

            VirtualApplianceDAO dao = factory.getVirtualApplianceDAO();
            UserDAO userDAO = factory.getUserDAO();

            UserHB user =
                userDAO.getUserByLoginAuth(userSession.getUser(), userSession.getAuthType());
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

        if (current.getState() != StateEnum.LOCKED)
        {
            current.setState(StateEnum.LOCKED);
            // current.setSubState(subState);

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

    protected DataResult<VirtualAppliance> traceErrorStartingVirtualAppliance(
        final UserSession userSession, VirtualAppliance vApp, State state, State subState,
        final UserHB userHB, final ComponentType componentType, final String message,
        final String reportErrorKey, final Exception exception, EventType eventType,
        final int... resultCode)
    {

        eventType = eventType == null ? EventType.VAPP_POWERON : eventType;
        DataResult<VirtualAppliance> dataResult = new DataResult<VirtualAppliance>();

        if (resultCode != null && resultCode.length > 0
            && resultCode[0] == BasicResult.SOFT_LIMT_EXCEEDED)
        {
            traceLog(SeverityType.INFO, componentType, eventType, userSession, null, vApp
                .getVirtualDataCenter().getName(), message, vApp, null, null, null, null);
        }
        else
        {
            traceLog(SeverityType.CRITICAL, componentType, eventType, userSession, null, vApp
                .getVirtualDataCenter().getName(), message, vApp, null, null, null, null);
        }

        errorManager.reportError(resourceManager, dataResult, reportErrorKey, exception, vApp
            .getId());

        if (exception instanceof VirtualApplianceTimeoutException)
        {
            state = new State(StateEnum.UNKNOWN);
            subState = new State(StateEnum.UNKNOWN);
        }

        vApp = updateStateAndSubStateInDB(vApp, state, subState).getData();
        dataResult.setData(vApp);
        dataResult.setMessage(message);

        if (resultCode != null && resultCode.length > 0)
        {
            dataResult.setResultCode(resultCode[0]);
        }

        return dataResult;
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
        final UserSession userSession, final VirtualAppliance vApp, final State state,
        final State subState, final UserHB userHB, final ComponentType componentType,
        final String message, final String reportErrorKey, final Exception exception,
        final int... resultCode)
    {

        return traceErrorStartingVirtualAppliance(userSession, vApp, state, subState, userHB,
            componentType, message, reportErrorKey, exception, null, resultCode);

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
            // virtualAppliance.setSubState(subState.toEnum());

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

    protected void publishVirtualMachineEvents(final Collection<Node> nodes,
        final VMEventType eventType)
    {
        try
        {
            VSMProducer producer = new VSMProducer();

            producer.openChannel();

            for (Node< ? > node : nodes)
            {
                if (node.isNodeTypeVirtualImage())
                {
                    NodeVirtualImage nvi = (NodeVirtualImage) node;
                    VirtualMachine vm = nvi.getVirtualMachine();
                    HyperVisor hv = (HyperVisor) vm.getAssignedTo();

                    VirtualSystemEvent event = new VirtualSystemEvent();
                    event.setEventType(eventType.name());
                    event.setVirtualSystemAddress(String.format("http://%s:%d/", hv.getIp(), hv
                        .getPort()));
                    event.setVirtualSystemId(vm.getName());
                    event.setVirtualSystemType(hv.getType().getName());

                    producer.publish(event);
                }
            }

            producer.closeChannel();
        }
        catch (IOException e)
        {
            logger.error("Can not connect RabbitMQ to refresh virtual machine states.");
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
        virtualMachineHB.setState(StateEnum.NOT_ALLOCATED);
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
            // virtualAppPojo.setSubState(newState);
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
                int resourceType = Integer.valueOf(resm.getIdResourceType());

                if (resourceType == CIMResourceTypeEnum.Ethernet_Adapter.getNumericResourceType())
                {
                    deleteNetworkRasd(session, resm);
                }
                else if (resourceType == CIMResourceTypeEnum.iSCSI_HBA.getNumericResourceType())
                {
                    deleteStorageRasd(session, resm);
                }
            }

            node.getVirtualMachineHB().getResman().clear();
        }
    }

    /**
     * Delete a network RASD when deleting a note or virtual appliance.
     * 
     * @param session The hibernate session.
     * @param resourceManagement The resource to delete.
     */
    protected void deleteNetworkRasd(final Session session,
        final ResourceManagementHB resourceManagement)
    {
        if (resourceManagement instanceof IpPoolManagementHB)
        {
            IpPoolManagementHB netMan = (IpPoolManagementHB) resourceManagement;

            ResourceAllocationSettingData rasd = netMan.getRasd();
            session.delete(rasd);

            if (resourceManagement.getVirtualDataCenter().getDefaultVlan().getNetworkType().equals(
                NetworkType.UNMANAGED.name()))
            {
                session.delete(netMan);
            }
            else
            {
                netMan.setRasd(null);
                netMan.setVirtualMachine(null);
                netMan.setVirtualApp(null);
                netMan.setConfigureGateway(Boolean.FALSE);

                if (rasd.getResourceSubType() != null
                    && rasd.getResourceSubType().equalsIgnoreCase("2"))
                {
                    netMan.setMac(null);
                    netMan.setVirtualDataCenter(null);
                    netMan.setName(null);
                }
                session.saveOrUpdate(netMan);
            }

        }
    }

    /**
     * Delete a storage RASD when removing a node or a virtual appliance.
     * 
     * @param session The Hibernate session.
     * @param resourceManagement The resource to delete.
     */
    protected void deleteStorageRasd(final Session session,
        final ResourceManagementHB resourceManagement)
    {
        resourceManagement.setVirtualApp(null);
        resourceManagement.setVirtualMachine(null);
        session.saveOrUpdate(resourceManagement);
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

}
