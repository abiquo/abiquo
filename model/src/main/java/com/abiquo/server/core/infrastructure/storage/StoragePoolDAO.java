package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaStoragePoolDAO")
@SuppressWarnings("unchecked")
public class StoragePoolDAO extends DefaultDAOBase<String, StoragePool>
{
    public StoragePoolDAO()
    {
        super(StoragePool.class);
    }

    public StoragePoolDAO(EntityManager entityManager)
    {
        super(StoragePool.class, entityManager);
    }

   
    public List<StoragePool> getPoolsByStorageDevice(Integer deviceId)
    {
        Criteria criteria = createCriteria(Restrictions.eq("device.id", deviceId));
        return criteria.list();
    }

    public StoragePool findPoolById(Integer deviceId, String poolId)
    {
        Criteria criteria =
            createCriteria(Restrictions.eq("device.id", deviceId)).add(Restrictions.eq("idStorage", poolId));
        Object obj = criteria.uniqueResult();
        return (StoragePool) obj;
    }

    public List<StoragePool> findPoolsByTier(Tier tier)
    {
        Criteria criteria = createCriteria(Restrictions.eq("tier", tier));
        return criteria.list();
    }

}
