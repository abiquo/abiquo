  package com.abiquo.server.core.cloud.stateful;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaDiskStatefulConversionDAO")
  public class DiskStatefulConversionDAO extends DefaultDAOBase<Integer, DiskStatefulConversion>
  {
      public DiskStatefulConversionDAO()
      {
          super(DiskStatefulConversion.class);
      }

      public DiskStatefulConversionDAO(EntityManager entityManager)
      {
          super(DiskStatefulConversion.class, entityManager);
      }

      
  }
