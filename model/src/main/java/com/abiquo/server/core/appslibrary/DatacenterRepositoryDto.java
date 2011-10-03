package com.abiquo.server.core.appslibrary;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.cloud.State;

@XmlRootElement(name = "DatacenterRepository")
public class DatacenterRepositoryDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String name;

    private String error;

    private String repositoryLocation;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getRepositoryLocation()
    {
        return repositoryLocation;
    }

    public void setRepositoryLocation(String repositoryLocation)
    {
        this.repositoryLocation = repositoryLocation;
    }

}
