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

package net.undf.abicloud.vo.service
{

    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.service.RemoteServiceType")]
    public class RemoteServiceType
    {
        /* ------------- Constants ------------- */
        public static const VIRTUAL_FACTORY:String = "VIRTUAL_FACTORY";

        public static const VIRTUAL_STORAGE_SYSTEM:String = "STORAGE_SYSTEM_MONITOR";

        public static const VIRTUAL_SYSTEM_MONITOR:String = "VIRTUAL_SYSTEM_MONITOR";

        public static const NODE_COLLECTOR:String = "NODE_COLLECTOR";

        public static const DHCP:String = "DHCP_SERVICE";

        public static const APPLIANCE_MANAGER:String = "APPLIANCE_MANAGER";

        public static const BPM:String = "BPM_SERVICE";

        /* ------------- Public atributes ------------- */
        public var name:String;

        public var serviceMapping:String;

        public var protocol:String;

        public var port:String;

        public var valueOf:String;

        /* ------------- Constructor ------------- */
        public function RemoteServiceType()
        {
            name = "";
            serviceMapping = "";
            protocol = "";
            port = "";
            valueOf = "";
        }

    }
}
