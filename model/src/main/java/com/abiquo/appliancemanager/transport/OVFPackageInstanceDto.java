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
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

/**
 * The materialization of an {@link OVFPackage} of a given {@link Datacenter} and {@link Enterprise}
 * .
 */
@XmlRootElement(name = "ovfInstance")
@XmlType
public class OVFPackageInstanceDto extends OVFPackageDto
{
    private static final long serialVersionUID = 6994372893155355385L;

    /**
     * Original location of the {@link OVFPackage}. Identify the entity combined with the
     * {@link Enterprise} identifier (id of the ApplianceManager EnterpriseRepository). Datacenter
     * identifier is implicit in the ApplianceManager context.
     */
    private String ovfId;

    /** Virtual disk file (.vmdk) path relative to the repository */
    private String diskFilePath;

    /** Optional. Only for vimages instances (bundles) */
    private String masterDiskFilePath;

    /** ############################## */
    /**
     * Descriptive attributes. Deprecated attributes (should use the {@link OVFPackageDto} links :
     * TODO remove them
     */
    /**
     * ##############################
     */

    private DiskFormatType diskFileFormat;

    private Integer cpu;

    private Long ram;

    private Long hd;

    private MemorySizeUnit ramSizeUnit;

    private MemorySizeUnit hdSizeUnit;

    private Integer idEnterprise;

    private Integer idUser;

    private String iconPath;

    private String categoryName;

    public String getOvfId()
    {
        return ovfId;
    }

    public void setOvfId(final String ovfId)
    {
        this.ovfId = ovfId;
    }

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

    /** ############################## */
    /**
     * Deprecated attributes (should use the {@link OVFPackageDto} links : TODO remove them
     */
    /**
     * ##############################
     */

    @Deprecated
    /** Use {@link OVFPackageDto} diskFormatTypeUri: TODO use the DiskFormatEnum in the OVFPackageDto*/
    public DiskFormatType getDiskFileFormat()
    {
        return diskFileFormat;
    }

    public void setDiskFileFormat(final DiskFormatType diskFileFormat)
    {
        this.diskFileFormat = diskFileFormat;
        this.setDiskFormatTypeUri(diskFileFormat.uri);
        // TODO FIXME once OVFPackageDto work with Enum
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
    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    /** TODO set to the EnterpriseRepository link */
    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    @Deprecated
    // TODO not being used
    public Integer getIdUser()
    {
        return idUser;
    }

    @Deprecated
    // TODO not being used
    public void setIdUser(final Integer idUser)
    {
        this.idUser = idUser;
    }

    /** ############################## */
    /**
     * {@link VirtualImage} hardware requirements : TODO move them to OVFPackageDto
     */
    /** ############################## */

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
