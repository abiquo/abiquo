  package com.abiquo.server.core.pricing;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaPricingTemplateDAO")
  public class PricingTemplateDAO extends DefaultDAOBase<Integer, PricingTemplate>
  {
      public PricingTemplateDAO()
      {
          super(PricingTemplate.class);
      }

      public PricingTemplateDAO(EntityManager entityManager)
      {
          super(PricingTemplate.class, entityManager);
      }

      
  }
