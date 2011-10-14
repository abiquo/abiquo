package com.abiquo.server.core.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "icons")
public class IconsDto extends WrapperDto<IconDto>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    @XmlElement(name = "icon")
    public List<IconDto> getCollection()
    {
        return collection;
    }

}
