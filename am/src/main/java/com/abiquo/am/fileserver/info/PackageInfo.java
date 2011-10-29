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

package com.abiquo.am.fileserver.info;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.am.resources.ApplianceManagerPaths;
import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.OVFPackageInstanceNotifier;
import com.abiquo.am.services.notify.AMNotifierFactory;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;

/**
 * An OVF package being download.
 */
public class PackageInfo
{
    private final static Logger logger = LoggerFactory.getLogger(PackageInfo.class);

    /** The OVF locator. Where the Envelope is available. */
    protected final String ovfId;

    /** The enterprise requesting these download. */
    protected final String idEnterprise;

    /** All the files on the package. */
    // TODO no so public
    public List<FileInfo> files;

    /** The total amount of bytes (sum of all the files) */
    private long allExpectedSize = 0;

    /** A percentage of the total remaining work to finalize the download of the package. */
    protected double progress;

    /** Shared object used to find all the being download packages, on end an entry removed. */
    private Map<String, PackageInfo> htTransfers;

    public PackageInfo(String ovfId, String enterpriseId, Map<String, PackageInfo> htTransfers)
    {
        this.ovfId = ovfId;
        this.idEnterprise = enterpriseId;
        this.htTransfers = htTransfers;

    }

    public Long getAllExpectedSize()
    {
        return allExpectedSize;
    }

    /**
     * Adds a new file to download on the package.
     */
    public void addFileInfo(FileInfo file)
    {
        if (files == null)
        {
            files = new LinkedList<FileInfo>();
        }

        files.add(file);
        allExpectedSize += file.expectedBytes;

        file.setPackage(this);
    }

    /**
     * When some file ends check if there are still pending packages, if not it indicate the package
     * has been successfully download.
     */
    public synchronized void onFileEnd()
    {
        boolean pendings = false;

        Iterator<FileInfo> itFiles = files.iterator();

        while (itFiles.hasNext() && !pendings)
        {
            FileInfo file = itFiles.next();

            if (file.isAlreadyBeingDownload)
            {
                file.getCurrentBytes(); // to check is done
            }

            if (!file.isDone)
            {
                pendings = true;
            }

        }

        if (!pendings)
        {

            synchronized (htTransfers)
            {
                htTransfers.remove(ovfId);
            }

            try
            {
                AMNotifierFactory.getInstance().setOVFStatus(idEnterprise, ovfId,
                    OVFStatusEnumType.DOWNLOAD);
            }
            catch (Exception e) // IdNotFoundException RepositoryException EventException
            {
                final String msg =
                    String.format("Can not notify the DOWNLOAD of [%s] caused by [%s]", ovfId,
                        e.getLocalizedMessage());
                logger.error(msg);
            }
        }
    }

    /**
     * When the OVF package needs to be cancelled, all the active file downloads are cancelled and
     * its destination files removed.
     */
    public synchronized void cancelDownload(boolean deleteFolder)
    {
        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(idEnterprise);

        for (FileInfo file : files)
        {
            file.onCancel();
        }

        try
        {

            AMNotifierFactory.getInstance().setOVFStatus(idEnterprise, ovfId,
                OVFStatusEnumType.NOT_DOWNLOAD);

            if (deleteFolder)
            {
                enterpriseRepository.deleteOVF(ovfId);
            }
        }
        catch (Exception e)
        {
            logger.error("Can not remove package, already have snpahots", e);
        }

        //
        // // also delete the OVF envelope
        // File envelopeFile = new File(enterpriseRepository.getOVFIdPath(ovfId));
        // envelopeFile.delete();
        //
        // // TODO check exist and error during .delete

        /**
         * XXX do not delete the package folder
         */

        htTransfers.remove(ovfId);

    }

    /**
     * When some file have a problem downloading, all the package is cancelled and the error cause
     * is indicated on the corresponding OVFIndex.
     */
    public void onError(String error)
    {
        cancelDownload(false);

        try
        {
            AMNotifierFactory.getInstance().setOVFStatusError(idEnterprise, ovfId, error);
        }
        catch (Exception e)
        {
            final String msg =
                String.format("Can not notify to ERROR of [%s] caused by [%s]", ovfId,
                    e.getLocalizedMessage());
            logger.error(msg);
        }
    }

    /**
     * Gets the average progress of all the files being download on the package.
     */
    public double getProgress()
    {
        long allcurrentBytes = 0;
        for (FileInfo file : files)
        {
            // TODO? check file error
            allcurrentBytes += file.getCurrentBytes();
        }

        return ((double) allcurrentBytes / allExpectedSize) * 100;
    }

    /**
     * Gets the OVF identifier (source locator)
     */
    public String getOvfId()
    {
        return ovfId;
    }

    /**
     * Gets the enterprise requesting these download.
     */
    public String getEnterpriseId()
    {
        return idEnterprise;
    }

}
