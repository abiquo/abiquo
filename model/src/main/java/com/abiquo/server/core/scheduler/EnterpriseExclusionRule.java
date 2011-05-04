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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = EnterpriseExclusionRule.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = EnterpriseExclusionRule.TABLE_NAME)
public class EnterpriseExclusionRule extends DefaultEntityBase implements PersistentRule
{
    public static final String TABLE_NAME = "workload_enterprise_exclusion_rule";

    public EnterpriseExclusionRule(Enterprise enterprise1, Enterprise enterprise2)
    {
        super();

        setEnterprise1(enterprise1);
        setEnterprise2(enterprise2);
    }

    protected EnterpriseExclusionRule()
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

    public final static String ENTERPRISE1_PROPERTY = "enterprise1";

    private final static boolean ENTERPRISE1_REQUIRED = true;

    private final static String ENTERPRISE1_ID_COLUMN = "idEnterprise1";

    @JoinColumn(name = ENTERPRISE1_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    // @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise1")
    private Enterprise enterprise1;

    @Required(value = ENTERPRISE1_REQUIRED)
    public Enterprise getEnterprise1()
    {
        return this.enterprise1;
    }

    public void setEnterprise1(Enterprise enterprise1)
    {
        this.enterprise1 = enterprise1;
    }

    public final static String ENTERPRISE2_PROPERTY = "enterprise2";

    private final static boolean ENTERPRISE2_REQUIRED = true;

    private final static String ENTERPRISE2_ID_COLUMN = "idEnterprise2";

    @JoinColumn(name = ENTERPRISE2_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    // @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_enterprise2")
    private Enterprise enterprise2;

    @Required(value = ENTERPRISE2_REQUIRED)
    public Enterprise getEnterprise2()
    {
        return this.enterprise2;
    }

    public void setEnterprise2(Enterprise enterprise2)
    {
        this.enterprise2 = enterprise2;
    }

}
