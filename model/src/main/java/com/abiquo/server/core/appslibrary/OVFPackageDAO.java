package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaOVFPackageDAO")
public class OVFPackageDAO extends DefaultDAOBase<Integer, OVFPackage>
{

    private final static String FIND_BY_ENTERPRISE = "SELECT * FROM OVFPackage ovf " //
        + "WHERE ovf.appsLibrary.enterprise.id = :enterpriseId ";

    public OVFPackageDAO()
    {
        super(OVFPackage.class);
    }

    public OVFPackageDAO(final EntityManager entityManager)
    {
        super(OVFPackage.class, entityManager);
    }

    public List<OVFPackage> findByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public List<OVFPackage> findByAppsLibrary(final AppsLibrary appsLibrary)
    {
        Criteria criteria = createCriteria(sameAppsLibrary(appsLibrary));
        criteria.addOrder(Order.asc(OVFPackage.NAME_PROPERTY));

        return criteria.list();
    }

    private static Criterion sameAppsLibrary(final AppsLibrary appsLibrary)
    {
        return Restrictions.eq(OVFPackage.APPS_LIBRARY_PROPERTY, appsLibrary);
    }
}
