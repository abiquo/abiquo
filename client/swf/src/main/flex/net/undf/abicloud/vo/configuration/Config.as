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

package net.undf.abicloud.vo.configuration
{
	import mx.utils.ObjectProxy;
	
	import net.undf.abicloud.vo.systemproperties.SystemProperty;

	[Bindable]
	public dynamic class Config extends ObjectProxy
	{
		public var defaultTheme:String = "abicloudDefault";
		private var _defaultHeaderLogo:String;
		
		//in case of everything else fails
		public var forcedDefaultEnterpriseLogoPath:String = "themes/" + defaultTheme + "/logo.png";
		
		//from themes.properties
		public var hardDefaultTheme:String;
		public var hardDefaultLogo:String;
		
		public var channels:*;
		public var currentUrl:*;
		public var selectedLanguage:*;

		//xml config vars
		public var USE_SECURE_CHANNEL_LOGIN:*;
		public var SERVER_ADDRESS:*;
		public var SERVER_PORT:*;
		public var SHOW_START_UP_ALERT:*;
		public var ALLOW_USERS_ACCESS:*; 

		public var client_applibrary_ovfpackagesDownloadingProgressUpdateInterval:SystemProperty;
		public var client_applibrary_virtualimageUploadProgressUpdateInterval:SystemProperty;
		public var client_dashboard_abiquoURL:SystemProperty;
		public var client_dashboard_allowUsersAccess:SystemProperty;
		public var client_dashboard_showStartUpAlert:SystemProperty;
		public var client_infra_googleMapsDefaultLatitude:SystemProperty;
		public var client_infra_googleMapsDefaultLongitude:SystemProperty;
		public var client_infra_googleMapsDefaultZoom:SystemProperty;
		public var client_infra_googleMapskey:SystemProperty;
		public var client_infra_googleMapsLadTimeOut:SystemProperty;
		public var client_infra_InfrastructureUpdateInterval:SystemProperty;
		public var client_metering_meteringUpdateInterval:SystemProperty;
		public var client_network_numberIpAdressesPerPage:SystemProperty;
		public var client_theme_defaultEnterpriseLogoPath:SystemProperty;
		public var client_user_numberEnterprisesPerPage:SystemProperty;
		public var client_user_numberUsersPerPage:SystemProperty;
		public var client_virtual_allowVMRemoteAccess:SystemProperty;
		public var client_virtual_virtualApplianceDeployingUpdateInterval:SystemProperty;
		public var client_virtual_virtualAppliancesUpdateInterval:SystemProperty;
		public var client_dashboard_dashboardUpdateInterval:SystemProperty;
		public var client_infra_defaultHypervisorPassword:SystemProperty;
		public var client_infra_defaultHypervisorPort:SystemProperty;
		public var client_infra_defaultHypervisorUser:SystemProperty;
		public var client_storage_volumeMaxSizeValues:SystemProperty;
		public var client_virtual_moreInfoAboutUploadLimitations:SystemProperty;
		public var client_infra_vlanIdMin:SystemProperty;
		public var client_infra_vlanIdMax:SystemProperty;
		public var client_virtual_virtualImagesRefreshConversionsInterval:SystemProperty; 
		public var client_main_enterpriseLogoURL:SystemProperty;
		public var client_main_billingUrl:SystemProperty;
		public var client_logout_url:SystemProperty;
		
		//wiki links
		public var client_wiki_showHelp:SystemProperty;
		public var client_wiki_showDefaultHelp:SystemProperty;
		public var client_wiki_defaultURL:SystemProperty;
		
		public var client_wiki_infra_createDatacenter:SystemProperty;
		public var client_wiki_infra_editDatacenter:SystemProperty;
		public var client_wiki_infra_editRemoteService:SystemProperty;
		public var client_wiki_infra_createPhysicalMachine:SystemProperty;
		public var client_wiki_infra_mailNotification:SystemProperty;
		public var client_wiki_infra_addDatastore:SystemProperty;
		public var client_wiki_infra_createMultiplePhysicalMachine:SystemProperty;
		public var client_wiki_infra_createRack:SystemProperty;
		public var client_wiki_infra_discoverBlades:SystemProperty;
		
		public var client_wiki_network_publicVlan:SystemProperty;
		
		public var client_wiki_storage_storageDevice:SystemProperty;
		public var client_wiki_storage_storagePool:SystemProperty;
		public var client_wiki_storage_tier:SystemProperty;
		
		public var client_wiki_allocation_global:SystemProperty;
		public var client_wiki_allocation_datacenter:SystemProperty;	
		
		public var client_wiki_vdc_createVdc:SystemProperty;
		public var client_wiki_vdc_createVapp:SystemProperty;
		public var client_wiki_vdc_createPrivateNetwork:SystemProperty;
		public var client_wiki_vdc_createPublicNetwork:SystemProperty;
		public var client_wiki_vdc_createVolume:SystemProperty;
		
		public var client_wiki_vm_editVirtualMachine:SystemProperty;
		public var client_wiki_vm_bundleVirtualMachine:SystemProperty;		
		public var client_wiki_vm_createNetworkInterface:SystemProperty;
		public var client_wiki_vm_createInstance:SystemProperty;
		public var client_wiki_vm_createStateful:SystemProperty;
		public var client_wiki_vm_captureVirtualMachine:SystemProperty;
		public var client_wiki_vm_deployInfo:SystemProperty;
		
		public var client_wiki_apps_uploadVM:SystemProperty;
		
		public var client_wiki_user_createEnterprise:SystemProperty;
		public var client_wiki_user_dataCenterLimits:SystemProperty;
		public var client_wiki_user_createUser:SystemProperty;
		public var client_wiki_user_createRole:SystemProperty;
		
		public var client_wiki_config_general:SystemProperty;
		public var client_wiki_config_heartbeat:SystemProperty;
		public var client_wiki_config_registration:SystemProperty;	
		public var client_wiki_config_licence:SystemProperty;	
		
		public var client_wiki_pricing_createTemplate:SystemProperty;
		public var client_wiki_pricing_createCostCode:SystemProperty;	
		public var client_wiki_pricing_createCurrency:SystemProperty;
		

		private var propertiesIndex:Array;

		public function Config(item:Object=null, uid:String=null, proxyDepth:int=-1)
		{
			super(item, uid, proxyDepth);
		}
		
		public function set defaultHeaderLogo(v:String):void{
			_defaultHeaderLogo = v;
		}
		
		public function get defaultHeaderLogo():String{
			var t:SystemProperty=client_theme_defaultEnterpriseLogoPath;
			
			if(client_theme_defaultEnterpriseLogoPath != null && client_theme_defaultEnterpriseLogoPath.value != ""){
				return client_theme_defaultEnterpriseLogoPath.value;
			}else{
				return forcedDefaultEnterpriseLogoPath;
			}
		}
		
		public function toArray():Array{
			var tmp:Array = new Array();

			for(var it:* in propertiesIndex){
				
				if(propertiesIndex[it].name != null){
					tmp.push(propertiesIndex[it]);
				}
			}

			return tmp;
		}
		
		public function addProperty(p:SystemProperty):void{

			if( propertiesIndex == null) propertiesIndex = new Array();
			
			var propName:String = p.name.split(".").join("_");
			
			this[propName] = p;

			propertiesIndex[propName] = this[propName]; 

		}
	}
}