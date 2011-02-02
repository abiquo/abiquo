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

package com.abiquo.abiserver.abicloudws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;

/**
 * This class agluttinate basic functionalities to interact with RemoteService
 * 
 * @author abiquo
 */
public class RemoteServiceUtils
{

    /** The logger object */
    private final static Logger logger = LoggerFactory.getLogger(RemoteServiceUtils.class);

    /**
     * Private helper to get the virtual factory destination address from the virtual appliance
     * object. Requires a nested session.
     * 
     * @param virtualAppliance the virtual appliance associated with a virtual datacenter
     * @return the address destination
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    public static String getVirtualFactoryFromVA(final VirtualAppliance virtualAppliance)
        throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();

        factory.beginConnection();
        DatacenterHB myDatacenter =
            datacenterDAO.findById(virtualAppliance.getVirtualDataCenter().getIdDataCenter());
        factory.endConnection();

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.VIRTUAL_FACTORY)
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no Virtual Factory remote service configured for this datacenter");
        }

        return destination;
    }

    /**
     * Private helper to get the VirtualSystemMonitorAddress address from the virtual appliance
     * object
     * 
     * @param virtualAppliance the virtual appliance associated with a virtual datacenter
     * @return the address destination
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    public static String getVirtualSystemMonitorFromVA(final VirtualAppliance virtualAppliance)
        throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();

        factory.beginConnection();
        DatacenterHB myDatacenter =
            datacenterDAO.findById(virtualAppliance.getVirtualDataCenter().getIdDataCenter());
        factory.endConnection();

         Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.VIRTUAL_SYSTEM_MONITOR)
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no VirtualSystemMonitor remote service configured for this datacenter");
        }

        return destination;
    }
    
    /**
     * Private helper to get the VirtualSystemMonitorAddress address from a physical machine
     * object
     * 
     * @param physicalMachine the physical machine
     * @return the address destination
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    public static String getVirtualSystemMonitorFromPhysicalMachine(final PhysicalMachine physicalMachine)
        throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();
        
        Rack rack = (Rack) physicalMachine.getAssignedTo();
        
        factory.beginConnection();
        DatacenterHB myDatacenter =
            datacenterDAO.findById(rack.getDataCenter().getId());
        factory.endConnection();

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.VIRTUAL_SYSTEM_MONITOR)
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no VirtualSystemMonitor remote service configured for this datacenter");
        }

        return destination;
    }

    /**
     * Private helper to get the NodeCollecto address from the datacenter id. Requires a nested
     * transaction
     * 
     * @param datacenterId the datacenter ID
     * @return the address destination
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    public static String getNodeCollectorFromDatacenter(final Integer datacenterId)
        throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();
        DatacenterHB myDatacenter = datacenterDAO.findById(datacenterId);

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.NODE_COLLECTOR)
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no NodeCollector remote service configured for this datacenter");
        }

        return destination;
    }
    
    /**
     * Private helper to get the Virtual system monitor address from the datacenter id. Requires a nested
     * transaction
     * 
     * @param datacenterId the datacenter ID
     * @return the address destination
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    public static String getVirtualSystemMonitorFromDatacenter(final Integer datacenterId)
        throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();
        DatacenterHB myDatacenter = datacenterDAO.findById(datacenterId);

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.VIRTUAL_SYSTEM_MONITOR)
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no Virtual system monitor remote service configured for this datacenter");
        }

        return destination;
    }

    /**
     * Private helper to get the NodeCollecto address from the datacenter id. Requires a nested
     * transaction
     * 
     * @param datacenterId the datacenter ID
     * @param the hibernate session
     * @return the address destination
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    @Deprecated
    // use RemoteServiceDAO
    public static String getNodeCollectorFromDatacenter(final Integer datacenterId,
        final Session session) throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DatacenterHB myDatacenter = (DatacenterHB) session.get(DatacenterHB.class, datacenterId);

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.NODE_COLLECTOR)
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no NodeCollector remote service configured for this datacenter");
        }

        return destination;
    }

    /**
     * Private helper to get the DHCP address. Requires a nested trabsaction
     * 
     * @param idDataCenter the datacenter id
     * @return the address destination
     * @throws RemoteServiceException
     * @throws PersistenceException
     */
    public static String getDhcpFromDatacenter(final Integer idDataCenter)
        throws PersistenceException, RemoteServiceException
    {
        String dhcpComplexAddress =
            getRemoteServiceByType(
                idDataCenter,
                com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.DHCP_SERVICE
                    .name());
        URI addressAddressURL = null;
        try
        {
            addressAddressURL = new URI(dhcpComplexAddress);
        }
        catch (URISyntaxException e)
        {
            logger.error("The dhcp address has a malformed URI:" + dhcpComplexAddress);
        }

        return addressAddressURL.getHost() + ":" + addressAddressURL.getPort();
    }

    /**
     * Private helper to get the remote service type from the idDatacenter and remote service type
     * 
     * @param idDataCenter the id datacenter
     * @param remoteServiceType the remote service type
     * @return the address
     * @throws PersistenceException
     * @throws RemoteServiceException
     */
    private static String getRemoteServiceByType(final Integer idDataCenter,
        final String remoteServiceType) throws PersistenceException, RemoteServiceException
    {
        String destination = null;
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();
        DatacenterHB myDatacenter = datacenterDAO.findById(idDataCenter);

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            if (remoteServiceHB.getRemoteServiceType() == RemoteServiceType
                .valueOf(remoteServiceType))
            {
                destination = remoteServiceHB.getUri();
                break;
            }
        }

        if (destination == null)
        {
            throw new RemoteServiceException("There is no remote service type: "
                + remoteServiceType + "configured for this datacenter");
        }

        return destination;
    }

    /**
     * Checks the remote services from the virtual appliance
     * 
     * @param virtualAppliance the virtual appliance to check its remote services
     * @throws RemoteServiceException, if a remote service check fails
     */
    public static void checkRemoteServicesFromVA(final VirtualAppliance virtualAppliance)
        throws PersistenceException, RemoteServiceException
    {
        DAOFactory factory = HibernateDAOFactory.instance();
        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();

        factory.beginConnection();
        DatacenterHB myDatacenter =
            datacenterDAO.findById(virtualAppliance.getVirtualDataCenter().getIdDataCenter());
        factory.endConnection();

        Set<RemoteServiceHB> remoteServices = myDatacenter.getRemoteServicesHB();
        for (RemoteServiceHB remoteServiceHB : remoteServices)
        {
            // For DHCP service and SSM the check is not done
            if (!remoteServiceHB.getRemoteServiceType().canBeChecked()
                || remoteServiceHB.getRemoteServiceType() == RemoteServiceType.STORAGE_SYSTEM_MONITOR)
            {
                continue;
            }

            RemoteServiceClient remoteServiceClient =
                new RemoteServiceClient(remoteServiceHB.getUri());
            remoteServiceClient.ping();
        }
    }

}
