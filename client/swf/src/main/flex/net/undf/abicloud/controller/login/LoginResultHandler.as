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

package net.undf.abicloud.controller.login
{
    import flash.events.Event;
    import flash.net.URLRequest;
    import flash.net.navigateToURL;
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.utils.ScreenBlocker;
    import net.undf.abicloud.vo.authentication.LoginResult;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.user.Role;
    import net.undf.abicloud.vo.user.Privilege;
    import net.undf.abicloud.vo.user.PrivilegeType;

    public class LoginResultHandler extends ResultHandler
    {

        /* ------------- Constructor --------------- */
        public function LoginResultHandler()
        {
            super();
        }



        public function loginHandler(result:BasicResult):void
        {    	
        	
            if (result.success)
            {
                var loginResult:LoginResult = DataResult(result).data as LoginResult;

                //Saving user's session
                AbiCloudModel.getInstance().loginManager.session = loginResult.session;
                AbiCloudModel.getInstance().loginManager.sessionValid = true;

                //Saving user's information
                AbiCloudModel.getInstance().loginManager.user = loginResult.user;

                //Saving user's authorized client resources
                AbiCloudModel.getInstance().userManager.privileges = loginResult.privileges;

                //Notifying that a user has logged in
                AbiCloudModel.getInstance().loginManager.userLogged = true;                

            }
            else
            {
            	//We unblock the screen as the login process failed
            	ScreenBlocker.unblockScreen();
            	
				//we dispatch an event to display the login form if it's not yet displayed
           		AbiCloudModel.getInstance().loginManager.dispatchEvent(new Event('tokenFailed'));
                //There was a problem with the login process.
                //Most frequently, when user or password were incorrect
                super.handleResult(result);
            }
        }


        public function logoutHandler(result:BasicResult):void
        {
            if (result.success)
            {
               
                //check if we close automatically the browser or not
                /***
                * 
                * In firefox, this functionality is impossible, 
                * the browser doesn't allow the automatic browse/tab to be close.
                * So this functionality is implemented but not used -> [ABICLOUDPREMIUM-396]
                * 
                * **/
                /* if(AbiCloudModel.getInstance().configurationManager.config['CLOSE_BROWSER_LOGOUT'] == 0){
	                navigateToURL(new URLRequest("javascript:location.replace('"+AbiCloudModel.getInstance().configurationManager.config.currentUrl+"')"),
	                              "_self");
                }else{
                	ExternalInterface.call("closeBrowser");
                }   */
                navigateToURL(new URLRequest("javascript:location.replace('"+AbiCloudModel.getInstance().configurationManager.config.currentUrl+"')"),
	                              "_self");
            }
            else
            {
                //There was a problem with the logout process
                super.handleResult(result);
            }
        }

    }
}