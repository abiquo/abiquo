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

package com.abiquo.server.core.cloud;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.infrastructure.network.Dhcp;
import com.abiquo.server.core.infrastructure.network.DhcpDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement.OrderByEnum;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDAO;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentDAO;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.NetworkConfigurationDAO;
import com.abiquo.server.core.infrastructure.network.NetworkDAO;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;

@Repository
public class VirtualDatacenterRep extends DefaultRepBase
{
    @Autowired
    DhcpDAO dhcpDAO;

    @Autowired
    IpPoolManagementDAO ipManagementDAO;

    @Autowired
    NetworkAssignmentDAO naDao;

    @Autowired
    NetworkConfigurationDAO networkConfigDAO;

    @Autowired
    NetworkDAO networkDAO;

    @Autowired
    NodeVirtualImageDAO nodeviDao;

    @Autowired
    RasdDAO rasdDAO;

    @Autowired
    RasdManagementDAO rasdManagementDAO;

    @Autowired
    VirtualApplianceDAO virtualApplianceDAO;

    @Autowired
    VLANNetworkDAO vlanDAO;

    @Autowired
    VirtualMachineDAO vmDao;

    @Autowired
    private VirtualDatacenterDAO virtualDatacenterDAO;

    public VirtualDatacenterRep()
    {

    }

    public VirtualDatacenterRep(final EntityManager em)
    {
        this.entityManager = em;
        this.virtualDatacenterDAO = new VirtualDatacenterDAO(em);
        this.vlanDAO = new VLANNetworkDAO(em);
        this.networkDAO = new NetworkDAO(em);
        this.dhcpDAO = new DhcpDAO(em);
        this.ipManagementDAO = new IpPoolManagementDAO(em);
        this.virtualApplianceDAO = new VirtualApplianceDAO(em);
        this.rasdManagementDAO = new RasdManagementDAO(em);
        this.rasdDAO = new RasdDAO(em);
        this.networkConfigDAO = new NetworkConfigurationDAO(em);
        this.dhcpDAO = new DhcpDAO(em);
        this.vmDao = new VirtualMachineDAO(em);
        this.nodeviDao = new NodeVirtualImageDAO(em);
    }

    /**
     * Creates teh nodevirtualimage to associate the virtual machine to a virtual appliance
     */
    public NodeVirtualImage associateToVirtualAppliance(final String name,
        final VirtualMachine vmachine, final VirtualAppliance vapp)
    {
        assert vmachine.getVirtualImage() != null;

        NodeVirtualImage nvi =
            new NodeVirtualImage(name, vapp, vmachine.getVirtualImage(), vmachine);

        nodeviDao.persist(nvi);

        return nvi;
    }

    public boolean containsResources(final VirtualDatacenter virtualDatacenter,
        final String idResource)
    {
        return !findResourcesByVirtualDatacenterAndResourceType(virtualDatacenter, idResource)
            .isEmpty();
    }

    public boolean containsVirtualAppliances(final VirtualDatacenter virtualDatacenter)
    {
        return !findVirtualAppliancesByVirtualDatacenter(virtualDatacenter).isEmpty();
    }

    public void delete(final VirtualDatacenter vdc)
    {
        Collection<VLANNetwork> vlans = findVlansByVirtualDatacener(vdc);

        for (VLANNetwork vlan : vlans)
        {
            vlanDAO.remove(vlan);
        }

        networkDAO.remove(vdc.getNetwork());
        virtualDatacenterDAO.remove(vdc);
    }

    public void deleteNodeVirtualImage(final NodeVirtualImage nvi)
    {
        // TODO deassociate
        nodeviDao.remove(nvi);
    }

    public void deleteVirtualMachine(final VirtualMachine vmachine)
    {
        vmDao.remove(vmachine);
    }

    public void deleteVLAN(final VLANNetwork vlanToDelete)
    {
        vlanDAO.remove(vlanToDelete);
    }

    public boolean existAnyIpWithMac(final String mac)
    {
        return ipManagementDAO.existsAnyWithMac(mac);
    }

    public boolean existAnyVlanWithName(final Network network, final String name)
    {
        return vlanDAO.existsAnyWithName(network, name);
    }

    public Collection<VirtualDatacenter> findAll()
    {
        return this.virtualDatacenterDAO.findAll();
    }

    public Collection<VLANNetwork> findAllVlans()
    {
        return this.vlanDAO.findAll();
    }

    public Collection<VirtualDatacenter> findByEnterprise(final Enterprise enterprise)
    {
        return virtualDatacenterDAO.findByEnterprise(enterprise);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenter(final Enterprise enterprise,
        final Datacenter datacenter)
    {
        return this.virtualDatacenterDAO.findByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenter(final Enterprise enterprise,
        final Datacenter datacenter, final User user)
    {
        return this.virtualDatacenterDAO
            .findByEnterpriseAndDatacenter(enterprise, datacenter, user);
    }

    public VirtualDatacenter findById(final Integer id)
    {
        assert id != null;

        return this.virtualDatacenterDAO.findById(id);
    }

    public VirtualDatacenter findByName(final String name)
    {
        return virtualDatacenterDAO.findUniqueByProperty(VirtualDatacenter.NAME_PROPERTY, name);
    }

    public IpPoolManagement findIp(final VLANNetwork vlan, final Integer ipId)
    {
        return ipManagementDAO.findIp(vlan, ipId);
    }

    /**
     * Return all the private IPs by Enterprise
     * 
     * @param entId enterprise identifier
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByEnterprise(final Integer entId, final Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderBy,
        final Boolean asc)
    {
        return ipManagementDAO.findIpsByEnterprise(entId, firstElem, numElem, has, orderBy, asc);
    }

    /**
     * Return all the private IPs by VLAN.
     * 
     * @param vdcId virtual datacenter identifier.
     * @param vlanId vlan identifier.
     * @return All the IPs of the VLAN.
     */
    public List<IpPoolManagement> findIpsByPrivateVLAN(final Integer vdcId, final Integer vlanId)
    {
        return ipManagementDAO.findIpsByPrivateVLAN(vdcId, vlanId);
    }

    /**
     * Return all the available private IPs by VLAN with filter options.
     * 
     * @param vlanId identifier of the vlan
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByPrivateVLANAvailableFiltered(final Integer vdcId,
        final Integer vlanId, final Integer firstElem, final Integer numElem, final String has,
        final IpPoolManagement.OrderByEnum orderBy, final Boolean asc)
    {
        return ipManagementDAO.findIpsByPrivateVLANAvailableFiltered(vdcId, vlanId, firstElem,
            numElem, has, orderBy, asc);
    }

    /**
     * Return all the private IPs by VLAN with filter options.
     * 
     * @param vlanId identifier of the vlan
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByPrivateVLANFiltered(final Integer vdcId,
        final Integer vlanId, final Integer firstElem, final Integer numElem, final String has,
        final IpPoolManagement.OrderByEnum orderBy, final Boolean asc)
    {
        return ipManagementDAO.findIpsByPrivateVLANFiltered(vdcId, vlanId, firstElem, numElem, has,
            orderBy, asc);
    }

    /**
     * Return all the private IPs by Virtual Datacenter
     * 
     * @param vdcId identifier of the virtual datacenter
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByVdc(final Integer vdcId, final Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderBy,
        final Boolean asc)
    {
        return ipManagementDAO.findIpsByVdc(vdcId, firstElem, numElem, has, orderBy, asc);
    }

    /**
     * Return all the private IPs used by Virtual Appliance.
     * 
     * @param vappId enterprise identifier
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByVirtualAppliance(final VirtualAppliance vapp)
    {
        return ipManagementDAO.findIpsByVirtualAppliance(vapp);
    }

    public List<IpPoolManagement> findIpsByVirtualMachine(final VirtualMachine vm)
    {
        return ipManagementDAO.findIpsByVirtualMachine(vm);
    }

    public Collection<NodeVirtualImage> findNodeVirtualImageByEnterprise(final Enterprise enterprise)
    {
        return nodeviDao.findByEnterprise(enterprise);
    }

    public NodeVirtualImage findNodeVirtualImageByVirtualMachine(final VirtualMachine vmachine)
    {
        return nodeviDao.findByVirtualMachine(vmachine);
    }

    public List<IpPoolManagement> findPublicIpsByDatacenter(final Integer datacenterId,
        final Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc)
    {
        return ipManagementDAO.findPublicIpsByDatacenter(datacenterId, startwith, limit, filter,
            orderByEnum, descOrAsc);
    }

    public List<IpPoolManagement> findPublicIpsByVlan(final Integer datacenterId,
        final Integer vlanId, final Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc, final Boolean all)
    {
        return ipManagementDAO.findPublicIpsByVlan(datacenterId, vlanId, startwith, limit, filter,
            orderByEnum, descOrAsc, all);
    }

    public List<IpPoolManagement> findPublicIpsPurchasedByVirtualDatacenter(final Integer vdcId,
        final Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc)
    {
        return ipManagementDAO.findpublicIpsPurchasedByVirtualDatacenter(vdcId, startwith, limit,
            filter, orderByEnum, descOrAsc);
    }

    public List<IpPoolManagement> findPublicIpsToPurchaseByVirtualDatacenter(final Integer vdcId,
        final Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc)
    {
        return ipManagementDAO.findpublicIpsToPurchaseByVirtualDatacenter(vdcId, startwith, limit,
            filter, orderByEnum, descOrAsc);
    }

    public Collection<RasdManagement> findResourcesByVirtualDatacenterAndResourceType(
        final VirtualDatacenter virtualDatacenter, final String idResource)
    {
        return rasdManagementDAO.findByVirtualDatacenterAndResourceType(virtualDatacenter,
            idResource);
    }

    /**
     * Return the used Ips of a private VLAN
     * 
     * @param vdcId virtual datacenter identifier.
     * @param vlanId vlan identifier.
     * @return List of IpPoolManagement used by an virtual machine.
     */
    public List<IpPoolManagement> findUsedIpsByPrivateVLAN(final Integer vdcId, final Integer vlanId)
    {
        return ipManagementDAO.findUsedIpsByPrivateVLAN(vdcId, vlanId);
    }

    public VirtualAppliance findVirtualApplianceById(final Integer vappId)
    {
        return virtualApplianceDAO.findById(vappId);
    }

    public VirtualAppliance findVirtualApplianceByName(final String name)
    {
        return virtualApplianceDAO.findByName(name);
    }

    public VirtualAppliance findVirtualApplianceByVirtualMachine(final VirtualMachine vmachine)
    {
        return nodeviDao.findVirtualAppliance(vmachine);
    }

    public Collection<VirtualAppliance> findVirtualAppliancesByVirtualDatacenter(
        final VirtualDatacenter virtualDatacenter)
    {
        return virtualApplianceDAO.findByVirtualDatacenter(virtualDatacenter);
    }

    public VirtualMachine findVirtualMachineById(final Integer virtualMachineId)
    {
        return vmDao.findById(virtualMachineId);
    }

    public VirtualMachine findVirtualMachineByName(final String name)
    {
        return vmDao.findByName(name);
    }

    public VLANNetwork findVlanByDefaultInVirtualDatacenter(
        final VirtualDatacenter virtualDatacenter)
    {
        return vlanDAO.findVlanByDefaultInVirtualDatacenter(virtualDatacenter);
    }

    public VLANNetwork findVlanById(final Integer id)
    {
        assert id != null;

        return vlanDAO.findById(id);
    }

    public VLANNetwork findVlanByName(final String name)
    {
        return vlanDAO.findUniqueByProperty(VLANNetwork.NAME_PROPERTY, name);
    }

    /**
     * Find a VLAN in a VDC by its name
     * 
     * @param vdc virtual datacenter that stores the VLAN
     * @param name name of the VLAN.
     * @return the VLAN.
     */
    public VLANNetwork findVlanByNameInNetwork(final Network network, final String name)
    {
        return vlanDAO.findVlanByNameInNetwork(network, name);
    }

    public VLANNetwork findVlanByVirtualDatacenterId(final VirtualDatacenter virtualdatacenter,
        final Integer vlanId)
    {
        return vlanDAO.findVlanByVirtualDatacenterId(virtualdatacenter, vlanId);
    }

    public Collection<VLANNetwork> findVlansByVirtualDatacener(
        final VirtualDatacenter virtualDatacenter)
    {
        assert virtualDatacenter != null;

        return this.vlanDAO.findVlanNetworks(virtualDatacenter);
    }

    public Collection<String> getAllMacs()
    {
        return ipManagementDAO.getAllMacs();
    }

    public void insert(final VirtualDatacenter vdc)
    {
        virtualDatacenterDAO.persist(vdc);
    }

    public void insertDhcp(final Dhcp dhcp)
    {
        dhcpDAO.persist(dhcp);
    }

    public void insertIpManagement(final IpPoolManagement ipManagement)
    {
        rasdDAO.persist(ipManagement.getRasd());
        ipManagementDAO.persist(ipManagement);
    }

    public void insertNetwork(final Network network)
    {
        networkDAO.persist(network);
    }

    public void insertNetworkAssignment(final NetworkAssignment na)
    {
        naDao.persist(na);
    }

    public void insertNetworkConfig(final NetworkConfiguration configuration)
    {
        networkConfigDAO.persist(configuration);
    }

    public void insertVirtualAppliance(final VirtualAppliance vapp)
    {
        virtualApplianceDAO.persist(vapp);
    }

    public void insertVirtualMachine(final VirtualMachine vm)
    {
        vmDao.persist(vm);
    }

    public void insertVlan(final VLANNetwork vlan)
    {
        vlanDAO.persist(vlan);
    }

    public void inserVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        virtualApplianceDAO.persist(virtualAppliance);
    }

    public void update(final VirtualDatacenter vdc)
    {
        virtualDatacenterDAO.flush();
    }

    public void updateIpManagement(final IpPoolManagement ip)
    {
        ipManagementDAO.flush();
    }

    public void updateVirtualAppliance(final VirtualAppliance vapp)
    {
        virtualApplianceDAO.flush();
    }

    public void updateVlan(final VLANNetwork vlan)
    {
        vlanDAO.flush();
    }

}
