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

package net.undf.abicloud.controller.theme
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	import mx.events.StyleEvent;
	import mx.styles.StyleManager;
	
	import net.undf.abicloud.events.ThemeEvent;

	public class ThemeElement extends EventDispatcher
	{
		
		public static var LOADED:Number=1;
		public static var NOT_LOADED:Number=2;
		public static var FAILED:Number=3;
		
		
		public var currentDeclaration:String;
		public var theme:ThemeDescriptor;
		public var file:String;
		public var name:String;
		
		public var isLoaded:Number = NOT_LOADED;
		
		public function ThemeElement(target:IEventDispatcher=null)
		{
			super(target);
		}
		
		public function loadStyle():void{
			trace("[ThemeElement] trying to load " + file + "-->" + name + "::"  + isLoaded);

			if(isLoaded == LOADED){
				trace("  [ThemeElement] already loaded " + name);
				return;
			}else if(isLoaded == FAILED){
				trace("  [ThemeElement] already FAILED " + name);
				return;
			}
			
			var myEvent:IEventDispatcher = StyleManager.loadStyleDeclarations( file );
			
			myEvent.addEventListener(StyleEvent.COMPLETE, onStyleLoadComplete);
			myEvent.addEventListener(StyleEvent.ERROR, onStyleLoadError);
		}

		public function onStyleLoadComplete(evt:StyleEvent):void{
			trace("     [ThemeElement] load complete " + name);
			var tmp:ThemeEvent = new ThemeEvent(ThemeEvent.THEME_ELEMENT_LOADED);
			tmp.themeElement = this;
			tmp.theme = theme;
			
			isLoaded = LOADED;
			dispatchEvent(tmp);
		}

		public function onStyleLoadError(evt:StyleEvent):void{
			var tmp:ThemeEvent = new ThemeEvent(ThemeEvent.THEME_ELEMENT_LOADED);
			tmp.themeElement = this;
			tmp.theme = theme;

			isLoaded = FAILED;

			dispatchEvent(tmp);
			trace("     [ThemeElement] load error " + name + "--"  + evt.errorText);
		}
		
	}
}