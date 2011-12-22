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

package net.undf.abicloud.events
{
    import flash.events.Event;
    
    import net.undf.abicloud.vo.configuration.Registration;

    public class ConfigurationEvent extends Event
    {
        /* ------------- Constants------------- */
        public static const ASK_FOR_REGISTRATION_REMINDER_SCREEN:String = "askForRegistrationStatusConfigurationEvent";

        public static const SHOW_REGISTRATION_REMINDER_SCREEN:String = "showRegistrationScreenConfigurationEvent";

        public static const SET_REGISTRATION_STATUS_LATER:String = "setRegistrationStatusLaterConfigurationEvent";

        public static const SET_REGISTRATION_STATUS_NO:String = "setRegistrationStatusNoConfigurationEvent";

        public static const SEND_REGISTRATION_INFO:String = "setRegistrationInfoConfigurationEvent";

        public static const GET_REGISTRATION_INFO:String = "getRegistrationInfoConfigurationEvent";
		
		//Improvement -> CONFIG
		//load properties from database
		public static const LOAD_CONFIGURATION_PROPERTIES:String = "";


        //////////////////////////////////////////////////////////////////////////////////////////
        public static const ASK_FOR_HEARTBEAT_REMINDER_SCREEN:String = "askForHeartbeatReminderScreenConfigurationEvent";

        public static const SHOW_HEARTBEAT_REMINDER_SCREEN:String = "showHeartbeatReminderScreenConfigurationEvent";

        public static const TEST_HEARTBEAT:String = "testHeartbeatConfigurationEvent";

        public static const HEARTBEAT_TESTED:String = "heartbeatTestedConfigurationEvent";

        public static const GET_HEARTBEAT_STATUS:String = "getHeartbeatStatusConfigurationEvent";

        public static const SET_HEARTBEAT_STATUS_ENABLED:String = "setHeartbeatStatusEnabledConfigurationEvent";

        public static const SET_HEARTBEAT_STATUS_DISABLED:String = "setHeartbeatStatusDisabledConfigurationEvent";

        public static const SET_HEARTBEAT_STATUS_LATER:String = "setHeartbeatStatusLaterConfigurationEvent";

        public static const SET_HEARTBEAT_STATUS_NO:String = "setHeartbeatStatusNoConfigurationEvent";

        public static const GET_LAST_HEARTBEAT_INFO:String = "getLastHeartbeatInfoConfigurationEvent";

        public static const GET_LAST_HEARTBEAT_ENTRIES:String = "getLastHeartbeatEntriesConfigurationEvent";
        
        public static const LOAD_LANGUAGE_LABEL:String = "loadLanguageLabelsConfigurationPremiumEvent";

        /* ------------- Constructor ------------- */
        public function ConfigurationEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            //TODO: implement function
            super(type, bubbles, cancelable);
        }

        /* ------------- Public atributes ------------- */
        public var registration:Registration;

        public var rows:int;
    }
}