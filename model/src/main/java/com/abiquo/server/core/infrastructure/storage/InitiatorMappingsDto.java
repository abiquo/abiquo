package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "initiatorMappings")
public class InitiatorMappingsDto extends WrapperDto<InitiatorMappingDto>
{
    private static final long serialVersionUID = 1L;

    @Override
    @XmlElement(name = "initiatorMapping")
    public List<InitiatorMappingDto> getCollection()
    {
        return collection;
    }

}
