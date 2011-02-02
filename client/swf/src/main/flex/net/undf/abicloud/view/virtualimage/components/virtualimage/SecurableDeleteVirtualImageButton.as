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

package net.undf.abicloud.view.virtualimage.components.virtualimage
{
    import mx.controls.Button;

    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.security.ISecurableComponent;
    import net.undf.abicloud.security.SecurableResource;
    import net.undf.abicloud.vo.virtualimage.VirtualImage;

    public class SecurableDeleteVirtualImageButton extends Button implements ISecurableComponent
    {
        //Securable Resources of this Securable Component
        private var _securableResource:SecurableResource;

        //We also need the VirtualImage to decide if user has permission to use this resource
        private var _virtualImage:VirtualImage;

        public function SecurableDeleteVirtualImageButton()
        {
            super();

            defineSecurableResources();
            if (this._virtualImage)
                checkSecurableResources();
            else
                makeUnavailable();
        }

        public function set virtualImage(value:VirtualImage):void
        {
            this._virtualImage = value;
            if (this._virtualImage)
                checkSecurableResources();
            else
                makeUnavailable();
        }

        public function defineSecurableResources():void
        {
            this._securableResource = new SecurableResource("DELETE_PUBLIC_VIRTUAL_IMAGE",
                                                            "APPLIANCE_LIBRARY");
        }

        public function checkSecurableResources():void
        {
            if (this._virtualImage.idEnterprise == 0 && !this._securableResource.applyAuthorization(AbiCloudModel.getInstance().authorizationManager))
                makeUnavailable();
            else
            {
                //The resource is authorized.
                makeAvailable();
            }
        }

        public function makeUnavailable():void
        {
            this.visible = false;
            this.includeInLayout = false;
        }

        public function makeAvailable():void
        {
            this.visible = true;
            this.includeInLayout = true;
        }

    }
}