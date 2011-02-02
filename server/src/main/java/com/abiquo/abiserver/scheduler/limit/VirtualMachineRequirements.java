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

package com.abiquo.abiserver.scheduler.limit;

/**
 * Holds the virtual image requirements and other additional virtual machine configuration (storage
 * and volumes).
 * 
 * @author apuig
 **/
public class VirtualMachineRequirements
{
    /** Required CPU by the virtual image. */
    protected Long cpu;

    /** Required RAM by the virtual image. TODO units. */
    protected Long ram;

    /** Required HD by the virtual image. TODO units. */
    protected Long hd;

    /** Required space on the Datacenter repository (NFS) virtual image disk file size. TODO units. */
    protected Long repository;

    /** Required space on external storage (all the attached volume size). */
    protected Long storage;

    /** Required private VLAN count. */
    protected Long privateVLAN;

    /** Required public IP count. */
    protected Long publicIP;

    public VirtualMachineRequirements()
    {
        this.cpu = 0l;
        this.ram = 0l;
        this.hd = 0l;

        this.repository = 0l;

        this.storage = 0l;
        this.publicIP = 0l;
        this.privateVLAN = 0l;
    }

    public Long getCpu()
    {
        return cpu;
    }

    public void setCpu(Long cpu)
    {
        this.cpu = cpu;
    }

    public Long getRam()
    {
        return ram;
    }

    public void setRam(Long ram)
    {
        this.ram = ram;
    }

    public Long getHd()
    {
        return hd;
    }

    public void setHd(Long hd)
    {
        this.hd = hd;
    }

    public Long getRepository()
    {
        return repository;
    }

    public void setRepository(Long repository)
    {
        this.repository = repository;
    }

    public Long getStorage()
    {
        return storage;
    }

    public void setStorage(Long storage)
    {
        this.storage = storage;
    }

    public Long getPrivateVLAN()
    {
        return privateVLAN;
    }

    public void setPrivateVLAN(Long publicVLAN)
    {
        this.privateVLAN = publicVLAN;
    }

    public Long getPublicIP()
    {
        return publicIP;
    }

    public void setPublicIP(Long publicIP)
    {
        this.publicIP = publicIP;
    }

}
