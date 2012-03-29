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

package com.abiquo.server.core.appslibrary;

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
import com.abiquo.server.core.enterprise.Enterprise;
import com.google.common.annotations.VisibleForTesting;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Category.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Category.TABLE_NAME)
public class Category extends DefaultEntityBase
{
    public static final String TABLE_NAME = "category";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Category()
    {
        // Just for JPA support
    }

    public Category(final String name)
    {
        setName(name);
        setDefaultCategory(false);
        setErasable(true);
        setEnterprise(null);
    }

    private final static String ID_COLUMN = "idCategory";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    /* package */final static int NAME_LENGTH_MIN = 1;

    /* package */final static int NAME_LENGTH_MAX = 30;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name = "";

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

    public final static String DEFAULT_PROPERTY = "defaultCategory";

    private final static boolean DEFAULT_REQUIRED = true;

    private final static String DEFAULT_COLUMN = "isDefault";

    @Column(name = DEFAULT_COLUMN, columnDefinition = "int", nullable = false)
    private boolean defaultCategory = false;

    @Required(value = DEFAULT_REQUIRED)
    public boolean isDefaultCategory()
    {
        return this.defaultCategory;
    }

    // Should never be set. Default category is always present in DB
    private void setDefaultCategory(final boolean defaultCategory)
    {
        this.defaultCategory = defaultCategory;
        this.erasable = false;
    }

    public final static String ERASABLE_PROPERTY = "erasable";

    private final static boolean ERASABLE_REQUIRED = true;

    private final static String ERASABLE_COLUMN = "isErasable";

    @Column(name = ERASABLE_COLUMN, columnDefinition = "int", nullable = false)
    private boolean erasable;

    @Required(value = ERASABLE_REQUIRED)
    public boolean isErasable()
    {
        return this.erasable;
    }

    // The default category should never be made erasable
    public void setErasable(final boolean erasable)
    {
        if (erasable && this.defaultCategory)
        {
            throw new IllegalStateException("Default category cannot be made erasable");
        }
        this.erasable = erasable;
    }

    // This is made package protected ONLY to be used by the generator fortesting purposes
    @VisibleForTesting
    /* package */static Category defaultCategory(final String name)
    {
        Category category = new Category(name);
        category.setDefaultCategory(true);
        return category;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = false;

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

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

}
