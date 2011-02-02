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

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions")]
    [Bindable]
    public class VirtualImageConversions
    {
        public var id:int = 0;

        public var image:VirtualImage;

        public var diskSourceFormatType:DiskFormatType;

        public var diskTargetFormatType:DiskFormatType;

        public var timestamp:Date;

        public var sourcePath:String;

        public var targetPath:String;

        public var state:StateConversion;

        public function VirtualImageConversions()
        {

        }
    }
}