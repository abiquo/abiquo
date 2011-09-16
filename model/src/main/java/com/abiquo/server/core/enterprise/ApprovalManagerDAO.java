  package com.abiquo.server.core.enterprise;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaApprovalManagerDAO")
  public class ApprovalManagerDAO extends DefaultDAOBase<Integer, ApprovalManager>
  {
      public ApprovalManagerDAO()
      {
          super(ApprovalManager.class);
      }

      public ApprovalManagerDAO(EntityManager entityManager)
      {
          super(ApprovalManager.class, entityManager);
      }

      
  }
