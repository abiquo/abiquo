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
	import net.undf.abicloud.controller.ThemeHandler;
	
	
    /**
     * Class that collects some utils frequently used across the Abicloud application
     * @author Oliver
     *
     */
    public class AbicloudUtils
    {

        /**
         * Given an Array with a collection of objects of the same class, looks for the position
         * of the object that contains a given value in the given field
         * @param array The array where the search will be performed. All objects inside the Array must
         * have the valueField attribute
         * @param valueField The array objects attribute to compare
         * @param object An object that also has a valueField attribute, that contains the value that we
         * are looking for
         * @return The position of the value if found, or -1 otherwise
         *
         */
        public static function getValuePositionInArray(array:Object, valueField:String,
                                                       object:Object):int
        {
            if (object)
            {
                var length:int = array.length;
                var i:int;
                for (i = 0; i < length; i++)
                {
                    if (array[i][valueField] == object[valueField])
                        return i;
                }
            }

            return -1;
        }

		/**
		 * Retrieves the image source from the given css selector. The CSS must be loaded  
		 * @param CSSSelector CSS Selector from wich the source will be extracted
		 * @param c the control to set the source to
		 * 
		 */
		public static function getImageFromStyle(CSSSelector:String, c:* = null):void{
			
			ThemeHandler.getInstance().getImageFromStyle(CSSSelector,c);

		}
		
		/**
		 * Format a string into an MAC address format  
		 * @param macAddress a date object
		 * 
		 */
		public static function formatMac(macAddress:String):String{
			
			if(macAddress){
				//This is a non formated MAC
				if(macAddress.length != 17){
					return macAddress.slice(0,2)
					+":"+macAddress.slice(2,4)
					+":"+macAddress.slice(4,6)
					+":"+macAddress.slice(6,8)
					+":"+macAddress.slice(8,10)
					+":"+macAddress.slice(10,12);
				}else{
					return macAddress;
				}
			}else{
				return "-";
			}
			
		}
    }
}