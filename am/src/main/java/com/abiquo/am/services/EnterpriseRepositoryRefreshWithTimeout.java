package com.abiquo.am.services;

import static com.abiquo.am.services.OVFPackageConventions.OVF_BUNDLE_PATH_IDENTIFIER;
import static com.abiquo.am.services.OVFPackageConventions.OVF_FILE_EXTENSION;
import static com.abiquo.am.services.OVFPackageConventions.OVF_LOCATION_PREFIX;
import static com.abiquo.am.services.OVFPackageConventions.createBundleOvfId;
import static com.abiquo.am.services.OVFPackageConventions.customDencode;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.services.util.BundleImageFileFilter;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;

import static com.abiquo.am.services.OVFPackageInstanceFileSystem.getOVFStatus;

/**
 * Run a folder list in another thread in order to cancel if it take too long
 */
public class EnterpriseRepositoryRefreshWithTimeout implements Callable<List<String>>
{
    private final static Logger LOG = LoggerFactory
        .getLogger(EnterpriseRepositoryRefreshWithTimeout.class);

    final String erPath;

    final String relativePath;

    final Boolean includeBundles;

    final Boolean cleanDeploys;

    public EnterpriseRepositoryRefreshWithTimeout(String erPath, String relativePath,
        Boolean includeBundles, Boolean cleanDeploys)
    {
        this.erPath = erPath;
        this.relativePath = relativePath;
        this.includeBundles = includeBundles;
        this.cleanDeploys = cleanDeploys;
    }

    @Override
    public List<String> call() throws Exception
    {
        return traverseOVFFolderStructure(erPath, relativePath, includeBundles, cleanDeploys,
            erPath);
    }

    /**
     * @param includeBundles, if true return also the OVF packages identifier for the bundled
     *            packages (only used on status = DOWNLOAD).
     * @param relativePath, recursive accumulated folder structure.(empty at the fist call).
     */
    private List<String> traverseOVFFolderStructure(final String erPath, final String relativePath,
        final Boolean includeBundles, final Boolean cleanDeploys,
        final String enterpriseRepositoryPath)
    {
        List<String> ovfids = new LinkedList<String>();
        File enterpriseRepositoryFile = new File(erPath);

        if (!enterpriseRepositoryFile.exists() || !enterpriseRepositoryFile.isDirectory())
        {
            return new LinkedList<String>();
        }

        for (File file : enterpriseRepositoryFile.listFiles())
        {
            /**
             * TODO assert inside a folder only one .ovf (to exactly know the bundle parent!)
             */
            String recRelativePath;
            if (relativePath.isEmpty())
            {

                recRelativePath = file.getName();
            }
            else
            {
                recRelativePath = relativePath + '/' + file.getName();
            }

            if (file.exists() && file.isDirectory() && file.listFiles().length != 0)
            {
                List<String> recOvfids =
                    traverseOVFFolderStructure(file.getAbsolutePath(), recRelativePath,
                        includeBundles, cleanDeploys, enterpriseRepositoryPath);

                ovfids.addAll(recOvfids);
            }
            // else if (file.isFile() && file.getName().endsWith(OVF_FILE_EXTENSION) &&
            // file.getName().contains(OVF_BUNDLE_PATH_IDENTIFIER))
            // { logger.debug("deleting [{}]", file.getName());
            // file.delete();
            // }
            else if (file.exists() && file.isFile() && file.getName().endsWith(OVF_FILE_EXTENSION))
            // an ovf
            {
                recRelativePath = customDencode(recRelativePath);

                String ovfId = OVF_LOCATION_PREFIX + recRelativePath;

                OVFPackageInstanceStatusType ovfStatus =
                    getOVFStatus(enterpriseRepositoryPath, ovfId);

                if (cleanDeploys && ovfStatus == OVFPackageInstanceStatusType.DOWNLOADING)
                {
                    try
                    {
                        FileUtils.deleteDirectory(enterpriseRepositoryFile);
                    }
                    catch (Exception e)
                    {
                        LOG.error("Can not delete the interrupted download [{}], \n{}", ovfId, e);
                    }
                }
                else if (ovfStatus == OVFPackageInstanceStatusType.DOWNLOAD)
                {
                    ovfids.add(ovfId);

                    if (includeBundles)
                    {
                        for (String fileBund : enterpriseRepositoryFile
                            .list(new BundleImageFileFilter()))
                        {
                            final String snapshot =
                                fileBund.substring(0, fileBund.indexOf(OVF_BUNDLE_PATH_IDENTIFIER));

                            final String bundleOvfId = createBundleOvfId(ovfId, snapshot);

                            ovfids.add(bundleOvfId);
                        }
                    }// includeBundles
                }

            }// an OVF

        }// files

        return ovfids;
    }

}// traverse class
