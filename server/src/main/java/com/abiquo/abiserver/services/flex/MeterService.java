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

package com.abiquo.abiserver.services.flex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.commands.MeterCommand;
import com.abiquo.abiserver.commands.impl.MeterCommandImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.metering.Meter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;

/**
 * This class defines all services related to Metering
 * 
 * @author Oliver
 */
public class MeterService
{

    /**
     * The command related to this service
     */
    private final MeterCommand meterCommand;

    /**
     * Constructor The implemention of the BasicCommand and the ResourceLocator to be used is
     * defined here
     */
    public MeterService()
    {
        meterCommand = new MeterCommandImpl();
    }

    protected MeterCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, meterCommand, MeterCommand.class);
    }

    /**
     * Get the list of meters matching with the filters
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param filters to filter the list of meters
     * @param numrows the number of rows to return
     * @return a BasicResult containing the list of the meters found
     */
    public BasicResult getMeters(UserSession userSession, HashMap<String, String> filters,
        Integer numrows)
    {
        DataResult<List<Meter>> dataResult = new DataResult<List<Meter>>();

        MeterCommand command = proxyCommand(userSession);

        // Get the list of private IPs
        List<MeterHB> meterList = new ArrayList<MeterHB>();
        try
        {
            meterList =
                command.getMeters(userSession, new HashMap<String, String>(filters), numrows);
            List<Meter> returnList = new ArrayList<Meter>();

            for (MeterHB meterHB : meterList)
            {
                returnList.add(meterHB.toPojo());
            }

            dataResult.setSuccess(true);
            dataResult.setData(returnList);
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Gets all the possible events we can filter
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @return a BasicResult containing a list of strings indication the events
     */
    public BasicResult getEventTypes(UserSession userSession)
    {
        DataResult<List<String>> dataResult = new DataResult<List<String>>();

        MeterCommand command = proxyCommand(userSession);

        List<String> eventList;
        try
        {
            eventList = command.getListOfEventTypes();
            dataResult.setSuccess(true);
            dataResult.setData(eventList);
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /*
     * * Gets all the possible severity logs we can filter
     * @param userSession UserSession object with the information of the user that called this
     * method
     * @return a BasicResult containing a list of strings indication the severities
     */
    public BasicResult getSeverityTypes(UserSession userSession)
    {
        DataResult<List<String>> dataResult = new DataResult<List<String>>();

        MeterCommand command = proxyCommand(userSession);

        List<String> severityList;
        try
        {
            severityList = command.getListOfSeverityTypes();
            dataResult.setSuccess(true);
            dataResult.setData(severityList);
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }
}
