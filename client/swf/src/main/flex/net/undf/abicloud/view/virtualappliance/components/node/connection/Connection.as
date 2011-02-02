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
    import flash.events.MouseEvent;
    import flash.geom.Point;

    import mx.controls.Button;
    import mx.core.Application;
    import mx.core.UIComponent;

    /**
     *
     * @author Oliver
     *
     */
    public class Connection extends UIComponent
    {
        //utton to delete this Connection
        private var _connectionEraseButton:Button;

        private var _connectorFrom:Connector;

        public function get connectorFrom():Connector
        {
            return this._connectorFrom;
        }

        private var _connectorTo:Connector;

        public function get connectorTo():Connector
        {
            return this._connectorTo;
        }

        private var _surface:UIComponent;

        public function Connection(surface:UIComponent, connectorFrom:Connector,
                                   connectorTo:Connector = null)
        {
            this._surface = surface;
            this._surface.addEventListener(ConnectionEvent.CONNECTOR_DELETED, onConnectorDeleted);

            this._connectorFrom = connectorFrom;
            this._connectorFrom.addEventListener(ConnectionEvent.CONNECTOR_MOVED,
                                                 onConnectorMoved);

            this._connectorTo = connectorTo;

            if (!this._connectorTo)
            {
                //This Connection is still being created
                //We need to follow the mouse pointer
                Application.application.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
            }
            else
            {
                //Registering events that this Connection needs
                this._connectorTo.addEventListener(ConnectionEvent.CONNECTOR_MOVED,
                                                   onConnectorMoved);
                this.addEventListener(MouseEvent.ROLL_OVER, onRollOver);
                this.addEventListener(MouseEvent.ROLL_OUT, onRollOut);

                //This Connection is confirmed, we just need to draw it
                drawConnection();
            }
        }

        override protected function createChildren():void
        {
            super.createChildren();

            this._connectionEraseButton = new Button();
            this._connectionEraseButton.visible = false;
            this._connectionEraseButton.width = 13;
            this._connectionEraseButton.height = 13;
            this._connectionEraseButton.styleName = "VirtualApplianceEraseNodeButton";
            this._connectionEraseButton.addEventListener(MouseEvent.CLICK, onClickConnectionEraseButton);

            addChild(this._connectionEraseButton);
        }

        /**
         * MouseEvent.ROLL_OVER handler
         * We give user option to delete this Connection
         * @param mouseEvent
         *
         */
        private function onRollOver(mouseEvent:MouseEvent):void
        {
            //Calculating the position for the erase button
            var buttonPosition:Point = new Point();
            buttonPosition.x = Math.abs((this._connectorTo.position.x + this._connectorFrom.position.x - 13) / 2);
            buttonPosition.y = Math.abs((this._connectorTo.position.y + this._connectorFrom.position.y - 13) / 2);

            this._connectionEraseButton.x = buttonPosition.x;
            this._connectionEraseButton.y = buttonPosition.y;
            this._connectionEraseButton.visible = true;

            drawConnection(0x333333);

        }

        /**
         * MouseEvent.ROLL_OUT handler
         * Removing feedback given to the user to show that this Connection can be deleted
         * @param mouseEvent
         *
         */
        private function onRollOut(mouseEvent:MouseEvent):void
        {
            this._connectionEraseButton.visible = false;
            drawConnection();
        }

        /**
         * MouseEvent.CLICK handler
         * When user click over the , and the DRAW_CONNECTION_TOOL is selected
         * we delete this Connection
         * @param mouseEvent
         *
         */
        private function onClickConnectionEraseButton(mouseEvent:MouseEvent):void
        {
            //Announcing that this Connection will be deleted
            var connectionEvent:ConnectionEvent = new ConnectionEvent(ConnectionEvent.CONNECTION_DELETED);
            connectionEvent.connection = this;
            dispatchEvent(connectionEvent);
        }

        /**
         * Cleans this Connection, so it can be destroyed
         */
        public function cleanConnection():void
        {
            graphics.clear();

            //Unregistering events from the surface
            if (this._surface)
                this._surface.removeEventListener(ConnectionEvent.CONNECTOR_DELETED,
                                                  onConnectorDeleted);

            //Unregistering events from Connectors
            if (this._connectorFrom)
                this._connectorFrom.removeEventListener(ConnectionEvent.CONNECTOR_MOVED,
                                                        onConnectorMoved);
            if (this._connectorTo)
                this._connectorTo.removeEventListener(ConnectionEvent.CONNECTOR_MOVED,
                                                      onConnectorMoved);

            //Unregistering events from the Connection itself
            if (this.hasEventListener(MouseEvent.ROLL_OVER))
            {
                this.removeEventListener(MouseEvent.ROLL_OVER, onRollOver);
                this.removeEventListener(MouseEvent.ROLL_OUT, onRollOut);
            }

            //Cleaning variables
            removeChild(this._connectionEraseButton);
            this._connectionEraseButton.removeEventListener(MouseEvent.CLICK, onClickConnectionEraseButton);
            this._connectionEraseButton = null;
            this._connectorFrom = null;
            this._connectorTo = null;
            this._surface = null;
        }

        private function drawConnection(connectionColor:uint = 0x000000):void
        {
            //Cleaning any previous old line
            graphics.clear();

            //First, we draw the thicker shadow line, used to make it easier to click over the erase button
            graphics.lineStyle(13, 0x000000, 0, true);
            graphics.moveTo(this._connectorFrom.position.x, this._connectorFrom.position.y);
            graphics.lineTo(this._connectorTo.position.x, this._connectorTo.position.y);


            //Now, we draw the line that will always be visible
            graphics.lineStyle(3, connectionColor, 0.9, true);
            graphics.moveTo(this._connectorFrom.position.x, this._connectorFrom.position.y);
            graphics.lineTo(this._connectorTo.position.x, this._connectorTo.position.y);
        }

        public function confirmConnection(connectorTo:Connector):void
        {
            //We do not need the MouseEvent listener anymore
            Application.application.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);

            //Setting the destination for this Connection
            this._connectorTo = connectorTo;

            //Registering events that we could not register until this Connection was confirmed
            this._connectorTo.addEventListener(ConnectionEvent.CONNECTOR_MOVED, onConnectorMoved);
            this.addEventListener(MouseEvent.ROLL_OVER, onRollOver);
            this.addEventListener(MouseEvent.ROLL_OUT, onRollOut);

            //Drawing the connection
            drawConnection();
        }

        public function cancelConnection():void
        {
            Application.application.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
            cleanConnection();
        }

        private function onMouseMove(mouseEvent:MouseEvent):void
        {
            //Cleaning the old line
            graphics.clear();
            graphics.lineStyle(3, 0x000000, 0.8, true);

            //The start point while user is creating this Connection, is always
            //the _connectorFrom position
            graphics.moveTo(this._connectorFrom.position.x, this._connectorFrom.position.y);

            //The end point while user is creating this Connection, is where the mouse is
            var pointTo:Point = this._surface.globalToContent(new Point(mouseEvent.stageX,
                                                                        mouseEvent.stageY));

            //We keep the line inside the surface
            if (pointTo.x < 0)
                pointTo.x = 0;
            if (pointTo.x > this._surface.width)
                pointTo.x = this._surface.width;

            if (pointTo.y < 0)
                pointTo.y = 0;
            if (pointTo.y > this._surface.height)
                pointTo.y = this._surface.height;

            //Drawing the new line
            graphics.lineTo(pointTo.x, pointTo.y);
        }

        private function onConnectorMoved(connectionEvent:ConnectionEvent):void
        {
            //When any connector from this Connection moves, we need to redraw this Connection
            drawConnection();
        }

        private function onConnectorDeleted(connectionEvent:ConnectionEvent):void
        {
            if (connectionEvent.connector == this._connectorFrom || connectionEvent.connector == this._connectorTo)
            {
                //Announcing that this Connection will be deleted
                var connectionEvent:ConnectionEvent = new ConnectionEvent(ConnectionEvent.CONNECTION_DELETED);
                connectionEvent.connection = this;
                dispatchEvent(connectionEvent);
            }
        }
    }
}