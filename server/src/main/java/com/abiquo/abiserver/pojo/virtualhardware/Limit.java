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

package com.abiquo.abiserver.pojo.virtualhardware;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.pojo.IPojo;

/**
 * An allocation limit.
 * 
 * @author ibarrera
 */
public class Limit implements Serializable, IPojo<LimitHB>
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

    public Limit()
    {
        super();
    }

    public Limit(final long hard, final long soft)
    {
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
        this.soft = soft;
    }

    @Override
    public LimitHB toPojoHB()
    {
        return new LimitHB(hard, soft);
    }

    @Override
    public String toString()
    {
        return String.format("[Hard: %d, Soft: %d]", hard, soft);
    }

}
