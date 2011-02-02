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

package net.undf.abicloud.business.managers
{
    import flash.display.DisplayObjectContainer;
    import flash.display.StageDisplayState;
    import flash.events.FullScreenEvent;

    import mx.containers.Box;
    import mx.core.Application;
    import mx.core.UIComponent;

    public class FullScreenManager
    {
        /* ------------- Private atributes ------------- */
        //Unique instance of this class 
        private static var instance:FullScreenManager;

        private var _fullScreenContainer:Box;

        private var _originalParent:DisplayObjectContainer;

        private var _component:UIComponent;

        private var _originaComponentPercentWidth:Number;

        private var _originalComponentPercentHeight:Number;

        public function FullScreenManager(access:Private)
        {
            if (access == null)
                throw Error("Unable to access the constructor of a Singleton class");
            else
            {
                this._fullScreenContainer = new Box();
                this._fullScreenContainer.percentWidth = 100;
                this._fullScreenContainer.percentHeight = 100;
            }
        }

        public static function getInstance():FullScreenManager
        {
            if (instance == null)
                instance = new FullScreenManager(new Private());

            return instance;
        }

        private function fullScreenHandler(fullScreenEvent:FullScreenEvent):void
        {
            if (fullScreenEvent.fullScreen)
            {
                //Placing the component in the full screen container
                this._fullScreenContainer.addChild(this._component);

                //Saving the component properties that will be changed
                this._originaComponentPercentWidth = this._component.percentWidth;
                this._originalComponentPercentHeight = this._component.percentHeight;

                //Setting the component to fill the whole screen
                this._component.percentWidth = 100;
                this._component.percentHeight = 100;

                //Adding the FullScreenContainer to the Application
                Application.application.addChild(this._fullScreenContainer);
            }
            else
            {
                //Returning the _component to its original parent
                this._originalParent.addChild(this._component);

                //Setting the original properties for the component
                this._component.percentWidth = this._originaComponentPercentWidth;
                this._component.percentHeight = this._originalComponentPercentHeight;

                //Unregistering the full screen events
                Application.application.stage.removeEventListener(FullScreenEvent.FULL_SCREEN,
                                                                  fullScreenHandler);

                //Removing the FullScreenContainer from the Application
                if (Application.application.getChildIndex(this._fullScreenContainer) > -1)
                    Application.application.removeChild(this._fullScreenContainer);
            }
        }

        public function get fullScreenContainer():UIComponent
        {
            return this._fullScreenContainer;
        }

        public function makeFullScreen(component:UIComponent):void
        {
            this._component = component;

            this._originalParent = component.parent;

            try
            {
                //Registering full screen events
                Application.application.stage.addEventListener(FullScreenEvent.FULL_SCREEN,
                                                               fullScreenHandler);

                //Calling for launch full screen
                Application.application.stage.displayState = StageDisplayState.FULL_SCREEN;
            }
            catch (err:SecurityError)
            {
                //Unregistering the full screen events
                Application.application.stage.removeEventListener(FullScreenEvent.FULL_SCREEN,
                                                                  fullScreenHandler);
            }
        }
    }
}

/**
 * Inner class which restricts constructor access to Private
 */
class Private
{
}