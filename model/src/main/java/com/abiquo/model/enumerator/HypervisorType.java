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

package com.abiquo.model.enumerator;

import static com.abiquo.model.enumerator.DiskFormatType.HYPERV_COMPATIBLES;
import static com.abiquo.model.enumerator.DiskFormatType.KVM_COMPATIBLES;
import static com.abiquo.model.enumerator.DiskFormatType.VBOX_COMPATIBLES;
import static com.abiquo.model.enumerator.DiskFormatType.VDI_FLAT;
import static com.abiquo.model.enumerator.DiskFormatType.VHD_SPARSE;
import static com.abiquo.model.enumerator.DiskFormatType.VMDK_FLAT;
import static com.abiquo.model.enumerator.DiskFormatType.VMWARE_COMPATIBLES;
import static com.abiquo.model.enumerator.DiskFormatType.XENSERVER_COMPATIBLES;
import static com.abiquo.model.enumerator.DiskFormatType.XEN_COMPATIBLES;

import java.util.Set;

import com.google.common.collect.Sets;

public enum HypervisorType
{
    VBOX(8889, VDI_FLAT, VBOX_COMPATIBLES, "Virtual Box"), KVM(8889, VMDK_FLAT, KVM_COMPATIBLES,
        "KVM"), XEN_3(8889, VMDK_FLAT, XEN_COMPATIBLES, "Xen"), VMX_04(443, VMDK_FLAT,
        VMWARE_COMPATIBLES, VMDK_FLAT, "ESXi"), HYPERV_301(5985, VHD_SPARSE, HYPERV_COMPATIBLES,
        "Hyper-V"), XENSERVER(9363, VHD_SPARSE, XENSERVER_COMPATIBLES, "Xen Server");

    public final int defaultPort;

    public DiskFormatType baseFormat;

    public Set<DiskFormatType> compatibleFormats;

    public DiskFormatType instanceFormat;

    public String friendlyName;

    /* package */private final static int ID_MAX = 6;

    private HypervisorType(final int defaultPort, final DiskFormatType baseFormat,
        final Set<DiskFormatType> compatibleFormats, final DiskFormatType instanceFormat, final String friendlyName)
    {
        this.defaultPort = defaultPort;
        this.baseFormat = baseFormat;
        this.compatibleFormats = compatibleFormats;
        this.instanceFormat = instanceFormat;
        this.friendlyName = friendlyName;
    }

    private HypervisorType(final int defaultPort, final DiskFormatType baseFormat, final Set<DiskFormatType> compatibleFormats, final String friendlyName)
    {
        this.defaultPort = defaultPort;
        this.baseFormat = baseFormat;
        this.compatibleFormats = compatibleFormats;
        this.friendlyName = friendlyName;
    }

    public int id()
    {
        return ordinal() + 1;
    }

    public boolean isInstanceFormatFixed()
    {
        return instanceFormat != null;
    }

    public DiskFormatType getInstanceFormat()
    {
        return instanceFormat;
    }

    public boolean isCompatible(final DiskFormatType type)
    {
        return compatibleFormats.contains(type);
    }

    /**
     * Performs the intersection between the hypervisor's {@link #compatibleFormats} and the set of
     * {@link DiskFormatType} passed. Returns true if the intersection is not empty, then some of
     * the passed {@link DiskFormatType} are compatible with the hypervisor.
     * 
     * @param types The set of {@link DiskFormatType} to consider.
     * @return True if some of the passed {@link DiskFormatType} are compatible with the hypervisor.
     */
    public boolean isCompatible(final Set<DiskFormatType> types)
    {
        return !Sets.intersection(compatibleFormats, types).isEmpty();
    }

    public static HypervisorType fromId(final int id)
    {
        return values()[id - 1];
    }

    public static int getIdMax()
    {
        return ID_MAX;
    }

    /**
     * Create a new instance of the HypervisorType from its 'value'
     * 
     * @param v value
     * @return
     */
    public static HypervisorType fromValue(final String v)
    {
        return HypervisorType.valueOf(v.toUpperCase());
    }

    public String getValue()
    {
        return name();
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }

    public boolean requiresCredentials()
    {
        switch (this)
        {
            case KVM:
            case XEN_3:
                return false;
            default:
                return true;
        }
    }

    /**
     * @return build a pseudo-random MAC address depending on the HypervisorType.
     */
    public String getRandomMacAddress()
    {
        switch (this)
        {
            case VMX_04:
                String outMac = new String("00:50:56:");
                for (int i = 0; i < 3; i++)
                {
                    int a = (int) (Math.random() * 10000 % 255);

                    if (i == 0)
                    {
                        while (a >= 62)
                        {
                            a = (int) (Math.random() * 10000 % 255);
                        }
                    }

                    String tmpHex = Integer.toHexString(a);
                    if (tmpHex.length() == 1)
                    {
                        tmpHex = "0" + tmpHex;
                    }
                    outMac += tmpHex;

                    if (i < 2)
                    {
                        outMac += ":";
                    }
                }
                return outMac;
            case XEN_3:
                outMac = new String("00:16:3e:");
                for (int i = 0; i < 3; i++)
                {
                    int a = (int) (Math.random() * 10000 % 255);
                    String tmpHex = Integer.toHexString(a);
                    if (tmpHex.length() == 1)
                    {
                        tmpHex = "0" + tmpHex;
                    }
                    outMac += tmpHex;
                    if (i < 2)
                    {
                        outMac += ":";
                    }
                }
                return outMac;
            case KVM:
                outMac = new String("52:54:00:");
                for (int i = 0; i < 3; i++)
                {
                    int a = (int) (Math.random() * 10000 % 255);
                    String tmpHex = Integer.toHexString(a);
                    if (tmpHex.length() == 1)
                    {
                        tmpHex = "0" + tmpHex;
                    }
                    outMac += tmpHex;
                    if (i < 2)
                    {
                        outMac += ":";
                    }
                }
                return outMac;
            case VBOX:
                outMac = new String("080027");
                for (int i = 0; i < 3; i++)
                {
                    int a = (int) (Math.random() * 10000 % 255);
                    String tmpHex = Integer.toHexString(a);
                    if (tmpHex.length() == 1)
                    {
                        tmpHex = "0" + tmpHex;
                    }
                    outMac += tmpHex;
                }
                return outMac;
            case HYPERV_301:
                outMac = new String("00155D"); // Hyper-V prefix: 00:15:5D
                for (int i = 0; i < 3; i++)
                {
                    int a = (int) (Math.random() * 10000 % 255);
                    String tmpHex = Integer.toHexString(a);
                    if (tmpHex.length() == 1)
                    {
                        tmpHex = "0" + tmpHex;
                    }
                    outMac += tmpHex;
                }
                return outMac;
            case XENSERVER:
                // A user-specified MAC address should at least be a unicast MAC address (bit8=0),
                // and probably locally administered (bit7=0). Basically the 2nd hex digit should be
                // one
                // of: 2, 6, A or E.
                // The other bits in the 3 first octets doesn't matter.

                outMac = new String("fe:32:32:");
                for (int i = 0; i < 3; i++)
                {
                    int a = (int) (Math.random() * 10000 % 255);
                    String tmpHex = Integer.toHexString(a);
                    if (tmpHex.length() == 1)
                    {
                        tmpHex = "0" + tmpHex;
                    }
                    outMac += tmpHex;
                    if (i < 2)
                    {
                        outMac += ":";
                    }
                }
                return outMac;
            default:
                return "";
        }
    }

    public static Integer transformHypervisorTypeToInteger(final String hypervisorType)
    {
        if (hypervisorType.equalsIgnoreCase(HypervisorType.VBOX.getValue()))
        {
            return 1;
        }
        else if (hypervisorType.equalsIgnoreCase(HypervisorType.KVM.getValue()))
        {
            return 2;
        }
        else if (hypervisorType.equalsIgnoreCase(HypervisorType.XEN_3.getValue()))
        {
            return 3;
        }
        else if (hypervisorType.equalsIgnoreCase(HypervisorType.VMX_04.getValue()))
        {
            return 4;
        }
        else if (hypervisorType.equalsIgnoreCase(HypervisorType.HYPERV_301.getValue()))
        {
            return 5;
        }
        else if (hypervisorType.equalsIgnoreCase(HypervisorType.XENSERVER.getValue()))
        {
            return 6;
        }

        return null;

    }

    public static HypervisorType transformHypervisorTypeFromInteger(final int hypervisorType)
    {
        if (hypervisorType == 1)
        {
            return HypervisorType.VBOX;
        }
        else if (hypervisorType == 2)
        {
            return HypervisorType.KVM;
        }
        else if (hypervisorType == 3)
        {
            return HypervisorType.XEN_3;
        }
        else if (hypervisorType == 4)
        {
            return HypervisorType.VMX_04;
        }
        else if (hypervisorType == 5)
        {
            return HypervisorType.HYPERV_301;
        }
        else if (hypervisorType == 6)
        {
            return HypervisorType.XENSERVER;
        }

        return null;
    }
}
