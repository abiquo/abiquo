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
    
    import net.undf.abicloud.events.virtualimage.OVFPackageEvent;
    import net.undf.abicloud.vo.virtualimage.OVFPackage;
    import net.undf.abicloud.vo.virtualimage.OVFPackageInstanceStatus;
    import net.undf.abicloud.vo.virtualimage.OVFPackageList;

    [Bindable]
    public class OVFPackageManager extends EventDispatcher
    {

        /**
         * The list of names of OVFPacakgeList that the user's Enterprise has
         */
        [Bindable(event="ovfPackageListNamesChange")]
        private var _ovfPackageListNames:ArrayCollection;

        public function get ovfPackageListNames():ArrayCollection
        {
            return _ovfPackageListNames;
        }

        public function set ovfPackageListNames(value:ArrayCollection):void
        {
            _ovfPackageListNames = value;
            dispatchEvent(new Event("ovfPackageListNamesChange"));
        }

        /**
         * OVFPackageManager manages this OVFPackageList and the OVFPackages it contains
         */
        [Bindable(event="ovfPackageListChange")]
        private var _ovfPackageList:OVFPackageList;

        public function get ovfPackageList():OVFPackageList
        {
            return _ovfPackageList;
        }

        public function set ovfPackageList(value:OVFPackageList):void
        {
            _ovfPackageList = value;
            buildOVFPackagesByURLHash();
            dispatchEvent(new Event("ovfPackageListChange"));
        }

        public function OVFPackageManager()
        {

        }

        private var ovfPackagesByURL:Object;

        /**
         * Builds a HashMap with ovfPackageURL / ovfPackage as key / value pairs. Used to optimize the recovery
         * of the OVFPacakges status for the current Repository
         */
        private function buildOVFPackagesByURLHash():void
        {
            if (_ovfPackageList)
            {
                ovfPackagesByURL = new Object();
                var length:int = _ovfPackageList.ovfpackages.length;
                var ovfPackage:OVFPackage;
                for (var i:int = 0; i < length; i++)
                {
                    ovfPackage = _ovfPackageList.ovfpackages.getItemAt(i) as OVFPackage;
                    ovfPackagesByURL[ovfPackage.url] = ovfPackage;
                }
            }
        }

        public function addOVFPackageList(ovfPackageList:OVFPackageList):void
        {
            //We only save its name
            _ovfPackageListNames.addItem(ovfPackageList.name);
            dispatchEvent(new Event("ovfPackageListNamesChange"));
        }

        public function removeOVFPackageList(ovfPackageList:OVFPackageList):void
        {
            var index:int = _ovfPackageListNames.getItemIndex(ovfPackageList.name);
            if (index > -1)
            {
                _ovfPackageListNames.removeItemAt(index);
                dispatchEvent(new Event("ovfPackageListNamesChange"));

                //Check if the deleted OVFPackageList is the current one we are managing
                if (_ovfPackageList.name == ovfPackageList.name)
                {
                    this.ovfPackageList = null;
                }
            }
        }

        /**
         * Announces that the current OVFPackageList has been refreshed and is no longer valid
         */
        public function ovfPackageListRefreshed(ovfPackageList:OVFPackageList):void
        {
            this.ovfPackageList = ovfPackageList;
        }

        /**
         * Set the OVFPackageStatus for the OVFPackages of the current OVFPackageList and the current VirtualImageManager Repository
         */
        public function setOVFPackageStatusForCurrentRepository(ovfPackagesStatus:ArrayCollection):void
        {
            var length:int = ovfPackagesStatus.length;
            var ovfPackageStatus:OVFPackageInstanceStatus;
            for (var i:int = 0; i < length; i++)
            {
                ovfPackageStatus = ovfPackagesStatus.getItemAt(i) as OVFPackageInstanceStatus;
                if (ovfPackagesByURL.hasOwnProperty(ovfPackageStatus.url))
                    OVFPackage(ovfPackagesByURL[ovfPackageStatus.url]).ovfPackageStatus = ovfPackageStatus;
            }

            var event:OVFPackageEvent = new OVFPackageEvent(OVFPackageEvent.OVF_PACKAGE_LIST_STATUS_RETRIEVED);
            dispatchEvent(event);
        }

        /**
         * Refreshes the status of some of the OVFPackage of the current OVFPackageList and the current VirtualImageManager repository
         */
        public function refreshOVFPackageStatusForCurrentRepository(ovfPackagesStatus:ArrayCollection):void
        {
            var length:int = ovfPackagesStatus.length;
            var ovfPackageStatus:OVFPackageInstanceStatus;
            for (var i:int = 0; i < length; i++)
            {
                ovfPackageStatus = ovfPackagesStatus.getItemAt(i) as OVFPackageInstanceStatus;
                if (ovfPackagesByURL.hasOwnProperty(ovfPackageStatus.url))
                    OVFPackage(ovfPackagesByURL[ovfPackageStatus.url]).ovfPackageStatus = ovfPackageStatus;
            }

            var event:OVFPackageEvent = new OVFPackageEvent(OVFPackageEvent.OVF_PACKAGE_STATUS_REFRESHED);
            dispatchEvent(event);
        }

        /**
         * Updates the status of an OVFPackage that was being downloaded, and has been canceled
         */
        public function ovfPackageDownloadCanceled(ovfPackage:OVFPackage, newOVFPackageStatus:OVFPackageInstanceStatus):void
        {
            ovfPackage.ovfPackageStatus = newOVFPackageStatus;
            var event:OVFPackageEvent = new OVFPackageEvent(OVFPackageEvent.OVF_PACKAGE_DOWNLOAD_CANCELED);
            event.ovfPackage = ovfPackage;
            dispatchEvent(event);
        }
    }
}