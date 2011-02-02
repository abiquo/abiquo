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
    import net.undf.abicloud.events.virtualimage.OVFPackageEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.virtualimage.OVFPackage;
    import net.undf.abicloud.vo.virtualimage.OVFPackageList;
    import net.undf.abicloud.vo.virtualimage.OVFPackageInstanceStatus;

    /**
     * Class to handle server responses when calling AppsLibrary remote services defined in OVFPackageEventMap
     */
    public class OVFPackageResultHandler extends ResultHandler
    {
        public function OVFPackageResultHandler()
        {
        }

        public function handleGetOVFPackageListNames(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.ovfPackageListNames = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetOVFPackageList(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.ovfPackageList = DataResult(result).data as OVFPackageList;
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCreateOVFPackageList(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.addOVFPackageList(DataResult(result).data as OVFPackageList);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleDeleteOVFPackageList(result:BasicResult, ovfPackageList:OVFPackageList):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.removeOVFPackageList(ovfPackageList);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleRefreshOVFPackageList(result:BasicResult):void
        {
            if (result.success)
            {
                //Announce that the OVFPackageList has been refreshed
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.ovfPackageListRefreshed(DataResult(result).data as OVFPackageList);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleGetOVFPackageStatus(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.setOVFPackageStatusForCurrentRepository(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleStartDownloadOVFPackage(result:BasicResult):void
        {
            if (result.success)
            {
                //Announce that the download has started
                var event:OVFPackageEvent = new OVFPackageEvent(OVFPackageEvent.OVF_PACKAGE_DOWNLOAD_STARTED);
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.dispatchEvent(event);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleRefreshOVFPackageStatus(result:BasicResult):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.refreshOVFPackageStatusForCurrentRepository(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }

        public function handleCancelDownloadOVFPackage(result:BasicResult, ovfPackage:OVFPackage):void
        {
            if (result.success)
            {
                AbiCloudModel.getInstance().virtualImageManager.ovfPackageManager.ovfPackageDownloadCanceled(ovfPackage,
                                                                                                             DataResult(result).data as OVFPackageInstanceStatus);
            }
            else
            {
                //There was a problem
                super.handleResult(result);
            }
        }
    }
}