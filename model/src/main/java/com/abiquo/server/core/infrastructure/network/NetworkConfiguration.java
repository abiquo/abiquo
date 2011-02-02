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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.model.validation.Ip;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = NetworkConfiguration.TABLE_NAME, uniqueConstraints = {})
// TODO: specify unique constraints
@org.hibernate.annotations.Table(appliesTo = NetworkConfiguration.TABLE_NAME, indexes = {})
// TODO: specify indexes
public class NetworkConfiguration extends DefaultEntityBase
{

    // ****************************** JPA support *******************************
    public static final String TABLE_NAME = "network_configuration";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected NetworkConfiguration()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "network_configuration_id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    // ******************************* Properties *******************************
    public final static String GATEWAY_PROPERTY = "gateway";

    private final static boolean GATEWAY_REQUIRED = false;

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

    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }

    public final static String ADDRESS_PROPERTY = "address";

    private final static boolean ADDRESS_REQUIRED = true;

    public final static int ADDRESS_LENGTH_MIN = 1;

    public final static int ADDRESS_LENGTH_MAX = 40;

    private final static boolean ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ADDRESS_COLUMN = "network_address";

    @Column(name = ADDRESS_COLUMN, nullable = !ADDRESS_REQUIRED, length = ADDRESS_LENGTH_MAX)
    private String address;

    @Required(value = ADDRESS_REQUIRED)
    @Length(min = ADDRESS_LENGTH_MIN, max = ADDRESS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getAddress()
    {
        return this.address;
    }

    private void setAddress(String address)
    {
        this.address = address;
    }

    public final static String MASK_PROPERTY = "mask";

    private final static boolean MASK_REQUIRED = true;

    private final static String MASK_COLUMN = "mask";

    @Column(name = MASK_COLUMN, nullable = !MASK_REQUIRED)
    private Integer mask;

    @Required(value = MASK_REQUIRED)
    public Integer getMask()
    {
        return this.mask;
    }

    private void setMask(Integer mask)
    {
        this.mask = mask;
    }

    public final static String NETMASK_PROPERTY = "netMask";

    private final static boolean NETMASK_REQUIRED = true;

    public final static int NETMASK_LENGTH_MIN = 0;

    public final static int NETMASK_LENGTH_MAX = 20;

    private final static boolean NETMASK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NETMASK_COLUMN = "netmask";

    @Column(name = NETMASK_COLUMN, nullable = !NETMASK_REQUIRED, length = NETMASK_LENGTH_MAX)
    private String netMask;

    @Required(value = NETMASK_REQUIRED)
    @Length(min = NETMASK_LENGTH_MIN, max = NETMASK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NETMASK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNetMask()
    {
        return this.netMask;
    }

    private void setNetMask(String netMask)
    {
        this.netMask = netMask;
    }

    public final static String PRIMARY_DNS_PROPERTY = "primaryDNS";

    private final static boolean PRIMARY_DNS_REQUIRED = false;

    private final static int PRIMARY_DNS_LENGTH_MIN = 0;

    private final static int PRIMARY_DNS_LENGTH_MAX = 20;

    private final static boolean PRIMARY_DNS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PRIMARY_DNS_COLUMN = "primary_dns";

    @Column(name = PRIMARY_DNS_COLUMN, nullable = !PRIMARY_DNS_REQUIRED, length = PRIMARY_DNS_LENGTH_MAX)
    private String primaryDNS;

    @Required(value = PRIMARY_DNS_REQUIRED)
    @Length(min = PRIMARY_DNS_LENGTH_MIN, max = PRIMARY_DNS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PRIMARY_DNS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip(required = PRIMARY_DNS_REQUIRED)
    public String getPrimaryDNS()
    {
        return this.primaryDNS;
    }

    public void setPrimaryDNS(String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    public final static String SECONDARY_DNS_PROPERTY = "secondaryDNS";

    private final static boolean SECONDARY_DNS_REQUIRED = false;

    private final static int SECONDARY_DNS_LENGTH_MIN = 0;

    private final static int SECONDARY_DNS_LENGTH_MAX = 20;

    private final static boolean SECONDARY_DNS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SECONDARY_DNS_COLUMN = "secondary_dns";

    @Column(name = SECONDARY_DNS_COLUMN, nullable = !SECONDARY_DNS_REQUIRED, length = SECONDARY_DNS_LENGTH_MAX)
    private String secondaryDNS;

    @Required(value = SECONDARY_DNS_REQUIRED)
    @Length(min = SECONDARY_DNS_LENGTH_MIN, max = SECONDARY_DNS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SECONDARY_DNS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip(required = SECONDARY_DNS_REQUIRED)
    public String getSecondaryDNS()
    {
        return this.secondaryDNS;
    }

    public void setSecondaryDNS(String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    public final static String SUFIX_DNS_PROPERTY = "sufixDNS";

    private final static boolean SUFIX_DNS_REQUIRED = false;

    private final static int SUFIX_DNS_LENGTH_MIN = 0;

    private final static int SUFIX_DNS_LENGTH_MAX = 40;

    private final static boolean SUFIX_DNS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SUFIX_DNS_COLUMN = "sufix_dns";

    @Column(name = SUFIX_DNS_COLUMN, nullable = !SUFIX_DNS_REQUIRED, length = SUFIX_DNS_LENGTH_MAX)
    private String sufixDNS;

    @Required(value = SUFIX_DNS_REQUIRED)
    @Length(min = SUFIX_DNS_LENGTH_MIN, max = SUFIX_DNS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SUFIX_DNS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSufixDNS()
    {
        return this.sufixDNS;
    }

    public void setSufixDNS(String sufixDNS)
    {
        this.sufixDNS = sufixDNS;
    }

    public final static String FENCE_MODE_PROPERTY = "fenceMode";

    private final static boolean FENCE_MODE_REQUIRED = true;

    public final static int FENCE_MODE_LENGTH_MIN = 1;

    public final static int FENCE_MODE_LENGTH_MAX = 20;

    private final static boolean FENCE_MODE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String FENCE_MODE_COLUMN = "fence_mode";

    @Column(name = FENCE_MODE_COLUMN, nullable = !FENCE_MODE_REQUIRED, length = FENCE_MODE_LENGTH_MAX)
    private String fenceMode;

    @Required(value = FENCE_MODE_REQUIRED)
    @Length(min = FENCE_MODE_LENGTH_MIN, max = FENCE_MODE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = FENCE_MODE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getFenceMode()
    {
        return this.fenceMode;
    }

    private void setFenceMode(String fenceMode)
    {
        this.fenceMode = fenceMode;
    }

    // ****************************** Associations ******************************
    // TODO: define associations

    public final static String DHCP_PROPERTY = "dhcp";

    private final static boolean DHCP_REQUIRED = false;

    private final static String DHCP_ID_COLUMN = "dhcp_service_id";

    @JoinColumn(name = DHCP_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_dhcp")
    private Dhcp dhcp;

    @Required(value = DHCP_REQUIRED)
    public Dhcp getDhcp()
    {
        return this.dhcp;
    }

    public void setDhcp(Dhcp dhcp)
    {
        this.dhcp = dhcp;
    }

    // *************************** Mandatory constructors ***********************
    public NetworkConfiguration(String address, Integer mask, String netmask, String fenceMode)
    {
        setAddress(address);
        setMask(mask);
        setFenceMode(fenceMode);
        setNetMask(netmask);
    }

    // *************************** Business methods ***********************
    // TODO: define business methods

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
