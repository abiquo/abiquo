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

package net.undf.abicloud.controller
{
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.core.Application;
	import mx.events.StyleEvent;
	import mx.styles.CSSStyleDeclaration;
	import mx.styles.StyleManager;
	
	import net.undf.abicloud.controller.theme.ThemeDescriptor;
	import net.undf.abicloud.events.ThemeEvent;
	import net.undf.abicloud.vo.theme.ThemeImage;
	
	/**
	 * Handles the theme loading and image setting 
	 * 
	 * @author GaB
	 */
	public class ThemeHandler extends EventDispatcher
	{
		
		private static var _instance:ThemeHandler;
		
		[Bindable]
		public var images:Object;
		
		public var currentSystemFont:String;
		
		public var loadedThemes:Array;

		public function ThemeHandler(caller:Function = null) { 
			if ( caller != getInstance ) { 
				throw new Error("[ThemeHandler] you can't instanciate this class");
			} 
		}  

		public static function getInstance() : ThemeHandler{
			if(_instance == null){
				_instance = new ThemeHandler(arguments.callee);
			}
			return _instance;
		}


		/**
		 * Loads a ThemeDescriptor  
		 * @param theme the definition of the theme
		 * @param themePath the path to load the theme from 
		 * 
		 */
		public function loadTheme(theme:ThemeDescriptor):void{
			
			if(theme.themeName != "base"){
				Application.application.cursorManager.setBusyCursor();
			}

			if(loadedThemes == null) loadedThemes = new Array();
			
			loadedThemes[theme.themeName] = false;
			
			theme.addEventListener(ThemeEvent.THEME_LOADED, onThemeLoaded);
			theme.loadTheme();
		}

		
		/**
		 * when the theme is loaded dispatch the event
		 * and refresh the defined images 
		 * @param evt
		 */
		private function onThemeLoaded(evt:ThemeEvent):void{
			
			if(loadedThemes == null) loadedThemes = new Array();
			
//			if(evt.theme.themeName != "base"){
//				refreshImages();
//			}

			loadedThemes[evt.theme.themeName] = true;

			var tmp:ThemeEvent = new ThemeEvent(ThemeEvent.THEME_LOADED);
			tmp.theme = evt.theme;

			dispatchEvent( tmp );
		}

		/**
		 * Load a font replacing the default  
		 * @param fontFamily
		 */
		public function changeSystemFont(fontFamily:String):void{
			trace("[ThemeHandler] changeSystemFont " + "themes/fonts/" + fontFamily + ".swf");
//			var myEvent:IEventDispatcher = StyleManager.loadStyleDeclarations("themes/fonts/" + fontFamily + ".swf" );
//			currentSystemFont = fontFamily;

//			myEvent.addEventListener(StyleEvent.COMPLETE, onFontLoadComplete);
//			myEvent.addEventListener(StyleEvent.ERROR, onFontLoadError);
//			setTimeout(this.completeFontLoading,0);
			this.completeFontLoading();

		}

		public function onFontLoadComplete(evt:StyleEvent):void{
			setTimeout(this.completeFontLoading,500);
		}

		public function completeFontLoading():void{
			dispatchEvent(new Event("onFontLoaded") );
		}
		
		public function onFontLoadError(evt:StyleEvent):void{
			trace("[ThemeHandler] Font loading error " + currentSystemFont + " " + evt.errorText);
			dispatchEvent(new Event("onFontLoadedError") );
		}

		
		/**
		 * retrieves the style for the specified selector. 
		 * If the selector is already loaded it will be refreshed in refreshImage
		 * if the target is not specified the function will try to return the style declaration.
		 * If the style declaration is not loaded it will raise an error
		 * _target must have a <code>source</code> property so it can be set
		 * 
		 * @param CSSSelector
		 * @param c
		 * @return 
		 * 
		 */
		public function getImageFromStyle(CSSSelector:String, _target:* = null):*{

			if(images == null) images = new Object();
			
			images[CSSSelector] = new ThemeImage();
			images[CSSSelector].target = _target;
			
			refreshImage(CSSSelector);
			
			try{	
				if(_target == null) return StyleManager.getStyleDeclaration("." + CSSSelector).getStyle("source");
			}catch(e:Error){
				trace(" [ThemeHandler] Error loading Declaration for " + CSSSelector); 
			}
		}
		
		/**
		 * gets the style declaration for imageSelector and assigns it to the 
		 * pending image control
		 *  
		 * @param imageSelector
		 * 
		 */
		private function refreshImage(imageSelector:String):void{
			var styleDeclaration:CSSStyleDeclaration = StyleManager.getStyleDeclaration("." + imageSelector);

			if(styleDeclaration != null){
				var tmp:Object= styleDeclaration.getStyle("source");
				if(images[imageSelector].target != null){
					try{
						images[imageSelector].target.source = tmp;
					}catch(e:Error){
						trace("[ThemeHandler] Error setting source for " + images[imageSelector].target); 
					}
				}
			}else{
				trace("[ThemeHandler] Style declaration not found: " + imageSelector);
			}
		}
		
		
		/**
		 * refreshes all the registered image controls 
		 */
		public function refreshImages():void{
			var errors:Array = new Array();
			for(var it:String in images){
				try{
					refreshImage(it);
				}catch(e:*){
					errors.push("[ThemeHandler] Problem loading image for selector : " + images[it].def + " to " + images[it].target);
				}
			}
			
			if(errors.length > 0){
				trace("[ThemeHandler] error loading images " + errors.join("\r").toString() );
			}
		}
		
		public function isThemeLoaded(themeName:String):Boolean{
		
			for(var it:String in loadedThemes){
				
				if(loadedThemes[themeName] == true){
					
					return true;
					
				}
			}
			
			return false;
		}		
	}
}

