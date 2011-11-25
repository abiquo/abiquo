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

package com.abiquo.abiserver.services.flex;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.UserCommand;
import com.abiquo.abiserver.commands.VirtualApplianceCommand;
import com.abiquo.abiserver.commands.impl.UserCommandImpl;
import com.abiquo.abiserver.commands.impl.VirtualApplianceCommandImpl;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub;
import com.abiquo.abiserver.commands.stub.impl.VirtualApplianceResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.Log;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;

/**
 * This class defines all services related to Virtual Appliances management
 * 
 * @author Oliver
 */

public class VirtualApplianceService
{

    /**
     * The command to invoke.
     */
    protected VirtualApplianceCommand virtualApplianceCommand;

    protected UserCommand userCommand;

    protected VirtualApplianceResourceStub virtualApplianceResourceStub;

    /** The stub used to connect to the API. */
    private VirtualApplianceResourceStub vappStub;

    /**
     * Default constructor.
     */
    public VirtualApplianceService()
    {
        virtualApplianceResourceStub = new VirtualApplianceResourceStubImpl();
        try
        {
            virtualApplianceCommand =
                (VirtualApplianceCommand) Thread
                    .currentThread()
                    .getContextClassLoader()
                    .loadClass(
                        "com.abiquo.abiserver.commands.impl.VirtualApplianceCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            virtualApplianceCommand = new VirtualApplianceCommandImpl();
        }
        try
        {
            userCommand =
                (UserCommand) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.commands.impl.UserCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            userCommand = new UserCommandImpl();
        }

        vappStub = new VirtualApplianceResourceStubImpl();

    }

    private VirtualApplianceCommand proxyCommand(final UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, virtualApplianceCommand,
            VirtualApplianceCommand.class);
    }

    private UserCommand proxyCommand2(final UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, userCommand, UserCommand.class);
    }

    /**
     * Proxies the stub to authenticate to the API.
     * 
     * @param userSession user session.
     * @return the stub to call the API.
     */
    protected VirtualApplianceResourceStub proxyStub(final UserSession userSession)
    {
        return APIStubFactory
            .getInstance(userSession, vappStub, VirtualApplianceResourceStub.class);
    }

    // /////////////////////////
    // VirtualDataCenter

    /**
     * Retrieves a list of VirtualDataCenter that belongs to the same Enterprise
     * 
     * @param userSession The UserSession with the user that called this method
     * @param enterprise The Enterprise of which the VirtualDataCenter will be returned
     * @return a BasicResult object, containing an ArrayList<VirtualDataCenter>, with the
     *         VirtualDataCenter assigned to the enterprise
     */
    public BasicResult getVirtualDataCentersByEnterprise(final UserSession userSession,
        final Enterprise enterprise)
    {

        VirtualApplianceCommand command = proxyCommand(userSession);

        try
        {
            return command.getVirtualDataCentersByEnterprise(userSession, enterprise);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    public BasicResult getVirtualDataCentersByEnterpriseFaster(final UserSession userSession,
        final Enterprise enterprise)
    {

        VirtualApplianceCommand command = proxyCommand(userSession);

        try
        {
            return command.getVirtualDataCentersByEnterpriseFaster(userSession, enterprise);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Creates a new VirtualDataCenter in the Data Base
     * 
     * @param userSession The UserSession with the user that called this method
     * @param virtualDataCenter The VirtualDataCenter that will be created in Data Base
     * @return a DataResult object containing the VirtualDataCenter that has been created
     */
    @SuppressWarnings("unchecked")
    public BasicResult createVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter, final String networkName,
        final NetworkConfiguration configuration)
    {
        VirtualApplianceCommand command = proxyCommand(userSession);

        try
        {
            DataResult<VirtualDataCenter> dataResult =
                command.createVirtualDataCenter(userSession, virtualDataCenter, networkName,
                    configuration.toPojoHB());
            return dataResult;
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Updates an existing VirtualDataCenter with new information
     * 
     * @param userSession The UserSession with the user that called this method
     * @param virtualDataCenter The VirtualDataCenter that will be updated
     * @return a BasicResult object, with the success of the edition
     */
    public BasicResult editVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter)
    {
        VirtualApplianceCommand command = proxyCommand(userSession);

        try
        {
            return command.editVirtualDataCenter(userSession, virtualDataCenter);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Deletes a VirtualDataCenter from the DataBase. A VirtualDataCenter can only be deleted if any
     * of its Virtual Appliances are powered on
     * 
     * @param userSession The UserSession with the user that called this method
     * @param virtualDataCenter The VirtualDataCenter to be deleted
     * @return A BasicResult object with the success of the deletion. BasicResult.success = false
     *         will be returned if the VirtualDataCenter has any assigned VirtualAppliance powered
     *         on
     */
    public BasicResult deleteVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter)
    {

        VirtualApplianceCommand command = proxyCommand(userSession);

        try
        {
            return command.deleteVirtualDataCenter(userSession, virtualDataCenter);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    // /////////////////////////
    // VirtualAppliance

    /**
     * Retrieves a list of Virtual Appliances that belong to the same Enterprise The
     * VirtualAppliance retrieved will not contain their Node list, for performance purposes It will
     * also return those Virtual Appliance marked as public
     * 
     * @param userSession The UserSession object with the user that called this method
     * @param enterprise The Enterprise to retrieve the VirtualAppliance list
     * @return a DataResult<ArrayList<VirtualAppliance>> object with the VirtualAppliance that
     *         belong to the given enterprise
     * @see getVirtualApplianceNodes
     */
    public BasicResult getVirtualAppliancesByEnterprise(final UserSession userSession,
        final Enterprise enterprise)
    {
        VirtualApplianceCommand command = proxyCommand(userSession);

        return command.getVirtualAppliancesByEnterprise(userSession, enterprise);
    }

    /**
     * Given a VirtualAppliance, retrieves its node list
     * 
     * @param userSession The UserSession object with the user that called this method
     * @param virtualAppliance The VirtualAppliance to retrieve the nodes
     * @return a DataResult<ArrayList<Node>> object, containing the virtualAppliance's Nodes
     */
    public BasicResult getVirtualApplianceNodes(final UserSession userSession,
        final VirtualAppliance virtualAppliance)
    {

        // VirtualApplianceCommand command = proxyCommand(userSession);
        return proxyVirtualApplianceResourceStub(userSession).getAppNodes(virtualAppliance);
        // return command.getVirtualApplianceNodes(virtualAppliance);
    }

    /**
     * Creates a new Virtual Appliance, that belongs to the user who called this method
     * 
     * @param session
     * @param virtualAppliance
     * @return A DataResult object containing the VirtualAppliance created in the Data Base
     */
    public BasicResult createVirtualAppliance(final UserSession userSession,
        final VirtualAppliance virtualAppliance)
    {
        return proxyStub(userSession).createVirtualAppliance(virtualAppliance);
    }

    /**
     * Modifies the information of a VirtualAppliance that already exists in the Data Base
     * 
     * @param session
     * @param virtualAppliance
     * @return A DataResult object, containing an ArrayList of Node, with the Virtual Appliance's
     *         Nodes updated
     */
    public DataResult editVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {

        // VirtualApplianceCommand command = proxyCommand(session);
        return proxyVirtualApplianceResourceStub(session).updateVirtualApplianceNodes(
            virtualAppliance.getVirtualDataCenter().getId(), virtualAppliance);
        // BasicResult result = command.editVirtualAppliance(session, virtualAppliance);

        // return result;

    }

    /**
     * Deletes a VirtualAppliance that exists in the Data Base
     * 
     * @param session
     * @param virtualAppliance
     * @return a BasicResult object, containing success = true if the deletion was successful
     */
    public BasicResult deleteVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        // VirtualApplianceCommand command = proxyCommand(session);
        //
        // DataResult<User> dr = userCommand.getUser(session, session.getUserIdDb());
        // if (dr.getSuccess())
        // {
        // BasicResult check =
        // SecurityService.checkEnterpriseForPOSTMethods(dr.getData(),
        // virtualAppliance.getEnterprise());
        // if (!check.getSuccess())
        // {
        // BasicCommand.traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
        // EventType.VAPP_DELETE, session, null, virtualAppliance.getName(),
        // "Cannot delete a virtual appliance from other enterprise", null, null, null,
        // null, null);
        // return check;
        // }
        // }
        // else
        // {
        // return dr;
        // }

        return proxyVirtualApplianceResourceStub(session).deleteVirtualAppliance(virtualAppliance,
            false);
        // return command.deleteVirtualAppliance(session, virtualAppliance);
    }

    /**
     * Returns the a list with all Logs entries for a Virtual Appliance Useful to frequently update
     * the logs for a VirtualAppliance, without having to return the entire Virtual Appliance
     * 
     * @param session
     * @param virtualAppliance The VirtualAppliance which we want to return the list of logs
     * @return A DataResult object, containing an ArrayList<Log> with the list of logs for the
     *         virtualAppliance
     */
    public BasicResult getVirtualApplianceUpdatedLogs(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceCommand command = proxyCommand(session);

        return command.getVirtualApplianceUpdatedLogs(virtualAppliance);
    }

    /**
     * Marks a Log entry from a Virtual Appliance, as deleted. This Log will no longer appear in the
     * log list of the VirtualAppliance which the Log belongs to
     * 
     * @param session
     * @param log a Log object to be marked as deleted
     * @return A BasicResult object
     */
    public BasicResult markLogAsDeleted(final UserSession session, final Log log)
    {
        VirtualApplianceCommand command = proxyCommand(session);

        return command.markLogAsDeleted(log);
    }

    /**
     * Forces an state refresh in the virtual Appliance
     * 
     * @param session
     * @param virtualAppliance the virtual appliance to refresh
     * @return A BasicResult object
     */
    public BasicResult forceRefreshVirtualApplianceState(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceCommand command = proxyCommand(session);

        return command.forceRefreshVirtualApplianceState(virtualAppliance);

    }

    protected VirtualApplianceResourceStub proxyVirtualApplianceResourceStub(
        final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, virtualApplianceResourceStub,
            VirtualApplianceResourceStub.class);
    }
}
