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
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.infrastructure.network.Dhcp;
import com.abiquo.server.core.infrastructure.network.DhcpDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
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
    private VirtualDatacenterDAO virtualDatacenterDAO;

    @Autowired
    VLANNetworkDAO vlanDAO;

    @Autowired
    NetworkDAO networkDAO;

    @Autowired
    DhcpDAO dhcpDAO;

    @Autowired
    IpPoolManagementDAO ipManagementDAO;

    @Autowired
    NetworkConfigurationDAO networkConfigDAO;

    @Autowired
    VirtualApplianceDAO virtualApplianceDAO;

    @Autowired
    RasdManagementDAO rasdDAO;

    @Autowired
    NetworkAssignmentDAO naDao;

    @Autowired
    VirtualMachineDAO vmDao;

    @Autowired
    NodeVirtualImageDAO nodeviDao;

    public VirtualDatacenterRep()
    {

    }

    public VirtualDatacenterRep(EntityManager em)
    {
        this.entityManager = em;
        this.virtualDatacenterDAO = new VirtualDatacenterDAO(em);
        this.vlanDAO = new VLANNetworkDAO(em);
        this.networkDAO = new NetworkDAO(em);
        this.dhcpDAO = new DhcpDAO(em);
        this.ipManagementDAO = new IpPoolManagementDAO(em);
        this.virtualApplianceDAO = new VirtualApplianceDAO(em);
        this.rasdDAO = new RasdManagementDAO(em);
        this.networkConfigDAO = new NetworkConfigurationDAO(em);
        this.dhcpDAO = new DhcpDAO(em);
        this.vmDao = new VirtualMachineDAO(em);
        this.nodeviDao = new NodeVirtualImageDAO(em);
    }

    public VirtualDatacenter findById(Integer id)
    {
        assert id != null;

        return this.virtualDatacenterDAO.findById(id);
    }

    public Collection<VirtualDatacenter> findAll()
    {
        return this.virtualDatacenterDAO.findAll();
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenter(Enterprise enterprise,
        Datacenter datacenter, User user)
    {
        return this.virtualDatacenterDAO
            .findByEnterpriseAndDatacenter(enterprise, datacenter, user);
    }

    public Collection<VirtualDatacenter> findByEnterpriseAndDatacenter(Enterprise enterprise,
        Datacenter datacenter)
    {
        return this.virtualDatacenterDAO.findByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    public VLANNetwork findVlanById(Integer id)
    {
        assert id != null;

        return vlanDAO.findById(id);
    }

    public VLANNetwork findVlanByName(String name)
    {
        return vlanDAO.findUniqueByProperty(VLANNetwork.NAME_PROPERTY, name);
    }
    
    public Collection<VLANNetwork> findAllVlans()
    {
        return this.vlanDAO.findAll();
    }

    public Collection<VLANNetwork> findVlansByVirtualDatacener(VirtualDatacenter virtualDatacenter)
    {
        assert virtualDatacenter != null;

        return this.vlanDAO.findVLANNetworks(virtualDatacenter);
    }


    public VLANNetwork findVlanByDefault(VirtualDatacenter virtualDatacenter)
    {
        return vlanDAO.findByDefault(virtualDatacenter);
    }
    
    public void insertNetwork(Network network)
    {
        networkDAO.persist(network);
    }

    public boolean existAnyVlanWithName(Network network, String name)
    {
        return vlanDAO.existsAnyWithName(network, name);
    }

    public void insertDhcp(Dhcp dhcp)
    {
        dhcpDAO.persist(dhcp);
    }

    public void insertIpManagement(IpPoolManagement ipManagement)
    {
        ipManagementDAO.persist(ipManagement);
    }

    public boolean existAnyIpWithMac(String mac)
    {
        return ipManagementDAO.existsAnyWithMac(mac);
    }

    public void insertNetworkConfig(NetworkConfiguration configuration)
    {
        networkConfigDAO.persist(configuration);
    }

    public void insertVlan(VLANNetwork vlan)
    {
        vlanDAO.persist(vlan);
    }
    
    public void updateVlan(VLANNetwork vlan)
    {
        vlanDAO.flush();
    }

    public void inserVirtualAppliance(VirtualAppliance virtualAppliance)
    {
        virtualApplianceDAO.persist(virtualAppliance);
    }

    public void insert(VirtualDatacenter vdc)
    {
        virtualDatacenterDAO.persist(vdc);
    }

    public void update(VirtualDatacenter vdc)
    {
        virtualDatacenterDAO.flush();
    }

    public void delete(VirtualDatacenter vdc)
    {
        Collection<VLANNetwork> vlans = findVlansByVirtualDatacener(vdc);

        for (VLANNetwork vlan : vlans)
        {
            vlanDAO.remove(vlan);
        }

        networkDAO.remove(vdc.getNetwork());
        virtualDatacenterDAO.remove(vdc);
    }

    public Collection<String> getAllMacs()
    {
        return ipManagementDAO.getAllMacs();
    }

    public Collection<VirtualDatacenter> findByEnterprise(Enterprise enterprise)
    {
        return virtualDatacenterDAO.findByEnterprise(enterprise);
    }

    public Collection<VirtualAppliance> findVirtualAppliancesByVirtualDatacenter(
        VirtualDatacenter virtualDatacenter)
    {
        return virtualApplianceDAO.findByVirtualDatacenter(virtualDatacenter);
    }

    public boolean containsVirtualAppliances(VirtualDatacenter virtualDatacenter)
    {
        return !findVirtualAppliancesByVirtualDatacenter(virtualDatacenter).isEmpty();
    }

    public Collection<RasdManagement> findResourcesByVirtualDatacenterAndResourceType(
        VirtualDatacenter virtualDatacenter, String idResource)
    {
        return rasdDAO.findByVirtualDatacenterAndResourceType(virtualDatacenter, idResource);
    }

    public boolean containsResources(VirtualDatacenter virtualDatacenter, String idResource)
    {
        return !findResourcesByVirtualDatacenterAndResourceType(virtualDatacenter, idResource)
            .isEmpty();
    }

    public VirtualDatacenter findByName(String name)
    {
        return virtualDatacenterDAO.findUniqueByProperty(VirtualDatacenter.NAME_PROPERTY, name);
    }

    public void insertNetworkAssignment(NetworkAssignment na)
    {
        naDao.persist(na);
    }

    public void insertVirtualMachine(VirtualMachine vm)
    {
        vmDao.persist(vm);
    }

    /**
     * Return all the private IPs by VLAN.
     * 
     * @param vlanId identifier of the vlan
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByVLAN(final Integer vlanId, final Integer page,
        final Integer numElem)
    {
        return ipManagementDAO.findByVLAN(vlanId, page, numElem);
    }

    /**
     * Return all the private IPs by Virtual Datacenter
     * 
     * @param vdcId identifier of the virtual datacenter
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByVdc(final Integer vdcId, final Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderBy, final Boolean asc)
    {
        return ipManagementDAO.findByVdc(vdcId, firstElem, numElem, has, orderBy, asc);
    }

    /**
     * Return all the private IPs by Enterprise
     * 
     * @param entId enterprise identifier
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByEnterprise(final Integer entId, final Integer firstElem,
        final Integer numElem, final String has, final IpPoolManagement.OrderByEnum orderBy, final Boolean asc)
    {
        return ipManagementDAO.findByEnterprise(entId, firstElem, numElem, has, orderBy, asc);
    }

    /**
     * Return all the private IPs used by Virtual Appliance.
     * 
     * @param vappId enterprise identifier
     * @return list of IpPoolManagement.
     */
    public List<IpPoolManagement> findIpsByVirtualAppliance(VirtualAppliance vapp)
    {
        return ipManagementDAO.findByVirtualAppliance(vapp);
    }

    public List<IpPoolManagement> findIpsByVirtualMachine(VirtualMachine vm)
    {
        return ipManagementDAO.findByVirtualMachine(vm);
    }

    /**
     * Creates teh nodevirtualimage to associate the virtual machine to a virtual appliance
     */
    public NodeVirtualImage associateToVirtualAppliance(String name, VirtualMachine vmachine,
        VirtualAppliance vapp)
    {
        assert (vmachine.getVirtualImage() != null);

        NodeVirtualImage nvi =
            new NodeVirtualImage(name, vapp, vmachine.getVirtualImage(), vmachine);

        nodeviDao.persist(nvi);

        return nvi;
    }

    public NodeVirtualImage findNodeVirtualImageByVirtualMachine(VirtualMachine vmachine)
    {
        return nodeviDao.findByVirtualMachine(vmachine);
    }

    public VirtualAppliance findVirtualApplianceByVirtualMachine(VirtualMachine vmachine)
    {
        return nodeviDao.findVirtualAppliance(vmachine);
    }

    public VirtualMachine findVirtualMachineByName(String name)
    {
        return vmDao.findByName(name);
    }

    public VirtualMachine findVirtualMachineById(Integer virtualMachineId)
    {
        return vmDao.findById(virtualMachineId);
    }

    public void deleteVirtualMachine(VirtualMachine vmachine)
    {
        vmDao.remove(vmachine);
    }

    public void deleteNodeVirtualImage(NodeVirtualImage nvi)
    {
        // TODO deassociate
        nodeviDao.remove(nvi);
    }

    public VirtualAppliance findVirtualApplianceByName(String name)
    {
        return virtualApplianceDAO.findByName(name);
    }

    public VirtualAppliance findVirtualApplianceById(Integer vappId)
    {
        return virtualApplianceDAO.findById(vappId);
    }

    public void updateVirtualAppliance(VirtualAppliance vapp)
    {
        virtualApplianceDAO.flush();
    }

    public void insertVirtualAppliance(VirtualAppliance vapp)
    {
        virtualApplianceDAO.persist(vapp);
    }
}
