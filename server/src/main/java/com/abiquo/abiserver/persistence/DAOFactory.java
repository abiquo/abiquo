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
package com.abiquo.abiserver.persistence;

import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.DatastoreDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.HyperVisorDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.RackDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.RemoteServiceDAO;
import com.abiquo.abiserver.persistence.dao.metering.MeterDAO;
import com.abiquo.abiserver.persistence.dao.networking.DHCPServiceDAO;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkAssigmntDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkConfigurationDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkDAO;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO;
import com.abiquo.abiserver.persistence.dao.user.RoleDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceConversionsDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceAllocationSettingDataDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceManagementDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.CategoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.IconDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.RepositoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageConversionsDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.workload.EnterpriseExclusionRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.FitPolicyRuleDAO;
import com.abiquo.abiserver.persistence.dao.workload.MachineLoadRuleDAO;

/**
 * The factoryDAO maintains the methods to create new DAO instances and provides abstract methods to
 * manage sessions and transactions
 * 
 * @author jdevesa
 */
public interface DAOFactory
{
    /**
     * Beggining a connection to create a serial of BusinessLayer to PersistenceLayer operation
     */
    public abstract void beginConnection() throws PersistenceException;

    /**
     * Finish the connection and persist the changes
     */
    public abstract void endConnection() throws PersistenceException;

    /**
     * Execute rollback function to the current connection
     */
    public abstract void rollbackConnection();

    /**
     * @return a Boolean value checking if connection is active
     */
    public abstract boolean isTransactionActive();

    /**
     * Check database connectivity.
     * 
     * @return Boolean indicating if there is connectivity to the database.
     */
    public boolean pingDB();

    // LIST OF ALL DAO INTERFACES THAT WE WILL NEED //

    // Network interfaces
    public abstract IpPoolManagementDAO getIpPoolManagementDAO();

    public abstract DHCPServiceDAO getDHCPServiceDAO();

    public abstract NetworkConfigurationDAO getNetworkConfigurationDAO();

    public abstract VlanNetworkDAO getVlanNetworkDAO();

    public abstract NetworkDAO getNetworkDAO();

    public abstract ResourceManagementDAO getResourceManagementDAO();

    public abstract ResourceAllocationSettingDataDAO getResourceAllocationSettingDataDAO();

    public abstract VirtualApplianceDAO getVirtualApplianceDAO();

    public abstract VirtualDataCenterDAO getVirtualDataCenterDAO();

    public abstract MeterDAO getMeterDAO();

    public abstract UserDAO getUserDAO();

    public abstract DataCenterDAO getDataCenterDAO();

    public abstract HyperVisorDAO getHyperVisorDAO();

    public abstract PhysicalMachineDAO getPhysicalMachineDAO();

    public abstract RackDAO getRackDAO();

    public abstract EnterpriseDAO getEnterpriseDAO();

    // public abstract ResourceAllocationLimitDAO getResourceAllocationLimitDAO();

    public abstract VirtualMachineDAO getVirtualMachineDAO();

    public abstract VirtualImageDAO getVirtualImageDAO();

    public abstract CategoryDAO getCategoryDAO();

    public abstract IconDAO getIconDAO();

    public abstract RepositoryDAO getRepositoryDAO();

    public abstract NodeVirtualImageDAO getNodeVirtualImageDAO();

    public abstract RoleDAO getRoleDAO();

    public abstract VirtualImageConversionsDAO getVirtualImageConversionsDAO();

    public abstract VirtualApplianceConversionsDAO getVirtualApplianceConversionsDAO();

    public abstract RemoteServiceDAO getRemoteServiceDAO();

    public abstract DatastoreDAO getDatastoreDAO();

    public abstract EnterpriseExclusionRuleDAO getEnterpriseExclusionRuleDAO();

    public abstract MachineLoadRuleDAO getMachineLoadRuleDAO();

    public abstract FitPolicyRuleDAO getFitPolicyRuleDAO();

    public abstract NetworkAssigmntDAO getNetworkAssigmentDAO();

}
