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

import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import com.hyper9.jwbem.SWbemMethod;
import com.hyper9.jwbem.SWbemObject;
import com.hyper9.jwbem.SWbemServices;

/**
 * Represents a CIM data file
 * 
 * @author pnavarro
 */
public class CIMDataFile extends SWbemObject
{
    private SWbemMethod copy;

    private SWbemMethod delete;

    /**
     * Initializes a new instance of the MsvmDataFile class.
     * 
     * @param objectDispatcher The underlying dispatch object used to communicate with the server.
     * @param service The service connection.
     */
    public CIMDataFile(IJIDispatch objectDispatcher, SWbemServices service)
    {
        super(objectDispatcher, service);
    }

    /**
     * Copies the file to the destination file
     * 
     * @param destinationFile the destination file
     * @throws Exception
     */
    public void copy(String destinationFile) throws Exception
    {
        if (this.copy == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("Copy"))
                {
                    this.copy = m;
                }
            }
        }

        SWbemObject inParams = this.copy.getInParameters();

        inParams.getObjectDispatcher().put("FileName", new JIVariant(destinationFile));

        Object[] methodParams =
            new Object[] {new JIString("Copy"), new JIVariant(inParams.getObjectDispatcher()),
            new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

    }

    /**
     * Deletes the file
     * 
     * @return a result
     * @throws Exception
     */
    public int delete() throws Exception
    {
        if (this.delete == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("Delete"))
                {
                    this.delete = m;
                }
            }
        }
        Object[] methodParams =
            new Object[] {new JIString("Delete"), new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

        // Get the out parameters.
        JIVariant outParamsVar = results[0];
        return outParamsVar.getObjectAsInt();

    }
}
