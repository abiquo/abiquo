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

package com.abiquo.server.core.tasks;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Task.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Task.TABLE_NAME)
public class Task extends DefaultEntityBase
{
    public static final String TABLE_NAME = "tasks";

    protected Task()
    {

    }

    public Task(Status status)
    {
        setStatus(status);
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return id;
    }

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Required(true)
    public Status getStatus()
    {
        return status;
    }

    private void setStatus(Status status)
    {
        this.status = status;
    }

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = "component", nullable = false)
    private Component component;

    public Component getComponent()
    {
        return component;
    }

    public void setComponent(Component component)
    {
        this.component = component;
    }

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;

    public Action getAction()
    {
        return action;
    }

    public void setAction(Action action)
    {
        this.action = action;
    }

    public enum Status
    {
        PENDING, FINISHED, FAILED;
    }

    public enum Component
    {
        VIRTUAL_APPLIANCE, VIRTUAL_IMAGE;
    }

    public enum Action
    {
        DEPLOY, BUNDLE;
    }
}
