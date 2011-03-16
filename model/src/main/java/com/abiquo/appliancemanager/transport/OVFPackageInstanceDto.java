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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;

//    
// OVFUrl - URL Descarga
// Name -> OVFPackage.ProductName
// SO - Produ
// Path -> Physical Disk File Path (.vmdk)
// ImageType -> DiskFormatType Name
// ImageSize -> Disk File Size
// IconPath -> ...
// CategoryName ->...
// Description -> OVFPackage.ProductInfo
// CPU
// RAM
// HD
// RAMunits -> Enum
// HDunits -> Enum
// EnterpriseId -> REST url path
// UserId -> not used
// MasterPath -> (only for bundles) -> imageBundledMaster

/**
 * Identified by idEnterprise + ovfURL
 */
@XmlRootElement
@XmlType(name = "OVFPackageInstance")
public class OVFPackageInstanceDto extends SingleResourceTransportDto implements Serializable
{

    /**
     * OVFEnvelope's URL. Identifies a OVFPackageInstanceDto uniquely for an enterprise
     */
    private String ovfUrl;

    /**
     * 
     */
    private String name;

    private String description;

    private String diskFilePath;

    private OVFPackageDiskFormat diskFileFormat;

    private Long diskFileSize;

    private Integer cpu;

    private Long ram;

    private Long hd;

    private MemorySizeUnit ramSizeUnit;

    private MemorySizeUnit hdSizeUnit;

    private Integer idEnterprise;

    private Integer idUser;

    /**
     * Only for bundles
     */
    private String masterDiskFilePath;

    private String iconPath;

    private String categoryName;

    public String getOvfUrl()
    {
        return ovfUrl;
    }

    public void setOvfUrl(String ovfUrl)
    {
        this.ovfUrl = ovfUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
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

    public String getDiskFilePath()
    {
        return diskFilePath;
    }

    public void setDiskFilePath(String diskFilePath)
    {
        this.diskFilePath = diskFilePath;
    }

    public OVFPackageDiskFormat getDiskFileFormat()
    {
        return diskFileFormat;
    }

    public void setDiskFileFormat(OVFPackageDiskFormat diskFileFormat)
    {
        this.diskFileFormat = diskFileFormat;
    }

    public Long getDiskFileSize()
    {
        return diskFileSize;
    }

    public void setDiskFileSize(Long diskFileSize)
    {
        this.diskFileSize = diskFileSize;
    }

    public Integer getCpu()
    {
        return cpu;
    }

    public void setCpu(Integer cpu)
    {
        this.cpu = cpu;
    }

    public Long getRam()
    {
        return ram;
    }

    public void setRam(Long ram)
    {
        this.ram = ram;
    }

    public Long getHd()
    {
        return hd;
    }

    public void setHd(Long hd)
    {
        this.hd = hd;
    }

    public MemorySizeUnit getRamSizeUnit()
    {
        return ramSizeUnit != null ? ramSizeUnit : MemorySizeUnit.MEGABYTE;
    }

    public void setRamSizeUnit(MemorySizeUnit ramSizeUnit)
    {
        this.ramSizeUnit = ramSizeUnit;
    }

    public MemorySizeUnit getHdSizeUnit()
    {
        return hdSizeUnit != null ? hdSizeUnit : MemorySizeUnit.MEGABYTE;
    }

    public void setHdSizeUnit(MemorySizeUnit hdSizeUnit)
    {
        this.hdSizeUnit = hdSizeUnit;
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    public Integer getIdUser()
    {
        return idUser;
    }

    public void setIdUser(Integer idUser)
    {
        this.idUser = idUser;
    }

    public String getMasterDiskFilePath()
    {
        return masterDiskFilePath;
    }

    public void setMasterDiskFilePath(String masterDiskFilePath)
    {
        this.masterDiskFilePath = masterDiskFilePath;
    }

    public String getIconPath()
    {
        return iconPath;
    }

    public void setIconPath(String iconPath)
    {
        this.iconPath = iconPath;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

}
