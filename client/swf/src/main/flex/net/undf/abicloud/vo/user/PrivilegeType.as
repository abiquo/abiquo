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

package net.undf.abicloud.vo.user
{
	//[RemoteClass(alias="com.abiquo.abiserver.pojo.virtualstorage.InitiatorMapping")]
    [Bindable]
	public class PrivilegeType
	{
		
		//HOME
		public static const ENTERPRISE_ENUMERATE:String = "ENTERPRISE_ENUMERATE";
		public static const ENTERPRISE_ADMINISTER_ALL:String = "ENTERPRISE_ADMINISTER_ALL";
		public static const ENTERPRISE_RESOURCE_SUMMARY_ENT:String = "ENTERPRISE_RESOURCE_SUMMARY_ENT";
		//INFRASTRUCTURE
		public static const PHYS_DC_ENUMERATE:String = "PHYS_DC_ENUMERATE";
		public static const PHYS_DC_RETRIEVE_RESOURCE_USAGE:String = "PHYS_DC_RETRIEVE_RESOURCE_USAGE";
		public static const PHYS_DC_MANAGE:String = "PHYS_DC_MANAGE";
		public static const PHYS_DC_RETRIEVE_DETAILS:String = "PHYS_DC_RETRIEVE_DETAILS";
		public static const PHYS_DC_ALLOW_MODIFY_SERVERS:String = "PHYS_DC_ALLOW_MODIFY_SERVERS";
		public static const PHYS_DC_ALLOW_MODIFY_NETWORK:String = "PHYS_DC_ALLOW_MODIFY_NETWORK";
		public static const PHYS_DC_ALLOW_MODIFY_STORAGE:String = "PHYS_DC_ALLOW_MODIFY_STORAGE";
		public static const PHYS_DC_ALLOW_MODIFY_ALLOCATION:String = "PHYS_DC_ALLOW_MODIFY_ALLOCATION";
		//VIRTUAL DATACENTERS
		public static const VDC_ENUMERATE:String = "VDC_ENUMERATE";
		public static const VDC_MANAGE:String = "VDC_MANAGE";
		public static const VDC_MANAGE_VAPP:String = "VDC_MANAGE_VAPP";
		public static const VDC_MANAGE_NETWORK:String = "VDC_MANAGE_NETWORK";
		public static const VDC_MANAGE_STORAGE:String = "VDC_MANAGE_STORAGE";
		public static const VAPP_CUSTOMISE_SETTINGS:String = "VAPP_CUSTOMISE_SETTINGS";
		public static const VAPP_DEPLOY_UNDEPLOY:String = "VAPP_DEPLOY_UNDEPLOY";
		public static const VAPP_ASSIGN_NETWORK:String = "VAPP_ASSIGN_NETWORK";
		public static const VAPP_ASSIGN_VOLUME:String = "VAPP_ASSIGN_VOLUME";
		public static const VAPP_PERFORM_ACTIONS:String = "VAPP_PERFORM_ACTIONS";
		public static const VAPP_CREATE_STATEFUL:String = "VAPP_CREATE_STATEFUL";
		public static const VAPP_CREATE_INSTANCE:String = "VAPP_CREATE_INSTANCE";
		//APPS LIBRARY
		public static const APPLIB_VIEW:String = "APPLIB_VIEW";
		public static const APPLIB_ALLOW_MODIFY:String = "APPLIB_ALLOW_MODIFY";
		public static const APPLIB_VM_COST_CODE:String = "APPLIB_VM_COST_CODE";
		public static const APPLIB_UPLOAD_IMAGE:String = "APPLIB_UPLOAD_IMAGE";
		public static const APPLIB_MANAGE_REPOSITORY:String = "APPLIB_MANAGE_REPOSITORY";
		public static const APPLIB_DOWNLOAD_IMAGE:String = "APPLIB_DOWNLOAD_IMAGE";
		public static const APPLIB_MANAGE_CATEGORIES:String = "APPLIB_MANAGE_CATEGORIES";
		//USERS
		public static const USERS_VIEW:String = "USERS_VIEW";
		public static const USERS_MANAGE_ENTERPRISE:String = "USERS_MANAGE_ENTERPRISE";
		public static const USERS_MANAGE_ENTERPRISE_BRANDING:String = "USERS_MANAGE_ENTERPRISE_BRANDING";
		public static const USERS_MANAGE_USERS:String = "USERS_MANAGE_USERS";
		public static const USERS_MANAGE_OTHER_ENTERPRISES:String = "USERS_MANAGE_OTHER_ENTERPRISES";
		public static const USERS_PROHIBIT_VDC_RESTRICTION:String = "USERS_PROHIBIT_VDC_RESTRICTION";
		public static const USERS_VIEW_PRIVILEGES:String = "USERS_VIEW_PRIVILEGES";
		public static const USERS_MANAGE_ROLES:String = "USERS_MANAGE_ROLES";
		public static const USERS_MANAGE_ROLES_OTHER_ENTERPRISES:String = "USERS_MANAGE_ROLES_OTHER_ENTERPRISES";
		public static const USERS_MANAGE_SYSTEM_ROLES:String = "USERS_MANAGE_SYSTEM_ROLES";
		public static const USERS_MANAGE_LDAP_GROUP:String = "USERS_MANAGE_LDAP_GROUP";
		public static const USERS_ENUMERATE_CONNECTED:String = "USERS_ENUMERATE_CONNECTED";
		public static const USERS_DEFINE_AS_MANAGER:String = "USERS_DEFINE_AS_MANAGER";
		//PRICING
		public static const PRICING_VIEW:String = "PRICING_VIEW";
		public static const PRICING_MANAGE:String = "PRICING_MANAGE";
		//SYSTEM CONFIG
		public static const SYSCONFIG_VIEW:String = "SYSCONFIG_VIEW";
		public static const SYSCONFIG_ALLOW_MODIFY:String = "SYSCONFIG_ALLOW_MODIFY";
		public static const SYSCONFIG_SHOW_REPORTS:String = "SYSCONFIG_SHOW_REPORTS";
		//SYSTEM CONFIG
		public static const EVENTLOG_VIEW_ENTERPRISE:String = "EVENTLOG_VIEW_ENTERPRISE";
		public static const EVENTLOG_VIEW_ALL:String = "EVENTLOG_VIEW_ALL";

				
		public function PrivilegeType()
		{

		}

	}
}