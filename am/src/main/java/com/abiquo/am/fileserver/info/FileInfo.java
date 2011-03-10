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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

/**
 * A file on an OVF package being download. Used as session object on the HttpResponseHandler.
 */
public class FileInfo implements AsyncHandler<Boolean>
{
    @Override
    public STATE onStatusReceived(HttpResponseStatus status) throws Exception
    {
        if (status.getStatusCode() / 200 != 1)
        {
            String error;

            if (status.getStatusCode() == 401)
            {
                error =
                    String.format("[Unauthorized] You might not have permissions to read "
                        + "the file or folder at the following location: %s", fileUrl);
            }
            else if (status.getStatusCode() == 403)
            {
                error =
                    String.format("[Forbidden] You might not have permissions to read "
                        + "the file or folder at the following location: %s", fileUrl);
            }
            else if (status.getStatusCode() == 404)
            {
                error =
                    String.format(
                        "[Not Found] The file or folder at the location: %s does not exist",
                        fileUrl);
            }
            else
            // generic error status message
            {
                error =
                    String.format("%d - [%s] at the location : %s", status.getStatusCode(),
                        status.getStatusText(), status.getUrl().toString());
            }

            getPackage().onError(error);

            return STATE.ABORT;
        }
        else
        {
            return STATE.CONTINUE;
        }
    }

    @Override
    public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception
    {
        // Headers headers = h.getHeaders();
        // // The headers have been read
        // // If you don't want to read the body, or stop processing the response

        return STATE.CONTINUE;// STATE..ABORT;
    }

    // @Override
    @Override
    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception
    {
        try
        {
            byte[] bodyBytes = bodyPart.getBodyPartBytes();

            ByteBuffer bb = ByteBuffer.wrap(bodyBytes);

            int count = 0;
            while ((count += osDestFile.write(bb)) < bodyBytes.length)
                ;

            // .write(bodyBytes);
            currentBytes += bodyBytes.length;

            return STATE.CONTINUE;
        }
        catch (IOException e)
        {
            getPackage().onError(e.getMessage());
            return STATE.ABORT;
            // throw e;
        }
    }

    @Override
    public Boolean onCompleted() throws Exception
    {

        onDownload();
        // Will be invoked once the response has been fully read or a ResponseComplete exception
        // has been thrown.

        return true;
    }

    @Override
    public void onThrowable(Throwable t)
    {
        getPackage().onError(t.getMessage());
    }

    public Future<Boolean> execution;

    private final static Logger logger = LoggerFactory.getLogger(FileInfo.class);

    /** The OVF package this file belongs to. */
    protected volatile PackageInfo ovfPackage;

    /** Already read bytes. */
    protected volatile long currentBytes;

    /** All the expected bytes on the file. */
    protected volatile long expectedBytes = 0;

    /** Source URL of the file being download. */
    public volatile String fileUrl;

    /** Target path where the file will be download (on some enterprise repository). */
    protected volatile String destinationPath;

    /** An open stream to the destinationPath. */
    public volatile FileChannel osDestFile;

    /** The package is being cancelled. */
    protected volatile boolean isCancell = false;

    /** The file ends download. */
    protected volatile boolean isDone = false;

    /** The same file is being download by another OVF package (see ''getCurrentBytes'') */
    // TODO no so public
    public volatile boolean isAlreadyBeingDownload = false;

    /**
     * A prefix to add at ''destinationPath'' to indicate the file is being download (see
     * ''takeFile'' and ''releaseFile'').
     */
    private final static String FILE_MARK = ".download";

    public FileInfo(String fileUrl, long expectedBytes, String destinationPath)
    {
        this.expectedBytes = expectedBytes;
        this.fileUrl = fileUrl;
        this.destinationPath = destinationPath;

        try
        {
            this.osDestFile = takeFile(destinationPath);
        }
        catch (FileNotFoundException e)
        {
            this.isDone = true;

            final String error =
                String.format("Can not create the destination file at [%s] for file [%s]",
                    destinationPath, fileUrl);

            if (ovfPackage != null)
            {
                ovfPackage.onError(error);
            }

            logger.error(error);
        }

        if (this.osDestFile == null)
        {
            this.isAlreadyBeingDownload = true;
        }
    }

    /**
     * Get the current bytes downloaded from the source. It the file ''isAreadyBeingDownload'' get
     * current size of the ''destinationFile''. Also check if the file is already being download
     * (perhaps the other OVF has been cancelled).
     * 
     * @return the number of bytes of the ''destinationPath''.
     */
    public synchronized long getCurrentBytes()
    {
        if (this.isAlreadyBeingDownload)
        {
            final File destFile = new File(destinationPath);

            if (!destFile.exists()) // it was canceled
            {
                // startDownload();
                isDone = false;
                isAlreadyBeingDownload = false;

                ovfPackage.onError("File was removed :" + destinationPath);

                return -1; // TODO
            }
            else
            // being download by another package
            {
                final long fileSize = destFile.length();

                // the other package ends download these file
                if (fileSize >= expectedBytes)
                {
                    isDone = true;
                    onDownload();
                }

                return fileSize;
            }
        }
        else
        // being download
        {
            return currentBytes;
        }
    }

    // /**
    // * If an ''isAlreadyBeingDownload'' was cancelled then the file is started again.
    // */
    // private void startDownload()
    // {
    //
    // try
    // {
    // this.osDestFile = takeFile(destinationPath);
    // // XXX can not occurs if(this.osDestFile == null)
    // }
    // catch (FileNotFoundException e)
    // {
    // e.printStackTrace();
    // }
    //
    // try
    // {
    // HttpClient.getInstance().addDownload(this);
    // }
    // catch (DownloadException e)
    // {
    // ovfPackage.onError(e.toString());
    // }
    //
    // isDone = false;
    // isAlreadyBeingDownload = false;
    //
    // }

    /**
     * Set the OVF package it belongs to.
     */
    public void setPackage(PackageInfo ovfPackage)
    {
        this.ovfPackage = ovfPackage;
    }

    /**
     * Gets the OVF package it belongs to.
     */
    public PackageInfo getPackage()
    {
        return ovfPackage;
    }

    /**
     * When the file ends the download. Remove the file mark and advice the OVF package some file
     * ends.
     */
    public void onDownload()
    {
        isDone = true;

        if (!isAlreadyBeingDownload)
        {
            try
            {
                // osDestFile.flush();
                osDestFile.close();
            }
            catch (IOException e)
            {
                logger.error("Can not close file [{}]", destinationPath);
            }

            releaseFile(destinationPath);
        }

        isAlreadyBeingDownload = false;

        ovfPackage.onFileEnd();
    }

    /**
     * When the file (and its package) was cancelled. Remove the file.
     */
    public void onCancel()
    {
        isDone = true;

        if (!isAlreadyBeingDownload)
        {
            releaseFile(destinationPath);

            File destiantion = new File(destinationPath);
            destiantion.delete();

        }
    }

    /**
     * Check the target file is not being download by another package.
     * 
     * @return null if the destination file is being download by another package.
     * @throws FileNotFoundException
     */
    public static synchronized FileChannel takeFile(final String destinationPath)
        throws FileNotFoundException
    {
        File destination = new File(destinationPath);
        File destinationMark = new File(destinationPath + FILE_MARK);

        if (destination.exists())
        {
            if (destinationMark.exists())
            {
                final String msg =
                    String.format("The destination file [%s] is being download by another package",
                        destinationPath);
                logger.warn(msg);

                return null;
            }
        }

        try
        {
            File parent = destinationMark.getParentFile();
            if (!parent.exists())
            {
                if (!parent.mkdirs())
                {
                    logger.error("Can't create file folder at " + parent.getAbsolutePath());
                }
            }

            destinationMark.createNewFile();
        }
        catch (IOException e)
        {
            logger.error(String.format("Can't create the destination mark for [%s]",
                destinationPath));
            e.printStackTrace();
        }

        return new FileOutputStream(destination).getChannel();
    }

    /**
     * Ends a file download transaction.
     */
    public static synchronized void releaseFile(final String destinationPath)
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
            logger.error(msg);
        }
    }
}
