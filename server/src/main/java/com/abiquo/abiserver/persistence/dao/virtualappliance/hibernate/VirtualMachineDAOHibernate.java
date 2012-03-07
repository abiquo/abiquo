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

package com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeTypeEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class VirtualMachineDAOHibernate extends HibernateDAO<VirtualmachineHB, Integer> implements
    VirtualMachineDAO
{
    private static final String VIRTUAL_MACHINE_SEARCH_BY_NAME_TRUNCATED =
        "from com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB as vm "
            + "where locate(:name, vm.name) = 1";

    private static final String VIRTUAL_MACHINE_SEARCH_VAPP =
        "VIRTUALMACHINE.VIRTUAL_MACHINE_SEARCH_VAPP";

    @Override
    public VirtualmachineHB findByUUID(String uuid) throws PersistenceException
    {
        VirtualmachineHB vmHB;

        try
        {
            vmHB =
                (VirtualmachineHB) getSession().createCriteria(VirtualmachineHB.class)
                    .add(Restrictions.eq("uuid", uuid)).uniqueResult();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return vmHB;
    }

    @Override
    public VirtualmachineHB findByName(String name) throws PersistenceException
    {
        VirtualmachineHB vmHB;

        try
        {
            vmHB =
                (VirtualmachineHB) getSession().createCriteria(VirtualmachineHB.class)
                    .add(Restrictions.eq("name", name)).uniqueResult();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return vmHB;
    }

    @Override
    public List<VirtualmachineHB> findByNameTruncated(String name) throws PersistenceException
    {
        List<VirtualmachineHB> vmHB;

        try
        {
            Query query = getSession().createQuery(VIRTUAL_MACHINE_SEARCH_BY_NAME_TRUNCATED);
            query.setString("name", name);

            vmHB = (List<VirtualmachineHB>) query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return vmHB;
    }

    @Override
    public List<VirtualmachineHB> findByDatastore(Integer datastoreId) throws PersistenceException
    {
        List<VirtualmachineHB> listOfvmHB = new ArrayList<VirtualmachineHB>();

        try
        {
            listOfvmHB =
                getSession().createCriteria(VirtualmachineHB.class)
                    .add(Restrictions.eq("datastore.idDatastore", datastoreId)).list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return listOfvmHB;
    }

    @Override
    public VirtualappHB findVirtualAppFromVM(Integer vmID) throws PersistenceException
    {
        VirtualappHB virtualappHB;

        try
        {
            Query query = getSession().getNamedQuery(VIRTUAL_MACHINE_SEARCH_VAPP);
            query.setInteger("idVm", vmID);
            query.setParameter("type", NodeTypeEnum.VIRTUAL_IMAGE);

            virtualappHB = (VirtualappHB) query.uniqueResult();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return virtualappHB;

    }

}
