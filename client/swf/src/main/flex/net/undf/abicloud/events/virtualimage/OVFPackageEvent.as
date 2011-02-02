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

    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.virtualimage.OVFPackage;
    import net.undf.abicloud.vo.virtualimage.OVFPackageList;
    import net.undf.abicloud.vo.virtualimage.Repository;

    public class OVFPackageEvent extends Event
    {
        public static const GET_OVF_PACKAGE_LIST_NAMES:String = "getOVFPackageListNamesOVFPackageEvent";

        public static const GET_OVF_PACKAGE_LIST:String = "getOVFPackageListOVFPackageEvent";

        public static const CREATE_OVF_PACKAGE_LIST:String = "createOVFPackageListOVFPackageEvent";

        public static const DELETE_OVF_PACKAGE_LIST:String = "deleteOVFPackageListOVFPackageEvent";

        public static const REFRESH_OVF_PACKAGE_LIST:String = "refreshOVFPackageListOVFPackageEvent";

        public static const GET_OVF_PACKAGE_LIST_STATUS:String = "getOVFPackageListStatusOVFPackageEvent";

        public static const OVF_PACKAGE_LIST_STATUS_RETRIEVED:String = "ovfPackageListStatusRetrievedOVFPackageEvent";

        public static const START_DOWNLOAD_OVF_PACKAGE:String = "startDownloadOVFPackageOVFPackageEvent";

        public static const OVF_PACKAGE_DOWNLOAD_STARTED:String = "ovfPackageDownloadStartedOVFPackageEvent";

        public static const REFRESH_OVF_PACKAGE_STATUS:String = "refreshOVFPackageStatusOVFPackageEvent";

        public static const OVF_PACKAGE_STATUS_REFRESHED:String = "ovfPackageStatusRefreshedOVFPackageEvent";

        public static const CANCEL_DOWNLOAD_OVF_PACKAGE:String = "cancelDownloadOVFPackageOVFPackageEvent";

        public static const OVF_PACKAGE_DOWNLOAD_CANCELED:String = "ovfPackageDownloadCanceledOVFPackageEvent";

        public static const OVFPACKAGE_SELECTED_TO_DOWNLOAD:String = "ovfPackageSelectedToDownloadOVFPackageEvent";

        public static const OVFPACKAGE_UNSELECTED_TO_DOWNLOAD:String = "ovfPackageUnselectedToDownloadOVFPackageEvent";


        public var enterprise:Enterprise;

        public var ovfPackageList:OVFPackageList;

        public var repository:Repository;

        public var ovfPackageURLList:ArrayCollection;

        public var ovfPackage:OVFPackage;


        /* ------------- Constructor ------------- */
        public function OVFPackageEvent(type:String, bubbles:Boolean = true,
                                        cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }
    }
}