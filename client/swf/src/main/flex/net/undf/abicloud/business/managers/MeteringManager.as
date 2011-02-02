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

package net.undf.abicloud.business.managers
{
    import flash.events.Event;
    import flash.events.EventDispatcher;

    import mx.collections.ArrayCollection;

    [Bindable]
    public class MeteringManager extends EventDispatcher
    {
        public function MeteringManager()
        {

        }


        /**
         * Contains a list of Events
         */
        private var _eventsFiltered:ArrayCollection = new ArrayCollection();

        [Bindable(event="eventsFilteredChange")]
        public function get eventsFiltered():ArrayCollection
        {
            return this._eventsFiltered;
        }

        public function set eventsFiltered(value:ArrayCollection):void
        {
            this._eventsFiltered = value;
            dispatchEvent(new Event("eventsFilteredChange"));
        }

        /**
         * List of all possible event types
         */
        private var _eventTypes:ArrayCollection = new ArrayCollection();

        [Bindable(event="eventTypesChange")]
        public function get eventTypes():ArrayCollection
        {
            return this._eventTypes;
        }

        public function set eventTypes(value:ArrayCollection):void
        {
            this._eventTypes = value;
            dispatchEvent(new Event("eventTypesChange"));
        }

        /**
         * List of all possible severity types for an Event
         */
        private var _severityTypes:ArrayCollection = new ArrayCollection();

        [Bindable(event="severityTypesChange")]
        public function get severityTypes():ArrayCollection
        {
            return this._severityTypes;
        }

        public function set severityTypes(value:ArrayCollection):void
        {
            this._severityTypes = value;
            dispatchEvent(new Event("severityTypesChange"));
        }

    }
}