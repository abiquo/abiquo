  package com.abiquo.server.core.infrastructure.storage;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaTierDAO")
  public class TierDAO extends DefaultDAOBase<Integer, Tier>
  {
      public TierDAO()
      {
          super(Tier.class);
      }

      public TierDAO(EntityManager entityManager)
      {
          super(Tier.class, entityManager);
      }

      
  }
