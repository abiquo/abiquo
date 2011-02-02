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

package net.undf.abicloud.controller.virtualimage
{
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.virtualimage.Category;
    import net.undf.abicloud.vo.virtualimage.Icon;
    import net.undf.abicloud.vo.virtualimage.OVFPackageInstanceStatus;
    import net.undf.abicloud.vo.virtualimage.Repository;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    /**
     * Class to handle server responses when calling Virtual Images remote services defined in VirtualImageEventMap
     */
    public class VirtualImageResultHandler extends ResultHandler
    {
        /* ------------- Constructor --------------- */
        public function VirtualImageResultHandler()
        {
            super();
        }


        public function handleGetDiskFormatTypes(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.diskFormatTypes = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetDatacenterRepository(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.repository = DataResult(result).data as Repository;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetCategories(result:BasicResult):void
        {
            if (result.success)
            {               
                //Saving the Categories in model                
                AbiCloudModel.getInstance().virtualImageManager.categories = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCreateCategory(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new category to the model
                var categoryCreated:Category = DataResult(result).data as Category;
                AbiCloudModel.getInstance().virtualImageManager.addCategory(categoryCreated);
            }
            else
            {
                //There was a problem creating the new category
                super.handleResult(result);
            }
        }

        public function handleDeleteCategory(result:BasicResult, deletedCategory:Category):void
        {
            if (result.success)
            {
                //Deleting the category from the model
                AbiCloudModel.getInstance().virtualImageManager.removeCategory(deletedCategory);
            }
            else
            {
                //There was a problem deleting the category
                super.handleResult(result);
            }
        }

        public function handleGetIcons(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.icons = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCreateIcon(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new icon to the model
                var iconCreated:Icon = DataResult(result).data as Icon;
                AbiCloudModel.getInstance().virtualImageManager.createIcon(iconCreated);
            }
            else
            {
                //There was a problem creating the new category
                super.handleResult(result);
            }
        }

        public function handleDeleteIcon(result:BasicResult, iconDeleted:Icon):void
        {
            if (result.success)
            {
                //Deleting the icon from the model
                AbiCloudModel.getInstance().virtualImageManager.deleteIcon(iconDeleted);
            }
            else
            {
                //There was a problem deleting the icon
                super.handleResult(result);
            }
        }

        public function handleEditIcon(result:BasicResult, newValues:Icon):void
        {
            if (result.success)
            {
                //Updating the icon in the model
                AbiCloudModel.getInstance().virtualImageManager.editIcon(newValues);
            }
            else
            {
                //There was a problem editing the icon
                super.handleResult(result);
            }
        }

        public function handleGetVirtualImagesByCategory(result:BasicResult, callback:Function):void
        {
            if (result.success)
            {
                //The VirtualImages are not saved in model, but returned to who asked for them
                AbiCloudModel.getInstance().virtualImageManager.virtualImageList = DataResult(result).data as ArrayCollection;
                callback(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleEditVirtualImage(result:BasicResult, virtualImage:VirtualImage,
                                               updatedValues:VirtualImage):void
        {
            if (result.success)
            {
                //Editing the Virtual Image in the model
                AbiCloudModel.getInstance().virtualImageManager.editVirtualImage(virtualImage,
                                                                                 updatedValues);
            }
            else
            {
                //There was a problem editing the Virtual Image
                super.handleResult(result);
            }
        }

        public function handleDeleteVirtualImage(result:BasicResult, virtualImage:VirtualImage):void
        {
            if (result.success)
            {
                //Deleting the virtual image in model
                AbiCloudModel.getInstance().virtualImageManager.deleteVirtualImage(virtualImage);
            }
            else
            {
                //There was a problem with the virtual image deletion
                super.handleResult(result);
            }
        }

        public function handleCheckVirtualImageUploadProgress(result:BasicResult,
                                                              callback:Function):void
        {
            if (result.success)
            {
                var ovfPackageStatusList:ArrayCollection = DataResult(result).data as ArrayCollection;
                if (ovfPackageStatusList.length > 0)
                {
                    //The upload progress are not saved in model, but returned to who asked for them
                    callback(ovfPackageStatusList.getItemAt(0) as OVFPackageInstanceStatus);
                }
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
    }
}