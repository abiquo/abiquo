package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;

@Repository
public class ApprovalRep extends DefaultRepBase
{
    @Autowired
    private ApprovalDAO approvalDAO;

    @Autowired
    private ApprovalManagerDAO approvalManagerDAO;

    public ApprovalRep()
    {

    }

    public ApprovalRep(final EntityManager entityManager)
    {
        assert entityManager != null : "EntityManager cannot be null (ApprovalRep Constructor)";
        assert entityManager.isOpen() : "Fail in Constructor ApprovalRep";

        this.entityManager = entityManager;

        approvalDAO = new ApprovalDAO(entityManager);
        approvalManagerDAO = new ApprovalManagerDAO(entityManager);
    }

    public Approval findApprovalById(final Integer approvalId)
    {
        return approvalDAO.findById(approvalId);
    }

    public Approval findApprovalByToken(final String token)
    {
        return approvalDAO.findByToken(token);
    }

    public Approval findApprovalByRequest(final byte[] request)
    {
        return approvalDAO.findByRequest(request);
    }

    public List<Approval> findAll()
    {
        List<Approval> list = approvalDAO.findAll();
        return list;
    }

    public void insertApproval(final Approval approval)
    {
        approvalDAO.persist(approval);
        approvalDAO.flush();
    }

    public void updateApproval(final Approval approval)
    {
        approvalDAO.flush();
    }

    public void removeApproval(final Approval approval)
    {
        approvalDAO.remove(approval);
        approvalDAO.flush();
    }

    public List<ApprovalManager> findAllApprovalManager()
    {
        List<ApprovalManager> appmList = approvalManagerDAO.findAll();
        return appmList;
    }

    public void insertApprovalManager(final ApprovalManager appm)
    {
        approvalManagerDAO.persist(appm);
        approvalManagerDAO.flush();

    }

    public ApprovalManager findApprovalManagerById(final Integer approvalManagerId)
    {
        assert approvalManagerId != null;
        return approvalManagerDAO.findById(approvalManagerId);
    }

    public void deleteApprovalManager(final ApprovalManager appm)
    {
        approvalManagerDAO.remove(appm);
        approvalManagerDAO.flush();

    }

    public void updateApprovalManager(final ApprovalManager aprovalmanager)
    {
        assert aprovalmanager != null;
        approvalManagerDAO.flush();
    }
}
