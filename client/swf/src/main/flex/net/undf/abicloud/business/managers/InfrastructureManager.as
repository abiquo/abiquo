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
    import mx.utils.ObjectUtil;
    
    import net.undf.abicloud.events.InfrastructureEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.utils.customtree.CustomTreeNode;
    import net.undf.abicloud.vo.infrastructure.DataCenter;
    import net.undf.abicloud.vo.infrastructure.DataCenterAllocationLimit;
    import net.undf.abicloud.vo.infrastructure.Datastore;
    import net.undf.abicloud.vo.infrastructure.HyperVisor;
    import net.undf.abicloud.vo.infrastructure.HyperVisorType;
    import net.undf.abicloud.vo.infrastructure.InfrastructureElement;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachine;
    import net.undf.abicloud.vo.infrastructure.Rack;
    import net.undf.abicloud.vo.infrastructure.State;
    import net.undf.abicloud.vo.infrastructure.UcsRack;
    import net.undf.abicloud.vo.infrastructure.VirtualMachine;
    import net.undf.abicloud.vo.service.RemoteService;
    import net.undf.abicloud.vo.service.RemoteServiceType;
    import net.undf.abicloud.vo.virtualhardware.ResourceAllocationLimit;

    /**
     * Data manager for the Infrastructure Managment, a component from AbiCloud application.
     * It can only be created and accessed through AbiCloudManager
     *
     * This class allows to view and manipulate the user's infrastructure, as well the infrastructure elements.
     * When editing infrastructure information, like add a new element or editing one, it is important that this
     * information is saved server side, not only client side. To do so, the proper function must be used.
     *
     * See each function documentation for more information
     **/

    public class InfrastructureManager extends EventDispatcher
    {

        /* ------------- Constants------------- */

        //Internal events to notify changes over the infrastructure
        public static const DATACENTERS_UPDATED:String = "dataCentersUpdated_InfrastructureManager";
        
        public static const ALLOWED_DATACENTERS_UPDATED:String = "allowedDataCentersUpdated_InfrastructureManager";

        public static const INFRASTRUCTURE_UPDATED:String = "infrastructureUpdated_InfrastructureManager";
        
        public static const RACKS_UPDATED:String = "racksUpdated_InfrastructureManager";

        /* ------------- Private attributes ------------- */

        //Represents the infrastructure, where infrastructure elements are stored.
        //For more information about the relations between infrastructure elements, see the relevant class
        //This array may contain any Infrastructure Element
        private var _infrastructure:ArrayCollection;


        //ArrayCollection with all Data Centers in the Data Base
        private var _dataCenters:ArrayCollection;
        
        //ArrayCollection with allowed Datacenters
        private var _allowedDataCenters:ArrayCollection;
        
        //ArrayCollection with all racks
        private var _racks:ArrayCollection;



        /* ------------- Constructor ------------- */
        public function InfrastructureManager()
        {
            _infrastructure = new ArrayCollection();
            _dataCenters = new ArrayCollection();
            _racks = new ArrayCollection();
        }



        /* ------------- Public methods ------------- */

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to Infrastructure

        /**
         * Sets the infrastructure that will be managed by this infrastructure manager
         **/
        public function set infrastructure(infr:ArrayCollection):void
        {
            _infrastructure = infr;

            //Infrastructure has been updated
            this.dispatchEvent(new Event("infrastructureChange"));
        }


        [Bindable(event="infrastructureChange")]
        public function get infrastructure():ArrayCollection
        {
            return _infrastructure;
        }


        /** Adds a new infrastructure element to infrastructure
         *
         * @return true if successful
         **/
        public function addInfrastructureElement(iE:InfrastructureElement):Boolean
        {
            if (!_infrastructure.contains(iE))
            {
                _infrastructure.addItem(iE);
                //Announcing that infrastructure has been updated
                this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED));
                return true;
            }
            else
                return false;
        }
        
        /** Adds a new rack 
         *
         * 
         **/
        public function addRack(rack:Rack):void
        {
            _racks.addItem(rack);
            //Announcing that infrastructure has been updated
            this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED));
        }



        /** Deletes the given infrastructure element from infrastructure
         *
         * @return true if successful
         **/
        public function deleteInfrastructureElement(iE:InfrastructureElement):Boolean
        {
        	//As we had change the way of showing the infrastructure
        	//We remove from list of racks if the element is a rack
        	/* if(iE is Rack){
        		removeRack(Rack(iE));
        		return true;
        	} */
  
        	
            var index:int = _infrastructure.getItemIndex(iE);
            if (index >= 0)
            {
                _infrastructure.removeItemAt(index);
	            //Announcing that infrastructure has been updated
	            this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED));
                return true;
            }
            else{
            	this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED));
                return false;
            }
                
        }

        /**
         * Replaces the infrastructure with a new version just retrieved
         */
        public function infrastructureChecked(newInfrastructure:ArrayCollection):void
        {
            this._infrastructure = newInfrastructure;
            dispatchEvent(new Event(INFRASTRUCTURE_UPDATED));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to Data Centers

        /**
         * Sets the arraycollection that contains all data centers
         **/
        public function set dataCenters(dC:ArrayCollection):void
        {
            this._dataCenters = dC;

            //Data Centers list has been updated
            dispatchEvent(new Event(DATACENTERS_UPDATED, true));
        }

        [Bindable]
        public function get allowedDataCenters():ArrayCollection
        {
            return this._allowedDataCenters;
        }
        
        /**
         * Sets the arraycollection that contains all data centers
         **/
        public function set allowedDataCenters(dC:ArrayCollection):void
        {
            this._allowedDataCenters = dC;

            //Data Centers list has been updated
            dispatchEvent(new Event(ALLOWED_DATACENTERS_UPDATED, true));
        }

        [Bindable(event="dataCentersUpdated_InfrastructureManager")]
        public function get dataCenters():ArrayCollection
        {
            return this._dataCenters;
        }

        /**
         * Retrieves a Datacenter from model given its ID
         * @param datacenterID
         * @return
         *
         */
        public function getDatacenterByID(datacenterID:int):DataCenter
        {
            var length:int = this._dataCenters.length;
            for (var i:int = 0; i < length; i++)
            {
                if (DataCenter(_dataCenters.getItemAt(i)).id == datacenterID)
                    return _dataCenters.getItemAt(i) as DataCenter;
            }

            return null;
        }
        
        /**
         * Clone the list of Datacenters
         * @return ArrayCollection
         *
         */
        public function cloneDatacentersList():ArrayCollection{
        	var tmpArrayCollection:ArrayCollection = new ArrayCollection();
        	var tmpDatacenter:DataCenter;
        	for(var i:Number = 0 ; i < this.dataCenters.length ; i++){
        		tmpDatacenter = ObjectUtil.copy(this.dataCenters[i]) as DataCenter;
        		tmpArrayCollection.addItem(tmpDatacenter);
        	}
        	return tmpArrayCollection;
        }

        public function setHypervisorTypesByDatacenter(dataCenter:DataCenter, hypervisorTypes:ArrayCollection):void
        {
            var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.HYPERVISOR_TYPES_BY_DATACENTER_RETRIEVED);
            infrastructureEvent.dataCenter = dataCenter;
            infrastructureEvent.hypervisorTypesByDatacenter = hypervisorTypes;
            dispatchEvent(infrastructureEvent);
        }

        /**
         * Adds a new Data Center to the data centers list
         **/
        public function addDataCenter(dataCenter:DataCenter):void
        {
            if (!_dataCenters.contains(dataCenter))
            {
                this._dataCenters.addItem(dataCenter);

                //Announcing the Datacenter that has been just created
                var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.DATACENTER_CREATED);
                infrastructureEvent.dataCenter = dataCenter;
                dispatchEvent(infrastructureEvent);

                //Data Centers list has been updated
                dispatchEvent(new Event(DATACENTERS_UPDATED, true));
            }
        }


        /**
         * Deletes a data center from the data centers list
         **/
        public function deleteDataCenter(dataCenter:DataCenter):void
        {
            var index:int = _dataCenters.getItemIndex(dataCenter);
            if (index > -1)
            {
                _dataCenters.removeItemAt(index);

                //Data Centers list has been updated
                dispatchEvent(new Event(DATACENTERS_UPDATED, true));
            }
        }

        /**
         * Updated an existing Datacenter with new values, without modifying its
         * memory address
         */
        public function editDataCenter(editedDataCenter:DataCenter):void
        {
            //Looking for the datacenter in model with the old values
            var length:int = this._dataCenters.length;
            var i:int;
            var oldDataCenter:DataCenter;
            for (i = 0; i < length; i++)
            {
                oldDataCenter = this._dataCenters.getItemAt(i) as DataCenter;
                if (oldDataCenter.id == editedDataCenter.id)
                    //DataCenter to updated found. Exiting...
                    break;
                else
                    oldDataCenter = null;
            }

            if (oldDataCenter)
            {
                //Updating the old Data Center with the new values, without modifying its memory address
                oldDataCenter.id = editedDataCenter.id;
                oldDataCenter.name = editedDataCenter.name;
                oldDataCenter.situation = editedDataCenter.situation;
                oldDataCenter.remoteServices = editedDataCenter.remoteServices;

                //Announcing that a data center has been edited
                var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.DATACENTER_EDITED,
                                                                                      false);
                infrastructureEvent.dataCenter = oldDataCenter;
                dispatchEvent(infrastructureEvent);
            }
            else
            {
                //The DataCenter has not been found. Ignoring edition...
            }
        }

        /**
         * Announces that the used resources of a Datacenter have been successfully updated
         */
        public function datacenterUserResourcesUpdated(datacenter:DataCenter):void
        {
            var iE:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.USED_RESOURCES_BY_DATACENTER_UPDATED);
            iE.dataCenter = datacenter;
            dispatchEvent(iE);
        }
        
        /**
         * Search for an existing ressource allocation limit within a list of datacenter for a specific enterprise
         */
        public function getDatacenterRessourceLimit(enterpriseId:int, allocationLimitCollection:ArrayCollection):ResourceAllocationLimit{
        	var returnedResourceAllocationLimit:ResourceAllocationLimit;
        	for(var i:Number = 0 ; i < allocationLimitCollection.length ; i++){
        		if(DataCenterAllocationLimit(allocationLimitCollection[i]).id_enterprise == enterpriseId){
        			return DataCenterAllocationLimit(allocationLimitCollection[i]).limits;
        		}
        	}
        	return returnedResourceAllocationLimit = new ResourceAllocationLimit();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to Racks
        
        
        /**
         * Sets the arraycollection that contains all racks
         **/
        public function set racks(racks:ArrayCollection):void
        {
            this._racks = racks;
            //Data Centers list has been updated
            //dispatchEvent(new Event(DATACENTERS_UPDATED, true));
        }

        [Bindable]
        public function get racks():ArrayCollection
        {
            return this._racks;
        }

        /**
         * Returns all user's racks
         **/
       /* [Bindable(event="infrastructureUpdated_InfrastructureManager")]
         public function get racks():Array
        {
            var allRacks:Array = new Array();
            var infrastructureLength:int = _infrastructure.length;
            var i:int;
            var element:InfrastructureElement;

            for (i = 0; i < infrastructureLength; i++)
            {
                element = _infrastructure.getItemAt(i) as InfrastructureElement;
                if (element is Rack)
                    allRacks.push(element);
            }

            return allRacks;
        } */
        
       /*  [Bindable(event="infrastructureUpdated_InfrastructureManager")]
        public function get racks():ArrayCollection{
        	return this._racks;
        }
        
        public function set racks(racks:ArrayCollection):void{
        	this._racks = racks;
        } */
        
         /**
         * Set the list of Physical Machines assigned to a given rack
         **/
        public function setPhysicalMachineByRack(physicalMachines:ArrayCollection , branch:CustomTreeNode):void{
        	var event:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.PHYSICALMACHINE_BY_RACK_RETRIEVED);
        	event.branch = branch;
        	event.physicalMachineByRack = physicalMachines;
            dispatchEvent(event);
        }
        
         /**
         * Set the list of Available Physical Machines assigned to a given rack
         **/
        public function setAvailablePhysicalMachineByRack(physicalMachines:ArrayCollection):void{
        	var event:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.AVAILABLE_PHYSICAL_MACHINE_BY_RACK_RETRIEVED);
        	event.physicalMachineByRack = physicalMachines;
            dispatchEvent(event);
        }


        /**
         * Get all Physical Machines assigned to a given rack
         **/
        public function getPhysicalMachinesByRack(rack:Rack):Array
        {
            var allPM:Array = physicalMachines;
            var i:int;
            var allPMLength:int = allPM.length;
            var pm:PhysicalMachine;
            var rackPhysicalMachines:Array = new Array();

            for (i = 0; i < allPMLength; i++)
            {
                pm = allPM[i] as PhysicalMachine;
                if (pm.assignedTo != null && pm.assignedTo.id == rack.id)
                    rackPhysicalMachines.push(pm);
            }

            return rackPhysicalMachines;
        }
        
        /**
         * Get the arraycollection's index for a specific Physical Machines
         **/
        public function getPhysicalMachinesById(id:int , listOfMachines:ArrayCollection):int{
        	for(var i:int = 0 ; i < listOfMachines.length ; i++ ){
        		if(id == PhysicalMachine(listOfMachines[i]).id){
        			return i;
        		}
        	}
        	return 0;
        }

        /**
         * Get all Virtual Machines assigned to the physical machines that are assigned to a given rack
         **/
        public function getVirtualMachinesByRack(rack:Rack):Array
        {
            //First, we get the Physical Machines assigned to this rack
            var physicalMachinesByRack:ArrayCollection = new ArrayCollection(getPhysicalMachinesByRack(rack));

            var i:int;
            var length:int = physicalMachinesByRack.length;
            var virtualMachinesByRack:Array = new Array();
            var physicalMachine:PhysicalMachine;

            for (i = 0; i < length; i++)
            {
                physicalMachine = physicalMachinesByRack[i] as PhysicalMachine;
                virtualMachinesByRack = virtualMachinesByRack.concat(getVirtualMachinesByPhysicalMachine(physicalMachine));
            }

            return virtualMachinesByRack;
        }

        /**
         * Updates a Rack with new values
         * @param editedRack A Rack object with new values for a Rack that exists in racks ArrayCollection
         */
        public function editRack(editedRack:Rack):void
        {
            //Looking for the rack to update
           /*  var rackList:ArrayCollection = racks;
            var length:int = rackList.length;
            var i:int;
            var oldRack:Rack;
            for (i = 0; i < length; i++)
            {
                oldRack = rackList[i] as Rack;
                if (oldRack.id == editedRack.id)
                    //Rack found. Exiting...
                    break;
                else
                    oldRack = null;
            }

            if (oldRack)
            {
                //Updating the old Rack without modifying its memory address
                oldRack.id = editedRack.id;
                oldRack.name = editedRack.name;
                oldRack.shortDescription = editedRack.shortDescription;
                oldRack.largeDescription = editedRack.largeDescription;

                //Announcing that this rack has been edited successfully
                var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.INFRASTRUCTURE_ELEMENT_EDITED,
                                                                                      false);
                infrastructureEvent.infrastructureElement = oldRack;
                dispatchEvent(infrastructureEvent);
            }
            else
            {
                //Rack not found. Ignoring changes
            } */

			 //Announcing that infrastructure has been updated
            this.dispatchEvent(new Event(RACKS_UPDATED, true));
        }
        
        public function removeRack(rack:Rack):void
        {
        	for(var i:int = 0 ; i < this._racks.length ; i++){
        		if(this._racks[i].id == rack.id){
        			this._racks.removeItemAt(i);
        			this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED));
        			break;
        		}
        	}
        }
		

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to Physical Machines

        /**
         * Returns all physical machines present in the infrastructure
         *
         **/
        public function get physicalMachines():Array
        {
            var allPM:Array = new Array();
            var infrastructureLength:int = _infrastructure.length;
            var element:InfrastructureElement;
            var i:int;


            for (i = 0; i < infrastructureLength; i++)
            {
                element = _infrastructure.getItemAt(i) as InfrastructureElement;
                if (element is PhysicalMachine)
                    allPM.push(element);
            }

            return allPM;
        }

        /**
         * Returns all HyperVisors assigned to a given Physical Machine
         */
        public function getHyperVisorsByPhysicalMachine(physicalMachine:PhysicalMachine):Array
        {
            var pmHyperVisors:Array = new Array();
            var length:int = this._infrastructure.length;
            var infrastructureElement:InfrastructureElement;
            var i:int;
            for (i = 0; i < length; i++)
            {
                infrastructureElement = this._infrastructure.getItemAt(i) as InfrastructureElement;
                if (infrastructureElement is HyperVisor && HyperVisor(infrastructureElement).assignedTo.id == physicalMachine.id)
                    pmHyperVisors.push(infrastructureElement);
            }

            return pmHyperVisors;
        }

        /**
         * Get all Virtual Machines assigned to a given Physical Machine
         **/
        public function getVirtualMachinesByPhysicalMachine(physicalMachine:PhysicalMachine):Array
        {
            var allVM:Array = virtualMachines;
            var i:int;
            var allVMLength:int = allVM.length;
            var vm:VirtualMachine;
            var pmVirtualMachines:Array = new Array();

            for (i = 0; i < allVMLength; i++)
            {
                vm = allVM[i] as VirtualMachine;
                if (vm.assignedTo.assignedTo.id == physicalMachine.id)
                    pmVirtualMachines.push(vm);
            }

            return pmVirtualMachines;
        }


        public function addPhysicalMachine():void
        {
            /*****
            *  
            * Since the infrastructure element has been deprecated
            * we don't use it anymore
            *  
            * ***/
            
            //Adding the physical machine created
            /* this._infrastructure.addItem(physicalMachine);

            //Adding the hypervisors created (if there is any)
            var length:int = hypervisors.length;
            var i:int;
            for (i = 0; i < length; i++)
            {
                this._infrastructure.addItem(hypervisors.getItemAt(i));
            } */

            //Announcing that infrastructure has been updated
            this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED, true));
        }

        /**
         * Updates a Physical Machine with new values
         * @param editedPhysicalMachine A PhysicalMachine object with new values
         * @param hypervisorsCreated When editing a PhysicalMachine, new Hypervisors can be created
         * for a PhysicalMachine that exists in this manager
         *
         */
        public function editPhysicalMachine(editedPhysicalMachine:PhysicalMachine,
                                            hypervisorsCreated:ArrayCollection):void
        {
             /*****
            *  
            * Since the infrastructure element has been deprecated
            * we don't use it anymore
            *  
            * ***/
            
            /* var length:int;
            var i:int;

            //Looking for the PhysicalMachine to update
            var physicalMachineList:Array = physicalMachines;
            length = physicalMachineList.length;
            var oldPhysicalMachine:PhysicalMachine;
            for (i = 0; i < length; i++)
            {
                oldPhysicalMachine = physicalMachineList[i] as PhysicalMachine;
                if (oldPhysicalMachine.id == editedPhysicalMachine.id)
                    //PhysicalMachine found. Exiting...
                    break;
                else
                    oldPhysicalMachine = null;
            }

            if (oldPhysicalMachine)
            {
                //Updating the old Physical Machine without modifying its memory address
                oldPhysicalMachine.id = editedPhysicalMachine.id;
                oldPhysicalMachine.name = editedPhysicalMachine.name;
                oldPhysicalMachine.assignedTo = editedPhysicalMachine.assignedTo;
                oldPhysicalMachine.description = editedPhysicalMachine.description;
                oldPhysicalMachine.ram = editedPhysicalMachine.ram;
                oldPhysicalMachine.cpu = editedPhysicalMachine.cpu;
                oldPhysicalMachine.cpuRatio = editedPhysicalMachine.cpuRatio;
                oldPhysicalMachine.hd = editedPhysicalMachine.hd;
                oldPhysicalMachine.idState = editedPhysicalMachine.idState;
                oldPhysicalMachine.vswitchName = editedPhysicalMachine.vswitchName;
                oldPhysicalMachine.datastores = editedPhysicalMachine.datastores;

                //Adding the hypervisors created (if there is any)
                length = hypervisorsCreated.length;
                for (i = 0; i < length; i++)
                {
                    this._infrastructure.addItem(hypervisorsCreated.getItemAt(i));
                }

                //Announcing that this physical machine has been edited successfully
                var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.INFRASTRUCTURE_ELEMENT_EDITED,
                                                                                      false);
                infrastructureEvent.infrastructureElement = oldPhysicalMachine;
                dispatchEvent(infrastructureEvent);
                
                
            }
            else
            {
                //The PhysicalMachine no longer exists. Ignoring edition...
            } */
            
            //Announcing that infrastructure has been updated
            this.dispatchEvent(new Event(INFRASTRUCTURE_UPDATED, true));
		
        }

        public function addDatastoreToPhysicalMachine(datastore:Datastore, physicalMachine:PhysicalMachine):void
        {
            physicalMachine.datastores.addItem(datastore);
        }

        public function updateDatastore(datastoreNewValues:Datastore, physicalMachine:PhysicalMachine):void
        {
            //Look for the Datastore
            var length:int = physicalMachine.datastores.length;
            var datastore:Datastore;
            for (var i:int = 0; i < length; i++)
            {
                datastore = physicalMachine.datastores.getItemAt(i) as Datastore;
                if (datastore.id == datastoreNewValues.id)
                {
                    break;
                }
                else
                {
                    datastore = null;
                }
            }

            if (datastore)
            {
                //Updating values
                datastore.directory = datastoreNewValues.directory;
                datastore.enabled = datastoreNewValues.enabled;
                datastore.name = datastoreNewValues.name;
                datastore.shared = datastoreNewValues.shared;
                datastore.size = datastoreNewValues.size;
                datastore.usedSize = datastoreNewValues.usedSize;
                datastore.UUID = datastoreNewValues.UUID;
            }
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to HyperVisors

        /**
         * Returns all hypervisors present in the infrastructure
         *
         **/
        public function get hypervisors():Array
        {
            var allHypervisors:Array = new Array();
            var infrastructureLength:int = _infrastructure.length;
            var element:InfrastructureElement;
            var i:int;


            for (i = 0; i < infrastructureLength; i++)
            {
                element = _infrastructure.getItemAt(i) as InfrastructureElement;
                if (element is HyperVisor)
                    allHypervisors.push(element);
            }

            return allHypervisors;
        }

        /**
         * ArrayCollection containing HypervisorType objects
         */

        private var _hypervisorTypes:ArrayCollection;

        //This list contains the Hyerpvisor Types that are currently in use (i.e not deprecated)
        private var _hypervisorTypesInUse:ArrayCollection;

        [Bindable]
        public function get hypervisorTypes():ArrayCollection
        {
            return this._hypervisorTypes;
        }

        public function set hypervisorTypes(value:ArrayCollection):void
        {
            this._hypervisorTypes = value;
			//Update the list of hypervisors in use
            updateHypervisors();
        }

        public function get hypervisorTypesInUse():ArrayCollection
        {
            return _hypervisorTypesInUse;
        }

        /**
         * Adds a new hypervisor to the infrastructure Array
         */
        public function addHypervisor(newHypervisor:HyperVisor):void
        {
            this._infrastructure.addItem(newHypervisor);
        }

        /**
         * Updates an Hypervisor with new values
         *
         */
        public function editHypervisor(editedHypervisor:HyperVisor):void
        {
             /*****
            *  
            * Since the infrastructure element has been deprecated
            * we don't use it anymore
            *  
            * ***/
            
            //Looking for the Hypervisor to edit
            /* var hypervisorList:Array = hypervisors;
            var length:int = hypervisorList.length;
            var i:int;
            var oldHypervisor:HyperVisor;
            for (i = 0; i < length; i++)
            {
                oldHypervisor = hypervisorList[i] as HyperVisor;
                if (oldHypervisor.id == editedHypervisor.id)
                    //Hypervisor found. Exiting...
                    break;
                else
                    oldHypervisor = null;
            }

            if (oldHypervisor)
            {
                //Updating the Hypervisor without modifying its memory address
                oldHypervisor.id = editedHypervisor.id;
                oldHypervisor.name = editedHypervisor.name;
                oldHypervisor.shortDescription = editedHypervisor.shortDescription;
                oldHypervisor.type = editedHypervisor.type;
                oldHypervisor.ip = editedHypervisor.ip;
                oldHypervisor.ipService = editedHypervisor.ipService;
                oldHypervisor.port = editedHypervisor.port;
                oldHypervisor.assignedTo = editedHypervisor.assignedTo;

                //Announcing that this physical machine has been edited successfully
                var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.INFRASTRUCTURE_ELEMENT_EDITED,
                                                                                      false);
                infrastructureEvent.infrastructureElement = oldHypervisor;
                dispatchEvent(infrastructureEvent);
            }
            else
            {
                //Hypervisor not found. Ignoring edition...
            } */
        }
        
        /**
         * Update the list of hypervisor in use
         */
        public function updateHypervisors():void{
        	if (_hypervisorTypes)
            {
                var length:int = _hypervisorTypes.length;
                _hypervisorTypesInUse = new ArrayCollection();
                for (var i:int = 0; i < length; i++)
                {
                    //If VirtualBox is supported
                    if(AbiCloudModel.getInstance().configurationManager.config.client_infra_useVirtualBox.value == 1){
                    	_hypervisorTypesInUse.addItem(_hypervisorTypes.getItemAt(i));
                    }else{
	                    if (HyperVisorType(_hypervisorTypes.getItemAt(i)).id != HyperVisorType.VIRTUAL_BOX)
	                    {
	                    	_hypervisorTypesInUse.addItem(_hypervisorTypes.getItemAt(i));
	                    }                    	
                    }
                }
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to Virtual Machines

        /**
         * Returns all user's virtual machines
         *
         **/
        public function get virtualMachines():Array
        {

            var allVM:Array = new Array();
            var infrastructureLength:int = _infrastructure.length;
            var element:InfrastructureElement;
            var i:int;


            for (i = 0; i < infrastructureLength; i++)
            {
                element = _infrastructure.getItemAt(i) as InfrastructureElement;
                if (element is VirtualMachine && VirtualMachine(element).state.id != State.NOT_DEPLOYED)
                    allVM.push(element);
            }

            return allVM;
        }


        /**
         * Changes the state of a Virtual Machine, to a new State
         * @param virtualMachine A VirtualMachine object with the information of the Virtual Machine
         * which the state has been changed
         **/
        public function changeVirtualMachineState(virtualMachine:VirtualMachine,
                                                  newState:State):void
        {
            //Looking for the virtual machine to change its state
            var virtualMachineList:Array = virtualMachines;
            var length:int = virtualMachineList.length;
            var i:int;
            var oldVirtualMachine:VirtualMachine;
            for (i = 0; i < length; i++)
            {
                oldVirtualMachine = virtualMachineList[i] as VirtualMachine;
                if (oldVirtualMachine.id == virtualMachine.id)
                    //VirtualMachine found. Exiting...
                    break;
                else
                    oldVirtualMachine = null;
            }

            if (!oldVirtualMachine)
                oldVirtualMachine = virtualMachine;

            oldVirtualMachine.state = newState;

        }


        /**
         * Updates a VirtualMachine with new values
         *
         */
        public function editVirtualMachine(editedVirtualMachine:VirtualMachine):void
        {
            var length:int = this._infrastructure.length;
            var i:int;
            var infrastructureElement:InfrastructureElement;
            var oldVirtualMachine:VirtualMachine;
            for (i = 0; i < length; i++)
            {
                infrastructureElement = this._infrastructure.getItemAt(i) as InfrastructureElement;
                if (infrastructureElement is VirtualMachine && infrastructureElement.id == editedVirtualMachine.id)
                {
                    oldVirtualMachine = infrastructureElement as VirtualMachine;
                    break;
                }
            }

            if (oldVirtualMachine)
            {
                //Updating the old Virtual Machine without modifying its memory address
                oldVirtualMachine.id = editedVirtualMachine.id;
                oldVirtualMachine.cpu = editedVirtualMachine.cpu;
                oldVirtualMachine.description = editedVirtualMachine.description;
                oldVirtualMachine.hd = editedVirtualMachine.hd;
                oldVirtualMachine.highDisponibility = editedVirtualMachine.highDisponibility;
                oldVirtualMachine.name = editedVirtualMachine.name;
                oldVirtualMachine.ram = editedVirtualMachine.ram;
                oldVirtualMachine.state = editedVirtualMachine.state;
                oldVirtualMachine.UUID = editedVirtualMachine.UUID;
                oldVirtualMachine.vdrpIP = editedVirtualMachine.vdrpIP;
                oldVirtualMachine.vdrpPort = editedVirtualMachine.vdrpPort;
                oldVirtualMachine.virtualImage = editedVirtualMachine.virtualImage;
                oldVirtualMachine.assignedTo = editedVirtualMachine.assignedTo;
                oldVirtualMachine.idType = editedVirtualMachine.idType;
                oldVirtualMachine.user = editedVirtualMachine.user;
                oldVirtualMachine.enterprise = editedVirtualMachine.enterprise;
            }
            else
                oldVirtualMachine = editedVirtualMachine;


            //Announcing that this virtual machine has been edited successfully
            var infrastructureEvent:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.INFRASTRUCTURE_ELEMENT_EDITED,
                                                                                  false);
            infrastructureEvent.infrastructureElement = oldVirtualMachine;
            dispatchEvent(infrastructureEvent);
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //Related to RemoteServices

        private var _remoteServiceTypes:ArrayCollection = new ArrayCollection();

        [Bindable(event="remoteServiceTypesChange")]
        public function get remoteServiceTypes():ArrayCollection
        {
            return _remoteServiceTypes;
        }

        public function set remoteServiceTypes(value:ArrayCollection):void
        {
            _remoteServiceTypes = value;
            dispatchEvent(new Event("remoteServiceTypesChange"));
        }

        public function getRemoteServiceTypeById(valueOf:String):RemoteServiceType
        {
            var length:int = _remoteServiceTypes.length;
            for (var i:int = 0; i < length; i++)
            {
                if (RemoteServiceType(_remoteServiceTypes.getItemAt(i)).valueOf == valueOf)
                    return _remoteServiceTypes.getItemAt(i) as RemoteServiceType
            }

            //No RemoteServiceType found for the given id
            return null;
        }

        /**
         * Adds a new RemoteService to an existing Datacenter
         */
        public function addRemoteServiceToDatacenter(newRemoteService:RemoteService,
                                                     datacenter:DataCenter):void
        {
            //Always look for the Datacenter from model
            var modelDatacenter:DataCenter = getDatacenterByID(datacenter.id);

            if (modelDatacenter)
            {
                modelDatacenter.remoteServices.addItem(newRemoteService);

                //Announcing that the RemoteService has been created
                var event:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.REMOTE_SERVICE_CREATED);
                event.remoteService = newRemoteService;
                dispatchEvent(event);
            }
            else
            {
                //The Datacenter no longer exists. We ignore the RemoteService
            }
        }

        /**
         * Updates an existing RemoteService with new values
         * @param remoteService
         * @param datacenter
         *
         */
        public function updateRemoteService(remoteService:RemoteService, datacenter:DataCenter):void
        {
            //We always try to work with models data

            var modelDatacenter:DataCenter = getDatacenterByID(datacenter.id);
            var length:int = modelDatacenter.remoteServices.length;
            var remoteServiceToUpdate:RemoteService;
            for (var i:int = 0; i < length; i++)
            {
                if (RemoteService(modelDatacenter.remoteServices.getItemAt(i)).idRemoteService == remoteService.idRemoteService)
                {
                    remoteServiceToUpdate = modelDatacenter.remoteServices.getItemAt(i) as RemoteService;
                    break;
                }
            }

            if (remoteServiceToUpdate)
            {
                remoteServiceToUpdate.updateWithValues(remoteService);
                var event:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.REMOTE_SERVICE_EDITED);
                event.remoteService = remoteServiceToUpdate;
                dispatchEvent(event);
            }
        }

        public function deleteRemoteService(remoteService:RemoteService, datacenter:DataCenter):void
        {
            //We always try to work with models data

            var modelDatacenter:DataCenter = getDatacenterByID(datacenter.id);
            var length:int = modelDatacenter.remoteServices.length;
            for (var i:int = 0; i < length; i++)
            {
                if (RemoteService(modelDatacenter.remoteServices.getItemAt(i)).idRemoteService == remoteService.idRemoteService)
                {
                    var remoteServiceDeleted:RemoteService = modelDatacenter.remoteServices.removeItemAt(i) as RemoteService;

                    var event:InfrastructureEvent = new InfrastructureEvent(InfrastructureEvent.REMOTE_SERVICE_DELETED);
                    event.remoteService = remoteServiceDeleted;
                    dispatchEvent(event);

                    return;
                }
            }
        }

        /**
         * Sets a new status for a RemoteService
         * @param remoteService
         * @param status
         *
         */
        public function updateRemoteServiceStatus(remoteService:RemoteService, status:int):void
        {
            remoteService.status = status;
        }
    }

}
