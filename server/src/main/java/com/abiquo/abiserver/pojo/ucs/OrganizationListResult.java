package com.abiquo.abiserver.pojo.ucs;

import java.util.ArrayList;
import java.util.Collection;

public class OrganizationListResult
{
    // The List of Organization (limited by a length) that match the ListOptions given to
    // retrieve the list of Organization
    private Collection<Organization> logicOrganizationList;

    // The total number of Organization that match the ListOptions given to
    // retrieve the list of Organization
    private int totalLogicOrganization;

    public OrganizationListResult()
    {
        logicOrganizationList = new ArrayList<Organization>();
        totalLogicOrganization = 0;
    }

    public Collection<Organization> getLogicOrganizationList()
    {
        return logicOrganizationList;
    }

    public void setLogicOrganizationList(final Collection<Organization> logicOrganizationList)
    {
        this.logicOrganizationList = logicOrganizationList;
    }

    public int getTotalLogicOrganization()
    {
        return totalLogicOrganization;
    }

    public void setTotalLogicOrganization(final int totalLogicOrganization)
    {
        this.totalLogicOrganization = totalLogicOrganization;
    }
}
