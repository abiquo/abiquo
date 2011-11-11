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

package com.abiquo.am.services.util;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.api.service.DefaultApiService;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;

/**
 * This class intends to avoid the application to hang when the NFS repository is not longer
 * exported.
 */
public class TimeoutFSUtils extends DefaultApiService

{
    private static TimeoutFSUtils instance;

    private final static Integer FILE_EXIST_TIMEOUT_SECONDS = 10;

    private final static File REPOSITORY_FILE_MARK = new File(AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryPath()
        + ".abiquo_repository");

    public static synchronized TimeoutFSUtils getInstance()
    {
        if (instance == null)
        {
            instance = new TimeoutFSUtils();
        }
        return instance;
    }

    public Void canUseRepository()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        boolean exist;

        final Future<Boolean> futureExist =
            executor.submit(new FileExistTimeout(REPOSITORY_FILE_MARK));

        try
        {
            exist = futureExist.get(FILE_EXIST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            exist = false;
        }
        catch (ExecutionException e)
        {
            exist = false;
        }
        catch (TimeoutException e)
        {
            futureExist.cancel(true);
            exist = false;
        }
        finally
        {
            executor.shutdownNow();
        }

        if (!exist)
        {
            addError(new AMException(AMError.REPO_NOT_ACCESSIBLE));
            flushErrors();
        }

        return null;
    }

    public Void canWriteRepository()
    {
        boolean canWrite;

        try
        {
            canWrite = REPOSITORY_FILE_MARK.canWrite();

        }
        catch (Exception e)
        {
            canWrite = false;
        }

        if (!canWrite)
        {
            addError(new AMException(AMError.REPO_NOT_WRITABLE));
            flushErrors();
        }

        return null;
    }

    public class FileExistTimeout implements Callable<Boolean>
    {
        final File path;

        public FileExistTimeout(final File path)
        {
            this.path = path;
        }

        @Override
        public Boolean call() throws Exception
        {
            return path.exists();
        }
    }

}
