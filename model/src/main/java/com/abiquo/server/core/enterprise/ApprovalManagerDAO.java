package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaApprovalManagerDAO")
public class ApprovalManagerDAO extends DefaultDAOBase<Integer, ApprovalManager>
{
    private final static String QUERY_MANAGERS_FROM_USER =
        "SELECT a.idManager FROM com.abiquo.server.core.enterprise.ApprovalManager a "
            + "WHERE a.id = :idUser";

    private final static String QUERY_USERS_FROM_MANAGER =
        "SELECT a.id FROM com.abiquo.server.core.enterprise.ApprovalManager a "
            + "WHERE a.idManager = :idManager";

    private final static String QUERY_APPROVALMANAGER =
        "FROM com.abiquo.server.core.enterprise.ApprovalManager a "
            + "WHERE a.id = :idUser AND a.idManager = :idManager";

    public ApprovalManagerDAO()
    {
        super(ApprovalManager.class);
    }

    public ApprovalManagerDAO(final EntityManager entityManager)
    {
        super(ApprovalManager.class, entityManager);
    }

    public List<Integer> findManagersByUserId(final Integer userId)
    {
        Query query = getSession().createQuery(QUERY_MANAGERS_FROM_USER);
        query.setInteger("idUser", userId);

        List<Integer> idManagers = query.list();
        return idManagers;
    }

    public List<Integer> findUsersByManagerId(final Integer managerId)
    {
        Query query = getSession().createQuery(QUERY_USERS_FROM_MANAGER);
        query.setInteger("idManager", managerId);

        List<Integer> idUsers = query.list();
        return idUsers;
    }

    public ApprovalManager findApprovalManager(final Integer userId, final Integer managerId)
    {
        Query query = getSession().createQuery(QUERY_APPROVALMANAGER);
        query.setInteger("idUser", userId);
        query.setInteger("idManager", managerId);

        return (ApprovalManager) query.uniqueResult();
    }
}
