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

    import mx.binding.utils.ChangeWatcher;
    import mx.containers.Canvas;
    import mx.effects.Move;
    import mx.effects.easing.Exponential;
    import mx.events.EffectEvent;

    /**
     * Custom component to place content over another component, simulating a blind.
     *
     * When creating an AbiCloudBlind, think of where the blind should start to open from, and the maximum size it can have.
     * AbiCloudBlind will be invisible until it has some content to show.
     *
     *
     * AbiCloudBlindContainer has a close button that, once clicked, will close the blind, and remove its content.
     *
     * @author Oliver
     *
     */

    [Event(name="blindOpened", type="flash.events.Event")]
    [Event(name="blindClosed", type="flash.events.Event")]

    public class AbiCloudBlind extends Canvas
    {
        //Container where the blind content will be placed
        private var _blindContainer:AbiCloudBlindContainer;

        // Move effect when opening or closing the blind
        private var _moveEffect:Move;

        //Flag indicating if the blind is opened or nor
        private var _isBlindOpened:Boolean = false;

        [Bindable(event="isBlindOpenedChange")]
        public function get isBlindOpened():Boolean
        {
            return this._isBlindOpened;
        }

        public function AbiCloudBlind()
        {
            super();

            this._moveEffect = new Move();
            this._moveEffect.duration = 1000;
        }

        override protected function createChildren():void
        {
            super.createChildren();

            //Creating the blind container
            this._blindContainer = new AbiCloudBlindContainer();
            this._blindContainer.visible = false;
            this._blindContainer.addEventListener("abiCloudBlindContainer_close",
                                                  onAbiCloudBlindContainerClose);
            ChangeWatcher.watch(this._blindContainer, "height", onBlindContainerSizeChanged);
            addChild(this._blindContainer);
        }


        /**
         * Opens an AbiCloudBlind
         * @param blindContent DisplayContent with the content to show inside the blind
         * @param expandBlind when true, the blindContent's with and height will be set to 100%
         *
         */
        public function openBlind(blindContent:DisplayObject, expandBlind:Boolean = false):void
        {
            if (expandBlind)
            {
                this._blindContainer.percentWidth = 100;
                this._blindContainer.percentHeight = 100;
            }
            else
            {
                this._blindContainer.percentWidth = 0;
                this._blindContainer.percentHeight = 0;
            }

            this._isBlindOpened = true;
            dispatchEvent(new Event("isBlindOpenedChange"));
            this._blindContainer.blindContent = blindContent;
        }

        /**
         * Once the AbicloudBlindContainer is set with content, and
         * has a proper size we move it up, so we can simulate
         * an opening effect
         */
        private function onBlindContainerSizeChanged(event:Event):void
        {
            if (this._isBlindOpened)
            {
                this._blindContainer.y = -this._blindContainer.height;
                this._blindContainer.visible = true;

                //Start the open effect
                this._moveEffect.addEventListener(EffectEvent.EFFECT_END, onMoveEffectEndOnOpen);
                this._moveEffect.yFrom = -this._blindContainer.height;
                this._moveEffect.yTo = 0;
                this._moveEffect.easingFunction = Exponential.easeOut;
                this._moveEffect.play([ this._blindContainer ]);
            }
        }

        /**
         * Handler called when the open effect ends
         */
        private function onMoveEffectEndOnOpen(effectEvent:EffectEvent):void
        {
            this._moveEffect.removeEventListener(EffectEvent.EFFECT_END, onMoveEffectEndOnOpen);

            dispatchEvent(new Event("blindOpened"));
        }

        /**
         * Handler called when the close button in AbiCloudBlindContainer is clicked
         */
        private function onAbiCloudBlindContainerClose(event:Event):void
        {
            closeBlind();
        }

        /**
         * Closes this AbiCloudBlind, if opened
         */
        public function closeBlind():void
        {
            if (this._isBlindOpened)
            {
                //Play close effect
                this._moveEffect.addEventListener(EffectEvent.EFFECT_END, onMoveEffectEndOnClose);
                this._moveEffect.easingFunction = Exponential.easeIn;
                this._moveEffect.play([ this._blindContainer ], true);
            }
        }

        /**
         * Handler called when close effect is done
         */
        private function onMoveEffectEndOnClose(effectEvent:EffectEvent):void
        {
            this._moveEffect.removeEventListener(EffectEvent.EFFECT_END, onMoveEffectEndOnClose);

            this._blindContainer.visible = false;
            this._blindContainer.percentWidth = 0;
            this._blindContainer.percentHeight = 0;

            this._isBlindOpened = false;
            dispatchEvent(new Event("isBlindOpenedChange"));

            dispatchEvent(new Event("blindClosed"));
            this._blindContainer.blindContent = null;
        }
    }
}