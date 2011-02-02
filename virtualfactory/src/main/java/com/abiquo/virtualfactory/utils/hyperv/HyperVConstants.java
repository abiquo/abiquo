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
package com.abiquo.virtualfactory.utils.hyperv;

/**
 * Hyper-v constants
 * 
 * @author pnavarro
 */
public class HyperVConstants
{

    public static final String WBEM_LOCATOR = "WbemScripting.SWbemLocator";

    public static final String HYPER_TYPE = "hyperv-301";

    public static final String VIRTUALIZATION_NS = "root\\virtualization";

    public static final String CIM_NS = "root\\cimv2";

    public static final String WMI_NS = "root\\wmi";

    public static final int POWER_ON = 2;

    public static final int POWER_OFF = 3;

    public static final int REBOOT = 10;

    public static final int RESET = 11;

    public static final int PAUSED = 32768;

    public static final int SUSPENDED = 32769;

    public static final String DISKETTECONTROLLER = null;

    public static final String DISKETTEDRIVE = "MICROSOFT SYNTHETIC DISKETTE DRIVE";

    public static final String PARALLELSCSIHBA = "MICROSOFT SYNTHETIC SCSI CONTROLLER";

    public static final String IDECONTROLLER = "MICROSOFT EMULATED IDE CONTROLLER";

    public static final String DISKSYNTHETIC = "MICROSOFT SYNTHETIC DISK DRIVE";

    public static final String DISKPHYSICAL = "MICROSOFT PHYSICAL DISK DRIVE";

    public static final String DVDPHYSICAL = "MICROSOFT PHYSICAL DVD DRIVE";

    public static final String DVDSYNTHETIC = "MICROSOFT SYNTHETIC DVD DRIVE";

    public static final String CDROMPHYSICAL = "MICROSOFT PHYSICAL CD DRIVE";

    public static final String CDROMSYNTHETIC = "MICROSOFT SYNTHETIC CD DRIVE";

    public static final String ETHERNETSYNTHETIC = "MICROSOFT SYNTHETIC ETHERNET PORT";

    // LOGICAL DRIVE
    public static final String DVDLOGICAL = "MICROSOFT VIRTUAL CD/DVD DISK";

    public static final String ISOIMAGE = "MICROSOFT ISO IMAGE";

    public static final String VHD = "MICROSOFT VIRTUAL HARD DISK";

    public static final String DVD = "MICROSOFT VIRTUAL DVD DISK";

    public static final String VFD = "MICROSOFT VIRTUAL FLOPPY DISK";

    public static final String VIDEOSYNTHETIC = "MICROSOFT SYNTHETIC DISPLAY CONTROLLER";

    public static final int OTHER = 1;

    public static final int COMPUTERSYSTEM = 2;

    public static final int PROCESSOR = 3;

    public static final int MEMORY = 4;

    public static final int IDECONTROLLERRTYPE = 5;

    public static final int PARALLELSCSIHBARTYPE = 6;

    public static final int FCHBA = 7;

    public static final int ISCSIHBA = 8;

    public static final int IBHCA = 9;

    public static final int ETHERNETADAPTER = 10;

    public static final int OTHERNETWORKADAPTER = 11;

    public static final int IOSLOT = 12;

    public static final int IODEVICE = 13;

    public static final int FLOPPYDRIVE = 14;

    public static final int CDDRIVE = 15;

    public static final int DVDDRIVE = 16;

    public static final int SERIALPORT = 17;

    public static final int PARALLELPORT = 18;

    public static final int USBCONTROLLER = 19;

    public static final int GRAPHICSCONTROLLER = 20;

    public static final int STORAGEEXTENT = 21;

    public static final int DISK = 22;

    public static final int TAPE = 23;

    public static final int OTHERSTORAGEDEVICE = 24;

    public static final int FIREWIRECONTROLLER = 25;

    public static final int PARTITIONABLEUNIT = 26;

    public static final int BASEPARTITIONABLEUNIT = 27;

    public static final int POWERSUPPLY = 28;

    public static final int COOLINGDEVICE = 29;

    public static String EXTERNAL_NETWORK_NAME = "External Network";

    public static final String SEND_TARGETS_COMMAND = "cmd /c iscsicli QAddTargetPortal ";

    public static final String LOGIN_COMMAND = "cmd /c iscsicli qlogintarget ";

    public static final String LIST_TARGETS = "cmd /c iscsicli ListTargets";

    public static final String LOGOUT_COMMAND = "cmd /c iscsicli logouttarget ";

}
