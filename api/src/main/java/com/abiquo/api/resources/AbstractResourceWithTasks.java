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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.TaskService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.JobDto;
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
        String ownerId = extractOwnerId(uriInfo);
        List<Task> tasks = service.findTasks(getTaskOwnerType(), ownerId);

        return transform(tasks, uriInfo);
    }

    @GET
    @Path(TASK_PATH)
    public TaskDto getTask(@PathParam(value = "id") final String id,
        @Context final UriInfo uriInfo, @Context final IRESTBuilder restBuilder)
    {
        Task task = service.findTask(id);
        String ownerId = extractOwnerId(uriInfo);

        if (ownerId != task.getOwnerId())
        {
            throw new NotFoundException(APIError.NON_EXISTENT_TASK);
        }

        return transform(task, uriInfo);
    }

    protected String extractOwnerId(final UriInfo uriInfo)
    {
        Class< ? > clazz = this.getClass();
        String ownerId = null;

        if (clazz.isAnnotationPresent(Parent.class))
        {
            Parent parent = this.getClass().getAnnotation(Parent.class);
            clazz = parent.value();
        }

        if (clazz.isAnnotationPresent(Path.class))
        {
            Path path = this.getClass().getAnnotation(Path.class);
            String segment = path.value();

            String regex = String.format(".*%s/", segment);
            ownerId = uriInfo.getPath().replaceAll(regex, "");

            regex = String.format("%s.*", TASKS_PATH);
            ownerId = ownerId.replaceAll(regex, "");
        }

        if (StringUtils.isEmpty(ownerId))
        {
            throw new NotFoundException(APIError.TASK_OWNER_NOT_FOUND);
        }

        return ownerId;
    }

    protected TasksDto transform(List<Task> tasks, UriInfo uriInfo)
    {
        TasksDto dto = new TasksDto();

        for (Task task : tasks)
        {
            dto.add(transform(task, uriInfo));
        }

        return dto;
    }

    protected TaskDto transform(Task task, UriInfo uriInfo)
    {
        // Build self link
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        RESTLink selfLink = new RESTLink();
        selfLink.setRel("self");
        selfLink.setHref(uriBuilder.path(TASK_PATH).build(task.getTaskId()).getPath());

        // Build the TaskDto
        TaskDto dto = new TaskDto();

        dto.addLink(selfLink);
        dto.setTaskId(task.getTaskId());
        dto.setOwnerId(task.getOwnerId());
        dto.setUserId(task.getUserId());
        dto.setType(task.getType());
        dto.setState(task.getState());
        dto.setTimestamp(task.getTimestamp());

        for (Job job : task.getJobs())
        {
            dto.getJobs().add(transform(job));
        }

        return dto;
    }

    protected JobDto transform(Job job)
    {
        JobDto dto = new JobDto();

        dto.setId(job.getId());
        dto.setParentTaskId(job.getParentTaskId());
        dto.setType(job.getType());
        dto.setDescription(job.getDescription());
        dto.setState(job.getState());
        dto.setRollbackState(job.getRollbackState());
        dto.setTimestamp(job.getTimestamp());

        return dto;
    }
}
