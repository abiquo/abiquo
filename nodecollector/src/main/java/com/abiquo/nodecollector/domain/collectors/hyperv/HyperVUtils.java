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

import static org.jinterop.dcom.impls.JIObjectFactory.narrowObject;

import java.util.ArrayList;
import java.util.List;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.jinterop.dcom.impls.automation.IJIEnumVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemStatusEnumType;
import com.hyper9.jwbem.SWbemServices;

/**
 * Utility functions to manage HyperV data.
 * 
 * @author ibarrera
 */
public final class HyperVUtils
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperVUtils.class);

    /**
     * Parses an array of raw objects and transform them into COM Objects.
     * 
     * @param set The array to transform.
     * @return The parsed Array.
     * @throws JIException If transformation cannot be done.
     */
    public static JIVariant[][] enumToJIVariantArray(final JIVariant[] set) throws JIException
    {
        final Integer A_MILLION = 1000000;

        final IJIDispatch toConvertDispatch =
            (IJIDispatch) narrowObject(set[0].getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));
        final JIVariant toConvertVariant = toConvertDispatch.get("_NewEnum");
        final IJIComObject toConvertComObject = toConvertVariant.getObjectAsComObject();
        final IJIEnumVariant toConvertEnumVariant =
            (IJIEnumVariant) narrowObject(toConvertComObject.queryInterface(IJIEnumVariant.IID));
        final ArrayList<JIVariant[]> res = new ArrayList<JIVariant[]>();
        int i = 0;
        final int threshold = A_MILLION; // to avoid infinite loop, in both msdn and j-interop
        // nothing is said about enumeration
        while (true)
        {
            Object[] values = null;
            try
            {
                values = toConvertEnumVariant.next(1);
            }
            catch (JIException e)
            {
                break;
            }
            if (values != null)
            {
                JIArray array = (JIArray) values[0];
                res.add((JIVariant[]) array.getArrayInstance());
            }
            i++;
            if (i >= threshold)
            {
                break;
            }
        }
        return res.toArray(new JIVariant[res.size()][]);
    }

    /**
     * Parses an array of COM Objects and transforms them to a list of {@link IJIDispatch} objects.
     * 
     * @param set The array to transform.
     * @return The list of <code>IJIDispatch</code> objects.
     * @throws JIException If transformation cannot be done.
     */
    public static List<IJIDispatch> enumToIJIDispatchList(final JIVariant[] set) throws JIException
    {
        List<IJIDispatch> results = new ArrayList<IJIDispatch>();
        JIVariant[][] tmpSet = enumToJIVariantArray(set);

        for (JIVariant[] element : tmpSet)
        {
            for (JIVariant element2 : element)
            {
                IJIDispatch dispatch =
                    (IJIDispatch) JIObjectFactory.narrowObject(element2.getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));

                results.add(dispatch);
            }
        }

        return results;
    }

    /**
     * Runs a query using the specified service.
     * 
     * @param query The query to run.
     * @param service Service to use to run the query.
     * @return The query results.
     * @throws JIException If query can not be executed.
     */
    public static List<IJIDispatch> execQuery(final String query, final SWbemServices service)
        throws JIException
    {
        IJIDispatch dispatcher = service.getObjectDispatcher();

        Object[] inParams =
            new Object[] {new JIString(query), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM()};

        JIVariant[] results = dispatcher.callMethodA("ExecQuery", inParams);

        return HyperVUtils.enumToIJIDispatchList(results);
    }

    /**
     * Gets the path of the dispatch.
     * 
     * @param dispatch the dispatch to get the path from
     * @return the dispatch path
     * @throws JIException If an error occurs.
     */
    public static String getDispatchPath(final IJIDispatch dispatch) throws JIException
    {
        // Getting the dispatcher of the Path
        IJIDispatch pathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(dispatch.get("Path_").getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));

        // Getting the path
        return pathDispatcher.get("Path").getObjectAsString2();
    }

    /**
     * Checks if the specified object is a virtual hard disk.
     * 
     * @param dispatch The object to be checked.
     * @return Boolean indicating if the specified object is a virtual hard disk.
     * @throws JIException If an error occurs.
     */
    public static boolean isVirtualHardDisk(final IJIDispatch dispatch) throws JIException
    {
        int type = dispatch.get("ResourceType").getObjectAsInt();

        if (type == HyperVConstants.VIRTUAL_DISK_RESOURCE_TYPE)
        {
            String subType = dispatch.get("ResourceSubType").getObjectAsString2();
            return subType.equals(HyperVConstants.VIRTUAL_DISK_RESOURCE_SUBTYPE);
        }

        return false;
    }

    /**
     * Translates an HyperV state code into an {@link VirtualMachineStateType}.
     * 
     * @param state State code to translate.
     * @return Translated state.
     */
    public static VirtualSystemStatusEnumType translateState(final int state)
    {
        HyperVState st = null;
        try
        {
            st = HyperVState.fromValue(state);
        }
        catch (IllegalArgumentException e)
        {
            // Probably we got a transition state Starting (32770), Saving (32773), Saving (32773),
            // Stopping (32774), Pausing (32776), Resuming (32777) or Unknown
            LOGGER.warn("Could not translate virtual machine state: {}", state);
            return null;
        }

        switch (st)
        {
            case POWER_ON:
                return VirtualSystemStatusEnumType.ON;
            case POWER_OFF:
                return VirtualSystemStatusEnumType.OFF;
            case SUSPENDED:
                return VirtualSystemStatusEnumType.OFF;
            case PAUSED:
                return VirtualSystemStatusEnumType.PAUSED;
            default:
                LOGGER.warn("Could not translate virtual machine state: {}", state);
                return null;
        }
    }

    /**
     * Private constructor for utility class.
     */
    private HyperVUtils()
    {
    }

}
