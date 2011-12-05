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
    import net.undf.abicloud.vo.virtualhardware.ResourceManagement;

    /**
     * This class is used when listing NICs
     * @author Oliver
     *
     */
    [RemoteClass(alias="com.abiquo.abiserver.pojo.networking.IpPoolManagement")]
    [Bindable]
    public class IPPoolManagement extends ResourceManagement
    {
        public var dhcpServiceId:int;

        public var mac:String;

        public var name:String;

        public var ip:String;

        public var vlanNetworkName:String;
        
        public var vlanNetworkId:int;

        public var enterpriseId:int;

        public var enterpriseName:String;

        public var configureGateway:Boolean;

        public var quarantine:Boolean;
        
        public var available:Boolean;

        public function IPPoolManagement()
        {
        }
    }
}