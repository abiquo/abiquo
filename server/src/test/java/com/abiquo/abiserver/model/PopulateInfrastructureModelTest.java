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

package com.abiquo.abiserver.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abicloud.model.test.infrastructure.Infrastructure;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.model.enumerator.HypervisorType;

public class PopulateInfrastructureModelTest
{

    private final static Logger log = LoggerFactory
        .getLogger(PopulateInfrastructureModelTest.class);

    public static void main(String[] args) throws PersistenceException, JAXBException
    {
        // final String xmlPath =
        // "/home/apuig/repository/expo/abicloud/server/src/test/resources/Infrastructure.xml";

        args = new String[1];
        args[0] =
            "/home/apuig/repository/expo/abicloud/server/src/test/resources/Infrastructure.xml";

        if (args == null || args[0] == null || args[0].length() == 0)
        {
            log.error("missing XML infrastructure file path");
        }
        else
        {
            log.info("Reading from [{}]", args[0]);
        }

        PopulateInfrastructureModelTest test = new PopulateInfrastructureModelTest();
        test.createTestInfrastructure(readInfraestructure(args[0]));

        // test.createVirtualDatacenter("datacenterName", "KVM", "physicalMachineName",
        // "physicalMachineIp", "http://virtualfactorylocation:8080/vf");
    }

    private static Infrastructure readInfraestructure(final String path) throws JAXBException
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(Infrastructure.class);

        return (Infrastructure) jaxbContext.createUnmarshaller().unmarshal(new File(path));
    }

    public void createTestInfrastructure(final Infrastructure infra) throws PersistenceException
    {
        DAOFactory daoFacto = HibernateDAOFactory.instance();
        daoFacto.beginConnection();

        // TODO infra.getUser and infra.getEnterprise UNUSED

        for (com.abicloud.model.test.infrastructure.Datacenter tstDc : infra.getDatacenter())
        {
            createTestDatacenter(tstDc);
        }

        daoFacto.endConnection();
    }

    private void createTestDatacenter(final com.abicloud.model.test.infrastructure.Datacenter tstDc)
        throws PersistenceException
    {

        DatacenterHB dc = createDataCenter(tstDc.getName());
        RemoteServiceHB vf = createVirtualFactory(tstDc.getRemoteServicesBaseURI(), dc);
        RackHB rack = createRack(dc);
        /**
         * TODO creating a default RACK
         */

        for (com.abicloud.model.test.infrastructure.Machine tstMachine : tstDc.getMachine())
        {
            createTestMachine(tstMachine, rack);
        }

        for (com.abicloud.model.test.infrastructure.VirtualDatacenter tstVdc : tstDc
            .getVirtualDatacenter())
        {
            createTestVirtualDatacenter(tstVdc, dc);
        }
    }

    private void createTestMachine(final com.abicloud.model.test.infrastructure.Machine tstMachine,
        final RackHB rack) throws PersistenceException
    {
        HypervisorType htype = tstMachine.getHypervisorType();
        PhysicalmachineHB pm =
            createPhysicalMachine(tstMachine.getName(), rack, htype, tstMachine.getCPU(),
                tstMachine.getRAM(), tstMachine.getHD());
        HypervisorHB hyper = createHypervisor(htype, pm, tstMachine.getIP());
    }

    private void createTestVirtualDatacenter(
        final com.abicloud.model.test.infrastructure.VirtualDatacenter tstVdc, final DatacenterHB dc)
        throws PersistenceException
    {
        HypervisorType htype = tstVdc.getHypervisorType();
        VirtualDataCenterHB vdc = createVirtualDataCenter(dc, htype);

        for (com.abicloud.model.test.infrastructure.VirtualAppliance tstVa : tstVdc
            .getVirtualAppliance())
        {
            createTestVirtualAppliance(tstVa, vdc);
        }

    }

    private void createTestVirtualAppliance(
        final com.abicloud.model.test.infrastructure.VirtualAppliance tstVa,
        final VirtualDataCenterHB vdc) throws HibernateException, PersistenceException
    {
        VirtualappHB va = createVirtualAppliance(tstVa.getName(), vdc);
    }

    /**
     * non XML based tests
     */

    /**
     * @return the VirtualDatacenter identifier
     */
    public Integer createVirtualDatacenter(final String datacenterName,
        final String hypervisorType, final String physicalMachineName,
        final String physicalMachineIp, final String virtualFactoryURL) throws PersistenceException
    {
        DAOFactory daoFacto = HibernateDAOFactory.instance();
        daoFacto.beginConnection();

        DatacenterHB dc = createDataCenter(datacenterName);
        RemoteServiceHB vf = createVirtualFactory(virtualFactoryURL, dc);
        RackHB rack = createRack(dc);
        HypervisorType htype =
            HypervisorType.valueOf(hypervisorType.replace("-", "_").toUpperCase());

        VirtualDataCenterHB vdc = createVirtualDataCenter(dc, htype);

        PhysicalmachineHB pm =
            createPhysicalMachine(physicalMachineName, rack, htype, 1, 3000, 40000);
        HypervisorHB hyper = createHypervisor(htype, pm, physicalMachineIp);

        daoFacto.endConnection();

        return vdc.getIdDataCenter();
    }

    private VirtualappHB createVirtualAppliance(final String name,
        final VirtualDataCenterHB virtualDatacenter) throws HibernateException,
        PersistenceException
    {
        VirtualappHB va = new VirtualappHB();

        va.setEnterpriseHB(getDefaultEnterprise());
        va.setName(name);
        va.setVirtualDataCenterHB(virtualDatacenter);
        va.setState(StateEnum.NOT_DEPLOYED);
        va.setError(0);

        HibernateDAOFactory.instance().getVirtualApplianceDAO().makePersistentBasic(va);

        log.info("VirtualAppliance\t[{}]", va.getIdVirtualApp());

        return va;
    }

    private RemoteServiceHB createVirtualFactory(final String virtualFactoryURL,
        final DatacenterHB datacenter) throws HibernateException, PersistenceException
    {

        RemoteServiceHB vf = new RemoteServiceHB();

        vf.setIdDataCenter(datacenter.getIdDataCenter());
        vf.setRemoteServiceType(RemoteServiceType.VIRTUAL_FACTORY);

        vf.setUri(virtualFactoryURL);

        HibernateDAOFactory.instance().getRemoteServiceDAO().makePersistent(vf);

        log.info("RemoteService\t[{}]", vf.getIdRemoteService());

        return null;
    }

    protected PhysicalmachineHB createPhysicalMachine(final String physicalMachineName,
        final RackHB rack, final HypervisorType htype, final int cpu, final int ram, final long hd)
        throws PersistenceException
    {

        PhysicalmachineHB machine = new PhysicalmachineHB();
        machine.setName(physicalMachineName);
        machine.setDescription(physicalMachineName + "_desc");

        machine.setCpu(cpu);
        machine.setRam(ram);

        machine.setCpuRatio(1); // XXX

        machine.setCpuUsed(0);
        machine.setRamUsed(0);

        machine.setRack(rack);
        machine.setDataCenter(rack.getDatacenter());

        // machine.setHypervisor(createHypervisor(htype));

        HibernateDAOFactory.instance().getPhysicalMachineDAO().makePersistent(machine);

        log.info("PhysicalMachine\t[{}]", machine.getIdPhysicalMachine());

        return machine;
    }

    private HypervisorHB createHypervisor(final HypervisorType hType,
        final PhysicalmachineHB physicalMachien, final String ip) throws PersistenceException
    {
        HypervisorHB hyp = new HypervisorHB();

        hyp.setIp(ip);
        hyp.setPort(hType.defaultPort);

        hyp.setType(hType);
        hyp.setPhysicalMachine(physicalMachien);

        HibernateDAOFactory.instance().getHyperVisorDAO().makePersistent(hyp);

        log.info("Hypervisor\t\t[{}]", hyp.getIdHyper());

        return hyp;
    }

    private VirtualDataCenterHB createVirtualDataCenter(final DatacenterHB dc,
        final HypervisorType htype) throws PersistenceException
    {

        VirtualDataCenterHB vdc = new VirtualDataCenterHB();

        vdc.setEnterpriseHB(getDefaultEnterprise());
        vdc.setIdDataCenter(dc.getIdDataCenter());

        vdc.setName(dc.getName() + "_" + htype.getValue());

        vdc.setHypervisorType(htype);
        // TODO: make an alternative
        vdc.setNetwork(null);

        // vdc.setNetworkType(createDefaultNetwork());

        HibernateDAOFactory.instance().getVirtualDataCenterDAO().makePersistent(vdc);

        log.info("VirtualDatacenter\t[{}]", vdc.getIdVirtualDataCenter());

        return vdc;
    }

    private RackHB createRack(final DatacenterHB dc) throws PersistenceException
    {
        RackHB rack = new RackHB();

        rack.setDatacenter(dc);
        rack.setName(dc.getName() + "__Rack");
        rack.setShortDescription(dc.getName() + "__Rack");
        rack.setLargeDescription(dc.getName() + "__Rack");

        HibernateDAOFactory.instance().getRackDAO().makePersistent(rack);

        log.info("Rack\t\t[{}]", rack.getIdRack());

        return rack;
    }

    private DatacenterHB createDataCenter(final String datacenterName) throws PersistenceException
    {
        DatacenterHB data = new DatacenterHB();

        data.setName(datacenterName);
        data.setSituation("situation");

        HibernateDAOFactory.instance().getDataCenterDAO().makePersistent(data);

        log.info("Datacenter\t\t[{}]", data.getIdDataCenter());

        return data;
    }

    /**
     * Creates a {@link AbicloudNetworkHB} object
     * 
     * @return AbicloudNetworkHB object created
     * @throws PersistenceException
     */
    /*
     * private AbicloudNetworkHB createDefaultNetwork() throws PersistenceException {
     * AbicloudNetworkDAO dao = HibernateDAOFactory.instance().getAbicloudNetworkDAO(); DHCPDAO
     * dhcpDAO = HibernateDAOFactory.instance().getDHCPDAO(); IpPoolManagementDAO hostDAO =
     * HibernateDAOFactory.instance().getHostDAO(); AbicloudNetworkHB abicloudNetHB = new
     * AbicloudNetworkHB(); abicloudNetHB.setVlanID("vlanID"); abicloudNetHB.setUuid("uuid");
     * BridgeHB bridge = new BridgeHB(); bridge.setName("vbr1"); abicloudNetHB.setBridge(bridge);
     * ForwardHB forward = new ForwardHB(); forward.setDev("eth0"); forward.setMode("route");
     * abicloudNetHB.setForward(forward); RangeHB range = new RangeHB();
     * range.setFirstIp("first_ip"); range.setLastIp("last_ip"); range.setMask(25555);
     * abicloudNetHB.setRange(range); dao.makePersistent(abicloudNetHB); log.info("Network\t\t[{}]",
     * abicloudNetHB.getNetworktypeID()); DHCPHB dhcp = new DHCPHB();
     * dhcp.setAddress("dhcp_address"); dhcp.setNetmask("dhcp_netmask");
     * dhcp.setGateway("dhcp_gateway"); // Now make the DHCPHB objects persistent
     * dhcp.setNetworktypeID(abicloudNetHB.getNetworktypeID()); dhcpDAO.makePersistent(dhcp);
     * log.info("DHCP\t\t[{}]", dhcp.getDhcptypeID()); HostHB host = new HostHB();
     * host.setIp("127.0.0.1"); host.setName("localhost"); host.setMac("22255555");
     * host.setDhcptypeID(dhcp.getDhcptypeID()); hostDAO.makePersistent(host);
     * log.info("Host\t\t[{}]", host.getHosttypeID()); abicloudNetHB =
     * dao.findById(abicloudNetHB.getNetworktypeID()); return abicloudNetHB; }
     */

    private EnterpriseHB getDefaultEnterprise() throws HibernateException, PersistenceException
    {
        return HibernateDAOFactory.instance().getEnterpriseDAO().findById(1);
    }

}
