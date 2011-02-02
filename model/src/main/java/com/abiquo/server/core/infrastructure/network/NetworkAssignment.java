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

package com.abiquo.server.core.infrastructure.network;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.Rack;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = NetworkAssignment.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = NetworkAssignment.TABLE_NAME)
public class NetworkAssignment extends DefaultEntityBase
{
    public static final String TABLE_NAME = "vlan_network_assignment";

    protected NetworkAssignment()
    {
    }

    public NetworkAssignment(VirtualDatacenter virtualDatacenter, Rack rack, VLANNetwork vlannetwork)
    {
        setVirtualDatacenter(virtualDatacenter);
        setRack(rack);
        setVlanNetwork(vlannetwork);
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

    public final static String VIRTUAL_DATACENTER_PROPERTY = "virtualDatacenter";

    private final static boolean VIRTUAL_DATACENTER_REQUIRED = true;

    private final static String VIRTUAL_DATACENTER_ID_COLUMN = "idVirtualDataCenter";

    @JoinColumn(name = VIRTUAL_DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_virtualDatacenter")
    private VirtualDatacenter virtualDatacenter;

    @Required(value = VIRTUAL_DATACENTER_REQUIRED)
    public VirtualDatacenter getVirtualDatacenter()
    {
        return this.virtualDatacenter;
    }

    public void setVirtualDatacenter(VirtualDatacenter virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public final static String VLAN_NETWORK_PROPERTY = "vlanNetwork";

    private final static boolean VLAN_NETWORK_REQUIRED = true;

    private final static String VLAN_NETWORK_ID_COLUMN = "vlan_network_id";

    @JoinColumn(name = VLAN_NETWORK_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_vlanNetwork")
    private VLANNetwork vlanNetwork;

    @Required(value = VLAN_NETWORK_REQUIRED)
    public VLANNetwork getVlanNetwork()
    {
        return this.vlanNetwork;
    }

    public void setVlanNetwork(VLANNetwork vlanNetwork)
    {
        this.vlanNetwork = vlanNetwork;
    }

    public final static String RACK_PROPERTY = "rack";

    private final static boolean RACK_REQUIRED = true;

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

    public void setRack(Rack rack)
    {
        this.rack = rack;
    }

}
