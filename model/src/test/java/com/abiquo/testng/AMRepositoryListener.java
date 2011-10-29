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
