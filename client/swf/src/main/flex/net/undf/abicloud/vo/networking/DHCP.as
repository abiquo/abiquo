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

package net.undf.abicloud.vo.networking
{
    /**
     *
     * A DHCP is an object of a Network that stores the list of IP/Mac's in a network
     *
     * @author xfernandez
     **/
    import mx.collections.ArrayCollection;

    [RemoteClass(alias="com.abiquo.abiserver.pojo.networking.DHCP")]
    [Bindable]
    public class DHCP
    {
        public var dhcptypeID:int;

        public var networktypeID:int;

        public var hosts:ArrayCollection;

        public var address:String;

        public var netmask:String;

        public var gateway:String;

        public var required:Boolean;

        public function DHCP()
        {
            dhcptypeID = 0;
            networktypeID = 0;
            hosts = new ArrayCollection();
            address = "";
            netmask = "";
            gateway = "";
            required = false;
        }

    }
}