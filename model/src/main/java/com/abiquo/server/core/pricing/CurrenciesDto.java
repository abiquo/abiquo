package com.abiquo.server.core.pricing;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "currencies")
public class CurrenciesDto extends WrapperDto<CurrencyDto>
{
    @Override
    @XmlElement(name = "currency")
    public List<CurrencyDto> getCollection()
    {
        return collection;
    }

    public void setCollection(final List<CurrencyDto> currencies)
    {
        collection = currencies;
    }

}
