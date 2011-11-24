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

package com.abiquo.api.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.api.services.TaskService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.task.enums.TaskOwnerType;

public abstract class AbstractResourceWithTasks extends AbstractResource
{
    public static final String TASKS_PATH = "/tasks";

    public static final String TASK_PATH = TASKS_PATH + "/{id}";

    public abstract TaskOwnerType getTaskOwnerType();

    @Autowired
    TaskService service;

    @GET
    @Path(TASKS_PATH)
    public TasksDto getTasks(@Context final UriInfo uriInfo, @Context final IRESTBuilder restBuilder)
    {
        String parentId = extractParentId(uriInfo);
        // Links to all tasks?

        List<Task> tasks = service.findTasks(getTaskOwnerType(), parentId);

        return new TasksDto();
    }

    @GET
    @Path(TASK_PATH)
    public TaskDto getTask(@PathParam(value = "id") final String id,
        @Context final UriInfo uriInfo, @Context final IRESTBuilder restBuilder)
    {
        Task task = service.findTask(id);

        return new TaskDto();
    }

    protected String extractParentId(final UriInfo uriInfo)
    {
        // TODO
        return null;
    }
}
