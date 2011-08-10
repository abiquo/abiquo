package com.abiquo.server.core.pricing;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "pricingCostCodes")
public class PricingCostCodesDto extends WrapperDto<PricingCostCodeDto>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    @XmlElement(name = "pricingCostCode")
    public List<PricingCostCodeDto> getCollection()
    {
        return collection;
    }

}
