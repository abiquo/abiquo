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
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * OVFPackagesList TODO: Description
 * 
 * @author destevez@abiquo.com, apuig
 */
@Entity
@Table(name = "ovf_package_list")
public class OVFPackageList implements Serializable, PersistenceDto
{

    private static final long serialVersionUID = 5404783524189416182L;

    /**
     * OVFPackagesList identifier
     */
    @Id
    @GeneratedValue
    @Column(name = "id_ovf_package_list")
    private Integer id;

    /**
     * OVFPackagesList name
     */
    private String name;

    /** The source of the list. */
    private String url;

    /**
     * Datacenter Repository associated
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apps_library")
    private AppsLibrary appsLibrary;

    /**
     * OVFPackages included in this list
     */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OVFPackage.class, cascade = CascadeType.DETACH)
    @JoinTable(name = "ovf_package_list_has_ovf_package", joinColumns = @JoinColumn(name = "id_ovf_package_list"), inverseJoinColumns = @JoinColumn(name = "id_ovf_package"))
    private List<OVFPackage> ovfPackages;

    public OVFPackageList()
    {
    }

    /**
     * @return the idOVFPackagesList
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param idOVFPackagesList the idOVFPackagesList to set
     */
    public void setId(final Integer idOVFPackagesList)
    {
        this.id = idOVFPackagesList;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setOvfPackages(List<OVFPackage> ovfPackages)
    {
        this.ovfPackages = ovfPackages;
    }

    public List<OVFPackage> getOvfPackages()
    {
        if (ovfPackages == null)
        {
            ovfPackages = new LinkedList<OVFPackage>();
        }

        return ovfPackages;
    }

    public AppsLibrary getAppsLibrary()
    {
        return appsLibrary;
    }

    public void setAppsLibrary(AppsLibrary appsLibrary)
    {
        this.appsLibrary = appsLibrary;
    }

}
