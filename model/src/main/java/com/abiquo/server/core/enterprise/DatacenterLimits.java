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

import org.hibernate.validator.constraints.Range;

import com.abiquo.model.validation.LimitRange;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.Limit;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DatacenterLimits.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DatacenterLimits.TABLE_NAME)
public class DatacenterLimits extends DefaultEntityWithLimits
{
    public static final String TABLE_NAME = "enterprise_limits_by_datacenter";

    protected DatacenterLimits()
    {
    }

    private final static String ID_COLUMN = "idDatacenterLimit";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    // ****************************** Associations
    public static final String DATACENTER_PROPERTY = "datacenter";

    public static final String DATACENTER_ID_COLUMN = "idDataCenter";

    public static final boolean DATACENTER_REQUIRED = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = DATACENTER_ID_COLUMN)
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    private void setDatacenter(Datacenter value)
    {
        this.datacenter = value;
    }

    public final static String ENTERPRISE_PROPERTY = "enterprise";

    private final static boolean ENTERPRISE_REQUIRED = true;

    private final static String ENTERPRISE_ID_COLUMN = "idEnterprise";

    /**
     * Enterprise associated to this limits
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ENTERPRISE_ID_COLUMN)
    private Enterprise enterprise;

    public void setEnterprise(Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    @Required(value = ENTERPRISE_REQUIRED)
    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    // ************************** Mandatory constructors ***********************
    public DatacenterLimits(Enterprise ent, Datacenter dc)
    {
        setEnterprise(ent);
        setDatacenter(dc);
        setRamLimitsInMb(new Limit(0L, 0L));
        setHdLimitsInMb(new Limit(0L, 0L));
        setCpuCountLimits(new Limit(0L, 0L));
        setRepositoryLimits(new Limit(0L, 0L));
        setPublicIPLimits(new Limit(0L, 0L));
        setVlansLimits(new Limit(0L, 0L));
    }

    // *************************** Mandatory constructors ***********************
    public DatacenterLimits(Enterprise ent, Datacenter dc, int ramSoftLimitInMb,
        int cpuCountSoftLimit, long hdSoftLimitInMb, int ramHardLimitInMb, int cpuCountHardLimit,
        long hdHardLimitInMb, long storageSoftLimitInMb, long storageHardLimitInMb,
        long publicIPsoft, long publicIPHard, long vlanHard, long vlanSoft)
    {
        setEnterprise(ent);
        setDatacenter(dc);
        setRamLimitsInMb(new Limit((long) ramSoftLimitInMb, (long) ramHardLimitInMb));
        setHdLimitsInMb(new Limit(hdSoftLimitInMb, hdHardLimitInMb));
        setCpuCountLimits(new Limit((long) cpuCountSoftLimit, (long) cpuCountHardLimit));
        setStorageLimits(new Limit(storageSoftLimitInMb, storageHardLimitInMb));
        setPublicIPLimits(new Limit((long) publicIPsoft, (long) publicIPHard));
        setVlansLimits(new Limit((long) vlanHard, (long) vlanSoft));
        setRepositoryLimits(new Limit(0L, 0L));
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

}
