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

    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.HyperVisor")]
    public class HyperVisor extends InfrastructureElement
    {

        /* ------------- Public atributes ------------- */
        public var shortDescription:String;

        public var ip:String;

        public var port:int;

        public var type:HyperVisorType;

        public var ipService:String;
		
		public var user:String;
		
		public var password:String;

        /* ------------- Constructor ------------- */
        public function HyperVisor()
        {
            super();
            shortDescription = "";
            ip = "";
            type = new HyperVisorType();
            ipService = "";
        }

        override public function set assignedTo(iE:InfrastructureElement):void
        {
            if (iE is PhysicalMachine || iE == null)
                _assignedTo = iE;
            else
                throw Error("A Hyper Visor can only be assigned to a physical machine");
        }

    }
}