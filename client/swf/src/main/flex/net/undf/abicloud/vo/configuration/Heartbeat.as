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

package net.undf.abicloud.vo.configuration
{

    [Bindable]
    [RemoteClass(alias="com.abiquo.heartbeat.shared.dto.HeartbeatDTO")]
    public class Heartbeat
    {

        public var id:String;

        public var abiCloudId:String = "";

        public var clientIP:String = "";

        public var physicalServers:Number = 0;

        public var virtualMachines:Number = 0;

        public var volumes:Number = 0;

        public var virtualDataCenters:Number = 0;

        public var virtualAppliances:Number = 0;

        public var organizations:Number = 0;

        public var virtualCores:Number = 0;

        public var totalVirtualCoresAllocated:Number = 0;

        public var totalVirtualCoresUsed:Number = 0;

        public var totalVirtualMemoryAllocated:Number = 0;

        public var totalVirtualMemoryUsed:Number = 0;

        public var totalVolumeSpaceAllocated:Number = 0;

        public var totalVolumeSpaceUsed:Number = 0;

        public var totalVirtualImages:Number = 0;

        public var operatingSystemName:String = "";

        public var operatingSystemVersion:String = "";

        public var databaseName:String = "";

        public var databaseVersion:String = "";

        public var applicationServerName:String = "";

        public var applicationServerVersion:String = "";

        public var javaVersion:String = "";

        public var abicloudVersion:String = "";

        public var abicloudDistribution:String = "";

        public var timestamp:Number;

        public function Heartbeat()
        {

        }

    }
}
