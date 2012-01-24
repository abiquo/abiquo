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

/**
 * 
 */
package com.abiquo.abiserver.pojo.virtualappliance;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.server.core.task.Task;

/**
 * @author jaume
 * @author <a href="mailto:serafin.sedano@abiquo.com">Serafin Sedano</a>
 */
public class TaskStatus
{
    private List<Task> tasks;

    private List<String> uris;

    public List<Task> getTasks()
    {
        if (tasks == null)
        {
            tasks = new ArrayList<Task>();
        }
        return tasks;
    }

    public void setTasks(final List<Task> tasks)
    {
        this.tasks = tasks;
    }

    public void addTask(final Task task)
    {
        this.getTasks().add(task);
    }

    public List<String> getUris()
    {
        if (uris == null)
        {
            uris = new ArrayList<String>();
        }
        return uris;
    }

    public void setUris(final List<String> uris)
    {
        this.uris = uris;
    }

    public void addUri(final String uri)
    {
        this.getUris().add(uri);
    }
}
