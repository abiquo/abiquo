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

package net.undf.abicloud.view.virtualappliance.components.node.connection
{
    import flash.events.EventDispatcher;
    import flash.geom.Point;

    public class Connector extends EventDispatcher
    {
        public function Connector(position:Point, data:Object = null)
        {
            this._position = position;
            this._data = data;
        }

        /**
         * The position of a Connector
         *
         * This value must be a valid position in the Connection Surface
         */
        private var _position:Point;

        public function get position():Point
        {
            return this._position;
        }

        public function set position(value:Point):void
        {
            this._position = value;

            var connectionEvent:ConnectionEvent = new ConnectionEvent(ConnectionEvent.CONNECTOR_MOVED);
            connectionEvent.connector = this;
            dispatchEvent(connectionEvent);
        }

        /**
         * The data associated to this Connector
         */
        private var _data:Object;

        public function get data():Object
        {
            return this._data;
        }

        public function set data(value:Object):void
        {
            this._data = value;
        }
    }
}