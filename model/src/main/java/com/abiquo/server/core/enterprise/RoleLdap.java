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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = RoleLdap.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = RoleLdap.TABLE_NAME)
public class RoleLdap extends DefaultEntityBase
{
    public static final String TABLE_NAME = "role_ldap";

    public RoleLdap()
    {
    }

    public RoleLdap(final String roleLdap, final Role role)
    {
        super();
        this.roleLdap = roleLdap;
        this.role = role;
    }

    private final static String ID_COLUMN = "idRole_ldap";

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

    public final static String ROLE_PROPERTY = "role";

    private final static boolean ROLE_REQUIRED = true;

    private final static String ROLE_ID_COLUMN = "idRole";

    @JoinColumn(name = ROLE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "fk_" + TABLE_NAME + "_role")
    private Role role;

    @Required(value = ROLE_REQUIRED)
    public Role getRole()
    {
        return this.role;
    }

    public void setRole(final Role role)
    {
        this.role = role;
    }

    public final static String ROLE_LDAP_PROPERTY = "roleLdap";

    private final static boolean ROLE_LDAP_REQUIRED = false;

    public final static int ROLE_LDAP_LENGTH_MIN = 0;

    public final static int ROLE_LDAP_LENGTH_MAX = 255;

    private final static boolean ROLE_LDAP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ROLE_LDAP_COLUMN = "role_ldap";

    @Column(name = ROLE_LDAP_COLUMN, nullable = !ROLE_LDAP_REQUIRED, length = ROLE_LDAP_LENGTH_MAX)
    private String roleLdap;

    @Required(value = ROLE_LDAP_REQUIRED)
    @Length(min = ROLE_LDAP_LENGTH_MIN, max = ROLE_LDAP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ROLE_LDAP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getRoleLdap()
    {
        return this.roleLdap;
    }

    public void setRoleLdap(final String roleLdap)
    {
        this.roleLdap = roleLdap;
    }

}
