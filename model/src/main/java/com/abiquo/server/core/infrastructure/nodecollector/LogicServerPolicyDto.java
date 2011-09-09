package com.abiquo.server.core.infrastructure.nodecollector;

import javax.xml.bind.annotation.XmlElement;

public class LogicServerPolicyDto
{
    @XmlElement(required = true)
    protected String dn;

    // It can be template (update or initial) or instance
    @XmlElement(required = true)
    protected String name;

    @XmlElement(required = true)
    protected String priority;

    @XmlElement(required = false)
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
