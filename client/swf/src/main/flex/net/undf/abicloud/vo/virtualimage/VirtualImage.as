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


    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualimage.VirtualImage")]
    public class VirtualImage
    {
        public static const TYPE_STATELESS:int = 0;

        public static const TYPE_STATEFUL:int = 1;

        /* ------------- Public atributes ------------- */
        public var id:int;

        public var name:String;

        public var description:String;

        public var path:String;

        public var hdRequired:Number;

        public var ramRequired:int;

        public var cpuRequired:int;

        public var category:Category;

        public var repository:Repository;

        public var icon:Icon;

        public var deleted:Boolean;

        public var diskFormatType:DiskFormatType;

        public var master:VirtualImage;

        public var idEnterprise:int;

        public var ovfId:String;

        public var stateful:int;

        public var diskFileSize:Number;
        
        public var shared:int;
        
        public var costCode:int;
        
        public var chefEnabled:Boolean;

        /* ------------- Constructor ------------- */
        public function VirtualImage()
        {
            id = 0;
            name = "";
            description = "";
            path = "";
            hdRequired = 0;
            ramRequired = 0;
            cpuRequired = 0;
            category = new Category();
            repository = new Repository();
            icon = new Icon();
            deleted = false;
            diskFormatType = new DiskFormatType();
            master = null;
            idEnterprise = 0;
            ovfId = "";
            stateful = TYPE_STATELESS;
            diskFileSize = 0;
            shared = 0;
            costCode = 0;
            chefEnabled = false;
        }

    }
}
