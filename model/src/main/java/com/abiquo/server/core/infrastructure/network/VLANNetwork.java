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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VLANNetwork.TABLE_NAME, uniqueConstraints = {})
// TODO: specify unique constraints
@org.hibernate.annotations.Table(appliesTo = VLANNetwork.TABLE_NAME, indexes = {})
// TODO: specify indexes
public class VLANNetwork extends DefaultEntityBase
{

    // ****************************** JPA support *******************************
    public static final String TABLE_NAME = "vlan_network";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected VLANNetwork()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "vlan_network_id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    // ******************************* Properties *******************************
    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    private final static int NAME_LENGTH_MIN = 1;

    private final static int NAME_LENGTH_MAX = 40;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "network_name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    private void setName(String name)
    {
        this.name = name;
    }

    public final static String TAG_PROPERTY = "tag";

    private final static boolean TAG_REQUIRED = false;

    private final static String TAG_COLUMN = "vlan_tag";

    public static final Integer VLAN_MAX_TAG = 4094;

    @Column(name = TAG_COLUMN, nullable = !TAG_REQUIRED)
    private Integer tag;

    public Integer getTag()
    {
        return this.tag;
    }

    public void setTag(Integer tag)
    {
        this.tag = tag;
    }

    public final static String DEFAULT_PROPERTY = "defaultNetwork";

    private final static boolean DEFAULT_REQUIRED = true;

    private final static String DEFAULT_COLUMN = "default_network";

    @Column(name = DEFAULT_COLUMN, nullable = false)
    private Boolean defaultNetwork;

    @Required(value = DEFAULT_REQUIRED)
    public Boolean getDefaultNetwork()
    {
        return this.defaultNetwork;
    }

    private void setDefaultNetwork(Boolean defaultNetwork)
    {
        this.defaultNetwork = defaultNetwork;
    }

    // ****************************** Associations ******************************
    public final static String NETWORK_PROPERTY = "network";

    private final static boolean NETWORK_REQUIRED = true;

    private final static String NETWORK_ID_COLUMN = "network_id";

    @JoinColumn(name = NETWORK_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
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

    // `enterprise_id` int(10) unsigned DEFAULT NULL,

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = false;

    private final static String ENTERPRISE_ID_COLUMN = "enterprise_id";

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

    //

    public final static String CONFIGURATION_PROPERTY = "configuration";

    private final static boolean CONFIGURATION_REQUIRED = true;

    private final static String CONFIGURATION_ID_COLUMN = "network_configuration_id";

    @JoinColumn(name = CONFIGURATION_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_configuration")
    private NetworkConfiguration configuration;

    @Required(value = CONFIGURATION_REQUIRED)
    public NetworkConfiguration getConfiguration()
    {
        return this.configuration;
    }

    public void setConfiguration(NetworkConfiguration configuration)
    {
        this.configuration = configuration;
    }

    // I don't want to access the ips directly but I want to remove them in cascade
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "vlanNetwork")
    private List<IpPoolManagement> ipPoolManagements;

    // *************************** Mandatory constructors ***********************
    public VLANNetwork(String name, Network network, Boolean defaultNetwork,
        NetworkConfiguration configuration)
    {
        setName(name);
        setTag(tag);
        setNetwork(network);
        setDefaultNetwork(defaultNetwork);
        setConfiguration(configuration);
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
