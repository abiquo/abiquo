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

package net.undf.abicloud.events
{
    import flash.events.Event;
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.utils.customtree.CustomTreeNode;
    import net.undf.abicloud.vo.infrastructure.DataCenter;
    import net.undf.abicloud.vo.infrastructure.Datastore;
    import net.undf.abicloud.vo.infrastructure.InfrastructureElement;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachine;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachineCreation;
    import net.undf.abicloud.vo.service.RemoteService;

    public class InfrastructureEvent extends Event
    {

        /* ------------- Constants------------- */
        public static const GET_DATACENTERS:String = "getDataCentersInfrastructureEvent";
        
        public static const GET_ALLOWED_DATACENTERS:String = "getAllowedDataCentersInfrastructureEvent";

        public static const CREATE_DATACENTER:String = "createDataCenterInfrastructureEvent";

        public static const EDIT_DATACENTER:String = "editDataCenterInfrastructureEvent";

        public static const DELETE_DATACENTER:String = "deleteDataCenterInfrastructureEvent";

        public static const GET_INFRASTRUCTURE_BY_DATACENTER:String = "getInfrastructureByDataCenterInfrastructureEvent";
        
        public static const GET_RACKS_BY_DATACENTER:String = "getRacksByDatacenterInfrastructureEvent";

        public static const CHECK_INFRASTRUCTURE_BY_DATACENTER:String = "checkInfrastructureByDataCenterInfrastructureEvent";

        public static const GET_HYPERVISOR_TYPES_BY_DATACENTER:String = "getHypervisorTypesByDatacenterInfrastructureEvent";

        public static const HYPERVISOR_TYPES_BY_DATACENTER_RETRIEVED:String = "hypervisorTypesByDatacenterRetrievedInfrastructureEvent";
        
        public static const GET_HYPERVISOR_BY_PHYSICAL_MACHINE:String = "getHypervisorByPhysicalMachineInfrastructureEvent";
        
        public static const GET_PHYSICALMACHINE_BY_RACK:String = "gePhysicalMachineByRack";
        
        public static const GET_AVAILABLE_PHYSICAL_MACHINE_BY_RACK:String = "getAvailablePhysicalMachineByRack";
        
        public static const PHYSICALMACHINE_BY_RACK_RETRIEVED:String = "physicalMachineByRackRetrieved";
        
        public static const AVAILABLE_PHYSICAL_MACHINE_BY_RACK_RETRIEVED:String = "availablePhysicalMachineByRackRetrieved";

        public static const UPDATE_USED_RESOURCES_BY_DATACENTER:String = "updateUsedResourcesByDatacenterInfrastructureEvent";
        
        public static const CHECK_VIRTUAL_INFRASTRUCTURE_STATE:String = "checkVirtualInfrastructureStateInfrastructureEvent";

        public static const USED_RESOURCES_BY_DATACENTER_UPDATED:String = "usedResourcesByDatacenterUpdatedInfrastructureEvent";

        /* ------------------------------------ */

        //This event is dispatched when a data center has been edited
        public static const DATACENTER_CREATED:String = "dataCenterCreatedInfrastructureEvent";

        public static const DATACENTER_EDITED:String = "dataCenterEditedInfrastructureEvent";

        //this event is dispatched when an infrastructure element has been edited (except an Hypervisor)
        public static const INFRASTRUCTURE_ELEMENT_EDITED:String = "infrastructureElementEditedInfrastructureEvent";

        /* ------------------------------------ */

        public static const CREATE_RACK:String = "createClusterInfrastructureEvent";
        
        public static const DELETE_RACK:String = "deleteClusterInfrastructureEvent";

        public static const EDIT_RACK:String = "saveRackInfrastructureEvent";

        /* ------------------------------------ */

        public static const CREATE_PHYSICALMACHINE:String = "createPhysicalMachineInfrastructureEvent";

        public static const DELETE_PHYSICALMACHINE:String = "deletePhysicalMachineInfrastructureEvent";

        public static const EDIT_PHYSICALMACHINE:String = "editPhysicalMachineInfrastructureEvent";

        public static const ADD_DATASTORE:String = "addDatastoreInfrastructureEvent";

        public static const EDIT_DATASTORE:String = "editDatastoreInfrastructureEvent";

        /* ------------------------------------ */

        public static const CREATE_HYPERVISOR:String = "createHypervisorInfrastructureEvent";

        public static const EDIT_HYPERVISOR:String = "editHypervisorInfrastructureEvent";

        public static const DELETE_HYPERVISOR:String = "deleteHypervisorInfrastructureEvent";

        /* ------------------------------------ */

        public static const CREATE_VIRTUALMACHINE:String = "createVirtualMachineInfrastructureElementEvent";

        public static const EDIT_VIRTUALMACHINE:String = "editVirtualMachineInfrastructureElementEvent";
        
        public static const GET_VIRTUAL_MACHINE_BY_PHYSICAL_MACHINE:String = "getVirtualMachineByPhysicalMachineInfrastructureEvent";

        public static const START_VIRTUALMACHINE:String = "startVirtualMachineInfrastructureElementEvent";

        public static const PAUSE_VIRTUALMACHINE:String = "pauseVirtualMachineInfrastructureElementEvent";

        public static const REBOOT_VIRTUALMACHINE:String = "rebootVirtualMachineInfrastructureElementEvent";

        public static const SHUTDOWN_VIRTUALMACHINE:String = "shutdownVirtualMachineInfrastructureElementEvent";

        public static const FORCE_REFRESH_VIRTUAL_MACHINE_STATE:String = "forceResfreshVirtualMachineStateInfrastructureEvent";

        /* ------------------------------------ */

        public static const GET_REMOTE_SERVICE_TYPES:String = "getRemoteServiceTypesInfrastructureEvent";

        public static const CREATE_REMOTE_SERVICE:String = "createRemoteServiceInfrastructureEvent";

        public static const REMOTE_SERVICE_CREATED:String = "remoteServiceCreatedInfrastructureEvent";

        public static const EDIT_REMOTE_SERVICE:String = "editRemoteServiceInfrastructureEvent";

        public static const REMOTE_SERVICE_EDITED:String = "remoteServiceEditedInfrastructureEvent";

        public static const DELETE_REMOTE_SERVICE:String = "deleteRemoteServiceInfrastructureEvent";

        public static const REMOTE_SERVICE_DELETED:String = "remoteServiceDeletedInfrastructureEvent";

        public static const CHECK_REMOTE_SERVICE:String = "checkRemoteServiceInfrastructureEvent";

        public static const CHECK_REMOTE_SERVICE_BY_URI:String = "checkRemoteServiceByUriInfrastructureEvent";


        /* ------------- Public atributes ------------- */
        public var infrastructureElement:InfrastructureElement;

        public var dataCenter:DataCenter;

        public var physicalMachineCreation:PhysicalMachineCreation;

        public var hypervisorTypesByDatacenter:ArrayCollection;

        public var remoteService:RemoteService;

        public var datastore:Datastore;

        public var callback:Function;
        
        public var physicalMachineByRack:ArrayCollection;
        
        public var rackId:int;
        
        public var enterpriseId:int;
        
        public var filters:String;
        
        public var physicalMachine:PhysicalMachine;
        
        public var branch:CustomTreeNode;

        /* ------------- Constructor ------------- */
        public function InfrastructureEvent(type:String, bubbles:Boolean = true,
                                            cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }


    }
}