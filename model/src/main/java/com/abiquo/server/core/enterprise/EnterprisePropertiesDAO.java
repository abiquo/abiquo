package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaEnterprisePropertiesDAO")
public class EnterprisePropertiesDAO extends DefaultDAOBase<Integer, EnterpriseProperties>
{

    public EnterprisePropertiesDAO()
    {
        super(EnterpriseProperties.class);
    }

    public EnterprisePropertiesDAO(final EntityManager entityManager)
    {
        super(EnterpriseProperties.class, entityManager);
    }

    public EnterpriseProperties findByEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;
        List<EnterpriseProperties> list = findByCriterions(sameEnterprise(enterprise));
        assert list.size() != 1;

        return list.get(0);
    }

    private Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(EnterpriseProperties.ENTERPRISE_PROPERTY, enterprise);
    }
}
