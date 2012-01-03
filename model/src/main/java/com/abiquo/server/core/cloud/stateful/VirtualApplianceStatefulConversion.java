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

package com.abiquo.server.core.cloud.stateful;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.User;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualApplianceStatefulConversion.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualApplianceStatefulConversion.TABLE_NAME)
public class VirtualApplianceStatefulConversion extends DefaultEntityBase
{
    public static final String TABLE_NAME = "vappstateful_conversions";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected VirtualApplianceStatefulConversion()
    {
        // Just for JPA support
    }

    public VirtualApplianceStatefulConversion(final User user, final VirtualAppliance vapp)
    {
        setUser(user);
        setVirtualAppliance(vapp);
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

    public final static String VIRTUAL_APPLIANCE_PROPERTY = "virtualAppliance";

    private final static boolean VIRTUAL_APPLIANCE_REQUIRED = true;

    private final static String VIRTUAL_APPLIANCE_ID_COLUMN = "idVirtualApp";

    @JoinColumn(name = VIRTUAL_APPLIANCE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualAppliance")
    private VirtualAppliance virtualAppliance;

    @Required(value = VIRTUAL_APPLIANCE_REQUIRED)
    public VirtualAppliance getVirtualAppliance()
    {
        return this.virtualAppliance;
    }

    public void setVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        this.virtualAppliance = virtualAppliance;
    }

    // public final static String SUB_STATE_PROPERTY = "subState";
    //
    // private final static boolean SUB_STATE_REQUIRED = true;
    //
    // private final static String SUB_STATE_COLUMN = "subState";
    //
    // @Enumerated(value = javax.persistence.EnumType.STRING)
    // @Column(name = SUB_STATE_COLUMN, nullable = !SUB_STATE_REQUIRED)
    // private VirtualMachineState subState;
    //
    // @Required(value = SUB_STATE_REQUIRED)
    // public VirtualMachineState getSubState()
    // {
    // return this.subState;
    // }
    //
    // public void setSubState(final VirtualMachineState subState)
    // {
    // this.subState = subState;
    // }
    //
    // public final static String STATE_PROPERTY = "state";
    //
    // private final static boolean STATE_REQUIRED = true;
    //
    // private final static String STATE_COLUMN = "state";
    //
    // @Enumerated(value = javax.persistence.EnumType.STRING)
    // @Column(name = STATE_COLUMN, nullable = !STATE_REQUIRED)
    // private VirtualMachineState state;
    //
    // @Required(value = STATE_REQUIRED)
    // public VirtualMachineState getState()
    // {
    // return this.state;
    // }
    //
    // public void setState(final VirtualMachineState state)
    // {
    // this.state = state;
    // }

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = true;

    private final static String USER_ID_COLUMN = "idUser";

    @JoinColumn(name = USER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_user")
    private User user;

    @Required(value = USER_REQUIRED)
    public User getUser()
    {
        return user;
    }

    public void setUser(final User user)
    {
        this.user = user;
    }

}
