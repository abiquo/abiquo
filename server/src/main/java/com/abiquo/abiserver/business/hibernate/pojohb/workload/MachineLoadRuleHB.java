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

package com.abiquo.abiserver.business.hibernate.pojohb.workload;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.engine.core.Rule;
import com.abiquo.abiserver.pojo.workload.MachineLoadRule;

/**
 * Defines a Machine Load rule.
 * <p>
 * This rule allows to define the RAM and CPU percentage that will be considered on a
 * {@link PhysicalmachineHB} in the scheduler in order to select it as a candidate for deployment.
 * 
 * @author ibarrera
 */
public class MachineLoadRuleHB implements Serializable, IPojoHB<MachineLoadRule>,
    Rule<VirtualimageHB, PhysicalmachineHB, Integer>
{
    /** Serial UID. */
    private static final long serialVersionUID = -7472015457715832653L;

    /** A default and dummy rule. */
    public static final MachineLoadRuleHB DEFAULT_RULE = new MachineLoadRuleWithNoLocationHB();

    /** The rule id. */
    private Integer id;

    /** The RAM load percentage. */
    private int ramLoadPercentage;

    /** The CPU load percentage. */
    private int cpuLoadPercentage;

    /** The Datacenter where this rule applies. */
    private DatacenterHB datacenter;

    /** The Rack where this rule applies. */
    private RackHB rack;

    /** The Physical Machine where this rule applies. */
    private PhysicalmachineHB machine;

    /**
     * Default constructor.
     */
    public MachineLoadRuleHB()
    {
        super();
    }

    /**
     * Creates a rule given a location.
     * 
     * @param datacenter The datacenter where the rules is applied.
     * @param rack The rack where the rules is applied.
     * @param machine The physical machine where the rules is applied.
     */
    public MachineLoadRuleHB(final DatacenterHB datacenter, final RackHB rack,
        final PhysicalmachineHB machine)
    {
        super();
        this.datacenter = datacenter;
        this.rack = rack;
        this.machine = machine;
    }

    /**
     * Creates a rule given a location.
     * 
     * @param datacenter The datacenter where the rules is applied.
     * @param rack The rack where the rules is applied.
     * @param machine The physical machine where the rules is applied.
     * @param ramLoadPercentage The RAM load percentage.
     * @param cpuLoadPercentage The CPU load percentage.
     */
    public MachineLoadRuleHB(final DatacenterHB datacenter, final RackHB rack,
        final PhysicalmachineHB machine, final int ramLoadPercentage, final int cpuLoadPercentage)
    {
        super();
        this.ramLoadPercentage = ramLoadPercentage;
        this.cpuLoadPercentage = cpuLoadPercentage;
        this.datacenter = datacenter;
        this.rack = rack;
        this.machine = machine;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public int getRamLoadPercentage()
    {
        return ramLoadPercentage;
    }

    public void setRamLoadPercentage(final int ramLoadPercentage)
    {
        this.ramLoadPercentage = ramLoadPercentage;
    }

    public int getCpuLoadPercentage()
    {
        return cpuLoadPercentage;
    }

    public void setCpuLoadPercentage(final int cpuLoadPercentage)
    {
        this.cpuLoadPercentage = cpuLoadPercentage;
    }

    public DatacenterHB getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final DatacenterHB datacenter)
    {
        this.datacenter = datacenter;
    }

    public RackHB getRack()
    {
        return rack;
    }

    public void setRack(final RackHB rack)
    {
        this.rack = rack;
    }

    public PhysicalmachineHB getMachine()
    {
        return machine;
    }

    public void setMachine(final PhysicalmachineHB machine)
    {
        this.machine = machine;
    }

    @Override
    public MachineLoadRule toPojo()
    {
        final MachineLoadRule rule = new MachineLoadRule();
        rule.setCpuLoadPercentage(cpuLoadPercentage);
        rule.setRamLoadPercentage(ramLoadPercentage);
        rule.setDatacenter(datacenter.toPojo());

        if (rack != null)
        {
            rule.setRack(rack.toPojo());
        }

        if (machine != null)
        {
            rule.setMachine(machine.toPojo());
        }

        return rule;
    }

    // We define a private static class as a trick to bypasses the validation for the
    // special case of the default rule, which MUST not belong to any datacenter/rack/machine.
    // The only instance that will ever exist is DEFAULT_RULE
    private static class MachineLoadRuleWithNoLocationHB extends MachineLoadRuleHB
    {
        private static final long serialVersionUID = 1479156414262769638L;

        private MachineLoadRuleWithNoLocationHB()
        {
            setCpuLoadPercentage(100);
            setRamLoadPercentage(100);
        }
    }

    @Override
    public boolean pass(final VirtualimageHB image, final PhysicalmachineHB machine,
        final Integer contextData)
    {

        final boolean passCPU =
            pass(machine.getCpuUsed(), image.getCpuRequired(), machine.getCpu(), cpuLoadPercentage);

        // if (!passCPU)
        // {
        // return false;
        // }

        final boolean passRAM =
            pass(machine.getRamUsed(), image.getRamRequired(), machine.getRam(), ramLoadPercentage);

        return passCPU && passRAM;
    }

    /**
     * TODO use Long for used on physical machine DDBB schema
     */
    public static boolean pass(final Integer used, final Integer required, final Integer allowed,
        final Integer oversubscription)
    {
        return (used + required) <= (oversubscription * allowed / 100);
    }
}
