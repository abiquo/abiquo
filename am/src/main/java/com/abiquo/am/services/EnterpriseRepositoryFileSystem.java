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

package com.abiquo.am.services;

import static com.abiquo.am.services.OVFPackageConventions.OVF_BUNDLE_PATH_IDENTIFIER;
import static com.abiquo.am.services.OVFPackageConventions.cleanOVFurlOnOldRepo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.util.OVFPackageInstanceToOVFEnvelope;
import com.abiquo.am.services.util.TimeoutFSUtils;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

public class EnterpriseRepositoryFileSystem
{
    private final static Logger LOG = LoggerFactory.getLogger(EnterpriseRepositoryFileSystem.class);

    private final static String BASE_REPO_PATH = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryPath();

    private final static Integer FS_TIMOUT_MS = AMConfigurationManager.getInstance()
        .getAMConfiguration().getFsTimeoutMs();

    private static Map<String, List<String>> cachedpackages = new HashMap<String, List<String>>();

    /**
     * Check if it exist or create it.
     */
    public static void validateEnterpirseRepositoryPathFile(final String enterpriseRepositoryPath)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        File enterpriseRepositoryFile = new File(enterpriseRepositoryPath);

        if (!enterpriseRepositoryFile.exists())
        {
            if (!enterpriseRepositoryFile.mkdirs())
            {
                throw new AMException(AMError.REPO_NOT_ACCESSIBLE, enterpriseRepositoryPath);

            }
        }

        if (!(enterpriseRepositoryFile.exists() && enterpriseRepositoryFile.canWrite() && enterpriseRepositoryFile
            .isDirectory()))
        {
            throw new AMException(AMError.REPO_NOT_ACCESSIBLE, enterpriseRepositoryPath);
        }
    }

    public static List<String> getAllOVF(final String enterpriseRepositoryPath,
        final boolean includeBundeles)
    {

        /**
         * TODO caching results
         */
        File enterpriseRepositoryFile = new File(enterpriseRepositoryPath);

        TimeoutFSUtils.getInstance().canUseRepository();

        List<String> availableOvs = null;

        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<List<String>> futureAvailable =
            executor.submit(new EnterpriseRepositoryRefreshWithTimeout(enterpriseRepositoryFile
                .getAbsolutePath(), new String(), includeBundeles, false));

        try
        {
            availableOvs = futureAvailable.get(FS_TIMOUT_MS, TimeUnit.MILLISECONDS); 
        }
        catch (TimeoutException e)
        {
            futureAvailable.cancel(true);
        }
        catch (Exception e)
        {
            LOG.error("Can't access the folder " + enterpriseRepositoryPath, e);
        }
        finally
        {
            executor.shutdownNow();
        }

        if (availableOvs != null)
        {
            cachedpackages.put(enterpriseRepositoryPath, availableOvs);
        }
        else if (cachedpackages.containsKey(enterpriseRepositoryPath))
        {
            LOG.warn("Slow file system, can't list folder [{}] content (timout), "
                + "using cached result.", enterpriseRepositoryPath);

            availableOvs = cachedpackages.get(enterpriseRepositoryPath);
        }
        else
        {
            throw new AMException(AMError.REPO_TIMEOUT_REFRESH, enterpriseRepositoryPath);
        }

        List<String> cleanovfids = new LinkedList<String>();

        for (String ovfid : availableOvs)
        {
            cleanovfids.add(cleanOVFurlOnOldRepo(ovfid));
        }

        return cleanovfids;
    }

    public static Long getUsedMb(String enterpriseRepositoryPath)
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        return sizeOfDirectory(new File(enterpriseRepositoryPath)) / (1024 * 1024);
    }

    public static Long getCapacityMb()
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        return new File(BASE_REPO_PATH).getTotalSpace() / (1024 * 1024);
    }

    public static Long getFreeMb()
    {
        TimeoutFSUtils.getInstance().canUseRepository();

        return new File(BASE_REPO_PATH).getFreeSpace() / (1024 * 1024);
    }

    private static Long sizeOfDirectory(final File f)
    {
        if (f.isFile())
        {
            return f.length();
        }
        else if (f.isDirectory() && f.listFiles().length != 0)
        {
            Long acum = 0l;
            for (File element : f.listFiles())
            {
                acum += sizeOfDirectory(element);
            }

            return acum;
        }
        else
        {
            return 0l;
        }
    }

    public static String createBundle(final String packagePath, final String snapshot,
        final String packageName, EnvelopeType envelopeBundle)
    {

        final String snapshotMark = snapshot + OVF_BUNDLE_PATH_IDENTIFIER;
        final String bundlePath = packagePath + snapshotMark + packageName;

        File envelopeBundleFile = new File(bundlePath);
        if (envelopeBundleFile.exists())
        {
            throw new AMException(AMError.OVFPI_SNAPSHOT_ALREADY_EXIST, bundlePath);
        }

        FileOutputStream bundleEnvelopeStream = null;
        try
        {
            envelopeBundleFile.createNewFile();
            bundleEnvelopeStream = new FileOutputStream(envelopeBundleFile);
        }
        catch (Exception e1)
        {
            throw new AMException(AMError.OVFPI_SNAPSHOT_ALREADY_EXIST, bundlePath);
        }

        envelopeBundle =
            OVFPackageInstanceToOVFEnvelope.fixFilePathsAndSize(envelopeBundle, snapshot,
                packagePath);

        try
        {
            OVFSerializer.getInstance().writeXML(envelopeBundle, bundleEnvelopeStream);
        }
        catch (XMLException e)
        {
            throw new AMException(AMError.OVFPI_SNAPSHOT_CREATE, bundlePath, e);
        }
        finally
        {
            try
            {
                bundleEnvelopeStream.close();
            }
            catch (IOException e)
            {
                final String cause = String.format("Can not close the stream to [%s]", bundlePath);
                LOG.error(cause);
            }
        }// close envelope write stream

        return snapshotMark;
    }

    public static boolean isEnoughtSpaceOn(final String enterpriseRepositoryPath,
        final Long expected)
    {
        return new File(enterpriseRepositoryPath).getFreeSpace() > expected;
    }
}
