  package com.abiquo.server.core.pricing;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaPricingDAO")
  public class PricingDAO extends DefaultDAOBase<Integer, Pricing>
  {
      public PricingDAO()
      {
          super(Pricing.class);
      }

      public PricingDAO(EntityManager entityManager)
      {
          super(Pricing.class, entityManager);
      }

      
  }
