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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.LogHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.user.Enterprise;

/**
 * This class represents a Virtual Appliance
 * 
 * @author Oliver
 */

public class VirtualAppliance implements IPojo<VirtualappHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    private String name;

    private Boolean isPublic;

    private State state;

    // private State subState;

    private Boolean highDisponibility;

    // Array containing a list of nodes
    // This array may not be always filled, depending on how the Virtual Appliance
    // is retrieved with Hibernate
    private Collection<Node> nodes;

    // String with an XML document that contains the relations between nodes
    private String nodeConnections;

    // Array containing a Log list
    private ArrayList<Log> logs;

    /**
     * false - no errors true - errors
     */
    private Boolean error;

    /**
     * The virtualDataCenter identification.
     */
    private VirtualDataCenter virtualDataCenter;

    /**
     * The Enterprise to which this Virtual Appliance belongs it may be null, if this virtual
     * appliance is not assigned to any Enterprise
     */
    private Enterprise enterprise;

    public VirtualAppliance()
    {
        nodes = new ArrayList<Node>();
    }

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Boolean getIsPublic()
    {
        return isPublic;
    }

    public void setIsPublic(final Boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

    // /**
    // * @param subState the subState to set
    // */
    // public void setSubState(final State subState)
    // {
    // this.subState = subState;
    // }
    //
    // /**
    // * @return the subState
    // */
    // public State getSubState()
    // {
    // return subState;
    // }

    public Boolean getHighDisponibility()
    {
        return highDisponibility;
    }

    public void setHighDisponibility(final Boolean highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    public Collection<Node> getNodes()
    {
        return nodes;
    }

    public void setNodes(final Collection<Node> nodes)
    {
        this.nodes = nodes;
    }

    public String getNodeConnections()
    {
        return nodeConnections;
    }

    public void setNodeConnections(final String nodeConnections)
    {
        this.nodeConnections = nodeConnections;
    }

    public Boolean getError()
    {
        return error;
    }

    public void setError(final Boolean error)
    {
        this.error = error;
    }

    public VirtualDataCenter getVirtualDataCenter()
    {
        return virtualDataCenter;
    }

    public void setVirtualDataCenter(final VirtualDataCenter virtualDataCenter)
    {
        this.virtualDataCenter = virtualDataCenter;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public ArrayList<Log> getLogs()
    {
        return logs;
    }

    public void setLogs(final ArrayList<Log> logs)
    {
        this.logs = logs;
    }

    /**
     * This method transform the pojo object to hibernate pojo object.
     */
    @Override
    public VirtualappHB toPojoHB()
    {
        VirtualappHB virtualappHB = new VirtualappHB();
        virtualappHB.setIdVirtualApp(id);
        virtualappHB.setName(name);
        virtualappHB.setHighDisponibility(highDisponibility ? 1 : 0);
        virtualappHB.setPublic_(isPublic ? 1 : 0);
        virtualappHB.setState(StateEnum.fromId(state.getId()));
        // virtualappHB.setSubState(StateEnum.fromId(subState.getId()));
        virtualappHB.setError(error ? 1 : 0);
        virtualappHB.setVirtualDataCenterHB(virtualDataCenter.toPojoHB());
        virtualappHB.setNodeConnections(nodeConnections);

        if (enterprise != null)
        {
            virtualappHB.setEnterpriseHB(enterprise.toPojoHB());
        }
        else
        {
            virtualappHB.setEnterpriseHB(null);
        }

        if (logs != null)
        {
            Set<LogHB> logsHB = new HashSet<LogHB>(0);
            for (Log log : logs)
            {
                logsHB.add(log.toPojoHB());
            }

            virtualappHB.setLogsHB(logsHB);
        }
        else
        {
            virtualappHB.setLogsHB(null);
        }

        if (nodes != null)
        {
            Set<NodeHB< ? >> nodesHB = new HashSet<NodeHB< ? >>(0);
            for (Node node : nodes)
            {
                nodesHB.add(node.toPojoHB());
            }

            virtualappHB.setNodesHB(nodesHB);
        }
        else
        {
            virtualappHB.setNodesHB(null);
        }

        return virtualappHB;

    }

}
