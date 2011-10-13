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

package com.abiquo.scheduler;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageGenerator;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterGenerator;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageDAO;
import com.abiquo.server.core.cloud.VirtualImageGenerator;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineGenerator;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.DatacenterLimitsDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementGenerator;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentGenerator;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkGenerator;
import com.softwarementors.commons.test.SeedGenerator;

@Component
@Transactional
public class PopulateVirtualInfrastructure extends PopulateConstants
{

    @Autowired
    InfrastructureRep dcRep;

    @Autowired
    VirtualDatacenterRep vdcRep;

    @Autowired
    EnterpriseRep enterRep;

    @Autowired
    DatacenterLimitsDAO dcLimitsDao;

    @Autowired
    RepositoryDAO repoDao;

    @Autowired
    VirtualImageDAO vimageDao;

    @Autowired
    VirtualMachineDAO vmachineDao;

    // Generators
    SeedGenerator seed = new SeedGenerator();

    EnterpriseGenerator enterGen = new EnterpriseGenerator(seed);

    VirtualDatacenterGenerator vdcGen = new VirtualDatacenterGenerator(seed);

    VLANNetworkGenerator vlanNetGen = new VLANNetworkGenerator(seed);

    NetworkAssignmentGenerator naGen = new NetworkAssignmentGenerator(seed);

    VirtualApplianceGenerator vappGen = new VirtualApplianceGenerator(seed);

    VirtualMachineGenerator vmGen = new VirtualMachineGenerator(seed);

    NodeVirtualImageGenerator nodeviGen = new NodeVirtualImageGenerator(seed);

    IpPoolManagementGenerator ipPoolGen = new IpPoolManagementGenerator(seed);

    VirtualImageGenerator vimageGen = new VirtualImageGenerator(seed);

    public PopulateVirtualInfrastructure()
    {

    }

    /**
     * <ul>
     * <li>e1 (Enterprise)
     * <li>e1.vi1:d1,1,2,10 (VirtualImage)
     * <li>e1.vdc1:d1,HTYPE (VirtualDatacenter)
     * <li>vlan1:vdc1,r1 (VLAN)
     * <li>e1.vdc1.va1 (VirtualAppliance)
     * <li>e1.vdc1.va1.vm1:vi1,vnic1,vlan1 (VirtualMachine)
     * <ul>
     */
    public void createVirtualInfrastructure(final String declar)
    {
        // also vlan assertTrue(declar.startsWith("e") );

        String enterpriseName;
        String virtualDatacenterDec;
        String virtualImageDec;
        String virtualApplianceDec;
        String virtualMachineDec;

        String[] fragments = declar.split(DELIMITER_ENTITIES);

        switch (fragments.length)
        {
            case 1:// create enterprise or vlan

                if (fragments[0].startsWith(DEC_ENTERPRISE))
                {
                    enterpriseName = fragments[0];
                    createEnterprise(enterpriseName);
                }
                else if (fragments[0].startsWith(DEC_VLAN))
                {
                    String vlanDec = fragments[0];
                    createVlanNetwork(vlanDec);
                }
                else
                {
                    throw new PopulateException("Invalid virtual infrastructure dec " + declar);
                }
                break;

            case 2: // create virtual datacenter o virtual image
                enterpriseName = fragments[0];
                assertTrue("expected enterprise declaration : " + declar,
                    enterpriseName.startsWith(DEC_ENTERPRISE));

                if (fragments[1].startsWith(DEC_VIRTUAL_DATACENTER))
                {
                    virtualDatacenterDec = fragments[1];
                    createVirtualDatacenter(enterpriseName, virtualDatacenterDec);
                }
                else if (fragments[1].startsWith(DEC_VIRTUAL_IMAGE))
                {
                    virtualImageDec = fragments[1];
                    createVirtualImage(enterpriseName, virtualImageDec);
                }
                break;

            case 3: // create virtual appliance
                enterpriseName = fragments[0];
                virtualDatacenterDec = fragments[1];
                virtualApplianceDec = fragments[2];
                assertTrue("expected vapp declaration : " + declar,
                    enterpriseName.startsWith(DEC_ENTERPRISE));
                assertTrue("expected vapp declaration : " + declar,
                    virtualDatacenterDec.startsWith(DEC_VIRTUAL_DATACENTER));
                assertTrue("expected vapp declaration : " + declar,
                    virtualApplianceDec.startsWith(DEC_VIRTUAL_APPLIANCE));

                createVirtualAppliance(enterpriseName, virtualDatacenterDec, virtualApplianceDec);
                break;

            case 4: // create virtual machine
                enterpriseName = fragments[0];
                virtualDatacenterDec = fragments[1];
                virtualApplianceDec = fragments[2];
                virtualMachineDec = fragments[3];
                assertTrue("expected vmachine declaration : " + declar,
                    enterpriseName.startsWith(DEC_ENTERPRISE));
                assertTrue("expected vmachine declaration : " + declar,
                    virtualDatacenterDec.startsWith(DEC_VIRTUAL_DATACENTER));
                assertTrue("expected vmachine declaration : " + declar,
                    virtualApplianceDec.startsWith(DEC_VIRTUAL_APPLIANCE));
                assertTrue("expected vmachine declaration : " + declar,
                    virtualMachineDec.startsWith(DEC_VIRTUAL_MACHINE));

                createVirtualMachine(enterpriseName, virtualDatacenterDec, virtualApplianceDec,
                    virtualMachineDec);
                break;
            default:
                throw new PopulateException("Invalid create virtual infrastructure declaration : "
                    + declar);
        }

    }

    /**
     * @param enterprise, e1:1 (enterprise isReservationRestricted=1)
     * @return
     */
    private Enterprise createEnterprise(final String enter)
    {
        Enterprise enterprise = enterRep.findByName(enter);

        if (enterprise == null)
        {

            String[] frg = enter.split(DELIMITER_DEFINITION);

            String enterName = frg[0];

            enterprise = enterGen.createInstanceNoLimits(enter);

            if (frg.length == 2)
            {
                String isReservationRestricted = frg[1];

                if (isReservationRestricted.equals("1"))
                {
                    enterprise.setIsReservationRestricted(true);
                }
                else
                {
                    enterprise.setIsReservationRestricted(false);
                }

            }

            enterRep.insert(enterprise);

            // allowAllDatacentersByDefault(enterprise);
        }

        return enterprise;
    }

    public void allowAllDatacentersByDefault(final Enterprise enterprise)
    {
        for (Datacenter dc : dcRep.findAll())
        {
            DatacenterLimits dcLimit = new DatacenterLimits(enterprise, dc);

            dcLimitsDao.persist(dcLimit);
        }
    }

    /**
     * @param vimageDec, vi1:d1,1,2,10 (VirtualImage)
     */
    private VirtualImage createVirtualImage(final String enterStr, final String vimageDec)
    {
        Enterprise enterprise = enterRep.findByName(enterStr);

        assertNotNull("enterprise not found " + enterStr, enterprise);

        String[] frg = vimageDec.split(DELIMITER_DEFINITION);

        assertTrue("expected vimage delaraction " + vimageDec, frg.length == 2);

        String virtualimageName = frg[0];

        frg = frg[1].split(DELIMITER_ATTRIBUTES);

        assertTrue("expected vimage delaraction " + vimageDec, frg.length == 1 || frg.length == 4);

        String datacenterName = frg[0];

        Datacenter dc = dcRep.findByName(datacenterName);
        Repository repository = repoDao.findByDatacenter(dc);

        int cpuRequired = DEF_IMAGE_CPU;
        int ramRequired = DEF_IMAGE_RAM;
        long hdRequired = DEF_IMAGE_HD;
        if (frg.length == 4) // requirements
        {
            cpuRequired = Integer.parseInt(frg[1]);
            ramRequired = (int) (Integer.parseInt(frg[2]) * GB_TO_MB);
            hdRequired = Integer.parseInt(frg[3]) * GB_TO_MB * 1014 * 1024; // bytes
        }

        VirtualImage vimage =
            vimageGen.createInstance(enterprise, repository, cpuRequired, ramRequired, hdRequired,
                virtualimageName);
        vimageDao.persist(vimage);

        return vimage;
    }

    /**
     * @param vdcDeclaration, vdc1:d1,HTYPE (VirtualDatacenter)
     */
    private VirtualDatacenter createVirtualDatacenter(final String enter,
        final String vdcDeclaration)
    {
        Enterprise enterprise = enterRep.findByName(enter);

        assertNotNull("enterprise not found " + enter, enterprise);

        String[] fragments = vdcDeclaration.split(DELIMITER_DEFINITION);

        assertTrue("expected virtual datacenter declaration " + vdcDeclaration,
            fragments.length == 2);

        String vdcName = fragments[0];

        fragments = fragments[1].split(DELIMITER_ATTRIBUTES);

        assertTrue("expected virtual datacenter declaration " + vdcDeclaration,
            fragments.length == 2);

        String dcName = fragments[0];
        String htype = fragments[1];

        Datacenter dc = dcRep.findByName(dcName);
        HypervisorType hypervisor = HypervisorType.valueOf(htype);

        assertNotNull("Datacenter doesn't exist" + dcName, dc);

        VirtualDatacenter vdc = vdcGen.createInstance(dc, enterprise, hypervisor, vdcName);
        vdcRep.insert(vdc);

        return vdc;
    }

    private VirtualAppliance createVirtualAppliance(final String enterName, final String vdcName,
        final String vappDec)
    {

        // XXX unused enterName to check vdc !!!
        VirtualDatacenter virtualDatacenter = vdcRep.findByName(vdcName);
        assertNotNull("Virtual datacenter doesn't exist " + vdcName, virtualDatacenter);

        VirtualAppliance vapp = vappGen.createInstance(virtualDatacenter, vappDec);
        vapp.setEnterprise(virtualDatacenter.getEnterprise());// XXX
        vdcRep.inserVirtualAppliance(vapp);

        return vapp;
    }

    /**
     * @param vmachineStr, vm1:vi1,vnic1,vlan1 (VirtualMachine)
     */
    private VirtualMachine createVirtualMachine(final String enterStr, final String vdcStr,
        final String vappStr, final String vmachineStr)
    {
        // TODO unused enterStr, vdcStr
        Enterprise enterprise = enterRep.findByName(enterStr);
        assertNotNull("Enterprise not found" + enterStr, enterprise);

        VirtualAppliance vapp = vdcRep.findVirtualApplianceByName(vappStr);
        assertNotNull("Virtual app not found" + vappStr, vapp);

        String[] frg = vmachineStr.split(DELIMITER_DEFINITION);

        assertTrue("Expected virutal machine declarartio " + vmachineStr, frg.length == 2);

        String vmachineName = frg[0];

        frg = frg[1].split(DELIMITER_ATTRIBUTES);

        assertTrue("Expected virutal machine declarartio " + vmachineStr, frg.length == 1
            || frg.length == 3);

        String virtualimageName = frg[0];

        if (frg.length == 3)
        {
            String vnicName = frg[1];
            String vlanName = frg[2];

            VirtualDatacenter vdc = vdcRep.findByName(vdcStr);

            createIpMan(vnicName, vlanName, vdc);
        }

        VirtualImage vimage = vimageDao.findByName(virtualimageName);
        assertNotNull("vimage not found " + virtualimageName, vimage);

        VirtualMachine vmachine = vmGen.createInstance(vimage, enterprise, vmachineName);
        enterRep.insertRole(vmachine.getUser().getRole());
        enterRep.insertUser(vmachine.getUser());
        vdcRep.insertVirtualMachine(vmachine);

        // Associate vapp with vmachine
        vdcRep.associateToVirtualAppliance(vmachine.getName() + "_image", vmachine, vapp);

        return vmachine;
    }

    /**
     * vlan1:vdc1,r1
     * 
     * @param vdcStr
     * @param vlanNetworkName
     * @return
     */
    private void createVlanNetwork(final String declar)
    {
        assertTrue("Expected vlan declaration " + declar, declar.startsWith(DEC_VLAN));

        String[] fragments = declar.split(DELIMITER_DEFINITION);

        assertTrue("Expected vlan declaration " + declar, fragments.length == 2);

        String vlanNetworkName = fragments[0];

        fragments = fragments[1].split(DELIMITER_ATTRIBUTES);

        String vdcName = fragments[0];

        VirtualDatacenter vdc = vdcRep.findByName(vdcName);
        assertNotNull("virtual datacenter not found " + vdcName, vdc);

        Network network = vdc.getDatacenter().getNetwork();

        VLANNetwork vlan = vlanNetGen.createInstance(network, vlanNetworkName);
        //
        // RemoteService rsDhcp = vlan.getConfiguration().getDhcp().getRemoteService();
        // // XXX save remote service on the current datacenter
        //
        // rsDhcp.setDatacenter(vdc.getDatacenter());
        // dcRep.insertRemoteService(rsDhcp);

        if (fragments.length == 2)
        {
            // vlan.setTag(Integer.valueOf(vlanNetworkName.substring(vlanNetworkName.indexOf("n") +
            // 1)));
            vdcRep.insertVlan(vlan);
            String rName = fragments[1];

            Rack rack = dcRep.findRackByName(rName);
            assertNotNull("rack not found " + rName, rack);

            NetworkAssignment na = naGen.createInstance(vdc, rack, vlan);

            vdcRep.insertNetworkAssignment(na);
        }
        else
        {
            // If no rack is to assigned not to force tag
            vdcRep.insertVlan(vlan);
        }

    }

    /**
     * vapp1:vm1:vnic1:vlan1
     * 
     * @param declar
     */
    private void createIpMan(final String vnicName, final String vlanName,
        final VirtualDatacenter vdc)
    {
        VLANNetwork vlanNetwork = vdcRep.findVlanByName(vlanName);

        IpPoolManagement ipPoolManagement = ipPoolGen.createInstance(vdc, vdc.getNetwork());
        ipPoolManagement.setVlanNetwork(vlanNetwork);
        ipPoolManagement.setName(vnicName);
        vdcRep.insertIpManagement(ipPoolManagement);

    }

    public void removeVirtualMachine(final Integer virtualMachineId)
    {

        VirtualMachine vm = vdcRep.findVirtualMachineById(virtualMachineId);
        NodeVirtualImage nvi = vdcRep.findNodeVirtualImageByVirtualMachine(vm);

        vdcRep.deleteNodeVirtualImage(nvi);
        // XXX can not update XXX vdcRep.deleteVirtualMachine(vm);
    }

    public void runningVirtualMachine(final Integer virtualMachineId)
    {
        vmachineDao.updateVirtualMachineState(virtualMachineId, State.RUNNING);
    }
}
