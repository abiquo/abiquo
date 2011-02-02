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

package net.undf.abicloud.vo.virtualimage
{

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualimage.OVFPackage")]
    [Bindable]
    public class OVFPackage
    {
        public var idOVFPackage:int;

        public var url:String; //ovfid

        public var name:String;

        public var description:String;

        public var productName:String;

        public var productUrl:String;

        public var productVersion:String;

        public var productVendor:String;

        public var category:String;

        public var iconUrl:String;

        public var diskFormat:String;

        public var diskSizeMb:Number; //MBytes

        public var ovfPackageStatus:OVFPackageInstanceStatus;

        //Custom attribute to control when an OVFPackage has been selected to download
        public var selectedToDownload:Boolean;

        /**
         * Creates an OVFPackage
         *
         */
        public function OVFPackage()
        {

        }
    }
}