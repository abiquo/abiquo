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

package net.undf.abicloud.vo.virtualhardware
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	public class ExceedLimit
	{
		public static const GB_TO_BYTES:Number = 1073741824;
        public static const MB_TO_BYTES:Number = 1048576;
        public static const KB_TO_BYTES:Number = 1024;
        public static const GB_TO_MBYTES:Number = 1024;
        
        public static const MBYTES:String = "MBYTES";
        public static const BYTES:String = "BYTES";
        public static const OK:String = "OK";
        public static const HARD_LIMIT:String = "HARD_LIMIT";
        public static const SOFT_LIMIT:String = "SOFT_LIMIT";
        
        public var cause:String;            
            
        public var usedCPU:String;     
        public var usedRAM:String;                   
        public var usedHD:String;        
        public var usedStorage:String;                    
        public var usedVLAN:String;                  
        public var usedIP:String;
               
        public var requiredCPU:String;                  
        public var requiredRAM:String;             
        public var requiredHD:String;        
        public var requiredStorage:String;                  
        public var requiredVLAN:String;            
        public var requiredIP:String;
              
        public var softCPU:String;            
        public var softRAM:String;                   
        public var softHD:String;        
        public var softStorage:String;                    
        public var softVLAN:String;            
        public var softIP:String;        
        
        public var hardCPU:String;                    
        public var hardRAM:String;                    
        public var hardHD:String;        
        public var hardStorage:String;                   
        public var hardVLAN:String;                   
        public var hardIP:String;
        
        public var statusCPU:String;                    
        public var statusRAM:String;                    
        public var statusHD:String;        
        public var statusStorage:String;                   
        public var statusVLAN:String;                   
        public var statusIP:String;  
        
        public var includeStorage:Boolean;
        public var includeVLAN:Boolean;
        public var includeIP:Boolean;
             
        
		public function ExceedLimit(serverObject:String)
		{
		   var lines:Array = serverObject.split(';');
        
           var used_value:Array = String(lines[1]).split('=');
           var required_value:Array = String(lines[2]).split('=');
           var soft_value:Array = String(lines[3]).split('=');
           var hard_value:Array = String(lines[4]).split('=');
           var status_value:Array = String(lines[5]).split('=');
        	 
           cause = String(lines[0]);
           usedCPU = String(used_value[1]).slice(0,String(used_value[1]).search(' '));
           usedRAM = convertValue(String(used_value[2]).slice(0,String(used_value[2]).search(' ')),ExceedLimit.MBYTES);
           usedHD = convertValue(String(used_value[3]).slice(0,String(used_value[3]).search(' ')),ExceedLimit.MBYTES);  
           usedStorage = convertValue(String(used_value[4]).slice(0,String(used_value[4]).search(' ')),ExceedLimit.MBYTES);
           usedVLAN = String(used_value[5]).slice(0,String(used_value[5]).search(' '));
           usedIP = String(used_value[6]);
                    
           requiredCPU = String(required_value[1]).slice(0,String(required_value[1]).search(' '));
           requiredRAM  = convertValue(String(required_value[2]).slice(0,String(required_value[2]).search(' ')),ExceedLimit.MBYTES);
           requiredHD= convertValue(String(required_value[3]).slice(0,String(required_value[3]).search(' ')),ExceedLimit.MBYTES);   
           requiredStorage = convertValue(String(required_value[4]).slice(0,String(required_value[4]).search(' ')),ExceedLimit.MBYTES);
           requiredVLAN  = String(required_value[5]).slice(0,String(required_value[5]).search(' '));
           requiredIP= String(required_value[6]); 
                    
           softCPU  = displayUnlimitedValue(String(soft_value[1]).slice(0,String(soft_value[1]).search(' ')));
           softRAM  = displayUnlimitedValue(String(soft_value[2]).slice(0,String(soft_value[2]).search(' ')),true,ExceedLimit.MBYTES); 
           softHD = displayUnlimitedValue(String(soft_value[3]).slice(0,String(soft_value[3]).search(' ')), true,ExceedLimit.MBYTES);    
           softStorage  = displayUnlimitedValue(String(soft_value[4]).slice(0,String(soft_value[4]).search(' ')), true,ExceedLimit.MBYTES);
           softVLAN  = displayUnlimitedValue(String(soft_value[5]).slice(0,String(soft_value[5]).search(' '))); 
           softIP = displayUnlimitedValue(String(soft_value[6]));
                   
           hardCPU  = displayUnlimitedValue(String(hard_value[1]).slice(0,String(hard_value[1]).search(' ')));
           hardRAM  = displayUnlimitedValue(String(hard_value[2]).slice(0,String(hard_value[2]).search(' ')), true,ExceedLimit.MBYTES); 
           hardHD = displayUnlimitedValue(String(hard_value[3]).slice(0,String(hard_value[3]).search(' ')), true,ExceedLimit.MBYTES);
           hardStorage  = displayUnlimitedValue(String(hard_value[4]).slice(0,String(hard_value[4]).search(' ')), true,ExceedLimit.MBYTES);
           hardVLAN  = displayUnlimitedValue(String(hard_value[5]).slice(0,String(hard_value[5]).search(' '))); 
           hardIP = displayUnlimitedValue(String(hard_value[6])); 
           
           statusCPU  = String(status_value[1]).slice(0,String(status_value[1]).search(' '));
           statusRAM  = String(status_value[2]).slice(0,String(status_value[2]).search(' ')); 
           statusHD = String(status_value[3]).slice(0,String(status_value[3]).search(' '));
           statusStorage  = String(status_value[4]).slice(0,String(status_value[4]).search(' '));
           statusVLAN  = String(status_value[5]).slice(0,String(status_value[5]).search(' ')); 
           statusIP = String(status_value[6]);
           
           includeStorage = updateInclude(statusStorage);   
           includeVLAN = updateInclude(statusVLAN);
           includeIP = updateInclude(statusIP);          
			
		}
		
		private function updateInclude(status:String):Boolean{
			if(status == 'null'){
				return false;
			}else{
				return true;
			}
			
		}
		
		private function convertValue(value:String , type:String = ExceedLimit.BYTES):String{			
			if(type == ExceedLimit.BYTES){
				if(Number(value) / ExceedLimit.GB_TO_BYTES < 1){
					return (Number(value) / ExceedLimit.MB_TO_BYTES).toFixed(2).toString() + 'MB';
				}else{
					return (Number(value) / ExceedLimit.GB_TO_BYTES).toFixed(2).toString() + 'GB';
				}
			}else{
				if(Number(value) / ExceedLimit.GB_TO_MBYTES < 1){
					return value + 'MB';
				}else{
					return (Number(value) / ExceedLimit.GB_TO_MBYTES).toFixed(2).toString() + 'GB';
				}
			}		
			
		}
		
		private function displayUnlimitedValue(value:String , conversion:Boolean = false , type:String = ExceedLimit.BYTES):String{
			//Check if we display the No Limit label
			if(value == "0"){
				return "No limit";
			}else if(!conversion){
				return value;
			}else{
				return convertValue(value,type);
			}				
		}
		
		public function limitExceeded(status:String):Boolean{
			if(status == ExceedLimit.HARD_LIMIT || status == ExceedLimit.SOFT_LIMIT){
				return true;
			}else{
				return false;
			}
			
		}
	}
}