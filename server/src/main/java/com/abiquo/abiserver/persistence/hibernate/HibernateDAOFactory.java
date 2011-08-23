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

/**
 * 
 */
package com.abiquo.abiserver.persistence.hibernate;

import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.abiquo.abiserver.business.hibernate.pojohb.authorization.OneTimeTokenSessionHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkAssignmentHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualApplianceConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.EnterpriseExclusionRuleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.FitPolicyRuleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.workload.MachineLoadRuleHB;
import com.abiquo.abiserver.persistence.DAO;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.authorization.OneTimeTokenSessionDAO;
import com.abiquo.abiserver.persistence.dao.authorization.hibernate.OneTimeTokenSessionDAOHibernate;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.DatastoreDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.HyperVisorDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.RackDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.RemoteServiceDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.hibernate.DataCenterDAOHibernate;
import com.abiquo.abiserver.persistence.dao.infrastructure.hibernate.DatastoreDAOHibernate;
import com.abiquo.abiserver.persistence.dao.infrastructure.hibernate.HyperVisorDAOHibernate;
import com.abiquo.abiserver.persistence.dao.infrastructure.hibernate.PhysicalMachineDAOHibernate;
import com.abiquo.abiserver.persistence.dao.infrastructure.hibernate.RackDAOHibernate;
import com.abiquo.abiserver.persistence.dao.infrastructure.hibernate.RemoteServiceDAOHibernate;
import com.abiquo.abiserver.persistence.dao.metering.MeterDAO;
import com.abiquo.abiserver.persistence.dao.metering.hibernate.MeterDAOHibernate;
import com.abiquo.abiserver.persistence.dao.networking.DHCPServiceDAO;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkAssigmntDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkConfigurationDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkDAO;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.dao.networking.hibernate.DHCPServiceDAOHibernate;
import com.abiquo.abiserver.persistence.dao.networking.hibernate.IpPoolManagementDAOHibernate;
import com.abiquo.abiserver.persistence.dao.networking.hibernate.NetworkAssigmntDAOHibernate;
import com.abiquo.abiserver.persistence.dao.networking.hibernate.NetworkConfigurationDAOHibernate;
import com.abiquo.abiserver.persistence.dao.networking.hibernate.NetworkDAOHibernate;
import com.abiquo.abiserver.persistence.dao.networking.hibernate.VlanNetworkDAOHibernate;
import com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO;
import com.abiquo.abiserver.persistence.dao.user.RoleDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.user.UserSessionDAO;
import com.abiquo.abiserver.persistence.dao.user.hibernate.EnterpriseDAOHibernate;
import com.abiquo.abiserver.persistence.dao.user.hibernate.RoleDAOHibernate;
import com.abiquo.abiserver.persistence.dao.user.hibernate.UserDAOHibernate;
import com.abiquo.abiserver.persistence.dao.user.hibernate.UserSessionDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceConversionsDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate.NodeVirtualImageDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate.VirtualApplianceConversionsDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate.VirtualApplianceDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate.VirtualDataCenterDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate.VirtualMachineDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceAllocationSettingDataDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceManagementDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.hibernate.ResourceAllocationSettingDataDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualhardware.hibernate.ResourceManagementDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualimage.CategoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.IconDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.RepositoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageConversionsDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.hibernate.CategoryDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualimage.hibernate.IconDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualimage.hibernate.RepositoryDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualimage.hibernate.VirtualImageConversionsDAOHibernate;
import com.abiquo.abiserver.persistence.dao.virtualimage.hibernate.VirtualImageDAOHibernate;
import com.abiquo.abiserver.persistence.dao.workload.EnterpriseExclusionRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.FitPolicyRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.MachineLoadRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.hibernate.EnterpriseExclusionRuleDAOHibernate;
import com.abiquo.abiserver.persistence.dao.workload.hibernate.FitPolicyRuleDAOHibernate;
import com.abiquo.abiserver.persistence.dao.workload.hibernate.MachineLoadRuleDAOHibernate;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * @author jdevesa
 */
public class HibernateDAOFactory implements DAOFactory
{

    /**
     * HibernateDAOfactory
     */
    private static HibernateDAOFactory hibernateDAOfactory = null;

    /**
     * Entity to manage sessions
     */
    private static SessionFactory sessionFactory = null;

    /**
     * @return the sessionFactory
     */
    public static SessionFactory getSessionFactory()
    {
        if (sessionFactory == null) // XXX WTF
        {
            sessionFactory = HibernateUtil.getSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * This class creates an instance of the DAO factory and initializes the Hibernate Session
     * 
     * @return
     * @throws URISyntaxException
     * @throws HibernateException
     */
    public static DAOFactory instance() throws HibernateException
    {
        // create the hibernatefactory
        if (hibernateDAOfactory == null)
        {
            try
            {
                hibernateDAOfactory = new HibernateDAOFactory();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new RuntimeException("Couldn't create DAOFactory: HibernateDAOFactory", ex);
            }

            createSessionFactory();
        }

        return hibernateDAOfactory;
    }

    protected static void createSessionFactory()
    {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public static void setSessionFactory(final SessionFactory sessionFactory)
    {
        HibernateDAOFactory.sessionFactory = sessionFactory;
    }

    @Override
    public boolean pingDB()
    {
        try
        {
            beginConnection();

            Query pingQuery =
                getSessionFactory().getCurrentSession().createSQLQuery("SELECT 1 FROM DUAL");

            pingQuery.list();

            endConnection();

            return true;
        }
        catch (Exception ex)
        {
            Transaction tx = getSessionFactory().getCurrentSession().getTransaction();
            if (tx != null && tx.isActive())
            {
                tx.rollback();
            }

            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#beginConnection()
     */
    @Override
    public void beginConnection()
    {
        getSessionFactory().getCurrentSession().beginTransaction();
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#endConnection()
     */
    @Override
    public void endConnection()
    {
        Transaction tx = getSessionFactory().getCurrentSession().getTransaction();
        if (tx != null && tx.isActive())
        {
            tx.commit();
        }
    }

    @Override
    public boolean isTransactionActive()
    {
        Transaction tx = getSessionFactory().getCurrentSession().getTransaction();
        return tx != null && tx.isActive();
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getResourceAllocationSettingDataDAO()
     */
    @Override
    public ResourceAllocationSettingDataDAO getResourceAllocationSettingDataDAO()
    {
        return (ResourceAllocationSettingDataDAO) instantiateDAO(
            ResourceAllocationSettingDataDAOHibernate.class, ResourceAllocationSettingData.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getResourceManagementDAO()
     */
    @Override
    public ResourceManagementDAO getResourceManagementDAO()
    {
        return (ResourceManagementDAO) instantiateDAO(ResourceManagementDAOHibernate.class,
            ResourceManagementHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getVirtualApplianceDAO()
     */
    @Override
    public VirtualApplianceDAO getVirtualApplianceDAO()
    {
        return (VirtualApplianceDAO) instantiateDAO(VirtualApplianceDAOHibernate.class,
            VirtualappHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getVirtualDataCenterDAO()
     */
    @Override
    public VirtualDataCenterDAO getVirtualDataCenterDAO()
    {
        return (VirtualDataCenterDAO) instantiateDAO(VirtualDataCenterDAOHibernate.class,
            VirtualDataCenterHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getMeterDAO()
     */
    @Override
    public MeterDAO getMeterDAO()
    {
        return (MeterDAO) instantiateDAO(MeterDAOHibernate.class, MeterHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getUserDAO()
     */
    @Override
    public UserDAO getUserDAO()
    {
        return (UserDAO) instantiateDAO(UserDAOHibernate.class, UserHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getDataCenterDAO()
     */
    @Override
    public DataCenterDAO getDataCenterDAO()
    {
        return (DataCenterDAO) instantiateDAO(DataCenterDAOHibernate.class, DatacenterHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getHyperVisorDAO()
     */
    @Override
    public HyperVisorDAO getHyperVisorDAO()
    {
        return (HyperVisorDAO) instantiateDAO(HyperVisorDAOHibernate.class, HypervisorHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getPhysicalMachineDAO()
     */
    @Override
    public PhysicalMachineDAO getPhysicalMachineDAO()
    {
        return (PhysicalMachineDAO) instantiateDAO(PhysicalMachineDAOHibernate.class,
            PhysicalmachineHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getRackDAO()
     */
    @Override
    public RackDAO getRackDAO()
    {
        return (RackDAO) instantiateDAO(RackDAOHibernate.class, RackHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getEnterpriseDAO()
     */
    @Override
    public EnterpriseDAO getEnterpriseDAO()
    {
        return (EnterpriseDAO) instantiateDAO(EnterpriseDAOHibernate.class, EnterpriseHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getVirtualMachineDAO()
     */
    @Override
    public VirtualMachineDAO getVirtualMachineDAO()
    {
        return (VirtualMachineDAO) instantiateDAO(VirtualMachineDAOHibernate.class,
            VirtualmachineHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getVirtualImageDAO()
     */
    @Override
    public VirtualImageDAO getVirtualImageDAO()
    {
        return (VirtualImageDAO) instantiateDAO(VirtualImageDAOHibernate.class,
            VirtualimageHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getCategoryDAO()
     */
    @Override
    public CategoryDAO getCategoryDAO()
    {
        return (CategoryDAO) instantiateDAO(CategoryDAOHibernate.class, CategoryHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getIconDAO()
     */
    @Override
    public IconDAO getIconDAO()
    {
        return (IconDAO) instantiateDAO(IconDAOHibernate.class, IconHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getRepositoryDAO()
     */
    @Override
    public RepositoryDAO getRepositoryDAO()
    {
        return (RepositoryDAO) instantiateDAO(RepositoryDAOHibernate.class, RepositoryHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getNodeVirtualImageDAO()
     */
    @Override
    public NodeVirtualImageDAO getNodeVirtualImageDAO()
    {
        return (NodeVirtualImageDAO) instantiateDAO(NodeVirtualImageDAOHibernate.class,
            NodeVirtualImageHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#getRoleDAO()
     */
    @Override
    public RoleDAO getRoleDAO()
    {
        return (RoleDAO) instantiateDAO(RoleDAOHibernate.class, RoleHB.class);
    }

    @Override
    public VirtualImageConversionsDAO getVirtualImageConversionsDAO()
    {
        return (VirtualImageConversionsDAO) instantiateDAO(
            VirtualImageConversionsDAOHibernate.class, VirtualImageConversionsHB.class);
    }

    @Override
    public VirtualApplianceConversionsDAO getVirtualApplianceConversionsDAO()
    {
        return (VirtualApplianceConversionsDAO) instantiateDAO(
            VirtualApplianceConversionsDAOHibernate.class, VirtualApplianceConversionsHB.class);
    }

    @Override
    public RemoteServiceDAO getRemoteServiceDAO()
    {
        return (RemoteServiceDAO) instantiateDAO(RemoteServiceDAOHibernate.class,
            RemoteServiceHB.class);
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.persistence.DAOFactory#rollbackConnection()
     */
    @Override
    public void rollbackConnection()
    {
        if (sessionFactory.getCurrentSession().getTransaction().isActive())
        {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }

    }

    /**
     * Generates a new DAO class
     * 
     * @param daoClass class to be instantiated
     * @param persistentClass class to persist with this DAO
     * @return the new Generic DAO class
     */
    @SuppressWarnings("unchecked")
    protected DAO instantiateDAO(final Class daoClass, final Class persistentClass)
    {
        try
        {
            HibernateDAO dao = (HibernateDAO) daoClass.newInstance();
            dao.setSession(getSessionFactory().getCurrentSession());
            dao.setPersistentClass(persistentClass);
            return dao;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
        }
    }

    @Override
    public DHCPServiceDAO getDHCPServiceDAO()
    {
        return (DHCPServiceDAO) instantiateDAO(DHCPServiceDAOHibernate.class, DHCPServiceHB.class);
    }

    @Override
    public IpPoolManagementDAO getIpPoolManagementDAO()
    {
        return (IpPoolManagementDAO) instantiateDAO(IpPoolManagementDAOHibernate.class,
            IpPoolManagementHB.class);
    }

    @Override
    public NetworkConfigurationDAO getNetworkConfigurationDAO()
    {
        return (NetworkConfigurationDAO) instantiateDAO(NetworkConfigurationDAOHibernate.class,
            NetworkConfigurationHB.class);
    }

    @Override
    public NetworkDAO getNetworkDAO()
    {
        return (NetworkDAO) instantiateDAO(NetworkDAOHibernate.class, NetworkHB.class);
    }

    @Override
    public VlanNetworkDAO getVlanNetworkDAO()
    {
        return (VlanNetworkDAO) instantiateDAO(VlanNetworkDAOHibernate.class, VlanNetworkHB.class);
    }

    @Override
    public DatastoreDAO getDatastoreDAO()
    {
        return (DatastoreDAO) instantiateDAO(DatastoreDAOHibernate.class, DatastoreHB.class);
    }

    @Override
    public EnterpriseExclusionRuleDAO getEnterpriseExclusionRuleDAO()
    {
        return (EnterpriseExclusionRuleDAO) instantiateDAO(
            EnterpriseExclusionRuleDAOHibernate.class, EnterpriseExclusionRuleHB.class);
    }

    @Override
    public MachineLoadRuleDAO getMachineLoadRuleDAO()
    {
        return (MachineLoadRuleDAO) instantiateDAO(MachineLoadRuleDAOHibernate.class,
            MachineLoadRuleHB.class);
    }

    @Override
    public FitPolicyRuleDAO getFitPolicyRuleDAO()
    {
        return (FitPolicyRuleDAO) instantiateDAO(FitPolicyRuleDAOHibernate.class,
            FitPolicyRuleHB.class);
    }

    @Override
    public NetworkAssigmntDAO getNetworkAssigmentDAO()
    {
        return (NetworkAssigmntDAO) instantiateDAO(NetworkAssigmntDAOHibernate.class,
            NetworkAssignmentHB.class);
    }

    @Override
    public UserSessionDAO getUserSessionDAO()
    {
        return (UserSessionDAO) instantiateDAO(UserSessionDAOHibernate.class, UserSession.class);
    }

    @Override
    public OneTimeTokenSessionDAO getOneTimeTokenSessionDAO()
    {
        return (OneTimeTokenSessionDAO) instantiateDAO(OneTimeTokenSessionDAOHibernate.class,
            OneTimeTokenSessionHB.class);
    }
}
