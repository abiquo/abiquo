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
     * Interface to be implemented by any component inside the Main.mxml body viewstack
     * @author Oliver
     *
     */
    public interface IBodyComponent
    {

        /**
         * When this function is called, means that this IBodyComponent will be removed from the view because User has selected
         * another application's IBodyComponent.
         * The IBodyComponent must save or ask for unsaved changes to the user, ensure that it or its
         * IBodyComponentChildren has no background updates, or are actively listening
         * events from model that can affect the IBodyComponent that will be used next.
         * Is also a good practice that the IBodyComponent remains as clean as possible, so memory consumption is reduced.
         *
         * The IBodyComponent MUST dispatch the MainEvent.IBODYCOMPONENT_READY_TO_BE_CHANGED event, so the Main can change to
         * the next IBodyComponent
         */
        function bodyComponentWillChange():void;

        /**
         * Whent his function is called, this IBodyComponent will be shown in the main area.
         * The IBodyComponent must ensure that its IBodyComponentChildren's view is clean as first time, and ready to be used.
         */
        function bodyComponentWillShow():void;
    }
}