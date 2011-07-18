package com.abiquo.server.core.pricing;

import java.util.Currency;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.infrastructure.DatacenterDAO;

public class PricingRep extends DefaultRepBase
{

    @Autowired
    private DatacenterDAO dao;

    @Autowired
    private PricingTemplateDAO pricingDao;

    @Autowired
    private CurrencyDAO currencyDao;

    @Autowired
    private CostCodeDAO costCodeDao;

    public PricingRep()
    {

    }

    public List<Currency> findAllCurrency()
    {
        return currencyDao.findAll();
    }

}
