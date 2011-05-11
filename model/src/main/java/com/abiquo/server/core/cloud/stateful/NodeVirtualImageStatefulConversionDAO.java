  package com.abiquo.server.core.cloud.stateful;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaNodeVirtualImageStatefulConversionDAO")
  public class NodeVirtualImageStatefulConversionDAO extends DefaultDAOBase<Integer, NodeVirtualImageStatefulConversion>
  {
      public NodeVirtualImageStatefulConversionDAO()
      {
          super(NodeVirtualImageStatefulConversion.class);
      }

      public NodeVirtualImageStatefulConversionDAO(EntityManager entityManager)
      {
          super(NodeVirtualImageStatefulConversion.class, entityManager);
      }

      
  }
