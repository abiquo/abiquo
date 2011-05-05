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

package com.abiquo.abiserver.appslibrary;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;

public class OVFPackageInstanceToVirtualImage
{
    private final static Logger logger = LoggerFactory
        .getLogger(OVFPackageInstanceToVirtualImage.class);

    public static List<VirtualimageHB> insertVirtualDiskOnDatabase(
        List<OVFPackageInstanceDto> disks, RepositoryHB repo) throws VirtualImageException
    {

        DAOFactory daoF = HibernateDAOFactory.instance();

        List<OVFPackageInstanceDto> disksToInsert =
            filterAlreadyInsertedVirtualImagePathsOrEnterpriseDoNotExist(disks,
                repo.getIdRepository());
        List<VirtualimageHB> images = new LinkedList<VirtualimageHB>();

        // first masters
        for (OVFPackageInstanceDto disk : disksToInsert)
        {
            if (disk.getMasterDiskFilePath() == null)
            {

                try
                {
                    daoF.beginConnection();

                    VirtualimageHB vi = imageFromDisk(disk, repo.getIdRepository());

                    vi.setRepository(repo); // on this enterprise repository of current datacenter

                    daoF.getVirtualImageDAO().makePersistent(vi);

                    logger.info("Inserted virtual image [{}]", vi.getPathName());

                    daoF.endConnection();

                    images.add(vi);
                }
                catch (PersistenceException pe)
                {
                    logger.error("Can not insert virtual image [{}]", disk.getDiskFilePath());
                    daoF.rollbackConnection();
                }
            }
        }

        // second bunded
        for (OVFPackageInstanceDto disk : disksToInsert)
        {
            if (disk.getMasterDiskFilePath() != null)
            {

                try
                {
                    daoF.beginConnection();

                    VirtualimageHB vi = imageFromDisk(disk, repo.getIdRepository());

                    vi.setRepository(repo); // on this enterprise repository of current datacenter

                    daoF.getVirtualImageDAO().makePersistent(vi);

                    logger.info("Inserted BUNDLE virtual image [{}]", vi.getPathName());

                    daoF.endConnection();

                    images.add(vi);
                }
                catch (PersistenceException pe)
                {
                    logger
                        .error("Can not insert BUNDLE virtual image [{}]", disk.getDiskFilePath());
                    daoF.rollbackConnection();
                }
            }
        }

        return images;
    }

    private static List<OVFPackageInstanceDto> filterAlreadyInsertedVirtualImagePathsOrEnterpriseDoNotExist(
        List<OVFPackageInstanceDto> disks, final Integer idRepo)
    {

        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();

        List<OVFPackageInstanceDto> notInsertedDisks = new LinkedList<OVFPackageInstanceDto>();
        for (OVFPackageInstanceDto disk : disks)
        {

            boolean enter = isEnterpriseOnDB(session, disk.getIdEnterprise().longValue());
            boolean inserted =
                isAlreadyInsertedVirtualImagePath(session, disk.getDiskFilePath(),
                    Long.valueOf(disk.getIdEnterprise()).intValue(), idRepo);

            if (enter && !inserted)
            {
                notInsertedDisks.add(disk);
            }
        }

        tx.commit();

        return notInsertedDisks;
    }

    private static VirtualimageHB imageFromDisk(OVFPackageInstanceDto disk,
        final Integer idRepository) throws VirtualImageException
    {

        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        // HibernateUtil.getSession();
        // Transaction transaction = session.beginTransaction();

        VirtualimageHB vimage = new VirtualimageHB();

        /**
         * TODO taking only the first category !!!
         */

        String category;

        if (disk.getCategoryName() != null)
        {
            category = disk.getCategoryName();
        }
        else
        {
            category = findCategoryOnOVFPackage(disk.getOvfUrl(), session);

            if (category == null)
            {
                category = "Others";
            }
        }

        vimage.setCategory(getCategory(session, category));

        if (disk.getIdEnterprise() != 0)
        {

            vimage.setIdEnterprise(Long.valueOf(disk.getIdEnterprise()).intValue());
        }
        else
        {
            logger.error("Enterpirse ID not set on the DiskInfo");
        }

        vimage.setName(disk.getName());
        vimage.setPathName(disk.getDiskFilePath());

        // XXXvimage.setRepository(getRepository(session));

        BigInteger byteRam = getBytes(String.valueOf(disk.getRam()), disk.getRamSizeUnit().name());
        // on Mb

        // BigInteger factor = new BigInteger("2");
        // BigInteger ramCapacityMB = byteRam.divide(factor.pow(20));
        // BigInteger ramCapacityMB = byteRam.divide(BigInteger.valueOf(1048576));

        long ramCapacityMB = byteRam.longValue() / 1048576;

        BigInteger byteHd = getBytes(String.valueOf(disk.getHd()), disk.getHdSizeUnit().name());

        vimage.setCpuRequired(disk.getCpu());

        String truncatedDescription = disk.getDescription();
        if (truncatedDescription.length() > 254) // TODO data truncation
        {
            truncatedDescription = truncatedDescription.substring(0, 254);
        }

        vimage.setDescription(truncatedDescription);

        vimage.setRamRequired(new Long(ramCapacityMB).intValue());
        vimage.setHdRequired(byteHd.longValue());

        String iconPath = disk.getIconPath();
        if (iconPath == null)
        {
            iconPath = findIconOnOVFPackage(disk.getOvfUrl(), session);
        }

        if (iconPath != null)
        {
            IconHB icon = getIcon("user", session, iconPath);
            vimage.setIcon(icon);
        }

        vimage.setTreaty(0);

        vimage.setOvfId(disk.getOvfUrl());

        DiskFormatType diskFormat;

        if (disk.getMasterDiskFilePath() != null)
        {
            try
            {

                VirtualimageHB master =
                    getVirtualImageIdFromPath(session, disk.getMasterDiskFilePath(),
                        disk.getIdEnterprise(), idRepository);
                // getDiskFormatTypeFromUri(session, disk.getImageType()).getId()

                vimage.setMaster(master);
                diskFormat = master.getType();
            }
            catch (IdNotFoundException e)
            {
                final String msg = "Can not find the MasterId for the disk";
                throw new VirtualImageException(msg, e);
            }
        }
        else
        {
            diskFormat = DiskFormatType.valueOf(disk.getDiskFileFormat().name().toUpperCase());
        }

        vimage.setType(diskFormat);

        vimage.setDiskFileSize(disk.getDiskFileSize());

        // XXX daoF.endConnection();
        // TODO transaction.commit();

        return vimage;
    }

    /**
     * NOTE: don't do in you home.
     */
    private static String findIconOnOVFPackage(String ovfUrl, Session session)
    {

        List<String> iconPaths =
            session.createSQLQuery(
                "select i.path from icon i, ovf_package p where p.idIcon=i.idIcon and p.url='"
                    + ovfUrl + "'").list();

        if (iconPaths != null && iconPaths.size() > 0)
        {
            return iconPaths.get(0);
        }
        else
        {
            return null;
        }
    }

    private static String findCategoryOnOVFPackage(String ovfUrl, Session session)
    {
        List<String> categories =
            session.createSQLQuery(
                "select c.name from category c, ovf_package p where p.idCategory=c.idCategory and p.url='"
                    + ovfUrl + "'").list();

        if (categories != null && categories.size() > 0)
        {
            return categories.get(0);
        }
        else
        {
            return null;
        }
    }

    private static boolean isEnterpriseOnDB(Session session, Long idEnterprise)
    {
        /**
         * TODO use DAO
         */

        Long count =
            (Long) session
                .createQuery(
                    "SELECT COUNT(*) FROM com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB WHERE idEnterprise='"
                        + String.valueOf(idEnterprise) + "'").uniqueResult();

        if (count == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static VirtualimageHB getVirtualImageIdFromPath(Session session, String imagePath,
        final Integer idEnterprise, final Integer idRepository) throws IdNotFoundException
    {
        /**
         * TODO use DAO
         */

        VirtualimageHB image =
            (VirtualimageHB) session
                .createQuery(
                    "SELECT vi FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB vi WHERE vi.pathName= :pathName"
                        + " AND vi.idEnterprise= :idEnterprise AND vi.repository.idRepository= :idRepository")
                .setParameter("pathName", imagePath).setParameter("idEnterprise", idEnterprise)
                .setParameter("idRepository", idRepository).uniqueResult();

        if (image == null)
        {
            final String msg =
                String.format(
                    "Can not find the Virtual Image with path [%s] for the enterprise [%s]",
                    imagePath, idEnterprise);
            throw new IdNotFoundException(msg);
        }
        else
        {
            return image;
        }
    }

    /**
     * XXX unused idDiskFormat
     */
    private static boolean isAlreadyInsertedVirtualImagePath(Session session, String imagePath,
        Integer idEnterprise, final Integer idRepo)
    {
        /**
         * TODO use DAO
         */
        Long count =
            (Long) session
                .createQuery(
                    "SELECT COUNT(*) FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB WHERE "
                        + "pathName= :pathName"
                        + " AND (idEnterprise= :idEnterprise OR idEnterprise=NULL)"
                        + " AND repository.idRepository= :idRepo")
                .setParameter("pathName", imagePath).setParameter("idEnterprise", idEnterprise)
                .setParameter("idRepo", idRepo).uniqueResult();

        return count != 0;
    }

    private static CategoryHB getCategory(Session session, String category)
        throws HibernateException
    {
        /**
         * TODO use DAO
         */

        logger.trace("Find on DB the category [{}]", category);

        String customQuery = "SELECT * FROM category WHERE name='" + category + "'";

        CategoryHB categoryHB =
            (CategoryHB) session.createSQLQuery(customQuery).addEntity(CategoryHB.class)
                .uniqueResult();

        if (categoryHB == null)
        {
            categoryHB = new CategoryHB();
            categoryHB.setIsDefault(0);
            categoryHB.setIsErasable(1);
            categoryHB.setName(category);

            session.save(categoryHB);
        }

        return categoryHB;

    }

    // Helper method to createVirtualImageDownloadedFromRepositorySpace
    // If the icon doesn't exist it creates a new entry in the database
    private static IconHB getIcon(String user, Session session, String iconPath)
        throws HibernateException
    {
        /**
         * TODO use DAO
         */

        String sql = "SELECT * FROM icon where LOWER(path)='" + iconPath.toLowerCase() + "'";
        IconHB iconHB = (IconHB) session.createSQLQuery(sql).addEntity(IconHB.class).uniqueResult();

        if (iconHB == null)
        {
            String iconName;
            if (iconPath.startsWith("http"))
            {
                iconName =
                    iconPath.substring(iconPath.lastIndexOf('/') + 1, iconPath.lastIndexOf('.'));

                if (iconName.length() > 20)
                {
                    iconName = iconName.substring(0, 20);
                }
            }
            else
            {
                iconName = "InvalidIcon";
            }

            // Create a new icon
            iconHB = new IconHB();
            iconHB.setName(iconName);
            iconHB.setPath(iconPath);

            session.save(iconHB);
        }

        return iconHB;
    }

    /**
     * Gets the disk capacity on bytes.
     * 
     * @param capacity, numeric value
     * @param alloctionUnit, bytes by default but can be Kb, Mb, Gb or Tb.
     * @return capacity on bytes
     **/
    private static BigInteger getBytes(String capacity, String allocationUnits)
        throws VirtualImageException
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
            throw new VirtualImageException(msg);
        }

        return capa.multiply(factor);
    }

}
