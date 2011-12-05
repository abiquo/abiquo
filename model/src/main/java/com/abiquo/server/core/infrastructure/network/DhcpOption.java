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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.validation.Ip;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DhcpOption.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DhcpOption.TABLE_NAME)
public class DhcpOption extends DefaultEntityBase
{
    public static final String TABLE_NAME = "dhcpOption";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected DhcpOption()
    {
        // Just for JPA support
    }

    public DhcpOption(final Integer option, final String gateway, final String address,
        final Integer mask, final String netmask)
    {
        setOption(option);
        setGateway(gateway);
        setNetworkAddress(address);
        setMask(mask);
        setNetmask(netmask);

    }

    private final static String ID_COLUMN = "idDhcpOption";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String OPTION_PROPERTY = "option";

    private final static boolean OPTION_REQUIRED = false;

    private final static String OPTION_COLUMN = "dhcp_opt";

    private final static int OPTION_MIN = Integer.MIN_VALUE;

    private final static int OPTION_MAX = Integer.MAX_VALUE;

    @Column(name = OPTION_COLUMN, nullable = !OPTION_REQUIRED)
    @Range(min = OPTION_MIN, max = OPTION_MAX)
    private Integer option;

    public Integer getOption()
    {
        return this.option;
    }

    public void setOption(final Integer option)
    {
        this.option = option;
    }

    // ******************************* Properties *******************************
    public final static String GATEWAY_PROPERTY = "gateway";

    private final static boolean GATEWAY_REQUIRED = true;

    private final static int GATEWAY_LENGTH_MIN = 1;

    private final static int GATEWAY_LENGTH_MAX = 40;

    private final static boolean GATEWAY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String GATEWAY_COLUMN = "gateway";

    @Column(name = GATEWAY_COLUMN, nullable = !GATEWAY_REQUIRED, length = GATEWAY_LENGTH_MAX)
    private String gateway;

    @Required(value = GATEWAY_REQUIRED)
    @Length(min = GATEWAY_LENGTH_MIN, max = GATEWAY_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = GATEWAY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getGateway()
    {
        return this.gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public final static String ADDRESS_PROPERTY = "networkAddress";

    private final static boolean ADDRESS_REQUIRED = true;

    public final static int ADDRESS_LENGTH_MIN = 1;

    public final static int ADDRESS_LENGTH_MAX = 40;

    private final static boolean ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ADDRESS_COLUMN = "network_address";

    @Column(name = ADDRESS_COLUMN, nullable = !ADDRESS_REQUIRED, length = ADDRESS_LENGTH_MAX)
    private String networkAddress;

    @Required(value = ADDRESS_REQUIRED)
    @Length(min = ADDRESS_LENGTH_MIN, max = ADDRESS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getNetworkAddress()
    {
        return networkAddress;
    }

    public void setNetworkAddress(final String networkAddress)
    {
        this.networkAddress = networkAddress;
    }

    public final static String MASK_PROPERTY = "mask";

    private final static boolean MASK_REQUIRED = true;

    private final static String MASK_COLUMN = "mask";

    private final static long MASK_MIN_VALUE = 0L;

    private final static long MASK_MAX_VALUE = 31L;

    @Column(name = MASK_COLUMN, nullable = !MASK_REQUIRED)
    private Integer mask;

    @Required(value = MASK_REQUIRED)
    @Min(MASK_MIN_VALUE)
    @Max(MASK_MAX_VALUE)
    public Integer getMask()
    {
        return this.mask;
    }

    public void setMask(final Integer mask)
    {
        this.mask = mask;
    }

    public final static String NETMASK_PROPERTY = "netmask";

    private final static boolean NETMASK_REQUIRED = true;

    public final static int NETMASK_LENGTH_MIN = 0;

    public final static int NETMASK_LENGTH_MAX = 20;

    private final static boolean NETMASK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NETMASK_COLUMN = "netmask";

    @Column(name = NETMASK_COLUMN, nullable = !NETMASK_REQUIRED, length = NETMASK_LENGTH_MAX)
    private String netmask;

    @Required(value = NETMASK_REQUIRED)
    @Length(min = NETMASK_LENGTH_MIN, max = NETMASK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NETMASK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNetmask()
    {
        return netmask;
    }

    public void setNetmask(final String netmask)
    {
        this.netmask = netmask;
    }

    public final static String ASSOCIATION_TABLE = "vlans_dhcpOption";

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = VLANNetwork.class, cascade = CascadeType.DETACH)
    @JoinTable(name = ASSOCIATION_TABLE, joinColumns = @JoinColumn(name = "idDhcpOption"), inverseJoinColumns = @JoinColumn(name = "idVlan"))
    private List<DhcpOption> dhcpOptions;
}
