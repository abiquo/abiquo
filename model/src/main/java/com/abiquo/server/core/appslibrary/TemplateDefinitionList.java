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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = TemplateDefinitionList.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = TemplateDefinitionList.TABLE_NAME)
public class TemplateDefinitionList extends DefaultEntityBase
{
    public static final String TABLE_NAME = "ovf_package_list";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    public TemplateDefinitionList()
    {
        // Just for JPA support
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

    private final static boolean NAME_REQUIRED = true;

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

    public static final String TEMPLATE_DEFINITION_TABLE = "ovf_package_list_has_ovf_package";

    public static final String TEMPLATE_DEFINITION_PROPERTY = "templateDefinitions";

    static final String TEMPLATE_DEFINITION_ID_COLUMN = "id_ovf_package";

    static final String TEMPLATE_DEFINITIONS_LIST_ID_COLUMN = "id_ovf_package_list";

    @ManyToMany(mappedBy = TemplateDefinition.TEMPLATE_DEFINITION_LIST_PROPERTY, fetch = FetchType.LAZY)
    private List<TemplateDefinition> templateDefinitions = new ArrayList<TemplateDefinition>();

    public void setTemplateDefinitions(final List<TemplateDefinition> templateDefinitions)
    {
        this.templateDefinitions = templateDefinitions;
    }

    public List<TemplateDefinition> getTemplateDefinitions()
    {
        if (templateDefinitions == null)
        {
            templateDefinitions = new ArrayList<TemplateDefinition>();
        }

        return templateDefinitions;
    }

    public void addTemplateDefinition(final TemplateDefinition templateDef)
    {
        if (templateDefinitions == null)
        {
            templateDefinitions = new ArrayList<TemplateDefinition>();
            templateDefinitions.add(templateDef);
            templateDef.addToTemplateDefinitionLists(this);
        }
        if (!templateDefinitions.contains(templateDef))
        {
            templateDefinitions.add(templateDef);
            templateDef.addToTemplateDefinitionLists(this);
        }
    }

    public void removeTemplateDefinition(final TemplateDefinition templateDef)
    {
        assert templateDef != null;
        assert this.templateDefinitions.contains(templateDef);

        this.templateDefinitions.remove(templateDef);
    }

    public TemplateDefinitionList(final String name, final String url)
    {
        setName(name);
        setUrl(url);
    }

}
