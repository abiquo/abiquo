package com.abiquo.server.core.enterprise;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "events")
public class EventsDto extends WrapperDto<EventDto>
{
    private static final long serialVersionUID = 1L;

    @Override
    @XmlElement(name = "event")
    public List<EventDto> getCollection()
    {
        if (collection == null)
        {
            collection = new ArrayList<EventDto>();
        }
        return collection;
    }
}
