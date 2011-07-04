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

package net.undf.abicloud.events.virtualimage
{
    import flash.events.Event;
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.view.virtualimage.components.virtualimage.VirtualImageMiniature;
    import net.undf.abicloud.vo.infrastructure.DataCenter;
    import net.undf.abicloud.vo.infrastructure.HyperVisorType;
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.virtualimage.Category;
    import net.undf.abicloud.vo.virtualimage.Icon;
    import net.undf.abicloud.vo.virtualimage.Repository;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    public class VirtualImageEvent extends Event
    {

        /* ------------- Constants------------- */

        public static const GET_DISK_FORMAT_TYPES:String = "getDiskFormatTypesVirtualImageEvent";

        public static const GET_DATACENTER_REPOSITORY:String = "getDatacenterRepositoryVirtualImageEvent";

        public static const GET_CATEGORIES:String = "getCategoriesVirtualImageEvent";

        public static const CREATE_CATEGORY:String = "createCategoryVirtualImageEvent";

        public static const CATEGORY_CREATED:String = "categoryCreatedVirtualImageEvent";

        public static const DELETE_CATEGORY:String = "deleteCategoryVirtualImageEvent";

        public static const CATEGORY_DELETED:String = "categoryDeletedVirtualImageEvent";

        public static const GET_ICONS:String = "getIconsVirtualImageEvent";

        public static const CREATE_ICON:String = "createIconVirtualImageEvent";

        public static const DELETE_ICON:String = "deleteIconVirtualImageEvent";

        public static const EDIT_ICON:String = "editIconVirtualImageEvent";

        public static const GET_VIRTUAL_IMAGES_BY_CATEGORY:String = "getVirtualImageByCategoryVirtualImageEvent";

        public static const GET_VIRTUAL_IMAGES_BY_CATEGORY_AND_HYPERVISOR_TYPE:String = "getVirtualImagesByCategoryAndHypervisorTypeVirtualImageEvent";

        public static const DELETE_VIRTUAL_IMAGE:String = "deleteVirtualImageVirtualImageEvent";

        public static const EDIT_VIRTUAL_IMAGE:String = "editVirtualImageVirtualImageEvent";

        public static const VIRTUAL_IMAGE_EDITED:String = "virtualImageEditedVirtualImageEvent";

        public static const VIRTUAL_IMAGE_DELETED:String = "virtualImageDeletedVirtualImageEvent";

        public static const CHECK_VIRTUAL_IMAGE_UPLOAD_PROGRESS:String = "checkVirtualImageUploadProgressVirtualImageEvent";
        
        public static const MINIATURE_CLICKED:String = "miniatureClickedVirtualImageEvent";
        
        public static const VIRTUAL_IMAGE_CHECKED:String = "virtualImageCheckedVirtualImageEvent";
        
        public static const VIRTUAL_IMAGE_UNCHECKED:String = "virtualImageUncheckedVirtualImageEvent";
        
        public static const VIRTUAL_IMAGE_UPLOAD_ERROR:String = "virtualImageUploadErrorVirtualImageEvent";

        /* ------------- Public atributes ------------- */

        public var callback:Function;

        public var enterprise:Enterprise;

        public var datacenter:DataCenter;

        public var repository:Repository;

        public var category:Category;

        public var hypervisorType:HyperVisorType;

        public var icon:Icon;

        public var virtualImage:VirtualImage;

        public var viUpdatedValues:VirtualImage;

        public var ovfPackageURLList:ArrayCollection;
        
        public var ovfInstanceId:String;
        
        public var virtualImageMiniature:VirtualImageMiniature;

        /* ------------- Constructor ------------- */
        public function VirtualImageEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}