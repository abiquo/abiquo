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

package net.undf.abicloud.controller.networking
{
    import flash.events.Event;
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.events.NetworkingEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.infrastructure.VirtualMachine;
    import net.undf.abicloud.vo.networking.IPAddress;
    import net.undf.abicloud.vo.networking.Network;
    import net.undf.abicloud.vo.networking.NetworkConfiguration;
    import net.undf.abicloud.vo.networking.VlanNetwork;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.result.ListResponse;

    public class NetworkingResultHandler extends ResultHandler
    {
        /* ------------- Constructor --------------- */
        public function NetworkingResultHandler()
        {
            super();
        }


        public function handleGetNetmasksByNetworkClass(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                //The list of Netmasks is not saved in model, but returned to who asked for it instead
                callback(DataResult(result).data);
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }

        public function handleGetNetworksByClassAndMask(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                //The list of Networks is not saved in model, but returned to who asked for it instead
                callback(DataResult(result).data[0], DataResult(result).data[1],
                         DataResult(result).data[2], DataResult(result).data[3]);
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }

        public function handleCreateVLAN(result:BasicResult, network:Network):void
        {
            if (result.success)
            {
                //Adding the new VLAN to the Network
                AbiCloudModel.getInstance().networkingManager.addVLANToNetwork(network,
                                                                               DataResult(result).data as VlanNetwork);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }


        public function handleEditVLAN(result:BasicResult, network:Network, vlanNetwork:VlanNetwork,
                                       networkName:String, networkConfiguration:NetworkConfiguration,
                                       defaultNetwork:Boolean):void
        {
            if (result.success)
            {
                //Updating VlanNetwork values in model
                AbiCloudModel.getInstance().networkingManager.updateVLANNetwork(network,
                                                                                vlanNetwork,
                                                                                networkName,
                                                                                networkConfiguration,
                                                                                defaultNetwork);
                //dispatch an event to close the edit vlan form
                AbiCloudModel.getInstance().networkingManager.dispatchEvent(new Event("vlanUpdated"));
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleDeleteVLAN(result:BasicResult, network:Network, vlanNetwork:VlanNetwork):void
        {
            if (result.success)
            {
                //Deleting the VLAN from model
                AbiCloudModel.getInstance().networkingManager.removeVLANFromNetwork(network,
                                                                                    vlanNetwork);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetPrivateIPs(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                //The list of Private IPs is not saved in model, but returned to who asked for it instead
                var listResult:ListResponse = DataResult(result).data as ListResponse;
                callback(listResult.list, listResult.totalNumEntities);
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }

        public function handleGetNICsByVirtualMachine(result:BasicResult,
                                                      callback:Function):void
        {
            if (result.success)
            {
                //The list of assigned Private IPs is not saved in model, but returned to who asked for it instead
                callback(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }

        public function handleAssignNICToVirtualMachine(result:BasicResult, virtualMachine:VirtualMachine):void
        {
            if (result.success)
            {
                //Announcing that the NIC list of the VirtualMachine has changed
                var event:NetworkingEvent = new NetworkingEvent(NetworkingEvent.VIRTUAL_MACHINE_NICs_CHANGED);
                event.virtualMachine = virtualMachine;
                AbiCloudModel.getInstance().networkingManager.dispatchEvent(event);
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }
        public function handleOrderedVirtualMachineNICsByVLAN(result:BasicResult):void
        {
            if (result.success)
            {
                //Announcing that the NIC list of the VirtualMachine has changed
                /* var event:NetworkingEvent = new NetworkingEvent(NetworkingEvent.VIRTUAL_MACHINE_NICs_CHANGED);
                event.virtualMachine = virtualMachine;
                AbiCloudModel.getInstance().networkingManager.dispatchEvent(event); */
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }

        public function handleRemoveNICFromVirtualMachine(result:BasicResult, virtualMachine:VirtualMachine):void
        {
            if (result.success)
            {
                //Announcing that the NIC list of the VirtualMachine has changed
                var event:NetworkingEvent = new NetworkingEvent(NetworkingEvent.VIRTUAL_MACHINE_NICs_CHANGED);
                event.virtualMachine = virtualMachine;
                AbiCloudModel.getInstance().networkingManager.dispatchEvent(event);
            }
            else
            {
                //There as a problem
                super.handleResult(result);
            }
        }

        public function handleGetVirtualMachineGateway(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                callback(DataResult(result).data as IPAddress);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetAvailableGatewaysForVirtualMachine(result:BasicResult,
                                                                    callback:Function):void
        {
            if (result.success)
            {
                callback(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleAssignGatewayVirtualMachine(result:BasicResult, virtualMachine:VirtualMachine,
                                                          gateway:IPAddress):void
        {
            if (result.success)
            {
                var event:NetworkingEvent = new NetworkingEvent(NetworkingEvent.VIRTUAL_MACHINE_GATEWAY_CHANGED);
                event.virtualMachine = virtualMachine;
                event.gateway = gateway;
                AbiCloudModel.getInstance().networkingManager.dispatchEvent(event);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetEnterprisesWithNetworksInDatacenter(result:BasicResult,
                                                                     callback:Function):void
        {
            if (result.success)
            {
                //Returning the list of Enterprises to who asked for them
                var listResult:ListResponse = DataResult(result).data as ListResponse;
                callback(listResult.list, listResult.totalNumEntities);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetPrivateNetworksByEnterprise(result:BasicResult,
                                                             callback:Function):void
        {
            if (result.success)
            {
                //Return the Private Networks to who asked for them
                callback(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
        
        public function handleGetPrivateNetworksByVirtualDatacenter(result:BasicResult,
                                                             callback:Function):void
        {
            if (result.success)
            {
                //Return the Private Networks to who asked for them
                callback(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
        
        public function handleSetExternalVlanAsDefaultInVirtualDatacenter(result:BasicResult):void
        {
            if (result.success)
            {
                //dispatch an event to close the edit vlan form
                AbiCloudModel.getInstance().networkingManager.dispatchEvent(new Event("vlanUpdated"));
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
        
    }

}