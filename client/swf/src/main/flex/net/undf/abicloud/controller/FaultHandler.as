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

    import mx.controls.Alert;
    import mx.resources.ResourceBundle;
    import mx.resources.ResourceManager;
    import mx.rpc.Fault;
    
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.view.general.AbiCloudAlert;
    import net.undf.abicloud.vo.result.BasicResult;


    /**
     * Fault Handler class to manage uncontrolled fault server responses.
     * For example, when the server is unreachable
     **/
    public class FaultHandler
    {
        public function FaultHandler()
        {

        }

        [ResourceBundle("Common")]
        private var rb:ResourceBundle;

        public function handleFault(fault:Object):void
        {        	
        	if (fault.faultCode == "Channel.Call.Failed" || fault.faultCode == "Client.Error.MessageSend")
            {
                if (AbiCloudModel.getInstance().loginManager.userLogged)
                {
                    //Using NotificationManager when the user is logged and the fault reason
                    //was due network problems or server unreachable
                    AbiCloudModel.getInstance().notificationManager.isServerUnreachable = true;
                }
                
                AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_TITLE_LABEL"),
                                        ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                        ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_NO_CONNECTION_TEXT"),
                                        Alert.OK);

            }
            else if(fault.faultCode == "Server.Processing"){
            	
            	//TEMP [ABICLOUDPREMIUM-440] -> catch the session expired error message to redirect it to the standard handleResult process
        		if(Fault(fault).rootCause.hasOwnProperty("result")){
	        		if(Fault(fault).rootCause.result is BasicResult){
		        		var tempBasicResult:BasicResult = Fault(fault).rootCause.result as BasicResult;
		        		var resultHandler:ResultHandler = new ResultHandler();
		        		resultHandler.handleResult(tempBasicResult);
		        		return;        			
	        		}        			
        		}
            	
            	//Optimize the displayed message
            	var index:int = String(fault.faultString).indexOf(":");
            	var errorTxt:String = String(fault.faultString).substr(index + 1);
            	
            	AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_TITLE_LABEL"),
                                        ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                        errorTxt,
                                        Alert.OK);
            }
            else
                //Standar fault behaviour
                AbiCloudAlert.showError(ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_TITLE_LABEL"),
                                        ResourceManager.getInstance().getString("Common",
                                                                                "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
                                        fault.toString(),
                                        Alert.OK);
        }

        public function handleFaultInBackground(fault:Object):void
        {
            if (fault.faultCode == "Channel.Call.Failed" || fault.faultCode == "Client.Error.MessageSend")
            {
                //Using NotificationManager when the user is logged and the fault reason
                //was due network problems or server unreachable
                AbiCloudModel.getInstance().notificationManager.isServerUnreachable = true;
            }

        /* We were asked to no generate  Notification...
           var notification:Notification = new Notification(ResourceManager.getInstance().getString("Common", "ALERT_ERROR_SERVER_RESPONSE_HEADER"),
           fault.toString());
         AbiCloudModel.getInstance().notificationManager.addNotification(notification); */
        }
    }
}