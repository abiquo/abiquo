package com.abiquo.server.core.appslibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;

public class OVFPackageRep extends DefaultRepBase
{

    @Autowired
    OVFPackageDAO dao;

    @Autowired
    OVFPackageListDAO listDao;

    @Autowired
    AppsLibraryDAO appsLibraryDao;

    @Autowired
    CategoryDAO categoryDao;

    @Autowired
    IconDAO iconDao;

    public OVFPackageRep(final EntityManager em)
    {
        dao = new OVFPackageDAO(em);
        listDao = new OVFPackageListDAO(em);
        categoryDao = new CategoryDAO(em);
        iconDao = new IconDAO(em);
    }

    public List<OVFPackage> getOVFPackagesByEnterprise(final Integer idEnterprise)
    {
        return dao.findByEnterprise(idEnterprise);
    }

    public OVFPackage addOVFPackage(final OVFPackage ovfPackage, final Enterprise enterprise)
    {
        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(enterprise);
        ovfPackage.setAppsLibrary(appsLib);
        dao.persist(ovfPackage);

        return ovfPackage;
    }

    public OVFPackage getOVFPackage(final Integer id)
    {
        return dao.findById(id);
    }

    public OVFPackage modifyOVFPackage(final Integer ovfPackageId, final OVFPackage ovfPackage,
        final Enterprise enterprise)
    {
        OVFPackage old = dao.findById(ovfPackageId);

        // TODO - Apply changes and compare etags
        old.setName(ovfPackage.getName());
        old.setDescription(ovfPackage.getDescription());

        AppsLibrary appsLib = appsLibraryDao.findByEnterprise(enterprise);
        ovfPackage.setAppsLibrary(appsLib);

        old.setCategory(ovfPackage.getCategory());
        old.setType(ovfPackage.getType());
        old.setIcon(ovfPackage.getIcon());
        old.setProductName(ovfPackage.getProductName());
        old.setProductUrl(ovfPackage.getProductUrl());
        old.setProductVendor(ovfPackage.getProductVendor());
        old.setProductVersion(ovfPackage.getProductVersion());
        old.setUrl(ovfPackage.getUrl());
        old.setOvfPackageLists(ovfPackage.getOvfPackageLists());

        dao.persist(old);

        return old;
    }

    public void removeOVFPackage(final Integer id)
    {
        OVFPackage ovfPackage = dao.findById(id);

        // manually remove lists associated
        // As OVFPackage<->OVFPackageLists is a Many-to-many relation, the delete operation
        // must be done manually for the dependant (not owner) side in the relation: OVFPackage in
        // this case
        List<OVFPackageList> lists = ovfPackage.getOvfPackageLists();
        for (Object element : lists)
        {
            OVFPackageList ovfPackageList = (OVFPackageList) element;
            ovfPackageList.getOvfPackages().remove(ovfPackage);
            listDao.persist(ovfPackageList);
        }

        dao.persist(ovfPackage);
    }

    public Icon findByIconPathOrCreateNew(final String iconPath)
    {
        if (iconPath == null)
        {
            return null;
        }

        Icon icon;

        icon = iconDao.findByPath(iconPath);

        if (icon == null)
        {
            icon = new Icon();
            icon.setName("unname"); // TODO
            icon.setPath(iconPath);

            iconDao.persist(icon);
        }

        return icon;
    }

    public Category findByCategoryNameOrCreateNew(final String categoryName)
    {
        if (categoryName == null || categoryName.isEmpty())
        {
            return categoryDao.findDefault();
        }

        Category cat = categoryDao.findByName(categoryName);

        if (cat == null)
        {
            cat = new Category();
            cat.setName(categoryName);
            cat.setIsDefault(0);
            cat.setIsErasable(1);
            categoryDao.persist(cat);
        }

        return cat;
    }

}
