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

package net.undf.abicloud.business.managers.virtualimage
{
    import flash.events.Event;
    import flash.events.EventDispatcher;
    
    import mx.collections.ArrayCollection;
    import mx.resources.ResourceManager;
    
    import net.undf.abicloud.events.virtualimage.VirtualImageEvent;
    import net.undf.abicloud.vo.virtualimage.Category;
    import net.undf.abicloud.vo.virtualimage.Icon;
    import net.undf.abicloud.vo.virtualimage.Repository;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    /**
     * Manager for Virtual Images
     */
    public class VirtualImageManager extends EventDispatcher
    {

        public static const DEFAULT_ICON_IMAGE_PATH:String = "themes/base/VirtualImage_defaultIcon.jpg";
//        public static const DEFAULT_ICON_IMAGE_PATH:String = "assets/virtualimage/VirtualImage_defaultIcon.jpg";

        private var _virtualImageList:ArrayCollection;
        
        public function set virtualImageList(value:ArrayCollection):void{
        	this._virtualImageList = value;
        }
        
        public function get virtualImageList():ArrayCollection{
        	return this._virtualImageList;
        }
        
        /**
         * List of all DiskFormatType available
         */
        private var _diskFormatTypes:ArrayCollection = new ArrayCollection();

        [Bindable(event="diskFormatTypesChange")]
        public function get diskFormatTypes():ArrayCollection
        {
            return _diskFormatTypes;
        }

        public function set diskFormatTypes(value:ArrayCollection):void
        {
            _diskFormatTypes = value;
            dispatchEvent(new Event("diskFormatTypesChange"))
        }

        /**
         * The VirtualImageManager is bound to a given Repository
         */
        private var _repository:Repository;

        [Bindable(event="repositoryChange")]
        public function get repository():Repository
        {
            return _repository;
        }

        public function set repository(value:Repository):void
        {
            _repository = value;
            dispatchEvent(new Event("repositoryChange"));
        }

        /**
         * List of Virtual Images Categories for the current User's Enterprise
         */
        private var _categories:ArrayCollection = new ArrayCollection();

        [Bindable(event="categoriesChange")]
        public function get categories():ArrayCollection
        {
            return _categories;
        }

        public function set categories(value:ArrayCollection):void
        {
            _categories = value;
            updateWithAllCategories();
            dispatchEvent(new Event("categoriesChange"));
        }
        
        private function updateWithAllCategories():void{
        	if(_categories.getItemAt(0).name != ResourceManager.getInstance().getString("VirtualImage","LABEL_ALL_CATEGORIES")){
        		 //We add a new category to allow searching in all categories
                var category:Category = new Category();
                category.name = ResourceManager.getInstance().getString("VirtualImage","LABEL_ALL_CATEGORIES");
                category.isErasable = false;
                category.isDefault = true;
                _categories.addItemAt(category,0);
        	}
        }

        /**
         * List of Icons for the current User's Enterprise
         */
        private var _icons:ArrayCollection = new ArrayCollection();

        [Bindable(event="iconsChange")]
        public function get icons():ArrayCollection
        {
            return _icons;
        }

        public function set icons(value:ArrayCollection):void
        {
            _icons = value;
            dispatchEvent(new Event("iconsChange"));
        }

        /**
         * The OVFPackageManager contains all the logic related to OVFPackages
         */
        [Bindable]
        public var ovfPackageManager:OVFPackageManager = new OVFPackageManager();

        /* ------------- Constructor ------------- */
        public function VirtualImageManager()
        {

        }

        /**
         * Adds a new Category to the current list of categories
         */
        public function addCategory(category:Category):void
        {
            _categories.addItem(category);

            var event:VirtualImageEvent = new VirtualImageEvent(VirtualImageEvent.CATEGORY_CREATED);
            event.category = category;
            dispatchEvent(event);
        }

        /**
         * Removes a Category from the current list of categories
         */
        public function removeCategory(category:Category):void
        {
            var index:int = _categories.getItemIndex(category);
            if (index > -1)
            {
                _categories.removeItemAt(index);

                var event:VirtualImageEvent = new VirtualImageEvent(VirtualImageEvent.CATEGORY_DELETED);
                event.category = category;
                dispatchEvent(event);
            }
        }


        /**
         * Adds a new icon already created in server, to the icons list
         */
        public function createIcon(icon:Icon):void
        {
            _icons.addItem(icon);
            dispatchEvent(new Event("iconsChange"));
        }

        /**
         * Deletes an icon from the VirtualImageManager
         * @param icon
         *
         */
        public function deleteIcon(icon:Icon):void
        {
            var index:int = this._icons.getItemIndex(icon);
            if (index > -1)
            {
                _icons.removeItemAt(index);
                dispatchEvent(new Event("iconsChange"));
            }
        }

        /**
         * Edits an existing icon in the VirtualImageManager, with new values
         * @param oldIcon The icon in the virtualimagemanager that will be updated
         * @param newIcon An Icon object with the new values
         *
         */
        public function editIcon(newValues:Icon):void
        {
            var icon:Icon;
            var length:int = _icons.length;
            for (var i:int = 0; i < length; i++)
            {
                icon = _icons.getItemAt(i) as Icon;
                if (icon.id == newValues.id)
                {
                    break;
                }
            }

            if (icon)
            {
                //Updating the old icon without modifying its memory address
                icon.id = newValues.id;
                icon.name = newValues.name;
                icon.path = newValues.path;
            }
        }

        /**
         * Updates a VirtualImage with the information that has been edited, but without modifying its memory
         * address.
         */
        public function editVirtualImage(virtualImage:VirtualImage, updatedValues:VirtualImage):void
        {
            //Update values
            virtualImage.id = updatedValues.id;
            virtualImage.diskFormatType = updatedValues.diskFormatType;
            virtualImage.category = updatedValues.category;
            virtualImage.cpuRequired = updatedValues.cpuRequired;
            virtualImage.deleted = updatedValues.deleted;
            virtualImage.description = updatedValues.description;
            virtualImage.hdRequired = updatedValues.hdRequired;
            virtualImage.icon = updatedValues.icon;
            virtualImage.name = updatedValues.name;
            virtualImage.path = updatedValues.path;
            virtualImage.ramRequired = updatedValues.ramRequired;
            virtualImage.repository = updatedValues.repository;
            virtualImage.idEnterprise = updatedValues.idEnterprise;
            virtualImage.ovfId = updatedValues.ovfId;
            virtualImage.stateful = updatedValues.stateful;
            virtualImage.diskFileSize = updatedValues.diskFileSize;

            //Announcing that this virtual image has been updated
            var virtualImageEvent:VirtualImageEvent = new VirtualImageEvent(VirtualImageEvent.VIRTUAL_IMAGE_EDITED,
                                                                            false);
            virtualImageEvent.virtualImage = virtualImage;
            dispatchEvent(virtualImageEvent);
        }

        /**
         * Deletes a Virtual Image from the virtual images list, that has been already deleted
         * in server
         */
        public function deleteVirtualImage(virtualImage:VirtualImage):void
        {
            var virtualImageEvent:VirtualImageEvent = new VirtualImageEvent(VirtualImageEvent.VIRTUAL_IMAGE_DELETED);
            virtualImageEvent.virtualImage = virtualImage;
            dispatchEvent(virtualImageEvent);
        }

    }


}