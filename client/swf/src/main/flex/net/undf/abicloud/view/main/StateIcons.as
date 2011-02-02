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

package net.undf.abicloud.view.main
{
	import mx.controls.Alert;
	import mx.styles.StyleManager;
	
	
	/**
	 * This class holds the common state icons aquired from the CSS  
	 * @author GaB
	 * 
	 */
	public class StateIcons
	{
		[Bindable]
		public static var RUNNING:*;
		[Bindable]
		public static var PAUSED:*;
		[Bindable]
		public static var APPLY_CHANGES_NEEDED:*;
		[Bindable]
		public static var POWERED_OFF:*;
		[Bindable]
		public static var NOT_DEPLOYED:*;
		[Bindable]
		public static var STOPPED:*;
		[Bindable]
		public static var DISABLED:*;
		[Bindable]
		public static var MOUNTED_RESERVED:*;
		[Bindable]
		public static var NOT_MOUNTED_RESERVED:*;
		[Bindable]
		public static var NOT_MOUNTED_NOT_RESERVED:*;
		
		
		private static var errors:Array;
		
		public function StateIcons(){
			
		} 
		public static function refresh():void{
				errors = new Array();
				
				RUNNING = getImage("MainStateRUNNING");			
				PAUSED = getImage("MainStatePAUSED");			
				APPLY_CHANGES_NEEDED = getImage("MainStateAPPLYCHANGESNEEDED");			
				POWERED_OFF = getImage("MainStatePOWEREDOFF");			
				NOT_DEPLOYED = getImage("MainStateNOTDEPLOYED");			
				STOPPED = getImage("MainStateSTOPPED");			
				DISABLED = getImage("MainStateDISABLED");			
				MOUNTED_RESERVED = getImage("MainStateMOUNTEDRESERVED");			
				NOT_MOUNTED_RESERVED = getImage("MainStateNOTMOUNTEDRESERVED");			
				NOT_MOUNTED_NOT_RESERVED = getImage("MainStateNOTMOUNTEDNOTRESERVED");
				
				if(errors.length > 0){
					trace(errors.toString());		
				}
		}
		
		private static function getImage(CSSSelector:String):*{
			try {
				return StyleManager.getStyleDeclaration("." + CSSSelector).getStyle("source")
			}catch(e:Error){
				trace("error getting image " +  CSSSelector);
				errors.push("[StateIcons] Image not found from declaration " + CSSSelector);
				return null;
			}		
		} 
		
	}
}