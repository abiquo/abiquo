package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaStoragePoolDAO")
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

    @SuppressWarnings("unchecked")
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

}
