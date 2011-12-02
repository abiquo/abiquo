package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "dhcpoptions")
public class DhcpOptionsDto extends WrapperDto<DhcpOptionDto>
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    @XmlElement(name = "dhcpoption")
    public List<DhcpOptionDto> getCollection()
    {
        return collection;
    }

}
