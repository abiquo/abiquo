  package com.abiquo.server.core.appslibrary;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaOVFPackageListDAO")
  public class OVFPackageListDAO extends DefaultDAOBase<Integer, OVFPackageList>
  {
      public OVFPackageListDAO()
      {
          super(OVFPackageList.class);
      }

      public OVFPackageListDAO(EntityManager entityManager)
      {
          super(OVFPackageList.class, entityManager);
      }

      
  }
