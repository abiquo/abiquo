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

package com.abiquo.abiserver.abicloudws;

import javax.xml.namespace.QName;

public class AbiCloudConstants
{

    // The resource URI of the VirtualAppliance Resource
    public static final String RESOURCE_URI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    // machine State Qname
    public final static QName machineStateQname = new QName("machineStateAction");

    // Remote Desktop Port
    public final static QName remoteDesktopPortQname = new QName("remoteDesktopPort");

    // Remote Desktop Password
    public final static QName remoteDesktopPasswordQname = new QName("remoteDesktopPassword");

    /** User QNAME */
    public final static QName ADMIN_USER_QNAME = new QName("adminUser");

    /** Password QNAME */
    public final static QName ADMIN_USER_PASSWORD_QNAME = new QName("adminPassword");

    /** Datastore QNAME */
    public final static QName DATASTORE_QNAME = new QName("targetDatastore");

    // PowerUp action
    public final static String POWERUP_ACTION = "PowerUp";

    // Powerdown action
    public final static String POWERDOWN_ACTION = "PowerOff";

    // Pause action
    public final static String PAUSE_ACTION = "Pause";

    // Resume action
    public final static String RESUME_ACTION = "Resume";

    // Undeploy action
    public final static String UNDEPLOY_ACTION = "Undeploy";

    public final static String ERROR_PREFIX = "ABI-S";

    /** The Ws-management action to check the health check of a virtual system */
    public static final String CHECK_VIRTUALSYSTEM_ACTION =
        "http://abiquo.com/healthcheck/checkVirtualSystem";

    /**
     * VLAN standard limit
     */
    public static final Integer VLAN_MAX = 4094;

    /** The remote desktop min port **/
    public final static int MIN_REMOTE_DESKTOP_PORT = 5900;

    public final static int MAX_REMOTE_DESKTOP_PORT = 65534;

    /** The Ws-management action to check the health check of a virtual system */
    public static final String BUNDLE_VIRTUALAPPLIANCE =
        "http://abiquo.com/VirtualAppliance/bundle";

    /** The Ws-management action to add a virtual system */
    public static final String ADD_VIRTUALSYSTEM_ACTION = "http://abiquo.com/virtualSystem/add";

    /** The Ws-management action to remove a virtual system */
    public static final String REMOVE_VIRTUALSYSTEM_ACTION =
        "http://abiquo.com/virtualSystem/remove";
}
