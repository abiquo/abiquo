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

import java.util.ArrayList;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.commands.BundleCommand;
import com.abiquo.abiserver.commands.InfrastructureCommand;
import com.abiquo.abiserver.commands.VirtualApplianceCommand;
import com.abiquo.abiserver.commands.impl.BundleCommandImpl;
import com.abiquo.abiserver.commands.impl.InfrastructureCommandImpl;
import com.abiquo.abiserver.commands.impl.VirtualApplianceCommandImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;

/**
 * This class defines a wide set of services that are considered "non-blocking services".
 * 
 * @author Oliver
 */

public class NonBlockingService
{

    /**
     * The commands related to this service
     */
    InfrastructureCommand infrastructureCommand;

    VirtualApplianceCommand virtualAppCommand;

    /**
     * Constructor The implemention of the BasicCommand
     */
    public NonBlockingService()
    {
        try
        {
            infrastructureCommand =
                (InfrastructureCommand) Thread
                    .currentThread()
                    .getContextClassLoader()
                    .loadClass(
                        "com.abiquo.abiserver.commands.impl.InfrastructureCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            infrastructureCommand = new InfrastructureCommandImpl();
        }

        try
        {
            virtualAppCommand =
                (VirtualApplianceCommand) Thread
                    .currentThread()
                    .getContextClassLoader()
                    .loadClass(
                        "com.abiquo.abiserver.commands.impl.VirtualApplianceCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            virtualAppCommand = new VirtualApplianceCommandImpl();
        }
    }

    /* ______________________________ INFRASTRUCTURE _______________________________ */

    /**
     * Retrieves an updated Infrastructre that belongs to a Datacenter
     * 
     * @param session
     * @param dataCenter The Datacenter to return its infrastructure
     * @result a DataResult object, containing an ArrayList of InfrastructureElements with the last
     *         state of the infrastructure for the given datacenter
     */
    public BasicResult checkInfrastructureByDatacenter(final UserSession session,
        final DataCenter dataCenter)
    {

        InfrastructureCommand command =
            BusinessDelegateProxy.getInstance(session, infrastructureCommand,
                InfrastructureCommand.class);

        return command.getInfrastructureByDataCenter(dataCenter);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult startVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {
        InfrastructureCommand command =
            BusinessDelegateProxy.getInstance(session, infrastructureCommand,
                InfrastructureCommand.class);

        return command.startVirtualMachine(session, virtualMachine);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult pauseVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {
        InfrastructureCommand command =
            BusinessDelegateProxy.getInstance(session, infrastructureCommand,
                InfrastructureCommand.class);

        return command.pauseVirtualMachine(session, virtualMachine);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult rebootVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {
        InfrastructureCommand command =
            BusinessDelegateProxy.getInstance(session, infrastructureCommand,
                InfrastructureCommand.class);

        return command.rebootVirtualMachine(session, virtualMachine);
    }

    /**
     * @param session
     * @param virtualMachine
     * @return A DataResult object, containing the new State for the virtualMachine
     */
    public BasicResult shutdownVirtualMachine(final UserSession session,
        final VirtualMachine virtualMachine)
    {
        InfrastructureCommand command =
            BusinessDelegateProxy.getInstance(session, infrastructureCommand,
                InfrastructureCommand.class);

        return command.shutdownVirtualMachine(session, virtualMachine);
    }

    /* ______________________________ VIRTUAL APPLIANCE _______________________________ */

    /**
     * Performs a "Start" action in the Virtual Machine
     * 
     * @param session
     * @param virtualAppliance
     * @param force, indicating if the virtual appliance should be started even when the soft limit
     *            is exceeded. if false and the soft limit is reached the BasicResult result code is
     *            set to SOFT_LIMT_EXCEEDED.
     * @return a DataResult object, with a com.abiquo.abiserver.pojo.infrastructure.State object
     *         that represents the state "Running"
     */
    public BasicResult startVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final Boolean force)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(session, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.beforeStartVirtualAppliance(session, virtualAppliance, force);
    }

    /**
     * Performs a "Shutdown" action in the Virtual Machine
     * 
     * @param session
     * @param virtualAppliance
     * @return a DataResult object, with a com.abiquo.abiserver.pojo.infrastructure.State object
     *         that represents the state "Powered Off"
     */
    public BasicResult shutdownVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(session, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.shutdownVirtualAppliance(session, virtualAppliance);
    }

    /**
     * Applies the VirtualAppliance changes in the virtual factory
     * 
     * @param session
     * @param virtualAppliance
     * @return a BasicResult object, containing success = true if the changes were applied
     *         successfully
     */
    public BasicResult applyChangesVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final Boolean force)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(session, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.applyChangesVirtualAppliance(session, virtualAppliance, force);

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

        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(session, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.deleteVirtualAppliance(session, virtualAppliance);
    }

    /**
     * Retrieves an updated list of VirtualDatacenters and Virtual Appliances that belong to the
     * same Enterprise The Virtual Appliances retrieved will not contain their list of nodes, for
     * performance purposes
     * 
     * @param userSession
     * @param enterprise The Enterprise to retrieve the VirtualAppliance list
     * @return a DataResult<ArrayList> object. The first position will contain an
     *         ArrayList<VirtualDatacenter> object, and second an ArrayList<VirtualAppliance> object
     */
    public BasicResult checkVirtualDatacentersAndAppliancesByEnterprise(
        final UserSession userSession, final Enterprise enterprise)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(userSession, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.checkVirtualDatacentersAndAppliancesByEnterprise(userSession, enterprise);
    }

    public BasicResult checkVirtualDatacentersAndAppliancesByEnterpriseAndDatacenter(
        final UserSession userSession, final Enterprise enterprise, final DataCenter datacenter)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(userSession, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.checkVirtualDatacentersAndAppliancesByEnterpriseAndDatacenter(userSession,
            enterprise, datacenter);
    }

    /**
     * Retrieves a VirtualAppliance, with the current values in DataBase. Since a client can have an
     * old version of a VirtualAppliance, this service is useful to get the updated state of a
     * Virtual Appliance
     * 
     * @param session
     * @param virtualAppliance The VirtualAppliance to check.
     * @return a DataResult<VirtualAppliance> object with the last updated values in DataBase The
     *         returned VirtualAppliance will contain its list of noded
     */
    public BasicResult checkVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(session, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.checkVirtualAppliance(virtualAppliance);
    }

    /**
     * Bundles the virtual images associated to the specified nodes of a virtual appliance.
     * 
     * @param session
     * @param virtualAppliance The VirtualAppliance to bundle.
     * @param nodes Selected nodes to bundle.
     * @param updateNodes True if the nodes must be updated with the bundled images.
     * @return A DataResult<VirtualAppliance> object with the last updated values in database.
     */
    public BasicResult bundleVirtualAppliance(final UserSession session,
        final VirtualAppliance virtualAppliance, final ArrayList<Node> nodes,
        final Boolean updateNodes)
    {
        BundleCommand bundleCommand = null;

        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            bundleCommand =
                (BundleCommand) cl.loadClass(
                    "com.abiquo.abiserver.commands.impl.BundleCommandPremium").newInstance();
        }
        catch (ClassNotFoundException ex)
        {
            bundleCommand = new BundleCommandImpl();
        }
        catch (Exception ex)
        {
            BasicResult result = new BasicResult();
            result.setSuccess(false);
            result.setMessage("Unable to instance the implementation of bundle service.");

            return result;
        }

        BundleCommand command =
            BusinessDelegateProxy.getInstance(session, bundleCommand, BundleCommand.class);

        return command
            .bundleVirtualAppliance(session, virtualAppliance, new ArrayList<Node>(nodes));
    }

    public BasicResult getVirtualApplianceLogs(final UserSession userSession,
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceCommand command =
            BusinessDelegateProxy.getInstance(userSession, virtualAppCommand,
                VirtualApplianceCommand.class);

        return command.getVirtualApplianceLogs(userSession, virtualAppliance);
    }
}
