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
import com.abiquo.server.core.task.TasksDto;

@XmlRootElement(name = "virtualAppliance")
public class VirtualApplianceDto extends SingleResourceTransportDto
{
    public static final String COPY_VIRTUAL_APPLIANCE_MIME_TYPE =
        "application/vnd.cp-virtualappliance+xml";

    public static final String MOVE_VIRTUAL_APPLIANCE_MIME_TYPE =
        "application/vnd.mv-virtualappliance+xml";

    /**
     * 
     */
    private static final long serialVersionUID = 6614050007994524638L;
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.virtualappliance+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

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

    private String nodeconnections;

    public String getNodeconnections()
    {
        return nodeconnections;
    }

    public void setNodeconnections(final String nodeconnections)
    {
        this.nodeconnections = nodeconnections;
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

    private VirtualApplianceState subState;

    public VirtualApplianceState getSubState()
    {
        return subState;
    }

    public void setSubState(final VirtualApplianceState subState)
    {
        this.subState = subState;
    }

    private VirtualApplianceState state;

    public VirtualApplianceState getState()
    {
        return state;
    }

    public void setState(final VirtualApplianceState state)
    {
        this.state = state;
    }
    
    @Override
    public String getMediaType()
    {
        return VirtualApplianceDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

    /**
     * TODO This should be abstracted
     */
    private TasksDto lastTasks;

    public TasksDto getLastTasks()
    {
        return lastTasks;
    }

    public void setLastTasks(final TasksDto lastTasks)
    {
        this.lastTasks = lastTasks;
    }

}
