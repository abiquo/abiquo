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

package net.undf.abicloud.controller.systemProperties
{
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import net.undf.abicloud.controller.ResultHandler;
	import net.undf.abicloud.model.AbiCloudModel;
	import net.undf.abicloud.vo.result.BasicResult;
	import net.undf.abicloud.vo.result.DataResult;
	import net.undf.abicloud.vo.systemproperties.SystemProperty;

	public class SystemPropertiesResultHandler extends ResultHandler
	{
		public function SystemPropertiesResultHandler()
		{
			super();
		}

		public function sysPropsError(evt:*):void{
			Alert.show("Error retrieving/saving properties");
		}

        public function handleGetSystemProperties(result:BasicResult):void
        {
            if (result.success)
            {
                // Data is an array of net.undf.abicloud.vo.main.SystemProperty objects
                var properties:ArrayCollection = DataResult(result).data as ArrayCollection;
            
                for (var i:int = 0; i < properties.length; i++)
                {
                    AbiCloudModel.getInstance().configurationManager.config.addProperty(new SystemProperty(properties[i]) );              
                }
                
                AbiCloudModel.getInstance().configurationManager.reportSystemPropertiesLoaded();
            }
            else
            {
                //There was a problem retrieving the common information
                super.handleResult(result);
            }
        }
        
        
        public function handleSetSystemProperties(result:BasicResult):void
        {
            if (result.success)
            {
				AbiCloudModel.getInstance().configurationManager.config.updateProperties(DataResult(result).data as ArrayCollection);
            }
            else
            {
                //There was a problem retrieving the common information
                super.handleResult(result);
            }
        }

	}
}