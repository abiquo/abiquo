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

package com.abiquo.vsm.monitor.esxi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.vmware.vim25.MethodFault;

/*
 * Useful Client utility functions.
 */
public class ClientUtil
{

    private AppUtil _cb;

    public ClientUtil(AppUtil c)
    {
        _cb = c;
    }

    public void printUsage()
    {
        _cb.displayUsage();
    }

    /*
     * Prompt user for an integer value
     */
    public int getIntInput(String prompt, int defaultVal) throws Exception
    {
        String input = getStrInput(prompt);
        if (input == null || input.length() == 0)
            return defaultVal;
        else
            return Integer.parseInt(input);
    }

    /*
     * Prompt user for an integer value
     */
    public long getLongInput(String prompt, long defaultVal) throws Exception
    {
        String input = getStrInput(prompt);
        if (input == null || input.length() == 0)
            return defaultVal;
        else
            return Long.parseLong(input);
    }

    public String getStrInput(String prompt) throws Exception
    {
        System.out.print(prompt);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    /**
     * Log the Exception - fault or otherwise TODO: Enhance to handle different detail contents.
     */
    public void logException(Exception e)
    {
        if (e instanceof MethodFault)
        {
            MethodFault mf = (MethodFault) e;
            logFault(mf);
        }
        else
        {
            _cb.getLog().logLine(
                "Caught Exception : " + " Exception : " + e.getClass().getName() + " Message : "
                    + e.getMessage() + " StackTrace : ");
            e.printStackTrace();
        }
    }

    /**
     * Log a fault.
     */
    public void logFault(MethodFault mf)
    {
        _cb.getLog().logLine(
            "Caught Fault - " + "\n Type : " + mf.getClass().getName() + "\n Fault String : "
                + mf.getFaultCause().getLocalizedMessage());
    }
}
