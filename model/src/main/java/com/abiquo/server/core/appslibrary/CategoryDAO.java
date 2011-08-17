  package com.abiquo.server.core.appslibrary;

  import javax.persistence.EntityManager;

  import org.springframework.stereotype.Repository;

  import com.abiquo.server.core.common.persistence.DefaultDAOBase;

  @Repository("jpaCategoryDAO")
  public class CategoryDAO extends DefaultDAOBase<Integer, Category>
  {
      public CategoryDAO()
      {
          super(Category.class);
      }

      public CategoryDAO(EntityManager entityManager)
      {
          super(Category.class, entityManager);
      }

      
  }
