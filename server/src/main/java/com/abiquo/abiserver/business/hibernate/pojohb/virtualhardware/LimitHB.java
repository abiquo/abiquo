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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.virtualhardware.Limit;

/**
 * An allocation limit.
 * 
 * @author ibarrera
 */
public class LimitHB implements Serializable, IPojoHB<Limit>
{
    private static final long serialVersionUID = 1L;

    /**
     * The hard limit.
     */
    private long hard;

    /**
     * The soft limit.
     */
    private long soft;

    /**
     * Default constructor.
     */
    public LimitHB()
    {
        super();
    }

    /**
     * Creates a new Limit.
     * 
     * @param hard The hard limit.
     * @param soft The soft limit.
     */
    public LimitHB(final long hard, final long soft)
    {
        super();

        if (hard < soft && hard != 0)
        {
            throw new IllegalArgumentException("Hard limit must be greater or equal than soft limit");
        }

        this.hard = hard;
        this.soft = soft;
    }

    /**
     * @return the hard
     */
    public long getHard()
    {
        return hard;
    }

    /**
     * @param hard the hard to set
     */
    public void setHard(final long hard)
    {
        if (hard < soft && hard != 0)
        {
            throw new IllegalArgumentException("Hard limit must be greater or equal than soft limit");
        }

        this.hard = hard;
    }

    /**
     * @return the soft
     */
    public long getSoft()
    {
        return soft;
    }

    /**
     * @param soft the soft to set
     */
    public void setSoft(final long soft)
    {
        if (hard < soft && hard != 0)
        {
            throw new IllegalArgumentException("Hard limit must be greater or equal than soft limit");
        }

        this.soft = soft;
    }

    @Override
    public Limit toPojo()
    {
        Limit limit = new Limit();

        limit.setHard(hard);
        limit.setSoft(soft);

        return limit;
    }

    @Override
    public String toString()
    {
        return String.format("[Hard: %d, Soft: %d]", hard, soft);
    }

}
