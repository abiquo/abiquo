  package com.abiquo.server.core.pricing;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaCosteCodeDAO")
  public class CosteCodeDAO extends DefaultDAOBase<Integer, CosteCode>
  {
      public CosteCodeDAO()
      {
          super(CosteCode.class);
      }

      public CosteCodeDAO(EntityManager entityManager)
      {
          super(CosteCode.class, entityManager);
      }

      
  }
