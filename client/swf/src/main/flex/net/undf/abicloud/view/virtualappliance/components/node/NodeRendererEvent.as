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

package net.undf.abicloud.view.virtualappliance.components.node
{
    import flash.events.Event;

    /**
     * Event related to NodeRenderer operations
     **/
    public class NodeRendererEvent extends Event
    {
        public static const NODERENDERER_ERASE:String = "nodeRendererErase_NodeRendererEvent";

        public static const NODERENDERER_ADDED:String = "nodeRendererAdded_NodeRendererEvent";

        public static const NODERENDERER_ERASED:String = "NodeRendererErased_NodeRendererEvent";

        public static const NODERENDERER_CHANGED:String = "nodeRendererMoved_NodeRendererEvent";

        public static const NODERENDERER_SELECTED:String = "nodeRendererSelected_NodeRendererEvent";

        public static const NODERENDERER_ANY_SELECTED:String = "nodeRendererAnySelected_NodeRendererEvent";

        public static const NODERENDERER_CONFIGURATION_REQUESTED:String = "nodeRendererConfigurationRequested_NodeRendererEvent";

        public var nodeRenderer:NodeRendererBase;

        public function NodeRendererEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}