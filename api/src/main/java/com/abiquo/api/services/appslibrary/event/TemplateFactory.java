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

package com.abiquo.api.services.appslibrary.event;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.TemplateDefinition;
import com.abiquo.server.core.appslibrary.TemplateDefinitionDAO;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.tracer.User;

/**
 * Transforms an {@link TemplateDto} from ApplianceManager into a {@link VirtualMachineTemplate} in
 * API
 */
@Service
public class TemplateFactory
{
    protected final static Logger logger = LoggerFactory.getLogger(TemplateFactory.class);

    @Autowired
    private AppsLibraryRep appslibraryRep;

    @Autowired
    private TemplateDefinitionDAO templateDefDao;

    @Autowired
    private EnterpriseRep entRepo;

    @Autowired
    protected DatacenterService dcService;

    @Autowired
    protected TracerLogger tracer;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<VirtualMachineTemplate> insertVirtualMachineTemplates(
        final List<TemplateDto> disks, final Repository repo)
    {
        List<VirtualMachineTemplate> addedvmtemplates = new LinkedList<VirtualMachineTemplate>();
        List<TemplateDto> disksToInsert =
            filterAlreadyInsertedVirtualMachineTemplatePathsOrEnterpriseDoNotExist(disks, repo);

        // first masters
        for (TemplateDto disk : disksToInsert)
        {
            if (disk.getMasterDiskFilePath() == null)
            {
                try
                {
                    VirtualMachineTemplate vi = virtualMachineTemplateFromTemplate(disk, repo);
                    appslibraryRep.insertVirtualMachineTemplate(vi);

                    addedvmtemplates.add(vi);
                    logger.info("Inserted virtual machine template [{}]", vi.getPath());
                }
                catch (Exception pe)
                {
                    logger.error("Can not insert virtual machine template [{}]",
                        disk.getDiskFilePath());
                }
            }
        }

        // second bunded
        for (TemplateDto disk : disksToInsert)
        {
            if (disk.getMasterDiskFilePath() != null)
            {
                try
                {
                    VirtualMachineTemplate vi = virtualMachineTemplateFromTemplate(disk, repo);
                    appslibraryRep.insertVirtualMachineTemplate(vi);
                    addedvmtemplates.add(vi);
                    logger.info("Inserted bundle virtual machine template [{}]", vi.getPath());
                }
                catch (Exception pe)
                {
                    logger.error("Can not insert bundle virtual machine template [{}]",
                        disk.getDiskFilePath());
                }
            }
        }
        return addedvmtemplates;
    }

    /**
     * Filer already present virtual machne template paths. Ignore virtual vmtemplates from not
     * present enterprise repository.
     */
    private List<TemplateDto> filterAlreadyInsertedVirtualMachineTemplatePathsOrEnterpriseDoNotExist(
        final List<TemplateDto> disks, final Repository repository)
    {

        List<TemplateDto> notInsertedDisks = new LinkedList<TemplateDto>();

        for (TemplateDto disk : disks)
        {
            Enterprise enterprise = entRepo.findById(disk.getEnterpriseRepositoryId());

            if (enterprise != null)
            {
                if (!appslibraryRep.existVirtualMachineTemplateWithSamePath(enterprise, repository,
                    disk.getDiskFilePath()))
                {
                    notInsertedDisks.add(disk);
                }
            }
        }

        return notInsertedDisks;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void generateConversions(final List<VirtualMachineTemplate> templates,
        final Datacenter datacenter)
    {
        // community do nothing
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected VirtualMachineTemplate virtualMachineTemplateFromTemplate(final TemplateDto disk,
        final Repository repository)
    {
        Enterprise enterprise = entRepo.findById(disk.getEnterpriseRepositoryId());

        DiskFormatType diskFormat;
        VirtualMachineTemplate master = null;

        if (disk.getMasterDiskFilePath() != null)
        {
            master =
                appslibraryRep.findVirtualMachineTemplateByPath(enterprise, repository,
                    disk.getDiskFilePath());

            diskFormat = master.getDiskFormatType();
        }
        else
        {
            diskFormat = DiskFormatType.valueOf(disk.getDiskFileFormat().name().toUpperCase());
        }

        Category category = getCategory(disk);

        VirtualMachineTemplate vmtemplate =
            new VirtualMachineTemplate(enterprise,
                disk.getName(),
                diskFormat,
                disk.getDiskFilePath(),
                disk.getDiskFileSize(),
                category,
                User.SYSTEM_USER.getName()); // TODO

        vmtemplate.setIconUrl(getIcon(disk));
        vmtemplate.setDescription(getDescription(disk));
        vmtemplate.setCpuRequired(disk.getCpu());
        vmtemplate.setRamRequired(getRamInMb(disk).intValue());
        vmtemplate.setHdRequiredInBytes(getHdInBytes(disk));
        vmtemplate.setOvfid(disk.getUrl());
        vmtemplate.setRepository(repository);

        if (disk.getEthernetDriverType() != null)
        {
            vmtemplate.setEthernetDriverType(disk.getEthernetDriverType());
        }

        if (master != null)
        {
            vmtemplate.setMaster(master);
        }

        return vmtemplate;
    }

    /**
     * If the icon is not found in the OVF document then look in the {@link TemplateDefinition}
     * table (from the ovfindex.xml)
     */
    private String getIcon(final TemplateDto template)
    {
        if (!StringUtils.isEmpty(template.getIconPath()))
        {
            return template.getIconPath();
        }

        TemplateDefinition tdef = templateDefDao.findByUrl(template.getUrl());
        if (tdef != null)
        {
            logger.warn("Missing icon url in the OVF document, reading from ovfindex");
            return tdef.getIconUrl();
        }
        return null;
    }

    private Category getCategory(final TemplateDto disk)
    {

        String categoryName = disk.getCategoryName();
        if (!StringUtils.isEmpty(categoryName))
        {
            return appslibraryRep.findByCategoryNameOrCreateNew(disk.getCategoryName());
        }

        // try to find in the TemplateDefinition
        TemplateDefinition templateDef = templateDefDao.findByUrl(disk.getUrl());
        return templateDef != null ? templateDef.getCategory() : appslibraryRep
            .getDefaultCategory();
    }

    /*
     * returns a 254 trucated description. TODO in the DAO
     */
    private String getDescription(final TemplateDto disk)
    {
        String truncatedDescription = disk.getDescription();
        if (truncatedDescription.length() > 254) // TODO data truncation
        {
            truncatedDescription = truncatedDescription.substring(0, 254);
        }
        return truncatedDescription;
    }

    private Long getRamInMb(final TemplateDto disk)
    {
        BigInteger byteRam = getBytes(String.valueOf(disk.getRam()), disk.getRamSizeUnit().name());
        return byteRam.longValue() / 1048576;
    }

    private Long getHdInBytes(final TemplateDto disk)
    {
        return getBytes(String.valueOf(disk.getHd()), disk.getHdSizeUnit().name()).longValue();
    }

    /**
     * Gets the disk capacity on bytes.
     * 
     * @param capacity, numeric value
     * @param alloctionUnit, bytes by default but can be Kb, Mb, Gb or Tb.
     * @return capacity on bytes
     **/
    private static BigInteger getBytes(final String capacity, final String allocationUnits)
    {
        BigInteger capa = new BigInteger(capacity);

        if (allocationUnits == null)
        {
            return capa;
        }
        if ("byte".equalsIgnoreCase(allocationUnits) || "bytes".equalsIgnoreCase(allocationUnits))
        {
            return capa;
        }

        BigInteger factor = new BigInteger("2");
        if ("byte * 2^10".equals(allocationUnits) || "KB".equalsIgnoreCase(allocationUnits)
            || "KILOBYTE".equalsIgnoreCase(allocationUnits)
            || "KILOBYTES".equalsIgnoreCase(allocationUnits)) // kb
        {
            factor = factor.pow(10);
        }
        else if ("byte * 2^20".equals(allocationUnits) || "MB".equalsIgnoreCase(allocationUnits)
            || "MEGABYTE".equalsIgnoreCase(allocationUnits)
            || "MEGABYTES".equalsIgnoreCase(allocationUnits)) // mb
        {
            factor = factor.pow(20);
        }
        else if ("byte * 2^30".equals(allocationUnits) || "GB".equalsIgnoreCase(allocationUnits)
            || "GIGABYTE".equalsIgnoreCase(allocationUnits)
            || "GIGABYTES".equalsIgnoreCase(allocationUnits)) // gb
        {
            factor = factor.pow(30);
        }
        else if ("byte * 2^40".equals(allocationUnits) || "TB".equalsIgnoreCase(allocationUnits)
            || "TERABYTE".equalsIgnoreCase(allocationUnits)
            || "TERABYTES".equalsIgnoreCase(allocationUnits)) // tb
        {
            factor = factor.pow(40);
        }
        else
        {
            final String msg =
                "Unknow disk capacityAllocationUnits factor [" + allocationUnits + "]";
            throw new RuntimeException(msg);
        }

        return capa.multiply(factor);
    }

}
