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
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.abiquo.server.core.config.Category;

/**
 * OVFPackages TODO: Description
 * 
 * @author destevez@abiquo.com, apuig
 */
@Entity
@Table(name = "ovf_package")
public class OVFPackage implements Serializable, PersistenceDto
{
    private static final long serialVersionUID = 2958869474155418264L;

    /**
     * OVFPackages identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_ovf_package")
    private Integer id;

    /**
     * OVFPackages name
     */
    private String name;

    /**
     * OVFPackages description
     */
    private String description;

    /**
     * OVFPackages url
     */
    private String url;

    private String productName;

    private String productUrl;

    private String productVersion;

    private String productVendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategory")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idIcon")
    private Icon icon;

    @Enumerated(value = javax.persistence.EnumType.STRING)
    private com.abiquo.model.enumerator.DiskFormatType type;

    private Long diskSizeMb;

    /**
     * Datacenter Repository associated
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apps_library")
    private AppsLibrary appsLibrary;

    /**
     * OVF Package List Association
     */
    @ManyToMany(targetEntity = OVFPackageList.class, mappedBy = "ovfPackages", cascade = CascadeType.DETACH)
    private List<OVFPackageList> ovfPackageLists;

    public OVFPackage()
    {
    }

    /**
     * @return the idOVFPackages
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
     * @param idOVFPackages the idOVFPackages to set
     */
    public void setId(final Integer idOVFPackages)
    {
        this.id = idOVFPackages;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setOvfPackageLists(List<OVFPackageList> ovfPackageLists)
    {
        this.ovfPackageLists = ovfPackageLists;
    }

    public List<OVFPackageList> getOvfPackageLists()
    {
        return ovfPackageLists;
    }

    public AppsLibrary getAppsLibrary()
    {
        return appsLibrary;
    }

    public void setAppsLibrary(AppsLibrary appsLibrary)
    {
        this.appsLibrary = appsLibrary;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductUrl()
    {
        return productUrl;
    }

    public void setProductUrl(String productUrl)
    {
        this.productUrl = productUrl;
    }

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(String productVersion)
    {
        this.productVersion = productVersion;
    }

    public String getProductVendor()
    {
        return productVendor;
    }

    public void setProductVendor(String productVendor)
    {
        this.productVendor = productVendor;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }

    public Long getDiskSizeMb()
    {
        return diskSizeMb;
    }

    public void setDiskSizeMb(Long diskSizeMb)
    {
        this.diskSizeMb = diskSizeMb;
    }

    public com.abiquo.model.enumerator.DiskFormatType getType()
    {
        return type;
    }

    public void setType(com.abiquo.model.enumerator.DiskFormatType type)
    {
        this.type = type;
    }

}
