  package com.abiquo.server.core.infrastructure;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaUcsRackDAO")
  public class UcsRackDAO extends DefaultDAOBase<Integer, UcsRack>
  {
      public UcsRackDAO()
      {
          super(UcsRack.class);
      }

      public UcsRackDAO(EntityManager entityManager)
      {
          super(UcsRack.class, entityManager);
      }

      
  }
