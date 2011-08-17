package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaIconDAO")
public class IconDAO extends DefaultDAOBase<Integer, Icon>
{
    public IconDAO()
    {
        super(Icon.class);
    }

    public IconDAO(final EntityManager entityManager)
    {
        super(Icon.class, entityManager);
    }

}
