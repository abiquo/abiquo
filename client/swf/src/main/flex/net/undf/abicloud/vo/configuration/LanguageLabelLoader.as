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
 
package net.undf.abicloud.vo.configuration
{
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.utils.Dictionary;
	
	import mx.controls.Alert;
	import mx.resources.ResourceBundle;
	import mx.resources.ResourceManager;
	
	import net.undf.abicloud.model.AbiCloudModel;
	
	[Bindable]
	public class LanguageLabelLoader
	{
	    private var PROPERTY_FILE:String = "default.properties";
	    
	    public var vars:Dictionary = new Dictionary();
	    
	    private var url_request:URLRequest;
	    
	    private var url_loader:URLLoader;
	    
	    //Not used as we use ResourceManager
	    [Bindable(event="labelChanged")]
	    public function getValue(key:String):String
	    {
	      if(key != null && key.length > 0)
	      {
	        return vars[key];
	      }
	      return null;
	    }
	
	    public function LanguageLabelLoader(fileName:String) 
	    {
	      /* Initialize the two ArrayCollections objects with empty arrays. */
	      PROPERTY_FILE = fileName;
	      /* Initialize the URLRequest object with the URL to the file of name/value pairs. */
	      url_request = new URLRequest("languages/"+AbiCloudModel.getInstance().configurationManager.selectedLanguage.value+"/"+PROPERTY_FILE);
	      /* Initialize the URLLoader object, assign the various event listeners, and load the specified URLRequest object. */
	      url_loader = new URLLoader();
	      url_loader.addEventListener(Event.COMPLETE, loadLabels);
	      url_loader.addEventListener(IOErrorEvent.IO_ERROR, loadPropertyFileIOErrorHandler);
	      url_loader.load(url_request);
	    }
	    
	    private function loadLabels(evt:Event):void  
	    {
            var ldr:URLLoader = evt.currentTarget as URLLoader;
            var lines:Array = ( ldr.data as String ).split( "\n" );

            var resourceBundleName:String = PROPERTY_FILE.split(".")[0].toString();
            //Adding resource to ResourceManager
            var resouceBundle:ResourceBundle = new ResourceBundle(AbiCloudModel.getInstance().configurationManager.selectedLanguage.value,resourceBundleName);

            for each ( var line:String in lines ) {
                var pair:Array = line.split( "=" );
                //Only if line has key=value
                if(pair.length == 2){
	                var value:String = pair[1].toString().replace('\r','');
	                vars[pair[0]] = value;
		            resouceBundle.content[pair[0]] = value;
	                dispatchEvent(new Event("labelChanged"));
                }
            }
            
            //Adding ResourceBundle to ResourceManager
            ResourceManager.getInstance().addResourceBundle(resouceBundle);
            
            ResourceManager.getInstance().update();
            
	   }
	   
	   /**
         * Handler when it fails to load the property file
         */
        private function loadPropertyFileIOErrorHandler(ioErrorEvent:IOErrorEvent):void
        {
            Alert.show('Error while loading loading property file : '+ PROPERTY_FILE + " for language " + AbiCloudModel.getInstance().configurationManager.selectedLanguage.name);
        }
	}
}