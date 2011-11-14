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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is used to define all the ranges used by the private networks.
 */
public class NetworkResolver
{

    /**
     * Specify all the masks user can choose
     */
    private static String[] allMasks = {"255.0.0.0", "255.128.0.0", "255.192.0.0", "255.224.0.0",
    "255.240.0.0", "255.248.0.0", "255.252.0.0", "255.254.0.0", "255.255.0.0", "255.255.128.0",
    "255.255.192.0", "255.255.224.0", "255.255.240.0", "255.255.248.0", "255.255.252.0",
    "255.255.254.0", "255.255.255.0", "255.255.255.128", "255.255.255.192", "255.255.255.224",
    "255.255.255.240", "255.255.255.248", "255.255.255.252"};

    /**
     * List of values from 0 to 255
     */
    private static List<String> possibleValues;

    static
    {
        possibleValues = new ArrayList<String>();

        for (int i = 0; i < 256; i++)
        {
            possibleValues.add(String.valueOf(i));
        }
    }

    public static boolean isValidNetworkMask(IPAddress address, Integer mask)
    {
        String firstOctet = address.getFirstOctet();
        String secondOctet = address.getSecondOctet();
        String thirdOctet = address.getThirdOctet();
        String fouthOctet = address.getFourthOctet();

        List<List<String>> listPossibleNetworks =
            resolvePossibleNetworks(Integer.parseInt(firstOctet),
                IPNetworkRang.transformIntegerMaskToIPMask(mask));
        if (listPossibleNetworks.get(0).contains(firstOctet)
            && listPossibleNetworks.get(1).contains(secondOctet)
            && listPossibleNetworks.get(2).contains(thirdOctet)
            && listPossibleNetworks.get(3).contains(fouthOctet))
        {
            return true;
        }
        return false;
    }

    /**
     * @param privateNetworkClassType
     * @param mask
     * @return
     * @throws InvalidPrivateNetworkClassTypeException
     * @throws InvalidMaskException
     */
    private static List<List<String>> resolvePossibleNetworks(Integer firstOctet, IPAddress mask)
    {
        List<List<String>> networks = new ArrayList<List<String>>();
        List<String> firstElement = new ArrayList<String>();
        List<String> secondElement = new ArrayList<String>();
        List<String> thirdElement = new ArrayList<String>();
        List<String> fourthElement = new ArrayList<String>();
        String maskString = mask.toString();

        // After check if the mask is inside the array of the accepted masks
        if ((firstOctet == 10 && !Arrays.asList(allMasks).subList(14, allMasks.length)
            .contains(maskString))
            || (firstOctet == 172 && !Arrays.asList(allMasks).subList(14, allMasks.length)
                .contains(maskString))
            || (firstOctet == 192 && !Arrays.asList(allMasks).subList(16, allMasks.length)
                .contains(maskString)))
        {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(maskString, ".");

        // First element of the list of lists.
        // First element is always depending on the class mask.
        tokenizer.nextToken();

        firstElement.add(String.valueOf(firstOctet));

        // Second element of the list of lists
        secondElement.addAll(defineSecondList(firstOctet, tokenizer.nextToken()));

        // Third and fourth element of the list token depends on absolutly the mask
        thirdElement.addAll(defineListFromMask(tokenizer.nextToken()));
        fourthElement.addAll(defineListFromMask(tokenizer.nextToken()));

        networks.add(firstElement);
        networks.add(secondElement);
        networks.add(thirdElement);
        networks.add(fourthElement);

        return networks;
    }

    private static List<String> defineSecondList(Integer firstOctet, String nextToken)
    {
        List<String> secondList = new ArrayList<String>();

        if (firstOctet == 192)
        {
            secondList.add("168");
        }
        else if (firstOctet == 172)
        {
            for (int i = 16; i < 32; i++)
            {
                secondList.add(String.valueOf(i));
            }
        }
        else
        {
            secondList.addAll(defineListFromMask(nextToken));
        }

        return secondList;
    }

    /**
     * From all the range of possible values in a network slot, it will return the sublist of all
     * the ranges that the user will choose depending on its mask.
     * 
     * @param mask value from 0 to 255
     * @return
     */
    private static List<String> defineListFromMask(String mask)
    {
        int token = Integer.parseInt(mask);
        int numberOfValues = 256;

        String binari = Integer.toBinaryString(token);
        List<String> list = new ArrayList<String>();

        if (mask.equalsIgnoreCase("0"))
        {
            list.add("0");
        }
        else
        {
            int numberOfOnes = binari.lastIndexOf("1") + 1;

            for (int i = 0; i < numberOfOnes; i++)
            {
                numberOfValues = numberOfValues / 2;
            }

            list = mapModule(numberOfValues);
        }

        return list;
    }

    /**
     * Apply the operation module to all the values in array possibleValues.
     * 
     * @param mod module value.
     * @return List of 'possibleValues' variable where its values are equal to 0 applying the
     *         module.
     */
    private static List<String> mapModule(Integer mod)
    {
        List<String> moduleZero = new ArrayList<String>();

        for (String value : possibleValues)
        {
            if (Integer.valueOf(value) % mod == 0)
            {
                moduleZero.add(value);
            }
        }

        return moduleZero;
    }

}
