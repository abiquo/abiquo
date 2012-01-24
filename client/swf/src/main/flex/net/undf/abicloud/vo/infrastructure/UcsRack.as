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
    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.UcsRack")]
	public class UcsRack extends Rack
	{
		
		/* ------------- Public atributes ------------- */

        public var ip:String;

        public var user:String;

        public var password:String;
        
        public var port:int;
        
        public var defaultTemplate:String;
        
        public var maxMachinesOn:int;
        
        public static const TYPE:String = "Cisco UCS";
        public static const DEFAULT_PORT:int = 80;

        /* ------------- Constructor ------------- */
		public function UcsRack()
		{
			super();
			ip = "";
			user = "";
			password = "";
			port = DEFAULT_PORT;
			maxMachinesOn = 0;
		}
		
	}
}
