package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaApprovalDAO")
public class ApprovalDAO extends DefaultDAOBase<Integer, Approval>
{
    public ApprovalDAO()
    {
        super(Approval.class);
    }

    public ApprovalDAO(final EntityManager entityManager)
    {
        super(Approval.class, entityManager);
    }

    @Override
    public List<Approval> findAll()
    {
        return createCriteria().list();
    }

    public Approval findByToken(final String token)
    {
        return findUniqueByProperty(Approval.TOKEN_PROPERTY, token);
    }

    public Approval findByRequest(final byte[] request)
    {
        return findUniqueByProperty(Approval.REQUEST_PROPERTY, request);
    }
}
