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
    import flash.events.EventDispatcher;

    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.InfrastructureElement")]
    public class InfrastructureElement
    {
        /* ------------- Public atributes ------------- */
        public var id:int;

        protected var _name:String;

        //The Infrastructure Element which this infrastructure element is assigned to
        protected var _assignedTo:InfrastructureElement;

        /* ------------- Constructor ------------- */
        public function InfrastructureElement()
        {
            id = 0;
            name = "";
            _assignedTo = null;
        }

        public function isAssigned():Boolean
        {
            if (assignedTo == null)
                return false;
            else
                return true;
        }

        //Removes the Infrastructure Element to which this infrastructure element is assigned
        public function unassign():void
        {
            assignedTo = null;
        }

        /**
         * Assigns this infrastructure element to another infrastructure element
         * This function should be overrided if this infrastructure element can only be assigned to
         * a certain infrastructure element type
         **/
        public function set assignedTo(iE:InfrastructureElement):void
        {
            _assignedTo = iE;
        }

        public function get assignedTo():InfrastructureElement
        {
            return _assignedTo;
        }
        
        //Assign the InfrastructureElement name
        public function set name(value:String):void
        {
            _name = value;
        }

        public function get name():String
        {
            return _name;
        }
    }
}