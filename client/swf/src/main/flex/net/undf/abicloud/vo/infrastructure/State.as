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

        public static const RUNNING:int = 1; // ON (VM)

        public static const PAUSED:int = 2; // PAUSED (VM)

        public static const POWERED_OFF:int = 3; // OFF (VM)

        public static const REBOOTED:int = 4; // ON (VM)

        public static const NOT_DEPLOYED:int = 5; // NOT_DEPLOYED (VAPP) NOT_ALLOCATED (VM)

        public static const IN_PROGRESS:int = 6; // LOCKED (VAPP) (VM)

        public static const APPLY_CHANGES_NEEDED:int = 7; // NEEDS_SYNC (VAPP)

        public static const UPDATING_NODES:int = 8; // LOCKED (VAPP)

        public static const FAILED:int = 9; // GONE

        public static const COPYING:int = 10; // LOCKED (VAPP)

        public static const MOVING:int = 11; // LOCKED (VAPP)

        public static const CHECKING:int = 12; // LOCKED (VM)

        public static const BUNDLING:int = 13; // LOCKED (VM)

        public static const STATEFUL:int = 14; // GONE
        
        public static const UNKNOWN:int = 15; // UNKNOWN (VAPP) (VM)
        

        public var id:int;

        public var description:String;

        public function State(id:int = 0)
        {
            this.id = id;
            this.description = "";
        }

    }
}
