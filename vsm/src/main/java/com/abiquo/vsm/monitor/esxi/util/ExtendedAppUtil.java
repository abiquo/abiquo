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

import java.util.HashMap;

import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

public class ExtendedAppUtil extends AppUtil
{

    public static ExtendedAppUtil init(ServiceInstance serviceInstance, OptionSpec[] options,
        HashMap optsEntered) throws Exception
    {
        ExtendedAppUtil cb = new ExtendedAppUtil(serviceInstance);
        cb.addOptions(options);
        cb.setOptsEntered(optsEntered);
        return cb;
    }

    public static ExtendedAppUtil initialize(ServiceInstance serviceInstance,
        OptionSpec[] userOptions, String[] args) throws Exception
    {
        ExtendedAppUtil cb = new ExtendedAppUtil(serviceInstance);
        if (userOptions != null)
        {
            cb.addOptions(userOptions);
            cb.parseInput(args);
            cb.validate();
        }
        else
        {
            cb.parseInput(args);
            cb.validate();
        }
        return cb;
    }

    public static ExtendedAppUtil initialize(ServiceInstance serviceInstance, String[] args)
        throws Exception
    {
        ExtendedAppUtil cb = initialize(serviceInstance, null, args);
        return cb;
    }

    public ExtendedAppUtil(ServiceInstance serviceInstance)
    {
        super(serviceInstance);
    }

    public void disConnect() throws Exception
    {
        // log.logLine("Ended " + getAppName());
        if (serviceInstance != null && serviceInstance.getServerConnection() != null)
        {
            serviceInstance.getServerConnection().logout();
        }
    }
}
