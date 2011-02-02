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
    import flash.events.Event;

    public class ConnectionEvent extends Event
    {

        public static const REGISTER_CONNECTOR:String = "registerConnectorConnectionEvent";

        public static const UNREGISTER_CONNECTOR:String = "unregisterConnectorConnectionEvent";

        public static const CONNECTOR_DELETED:String = "connectorDeletedConnectionEvent";

        public static const CONNECTOR_MOVED:String = "connectorMovedConnectionEvent";

        public static const BEGIN_CONNECTION:String = "beginConnectionConnectionEvent";

        public static const ACCEPT_CONNECTION:String = "acceptConnectionConnectionEvent"

        public static const CONNECTION_DELETED:String = "connectionDeletedConnectionEvent";

        public var connector:Connector;

        public var connection:Connection;

        public function ConnectionEvent(type:String, bubbles:Boolean = false, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}