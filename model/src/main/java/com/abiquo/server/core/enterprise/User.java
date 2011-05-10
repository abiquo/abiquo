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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = User.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = User.TABLE_NAME)
public class User extends DefaultEntityBase
{
    public static final String TABLE_NAME = "user";

    protected User()
    {
    }

    private final static String ID_COLUMN = "idUser";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

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

    public void setName(String name)
    {
        this.name = name;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = true;

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

    public void setEnterprise(Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public final static String NICK_PROPERTY = "nick";

    private final static boolean NICK_REQUIRED = false;

    private final static int NICK_LENGTH_MIN = 0;

    private final static int NICK_LENGTH_MAX = 255;

    private final static boolean NICK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NICK_COLUMN = "user";

    @Column(name = NICK_COLUMN, nullable = !NICK_REQUIRED, length = NICK_LENGTH_MAX)
    private String nick;

    @Required(value = NICK_REQUIRED)
    @Length(min = NICK_LENGTH_MIN, max = NICK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NICK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNick()
    {
        return this.nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public final static String ROLE_PROPERTY = "role";

    private final static boolean ROLE_REQUIRED = true;

    private final static String ROLE_ID_COLUMN = "idRole";

    @JoinColumn(name = ROLE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_role")
    private Role role;

    @Required(value = ROLE_REQUIRED)
    public Role getRole()
    {
        return this.role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }

    public final static String LOCALE_PROPERTY = "locale";

    private final static boolean LOCALE_REQUIRED = false;

    private final static int LOCALE_LENGTH_MIN = 0;

    private final static int LOCALE_LENGTH_MAX = 255;

    private final static boolean LOCALE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String LOCALE_COLUMN = "locale";

    @Column(name = LOCALE_COLUMN, nullable = !LOCALE_REQUIRED, length = LOCALE_LENGTH_MAX)
    private String locale;

    @Required(value = LOCALE_REQUIRED)
    @Length(min = LOCALE_LENGTH_MIN, max = LOCALE_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = LOCALE_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getLocale()
    {
        return this.locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public final static String PASSWORD_PROPERTY = "password";

    private final static boolean PASSWORD_REQUIRED = false;

    private final static int PASSWORD_LENGTH_MIN = 0;

    private final static int PASSWORD_LENGTH_MAX = 255;

    private final static boolean PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PASSWORD_COLUMN = "password";

    @Column(name = PASSWORD_COLUMN, nullable = !PASSWORD_REQUIRED, length = PASSWORD_LENGTH_MAX)
    private String password;

    @Required(value = PASSWORD_REQUIRED)
    @Length(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public final static String SURNAME_PROPERTY = "surname";

    private final static boolean SURNAME_REQUIRED = false;

    private final static int SURNAME_LENGTH_MIN = 0;

    private final static int SURNAME_LENGTH_MAX = 255;

    private final static boolean SURNAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String SURNAME_COLUMN = "surname";

    @Column(name = SURNAME_COLUMN, nullable = !SURNAME_REQUIRED, length = SURNAME_LENGTH_MAX)
    private String surname;

    @Required(value = SURNAME_REQUIRED)
    @Length(min = SURNAME_LENGTH_MIN, max = SURNAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = SURNAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getSurname()
    {
        return this.surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public final static String ACTIVE_PROPERTY = "active";

    private final static String ACTIVE_COLUMN = "active";

    private final static int ACTIVE_MIN = Integer.MIN_VALUE;

    private final static int ACTIVE_MAX = Integer.MAX_VALUE;

    @Column(name = ACTIVE_COLUMN, nullable = true)
    @Range(min = ACTIVE_MIN, max = ACTIVE_MAX)
    private int active;

    public int getActive()
    {
        return this.active;
    }

    public void setActive(int active)
    {
        this.active = active;
    }

    public final static String EMAIL_PROPERTY = "email";

    private final static boolean EMAIL_REQUIRED = false;

    private final static int EMAIL_LENGTH_MIN = 0;

    private final static int EMAIL_LENGTH_MAX = 255;

    private final static boolean EMAIL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String EMAIL_COLUMN = "email";

    @Column(name = EMAIL_COLUMN, nullable = !EMAIL_REQUIRED, length = EMAIL_LENGTH_MAX)
    private String email;

    @Required(value = EMAIL_REQUIRED)
    @Length(min = EMAIL_LENGTH_MIN, max = EMAIL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = EMAIL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    private final static int DESCRIPTION_LENGTH_MIN = 0;

    private final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = true;

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

    public void setDescription(String description)
    {
        this.description = description;
    }

    public final static String AVAILABLE_VIRTUAL_DATACENTERS_PROPERTY =
        "availableVirtualDatacenters";

    @Column(name = AVAILABLE_VIRTUAL_DATACENTERS_PROPERTY, nullable = true)
    private String availableVirtualDatacenters;

    @LeadingOrTrailingWhitespace(allowed = false)
    public String getAvailableVirtualDatacenters()
    {
        return this.availableVirtualDatacenters;
    }

    public void setAvailableVirtualDatacenters(String availableVirtualDatacenters)
    {
        this.availableVirtualDatacenters = availableVirtualDatacenters;
    }

    /**
     * Constructor.
     * 
     * @param enterprise Enterprise to which the user belonsg.
     * @param role Role in Abiquo.
     * @param name Name.
     * @param surname Surname.
     * @param email Email address.
     * @param nick Login.
     * @param password Password.
     * @param locale Language preferred for communications with Abiquo.
     * @deprecated use instead
     *             {@link #User(Enterprise, Role, String, String, String, String, String, String, AuthType)}
     */
    @Deprecated
    public User(Enterprise enterprise, Role role, String name, String surname, String email,
        String nick, String password, String locale)
    {
        setEnterprise(enterprise);
        setRole(role);
        setName(name);
        setSurname(surname);
        setEmail(email);
        setNick(nick);
        setPassword(password);
        setLocale(locale);
        setAuthType(AuthType.ABIQUO);
    }

    /**
     * Constructor.
     * 
     * @param enterprise Enterprise to which the user belonsg.
     * @param role Role in Abiquo.
     * @param name Name.
     * @param surname Surname.
     * @param email Email address.
     * @param nick Login.
     * @param password Password.
     * @param locale Language preferred for communications with Abiquo.
     * @param authType Which system this user signed up.
     */
    public User(Enterprise enterprise, Role role, String name, String surname, String email,
        String nick, String password, String locale, AuthType authType)
    {
        setEnterprise(enterprise);
        setRole(role);
        setName(name);
        setSurname(surname);
        setEmail(email);
        setNick(nick);
        setPassword(password);
        setLocale(locale);
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
    private List<Session> sessions = new ArrayList<Session>();

    protected void addSession(Session session)
    {
        sessions.add(session);
    }

    /**
     * Which system this user signed up. This also indicates to which system this user will
     * authenticate.
     */
    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = "authType", nullable = false)
    private AuthType authType;

    /**
     * Which system this user signed up. This also indicates to which system this user will
     * authenticate.
     * 
     * @return AuthType
     */
    @Required(value = true)
    public AuthType getAuthType()
    {
        return authType;
    }

    /**
     * Which system this user signed up. This also indicates to which system this user will
     * authenticate.
     * 
     * @param authType {@link com.abiquo.server.core.enterprise.User.AuthType} value.
     */
    private void setAuthType(AuthType authType)
    {
        this.authType = authType;
    }

    /**
     * Which system this user signed up. This also indicates to which system this user will
     * authenticate.
     * 
     * @author ssedano
     */
    public static enum AuthType
    {
        ABIQUO, LDAP;
    }
}
