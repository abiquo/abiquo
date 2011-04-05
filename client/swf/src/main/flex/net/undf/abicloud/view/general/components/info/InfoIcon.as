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

package net.undf.abicloud.view.general.components.info
{
	import flash.events.MouseEvent;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	
	import mx.controls.Image;
	
	import net.undf.abicloud.model.AbiCloudModel;
	import net.undf.abicloud.view.main.CommonAssets;

	[Bindable]
	public class InfoIcon extends Image
	{		
		//Wiki's URL opens when user clicks
		private var _wikiUrl:String;
		
		public function InfoIcon()
		{
			super();
			source = CommonAssets.info;
			buttonMode = true;
			toolTip = resourceManager.getString('Common','TOOLTIP_INFO');
			addEventListener(MouseEvent.CLICK, openMoreInfo);
		}
		
		/******************
		 * 
		 * Getters/Setters 
		 * 
		 * ****************/
		
		public function set wikiUrl(url:String):void{
			this._wikiUrl = url;
			displayIcon();
		}
		
		public function get wikiUrl():String{
			return this._wikiUrl;
		}
		
		
		/**
         * Display the specific Wiki page
         */
        private function openMoreInfo(mouseEvent:MouseEvent):void{
        	if(visible){
        		//visible = false;
        		navigateToURL(new URLRequest(wikiUrl),"_blank");
        	}
        }
        
        /**
         * Display the icon if required
         */
        private function displayIcon():void{
    		visible = true;
        	if(AbiCloudModel.getInstance().configurationManager.config.client_wiki_showDefaultHelp.value == 0){
        		if(this._wikiUrl == AbiCloudModel.getInstance().configurationManager.config.client_wiki_defaultURL.value){
	        		visible = false;        			
        		}
        	}
        }
		
	}
}