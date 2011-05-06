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

package net.undf.abicloud.view.main
{
	import mx.styles.StyleManager;
	
	public class CommonAssets
	{
		[Bindable]
		public static var magnifier:Object;
		
		[Bindable]
		public static var info:Object;

		[Bindable]
		public static var alert:Object;

		[Bindable]
		public static var checked:Object;
		
		[Bindable]
		public static var headerLogo:Object;

		[Bindable]
		public static var loginLogo:Object;

		[Bindable]
		public static var alertIconOk:Object;

		[Bindable]
		public static var alertIconAlert:Object;
		
		[Bindable]
		public static var crashedIcon:Object;
		
		[Bindable]
		public static var unknownIcon:Object;

		[Bindable]
		public static var alertIconError:Object;

		[Bindable]
		public static var genericIdle:Object;
		
		[Bindable]
		public static var genericOK:Object;
		
		[Bindable]
		public static var genericKO:Object;
		
		[Bindable]
		public static var genericShared:Object;		

		[Bindable]
		public static var genericLoadingSmall:Object;

		[Bindable]
		public static var cursorLineTool:Object;

		[Bindable]
		public static var cursorMoveTool:Object;

		[Bindable]
		public static var cursorScissorsTool:Object;

		[Bindable]
		public static var postLoginBackground:Object;
		
		[Bindable]
		public static var physicalMachineSmallIcon:Object;

		[Bindable]
		public static var virtualApplianceDrawingArea:Object;

		[Bindable]
		public static var openBranchArrow:*;
		
		[Bindable]
		public static var closeBranchArrow:*;

		//vertical separator 3x20
		[Bindable]
		public static var separator:*;

		//vertical separator 2x19
		[Bindable]
		public static var headerSpacer:*;

		[Bindable]
		public static var dashboardMenuInfrastructure:*;
		[Bindable]
		public static var dashboardMenuMetering:*;
		[Bindable]
		public static var dashboardMenuVirtualApplications:*;
		[Bindable]
		public static var dashboardMenuUsers:*;
		[Bindable]
		public static var dashboardMenuConfiguration:*;
		[Bindable]
		public static var dashboardMenuStatistics:*;
		[Bindable]
		public static var dashboardMenuVirtualImages:*;

		private static var errors:Array;
		
		public function CommonAssets(){
		}

		public static function refresh():void{
			errors = new Array();
			
			CommonAssets.magnifier = getImage("CommonAssetsMagnifier");
			CommonAssets.info = getImage("CommonAssetsInfo");
			CommonAssets.alert = getImage("CommonAssetsAlert");
			CommonAssets.checked = getImage("CommonAssetsChecked");
			CommonAssets.alertIconOk = getImage("CommonAssetsAlertIconOk");
			CommonAssets.alertIconAlert = getImage("CommonAssetsAlertIconAlert");
			CommonAssets.alertIconError = getImage("CommonAssetsAlertIconError");
			
			CommonAssets.cursorLineTool = getImage("CommonAssetsCursorLineTool");
			CommonAssets.cursorMoveTool = getImage("CommonAssetsCursorMoveTool");
			CommonAssets.cursorScissorsTool = getImage("CommonAssetsCursorScissorsTool");

			CommonAssets.postLoginBackground = getImage("CommonAssetsPostLoginBackground");
			
			CommonAssets.genericOK = getImage("genericOK");
			CommonAssets.genericKO = getImage("genericKO");
			CommonAssets.genericShared = getImage("genericShared");
			CommonAssets.genericIdle = getImage("genericIdle");
			
			CommonAssets.alertIconError = getImage("genericLoadingSmall");
			CommonAssets.crashedIcon = getImage("crashedIcon");
			CommonAssets.unknownIcon = getImage("unknownIcon");

			CommonAssets.physicalMachineSmallIcon = getImage("physicalMachineSmallIcon");
			CommonAssets.virtualApplianceDrawingArea = getImage("virtualApplianceDrawingArea");

			CommonAssets.headerLogo = getImage("headerLogo");
			CommonAssets.loginLogo = getImage("loginLogo");
			
			CommonAssets.openBranchArrow = getImage("openBranchArrow");
			CommonAssets.closeBranchArrow = getImage("closeBranchArrow");
			
			CommonAssets.separator = getImage("CommonAssetsSeparator");
			CommonAssets.headerSpacer = getImage("CommonAssetsHeaderSpacer");

			CommonAssets.dashboardMenuInfrastructure = getImage("dashboardMenuInfrastructure");
			CommonAssets.dashboardMenuMetering = getImage("dashboardMenuMetering");
			CommonAssets.dashboardMenuVirtualApplications = getImage("dashboardMenuVirtualApplications");
			CommonAssets.dashboardMenuUsers = getImage("dashboardMenuUsers");
			CommonAssets.dashboardMenuVirtualImages = getImage("dashboardMenuVirtualImages");
			CommonAssets.dashboardMenuConfiguration = getImage("dashboardMenuConfiguration");
			CommonAssets.dashboardMenuStatistics = getImage("dashboardMenuStatistics");

			if(errors.length > 0 ){
				trace( "[CommonAssets] Image not found " + errors.join("\r") ) ; 
			}
		}
		
		
		private static function getImage(CSSSelector:String):*{
			try {
				return StyleManager.getStyleDeclaration("." + CSSSelector).getStyle("source")
			}catch(e:Error){
//				Alert.show( "[CommonAssets] Image not found " + CSSSelector);
				trace("[CommonAssets] Image not found " + CSSSelector);
				errors.push("[CommonAssets] Image not found " + CSSSelector);
				return null;
			}		
		} 
	}
}

