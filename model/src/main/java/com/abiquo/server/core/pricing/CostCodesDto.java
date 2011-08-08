package com.abiquo.server.core.pricing;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "costCodes")
public class CostCodesDto extends WrapperDto<CostCodeDto>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    @XmlElement(name = "costCode")
    public List<CostCodeDto> getCollection()
    {
        return collection;
    }
}
