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

package net.undf.abicloud.vo.networking
{

    [RemoteClass(alias="com.abiquo.abiserver.pojo.networking.VlanNetworkParameters")]
    [Bindable]
    public class VlanNetworkParameters
    {
       /**
		* Identifer of the parameter.
		*/
		public var vlan_network_parametersId:int;
		
		/**
		* The VLAN id min
		*/
		public var vlan_id_min:int;
		
		/**
		* The VLAN id max
		*/
		public var vlan_id_max:int;
		
		/**
		* The VLAN's id to avoid
		*/
		public var vlans_id_avoided:String;
		
		
		/**
		* The VLAN's per VDC
		*/
		public var vlan_per_vdc_expected:int;
		
		/**
		* The VLAN's networking resource security quotient
		*/
		public var NRSQ:int;


        public function VlanNetworkParameters()
        {
        	
        }
    }
}