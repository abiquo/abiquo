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

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.abiquo.server.core.enterprise.Enterprise;

/**
 * Aggregate OVFPackageList and OVFPackages of an enterprise
 * 
 * @author apuig
 */
@Entity
@Table(name = "apps_library")
public class AppsLibrary implements Serializable, PersistenceDto
{
    private static final long serialVersionUID = -1074018257447931247L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_apps_library")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEnterprise")
    private Enterprise enterprise;

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

    public AppsLibrary(Enterprise enterprise)
    {
        setEnterprise(enterprise);
    }

    public AppsLibrary()
    {

    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public List<OVFPackageList> getOvfPackageLists()
    {
        return ovfPackageLists;
    }

    public void setOvfPackageLists(List<OVFPackageList> ovfPackageLists)
    {
        this.ovfPackageLists = ovfPackageLists;
    }

    public List<OVFPackage> getOvfPackages()
    {
        return ovfPackages;
    }

    public void setOvfPackages(List<OVFPackage> ovfPackages)
    {
        this.ovfPackages = ovfPackages;
    }

}
