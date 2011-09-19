package com.abiquo.server.core.appslibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public OVFPackageList()
    {
        // Just for JPA support
    }

    private final static String ID_COLUMN = "id_ovf_package_list";

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

    public void setName(final String name)
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

    public void setUrl(final String url)
    {
        this.url = url;
    }

    public static final String OVF_PACKAGE_TABLE = "ovf_package_list_has_ovf_package";

    public static final String OVF_PACKAGE_PROPERTY = "ovf_packages";

    static final String OVF_PACKAGE_ID_COLUMN = "id_ovf_package";

    static final String OVF_PACKAGES_LIST_ID_COLUMN = "id_ovf_package_list";

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = OVF_PACKAGE_TABLE, joinColumns = {@JoinColumn(name = OVF_PACKAGE_ID_COLUMN)}, inverseJoinColumns = {@JoinColumn(name = OVF_PACKAGES_LIST_ID_COLUMN)})
    private List<OVFPackage> ovfPackages = new ArrayList<OVFPackage>();

    public void setOvfPackages(final List<OVFPackage> ovfPackages)
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

    void addToOvfPackages(final OVFPackage ovfpackage)
    {
        assert ovfpackage != null;
        assert !this.ovfPackages.contains(ovfpackage);

        this.ovfPackages.add(ovfpackage);
    }

    void removeFromOvfPackages(final OVFPackage ovfpackage)
    {
        assert ovfpackage != null;
        assert this.ovfPackages.contains(ovfpackage);

        this.ovfPackages.remove(ovfpackage);
    }

    public OVFPackageList(final String name, final String url)
    {
        setName(name);
        setUrl(url);
    }

}
