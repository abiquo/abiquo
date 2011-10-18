  package com.abiquo.server.core.cloud;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaVirtualImageConversionDAO")
  public class VirtualImageConversionDAO extends DefaultDAOBase<Integer, VirtualImageConversion>
  {
      public VirtualImageConversionDAO()
      {
          super(VirtualImageConversion.class);
      }

      public VirtualImageConversionDAO(EntityManager entityManager)
      {
          super(VirtualImageConversion.class, entityManager);
      }

      
  }
