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

package com.abiquo.scheduler.limit;

public class LimitExceededExceptionNoDetail extends LimitExceededException
{
    private static final long serialVersionUID = 7377142657394476370L;

    public LimitExceededExceptionNoDetail(LimitExceededException e)
    {
        super(e.getResourcesStatus(), e.getEntity(), e.getRequirements(), e.getActual(), e
            .getEntityId());
    }

    @Override
    public String toString()
    {
        return String.format("Not enough resources on %s ", this.getEntityId());
    }

}
