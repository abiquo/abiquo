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

package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Tier.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Tier.TABLE_NAME)
public class Tier extends DefaultEntityBase
{
    public static final String TABLE_NAME = "tier";

    protected Tier()
    {
    }

    /**
     * Public constructor with the mandatory attributes.
     * 
     * @param name name of the tier
     * @param description description of the tier level
     * @param dc datacenter the Tier belongs to.
     */
    public Tier(final String name, final String description, final Datacenter dc)
    {
        this.setName(name);
        this.setDescription(description);
        this.setDatacenter(dc);
        this.setEnabled(Boolean.TRUE);
    }

    /* package */public final static String ID_COLUMN = "id";

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

    private final static boolean NAME_REQUIRED = true;

    /* package */final static int NAME_LENGTH_MIN = 0;

    /* package */final static int NAME_LENGTH_MAX = 255;

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

    public void setDatacenter(final Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String ENABLED_PROPERTY = "enabled";

    private final static boolean ENABLED_REQUIRED = true;

    private final static String ENABLED_COLUMN = "isEnabled";

    @Column(name = ENABLED_COLUMN, nullable = !ENABLED_REQUIRED)
    private boolean enabled;

    @Required(value = ENABLED_REQUIRED)
    public boolean getEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = true;

    /* package */final static int DESCRIPTION_LENGTH_MIN = 0;

    /* package */final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

}
