package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaCategoryDAO")
public class CategoryDAO extends DefaultDAOBase<Integer, Category>
{
    private final static String QUERY_GET_DEFAULT = "FROM " + Category.class.getName() + " WHERE " //
        + "isDefault = 1";

    public CategoryDAO()
    {
        super(Category.class);
    }

    public CategoryDAO(final EntityManager entityManager)
    {
        super(Category.class, entityManager);
    }

    public Category findDefault()
    {
        return findUniqueByProperty(Category.IS_DEFAULT_PROPERTY, "1");
    }

    public Category findByName(final String categoryName)
    {
        return findUniqueByProperty(Category.NAME_PROPERTY, categoryName);
    }

}
