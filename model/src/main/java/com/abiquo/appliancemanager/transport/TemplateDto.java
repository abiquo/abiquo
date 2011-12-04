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

package com.abiquo.appliancemanager.transport;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

/**
 * The materialization of an {@link TemplateDefinition} of a given {@link Datacenter} and
 * {@link Enterprise} .
 */
@XmlType
@XmlRootElement(name = "template")
public class TemplateDto extends TemplateDefinitionDto
{
    private static final long serialVersionUID = 6994372893155355385L;

    /** Virtual disk file (.vmdk) path relative to the repository */
    private String diskFilePath;

    /** Optional. Only for vimages instances (bundles) */
    private String masterDiskFilePath;

    /** ######### hardware requirements : TODO move to {@link TemplateDefinitionDto} ######### */

    @Deprecated
    /** Use {@link TemplateDefinitionDto} diskFormatTypeUri: TODO use the DiskFormatEnum in the TemplateDefinitionDto*/
    private DiskFormatType diskFileFormat;

    @Deprecated
    // Use links
    private String iconPath;

    @Deprecated
    // Use links
    private String categoryName;

    private Integer cpu;

    private Long ram;

    private Long hd;

    private MemorySizeUnit ramSizeUnit;

    private MemorySizeUnit hdSizeUnit;

    @Deprecated
    // Use links
    private Integer enterpriseRepositoryId;

    public String getDiskFilePath()
    {
        return diskFilePath;
    }

    public void setDiskFilePath(final String diskFilePath)
    {
        this.diskFilePath = diskFilePath;
    }

    /** Optional. Only for vimages instances (bundles) */
    public String getMasterDiskFilePath()
    {
        return masterDiskFilePath;
    }

    public void setMasterDiskFilePath(final String masterDiskFilePath)
    {
        this.masterDiskFilePath = masterDiskFilePath;
    }

    public DiskFormatType getDiskFileFormat()
    {
        return diskFileFormat;
    }

    public void setDiskFileFormat(final DiskFormatType diskFileFormat)
    {
        this.diskFileFormat = diskFileFormat;
        this.setDiskFormatTypeUri(diskFileFormat.uri);
        // FIXME once TemplateDefinitionDto work with Enum
    }

    public String getIconPath()
    {
        return iconPath;
    }

    public void setIconPath(final String iconPath)
    {
        this.iconPath = iconPath;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(final String categoryName)
    {
        this.categoryName = StringUtils.strip(categoryName);
    }

    /** TODO get from the EnterpriseRepository link */
    public Integer getEnterpriseRepositoryId()
    {
        return enterpriseRepositoryId;
    }

    /** TODO set to the EnterpriseRepository link */
    public void setEnterpriseRepositoryId(final Integer enterpriseRepositoryId)
    {
        this.enterpriseRepositoryId = enterpriseRepositoryId;
    }

    public Integer getCpu()
    {
        return cpu;
    }

    public void setCpu(final Integer cpu)
    {
        this.cpu = cpu;
    }

    public Long getRam()
    {
        return ram;
    }

    public void setRam(final Long ram)
    {
        this.ram = ram;
    }

    public Long getHd()
    {
        return hd;
    }

    public void setHd(final Long hd)
    {
        this.hd = hd;
    }

    /** default MB */
    public MemorySizeUnit getRamSizeUnit()
    {
        return ramSizeUnit != null ? ramSizeUnit : MemorySizeUnit.MEGABYTE;
    }

    public void setRamSizeUnit(final MemorySizeUnit ramSizeUnit)
    {
        this.ramSizeUnit = ramSizeUnit;
    }

    /** default MB */
    public MemorySizeUnit getHdSizeUnit()
    {
        return hdSizeUnit != null ? hdSizeUnit : MemorySizeUnit.MEGABYTE;
    }

    public void setHdSizeUnit(final MemorySizeUnit hdSizeUnit)
    {
        this.hdSizeUnit = hdSizeUnit;
    }

}
