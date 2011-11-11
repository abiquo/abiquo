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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Icon.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Icon.TABLE_NAME)
public class Icon extends DefaultEntityBase
{
    public static final String TABLE_NAME = "icon";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Icon()
    {

    }

    public Icon(final String name, final String iconPath)
    {
        setName(name);
        setPath(iconPath);
    }

    private final static String ID_COLUMN = "idIcon";

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

    /* package */final static int NAME_LENGTH_MIN = 0;

    /* package */final static int NAME_LENGTH_MAX = 20;

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

    public final static String PATH_PROPERTY = "path";

    private final static boolean PATH_REQUIRED = true;

    /* package */final static int PATH_LENGTH_MIN = 0;

    /* package */final static int PATH_LENGTH_MAX = 200;

    private final static boolean PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PATH_COLUMN = "path";

    @Column(name = PATH_COLUMN, nullable = !PATH_REQUIRED, length = PATH_LENGTH_MAX)
    private String path;

    @Required(value = PATH_REQUIRED)
    @Length(min = PATH_LENGTH_MIN, max = PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPath()
    {
        return this.path;
    }

    public void setPath(final String path)
    {
        this.path = path;
    }

}
