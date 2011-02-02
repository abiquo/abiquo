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
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeTypeEnum;
import com.abiquo.abiserver.pojo.IPojo;

/**
 * This class represents a Virtual Appliance's node Relations between nodes are from a child to its
 * parent
 * 
 * @author Oliver
 */

public class Node<T extends NodeHB< ? >> implements IPojo<NodeHB< ? >>
{
    /* ------------- Public constants ------------- */
    public static final int NODE_NOT_MODIFIED = 0;

    public static final int NODE_MODIFIED = 1;

    public static final int NODE_ERASED = 2;

    public static final int NODE_NEW = 3;

    public static final int NODE_CRASHED = 4;

    /* ------------- Public attributes ------------- */
    protected int id;

    protected String name;

    protected int idVirtualAppliance;

    protected NodeType nodeType;

    // For drawing purposes
    protected int posX;

    protected int posY;

    // For performance purposes
    // To be set when a node has been modified, when we want to save changes on editing a virtual
    // appliance
    protected int modified;

    /* ------------- Constructor ------------- */
    public Node()
    {
        id = 0;
        name = "";
        idVirtualAppliance = 0;
        nodeType = new NodeType();

        posX = 0;
        posY = 0;

        modified = NODE_NOT_MODIFIED;
    }

    /**
     * Constructs a Node from another one This is useful to convert from a child type Node
     * (NodeVirtualImage, NodeStorage, etc.) to a Node class
     */
    public Node(Node< ? > node)
    {
        id = node.getId();
        name = node.getName();
        idVirtualAppliance = node.getIdVirtualAppliance();
        nodeType = node.getNodeType();
        posX = node.getPosX();
        posY = node.getPosY();
        modified = node.getModified();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getIdVirtualAppliance()
    {
        return idVirtualAppliance;
    }

    public void setIdVirtualAppliance(int idVirtualAppliance)
    {
        this.idVirtualAppliance = idVirtualAppliance;
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType)
    {
        this.nodeType = nodeType;
    }

    public int getPosX()
    {
        return posX;
    }

    public void setPosX(int posX)
    {
        this.posX = posX;
    }

    public int getPosY()
    {
        return posY;
    }

    public void setPosY(int posY)
    {
        this.posY = posY;
    }

    public int getModified()
    {
        return modified;
    }

    public void setModified(int modified)
    {
        this.modified = modified;
    }

    @SuppressWarnings("unchecked")
    public NodeHB toPojoHB()
    {
        NodeHB nodeHB = new NodeHB();
        nodeHB.setModified(modified);
        nodeHB.setIdNode(id);
        nodeHB.setIdVirtualApp(idVirtualAppliance);
        nodeHB.setName(name);
        nodeHB.setType(nodeType.toEnum());
        nodeHB.setPosX(posX);
        nodeHB.setPosY(posY);

        return nodeHB;
    }

    public boolean isNodeTypeVirtualImage()
    {
        return nodeType.toEnum() == NodeTypeEnum.VIRTUAL_IMAGE;
    }
}
