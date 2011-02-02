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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;

public class VirtualApplianceConversionsHB extends PendingEventConversion
{
    /**
     * 
     */
    private static final long serialVersionUID = -1678524086102622464L;

    public VirtualApplianceConversionsHB()
    {
    }

    private int id;

    private VirtualappHB virtualAppliance;

    private Integer idUser;

    private boolean forceLimits;

    public VirtualApplianceConversionsHB(VirtualImageConversionsHB conversion,
        VirtualappHB virtualAppliance, NodeVirtualImageHB nodeVirtualImage, int idUser,
        boolean force)
    {
        super(nodeVirtualImage, conversion);
        this.virtualAppliance = virtualAppliance;
        this.idUser = idUser;
        this.forceLimits = force;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Integer getIdUser()
    {
        return idUser;
    }

    public void setIdUser(Integer idUser)
    {
        this.idUser = idUser;
    }

    public boolean isForceLimits()
    {
        return forceLimits;
    }

    public void setForceLimits(boolean force)
    {
        this.forceLimits = force;
    }

    public VirtualappHB getVirtualAppliance()
    {
        return virtualAppliance;
    }

    public void setVirtualAppliance(VirtualappHB virtualAppliance)
    {
        this.virtualAppliance = virtualAppliance;
    }
}
