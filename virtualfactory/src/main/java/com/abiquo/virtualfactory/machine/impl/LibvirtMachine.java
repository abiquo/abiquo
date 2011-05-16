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

package com.abiquo.virtualfactory.machine.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StoragePool;
import org.libvirt.StorageVol;
import org.libvirt.DomainInfo.DomainState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.abiquo.aimstub.TTransportProxy;
import com.abiquo.aimstub.Aim.Iface;
import com.abiquo.util.AddressingUtils;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.AbsLibvirtHypervisor;
import com.abiquo.virtualfactory.hypervisor.impl.KVMHypervisor;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.ConfigurationManager;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.abiquo.virtualfactory.utils.XPathUtils;
import com.abiquo.virtualfactory.vlanstub.VLANException;
import com.abiquo.virtualfactory.vlanstub.VlanStub;

/**
 * The KVM - XEN machine representation.
 * 
 * @author Marc Morata Fité
 * @author Zeus Gomez
 */

public class LibvirtMachine extends AbsVirtualMachine
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(LibvirtMachine.class);

    /** The Libvirt hyper. */
    private AbsLibvirtHypervisor libvirtHyper;

    /** the memory ram in Kbytes */
    private long memoryRam;

    /** The number of cpus */
    private int cpuNumbers;

    /** The machine name. */
    private final String machineName;

    /** The machine UUID. */
    private final UUID machineId;

    /** The remote desktop port */
    private final int rdpPort;

    /** Source virtual image on the remote repository. */
    private String imagePath;

    private final List<String> storagePoolList;

    /**
     * Sets containing the devices name used
     */
    private final CopyOnWriteArraySet<String> hdSet;

    private String domainXml;

    private final String kvmemulation;

    private String targetDatstore;

    /**
     * Instantiates a new Libvirt machine.
     * 
     * @param config the config
     * @throws VirtualMachineException the virtual machine exception
     */
    public LibvirtMachine(final VirtualMachineConfiguration config) throws VirtualMachineException
    {
        super(config);

        if (config.isSetHypervisor() && config.getHyper() instanceof AbsLibvirtHypervisor)
        {
            libvirtHyper = (AbsLibvirtHypervisor) config.getHyper();
        }
        else
        {
            throw new VirtualMachineException("LibvirtMachine requires a XenHypervisor or KVMHypervisor"
                + "on VirtualMachineConfiguration, not a "
                + config.getHyper().getClass().getCanonicalName());
        }

        machineName = config.getMachineName();
        machineId = config.getMachineId();
        rdpPort = config.getRdPort();
        cpuNumbers = config.getCpuNumber();
        memoryRam = config.getMemoryRAM() / 1024;
        hdSet = new CopyOnWriteArraySet<String>();
        storagePoolList = new ArrayList<String>();

        if (config.getHyper() instanceof KVMHypervisor
            && !new ConfigurationManager().getConfiguration().isFullVirt())
        {
            kvmemulation = "<type arch='x86_64' machine='pc'>hvm</type>";
        }
        else
        {
            kvmemulation = "<type>hvm</type>";
        }
    }

    private Connect connect(Connect conn) throws LibvirtException
    {
        if (conn != null)
        {
            if (conn.isConnected())
            {
                logger
                    .error("Trying to instance a connection already connected. Something is wrong in the code!.");
                return conn;
            }
        }

        conn = new Connect(libvirtHyper.getHypervisorUrl(libvirtHyper.getAddress()));

        return conn;
    }

    private Connect disconnect(final Connect conn) throws LibvirtException
    {
        if (conn != null)
        {
            conn.close();
        }

        return conn;
    }

    private Domain freeDomain(final Domain dom) throws LibvirtException
    {
        if (dom != null)
        {
            dom.free();
        }

        return dom;
    }

    private void disconnectAndThrowError(final Connect conn) throws VirtualMachineException
    {
        try
        {
            disconnect(conn);
        }
        catch (LibvirtException e)
        {
            logger.error("Failed to disconnect from Hypervisor :{}", e);
            throw new VirtualMachineException(e);
        }
    }

    private void disconnectAndThrowError(final Connect conn, final Domain... doms)
        throws VirtualMachineException
    {
        try
        {
            for (Domain dom : doms)
            {
                freeDomain(dom);
            }

            disconnect(conn);
        }
        catch (LibvirtException e)
        {
            logger.error("Failed to disconnect from Hypervisor :{}", e);
            throw new VirtualMachineException(e);
        }
    }

    private void disconnectAndLogError(final Connect conn, final Domain... doms)
    {
        try
        {
            for (Domain dom : doms)
            {
                freeDomain(dom);
            }

            disconnect(conn);
        }
        catch (LibvirtException e)
        {
            logger.error("Failed to disconnect from Hypervisor :{}", e);
        }
    }

    /**
     * Deploys the machine.
     */
    @Override
    public void deployMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            // Defines the domain
            if (!isVMAlreadyCreated())
            {
                // Clone the source image
                logger.info("Cloning the virtual machine: {}", getMachineName().toString());

                if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
                {
                    cloneVirtualDisk();
                }

                logger.info("Cloning successfully completed of virtual machine: {}",
                    getMachineName().toString());

                domainXml = defineXMLdomain(config);

                conn = connect(conn);
                dom = conn.domainDefineXML(domainXml);
            }

            logger.debug("Deploying machine for xml: {}", domainXml);

            checkIsCancelled();
        }
        catch (Exception e)
        {
            // The roll back in the virtual machine is done in top level when rolling back the
            // virtual appliance

            logger.error("Error while clonning virtual disk:{}", e);
            rollBackVirtualMachine();
            state = State.CANCELLED;
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }

        logger.info("Created libvirt machine name:" + config.getMachineName() + "\t ID:"
            + config.getMachineId().toString() + "\t " + "using hypervisor connection at "
            + config.getHyper().getAddress().toString());

        state = State.DEPLOYED;
    }

    /**
     * Private helper to create the xml definition of iscsi Storage Pool
     * 
     * @param poolName the storage pool name
     * @param hostIP the host IP
     * @param iqn the iqn
     * @return the storage pool XML definition
     * @throws TransformerFactoryConfigurationError
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private String createisCsiStoragePoolXML(final String poolName, final String hostIP,
        final String iqn) throws TransformerFactoryConfigurationError, SAXException, IOException,
        ParserConfigurationException, TransformerException
    {
        // Base XML
        String pool_xml =
            "<pool type='iscsi'>" + "<name>pool</name>" + "<source>"
                + "<host name='192.168.1.221'/>"
                + "<device path='iqn.1986-03.com.sun:02:5da9fe7e-faa6-e672-e9a1-cc7582f19650'/>"
                + "</source>" + "<target>" + "<path>/dev/disk/by-path</path>" + "</target>"
                + "</pool>";

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        StringReader reader = new StringReader(pool_xml);
        InputSource inputSource = new InputSource(reader);
        Document doc = docBuilder.parse(inputSource);
        replaceValue(doc, "name", poolName);
        // TODO the port attribute in host defines the port
        replaceAttribute(doc, "host", "name", hostIP);
        replaceAttribute(doc, "device", "path", iqn);
        // Convert document to string
        Transformer t = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        Source source_xml = new DOMSource(doc);
        t.transform(source_xml, result);
        pool_xml = result.getWriter().toString();
        return pool_xml;
    }

    @Override
    public State getStateInHypervisor()
    {
        State actualState = null;
        Connect conn = null;
        Domain dom = null;

        try
        {
            if (!isVMAlreadyCreated())
            {
                return State.NOT_DEPLOYED;
            }

            conn = connect(conn);
            dom = conn.domainLookupByName(getMachineName());
            DomainState domainState = dom.getInfo().state;

            switch (domainState)
            {
                case VIR_DOMAIN_RUNNING:
                    actualState = State.POWER_UP;
                    break;
                case VIR_DOMAIN_PAUSED:
                    actualState = State.PAUSE;
                    break;
                case VIR_DOMAIN_SHUTDOWN:
                    actualState = State.POWER_OFF;
                    break;
                case VIR_DOMAIN_CRASHED:
                    actualState = State.NOT_DEPLOYED;
                    break;
                case VIR_DOMAIN_SHUTOFF:
                    actualState = State.POWER_OFF;
                    break;
                case VIR_DOMAIN_BLOCKED:
                    actualState = State.POWER_UP;
                    break;
                case VIR_DOMAIN_NOSTATE:
                    actualState = State.UNKNOWN;
                default:
                    break;
            }
        }
        catch (LibvirtException e)
        {
            logger.error("{}", e);
            actualState = State.UNKNOWN;
        }
        catch (VirtualMachineException e)
        {
            logger.error("{}", e);
        }
        finally
        {
            disconnectAndLogError(conn, dom);
        }

        return actualState;
    }

    /**
     * Starts the virtual machine execution.
     */
    @Override
    public void powerOnMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            if (!checkState(State.POWER_UP))
            {
                // Create the domain
                conn = connect(conn);
                dom = conn.domainLookupByName(getMachineName());
                dom.create();
            }
        }
        catch (LibvirtException e)
        {
            logger.error("PowerOn exception caught:" + e);
            throw new VirtualMachineException(e);
        }
        catch (Exception e)
        {
            logger.error("Can not poweron the machine: {} ", e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Stops the virtual machine execution.
     */
    @Override
    public void powerOffMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            if (!checkState(State.POWER_OFF))
            {
                // Removes the domain
                conn = connect(conn);
                dom = conn.domainLookupByName(getMachineName());
                dom.destroy();
            }
        }
        catch (LibvirtException e)
        {
            logger.error("PowerOff exception caught:" + e);
            throw new VirtualMachineException(e);
        }
        catch (Exception e)
        {
            logger.error("Can not poweroff the machine: {} ", e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Pauses the virtual machine execution.
     */
    @Override
    public void pauseMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            if (!checkState(State.PAUSE))
            {
                // Suspend a domain
                conn = connect(conn);
                dom = conn.domainLookupByName(getMachineName());
                dom.suspend();
            }
        }
        catch (LibvirtException e)
        {
            logger.error("Pause exception caught:" + e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Resumes the virtual machine execution.
     */
    @Override
    public void resumeMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            if (!checkState(State.POWER_UP))
            {
                // Resume a domain
                conn = connect(conn);
                dom = conn.domainLookupByName(getMachineName());
                dom.resume();
            }
        }
        catch (LibvirtException e)
        {
            logger.error("Resume exception caught:" + e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Resets the virtual machine.
     */
    @Override
    public void resetMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            if (!checkState(State.POWER_UP))
            {
                conn = connect(conn);
                dom = conn.domainLookupByName(getMachineName());
                dom.reboot(0);
            }
        }
        catch (LibvirtException e)
        {
            logger.error("Reset exception caught:" + e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Removes virtual machine from hypervisor.
     */
    @Override
    public void deleteMachine() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            try
            {
                detachBridgeInterfaces(config);
            }
            catch (Exception e)
            {
                logger.error(
                    "An error was occurred then deconfiguring the networking resources: {}", e);
            }

            // Removes the domain
            conn = connect(conn);

            dom = conn.domainLookupByName(getMachineName());
            dom.undefine();

            // [ABICLOUDPREMIUM-1459] Should not be executed in stateful images
            if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
            {
                removeImage();
            }

            detachExtendedDisksFromConfig(config);
        }
        catch (LibvirtException e)
        {
            logger.error("Delete exception caught:" + e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Reconfig the virtual machine.
     */
    @Override
    public void reconfigVM(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            conn = connect(conn);
            dom = conn.domainLookupByName(getMachineName());

            // Setting the new Ram value
            if (newConfiguration.isRam_set())
            {
                logger.info("Libvirt: Reconfiguring The Virtual Machine For Memory Update "
                    + getMachineName());
                // the memory is set in kilobytes
                dom.setMaxMemory((newConfiguration.getMemoryRAM() / 1024));
                dom.setMemory((newConfiguration.getMemoryRAM() / 1024));
                memoryRam = newConfiguration.getMemoryRAM();
            }

            // Setting the number cpu value
            if (newConfiguration.isCpu_number_set())
            {
                logger.info("Libvirt: Reconfiguring The Virtual Machine For CPU Update "
                    + getMachineName());
                dom.setVcpus(newConfiguration.getCpuNumber());
                cpuNumbers = newConfiguration.getCpuNumber();
            }

            // Setting the disk cpu value
            logger.info("Libvirt: Reconfiguring The Virtual Machine For disk Update "
                + getMachineName());

            reconfigDisks(newConfiguration, config);

        }
        catch (Exception e)
        {
            logger.error("Libvirt reconfigure error:" + e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }
    }

    /**
     * Reconfigs the new extended disk list added
     * 
     * @param newConfiguration the new configuration with the new extended disks added
     * @param config the present configuration
     * @throws VirtualMachineException
     */
    private void reconfigDisks(final VirtualMachineConfiguration newConfiguration,
        final VirtualMachineConfiguration config) throws VirtualMachineException
    {
        throw new VirtualMachineException("Cannot reconfigure the extended disks in "
            + "KVM and XEN guests wihout undeploying.");

        // List<VirtualDisk> newExtendedDiskList = newConfiguration.getExtendedVirtualDiskList();
        // List<VirtualDisk> oldExtendedDiskList = config.getExtendedVirtualDiskList();
        //
        // // If there are no more extended disks, I remove the existent ones
        // if (newExtendedDiskList.isEmpty()
        // && newExtendedDiskList.size() < oldExtendedDiskList.size())
        // {
        // // It deteches all the storage pools
        // // detachExtendedDisksFromConfig(newConfiguration);
        //
        // // Updates the domain definition
        // // As the attach configuration does not work properly we are just adding the storage
        // // pools and modifying the xml definition
        // // Updates the domain definition
        // updateDomain(newConfiguration);
        // }
    }

    /**
     * Updates the xml domain definition with new changes contained in the virtual machine
     * configuration
     * 
     * @param newConfiguration the new configuration containing the changes
     * @throws VirtualMachineException
     */
    private void updateDomain(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        Connect conn = null;
        Domain domToUndefine = null;
        Domain domToDefine = null;

        try
        {
            conn = connect(conn);

            // It removes the domain
            domToUndefine = conn.domainLookupByName(getMachineName());
            domToUndefine.undefine();

            // It redefines the new domain with the new configuration
            domainXml = defineXMLdomain(newConfiguration);
            domToDefine = conn.domainDefineXML(domainXml);
        }
        catch (LibvirtException e)
        {
            logger.error("{}", e);
            throw new VirtualMachineException("There was a problem updating the domain, {}", e);
        }
        finally
        {
            disconnectAndThrowError(conn, domToUndefine, domToDefine);
        }
    }

    /**
     * Detaches the extended disk list from the configuration
     * 
     * @param newConfiguration the configuration containing the extended disk to detach
     * @throws VirtualMachineException
     */
    private void detachExtendedDisksFromConfig(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        Connect conn = null;

        try
        {
            conn = connect(conn);

            List<VirtualDisk> disksToRemove =
                new ArrayList<VirtualDisk>(config.getExtendedVirtualDiskList());
            disksToRemove.removeAll(newConfiguration.getExtendedVirtualDiskList());

            for (VirtualDisk vdisk : disksToRemove)
            {
                // TODO Attaching other STANDARD extended disks
                if (vdisk.getDiskType().compareTo(VirtualDiskType.ISCSI) == 0)
                {
                    String location = vdisk.getLocation();
                    int index = location.indexOf("|");
                    String ip = location.substring(0, index);
                    String iqn = location.substring(index + 1);
                    String pool_xml =
                        createisCsiStoragePoolXML(UUID.randomUUID().toString(), ip, iqn);
                    logger.debug("Deleting the Storage pool: {}", pool_xml);
                    List<String> poolsToRemove = new ArrayList<String>();

                    for (String spId : storagePoolList)
                    {
                        StoragePool storagePool = conn.storagePoolLookupByName(spId);
                        poolsToRemove.add(storagePool.getName());

                        String[] volumes = storagePool.listVolumes();
                        for (String volumeName : volumes)
                        {
                            StorageVol volume = storagePool.storageVolLookupByName(volumeName);
                            if (volume.getPath().contains(iqn))
                            {
                                String volumeXML = volume.getXMLDesc(0); // Freeing unused hd dev
                                // target
                                hdSet.remove(getTargetFromVolumeXML(volumeXML));
                                logger.debug("Deleting the volume: {}", volume.getXMLDesc(0));
                            }
                        }
                        storagePool.destroy();
                    }
                    storagePoolList.removeAll(poolsToRemove);
                }
            }
        }
        catch (ParserConfigurationException e)
        {
            logger.error("Exception caught in parsing XML:" + e);
            throw new VirtualMachineException(e);
        }
        catch (SAXException e)
        {
            logger.error("Exception caught in parsing XML:" + e);
            throw new VirtualMachineException(e);
        }
        catch (IOException e)
        {
            logger.error("Exception caught in parsing XML:" + e);
            throw new VirtualMachineException(e);
        }
        catch (LibvirtException e)
        {
            logger.error("Exception caught in creating device XML:" + e);
            throw new VirtualMachineException(e);
        }
        catch (TransformerConfigurationException e)
        {
            logger.error("Exception caught in transoforming XML to String:" + e);
            throw new VirtualMachineException(e);
        }
        catch (TransformerFactoryConfigurationError e)
        {
            logger.error("Exception caught in transoforming XML to String:" + e);
            throw new VirtualMachineException(e);
        }
        catch (TransformerException e)
        {
            logger.error("Exception caught in transoforming XML to String:" + e);
            throw new VirtualMachineException(e);
        }
        finally
        {
            disconnectAndThrowError(conn);
        }
    }

    /**
     * Private helper to get the target forum the XML volume definition
     * 
     * @param xmlVolume the xml definition volum
     * @return the dev target
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private String getTargetFromVolumeXML(final String xmlVolume) throws SAXException, IOException,
        ParserConfigurationException
    {
        String dev = null;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        StringReader reader = new StringReader(xmlVolume);
        InputSource inputSource = new InputSource(reader);
        Document doc = docBuilder.parse(inputSource);
        NodeList nodeList = doc.getElementsByTagName("target");
        nodeList.item(0);

        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node nodeAttr = nodeList.item(i).getAttributes().getNamedItem("dev");
            if (nodeAttr != null)
            {
                dev = nodeAttr.getNodeValue();
            }
        }

        return dev;
    }

    /**
     * Creates de xml config for the virtual machine.
     * 
     * @param configuration the virtual machine configuration
     * @return XML config
     */
    private String defineXMLdomain(final VirtualMachineConfiguration configuration)
        throws VirtualMachineException
    {
        // Base XML
        String src_xml =
            "<domain type='kvm'>" + "<name>debian</name>"
                + "<uuid>cdba2d72-2e91-b5ff-81ab-b9088648d4db</uuid>" + "<memory>524288</memory>"
                + "<currentMemory>524288</currentMemory>" + "<vcpu>1</vcpu>" + "<os>"
                + kvmemulation + "<boot dev='hd'/>" + "<loader>/usr/bin/qemu-kvm</loader>"
                + "</os>" + "<features>" + "<acpi/><apic/><pae/>" + "</features>"
                + "<clock offset='utc'/>" + "<on_poweroff>destroy</on_poweroff>"
                + "<on_reboot>restart</on_reboot>" + "<on_crash>destroy</on_crash>" + "<devices>"
                + "<emulator>/usr/bin/qemu-kvm</emulator>" + "<input type='mouse' bus='ps2'/>";

        // Only add the VNC port if it is enabled
        if (AddressingUtils.isValidPort(String.valueOf(rdpPort)))
        {
            src_xml += "<graphics type='vnc' port='???' listen='???'/>";
        }

        src_xml +=
            "<serial type='pty'>" + "<target port='0'/>" + "</serial>" + "<console type='pty'>"
                + "<target port='0'/>" + "</console>" + "</devices>" + "</domain>";

        String new_xml = null;
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            StringReader reader = new StringReader(src_xml);
            InputSource inputSource = new InputSource(reader);
            Document doc = docBuilder.parse(inputSource);

            /*
             * Connect conn = libvirtHyper.getConnection(); int nextId = conn.numOfDefinedDomains()
             * + conn.numOfDomains(); replaceAttribute(doc, "domain", "id", String.valueOf(nextId));
             */
            replaceValue(doc, "name", getMachineName().toString());
            replaceValue(doc, "uuid", machineId.toString());
            replaceValue(doc, "memory", Long.toString(memoryRam));
            replaceValue(doc, "currentMemory", Long.toString(memoryRam));
            replaceValue(doc, "vcpu", Long.toString(cpuNumbers));

            // Analyzing if the base virtual disk is an stateless or statefull disk

            if (configuration.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
            {
                String targetDatastore = getDatastore(configuration.getVirtualDiskBase());
                attachDisktoDoc(doc, "hda", targetDatastore + getMachineName().toString(), "ide",
                    configuration.getVirtualDiskBase().getFormat());
            }
            else if (configuration.getVirtualDiskBase().getDiskType() == VirtualDiskType.ISCSI)
            {
                attachIscsiDisk(configuration.getVirtualDiskBase(), doc, "hda", "virtio");
            }

            // replaceAttribute(doc, "source", "file", destinationRepository// + File.separatorChar
            // + machineId.toString());// XXX clonedImageName);

            // Attach extended disk to the xml domain definition
            attachExtendedDisk(doc, configuration);

            // Attach network interface to the xml definition
            attachBridgeInterfaces(doc, configuration);

            // Only add the VNC port if it is enabled
            if (AddressingUtils.isValidPort(String.valueOf(rdpPort)))
            {
                replaceAttribute(doc, "graphics", "listen", "0.0.0.0");
                replaceAttribute(doc, "graphics", "port", Integer.toString(rdpPort));
            }

            if (libvirtHyper.getHypervisorType().toLowerCase().equals("kvm"))
            {
                replaceAttribute(doc, "domain", "type", "kvm");
            }
            else if (libvirtHyper.getHypervisorType().toLowerCase().equals("xen-3"))
            {
                replaceValue(doc, "emulator", "/usr/lib64/xen/bin/qemu-dm");
                replaceAttribute(doc, "domain", "type", "xen");
                replaceValue(doc, "loader", "/usr/lib/xen/boot/hvmloader");
            }

            // Convert document to string
            Transformer t = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            Source source_xml = new DOMSource(doc);
            t.transform(source_xml, result);
            new_xml = result.getWriter().toString();

            logger.debug("libVirt virtual machine document :\n{}", new_xml);

        }
        catch (Exception e)
        {
            logger.error("Exception caught in parsing XML:" + e);
            throw new VirtualMachineException(e);
        }
        return new_xml;
    }

    /**
     * Private helper to attach bridge interfaces list to document
     * 
     * @param doc
     * @param configuration
     * @throws VirtualMachineException
     */
    private void attachBridgeInterfaces(final Document doc,
        final VirtualMachineConfiguration configuration) throws VirtualMachineException
    {
        try
        {
            String abiquoPrefix =
                AbiCloudModel.getInstance().getConfigManager().getConfiguration().getBridgePrefix();
            for (VirtualNIC virtualNIC : configuration.getVnicList())
            {
                String bridgeName = abiquoPrefix + "_" + virtualNIC.getVlanTag();

                // Creating the VLAN
                URL phymach_ip = configuration.getHyper().getAddress();

                VlanStub.createVlan(phymach_ip, String.valueOf(virtualNIC.getVlanTag()), virtualNIC
                    .getVSwitchName(), bridgeName);

                attachBridgeToDoc(doc, virtualNIC.getMacAddress(), bridgeName);

            }
        }
        catch (VLANException e)
        {
            throw new VirtualMachineException("An error was occurred when configuring the networking for virtual machine :"
                + getMachineName(),
                e);
        }

    }

    /**
     * Private helper to detach bridge interfaces list to document
     * 
     * @param configuration
     * @throws VirtualMachineException
     */
    private void detachBridgeInterfaces(final VirtualMachineConfiguration configuration)
        throws VirtualMachineException
    {
        try
        {
            String abiquoPrefix =
                AbiCloudModel.getInstance().getConfigManager().getConfiguration().getBridgePrefix();
            for (VirtualNIC virtualNIC : configuration.getVnicList())
            {
                String bridgeName = abiquoPrefix + "_" + virtualNIC.getVlanTag();
                if (mustDeleteVLAN(bridgeName))
                {
                    // Creating the VLAN
                    URL phymach_ip = configuration.getHyper().getAddress();

                    VlanStub.deleteVlan(phymach_ip, String.valueOf(virtualNIC.getVlanTag()),
                        virtualNIC.getVSwitchName(), bridgeName);
                }

            }
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("An error was occurred when configuring the networking for virtual machine :"
                + getMachineName(),
                e);
        }

    }

    /**
     * Checks in all virtual machines in the hypervisor to check if the bridge name is in used. If
     * is in used the bridge name tagged with the VLAN tag cann't be deleted
     * 
     * @param bridgeName the bridge name
     * @return true if the bridge name can be deleted, false if not.
     * @throws LibvirtException
     * @throws XPathExpressionException
     */
    private boolean mustDeleteVLAN(final String bridgeName) throws XPathExpressionException,
        LibvirtException
    {
        final List<Domain> listOfDomains = new ArrayList<Domain>();
        Connect conn = null;
        Domain dom = null;

        try
        {
            conn = connect(conn);

            // Defined domains are the closed ones!
            for (String domainValue : conn.listDefinedDomains())
            {
                listOfDomains.add(conn.domainLookupByName(domainValue));
            }

            // Domains are the started ones
            for (int domainInt : conn.listDomains())
            {
                listOfDomains.add(conn.domainLookupByID(domainInt));
            }

            dom = conn.domainLookupByName(getMachineName());
            listOfDomains.remove(dom);

            for (Domain domain : listOfDomains)
            {
                if (domain.getName().equals(getMachineName().toString()))
                {
                    continue;
                }

                String domainXML = domain.getXMLDesc(0);

                if (bridgeName.equals(XPathUtils.getValue("//devices/interface/source/@bridge",
                    domainXML)))
                {
                    return false;
                }
            }
        }
        finally
        {
            for (Domain domain : listOfDomains)
            {
                freeDomain(domain);
            }

            freeDomain(dom);

            disconnect(conn);
        }

        return true;

    }

    /**
     * Private helper to attach a bridge interface to the doc element
     * 
     * @param doc the domain xml definition
     * @param macAddress the mac address
     * @param bridgeName the bridge name
     */
    private void attachBridgeToDoc(final Document doc, final String macAddress,
        final String bridgeName)
    {
        NodeList nodeList = doc.getElementsByTagName("devices");
        Node node = nodeList.item(0);
        Element modelElement = doc.createElement("model");
        modelElement.setAttribute("type", "e1000");
        Element interfaceElement = doc.createElement("interface");
        interfaceElement.setAttribute("type", "bridge");
        Element macElement = doc.createElement("mac");
        macElement.setAttribute("address", macAddress);
        Element sourceElement = doc.createElement("source");
        sourceElement.setAttribute("bridge", bridgeName);
        interfaceElement.appendChild(macElement);
        interfaceElement.appendChild(sourceElement);
        interfaceElement.appendChild(modelElement);
        node.appendChild(interfaceElement);

    }

    /**
     * Private helper to attach the extended disks to the xml domain definition
     * 
     * @param doc the xml domain definition
     * @param config the configuration file containing the extended disks to attach
     */
    private void attachExtendedDisk(final Document doc,
        final VirtualMachineConfiguration configuration) throws Exception
    {
        for (VirtualDisk vdisk : configuration.getExtendedVirtualDiskList())
        {
            // TODO Attaching other STANDARD extended disks
            if (vdisk.getDiskType().compareTo(VirtualDiskType.ISCSI) == 0)
            {
                vdisk.setFormat("raw");
                attachIscsiDisk(vdisk, doc, null, "ide");
            }
        }
    }

    /**
     * Private helper to attach an ISCSI disk to a virtual machine
     * 
     * @param vdisk The Virtual disk to attach
     * @param doc the document of the domain definition
     * @param forcedTarget TODO
     * @param busType TODO
     * @throws Exception
     */
    private void attachIscsiDisk(final VirtualDisk vdisk, final Document doc,
        final String forcedTarget, final String busType) throws Exception
    {
        Connect conn = null;

        try
        {
            conn = connect(conn);

            String location = vdisk.getLocation();
            int index = location.indexOf("|");
            String ip = location.substring(0, index);
            String iscsiPath = location.substring(index + 1);
            String iqn = AddressingUtils.getIQN(iscsiPath);
            String pool_xml = createisCsiStoragePoolXML(UUID.randomUUID().toString(), ip, iqn);
            logger.debug("Creating the Storage pool: {}", pool_xml);
            StoragePool storagePool = conn.storagePoolCreateXML(pool_xml, 0);
            storagePoolList.add(storagePool.getName());
            String volumePath = "/dev/disk/by-path/" + iscsiPath;
            logger.debug("Adding the volume in this volume path: {}", volumePath);
            String target;
            if (forcedTarget == null)
            {
                target = getFreeTargetForDevice();
                hdSet.add(target);
            }
            else
            {
                target = forcedTarget;
            }
            attachLUNDisktoDoc(doc, target, volumePath, busType, vdisk.getFormat());
            logger.debug("Added new ISCSI target, in this location: {}", location);
        }
        finally
        {
            disconnect(conn);
        }
    }

    /**
     * Private helper to attaching a disk element to the xml domain definition
     * 
     * @param doc document defining the xml element
     * @param target the target device
     * @param volumePath the path disk file
     * @param busType TODO
     */
    private void attachDisktoDoc(final Document doc, final String target, final String volumePath,
        final String busType, final String diskFormat)
    {

        NodeList nodeList = doc.getElementsByTagName("devices");
        Node node = nodeList.item(0);
        Element diskElement = doc.createElement("disk");
        diskElement.setAttribute("device", "disk");
        Element targetElement = doc.createElement("target");
        targetElement.setAttribute("dev", target);
        targetElement.setAttribute("bus", busType);
        Element sourceElement = doc.createElement("source");
        if (libvirtHyper.getHypervisorType().toLowerCase().equals("kvm"))
        {
            diskElement.setAttribute("type", "file");
            sourceElement.setAttribute("file", volumePath);
            Element driverElement = doc.createElement("driver");
            driverElement.setAttribute("name", "qemu");
            driverElement.setAttribute("type", diskFormat);
            diskElement.appendChild(driverElement);
        }
        else if (libvirtHyper.getHypervisorType().toLowerCase().equals("xen-3"))
        {
            /*
             * diskElement.setAttribute("type", "block"); Element driverElement =
             * doc.createElement("driver"); driverElement.setAttribute("name", "phy");
             * sourceElement.setAttribute("dev", volumePath);
             * diskElement.appendChild(driverElement);
             */
            diskElement.setAttribute("type", "file");
            sourceElement.setAttribute("file", volumePath);
            Element driverElement = doc.createElement("driver");
            driverElement.setAttribute("name", "file");
            // driverElement.setAttribute("type", diskFormat);
            diskElement.appendChild(driverElement);
        }
        diskElement.appendChild(sourceElement);
        diskElement.appendChild(targetElement);
        node.appendChild(diskElement);
    }

    /**
     * Private helper to attaching a LUN disk element to the xml domain definition
     * 
     * @param doc document defining the xml element
     * @param target the target device
     * @param volumePath the path disk file
     * @param busType TODO
     */
    private void attachLUNDisktoDoc(final Document doc, final String target,
        final String volumePath, final String busType, final String diskFormat)
    {

        NodeList nodeList = doc.getElementsByTagName("devices");
        Node node = nodeList.item(0);
        Element diskElement = doc.createElement("disk");
        diskElement.setAttribute("device", "disk");
        Element targetElement = doc.createElement("target");
        targetElement.setAttribute("dev", target);
        targetElement.setAttribute("bus", busType);
        Element sourceElement = doc.createElement("source");
        if (libvirtHyper.getHypervisorType().toLowerCase().equals("kvm"))
        {
            // diskElement.setAttribute("type", "file");
            diskElement.setAttribute("type", "block");
            // sourceElement.setAttribute("file", volumePath);
            sourceElement.setAttribute("dev", volumePath);
            Element driverElement = doc.createElement("driver");
            driverElement.setAttribute("name", "qemu");
            driverElement.setAttribute("type", diskFormat);
            diskElement.appendChild(driverElement);
        }
        else if (libvirtHyper.getHypervisorType().toLowerCase().equals("xen-3"))
        {
            diskElement.setAttribute("type", "block");
            Element driverElement = doc.createElement("driver");
            driverElement.setAttribute("name", "phy");
            sourceElement.setAttribute("dev", volumePath);
            diskElement.appendChild(driverElement);
        }
        diskElement.appendChild(sourceElement);
        diskElement.appendChild(targetElement);
        node.appendChild(diskElement);
    }

    /**
     * Perform the virtual image cloning. Creates a copy of the original image and put it on where
     * the current hypervisor expects to load it.
     * 
     * @throws
     * @throws RimpException
     */
    protected void cloneVirtualDisk() throws Exception
    {
        String hypervisorLocation = libvirtHyper.getAddress().getHost();
        VirtualDisk diskBase = config.getVirtualDiskBase();
        imagePath = diskBase.getImagePath();
        targetDatstore = getDatastore(diskBase);

        logger.debug("Cloning image[{}]", imagePath);

        Iface aimclient =
            TTransportProxy.getInstance(hypervisorLocation, libvirtHyper.getAddress().getPort());
        aimclient.copyFromRepositoryToDatastore(imagePath, targetDatstore, getMachineName()
            .toString());

        logger.debug("Cloning success, at [{}] ", targetDatstore + getMachineName().toString());
    }

    /**
     * Removes a virtual machine disk.
     */
    protected void removeImage() throws VirtualMachineException
    {
        VirtualDisk diskBase = config.getVirtualDiskBase();
        targetDatstore = getDatastore(diskBase);

        if (targetDatstore != null)
        {
            String hypervisorLocation = libvirtHyper.getAddress().getHost();

            try
            {
                Iface aimclient =
                    TTransportProxy.getInstance(hypervisorLocation, libvirtHyper.getAddress()
                        .getPort());

                aimclient.deleteVirtualImageFromDatastore(targetDatstore, getMachineName()
                    .toString());
            }
            catch (Exception e)
            {
                throw new VirtualMachineException(e);
            }
        }
        // else is an statefull one
    }

    /**
     * Modify a XML tag
     * 
     * @param doc Original XML document
     * @param tagName Tag name will be modified
     * @param replaceValue New value for the tagName
     * @return
     */
    private static void replaceValue(final Document doc, final String tagName,
        final String replaceValue)
    {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        Node node = nodeList.item(0);
        node.getFirstChild().setNodeValue(replaceValue);
    }

    /**
     * Modify a XML tag
     * 
     * @param doc Original XML document
     * @param tagName Tag name will be modified
     * @param replaceValue New value for the tagName
     * @return
     */
    private static void replaceAttribute(final Document doc, final String tagName,
        final String attrName, final String attrValue)
    {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node nodeAttr = nodeList.item(i).getAttributes().getNamedItem(attrName);
            if (nodeAttr != null)
            {
                nodeAttr.setNodeValue(attrValue);
            }
        }
    }

    /**
     * Gets the free device name
     * 
     * @return
     */
    private String getFreeTargetForDevice()
    {
        char init = 'b';
        char end = 'z';
        for (char i = init; i <= end; i++)
        {
            if (!hdSet.contains("hd" + i))
            {
                return "hd" + i;
            }
        }
        return "hd";
    }

    @Override
    public boolean isVMAlreadyCreated() throws VirtualMachineException
    {
        Connect conn = null;
        Domain dom = null;

        try
        {
            conn = connect(conn);
            dom = conn.domainLookupByName(getMachineName());
        }
        catch (LibvirtException e)
        {
            logger.debug("The Virtual machine: {} does not exist, proceding to create",
                getMachineName());
            return false;
        }
        finally
        {
            disconnectAndThrowError(conn, dom);
        }

        return true;
    }

    /**
     * Private helper to check the real state of the virtual machine
     * 
     * @param stateToCheck the state to check
     * @return true if the state in the hypervisors equals to the state as parameter, false if
     *         contrary
     */
    protected boolean checkState(final State stateToCheck) throws VirtualMachineException
    {
        State actualState = getStateInHypervisor();
        if (actualState.compareTo(stateToCheck) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void bundleVirtualMachine(final String sourcePath, final String destinationPath,
        final String snapshotName, final boolean isManaged) throws VirtualMachineException
    {
        try
        {
            String hypervisorLocation = libvirtHyper.getAddress().getHost();
            String sourceFolder = null;

            if (isManaged)
            {
                sourceFolder = getDatastore(config.getVirtualDiskBase());
                imagePath = sourceFolder;
            }
            else
            {
                imagePath = config.getVirtualDiskBase().getImagePath();
                int indexEndImagePath = imagePath.lastIndexOf('/');
                imagePath = imagePath.substring(0, indexEndImagePath);
                sourceFolder = imagePath;

            }

            Iface aimclient =
                TTransportProxy
                    .getInstance(hypervisorLocation, libvirtHyper.getAddress().getPort());
            aimclient.copyFromDatastoreToRepository(getMachineName().toString(), snapshotName,
                destinationPath, sourceFolder);
        }
        catch (Exception e)
        {
            String errorMessage = "Failed to bundle the virtual machine: {}, :{}";
            logger.error(errorMessage, getMachineName(), e);
            throw new VirtualMachineException(e.getMessage(), e);
        }
    }

    private String getDatastore(final VirtualDisk disk)
    {
        String datastore = disk.getTargetDatastore();
        if (!datastore.endsWith("/"))
        {
            datastore += "/";
        }
        return datastore;
    }

    // Gettes and setters

    public AbsLibvirtHypervisor getLibvirtHyper()
    {
        return libvirtHyper;
    }

    public void setLibvirtHyper(final AbsLibvirtHypervisor libvirtHyper)
    {
        this.libvirtHyper = libvirtHyper;
    }

    public long getMemoryRam()
    {
        return memoryRam;
    }

    public void setMemoryRam(final long memoryRam)
    {
        this.memoryRam = memoryRam;
    }

    public int getCpuNumbers()
    {
        return cpuNumbers;
    }

    public void setCpuNumbers(final int cpuNumbers)
    {
        this.cpuNumbers = cpuNumbers;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(final String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getDomainXml()
    {
        return domainXml;
    }

    public void setDomainXml(final String domainXml)
    {
        this.domainXml = domainXml;
    }

    public int getRdpPort()
    {
        return rdpPort;
    }

    public List<String> getStoragePoolList()
    {
        return storagePoolList;
    }

    public CopyOnWriteArraySet<String> getHdSet()
    {
        return hdSet;
    }

    public String getMachineName()
    {
        return machineName;
    }

}
