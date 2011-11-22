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

package net.undf.abicloud.vo.infrastructure
{

    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.State")]
    [Bindable]
    public class State
    {

        public static const ON:State = new State(6, "ON"); // ON (VM) -> RUNNING:1

        public static const PAUSED:State = new State(7, "PAUSED"); // PAUSED (VM)

        public static const OFF:State = new State(8, "OFF"); // OFF (VM) -> OFF:int = 3

        public static const NOT_DEPLOYED:State = new State(1, "NOT_DEPLOYED"); // NOT_DEPLOYED (VAPP) -> NOT_DEPLOYED:int = 5;
        
        public static const NOT_ALLOCATED:State = new State(2, "NOT_ALLOCATED"); // NOT_ALLOCATED (VM) -> NOT_DEPLOYED:int = 5;

        public static const LOCKED:State = new State(10, "LOCKED"); // LOCKED (VAPP) (VM) -> IN_PROGRESS:int = 6

        public static const NEEDS_SYNC:State = new State(9, "NEEDS_SYNC"); // NEEDS_SYNC (VAPP) -> APPLY_CHANGES_NEEDED:int = 7;

        //public static const UPDATING_NODES:int = 8; // LOCKED (VAPP) -> UPDATING_NODES:int = 8;

        //public static const FAILED:int = 9; // UNKNOWN (VAPP) (VM) -> FAILED:int = 9;

        public static const UNKNOWN:State = new State(11, "UNKNOWN"); // UNKNOWN (VAPP) (VM) -> UNKNOWN:int = 15;
        public static const ALLOCATED:State = new State(3, "ALLOCATED"); // UNKNOWN (VAPP) (VM) -> UNKNOWN:int = 15;
        public static const DEPLOYED:State = new State(4, "DEPLOYED"); // UNKNOWN (VAPP) (VM) -> UNKNOWN:int = 15;
        public static const CONFIGURED:State = new State(5, "CONFIGURED"); // UNKNOWN (VAPP) (VM) -> UNKNOWN:int = 15;
        

        public var id:int;

        public var description:String;

        public function State(id:int = 0, description:String = "")
        {
            this.id = id;
            this.description = description;
        }

    }
}
