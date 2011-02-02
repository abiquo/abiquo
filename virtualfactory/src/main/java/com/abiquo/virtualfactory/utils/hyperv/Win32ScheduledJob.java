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

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import com.hyper9.jwbem.SWbemMethod;
import com.hyper9.jwbem.SWbemObject;
import com.hyper9.jwbem.SWbemServices;

/**
 * Represents a job created with the AT command. The Win32_ScheduledJob class does not represent a
 * job created with the Scheduled Task Wizard from the Control Panel
 * 
 * @author pnavarro
 */
public class Win32ScheduledJob extends SWbemObject
{

    private SWbemMethod create;

    public Win32ScheduledJob(IJIDispatch objectDispatcher, SWbemServices service)
    {
        super(objectDispatcher, service);
    }

    /**
     * Submits a job to the operating system for execution at a specified future time and date.
     * 
     * @param command the command to execute
     * @throws JIException
     */
    public void create(String command) throws JIException
    {
        if (this.create == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("Create"))
                {
                    this.create = m;
                }
            }
        }

        // Get the IN parameters.
        SWbemObject inParams = this.create.getInParameters();
        inParams.getObjectDispatcher().put("Command", new JIVariant(new JIString(command)));
        inParams.getObjectDispatcher().put("StartTime",
            new JIVariant(new JIString("********143000.000000-420")));

        Object[] methodParams =
            new Object[] {new JIString("Create"), new JIVariant(inParams.getObjectDispatcher()),
            new Integer(0), JIVariant.NULL(),};

        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);
    }

}
