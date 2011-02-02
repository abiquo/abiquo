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

package net.undf.abicloud.view.general.abicloudblind
{
    import flash.display.DisplayObject;
    import flash.events.Event;
    import flash.events.MouseEvent;

    import mx.containers.HBox;
    import mx.containers.VBox;
    import mx.controls.Button;
    import mx.resources.ResourceManager;

    /**
     * Component used by AbiCloudBlind to place content
     *
     * It has a button, to announce AbiCloudBlind to close the blind
     * @author Oliver
     *
     */
    public class AbiCloudBlindContainer extends VBox
    {
        private var _closeButton:Button;

        //The content that will be shown
        private var _blindContent:DisplayObject;

        public function get blindContent():DisplayObject
        {
            return this._blindContent;
        }

        public function set blindContent(value:DisplayObject):void
        {
            if (this._blindContent)
                removeChild(this._blindContent);

            this._blindContent = value;

            if (this._blindContent)
                addChildAt(this._blindContent, 0);
        }

        public function AbiCloudBlindContainer()
        {
            super();
            styleName = "AbiCloudBlindContainer";
        }

        override protected function createChildren():void
        {
            super.createChildren();

            //Create the close Button
            var closeButtonContainer:HBox = new HBox();
            closeButtonContainer.percentWidth = 100;
            closeButtonContainer.setStyle("horizontalAlign", "right");

            this._closeButton = new Button();
            this._closeButton.height = 18;
            this._closeButton.label = ResourceManager.getInstance().getString("Common",
                                                                              "BUTTON_CLOSE");
            this._closeButton.addEventListener(MouseEvent.CLICK, onClickCloseButton);
            closeButtonContainer.addChild(this._closeButton);

            addChild(closeButtonContainer);
        }

        private function onClickCloseButton(event:Event):void
        {
            dispatchEvent(new Event("abiCloudBlindContainer_close"));
        }
    }
}