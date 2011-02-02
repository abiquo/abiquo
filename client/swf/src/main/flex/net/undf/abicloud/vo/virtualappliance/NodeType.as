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

package net.undf.abicloud.vo.virtualappliance
{

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualappliance.NodeType")]
    [Bindable]
    public class NodeType
    {

        public static const VIRTUALIMAGE:int = 1;

        public static const STORAGE:int = 2;

        public static const NETWORK:int = 3;

        public var id:int;

        public var name:String;

        public function NodeType()
        {
            id = 0;
            name = "";
        }
    }
}