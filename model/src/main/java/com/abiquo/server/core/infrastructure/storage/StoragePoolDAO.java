  package com.abiquo.server.core.infrastructure.storage;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaStoragePoolDAO")
  public class StoragePoolDAO extends DefaultDAOBase<String, StoragePool>
  {
      public StoragePoolDAO()
      {
          super(StoragePool.class);
      }

      public StoragePoolDAO(EntityManager entityManager)
      {
          super(StoragePool.class, entityManager);
      }

      
  }
