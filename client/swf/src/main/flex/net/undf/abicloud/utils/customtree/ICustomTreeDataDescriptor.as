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

package net.undf.abicloud.utils.customtree
{
    import mx.collections.ICollectionView;

    /**
     * ICustomTreeDataDescriptor extends ITreeDataDescriptor adding a new method
     **/
    public interface ICustomTreeDataDescriptor
    {

        /**
         *  Provides access to a node's children, returning a collection
         *  view of children if they exist.
         *  A node can return any object in the collection as its children;
         *  children need not be nested.
         *  It is best-practice to return the same collection view for a
         *  given node.
         *
         *  @param node The node object currently being evaluated.
         *
         *  @param model The entire collection that this node is a part of.
         *
         *  @return An collection view containing the child nodes.
         */
        function getChildren(node:Object, model:Object = null):ICollectionView;

        /**
         *  Tests for the existence of children in a non-terminating node.
         *
         *  @param node The current node.
         *
         *  @param model The entire collection that this node is a part of.
         *
         *  @return <code>true</code> if the node has at least one child.
         */
        function hasChildren(node:Object, model:Object = null):Boolean;

        /**
         *  Tests a node for termination.
         *  Branches are non-terminating but are not required
         *  to have any leaf nodes.
         *
         *  @param node The node object currently being evaluated.
         *
         *  @param model The entire collection that this node is a part of.
         *
         *  @return A Boolean indicating if this node is non-terminating.
         */
        function isBranch(node:Object, model:Object = null):Boolean;

        /**
         * Test if a move operation is allowed
         * @param parent the parent node where a child node will be moved
         * @param newChild the child node being moved
         * @return A Boolean indicating if the move operation is allowed (but the operation will actually not be performed)
         *
         */
        function isMoveAllowed(parent:Object, newChild:Object):Boolean;

        /**
         * Test if a copy operation is allowed
         * @param parent the parent node where a child will be copied
         * @param newChild the child node being copied
         * @return A Boolean indicating if the copy operation is allowed (but the operation will actually not be performed)
         *
         */
        function isCopyAllowed(parent:Object, newChild:Object):Boolean;

        /**
         *  Copies a child node to a node at the specified index.
         *
         *  @param node The node object that will parent the child.
         *
         *  @param child The node object that will be parented by the node.
         *
         *  @param index The 0-based index of where to put the child node.
         *
         *  @param model The entire collection that this node is a part of.
         *
         *  @return <code>true</code> if successful.
         */
        function copyChild(parent:Object, newChild:Object):Boolean;


        /**
         *  Moves a child node to a node at the specified index.
         *
         *  @param node The node object that will parent the child.
         *
         *  @param child The node object that will be parented by the node.
         *
         *  @param index The 0-based index of where to put the child node.
         *
         *  @param model The entire collection that this node is a part of.
         *
         *  @return <code>true</code> if successful.
         */
        function moveChild(parent:Object, newChild:Object):Boolean;

        /**
         * Gets the level of a given node inside the Tree
         *
         * @param node The node object that we want to know in which level inside the Tree ist
         *
         * @return The requested level
         */
        function getNodeLevel(node:Object):int;


        /**
         * Tests if a node can be dragged as part of a Drag & Drop operation
         *
         * @param node The node to test
         *
         * @return <code>true</code> if node can be dragged
         */
        function isNodeDraggable(node:Object):Boolean;

        /**
         * Marks a branch as opened. Useful to be able to save the state of a Tree, when its
         * data provider is updated and we want to preserve the opened branches
         * @param node The node (which is branch) to be marked as opened
         *
         */
        function markBranchAsOpened(node:Object):void;

        /**
         * Unmarks a branch as opened. Useful to be able to save the state of a Tree, when its
         * data provider is updated and we want to preserve the opened branches
         * @param node The node (which is branch) to be unmarked as opened
         *
         */
        function unmarkBranchAsOpened(node:Object):void;

        /**
         * Indicates if a branch is marked as opened
         * @param node The node to be checked
         * @return True, if this node is marked as opened
         *
         */
        function isBranchOpened(node:Object):Boolean;

        /**
         * Clean all branches marked as opened. Useful when a tree no longer wants to preserve
         * its state (for exemple, when its data provider has changed)
         *
         */
        function cleanMarks():void
    }
}