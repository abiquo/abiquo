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

package net.undf.abicloud.controller.virtualappliance
{
    import flash.events.Event;
    
    import mx.collections.ArrayCollection;
    import mx.controls.Alert;
    import mx.core.Application;
    import mx.events.CloseEvent;
    import mx.resources.ResourceBundle;
    import mx.resources.ResourceManager;
    
    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.events.VirtualApplianceEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.infrastructure.State;
    import net.undf.abicloud.vo.networking.DHCP;
    import net.undf.abicloud.vo.networking.Host;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.virtualappliance.Log;
    import net.undf.abicloud.vo.virtualappliance.VirtualAppliance;
    import net.undf.abicloud.vo.virtualappliance.VirtualDataCenter;

    /**
     * Class to handle server responses when calling virtual appliance remote services defined in VirtualApplianceEventMap
     **/

    public class VirtualApplianceResultHandler extends ResultHandler
    {
        private var _virtualAppliance:VirtualAppliance;

        private var _virtualApplianceReturnedByServer:VirtualAppliance;

        //Dummy variables for VirtualDataCenter networType attribute (needed to make compiler to import some classes)
        private var dummyDHCP:DHCP = new DHCP();

        private var dummyHOST:Host = new Host();

        [ResourceBundle("Common")]
        private var rb:ResourceBundle;

        [ResourceBundle("VirtualAppliance")]
        private var rb2:ResourceBundle;

        public function VirtualApplianceResultHandler()
        {
            super();
        }


        ////////////////////////////////////////////
        //Virtual Datacenters

        public function handleGetVirtualDataCentersByEnterprise(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the VirtualDataCenter list to the model
                AbiCloudModel.getInstance().virtualApplianceManager.virtualDataCenters = DataResult(result).data as ArrayCollection;
                
                AbiCloudModel.getInstance().virtualApplianceManager.dispatchEvent(new Event("virtualDataCentersRetrieved"));
                
            }
            else
            {
                //There was a problem retrieving the VirtualDataCenter list
                super.handleResult(result);
            }
        }

        public function handleGetVirtualApplianceNodes(result:BasicResult, virtualAppliance:VirtualAppliance):void
        {
            if (result.success)
            {
                //Setting the list of nodes for this VirtualAppliance
                AbiCloudModel.getInstance().virtualApplianceManager.setVirtualApplianceNodes(virtualAppliance,
                                                                                             DataResult(result).data as ArrayCollection);
            }
            else
                //There was a problem retrieving the VirtualAppliance's nodes
                super.handleResult(result);
        }

        public function handleCreateVirtualDataCenter(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new VirtualDataCenter to the model
                AbiCloudModel.getInstance().virtualApplianceManager.addVirtualDataCenter(DataResult(result).data as VirtualDataCenter);
            }
            else
            {
                //There was a problem creating a new VirtualDataCenter
                super.handleResult(result);
            }
        }

        public function handleDeleteVirtualDataCenter(result:BasicResult, virtualDataCenter:VirtualDataCenter):void
        {
            if (result.success)
            {
                //Removing the VirtualDataCenter from the model
                AbiCloudModel.getInstance().virtualApplianceManager.deleteVirtualDataCenter(virtualDataCenter);
            }
            else
            {
                //There was a problem deleting the VirtualDataCenter from the server
                super.handleResult(result);
            }
        }

        public function handleEditVirtualDataCenter(result:BasicResult, vdcNewValues:VirtualDataCenter):void
        {
            if (result.success)
            {
                //Updating the VirtualDataCenter in model
                AbiCloudModel.getInstance().virtualApplianceManager.editVirtualDataCenter(vdcNewValues);
            }
            else
            {
                //There was a problem editing the VirtualDataCenter
                super.handleResult(result);
            }
        }

        public function handleGetVirtualDataCenterDHCPConf(result:BasicResult, virtualDataCenter:VirtualDataCenter):void
        {
            if (result.success)
            {
                //DHCP configuration retrieved successfully
                AbiCloudModel.getInstance().virtualApplianceManager.showVirtualDataCenterDHCPConf(DataResult(result).data as String,
                                                                                                  virtualDataCenter);
            }
            else
            {
                //There was a problem retrieving the DHCP configuration
                super.handleResult(result);
            }
        }


        ////////////////////////////////////////////
        //Virtual Appliances
        public function handleGetVirtualAppliancesByEnterprise(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding to the model the list of virtual appliances
                AbiCloudModel.getInstance().virtualApplianceManager.virtualAppliances = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem retrieving the virtual appliances
                super.handleResult(result);
            }
        }

        public function handleGetVirtualApplianceUpdatedLogs(result:BasicResult,
                                                             virtualAppliance:VirtualAppliance):void
        {
            if (result.success)
            {
                //Updating Log list for the Virtual Appliance
                AbiCloudModel.getInstance().virtualApplianceManager.setVirtualApplianceUpdatedLogs(virtualAppliance,
                                                                                                   DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem retrieving the updated virtual appliance logs
                super.handleResult(result);
            }
        }

        public function handleMarkLogAsDeleted(result:BasicResult, virtualAppliance:VirtualAppliance,
                                               log:Log):void
        {
            if (result.success)
            {
                //Updating the Log entry
                AbiCloudModel.getInstance().virtualApplianceManager.markLogAsDeleted(virtualAppliance,
                                                                                     log);
            }
            else
            {
                //There was a problem retrieving the updated virtual appliance logs
                super.handleResult(result);
            }
        }

        public function handleCreateVirtualAppliance(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new virtual appliance to the model
                AbiCloudModel.getInstance().virtualApplianceManager.addVirtualAppliance(DataResult(result).data as VirtualAppliance);
            }
            else
            {
                //There was a problem creating the virtual appliance
                super.handleResult(result);
            }
        }

        public function handleEditVirtualAppliance(result:BasicResult, virtualAppliance:VirtualAppliance):void
        {
            //Updating variables
            _virtualAppliance = virtualAppliance;

            //First, check if server returned a VirtualAppliance
            if (result is DataResult && DataResult(result).data is VirtualAppliance)
                this._virtualApplianceReturnedByServer = DataResult(result).data as VirtualAppliance;
            else
                this._virtualApplianceReturnedByServer = null;

            if (result.success)
            {
                //Announcing that a virtual appliance has been edited
                AbiCloudModel.getInstance().virtualApplianceManager.editVirtualAppliance(this._virtualApplianceReturnedByServer);
            }
            else
            {
                //There was a problem editing the VirtualAppliance
                if (this._virtualApplianceReturnedByServer)
                    //There was an error, but the server returned a new state for the virtual appliance
                    AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                super.handleResult(result);
            }
        }

        public function handleApplyChangesVirtualAppliance(result:BasicResult, virtualAppliance:VirtualAppliance):void
        {
            //Updating variables
            _virtualAppliance = virtualAppliance;

            //First, check if server returned a VirtualAppliance
            if (result is DataResult && DataResult(result).data is VirtualAppliance)
                this._virtualApplianceReturnedByServer = DataResult(result).data as VirtualAppliance;
            else
                this._virtualApplianceReturnedByServer = null;

            if (result.success)
            {
                //Announcing that a virtual appliance has been edited
                //AbiCloudModel.getInstance().virtualApplianceManager.editVirtualAppliance(this._virtualApplianceReturnedByServer);
            }
            else
            {
                //Check if there was an error due Soft or Hard Limits
                if (result.resultCode == BasicResult.SOFT_LIMT_EXCEEDED)
                {
                    //Need to revert the virtual appliance state and subState
                    _virtualAppliance.state = VirtualAppliance(DataResult(result).data).state;
                    _virtualAppliance.subState = VirtualAppliance(DataResult(result).data).subState;
                    //Soft limits exceeded, but we can still force operation. Asking user...
                    AbiCloudAlert.showAlert(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_SOFT_LIMITS_EXCEEDED_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_SOFT_LIMITS_EXCEEDED_TEXT") + '\n' + result.message,
                                            Alert.YES | Alert.NO, onSoftLimitsExceededVirtualApplianceApplyChanges
                                            ,false,result as Object);
                }
                else if (result.resultCode == BasicResult.HARD_LIMT_EXCEEDED)
                {
                    //Hard limits exceeded, we can not edit the Virtual Appliance
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_HARD_LIMITS_EXCEEDED_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_HARD_LIMITS_EXCEEDED_TEXT") + '\n' + result.message,
                                            Alert.OK,null,false,result as Object);

                    if (this._virtualApplianceReturnedByServer)
                    {
                        //Update the VirtualAppliance with the one returned by server
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                    }
                }
                else if (result.resultCode == BasicResult.CLOUD_LIMT_EXCEEDED)
                {
                    //Not enogh resources on Datacenter, we can not edit the Virtual Appliance
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_CLOUD_LIMITS_EXCEEDED_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_CLOUD_LIMITS_EXCEEDED_TEXT") + '\n' + result.message,
                                            Alert.OK);

                    if (this._virtualApplianceReturnedByServer)
                    {
                        //Update the VirtualAppliance with the one returned by server
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                    }
                }
                else
                {
                    //There was a problem applying changes to the VirtualAppliance
                    if (this._virtualApplianceReturnedByServer)
                        //We received a VirtualAppliance from server
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);
                    else
                    {
                        //We try to make a local change to not block user
                        if (_virtualAppliance.state.id == State.IN_PROGRESS)
                            //The VirtualAppliance was APPLY_CHANGES_NEEDED. We leave as it was
                            AbiCloudModel.getInstance().virtualApplianceManager.setVirtualApplianceApplyChangesNeeded(_virtualAppliance);
                    }

                    super.handleResultInBackground(result);
                }
            }
        }

        private function onSoftLimitsExceededVirtualApplianceApplyChanges(closeEvent:CloseEvent):void
        {
            if (closeEvent.detail == Alert.YES)
            {
                //Forcing edit operation
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.APPLY_CHANGES_VIRTUAL_APPLIANCE);
                virtualApplianceEvent.virtualAppliance = _virtualAppliance;
                virtualApplianceEvent.force = true;
                Application.application.dispatchEvent(virtualApplianceEvent);
            }
            else
            {
                //We don't force the operation
                if (this._virtualApplianceReturnedByServer)
                {
                    //Update the VirtualAppliance with the one returned by server
                    AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                }
            }
        }

        public function handleDeleteVirtualAppliance(result:BasicResult, deletedVirtualAppliance:VirtualAppliance,
                                                     handleInBackground:Boolean = false):void
        {
            if (result.success)
            {
                //Announcing that a virtual appliance has been deleted
                AbiCloudModel.getInstance().virtualApplianceManager.deleteVirtualAppliance(deletedVirtualAppliance);
            }
            else
            {
                //There was a problem with the virtual appliance deletion
                if (handleInBackground)
                    super.handleResultInBackground(result);
                else
                    super.handleResult(result);
            }
        }

        public function handleStartVirtualAppliance(result:BasicResult, virtualAppliance:VirtualAppliance):void
        {
            //Updating variables

            //First, check if server returned a VirtualAppliance
            if (result is DataResult && DataResult(result).data is VirtualAppliance)
                this._virtualApplianceReturnedByServer = DataResult(result).data as VirtualAppliance;
            else
                this._virtualApplianceReturnedByServer = null;

            if (result.success)
            {
                //Announcing that the state of a Virtual Appliance has been changed
                //AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);
            }
            else
            {
                //First, check if there was an error due Soft or Hard Limits
                if (result.resultCode == BasicResult.SOFT_LIMT_EXCEEDED)
                {
                	//Updating variables
                	_virtualAppliance = virtualAppliance;
                    //Soft limits exceeded, but we can still force operation. Asking user...
                    AbiCloudAlert.showAlert(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_SOFT_LIMITS_EXCEEDED_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_SOFT_LIMITS_EXCEEDED_TEXT") + '\n' + result.message,
                                            Alert.YES | Alert.NO, onSoftLimitsExceededVirtualApplianceStart,false,result as Object);
                }
                else if (result.resultCode == BasicResult.HARD_LIMT_EXCEEDED)
                {
                    //Hard limits exceeded, we can not edit the Virtual Appliance
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_HARD_LIMITS_EXCEEDED_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_HARD_LIMITS_EXCEEDED_TEXT") + '\n' + result.message,
                                            Alert.OK,null,false,result as Object);

                    if (this._virtualApplianceReturnedByServer)
                    {
                        //Update the VirtualAppliance with the one returned by server
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                    }
                    else
                    {
                        //We try to make a local change to not block user
                        AbiCloudModel.getInstance().virtualApplianceManager.setVirtualAppliancePoweredOff(virtualAppliance);
                    }
                }
                else if (result.resultCode == BasicResult.CLOUD_LIMT_EXCEEDED)
                {
                    //Hard limits exceeded, we can not edit the Virtual Appliance
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_CLOUD_LIMITS_EXCEEDED_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_CLOUD_LIMITS_EXCEEDED_TEXT") + '\n' + result.message,
                                            Alert.OK);

                    if (this._virtualApplianceReturnedByServer)
                    {
                        //Update the VirtualAppliance with the one returned by server
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                    }
                    else
                    {
                        //We try to make a local change to not block user
                        AbiCloudModel.getInstance().virtualApplianceManager.setVirtualAppliancePoweredOff(virtualAppliance);
                    }
                }
                else if (result.resultCode == BasicResult.EMPTY_VIRTUAL_APPLIANCE)
                {
                    //Can't start an empty virtual appliance
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_EMPTY_VIRTUAL_APP_HEADER"),
                                            ResourceManager.getInstance().getString("VirtualAppliance",
                                                                                    "ALERT_EMPTY_VIRTUAL_APP_TEXT"),
                                            Alert.OK);

                    if (this._virtualApplianceReturnedByServer)
                    {
                        //Update the VirtualAppliance with the one returned by server
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                    }
                    else
                    {
                        //We try to make a local change to not block user
                        AbiCloudModel.getInstance().virtualApplianceManager.setVirtualAppliancePoweredOff(virtualAppliance);
                    }
                }
                else
                {
                    //There was a problem changing the state of a Virtual Appliance
                    if (this._virtualApplianceReturnedByServer)
                        //There was an error, but the server returned a new state for the virtual appliance
                        AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                    super.handleResultInBackground(result);
                }

            }
        }

        private function onSoftLimitsExceededVirtualApplianceStart(closeEvent:CloseEvent):void
        {
            if (closeEvent.detail == Alert.YES)
            {
                //Forcing edit operation
                var virtualApplianceEvent:VirtualApplianceEvent = new VirtualApplianceEvent(VirtualApplianceEvent.START_VIRTUALAPPLIANCE);
                virtualApplianceEvent.virtualAppliance = _virtualAppliance;
                virtualApplianceEvent.force = true;
                Application.application.dispatchEvent(virtualApplianceEvent);
            }
            else
            {
                //We don't force the operation
                if (this._virtualApplianceReturnedByServer)
                {
                    //Update the VirtualAppliance with the one returned by server
                    AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                }
                else
                {
                    //We try to make a local change to not block user
                    AbiCloudModel.getInstance().virtualApplianceManager.setVirtualAppliancePoweredOff(_virtualAppliance);
                }
            }
        }

        public function handleShutDownVirtualAppliance(result:BasicResult, virtualAppliance:VirtualAppliance):void
        {
            if (result.success)
            {
                //Announcing that the state of a Virtual Appliance has been changed
                AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(DataResult(result).data as VirtualAppliance);
            }
            else
            {
                //There was a problem changing the state of a Virtual Appliance
                if (result is DataResult && DataResult(result).data != null)
                    //There was an error, but the server returned a new state for the virtual appliance
                    AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(DataResult(result).data as VirtualAppliance);

                super.handleResultInBackground(result);
            }
        }

        public function handleCheckVirtualDatacentersAndAppliancesByEnterprise(result:BasicResult):void
        {
            if (result.success)
            {
                //VirtualDatacenters and Appliance's checked successfully
                var virtualDatacentersAndAppliancesChecked:ArrayCollection = DataResult(result).data as ArrayCollection;

                var virtualDatacentersChecked:ArrayCollection = virtualDatacentersAndAppliancesChecked.getItemAt(0) as ArrayCollection;
                var virtualAppliancesChecked:ArrayCollection = virtualDatacentersAndAppliancesChecked.getItemAt(1) as ArrayCollection;
                AbiCloudModel.getInstance().virtualApplianceManager.checkVirtualDatacentersAndAppliances(virtualDatacentersChecked,
                                                                                                         virtualAppliancesChecked);
            }
            else
            {
                //There was a problem checking the virtual datacenters or appliance's state
                //We check if it's a manual user interaction or a background process
                if(AbiCloudModel.getInstance().virtualApplianceManager.serverCallType){
                	super.handleResultInBackground(result);                	
                }else{
                	super.handleResult(result);
                }
                AbiCloudModel.getInstance().virtualApplianceManager.callProcessComplete = true;
                
            }
        }

        public function handleCheckVirtualAppliance(result:BasicResult):void
        {
            if (result.success)
            {
                //Virtual Appliance checked successfully
                var virtualApplianceChecked:VirtualAppliance = DataResult(result).data as VirtualAppliance;
                AbiCloudModel.getInstance().virtualApplianceManager.checkVirtualAppliance(virtualApplianceChecked);
            }
            else
            {
                //There was a problem checking the virtual appliance's state
                super.handleResultInBackground(result);
            }
        }

        public function handleForceRefreshVirtualApplianceState(result:BasicResult):void
        {
            if (result.success)
            {
                //Nothing to do. This call does not have any effect in client
            }
            else
            {
                //There was a problem forcing the virtual appliance refresh
                super.handleResult(result);
            }
        }

        public function handleCreateVirtualApplianceBundle(result:BasicResult, virtualAppliance:VirtualAppliance):void
        {
            //Updating variables
            _virtualAppliance = virtualAppliance;
            //First, check if server returned a VirtualAppliance
            if (result is DataResult && DataResult(result).data is VirtualAppliance)
                this._virtualApplianceReturnedByServer = DataResult(result).data as VirtualAppliance;
            else
                this._virtualApplianceReturnedByServer = null;

            if (result.success)
            {
                //Announcing that bundle has been created and the state of a Virtual Appliance has been changed
                AbiCloudModel.getInstance().virtualApplianceManager.virtualApplianceBundleCreated(this._virtualApplianceReturnedByServer);
            }
            else
            {
                //There was a problem creating the bundle
                if (this._virtualApplianceReturnedByServer)
                    //There was an error, but the server returned a new state for the virtual appliance
                    AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(this._virtualApplianceReturnedByServer);

                super.handleResultInBackground(result);
            }
        }


        public function handleCancelVirtualApplianceDeployment(result:BasicResult):void
        {
            if (result.success)
            {
                //Announcing that the state of a Virtual Appliance has been changed
                AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(DataResult(result).data as VirtualAppliance);
            }
            else
            {
                //There was a problem changing the state of a Virtual Appliance
                if (result is DataResult && DataResult(result).data != null)
                    //There was an error, but the server returned a new state for the virtual appliance
                    AbiCloudModel.getInstance().virtualApplianceManager.changeVirtualApplianceState(DataResult(result).data as VirtualAppliance);

                super.handleResultInBackground(result);
            }
        }
        
        public function handleGetVirtualApplianceLogs(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                //Returning the list of logs retrieved
                var logs:ArrayCollection = DataResult(result).data as ArrayCollection;
                callback(logs);
            }
            else
            {
                //There was a problem forcing the virtual appliance refresh
                super.handleResult(result);
            }
        }
        
    }
}