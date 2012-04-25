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

package com.abiquo.abiserver.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDatacentersListResult;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions;

public interface VirtualApplianceCommand
{

    /**
     * Retrieves a VirtualAppliance, with the current values in DataBase. Since a client can have an
     * old version of a VirtualAppliance, this service is useful to get the updated state of a
     * Virtual Appliance
     * 
     * @param virtualAppliance The VirtualAppliance to check.
     * @return a DataResult<VirtualAppliance> object with the last updated values in DataBase. The
     *         returned VirtualAppliance will contain its list of noded
     */
    public abstract DataResult<VirtualAppliance> checkVirtualAppliance(
        final VirtualAppliance virtualAppliance);

    /**
     * Creates a new Virtual Appliance, that belongs to the user who called this method
     * 
     * @param userSession the user connected.
     * @param virtualAppliance object
     * @return A DataResult object containing the VirtualAppliance created in the Data Base
     */
    public abstract DataResult<VirtualAppliance> createVirtualAppliance(
        final UserSession userSession, VirtualAppliance virtualAppliance);

    /**
     * Creates a new VirtualDataCenter in the Data Base
     * 
     * @param userSession The UserSession with the user that called this method
     * @param virtualDataCenter The VirtualDataCenter that will be created in Data Base
     * @return a DataResult object containing the VirtualDataCenter that has been created
     */
    public abstract DataResult<VirtualDataCenter> createVirtualDataCenter(
        final UserSession userSession, final VirtualDataCenter virtualDataCenter,
        final String networkName, final NetworkConfigurationHB configuration);

    /**
     * Deletes a VirtualDataCenter from the DataBase. A VirtualDataCenter can only be deleted if any
     * of its Virtual Appliances are powered on
     * 
     * @param virtualDataCenter The VirtualDataCenter to be deleted
     * @return A BasicResult object with the success of the deletion. BasicResult.success = false
     *         will be returned if the VirtualDataCenter has any assigned VirtualAppliance powered
     *         on
     */
    public abstract BasicResult deleteVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter);

    /**
     * Updates an existing VirtualDataCenter with new information
     * 
     * @param userSession The UserSession with the user that called this method
     * @param virtualDataCenter The VirtualDataCenter that will be updated
     * @return a BasicResult object, with the success of the edition
     */
    public abstract BasicResult editVirtualDataCenter(final UserSession userSession,
        final VirtualDataCenter virtualDataCenter);

    /**
     * Given a VirtualAppliance, retrieves its node list.
     * 
     * @param virtualAppliance The VirtualAppliance to retrieve the nodes.
     * @return a DataResult<ArrayList<Node>> object, containing the virtualAppliance's Nodes.
     */
    public abstract DataResult<Collection<Node>> getVirtualApplianceNodes(
        final VirtualAppliance virtualAppliance);

    /**
     * Retrieves a list of Virtual Appliances that belong to the same Enterprise The
     * VirtualAppliance retrieved will not contain their Node list, for performance purposes It will
     * also return those Virtual Appliance marked as public
     * 
     * @param enterprise The Enterprise to retrieve the VirtualAppliance list
     * @return a DataResult<ArrayList<VirtualAppliance>> object with the VirtualAppliance that
     *         belong to the given enterprise.
     */
    public abstract DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterprise(
        UserSession userSession, final Enterprise enterprise);

    public abstract DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterpriseAndDatacenter(
        UserSession userSession, final Enterprise enterprise, final DataCenter datacenter);

    public DataResult<VirtualDatacentersListResult> getVirtualDataCentersByEnterprise(
        final UserSession userSession, final Enterprise enterprise, final ListRequest listRequest);

    public abstract DataResult<Collection<VirtualDataCenter>> getVirtualDataCentersByEnterpriseFaster(
        UserSession userSession, final Enterprise enterprise);

    public abstract boolean blockVirtualAppliance(final VirtualAppliance virtualAppliance,
        final StateEnum subState) throws PersistenceException;

    // public abstract DataResult<VirtualAppliance> traceErrorStartingVirtualAppliance(
    // VirtualAppliance vApp, final State state, final State subState, final UserHB userHB,
    // final ComponentType componentType, final String message, final String reportErrorKey,
    // final boolean mustRecoverResources, final int... resultCode);

    /**
     * Performs a "Start" action in the Virtual Machine. It the hard resource allocation limit is
     * exceeded the BasicResult result code is set to HARD_LIMT_EXCEEDED.
     * 
     * @param virtualAppliance virtualApp to start
     * @param force , indicating if the virtual appliance should be started even when the soft limit
     *            is exceeded. if false and the soft limit is reached the BasicResult result code is
     *            set to SOFT_LIMT_EXCEEDED.
     * @param sourceState The state of the virtual appliance before to be started.
     * @param sourceSubState The sub state of the virtual appliance before to be started.
     * @return a DataResult object, with a VirtualAppliance object with all its virtual machines
     *         created
     */
    // public abstract DataResult<VirtualAppliance> startVirtualAppliance(final int idUser,
    // VirtualAppliance virtualAppliance, final State sourceState, final State sourceSubState,
    // final Boolean force);

    /**
     * Used to perform last environment configurations before calling the Virtual Factory.
     * 
     * @param virtualAppliance The Virtual Appliance being deployed.
     * @throws Exception If an error occurs.
     */
    public abstract void beforeCallingVirtualFactory(final VirtualAppliance virtualAppliance)
        throws Exception;

    /**
     * Private helper to create an empty virtual machine used for pre-instantiating a virtual
     * machine
     * 
     * @param nodeVIPojo the virtual image to attach the empty virtual machine
     * @param owner the virtual machine's owner
     * @return the virtual machine
     */
    public abstract VirtualmachineHB createEmptyVirtualMachine(final NodeVirtualImageHB nodeVIPojo,
        final UserHB owner);

    public abstract VirtualimageHB prepareVirtualImage(final VirtualimageHB virtualImage,
        final VirtualmachineHB virtualMachine);

    /**
     * Private helper to update the state in the Database
     * 
     * @param virtualappliance
     * @param newState the new state to update
     * @return a basic Result with the operation result
     */
    public abstract DataResult<VirtualAppliance> updateStateInDB(VirtualAppliance virtualappliance,
        final StateEnum newState);

    /**
     * Private helper to update the state in the Database
     * 
     * @param virtualappliance
     * @param newState the new state to update
     * @return a basic Result with the operation result
     */
    public abstract DataResult<VirtualAppliance> updateOnlyStateInDB(
        VirtualAppliance virtualappliance, final StateEnum newState);

    /**
     * This method deletes the existing rasd of a node
     * 
     * @param session the session to mantain the transaction
     * @param node the objecte to delete the rasd
     */
    public abstract void deleteRasdFromNode(final Session session, final NodeVirtualImageHB node);

    /**
     * This method deletes the existing rasd of a node.
     * 
     * @param node the objecte to delete the rasd
     */
    public abstract void deleteRasdFromNode(final NodeVirtualImageHB node);

    public abstract VirtualImageConversions getConversionByNodeVirtualImage(final int id);

    /**
     * Retrives a virtual appliance based on its id.
     * 
     * @param virtualApplianceId identifier of the virtual appliance
     * @return a {@link VirtualAppliance} object
     * @throws VirtualApplianceCommandException if can not retrieve for persitence errors
     */
    public abstract VirtualappHB getVirtualAppliance(final Integer virtualApplianceId)
        throws VirtualApplianceCommandException;

    /**
     * Called before deleting a node to customize deletion behavior.
     * 
     * @param session The Hibernate session.
     * @param nodeVi The node being deleted.
     */
    public abstract void beforeDeletingNode(final Session session, final NodeVirtualImageHB nodeVi);

    /**
     * Called after creating a new node to customize creation behavior.
     * 
     * @param session The Hibernate session.
     * @param virtualAppliance The Virtual Appliance being edited.
     * @param newNode The node being created.
     */
    public abstract void afterCreatingNode(final Session session,
        final VirtualAppliance virtualAppliance, final NodeHB newNode);
}
