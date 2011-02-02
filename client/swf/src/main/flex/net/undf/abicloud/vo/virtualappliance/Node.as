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

    /**
     * This class represents a Virtual Appliance's node
     **/

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualappliance.Node")]
    [Bindable]
    public class Node
    {

        /* ------------- Public constants ------------- */
        public static const NODE_NOT_MODIFIED:int = 0;

        public static const NODE_MODIFIED:int = 1;

        public static const NODE_ERASED:int = 2;

        public static const NODE_NEW:int = 3;

        public static const CRASH:int = 4;

        /* ------------- Public atributes ------------- */
        public var id:int;

        public var name:String;

        public var idVirtualAppliance:int;

        public var nodeType:NodeType;

        //For drawing purposes
        public var posX:int;

        public var posY:int;

        //Networking
        public var privateIP:String;

        public var macAddress:String;

        //For performance purposes
        //To be set when a node has been modified, when we want to save changes on editing a virtual appliance
        public var modified:int;

        /* ------------- Constructor ------------- */
        public function Node()
        {
            id = 0;
            name = "";
            idVirtualAppliance = 0;
            nodeType = new NodeType();

            posX = 0;
            posY = 0;

            privateIP = "";
            macAddress = "";

            modified = NODE_NOT_MODIFIED;
        }

    }
}