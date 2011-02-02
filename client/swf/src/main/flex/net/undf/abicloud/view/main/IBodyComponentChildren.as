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

package net.undf.abicloud.view.main
{

    /**
     * An interface that must implement any IBodyComponent direct children
     * @author Oliver
     *
     */
    public interface IBodyComponentChildren
    {

        /**
         * Cleans and reset the view from any user interaction. The view must be left as if it had just created
         */
        function cleanView():void;

        /**
         * Prepares this component to be used by the User. Before calling this function, the IBodyComponent which is parent
         * of this IBodyComponentChildren, must set all necessary data.
         *
         * Background updates can be started here, and necessary data from the server can be retrieved.
         */
        function willShow():void;

        /**
         * Prepares this IBodyComponentChild to be hidden from the view. This means that any background update must be stopped, or
         * any listener that may interfere with other component must be removed.
         */
        function willHide():void;
    }
}