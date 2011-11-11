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

package com.abiquo.testng;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Set ups file system repository (creating the repository file mark)
 */
public class AMRepositoryListener implements ISuiteListener
{
    protected static final Logger LOGGER = LoggerFactory.getLogger(AMRepositoryListener.class);

    // TODO get form config !!!
    public final static String REPO_PATH = "/tmp/testrepo/";

    @Override
    public void onStart(ISuite suite)
    {
        resetRepo();
    }

    public static void resetRepo()
    {
        File vmrepo = new File(REPO_PATH);
        try
        {
            if (vmrepo.exists())
            {
                FileUtils.deleteDirectory(vmrepo);
            }

            vmrepo.mkdirs();
            new File(REPO_PATH + ".abiquo_repository").createNewFile();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't init test repository filesystem", e);
        }

        LOGGER.info("Created AM repository");
    }

    @Override
    public void onFinish(ISuite suite)
    {
        try
        {
            FileUtils.deleteDirectory(new File(REPO_PATH));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't clean test repository filesystem", e);
        }
    }

}
