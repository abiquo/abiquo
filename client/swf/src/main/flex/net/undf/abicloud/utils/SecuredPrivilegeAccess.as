package net.undf.abicloud.utils
{
	import mx.core.UIComponent;
	
	import net.undf.abicloud.model.AbiCloudModel;
	
	public class SecuredPrivilegeAccess
	{	
		public static function checkElementAccess(privilege:String, property:String, element:UIComponent):void{
				
			element[property] = AbiCloudModel.getInstance().userManager.userHasPrivilege(privilege);
			
		}
	}
}