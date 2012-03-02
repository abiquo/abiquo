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

package com.abiquo.server.core.cloud;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = NodeVirtualImage.TABLE_NAME)
@DiscriminatorValue(NodeVirtualImage.DISCRIMINATOR)
public class NodeVirtualImage extends Node
{
    public static final String DISCRIMINATOR = "VIRTUAL_IMAGE";

    public static final String TABLE_NAME = "nodevirtualimage";

    public NodeVirtualImage(final String name, final VirtualAppliance virtualAppliance,
        final VirtualMachineTemplate virtualImage, final VirtualMachine virtualMachine)
    {
        super(DISCRIMINATOR);

        setName(name);
        setVirtualAppliance(virtualAppliance);
        setVirtualImage(virtualImage);
        setVirtualMachine(virtualMachine);
    }

    protected NodeVirtualImage()
    {
    }

    public final static String VIRTUAL_IMAGE_PROPERTY = "virtualImage";

    private final static boolean VIRTUAL_IMAGE_REQUIRED = true;

    private final static String VIRTUAL_IMAGE_ID_COLUMN = "idImage";

    @JoinColumn(name = VIRTUAL_IMAGE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualImage")
    private VirtualMachineTemplate virtualImage;

    @Required(value = VIRTUAL_IMAGE_REQUIRED)
    public VirtualMachineTemplate getVirtualImage()
    {
        return this.virtualImage;
    }

    public void setVirtualImage(final VirtualMachineTemplate virtualImage)
    {
        this.virtualImage = virtualImage;
    }

    public final static String VIRTUAL_MACHINE_PROPERTY = "virtualMachine";

    private final static boolean VIRTUAL_MACHINE_REQUIRED = true;

    private final static String VIRTUAL_MACHINE_ID_COLUMN = "idVM";

    @JoinColumn(name = VIRTUAL_MACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualMachine")
    private VirtualMachine virtualMachine;

    @Required(value = VIRTUAL_MACHINE_REQUIRED)
    public VirtualMachine getVirtualMachine()
    {
        return this.virtualMachine;
    }

    public void setVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    @Override
    public void setName(final String name)
    {
        super.setName(name);
    }

    /**
     * This method clone this node virtual image but shares references. Same virtual appliance, same
     * virtual machine template, same virtual machine.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public NodeVirtualImage clone()
    {
        NodeVirtualImage nodeVirtualImage =
            new NodeVirtualImage(this.getName(), virtualAppliance, virtualImage, virtualMachine);
        nodeVirtualImage.setY(getY());
        nodeVirtualImage.setX(getX());
        return nodeVirtualImage;
    }
}
