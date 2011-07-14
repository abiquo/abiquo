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

import com.abiquo.server.core.cloud.VirtualMachine;

/**
 * Holds the virtual image requirements and other additional virtual machine configuration (storage
 * and volumes).
 * 
 * @author apuig
 **/
public class VirtualMachineRequirements
{
    /** Required CPU by the virtual image. */
    protected final Long cpu;

    /** Required RAM by the virtual image. TODO units. */
    protected final Long ram;

    /** Required HD by the virtual image. TODO units. */
    protected final Long hd;

    /** Required space on the Datacenter repository (NFS) virtual image disk file size. TODO units. */
    protected final Long repository;

    /** Required space on external storage (all the attached volume size). */
    protected Long storage;

    /** Required public VLAN count. */
    protected Long publicVLAN;

    /** Required public IP count. */
    protected Long publicIP;

    protected VirtualMachineRequirements()
    {
        this.cpu = 0l;
        this.ram = 0l;
        this.hd = 0l;

        this.repository = 0l;

        this.storage = 0l;
        this.publicIP = 0l;
        this.publicVLAN = 0l;
    }

    public VirtualMachineRequirements(final VirtualMachine vmachine)
    {
        this.cpu = Long.valueOf(vmachine.getCpu());
        this.ram = Long.valueOf(vmachine.getRam());
        this.hd =
            vmachine.getVirtualImage().getStateful() == 1 ? 0 : Long.valueOf(vmachine.getHdInBytes());

        this.repository = vmachine.getVirtualImage().getDiskFileSize();

        this.storage = 0l;
        this.publicIP = 0l;
        this.publicVLAN = 0l;
    }

    public VirtualMachineRequirements(Long cpu, Long ram, Long hd, Long repository, Long storage,
        Long publicVLAN, Long publicIP)
    {
        super();
        this.cpu = cpu;
        this.ram = ram;
        this.hd = hd;
        this.repository = repository;
        this.storage = storage;
        this.publicVLAN = publicVLAN;
        this.publicIP = publicIP;
    }

    public Long getCpu()
    {
        return cpu;
    }

    public Long getRam()
    {
        return ram;
    }

    public Long getHd()
    {
        return hd;
    }

    public Long getStorage()
    {
        return storage;
    }

    public Long getRepository()
    {
        return repository;
    }

    public Long getPublicVLAN()
    {
        return publicVLAN;
    }

    public Long getPublicIP()
    {
        return publicIP;
    }

}
