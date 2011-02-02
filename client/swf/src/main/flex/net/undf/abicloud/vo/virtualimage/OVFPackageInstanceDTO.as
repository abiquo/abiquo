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

    /**
     * Data transfer class to create Virtual Images when user uploads a local virtual image file
     * @author Oliver
     *
     */
    public class OVFPackageInstanceDTO
    {
        //OVFPackageDiskFormat possible values
        public static const OVF_PACKAGE_DISK_FORMAT:Array = [ "UNKOWN", "RAW", "INCOMPATIBLE", "VMDK_STREAM_OPTIMIZED",
                                                              "VMDK_FLAT", "VMDK_SPARSE", "VHD_FLAT", "VHD_SPARSE", "VDI_FLAT",
                                                              "VDI_SPARSE", "QCOW2_FLAT", "QCOW2_SPARSE" ];


        public static const MEMORY_SIZE_UNIT:Array = [ "BYTE", "KILOBYTE", "MEGABYTE", "GIGABYTE", "TERABYTE" ]

        public var ovfUrl:String;

        public var name:String;

        public var description:String;

        public var diskFilePath:String; 

        public var diskFileFormat:String; //OVF_PACKAGE_DISK_FORMAT

        public var diskFileSize:Number; //Filled by the Appliance Manager

        public var cpu:int;

        public var ram:Number;

        public var hd:Number;

        public var ramSizeUnit:String; //MEMORY_SIZE_UNIT

        public var hdSizeUnit:String; //MEMORY_SIZE_UNIT

        public var idEnterprise:Number;

        public var idUser:Number;

        public var masterDiskFilePath:String;

        public var iconPath:String;

        public var categoryName:String;


        public function OVFPackageInstanceDTO()
        {
        }
    }
}