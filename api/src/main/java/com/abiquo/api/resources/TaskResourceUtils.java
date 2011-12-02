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

import javax.ws.rs.core.UriInfo;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.JobDto;
import com.abiquo.server.core.task.JobsDto;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TasksDto;

/**
 * Utility methods to manage the redis-backed tasks from any existing resource
 * 
 * @author eruiz
 */
public class TaskResourceUtils extends AbstractResource
{
    public static final String TASKS_PATH = "/tasks";

    public static final String TASK = "id";

    public static final String TASK_PATH = TASKS_PATH + "/{" + TASK + "}";

    protected static final String SELF_REL = "self";

    protected static final String PARENT_REL = "parent";

    protected static final String TASKS_REL = "tasks";

    public static TasksDto transform(List<Task> tasks, UriInfo uriInfo)
    {
        TasksDto dto = new TasksDto();

        // Build links
        String parentHref = removeTaskSegments(uriInfo.getRequestUri().toString());
        String selfHref = parentHref.concat(TASKS_PATH);

        addLink(dto, SELF_REL, selfHref);
        addLink(dto, PARENT_REL, parentHref);

        // Add each task
        for (Task task : tasks)
        {
            dto.add(transform(task, uriInfo));
        }

        return dto;
    }

    public static TaskDto transform(Task task, UriInfo uriInfo)
    {
        TaskDto dto = new TaskDto();

        // Build links
        String uri = removeTaskSegments(uriInfo.getRequestUri().toString());
        String parentHref = uri.concat(TASKS_PATH);
        String selfHref = parentHref.concat("/").concat(task.getTaskId());

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

    public static RESTLink buildTasksLink(final RESTLink baseLink)
    {
        String href = removeEndSlashes(baseLink.getHref());
        return new RESTLink(TASKS_REL, href.concat(TASKS_PATH));
    }

    protected static JobsDto transform(List<Job> jobs)
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

    protected static String removeTaskSegments(final String path)
    {
        String regex = String.format("%s.*", TASKS_PATH);
        return path.replaceAll(regex, "");
    }

    protected static String removeEndSlashes(final String path)
    {
        return path.replaceAll("(/)*$", "");
    }

    protected static SingleResourceTransportDto addLink(SingleResourceTransportDto dto,
        final String rel, final String href)
    {
        dto.addLink(new RESTLink(rel, href));
        return dto;
    }
}
