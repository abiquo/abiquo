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
    import flash.events.MouseEvent;
    
    import mx.containers.HBox;
    import mx.controls.Image;
    import mx.controls.Label;
    import mx.controls.listClasses.IListItemRenderer;
    import mx.core.DragSource;
    import mx.events.DragEvent;
    import mx.events.ItemClickEvent;
    import mx.managers.DragManager;
    
    import net.undf.abicloud.controller.ThemeHandler;


    public class CustomTreeNodeRenderer extends HBox implements IListItemRenderer
    {

        public var OPEN_BRANCH_ICON:* = ThemeHandler.getInstance().getImageFromStyle("infrastructureNodeRendererOpenBranchIcon"); 

        public var CLOSED_BRANCH_ICON:* = ThemeHandler.getInstance().getImageFromStyle("infrastructureNodeRendererClosedBranchIcon"); 

        public var LEAF_ICON:*;


        public function CustomTreeNodeRenderer()
        {
            super();

            this.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
            this.addEventListener(DragEvent.DRAG_ENTER, dragEnterHandler);
            this.addEventListener(DragEvent.DRAG_DROP, dragDropHandler);

            //Set basic style
            verticalScrollPolicy = "off";
            horizontalScrollPolicy = "off";
        }

        //A label to describe this CustomTreeNode
        protected var _label:Label;

        //An Icon to show when the branch is opened, or an image for the leaves
        protected var _nodeIcon:Image;

        //The CustomTreeNode that is drawing this CustomTreeNodeRenderer
        protected var _customTreeNode:CustomTreeNode;

        override public function set data(object:Object):void
        {
            if (object is CustomTreeNode)
            {
                super.data = object;
                this._customTreeNode = object as CustomTreeNode;
            }
        }

        override protected function createChildren():void
        {
            super.createChildren();

            //Creating the node icon image
            this._nodeIcon = new Image();
            this._nodeIcon.width = 16;
            this._nodeIcon.height = 16;
            this._nodeIcon.scaleContent = false;
            this._nodeIcon.setStyle("verticalAlign", "middle");
            this._nodeIcon.setStyle("horizontalAlign", "center");
            this._nodeIcon.addEventListener(MouseEvent.CLICK, clickHandler);

            addChild(this._nodeIcon);

            //Creating the label
            this._label = new Label();
            this._label.truncateToFit = true;
            this._label.maxWidth = 150;
            addChild(this._label);

        }

        /**
         * To draw our CustomTreeNode properly
         **/
        override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
        {
            super.updateDisplayList(unscaledWidth, unscaledHeight);

            if (this._customTreeNode != null)
            {
                //Setting the proper Label text
                this._label.text = this._customTreeNode.labelText;
                
                //Setting the proper node icon
                if (this._customTreeNode.customTreeDataDescriptor.isBranch(this._customTreeNode.item))
                {
                    styleName = "CustomTreeNodeBranch"
                    this._nodeIcon.source = this._customTreeNode.isBranchOpened ? OPEN_BRANCH_ICON : CLOSED_BRANCH_ICON;
                }
                else
                {
                    styleName = "CustomTreeNodeLeaf";
                    this._nodeIcon.source = LEAF_ICON;
                }
            }
        }

        /**
         * Handler called when user clicks on nodeIcon
         */
        private function clickHandler(mouseEvent:MouseEvent):void
        {
            var itemClickEvent:ItemClickEvent = new ItemClickEvent("customTreeNodeClick",
                                                                   true);
            itemClickEvent.item = this._customTreeNode;
            dispatchEvent(itemClickEvent);

            //There was no Drag operation. We can remove this handler
            this.removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
        }

        /**
         * Handler to detect when user wants to initate a drag operation
         */
        private function mouseDownHandler(mouseEvent:MouseEvent):void
        {
            this.addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
        }

        private function mouseMoveHandler(mouseEvent:MouseEvent):void
        {
            //We don't need this handler until user initiates another Drag operation
            this.removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);

            if (this._customTreeNode.customTreeDataDescriptor.isNodeDraggable(this._customTreeNode.item))
            {
                var dragSource:DragSource = new DragSource();
                dragSource.addData(this._customTreeNode, "CustomTreeNode");

                var dragProxy:Image = new Image();
                dragProxy.width = this._nodeIcon.width;
                dragProxy.height = this._nodeIcon.height;
                dragProxy.source = this._nodeIcon.source;

                DragManager.doDrag(this, dragSource, mouseEvent, dragProxy, -mouseEvent.localX,
                                   -mouseEvent.localY, 0.9);
            }
        }

        private function dragEnterHandler(dragEvent:DragEvent):void
        {
            //Check that the Data being dropped is valid
            if (dragEvent.dragSource.hasFormat("CustomTreeNode"))
            {
                var customTreeNodeDragged:CustomTreeNode = dragEvent.dragSource.dataForFormat("CustomTreeNode") as CustomTreeNode;

                //Check if this Node can accept drops
                if (dragEvent.shiftKey)
                {
                    //Copy operation
                    if (this._customTreeNode.customTreeDataDescriptor.isCopyAllowed(this._customTreeNode.item,
                                                                                    customTreeNodeDragged.item))
                    {
                        DragManager.acceptDragDrop(this);
                        DragManager.showFeedback(DragManager.COPY);
                    }
                }

                else if (this._customTreeNode.customTreeDataDescriptor.isMoveAllowed(this._customTreeNode.item,
                                                                                     customTreeNodeDragged.item))
                {
                    //Move operation
                    DragManager.acceptDragDrop(this);
                    DragManager.showFeedback(DragManager.MOVE);
                }
            }
        }

        private function dragDropHandler(dragEvent:DragEvent):void
        {
            if (dragEvent.dragSource.hasFormat("CustomTreeNode"))
            {
                var customTreeNodeDragged:CustomTreeNode = dragEvent.dragSource.dataForFormat("CustomTreeNode") as CustomTreeNode;

                if (dragEvent.shiftKey)
                    this._customTreeNode.customTreeDataDescriptor.copyChild(this._customTreeNode.item,
                                                                            customTreeNodeDragged.item);
                else
                    this._customTreeNode.customTreeDataDescriptor.moveChild(this._customTreeNode.item,
                                                                            customTreeNodeDragged.item);
            }
        }

    }
}