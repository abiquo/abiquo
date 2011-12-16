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

package com.abiquo.server.core.enterprise;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;

import com.abiquo.model.validation.StringMap;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = EnterpriseProperties.TABLE_NAME, uniqueConstraints = {})
@org.hibernate.annotations.Table(appliesTo = EnterpriseProperties.TABLE_NAME)
public class EnterpriseProperties extends DefaultEntityBase
{

    // ****************************** JPA support *******************************
    public static final String TABLE_NAME = "enterprise_properties";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public EnterpriseProperties()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "idProperties";

    @Id
    @Column(name = ID_COLUMN, nullable = false)
    @GeneratedValue
    private Integer id;

    @Override
    public Integer getId()
    {
        return id;
    }

    // ******************************* Properties *******************************

    private final static boolean ENTERPRISE_REQUIRED = true;

    public final static String ENTERPRISE_PROPERTY = Enterprise.TABLE_NAME;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Enterprise.TABLE_NAME)
    @ForeignKey(name = "FK_" + Enterprise.TABLE_NAME)
    private Enterprise enterprise;

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return this.enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public static final String KEY_COLUMN = "map_key";

    public static final String VALUE_COLUMN = "value";

    public static final String JOIN_COLUMN = "enterprise_properties";

    private final static boolean PROPERTIES_REQUIRED = true;

    public final static int KEY_LENGTH_MIN = 1;

    public final static int KEY_LENGTH_MAX = 30;

    public final static int VALUE_LENGTH_MIN = 0;

    public final static int VALUE_LENGTH_MAX = 50;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_NAME + "_map", joinColumns = @JoinColumn(name = JOIN_COLUMN))
    @MapKeyColumn(name = KEY_COLUMN, length = 30, nullable = false)
    @Column(name = VALUE_COLUMN, length = 50, nullable = false)
    private Map<String, String> map = new HashMap<String, String>();

    @StringMap(minKey = KEY_LENGTH_MIN, maxKey = KEY_LENGTH_MAX, minValue = VALUE_LENGTH_MIN, maxValue = VALUE_LENGTH_MAX)
    public Map<String, String> getProperties()
    {
        return map;
    }

    public void setProperties(final Map<String, String> map)
    {
        this.map = map;
    }

    // *************************** Mandatory constructors ***********************
    public EnterpriseProperties(final Enterprise enterprise)
    {
        super();
        this.enterprise = enterprise;
    }

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
