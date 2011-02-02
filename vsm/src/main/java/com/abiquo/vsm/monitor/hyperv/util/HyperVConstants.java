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
package com.abiquo.vsm.monitor.hyperv.util;

/**
 * Hyper-v constants.
 * 
 * @author ibarrera
 */
public class HyperVConstants
{
    /** The virtualization namespace of the WMI API. */
    public static final String VIRTUALIZATION_NS = "root\\virtualization";

    /** The CIm namespace of the WMI API. */
    public static final String CIM_NS = "root\\cimv2";

    /** The POWER_ON */
    public static final int POWER_ON = 2;

    public static final int POWER_OFF = 3;

    public static final int REBOOT = 10;

    public static final int RESET = 11;

    public static final int PAUSED = 32768;

    public static final int SUSPENDED = 32769;
}
