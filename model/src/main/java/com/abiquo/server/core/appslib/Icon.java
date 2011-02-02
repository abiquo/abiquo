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

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Icon.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Icon.TABLE_NAME)
public class Icon extends DefaultEntityBase
{
    public static final String TABLE_NAME = "icon";

    protected Icon()
    {
    }

    public Icon(String path)
    {
        setPath(path);
        setName("icon");
    }

    public Icon(String path, String name)
    {
        setPath(path);
        setName(name);
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

    private void setName(String name)
    {
        this.name = name;
    }

    public final static String PATH_PROPERTY = "path";

    private final static boolean PATH_REQUIRED = true;

    public final static int PATH_LENGTH_MIN = 0;

    public final static int PATH_LENGTH_MAX = 255;

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

    private void setPath(String path)
    {
        this.path = path;
    }

}
