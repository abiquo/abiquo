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

package net.undf.abicloud.utils
{
    import flash.events.KeyboardEvent;
    import flash.events.MouseEvent;

    import mx.core.Application;
    import mx.managers.CursorManager;


    /**
     * Blocks the screen, capturing all KeyBoard and MouseEvent in the Application,
     * stopping their propagation
     *
     * @author Oliver
     * @
     *
     */
    public class ScreenBlocker
    {

        private static var numBlocks:int = 0;

        /**
         * Blocks screen, if it has not been blocked yet
         *
         */
        public static function blockScreen():void
        {
            numBlocks++;

            if (numBlocks == 1)
            {
                //Showing busy cursor
                CursorManager.setBusyCursor();

                //Block mouse interaction
                Application.application.systemManager.addEventListener(MouseEvent.CLICK,
                                                                       mouseHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(MouseEvent.DOUBLE_CLICK,
                                                                       mouseHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(MouseEvent.MOUSE_DOWN,
                                                                       mouseHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(MouseEvent.MOUSE_UP,
                                                                       mouseHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(MouseEvent.MOUSE_OVER,
                                                                       mouseHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(MouseEvent.MOUSE_OUT,
                                                                       mouseHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(MouseEvent.MOUSE_WHEEL,
                                                                       mouseHandler,
                                                                       true);

                //We block keyboard interaction too
                Application.application.systemManager.addEventListener(KeyboardEvent.KEY_DOWN,
                                                                       keyboardHandler,
                                                                       true);
                Application.application.systemManager.addEventListener(KeyboardEvent.KEY_UP,
                                                                       keyboardHandler,
                                                                       true);
            }
        }

        private static function keyboardHandler(keyboardEvent:KeyboardEvent):void
        {
            keyboardEvent.stopImmediatePropagation();
            keyboardEvent.stopPropagation();
        }

        private static function mouseHandler(mouseEvent:MouseEvent):void
        {
            mouseEvent.stopImmediatePropagation();
            mouseEvent.stopPropagation();
        }

        /**
         * Unblocks screen if it is blocked
         *
         */
        public static function unblockScreen():void
        {
            if (numBlocks > 0)
            {
                numBlocks--;
                if (numBlocks == 0)
                {
                    //Removing mouse blocking
                    Application.application.systemManager.removeEventListener(MouseEvent.CLICK,
                                                                              mouseHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(MouseEvent.DOUBLE_CLICK,
                                                                              mouseHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(MouseEvent.MOUSE_DOWN,
                                                                              mouseHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(MouseEvent.MOUSE_UP,
                                                                              mouseHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(MouseEvent.MOUSE_OVER,
                                                                              mouseHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(MouseEvent.MOUSE_OUT,
                                                                              mouseHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(MouseEvent.MOUSE_WHEEL,
                                                                              mouseHandler,
                                                                              true);

                    //Removing keyboard blocking too
                    Application.application.systemManager.removeEventListener(KeyboardEvent.KEY_DOWN,
                                                                              keyboardHandler,
                                                                              true);
                    Application.application.systemManager.removeEventListener(KeyboardEvent.KEY_UP,
                                                                              keyboardHandler,
                                                                              true);

                    //Removing busy cursor
                    CursorManager.removeBusyCursor();
                }
            }
        }
    }
}