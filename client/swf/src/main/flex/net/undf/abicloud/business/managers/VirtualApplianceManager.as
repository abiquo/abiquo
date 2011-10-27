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
    import flash.events.Event;
    import flash.events.EventDispatcher;
    
    import mx.collections.ArrayCollection;
    import mx.controls.Alert;
    import mx.resources.ResourceBundle;
    import mx.resources.ResourceManager;
    
    import net.undf.abicloud.events.VirtualApplianceEvent;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.infrastructure.State;
    import net.undf.abicloud.vo.virtualappliance.Log;
    import net.undf.abicloud.vo.virtualappliance.VirtualAppliance;
    import net.undf.abicloud.vo.virtualappliance.VirtualDataCenter;

    /**
     * Manager for Virtual Appliances
     * Stores user's virtual appliances, as well another useful information for virtual appliances creation and management
     **/
    public class VirtualApplianceManager extends EventDispatcher
    {
        /* ------------- Constructor ------------- */
        public function VirtualApplianceManager()
        {
            this._virtualAppliances = new ArrayCollection();
            this._virtualDataCenters = new ArrayCollection();
        }


        /* ------------- Public methods ------------- */

		//use to differenciate server calls type
		public var serverCallType:Boolean;
		public var callProcessComplete:Boolean;
		
        //////////////////////////////////////////////
        //Virtual Data Centers
        private var _virtualDataCenters:ArrayCollection;

        /**
         * ArrayCollection containing the list of Virtual Datacenters
         * that belongs to a given Enterprise
         */
        [Bindable(event="virtualDataCentersChange")]
        public function get virtualDataCenters():ArrayCollection
        {
            return this._virtualDataCenters;
        }

        public function set virtualDataCenters(value:ArrayCollection):void
        {
            this._virtualDataCenters = value;
            dispatchEvent(new Event("virtualDataCentersChange"));
        }

        /**
         * Retuns the VirtualDatacenter from model given its id, or null if no VirtualDatacenter exists
         * for this id
         */
        public function getVirtualDatacenterById(vdcID:int):VirtualDataCenter
        {
            var length:int = this._virtualDataCenters.length;
            var i:int;
            for (i = 0; i < length; i++)
            {
                if (VirtualDataCenter(_virtualDataCenters.getItemAt(i)).id == vdcID)
                    return _virtualDataCenters.getItemAt(i) as VirtualDataCenter;
            }

            //VirtualDatacenter not found for this id
            return null;
        }

        /**
         * Adds a new VirtualDataCenter to the VirtualDataCenter list
         * Usually, the VirtualDataCenter is first created in server, before calling this method,
         * to ensure data consistency
         * @param virtualDataCenter The VirtualDataCenter that will be added to the list
         *
         */
        public function addVirtualDataCenter(virtualDataCenter:VirtualDataCenter):void
        {
            this._virtualDataCenters.addItem(virtualDataCenter);
            var event:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_DATACENTER_ADDED);
            event.virtualDataCenter = virtualDataCenter;
            dispatchEvent(event);
        }

        /**
         * Removes a VirtualDataCenter from the virtualDataCenters list.
         * Usually, the VirtualDataCenter is first removed from the server, before calling this method
         * @param virtualDataCenter The VirtualDataCenter that will be removed from the list
         *
         */
        public function deleteVirtualDataCenter(virtualDataCenter:VirtualDataCenter):void
        {
            //Check that the VDC to delete exists in model
            var vdcToDelete:VirtualDataCenter = getVirtualDatacenterById(virtualDataCenter.id);

            if (vdcToDelete)
            {
                var index:int = this._virtualDataCenters.getItemIndex(virtualDataCenter);
                _virtualDataCenters.removeItemAt(index);
                var event:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_DATACENTER_DELETED);
                event.virtualDataCenter = vdcToDelete;
                dispatchEvent(event);
            }
        }

        /**
         * Updated a VirtualDataCenter from the model with new values
         * @param vdcNewValues A VirtualDataCenter object containing the new values
         *
         */
        public function editVirtualDataCenter(vdcNewValues:VirtualDataCenter):void
        {
            var vdcToEdit:VirtualDataCenter = getVirtualDatacenterById(vdcNewValues.id);

            if (vdcToEdit)
            {
                //Updating the virtual data center without modifying its memory address
                vdcToEdit.id = vdcNewValues.id;
                vdcToEdit.name = vdcNewValues.name;
                vdcToEdit.idDataCenter = vdcNewValues.idDataCenter;
                vdcToEdit.enterprise = vdcNewValues.enterprise;
                vdcToEdit.hyperType = vdcNewValues.hyperType;

                //Announcing that this virtual data center has been updated
                var event:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_DATACENTER_EDITED);
                event.virtualDataCenter = vdcToEdit;
                dispatchEvent(event);
            }
        }

        /**
         *  Returns the number of VirtualAppliances (inside _virtualAppliances) that are assigned to a given
         * VirtualDataCenter
         * @param virtualDataCenter The VirtualDataCenter to know its number of VirtualAppliances
         *
         */
        public function numberOfVirtualAppliances(virtualDataCenter:VirtualDataCenter):int
        {
            var length:int = this._virtualAppliances.length;
            var i:int;
            var numberOfVirtualAppliances:int = 0;
            for (i = 0; i < length; i++)
            {
                if (VirtualAppliance(this._virtualAppliances.getItemAt(i)).virtualDataCenter.id == virtualDataCenter.id)
                    numberOfVirtualAppliances++;
            }

            return numberOfVirtualAppliances;
        }

        /**
         * Shows, using an AbilcoudAlert, the DHCP configuration for a given VirtualDataCenter
         */
        [ResourceBundle("VirtualAppliance")]
        private var rb:ResourceBundle;

        public function showVirtualDataCenterDHCPConf(dhcpConf:String, virtualDataCenter:VirtualDataCenter):void
        {
            AbiCloudAlert.showConfirmation(ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                   "ALERT_DHCP_TITLE_LABEL"),
                                           ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                   "ALERT_DHCP_HEADER_LABEL"),
                                           dhcpConf, Alert.OK, null, true);
        }


        //////////////////////////////////////////////
        //Virtual Appliances

        private var _virtualAppliances:ArrayCollection;

        /**
         * ArrayCollection containing a list of Virtual Appliances
         * This list may contain all virtual appliances that belongs to an Enterprise,
         * or only Virtual Appliances that belong to a given VirtualDataCenter
         **/
        [Bindable(event="virtualAppliancesChange")]
        public function get virtualAppliances():ArrayCollection
        {
            return this._virtualAppliances;
        }

        public function set virtualAppliances(array:ArrayCollection):void
        {
            this._virtualAppliances = array;
            dispatchEvent(new Event("virtualAppliancesChange"));
        }

        /**
         * Retuns the VirtualAppliance from model given its id, or null if no VirtualAppliance exists
         * for this id
         */
        public function getVirtualApplianceById(vaID:int):VirtualAppliance
        {
            var length:int = _virtualAppliances.length;
            var i:int;
            for (i = 0; i < length; i++)
            {
                if (VirtualAppliance(_virtualAppliances.getItemAt(i)).id == vaID)
                    return _virtualAppliances.getItemAt(i) as VirtualAppliance;
            }

            //VirtualAppliance not found for this id
            return null;
        }

        /**
         * Updates a VirtualAppliance from model with new values
         */
        public function updateVAWithNewValues(vaNewValues:VirtualAppliance):VirtualAppliance
        {
            var vaToUpdate:VirtualAppliance = getVirtualApplianceById(vaNewValues.id);
            if (vaToUpdate)
            {
                //Updating the virtualAppliance with the new values, without modifying its memory address
                vaToUpdate.id = vaNewValues.id;
                vaToUpdate.enterprise = vaNewValues.enterprise;
                vaToUpdate.error = vaNewValues.error;
                vaToUpdate.highDisponibility = vaNewValues.highDisponibility;
                vaToUpdate.isPublic = vaNewValues.isPublic;
                vaToUpdate.logs = vaNewValues.logs;
                vaToUpdate.name = vaNewValues.name;
                vaToUpdate.nodeConnections = vaNewValues.nodeConnections;
                vaToUpdate.state = vaNewValues.state;
                vaToUpdate.virtualDataCenter = vaNewValues.virtualDataCenter;
                vaToUpdate.nodes = vaNewValues.nodes;

                return vaToUpdate;
            }
            else
            {
                //No VirtualAppliance found
                return null;
            }
        }

        /**
         * Sets the node list of a virtual appliance
         * @param virtualAppliance The VirtualAppliance, that exists in virtualAppliance list, to set its list of nodes
         * @param nodes ArrayCollection containing the nodes for the virtualAppliance
         *
         */
        public function setVirtualApplianceNodes(virtualAppliance:VirtualAppliance,
                                                 nodes:ArrayCollection):void
        {
            var virtualApplianceToUpdate:VirtualAppliance = getVirtualApplianceById(virtualAppliance.id);
            if (virtualApplianceToUpdate)
            {
                virtualApplianceToUpdate.nodes = nodes;

                //Announcing that this VirtualAppliance has ready its list of nodes
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_APPLIANCE_NODES_RETRIEVED,
                                                                                            false);
                virtualApplianceEvent.virtualAppliance = virtualApplianceToUpdate;
                dispatchEvent(virtualApplianceEvent);
            }
        }

        /**
         * Adds a new virtual appliance to the list of virtual appliances
         **/
        public function addVirtualAppliance(newVirtualAppliance:VirtualAppliance):void
        {
            if (!this._virtualAppliances.contains(newVirtualAppliance))
            {
                this._virtualAppliances.addItem(newVirtualAppliance);
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_APPLIANCE_CREATED,
                                                                                            false);
                virtualApplianceEvent.virtualAppliance = newVirtualAppliance;
                dispatchEvent(virtualApplianceEvent);
            }
        }


        /**
         * Deletes the given virtual appliance from the virtual appliances list
         **/
        public function deleteVirtualAppliance(virtualAppliance:VirtualAppliance):void
        {
            var vaToDelete:VirtualAppliance = getVirtualApplianceById(virtualAppliance.id);
            if (vaToDelete)
            {
                var index:int = this._virtualAppliances.getItemIndex(vaToDelete);
                this._virtualAppliances.removeItemAt(index);
                var event:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_APPLIANCE_DELETED);
                event.virtualAppliance = virtualAppliance;
                dispatchEvent(event);
            }
        }


        /**
         * Refreshes the list of virtual appliances when one has been edited
         * For the Virtual Appliance that has been edited, refreshes its Nodes list
         **/
        public function editVirtualAppliance(vaNewValues:VirtualAppliance):void
        {
            var vaEdited:VirtualAppliance = updateVAWithNewValues(vaNewValues);

            if (vaEdited)
            {
                //Announcing that this virtual appliance has been updated
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_APPLIANCE_EDITED);
                virtualApplianceEvent.virtualAppliance = vaEdited;
                dispatchEvent(virtualApplianceEvent);
            }
        }


        /**
         * Changes a Virtual Appliances state to State.LOCKED
         */
        public function setVirtualApplianceInProgress(virtualAppliance:VirtualAppliance):void
        {
            //To avoid the application crashes when the virtual appliance is null
            if(virtualAppliance){
	            var vaToUpdate:VirtualAppliance = getVirtualApplianceById(virtualAppliance.id);
	
	            if (vaToUpdate)
	            {
	                vaToUpdate.state = new State(State.LOCKED);
	            }            	
            }
        }

        public function setVirtualAppliancePoweredOff(virtualAppliance:VirtualAppliance):void
        {
            var vaToUpdate:VirtualAppliance = getVirtualApplianceById(virtualAppliance.id);

            if (vaToUpdate)
            {
                vaToUpdate.state = new State(State.OFF);
            }
        }

        public function setVirtualApplianceApplyChangesNeeded(virtualAppliance:VirtualAppliance):void
        {
            var vaToUpdate:VirtualAppliance = getVirtualApplianceById(virtualAppliance.id);

            if (vaToUpdate)
            {
                vaToUpdate.state = new State(State.NEEDS_SYNC);
            }
        }

        /**
         * Updates Log list of a Virtual Appliance
         */
        public function setVirtualApplianceUpdatedLogs(virtualAppliance:VirtualAppliance,
                                                       logs:ArrayCollection):void
        {
            var vaToUpdate:VirtualAppliance = getVirtualApplianceById(virtualAppliance.id);
            if (vaToUpdate)
            {
                vaToUpdate.logs = logs;
            }
        }

        /**
         * Marks a log as deleted and removes it from the its VirtualAppliance log list
         */
        public function markLogAsDeleted(virtualAppliance:VirtualAppliance, log:Log):void
        {
            log.deleted = 1;

            var index:int = virtualAppliance.logs.getItemIndex(log);
            if (index > -1)
                virtualAppliance.logs.removeItemAt(index);
        }

        /**
         * Updates the whole list of model's VirtualDatacenters and Appliances
         */
        public function checkVirtualDatacentersAndAppliances(virtualDatacentersChecked:ArrayCollection,
                                                             virtualAppliancesChecked:ArrayCollection):void
        {
            this._virtualDataCenters = virtualDatacentersChecked;
            this._virtualAppliances = virtualAppliancesChecked;

            //Announcing that VirtualDatacenters and Appliances list has been updated
            var event:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_DATACENTERS_AND_APPLIANCES_CHECKED);
            dispatchEvent(event)
        }

        public function changeVirtualApplianceState(vaNewValues:VirtualAppliance):void
        {
            var vaChanged:VirtualAppliance = updateVAWithNewValues(vaNewValues);
        }

        /**
         * Updates a given Virtual Appliance with new values just retrieved from the server
         */
        public function checkVirtualAppliance(vaNewValues:VirtualAppliance):void
        {
            var vaChecked:VirtualAppliance = updateVAWithNewValues(vaNewValues);
            if (vaChecked)
            {
                //Announcing that this VirtualAppliance has been checked
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_APPLIANCE_CHECKED);
                virtualApplianceEvent.virtualAppliance = vaChecked;
                dispatchEvent(virtualApplianceEvent);
            }
        }

        /**
         * When a bundle from a Virtual Appliance is created, updates the Virtual Appliance, and notifies the operation
         */
        public function virtualApplianceBundleCreated(vaNewValues:VirtualAppliance):void
        {
            var vaUpdated:VirtualAppliance = updateVAWithNewValues(vaNewValues);
            if (vaUpdated)
            {
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.VIRTUAL_APPLIANCE_BUNDLE_CREATED);
                virtualApplianceEvent.virtualAppliance = vaUpdated;
                dispatchEvent(virtualApplianceEvent);
            }
        }
    }
}