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
     * A Host is an object that contains a relation IP/MAC inside a DHCP
     * Usually a DHCP object will have a Host's ArrayCollection
     *
     * @author xfernandez
     **/
    import mx.collections.ArrayCollection;

    [RemoteClass(alias="com.abiquo.abiserver.pojo.networking.Host")]
    [Bindable]
    public class Host
    {
        public var hosttypeID:int;

        public var dhcptypeID:int;

        public var mac:String;

        public var name:String;

        public var ip:String;

        public var required:Boolean;

        public var configureGateway:Boolean;

        public function Host()
        {
            dhcptypeID = 0;
            hosttypeID = 0;
            mac = "";
            name = "";
            ip = "";
            required = false;
        }

    }
}