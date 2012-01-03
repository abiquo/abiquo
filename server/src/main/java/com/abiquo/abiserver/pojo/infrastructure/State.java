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

package com.abiquo.abiserver.pojo.infrastructure;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;

public class State implements Serializable
{
    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(State.class);

    private static final long serialVersionUID = 1L;

    private int id;

    private String description;

    public State()
    {
        this(StateEnum.NOT_ALLOCATED);
    }

    public State(final StateEnum state)
    {
        this(state.id(), state.toString());
    }

    public State(final int id)
    {
        this(id, StateEnum.fromId(id).toString());
    }

    public State(final int id, final String description)
    {
        this.id = id;
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
    {
        this.id = id;
        description = StateEnum.fromId(id).toString();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public StateEnum toEnum()
    {
        StateEnum result = null;
        try
        {
            result = StateEnum.valueOf(description);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("State description not recognized : " + description, e);
            result = StateEnum.UNKNOWN;
        }
        return result;
    }
}
