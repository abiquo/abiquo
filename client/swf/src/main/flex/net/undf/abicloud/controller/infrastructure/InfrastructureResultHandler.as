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

package net.undf.abicloud.controller.infrastructure
{
    import flash.events.Event;
    
    import mx.collections.ArrayCollection;
    import mx.resources.ResourceManager;
    
    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.events.InfrastructureEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.infrastructure.DataCenter;
    import net.undf.abicloud.vo.infrastructure.Datastore;
    import net.undf.abicloud.vo.infrastructure.HyperVisor;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachine;
    import net.undf.abicloud.vo.infrastructure.PhysicalMachineCreation;
    import net.undf.abicloud.vo.infrastructure.Rack;
    import net.undf.abicloud.vo.infrastructure.State;
    import net.undf.abicloud.vo.infrastructure.VirtualMachine;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.service.RemoteService;

    /**
     * Class to handle server responses when calling infrastructure remote services defined in InfrastructureEventMap
     **/
    public class InfrastructureResultHandler extends ResultHandler
    {

        /* ------------- Constructor --------------- */
        public function InfrastructureResultHandler()
        {
            super();
        }


        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //DATA CENTER HANDLERS
        public function handleGetDataCenters(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding to the model the list of data centers
                var dataCenters:ArrayCollection = DataResult(result).data as ArrayCollection;
                AbiCloudModel.getInstance().infrastructureManager.dataCenters = dataCenters;
                
                AbiCloudModel.getInstance().infrastructureManager.dispatchEvent(new Event("dataCentersRetrieved"));
            }
            else
            {
                //There was a problem retrieving the list of data centers
                super.handleResult(result);
            }
        }
        
        public function handleGetAllowedDataCenters(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding to the model the list of data centers
                var dataCenters:ArrayCollection = DataResult(result).data as ArrayCollection;
                AbiCloudModel.getInstance().infrastructureManager.allowedDataCenters = dataCenters;
                
                AbiCloudModel.getInstance().infrastructureManager.dispatchEvent(new Event("allowedDataCentersRetrieved"));
            }
            else
            {
                //There was a problem retrieving the list of data centers
                super.handleResult(result);
            }
        }


        public function handleCreateDataCenter(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new Data Center to the model
                AbiCloudModel.getInstance().infrastructureManager.addDataCenter(DataResult(result).data as DataCenter);
            }
            else
            {
                //There was a problem creating the data center

                if (result is DataResult && DataResult(result).data is DataCenter)
                {
                    //But the Datacenter has been created
                    AbiCloudModel.getInstance().infrastructureManager.addDataCenter(DataResult(result).data as DataCenter);
                }

                super.handleResult(result);
            }
        }


        public function handleDeleteDataCenter(result:BasicResult, deletedDataCenter:DataCenter):void
        {
            if (result.success)
            {
                //Deleting the data center from the model
                AbiCloudModel.getInstance().infrastructureManager.deleteDataCenter(deletedDataCenter);
            }
            else
            {
                //There was a problem deleting the data center
                super.handleResult(result);
            }
        }

        public function handleEditDataCenter(result:BasicResult, editedDataCenter:DataCenter):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().infrastructureManager.editDataCenter(editedDataCenter);
            }
            else
            {
                //There was a problem editing the data center
                super.handleResult(result);
            }
        }

        public function handleGetHypervisorTypesByDatacenter(result:BasicResult,
                                                             datacenter:DataCenter):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().infrastructureManager.setHypervisorTypesByDatacenter(datacenter,
                                                                                                 DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem retrieving the hypervisors
                super.handleResult(result);
            }
        }
        
        public function handleGetHypervisorByPhysicalMachine(result:BasicResult,
                                                             callback:Function):void
        {
            if (result.success)
            {
                //Hypervisor retrieved successfully
                var hypervisor:HyperVisor = DataResult(result).data as HyperVisor;
                callback(hypervisor);    
            }
            else
            {
                //There was a problem retrieving the hypervisors
                super.handleResult(result);
            }
        }
        
        

        public function handleGetInfrastructureByDataCenter(result:BasicResult):void
        {
            if (result.success)
            {
                //Infrastructure retrieved successfully
                AbiCloudModel.getInstance().infrastructureManager.infrastructure = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem retrieving the infrastructure
                super.handleResult(result);
            }
        }
        
        public function handleGetRacksByDatacenter(result:BasicResult):void
        {
            if (result.success)
            {
                //Racks retrieved successfully
                AbiCloudModel.getInstance().infrastructureManager.racks = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem retrieving the infrastructure
                super.handleResult(result);
            }
        }
        
        

        public function handleCheckInfrastructure(result:BasicResult):void
        {
            if (result.success)
            {
                //Infrastructure checked succesfully
                AbiCloudModel.getInstance().infrastructureManager.infrastructureChecked(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem checking the infrastructure 
                super.handleResultInBackground(result);
            }
        }

        public function handleUpdateUsedResourcesByDatacenter(result:BasicResult,
                                                              datacenter:DataCenter):void
        {
            if (result.success)
            {
                //Used resources updated successfully
                AbiCloudAlert.showConfirmation(ResourceManager.getInstance().getString("Common",
                                                                                       "ALERT_TITLE_LABEL"),
                                               ResourceManager.getInstance().getString("Infrastructure",
                                                                                       "ALERT_USED_RESOURCES_UPDATED_HEADER"),
                                               ResourceManager.getInstance().getString("Infrastructure",
                                                                                       "ALERT_USED_RESOURCES_UPDATED_TEXT"));

                AbiCloudModel.getInstance().infrastructureManager.datacenterUserResourcesUpdated(datacenter);
            }
            else
            {
                //There was a problem updating the used resources 
                super.handleResultInBackground(result);
            }
        }
        
         public function handleCheckVirtualInfrastructureState(result:BasicResult):void
        {
            if (result.success)
            {
                //Virtual Infrastructure State checked successfully
                AbiCloudAlert.showConfirmation(ResourceManager.getInstance().getString("Common",
                                                                                       "ALERT_TITLE_LABEL"),
                                               ResourceManager.getInstance().getString("Infrastructure",
                                                                                       "ALERT_USED_RESOURCES_UPDATED_HEADER"),
                                               ResourceManager.getInstance().getString("Infrastructure",
                                                                                       "ALERT_USED_RESOURCES_UPDATED_TEXT"));

                //AbiCloudModel.getInstance().infrastructureManager.datacenterUserResourcesUpdated(datacenter);
            }
            else
            {
                //There was a problem updating the used resources 
                super.handleResultInBackground(result);
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // RACKS HANDLERS
        public function handleCreateRack(result:BasicResult):void
        {
            if (result.success)
            {
                //Rack created successfully
                //AbiCloudModel.getInstance().infrastructureManager.addInfrastructureElement(DataResult(result).data as InfrastructureElement);
                AbiCloudModel.getInstance().infrastructureManager.addRack(DataResult(result).data as Rack);
            }
            else
            {
                //There was a problem creating the rack
                super.handleResult(result);
            }
        }

        public function handleEditRack(result:BasicResult, editedRack:Rack):void
        {
            if (result.success)
            {
                //Rack edited successfully
                AbiCloudModel.getInstance().infrastructureManager.editRack(editedRack);
            }
            else
            {
                //There was a problem with the rack edition
                super.handleResult(result);
            }
        }

        public function handleDeleteRack(result:BasicResult, deletedRack:Rack):void
        {
            if (result.success)
            {
                //Deleting Rack from the model
                AbiCloudModel.getInstance().infrastructureManager.removeRack(deletedRack);
                //AbiCloudModel.getInstance().infrastructureManager.deleteInfrastructureElement(deletedRack);
            }
            else
            {
                //There was a problem deleting the Rack
                super.handleResult(result);
            }
        }



        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // PHYSICAL MACHINES HANDLERS
        public function handleCreatePhysicalMachine(result:BasicResult, originalPhysicalMachine:PhysicalMachine):void
        {
            if (result.success)
            {
                /*****
	            *  
	            * Since the infrastructure element has been deprecated
	            * we don't use it anymore
	            *  
	            * ***/
	            
                /* var physicalMachineCreation:PhysicalMachineCreation = DataResult(result).data as PhysicalMachineCreation;

                //This solves the bug of the new physical machine not appearing in the Infrastructure Tree
                var physicalMachineCreated:PhysicalMachine = physicalMachineCreation.physicalMachine;
                physicalMachineCreated.assignedTo = originalPhysicalMachine.assignedTo;

                var hypervisorsCreated:ArrayCollection = physicalMachineCreation.hypervisors; */

                //Adding to the model the new Physical Machine
                AbiCloudModel.getInstance().infrastructureManager.addPhysicalMachine();
            }
            else
            {
                //There was a problem creating the Physical Machine
                super.handleResult(result);
            }
        }

        public function handleDeletePhysicalMachine(result:BasicResult, deletedPhysicalMachine:PhysicalMachine):void
        {
            if (result.success)
            {
                //Deleting the Physical Machine from the model
                AbiCloudModel.getInstance().infrastructureManager.deleteInfrastructureElement(deletedPhysicalMachine);
            }
            else
            {
                //There was a problem deleting the Physical Machine
                super.handleResult(result);
            }
        }

        public function handleEditPhysicalMachine(result:BasicResult, physicalMachineEdition:PhysicalMachineCreation):void
        {
            if (result.success)
            {
                 /*****
	            *  
	            * Since the infrastructure element has been deprecated
	            * we don't use it anymore
	            *  
	            * ***/
	            
                //Physical Machine edited successfully
                var hypervisorsCreated:ArrayCollection = DataResult(result).data as ArrayCollection;
                AbiCloudModel.getInstance().infrastructureManager.editPhysicalMachine(physicalMachineEdition.physicalMachine,
                                                                                      hypervisorsCreated);

               /*  //When editing a PhysicaLamchine, existing hypervisors may have been edited too
                var hypervisorsEdited:ArrayCollection = physicalMachineEdition.hypervisors;
                var length:int = hypervisorsEdited.length;
                var i:int;
                for (i = 0; i < length; i++)
                {
                    AbiCloudModel.getInstance().infrastructureManager.editHypervisor(hypervisorsEdited.getItemAt(i) as HyperVisor);
                } */
            }
            else
            {
                //There was a problem with the physical machine edition
                super.handleResult(result);
            }
        }
        
        public function handleGetPhysicalMachineByRack(result:BasicResult , event:InfrastructureEvent):void
        {
            if (result.success)
            {
                //Physical Machine edited successfully
                var physicalMachineList:ArrayCollection = DataResult(result).data as ArrayCollection;                
                AbiCloudModel.getInstance().infrastructureManager.setPhysicalMachineByRack(physicalMachineList,event.branch);               
            }
            else
            {
                //There was a problem with the list of physical machine by rack
                super.handleResult(result);
            }
        }
        
        public function handleGetAvailablePhysicalMachineByRack(result:BasicResult):void
        {
            if (result.success)
            {
                //Physical Machine edited successfully
                var physicalMachineList:ArrayCollection = DataResult(result).data as ArrayCollection;                
                AbiCloudModel.getInstance().infrastructureManager.setAvailablePhysicalMachineByRack(physicalMachineList);               
            }
            else
            {
                //There was a problem with the list of physical machine by rack
                super.handleResult(result);
            }
        }
        
        


        public function handleAddDatastore(result:BasicResult, physicalMachine:PhysicalMachine):void
        {
            if (result.success)
            {
                var newDatastore:Datastore = DataResult(result).data as Datastore;
                AbiCloudModel.getInstance().infrastructureManager.addDatastoreToPhysicalMachine(newDatastore,
                                                                                                physicalMachine);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleEditDatastore(result:BasicResult, physicalMachine:PhysicalMachine,
                                            datastore:Datastore):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().infrastructureManager.updateDatastore(datastore,
                                                                                  physicalMachine);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // HYPERVISOR HANDLERS

        public function handleCreateHypervisor(result:BasicResult):void
        {
            if (result.success)
            {
                //Hypervisor created successfully
                var createdHypervisor:HyperVisor = DataResult(result).data as HyperVisor;
                AbiCloudModel.getInstance().infrastructureManager.addInfrastructureElement(createdHypervisor);
            }
            else
            {
                //There was a problem with the Hypervisor creation
                super.handleResult(result);
            }
        }

        public function handleEditHypervisor(result:BasicResult, editedHypervisor:HyperVisor):void
        {
            if (result.success)
            {
                //Physical Machine edited successfully
                AbiCloudModel.getInstance().infrastructureManager.editHypervisor(editedHypervisor);
            }
            else
            {
                //There was a problem with the physical machine edition
                super.handleResult(result);
            }
        }


        public function handleDeleteHypervisor(result:BasicResult, deletedHypervisor:HyperVisor):void
        {
            if (result.success)
            {
                //Deleting the Hypervisor from the model
                AbiCloudModel.getInstance().infrastructureManager.deleteInfrastructureElement(deletedHypervisor);
            }
            else
            {
                //There was a problem deleting the Physical Machine
                super.handleResult(result);
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // VIRTUAL MACHINES HANDLERS

        public function handleCreateVirtualMachine(result:BasicResult):void
        {
            if (result.success)
            {
                //VirtualMachine was created successfully
                var createdVirtualMachine:VirtualMachine = DataResult(result).data as VirtualMachine;
                AbiCloudModel.getInstance().infrastructureManager.addInfrastructureElement(createdVirtualMachine);
            }
            else
            {
                //There was a problem with the Virtual Machine creation
                super.handleResult(result);
            }
        }

        public function handleEditVirtualMachine(result:BasicResult, editedVirtualMachine:VirtualMachine):void
        {
            if (result.success)
            {
                //Virtual Machine edited successfully
                AbiCloudModel.getInstance().infrastructureManager.editVirtualMachine(editedVirtualMachine);
            }
            else
            {
                //There was a problem with the virtual machine edition
                super.handleResult(result);
            }
        }
        
        public function handleGetVirtualMachineByPhysicalMachine(result:BasicResult , callback:Function):void
        {
            if (result.success)
            {
                //Virtual Machine retrieved successfully
                var virtualMachines:ArrayCollection = DataResult(result).data as ArrayCollection;
                callback(virtualMachines);            
            }
            else
            {
                //There was a problem with the list of physical machine by rack
                super.handleResult(result);
            }
        }
        
        
        public function handleVirtualMachineStateChanged(result:BasicResult, virtualMachine:VirtualMachine):void
        {
            if (result.success)
            {
                //Virtual Machine's state changed successfully
                AbiCloudModel.getInstance().infrastructureManager.changeVirtualMachineState(virtualMachine,
                                                                                            DataResult(result).data as State);
            }
            else
            {
                //There was a problem performing a action over a Virtual Machine
                if (result is DataResult && DataResult(result).data != null)
                    //There was an error, but the server returned a new state for the virtual machine
                    AbiCloudModel.getInstance().infrastructureManager.changeVirtualMachineState(virtualMachine,
                                                                                                DataResult(result).data as State);

                super.handleResultInBackground(result);
            }
        }

        public function handleForceRefreshVirtualMachineState(result:BasicResult):void
        {
            if (result.success)
            {
                //Nothing to do. This call does not have any effect in client
            }
            else
            {
                //There was a problem forcing the virtual machine refresh
                super.handleResult(result);
            }
        }

        //////////////////////////////////////////////////////////////////////////////
        //REMOTE SERVICES HANDLERS

        public function handleGetRemoteServiceTypes(result:BasicResult):void
        {
            if (result.success)
            {
                //Saving the list of RemoteServiceType
                AbiCloudModel.getInstance().infrastructureManager.remoteServiceTypes = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCreateRemoteService(result:BasicResult, datacenter:DataCenter):void
        {
            if (result.success)
            {
                //Adding the new RemoteService to the Datacenter
                AbiCloudModel.getInstance().infrastructureManager.addRemoteServiceToDatacenter(DataResult(result).data as RemoteService,
                                                                                               datacenter);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleEditRemoteService(result:BasicResult, datacenter:DataCenter,
                                                remoteService:RemoteService):void
        {
            if (result.success)
            {
                //Updating the RemoteService with new values
                AbiCloudModel.getInstance().infrastructureManager.updateRemoteService(DataResult(result).data as RemoteService,
                                                                                      datacenter);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleDeleteRemoteService(result:BasicResult, datacenter:DataCenter,
                                                  remoteService:RemoteService):void
        {
            if (result.success)
            {
                //Deleting the RemoteService from model
                AbiCloudModel.getInstance().infrastructureManager.deleteRemoteService(remoteService,
                                                                                      datacenter);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCheckRemoteService(result:BasicResult, remoteService:RemoteService):void
        {
            if (result.success)
            {
                //Updating RemoteService status
                var remoteServiceOK:Boolean = DataResult(result).data as Boolean;

                AbiCloudModel.getInstance().infrastructureManager.updateRemoteServiceStatus(remoteService,
                                                                                            remoteServiceOK ? 1 : 0);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCheckRemoteServiceByURI(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                //Returing the result to who dispatched the event
                callback(DataResult(result).data as Boolean);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
    }
}