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
    import flash.events.Event;
    import flash.events.IOErrorEvent;
    
    import mx.controls.Image;
    
    import net.undf.abicloud.business.managers.virtualimage.VirtualImageManager;

    /**
     * This class extends from Image
     * If it is unable to load the image set in source argument, or with method load(), it will
     * load the image found in defaultImagePath property
     *
     * @author Oliver
     *
     */

    public class ImageDefault extends Image
    {
        /**
         *@private
         */
        private var _defaultImagePath:String = VirtualImageManager.DEFAULT_ICON_IMAGE_PATH;

        [Bindable(event="defaultImagePathChanged")]

        /**
         * Path to the image that will be loaded when a load attempt fails
         * @default null
         */

        public function get defaultImagePath():String
        {
            return this._defaultImagePath;
        }

        public function set defaultImagePath(value:String):void
        {
            this._defaultImagePath = value;

            dispatchEvent(new Event("defaultImagePathChanged"));
        }

        private var _loadSuccess:Boolean = true;


        [Bindable(event="loadSuccessChanged")]

        /**
         * Boolean indicating if the last attempt of load an image had success or not
         * @default true
         */
        public function get loadSuccess():Boolean
        {
            return this._loadSuccess;
        }
        
        public function set loadSuccess(value:Boolean):void
        {
            this._loadSuccess = value;
        }

        /**
         * Constructor
         * Uses default Image class constructor, and adds listeners to be able to check when the load attempt
         * is successful
         */
        public function ImageDefault()
        {
            super();

            source = this._defaultImagePath;
            addEventListener(IOErrorEvent.IO_ERROR, loadImageError_handler);
            addEventListener(Event.COMPLETE, loadImageComplete_handler);
        }

        /**
         * Load error handler
         * @param ioErrorEvent
         * @private
         */
        private function loadImageError_handler(ioErrorEvent:IOErrorEvent):void
        {
            this._loadSuccess = false;
            this.source = this._defaultImagePath;

            dispatchEvent(new Event("loadSuccessChanged"));
        }

        /**
         * Load success handler
         * @param event
         * @private
         */
        private function loadImageComplete_handler(event:Event):void
        {
            if(this.source != this._defaultImagePath){
	            this._loadSuccess = true;
            }

            dispatchEvent(new Event("loadSuccessChanged"));
        }

        override public function set source(value:Object):void
        {
            if (value == null || value == "")
                super.source = this._defaultImagePath;
            else
                super.source = value;
        }
    }
}