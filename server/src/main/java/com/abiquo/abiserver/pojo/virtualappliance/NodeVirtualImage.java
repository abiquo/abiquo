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

package com.abiquo.abiserver.pojo.virtualappliance;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;

/**
 * @author Oliver
 */
public class NodeVirtualImage extends Node<NodeVirtualImageHB>
{
    private VirtualImage virtualImage;

    private VirtualMachine virtualMachine;

    public NodeVirtualImage()
    {
        super();
        virtualImage = null;
        virtualMachine = null;
    }

    public NodeVirtualImage(final Node<NodeVirtualImageHB> node)
    {
        super(node);

        virtualImage = null;
        virtualMachine = null;
    }

    public VirtualImage getVirtualImage()
    {
        return virtualImage;
    }

    public void setVirtualImage(final VirtualImage virtualImage)
    {
        this.virtualImage = virtualImage;
    }

    public VirtualMachine getVirtualMachine()
    {
        return virtualMachine;
    }

    public void setVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public boolean isManaged()
    {
        return getVirtualImage().isManaged();
    }

    public boolean isImageStateful()
    {
        return getVirtualImage().isImageStateful();
    }

    @Override
    public NodeVirtualImageHB toPojoHB()
    {
        NodeHB nodeHB = super.toPojoHB();
        NodeVirtualImageHB nodeVirtualImageHB = new NodeVirtualImageHB(nodeHB);

        // Adding NodeVirtualImage's fields
        nodeVirtualImageHB.setVirtualImageHB(virtualImage.toPojoHB());
        if (virtualMachine != null)
        {
            nodeVirtualImageHB.setVirtualMachineHB(virtualMachine.toPojoHB());
        }
        else
        {
            nodeVirtualImageHB.setVirtualMachineHB(null);
        }

        return nodeVirtualImageHB;
    }

}
