package com.abiquo.server.core.cloud;

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

@Repository("jpaChefCookbookDAO")
public class ChefCookbookDAO extends DefaultDAOBase<Integer, ChefCookbook>
{
    public ChefCookbookDAO()
    {
        super(ChefCookbook.class);
    }

    public ChefCookbookDAO(EntityManager entityManager)
    {
        super(ChefCookbook.class, entityManager);
    }

    private final String QUERY_COOKBOOKS_BY_VM = "SELECT cb.cookbook " + //
        "FROM com.abiquo.server.core.cloud.ChefCookbook cb" + //
        "WHERE cb.idVM.id = :idVirtualMachine ";
    /**
     * 
     * 
     * @param idVirtualMachine virtual machine ID
     * @return a list of String containing cookbooks
     */
    public List<String> getAllCookBooksByVirtualMachine(Integer idVirtualMachine)
    {
        Query query = getSession().createQuery(QUERY_COOKBOOKS_BY_VM);
        query.setParameter("idVM", idVirtualMachine);

        return query.list();
    }
    
    private static Criterion sameVirtualMachine(VirtualMachine virtualmachine)
    {
        return Restrictions.eq(ChefCookbook.VIRTUALMACHINE_PROPERTY, virtualmachine);
    }
    
    public List<ChefCookbook> findByVirtualMachine(VirtualMachine virtualmachine)
    {
        Criteria criteria = createCriteria(sameVirtualMachine(virtualmachine));
        criteria.addOrder(Order.asc(ChefCookbook.VIRTUALMACHINE_PROPERTY));

        return criteria.list();
    }
}
