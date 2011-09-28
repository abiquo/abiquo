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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
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
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = OVFPackage.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = OVFPackage.TABLE_NAME)
public class OVFPackage extends DefaultEntityBase
{
    public static final String TABLE_NAME = "ovf_package";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public OVFPackage()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "id_ovf_package";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public final static String APPS_LIBRARY_PROPERTY = "appsLibrary";

    private final static boolean APPS_LIBRARY_REQUIRED = true;

    private final static String APPS_LIBRARY_ID_COLUMN = "id_apps_library";

    @JoinColumn(name = APPS_LIBRARY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_appsLibrary")
    private AppsLibrary appsLibrary;

    @Required(value = APPS_LIBRARY_REQUIRED)
    public AppsLibrary getAppsLibrary()
    {
        return this.appsLibrary;
    }

    public void setAppsLibrary(final AppsLibrary appsLibrary)
    {
        this.appsLibrary = appsLibrary;
    }

    public final static String PRODUCT_VERSION_PROPERTY = "productVersion";

    private final static boolean PRODUCT_VERSION_REQUIRED = false;

    private final static int PRODUCT_VERSION_LENGTH_MIN = 0;

    private final static int PRODUCT_VERSION_LENGTH_MAX = 45;

    private final static boolean PRODUCT_VERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PRODUCT_VERSION_COLUMN = "productVersion";

    @Column(name = PRODUCT_VERSION_COLUMN, nullable = !PRODUCT_VERSION_REQUIRED, length = PRODUCT_VERSION_LENGTH_MAX)
    private String productVersion;

    @Required(value = PRODUCT_VERSION_REQUIRED)
    @Length(min = PRODUCT_VERSION_LENGTH_MIN, max = PRODUCT_VERSION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PRODUCT_VERSION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getProductVersion()
    {
        return this.productVersion;
    }

    public void setProductVersion(final String productVersion)
    {
        this.productVersion = productVersion;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 45;

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

    public final static String CATEGORY_PROPERTY = "category";

    private final static boolean CATEGORY_REQUIRED = true;

    private final static String CATEGORY_ID_COLUMN = "idCategory";

    @JoinColumn(name = CATEGORY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_category")
    private Category category;

    @Required(value = CATEGORY_REQUIRED)
    public Category getCategory()
    {
        return this.category;
    }

    public void setCategory(final Category category)
    {
        this.category = category;
    }

    public final static String PRODUCT_VENDOR_PROPERTY = "productVendor";

    private final static boolean PRODUCT_VENDOR_REQUIRED = false;

    private final static int PRODUCT_VENDOR_LENGTH_MIN = 0;

    private final static int PRODUCT_VENDOR_LENGTH_MAX = 45;

    private final static boolean PRODUCT_VENDOR_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PRODUCT_VENDOR_COLUMN = "productVendor";

    @Column(name = PRODUCT_VENDOR_COLUMN, nullable = !PRODUCT_VENDOR_REQUIRED, length = PRODUCT_VENDOR_LENGTH_MAX)
    private String productVendor;

    @Required(value = PRODUCT_VENDOR_REQUIRED)
    @Length(min = PRODUCT_VENDOR_LENGTH_MIN, max = PRODUCT_VENDOR_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PRODUCT_VENDOR_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getProductVendor()
    {
        return this.productVendor;
    }

    public void setProductVendor(final String productVendor)
    {
        this.productVendor = productVendor;
    }

    public final static String PRODUCT_URL_PROPERTY = "productUrl";

    private final static boolean PRODUCT_URL_REQUIRED = false;

    private final static int PRODUCT_URL_LENGTH_MIN = 0;

    private final static int PRODUCT_URL_LENGTH_MAX = 45;

    private final static boolean PRODUCT_URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PRODUCT_URL_COLUMN = "productUrl";

    @Column(name = PRODUCT_URL_COLUMN, nullable = !PRODUCT_URL_REQUIRED, length = PRODUCT_URL_LENGTH_MAX)
    private String productUrl;

    @Required(value = PRODUCT_URL_REQUIRED)
    @Length(min = PRODUCT_URL_LENGTH_MIN, max = PRODUCT_URL_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PRODUCT_URL_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getProductUrl()
    {
        return this.productUrl;
    }

    public void setProductUrl(final String productUrl)
    {
        this.productUrl = productUrl;
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

    public void setUrl(final String url)
    {
        this.url = url;
    }

    public final static String ICON_PROPERTY = "icon";

    private final static boolean ICON_REQUIRED = true;

    private final static String ICON_ID_COLUMN = "idIcon";

    @JoinColumn(name = ICON_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_icon")
    private Icon icon;

    @Required(value = ICON_REQUIRED)
    public Icon getIcon()
    {
        return this.icon;
    }

    public void setIcon(final Icon icon)
    {
        this.icon = icon;
    }

    public final static String TYPE_PROPERTY = "type";

    private final static boolean TYPE_REQUIRED = true;

    private final static String TYPE_COLUMN = "type";

    @Enumerated(value = javax.persistence.EnumType.STRING)
    @Column(name = TYPE_COLUMN, nullable = !TYPE_REQUIRED)
    private DiskFormatType type;

    @Required(value = TYPE_REQUIRED)
    public DiskFormatType getType()
    {
        return this.type;
    }

    public void setType(final DiskFormatType type)
    {
        this.type = type;
    }

    public final static String PRODUCT_NAME_PROPERTY = "productName";

    private final static boolean PRODUCT_NAME_REQUIRED = false;

    private final static int PRODUCT_NAME_LENGTH_MIN = 0;

    private final static int PRODUCT_NAME_LENGTH_MAX = 255;

    private final static boolean PRODUCT_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PRODUCT_NAME_COLUMN = "productName";

    @Column(name = PRODUCT_NAME_COLUMN, nullable = !PRODUCT_NAME_REQUIRED, length = PRODUCT_NAME_LENGTH_MAX)
    private String productName;

    @Required(value = PRODUCT_NAME_REQUIRED)
    @Length(min = PRODUCT_NAME_LENGTH_MIN, max = PRODUCT_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PRODUCT_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getProductName()
    {
        return this.productName;
    }

    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    public final static String DISK_SIZE_MB_PROPERTY = "diskSizeMb";

    private final static boolean DISK_SIZE_MB_REQUIRED = false;

    private final static String DISK_SIZE_MB_COLUMN = "diskSizeMb";

    private final static long DISK_SIZE_MB_MIN = Long.MIN_VALUE;

    private final static long DISK_SIZE_MB_MAX = Long.MAX_VALUE;

    @Column(name = DISK_SIZE_MB_COLUMN, nullable = !DISK_SIZE_MB_REQUIRED)
    @Range(min = DISK_SIZE_MB_MIN, max = DISK_SIZE_MB_MAX)
    private long diskSizeMb;

    public long getDiskSizeMb()
    {
        return this.diskSizeMb;
    }

    public void setDiskSizeMb(final long diskSizeMb)
    {
        this.diskSizeMb = diskSizeMb;
    }

    public final static String DESCRIPTION_PROPERTY = "description";

    private final static boolean DESCRIPTION_REQUIRED = false;

    private final static int DESCRIPTION_LENGTH_MIN = 0;

    private final static int DESCRIPTION_LENGTH_MAX = 255;

    private final static boolean DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DESCRIPTION_COLUMN = "description";

    @Column(name = DESCRIPTION_COLUMN, nullable = !DESCRIPTION_REQUIRED, length = DESCRIPTION_LENGTH_MAX)
    private String description;

    @Required(value = DESCRIPTION_REQUIRED)
    @Length(min = DESCRIPTION_LENGTH_MIN, max = DESCRIPTION_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DESCRIPTION_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    /**
     * OVF Package List Association
     */
    public static final String OVF_PACKAGE_LIST_TABLE = "ovf_package_list_has_ovf_package";

    public static final String OVF_PACKAGE_LIST_PROPERTY = "ovfPackageLists";

    static final String OVF_PACKAGE_LIST_ID_COLUMN = "id_ovf_package_list";

    static final String OVF_PACKAGES_ID_COLUMN = "id_ovf_package";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = OVF_PACKAGE_LIST_TABLE, joinColumns = {@JoinColumn(name = OVF_PACKAGES_ID_COLUMN)}, inverseJoinColumns = {@JoinColumn(name = OVF_PACKAGE_LIST_ID_COLUMN)})
    private List<OVFPackageList> ovfPackageLists = new ArrayList<OVFPackageList>();

    public List<OVFPackageList> getOvfPackageLists()
    {
        if (ovfPackageLists == null)
        {
            ovfPackageLists = new ArrayList<OVFPackageList>();
        }
        return ovfPackageLists;
    }

    public void setOvfPackageLists(final List<OVFPackageList> ovfPackageLists)
    {
        this.ovfPackageLists = ovfPackageLists;
    }

    public void addToOvfPackageLists(final OVFPackageList list)
    {
        if (ovfPackageLists == null)
        {
            ovfPackageLists = new ArrayList<OVFPackageList>();
        }
        if (!ovfPackageLists.contains(list))
        {
            ovfPackageLists.add(list);
            list.addToOvfPackages(this);
        }
    }

    public OVFPackage(final String name, final String productName, final String productUrl,
        final String productVendor, final String productVersion, final DiskFormatType type,
        final String url, final long diskSizeMb)
    {
        setName(name);
        setProductName(productName);
        setProductUrl(productUrl);
        setProductVendor(productVendor);
        setProductVersion(productVersion);
        setDiskSizeMb(diskSizeMb);
        setUrl(url);
        setType(type);
    }
}
