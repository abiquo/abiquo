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

package com.abiquo.api.services.appslibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.persistence.impl.AppsLibraryDAO;
import com.abiquo.api.persistence.impl.CategoryDAO;
import com.abiquo.api.persistence.impl.IconDAO;
import com.abiquo.api.persistence.impl.OVFPackageDAO;
import com.abiquo.api.persistence.impl.OVFPackageListDAO;
import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.appslibrary.AppsLibrary;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.enumerator.DiskFormatType;

@Service
@Transactional
public class OVFPackageService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(OVFPackageService.class);

    @Autowired
    OVFPackageDAO dao;

    @Autowired
    OVFPackageListDAO listDao;

    @Autowired
    AppsLibraryDAO appsLibraryDao;

    @Autowired
    CategoryDAO categoryDao;

    @Autowired
    IconDAO iconDao;

    public List<OVFPackage> getOVFPackagesByEnterprise(final Integer idEnterprise)
    {
        return dao.findByEnterprise(idEnterprise);
    }

    public OVFPackage addOVFPackage(OVFPackage ovfPackage, final Integer idEnterprise)
    {
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(idEnterprise);
        ovfPackage.setAppsLibrary(appsLib);

        try
        {
            dao.makePersistent(ovfPackage);
        }
        catch (Throwable ex)
        {
            LOGGER.error("Could not add package: " + ovfPackage.getUrl(), ex);
        }

        return ovfPackage;
    }

    public OVFPackage getOVFPackage(Integer id)
    {
        return dao.findById(id);
    }

    public OVFPackage modifyOVFPackage(Integer ovfPackageId, OVFPackage ovfPackage,
        final Integer idEnterprise)
    {
        OVFPackage old = dao.findById(ovfPackageId);

        // TODO - Apply changes and compare etags
        old.setName(ovfPackage.getName());
        old.setDescription(ovfPackage.getDescription());

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(idEnterprise);
        ovfPackage.setAppsLibrary(appsLib);

        old.setCategory(ovfPackage.getCategory());
        old.setType(ovfPackage.getType());
        old.setIcon(ovfPackage.getIcon());
        old.setProductName(ovfPackage.getProductName());
        old.setProductUrl(ovfPackage.getProductUrl());
        old.setProductVendor(ovfPackage.getProductVendor());
        old.setProductVersion(ovfPackage.getProductVersion());
        old.setUrl(ovfPackage.getUrl());
        old.setOvfPackageLists(ovfPackage.getOvfPackageLists());

        return dao.makePersistent(old);
    }

    public void removeOVFPackage(Integer id)
    {
        OVFPackage ovfPackage = dao.findById(id);

        // manually remove lists associated
        // As OVFPackage<->OVFPackageLists is a Many-to-many relation, the delete operation
        // must be done manually for the dependant (not owner) side in the relation: OVFPackage in
        // this case
        List<OVFPackageList> lists = ovfPackage.getOvfPackageLists();
        for (Iterator iterator = lists.iterator(); iterator.hasNext();)
        {
            OVFPackageList ovfPackageList = (OVFPackageList) iterator.next();
            ovfPackageList.getOvfPackages().remove(ovfPackage);
            listDao.makePersistent(ovfPackageList);
        }

        dao.makeTransient(ovfPackage);
    }

    public OVFPackage ovfPackageFromOvfDescription(OVFDescription descr,
        final String baseRepositorySpaceURL)
    {
        String packageUrl = baseRepositorySpaceURL + descr.getOVFFile(); // TODO check not double //

        String name = descr.getProduct().getValue();
        String description = descr.getInfo().getValue();

        // TODO data truncation
        if (description.length() > 255)
        {
            description = description.substring(0, 254);
        }

        OVFPackage pack = new OVFPackage();
        pack.setDescription(description);
        pack.setName(name);
        pack.setProductName(name);

        /**
         * TODO product verison .... url ...
         */

        pack.setUrl(packageUrl);

        if (descr.getIcon() != null && descr.getIcon().size() == 1)
        {
            String iconPath = descr.getIcon().get(0).getFileRef();
            // TODO start with http://

            Icon icon = findByIconPathOrCreateNew(iconPath);
            pack.setIcon(icon);
        }

        DiskFormatType format = findByDiskFormatNameOrUnknow(descr.getDiskFormat());
        pack.setType(format);

        Category category = findByCategoryNameOrCreateNew(descr.getOVFCategories());
        pack.setCategory(category);

        Long diskSizeL = null;
        final String diskSize = descr.getDiskSize();
        if (diskSize != null && !diskSize.isEmpty())
        {
            diskSizeL = Long.parseLong(diskSize);
        }
        else
        {
            diskSizeL = getDiskFileSizeMbFromOvfId(packageUrl);
        }

        pack.setDiskSizeMb(diskSizeL);

        return pack;
    }

    private Long getDiskFileSizeMbFromOvfId(final String ovfid)
    {
        InputStream isovf = null;
        try
        {
            URL ovfurl = new URL(ovfid);
            isovf = ovfurl.openStream();
            EnvelopeType envelope = OVFSerializer.getInstance().readXMLEnvelope(isovf);

            Long accSize = 0l;

            for (FileType file : envelope.getReferences().getFile())
            {
                accSize += file.getSize().longValue();
            }

            return accSize / (1024 * 1024);
        }
        catch (Exception e)
        {
            // TODO logger can not obtain ....
            return null;
        }
        finally
        {
            if (isovf != null)
            {
                try
                {
                    isovf.close();
                }
                catch (IOException e)
                {
                    //
                }
            }
        }// finally
    }

    private DiskFormatType findByDiskFormatNameOrUnknow(final String diskFormatName)
    {
        DiskFormatType format;
        if (diskFormatName == null)
        {
            format = DiskFormatType.UNKNOWN;
        }
        else if ("STREAM_OPTIMIZED".equalsIgnoreCase(diskFormatName)) // FIXME
        {
            format = DiskFormatType.VMDK_STREAM_OPTIMIZED;
        }
        else
        {
            try
            {
                format = DiskFormatType.valueOf(diskFormatName);
            }
            catch (Exception e)
            {
                format = DiskFormatType.UNKNOWN;
            }
        }

        return format;
    }

    private Icon findByIconPathOrCreateNew(final String iconPath)
    {
        if (iconPath == null)
        {
            return null;
        }

        Icon icon;

        icon = iconDao.findByPath(iconPath);

        if (icon == null)
        {
            icon = new Icon();
            icon.setName("unname"); // TODO
            icon.setPath(iconPath);

            icon = iconDao.makePersistent(icon);
        }

        return icon;
    }

    private Category findByCategoryNameOrCreateNew(final String categoryName)
    {
        if (categoryName == null || categoryName.isEmpty())
        {
            return categoryDao.findDefault();
        }

        Category cat;

        try
        {
            cat = categoryDao.findByName(categoryName);
        }
        catch (Exception e) // XXX if (cat == null)
        {
            cat = new Category();
            cat.setName(categoryName);
            cat.setIsDefault(0);
            cat.setIsErasable(1);

            cat = categoryDao.makePersistent(cat);
        }

        return cat;
    }

}
