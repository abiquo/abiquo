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
    import flash.geom.Point;

    import mx.collections.ArrayCollection;

    /**
     * This Interface represents a component that can display Nodes from a VirtualAppliance,
     * rendered using NodeRenderer components
     *
     * A component implementing this interface should handle the following events
     * NodeRendererEvent.NODERENDERER_ERASED
     * NodeRendererEvent.NODERENDERER_ADDED
     * and must capture these events from its INodeViewController, in order to synchronize multiple INodeView
     *
     * @author Oliver
     *
     */
    public interface INodeView
    {
        /**
         * Sets the INodeViewController that controls this INodeView.
         * The INodeView will register NodeRendererEvents from its INodeViewController
         *
         * @param inodeViewController the INodeViewController that contains and mantains this INodeView
         *
         */
        function set nodeViewController(inodeViewController:INodeViewController):void;

        /**
         * Cleans this INodeView from any NodeRenderer
         */
        function cleanNodes():void;

        /**
         * Draws all NodeRenderers contained in nodes ArrayCollection
         * To draw them, this INodeView may modify values from the NodeRenderer component, to
         * be able to display them, but this INodeView can never modify attributes from the Node
         * class contained inside the NodeRenderer
         *
         * nodeConnections is a XML document that describes the connections between Nodes.
         * Format is as follows
         * <connections>
         * 	<connection nodeFromID="a" nodeToID="b"/>
         * </connections>
         * The INodeView is responsible to mantain this XML document
         */
        function drawNodes(nodes:ArrayCollection, nodeConnections:XML):void;

        /**
         * This flag indicates if this INodeView is asked to display the nodes that it may contain
         * If true, this INodeView must respond to any NodeRendererEvent in order to correctly display
         * the NodeRenderers that it contains.
         * If false, this INodeView should take any action against a NodeRendererEvent
         */
        function get isDisplayingNodes():Boolean;

        function set isDisplayingNodes(value:Boolean):void;

        /**
         * Performs a coordenates conversion between x and y from the NodeRenderer,
         * to posX and posY that must be saved in the Node object that contains the NodeRenderer
         *
         * These coordenates conversion helps save the coordenates of a Node in a standard way, so
         * we can have multiple INodeView that can display the NodeRenderer in different ways.
         *
         * @param point The coordenates x and y from the NodeRenderer component
         *
         * @return a Point with the coordenates that will be saved in the Node object
         */
        function fromINodeViewToNode(point:Point):Point;

        /**
         * Performs a coordenates conversion between posX and posY from the Node object contained
         * iniside a NodeRenderer, to the values x and y that will be used to correctly display
         * a NodeRenderer by a INodeView
         *
         * These coordenates conversion helps save the coordenates of a Node in a standard way, so
         * we can have multiple INodeView that can display the NodeRenderer in different ways.
         *
         * @param point The coordenates x and y from the NodeRenderer component
         *
         * @return a Point with the coordenates that will be saved in the Node object
         */
        function fromNodeToINodeView(point:Point):Point;
    }
}