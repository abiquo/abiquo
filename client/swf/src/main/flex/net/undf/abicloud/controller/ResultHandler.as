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

package net.undf.abicloud.controller
{
    import flash.net.URLRequest;
    import flash.net.navigateToURL;
    
    import mx.controls.Alert;
    import mx.events.CloseEvent;
    import mx.resources.ResourceBundle;
    import mx.resources.ResourceManager;
    
    import net.undf.abicloud.business.managers.notification.Notification;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.result.BasicResult;

    public class ResultHandler
    {
        /* ------- Constructor ------ */
        public function ResultHandler()
        {

        }

        [ResourceBundle("Common")]
        private var rb:ResourceBundle;

        //Standard method to notify user how last remote call has ended
        public function handleResult(basicResult:BasicResult):void
        {
            if (!basicResult.success)
            {
                //Server returned an error
                switch (basicResult.resultCode)
                {
                    case BasicResult.USER_INVALID:
                        //Invalid user or password				   
                        AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_TITLE_LABEL"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_MESSAGE_USERINVALID"),
                                                Alert.OK);
                        break;

                    case BasicResult.SESSION_INVALID:
                        //Invalid Session
                        if (AbiCloudModel.getInstance().loginManager.sessionValid)
                        {
                            AbiCloudModel.getInstance().loginManager.sessionValid = false;

                            AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                            "ALERT_ERROR_TITLE_LABEL"),
                                                    ResourceManager.getInstance().getString("Common",
                                                                                            "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                    ResourceManager.getInstance().getString("Common",
                                                                                            "ALERT_MESSAGE_SESSIONINVALID"),
                                                    Alert.OK,
                                                    onSessionInvalid);
                        }

                        break;

                    case BasicResult.SESSION_TIMEOUT:
                        //The session has time out
                        if (AbiCloudModel.getInstance().loginManager.sessionValid)
                        {
                            AbiCloudModel.getInstance().loginManager.sessionValid = false;

                            AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                            "ALERT_ERROR_TITLE_LABEL"),
                                                    ResourceManager.getInstance().getString("Common",
                                                                                            "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                    ResourceManager.getInstance().getString("Common",
                                                                                            "ALERT_MESSAGE_SESSIONTIMEOUT"),
                                                    Alert.OK,
                                                    onSessionInvalid);
                        }

                        break;

                    case BasicResult.SESSION_MAX_NUM_REACHED:
                        //The maximum number of simultaneous sessions has been reached
                        AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_TITLE_LABEL"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_MESSAGE_SESSION_MAX_NUM"),
                                                Alert.OK);
                        break;

                    case BasicResult.AUTHORIZATION_NEEDED:
                        //Not used yet
                        break;

                    case BasicResult.NOT_AUTHORIZED:
                        //The resource is not allowed
                        AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_TITLE_LABEL"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                basicResult.message,
                                                Alert.OK);
                        break;

                    case BasicResult.VIRTUAL_IMAGE_IN_USE:
                        AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_TITLE_LABEL"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_MESSAGE_SESSION_VIRTUAL_IMAGE_IN_USE"),
                                                Alert.OK);
                        break;

                    default:
                        //Default response -> BasicResult.STANDARD_RESULT
                        //TEMP [ABICLOUDPREMIUM-440] -> catch the session expired error message to redirect it to the standard handleResult process
                        var searchTimeOut:int = basicResult.message.search("The session is invalid");
                        var searchInvalid:int = basicResult.message.search("The session has timed out");
                        var newBasicResult:BasicResult = basicResult;
                        if(searchTimeOut != -1){
                        	newBasicResult.resultCode = BasicResult.SESSION_TIMEOUT;
                        	handleResult(newBasicResult);
                        	return;
                        }
                        if(searchInvalid != -1){
                        	newBasicResult.resultCode = BasicResult.SESSION_INVALID;
                        	handleResult(newBasicResult);
                        	return;
                        }
                        AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_TITLE_LABEL"),
                                                ResourceManager.getInstance().getString("Common",
                                                                                        "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                basicResult.message,
                                                Alert.OK);
                }
            }
            else
            {
                //Server call was successful...
                AbiCloudAlert.showConfirmation(ResourceManager.getInstance().getString("Common",
                                                                                       "ALERT_SUCCESS_TITLE_LABEL"),
                                               ResourceManager.getInstance().getString("Common",
                                                                                       "ALERT_SUCCESS_SERVER_RESPONSE_HEADER"),
                                               basicResult.message,
                                               Alert.OK);
            }


        }


        public function handleJSONResult(jsonResult:Object):void
        {
            if (jsonResult)
            {
                if (jsonResult.hasOwnProperty("msg"))
                    //The JSON call returned an error message
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                            jsonResult.msg,
                                            Alert.OK);

                else
                    //There is any error message
                    AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_TITLE_LABEL"),
                                            ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                            ResourceManager.getInstance().getString("Common",
                                                                                    "ALERT_UNEXPECTED_ERROR_APPLIANCE_MANAGER"),
                                            Alert.OK);
            }
            else
            {
                //The server returned anything
                AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_TITLE_LABEL"),
                                        ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                        ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_UNEXPECTED_ERROR_APPLIANCE_MANAGER"),
                                        Alert.OK);
            }
        }


        //Same as function above, but in case of error, handles server's response using the background notification system
        public function handleResultInBackground(basicResult:BasicResult):void
        {
            if (!basicResult.success)
            {
                var notification:Notification = new Notification(ResourceManager.getInstance().getString("Common",
                                                                                                         "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                                                 basicResult.message);
                AbiCloudModel.getInstance().notificationManager.addNotification(notification);
            }
        }


        private function onSessionInvalid(closeEvent:CloseEvent):void
        {
            //Provisional logout
            navigateToURL(new URLRequest("javascript:location.reload(true)"), "_self");
        }
    }
}