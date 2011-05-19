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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

/**
 * @author ssedano
 */
@Entity
@Table(name = OneTimeTokenSession.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = OneTimeTokenSession.TABLE_NAME)
public class OneTimeTokenSession extends DefaultEntityBase
{
    public static final String TABLE_NAME = "one_time_token";

    protected OneTimeTokenSession()
    {
    }

    public OneTimeTokenSession(String token)
    {
        this.token = token;
    }

    private final static String ID_COLUMN = "idOneTimeTokenSession";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String TOKEN_PROPERTY = "token";

    private final static boolean TOKEN_REQUIRED = false;

    final static int TOKEN_LENGTH_MIN = 0;

    final static int TOKEN_LENGTH_MAX = 255;

    private final static boolean TOKEN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String TOKEN_COLUMN = "token";

    @Column(name = TOKEN_COLUMN, nullable = !TOKEN_REQUIRED, length = TOKEN_LENGTH_MAX)
    private String token;

    @Required(value = TOKEN_REQUIRED)
    @Length(min = TOKEN_LENGTH_MIN, max = TOKEN_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = TOKEN_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getToken()
    {
        return this.token;
    }

    private void setToken(String token)
    {
        this.token = token;
    }

}
