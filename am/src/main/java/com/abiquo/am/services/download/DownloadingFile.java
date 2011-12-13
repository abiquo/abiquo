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

package com.abiquo.am.services.download;

import static com.abiquo.am.services.filesystem.EnterpriseRepositoryFileSystem.releaseFile;
import static com.abiquo.am.services.filesystem.EnterpriseRepositoryFileSystem.takeFile;
import static com.abiquo.appliancemanager.exceptions.AMException.getErrorMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.data.AMRedisDao;
import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.notify.AMNotifier;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

/**
 * A file on an OVF package being download. Used as session object on the HttpResponseHandler.
 */
public class DownloadingFile implements AsyncHandler<Boolean>
{
    private final static Logger LOG = LoggerFactory.getLogger(DownloadingFile.class);

    /** Already read bytes. */
    protected volatile long currentBytes;

    /** All the expected bytes on the file. Based on the Content-Length header. */
    protected long expectedBytes;

    /** Target path where the file will be download (on some enterprise repository). */
    protected final String destinationPath;

    /** An open stream to the destinationPath. */
    protected final FileOutputStream osDestFile;

    /** The package is being cancelled. */
    protected volatile boolean isCancell = false;

    /** The file ends download. */
    protected volatile boolean isDone = false;

    protected volatile boolean isError = false;

    private final static Integer B_2_MB = 1048576;

    private final static Integer NOTIFY_EVERY_X_SEC = 1; // TODO CONFIGURABLE

    private long lastNotifyMs = 0;

    /** Source URL of the file being download. */
    public final String fileUrl;

    public final String erepoId;

    public final String ovfId;

    private final AMNotifier notifier;

    public DownloadingFile(final String fileUrl, final String destinationPath,
        final String enterpriseid, final String ovfId, final AMNotifier notifier)
    {
        this.fileUrl = fileUrl;
        this.erepoId = enterpriseid;
        this.ovfId = ovfId;
        this.destinationPath = destinationPath;
        this.osDestFile = takeFile(destinationPath);

        this.notifier = notifier;
    }

    @Override
    public STATE onStatusReceived(final HttpResponseStatus status) throws Exception
    {
        LOG.debug("GET status {} received {}", status.getStatusCode(), fileUrl);

        if (status.getStatusCode() / 200 != 1)
        {
            onError(getErrorMessage(status.getStatusCode(), fileUrl));
            return STATE.ABORT;
        }
        else
        {
            return STATE.CONTINUE;
        }
    }

    @Override
    public STATE onHeadersReceived(final HttpResponseHeaders h) throws Exception
    {
        try
        {
            expectedBytes =
                Long.parseLong(h.getHeaders().getFirstValue(HttpHeaders.CONTENT_LENGTH));
            LOG.debug("File {} will download %d Mb", destinationPath, expectedBytes / B_2_MB);
        }
        catch (Exception e)
        {
            onError("Content-Length header not present");
        }

        return STATE.CONTINUE;
    }

    @Override
    public STATE onBodyPartReceived(final HttpResponseBodyPart bodyPart) throws Exception
    {
        if (isCancell)
        {
            LOG.warn("Download aborted {}", fileUrl);
            return STATE.ABORT;
        }

        try
        {
            currentBytes += bodyPart.writeTo(osDestFile);
            updateProgress();

            return STATE.CONTINUE;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            onError(String.format("Can't flush content to %s\n%s", destinationPath, e.getMessage()));
            return STATE.ABORT;
        }
    }

    private void updateProgress()
    {
        final long now = System.currentTimeMillis();

        if ((now - lastNotifyMs) > 100 * NOTIFY_EVERY_X_SEC)
        {
            final Integer progress = (int) ((currentBytes * 100) / expectedBytes);
            LOG.trace("{} {}", ovfId, progress);

            AMRedisDao dao = AMRedisDao.getDao();
            dao.setDownloadProgress(erepoId, ovfId, progress);
            AMRedisDao.returnDao(dao);

            lastNotifyMs = now;
        }
    }

    /**
     * Will be invoked once the response has been fully read or a ResponseComplete exception has
     * been thrown.
     */
    @Override
    public Boolean onCompleted() throws Exception
    {
        if (!isError && !isCancell)
        {
            onDownload();
        }

        return Boolean.TRUE;
    }

    @Override
    public void onThrowable(final Throwable t)
    {
        // annoying AHC 1.0.0 bug ... internal NPE
        String error = t.getLocalizedMessage();
        error = error != null ? error : "Internal AHC error " + t.getClass().getCanonicalName();
        onError(error);
        t.printStackTrace();
    }

    /**
     * When the file ends the download. Remove the file mark and advice the OVF package some file
     * ends.
     */
    public void onDownload()
    {
        isDone = true;

        try
        {
            // osDestFile.flush();
            osDestFile.close();
        }
        catch (IOException e)
        {
            onError("Can not close file " + destinationPath);
            return;
        }

        releaseFile(destinationPath);

        // AMRedisDao dao = AMRedisDao.getDao();
        // dao.setDownloadProgress(erepoId, ovfId, 100);
        // // dao.setState(ovfId, OVFStatusEnumType.DOWNLOAD);
        // AMRedisDao.returnDao(dao);

        notifier.setTemplateStatus(erepoId, ovfId, TemplateStatusEnumType.DOWNLOAD);
    }

    /**
     * When the file (and its package) was cancelled. Remove the file.
     */
    public void onCancel(final boolean deleteFolder)
    {
        isCancell = true;

        releaseFile(destinationPath);
        File destiantion = new File(destinationPath);
        destiantion.delete();

        // AMRedisDao dao = AMRedisDao.getDao();
        // dao.setDownloadProgress(erepoId, ovfId, 0);
        // // dao.setState(ovfId, OVFStatusEnumType.NOT_DOWNLOAD);
        // AMRedisDao.returnDao(dao);

        if (deleteFolder)
        {
            ErepoFactory.getRepo(erepoId).deleteTemplate(ovfId);

            notifier.setTemplateStatus(erepoId, ovfId, TemplateStatusEnumType.NOT_DOWNLOAD);
        }

    }

    private void onError(final String msg)
    {
        isError = true;
        onCancel(false); // NO delete folder (to remain error cause ).
        notifier.setTemplateStatusError(erepoId, ovfId, msg);
    }

    public boolean done()
    {
        return isCancell || isDone || isError;
    }

}
