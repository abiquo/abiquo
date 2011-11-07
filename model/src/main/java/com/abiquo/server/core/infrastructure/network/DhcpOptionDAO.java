  package com.abiquo.server.core.infrastructure.network;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaDhcpOptionDAO")
  public class DhcpOptionDAO extends DefaultDAOBase<Integer, DhcpOption>
  {
      public DhcpOptionDAO()
      {
          super(DhcpOption.class);
      }

      public DhcpOptionDAO(EntityManager entityManager)
      {
          super(DhcpOption.class, entityManager);
      }

      
  }
