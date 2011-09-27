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

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.enterprise.Approval;

@XmlRootElement(name = "virtualAppliance")
public class VirtualApplianceDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private String nodecollections;

    public String getNodecollections()
    {
        return nodecollections;
    }

    public void setNodecollections(final String nodecollections)
    {
        this.nodecollections = nodecollections;
    }

    private int publicApp;

    public int getPublicApp()
    {
        return publicApp;
    }

    public void setPublicApp(final int publicApp)
    {
        this.publicApp = publicApp;
    }

    private int highDisponibility;

    public int getHighDisponibility()
    {
        return highDisponibility;
    }

    public void setHighDisponibility(final int highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    private int error;

    public int getError()
    {
        return error;
    }

    public void setError(final int error)
    {
        this.error = error;
    }

    private State subState;

    public State getSubState()
    {
        return subState;
    }

    public void setSubState(final State subState)
    {
        this.subState = subState;
    }

    private State state;

    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

    private Approval approval;

    public void setApproval(final Approval approval)
    {
        this.approval = approval;
    }

    public Approval getApproval()
    {
        return approval;
    }
}
