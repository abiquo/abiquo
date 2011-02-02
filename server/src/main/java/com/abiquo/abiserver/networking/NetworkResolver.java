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

package com.abiquo.abiserver.networking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import com.abiquo.abiserver.exception.InvalidMaskException;
import com.abiquo.abiserver.exception.InvalidPrivateNetworkClassTypeException;

/**
 * This class is used to define all the ranges used by the private networks.
 */
public class NetworkResolver
{

    /**
     * Specify all the masks user can choose
     */
    private String[] allMasks =
        {"255.0.0.0", "255.128.0.0", "255.192.0.0", "255.224.0.0", "255.240.0.0", "255.248.0.0",
        "255.252.0.0", "255.254.0.0", "255.255.0.0", "255.255.128.0", "255.255.192.0",
        "255.255.224.0", "255.255.240.0", "255.255.248.0", "255.255.252.0", "255.255.254.0",
        "255.255.255.0", "255.255.255.128", "255.255.255.192", "255.255.255.224",
        "255.255.255.240", "255.255.255.248", "255.255.255.252"};

    /**
     * List of values from 0 to 255
     */
    private List<String> possibleValues;
    
    /**
     * Error messages 
     */
    private static String CLASSTYPE_CANNOT_BE_NULL = "Network class type can not be null.";
    private static String INVALID_CLASSTYPE = "Invalid private network ClassType.";
    private static String INVALID_NETWORK_MASK = "Network mask is invalid.";

    public NetworkResolver()
    {
        possibleValues = new ArrayList<String>();

        for (int i = 0; i < 256; i++)
        {
            possibleValues.add(String.valueOf(i));
        }
    }

    /**
     * Retrieve the list of enabled masks.
     * 
     * @param privateNetworkClassType class type of the subnet.
     * @return a list of available masks.
     * @throws InvalidPrivateNetworkClassTypeException if the privateNetworkClassType is not
     *             supported.
     */
    public List<String> resolveMask(String privateNetworkClassType)
        throws InvalidPrivateNetworkClassTypeException
    {

        List<String> masks = new ArrayList<String>();

        if (privateNetworkClassType == null)
        {
            throw new InvalidPrivateNetworkClassTypeException(CLASSTYPE_CANNOT_BE_NULL);
        }

        if (privateNetworkClassType.equalsIgnoreCase("A"))
        {
            //masks = Arrays.asList(allMasks);
            masks = Arrays.asList(allMasks).subList(14, allMasks.length);
        }
        else if (privateNetworkClassType.equalsIgnoreCase("B"))
        {
            //masks = Arrays.asList(allMasks).subList(8, allMasks.length);
            masks = Arrays.asList(allMasks).subList(14, allMasks.length);
        }
        else if (privateNetworkClassType.equalsIgnoreCase("C"))
        {
            masks = Arrays.asList(allMasks).subList(16, allMasks.length);
        }
        else
        {
            throw new InvalidPrivateNetworkClassTypeException(INVALID_CLASSTYPE);
        }

        return masks;
    }

    /**
     * @param privateNetworkClassType
     * @param mask
     * @return
     * @throws InvalidPrivateNetworkClassTypeException
     * @throws InvalidMaskException
     */
    public List<List<String>> resolvePossibleNetworks(String privateNetworkClassType, IPAddress mask)
        throws InvalidPrivateNetworkClassTypeException, InvalidMaskException
    {
        List<List<String>> networks = new ArrayList<List<String>>();
        List<String> firstElement = new ArrayList<String>();
        List<String> secondElement = new ArrayList<String>();
        List<String> thirdElement = new ArrayList<String>();
        List<String> fourthElement = new ArrayList<String>();
        String maskString = mask.toString();

        // check correct values

        // First check if the class type is defined according with the standard network private
        // class types
        if ((privateNetworkClassType == null) || !privateNetworkClassType.equalsIgnoreCase("A")
            && !privateNetworkClassType.equalsIgnoreCase("B")
            && !privateNetworkClassType.equalsIgnoreCase("C"))
        {
            throw new InvalidPrivateNetworkClassTypeException(INVALID_CLASSTYPE);
        }

        // After check if the mask is inside the array of the accepted masks
        if ((privateNetworkClassType.equalsIgnoreCase("A") && !Arrays.asList(allMasks).subList(
            14, allMasks.length).contains(maskString))
            || (privateNetworkClassType.equalsIgnoreCase("B") && !Arrays.asList(allMasks).subList(
                14, allMasks.length).contains(maskString))
            || (privateNetworkClassType.equalsIgnoreCase("C") && !Arrays.asList(allMasks).subList(
                16, allMasks.length).contains(maskString)))
        {
            throw new InvalidMaskException(INVALID_NETWORK_MASK);
        }

        StringTokenizer tokenizer = new StringTokenizer(maskString, ".");

        // First element of the list of lists.
        // First element is always depending on the class mask.
        tokenizer.nextToken();

        firstElement.add(defineFirstList(privateNetworkClassType));

        // Second element of the list of lists
        secondElement.addAll(defineSecondList(privateNetworkClassType, tokenizer.nextToken()));

        // Third and fourth element of the list token depends on absolutly the mask
        thirdElement.addAll(defineListFromMask(tokenizer.nextToken()));
        fourthElement.addAll(defineListFromMask(tokenizer.nextToken()));

        networks.add(firstElement);
        networks.add(secondElement);
        networks.add(thirdElement);
        networks.add(fourthElement);

        return networks;
    }

    /**
     * @param privateNetworkClassType
     * @return
     */
    private String defineFirstList(String privateNetworkClassType)
    {
        if (privateNetworkClassType.equalsIgnoreCase("A"))
        {
            return "10";
        }
        else if (privateNetworkClassType.equalsIgnoreCase("B"))
        {
            return "172";
        }
        else
        {
            return "192";
        }
    }

    private List<String> defineSecondList(String privateNetworkClassType, String nextToken)
    {
        List<String> secondList = new ArrayList<String>();

        if (privateNetworkClassType.equalsIgnoreCase("C"))
        {
            secondList.add("168");
        }
        else if (privateNetworkClassType.equalsIgnoreCase("B"))
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
    private List<String> defineListFromMask(String mask)
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
    private List<String> mapModule(Integer mod)
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
