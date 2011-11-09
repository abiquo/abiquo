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

package net.undf.abicloud.vo.systemproperties
{
    [RemoteClass(alias="com.abiquo.abiserver.pojo.config.SystemProperty")]
    [Bindable]
    public class SystemProperty
    {
        /* ------------- Public atributes ------------- */
        public var id:int;
        public var name:String;
        public var value:*;
        public var description:String;
        public var type:*;
        public var validatorNames:Array;

        /* ------------- Constructor ------------- */
        public function SystemProperty(base:Object = null)
        {
        	if(base == null){
	            id = 0;
	            name = "";
	            value = "";
	            description = "";
	            type = null;
	        }
	        else
	        {
		        if(base.id != null){
		        	id = base.id;
		        }
		        if(base.name != null){
		        	name = base.name;
		        }
		        if(base.value != null){
		        	value = base.value;
		        }
		        if(base.description != null){
		        	description = base.description;
		        }
		        if(base.type != null){
		        	type = base.type;
		        }
		    }
        }
    }
}
