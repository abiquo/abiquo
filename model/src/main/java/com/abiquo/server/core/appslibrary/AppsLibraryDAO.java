package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaAppsLibraryDAO")
public class AppsLibraryDAO extends DefaultDAOBase<Integer, AppsLibrary>
{
    public AppsLibraryDAO()
    {
        super(AppsLibrary.class);
    }

    public AppsLibraryDAO(final EntityManager entityManager)
    {
        super(AppsLibrary.class, entityManager);
    }

}
