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

    import mx.containers.accordionClasses.AccordionHeader;
    import mx.events.DragEvent;
    import mx.managers.DragManager;
    import mx.utils.ObjectUtil;

    import net.undf.abicloud.events.virtualimage.VirtualImageEvent;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    public class VirtualImageAccordionHeader extends AccordionHeader
    {


        private var _editable:Boolean = false;

        public function VirtualImageAccordionHeader()
        {
            this.addEventListener(DragEvent.DRAG_ENTER, onDragEnter);
            this.addEventListener(DragEvent.DRAG_DROP, onDragDrop);
            this.addEventListener(DragEvent.DRAG_EXIT, onDragExit);
            this.addEventListener(Event.REMOVED, onRemove);

            super();
        }


        public function set editable(value:Boolean):void
        {
            this._editable = value;
        }

        private function onDragEnter(event:DragEvent):void
        {
            if (this._editable && event.dragSource.hasFormat("VirtualImage"))
            {
                drawFocus(true);

                //We only accept a drop if this CategoryRenderer does not already contain the Virtual Image that
                //the user wants to drop
                var virtualImage:VirtualImage = event.dragSource.dataForFormat("VirtualImage") as VirtualImage;

                if (!this.data.containsVirtualImage(virtualImage))
                    DragManager.acceptDragDrop(this);
            }
        }

        private function onDragDrop(event:DragEvent):void
        {
            //Editing the Virtual Image being dragged with the new category
            //We do not modify the original one, until changes are made in server
            var virtualImage:VirtualImage = event.dragSource.dataForFormat("VirtualImage") as VirtualImage;
            var updatedValues:VirtualImage = ObjectUtil.copy(virtualImage) as VirtualImage;
            updatedValues.category = this.data.category;

            //Announcing that a VirtualImage has been edited
            var virtualImageEvent:VirtualImageEvent = new VirtualImageEvent(VirtualImageEvent.EDIT_VIRTUAL_IMAGE);
            virtualImageEvent.virtualImage = virtualImage;
            virtualImageEvent.viUpdatedValues = updatedValues;
            dispatchEvent(virtualImageEvent);

            drawFocus(false);
        }

        private function onDragExit(event:DragEvent):void
        {
            drawFocus(false);
        }

        /**
         * To properly destroy this component
         */
        private function onRemove(event:Event):void
        {
            this.removeEventListener(DragEvent.DRAG_ENTER, onDragEnter);
            this.removeEventListener(DragEvent.DRAG_DROP, onDragDrop);
            this.removeEventListener(DragEvent.DRAG_EXIT, onDragExit);
            this.removeEventListener(Event.REMOVED, onRemove);
        }
    }
}