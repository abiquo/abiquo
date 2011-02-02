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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.virtualappliance.NodeType;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;
import com.abiquo.server.core.enumerator.HypervisorType;

/**
 * @author Oliver
 */
public class NodeVirtualImageHB extends NodeHB<NodeVirtualImage> // implements
// IPojoHB<NodeVirtualImage>
{
    private static final long serialVersionUID = 2350380932461612409L;

    private VirtualmachineHB virtualMachineHB;

    private VirtualimageHB virtualImageHB;

    public NodeVirtualImageHB()
    {
        super();

        virtualMachineHB = null;
        virtualImageHB = null;
    }

    public NodeVirtualImageHB(final NodeHB<NodeVirtualImage> nodeHB)
    {
        super(nodeHB);

        virtualImageHB = null;
        virtualMachineHB = null;
    }

    public VirtualmachineHB getVirtualMachineHB()
    {
        return virtualMachineHB;
    }

    public void setVirtualMachineHB(final VirtualmachineHB virtualMachineHB)
    {
        this.virtualMachineHB = virtualMachineHB;
    }

    public VirtualimageHB getVirtualImageHB()
    {
        return virtualImageHB;
    }

    public void setVirtualImageHB(final VirtualimageHB virtualImageHB)
    {
        this.virtualImageHB = virtualImageHB;
    }

    @Override
    public NodeVirtualImage toPojo()
    {
        NodeVirtualImage nodeVirtualImage = fillPojo();
        nodeVirtualImage.setVirtualImage(getVirtualImageHB().toPojo());

        return nodeVirtualImage;
    }

    public NodeVirtualImage toDecorator()
    {
        NodeVirtualImage nodeVirtualImage = fillPojo();
        nodeVirtualImage.setVirtualImage(getVirtualImageHB().toDecorator());

        HyperVisor hypervisor = (HyperVisor) nodeVirtualImage.getVirtualMachine().getAssignedTo();

        if (hypervisor.getType().getName().compareTo(HypervisorType.HYPERV_301.getValue()) == 0)
        {
            nodeVirtualImage.getVirtualImage().convertPathToVhd();
        }

        return nodeVirtualImage;
    }

    private NodeVirtualImage fillPojo()
    {
        NodeVirtualImage nodeVirtualImage = new NodeVirtualImage();

        nodeVirtualImage.setId(getIdNode());
        nodeVirtualImage.setIdVirtualAppliance(getIdVirtualApp());
        nodeVirtualImage.setName(getName());
        nodeVirtualImage.setNodeType(new NodeType(getType()));
        nodeVirtualImage.setPosX(getPosX());
        nodeVirtualImage.setPosY(getPosY());
        nodeVirtualImage.setModified(getModified());

        if (virtualMachineHB != null)
        {
            nodeVirtualImage.setVirtualMachine(getVirtualMachineHB().toPojo());
        }

        return nodeVirtualImage;
    }

    public boolean isImageStateful()
    {
        return getVirtualImageHB().isImageStateful();
    }

    public boolean isManaged()
    {
        return getVirtualImageHB().getRepository() != null;
    }
}
