  package com.abiquo.server.core.pricing;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaCosteCodeDAO")
  public class CostCodeDAO extends DefaultDAOBase<Integer, CostCode>
  {
      public CostCodeDAO()
      {
          super(CostCode.class);
      }

      public CostCodeDAO(EntityManager entityManager)
      {
          super(CostCode.class, entityManager);
      }

      
  }
