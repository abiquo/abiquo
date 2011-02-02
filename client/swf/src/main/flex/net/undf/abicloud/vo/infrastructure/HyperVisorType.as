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
	import flash.events.Event;
	import flash.events.EventDispatcher;
	
	import mx.utils.ObjectProxy;
	
	import net.undf.abicloud.vo.virtualimage.DiskFormatType;
	[RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.HyperVisorType")]
	[Bindable]
	public class HyperVisorType extends EventDispatcher
	{
		
		public static const VIRTUAL_BOX:int = 1;
		public static const KVM:int = 2;
		public static const XEN:int = 3;
		public static const VMX:int = 4;
		public static const HYPERV:int = 5;
		public static const XENSERVER:int = 6;
		
		public static const HYPERVISOR_NAMES:Object = {(VIRTUAL_BOX.toString()):"virtualBox", 
													   (KVM.toString()):"KVM", 
													   (XEN.toString()):"XEN", 
													   (VMX.toString()):"ESXi", 
													   (HYPERV.toString()):"Hyper-V",
													(XENSERVER.toString()):"XenServer"};
		
		/* ------------- Public atributes ------------- */
		public var id:int;
		public var name:String;
		public var defaultPort:int;
		public var baseFormat:DiskFormatType;
		
		/* ------------- Constructor ------------- */
		public function HyperVisorType()
		{
			id = 0;
			//name = "";
			defaultPort = 0;
			baseFormat = null;
		}

	}
}
