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

package com.abiquo.nodecollector.domain.collectors.hyperv;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import com.abiquo.nodecollector.exception.CollectorException;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.MsvmObject;

/**
 * This class represents the Msvm_ImageManagementService class.
 * 
 * @author ibarrera
 */
public class MsvmImageManagementService extends MsvmObject
{
    /**
     * Initializes a new instance of the MsvmVirtualSystemManagementService class.
     * 
     * @param dispatch The dispatch object.
     * @param service The service connection.
     */
    public MsvmImageManagementService(final IJIDispatch dispatch, final SWbemServices service)
    {
        super(dispatch, service);
    }

    /**
     * Gets the hosts Msvm_ImageManagementService.
     * 
     * @param service The service connection.
     * @return The hosts Msvm_ImageManagementService.
     * @throws CollectorException When an error occurs.
     */
    public static MsvmImageManagementService getManagementService(final SWbemServices service)
        throws CollectorException
    {
        // Get the management service.
        String wql = "SELECT * FROM Msvm_ImageManagementService";
        SWbemObjectSet<MsvmImageManagementService> objSetMgmtSvc =
            service.execQuery(wql, MsvmImageManagementService.class);

        int size = objSetMgmtSvc.getSize();
        if (size != 1)
        {
            throw new CollectorException("There should be exactly 1 Msvm_ImageManagementService");
        }

        return objSetMgmtSvc.iterator().next();
    }

    /**
     * Gets the info of the specified virtual hard disk.
     * 
     * @param path The path to the virtual hard disk.
     * @return The information about the virtual hard disk.
     * @throws JIException If disk information cannot be retrieved.
     * @throws CollectorException If disk information cannot be retrieved.
     */
    public String getVirtualHardDiskInfo(final String path) throws JIException, CollectorException
    {
        JIVariant[] results =
            getObjectDispatcher()
                .callMethodA(
                    "GetVirtualHardDiskInfo",
                    new Object[] {new JIVariant(path), JIVariant.EMPTY_BYREF(),
                    JIVariant.EMPTY_BYREF()});

        int result = results[0].getObjectAsInt();

        if (result != 0)
        {
            throw new CollectorException("Could not get virtual disk information");
        }

        return results[2].getObjectAsVariant().getObjectAsString2();
    }
}
