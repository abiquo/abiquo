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
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.user.User;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;
    import net.undf.abicloud.vo.virtualimage.VirtualImageConversions;


    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.VirtualMachine")]
    public class VirtualMachine extends InfrastructureElement
    {
        /* ------------- Constants ------------- */
        //Possible values for idType
        public static const VIRTUAL_MACHINE_NOT_MANAGED:int = 0;

        public static const VIRTUAL_MACHINE_MANAGED:int = 1;



        /* ------------- Public atributes ------------- */
        public var virtualImage:VirtualImage;

        public var UUID:String;

        public var description:String;

        public var ram:int;

        public var cpu:int;

        public var hd:Number;

        public var vdrpPort:int;

        public var vdrpIP:String;

        public var state:State;

        public var highDisponibility:Boolean;

        public var user:User;

        public var enterprise:Enterprise;

        public var idType:int;

        public var conversion:VirtualImageConversions;

        public var datastore:Datastore;


        /* ------------- Constructor ------------- */
        public function VirtualMachine()
        {
            super();
            virtualImage = new VirtualImage();
            UUID = "";
            description = "";
            ram = 0;
            cpu = 0;
            hd = 0;
            vdrpPort = 0;
            vdrpIP = "";
            state = new State();
            highDisponibility = false;
            user = new User();
            idType = 0;
        }


        override public function set assignedTo(iE:InfrastructureElement):void
        {
            if (iE is HyperVisor || iE == null)
                _assignedTo = iE;
            else
                throw Error("A Virtual Machine can only be assigned to a Hyper Visor");
        }
    }
}