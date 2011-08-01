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

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = IpPoolManagement.TABLE_NAME)
@DiscriminatorValue("10")
@NamedQueries({@NamedQuery(name = "IP_POOL_MANAGEMENT.BY_VLAN", query = IpPoolManagement.BY_VLAN),
@NamedQuery(name = "IP_POOL_MANAGEMENT.BY_VDC", query = IpPoolManagement.BY_VDC),
@NamedQuery(name = "IP_POOL_MANAGEMENT.BY_ENT", query = IpPoolManagement.BY_ENT)})
public class IpPoolManagement extends RasdManagement
{
    public static final String DISCRIMINATOR = "10";

    private static final String DEFAULT_RESOURCE_NAME = "MAC Address";

    private static final String DEFAULT_RESOURCE_DESCRIPTION =
        "MAC Address asociated to private Network";

    public static enum Type
    {
        PRIVATE, PUBLIC; // 0 = private, 1 = public
    }

    public static final String TABLE_NAME = "ip_pool_management";

    public static final String BY_VLAN = " SELECT ip FROM IpPoolManagement ip, "
        + " NetworkConfiguration nc, " + " VLANNetwork vn " + " WHERE ip.dhcp.id = nc.dhcp.id "
        + " AND nc.id = vn.configuration.id " + " AND vn.id = :vlan_id";

    public static final String BY_VDC = " SELECT ip FROM IpPoolManagement ip, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn "
        + " WHERE ip.dhcp.id = nc.dhcp.id " + " AND nc.id = vn.configuration.id "
        + " AND vn.network.id = vdc.network.id" + " AND vdc.id = :vdc_id";

    public static final String BY_ENT = " SELECT ip FROM IpPoolManagement ip, "
        + " NetworkConfiguration nc, " + " VirtualDatacenter vdc, " + " VLANNetwork vn, "
        + " Enterprise ent " + " WHERE ip.dhcp.id = nc.dhcp.id "
        + " AND nc.id = vn.configuration.id " + " AND vn.network.id = vdc.network.id"
        + " AND vdc.enterprise.id = ent.id" + " AND ent.id = :ent_id";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public IpPoolManagement()
    {
        // Just for JPA support
    }

    public IpPoolManagement(final Dhcp dhcp, final VLANNetwork vlan, final String mac,
        final String name, final String ip, final String networkName, final Type type)
    {
        super(DISCRIMINATOR);

        // RasdManagement properties
        Rasd rasd =
            new Rasd(UUID.randomUUID().toString(),
                DEFAULT_RESOURCE_NAME,
                Integer.valueOf(DISCRIMINATOR));

        rasd.setDescription(DEFAULT_RESOURCE_DESCRIPTION);
        rasd.setConnection("");
        rasd.setAllocationUnits("0");
        rasd.setAutomaticAllocation(0);
        rasd.setAutomaticDeallocation(0);

        setRasd(rasd);

        // IpManagement properties
        setType(type);
        setDhcp(dhcp);
        setMac(mac);
        setName(name);
        setIp(ip);
        setVlanNetwork(vlan);
        setNetworkName(networkName);
        setAvailable(Boolean.TRUE);
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 255;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public final static String VLAN_NETWORK_PROPERTY = "vlanNetwork";

    private final static boolean VLAN_NETWORK_REQUIRED = true;

    private final static String VLAN_NETWORK_ID_COLUMN = "vlan_network_id";

    @JoinColumn(name = VLAN_NETWORK_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "ippool_vlan_network_FK")
    private VLANNetwork vlanNetwork;

    @Required(value = VLAN_NETWORK_REQUIRED)
    public VLANNetwork getVlanNetwork()
    {
        return this.vlanNetwork;
    }

    public void setVlanNetwork(final VLANNetwork vlanNetwork)
    {
        this.vlanNetwork = vlanNetwork;
    }

    public final static String MAC_PROPERTY = "mac";

    private final static boolean MAC_REQUIRED = false;

    private final static int MAC_LENGTH_MIN = 0;

    private final static int MAC_LENGTH_MAX = 255;

    private final static boolean MAC_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String MAC_COLUMN = "mac";

    @Column(name = MAC_COLUMN, nullable = !MAC_REQUIRED, length = MAC_LENGTH_MAX)
    private String mac;

    @Required(value = MAC_REQUIRED)
    @Length(min = MAC_LENGTH_MIN, max = MAC_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = MAC_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getMac()
    {
        return this.mac;
    }

    public void setMac(final String mac)
    {
        this.mac = mac;
        // When we perform the persistenceFromTransport(Dto.class) the rasd is null
        // and it raises an exception without this property.
        if (getRasd() != null)
        {
            getRasd().setAddress(mac);
        }
    }

    public final static String CONFIGURATION_GATEWAY_PROPERTY = "configureGateway";

    private final static String CONFIGURATION_GATEWAY_COLUMN = "configureGateway";

    private final static boolean CONFIGURATION_GATEWAY_REQUIRED = true;

    @Column(name = CONFIGURATION_GATEWAY_COLUMN, nullable = false)
    private boolean configurareGateway;

    @Required(value = CONFIGURATION_GATEWAY_REQUIRED)
    public boolean getConfigureGateway()
    {
        return this.configurareGateway;
    }

    public void setConfigureGateway(final boolean configurareGateway)
    {
        this.configurareGateway = configurareGateway;
    }

    public final static String QUARANTINE_PROPERTY = "quarantine";

    private final static String QUARANTINE_COLUMN = "quarantine";

    private final static boolean QUARANTINE_REQUIRED = true;

    @Column(name = QUARANTINE_COLUMN, nullable = false)
    private boolean quarantine;

    @Required(value = QUARANTINE_REQUIRED)
    public boolean getQuarantine()
    {
        return this.quarantine;
    }

    public void setQuarantine(final boolean quarantine)
    {
        this.quarantine = quarantine;
    }

    public final static String AVAILABLE_PROPERTY = "available";

    private final static String AVAILABLE_COLUMN = "available";

    private final static boolean AVAILABLE_REQUIRED = false;

    @Column(name = AVAILABLE_COLUMN, nullable = false)
    private boolean available;

    @Required(value = AVAILABLE_REQUIRED)
    public boolean getAvailable()
    {
        return this.available;
    }

    public void setAvailable(final boolean available)
    {
        this.available = available;
    }

    public final static String IP_PROPERTY = "ip";

    private final static boolean IP_REQUIRED = false;

    private final static int IP_LENGTH_MIN = 0;

    private final static int IP_LENGTH_MAX = 255;

    private final static boolean IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String IP_COLUMN = "ip";

    @Column(name = IP_COLUMN, nullable = !IP_REQUIRED, length = IP_LENGTH_MAX)
    private String ip;

    @Required(value = IP_REQUIRED)
    @Length(min = IP_LENGTH_MIN, max = IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getIp()
    {
        return this.ip;
    }

    public void setIp(final String ip)
    {
        this.ip = ip;
    }

    public final static String DHCP_PROPERTY = "dhcp";

    private final static boolean DHCP_REQUIRED = true;

    private final static String DHCP_ID_COLUMN = "dhcp_service_id";

    @JoinColumn(name = DHCP_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_dhcp")
    private Dhcp dhcp;

    @Required(value = DHCP_REQUIRED)
    public Dhcp getDhcp()
    {
        return this.dhcp;
    }

    public void setDhcp(final Dhcp dhcp)
    {
        this.dhcp = dhcp;
    }

    public final static String NETWORK_NAME_PROPERTY = "networkName";

    private final static boolean NETWORK_NAME_REQUIRED = false;

    private final static int NETWORK_NAME_LENGTH_MIN = 0;

    private final static int NETWORK_NAME_LENGTH_MAX = 255;

    private final static boolean NETWORK_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NETWORK_NAME_COLUMN = "vlan_network_name";

    @Column(name = NETWORK_NAME_COLUMN, nullable = !NETWORK_NAME_REQUIRED, length = NETWORK_NAME_LENGTH_MAX)
    private String networkName;

    @Required(value = NETWORK_NAME_REQUIRED)
    @Length(min = NETWORK_NAME_LENGTH_MIN, max = NETWORK_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NETWORK_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNetworkName()
    {
        return this.networkName;
    }

    public void setNetworkName(final String networkName)
    {
        this.networkName = networkName;
        // When we perform the persistenceFromTransport(Dto.class) the rasd is null
        // and it raises an exception without this property.
        if (getRasd() != null)
        {
            getRasd().setParent(networkName);
        }
    }

    // ********************************** Helper methods ********************************

    public Type getType()
    {
        int typeFlag = Integer.valueOf(getRasd().getResourceSubType());
        return Type.values()[typeFlag];
    }

    public void setType(final Type type)
    {
        getRasd().setResourceSubType(String.valueOf(type.ordinal()));
        getRasd().setConfigurationName(String.valueOf(type.ordinal()));
    }

    public boolean isPrivateIp()
    {
        return getType() == Type.PRIVATE;
    }

    public boolean isPublicIp()
    {
        return !isPrivateIp();
    }

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Ways to order this element in the queries.
     */
    public static enum OrderByEnum
    {
        IP, QUARANTINE, MAC, LEASE, VLAN, VIRTUALDATACENTER, VIRTUALMACHINE, VIRTUALAPPLIANCE;

        public static OrderByEnum fromValue(final String orderBy)
        {
            for (OrderByEnum currentOrder : OrderByEnum.values())
            {
                if (currentOrder.name().equalsIgnoreCase(orderBy))
                {
                    return currentOrder;
                }
            }

            return null;
        }
    }

}
