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

package net.undf.abicloud.view.infrastructure.components
{
    import net.undf.abicloud.controller.ThemeHandler;
    import net.undf.abicloud.utils.customtree.CustomTreeNodeRenderer;
    import net.undf.abicloud.vo.infrastructure.Rack;
    import net.undf.abicloud.vo.infrastructure.UcsRack;

    public class InfrastructureNodeRenderer extends CustomTreeNodeRenderer
    {
        public function InfrastructureNodeRenderer()
        {
            super();

            //Set the ICON_LEAF for PhysicalMachines
            LEAF_ICON = ThemeHandler.getInstance().getImageFromStyle("infrastructureNodeRendererLeafIcon");

        }

        override public function set data(object:Object):void
        {
            super.data = object;

            if (this._customTreeNode && this._customTreeNode.item is Rack)
                this._label.setStyle("fontWeight", "bold");
            else
                this._label.setStyle("fontWeight", "normal");

        }

    }
}