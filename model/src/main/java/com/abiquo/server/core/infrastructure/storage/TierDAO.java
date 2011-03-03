package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaTierDAO")
public class TierDAO extends DefaultDAOBase<Integer, Tier>
{
    public TierDAO()
    {
        super(Tier.class);
    }

    public TierDAO(EntityManager entityManager)
    {
        super(Tier.class, entityManager);
    }

    @SuppressWarnings("unchecked")
    public List<Tier> getTiersByDatacenter(final Integer datacenterId)
    {
        Criteria criteria = createCriteria(Restrictions.eq("datacenter.id", datacenterId));
        return criteria.list();
    }

    public Tier getTierById(Integer datacenterId, Integer tierId)
    {

        Criteria criteria =
            createCriteria(Restrictions.eq("datacenter.id", datacenterId)).add(
                Restrictions.eq("id", tierId));
        Object obj = criteria.uniqueResult();
        return (Tier) obj;
    }

}
