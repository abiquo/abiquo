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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Rack.TABLE_NAME, uniqueConstraints = {})
@Inheritance(strategy = InheritanceType.JOINED)
@org.hibernate.annotations.Table(appliesTo = Rack.TABLE_NAME, indexes = {})
public class Rack extends DefaultEntityBase
{

    // ****************************** JPA support
    public static final String TABLE_NAME = "rack";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER*
    // call from business code
    public Rack()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "idRack";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    // ******************************* Properties
    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    final static int NAME_LENGTH_MIN = 1;

    final static int NAME_LENGTH_MAX = 20;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    public final static String NAME_COLUMN = "Name";

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

    public final static String SHORT_DESCRIPTION_PROPERTY = "shortDescription";

    private final static boolean SHORT_DESCRIPTION_REQUIRED = false;

    final static int SHORT_DESCRIPTION_LENGTH_MIN = 0;

    final static int SHORT_DESCRIPTION_LENGTH_MAX = 30;

    private final static boolean SHORT_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SHORT_DESCRIPTION_COLUMN = "ShortDescription";

    @Column(name = SHORT_DESCRIPTION_COLUMN, nullable = !SHORT_DESCRIPTION_REQUIRED, length = SHORT_DESCRIPTION_LENGTH_MAX)
    private String shortDescription;

    @Required(value = SHORT_DESCRIPTION_REQUIRED)
    @Length(min = SHORT_DESCRIPTION_LENGTH_MIN, max = SHORT_DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SHORT_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getShortDescription()
    {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public final static String LONG_DESCRIPTION_PROPERTY = "longDescription";

    private final static boolean LONG_DESCRIPTION_REQUIRED = false;

    final static int LONG_DESCRIPTION_LENGTH_MIN = 0;

    final static int LONG_DESCRIPTION_LENGTH_MAX = 100;

    private final static boolean LONG_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String LONG_DESCRIPTION_COLUMN = "largeDescription";

    @Column(name = LONG_DESCRIPTION_COLUMN, nullable = !LONG_DESCRIPTION_REQUIRED, length = LONG_DESCRIPTION_LENGTH_MAX)
    private String longDescription;

    @Required(value = LONG_DESCRIPTION_REQUIRED)
    @Length(min = LONG_DESCRIPTION_LENGTH_MIN, max = LONG_DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = LONG_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getLongDescription()
    {
        return this.longDescription;
    }

    public void setLongDescription(String longDescription)
    {
        this.longDescription = longDescription;
    }

    // ****************************** Associations
    public static final String DATACENTER_PROPERTY = "datacenter";

    public static final String DATACENTER_ID_COLUMN = "idDataCenter";

    public static final boolean DATACENTER_REQUIRED = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DATACENTER_ID_COLUMN)
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter value)
    {
        this.datacenter = value;
    }

    // ************************** Mandatory constructors ***********************
    /* package */Rack(String name, Datacenter datacenter, Integer vlanIdMin, Integer vlanIdMax,
        Integer vlanPerVdcExpected, Integer nrsq)
    {
        setDatacenter(datacenter);
        setName(name);
        setVlanIdMin(vlanIdMin);
        setVlanIdMax(vlanIdMax);
        setVlanPerVdcExpected(vlanPerVdcExpected);
        setNrsq(nrsq);

    }

    public final static String VLAN_ID_MIN_PROPERTY = "vlanIdMin";

    public final static int VLAN_ID_MIN_DEFAULT_VALUE = 2;

    private final static String VLAN_ID_MIN_COLUMN = "vlan_id_min";

    private final static int VLAN_ID_MIN_MIN = Integer.MIN_VALUE;

    private final static int VLAN_ID_MIN_MAX = Integer.MAX_VALUE;

    @Column(name = VLAN_ID_MIN_COLUMN, nullable = true)
    @Range(min = VLAN_ID_MIN_MIN, max = VLAN_ID_MIN_MAX)
    private Integer vlanIdMin;

    public Integer getVlanIdMin()
    {
        return this.vlanIdMin;
    }

    public void setVlanIdMin(Integer vlanIdMin)
    {
        this.vlanIdMin = vlanIdMin;
    }

    public final static String VLAN_PER_VDC_EXPECTED_PROPERTY = "vlanPerVdcExpected";

    private final static String VLAN_PER_VDC_EXPECTED_COLUMN = "vlan_per_vdc_expected";

    public final static int VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE = 2;

    private final static int VLAN_PER_VDC_EXPECTED_MIN = Integer.MIN_VALUE;

    private final static int VLAN_PER_VDC_EXPECTED_MAX = Integer.MAX_VALUE;

    @Column(name = VLAN_PER_VDC_EXPECTED_COLUMN, nullable = true)
    @Range(min = VLAN_PER_VDC_EXPECTED_MIN, max = VLAN_PER_VDC_EXPECTED_MAX)
    private Integer vlanPerVdcExpected;

    public Integer getVlanPerVdcExpected()
    {
        return this.vlanPerVdcExpected;
    }

    public void setVlanPerVdcExpected(Integer vlanPerVdcExpected)
    {
        this.vlanPerVdcExpected = vlanPerVdcExpected;
    }

    public final static String NRSQ_PROPERTY = "nrsq";

    private final static String NRSQ_COLUMN = "nrsq";

    private final static int NRSQ_MIN = Integer.MIN_VALUE;

    private final static int NRSQ_MAX = Integer.MAX_VALUE;

    public final static int NRSQ_DEFAULT_VALUE = 0;

    @Column(name = NRSQ_COLUMN, nullable = true)
    @Range(min = NRSQ_MIN, max = NRSQ_MAX)
    private Integer nrsq;

    public Integer getNrsq()
    {
        return this.nrsq;
    }

    public void setNrsq(Integer nrsq)
    {
        this.nrsq = nrsq;
    }

    public final static String VLAN_ID_MAX_PROPERTY = "vlanIdMax";

    private final static String VLAN_ID_MAX_COLUMN = "vlan_id_max";

    private final static int VLAN_ID_MAX_MIN = Integer.MIN_VALUE;

    private final static int VLAN_ID_MAX_MAX = Integer.MAX_VALUE;

    public final static int VLAN_ID_MAX_DEFAULT_VALUE = 4094;

    @Column(name = VLAN_ID_MAX_COLUMN, nullable = true)
    @Range(min = VLAN_ID_MAX_MIN, max = VLAN_ID_MAX_MAX)
    private Integer vlanIdMax;

    public Integer getVlanIdMax()
    {
        return this.vlanIdMax;
    }

    public void setVlanIdMax(Integer vlanIdMax)
    {
        this.vlanIdMax = vlanIdMax;
    }

    public final static String VLANS_ID_AVOIDED_PROPERTY = "vlansIdAvoided";

    public final static String VLANS_ID_AVOIDED_DEFAULT_VALUE = "";

    private final static boolean VLANS_ID_AVOIDED_REQUIRED = false;

    public final static int VLANS_ID_AVOIDED_LENGTH_MIN = 0;

    public final static int VLANS_ID_AVOIDED_LENGTH_MAX = 255;

    private final static boolean VLANS_ID_AVOIDED_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VLANS_ID_AVOIDED_COLUMN = "vlans_id_avoided";

    @Column(name = VLANS_ID_AVOIDED_COLUMN, nullable = !VLANS_ID_AVOIDED_REQUIRED, length = VLANS_ID_AVOIDED_LENGTH_MAX)
    private String vlansIdAvoided;

    @Required(value = VLANS_ID_AVOIDED_REQUIRED)
    @Length(min = VLANS_ID_AVOIDED_LENGTH_MIN, max = VLANS_ID_AVOIDED_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VLANS_ID_AVOIDED_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVlansIdAvoided()
    {
        return this.vlansIdAvoided;
    }

    public void setVlansIdAvoided(String vlansIdAvoided)
    {
        this.vlansIdAvoided = vlansIdAvoided;
    }
    
    public final static String HAENABLED_PROPERTY = "haEnabled";

    private final static String HAENABLED_COLUMN = "haEnabled";

    private final static boolean HAENABLED_REQUIRED = true;

    @Column(name = HAENABLED_COLUMN, nullable = false)
    private boolean haEnabled = false;

    @Required(value = HAENABLED_REQUIRED)
    public boolean isHaEnabled()
    {
        return this.haEnabled;
    }

    public void setHaEnabled(boolean haEnabled)
    {
        this.haEnabled = haEnabled;
    }

    // ********************************** Others
    // ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
