package com.abiquo.model.transport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;

/**
 * This Entity is the response of 301. It is a link location.
 * 
 * @author sacedo
 */
@XmlRootElement(name = "moved")
public class MovedPermanentlyDto
{
    protected RESTLink locationLink;

    private VolumeManagementDto volumeDto;

    @XmlElement(name = "location")
    public RESTLink getLocationLink()
    {
        return locationLink;
    }

    public void setLocationLink(final RESTLink locationLink)
    {
        this.locationLink = locationLink;
    }

    @XmlElement(name = "volume")
    public VolumeManagementDto getVolumeDto()
    {
        return volumeDto;
    }

    public void setVolumeDto(final VolumeManagementDto volumeDto)
    {
        this.volumeDto = volumeDto;
    }

}
