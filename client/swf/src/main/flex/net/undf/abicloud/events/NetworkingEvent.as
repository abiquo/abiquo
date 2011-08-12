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
    
    import net.undf.abicloud.vo.infrastructure.DataCenter;
    import net.undf.abicloud.vo.infrastructure.VirtualMachine;
    import net.undf.abicloud.vo.networking.IPAddress;
    import net.undf.abicloud.vo.networking.IPPoolManagement;
    import net.undf.abicloud.vo.networking.Network;
    import net.undf.abicloud.vo.networking.NetworkConfiguration;
    import net.undf.abicloud.vo.networking.VlanNetwork;
    import net.undf.abicloud.vo.result.ListRequest;
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.virtualappliance.VirtualAppliance;
    import net.undf.abicloud.vo.virtualappliance.VirtualDataCenter;

    public class NetworkingEvent extends Event
    {
        /* ------------- Constants------------- */

        public static const GET_NETMASKS_BY_NETWORK_CLASS:String = "getNetmasksByNetworkClassNetworkingEvent";

        public static const GET_NETWORKS_BY_CLASS_AND_NETMASK:String = "getNetworksByClassAndNetmaskNetworkingEvent";

        public static const CREATE_VLAN:String = "createVLANNetworkingEvent";

        public static const VLAN_CREATED:String = "vlanCreatedNetworkingEvent";

        public static const EDIT_VLAN:String = "editVLANNetworkingEvent";

        public static const DELETE_VLAN:String = "deleteVLANNetworkingEvent";

        public static const VLAN_DELETED:String = "vlanDeletedNetworkingEvent";

        public static const GET_PRIVATE_IPs_BY_ENTERPRISE:String = "getPrivateIPsByEnterpriseNetworkingEvent";

        public static const GET_PRIVATE_IPs_BY_VDC:String = "getPrivateIPsByVDCNetworkingEvent";

        public static const GET_PRIVATE_IPs_BY_VLAN:String = "getPrivateIPsByVLANNetworkingEvent";

        public static const GET_NICs_BY_VIRTUAL_MACHINE:String = "getNICsByVirtualMachineNetworkingEvent";

        public static const GET_VIRTUAL_MACHINE_GATEWAY:String = "getVirtualMachineGatewayNetworkingEvent";

        public static const GET_AVAILABLE_NICs_BY_VLAN:String = "getAvailableNICsByVLANNetworkingEvent";

        public static const GET_AVAILABLE_GATEWAYS_FOR_VIRTUAL_MACHINE:String = "getAvailableGatewaysForVirtualMachineNetworkingEvent";

        public static const ASSIGN_GATEWAY_VIRTUAL_MACHINE:String = "assignGatewayVirtualMachineNetworkingEvent";

        public static const VIRTUAL_MACHINE_GATEWAY_CHANGED:String = "virtualMachineGatewayChangedNetworkingEvent";

        public static const ASSIGN_NIC_TO_VIRTUAL_MACHINE:String = "assignNICToVirtualMachineNetworkingEvent";

        public static const REMOVE_NIC_FROM_VIRTUAL_MACHINE:String = "removeNICFromVirtualMachineNetworkingEvent";

        public static const VIRTUAL_MACHINE_NICs_CHANGED:String = "virtualMachineNICsChangedNetworkingEvent";

        public static const GET_ENTERPRISES_WITH_NETWORKS_IN_DATACENTER:String = "getEnterprisesWithNetworksInDatacenterNetw";

        public static const GET_PRIVATE_NETWORKS_BY_ENTERPRISE:String = "getPrivateNetworksByEnterpriseNetworkingEvent";
        
        public static const GET_PRIVATE_NETWORKS_BY_VIRTUALDATACENTER:String = "getPrivateNetworksByVirtualDatacenterNetworkingEvent";
        
        public static const REORDER_NIC_INTO_VIRTUAL_MACHINE:String = "reorderNicIntoVirtualMachineEvent";

        /* ------------- Public atributes ------------- */
        public var datacenter:DataCenter;

        public var enterprise:Enterprise;

        public var virtualDatacenter:VirtualDataCenter;

        public var networkClass:String;

        public var netmask:IPAddress;

        public var networkName:String;

        public var networkConfiguration:NetworkConfiguration;

        public var defaultNetwork:Boolean;

        public var network:Network;

        public var vlanNetwork:VlanNetwork;

        public var listRequest:ListRequest

        public var ipAddress:IPAddress;

        public var ipPoolManagement:IPPoolManagement;

        public var virtualMachine:VirtualMachine;

        public var gateway:IPAddress;

        public var callback:Function;
        
        public var newOrder:int;
        
        public var oldOrder:int;
        
        public var ipPoolManagementId:int;
        
        public var virtualAppliance:VirtualAppliance;
        
        public var orderInList:int;

        /* ------------- Constructor ------------- */
        public function NetworkingEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}