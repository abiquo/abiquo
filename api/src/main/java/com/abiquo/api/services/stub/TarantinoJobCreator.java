package com.abiquo.api.services.stub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.commons.amqp.impl.tarantino.domain.DiskDescription;
import com.abiquo.commons.amqp.impl.tarantino.domain.DiskDescription.DiskControllerType;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition;
import com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition.PrimaryDisk;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ApplyVirtualMachineStateOp;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ReconfigureVirtualMachineOp;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.StorageRep;

/**
 * Creates tarantino data transfer objects form {@link VirtualMachine}
 */
@Service
// TODO consider not using a singleton. @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class TarantinoJobCreator extends DefaultApiService
{
    protected final static Logger logger = LoggerFactory.getLogger(TarantinoJobCreator.class);

    @Autowired
    protected StorageRep storageRep;

    @Autowired
    protected VirtualDatacenterRep vdcRep;

    @Autowired
    protected InfrastructureRep infRep;

    @Autowired
    protected RemoteServiceService remoteServiceService;

    /* ********************************** Be aware of storing any kind of state in this Service */

    public TarantinoJobCreator()
    {

    }

    public TarantinoJobCreator(final EntityManager em)
    {
        storageRep = new StorageRep(em);
        vdcRep = new VirtualDatacenterRep(em);
        infRep = new InfrastructureRep(em);
        remoteServiceService = new RemoteServiceService(em);
    }

    /**
     * Creates a {@link VirtualMachineDescriptionBuilder} from the current BBDD state.
     * 
     * @param virtualMachine to mount definition.
     * @param virtualAppliance, virtual machine context info (app and virtualdatacenter)
     * @return VirtualMachineDescriptionBuilder to represent the current virtual machine
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VirtualMachineDescriptionBuilder toTarantinoDto(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance)
    {
        final VirtualDatacenter virtualDatacenter = virtualAppliance.getVirtualDatacenter();
        final Integer dcId = virtualDatacenter.getDatacenter().getId();

        final VirtualMachineDescriptionBuilder vmDesc = new VirtualMachineDescriptionBuilder();

        logger.debug("Creating disk information");
        primaryDiskDefinitionConfiguration(virtualMachine, vmDesc, dcId);
        logger.debug("Disk information created!");

        vmDesc.hardware(virtualMachine.getCpu(), virtualMachine.getRam());
        vmDesc.setRdPort(virtualMachine.getVdrpPort());

        logger.debug("Creating the network related configuration");
        addDhcpConfiguration(dcId, vmDesc);
        vnicDefinitionConfiguration(virtualMachine, vmDesc);
        logger.debug("Network configuration done!");

        logger.debug("Creating the bootstrap configuration");
        bootstrapConfiguration(virtualMachine, vmDesc, virtualDatacenter, virtualAppliance);
        logger.debug("Bootstrap configuration done!");

        logger.debug("Configure secondary iSCSI volumes");
        secondaryScsiDefinition(virtualMachine, vmDesc);
        logger.debug("Configure secondary iSCSI done!");

        logger.debug("Configure secondary Hard Disks");
        secondaryHardDisksDefinition(virtualMachine, vmDesc);
        logger.debug("Configure secondary Hard Disks done!");

        return vmDesc;
    }

    /**
     * Gets the configured DCHP in the datacenter to set its URL in the
     * {@link com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition.NetworkConfiguration}
     */
    private void addDhcpConfiguration(final Integer datacenterId,
        final VirtualMachineDescriptionBuilder vmDesc)
    {
        // TODO 2.0 will support manual DHCP configuration
        final RemoteService dhcp =
            remoteServiceService.getRemoteService(datacenterId, RemoteServiceType.DHCP_SERVICE);

        try
        {
            final URI dhcpUri = new URI(dhcp.getUri());
            vmDesc.dhcp(dhcpUri.getHost(), dhcpUri.getPort());
        }
        catch (URISyntaxException e)
        {
            addConflictErrors(APIError.REMOTE_SERVICE_DHCP_WRONG_URI);
            flushErrors();
        }
    }

    /**
     * Documented in Abiquo EE.
     * 
     * @param virtualMachine
     * @param vmDesc void
     */
    public void secondaryScsiDefinition(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc)
    {
        // PREMIUM
        logger.debug("auxDiscsDefinition community implementation");
    }

    /**
     * Add the secondary hard disks.
     * 
     * @param virtualMachine virtual machine object.
     * @param vmDesc definition to send.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void secondaryHardDisksDefinition(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc)
    {

        List<DiskManagement> hardDisks = storageRep.findHardDisksByVirtualMachine(virtualMachine);

        String datastore =
            FilenameUtils.concat(virtualMachine.getDatastore().getRootPath(), virtualMachine
                .getDatastore().getDirectory());

        DiskControllerType cntrlType =
            getDiskController(virtualMachine.getHypervisor().getType(), false, false);

        Integer sequence = 1;

        for (DiskManagement imHard : hardDisks)
        {
            vmDesc.addSecondaryHardDisk(imHard.getSizeInMb() * 1048576, sequence, datastore,
                cntrlType);

            sequence++;
        }
    }

    public ApplyVirtualMachineStateOp applyStateVirtualMachineConfiguration(
        final VirtualMachine virtualMachine, final DatacenterTasks deployTask,
        final VirtualMachineDescriptionBuilder vmDesc,
        final HypervisorConnection hypervisorConnection,
        final VirtualMachineStateTransition stateTransition)
    {
        ApplyVirtualMachineStateOp stateJob = new ApplyVirtualMachineStateOp();
        stateJob.setVirtualMachine(vmDesc.build(virtualMachine.getUuid()));
        stateJob.setHypervisorConnection(hypervisorConnection);
        stateJob.setTransaction(com.abiquo.commons.amqp.impl.tarantino.domain.StateTransition
            .fromValue(stateTransition.name()));
        stateJob.setId(deployTask.getId() + "." + virtualMachine.getUuid());
        return stateJob;
    }

    public ApplyVirtualMachineStateOp configureJobConfiguration(
        final VirtualMachine virtualMachine, final DatacenterTasks deployTask,
        final VirtualMachineDescriptionBuilder vmDesc,
        final HypervisorConnection hypervisorConnection)
    {
        ApplyVirtualMachineStateOp configJob = new ApplyVirtualMachineStateOp();
        configJob.setVirtualMachine(vmDesc.build(virtualMachine.getUuid()));
        configJob.setHypervisorConnection(hypervisorConnection);
        configJob.setTransaction(StateTransition.CONFIGURE);
        configJob.setId(deployTask.getId() + "." + virtualMachine.getUuid() + "configure");
        return configJob;
    }

    /**
     * Creates a reconfigure task. The Job id identifies this job and is neede to create the ids of
     * the items. It is hyerarchic so Task 1 and its job would be 1.1, another 1.2
     * 
     * @param vm The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The reconfigure task.
     */
    public DatacenterTasks reconfigureTask(final VirtualMachine vm,
        final VirtualMachineDescriptionBuilder originalConfig,
        final VirtualMachineDescriptionBuilder newConfig)
    {
        DatacenterTasks reconfigureTask = new DatacenterTasks();
        reconfigureTask.setId(vm.getUuid());
        reconfigureTask.setDependent(true);

        HypervisorConnection hypervisorConnection =
            hypervisorConnectionConfiguration(vm.getHypervisor());

        ReconfigureVirtualMachineOp reconfigureJob = new ReconfigureVirtualMachineOp();
        reconfigureJob.setVirtualMachine(originalConfig.build(vm.getUuid()));
        reconfigureJob.setNewVirtualMachine(newConfig.build(vm.getUuid()));
        reconfigureJob.setHypervisorConnection(hypervisorConnection);
        reconfigureJob.setId(reconfigureTask.getId() + "." + vm.getUuid() + "reconfigure");

        reconfigureTask.getJobs().add(reconfigureJob);

        return reconfigureTask;
    }

    public HypervisorConnection hypervisorConnectionConfiguration(final Hypervisor hypervisor)
    {
        HypervisorConnection hypervisorConnection = new HypervisorConnection();
        hypervisorConnection.setHypervisorType(HypervisorConnection.HypervisorType
            .valueOf(hypervisor.getType().name()));
        // XXX Dummy implementation
        // hypervisorConnection.setHypervisorType(HypervisorConnection.HypervisorType.TEST);
        hypervisorConnection.setIp(hypervisor.getIp());
        hypervisorConnection.setLoginPassword(hypervisor.getPassword());
        hypervisorConnection.setLoginUser(hypervisor.getUser());
        return hypervisorConnection;
    }

    /**
     * In community there are no statful image. If some {@link VirtualImageConversion} attached use
     * his properties when defining the {@link PrimaryDisk}, else use the {@link VirtualImage}
     * 
     * @param virtualMachine
     * @param vmDesc
     * @param idDatacenter void
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void primaryDiskDefinitionConfiguration(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc, final Integer idDatacenter)
    {
        String datastore =
            FilenameUtils.concat(virtualMachine.getDatastore().getRootPath(), virtualMachine
                .getDatastore().getDirectory());

        // Repository Manager address
        List<RemoteService> services =
            infRep.findRemoteServiceWithTypeInDatacenter(infRep.findById(idDatacenter),
                RemoteServiceType.APPLIANCE_MANAGER);
        RemoteService repositoryManager = null;
        if (!services.isEmpty())
        {
            // Only one remote service of each type by datacenter.
            repositoryManager = services.get(0);
        }
        else
        {
            addNotFoundErrors(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
            flushErrors();
        }

        final VirtualImage vimage = virtualMachine.getVirtualImage();
        final HypervisorType htype = virtualMachine.getHypervisor().getType();

        final VirtualImageConversion conversion = virtualMachine.getVirtualImageConversion();

        final DiskFormatType format =
            conversion != null ? conversion.getTargetType() : vimage.getDiskFormatType();
        final Long size = conversion != null ? conversion.getSize() : vimage.getDiskFileSize();
        final String path = conversion != null ? conversion.getTargetPath() : vimage.getPath();
        final DiskControllerType cntrlType = getDiskController(htype, true, false);

        if (cntrlType != null && cntrlType == DiskControllerType.SCSI
            && format == DiskFormatType.VMDK_SPARSE)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_ESXI_INCOMPATIBLE_DISK_CONTROLLER);
            flushErrors();
        }

        vmDesc.primaryDisk(DiskDescription.DiskFormatType.valueOf(format.name()), size,
            virtualMachine.getVirtualImage().getRepository().getUrl(), path, datastore,
            repositoryManager.getUri(), cntrlType);
    }

    /**
     * Only ESXi. Else return null.
     * <p>
     * Reads the ''abiquo.esxi.diskController'' properties or use the default: IDE for
     * non-persistent primary disks and SCSI for aux disk and persistent primary.
     * 
     * @param hypervisorType, the target hypervisor type
     * @param isPrimary, primary or secondary disk being added.
     * @param aux disks always isStateful
     */
    protected DiskControllerType getDiskController(final HypervisorType hypervisorType,
        final boolean isPrimary, final boolean isStateful)
    {

        if (hypervisorType != HypervisorType.VMX_04)
        {
            return null;
        }
        else
        {
            final String primaryOrSecondary = isPrimary ? "primary" : "secondary";

            final String controllerProperty =
                System.getProperty("abiquo.diskController." + primaryOrSecondary);

            if (!StringUtils.isEmpty(controllerProperty))
            {
                try
                {
                    return DiskControllerType.valueOf(controllerProperty.toUpperCase());
                }
                catch (Exception e)
                {
                    logger.error("Invalid ''abiquo.diskController.{}'' property,"
                        + "should use IDE/SCSI, but is {}", primaryOrSecondary, controllerProperty);
                }
            }

            if (isStateful)
            {
                return DiskControllerType.SCSI;
            }
            else
            {
                return DiskControllerType.IDE;
            }
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public void vnicDefinitionConfiguration(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc)
    {

        List<IpPoolManagement> ipPoolManagementByMachine =
            vdcRep.findIpsByVirtualMachine(virtualMachine);

        for (IpPoolManagement i : ipPoolManagementByMachine)
        {
            if (i.getConfigureGateway())
            {
                // This interface is the one that configures the Network parameters.
                // We force the forward mode to BRIDGED
                logger.debug("Network configuration with gateway");

                NetworkConfiguration configuration = i.getVlanNetwork().getConfiguration();
                vmDesc.addNetwork(i.getMac(), i.getIp(), virtualMachine.getHypervisor()
                    .getMachine().getVirtualSwitch(), i.getNetworkName(), i.getVlanNetwork()
                    .getTag() == null ? 0 : i.getVlanNetwork().getTag(), i.getName(), configuration
                    .getFenceMode(), configuration.getAddress(), configuration.getGateway(),
                    configuration.getNetMask(), configuration.getPrimaryDNS(), configuration
                        .getSecondaryDNS(), configuration.getSufixDNS(), Integer.valueOf(i
                        .getRasd().getConfigurationName()));
                continue;
            }
            logger.debug("Network configuration without gateway");
            // Only the data not related to the network since this data is configured based on the
            // configureNetwork parameter
            vmDesc.addNetwork(i.getMac(), i.getIp(), virtualMachine.getHypervisor().getMachine()
                .getVirtualSwitch(), i.getNetworkName(), i.getVlanNetwork().getTag(), i.getName(),
                null, null, null, null, null, null, null,
                Integer.valueOf(i.getRasd().getConfigurationName()));
        }
    }

    public void bootstrapConfiguration(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder vmDesc, final VirtualDatacenter virtualDatacenter,
        final VirtualAppliance virtualAppliance)
    {
        // PREMIUM
        logger.debug("bootstrap community implementation");
    }
}
