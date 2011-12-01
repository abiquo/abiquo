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

package net.undf.abicloud.controller.configuration
{
    import mx.collections.ArrayCollection;
    import mx.controls.Alert;
    import mx.resources.ResourceBundle;
    import mx.resources.ResourceManager;

    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.events.ConfigurationEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.configuration.Heartbeat;
    import net.undf.abicloud.vo.configuration.Registration;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;

    public class ConfigurationResultHandler extends ResultHandler
    {

        [ResourceBundle("Common")]
        private var commonRB:ResourceBundle;

        [ResourceBundle("Configuration")]
        private var configurationRB:ResourceBundle;

        public function ConfigurationResultHandler()
        {
            super();
        }


        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //REGISTRATION HANDLERS
        public function handleAskForRegistrationReminderScreen(result:BasicResult):void
        {
            if (result.success)
            {
                if (DataResult(result).data == true)
                {
                    //Announcing that registration reminder screen must be shown
                    var configurationEvent:ConfigurationEvent = new ConfigurationEvent(ConfigurationEvent.SHOW_REGISTRATION_REMINDER_SCREEN);
                    AbiCloudModel.getInstance().configurationManager.dispatchEvent(configurationEvent);
                }
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleSendRegistrationInfo(result:BasicResult):void
        {
            if (result.success)
            {
                //Registration was sent successfully
                AbiCloudAlert.showConfirmation(ResourceManager.getInstance().getString("Common",
                                                                                       "ALERT_SUCCESS_TITLE_LABEL"),
                                               ResourceManager.getInstance().getString("Configuration",
                                                                                       "ALERT_REGISTRATION_INFO_SEND_SUCCESS_HEADER"),
                                               ResourceManager.getInstance().getString("Configuration",
                                                                                       "ALERT_REGISTRATION_INFO_SEND_SUCCESS_TEXT"),
                                               Alert.OK);

                //Updating registration info in model
                AbiCloudModel.getInstance().configurationManager.registration = DataResult(result).data as Registration;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetRegistrationInfo(result:BasicResult):void
        {
            if (result.success)
            {
                //Registration retrieved successfully
                AbiCloudModel.getInstance().configurationManager.registration = DataResult(result).data as Registration;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleSetRegistrationStatusLater(result:BasicResult):void
        {
            if (result.success)
            {
                //Nothing to do	
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleSetRegistrationStatusNo(result:BasicResult):void
        {
            if (result.success)
            {
                //Nothing to do	
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }



        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //HEARTBEATING HANDLERS
        public function handleAskForHeartbeatReminderScreen(result:BasicResult):void
        {
            if (result.success)
            {
                if (DataResult(result).data == true)
                {
                    var configurationEvent:ConfigurationEvent = new ConfigurationEvent(ConfigurationEvent.SHOW_HEARTBEAT_REMINDER_SCREEN);
                    AbiCloudModel.getInstance().configurationManager.dispatchEvent(configurationEvent);
                }
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetHeartbeatStatus(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().configurationManager.isHeartbeatEnabled = DataResult(result).data as Boolean;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleSetHeartbeatStatusEnabled(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().configurationManager.isHeartbeatEnabled = true;
                AbiCloudModel.getInstance().configurationManager.lastHeartbeatInfo = DataResult(result).data as Heartbeat;
            }
            else
            {
                //There was a problem
                AbiCloudModel.getInstance().configurationManager.isHeartbeatEnabled = false;
                super.handleResult(result);
            }
        }

        public function handleSetHeartbeatStatusDisabled(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().configurationManager.isHeartbeatEnabled = false;
            }
            else
            {
                //There was a problem
                AbiCloudModel.getInstance().configurationManager.isHeartbeatEnabled = true;
                super.handleResult(result);
            }
        }

        public function handleSetHeartbeatStatusLater(result:BasicResult):void
        {
            if (result.success)
            {
                //Nothing to do...
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleSetHeartbeatStatusNo(result:BasicResult):void
        {
            if (result.success)
            {
                //Nothing to do...
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetLastHeartbeatInfo(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().configurationManager.lastHeartbeatInfo = DataResult(result).data as Heartbeat;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetLastHeartbeatEntries(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().configurationManager.lastHeartbeatEntries = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
    }
}