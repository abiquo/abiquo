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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeTypeEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.HyperVisorDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.PhysicalMachineDAO;
import com.abiquo.abiserver.persistence.dao.infrastructure.RackDAO;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkConfigurationDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkDAO;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceAllocationSettingDataDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceManagementDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.CategoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.IconDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.RepositoryDAO;
import com.abiquo.abiserver.persistence.dao.virtualimage.VirtualImageDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;

/**
 * XXX TBD + Creates a DataCenter a Rack and a VirtualDatacenter named ''test''.<br>
 * + Create a VirtualAppliance (all the target images associated at this) <br>
 * + Use the default user ''user'' (id=1) and enterprise ''abiquo'' (id=1) <br>
 **/
public class PopulateModelTest
{
    /** The logger object */
    private static Logger log = LoggerFactory.getLogger(PopulateModelTest.class);

    /** Test Datacenter name for all PhysicalMachines instances (to be clean at tearDown) */
    private final static String DATACENTER_TEST = "test";

    /** All added domain object's creation date. */
    protected Date date;

    /** All added domain object's creation user. */
    protected UserHB user;

    /** All added VirtualAppliances's state */
    private final StateEnum state;

    /** All added VirtualImages's category */
    private final CategoryHB category;

    /** All added PhysicalMachien's hypervisor type */
    private final HypervisorType htype;

    /** All added PhysicalMachine's Enterprise */
    private final EnterpriseHB enterprise;

    /** All added PhysicalMachine's DataCenter */
    protected DatacenterHB dc;

    /** All added PhysicalMachine's Rack */
    private final RackHB rack;

    /** All added VirtualAppliance's virtual datacenter. */
    private final VirtualDataCenterHB vdc;

    /** Virtual appliance requiring the virtual images. */
    private final VirtualappHB vApp;

    /** All added VirtualImage's icon. */
    private final IconHB icon;

    /** All added VirtualImage's image type */
    private final DiskFormatType imageType;

    /** All added VirtualImage's repository */
    private final RepositoryHB repository;

    private final HypervisorHB hypervisor;

    /** All added PhysicalPachines. */
    private final List<PhysicalmachineHB> phyMachines;

    /** Holds all the @link ResourceAllocationSettingData */
    private final List<ResourceAllocationSettingData> rasdHBs;

    /**
     * Holds a list of ResourceManagementHB objects which can be later accessed in order to deleted
     * the entries related to each on in the database
     */
    private final List<ResourceManagementHB> resourceManagementHBs;

    /** Singleton instance. */
    private static PopulateModelTest instance;

    /** DAO's factory */
    private DAOFactory factorytest;

    /**
     * Only created throw singleton access.
     * 
     * @throws PersistenceException
     */
    private PopulateModelTest() throws PersistenceException
    {

        date = new Date();
        htype = getDefaultHypervisorType();
        state = StateEnum.NOT_ALLOCATED;
        category = getDefaultCategory();
        user = getDefaultUser();
        enterprise = getDefaultEnterprise();

        hypervisor = getDefaultHypervisor();

        icon = createIcon();
        imageType = getDefaultImageType();
        repository = getDefaultRepository();

        dc = createDataCenter();

        rack = createRack(dc);
        vdc = createVirtualDataCenter(dc, enterprise, htype);

        vApp = createVirtualApp(vdc, enterprise, state);

        phyMachines = new LinkedList<PhysicalmachineHB>();

        rasdHBs = new ArrayList<ResourceAllocationSettingData>();

        resourceManagementHBs = new ArrayList<ResourceManagementHB>();

    }

    /**
     * Singleton access, return the unique instance.
     * 
     * @throws PersistenceException
     */
    public static PopulateModelTest getInstance() throws PersistenceException
    {
        if (instance == null)
        {
            instance = new PopulateModelTest();
        }

        return instance;
    }

    /**
     * @return the dataCenter for all PhysicalMachines on the test set (used to call
     *         IScheduler.select(xxx,datacenter))
     */
    public DatacenterHB getDataCenter()
    {
        return dc;
    }

    /**
     * @return the virtual application all the virtual images are referenced.
     */
    public VirtualappHB getVirtualApp()
    {
        return vApp;
    }

    /**
     * Adds on DB the PhysicalMachines from the ISchedulerTestCase
     * 
     * @param a list of configured machines.
     * @throws PersistenceException
     */
    public void initDBPhysicalMachines(final List<PhysicalmachineHB> machines)
        throws PersistenceException
    {
        for (PhysicalmachineHB pm : machines)
        {
            log.debug("Adding to DB PhysicalMachines " + pm.getName());

            phyMachines.add(createPhysicalMachine(pm, rack, htype));
        }
    }

    /**
     * Persist on database the input PhysicalMachine (also its DataCenter, Rack and So).
     * 
     * @throws PersistenceException
     */
    protected PhysicalmachineHB createPhysicalMachine(final PhysicalmachineHB machine,
        final RackHB rack, final HypervisorType htype) throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        PhysicalMachineDAO pmDAO = factorytest.getPhysicalMachineDAO();
        HyperVisorDAO hyperDAO = factorytest.getHyperVisorDAO();

        factorytest.beginConnection();

        HypervisorHB hyper = createHypervisor(htype);

        machine.setRack(rack);
        machine.setDataCenter(rack.getDatacenter());

        machine.setHypervisor(hyper);

        pmDAO.makePersistent(machine);

        log.debug("Added machine [{}]", machine.getName());

        hyper.setPhysicalMachine(machine);
        hyperDAO.makePersistent(hyper);

        factorytest.endConnection();

        return machine;
    }

    /** PhysicalMachine Factory construction XXX is not stored on DB. */
    public PhysicalmachineHB definePhysical(final String name, final int cpu, final int ram,
        final long hd, final int cpuRatio)
    {
        PhysicalmachineHB mach = new PhysicalmachineHB();

        mach.setName(name);
        mach.setDescription("test");

        mach.setCpu(cpu);
        mach.setRam(ram);

        mach.setCpuRatio(cpuRatio);

        mach.setCpuUsed(0);
        mach.setRamUsed(0);

        return mach;
    }

    /**
     * VirtualImage Factory construction XXX is not stored on DB. (not an HB because it's not useful
     * the ''category''/''repository''...)
     * 
     * @throws PersistenceException
     * @param name
     * @param cpu
     * @param ram
     * @param hd
     * @return a reference to the newly created VirtualimageHB persistent object
     * @throws PersistenceException
     */
    public VirtualimageHB createVirtualImage(final String name, final int cpu, final int ram,
        final long hd) throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        VirtualImageDAO daoVI = factorytest.getVirtualImageDAO();

        factorytest.beginConnection();

        VirtualimageHB vImage = new VirtualimageHB();

        vImage.setName(name);
        vImage.setDescription("test");

        vImage.setCpuRequired(cpu);
        vImage.setHdRequired(hd);
        vImage.setRamRequired(ram);

        vImage.setPathName("test");

        vImage.setIcon(icon);
        vImage.setCategory(category);
        vImage.setType(imageType);
        vImage.setRepository(repository);

        daoVI.makePersistent(vImage);

        vImagesCreated.add(vImage);

        factorytest.endConnection();

        return vImage;
    }

    /**
     * All the created virtual images throw ''createVirtualImage'', stored in order to clean the DB.
     */
    private final List<VirtualimageHB> vImagesCreated = new LinkedList<VirtualimageHB>();

    /**
     * Creates nodes that correspond to the Virtualapp for the this object
     * 
     * @throws PersistenceException
     */
    public void createNodes() throws PersistenceException
    {

        VirtualMachine virtualMachine = new VirtualMachine();
        factorytest = HibernateDAOFactory.instance();
        HyperVisorDAO dao = factorytest.getHyperVisorDAO();

        factorytest.beginConnection();

        virtualMachine.setAssignedTo(dao.findById(hypervisor.getIdHyper()).toPojo());
        virtualMachine.setHd(1);
        virtualMachine.setHighDisponibility(true);
        virtualMachine.setDescription("test-virtual machine");
        virtualMachine.setCpu(2);
        virtualMachine.setRam(256);
        virtualMachine.setUUID("apophis");
        State state = new State(StateEnum.ALLOCATED);
        virtualMachine.setState(state);
        virtualMachine.setVdrpIP("vdrpIP");
        virtualMachine.setVdrpPort(5050);

        VirtualimageHB virtualImageHB = createVirtualImage("image", 4, 256, 2);
        virtualMachine.setVirtualImage(virtualImageHB.toPojo());

        createVirtualImageNode(virtualImageHB, virtualMachine);

        factorytest.endConnection();
    }

    private DatacenterHB createDataCenter() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        DataCenterDAO daoDC = factorytest.getDataCenterDAO();

        factorytest.beginConnection();
        DatacenterHB data = new DatacenterHB();

        data.setName(DATACENTER_TEST); // XXX
        data.setSituation("test");

        // XXX data.setRacks(racks);

        daoDC.makePersistent(data);
        factorytest.endConnection();

        return data;
    }

    /**
     * Rack Factory construction
     * 
     * @throws PersistenceException
     */
    private RackHB createRack(final DatacenterHB dc) throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        RackDAO daoRack = factorytest.getRackDAO();

        factorytest.beginConnection();

        RackHB rack = new RackHB();
        rack.setDatacenter(dc);
        rack.setName("test");
        rack.setShortDescription("test");
        rack.setLargeDescription("test");
        daoRack.makePersistent(rack);

        factorytest.endConnection();

        return rack;
    }

    /**
     * HyperVisor Factory construction
     * 
     * @throws PersistenceException
     */
    private HypervisorHB createHypervisor(final HypervisorType hType) throws PersistenceException
    {
        // Crudable<HypervisorHB, Integer> daoHyper =
        // new GenericHibernateDAO<HypervisorHB, Integer>(HypervisorHB.class);

        HypervisorHB hyp = new HypervisorHB();

        hyp.setType(hType);

        hyp.setIp("test");
        hyp.setIpService("test");
        hyp.setPort(0);

        // XXX hyp.setPhysicalMachine(physicalMachine);

        // daoHyper.makePersistent(hyp);

        return hyp;
    }

    /**
     * VirtualDataCenter Factory construction
     * 
     * @throws PersistenceException
     */
    private VirtualDataCenterHB createVirtualDataCenter(final DatacenterHB dc,
        final EnterpriseHB enterprise, final HypervisorType htype) throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        VirtualDataCenterDAO daoVdc = factorytest.getVirtualDataCenterDAO();

        factorytest.beginConnection();

        VirtualDataCenterHB vdc = new VirtualDataCenterHB();
        vdc.setEnterpriseHB(enterprise);
        vdc.setIdDataCenter(dc.getIdDataCenter());
        vdc.setName("test");
        vdc.setHypervisorType(htype);
        vdc.setNetwork(createNetwork());
        daoVdc.makePersistent(vdc);

        factorytest.endConnection();

        return vdc;
    }

    /**
     * Creates a {@link AbicloudNetworkHB} object
     * 
     * @return AbicloudNetworkHB object created
     * @throws PersistenceException
     */
    private NetworkHB createNetwork() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        NetworkDAO networkDAO = factorytest.getNetworkDAO();
        VlanNetworkDAO vlanNetworkDAO = factorytest.getVlanNetworkDAO();
        NetworkConfigurationDAO netConfDAO = factorytest.getNetworkConfigurationDAO();
        IpPoolManagementDAO hostDAO = factorytest.getIpPoolManagementDAO();

        factorytest.beginConnection();

        NetworkConfigurationHB netConf = new NetworkConfigurationHB();
        netConf.setMask(24);
        netConf.setNetmask("255.255.255.0");
        netConf.setNetworkAddress("192.168.1.0");
        netConfDAO.makePersistent(netConf);

        VlanNetworkHB vlanHB = new VlanNetworkHB();
        vlanHB.setNetworkName("Test VLAN");
        vlanHB.setConfiguration(netConf);
        vlanNetworkDAO.makePersistent(vlanHB);

        List<VlanNetworkHB> listOfVlans = new ArrayList<VlanNetworkHB>();
        listOfVlans.add(vlanHB);

        NetworkHB network = new NetworkHB();
        network.setUuid(UUID.randomUUID().toString());
        network.setNetworks(listOfVlans);
        networkDAO.makePersistent(network);

        return network;
    }

    /**
     * VirtualAppliance Factory construction
     * 
     * @throws PersistenceException
     */
    private VirtualappHB createVirtualApp(final VirtualDataCenterHB vdc,
        final EnterpriseHB enterprise, final StateEnum state) throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        VirtualApplianceDAO daoApp = factorytest.getVirtualApplianceDAO();

        factorytest.beginConnection();

        VirtualappHB vApp = new VirtualappHB();
        vApp.setEnterpriseHB(enterprise);
        vApp.setState(state);
        vApp.setVirtualDataCenterHB(vdc);
        vApp.setError(0);
        vApp.setHighDisponibility(1);
        vApp.setName("test");
        daoApp.makePersistent(vApp);

        factorytest.endConnection();

        return vApp;
    }

    /**
     * Gets the default hypervisor type (vBox id = 1)
     * 
     * @throws PersistenceException
     */
    private HypervisorType getDefaultHypervisorType() throws PersistenceException
    {
        return HypervisorType.VBOX;
    }

    /**
     * Retrieves the default hypervisor and returns pojo encapsulating this information
     * 
     * @return a {@link HypervisorHB} object representing the default Hypervisor stored in the DB
     * @throws PersistenceException
     */
    public HypervisorHB getDefaultHypervisor() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        HyperVisorDAO dao = factorytest.getHyperVisorDAO();

        factorytest.beginConnection();

        HypervisorHB hb = dao.findById(1);

        factorytest.endConnection();

        return hb;
    }

    /**
     * Gets the default enterprise (abiquo with id 1)
     */
    private EnterpriseHB getDefaultEnterprise() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        EnterpriseDAO daoEnt = factorytest.getEnterpriseDAO();

        factorytest.beginConnection();

        EnterpriseHB entHB = daoEnt.findById(1);

        factorytest.endConnection();

        return entHB;
    }

    /**
     * Gets the default user (named user )
     */
    protected UserHB getDefaultUser() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        UserDAO daoUser = factorytest.getUserDAO();

        factorytest.beginConnection();

        UserHB userHB = daoUser.findById(2);

        factorytest.endConnection();

        return userHB;
    }

    /**
     * Gets the default virtual image category (Others with id=1)
     * 
     * @throws PersistenceException
     */
    protected CategoryHB getDefaultCategory() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        CategoryDAO daoCategory = factorytest.getCategoryDAO();

        factorytest.beginConnection();

        CategoryHB catHB = daoCategory.findById(1);

        factorytest.endConnection();

        return catHB;

    }

    /** Log all PhysicalMachines on the DataBase */
    public void listPhysicalMachines() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        PhysicalMachineDAO daoPM = factorytest.getPhysicalMachineDAO();

        factorytest.beginConnection();

        List<PhysicalmachineHB> machines;

        machines = daoPM.findAll();

        log.debug("########## All PhysicalMachiene ##########");
        for (PhysicalmachineHB pm : machines)
        {
            log.debug("PhysicalMachine name:" + pm.getName() + " dc:"
                + pm.getRack().getDatacenter().getName() + "\t cpu:" + pm.getCpu() + "("
                + pm.getCpuUsed() + ")" + "\t ram:" + pm.getRam() + "(" + pm.getRamUsed() + ")");
        }

        factorytest.endConnection();
    }

    /**
     * Remove from DB the PhyisicalMachines from initPhysical implementation (actually remove
     * DataCenter @see dataCenter, for cascading delete physical machines disapear) Clean from DB
     * the scheduler test virtual datacenter, virtual application, icon and virtual images.
     * 
     * @throws PersistenceException
     */
    public void clearCreatedDomainObjects() throws PersistenceException
    {
        if (dc == null)
        {
            return;
        }

        factorytest = HibernateDAOFactory.instance();
        DataCenterDAO daoDc = factorytest.getDataCenterDAO();
        VirtualDataCenterDAO daoVdc = factorytest.getVirtualDataCenterDAO();
        IconDAO daoIco = factorytest.getIconDAO();
        VirtualImageDAO daoVi = factorytest.getVirtualImageDAO();

        factorytest.beginConnection();

        log.debug("Cleaning all the created objects ");

        daoDc.makeTransient(dc);
        daoVdc.makeTransient(vdc);

        daoIco.makeTransient(icon);

        // TODO: create the speciphic method by description??
        // Criterion descriptionTest = Restrictions.eq("description", "test");
        // for (VirtualimageHB vi : daoVi.findByCriteria(descriptionTest))
        // {
        // HibernateUtil.getSession().delete(vi); // XXX use DAO
        // }

        factorytest.endConnection();

    }

    private IconHB createIcon() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        IconDAO daoIcon = factorytest.getIconDAO();

        factorytest.beginConnection();

        IconHB icon = new IconHB();
        icon.setName("test");
        icon.setPath("test");
        daoIcon.makePersistent(icon);

        factorytest.endConnection();

        return icon;
    }

    /**
     * Gets the default image type (vBox id =1)
     * 
     * @throws PersistenceException
     */
    private DiskFormatType getDefaultImageType() throws PersistenceException
    {
        return DiskFormatType.RAW;
    }

    /**
     * Gets the default repository (main id =1)
     * 
     * @throws PersistenceException
     */
    private RepositoryHB getDefaultRepository() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        RepositoryDAO daoRep = factorytest.getRepositoryDAO();

        factorytest.beginConnection();

        RepositoryHB repHB = daoRep.findById(1);

        factorytest.endConnection();

        return repHB;
    }

    /**
     * Save the virtual machine, create and save a node virtual image with the virtual image /
     * machine relation.
     */
    public void createVirtualImageNode(final VirtualimageHB vImage, final VirtualMachine vMachine)
        throws PersistenceException
    {
        VirtualmachineHB vMachineHb = vMachine.toPojoHB();

        createVirtualMachine(vMachineHb);

        createNodeVirtualImage(vImage, vMachineHb);
    }

    /**
     * Save the virtual machine. XXX it do not use Crudable because the ''session.saveOrUpdate''
     * fails caused by ''Batch update returned unexpected row count from update [0]; actual row
     * count: 0; expected: 1'' TODO fix
     */
    private VirtualmachineHB createVirtualMachine(final VirtualmachineHB vMachine)
        throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        VirtualMachineDAO vmDAO = factorytest.getVirtualMachineDAO();

        factorytest.beginConnection();
        try
        {
            vmDAO.makePersistent(vMachine);
            factorytest.endConnection();
        }
        catch (HibernateException e)
        {
            factorytest.rollbackConnection();
            throw new PersistenceException("Hibernate exception", e);
        }

        return vMachine;
    }

    /**
     * TODO VirtualmachineHB vMachine (can be null)
     */
    private NodeVirtualImageHB createNodeVirtualImage(final VirtualimageHB vImage,
        final VirtualmachineHB vMachine) throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        NodeVirtualImageDAO daoNVI = factorytest.getNodeVirtualImageDAO();

        factorytest.beginConnection();

        NodeVirtualImageHB nVi = new NodeVirtualImageHB();
        // XXX nVi.setIdNode(idNode);
        nVi.setIdVirtualApp(vApp.getIdVirtualApp());
        nVi.setVirtualImageHB(vImage);
        nVi.setVirtualMachineHB(vMachine);

        nVi.setType(NodeTypeEnum.VIRTUAL_IMAGE);

        nVi.setName("test");

        nVi.setPosX(10);
        nVi.setPosY(10);

        daoNVI.makePersistent(nVi);

        factorytest.endConnection();

        return nVi;
    }

    // ResourceAllocationSettingData related methods

    /**
     * Creates the ResourceAllocationSettingData for all the types (as shown in the table below) by
     * calling each method dedicated to the creation of a specific RASD.
     * <p>
     * Each of thes method should have the format: <br>
     * <code> createRASD[idResource] </code> <br>
     * E.g The method for idResource 10 will be createRASD10
     * <p>
     * Implement methods as needed and follow examples
     * 
     * <pre>
     * +------------+-------------------------+
     * | idResource | name                    |
     * +------------+-------------------------+
     * |          1 | Other                   |
     * |          2 | Computer_System         |
     * |          3 | Processor               |
     * |          4 | Memory                  |
     * |          5 | IDE_Controller          |
     * |          6 | Parallel_SCSI_HBA       |
     * |          7 | FC_HBA                  |
     * |          8 | iSCSI_HBA               |
     * |          9 | IB_HCA                  |
     * |         10 | Ethernet_Adapter        | 
     * |         11 | Other_Network_Adapter   |
     * |         12 | IO_Slot                 |
     * |         13 | IO_Device               |
     * |         14 | Floppy_Drive            |
     * |         15 | CD_Drive                |
     * |         16 | DVD_drive               |
     * |         17 | Disk_Drive              |
     * |         18 | Tape_Drive              |
     * |         19 | Storage_Extent          |
     * |         20 | Other_storage_device    |
     * |         21 | Serial_port             |
     * |         22 | Parallel_port           |
     * |         23 | USB_Controller          |
     * |         24 | Graphics_controller     |
     * |         25 | IEEE_1394_Controller    |
     * |         26 | Partitionable_Unit      |
     * |         27 | Base_Partitionable_Unit |
     * |         28 | Power                   |
     * |         29 | Cooling_Capacity        |
     * |         30 | Ethernet_Switch_Port    |
     * |         31 | DMTF_reserved           |
     * |         32 | Vendor_Reserved         |
     * +------------+-------------------------+
     * </pre>
     */
    public void createResourceAllocationSettingData()
    {
        Session session = null;
        Transaction transaction = null;

        try
        {

            session = HibernateUtil.getSession();
            session.beginTransaction();

            // Create the Rasds
            createRASD10(session);

            // This array of integers contains a list of resource types in which a rasd_management
            // entry will be made for each one and all related to the current virtualappliance
            int[] resourceTypes = {10};

            transaction = session.getTransaction();

            transaction.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            transaction = session.getTransaction();

            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }
        }
    }

    /**
     * Create a ResourceAllocatingSettingData of type 10
     * 
     * @param session
     * @throws PersistenceException
     */
    private void createRASD10(final Session session) throws PersistenceException
    {
        session.beginTransaction();

        ResourceAllocationSettingData rasd = new ResourceAllocationSettingData();
        rasd.setInstanceID("3");
        rasd.setAddress("00:1d:09:2c:50:d6");
        rasd.setResourceSubType("PCNet32");
        rasd.setConnection("VM Network");
        rasd.setElementName("Ethernet adapter on 'VM Network'");
        rasd.setResourceType(10);

        session.saveOrUpdate(rasd);

        // add this to the list "rasdHBs"
        rasdHBs.add(rasd);
    }

    private <T extends ResourceManagementHB> void createRasdManagement(final Session session,
        final int[] resourceTypes, final Class<T> cl) throws PersistenceException
    {

        VirtualappHB virtualAppHB =
            (VirtualappHB) session.get("VirtualappExtendedHB", vApp.getIdVirtualApp());

        VirtualDataCenterHB virtualDataCenterHB = virtualAppHB.getVirtualDataCenterHB();

        Disjunction virtualAppDisjuction = Restrictions.disjunction();
        virtualAppDisjuction.add(Restrictions.eq("idVirtualApp", virtualAppHB.getIdVirtualApp()));
        ArrayList<NodeHB> nodes =
            (ArrayList<NodeHB>) session.createCriteria(NodeHB.class).add(virtualAppDisjuction)
                .list();

        VirtualmachineHB virtualMachineHB;

        ArrayList<ResourceAllocationSettingData> rasds;

        ResourceManagementHB resourceManagement = null;
        Timestamp timestamp = new Timestamp(new GregorianCalendar().getTimeInMillis());
        for (NodeHB node : nodes)
        {
            if (node.getType() == NodeTypeEnum.VIRTUAL_IMAGE)
            {
                NodeVirtualImageHB nodeVirtualImage = (NodeVirtualImageHB) node;
                virtualMachineHB = nodeVirtualImage.getVirtualMachineHB();

                if (virtualMachineHB != null)
                {
                    for (int resourceType : resourceTypes)
                    {
                        rasds = getRasds(session, resourceType);

                        for (ResourceAllocationSettingData rasd : rasds)
                        {
                            try
                            {
                                resourceManagement = cl.newInstance();
                            }
                            catch (Exception e)
                            {
                                throw new PersistenceException("Unable to create a new instance of "
                                    + cl.getName());
                            }

                            resourceManagement.setIdResourceType(rasd.getResourceType() + "");
                            resourceManagement.setRasd(rasd);
                            resourceManagement.setVirtualApp(virtualAppHB);
                            resourceManagement.setVirtualMachine(virtualMachineHB);
                            resourceManagement.setVirtualDataCenter(virtualDataCenterHB);

                            session.saveOrUpdate(resourceManagement);

                            resourceManagementHBs.add(resourceManagement);

                        }
                    }

                }
            }

        }

    }

    /**
     * Gets a lists of ResourceAllocationSettingData that correspond to a particular resource type
     * 
     * @param session
     * @param resourceType
     * @return
     */
    private static ArrayList<ResourceAllocationSettingData> getRasds(final Session session,
        final int resourceType)
    {

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.eq("resourceType", resourceType));

        ArrayList<ResourceAllocationSettingData> rasds =
            (ArrayList<ResourceAllocationSettingData>) session
                .createCriteria(ResourceAllocationSettingData.class).add(disjunction).list();

        if (rasds == null)
        {
            rasds = new ArrayList<ResourceAllocationSettingData>();
        }

        return rasds;
    }

    /**
     * Deletes entries in the rasd and rasd-management tables
     * 
     * @throws PersistenceException
     */
    private void cleanRasd() throws PersistenceException
    {
        factorytest = HibernateDAOFactory.instance();
        ResourceManagementDAO resourceManagementHBDao = factorytest.getResourceManagementDAO();
        ResourceAllocationSettingDataDAO rasdDao =
            factorytest.getResourceAllocationSettingDataDAO();

        factorytest.beginConnection();
        for (ResourceManagementHB rasdManagementHB : resourceManagementHBs)
        {
            resourceManagementHBDao.makeTransient(rasdManagementHB);
        }

        for (ResourceAllocationSettingData rasd : rasdHBs)
        {
            rasdDao.makeTransient(rasd);
        }
        factorytest.endConnection();

        log.info("Deleted rasd and rasd-management entries");

    }
}
