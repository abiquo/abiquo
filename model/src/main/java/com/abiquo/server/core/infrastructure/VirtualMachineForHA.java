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

package com.abiquo.server.core.infrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualMachineForHA.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualMachineForHA.TABLE_NAME)
public class VirtualMachineForHA extends DefaultEntityBase
{
    public static final String TABLE_NAME = "virtualmachine_for_ha";

    protected VirtualMachineForHA()
    {
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String VIRTUAL_MACHINE_PROPERTY = "virtualMachine";

    private final static boolean VIRTUAL_MACHINE_REQUIRED = true;

    private final static String VIRTUAL_MACHINE_ID_COLUMN = "idVirtualMachine";

    @JoinColumn(name = VIRTUAL_MACHINE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualMachine")
    private VirtualMachine virtualMachine;

    @Required(value = VIRTUAL_MACHINE_REQUIRED)
    public VirtualMachine getVirtualMachine()
    {
        return this.virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public final static String MACHINE_PROPERTY = "machine";

    private final static boolean MACHINE_REQUIRED = true;

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

    public void setMachine(Machine machine)
    {
        this.machine = machine;
    }

    public VirtualMachineForHA(VirtualMachine virtualMachine, Machine machine)
    {
        this.setVirtualMachine(virtualMachine);
        this.setMachine(machine);
    }
}
