/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.abiserver.persistence.dao.virtualimage.hibernate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class VirtualImageDAOHibernate extends HibernateDAO<VirtualimageHB, Integer> implements
    VirtualImageDAO
{
    /** Get images by user query. */
    private static final String GET_VIRTUAL_IMAGES_BY_USER_QUERY = "GET_VIRTUAL_IMAGES_BY_USER";

    private static final String GET_VIRTUAL_IMAGES_BY_ENTR_AND_REPO_AND_CATE_QUERY =
        "GET_VIRTUAL_IMAGES_BY_ENTER_AND_REPOSITORY_AND_CATEGORY";

    /** Get images by user and stateful query. */
    private static final String GET_VIRTUAL_IMAGES_BY_USER_AND_STATEFUL_QUERY =
        "GET_VIRTUAL_IMAGES_BY_USER_AND_STATEFUL";

    private static final String GET_VIRTUAL_IMAGES_BY_ENTR_AND_REPO =
        "GET_VIRTUAL_IMAGES_BY_ENTER_AND_REPOSITORY";

    private static final String GET_VIRTUAL_IMAGES_BY_REPOSITORY_QUERY =
        "GET_VIRTUAL_IMAGES_BY_REPOSITORY";

    @Override
    @SuppressWarnings("unchecked")
    public Collection<VirtualimageHB> getImagesFromPath(Collection<String> paths)
    {
        return getSession()
            .createQuery(
                "FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB "
                    + "WHERE pathName in (:pathName)").setParameterList("pathName", paths).list();

    }

    @Override
    public Collection<VirtualimageHB> findByCategory(Integer idCategory)
    {
        return getSession()
            .createQuery(
                "FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB "
                    + "WHERE category.idCategory = :idCategory")
            .setParameter("idCategory", idCategory).list();
    }

    @Override
    public Collection<VirtualimageHB> findByIcon(final Integer idIcon)
    {
        return getSession()
            .createQuery(
                "FROM com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB "
                    + "WHERE icon.idIcon = :idIcon").setParameter("idIcon", idIcon).list();
    }

    @Override
    public Collection<VirtualimageHB> getImagesByUser(String username)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);

        return findByNamedQuery(GET_VIRTUAL_IMAGES_BY_USER_QUERY, params);
    }

    @Override
    public Collection<VirtualimageHB> getImagesByUserAndRepositoryAndCategory(String username,
        Integer idRepository, Integer idCategory)
    {

        UserDAO userdao = HibernateDAOFactory.instance().getUserDAO();
        UserHB user = userdao.getUserByUserName(username);

        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idEnterprise", user.getEnterpriseHB().getIdEnterprise());
        params.put("idRepository", idRepository);
        params.put("idCategory", idCategory);

        return findByNamedQuery(GET_VIRTUAL_IMAGES_BY_ENTR_AND_REPO_AND_CATE_QUERY, params);
    }

    @Override
    public List<VirtualimageHB> getImagesByEnterpriseAndRepositoryAndCategory(Integer idEnterprise,
        Integer idRepository, Integer idCategory)
    {

        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idEnterprise", idEnterprise);
        params.put("idRepository", idRepository);
        params.put("idCategory", idCategory);

        return findByNamedQuery(GET_VIRTUAL_IMAGES_BY_ENTR_AND_REPO_AND_CATE_QUERY, params);
    }

    @Override
    public List<VirtualimageHB> getImagesByEnterpriseAndRepository(final Integer idEnterprise,
        final Integer idRepository)
    {

        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idEnterprise", idEnterprise);
        params.put("idRepository", idRepository);

        return findByNamedQuery(GET_VIRTUAL_IMAGES_BY_ENTR_AND_REPO, params);
    }

    @Override
    public List<VirtualimageHB> getImagesByRepository(final Integer idRepository)
    {
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("idRepository", idRepository);

        return findByNamedQuery(GET_VIRTUAL_IMAGES_BY_REPOSITORY_QUERY, params);
    }

    @Override
    public Collection<VirtualimageHB> getImagesByUser(String username, int stateful)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("stateful", stateful);

        return findByNamedQuery(GET_VIRTUAL_IMAGES_BY_USER_AND_STATEFUL_QUERY, params);
    }

}
