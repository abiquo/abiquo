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
    import mx.controls.treeClasses.ITreeDataDescriptor;

    /**
     * Class that represents a ode in the CustomTree
     * It saves an item, and all the necessary information to draw a CustomTreeNode using a List
     **/
    [Bindable]
    public class CustomTreeNode
    {
        public var item:Object;

        public var labelText:String;

        public var customTreeDataDescriptor:ICustomTreeDataDescriptor;

        public var isBranchOpened:Boolean;

        public function CustomTreeNode(item:Object = null, labelText:String = "",
                                       customTreeDataDescriptor:ICustomTreeDataDescriptor = null,
                                       isBranchOpened:Boolean = false)
        {
            this.item = item;

            this.labelText = labelText;

            this.customTreeDataDescriptor = customTreeDataDescriptor;
            this.isBranchOpened = isBranchOpened;
        }

    }
}