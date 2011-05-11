  package com.abiquo.server.core.cloud.stateful;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaVirtualApplicanceStatefulConversionDAO")
  public class VirtualApplicanceStatefulConversionDAO extends DefaultDAOBase<Integer, VirtualApplicanceStatefulConversion>
  {
      public VirtualApplicanceStatefulConversionDAO()
      {
          super(VirtualApplicanceStatefulConversion.class);
      }

      public VirtualApplicanceStatefulConversionDAO(EntityManager entityManager)
      {
          super(VirtualApplicanceStatefulConversion.class, entityManager);
      }

      
  }
