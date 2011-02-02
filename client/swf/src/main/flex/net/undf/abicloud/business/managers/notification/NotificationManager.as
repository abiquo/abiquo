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

    import mx.collections.ArrayCollection;

    /**
     * This class manages all application's background notifications
     * @author Oliver
     *
     */
    public class NotificationManager extends EventDispatcher
    {
        public function NotificationManager()
        {
            this._notifications = new ArrayCollection();
            this._numUnreadNotifications = 0;
        }

        /**
         * @private
         */
        private var _isServerUnreachable:Boolean = false;

        /**
         * Flag indicating that currently there are no connection to server
         *
         */
        [Bindable(event="isServerUnreachableChange")]
        public function get isServerUnreachable():Boolean
        {
            return this._isServerUnreachable;
        }

        public function set isServerUnreachable(value:Boolean):void
        {
            this._isServerUnreachable = value;
            dispatchEvent(new Event("isServerUnreachableChange"));
        }

        /**
         * @private
         */
        private var _notifications:ArrayCollection;

        /**
         * Array containing all unread notifications
         */
        [Bindable(event="notificationsUpdated")]
        public function get notifications():ArrayCollection
        {
            return this._notifications;
        }

        /**
         * @private
         */
        private var _numUnreadNotifications:int;

        /**
         * Number of unread notifications that this NotificationManager has
         */
        [Bindable(event="numUnreadNotifications")]
        public function get numUnreadNotifications():int
        {
            return this._numUnreadNotifications;
        }

        /**
         * Adds a new Notification to this NotificationManager
         * @param notification The Notification to be added
         *
         */
        public function addNotification(notification:Notification):void
        {
            this._notifications.addItem(notification);
            dispatchEvent(new Event("notificationsUpdated"));

            if (!notification.read)
            {
                this._numUnreadNotifications++;
                dispatchEvent(new Event("numUnreadNotifications"));
            }
        }

        /**
         * Marks a Notification as read
         * @param notification The Notification to be marked
         *
         */
        public function markNotificationAsRead(notification:Notification):void
        {
            if (!notification.read)
            {
                notification.read = true;
                this._numUnreadNotifications--;
                dispatchEvent(new Event("numUnreadNotifications"));
            }
        }

        /**
         * Clears the Notification's array from all notifications that this
         * NotificationManager contains. The number of unread notifications
         * will be set to 0
         *
         */
        public function clearAllNotifications():void
        {
            this._notifications.removeAll();
            this._numUnreadNotifications = 0;

            dispatchEvent(new Event("notificationsUpdated"));
            dispatchEvent(new Event("numUnreadNotifications"));

        }

        /**
         * Clears a given notification from the notifications array
         * @param notification The notification to be removed from the notifications array
         *
         */
        public function clearNotification(notification:Notification):void
        {
            var index:int = this._notifications.getItemIndex(notification);
            if (index > -1)
                this._notifications.removeItemAt(index);
        }
    }
}