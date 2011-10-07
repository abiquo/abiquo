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

package com.abiquo.server.core.appslibrary;

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

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = AppsLibrary.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = AppsLibrary.TABLE_NAME)
public class AppsLibrary extends DefaultEntityBase
{
    public static final String TABLE_NAME = "apps_library";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public AppsLibrary()
    {
        // Just for JPA support
    }

    public AppsLibrary(final Enterprise enterprise)
    {
        setEnterprise(enterprise);
    }

    private final static String ID_COLUMN = "id_apps_library";

    /**
     * List of OVFPackageLists
     */
    @OneToMany(cascade = CascadeType.ALL, targetEntity = OVFPackageList.class, fetch = FetchType.LAZY, mappedBy = "appsLibrary")
    private List<OVFPackageList> ovfPackageLists;

    /**
     * List of OVFPackages
     */
    @OneToMany(cascade = CascadeType.ALL, targetEntity = OVFPackage.class, fetch = FetchType.LAZY, mappedBy = "appsLibrary")
    private List<OVFPackage> ovfPackages;

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
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

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public List<OVFPackageList> getOvfPackageLists()
    {
        return ovfPackageLists;
    }

    public void setOvfPackageLists(final List<OVFPackageList> ovfPackageLists)
    {
        this.ovfPackageLists = ovfPackageLists;
    }

    public List<OVFPackage> getOvfPackages()
    {
        return ovfPackages;
    }

    public void setOvfPackages(final List<OVFPackage> ovfPackages)
    {
        this.ovfPackages = ovfPackages;
    }

}
