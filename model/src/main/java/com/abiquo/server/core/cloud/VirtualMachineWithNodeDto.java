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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "virtualmachinewithnode")
public class VirtualMachineWithNodeDto extends VirtualMachineDto
{
    /**
     * 
     */
    private static final long serialVersionUID = -8877350185009627544L;

    private Integer nodeId;

    private int x;

    private String nodeName;

    private int y;

    public Integer getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(final Integer nodeId)
    {
        this.nodeId = nodeId;
    }

    public int getX()
    {
        return x;
    }

    public void setX(final int x)
    {
        this.x = x;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(final String nodeName)
    {
        this.nodeName = nodeName;
    }

    public int getY()
    {
        return y;
    }

    public void setY(final int y)
    {
        this.y = y;
    }

}
