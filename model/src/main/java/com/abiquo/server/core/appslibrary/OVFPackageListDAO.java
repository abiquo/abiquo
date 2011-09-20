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
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaOVFPackageListDAO")
public class OVFPackageListDAO extends DefaultDAOBase<Integer, OVFPackageList>
{
    private final static String FIND_BY_ENTERPRISE = "SELECT * FROM OVFPackageList ovflist " //
        + "WHERE ovflist.appsLibrary.enterprise.id = :enterpriseId ";

    private final static String FIND_BY_NAME_AND_ENTERPRISE =
        "SELECT * FROM OVFPackageList ovflist " //
            + "WHERE ovflist.appsLibrary.enterprise.id = :enterpriseId and ovflist.name = :name";

    public OVFPackageListDAO()
    {
        super(OVFPackageList.class);
    }

    public OVFPackageListDAO(final EntityManager entityManager)
    {
        super(OVFPackageList.class, entityManager);
    }

    public List<OVFPackageList> findByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public OVFPackageList findByNameAndEnterprise(final String name, final Enterprise ent)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", ent.getId());
        query.setParameter("name", name);

        return (OVFPackageList) query.uniqueResult();
    }

    public List<OVFPackage> findByName(final String name)
    {
        Criteria criteria = createCriteria(sameName(name));
        criteria.addOrder(Order.asc(OVFPackageList.NAME_PROPERTY));

        return criteria.list();
    }

    private static Criterion sameName(final String name)
    {
        return Restrictions.eq(OVFPackageList.NAME_PROPERTY, name);
    }
}
