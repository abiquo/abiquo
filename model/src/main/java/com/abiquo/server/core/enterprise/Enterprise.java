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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.validation.LimitRange;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.pricing.PricingTemplate;
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

    public void setName(final String name)
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

    public void setIsReservationRestricted(final boolean isReservationRestricted)
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

    private void setRepositorySoft(final long repositorySoft)
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

    private void setRepositoryHard(final long repositoryHard)
    {
        this.repositoryHard = repositoryHard;
    }

    @LimitRange(type = "repository")
    public Limit getRepositoryLimits()
    {
        return new Limit(repositorySoft, repositoryHard);
    }

    public void setRepositoryLimits(final Limit limit)
    {
        setRepositorySoft(limit.soft);
        setRepositoryHard(limit.hard);
    }

    private final static boolean PRICING_REQUIRED = false;

    @JoinColumn(name = PricingTemplate.ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_pricingTemp")
    private PricingTemplate pricingTemplate;

    @Required(value = PRICING_REQUIRED)
    public PricingTemplate getPricingTemplate()
    {
        return pricingTemplate;
    }

    public void setPricingTemplate(final PricingTemplate pricingTemplate)
    {
        this.pricingTemplate = pricingTemplate;
    }

    // *************************** Mandatory constructors ***********************
    public Enterprise(final String name, final int ramSoftLimitInMb, final int cpuCountSoftLimit,
        final long hdSoftLimitInMb, final int ramHardLimitInMb, final int cpuCountHardLimit,
        final long hdHardLimitInMb)
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

    public User createUser(final Role role, final String name, final String surname,
        final String email, final String nick, final String password, final String locale)
    {
        return new User(this, role, name, surname, email, nick, password, locale);
    }

}
