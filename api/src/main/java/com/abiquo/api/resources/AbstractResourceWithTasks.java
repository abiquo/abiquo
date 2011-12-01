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

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.TaskService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.JobDto;
import com.abiquo.server.core.task.JobsDto;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.task.enums.TaskOwnerType;

// TODO
// Add link to tasks in parent
// Validate entire URI

/**
 * Abstract resource to add asynchronous task management capabilities to an existent API Resource.
 * 
 * @author eruiz
 */
public abstract class AbstractResourceWithTasks extends AbstractResource
{
    protected static final String TASKS_PATH = "/tasks";

    protected static final String TASK_PATH = TASKS_PATH + "/{id}";

    protected static final String SELF_REL = "self";

    protected static final String PARENT_REL = "parent";

    protected abstract TaskOwnerType getTaskOwnerType();

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
            Path path = clazz.getAnnotation(Path.class);
            String segment = path.value();

            ownerId = removeTaskSegments(uriInfo.getPath());

            String regex = String.format(".*%s/", segment);
            ownerId = ownerId.replaceAll(regex, "");
        }

        if (StringUtils.isEmpty(ownerId))
        {
            throw new NotFoundException(APIError.TASK_OWNER_NOT_FOUND);
        }

        return ownerId;
    }

    protected String removeTaskSegments(final String path)
    {
        String regex = String.format("%s.*", TASKS_PATH);
        return path.replaceAll(regex, "");
    }

    protected TasksDto transform(List<Task> tasks, UriInfo uriInfo)
    {
        TasksDto dto = new TasksDto();

        // Build links
        String selfHref = uriInfo.getRequestUri().toString();
        String parentHref = removeTaskSegments(selfHref);

        addLink(dto, SELF_REL, selfHref);
        addLink(dto, PARENT_REL, parentHref);

        // Add each task
        for (Task task : tasks)
        {
            dto.add(transform(task, uriInfo));
        }

        return dto;
    }

    protected TaskDto transform(Task task, UriInfo uriInfo)
    {
        TaskDto dto = new TaskDto();

        // Build links
        String parentHref = uriInfo.getRequestUri().toString();
        String selfHref = parentHref.concat("/" + task.getTaskId());

        addLink(dto, SELF_REL, selfHref);
        addLink(dto, PARENT_REL, parentHref);

        // Add fields
        dto.setTaskId(task.getTaskId());
        dto.setOwnerId(task.getOwnerId());
        dto.setUserId(task.getUserId());
        dto.setType(task.getType());
        dto.setState(task.getState());
        dto.setTimestamp(task.getTimestamp());
        dto.setJobs(transform(task.getJobs()));

        return dto;
    }

    protected JobsDto transform(List<Job> jobs)
    {
        JobsDto jobsDto = new JobsDto();

        for (Job job : jobs)
        {
            JobDto jobDto = new JobDto();

            jobDto.setId(job.getId());
            jobDto.setParentTaskId(job.getParentTaskId());
            jobDto.setType(job.getType());
            jobDto.setDescription(job.getDescription());
            jobDto.setState(job.getState());
            jobDto.setRollbackState(job.getRollbackState());
            jobDto.setTimestamp(job.getTimestamp());

            jobsDto.getCollection().add(jobDto);
        }

        return jobsDto;
    }

    protected SingleResourceTransportDto addLink(SingleResourceTransportDto dto, final String rel,
        final String href)
    {
        dto.addLink(new RESTLink(rel, href));
        return dto;
    }
}
