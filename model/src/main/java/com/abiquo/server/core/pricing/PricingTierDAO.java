package com.abiquo.server.core.pricing;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaPricingTierDAO")
public class PricingTierDAO extends DefaultDAOBase<Integer, PricingTier>
{
    public PricingTierDAO()
    {
        super(PricingTier.class);
    }

    public PricingTierDAO(final EntityManager entityManager)
    {
        super(PricingTier.class, entityManager);
    }

}
