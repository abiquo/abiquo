  package com.abiquo.server.core.infrastructure.storage;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaCabinetDAO")
  public class CabinetDAO extends DefaultDAOBase<Integer, Cabinet>
  {
      public CabinetDAO()
      {
          super(Cabinet.class);
      }

      public CabinetDAO(EntityManager entityManager)
      {
          super(Cabinet.class, entityManager);
      }

      
  }
