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

package com.abiquo.server.core.scheduler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = MachineLoadRule.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = MachineLoadRule.TABLE_NAME)
public class MachineLoadRule extends DefaultEntityBase implements
    Rule<VirtualMachineRequirements, Machine, Integer>, PersistentRule

{
    public static final MachineLoadRule DEFAULT_RULE = new MachineLoadRuleWithNoLocation();

    public static final String TABLE_NAME = "workload_machine_load_rule";

    /**
     * TODO protect machine, rack or datacenter SETTER with unset all the other relations
     */

    // TODO deprectate this // used on generators
    public MachineLoadRule(final int cpuLoadPercentage, final int ramLoadPercentage)
    {
        super();

        setCpuLoadPercentage(cpuLoadPercentage);
        setRamLoadPercentage(ramLoadPercentage);
    }

    public MachineLoadRule(final Machine machine, final int cpuLoadPercentage,
        final int ramLoadPercentage)
    {
        super();
        setMachine(machine);
        setCpuLoadPercentage(cpuLoadPercentage);
        setRamLoadPercentage(ramLoadPercentage);
    }

    public MachineLoadRule(final Rack rack, final int cpuLoadPercentage, final int ramLoadPercentage)
    {
        super();
        setRack(rack);
        setCpuLoadPercentage(cpuLoadPercentage);
        setRamLoadPercentage(ramLoadPercentage);
    }

    public MachineLoadRule(final Datacenter datacenter, final int cpuLoadPercentage,
        final int ramLoadPercentage)
    {
        super();
        setDatacenter(datacenter);
        setCpuLoadPercentage(cpuLoadPercentage);
        setRamLoadPercentage(ramLoadPercentage);
    }

    protected MachineLoadRule()
    {
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String MACHINE_PROPERTY = "machine";

    private final static boolean MACHINE_REQUIRED = false;

    private final static String MACHINE_ID_COLUMN = "idMachine";

    @JoinColumn(name = MACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_machine")
    private Machine machine;

    @Required(value = MACHINE_REQUIRED)
    public Machine getMachine()
    {
        return this.machine;
    }

    public void setMachine(final Machine machine)
    {
        this.machine = machine;
    }

    public final static String DATACENTER_PROPERTY = "datacenter";

    private final static boolean DATACENTER_REQUIRED = false;

    private final static String DATACENTER_ID_COLUMN = "idDatacenter";

    @JoinColumn(name = DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datacenter")
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    public void setDatacenter(final Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String RACK_PROPERTY = "rack";

    private final static boolean RACK_REQUIRED = false;

    private final static String RACK_ID_COLUMN = "idRack";

    @JoinColumn(name = RACK_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_rack")
    private Rack rack;

    @Required(value = RACK_REQUIRED)
    public Rack getRack()
    {
        return this.rack;
    }

    public void setRack(final Rack rack)
    {
        this.rack = rack;
    }

    public final static String CPU_LOAD_PERCENTAGE_PROPERTY = "cpuLoadPercentage";

    private final static String CPU_LOAD_PERCENTAGE_COLUMN = "cpuLoadPercentage";

    private final static int CPU_LOAD_PERCENTAGE_MIN = Integer.MIN_VALUE;

    private final static int CPU_LOAD_PERCENTAGE_MAX = Integer.MAX_VALUE;

    @Column(name = CPU_LOAD_PERCENTAGE_COLUMN, nullable = false)
    @Range(min = CPU_LOAD_PERCENTAGE_MIN, max = CPU_LOAD_PERCENTAGE_MAX)
    private int cpuLoadPercentage;

    public int getCpuLoadPercentage()
    {
        return this.cpuLoadPercentage;
    }

    public void setCpuLoadPercentage(final int cpuLoadPercentage)
    {
        this.cpuLoadPercentage = cpuLoadPercentage;
    }

    public final static String RAM_LOAD_PERCENTAGE_PROPERTY = "ramLoadPercentage";

    private final static String RAM_LOAD_PERCENTAGE_COLUMN = "ramLoadPercentage";

    private final static int RAM_LOAD_PERCENTAGE_MIN = Integer.MIN_VALUE;

    private final static int RAM_LOAD_PERCENTAGE_MAX = Integer.MAX_VALUE;

    @Column(name = RAM_LOAD_PERCENTAGE_COLUMN, nullable = false)
    @Range(min = RAM_LOAD_PERCENTAGE_MIN, max = RAM_LOAD_PERCENTAGE_MAX)
    private int ramLoadPercentage;

    public int getRamLoadPercentage()
    {
        return this.ramLoadPercentage;
    }

    public void setRamLoadPercentage(final int ramLoadPercentage)
    {
        this.ramLoadPercentage = ramLoadPercentage;
    }

    //
    // We define a private static class as a trick to bypasses the validation for the
    // special case of the default rule, which MUST not belong to any datacenter/rack/machine.
    // The only instance that will ever exist is DEFAULT_RULE
    private static class MachineLoadRuleWithNoLocation extends MachineLoadRule
    {
        private static final long serialVersionUID = 1479156414262769638L;

        private MachineLoadRuleWithNoLocation()
        {
            setCpuLoadPercentage(100);
            setRamLoadPercentage(100);
        }
    }

    @Override
    public boolean pass(final VirtualMachineRequirements requirements, final Machine machine,
        final Integer contextData)
    {

        final boolean passCPU =
            pass(Long.valueOf(machine.getVirtualCpusUsed()), requirements.getCpu(),
                Long.valueOf(machine.getVirtualCpuCores() * machine.getVirtualCpusPerCore()),
                cpuLoadPercentage);

        // if (!passCPU)
        // {
        // return false;
        // }

        final boolean passRAM =
            pass(Long.valueOf(machine.getVirtualRamUsedInMb()), requirements.getRam(),
                Long.valueOf(machine.getVirtualRamInMb()), ramLoadPercentage);

        return passCPU && passRAM;
    }

    /**
     * TODO use Long for used on physical machine DDBB schema
     */
    public static boolean pass(final Long used, final Long required, final Long allowed,
        final Integer oversubscription)
    {
        return (used + required) <= (oversubscription * allowed / 100);
    }
}
