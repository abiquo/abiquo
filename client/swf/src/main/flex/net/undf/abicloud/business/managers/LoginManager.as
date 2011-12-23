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
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.vo.authentication.Session;
    import net.undf.abicloud.vo.user.User;

    /**
     * Manager for Login process, a component from AbiCloud application
     *
     * This class stores data from the user who has logged in AbiCloud
     * It can also inform the view when a user has logged in to the application
     **/

    [Bindable]
    public class LoginManager
    {

        /* ------------- Private attributes ------------- */

        //User's session who has logged in AbiCloud
        private var _session:Session;

        //User's information who has logged in AbiCloud
        private var _user:User;

        //True if a user has logged in
        private var _userLogged:Boolean;

        private var _sessionValid:Boolean;

        /* ------------- Setters & Getters ------------- */
        public function set session(userSession:Session):void
        {
            this._session = userSession;
        }

        public function get session():Session
        {
            return this._session;
        }

        public function set user(usr:User):void
        {
            this._user = usr;
        }

        public function get user():User
        {
            return this._user;
        }

        public function set userLogged(logged:Boolean):void
        {
            this._userLogged = logged;
        }

        public function get userLogged():Boolean
        {
            return this._userLogged;
        }


        public function set sessionValid(value:Boolean):void
        {
            this._sessionValid = value;
        }

        public function get sessionValid():Boolean
        {
            return this._sessionValid;
        }

        /* ------------- Constructor ------------- */
        public function LoginManager()
        {
            this._session = null;
            this._user = null;
            this._userLogged = false;
        }



    /* ------------- Public methods ------------- */
    	//function to return selected language passed in the URL -> singleSignOn
	    public function returnLanguageIndex(languageArray:ArrayCollection , language:String):int{
	    	for(var i:Number = 0 ; i < languageArray.length ; i++){
	    		if(languageArray.getItemAt(i).value == language){
	    			return i;
	    		}
	    	}
	    	return -1;
	    }
    }
}