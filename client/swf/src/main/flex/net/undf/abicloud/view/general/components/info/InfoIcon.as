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
	import mx.effects.Fade;
	
	import net.undf.abicloud.view.main.CommonAssets;

	[Bindable]
	public class InfoIcon extends Image
	{		
		//show/hide effect
		private var _fadeEffect:Fade;
		
		//Wiki's URL opens when user clicks
		private var _wikiUrl:String;
		
		public function InfoIcon()
		{
			super();
			_fadeEffect = new Fade();
			source = CommonAssets.info;
			buttonMode = true;
			//visible = false;
			toolTip = resourceManager.getString('Common','TOOLTIP_INFO');
			addEventListener(MouseEvent.CLICK, openMoreInfo);
			setStyle("showEffect", _fadeEffect);
            setStyle("hideEffect", _fadeEffect);
		}
		
		/******************
		 * 
		 * Getters/Setters 
		 * 
		 * ****************/
		
		public function set wikiUrl(url:String):void{
			this._wikiUrl = url;
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
         * Show the info icon if user presses the CTRL key
         */
        public function showIconInfo(keyPressed:Boolean):void
        {
           visible = keyPressed;
           buttonMode = keyPressed;
        }

        /**
         * Hide the info icon
         */
        public function hideIconInfo():void
        {
            visible = false;
            buttonMode = false;
        }		
		
		
	}
}