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

package net.undf.abicloud.business.managers
{
    import flash.events.EventDispatcher;

    import net.undf.abicloud.events.NetworkingEvent;
    import net.undf.abicloud.vo.networking.Network;
    import net.undf.abicloud.vo.networking.NetworkConfiguration;
    import net.undf.abicloud.vo.networking.VlanNetwork;

    [Bindable]
    public class NetworkingManager extends EventDispatcher
    {
        public function NetworkingManager()
        {

        }

        /**
         * Adds a new VlanNetwork to an existing Network
         * @param network
         * @param vlanNetwork
         *
         */
        public function addVLANToNetwork(network:Network, vlanNetwork:VlanNetwork):void
        {
            network.networks.addItem(vlanNetwork);
            if (vlanNetwork.defaultNetwork)
            {
                setDefaultVLANNetwork(network, vlanNetwork);
            }

            //Announcing that the VLAN has been created
            var event:NetworkingEvent = new NetworkingEvent(NetworkingEvent.VLAN_CREATED);
            event.vlanNetwork = vlanNetwork;
            dispatchEvent(event);
        }

        /**
         * Updates an existing VlanNetwork with new values
         * @param network The Network to which the Vlan belongs
         * @param vlanNetwork The VlanNetwork to be udpated
         * @param networkName The new network name
         * @param networkConfiguration The new network configuration values
         * @param defaultNetwork Flag indicating if this is the default network
         *
         */
        public function updateVLANNetwork(network:Network, vlanNetwork:VlanNetwork,
                                          networkName:String,                                          
                                          networkConfiguration:NetworkConfiguration,
                                          defaultNetwork:Boolean):void
        {
            //Updating values
            vlanNetwork.networkName = networkName;
            vlanNetwork.configuration = networkConfiguration;
            if (defaultNetwork)
            {
                setDefaultVLANNetwork(network, vlanNetwork);
            }
        }

        /**
         * Sets default VLAN Network of a Network
         */
        public function setDefaultVLANNetwork(network:Network, vlanNetwork:VlanNetwork):void
        {
            //First, remove the old default network
            var length:int = network.networks.length;
            for (var i:int = 0; i < length; i++)
            {
                VlanNetwork(network.networks.getItemAt(i)).defaultNetwork = false;
            }

            //Now, set the new default VLAN
            vlanNetwork.defaultNetwork = true;
        }

        /**
         * Removes a VlanNetwork form its Network
         * @param network
         * @param vlanNetwork
         *
         */
        public function removeVLANFromNetwork(network:Network, vlanNetwork:VlanNetwork):void
        {
            var position:int = network.networks.getItemIndex(vlanNetwork);
            if (position > -1)
            {
                network.networks.removeItemAt(position);

                //Announce that a VlanNetwork has been deleted
                var event:NetworkingEvent = new NetworkingEvent(NetworkingEvent.VLAN_DELETED);
                event.vlanNetwork = vlanNetwork;
                dispatchEvent(event);
            }
        }
    }
}