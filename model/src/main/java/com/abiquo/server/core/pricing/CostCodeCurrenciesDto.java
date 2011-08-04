package com.abiquo.server.core.pricing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "costCodeCurrencies")
public class CostCodeCurrenciesDto extends WrapperDto<CostCodeCurrencyDto>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String MEDIA_TYPE = "application/costcodecurrenciesdto+xml";

    @Override
    @XmlElement(name = "costCodeCurrency")
    public List<CostCodeCurrencyDto> getCollection()
    {
        if (collection == null)
        {
            collection = new ArrayList<CostCodeCurrencyDto>();
        }
        return collection;
    }

}
