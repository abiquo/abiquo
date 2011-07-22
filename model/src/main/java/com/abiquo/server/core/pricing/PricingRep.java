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

@Repository
@Transactional
public class PricingRep extends DefaultRepBase
{

    @Autowired
    private CurrencyDAO currencyDao;

    @Autowired
    private PricingTemplateDAO pricingTemplateDao;

    public PricingRep()
    {

    }

    public PricingRep(final EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = entityManager;
        pricingTemplateDao = new PricingTemplateDAO(entityManager);
        currencyDao = new CurrencyDAO(entityManager);
    }

    public List<Currency> findAllCurrency()
    {
        return currencyDao.findAll();
    }

    public void insertCurrency(final Currency currency)
    {
        if (currency != null)
        {
            currencyDao.persist(currency);
            currencyDao.flush();
        }
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

}
