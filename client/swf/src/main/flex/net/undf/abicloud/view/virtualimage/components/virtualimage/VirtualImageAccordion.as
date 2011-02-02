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
    import flash.events.Event;

    import mx.collections.ArrayCollection;
    import mx.containers.Accordion;
    import mx.core.ClassFactory;

    import net.undf.abicloud.vo.infrastructure.HyperVisorType;
    import net.undf.abicloud.vo.virtualimage.Category;
    import net.undf.abicloud.vo.virtualimage.Repository;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    public class VirtualImageAccordion extends Accordion
    {
        /**
         * ArrayCollection with the Categories this component is showing
         *
         */
        private var _categories:ArrayCollection = new ArrayCollection();

        [Bindable(event="categoriesChange")]
        public function get categories():ArrayCollection
        {
            return this._categories;
        }

        public function set categories(value:ArrayCollection):void
        {
            cleanCategoryRenderers();

            this._categories = value;
            dispatchEvent(new Event("categoriesChange"));

            buildCategoryRenderers();
        }

        /**
         * The Repository where the Categories and VirtualImages belongs
         */
        private var _repository:Repository;

        public function set repository(value:Repository):void
        {
            _repository = value;
        }

        /**
         * Multiplier used to increase or reduce the size of
         * the VirtualImageMiniatures drawn inside
         * this VirtualImageAccordion
         */
        private var _sizeMultiplier:Number = 1;

        [Bindable(event="sizeMultiplierChange")]
        public function get sizeMultiplier():Number
        {
            return this._sizeMultiplier;
        }

        public function set sizeMultiplier(value:Number):void
        {
            this._sizeMultiplier = value;
            dispatchEvent(new Event("sizeMultiplierChange"));
        }

        /**
         * This flag indicates
         * - That a VirtualImageMiniature can show action buttons such as Configure, Download,...
         * - That VirtualImageMiniatures are draggable between Categories
         * - That a VirtualImageAccordionHeader accepts drops
         */
        private var _editable:Boolean = false;

        [Bindable(event="editableChange")]
        public function get editable():Boolean
        {
            return this._editable;
        }

        public function set editable(value:Boolean):void
        {
            this._editable = value;
            dispatchEvent(new Event("editableChange"));
        }


        /**
         * When not null, the VirtualImages will be retrieved filtered by its HypervisorType
         */
        private var _hypervisorType:HyperVisorType;

        public function get hypervisorType():HyperVisorType
        {
            return _hypervisorType;
        }

        public function set hypervisorType(value:HyperVisorType):void
        {
            _hypervisorType = value;
        }


        public function VirtualImageAccordion()
        {
            super();

            //Setting basic layout
            this.percentWidth = 100;
            this.percentHeight = 100;

            //Setting the HeaderRenderer class
            var classFactory:ClassFactory = new ClassFactory(VirtualImageAccordionHeader);
            this.headerRenderer = classFactory;

            //Registering events
            addEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_MASTER_SELECTED,
                             onVirtualImageMasterSelectedUnselected, true);
            addEventListener(VirtualImageAccordionEvent.VIRTUAL_IMAGE_MASTER_UNSELECTED,
                             onVirtualImageMasterSelectedUnselected, true);
        }

        public function clean():void
        {
            cleanCategoryRenderers();
        }

        public function virtualImageEdited(virtualImage:VirtualImage):void
        {
            var event:VirtualImageAccordionEvent = new VirtualImageAccordionEvent(VirtualImageAccordionEvent.VIRTUAL_IMAGE_EDITION_COMPLETE);
            event.virtualImage = virtualImage;
            dispatchEvent(event);
        }


        private function buildCategoryRenderers():void
        {
            //For each category, we draw a category renderer
            if (this._categories)
            {
                var length:int = this._categories.length;
                var i:int;
                var accordionCategory:VirtualImageAccordionCategory;
                for (i = 0; i < length; i++)
                {
                    accordionCategory = new VirtualImageAccordionCategory(this, _repository,
                                                                          _categories.getItemAt(i) as Category,
                                                                          _editable,
                                                                          _sizeMultiplier,
                                                                          _hypervisorType);

                    addChild(accordionCategory);
                    VirtualImageAccordionHeader(getHeaderAt(i)).editable = _editable;
                }
            }
        }

        private function cleanCategoryRenderers():void
        {
            //First check if we are already drawing Categories
            if (this._categories && this._categories.length > 0)
            {
                //We need to notify our CategoryRenderers to dispose, since they will not be needed anymore. When disposed
                //they will be removed from the VirtualImageAccordion
                var event:VirtualImageAccordionEvent = new VirtualImageAccordionEvent(VirtualImageAccordionEvent.VIRTUAL_IMAGE_ACCORDION_CATEGORY_DISPOSE);
                dispatchEvent(event);

                //Cleaning the categories array
                this._categories = null;
                dispatchEvent(new Event("categoriesChange"));
            }
        }

        public function addCategory(category:Category):void
        {
            if (_categories == null)
            {
                _categories = new ArrayCollection();
                dispatchEvent(new Event("categoriesChange"));
            }

            var accordionCategory:VirtualImageAccordionCategory = new VirtualImageAccordionCategory(this,
                                                                                                    _repository,
                                                                                                    category,
                                                                                                    _editable,
                                                                                                    _sizeMultiplier);
            addChild(accordionCategory);
            VirtualImageAccordionHeader(getHeaderAt(numChildren - 1)).editable = _editable;
        }


        /**
         * Handler called when a Master Virtual Image has been selected or unselected
         * We redispatch the event to all Categories Containers can receive it
         */
        private function onVirtualImageMasterSelectedUnselected(event:VirtualImageAccordionEvent):void
        {
            if (event.target != this)
            {
                event.stopPropagation();
                dispatchEvent(event);
            }
        }
    }
}