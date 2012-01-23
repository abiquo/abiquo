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

package com.abiquo.abiserver.pojo.ucs;

import java.util.ArrayList;
import java.util.Collection;

public class LogicServerListResult
{

    // The List of LogicServer (limited by a length) that match the ListOptions given to
    // retrieve the list of LogicServer
    private Collection<LogicServer> logicServerList;

    // The total number of LogicServer that match the ListOptions given to
    // retrieve the list of LogicServer
    private int totalLogicServer;

    public LogicServerListResult()
    {
        logicServerList = new ArrayList<LogicServer>();
        totalLogicServer = 0;
    }

    public Collection<LogicServer> getLogicServerList()
    {
        return logicServerList;
    }

    public void setLogicServerList(final Collection<LogicServer> logicServerList)
    {
        this.logicServerList = logicServerList;
    }

    public int getTotalLogicServer()
    {
        return totalLogicServer;
    }

    public void setTotalLogicServer(final int totalLogicServer)
    {
        this.totalLogicServer = totalLogicServer;
    }

}
