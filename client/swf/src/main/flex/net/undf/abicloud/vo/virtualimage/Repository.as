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
    import net.undf.abicloud.vo.infrastructure.DataCenter;

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualimage.Repository")]
    [Bindable]
    public class Repository
    {
        public var id:int;

        public var name:String;

        public var URL:String;

        public var datacenter:DataCenter;

        public var repositoryCapacityMb:Number;

        public var repositoryEnterpriseUsedMb:Number;

        public var repositoryRemainingMb:Number;

        public function Repository()
        {
            id = 0;
            name = "";
            URL = "";
        }

    }
}