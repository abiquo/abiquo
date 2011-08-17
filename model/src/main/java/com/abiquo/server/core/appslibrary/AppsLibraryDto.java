package com.abiquo.server.core.appslibrary;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class AppsLibraryDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private int idAppsLibrary;

    public int getIdAppsLibrary()
    {
        return idAppsLibrary;
    }

    public void setIdAppsLibrary(final int idAppsLibrary)
    {
        this.idAppsLibrary = idAppsLibrary;
    }

}
