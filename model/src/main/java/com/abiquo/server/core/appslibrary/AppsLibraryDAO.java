package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaAppsLibraryDAO")
public class AppsLibraryDAO extends DefaultDAOBase<Integer, AppsLibrary>
{
    public AppsLibraryDAO()
    {
        super(AppsLibrary.class);
    }

    public AppsLibraryDAO(final EntityManager entityManager)
    {
        super(AppsLibrary.class, entityManager);
    }

    public AppsLibrary findByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(AppsLibrary.ENTERPRISE_PROPERTY));

        return (AppsLibrary) criteria.uniqueResult();
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(AppsLibrary.ENTERPRISE_PROPERTY, enterprise);
    }

}
