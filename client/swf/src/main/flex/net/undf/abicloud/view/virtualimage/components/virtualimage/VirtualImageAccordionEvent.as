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

    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    public class VirtualImageAccordionEvent extends Event
    {

        public static const VIRTUAL_IMAGE_ACCORDION_CATEGORY_DISPOSE:String = "virtualImageAccordionCategoryDisposeVirtualImageAccordionEvent";

        public static const VIRTUAL_IMAGE_MINIATURE_EDITION_REQUESTED:String = "virtualImageMiniatureEditionRequestedVirtualImageAccordionEvent";

        public static const VIRTUAL_IMAGE_MINIATURE_DOWNLOAD_REQUESTED:String = "virtualImageMiniatureDownloadResquestedVirtualImageAccordionEvent";

        public static const VIRTUAL_IMAGE_EDITION_COMPLETE:String = "virtualImageEditionCompleteVirtualImageAccordionEvent";

        public static const VIRTUAL_IMAGE_MASTER_SELECTED:String = "virtualImageMasterSelectedVirtualImageAccordionEvent";

        public static const VIRTUAL_IMAGE_MASTER_UNSELECTED:String = "virtualImageMasterUnselectedVirtualImageAccordionEvent";

        public var virtualImage:VirtualImage;

        public function VirtualImageAccordionEvent(type:String, bubbles:Boolean = false,
                                                   cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

        override public function clone():Event
        {
            var event:VirtualImageAccordionEvent = new VirtualImageAccordionEvent(this.type,
                                                                                  this.bubbles,
                                                                                  this.cancelable);
            event.virtualImage = this.virtualImage;

            return event;
        }

    }
}