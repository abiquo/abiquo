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
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	
	import net.undf.abicloud.events.ThemeEvent;
	import net.undf.abicloud.model.AbiCloudModel;
	
	public class ThemeDescriptor extends EventDispatcher
	{
		public var themeElements:Array;
		public var loaded:Boolean = false;
		public var themeName:String;
		public var themePath:String;
		private var loadingQueue:Array;
		
		private var tmpCount:Number=0;
		
		public function ThemeDescriptor(){
		}
		
		public function setAsLoaded(themeElementFilename:String):void{
			themeElements[themeElementFilename].isLoaded = ThemeElement.LOADED;
			checkStatus();
		}
		
		public function checkStatus():Boolean{
			
			var allLoaded:Boolean = false;
			
			for(var it:String in themeElements){
				
				if(themeElements[it].isLoaded == ThemeElement.NOT_LOADED || themeElements[it].isLoaded == ThemeElement.FAILED) {
					allLoaded = false;
					break;
				}else{
					allLoaded = true;
				}
			}
			return allLoaded;			
		}

		public function loadTheme():void{
			loadThemeDescriptor();
			
		}
		
		private function loadThemeDescriptor():void{

            var loader:URLLoader = new URLLoader();

            loader.addEventListener(Event.COMPLETE, loadThemeDescriptorCompleteHandler);
            loader.addEventListener(IOErrorEvent.IO_ERROR, loadThemeDescriptorIOErrorHandler);
            loader.load(new URLRequest(themePath + "/theme.lst"));

        }
        
        private function loadThemeDescriptorCompleteHandler(evt:Event):void{
			parseContent(evt.target.data);
			loadNextElement();
        }
        
        private function parseContent(_content:String):void{
        	var lines:Array = new Array();
        	lines = _content.split("\n");
        	
			var tmpProps:Array = new Array();
			    	
			if(themeElements == null) themeElements = new Array();
			
        	for(var it:* in lines){
				var tmp:ThemeElement = new ThemeElement();

				tmp.addEventListener(ThemeEvent.THEME_ELEMENT_LOADED, onThemeElementLoaded);
				tmp.theme = this;
				lines[it] = lines[it].replace("\r", "");
				lines[it] = lines[it].replace("\r\n", "");
				lines[it] = lines[it].replace("\n", "");
				
				tmp.file = themePath + "/" + lines[it] + ".swf";
				tmp.name = lines[it];
				
				if(lines[it] != ""){
					if(loadingQueue == null) loadingQueue = new Array();
					loadingQueue.push(tmp);
				}
        	}
        }        
        
        private function loadThemeDescriptorIOErrorHandler(evt:IOErrorEvent):void{
        	trace("[ThemeDescriptor] Error Loading Theme Descriptor: " + evt.text);
            var loader:URLLoader = new URLLoader();

            loader.addEventListener(Event.COMPLETE, loadThemeDescriptorCompleteHandler);
            loader.addEventListener(IOErrorEvent.IO_ERROR, loadThemeDescriptorIOErrorHandler);
            
            themeName = AbiCloudModel.getInstance().configurationManager.config.defaultTheme;
            themePath = "themes/"  + themeName;

            loader.load(new URLRequest("themes/" + AbiCloudModel.getInstance().configurationManager.config.defaultTheme + "/theme.lst"));

        }		
		
		private function onThemeElementLoaded(evt:ThemeEvent):void{
			if(loadingQueue.length == 0){
				loaded = true;

				var tmp:ThemeEvent= new ThemeEvent(ThemeEvent.THEME_LOADED);
				tmp.theme = this;

				dispatchEvent(tmp);
			}else{
				loadNextElement();
			}
		}
		
		private function loadNextElement():void{
			tmpCount++;

			var current:ThemeElement = loadingQueue.shift();

			if(current.isLoaded == ThemeElement.NOT_LOADED){
				current.loadStyle();
				themeElements[current.name] = current;
			}
		}
	}
}