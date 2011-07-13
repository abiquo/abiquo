  package com.abiquo.server.core.pricing;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaCurrencyDAO")
  public class CurrencyDAO extends DefaultDAOBase<Integer, Currency>
  {
      public CurrencyDAO()
      {
          super(Currency.class);
      }

      public CurrencyDAO(EntityManager entityManager)
      {
          super(Currency.class, entityManager);
      }

      
  }
