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

import java.net.MalformedURLException;
import java.net.URL;

import com.abiquo.appliancemanager.exceptions.DownloadException;

public class OVFPackageConventions
{
    public final static String OVF_FILE_EXTENSION = ".ovf";

    public final static String OVF_LOCATION_PREFIX = "http://";

    public final static String OVF_LOCATION_POSTFIX = ".ovf";

    public final static String OVF_BUNDLE_PATH_IDENTIFIER = "-snapshot-";

    public final static String OVF_STATUS_DOWNLOADING_MARK = ".deploing";

    public final static String OVF_STATUS_ERROR_MARK = "deploy.error";

    public final static String END_OF_FILE_MARK = "\\Z";

    public final static String FORMATS_PATH = "formats"; // TODO on EnterpriseRepositoryHandler

    public final static String OVF_BUNDLE_IMPORTED_PREFIX = "http://bundle-imported/";

    /**
     * XXX document me !!! return the last segment of the OVF location path. (ends with ''.ovf'')
     */
    public String getOVFPackageName(final String ovfid)
    {
        assert isValidOVFLocation(ovfid);

        return ovfid.substring(ovfid.lastIndexOf('/') + 1);
    }

    boolean isValidOVFLocation(final String ovfid)
    {
        return ovfid.startsWith(OVF_LOCATION_PREFIX) && ovfid.endsWith(OVF_LOCATION_POSTFIX);
    }

    boolean isImportedBundleOvfId(final String ovfId)
    {
        return ovfId.startsWith(OVF_BUNDLE_IMPORTED_PREFIX);
    }

    boolean isBundleOvfId(final String ovfId)
    {
        return ovfId.contains(OVF_BUNDLE_PATH_IDENTIFIER);
    }

    String createBundleOvfId(final String ovfId, final String snapshot)
    {
        final String masterPre = ovfId.substring(0, ovfId.lastIndexOf('/') + 1);
        final String masterPost = ovfId.substring(ovfId.lastIndexOf('/') + 1, ovfId.length());

        final String bundleOvfId = masterPre + snapshot + OVF_BUNDLE_PATH_IDENTIFIER + masterPost;

        return bundleOvfId;
    }

    String getBundleMasterOvfId(final String bundleOvfId)
    {
        final String masterPre = bundleOvfId.substring(0, bundleOvfId.lastIndexOf('/') + 1);
        final String masterPost =
            bundleOvfId.substring(bundleOvfId.indexOf(OVF_BUNDLE_PATH_IDENTIFIER)
                + OVF_BUNDLE_PATH_IDENTIFIER.length(), bundleOvfId.length());

        return masterPre + masterPost;
    }

    String getBundleSnapshot(final String bundleOvfId)
    {
        final String snapshot =
            bundleOvfId.substring(bundleOvfId.lastIndexOf('/') + 1,
                bundleOvfId.indexOf(OVF_BUNDLE_PATH_IDENTIFIER));

        return snapshot;
    }

    String codifyBundleOVFId(final String ovfid, final String snapshotMark, final String packageName)
    {
        return ovfid.substring(0, ovfid.lastIndexOf('/') + 1).concat(snapshotMark)
            .concat(packageName);
    }

    String cleanOVFurlOnOldRepo(String ovfid)
    {
        ovfid = ovfid.replaceFirst("http://http", "http://");
        ovfid = ovfid.replaceFirst(".coms3direct", ".com/s3direct");

        return ovfid;
    }

    String cleanOVFurlForOldRepo(String ovfid)
    {
        if (ovfid.startsWith("http://http"))
        {
            return ovfid;
        }

        ovfid = ovfid.replaceFirst("http://", "http://http");
        ovfid = ovfid.replaceFirst(".com/s3direct", ".coms3direct");

        return ovfid;
    }

    public String getRelativePackagePath(final String ovfid)
    {
        // TODO check OVFid do not contains any query param (envelope.ovf?queryparam=XXX)
        assert isValidOVFLocation(ovfid);

        String path = ovfid.substring(OVF_LOCATION_PREFIX.length(), ovfid.lastIndexOf('/') + 1);

        return customEncode(path);
    }

    String getRelativeOVFPath(final String ovfId)
    {
        assert ovfId.startsWith(OVF_LOCATION_PREFIX);

        String path = ovfId.substring(OVF_LOCATION_PREFIX.length());

        return customEncode(path);
    }

    /**
     * Encode
     * <ul>
     * <li>:</li>
     * <li>?</li>
     * <li>&</li>
     * </ul>
     */
    public static String customEncode(String path)
    {
        path = path.replaceAll(":", "/abiport");
        path = path.replaceAll("\\?", "/abiintermark");
        path = path.replaceAll("&", "/abiandper");

        // path = path.replaceAll(":", "%3A");
        // path = path.replaceAll("\\?", "%3F");
        // path = path.replaceAll("&", "%26");

        return path;
    }

    /**
     * Decode
     * <ul>
     * <li>:</li>
     * <li>?</li>
     * <li>&</li>
     * </ul>
     */
    public String customDencode(String path)
    {
        path = path.replaceAll("/abiport", ":");
        path = path.replaceAll("/abiintermark", "\\?");
        path = path.replaceAll("/abiandper", "&");

        // path = path.replaceAll("%3A", ":");
        // path = path.replaceAll("%3F", "\\?");
        // path = path.replaceAll("%26", "&");

        return path;
    }

    String getMasterOVFPackage(String ovfIdSnapshot)
    {

        // TODO convention
        final String masterOvf =
            ovfIdSnapshot.substring(0, ovfIdSnapshot.lastIndexOf('/') + 1)
                + ovfIdSnapshot.substring(ovfIdSnapshot.lastIndexOf("-snapshot-")
                    + "-snapshot-".length());

        return masterOvf;
    }

    /**
     * Gets an URL from the hRef attribute on a File's (References section). Try to interpret the
     * hRef attribute as an absolute URL (like http://some.where.com/file.vmdk), and if it fails try
     * to interpret the hRef as OVF package URI relative.
     * 
     * @param relativeFilePath, the value on the hRef attribute for the File.
     * @param ovfId, the OVF Package identifier (and locator).
     * @throws DownloadException, it the URL can not be created.
     */
    URL getFileUrl(String relativeFilePath, String ovfId) throws DownloadException
    {
        URL fileURL;

        try
        {
            fileURL = new URL(relativeFilePath);
        }
        catch (MalformedURLException e1)
        {
            // its a relative path
            if (e1.getMessage().startsWith("no protocol"))
            {
                try
                {
                    String packageURL = ovfId.substring(0, ovfId.lastIndexOf('/'));
                    fileURL = new URL(packageURL + '/' + relativeFilePath);
                }
                catch (MalformedURLException e)
                {
                    final String msg = "Invalid file reference " + ovfId + '/' + relativeFilePath;
                    throw new DownloadException(msg, e1);
                }
            }
            else
            {
                final String msg = "Invalid file reference " + relativeFilePath;
                throw new DownloadException(msg, e1);
            }
        }

        return fileURL;
    }
}
