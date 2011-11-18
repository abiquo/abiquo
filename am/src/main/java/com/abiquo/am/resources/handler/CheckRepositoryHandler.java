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

package com.abiquo.am.resources.handler;

import static com.abiquo.appliancemanager.config.AMConfiguration.REPOSITORY_FILE_MARK_CHECK_TIMEOUT_SECONDS;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.wink.server.handlers.AbstractHandler;
import org.apache.wink.server.handlers.MessageContext;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;

/**
 * Before attending any request checks the repository is Ok (file mark '.abiquo_repository' exist
 * and is writable).
 * <p>
 * It do by spawning a new thread as a stopped NFS repository can hang the java File API.
 */
public class CheckRepositoryHandler extends AbstractHandler
{
    @Override
    protected void handleRequest(final MessageContext context) throws Throwable
    {
        canUseRepository();

        super.handleRequest(context);
    }

    public Void canUseRepository()
    {
        // XXX consider global limiting of threads
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        boolean canUse;

        final Future<Boolean> futureExist =
            executor.submit(new RepositoryFileMarkExistAndWritable());

        try
        {
            canUse = futureExist.get(REPOSITORY_FILE_MARK_CHECK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            canUse = false;
        }
        catch (ExecutionException e)
        {
            canUse = false;
        }
        catch (TimeoutException e)
        {
            futureExist.cancel(true);
            canUse = false;
        }
        finally
        {
            executor.shutdownNow();
        }

        if (!canUse)
        {
            throw new AMException(AMError.REPO_NOT_ACCESSIBLE);
        }

        return null;
    }

    public class RepositoryFileMarkExistAndWritable implements Callable<Boolean>
    {
        private final File REPOSITORY_FILE_MARK = new File(AMConfigurationManager.getInstance()
            .getAMConfiguration().getRepositoryPath()
            + ".abiquo_repository");

        @Override
        public Boolean call() throws Exception
        {
            return REPOSITORY_FILE_MARK.exists() && REPOSITORY_FILE_MARK.canWrite();
        }
    }

}
