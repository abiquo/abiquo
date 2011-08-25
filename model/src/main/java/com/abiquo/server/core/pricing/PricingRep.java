/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.server.core.pricing;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.server.core.infrastructure.storage.Tier;

@Repository
@Transactional
public class PricingRep extends DefaultRepBase
{

    @Autowired
    private CurrencyDAO currencyDao;

    @Autowired
    private PricingTemplateDAO pricingTemplateDao;

    @Autowired
    private CostCodeDAO costCodeDao;

    @Autowired
    private CostCodeCurrencyDAO costCodeCurrencyDao;

    @Autowired
    private PricingCostCodeDAO pricingCostCodeDao;

    @Autowired
    private PricingTierDAO pricingTierDao;

    @Autowired
    private EnterpriseRep enterpriseRep;

    @Autowired
    private StorageRep storageRep;

    public PricingRep()
    {

    }

    public PricingRep(final EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = entityManager;
        enterpriseRep = new EnterpriseRep(entityManager);
        storageRep = new StorageRep(entityManager);
        pricingTemplateDao = new PricingTemplateDAO(entityManager);
        currencyDao = new CurrencyDAO(entityManager);
        costCodeDao = new CostCodeDAO(entityManager);
        costCodeCurrencyDao = new CostCodeCurrencyDAO(entityManager);
        pricingCostCodeDao = new PricingCostCodeDAO(entityManager);
        pricingTierDao = new PricingTierDAO(entityManager);
    }

    public List<Currency> findAllCurrency()
    {
        return currencyDao.findAll();
    }

    public Integer insertCurrency(final Currency currency)
    {
        if (currency != null)
        {
            currencyDao.persist(currency);
            currencyDao.flush();
        }

        return currency.getId();
    }

    public Currency findCurrencyById(final Integer currencyId)
    {
        return currencyDao.findById(currencyId);
    }

    public void removeCurrency(final Currency currency)
    {
        if (currency != null)
        {
            currencyDao.remove(currency);
            currencyDao.flush();
        }
    }

    public void updateCurrency(final Currency currency)
    {
        assert currency != null;
        assert currencyDao.isManaged(currency);
        // assert !existsAnyOtherRackWithName(rack, rack.getName()) :
        // BUG_UPDATE_RACK_NAME_MUST_BE_UNIQUE;

        this.currencyDao.flush();

    }

    public void insertPricingTemplate(final PricingTemplate pricingTemplate)
    {

        if (pricingTemplate != null)
        {
            pricingTemplateDao.persist(pricingTemplate);
            pricingTemplateDao.flush();
        }
    }

    public boolean existAnyPricTempWithSameName(final String name)
    {
        return pricingTemplateDao.existAnyPricTempWithSameName(name);
    }

    public Collection<PricingTemplate> findPricingTemplates(final String filter,
        final String order, final boolean desc, final Integer page, final Integer numResults)
    {
        return pricingTemplateDao.find(filter, order, desc, page, numResults);
    }

    public PricingTemplate findPricingTemplateById(final Integer id)
    {
        return pricingTemplateDao.findById(id);
    }

    public List<PricingTemplate> findPricingTemplatesByCurrency(final Integer id)
    {
        return pricingTemplateDao.findPricingTemplatesByCurrency(id);
    }

    public void updatePricingTemplate(final PricingTemplate pricingTemplate)
    {
        pricingTemplateDao.flush();
    }

    public List<PricingTemplate> findAllPricingTemplateByName(final String name)
    {
        return pricingTemplateDao.findAllPricingTemplateByName(name);
    }

    public List<PricingTemplate> findPricingTemplats()
    {
        return pricingTemplateDao.findAll();
    }

    public boolean existAnyOtherPricingTempWithName(final PricingTemplate pricingTemplate,
        final String name)
    {
        return pricingTemplateDao.existAnyOtherPricingTempWithName(pricingTemplate, name);
    }

    public boolean existAnyEnterpriseWithPricingTemplate(final PricingTemplate pricingTemplate)
    {
        return enterpriseRep.existAnyEnterpriseWithPricingTemplate(pricingTemplate);
    }

    public void deletePricingTemplate(final PricingTemplate pricingTemplate)
    {
        pricingTemplateDao.remove(pricingTemplate);
    }

    public CostCode findCostCodeById(final Integer costCodeId)
    {
        return costCodeDao.findById(costCodeId);
    }

    public CostCodeCurrency findCurrencyCostCode(final CostCode costCode, final Currency currency)
    {
        return costCodeCurrencyDao.findCurrencyCostCode(costCode, currency);
    }

    public void insertCostCodeCurrency(final CostCodeCurrency costCodeCurrency)
    {
        costCodeCurrencyDao.persist(costCodeCurrency);
    }

    public void insertCostCode(final CostCode costCode)
    {
        if (costCode != null)
        {
            costCodeDao.persist(costCode);
            costCodeDao.flush();
        }
    }

    public CostCodeCurrency findCostCodeCurrencyById(final Integer id)
    {
        return costCodeCurrencyDao.findById(id);
    }

    public void updateCostCodeCurrency(final CostCodeCurrency costCodeCurrency)
    {
        costCodeCurrencyDao.flush();
    }

    public boolean existAnyOtherWithCurrency(final CostCodeCurrency costCodeCurrency,
        final Currency currency, final CostCode costCode)
    {
        return costCodeCurrencyDao.existAnyOtherWithCurrency(costCodeCurrency, currency, costCode);
    }

    public void deleteCostCode(final CostCode costCode)
    {
        costCodeDao.remove(costCode);

    }

    public void deleteCostCodecurrency(final CostCodeCurrency costCodeCurrency)
    {
        costCodeCurrencyDao.remove(costCodeCurrency);
    }

    public boolean existAnyOtherCostCodeWithName(final CostCode costCode, final String name)
    {
        return costCodeDao.existAnyOtherCostCodeWithName(costCode, name);
    }

    public void updateCostCode(final CostCode costCode)
    {
        costCodeDao.flush();

    }

    public boolean existAnyOtherCostCodeWithName(final String name)
    {

        return costCodeDao.existAnyOtherCostCodeWithName(name);
    }

    public Collection<CostCodeCurrency> findCostCodeCurrencies(final CostCode cc,
        final Currency currency)
    {

        return costCodeCurrencyDao.find(cc, currency);

    }

    public Collection<CostCode> findCostCodes(final String filter, final String order,
        final boolean desc, final int page, final int numResults)
    {
        return costCodeDao.find(filter, order, desc, page, numResults);
    }

    public PricingCostCode findPricingCostCode(final CostCode costCode,
        final PricingTemplate pricing)
    {
        return pricingCostCodeDao.findPricingCostCode(costCode, pricing);
    }

    public void insertPricingCostCode(final PricingCostCode pricingCostCode)
    {
        pricingCostCodeDao.persist(pricingCostCode);
    }

    public List<CostCode> findCostCodesIds()
    {
        return costCodeCurrencyDao.findCostCodesIds();
    }

    public boolean existAnyOtherCurrencyWithName(final String name)
    {
        return currencyDao.existAnyOtherCurrencyWithName(name);
    }

    public Collection<PricingCostCode> findPricingCostCodes(final PricingTemplate pricing)
    {
        return pricingCostCodeDao.findPricingCostCodes(pricing);
    }

    public List<Currency> findCurrencies()
    {
        return currencyDao.findAll();
    }

    public PricingCostCode findPricingCostCodeById(final Integer id)
    {
        return pricingCostCodeDao.findById(id);
    }

    public boolean existAnyOtherWithCostCode(final PricingCostCode pricingCostCode,
        final CostCode costCode, final PricingTemplate pricingTemplate)
    {
        return pricingCostCodeDao.existAnyOtherWithCostCode(pricingCostCode, costCode,
            pricingTemplate);

    }

    public void updatePricingCostCode(final PricingCostCode old)
    {
        pricingCostCodeDao.flush();
    }

    public Collection<PricingTier> findPricingTiers(final PricingTemplate pricing)
    {
        return pricingTierDao.findPricingTiers(pricing);
    }

    public void insertPricingTier(final PricingTier pricingTier)
    {
        pricingTierDao.persist(pricingTier);
    }

    public PricingTier findPricingTier(final Tier tier, final PricingTemplate pricing)
    {
        return pricingTierDao.findPricingTier(tier, pricing);
    }

    public PricingTier findPricingTierById(final Integer id)
    {
        return pricingTierDao.findById(id);
    }

    public void updatePricingTier(final PricingTier old)
    {
        pricingTierDao.flush();

    }

    public Tier findTierById(final Integer id)
    {
        return storageRep.findTierById(id);
    }

}
