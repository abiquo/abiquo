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

package com.abiquo.abiserver.pojo.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.virtualhardware.DatacenterLimit;
import com.abiquo.abiserver.pojo.virtualhardware.Limit;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceAllocationLimit;
import com.abiquo.server.core.enterprise.EnterpriseDto;

/**
 * This pojo class store the information of the enterprise. On abiCloud, an user belongs to an
 * enterprise.
 * 
 * @author abiquo
 */
public class Enterprise implements IPojo<EnterpriseHB>
{

    /**
     * The Enterprise identification.
     */
    private Integer id;

    /**
     * The enterprise name
     */
    private String name;

    /** kind of reservation */
    private Boolean isReservationRestricted;

    private ResourceAllocationLimit limits;

    private Collection<PhysicalMachine> reservedMachines;

    private Set<DatacenterLimit> dcLimits;

    private String defaultTheme;

    public Enterprise()
    {
        reservedMachines = new ArrayList<PhysicalMachine>();
        dcLimits = new HashSet<DatacenterLimit>();
        defaultTheme = "abiquoDefault";
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Boolean getIsReservationRestricted()
    {
        return isReservationRestricted;
    }

    public void setIsReservationRestricted(Boolean isReservationRestricted)
    {
        this.isReservationRestricted = isReservationRestricted;
    }

    /**
     * @return the limits
     */
    public ResourceAllocationLimit getLimits()
    {
        return limits;
    }

    /**
     * @param limits the limits to set
     */
    public void setLimits(final ResourceAllocationLimit limits)
    {
        this.limits = limits;
    }

    public void setReservedMachines(Collection<PhysicalMachine> reservedMachines)
    {
        this.reservedMachines = reservedMachines;
    }

    public Collection<PhysicalMachine> getReservedMachines()
    {
        return reservedMachines;
    }

    public Set<DatacenterLimit> getDcLimits()
    {
        return dcLimits;
    }

    public void setDcLimits(Set<DatacenterLimit> dcLimits)
    {
        this.dcLimits = dcLimits;
    }

    public String getDefaultTheme()
    {
        return this.defaultTheme;
    }

    public void setDefaultTheme(String defaultTheme)
    {
        this.defaultTheme = defaultTheme;
    }

    public void addDatacenterLimit(DatacenterLimit limit)
    {
        if (dcLimits == null)
        {
            dcLimits = new LinkedHashSet<DatacenterLimit>();
        }
        dcLimits.add(limit);
    }

    public void addReservedMachine(PhysicalMachine machine)
    {
        if (reservedMachines == null)
        {
            reservedMachines = new LinkedHashSet<PhysicalMachine>();
        }
        reservedMachines.add(machine);
    }

    /**
     * This method transform the current enterprise pojo object to a enterprise hibernate pojo
     * object
     */
    @Override
    public EnterpriseHB toPojoHB()
    {
        EnterpriseHB enterpriseHB = new EnterpriseHB();

        enterpriseHB.setIdEnterprise(getId());
        enterpriseHB.setName(getName());
        enterpriseHB.setIsReservationRestricted(getIsReservationRestricted());

        if (limits != null)
        {
            // Default limit values will be loaded if null
            enterpriseHB.setLimits(getLimits().toPojoHB());
        }

        // Datacenter Limits
        Set<DatacenterLimitHB> dcLimitsHB = new HashSet<DatacenterLimitHB>();
        for (DatacenterLimit dcLimit : dcLimits)
        {
            if (dcLimit.getDatacenter() != null)
            {
                DatacenterLimitHB limit = dcLimit.toPojoHB();
                limit.setEnterprise(enterpriseHB);
                dcLimitsHB.add(limit);
            }
        }
        enterpriseHB.setDcLimits(dcLimitsHB);

        Collection<PhysicalmachineHB> reservedMachinesHB = new HashSet<PhysicalmachineHB>();

        for (PhysicalMachine physicalmachine : reservedMachines)
        {
            reservedMachinesHB.add(physicalmachine.toPojoHB());
        }

        enterpriseHB.setReservedMachines(reservedMachinesHB);

        return enterpriseHB;
    }

    public static Enterprise create(EnterpriseDto dto)
    {
        Enterprise enterprise = new Enterprise();

        enterprise.setId(dto.getId());
        enterprise.setName(dto.getName());
        enterprise.setIsReservationRestricted(dto.getIsReservationRestricted());

        ResourceAllocationLimit ral = new ResourceAllocationLimit();

        ral.setCpu(new Limit(dto.getCpuCountHardLimit(), dto.getCpuCountSoftLimit()));
        ral.setHd(new Limit(dto.getHdHardLimitInMb(), dto.getHdSoftLimitInMb()));
        ral.setPublicIP(new Limit(dto.getPublicIpsHard(), dto.getPublicIpsSoft()));
        ral.setVlan(new Limit(dto.getVlansHard(), dto.getVlansSoft()));
        ral.setRam(new Limit(dto.getRamHardLimitInMb(), dto.getRamSoftLimitInMb()));
        ral.setStorage(new Limit(dto.getStorageHard(), dto.getStorageSoft()));

        enterprise.setLimits(ral);

        return enterprise;
    }
}
