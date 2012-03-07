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
package com.abiquo.model.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * utility methods to work with the iSCSI addressing model.
 * 
 * @author ibarrera
 */
public class AddressingUtils
{
    private static final String OCTET_PATTERN = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    private static final String IP_PATTERN = "(?:" + OCTET_PATTERN + "\\.){3}" + OCTET_PATTERN;

    private static final String PORT_PATTERN =
        "(?:6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3})";

    private static final String PORTAL_PATTERN = "(" + IP_PATTERN + "):(" + PORT_PATTERN + ")";

    private static final String IQN_PATTERN = "iqn\\.\\d{4}-\\d{2}\\.[^:]+(?::.+)?";

    private static final String LUN_PATTERN = "\\d+";

    private static final String PARTITION_PATTERN = "\\d+";

    private static final String PATH_PATTERN = "ip-(" + PORTAL_PATTERN + ")-iscsi-(" + IQN_PATTERN
        + ")-lun-(" + LUN_PATTERN + ")(?:-part(" + PARTITION_PATTERN + "))?";

    private static final String DEVICE_ID_PATTERN = "[a-zA-Z0-9]{32}";

    /**
     * Creates a new Path for the given parameters.
     * 
     * @param ip The portal IP.
     * @param port The portal Port.
     * @param iqn The IQN.
     * @param lun The LUN.
     * @return The Path.
     * @throws IllegalArgumentException If any of the arguments has an invalid format.
     */
    public static String toPath(final String ip, final int port, final String iqn, final int lun)
    {
        return toPath(ip + ":" + port, iqn, lun);
    }

    /**
     * Creates a new Path for the given parameters.
     * 
     * @param portal The portal.
     * @param iqn The IQN.
     * @param lun The LUN.
     * @return The Path.
     * @throws IllegalArgumentException If any of the arguments has an invalid format.
     */
    public static String toPath(final String portal, final String iqn, final int lun)
    {
        String path = String.format("ip-%s-iscsi-%s-lun-%d", portal, iqn, lun);

        if (!isValidPath(path))
        {
            throw new IllegalArgumentException("The provided parameters do not have a valid format.");
        }

        return path;
    }

    /**
     * Checks if the provided IP is valid.
     * 
     * @param ip The IP to check.
     * @return Boolean indicating if the specified IP is valid.
     */
    public static boolean isValidIP(final String ip)
    {
        return ip.matches(IP_PATTERN);
    }

    /**
     * Checks if the provided Port is valid.
     * 
     * @param port The Port to check.
     * @return Boolean indicating if the specified Port is valid.
     */
    public static boolean isValidPort(final String port)
    {
        return port.matches(PORT_PATTERN);
    }

    /**
     * Checks if the provided IQN is valid.
     * 
     * @param iqn The IQN to check.
     * @return Boolean indicating if the specified IQN is valid.
     */
    public static boolean isValidIQN(final String iqn)
    {
        return iqn.matches(IQN_PATTERN);
    }

    /**
     * Checks if the provided Portal is valid.
     * 
     * @param portal The Portal to check.
     * @return Boolean indicating if the specified Portal is valid.
     */
    public static boolean isValidPortal(final String portal)
    {
        return portal.matches(PORTAL_PATTERN);
    }

    /**
     * Checks if the provided Device Path is valid.
     * 
     * @param iscsiPath The Device Path to check.
     * @return Boolean indicating if the specified Device Path is valid.
     */
    public static boolean isValidPath(final String path)
    {
        return path.matches(PATH_PATTERN);
    }

    /**
     * Checks if the provided Device Id is valid.
     * 
     * @param deviceId The Device Id to check.
     * @return Boolean indicating if the specified Device Id is valid.
     */
    public static boolean isValidDeviceId(final String deviceId)
    {
        return deviceId.matches(DEVICE_ID_PATTERN);
    }

    /**
     * Gets the IP from the specified path.
     * 
     * @param path The path.
     * @return The IP.
     * @throws IllegalArgumentException If the provided path has not a valid format.
     */
    public static String getIP(final String path)
    {
        return getPart(path, 2);
    }

    /**
     * Gets the Port from the specified path.
     * 
     * @param path The path.
     * @return The Port.
     * @throws IllegalArgumentException If the provided path has not a valid format.
     */
    public static String getPort(final String path)
    {
        return getPart(path, 3);
    }

    /**
     * Gets the Portal from the specified path.
     * 
     * @param path The path.
     * @return The Portal.
     * @throws IllegalArgumentException If the provided path has not a valid format.
     */
    public static String getPortal(final String path)
    {
        return getPart(path, 1);
    }

    /**
     * Gets the Portal from the specified url.
     * 
     * @param url The url.
     * @return The Portal.
     */
    public static String getPortalFromURL(final String url)
    {
        Pattern p = Pattern.compile(".*//(" + PORTAL_PATTERN + ").*");
        Matcher m = p.matcher(url);

        if (!m.matches())
        {
            throw new IllegalArgumentException("The url has an invalid format");
        }

        return m.group(1);
    }

    /**
     * Gets the IQN from the specified path.
     * 
     * @param path The path.
     * @return The IQN.
     * @throws IllegalArgumentException If the provided path has not a valid format.
     */
    public static String getIQN(final String path)
    {
        return getPart(path, 4);
    }

    /**
     * TODO should return Integer (for ''toPath'' parameter type compatibility)
     * <p>
     * Gets the LUN from the specified path.
     * 
     * @param path The path.
     * @return The LUN.
     * @throws IllegalArgumentException If the provided path has not a valid format.
     */
    public static String getLUN(final String path)
    {
        return getPart(path, 5);
    }

    /**
     * Gets the Partition from the specified path.
     * 
     * @param path The path.
     * @return The Partition.
     * @throws IllegalArgumentException If the provided path has not a valid format.
     */
    public static String getPartition(final String path)
    {
        return getPart(path, 6);
    }

    /**
     * Gets the specified field of the provided path.
     * 
     * @param path The path to parse.
     * @param part The field to get.
     * @return The value of the field.
     */
    private static String getPart(final String path, final int part)
    {
        if (!isValidPath(path))
        {
            throw new IllegalArgumentException("The path has an invalid format");
        }

        Pattern p = Pattern.compile(PATH_PATTERN);
        Matcher m = p.matcher(path);

        if (!m.matches())
        {
            throw new IllegalArgumentException("The path has an invalid format");
        }

        return m.group(part);
    }

    /** Used to split the SSM response to obtain the target IQN and the visible LUN. */
    private final static String LUN_SEPARATOR = "-lun-";

    /**
     * Gets the IQN fragment of the SSM response.
     * 
     * @param mapping, the the response of the SSM including the volume IQN and the LUN where the
     *            initiator can reach the volume.
     * @return the IQN part of the mapping
     */
    public static String getIqnFromSSMMappingResponse(final String mapping)
    {
        final Integer lunSeparator = mapping.indexOf(LUN_SEPARATOR);

        if (lunSeparator == -1)
        {
            final String cause =
                String.format("Malformed initiator mapping (expected {iqn}-lun-{lunid}) [%s]",
                    mapping);
            throw new IllegalArgumentException(cause);
        }

        final String iqn = mapping.substring(0, lunSeparator);
        return iqn;
    }

    /**
     * Gets the LUN fragment of the SSM response.
     * 
     * @param mapping, the the response of the SSM including the volume IQN and the LUN where the
     *            initiator can reach the volume.
     * @return the LUN part of the mapping
     */
    public static Integer getLunFromSSMMappingResponse(final String mapping)
    {

        final Integer lunSeparator = mapping.indexOf(LUN_SEPARATOR);

        if (lunSeparator == -1)
        {
            final String cause =
                String.format("Malformed initiator mapping (expected {iqn}-lun-{lunid}) [%s]",
                    mapping);
            throw new IllegalArgumentException(cause);
        }

        String lun = mapping.substring(lunSeparator + LUN_SEPARATOR.length());

        try
        {
            return Integer.parseInt(lun);
        }
        catch (Exception e)
        {
            final String cause =
                String.format("Malformed initiator mapping LUN (expected Integer lunid) [%s]", lun);
            throw new IllegalArgumentException(cause);
        }
    }
}
