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
