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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeTypeEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.model.enumerator.HypervisorType;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class VirtualApplianceDAOHibernate extends HibernateDAO<VirtualappHB, Integer> implements
    VirtualApplianceDAO
{

    private static final String FIND_BY_USED_VIRTUAL_IMAGE = "FIND_BY_USED_VIRTUAL_IMAGE";

    private static final String FIND_BY_USED_VIRTUAL_IMAGE_ON_REPOSITORY =
        "FIND_BY_USED_VIRTUAL_IMAGE_ON_REPOSITORY";

    private static final String BASIC = "VirtualappHB";

    private static final String EXTENDED = "VirtualappExtendedHB";

    private static final String VIRTUAL_APPLIANCE_BY_VIRTUAL_MACHINE_ID =
        "VIRTUAL_APPLIANCE_BY_VIRTUAL_MACHINE_ID";

    private static final String VIRTUAL_APPLIANCES_BY_ENTERPRISE =
        "VIRTUAL_APPLIANCES_BY_ENTERPRISE";

    private static final String VIRTUAL_APPLIANCES_BY_ENTERPRISE_AND_DATACENTER =
        "VIRTUAL_APPLIANCES_BY_ENTERPRISE_AND_DATACENTER";

    private static final String VIRTUAL_DATACENTER_ID_BY_VIRTUAL_APP_ID =
        "VIRTUAL_DATACENTER_ID_BY_VIRTUAL_APP_ID";

    @Override
    public Integer getVirtualDatacenterId(final Integer idVirtualApp)
    {
        Query query = getSession().getNamedQuery(VIRTUAL_DATACENTER_ID_BY_VIRTUAL_APP_ID);
        query.setInteger("idVirtualApp", idVirtualApp);

        return (Integer) query.uniqueResult();
    }

    @Override
    public VirtualappHB findByIdNamed(final Integer id)
    {
        return (VirtualappHB) getSession().get(BASIC, id);
    }

    @Override
    public VirtualappHB findByIdNamedExtended(final Integer id)
    {
        return (VirtualappHB) getSession().get(EXTENDED, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    // generic Hibernate query list cast
    public List<VirtualappHB> findByUsingVirtualImage(final String virtualImageId)
        throws PersistenceException
    {
        List<VirtualappHB> apps;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(FIND_BY_USED_VIRTUAL_IMAGE);
            query.setString("usedVIId", virtualImageId);
            query.setParameter("type", NodeTypeEnum.VIRTUAL_IMAGE);

            apps = query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return apps;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<VirtualappHB> findByUsingVirtualImageOnRepository(final Integer idRepository)
    {
        List<VirtualappHB> apps;
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery(FIND_BY_USED_VIRTUAL_IMAGE_ON_REPOSITORY);
        query.setInteger("idRepo", idRepository);
        query.setParameter("type", NodeTypeEnum.VIRTUAL_IMAGE);

        apps = query.list();

        return apps;
    }

    @Override
    @SuppressWarnings("unchecked")
    // generic Hibernate query list cast
    public List<VirtualappHB> findAllDeployed() throws PersistenceException
    {
        List<VirtualappHB> apps;

        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query =
                session.createQuery(
                    "SELECT va FROM VirtualappExtendedHB as va WHERE va.state not in (:states)")
                    .setParameterList("states",
                        new StateEnum[] {StateEnum.NOT_DEPLOYED, StateEnum.IN_PROGRESS});

            apps = query.list();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }

        return apps;
    }

    @Override
    public VirtualappHB getVirtualAppByVirtualMachine(final Integer vmId)
        throws PersistenceException
    {
        try
        {
            Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
            Query query = session.getNamedQuery(VIRTUAL_APPLIANCE_BY_VIRTUAL_MACHINE_ID);
            query.setInteger("vmId", vmId);
            return (VirtualappHB) query.uniqueResult();
        }
        catch (HibernateException e)
        {
            throw new PersistenceException(e.getMessage(), e);
        }
    }

    /**
     * Checks if the state of a given virtual appliance is actually the last valid state in the Data
     * Base If it is the same, the state of the virtual appliance will be updated to
     * State.IN_PROGRESS, and a boolean will be returned to true, to indicate that the virtual
     * appliance can be manipulated Otherwise, the current state will be returned, and the boolean
     * will be set to false, indicating that the virtual appliance can not be manipulated
     * 
     * @param virtualAppliance The virtual appliance that will be checked
     * @param subState the subState associated to the IN_PROGRESS state
     * @return A DataResult object, containing a boolean that indicates if the virtual appliance can
     *         be manipulated and, in any case, it will contain the virtualAppliance with the
     *         current values in Data Base (this returned VirtualAppliance will also contain the
     *         node list!)
     * @throws Exception An Exception is thrown if there was a problem connecting to the Data base
     */
    @Override
    public DataResult<VirtualAppliance> checkVirtualApplianceState(
        final VirtualAppliance virtualAppliance, final StateEnum subState) throws Exception
    {

        DataResult<VirtualAppliance> currentStateAndAllow = new DataResult<VirtualAppliance>();

        // Getting the current saved values for this Virtual Appliance
        VirtualappHB virtualAppHB =
            (VirtualappHB) getSession().get("VirtualappExtendedHB", virtualAppliance.getId());

        StateEnum previousState = StateEnum.valueOf(virtualAppliance.getState().getDescription());
        if (previousState == virtualAppHB.getState() && previousState != StateEnum.IN_PROGRESS)
        {
            // The given virtual appliance is up to date, and is not in
            // progress.
            // We set it now to IN_PROGRESS, and return that it is allowed
            // to manipulate it
            virtualAppHB.setState(StateEnum.IN_PROGRESS);
            virtualAppHB.setSubState(subState);

            getSession().update("VirtualappHB", virtualAppHB);

            // Generating the result
            currentStateAndAllow.setSuccess(true);
            currentStateAndAllow.setData(virtualAppHB.toPojo());
        }
        else
        {
            // The given virtual appliance is not up to date, or the virtual
            // appliance
            // is already in the state State.IN_PROGRESS. Manipulating it is
            // not allowed

            // Generating the result
            currentStateAndAllow.setSuccess(false);
            currentStateAndAllow.setData(virtualAppHB.toPojo());
        }

        return currentStateAndAllow;
    }

    @Override
    public VirtualappHB blockVirtualAppliance(final VirtualappHB virtualApp,
        final StateEnum subState) throws PersistenceException
    {
        if (virtualApp.getState() != StateEnum.IN_PROGRESS)
        {
            virtualApp.setState(StateEnum.IN_PROGRESS);
            virtualApp.setSubState(subState);

            makePersistent(virtualApp);
        }
        else
        {
            throw new PersistenceException("The virtual appliance is already blocked: "
                + virtualApp.getIdVirtualApp());
        }

        return virtualApp;
    }

    @Override
    public VirtualappHB makePersistentBasic(final VirtualappHB entity) throws PersistenceException
    {
        return makePersistent(BASIC, entity);
    }

    @Override
    public VirtualappHB makePersistentExtended(final VirtualappHB entity)
        throws PersistenceException
    {
        return makePersistent(EXTENDED, entity);
    }

    @Override
    public Collection<VirtualappHB> getVirtualAppliancesByEnterprise(final UserHB user,
        final Integer enterpriseId)
    {
        boolean isRestricted = !StringUtils.isEmpty(user.getAvailableVirtualDatacenters());

        String queryName =
            isRestricted ? "VIRTUAL_APPLIANCE_BY_ENTERPRISE_TINY_WITH_RESTRICTIONS"
                : "VIRTUAL_APPLIANCE_BY_ENTERPRISE_TINY";
        Query q = getSession().getNamedQuery(queryName);
        q.setParameter("enterpriseId", enterpriseId);
        if (isRestricted)
        {
            q.setParameterList("vdcs", getAvailableVdcs(user));
        }

        List<Object[]> results = q.list();
        return readVirtualApps(results);
    }

    @Override
    public Collection<VirtualappHB> getVirtualAppliancesByEnterpriseAndDatacenter(
        final UserHB user, final Integer enterpriseId, final Integer datacenterId)
    {
        boolean isRestricted = !StringUtils.isEmpty(user.getAvailableVirtualDatacenters());

        String queryName =
            isRestricted ? "VIRTUAL_APPLIANCE_BY_ENTERPRISE_AND_DC_TINY_WITH_RESTRICTIONS"
                : "VIRTUAL_APPLIANCE_BY_ENTERPRISE_AND_DC_TINY";
        Query q = getSession().getNamedQuery(queryName);
        q.setParameter("enterpriseId", enterpriseId);
        q.setParameter("datacenterId", datacenterId);
        if (isRestricted)
        {
            q.setParameterList("vdcs", getAvailableVdcs(user));
        }

        List<Object[]> results = q.list();
        return readVirtualApps(results);
    }

    private Collection<VirtualappHB> readVirtualApps(List<Object[]> results)
    {
        List<VirtualappHB> vapps = new ArrayList<VirtualappHB>();
        for (Object[] row : results)
        {
            VirtualappHB vapp = new VirtualappHB();
            vapp.setIdVirtualApp((Integer) row[0]);
            vapp.setName((String) row[1]);
            vapp.setHighDisponibility((Integer) row[2]);
            vapp.setState((StateEnum) row[3]);
            vapp.setSubState((StateEnum) row[4]);
            vapp.setError((Integer) row[5]);
            vapp.setPublic_((Integer) row[6]);
            vapp.setNodeConnections((String) row[7]);

            VirtualDataCenterHB vdc = new VirtualDataCenterHB();
            vdc.setIdVirtualDataCenter((Integer) row[8]);
            vdc.setName((String) row[9]);
            vdc.setIdDataCenter((Integer) row[10]);
            vdc.setHypervisorType((HypervisorType) row[11]);

            vapp.setVirtualDataCenterHB(vdc);

            vapps.add(vapp);
        }

        return vapps;
    }

    private Collection<Integer> getAvailableVdcs(final UserHB user)
    {
        String[] ids = user.getAvailableVirtualDatacenters().split(",");
        Collection<Integer> vdcs = new LinkedHashSet<Integer>();
        for (String id : ids)
        {
            if (org.springframework.util.StringUtils.hasText(id))
            {
                vdcs.add(Integer.parseInt(id));
            }
        }
        return vdcs;
    }
}
