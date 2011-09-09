package com.abiquo.server.core.infrastructure;


public class LogicServerPolicy
{
    protected String dn;

    // It can be template (update or initial) or instance
    protected String name;

    protected String priority;

    protected String description;

    public String getDn()
    {
        return dn;
    }

    public void setDn(final String dn)
    {
        this.dn = dn;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(final String priority)
    {
        this.priority = priority;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

}
