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

package net.undf.abicloud.business.managers.notification
{
    import flash.events.Event;
    import flash.events.EventDispatcher;

    /**
     * A Notification is a message sent by server, that is not instantly showed to the user.
     * @author Oliver
     *
     */
    public class Notification extends EventDispatcher
    {

        /**
         * Constructor for Notification class
         * @param message The message that this Notification will contain
         * @param date The Date when this Notification was created. If null
         * 				current system Date will be taken
         * @param read Flag that indicates if this Notification is marked as read
         *
         */
        public function Notification(title:String = "", message:String = "", date:Date = null,
                                     read:Boolean = false)
        {
            this._title = title;
            this._message = message;

            if (date)
                this._date = date;
            else
                this._date = new Date();

            this._read = read;
        }

        private var _title:String;

        /**
         * The title for this Notification
         */
        [Bindable(event="titleUpdated")]
        public function get title():String
        {
            return this._title;
        }

        public function set title(value:String):void
        {
            this._title = value;
            dispatchEvent(new Event("titleUpdated"));
        }


        private var _message:String;

        /**
         * The message that contains this Notification
         */
        [Bindable(event="messageUpdated")]
        public function get message():String
        {
            return this._message;
        }

        public function set message(value:String):void
        {
            this._message = value;
            dispatchEvent(new Event("messageUpdated"));
        }


        private var _date:Date;

        /**
         * The Date when this Notification has been created
         */
        [Bindable(event="dateUpdated")]
        public function get date():Date
        {
            return this._date;
        }

        public function set date(value:Date):void
        {
            this._date = value;
            dispatchEvent(new Event("dateUpdated"));
        }

        private var _read:Boolean;

        /**
         * Flag that indicates if this Notification has been read
         */
        [Bindable(event="readUpdated")]
        public function get read():Boolean
        {
            return this._read;
        }

        public function set read(value:Boolean):void
        {
            this._read = value;
            dispatchEvent(new Event("readUpdated"));
        }
    }
}