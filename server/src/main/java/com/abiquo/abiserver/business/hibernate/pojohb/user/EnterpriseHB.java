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

package com.abiquo.abiserver.business.hibernate.pojohb.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualhardware.DatacenterLimit;

/**
 * This object is the hibernate object of the enterprise.
 * 
 * @author xfernandez
 */
public class EnterpriseHB implements java.io.Serializable, IPojoHB<Enterprise>
{

    /** The generated serial Version UID. */
    private static final long serialVersionUID = -4186633712801242561L;

    /** The Enterprise identification. */
    private Integer idEnterprise;

    /** The enterprise name */
    private String name;

    /** kind of reservation */
    private Boolean isReservationRestricted;

    private ResourceAllocationLimitHB limits;

    private Collection<PhysicalmachineHB> reservedMachines;

    /** List of limits established by Datacenter */
    private Set<DatacenterLimitHB> dcLimits;
    
    /** assigned pricing */
    private PricingHB pricingHB;
    
    
    
    
    public EnterpriseHB()
    {
        super();

        reservedMachines = new HashSet<PhysicalmachineHB>();
        // Load default limits
        limits = AbiConfigManager.getInstance().getAbiConfig().getResourceReservationLimits();

        // ByDefault, all dc should be available
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
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
    public ResourceAllocationLimitHB getLimits()
    {
        return limits;
    }

    /**
     * @param limits the limits to set
     */
    public void setLimits(final ResourceAllocationLimitHB limits)
    {
        this.limits = limits;
    }

    public void setReservedMachines(Collection<PhysicalmachineHB> reservedMachines)
    {
        this.reservedMachines = reservedMachines;
    }

    public Collection<PhysicalmachineHB> getReservedMachines()
    {
        return reservedMachines;
    }

    public void setDcLimits(Set<DatacenterLimitHB> dcLimits)
    {
        this.dcLimits = dcLimits;
    }

    public Set<DatacenterLimitHB> getDcLimits()
    {
        return dcLimits;
    }
    
    public PricingHB getPricingHB()
    {
        return pricingHB;
    }

    public void setPricingHB(final PricingHB pricingHB)
    {
        this.pricingHB = pricingHB;
    }
    

    /**
     * This method create a generic enterprise pojo object.
     */
    @Override
    public Enterprise toPojo()
    {
        Enterprise enterprise = new Enterprise();

        enterprise.setId(getIdEnterprise());
        enterprise.setName(getName());
        enterprise.setLimits(limits.toPojo());
        enterprise.setIsReservationRestricted(isReservationRestricted);
        enterprise.setPricing(pricingHB == null ? null : pricingHB.toPojo());

        Set<DatacenterLimit> dcLimitsPojo = new HashSet<DatacenterLimit>();

        for (DatacenterLimitHB dcLimitHB : dcLimits)
        {
            dcLimitsPojo.add(dcLimitHB.toPojo());
        }

        enterprise.setDcLimits(dcLimitsPojo);

        ArrayList<PhysicalMachine> rMachines = new ArrayList<PhysicalMachine>();

        for (PhysicalmachineHB physicalMachineHB : reservedMachines)
        {
            rMachines.add(physicalMachineHB.toPojo());
        }

        enterprise.setReservedMachines(rMachines);

        return enterprise;
    }

}
