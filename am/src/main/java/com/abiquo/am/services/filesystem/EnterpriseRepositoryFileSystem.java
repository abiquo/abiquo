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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.transport.TemplateStateDto;

public class EnterpriseRepositoryFileSystem
{
    private final static Logger LOG = LoggerFactory.getLogger(EnterpriseRepositoryFileSystem.class);

    private final static String BASE_REPO_PATH = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryPath();

    private final static Integer FS_TIMOUT_MS = AMConfigurationManager.getInstance()
        .getAMConfiguration().getFsTimeoutMs();

    /**
     * Check if it exist or create it.
     */
    public static void validateEnterpirseRepositoryPathFile(final String enterpriseRepositoryPath)
    {
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

    public static List<TemplateStateDto> getAllOVF(final String enterpriseRepositoryPath,
        final boolean includeBundeles)
    {
        List<TemplateStateDto> availableOvs = null;

        // TODO consider global thread limit
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<List<TemplateStateDto>> futureAvailable =
            executor.submit(new EnterpriseRepositoryRefreshWithTimeout(//
            new File(enterpriseRepositoryPath).getAbsolutePath(),
                new String(),
                includeBundeles,
                false)); // do not clean downloading packages

        try
        {
            availableOvs = futureAvailable.get(FS_TIMOUT_MS, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            futureAvailable.cancel(true);
            LOG.warn("Timeout while refresh the repository folder " + enterpriseRepositoryPath, e);

        }
        catch (Exception e)
        {
            LOG.error("Can't access the folder " + enterpriseRepositoryPath, e);
        }
        finally
        {
            executor.shutdownNow();
        }

        if (availableOvs == null)
        {
            throw new AMException(AMError.REPO_TIMEOUT_REFRESH, enterpriseRepositoryPath);
        }

        return availableOvs;
    }

    /***/

    /** ############### REPOSITORY USAGE ############### */

    public static boolean isEnoughtSpaceOn(final String enterpriseRepositoryPath,
        final Long expected)
    {
        return new File(enterpriseRepositoryPath).getFreeSpace() > expected;
    }

    public static Long getUsedMb(final String enterpriseRepositoryPath)
    {
        return sizeOfDirectory(new File(enterpriseRepositoryPath)) / (1024 * 1024);
    }

    public static Long getCapacityMb()
    {
        return new File(BASE_REPO_PATH).getTotalSpace() / (1024 * 1024);
    }

    public static Long getFreeMb()
    {
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

    /** ############### DOWNLOADING FILE ############### */

    /**
     * A prefix to add at ''destinationPath'' to indicate the file is being download (see
     * ''takeFile'' and ''releaseFile'').
     */
    private final static String FILE_MARK = ".download";

    /**
     * Check the target file is not being download by another package.
     * 
     * @return null if the destination file is being download by another package.
     * @throws FileNotFoundException
     */
    public static FileOutputStream takeFile(final String destinationPath)
    {
        File destination = new File(destinationPath);
        File destinationMark = new File(destinationPath + FILE_MARK);

        if (destination.exists())
        {
            if (destinationMark.exists())
            {
                throw new AMException(AMError.TEMPLATE_INSTALL_ALREADY, destinationPath);
            }
        }

        try
        {
            File parent = destinationMark.getParentFile();
            if (!parent.exists())
            {
                if (!parent.mkdirs())
                {
                    LOG.error("Can't create file folder at " + parent.getAbsolutePath());
                }
            }

            destinationMark.createNewFile();
        }
        catch (IOException e)
        {
            LOG.error(String.format("Can't create the destination mark for [%s]", destinationPath));
            e.printStackTrace();
        }

        try
        {
            return new FileOutputStream(destination);
        }
        catch (FileNotFoundException e)
        {
            throw new AMException(AMError.TEMPLATE_INSTALL, e);
        }
    }

    /**
     * Ends a file download transaction.
     */
    public static void releaseFile(final String destinationPath)
    {
        File destinationMark = new File(destinationPath + FILE_MARK);

        if (destinationMark.exists())
        {
            destinationMark.delete();
        }
        else
        {
            final String msg =
                String.format("The destination file [%s] was not bbeing download", destinationPath);
            LOG.error(msg);
        }
    }
}
