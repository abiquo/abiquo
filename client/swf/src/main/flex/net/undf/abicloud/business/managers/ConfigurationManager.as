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
    import flash.events.IOErrorEvent;
    import flash.net.SharedObject;
    import flash.net.URLLoader;
    import flash.net.URLRequest;
    
    import mx.collections.ArrayCollection;
    import mx.controls.Alert;
    import mx.messaging.ChannelSet;
    import mx.messaging.channels.AMFChannel;
    import mx.messaging.channels.SecureAMFChannel;
    
    import net.undf.abicloud.vo.configuration.Config;
    import net.undf.abicloud.vo.configuration.Heartbeat;
    import net.undf.abicloud.vo.configuration.Registration;

    [Bindable]
    public class ConfigurationManager extends EventDispatcher
    {


        public function ConfigurationManager()
        {
            this._config = new Config();

            loadXMLConfigFile();
        }

        /**
         * Loads the client-config.xml file, containing all the parameters
         * for the application, and stores the parameters in _config associative array
         * for further access
         */
        private function loadXMLConfigFile():void
        {
            var loader:URLLoader = new URLLoader();
            loader.addEventListener(Event.COMPLETE, loadXMLConfigFileCompleteHandler);
            loader.addEventListener(IOErrorEvent.IO_ERROR, loadXMLConfigFileIOErrorHandler);
			loader.load(new URLRequest("config/client-config.xml")); 
        }

        /**
         * Handler when the xml file finishes to load
         */
        private function loadXMLConfigFileCompleteHandler(event:Event):void
        {
            var xmlFile:XML = XML(URLLoader(event.currentTarget).data);
            var xmlList:XMLList = xmlFile.child("param");

            var length:int = xmlList.length();
            var i:int;
            for (i = 0; i < length; i++)
            {
                this._config[xmlList[i].name] = xmlList[i].value;
            }

            dispatchEvent(new Event("configChange"));

            //Once the xml file is properly loaded, we can load specific configuration
            loadChannelSet();
        }

        /**
         * Handler when it fails to load the xml file
         */
        private function loadXMLConfigFileIOErrorHandler(ioErrorEvent:IOErrorEvent):void
        {
            Alert.show("Unable to load client-config.xml.jsp. The application will not start correctly",
                       "Error");
        }


        private function loadChannelSet():void
        {
            var channelList:XMLList = this._config.channels.channels.channel;

            var length:int = channelList.length();
            var i:int;
            this._channelSet = new ChannelSet();
            this._secureChannelSet = new ChannelSet();
            for (i = 0; i < length; i++)
            {
                //Checking the channel type
                if (channelList[i].type == "amf")
                {
                    //Creating the AMF channel and adding it to the Application's Channel Set
                    var amfChannel:AMFChannel = new AMFChannel(channelList[i].id,
                                                               channelList[i].endpoint);
                    this._channelSet.addChannel(amfChannel);
                }
                else if (channelList[i].type == "amfsecure")
                {
                    //Creating the Secure AMF Channel and adding it to the Application's Channel Set
                    var secureAmfChannel:SecureAMFChannel = new SecureAMFChannel(channelList[i].id,
                                                                                 channelList[i].endpoint);
                    this._secureChannelSet.addChannel(secureAmfChannel);
                }
            }

            dispatchEvent(new Event("channelSetUpdated"));
        }

        /**
         * Returns an associative array contaning key - value pairs
         * where a key is the name of a param node in client-config.xml
         * and value is the value of a param node in the same file
         *
         * @return the associative array containing all parameters for the application to run
         */
        private var _config:Config;

        [Bindable(event="configChange")]
        public function get config():Config
        {
            return this._config;
        }

        /**
         * The ChannelSet with the channels available to the application
         */
        private var _channelSet:ChannelSet;

        [Bindable(event="channelSetUpdated")]
        public function get channelSet():ChannelSet
        {
            return this._channelSet;
        }

        private var _secureChannelSet:ChannelSet;

        [Bindable(event="channelSetUpdated")]
        public function get secureChannelSet():ChannelSet
        {
            return this._secureChannelSet;
        }


        //////////////////////////////////////////////////////////////////////////////////////////
        // REGISTRATION

        /**
         * A Registration object contains the current system registration information, if
         * this system has been registered
         */
        private var _registration:Registration = null;

        [Bindable(change="registrationChange")]
        public function get registration():Registration
        {
            return this._registration;
        }

        public function set registration(value:Registration):void
        {
            this._registration = value;
            dispatchEvent(new Event("registrationChange"));
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // HEARTBEATING

        /**
         * Flag that indicates the Heartbeat enabled status
         */
        private var _isHeartbeatEnabled:Boolean = false;

        [Bindable(event="isHeartbeatEnabledChange")]
        public function get isHeartbeatEnabled():Boolean
        {
            return this._isHeartbeatEnabled
        }

        public function set isHeartbeatEnabled(value:Boolean):void
        {
            this._isHeartbeatEnabled = value;
            dispatchEvent(new Event("isHeartbeatEnabledChange"));
        }

        /**
         * Heartbeat object containing the last Heartbeat information available
         */
        private var _lastHeartbeatInfo:Heartbeat = new Heartbeat();

        [Bindable(event="lastHeartbeatInfoChange")]
        public function get lastHeartbeatInfo():Heartbeat
        {
            return this._lastHeartbeatInfo
        }

        public function set lastHeartbeatInfo(value:Heartbeat):void
        {
            this._lastHeartbeatInfo = value;
            dispatchEvent(new Event("lastHeartbeatInfoChange"));
        }

        /**
         * Array containing the last Heartbeat logs entries
         */
        private var _lastHeartbeatEntries:ArrayCollection;

        [Bindable(event="lastHeartbeatEntriesChange")]
        public function get lastHeartbeatEntries():ArrayCollection
        {
            return this._lastHeartbeatEntries;
        }

        public function set lastHeartbeatEntries(value:ArrayCollection):void
        {
            this._lastHeartbeatEntries = value;
            dispatchEvent(new Event("lastHeartbeatEntriesChange"));
        }
        
        public function reportSystemPropertiesLoaded():void{
        	dispatchEvent(new Event("systemPropertiesLoaded"));
        }
        
       /**
         * Check if the shared object (cookie) exists
         * and if it's the case we retrieved the value
         */
        public function checkRegisteredSharedObject(sharedObject:String):Boolean{
            var so:SharedObject = SharedObject.getLocal(sharedObject);
            if (so.data.value != null){
            	return so.data.value;
            }else{
            	return true;
            }
        }
        
        /**
         * Check if the shared object (cookie) exists
         * and if it's not the case we create it
         */
        public function modifyRegisteredSharedObject(sharedObject:String , value:Boolean):void{
            var so:SharedObject = SharedObject.getLocal(sharedObject);
            so.data.value = value;
        }
    }
}
