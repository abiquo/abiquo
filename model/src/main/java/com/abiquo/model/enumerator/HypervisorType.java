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

import org.apache.commons.lang.ArrayUtils;

public enum HypervisorType
{
    VBOX(8889, VDI_FLAT, VBOX_COMPATIBLES), KVM(8889, VMDK_FLAT, KVM_COMPATIBLES), XEN_3(8889,
        VMDK_FLAT, XEN_COMPATIBLES), VMX_04(443, VMDK_FLAT, VMWARE_COMPATIBLES), HYPERV_301(5985,
        VHD_SPARSE, HYPERV_COMPATIBLES), XENSERVER(9363, VHD_SPARSE, XENSERVER_COMPATIBLES);

    public final int defaultPort;

    public DiskFormatType baseFormat;

    public DiskFormatType[] compatibilityTable;

    private HypervisorType(int defaultPort, DiskFormatType baseFormat,
        DiskFormatType[] compatibilityTable)
    {
        this.defaultPort = defaultPort;
        this.baseFormat = baseFormat;
        this.compatibilityTable = compatibilityTable;
    }

    public int id()
    {
        return ordinal() + 1;
    }

    public boolean isCompatible(DiskFormatType type)
    {
        return ArrayUtils.contains(compatibilityTable, type);
    }

    public static HypervisorType fromId(int id)
    {
        return values()[id - 1];
    }

    /**
     * Create a new instance of the HypervisorType from its 'value'
     * 
     * @param v value
     * @return
     */
    public static HypervisorType fromValue(String v)
    {
        return HypervisorType.valueOf(v.toUpperCase().replace("-", "_"));
    }

    public String getValue()
    {
        return name().toLowerCase().replace("_", "-");
    }
    
    public boolean requiresCredentials()
    {
    	switch (this) {
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
}
