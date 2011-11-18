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

package net.undf.abicloud.view.virtualimage.components.virtualimage
{
    import mx.collections.ArrayCollection;
    import mx.collections.Sort;
    import mx.containers.Tile;
    import mx.core.Application;
    import mx.events.FlexEvent;

    import net.undf.abicloud.events.virtualimage.VirtualImageEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.infrastructure.HyperVisorType;
    import net.undf.abicloud.vo.virtualimage.Category;
    import net.undf.abicloud.vo.virtualimage.Repository;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    public class VirtualImageAccordionCategory extends Tile
    {
        /**
         * ArrayCollection containing Virtual Images
         * Only VirtualImages assigned to the Category
         * that this VirtualImageAccordionCategory represents, will be considereda VirtualImageMiniature will be build
         */
        private var virtualImages:ArrayCollection;

        /**
         * VirtualImagesMiniatures drawn inside this VirtualImageAccordionCategory
         * All VirtualImageMiniatures will contain VirtualImages assigned to the Category
         * that this VirtualImageAccordionCategory represents
         */
        private var virtualImageMiniatures:ArrayCollection;

        /**
         * The parent VirtualImageAccordion for this VirtualImageAccordionCategory
         */
        private var virtualImageAccordion:VirtualImageAccordion;

        /**
         * The Category that this component represents
         */
        private var _category:Category;

        public function get category():Category
        {
            return _category;
        }

        /**
         * Multiplier used to increase or reduce the size of
         * the VirtualImageMiniatures drawn inside
         * this VirtualImageAccordion
         */
        private var sizeMultiplier:Number = 1;

        /**
         * This flag indicates
         * - That a VirtualImageMiniature can show action buttons such as Configure, Download,...
         * - That VirtualImageMiniatures are draggable between Categories
         * - That a VirtualImageAccordionHeader accepts drops
         */
        private var editable:Boolean = false;

        /**
         * The Repository where the Category belongs
         */
        private var repository:Repository;

        /**
         * When not null, the VirtualImage will be retrieved filtered by its HyperVisor Type
         */
        private var hypervisorType:HyperVisorType;

        /**
         * Flag that indicates that the VirtualImage renderers must be built
         */
        private var needBuildMiniatures:Boolean = false;

        public function VirtualImageAccordionCategory(virtualImageAccordion:VirtualImageAccordion,
                                                      repository:Repository, category:Category,
                                                      editable:Boolean, sizeMultiplier:Number,
                                                      hypervisorType:HyperVisorType = null)
        {
            super();

            //Setting attributes
            this.virtualImageAccordion = virtualImageAccordion;
            this.repository = repository;
            _category = category;
            this.label = _category.name
            this.editable = editable;
            this.sizeMultiplier = sizeMultiplier;
            this.hypervisorType = hypervisorType;

            //Setting basic layout and style
            this.percentWidth = 100;
            this.percentHeight = 100;
            this.setStyle("backgroundColor", 0xFFFFFF);
            this.setStyle("verticalGap", 3);
            this.setStyle("horizontalGap", 3);

            //Registering events
            registerParentEvents();
            addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplete);
            addEventListener(FlexEvent.SHOW, onShow);

            //Request VirtualImages
            requestVirtualImages();
        }

        private function onCreationComplete(flexEvent:FlexEvent):void
        {
            if (needBuildMiniatures)
            {
                //Create VirtualImageMiniatures and show them
                buildVirtualImageMiniatures();
                drawVirtualImageMiniatures();
            }
        }

        private function onShow(flexEvent:FlexEvent):void
        {
            if (needBuildMiniatures)
            {
                buildVirtualImageMiniatures();
                drawVirtualImageMiniatures();
            }
        }

        private function requestVirtualImages():void
        {
            if (!virtualImages && _category)
            {
                var event:VirtualImageEvent;
                if (hypervisorType)
                {
                    //Request the VirtualImages for this category and Hypervisor type
                    event = new VirtualImageEvent(VirtualImageEvent.GET_VIRTUAL_IMAGES_BY_CATEGORY_AND_HYPERVISOR_TYPE);
                    event.hypervisorType = hypervisorType;
                }
                else
                {
                    //Request the VirtualImages for this category
                    event = new VirtualImageEvent(VirtualImageEvent.GET_VIRTUAL_IMAGES_BY_CATEGORY);
                }

                event.enterprise = AbiCloudModel.getInstance().loginManager.user.enterprise;
                event.datacenter = repository.datacenter;
                event.repository = repository;
                event.category = category;
                event.callback = setVirtualImages;

                Application.application.dispatchEvent(event);
            }
        }

        /**
         * Registers events dispatched by the VirtualImageAccordion parent, and sets bindings in some properties
         */
        private function registerParentEvents():void
        {
            virtualImageAccordion.addEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_ACCORDION_CATEGORY_DISPOSE,
                                                   onDisposeRequested);
            virtualImageAccordion.addEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_EDITION_COMPLETE,
                                                   onVirtualImageEditionComplete);
            virtualImageAccordion.addEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_MASTER_SELECTED,
                                                   onVirtualImageMasterSelectedUnselected);
            virtualImageAccordion.addEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_MASTER_UNSELECTED,
                                                   onVirtualImageMasterSelectedUnselected);
        }

        /**
         * Unregisters registered events and stops bindings
         */
        private function unregisterParentEvents():void
        {
            virtualImageAccordion.removeEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_ACCORDION_CATEGORY_DISPOSE,
                                                      onDisposeRequested);
            virtualImageAccordion.removeEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_EDITION_COMPLETE,
                                                      onVirtualImageEditionComplete);
            virtualImageAccordion.removeEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_MASTER_SELECTED,
                                                      onVirtualImageMasterSelectedUnselected);
            virtualImageAccordion.removeEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_MASTER_UNSELECTED,
                                                      onVirtualImageMasterSelectedUnselected);
        }

        /**
         * Handler called when our VirtualImageAccordion parent request us to dispose
         */
        private function onDisposeRequested(event:VirtualImageAccordionEvent):void
        {
            unregisterParentEvents();

            //Clean internal variables
            if (virtualImageMiniatures)
            {
                cleanVirtualImageMiniatures();
            }
            virtualImageMiniatures = null;

            _category = null;
            virtualImages = null;

            //We have to remove from our parent
            if (virtualImageAccordion.contains(this))
            {
                virtualImageAccordion.removeChild(this);
            }

            virtualImageAccordion = null;
        }

        private function setVirtualImages(value:ArrayCollection):void
        {
            virtualImages = value;
            this.label = _category.name + '  (' + virtualImages.length + ')';
            if (visible)
            {
                buildVirtualImageMiniatures();
                drawVirtualImageMiniatures();
            }
            else
            {
                needBuildMiniatures = true;
            }
        }

        /**
         * Builds VirtualImageMiniatures from the _virtualImages Array, and
         * this VirtualImageAccordionCategory has a Category assigned
         */
        private function buildVirtualImageMiniatures():void
        {
            needBuildMiniatures = false;

            if (virtualImageMiniatures)
                cleanVirtualImageMiniatures();
            else
                virtualImageMiniatures = new ArrayCollection();

            var length:int = virtualImages.length;
            var i:int;
            var virtualImage:VirtualImage;
            var virtualImageMiniature:VirtualImageMiniature;
            for (i = 0; i < length; i++)
            {
                virtualImage = virtualImages.getItemAt(i) as VirtualImage;
                if (virtualImage.category.id == this._category.id)
                {
                    virtualImageMiniature = new VirtualImageMiniature();
                    virtualImageMiniature.virtualImage = virtualImage;
                    virtualImageMiniature.sizeMultiplier = sizeMultiplier;
                    virtualImageMiniature.editable = editable;
                    virtualImageMiniatures.addItem(virtualImageMiniature);
                }
            }

            //Set the title
            this.label = _category.name + '  (' + virtualImages.length + ')';
        }

        /**
         * Draws the VirtualImageMiniature components present in the _virtualImageMiniatures Array
         */
        private function drawVirtualImageMiniatures():void
        {
            removeAllChildren();

            //Sorting the miniatures by name before drawing them
            var sort:Sort = new Sort();
            sort.compareFunction = orderByName;
            var array:Array = virtualImageMiniatures.toArray();
            sort.sort(array);
            virtualImageMiniatures = new ArrayCollection(array);

            var length:int = virtualImageMiniatures.length;
            var i:int;
            for (i = 0; i < length; i++)
            {
                this.addChild(virtualImageMiniatures.getItemAt(i) as VirtualImageMiniature);
            }
        }

        /**
         * Sort function to order VirtualImageMiniatures by name
         */
        private function orderByName(a:Object, b:Object, fields:Array = null):int
        {
            var virtualImageA:VirtualImage = VirtualImageMiniature(a).virtualImage;
            var virtualImageB:VirtualImage = VirtualImageMiniature(b).virtualImage;
            var compareValue:int = virtualImageA.name.toLowerCase().localeCompare(virtualImageB.name.toLowerCase());

            if (compareValue < 0)
                return -1;
            else if (compareValue > 0)
                return 1;
            else
                return 0;
        }

        /**
         * Cleans the current VirtualImageMiniatures (both from screen and from the _virtualImageMiniatures array
         */
        private function cleanVirtualImageMiniatures():void
        {
            var length:int = virtualImageMiniatures.length;
            var i:int;
            var virtualImageMiniature:VirtualImageMiniature;
            for (i = 0; i < length; i++)
            {
                virtualImageMiniature = virtualImageMiniatures.getItemAt(i) as VirtualImageMiniature;

                if (this.contains(virtualImageMiniature))
                {
                    this.removeChild(virtualImageMiniature);

                }
            }

            virtualImageMiniatures.removeAll();
        }



        /**
         * Handler called when a Virtual Image has been edited
         */
        private function onVirtualImageEditionComplete(event:VirtualImageAccordionEvent):void
        {
            var virtualImage:VirtualImage = event.virtualImage;

            if (virtualImage.category.id == _category.id && !containsVirtualImage(virtualImage))
            {
                //Add the new Virtual Image
                virtualImages.addItem(virtualImage);

                if (visible)
                {
                    //Build the miniature immediately
                    var virtualImageMiniature:VirtualImageMiniature = new VirtualImageMiniature();
                    virtualImageMiniature.sizeMultiplier = sizeMultiplier;
                    virtualImageMiniature.virtualImage = virtualImage;
                    virtualImageMiniature.editable = editable;

                    virtualImageMiniatures.addItem(virtualImageMiniature);
                    drawVirtualImageMiniatures();
                }
                else
                {
                    //Build the miniature when needed
                    needBuildMiniatures = true;
                }
            }
            else if (virtualImage.category.id != _category.id && containsVirtualImage(virtualImage))
            {
                //Remove the Virtual Image
                var length:int = virtualImages.length;
                for (var i:int = 0; i < length; i++)
                {
                    if (virtualImage.id == VirtualImage(virtualImages.getItemAt(i)).id)
                    {
                        virtualImages.removeItemAt(i);
                        break;
                    }
                }

                if (visible)
                {
                    //Remove the miniature immediately
                    virtualImageMiniatures.removeItemAt(virtualImageMiniatures.getItemIndex(getVirtualImageMiniature(virtualImage)));
                    drawVirtualImageMiniatures();
                }
                else
                {
                    //Rebuild the miniatures when needed
                    needBuildMiniatures = true;
                }
            }


            this.label = _category.name + '  (' + virtualImages.length + ')';
        }

        /**
         * Returns true if this VirtualImageAccordion contains the virtualImage
         **/
        public function containsVirtualImage(virtualImage:VirtualImage):Boolean
        {
            var length:int = virtualImages.length;

            for (var i:int = 0; i < length; i++)
            {
                if (virtualImage.id == VirtualImage(virtualImages.getItemAt(i)).id)
                    return true;
            }

            return false;
        }

        /**
         * Returns the VirtualImageMiniature that contains the virtualImage, or null if there is
         * no VirtualImageMiniature
         */
        private function getVirtualImageMiniature(virtualImage:VirtualImage):VirtualImageMiniature
        {
            var length:int = virtualImageMiniatures.length;
            var i:int;
            var virtualImageMiniature:VirtualImageMiniature;

            for (i = 0; i < length; i++)
            {
                virtualImageMiniature = virtualImageMiniatures.getItemAt(i) as VirtualImageMiniature;
                if (virtualImage.id == virtualImageMiniature.virtualImage.id)
                    return virtualImageMiniature;
            }

            return null;
        }

        private function onVirtualImageMasterSelectedUnselected(event:VirtualImageAccordionEvent):void
        {
            //Notify our VirtualImageMiniatures that a Master virtual image has been selected or unselected
            //They must check if they are a slave from the 	Master, and if so, give visual feedback
            if (virtualImageMiniatures)
            {
                var length:int = virtualImageMiniatures.length;
                var i:int;
                for (i = 0; i < length; i++)
                {
                    VirtualImageMiniature(virtualImageMiniatures.getItemAt(i)).masterHasBeenSelectedOrUnselected(event.virtualImage);
                }
            }
        }
    }
}