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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageDAO;
import com.abiquo.server.core.config.CategoryDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Repository;

/**
 * TODO icon and category !!!
 */
/**
 * Transforms an {@link OVFPackageInstanceDto} from ApplianceManager into a {@link VirtualImage} in
 * API
 */
@Service
public class OVFPackageInstanceToVirtualImage
{
    private final static Logger logger = LoggerFactory
        .getLogger(OVFPackageInstanceToVirtualImage.class);

    @Autowired
    private VirtualImageDAO vimageDao;

    @Autowired
    private EnterpriseRep entRepo;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<VirtualImage> insertVirtualImages(final List<OVFPackageInstanceDto> disks,
        final Repository repo)
    {
        List<VirtualImage> addedvimages = new LinkedList<VirtualImage>();
        List<OVFPackageInstanceDto> disksToInsert =
            filterAlreadyInsertedVirtualImagePathsOrEnterpriseDoNotExist(disks, repo);

        // first masters
        for (OVFPackageInstanceDto disk : disksToInsert)
        {
            if (disk.getMasterDiskFilePath() == null)
            {
                try
                {
                    VirtualImage vi = imageFromDisk(disk, repo);
                    vimageDao.persist(vi);

                    addedvimages.add(vi);
                    logger.info("Inserted virtual image [{}]", vi.getPathName());
                }
                catch (Exception pe)
                {
                    logger.error("Can not insert virtual image [{}]", disk.getDiskFilePath());
                }
            }
        }

        // second bunded
        for (OVFPackageInstanceDto disk : disksToInsert)
        {
            if (disk.getMasterDiskFilePath() == null)
            {
                try
                {
                    VirtualImage vi = imageFromDisk(disk, repo);
                    vimageDao.persist(vi);
                    addedvimages.add(vi);
                    logger.info("Inserted bundle virtual image [{}]", vi.getPathName());
                }
                catch (Exception pe)
                {
                    logger
                        .error("Can not insert bundle virtual image [{}]", disk.getDiskFilePath());
                }
            }
        }
        return addedvimages;
    }

    /**
     * Filer already present virtual image paths. Ignore virtual images from not present enterprise
     * repository.
     */
    private List<OVFPackageInstanceDto> filterAlreadyInsertedVirtualImagePathsOrEnterpriseDoNotExist(
        final List<OVFPackageInstanceDto> disks, final Repository repository)
    {

        List<OVFPackageInstanceDto> notInsertedDisks = new LinkedList<OVFPackageInstanceDto>();

        for (OVFPackageInstanceDto disk : disks)
        {
            Enterprise enterprise = entRepo.findById(disk.getIdEnterprise());

            if (enterprise != null)
            {
                if (!vimageDao.existWithSamePath(enterprise, repository, disk.getDiskFilePath()))
                {
                    notInsertedDisks.add(disk);
                }
            }
        }

        return notInsertedDisks;
    }

    @Autowired
    private CategoryDAO categoryDao;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    protected VirtualImage imageFromDisk(final OVFPackageInstanceDto disk,
        final Repository repository)
    {
        Enterprise enterprise = entRepo.findById(disk.getIdEnterprise());

        DiskFormatType diskFormat;
        VirtualImage master = null;

        if (disk.getMasterDiskFilePath() != null)
        {
            master =
                vimageDao.findVirtualImageByPath(enterprise, repository, disk.getDiskFilePath());

            diskFormat = master.getDiskFormatType();
        }
        else
        {
            diskFormat = DiskFormatType.valueOf(disk.getDiskFileFormat().name().toUpperCase());
        }

        VirtualImage vimage = new VirtualImage(enterprise, diskFormat);
        vimage.setOvfid(disk.getOvfUrl());
        vimage.setPathName(disk.getDiskFilePath());
        vimage.setRepository(repository);
        if (master != null)
        {
            vimage.setMaster(master);
        }

        vimage.setCategory(categoryDao.findDefault()); // TODO find by ProductSection

        /**
         * TODO category and icon
         */
        vimage.setName(disk.getName());
        vimage.setDescription(getDescription(disk));

        vimage.setCpuRequired(disk.getCpu());
        vimage.setRamRequired(getRamInMb(disk).intValue());
        vimage.setHdRequiredInBytes(getHdInBytes(disk));
        vimage.setDiskFileSize(disk.getDiskFileSize());

        return vimage;
    }

    /*
     * returns a 254 trucated description
     */
    private String getDescription(final OVFPackageInstanceDto disk)
    {
        String truncatedDescription = disk.getDescription();
        if (truncatedDescription.length() > 254) // TODO data truncation
        {
            truncatedDescription = truncatedDescription.substring(0, 254);
        }
        return truncatedDescription;
    }

    private Long getRamInMb(final OVFPackageInstanceDto disk)
    {
        BigInteger byteRam = getBytes(String.valueOf(disk.getRam()), disk.getRamSizeUnit().name());
        return byteRam.longValue() / 1048576;
    }

    private Long getHdInBytes(final OVFPackageInstanceDto disk)
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
