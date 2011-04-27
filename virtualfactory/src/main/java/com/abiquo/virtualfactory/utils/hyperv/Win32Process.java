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
 * Represents a process on an Win32 operating system
 * 
 * @author pnavarro
 */
public class Win32Process extends SWbemObject
{

    private SWbemMethod create;

    private IJIDispatch dispatch = null;

    public Win32Process(IJIDispatch objectDispatcher, SWbemServices service)
    {
        super(objectDispatcher, service);
        this.dispatch = objectDispatcher;
    }

    /**
     * Creates a new process
     * 
     * @param command the command to execute
     * @throws JIException
     */
    public void create(String command) throws JIException
    {
        // TODO: Do we need to get this Create method this way?
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
        inParams.getObjectDispatcher().put("CommandLine", new JIVariant(new JIString(command)));
// "CurrentDirectory" and "ProcessStartupInformation" are optional params        
//        inParams.getObjectDispatcher().put("CurrentDirectory", new JIVariant(new JIString("C:\\")));
//        inParams.getObjectDispatcher().put("CurrentDirectory", JIVariant.NULL());
//         inParams.getObjectDispatcher().put("ProcessStartupInformation", JIVariant.NULL());

        Object[] methodParams =
            new Object[] {new JIString("Create"), new JIVariant(inParams.getObjectDispatcher()),
            new Integer(0), JIVariant.NULL(),};

        JIVariant[] results = dispatch.callMethodA("ExecMethod_", methodParams);
        
// TODO: Get result actually returned; it's not an Integer
//        int result = results[0].getObjectAsInt();
    }
}
