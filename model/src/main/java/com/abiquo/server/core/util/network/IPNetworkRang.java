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

package com.abiquo.server.core.util.network;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.abiquo.server.core.enumerator.HypervisorType;

/**
 * This class provides a Rang of IP's available for a given DataCenter.
 * 
 * @author abiquo
 */
public class IPNetworkRang
{
    /**
     * From a given subnet mask, return the number of the nodes we can have.
     * 
     * @param mask mask of the subnet.
     * @return number of the nodes.
     */
    public static Integer masktoNumberOfNodes(Integer mask)
    {
        return Integer
            .parseInt(((Integer) ((Double) Math.pow(2, 32 - mask)).intValue()).toString());
    }

    /**
     * From a given number of nodes, return the mask
     */
    public static Integer numberOfNodesToMask(Integer numOfNodes)
    {
        return Integer.parseInt(((Integer) (32 - ((Double) (Math.log(numOfNodes) / Math.log(2)))
            .intValue())).toString());
    }

    /**
     * Moves integer mask (ej. 24) into IP mask format (ej. "255.255.255.0");
     * 
     * @param mask integer mask
     * @return an IP address containing a the mask
     * @throws InvalidIPAddressException
     */
    public static IPAddress transformIntegerMaskToIPMask(Integer mask)
    {
        switch (mask)
        {
            case 0:
                return IPAddress.newIPAddress("0.0.0.0");
            case 1:
                return IPAddress.newIPAddress("128.0.0.0");
            case 2:
                return IPAddress.newIPAddress("192.0.0.0");
            case 3:
                return IPAddress.newIPAddress("224.0.0.0");
            case 4:
                return IPAddress.newIPAddress("240.0.0.0");
            case 5:
                return IPAddress.newIPAddress("248.0.0.0");
            case 6:
                return IPAddress.newIPAddress("252.0.0.0");
            case 7:
                return IPAddress.newIPAddress("254.0.0.0");
            case 8:
                return IPAddress.newIPAddress("255.0.0.0");

            case 9:
                return IPAddress.newIPAddress("255.128.0.0");
            case 10:
                return IPAddress.newIPAddress("255.192.0.0");
            case 11:
                return IPAddress.newIPAddress("255.224.0.0");
            case 12:
                return IPAddress.newIPAddress("255.240.0.0");
            case 13:
                return IPAddress.newIPAddress("255.248.0.0");
            case 14:
                return IPAddress.newIPAddress("255.252.0.0");
            case 15:
                return IPAddress.newIPAddress("255.254.0.0");
            case 16:
                return IPAddress.newIPAddress("255.255.0.0");

            case 17:
                return IPAddress.newIPAddress("255.255.128.0");
            case 18:
                return IPAddress.newIPAddress("255.255.192.0");
            case 19:
                return IPAddress.newIPAddress("255.255.224.0");
            case 20:
                return IPAddress.newIPAddress("255.255.240.0");
            case 21:
                return IPAddress.newIPAddress("255.255.248.0");
            case 22:
                return IPAddress.newIPAddress("255.255.252.0");
            case 23:
                return IPAddress.newIPAddress("255.255.254.0");
            case 24:
                return IPAddress.newIPAddress("255.255.255.0");

            case 25:
                return IPAddress.newIPAddress("255.255.255.128");
            case 26:
                return IPAddress.newIPAddress("255.255.255.192");
            case 27:
                return IPAddress.newIPAddress("255.255.255.224");
            case 28:
                return IPAddress.newIPAddress("255.255.255.240");
            case 29:
                return IPAddress.newIPAddress("255.255.255.248");
            case 30:
                return IPAddress.newIPAddress("255.255.255.252");
            case 31:
                return IPAddress.newIPAddress("255.255.255.254");
            default:
                return IPAddress.newIPAddress("255.255.255.255");

        }
    }

    /**
     * For a given network Address and mask, return the list of all IP address that will compose the
     * network.
     * 
     * @param networkAddress address that defines the network.
     * @param mask mask of the network in the Integer way.
     * @return the list of IPAddress.
     * @throws InvalidIPAddressException
     */
    public static List<IPAddress> calculateWholeRange(IPAddress networkAddress, Integer mask)
    {
        List<IPAddress> wholeRange = new ArrayList<IPAddress>();

        // Define the list of IPs that will compose the network.
        Integer numberOfNodes = IPNetworkRang.masktoNumberOfNodes(mask);
        IPAddress firstIP = networkAddress;
        IPAddress lastIP = IPNetworkRang.lastIPAddressWithNumNodes(firstIP, numberOfNodes);
        IPAddress nextIP = firstIP.nextIPAddress();
        while (nextIP != null && !nextIP.equals(lastIP))
        {
            wholeRange.add(nextIP);
            nextIP = nextIP.nextIPAddress();
        }

        return wholeRange;
    }

    /**
     * Moves IP mask (ej. "255.255.255.0") into integer mask format (ej. 24);
     * 
     * @param mask integer mask
     * @return an IP address containing a the mask
     */
    public static Integer transformIPMaskToIntegerMask(IPAddress ipAddress)
    {
        if (ipAddress.toString().equalsIgnoreCase("0.0.0.0"))
        {
            return 0;
        }
        else if (ipAddress.toString().equalsIgnoreCase("128.0.0.0"))
        {
            return 1;
        }
        else if (ipAddress.toString().equalsIgnoreCase("192.0.0.0"))
        {
            return 2;
        }
        else if (ipAddress.toString().equalsIgnoreCase("224.0.0.0"))
        {
            return 3;
        }
        else if (ipAddress.toString().equalsIgnoreCase("240.0.0.0"))
        {
            return 4;
        }
        else if (ipAddress.toString().equalsIgnoreCase("248.0.0.0"))
        {
            return 5;
        }
        else if (ipAddress.toString().equalsIgnoreCase("252.0.0.0"))
        {
            return 6;
        }
        else if (ipAddress.toString().equalsIgnoreCase("254.0.0.0"))
        {
            return 7;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.0.0.0"))
        {
            return 8;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.128.0.0"))
        {
            return 9;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.192.0.0"))
        {
            return 10;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.224.0.0"))
        {
            return 11;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.240.0.0"))
        {
            return 12;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.248.0.0"))
        {
            return 13;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.252.0.0"))
        {
            return 14;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.254.0.0"))
        {
            return 15;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.0.0"))
        {
            return 16;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.128.0"))
        {
            return 17;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.192.0"))
        {
            return 18;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.224.0"))
        {
            return 19;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.240.0"))
        {
            return 20;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.248.0"))
        {
            return 21;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.252.0"))
        {
            return 22;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.254.0"))
        {
            return 23;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.0"))
        {
            return 24;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.128"))
        {
            return 25;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.192"))
        {
            return 26;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.224"))
        {
            return 27;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.240"))
        {
            return 28;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.248"))
        {
            return 29;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.252"))
        {
            return 30;
        }
        else if (ipAddress.toString().equalsIgnoreCase("255.255.255.254"))
        {
            return 31;
        }
        else
            return 32;

    }

    /**
     * Calculate the last IP address of the range for a given network address
     * 
     * @param networkAddress network address
     * @param numberOfNodes
     * @return
     * @throws InvalidIPAddressException
     */
    public static IPAddress lastIPAddressWithNumNodes(IPAddress networkAddress,
        Integer numberOfNodes)
    {
        // if we want 0 numberOfNodes it is an error, we return null
        if (numberOfNodes == 0)
        {
            return null;
        }

        // Please note if number of nodes is 1, we return the same IP
        // (if we have a first ip 10.0.0.1 and we want to know the last IP
        // with number of nodes = 1, it is the same IP... isn't it?)
        // So, the first we do is subtract 1
        Long numberOfNodesRemain = new Long(numberOfNodes);
        numberOfNodesRemain--;

        if (numberOfNodesRemain < new Long("4294967296"))
        {
            StringTokenizer tokenizer = new StringTokenizer(networkAddress.toString(), ".");

            // Thanks to the static method we can be sure we have 4 tokens
            // and they live between 0 and 256
            int tokenOne = Integer.parseInt(tokenizer.nextToken());
            int tokenTwo = Integer.parseInt(tokenizer.nextToken());
            int tokenThree = Integer.parseInt(tokenizer.nextToken());
            int tokenFour = Integer.parseInt(tokenizer.nextToken());

            while (numberOfNodesRemain > 0)
            {
                if (numberOfNodesRemain > 16777216)
                {
                    tokenOne = (tokenOne + 1) % 256;
                    numberOfNodesRemain -= 16777216;
                }
                else if (numberOfNodesRemain > 65536)
                {
                    tokenTwo = (tokenTwo + 1) % 256;
                    if (tokenTwo == 0)
                    {
                        tokenOne = (tokenOne + 1) % 256;
                    }
                    numberOfNodesRemain -= 65536;
                }
                else if (numberOfNodesRemain > 256)
                {
                    tokenThree = (tokenThree + 1) % 256;
                    if (tokenThree == 0)
                    {
                        tokenTwo = (tokenTwo + 1) % 256;
                        if (tokenTwo == 0)
                        {
                            tokenOne = (tokenOne + 1) % 256;
                        }
                    }
                    numberOfNodesRemain -= 256;
                }
                else
                {
                    tokenFour = (tokenFour + 1) % 256;

                    if (tokenFour == 0)
                    {
                        tokenThree = (tokenThree + 1) % 256;

                        if (tokenThree == 0)
                        {
                            tokenTwo = (tokenTwo + 1) % 256;
                            if (tokenTwo == 0)
                            {
                                tokenOne = (tokenOne + 1) % 256;
                            }
                        }
                    }
                    numberOfNodesRemain--;
                }

            }

            return IPAddress.newIPAddress(tokenOne + "." + tokenTwo + "." + tokenThree + "."
                + tokenFour);
        }
        else
        {
            return null;
        }
    }

    /**
     * This method generates a Random MAC valid for the HypervisorType from a given Hypervisor code
     * 
     * @param type which kind of hypervisor will use this MAC address
     * @return a DataResult object containing an String with the new MAC address
     * @throws NetworkCommandException if any problem occurs
     */
    public static String requestRandomMacAddress(HypervisorType type)
    {
        String randomMac = new String();

        switch (type)
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
                randomMac = outMac;
                break;

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
                randomMac = outMac;
                break;

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
                randomMac = outMac;
                break;

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
                randomMac = outMac;
                break;

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
                randomMac = outMac;
                break;

            case XENSERVER:
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
                randomMac = outMac;
                break;
        }

        return randomMac;
    }
};
