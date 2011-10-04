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

package com.abiquo.server.core.scheduler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = FitPolicyRule.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = FitPolicyRule.TABLE_NAME)
public class FitPolicyRule extends DefaultEntityBase implements PersistentRule
{
    public static final String TABLE_NAME = "workload_fit_policy_rule";

    public FitPolicyRule(Datacenter datacenter, FitPolicy fitpolicy)
    {
        super();
        setDatacenter(datacenter);
        setFitPolicy(fitpolicy);
    }

    // default, apply to all datacenters
    public FitPolicyRule(FitPolicy fitpolicy)
    {
        super();
        setFitPolicy(fitpolicy);
    }

    protected FitPolicyRule()
    {
    }

    private final static String ID_COLUMN = "id";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String DATACENTER_PROPERTY = "datacenter";

    private final static boolean DATACENTER_REQUIRED = false;

    private final static String DATACENTER_ID_COLUMN = "idDatacenter";

    @JoinColumn(name = DATACENTER_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_datacenter")
    private Datacenter datacenter;

    @Required(value = DATACENTER_REQUIRED)
    public Datacenter getDatacenter()
    {
        return this.datacenter;
    }

    public void setDatacenter(Datacenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public final static String FIT_POLICY_PROPERTY = "fitPolicy";

    private final static boolean FIT_POLICY_REQUIRED = true;

    private final static String FIT_POLICY_COLUMN = "fitPolicy";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = FIT_POLICY_COLUMN, nullable = !FIT_POLICY_REQUIRED)
    private FitPolicy fitPolicy;

    @Required(value = FIT_POLICY_REQUIRED)
    public FitPolicy getFitPolicy()
    {
        return this.fitPolicy;
    }

    private void setFitPolicy(FitPolicy fitPolicy)
    {
        this.fitPolicy = fitPolicy;
    }

    public enum FitPolicy
    {
        /**
         * Choose the machine that is under greater load
         */
        PROGRESSIVE,
        /**
         * Choose the machine that is under lesser load
         */
        PERFORMANCE;
    }

}
