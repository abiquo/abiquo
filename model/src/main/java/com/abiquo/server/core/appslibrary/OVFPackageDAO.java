package com.abiquo.server.core.appslibrary;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaOVFPackageDAO")
public class OVFPackageDAO extends DefaultDAOBase<Integer, OVFPackage>
{
    public OVFPackageDAO()
    {
        super(OVFPackage.class);
    }

    public OVFPackageDAO(final EntityManager entityManager)
    {
        super(OVFPackage.class, entityManager);
    }

}
