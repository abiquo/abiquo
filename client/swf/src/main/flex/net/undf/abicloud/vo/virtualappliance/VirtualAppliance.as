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
    import mx.collections.ArrayCollection;

    import net.undf.abicloud.vo.infrastructure.State;
    import net.undf.abicloud.vo.user.Enterprise;

    /**
     * This class represents a Virtual Appliance
     **/

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance")]
    [Bindable]
    public class VirtualAppliance
    {

        /* ------------- Public atributes ------------- */
        public var id:int;

        public var name:String;

        public var isPublic:Boolean;

        public var state:State;

        public var highDisponibility:Boolean;

        //Array containing a list of nodes
        //It may be null, if no nodes exists or the nodes list has not been retrieved
        public var nodes:ArrayCollection;

        //XML Document containing the relations between nodes
        public var nodeConnections:String;

        //ArrayCollection containing the log entries list for this VirtualAppliance
        public var logs:ArrayCollection;

        //Flag that indicates if this Virtual Appliance had any error in the last operation
        public var error:Boolean;

        //The VirtualDataCenter to which this VirtualAppliance belongs
        public var virtualDataCenter:VirtualDataCenter;

        //The Enterprise to which this VirtualAppliance belongs
        //It may be null, if this VirtualAppliance is not assigned to any Enterprsie
        public var enterprise:Enterprise;

        /* ------------- Constructor ------------- */
        public function VirtualAppliance()
        {
            id = 0;
            name = "";
            isPublic = false;
            state = new State(1, State.NOT_DEPLOYED.description);
            highDisponibility = false;
            nodes = new ArrayCollection();
            nodeConnections = "<connections></connections>";
            logs = new ArrayCollection();
            error = false;
            virtualDataCenter = new VirtualDataCenter();
            enterprise = null;
        }

    }
}
