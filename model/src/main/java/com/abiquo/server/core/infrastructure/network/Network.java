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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Network.TABLE_NAME, uniqueConstraints = {})
// TODO: specify unique constraints
@org.hibernate.annotations.Table(appliesTo = Network.TABLE_NAME, indexes = {})
// TODO: specify indexes
public class Network extends DefaultEntityBase
{

    // ****************************** JPA support *******************************
    public static final String TABLE_NAME = "network";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Network()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "network_id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    // ******************************* Properties *******************************
    public final static String UUID_PROPERTY = "uuid";

    private final static boolean UUID_REQUIRED = true;

    private final static int UUID_LENGTH_MIN = 1;

    private final static int UUID_LENGTH_MAX = 40;

    private final static boolean UUID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String UUID_COLUMN = "uuid";

    @Column(name = UUID_COLUMN, nullable = !UUID_REQUIRED, length = UUID_LENGTH_MAX)
    private String uuid;

    @Required(value = UUID_REQUIRED)
    @Length(min = UUID_LENGTH_MIN, max = UUID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = UUID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getUuid()
    {
        return this.uuid;
    }

    private void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    // ****************************** Associations ******************************
    // TODO: define associations

    // *************************** Mandatory constructors ***********************
    public Network(String uuid)
    {
        setUuid(uuid);
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
