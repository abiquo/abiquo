package com.abiquo.server.core.cloud.stateful;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

@XmlRootElement(name = "")
public class DiskStatefulConversionDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -1803802363006402113L;

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    private String imagePath;

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(final String imagePath)
    {
        this.imagePath = imagePath;
    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private State state;

    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

    private VolumeManagement volume;

    public VolumeManagement getVolume()
    {
        return volume;
    }

    public void setVolume(final VolumeManagement volume)
    {
        this.volume = volume;
    }

}
