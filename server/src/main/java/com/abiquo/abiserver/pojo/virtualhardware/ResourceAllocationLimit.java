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

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.model.transport.SingleResourceWithLimitsDto;

/**
 * @see com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB
 */
public class ResourceAllocationLimit implements Serializable, IPojo<ResourceAllocationLimitHB>
{
    private static final long serialVersionUID = 1L;

    private Limit cpu = new Limit();

    private Limit ram = new Limit();

    private Limit hd = new Limit();

    private Limit storage = new Limit();

    private Limit repository = new Limit();

    private Limit vlan = new Limit();

    private Limit publicIP = new Limit();

    /**
     * @return the cpu
     */
    public Limit getCpu()
    {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(final Limit cpu)
    {
        this.cpu = cpu;
    }

    /**
     * @return the ram
     */
    public Limit getRam()
    {
        return ram;
    }

    /**
     * @param ram the ram to set
     */
    public void setRam(final Limit ram)
    {
        this.ram = ram;
    }

    /**
     * @return the hd
     */
    public Limit getHd()
    {
        return hd;
    }

    /**
     * @param hd the hd to set
     */
    public void setHd(final Limit hd)
    {
        this.hd = hd;
    }

    /**
     * @return the storage
     */
    public Limit getStorage()
    {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(final Limit storage)
    {
        this.storage = storage;
    }

    /**
     * @return the repository
     */
    public Limit getRepository()
    {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(final Limit repository)
    {
        this.repository = repository;
    }

    /**
     * @return the publicVLAN
     */
    public Limit getVlan()
    {
        return vlan;
    }

    /**
     * @param vlan the publicVLAN to set
     */
    public void setVlan(final Limit vlan)
    {
        this.vlan = vlan;
    }

    /**
     * @return the publicIP
     */
    public Limit getPublicIP()
    {
        return publicIP;
    }

    /**
     * @param publicIP the publicIP to set
     */
    public void setPublicIP(final Limit publicIP)
    {
        this.publicIP = publicIP;
    }

    @Override
    public ResourceAllocationLimitHB toPojoHB()
    {
        ResourceAllocationLimitHB ralHB = new ResourceAllocationLimitHB();

        ralHB.setCpu(cpu.toPojoHB());
        ralHB.setHd(hd.toPojoHB());
        ralHB.setPublicIP(publicIP.toPojoHB());
        ralHB.setVlan(vlan.toPojoHB());
        ralHB.setRam(ram.toPojoHB());
        ralHB.setStorage(storage.toPojoHB());
        ralHB.setRepository(repository == null ? null : repository.toPojoHB());

        return ralHB;
    }

    @Override
    public String toString()
    {
        return String.format("CPU %s, RAM %s, HD %s, STORAGE %s, "
            + "REPOSITORY %s, VLAN %s, IP %s, ", cpu.toString(), ram.toString(), hd.toString(),
            storage.toString(), repository.toString(), vlan.toString(), publicIP.toString());
    }

    public static ResourceAllocationLimit create(final SingleResourceWithLimitsDto dto)
    {
        ResourceAllocationLimit limits = new ResourceAllocationLimit();

        limits.setCpu(new Limit(dto.getCpuCountHardLimit(), dto.getCpuCountSoftLimit()));
        limits.setHd(new Limit(dto.getHdHardLimitInMb(), dto.getHdSoftLimitInMb()));
        limits.setRam(new Limit(dto.getRamHardLimitInMb(), dto.getRamSoftLimitInMb()));
        limits.setStorage(new Limit(dto.getStorageHard(), dto.getStorageSoft()));
        limits.setVlan(new Limit(dto.getVlansHard(), dto.getVlansSoft()));
        limits.setPublicIP(new Limit(dto.getPublicIpsHard(), dto.getPublicIpsSoft()));

        return limits;
    }

}
