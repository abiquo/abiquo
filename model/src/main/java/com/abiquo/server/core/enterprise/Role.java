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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Role.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Role.TABLE_NAME)
public class Role extends DefaultEntityBase
{
    public static final String TABLE_NAME = "role";

    // TODO Alessia quittar campos y cambiar name, enterprise nullable

    protected Role()
    {
    }

    private final static String ID_COLUMN = "idRole";

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

    public void setName(final String name)
    {
        this.name = name;
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

    public final static String BLOCKED_PROPERTY = "blocked";

    private final static String BLOCKED_COLUMN = "blocked";

    private final static boolean BLOCKED_REQUIRED = true;

    @Column(name = BLOCKED_COLUMN, nullable = !BLOCKED_REQUIRED)
    private boolean blocked;

    @Required(value = BLOCKED_REQUIRED)
    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(final boolean blocked)
    {
        this.blocked = blocked;
    }

    public final static String SHORT_DESCRIPTION_PROPERTY = "shortDescription";

    private final static boolean SHORT_DESCRIPTION_REQUIRED = false;

    private final static int SHORT_DESCRIPTION_LENGTH_MIN = 0;

    private final static int SHORT_DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean SHORT_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SHORT_DESCRIPTION_COLUMN = "shortDescription";

    @Column(name = SHORT_DESCRIPTION_COLUMN, nullable = !SHORT_DESCRIPTION_REQUIRED, length = SHORT_DESCRIPTION_LENGTH_MAX)
    private String shortDescription;

    @Required(value = SHORT_DESCRIPTION_REQUIRED)
    @Length(min = SHORT_DESCRIPTION_LENGTH_MIN, max = SHORT_DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SHORT_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getShortDescription()
    {
        return this.shortDescription;
    }

    public void setShortDescription(final String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public final static String LARGE_DESCRIPTION_PROPERTY = "largeDescription";

    private final static boolean LARGE_DESCRIPTION_REQUIRED = false;

    private final static int LARGE_DESCRIPTION_LENGTH_MIN = 0;

    private final static int LARGE_DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean LARGE_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String LARGE_DESCRIPTION_COLUMN = "largeDescription";

    @Column(name = LARGE_DESCRIPTION_COLUMN, nullable = !LARGE_DESCRIPTION_REQUIRED, length = LARGE_DESCRIPTION_LENGTH_MAX)
    private String largeDescription;

    @Required(value = LARGE_DESCRIPTION_REQUIRED)
    @Length(min = LARGE_DESCRIPTION_LENGTH_MIN, max = LARGE_DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = LARGE_DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getLargeDescription()
    {
        return this.largeDescription;
    }

    public void setLargeDescription(final String largeDescription)
    {
        this.largeDescription = largeDescription;
    }

    public final static String SECURITY_LEVEL_PROPERTY = "securityLevel";

    private final static String SECURITY_LEVEL_COLUMN = "securityLevel";

    private final static int SECURITY_LEVEL_MIN = 0;

    private final static int SECURITY_LEVEL_MAX = Integer.MAX_VALUE;

    @Column(name = SECURITY_LEVEL_COLUMN, nullable = true)
    @Range(min = SECURITY_LEVEL_MIN, max = SECURITY_LEVEL_MAX)
    private float securityLevel;

    public float getSecurityLevel()
    {
        return this.securityLevel;
    }

    public void setSecurityLevel(final float securityLevel)
    {
        this.securityLevel = securityLevel;
    }

    public Role(final Type type, final String shortDescription, final String largeDescription,
        final float securityLevel, final String name, final boolean blocked)
    {
        setType(type);
        setShortDescription(shortDescription);
        setLargeDescription(largeDescription);
        setSecurityLevel(securityLevel);
        setName(name);
        setBlocked(blocked);
    }

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Required(value = true)
    public Type getType()
    {
        return type;
    }

    private void setType(final Type type)
    {
        this.type = type;
    }

    public static enum Type
    {
        SYS_ADMIN, ENTERPRISE_ADMIN, USER;
    }

    public final static String ASSOCIATION_TABLE = "roles_privileges";

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Privilege.class, cascade = CascadeType.DETACH)
    @JoinTable(name = ASSOCIATION_TABLE, joinColumns = @JoinColumn(name = "idRole"), inverseJoinColumns = @JoinColumn(name = "idPrivilege"))
    private List<Privilege> privileges;

    public List<Privilege> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges(final List<Privilege> privileges)
    {
        this.privileges = privileges;
    }

    // ************************* Helper methods ****************************

    public void addPrivilege(final Privilege privilege)
    {
        if (privileges == null)
        {
            privileges = new ArrayList<Privilege>();
        }
        privileges.add(privilege);
    }
}
