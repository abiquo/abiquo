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

package com.abiquo.server.core.appslib;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Category.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Category.TABLE_NAME)
public class Category extends DefaultEntityBase
{
    public static final String TABLE_NAME = "category";

    protected Category()
    {
    }

    public Category(String name)
    {
        setName(name);
        setIsDefault(0);
        setIsErasable(1);
    }

    public Category(String name, int isDefault, int isErasable)
    {
        setName(name);
        setIsDefault(0);
        setIsErasable(1);
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

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    public final static int NAME_LENGTH_MIN = 0;

    public final static int NAME_LENGTH_MAX = 255;

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

    private void setName(String name)
    {
        this.name = name;
    }

    public final static String IS_DEFAULT_PROPERTY = "isDefault";

    private final static boolean IS_DEFAULT_REQUIRED = true;

    private final static String IS_DEFAULT_COLUMN = "isDefault";

    private final static int IS_DEFAULT_MIN = Integer.MIN_VALUE;

    private final static int IS_DEFAULT_MAX = Integer.MAX_VALUE;

    @Column(name = IS_DEFAULT_COLUMN, nullable = !IS_DEFAULT_REQUIRED)
    @Range(min = IS_DEFAULT_MIN, max = IS_DEFAULT_MAX)
    private int isDefault;

    public int getIsDefault()
    {
        return this.isDefault;
    }

    private void setIsDefault(int isDefault)
    {
        this.isDefault = isDefault;
    }

    public final static String IS_ERASABLE_PROPERTY = "isErasable";

    private final static boolean IS_ERASABLE_REQUIRED = true;

    private final static String IS_ERASABLE_COLUMN = "isErasable";

    private final static int IS_ERASABLE_MIN = Integer.MIN_VALUE;

    private final static int IS_ERASABLE_MAX = Integer.MAX_VALUE;

    @Column(name = IS_ERASABLE_COLUMN, nullable = !IS_ERASABLE_REQUIRED)
    @Range(min = IS_ERASABLE_MIN, max = IS_ERASABLE_MAX)
    private int isErasable;

    public int getIsErasable()
    {
        return this.isErasable;
    }

    private void setIsErasable(int isErasable)
    {
        this.isErasable = isErasable;
    }

}
