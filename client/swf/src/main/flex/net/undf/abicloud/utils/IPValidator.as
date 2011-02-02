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
 
package net.undf.abicloud.utils
{
	import mx.resources.ResourceManager;
	import mx.validators.ValidationResult;
	import mx.validators.Validator;

	[Bindable]
	public class IPValidator extends Validator
	{
		// Define Array for the return value of doValidation().
        private var results:Array;
        
        private var _errorMessage:String;
        
        public var allowLocalHost:Boolean;
        
		public function IPValidator()
		{
			//TODO: implement function
			super();			
			errorMessage = ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_INVALID_URI');
			allowLocalHost = false;
		}
		
		public function set errorMessage(message:String):void{
			this._errorMessage = message;
		}
		
		public function get errorMessage():String{
			return this._errorMessage;
		}
		
		// Define the doValidation() method.
        override protected function doValidation(value:Object):Array {
            // Clear results Array.
            results = [];
            // Call base class doValidation().
            results = super.doValidation(value);        
            // Return if there are errors.
            if (results.length > 0)
                return results;
                
            if (String(value).length == 0){
            	results.push(new ValidationResult(true, null, "NaN",ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_REQUIRED_IP')));
		    	errorMessage = ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_REQUIRED_IP');
		     	return results;
            }
        
            var pattern:RegExp = /\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b/;
		    var r:Array = pattern.exec(String(value));
		    
		    if(!allowLocalHost){
			    if(String(value) == "127.0.0.1"){
			    	results.push(new ValidationResult(true, null, "NaN",ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_NOT_ALLOWED_IP')));
			    	errorMessage = ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_NOT_ALLOWED_IP');
			     	return results;
			    }
		    }
		    
		    if (r == null){
		    	results.push(new ValidationResult(true, null, "NaN",ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_VALID_IP')));
		     	errorMessage = ResourceManager.getInstance().getString('Infrastructure','TOOLTIP_VALID_IP');
		     	return results;
		    }
	       return results;
	    }				
	}
}