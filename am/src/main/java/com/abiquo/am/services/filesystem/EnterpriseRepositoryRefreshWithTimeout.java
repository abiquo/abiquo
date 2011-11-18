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

package com.abiquo.am.services.filesystem;

import static com.abiquo.am.services.OVFPackageConventions.OVF_BUNDLE_PATH_IDENTIFIER;
import static com.abiquo.am.services.OVFPackageConventions.OVF_FILE_EXTENSION;
import static com.abiquo.am.services.OVFPackageConventions.OVF_LOCATION_PREFIX;
import static com.abiquo.am.services.OVFPackageConventions.createBundleOvfId;
import static com.abiquo.am.services.OVFPackageConventions.customDencode;
import static com.abiquo.am.services.filesystem.OVFPackageInstanceFileSystem.getOVFStatus;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.services.filesystem.filters.BundleImageFileFilter;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;

/**
 * Run a folder list in another thread in order to cancel if it take too long
 */
public class EnterpriseRepositoryRefreshWithTimeout implements
    Callable<List<OVFPackageInstanceStateDto>>
{
    private final static Logger LOG = LoggerFactory
        .getLogger(EnterpriseRepositoryRefreshWithTimeout.class);

    final String erPath;

    final String relativePath;

    final Boolean includeBundles;

    final Boolean cleanDeploys;

    public EnterpriseRepositoryRefreshWithTimeout(final String erPath, final String relativePath,
        final Boolean includeBundles, final Boolean cleanDeploys)
    {
        this.erPath = erPath;
        this.relativePath = relativePath;
        this.includeBundles = includeBundles;
        this.cleanDeploys = cleanDeploys;
    }

    @Override
    public List<OVFPackageInstanceStateDto> call() throws Exception
    {
        return traverseOVFFolderStructure(erPath, relativePath, includeBundles, cleanDeploys,
            erPath);
    }

    /**
     * Returns DOWNLOAD Ovfs
     * 
     * @param includeBundles, if true return also the OVF packages identifier for the bundled
     *            packages (only used on status = DOWNLOAD).
     * @param relativePath, recursive accumulated folder structure.(empty at the fist call).
     */
    private List<OVFPackageInstanceStateDto> traverseOVFFolderStructure(final String erPath,
        final String relativePath, final Boolean includeBundles, final Boolean cleanDeploys,
        final String enterpriseRepositoryPath)
    {
        final List<OVFPackageInstanceStateDto> ovfids =
            new LinkedList<OVFPackageInstanceStateDto>();
        final File currentFile = new File(erPath);

        for (File file : currentFile.listFiles())
        {
            // TODO assert inside a folder only one .ovf (to exactly know the bundle parent!)

            final String recRelativePath =
                relativePath.isEmpty() ? file.getName() : relativePath + '/' + file.getName();

            if (file.isDirectory() && file.listFiles().length != 0)
            {
                // recursion
                ovfids.addAll(traverseOVFFolderStructure(file.getAbsolutePath(), recRelativePath,
                    includeBundles, cleanDeploys, enterpriseRepositoryPath));
            }
            else if (file.isFile() && file.getName().endsWith(OVF_FILE_EXTENSION))
            {
                final String ovfId = OVF_LOCATION_PREFIX + customDencode(recRelativePath);
                final OVFPackageInstanceStateDto status =
                    getOVFStatus(enterpriseRepositoryPath, ovfId);

                if (cleanDeploys && status.getStatus() == OVFStatusEnumType.DOWNLOADING)
                {
                    try
                    {
                        FileUtils.deleteDirectory(currentFile);
                    }
                    catch (Exception e)
                    {
                        LOG.error("Can not delete the interrupted download [{}], \n{}", ovfId, e);
                    }
                }
                else
                {
                    ovfids.add(status);

                    if (includeBundles)
                    {
                        for (String fileBund : currentFile.list(new BundleImageFileFilter()))
                        {
                            final String snapshot =
                                fileBund.substring(0, fileBund.indexOf(OVF_BUNDLE_PATH_IDENTIFIER));

                            final String bundleOvfId = createBundleOvfId(ovfId, snapshot);

                            OVFPackageInstanceStateDto bundleState =
                                new OVFPackageInstanceStateDto();

                            bundleState.setOvfId(bundleOvfId);
                            bundleState.setMasterOvf(ovfId);
                            bundleState.setStatus(OVFStatusEnumType.DOWNLOAD);

                            ovfids.add(bundleState);
                        }
                    }// includeBundles

                }
            }// its an Ovf
             // else if (file.isFile() && file.getName().endsWith(OVF_FILE_EXTENSION) &&
             // file.getName().contains(OVF_BUNDLE_PATH_IDENTIFIER))
             // { logger.debug("deleting [{}]", file.getName());
             // file.delete();
             // }

        }// all files in currentFile

        return ovfids;
    }

}
