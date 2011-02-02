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

package net.undf.abicloud.view.virtualappliance.components.virtualappliance
{
    import flash.events.Event;
    import flash.events.EventDispatcher;
    
    import mx.managers.CursorManager;
    
    import net.undf.abicloud.view.main.CommonAssets;

    /**
     * This class manages all tool available in VirtualApplianceDrawTool
     *
     * @author Oliver
     *
     */

    public class DrawToolsManager extends EventDispatcher
    {
        /* ------------- Private atributes ------------- */
        //Unique instance of this class 
        private static var instance:DrawToolsManager;

        private var _currentTool:int;

        public function DrawToolsManager(access:Private)
        {
            if (access == null)
                throw Error("Unable to access the constructor of a Singleton class");
            else
            {
                this._currentTool = NO_TOOL;
            }
        }

        public static function getInstance():DrawToolsManager
        {
            if (instance == null)
                instance = new DrawToolsManager(new Private());

            return instance;
        }


        /////////////////////////////////
        //TOOL CURSORS (SELECTION_TOOL uses the default system cursor)
//        [Embed(source="/assets/cursors/LineTool.p ng")]
//        private var LineToolCursor:Class;
//
//        [Embed(source="/assets/cursors/MoveTool.p ng")]
//        private var MoveToolCursor:Class;
//
//        [Embed(source="/assets/cursors/Scissors.p ng")]
//        private var ScissorsToolCursor:Class;

        /////////////////////////////////
        //AVAILABLE TOOLS
        public static const NO_TOOL:int = -1;

        public static const SELECTION_TOOL:int = 0;

        public static const DRAW_CONNECTION_TOOL:int = 1;

        public static const MOVE_NODE_TOOL:int = 2;

        public static const SCISSORS_TOOL:int = 3;


        /* ------------- Public methods ------------- */
        [Bindable(event="currentToolChange")]
        public function get currentTool():int
        {
            return this._currentTool;
        }

        public function set currentTool(value:int):void
        {
            this._currentTool = value;

            dispatchEvent(new Event("currentToolChange"));
        }

        /**
         * Sets a visible mouse cursor for a given tool
         * The visibility of the mouse cursor is totally independent from the current
         * selected tool. This means that although a tool is currently selected, its mouse cursor
         * should  not be showed until that tool can be used.
         *
         * Changing the mouse cursor never changes the currentTool value, and changing the currenTool
         * neither changes the current cursor.
         *
         * @param tool The tool to show its cursor
         *
         */
        public function setToolCursor(tool:int):void
        {
            CursorManager.removeAllCursors();
            switch (tool)
            {
                case NO_TOOL:
                    //When NO_TOOL is given, the default system cursor is shown
                    break;

                case SELECTION_TOOL:
                    //Selection tool uses the default system cursor
                    break;

                case DRAW_CONNECTION_TOOL:
                    CursorManager.setCursor(CommonAssets.cursorLineTool as Class);
                    break;

                case MOVE_NODE_TOOL:
                    CursorManager.setCursor(CommonAssets.cursorMoveTool as Class);
                    break;

                case SCISSORS_TOOL:
                    CursorManager.setCursor(CommonAssets.cursorScissorsTool as Class);
                    break;
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