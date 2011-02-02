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

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents a IPv4 Address and its features. Checks if has a correct format and
 * computes the next and the previous IPAddress. After build a new IPAddress object, its recommended
 * to check method isInvalid().
 * 
 * @author abiquo
 */
public class IPAddress
{

    public static final String IPADDRESS_PATTERN =
        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    /**
     * Checks the correct format of the ipAddress and returns empty string if is not correct.
     * 
     * @param ipAddress proposed ipAddress
     * @return IPAddress reference or null if it's not valid
     */
    public static IPAddress newIPAddress(String ipAddress)
    {
        if (!StringUtils.isEmpty(ipAddress))
        {
            Pattern p = Pattern.compile(IPADDRESS_PATTERN);
            Matcher m = p.matcher(ipAddress);
            boolean matches = m.matches();

            if (matches)
                return new IPAddress(ipAddress);
        }

        return null;
    }

    /**
     * Object reference where we store the IP.
     */
    String ip;

    public IPAddress()
    {

    }

    /**
     * IP Address constructor
     * 
     * @param ipAddress
     */
    private IPAddress(String ipAddress)
    {
        // parse the string to delete all the 0's in the left of the .
        StringTokenizer tokenizer = new StringTokenizer(ipAddress, ".");

        // Thanks to the static method we can be sure we have 4 tokens
        // and they live between 0 and 256
        int tokenOne = Integer.parseInt(tokenizer.nextToken());
        int tokenTwo = Integer.parseInt(tokenizer.nextToken());
        int tokenThree = Integer.parseInt(tokenizer.nextToken());
        int tokenFour = Integer.parseInt(tokenizer.nextToken());

        ip = tokenOne + "." + tokenTwo + "." + tokenThree + "." + tokenFour;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof IPAddress)
        {
            IPAddress aux = (IPAddress) o;
            return aux.toString().equalsIgnoreCase(toString());
        }
        else
        {
            return false;
        }
    }

    /**
     * @return the first octet of the IPAddress object.
     */
    public String getFirstOctet()
    {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");

        // FirstOctet
        return tokenizer.nextToken();
    }

    /**
     * @return the second octet of the IPAddress object.
     */
    public String getSecondOctet()
    {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");

        // SecondOctet
        tokenizer.nextToken();
        return tokenizer.nextToken();
    }

    /**
     * @return the third octet of the IPAddress object.
     */
    public String getThirdOctet()
    {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");

        // ThirdOctet
        tokenizer.nextToken();
        tokenizer.nextToken();
        return tokenizer.nextToken();

    }

    /**
     * @return the fourth octet of the IPAddress object.
     */
    public String getFourthOctet()
    {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");

        // FourthOctet
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        return tokenizer.nextToken();

    }

    public String getIp()
    {
        return ip;
    }

    /**
     * Calculates the next IP address. Used for look for the next available IP address in DB.
     * 
     * @return an IPAddress object with the next one.
     * @throws InvalidIPAddressException
     */
    public IPAddress nextIPAddress()
    {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");

        // Thanks to the static method we can be sure we have 4 tokens
        // and they live between 0 and 256
        int tokenOne = Integer.parseInt(tokenizer.nextToken());
        int tokenTwo = Integer.parseInt(tokenizer.nextToken());
        int tokenThree = Integer.parseInt(tokenizer.nextToken());
        int tokenFour = Integer.parseInt(tokenizer.nextToken());

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
        return IPAddress.newIPAddress(tokenOne + "." + tokenTwo + "." + tokenThree + "."
            + tokenFour);
    }

    /**
     * Given a IPAddress, calculates its previous IPAddress.
     * 
     * @return previous IPAddress.
     * @throws InvalidIPAddressException
     */
    public IPAddress previousIPAddress()
    {
        StringTokenizer tokenizer = new StringTokenizer(ip, ".");

        // Thanks to the static method we can be sure we have 4 tokens
        // and they live between 0 and 256
        int tokenOne = Integer.parseInt(tokenizer.nextToken());
        int tokenTwo = Integer.parseInt(tokenizer.nextToken());
        int tokenThree = Integer.parseInt(tokenizer.nextToken());
        int tokenFour = Integer.parseInt(tokenizer.nextToken());

        // Note: Negative numbers doesn't compute the operator %, so, instead of subtract -1
        // we decide to sum +255
        tokenFour = (tokenFour + 255) % 256;
        if (tokenFour == 255)
        {
            tokenThree = (tokenThree + 255) % 256;
            if (tokenThree == 255)
            {
                tokenTwo = (tokenTwo + 255) % 256;
                if (tokenTwo == 255)
                {
                    tokenOne = (tokenOne + 255) % 256;
                }
            }
        }
        return IPAddress.newIPAddress(tokenOne + "." + tokenTwo + "." + tokenThree + "."
            + tokenFour);

    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return ip;
    }

    /**
     * Compare if the current object is bigger than the incoming ipAddress.
     * 
     * @param ip
     * @return
     */
    public Boolean isBiggerThan(IPAddress ip)
    {
        if (Integer.valueOf(getFirstOctet()) != (Integer.valueOf(ip.getFirstOctet())))
        {
            return Integer.valueOf(getFirstOctet()) > (Integer.valueOf(ip.getFirstOctet()));
        }
        else if (Integer.valueOf(getSecondOctet()) != (Integer.valueOf(ip.getSecondOctet())))
        {
            return Integer.valueOf(getSecondOctet()) > (Integer.valueOf(ip.getSecondOctet()));
        }
        else if (Integer.valueOf(getThirdOctet()) != (Integer.valueOf(ip.getThirdOctet())))
        {
            return Integer.valueOf(getThirdOctet()) > (Integer.valueOf(ip.getThirdOctet()));
        }
        else if (Integer.valueOf(getFourthOctet()) != (Integer.valueOf(ip.getFourthOctet())))
        {
            return Integer.valueOf(getFourthOctet()) > (Integer.valueOf(ip.getFourthOctet()));
        }

        return false;

    }

    public static boolean isValidIpAddress(String address)
    {
        return newIPAddress(address) != null;
    }

    public static boolean isIntoRange(Collection<IPAddress> range, String address)
    {
        IPAddress ip = newIPAddress(address);

        return range.contains(ip);
    }
};
