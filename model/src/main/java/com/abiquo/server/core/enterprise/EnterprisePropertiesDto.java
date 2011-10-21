package com.abiquo.server.core.enterprise;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "enterpriseProperties")
public class EnterprisePropertiesDto extends SingleResourceTransportDto
{
    private Integer id;

    Map<String, String> properties;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(final Map<String, String> properties)
    {
        this.properties = properties;
    }
}
