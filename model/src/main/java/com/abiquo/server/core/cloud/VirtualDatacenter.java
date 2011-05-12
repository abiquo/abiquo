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

package com.abiquo.server.core.cloud;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.network.Network;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualDatacenter.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualDatacenter.TABLE_NAME)
public class VirtualDatacenter extends DefaultEntityWithLimits
{
    public static final String TABLE_NAME = "virtualdatacenter";

    protected VirtualDatacenter()
    {
    }

    private final static String ID_COLUMN = "idVirtualDataCenter";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    public final static int NAME_LENGTH_MIN = 1;

    public final static int NAME_LENGTH_MAX = 40;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "Name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public final static String HYPERVISOR_TYPE_PROPERTY = "hypervisorType";

    private final static boolean HYPERVISOR_TYPE_REQUIRED = true;

    private final static String HYPERVISOR_TYPE_COLUMN = "hypervisorType";

    private final static int HYPERVISOR_TYPE_COLUMN_LENGTH = 20;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = HYPERVISOR_TYPE_COLUMN, nullable = !HYPERVISOR_TYPE_REQUIRED, length = HYPERVISOR_TYPE_COLUMN_LENGTH)
    private HypervisorType hypervisorType;

    @Required(value = HYPERVISOR_TYPE_REQUIRED)
    public HypervisorType getHypervisorType()
    {
        return this.hypervisorType;
    }

    public void setHypervisorType(HypervisorType type)
    {
        this.hypervisorType = type;
    }

    public final static String DATACENTER_PROPERTY = "datacenter";

    private final static boolean DATACENTER_REQUIRED = true;

    private final static String DATACENTER_ID_COLUMN = "idDataCenter";

    @JoinColumn(name = DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datacenter")
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = true;

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    @JoinColumn(name = ENTERPRISE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise")
    private Enterprise enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String NETWORK_PROPERTY = "network";

    private final static boolean NETWORK_REQUIRED = true;

    private final static String NETWORK_ID_COLUMN = "networktypeID";

    @JoinColumn(name = NETWORK_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.ALL)
    @Cascade(CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_network")
    private Network network;

    @Required(value = NETWORK_REQUIRED)
    public Network getNetwork()
    {
        return this.network;
    }

    public void setNetwork(Network network)
    {
        this.network = network;
    }

    public VirtualDatacenter(Enterprise enterprise, Datacenter datacenter, Network network,
        HypervisorType type, String name)
    {
        super();

        this.setDatacenter(datacenter);
        this.setEnterprise(enterprise);
        this.setHypervisorType(type);
        this.setNetwork(network);
        this.setName(name);
    }
}
