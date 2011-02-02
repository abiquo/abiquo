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
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;

/**
 * @see com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB
 */
public class ResourceAllocationLimitHB implements Serializable, IPojoHB<ResourceAllocationLimit>
{
    private static final long serialVersionUID = 1L;

    private LimitHB cpu = new LimitHB();

    private LimitHB ram = new LimitHB();

    private LimitHB hd = new LimitHB();

    private LimitHB storage = new LimitHB();

    private LimitHB repository = new LimitHB();

    private LimitHB vlan = new LimitHB();

    private LimitHB publicIP = new LimitHB();

    /**
     * @return the cpu
     */
    public LimitHB getCpu()
    {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(final LimitHB cpu)
    {
        this.cpu = cpu;
    }

    /**
     * @return the ram
     */
    public LimitHB getRam()
    {
        return ram;
    }

    /**
     * @param ram the ram to set
     */
    public void setRam(final LimitHB ram)
    {
        this.ram = ram;
    }

    /**
     * @return the hd
     */
    public LimitHB getHd()
    {
        return hd;
    }

    /**
     * @param hd the hd to set
     */
    public void setHd(final LimitHB hd)
    {
        this.hd = hd;
    }

    /**
     * @return the storage
     */
    public LimitHB getStorage()
    {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(final LimitHB storage)
    {
        this.storage = storage;
    }

    /**
     * @return the repository
     */
    public LimitHB getRepository()
    {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(final LimitHB repository)
    {
        this.repository = repository;
    }

    /**
     * @return the vlans
     */
    public LimitHB getVlan()
    {
        return vlan;
    }

    /**
     * @param vlan the publicVLAN to set
     */
    public void setVlan(final LimitHB vlan)
    {
        this.vlan = vlan;
    }

    /**
     * @return the publicIP
     */
    public LimitHB getPublicIP()
    {
        return publicIP;
    }

    /**
     * @param publicIP the publicIP to set
     */
    public void setPublicIP(final LimitHB publicIP)
    {
        this.publicIP = publicIP;
    }

    @Override
    public ResourceAllocationLimit toPojo()
    {
        ResourceAllocationLimit ral = new ResourceAllocationLimit();

        ral.setCpu(cpu.toPojo());
        ral.setHd(hd.toPojo());
        ral.setPublicIP(publicIP.toPojo());
        ral.setVlan(vlan.toPojo());
        ral.setRam(ram.toPojo());
        ral.setStorage(storage.toPojo());

        if (repository != null)
        {
            ral.setRepository(repository.toPojo());
        }

        return ral;
    }

    @Override
    public String toString()
    {
        return String.format("CPU %s, RAM %s, HD %s, STORAGE %s, "
            + "REPOSITORY %s, VLAN %s, IP %s, ", cpu.toString(), ram.toString(), hd.toString(),
            storage.toString(), repository.toString(), vlan.toString(), publicIP.toString());
    }

}
