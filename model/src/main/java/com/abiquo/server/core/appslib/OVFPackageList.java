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

package com.abiquo.server.core.appslib;

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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = OVFPackageList.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = OVFPackageList.TABLE_NAME)
public class OVFPackageList extends DefaultEntityBase
{
    public static final String TABLE_NAME = "ovf_package_list";

    protected OVFPackageList()
    {
    }

    public OVFPackageList(AppsLibrary appsLibrary)
    {
        setAppsLibrary(appsLibrary);
    }

    private final static String ID_COLUMN = "id_ovf_package_list";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String APPS_LIBRARY_PROPERTY = "appsLibrary";

    private final static boolean APPS_LIBRARY_REQUIRED = true;

    private final static String APPS_LIBRARY_ID_COLUMN = "id_apps_library";

    @JoinColumn(name = APPS_LIBRARY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_appsLibrary")
    private AppsLibrary appsLibrary;

    @Required(value = APPS_LIBRARY_REQUIRED)
    public AppsLibrary getAppsLibrary()
    {
        return this.appsLibrary;
    }

    public void setAppsLibrary(AppsLibrary appsLibrary)
    {
        this.appsLibrary = appsLibrary;
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

    private void setName(String name)
    {
        this.name = name;
    }

    public final static String URL_PROPERTY = "url";

    private final static boolean URL_REQUIRED = false;

    private final static int URL_LENGTH_MIN = 0;

    private final static int URL_LENGTH_MAX = 255;

    private final static boolean URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String URL_COLUMN = "url";

    @Column(name = URL_COLUMN, nullable = !URL_REQUIRED, length = URL_LENGTH_MAX)
    private String url;

    @Required(value = URL_REQUIRED)
    @Length(min = URL_LENGTH_MIN, max = URL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getUrl()
    {
        return this.url;
    }

    private void setUrl(String url)
    {
        this.url = url;
    }

    public final static String ASSOCIATION_TABLE = "ovf_package_list_has_ovf_package";

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OVFPackage.class, cascade = CascadeType.DETACH)
    @JoinTable(name = ASSOCIATION_TABLE, joinColumns = @JoinColumn(name = "id_ovf_package_list"), inverseJoinColumns = @JoinColumn(name = "id_ovf_package"))
    private List<OVFPackage> ovfPackages;

    public List<OVFPackage> getOvfPackages()
    {
        return ovfPackages;
    }

    public void setOvfPackages(List<OVFPackage> ovfPackages)
    {
        this.ovfPackages = ovfPackages;
    }

}
