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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import com.abiquo.model.validation.LimitRange;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.Limit;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Enterprise.TABLE_NAME, uniqueConstraints = {})
@org.hibernate.annotations.Table(appliesTo = Enterprise.TABLE_NAME, indexes = {})
public class Enterprise extends DefaultEntityWithLimits
{

    // ****************************** JPA support *******************************
    public static final String TABLE_NAME = "enterprise";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected Enterprise()
    {
        // Just for JPA support
        setName("FIXME");
    }

    private final static String ID_COLUMN = "idEnterprise";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    // ******************************* Properties *******************************
    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = true;

    public final static int NAME_LENGTH_MIN = 1;

    public final static int NAME_LENGTH_MAX = 40;

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

    public final static String IS_RESTRICTED_RESERVATION_PROPERTY = "isReservationRestricted";

    private final static boolean IS_RESTRICTED_RESERVATION_REQUIRED = false;

    private final static String IS_RESTRICTED_RESERVATION_COLUMN = "isReservationRestricted";

    @Column(name = IS_RESTRICTED_RESERVATION_COLUMN, nullable = !IS_RESTRICTED_RESERVATION_REQUIRED)
    private boolean isReservationRestricted;

    @Required(value = IS_RESTRICTED_RESERVATION_REQUIRED)
    public boolean getIsReservationRestricted()
    {
        return this.isReservationRestricted;
    }

    public void setIsReservationRestricted(boolean isReservationRestricted)
    {
        this.isReservationRestricted = isReservationRestricted;
    }

    public final static String REPOSITORY_SOFT_PROPERTY = "repositorySoft";

    /* package */final static String REPOSITORY_SOFT_COLUMN = "repositorySoft";

    /* package */final static long REPOSITORY_SOFT_MIN = 0;

    /* package */final static long REPOSITORY_SOFT_MAX = Long.MAX_VALUE;

    /* package */final static boolean REPOSITORY_SOFT_REQUIRED = true;

    @Column(name = REPOSITORY_SOFT_COLUMN, nullable = false)
    @Range(min = REPOSITORY_SOFT_MIN, max = REPOSITORY_SOFT_MAX)
    private long repositorySoft;

    @Required(value = REPOSITORY_SOFT_REQUIRED)
    public long getRepositorySoft()
    {
        return this.repositorySoft;
    }

    private void setRepositorySoft(long repositorySoft)
    {
        this.repositorySoft = repositorySoft;
    }

    public final static String REPOSITORY_HARD_PROPERTY = "repositoryHard";

    /* package */final static String REPOSITORY_HARD_COLUMN = "repositoryHard";

    /* package */final static long REPOSITORY_HARD_MIN = 0;

    /* package */final static long REPOSITORY_HARD_MAX = Long.MAX_VALUE;

    /* package */final static boolean REPOSITORY_HARD_REQUIRED = true;

    @Column(name = REPOSITORY_HARD_COLUMN, nullable = false)
    @Range(min = REPOSITORY_HARD_MIN, max = REPOSITORY_HARD_MAX)
    private long repositoryHard;

    @Required(value = REPOSITORY_HARD_REQUIRED)
    public long getRepositoryHard()
    {
        return this.repositoryHard;
    }

    private void setRepositoryHard(long repositoryHard)
    {
        this.repositoryHard = repositoryHard;
    }

    @LimitRange(type = "repository")
    public Limit getRepositoryLimits()
    {
        return new Limit(repositorySoft, repositoryHard);
    }

    public void setRepositoryLimits(Limit limit)
    {
        setRepositorySoft(limit.soft);
        setRepositoryHard(limit.hard);
    }

    public final static String CHEF_URL_PROPERTY = "chefURL";

    private final static boolean CHEF_URL_REQUIRED = false;

    public final static int CHEF_URL_LENGTH_MIN = 1;

    public final static int CHEF_URL_LENGTH_MAX = 255;

    private final static boolean CHEF_URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String CHEF_URL_COLUMN = "chef_url";

    @Column(name = CHEF_URL_COLUMN, nullable = !CHEF_URL_REQUIRED, length = CHEF_URL_LENGTH_MAX)
    private String chefURL;

    @Required(value = CHEF_URL_REQUIRED)
    @Length(min = CHEF_URL_LENGTH_MIN, max = CHEF_URL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = CHEF_URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @URL
    public String getChefURL()
    {
        return chefURL;
    }

    public void setChefURL(String chefURL)
    {
        this.chefURL = chefURL;
    }

    public final static String CHEF_CLIENT_CERT_PROPERTY = "chefClientCertificate";

    private final static boolean CHEF_CLIENT_CERT_REQUIRED = false;

    private final static boolean CHEF_CLIENT_CERT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String CHEF_CLIENT_CERT_COLUMN = "chef_client_certificate";

    @Column(name = CHEF_CLIENT_CERT_COLUMN, nullable = !CHEF_CLIENT_CERT_REQUIRED, columnDefinition = "TEXT")
    private String chefClientCertificate;

    @Required(value = CHEF_CLIENT_CERT_REQUIRED)
    @LeadingOrTrailingWhitespace(allowed = CHEF_CLIENT_CERT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getChefClientCertificate()
    {
        return chefClientCertificate;
    }

    public void setChefClientCertificate(String chefClientCertificate)
    {
        this.chefClientCertificate = chefClientCertificate.trim();
    }

    public final static String CHEF_VALIDATOR_CERT_PROPERTY = "chefValidatorCertificate";

    private final static boolean CHEF_VALIDATOR_CERT_REQUIRED = false;

    private final static boolean CHEF_VALIDATOR_CERT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED =
        false;

    private final static String CHEF_VALIDATOR_CERT_COLUMN = "chef_validator_certificate";

    @Column(name = CHEF_VALIDATOR_CERT_COLUMN, nullable = !CHEF_VALIDATOR_CERT_REQUIRED, columnDefinition = "TEXT")
    private String chefValidatorCertificate;

    @Required(value = CHEF_VALIDATOR_CERT_REQUIRED)
    @LeadingOrTrailingWhitespace(allowed = CHEF_VALIDATOR_CERT_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getChefValidatorCertificate()
    {
        return chefValidatorCertificate;
    }

    public void setChefValidatorCertificate(String chefValidatorCertificate)
    {
        this.chefValidatorCertificate = chefValidatorCertificate.trim();
    }

    // *************************** Mandatory constructors ***********************
    public Enterprise(String name, int ramSoftLimitInMb, int cpuCountSoftLimit,
        long hdSoftLimitInMb, int ramHardLimitInMb, int cpuCountHardLimit, long hdHardLimitInMb)
    {
        setName(name);
        setIsReservationRestricted(Boolean.FALSE);
        setRamLimitsInMb(new Limit((long) ramSoftLimitInMb, (long) ramHardLimitInMb));
        setHdLimitsInMb(new Limit(hdSoftLimitInMb, hdHardLimitInMb));
        setCpuCountLimits(new Limit((long) cpuCountSoftLimit, (long) cpuCountHardLimit));
    }

    // ********************************** Others ********************************
    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    // I don't want to access the users directly but I want to remove them in cascade
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "enterprise")
    private final List<User> users = new ArrayList<User>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "enterprise")
    private final List<DatacenterLimits> datacenterLimits = new ArrayList<DatacenterLimits>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "enterprise")
    private final List<VirtualImage> virtualImages = new ArrayList<VirtualImage>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "enterprise")
    private final List<AppsLibrary> appsLibraries = new ArrayList<AppsLibrary>();

    public User createUser(Role role, String name, String surname, String email, String nick,
        String password, String locale)
    {
        return new User(this, role, name, surname, email, nick, password, locale);
    }
}
