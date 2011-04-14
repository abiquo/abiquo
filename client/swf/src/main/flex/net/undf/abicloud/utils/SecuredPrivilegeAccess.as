package net.undf.abicloud.utils
{
	import mx.core.UIComponent;
	
	import net.undf.abicloud.model.AbiCloudModel;
	
	public class SecuredPrivilegeAccess
	{	
		public static function checkElementAccess(privilege:String, property:String, element:UIComponent, parent:UIComponent = null):void{
			
			switch(property){
				case "removeChild":
					if(!SecuredPrivilegeAccess.userHasPrivilege(privilege)){
						parent.removeChild(element);
					}
					break;
				case "condition":
					parent.removeChild(element);
					break;
				default:
					element[property] = SecuredPrivilegeAccess.userHasPrivilege(privilege);
					break;
			}
		}
		
		public static function userHasPrivilege(privilege:String):Boolean{
			return AbiCloudModel.getInstance().userManager.userHasPrivilege(privilege);
		}
	}
}