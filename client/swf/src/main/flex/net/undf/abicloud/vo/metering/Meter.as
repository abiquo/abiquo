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

package net.undf.abicloud.vo.metering
{

    [RemoteClass(alias="com.abiquo.abiserver.pojo.metering.Meter")]
    [Bindable]
    public class Meter
    {

        public static const SEVERITY_SCALE:Object = { INFO: { color: 0x0099ff, value: 1 }, WARNING: { color: 0x663399, value: 2 }, MINOR: { color: 0x339933, value: 3 }, NORMAL: { color: 0xffcc00, value: 4 }, MAJOR: { color: 0xff9900, value: 5 }, CRITICAL: { color: 0xcc3333, value: 6 } };

        public var idMeter:Number;

        public var idDatacenter:int;

        public var datacenter:String;

        public var idRack:int;

        public var rack:String;

        public var idPhysicalMachine:int;

        public var physicalMachine:String;

        public var idStorageSystem:String;

        public var storageSystem:String;

        public var idStoragePool:String;

        public var storagePool:String;

        public var idVolume:String;

        public var volume:String;

        public var idNetwork:int;

        public var network:String;

        public var idSubnet:int;

        public var subnet:String;

        public var idEnterprise:int;

        public var enterprise:String;

        public var idUser:int;

        public var user:String;

        public var idVDC:int;

        public var virtualDataCenter:String;

        public var idVirtualApp:int;

        public var virtualApp:String;

        public var idVirtualMachine:int;

        public var virtualmachine:String;

        public var severity:String;

        public var timestamp:String;

        public var performedby:String;

        public var actionperformed:String;

        public var component:String;

        public var stacktrace:String;


        public function Meter()
        {
        }

    }
}