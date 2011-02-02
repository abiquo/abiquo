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

package net.undf.abicloud.utils
{
    import flash.display.Loader;
    import flash.display.Shape;
    import flash.display.Sprite;
    import flash.events.Event;
    import flash.events.IOErrorEvent;
    import flash.events.ProgressEvent;
    import flash.net.URLLoader;
    import flash.net.URLRequest;
    
    import mx.controls.Alert;
    import mx.events.FlexEvent;
    import mx.preloaders.DownloadProgressBar;
    import mx.preloaders.IPreloaderDisplay;

    public class CustomPreloader extends DownloadProgressBar implements IPreloaderDisplay
    {
        private var loadBar:Shape;
        
        private var colorLoader:URLLoader;

        private var dpbImageControl:Loader;

        private var _barTotalWidth:Number = 187;

        private var _barLoaded:Number = 0;

        private var _barHeight:Number = 4;
        
        private var _progressBarColor:uint= 0xFFD200;

        public function CustomPreloader()
        {
            super();
        }

        protected function draw():void
        {

        }

        override public function set preloader(preloader:Sprite):void
        {
            // Listening for relevant events
            preloader.addEventListener(ProgressEvent.PROGRESS, handleProgress);
            preloader.addEventListener(Event.COMPLETE, handleComplete);
            preloader.addEventListener(FlexEvent.INIT_PROGRESS, handleInitProgress);
            preloader.addEventListener(FlexEvent.INIT_COMPLETE, handleInitComplete);
        }

        //Initialize Loader control 
        override public function initialize():void
        {
        	//load color 
        	colorLoader = new URLLoader();
            colorLoader.addEventListener(Event.COMPLETE, color_loader_completeHandler);
            colorLoader.addEventListener(IOErrorEvent.IO_ERROR, color_loader_ioErrorHandler);
			colorLoader.load(new URLRequest("themes/base/color_loader.txt"));        	
        	
            loadBar = new Shape();
            dpbImageControl = new Loader();

            dpbImageControl.contentLoaderInfo.addEventListener(Event.COMPLETE, loader_completeHandler);
            dpbImageControl.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, loader_ioErrorHandler );
            
            //TODO: change to match the preloader image said by the userauthorization
            dpbImageControl.load(new URLRequest("themes/base/CustomPreloaderLogo.png"));
        }
        
        private function color_loader_ioErrorHandler(event:IOErrorEvent):void{
        	//do nothing, use the default color
        }

        // Once the SWF is loaded
        private function color_loader_completeHandler(event:Event):void
        {
            _progressBarColor = uint(URLLoader(event.currentTarget).data);
        }
        
        private function loader_ioErrorHandler(event:IOErrorEvent):void{
        	Alert.show("[CustomPreloader] Error loading Image");
        }

        // Once the SWF is loaded
        private function loader_completeHandler(event:Event):void
        {
            var stageWidth:int;
            var stageHeight:int;
            //to avoid a displayed bug
            if(!this.stage){
            	stageWidth = 1024;
            	stageHeight = 768;
            }else{
            	stageWidth = this.stage.stageWidth;
            	stageHeight = this.stage.stageHeight;
            }
            
            addChild(dpbImageControl);
            dpbImageControl.x = stageWidth / 2 - (dpbImageControl.width / 2);
            dpbImageControl.y = stageHeight / 2 - (dpbImageControl.height);

            addChild(loadBar);
            loadBar.scaleX = 3;
            loadBar.x = stageWidth / 2 - (_barTotalWidth * 3) / 2;
            loadBar.y = stageHeight / 2;
        }

        // On bar progress
        private function handleProgress(event:ProgressEvent):void
        {
            var relacion:Number = event.bytesLoaded / event.bytesTotal;

            _barLoaded = relacion * _barTotalWidth;
            loadBar.graphics.clear();
            loadBar.graphics.beginFill(_progressBarColor);
            loadBar.graphics.drawRoundRect(0, 0, _barLoaded, _barHeight, 4, 20);
            loadBar.graphics.endFill();
        }

        private function handleComplete(event:Event):void
        {
        }

        private function handleInitProgress(event:Event):void
        {
        }

        private function handleInitComplete(event:Event):void
        {
            dispatchEvent(new Event(Event.COMPLETE));
        }
    }
}