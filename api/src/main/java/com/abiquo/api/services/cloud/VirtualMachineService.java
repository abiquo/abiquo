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

package com.abiquo.api.services.cloud;

import static com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE;
import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoriesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplatesResource;
import com.abiquo.api.resources.cloud.DiskResource;
import com.abiquo.api.resources.cloud.DisksResource;
import com.abiquo.api.resources.cloud.IpAddressesResource;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.resources.cloud.PrivateNetworksResource;
import com.abiquo.api.resources.cloud.VirtualApplianceResource;
import com.abiquo.api.resources.cloud.VirtualAppliancesResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.resources.cloud.VirtualDatacentersResource;
import com.abiquo.api.resources.cloud.VirtualMachineNetworkConfigurationResource;
import com.abiquo.api.resources.cloud.VirtualMachineResource;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.VirtualMachineAllocatorService;
import com.abiquo.api.services.stub.AMServiceStub;
import com.abiquo.api.services.stub.TarantinoJobCreator;
import com.abiquo.api.services.stub.TarantinoService;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.api.util.URIResolver;
import com.abiquo.api.util.snapshot.SnapshotUtils.SnapshotType;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.model.enumerator.EthernetDriverType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.scheduler.SchedulerLock;
import com.abiquo.scheduler.VirtualMachineRequirementsFactory;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceRep;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachine.OrderByEnum;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDAO;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentDAO;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.util.network.IPNetworkRang;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VirtualMachineService extends DefaultApiService
{
    /** The logger object **/
    private final static Logger LOGGER = LoggerFactory.getLogger(VirtualMachineService.class);

    @Autowired
    protected VirtualMachineRep repo;

    @Autowired
    protected RasdManagementDAO rasdDao;

    @Autowired
    protected RasdDAO rasdRawRao;

    @Autowired
    protected VirtualApplianceRep vappRep;

    @Autowired
    protected VirtualDatacenterRep vdcRep;

    @Autowired
    private RemoteServiceService remoteServiceService;

    @Autowired
    private UserService userService;

    @Autowired
    protected EnterpriseRep enterpriseRep;

    @Autowired
    private StorageRep storageRep;

    @Autowired
    private VirtualMachineAllocatorService vmAllocatorService;

    @Autowired
    private VirtualMachineRequirementsFactory vmRequirements;

    @Autowired
    private VirtualDatacenterService vdcService;

    @Autowired
    private InfrastructureRep infRep;

    @Autowired
    protected AppsLibraryRep appsLibRep;

    @Autowired
    protected TarantinoService tarantino;

    @Autowired
    private TarantinoJobCreator jobCreator;

    @Autowired
    private NetworkService ipService;

    @Autowired
    private AMServiceStub amService;

    @Autowired
    private IpPoolManagementDAO ipPoolManDao;

    @Autowired
    private NetworkAssignmentDAO netAssignDao;

    @Autowired
    private VLANNetworkDAO vlanNetworkDao;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
        this.rasdDao = new RasdManagementDAO(em);
        this.vappRep = new VirtualApplianceRep(em);
        this.vdcRep = new VirtualDatacenterRep(em);
        this.remoteServiceService = new RemoteServiceService(em);
        this.userService = new UserService(em);
        this.enterpriseRep = new EnterpriseRep(em);
        this.vmAllocatorService = new VirtualMachineAllocatorService(em);
        this.vmRequirements = new VirtualMachineRequirementsFactory(); // XXX
        this.vdcService = new VirtualDatacenterService(em);
        this.infRep = new InfrastructureRep(em);
        this.appsLibRep = new AppsLibraryRep(em);
        this.tarantino = new TarantinoService(em);
        this.storageRep = new StorageRep(em);
        this.jobCreator = new TarantinoJobCreator(em);
        this.ipService = new NetworkService(em);
        this.ipPoolManDao = new IpPoolManagementDAO(em);
        this.vlanNetworkDao = new VLANNetworkDAO(em);
        this.tracer = new TracerLogger();
    }

    public Collection<VirtualMachine> findByHypervisor(final Hypervisor hypervisor)
    {
        return repo.findByHypervisor(hypervisor);
    }

    public Collection<VirtualMachine> findNotAllocated(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return repo.findVirtualMachinesNotAllocatedCompatibleHypervisor(hypervisor);
    }

    public Collection<VirtualMachine> findByEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;
        return repo.findByEnterprise(enterprise);
    }

    public List<VirtualMachine> findVirtualMachinesByUser(final Enterprise enterprise,
        final User user)
    {
        return repo.findVirtualMachinesByUser(enterprise, user);
    }

    public List<VirtualMachine> findByVirtualAppliance(final VirtualAppliance vapp,
        final Integer startwith, final String orderBy, final String filter, final Integer limit,
        final Boolean descOrAsc)
    {
        // Check if the orderBy element is actually one of the available ones
        VirtualMachine.OrderByEnum orderByEnum = VirtualMachine.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER
                .info("Bad parameter 'by' in request to get the virtual machines by virtual appliance.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }
        return repo.findVirtualMachinesByVirtualAppliance(vapp.getId(), startwith, limit, filter,
            orderByEnum, descOrAsc);
    }

    public VirtualMachine findByUUID(final String uuid)
    {
        return repo.findByUUID(uuid);
    }

    public VirtualMachine findByName(final String name)
    {
        return repo.findByName(name);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VirtualMachine getVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        VirtualDatacenter vdc = vdcRep.findById(vdcId);

        if (vm == null || !isAssignedTo(vmId, vapp.getId()))
        {
            LOGGER.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        LOGGER.debug("Virtual machine {} found", vmId);

        // if the ips are external, we need to set the limitID in order to return the
        // proper info.
        for (IpPoolManagement ip : vm.getIps())
        {
            if (ip.getVlanNetwork().getEnterprise() != null)
            {
                // needed for REST links.
                DatacenterLimits dl =
                    infRep.findDatacenterLimits(ip.getVlanNetwork().getEnterprise(),
                        vdc.getDatacenter());
                ip.getVlanNetwork().setLimitId(dl.getId());
            }
        }

        return vm;
    }

    /**
     * This method is semi-duplicated from VirtualApplianceService, but bean can not be used due
     * cross references
     */
    private VirtualAppliance getVirtualApplianceAndCheckVirtualDatacenter(final Integer vdcId,
        final Integer vappId)
    {
        // checks vdc exist
        vdcService.getVirtualDatacenter(vdcId);

        VirtualAppliance vapp = vappRep.findById(vappId);
        if (vapp == null || !vapp.getVirtualDatacenter().getId().equals(vdcId))
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        return vapp;
    }

    public VirtualMachine getVirtualMachine(final Integer vmId)
    {
        return repo.findVirtualMachineById(vmId);
    }

    public VirtualMachine getVirtualMachineByHypervisor(final Hypervisor hyp, final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineByHypervisor(hyp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        return vm;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void addImportedVirtualMachine(final VirtualMachine virtualMachine)
    {
        validate(virtualMachine);
        repo.insert(virtualMachine);
    }

/**
     * Gets the DTO object and validates all of its parameters. Prepares the {@link VirtualMachine} object
     * and sends the object to the method {@link VirtualMachineService#reconfigureVirtualMachine(VirtualDatacenter, VirtualAppliance, VirtualMachine, VirtualMachine).
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param vappId identifier of the {@link VirtualAppliance}
     * @param vmId identifier of the {@link VirtualMachine}
     * @param forceSoftLimits
     * @param dto input {@link VirtualMachineDto} object with all its links.
     * @return the link to the asnyncronous task.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineDto dto, final VirtualMachineState originalState,
        final Boolean forceSoftLimits)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        VirtualMachine newvm = buildVirtualMachineFromDto(vdc, virtualAppliance, dto);

        // the provided VM Dto doesn't have the conversion link (is itn't published already)
        // so the created object form Dto needs to set the same conversion
        newvm.setVirtualImageConversion(virtualMachine.getVirtualImageConversion());

        newvm.setTemporal(virtualMachine.getId()); // we set the id to temporal since we are trying

        // we need to get the configuration value ALWAYS after to set the ips of the new virtual
        // machine
        // since it depends to it to check if the configuration of the network is valid
        // And also we need to set AFTER to set the 'temporal' value from this DTO.
        newvm.setNetworkConfiguration(getNetworkConfigurationFromDto(virtualAppliance, newvm, dto));

        // to update the virtualMachine.

        // allocated resources not present in the requested reconfiguration
        newvm.setDatastore(virtualMachine.getDatastore());
        newvm.setHypervisor(virtualMachine.getHypervisor());

        return reconfigureVirtualMachine(vdc, virtualAppliance, virtualMachine, newvm,
            originalState, forceSoftLimits);
    }

    /**
     * updates the {@link NodeVirtualImage} name, y and x (those setted in the virtual appliance
     * builder. <br>
     * This method must persist the changes even if the reconfigure of the {@link VirtualMachine}
     * fails.
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param vappId identifier of the {@link VirtualAppliance}
     * @param vmId identifier of the {@link VirtualMachine}
     * @param dto input {@link VirtualMachineDto} object with all its links.
     * @return the link to the asnyncronous task.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNodeVirtualImageInfo(final Integer vdcId, final Integer vappId,
        final Integer vmId, final VirtualMachineWithNodeDto dto)
    {
        NodeVirtualImage nodeVirtualImage = getNodeVirtualImage(vdcId, vappId, vmId);

        nodeVirtualImage.setName(dto.getNodeName());
        nodeVirtualImage.setY(dto.getY());
        nodeVirtualImage.setX(dto.getX());
    }

    /**
     * <pre>
     * Prepare the machine to reconfigure. That means:
     * - Check the new allocation requirements
     * - Create the temporal register in database with the old values of the virtual machine for rollback purposes.
     * - Prepares and send the tarantino job.
     * - Returns the link to the asynchronous task to query in order to see the progress.
     * </pre>
     * 
     * This method assumes: - Any of the input params is null. - The 'isAssigned' checks are already
     * done: the virtual machine actually belongs to virtual appliance and the virtual appliance
     * actually belongs to virtual datacenter.
     * 
     * @param vdc {@link VirtualDatacenter} object where the virtual machine to reconfigure belongs
     *            to.
     * @param vapp {@link VirtualAppliance} object where the virtual machine to reconfigure belongs
     *            to.
     * @param newValues {@link VirtualMachine} exactly as we want to be after the reconfigure.
     * @param forceSoftLimits param if true does not trace the soft limit
     * @return a String containing the URI where to check the progress.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String reconfigureVirtualMachine(final VirtualDatacenter vdc,
        final VirtualAppliance vapp, final VirtualMachine vm, final VirtualMachine newValues,
        final VirtualMachineState originalState)
    {
        return reconfigureVirtualMachine(vdc, vapp, vm, newValues, originalState, false);
    }

    public String reconfigureVirtualMachine(final VirtualDatacenter vdc,
        final VirtualAppliance vapp, final VirtualMachine vm, final VirtualMachine newValues,
        final VirtualMachineState originalState, final Boolean forceSoftLimits)
    {

        if (checkReconfigureTemplate(vm.getVirtualMachineTemplate(),
            newValues.getVirtualMachineTemplate()))
        {
            if (vm.isCaptured())
            {
                // don't allow to change the template if the machine is capture
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
                flushErrors();
            }

            LOGGER.debug("Will reconfigure the vm template");

            if (originalState.existsInHypervisor())
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_IN_THE_HYPERVISOR);
                flushErrors();
            }

            // already checked is not attached
            if (newValues.getVirtualMachineTemplate().isStateful())
            {
                LOGGER.debug("Attaching virtual machine template volume");
                newValues.getVirtualMachineTemplate().getVolume().attach(0, vm);
                newValues.getVirtualMachineTemplate().getVolume().setVirtualAppliance(vapp);
                // primary disk sequence == 0
            }
        }

        LOGGER.debug("Starting the reconfigure of the virtual machine {}", vm.getId());

        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        newValues.setEnterprise(vm.getEnterprise());

        LOGGER.debug("Permission granted");

        VirtualMachine backUpVm = null;
        VirtualMachineDescriptionBuilder virtualMachineTarantino = null;

        // if NOT_ALLOCATED isn't necessary to check the resource limits and
        // insert the 'backup' resources
        try
        {
            if (originalState == VirtualMachineState.OFF)
            {
                // There might be different hardware needs. This call also recalculate.
                LOGGER
                    .debug("Updating the hardware needs in DB for virtual machine {}", vm.getId());
                VirtualMachineRequirements requirements =
                    vmRequirements.createVirtualMachineRequirements(vm, newValues);
                vmAllocatorService.checkAllocate(vapp.getId(), newValues, requirements,
                    forceSoftLimits);

                LOGGER
                    .debug("Creating the temporary register in Virtual Machine for rollback purposes");
                backUpVm = createBackUpMachine(vm);
                repo.insert(backUpVm);
                createBackUpResources(vm, backUpVm);
                insertBackUpResources(backUpVm);
                LOGGER.debug("Rollback register has id {}" + vm.getId());

                // Before to update the virtualmachine to new values, create the tarantino
                // descriptor
                // (only if the VM is deployed and OFF, othwerwise it won't have a datastore)
                virtualMachineTarantino = jobCreator.toTarantinoDto(vm, vapp);
            }

            // update the old virtual machine with the new virtual machine values.
            // and set the ID of the backupmachine (which has the old values) for recovery purposes.
            LOGGER.debug("Updating the virtual machine in the DB with id {}", vm.getId());
            updateVirtualMachineToNewValues(vapp, vm, newValues);
            LOGGER.debug("Updated virtual machine {}", vm.getId());

            // it is required a tarantino Task ?
            if (originalState == VirtualMachineState.NOT_ALLOCATED)
            {
                return null;
            }

            LOGGER.debug("Checking requires add initiatorMappings");
            initiatorMappings(vm);

            // refresh the virtualmachine object with the new values to get the
            // correct resources.
            VirtualMachineDescriptionBuilder newVirtualMachineTarantino =
                jobCreator.toTarantinoDto(newValues, vapp);

            // A datacenter task is a set of jobs and datacenter task. This is, the deploy of a
            // VirtualMachine is the definition of the VirtualMachine and the job, power on
            return tarantino.reconfigureVirtualMachine(vm, virtualMachineTarantino,
                newVirtualMachineTarantino);
        }
        catch (APIException e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, "virtualMachine.reconfigureError", vm.getName());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, e, "virtualMachine.reconfigureError", vm.getName());

            throw e;
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, "virtualMachine.reconfigureError", vm.getName());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_RECONFIGURE, ex, "virtualMachine.reconfigureError", vm.getName());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();

            return null;
        }
    }

    /**
     * Checks if the {@link VirtualMachineTemplate} is being changed, if so checks the new template
     * is an instance or a persistent of the original template (if not reports a conflict
     * {@link APIError}).
     * 
     * @return true if the {@link VirtualMachineTemplate} is being reconfigured.
     */
    protected boolean checkReconfigureTemplate(final VirtualMachineTemplate original,
        final VirtualMachineTemplate requested)
    {
        if (original.getId().equals(requested.getId()))
        {
            return false;
        }
        else if (!original.isManaged())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_NOT_MANAGED);
            flushErrors();
        }
        else if (!requested.isManaged())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_MANAGED);
            flushErrors();
        }
        else if (requested.isStateful() && requested.getVolume().isAttached())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_ATTACHED_PRESISTENT);
            flushErrors();
        }
        else if (original.isMaster() && !requested.isMaster()
            && !requested.getMaster().getId().equals(original.getId()))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_SAME_MASTER);
            flushErrors();
        }
        else if (!original.isMaster() && !requested.isMaster()
            && !requested.getMaster().getId().equals(original.getMaster().getId()))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_SAME_MASTER);
            flushErrors();
        }
        else if (requested.isMaster() && !original.isMaster()
            && !requested.getId().equals(original.getMaster().getId()))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_SAME_MASTER);
            flushErrors();
        }
        else if (original.isMaster() && requested.isMaster())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_SAME_MASTER);
            flushErrors();
        }

        return true;
    }

    /**
     * Insert the backup resources in database.
     * 
     * @param backUpVm
     */
    private void insertBackUpResources(final VirtualMachine backUpVm)
    {
        for (IpPoolManagement ip : backUpVm.getIps())
        {
            vdcRep.insertTemporalIpManagement(ip);
        }
        for (VolumeManagement vol : backUpVm.getVolumes())
        {
            storageRep.insertTemporalVolume(vol);
        }
        for (DiskManagement disk : backUpVm.getDisks())
        {
            storageRep.insertTemporalHardDisk(disk);
        }

        // XXX this a kind of magic !!!
        backUpVm.setIps(null);
        backUpVm.setVolumes(null);
        backUpVm.setDisks(null);

        repo.update(backUpVm);
    }

    /**
     * Just assign the new virtual machine values to the new ones.
     * 
     * @param old old virtual machine instance
     * @param vmnew new virtual machine values
     */
    private void updateVirtualMachineToNewValues(final VirtualAppliance vapp,
        final VirtualMachine old, final VirtualMachine vmnew)
    {
        // if client changes cpu or ram, can be changed in captured machines
        // only if hypervisor is ESXi
        if (old.getCpu() != vmnew.getCpu() || old.getRam() != vmnew.getRam())
        {
            if (old.isCaptured()
                && !vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
                flushErrors();

            }
        }

        // the rest of values never can be changed when reconfigure.
        if (differentDescription(old, vmnew) || differentNetworkConfiguration(old, vmnew)
            || differentPassword(old, vmnew))
        {
            if (old.isCaptured()
                && !vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
                flushErrors();
            }
            else if (old.isCaptured()
                && vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE_FULLY);
                flushErrors();
            }
        }

        old.setCpu(vmnew.getCpu());
        old.setDescription(vmnew.getDescription());
        old.setRam(vmnew.getRam());
        old.setNetworkConfiguration(vmnew.getNetworkConfiguration());
        old.setPassword(vmnew.getPassword());
        old.setVirtualMachineTemplate(vmnew.getVirtualMachineTemplate());

        List<Integer> usedNICslots = dellocateOldNICs(old, vmnew);
        // if the number of old nics still used is different from
        // the number of usedNICslots, OR the number of old nics is different
        // from the new ones, it means some NICs has changed.
        if (usedNICslots.size() != old.getIps().size()
            || old.getIps().size() != vmnew.getIps().size())
        {
            if (old.isCaptured()
                && !vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
                flushErrors();
            }
            else if (old.isCaptured()
                && vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE_FULLY);
                flushErrors();
            }
        }
        allocateNewNICs(vapp, old, vmnew.getIps(), usedNICslots);

        List<Integer> usedVolumeSlots = dellocateOldVolumes(old, vmnew);
        // if the number of old volumes still used is different from
        // the number of usedvolume Slots, OR the number of old volumes is different
        // from the new ones, it means some Volumes has changed.
        if (usedVolumeSlots.size() != old.getVolumes().size()
            || old.getVolumes().size() != vmnew.getVolumes().size())
        {
            if (old.isCaptured()
                && !vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
                flushErrors();
            }
            else if (old.isCaptured()
                && vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE_FULLY);
                flushErrors();
            }
        }

        // never use the slot 0 for storage since it is the virtual image
        List<Integer> usedStorageSlots = dellocateOldDisks(old, vmnew);

        // if the number of old hard disks still used is different from
        // the number of usedhard Slots, OR the number of old storage is different
        // from the new ones, it means some hard disk has changed.
        if (usedStorageSlots.size() != old.getDisks().size()
            || old.getDisks().size() != vmnew.getDisks().size())
        {
            if (old.isCaptured()
                && !vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE);
                flushErrors();
            }
            else if (old.isCaptured()
                && vapp.getVirtualDatacenter().getHypervisorType().equals(HypervisorType.VMX_04))
            {
                addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE_FULLY);
                flushErrors();
            }
        }

        usedStorageSlots.addAll(usedVolumeSlots);

        List<RasdManagement> storageResources = new ArrayList<RasdManagement>();
        storageResources.addAll(vmnew.getDisks());
        storageResources.addAll(vmnew.getVolumes());
        allocateNewStorages(vapp, old, storageResources, usedStorageSlots);

        repo.update(old);

        // FIXME: improvement related ABICLOUDPREMIUM-2925
        updateNodeVirtualImage(old, vmnew.getVirtualMachineTemplate());
    }

    private boolean differentNetworkConfiguration(final VirtualMachine old,
        final VirtualMachine vmnew)
    {
        if (old.getNetworkConfiguration() == null && vmnew.getNetworkConfiguration() == null)
        {
            return false;
        }
        else if (old.getNetworkConfiguration() == null && vmnew.getNetworkConfiguration() != null)
        {
            return true;
        }
        else if (old.getNetworkConfiguration() != null && vmnew.getNetworkConfiguration() == null)
        {
            return true;
        }
        else
        {
            return !old.getNetworkConfiguration().getId()
                .equals(vmnew.getNetworkConfiguration().getId());
        }
    }

    /**
     * Check if the password has changed.
     * 
     * @param old
     * @param vmnew
     * @return
     */
    private boolean differentPassword(final VirtualMachine old, final VirtualMachine vmnew)
    {
        if (vmnew.getPassword() != null && vmnew.getPassword().isEmpty())
        {
            vmnew.setPassword(null);
        }

        if (old.getPassword() == null && vmnew.getPassword() == null)
        {
            return false;
        }
        else if (old.getPassword() == null && vmnew.getPassword() != null)
        {
            return true;
        }
        else if (old.getPassword() != null && vmnew.getPassword() == null)
        {
            return true;
        }
        else
        {
            return !vmnew.getPassword().equals(old.getPassword());
        }
    }

    /**
     * Check if the description has changed.
     * 
     * @param old
     * @param vmnew
     * @return
     */
    private boolean differentDescription(final VirtualMachine old, final VirtualMachine vmnew)
    {
        if (old.getDescription() == null && vmnew.getDescription() == null)
        {
            return false;
        }
        else if (old.getDescription() == null && vmnew.getDescription() != null)
        {
            return true;
        }
        else if (old.getDescription() != null && vmnew.getDescription() == null)
        {
            return true;
        }
        else
        {
            return !old.getDescription().equals(vmnew.getDescription());
        }
    }

    /**
     * updates the virtual machine template from node virtual image with the template given by the
     * {@link VirtualMachineTemplate} param. <br>
     * This method must persist the changes even if the reconfigure of the {@link VirtualMachine}
     * fails.
     * 
     * @param vm {@link VirtualMachine} Virtual machine where obtains the related
     *            {@link NodeVirtualImage}
     * @parem template {@link VirtualMachineTemplate} Virtual Machine Template to set
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateNodeVirtualImage(final VirtualMachine vm,
        final VirtualMachineTemplate template)
    {
        NodeVirtualImage nvi = repo.findNodeVirtualImageByVm(vm);
        nvi.setVirtualImage(template);
        repo.updateNodeVirtualImage(nvi);
    }

    /**
     * Check if the resource is into the list of new resources.
     * 
     * @param resource {@link RasdManagement} resource to check
     * @param resources list of new Resources of the machine
     * @return true if the resource is into the new list.
     */
    private boolean resourceIntoNewList(final RasdManagement resource,
        final List< ? extends RasdManagement> newResources)
    {
        for (RasdManagement newResource : newResources)
        {
            // Since the values point to the same rasd, the id should be the same
            if (resource.getRasd().getId().equals(newResource.getRasd().getId()))
            {
                return true;
            }
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVirtualMachine(final VirtualMachine vm)
    {
        userService.checkCurrentEnterpriseForPostMethods(vm.getEnterprise());
        repo.update(vm);
    }

    /**
     * Check if a virtual machine is defined into a virtual appliance.
     * 
     * @param vmId identifier of the virtual machine
     * @param vappId identifier of the virtual appliance
     * @return True if it is, false otherwise.
     */
    public boolean isAssignedTo(final Integer vmId, final Integer vappId)
    {
        List<VirtualMachine> vms =
            repo.findVirtualMachinesByVirtualAppliance(vappId, 0, 0, "", OrderByEnum.NAME, true);
        for (VirtualMachine vm : vms)
        {
            if (vm.getId().equals(vmId))
            {
                return true;
            }
        }
        return false;
    }

    public VirtualMachineStateTransition validMachineStateChange(
        final VirtualMachine virtualMachine, final VirtualMachineState newState)
    {
        VirtualMachineStateTransition validTransition =
            VirtualMachineStateTransition.getValidVmStateChangeTransition(
                virtualMachine.getState(), newState);
        if (validTransition == null)
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_STATE_CHANGE_ERROR);
            flushErrors();
        }
        return validTransition;
    }

    /**
     * Compare the state of vm with the state passed through parameter
     * 
     * @param vm VirtualMachine to which compare the state
     * @param state a valid VirtualMachine state
     * @return true if its the same state, false otherwise
     */
    public Boolean sameState(final VirtualMachine vm, final VirtualMachineState state)
    {
        String actual = vm.getState().toOVF();// OVFGeneratorService.getActualState(vm);
        return state.toOVF().equalsIgnoreCase(actual);
    }

    /**
     * Delete a {@link VirtualMachine}. And the {@link VirtualMachineNode}.
     * 
     * @param virtualMachine to delete. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualMachine(final Integer vmId, final Integer vappId, final Integer vdcId,
        final VirtualMachineState originalState)
    {
        // We need to operate with concrete and this also check that the VirtualMachine belongs
        // to those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());

        if (VirtualMachineState.ALLOCATED.equals(originalState))
        {
            final String lockMsg = "Deallocate " + virtualMachine.getId();
            try
            {
                LOGGER
                    .warn("Delete of the ALLOCATED virtualMachine that has resources allocated. Deallocating");
                SchedulerLock.acquire(lockMsg);
                vmAllocatorService.deallocateVirtualMachine(virtualMachine);
            }
            finally
            {
                SchedulerLock.release(lockMsg);
                LOGGER
                    .warn("Delete of the ALLOCATED virtualMachine that has resources allocated. Deallocating successful");
            }
        }
        else if (originalState.existsInHypervisor())
        {
            LOGGER.debug("Delete of the virtualMachine that exists in hypervisor");
            undeployVirtualMachineAndDelete(virtualMachine, vappId, vdcId, originalState);
            return;
        }

        LOGGER.debug("Deleting the virtual machine with UUID {}", virtualMachine.getUuid());
        NodeVirtualImage nodeVirtualImage = findNodeVirtualImage(virtualMachine);
        LOGGER.trace("Deleting the node virtual image with id {}", nodeVirtualImage.getId());
        repo.deleteNodeVirtualImage(nodeVirtualImage);
        LOGGER.trace("Deleted node virtual image!");

        repo.deleteVirtualMachine(virtualMachine);

        // Does it has volumes? PREMIUM
        detachVolumesFromVirtualMachine(virtualMachine);
        LOGGER.debug("Detached the virtual machine's volumes with UUID {}",
            virtualMachine.getUuid());

        detachVirtualMachineIPs(virtualMachine);

        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DELETE,
            "virtualMachine.delete");
    }

    /**
     * Deletes the {@link Rasd} of an {@link IpPoolManagement}.
     * 
     * @param virtualMachine void
     */
    public void detachVirtualMachineIPs(final VirtualMachine virtualMachine)
    {
        for (IpPoolManagement ip : virtualMachine.getIps())
        {
            vdcRep.deleteRasd(ip.getRasd());
            switch (ip.getType())
            {
                case UNMANAGED:
                    rasdDao.remove(ip);
                    break;
                case EXTERNAL:
                    ip.setVirtualDatacenter(null);
                    ip.setMac(null);
                    ip.setName(null);
                default:
                    ip.detach();

            }
        }
    }

    /**
     * Delete a {@link VirtualMachine}. And the {@link VirtualMachineNode}. Without account for
     * permission.
     * 
     * @param virtualMachine to delete. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualMachineBySystem(final VirtualMachine virtualMachine)
    {

        LOGGER.debug("Deleting the virtual machine with UUID {}", virtualMachine.getUuid());
        NodeVirtualImage nodeVirtualImage = findNodeVirtualImage(virtualMachine);
        LOGGER.trace("Deleting the node virtual image with id {}", nodeVirtualImage.getId());
        repo.deleteNodeVirtualImage(nodeVirtualImage);
        LOGGER.trace("Deleted node virtual image!");

        repo.deleteVirtualMachine(virtualMachine);

        // Does it has volumes? PREMIUM
        detachVolumesFromVirtualMachine(virtualMachine);
        LOGGER.debug("Detached the virtual machine's volumes with UUID {}",
            virtualMachine.getUuid());

        detachVirtualMachineIPs(virtualMachine);

        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_DELETE,
            "virtualMachine.delete");
    }

    /**
     * This method is properly documented in the premium edition.
     * 
     * @param virtualMachine void
     */
    public void detachVolumesFromVirtualMachine(final VirtualMachine virtualMachine)
    {
        // PREMIUM
    }

    /**
     * Persists a {@link VirtualMachine}. If the preconditions are met.
     * 
     * @param virtualMachine to create. void
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine createVirtualMachine(final Integer vdcId, final Integer vappId,
        final VirtualMachineDto dto)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        // First we get from dto. All the values wi
        VirtualMachine virtualMachine = buildVirtualMachineFromDto(vdc, virtualAppliance, dto);
        virtualMachine.setUuid(UUID.randomUUID().toString());
        virtualMachine.setIdType(VirtualMachine.MANAGED);
        String nodeName = virtualMachine.getName();
        if (dto instanceof VirtualMachineWithNodeDto)
        {
            nodeName = ((VirtualMachineWithNodeDto) dto).getNodeName();// we use the name to create
            // the node
        }
        virtualMachine.setName("ABQ_" + virtualMachine.getUuid());

        // Set the user and enterprise
        virtualMachine.setUser(userService.getCurrentUser());
        virtualMachine.setEnterprise(userService.getCurrentUser().getEnterprise());

        // We check for a suitable conversion (PREMIUM)
        attachVirtualMachineTemplateConversion(virtualAppliance.getVirtualDatacenter(),
            virtualMachine);

        // Attach the matching stateful volume if the template is a saved persistent template
        if (virtualMachine.getVirtualMachineTemplate().isStateful())
        {
            LOGGER.debug("Attaching virtual machine template volume");
            virtualMachine.getVirtualMachineTemplate().getVolume()
                .attach(0, virtualMachine, virtualAppliance);
            virtualMachine.getVirtualMachineTemplate().getVolume()
                .setVirtualAppliance(virtualAppliance);
            virtualMachine.getVirtualMachineTemplate().getVolume()
                .setVirtualMachine(virtualMachine);
        }

        // At this stage the virtual machine is not associated with any hypervisor
        virtualMachine.setState(VirtualMachineState.NOT_ALLOCATED);

        // A user can only create virtual machine
        validate(virtualMachine);
        repo.createVirtualMachine(virtualMachine);

        // The entity that defines the relation between a virtual machine, virtual applicance and
        // virtual machine template is VirtualImageNode
        createNodeVirtualImage(virtualMachine, virtualAppliance, StringUtils.isBlank(nodeName)
            ? virtualMachine.getVirtualMachineTemplate().getName() : nodeName);

        // We must add the default NIC. This is the very next free IP in the virtual datacenter's
        // default VLAN
        ipService.assignDefaultNICToVirtualMachine(virtualMachine.getId());
        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_CREATE,
            "virtualMachine.create");
        return virtualMachine;
    }

    /**
     * @param vdcId
     * @param vappId
     * @param dto
     * @return
     */
    public VirtualMachine modifyVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {

        // XXX
        // TODO: Implement this modifier!
        return getVirtualMachine(vmId);
    }

    /**
     * Sets the virtual machine HD requirements based on the {@link VirtualMachineTemplate}
     * <p>
     * It also set the required CPU and RAM if it wasn't specified in the requested
     * {@link VirtualMachineDto}.
     * <p>
     * Specify the {@link EthernetDriverType} if the
     */
    protected void setVirtualMachineTemplateRequirementsIfNotAlreadyDefined(
        final VirtualMachine vmachine, final VirtualMachineTemplate vmtemplate)
    {
        if (vmtemplate.getEthernetDriverType() != null)
        {
            vmachine.setEthernetDriverType(vmtemplate.getEthernetDriverType());

            LOGGER.debug("VirtualMachine {} will use specific EthernetDriver {}",
                vmachine.getName(), vmtemplate.getEthernetDriverType().name());
        }

        if (vmachine.getCpu() == 0)
        {
            vmachine.setCpu(vmtemplate.getCpuRequired());
        }
        if (vmachine.getRam() == 0)
        {
            vmachine.setRam(vmtemplate.getRamRequired());
        }

        if (vmtemplate.isStateful())
        {
            vmachine.setHdInBytes(0);
        }
        else
        {
            vmachine.setHdInBytes(vmtemplate.getHdRequiredInBytes());
        }
    }

    /**
     * This code is semiduplicated from VirtualMachineTemplateService but can't be used due cross
     * refrerence dep
     */
    private VirtualMachineTemplate getVirtualMachineTemplateAndValidateEnterpriseAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualMachineTemplateId)
    {

        Datacenter datacenter = infRep.findById(datacenterId);
        Enterprise enterprise = enterpriseRep.findById(enterpriseId);

        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
        }
        if (enterprise == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_ENTERPRISE);
        }
        flushErrors();

        DatacenterLimits limits = infRep.findDatacenterLimits(enterprise, datacenter);
        if (limits == null)
        {
            addConflictErrors(APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
            flushErrors();
        }

        VirtualMachineTemplate virtualMachineTemplate =
            appsLibRep.findVirtualMachineTemplateById(virtualMachineTemplateId);
        if (virtualMachineTemplate == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE);
            flushErrors();
        }

        return virtualMachineTemplate;
    }

    /**
     * Creates the {@link NodeVirtualImage} that is the relation of {@link VirtualMachine}
     * {@link VirtualAppliance} and {@link VirtualMachineTemplate}.
     * 
     * @param virtualMachine virtual machine to be associated with the virtual appliance. It must
     *            contain the virtual machine template.
     * @param virtualAppliance void where the virtual machine exists.
     */
    protected void createNodeVirtualImage(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance, final String name)
    {
        LOGGER.debug("Create node virtual image with name virtual machine: {}",
            virtualMachine.getName());
        NodeVirtualImage nodeVirtualImage =
            new NodeVirtualImage(name,
                virtualAppliance,
                virtualMachine.getVirtualMachineTemplate(),
                virtualMachine);
        repo.insertNodeVirtualImage(nodeVirtualImage);
        LOGGER.debug("Node virtual image created!");
    }

    /**
     * Prepares the virtual image, in premium it sets the conversion. Attachs the conversion if
     * premium to the {@link VirtualMachine}.
     * 
     * @param virtualDatacenter from where we retrieve the hypervisor type.
     * @param virtualMachine virtual machine to persist.
     * @return VirtualImage in premium the conversion.
     */
    public void attachVirtualMachineTemplateConversion(final VirtualDatacenter virtualDatacenter,
        final VirtualMachine virtualMachine)
    {
        // COMMUNITY does nothing.
        LOGGER.debug("attachVirtualImageConversion community edition");
    }

    /**
     * Deploys a {@link VirtualMachine}. This involves some steps. <br>
     * <ol>
     * <li>Select a machine to allocate the virtual machine</li>
     * <li>Check limits</li>
     * <li>Check resources</li>
     * <li>Check remote services</li>
     * <li>In premium call initiator</li>
     * <li>Subscribe to VSM</li>
     * <li>Build the Task DTO</li>
     * <li>Build the Configure DTO</li>
     * <li>Build the Power On DTO</li>
     * <li>Enqueue in tarantino</li>
     * <li>Register in redis</li>
     * <li>Add Task DTO to rabbitmq</li>
     * <li>Enable the resource <code>Progress<code></li>
     * </ol>
     * 
     * @param vdcId VirtualDatacenter id
     * @param vappId VirtualAppliance id
     * @param vmId VirtualMachine id
     * @param forceEnterpriseSoftLimits Do we should take care of the soft limits?
     * @param restBuilder injected restbuilder context parameter
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String deployVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final Boolean foreceEnterpriseSoftLimits)
    {
        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        VirtualMachine vmachine = getVirtualMachine(vdcId, vappId, vmId);

        allocate(vmachine, vapp, foreceEnterpriseSoftLimits);

        return sendDeploy(vmachine, vapp);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void allocate(final VirtualMachine virtualMachine, final VirtualAppliance vapp,
        final Boolean foreceEnterpriseSoftLimits)
    {
        LOGGER.debug("Starting the deploy of the virtual machine {}", virtualMachine.getId());
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter

        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER.debug("Checking the virtual machine state. It must be in NOT_ALLOCATED");

        // The remote services must be up for this Datacenter if we are to deploy
        // LOGGER.debug("Check remote services");
        // FIXME checkRemoteServicesByVirtualDatacenter(vdcId);
        // LOGGER.debug("Remote services are ok!");

        // Tasks needs the definition of the virtual machine
        // VirtualAppliance virtualAppliance =
        // getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        try
        {
            LOGGER.debug("Allocating with force enterpise  soft limits : "
                + foreceEnterpriseSoftLimits);

            /*
             * Select a machine to allocate the virtual machine, Check limits, Check resources If
             * one of the above fail we cannot allocate the VirtualMachine
             */
            vmAllocatorService.allocateVirtualMachine(virtualMachine, vapp,
                foreceEnterpriseSoftLimits);
            LOGGER.debug("Allocated!");

            LOGGER.debug("Mapping the external volumes");
            // We need to map all attached volumes if any
            initiatorMappings(virtualMachine);
            LOGGER.debug("Mapping done!");
        }
        catch (APIException e)
        {
            traceApiExceptionVm(e, virtualMachine.getName());

            /*
             * Select a machine to allocate the virtual machine, Check limits, Check resources If
             * one of the above fail we cannot allocate the VirtualMachine It also perform the
             * resource recompute
             */
            if (virtualMachine.getHypervisor() != null)
            {
                vmAllocatorService.deallocateVirtualMachine(virtualMachine);
            }

            throw e;
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "virtualMachine.deploy", virtualMachine.getName());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, ex, "virtualMachine.deploy", virtualMachine.getName());

            if (virtualMachine.getHypervisor() != null)
            {
                vmAllocatorService.deallocateVirtualMachine(virtualMachine);
            }

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String sendDeploy(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance)
    {

        try
        {
            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            LOGGER.info("Generating the link to the status! {}", virtualMachine.getId());
            return tarantino.deployVirtualMachine(virtualMachine, vmDesc);
        }
        catch (APIException e)
        {
            traceApiExceptionVm(e, virtualMachine.getName());

            /*
             * Select a machine to allocate the virtual machine, Check limits, Check resources If
             * one of the above fail we cannot allocate the VirtualMachine It also perform the
             * resource recompute
             */
            if (virtualMachine.getHypervisor() != null)
            {
                vmAllocatorService.deallocateVirtualMachine(virtualMachine);
            }

            throw e;
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "virtualMachine.deploy", virtualMachine.getName());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, ex, "virtualMachine.deploy", virtualMachine.getName());

            vmAllocatorService.deallocateVirtualMachine(virtualMachine);

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();

            return null;
        }
    }

    private void traceApiExceptionVm(final APIException exception, final String vmName)
    {
        if (exception.getErrors().isEmpty())
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                exception.getMessage(), vmName);

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, exception, exception.getMessage(), vmName);
        }
        else
        {
            for (CommonError e : exception.getErrors())
            {
                String msg = e.getCode() + " " + e.getMessage();
                tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_DEPLOY, "virtualMachine.deploy.notEnoughResources", e.getCode(),
                    vmName);

                tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_DEPLOY, exception, msg, vmName);
            }

        }
    }

    /**
     * Checks the {@link RemoteService} of the {@link VirtualDatacenter} and logs if any error.
     * 
     * @param vdcId void
     */
    private void checkRemoteServicesByVirtualDatacenter(final Integer vdcId)
    {
        LOGGER.debug("Checking remote services for virtual datacenter {}", vdcId);
        VirtualDatacenter virtualDatacenter = vdcService.getVirtualDatacenter(vdcId);
        ErrorsDto rsErrors =
            checkRemoteServiceStatusByDatacenter(virtualDatacenter.getDatacenter().getId());
        if (!rsErrors.isEmpty())
        {
            LOGGER.error("Some errors found while cheking remote services");
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());
            // For the Admin to know all errors
            traceAdminErrors(rsErrors, SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, "remoteServices.down", true);

            // There is no point in continue
            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        LOGGER.debug("Remote services Ok!");
    }

    /**
     * Properly documented in Premium.
     * 
     * @param virtualMachine void
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void initiatorMappings(final VirtualMachine virtualMachine)
    {
        // PREMIUM
        LOGGER.debug("initiatorMappings community edition");
    }

    /**
     * /** Trace the Errors from a {@link ErrorsDto} to promote encapsulation.
     * 
     * @param rsErrors void
     * @param severityType severity.
     * @param componentType component.
     * @param eventType type.
     * @param msg message.
     * @param appendExceptionMsg should we append the exception message? the format would be
     *            <code>: error message</code> void
     */
    private void traceAdminErrors(final ErrorsDto rsErrors, final SeverityType severityType,
        final ComponentType componentType, final EventType eventType, final String msg,
        final boolean appendExceptionMsg)
    {
        for (ErrorDto e : rsErrors.getCollection())
        {
            tracer.systemLog(severityType, componentType, eventType, msg
                + (appendExceptionMsg ? ": " + e.getMessage() : ""));
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public String undeployVirtualMachineHA(final Integer vmId, final Integer vappId,
        final Integer vdcId, final Boolean forceUndeploy, final VirtualMachineState originalState,
        final Hypervisor originalHypervisor)
    {
        LOGGER.debug("Starting the undeploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        if (!originalState.existsInHypervisor())
        {
            return TaskResourceUtils.UNTRACEABLE_TASK;
        }
        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        LOGGER.debug("Remote services are ok!");

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        if (!forceUndeploy && virtualMachine.isCaptured())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_WILL_BE_DELETED);
            flushErrors();
        }

        try
        {
            LOGGER.debug("Check remote services");
            // The remote services must be up for this Datacenter if we are to deploy
            checkRemoteServicesByVirtualDatacenter(vdcId);
            LOGGER.debug("Remote services are ok!");

            // Tasks needs the definition of the virtual machine
            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance, true);

            String idAsyncTask =
                tarantino.undeployVirtualMachineHA(virtualMachine, vmDesc, originalState,
                    originalHypervisor);
            LOGGER.info("Undeploying of the virtual machine id {} in the virtual factory!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                "virtualMachine.enqueued", virtualMachine.getName());
            // For the Admin to know all errors
            tracer
                .systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                    "virtualMachine.enqueuedTarantino", virtualMachine.getName());

            return idAsyncTask;

        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "virtualMachine.undeployError", e.toString(),
                virtualMachine.getName(), e.getMessage());
            LOGGER
                .error(
                    "Error undeploying setting the virtual machine to UNKNOWN virtual machine name {}: {}",
                    virtualMachine.getUuid(), e.toString());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();
        }

        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public String undeployVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final Boolean forceUndeploy, final VirtualMachineState originalState)
    {
        LOGGER.debug("Starting the undeploy of the virtual machine {}", vmId);
        // We need to operate with concrete and this also check that the VirtualMachine belongs to
        // those VirtualAppliance and VirtualDatacenter
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);

        if (!originalState.existsInHypervisor())
        {
            return TaskResourceUtils.UNTRACEABLE_TASK;
        }
        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        LOGGER.debug("Remote services are ok!");

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        if (!forceUndeploy && virtualMachine.isCaptured())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_IMPORTED_WILL_BE_DELETED);
            flushErrors();
        }

        try
        {
            // Tasks needs the definition of the virtual machine
            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            String idAsyncTask =
                tarantino.undeployVirtualMachine(virtualMachine, vmDesc, originalState);
            LOGGER.info("Undeploying of the virtual machine id {} in the virtual factory!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                "virtualMachine.enqueued", virtualMachine.getName());
            // For the Admin to know all errors
            tracer
                .systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                    "virtualMachine.enqueuedTarantino", virtualMachine.getName());

            return idAsyncTask;

        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "virtualMachine.undeployError", e.toString(),
                virtualMachine.getName(), e.getMessage());
            LOGGER
                .error(
                    "Error undeploying setting the virtual machine to UNKNOWN virtual machine name {}: {}",
                    virtualMachine.getUuid(), e.toString());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();
        }

        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    private String undeployVirtualMachineAndDelete(final VirtualMachine virtualMachine,
        final Integer vappId, final Integer vdcId, final VirtualMachineState originalState)
    {
        LOGGER.debug("Check for permissions");
        // The user must have the proper permission
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        LOGGER.debug("Check remote services");
        // The remote services must be up for this Datacenter if we are to deploy
        checkRemoteServicesByVirtualDatacenter(vdcId);
        LOGGER.debug("Remote services are ok!");

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        try
        {

            // Tasks needs the definition of the virtual machine
            VirtualMachineDescriptionBuilder vmDesc =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            String idAsyncTask =
                tarantino.undeployVirtualMachineAndDelete(virtualMachine, vmDesc, originalState);
            LOGGER.info("Undeploying of the virtual machine id {} in the virtual factory!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                "virtualMachine.enqueued", virtualMachine.getName());
            // For the Admin to know all errors
            tracer
                .systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                    "virtualMachine.enqueuedTarantino", virtualMachine.getName());

            return idAsyncTask;

        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_UNDEPLOY,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            // For the Admin to know all errors
            tracer.systemLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_UNDEPLOY, "virtualMachine.undeployError", e.toString(),
                virtualMachine.getName(), e.getMessage());
            LOGGER
                .error(
                    "Error undeploying setting the virtual machine to UNKNOWN virtual machine name {}: {}",
                    virtualMachine.getUuid(), e.toString());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();
        }

        return null;
    }

    /**
     * Instance a {@link VirtualMachine}, handles all instance types {@link SnapshotType}.
     * 
     * @param vmId {@link VirtualMachine} Id
     * @param vappId {@link VirtualAppliance} Id
     * @param vdcId {@link VirtualDatacenter} Id
     * @return The {@link Task} UUID
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String instanceVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final String instanceName, final VirtualMachineState originalState)
    {
        // Retrieve entities
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        VirtualAppliance virtualApp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        LOGGER.debug("Starting the instance of the virtual machine {}", virtualMachine.getName());

        // Check if the operation is allowed and lock the virtual machine
        LOGGER.debug("Check for permissions");
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());
        LOGGER.debug("Permission granted");

        try
        {
            SnapshotType type = SnapshotType.getSnapshotType(virtualMachine);
            String taskId = null;

            LOGGER.debug("Instance type for virtual machine {} is {}", virtualMachine.getName(),
                type.name());

            switch (type)
            {
                case FROM_ORIGINAL_DISK:
                case FROM_DISK_CONVERSION:
                    taskId =
                        tarantino.snapshotVirtualMachine(virtualApp, virtualMachine, originalState,
                            instanceName);
                    break;

                case FROM_IMPORTED_VIRTUALMACHINE:
                    taskId =
                        instanceImportedVirtualMachine(virtualApp, virtualMachine, originalState,
                            instanceName);
                    break;

                case FROM_STATEFUL_DISK:
                    taskId =
                        tarantino.instanceStatefulVirtualMachine(virtualApp, virtualMachine,
                            originalState, instanceName);
                    break;
            }

            if (taskId != null)
            {
                LOGGER.debug("Instance of virtual machine {} enqueued!", virtualMachine.getName());
            }

            return taskId;
        }
        catch (APIException e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_INSTANCE,
                "virtualMachine.instanceFailed", virtualMachine.getName());

            tracer
                .systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_INSTANCE, e, "virtualMachine.instanceFailed",
                    virtualMachine.getName());

            throw e;
        }
        catch (Exception e)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_INSTANCE,
                "virtualMachine.instanceFailed", virtualMachine.getName());

            tracer
                .systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                    EventType.VM_INSTANCE, e, "virtualMachine.instanceFailed",
                    virtualMachine.getName());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();

            return null;
        }
    }

    /**
     * Performs an instance of type {@link SnapshotType#FROM_IMPORTED_VIRTUALMACHINE}
     * 
     * @param virtualAppliance {@link VirtualAppliance} where the {@link VirtualMachine} is
     *            contained.
     * @param virtualMachine The {@link VirtualMachine} to instance.
     * @param originalState The original {@link VirtualMachineState}.
     * @param instanceName The final name of the {@link VirtualMachineTemplate}
     * @return The {@link Task} UUID for progress tracking
     */
    private String instanceImportedVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String instanceName)
    {
        Datacenter datacenter = virtualMachine.getHypervisor().getMachine().getDatacenter();

        // Create the folder structure in the destination repository
        String ovfPath =
            amService.preBundleTemplate(datacenter.getId(), virtualAppliance.getEnterprise()
                .getId(), instanceName);

        // Do the instance
        String snapshotPath = FilenameUtils.getFullPath(ovfPath);
        String snapshotFilename =
            FilenameUtils.getName(virtualMachine.getVirtualMachineTemplate().getPath());

        return tarantino.snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            instanceName, snapshotPath, snapshotFilename);
    }

    /**
     * Changes the state of the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String applyVirtualMachineState(final Integer vmId, final Integer vappId,
        final Integer vdcId, final VirtualMachineStateTransition stateTransition)
    {
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());

        try
        {
            VirtualAppliance virtualAppliance =
                getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

            VirtualMachineDescriptionBuilder machineDescriptionBuilder =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            String location =
                tarantino.applyVirtualMachineState(virtualMachine, machineDescriptionBuilder,
                    stateTransition);
            LOGGER.info(
                "Applying the new state of the virtual machine id {} in the virtual factory!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                "virtualMachine.applyVirtualMachineEnqueued", virtualMachine.getName());
            // For the Admin to know all errors
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                "virtualMachine.applyVirtualMachineTarantinoEnqueued", virtualMachine.getName());

            // tasksService.
            // Here we add the url which contains the status
            return location;
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "virtualMachine.applyStateError", stateTransition.getEndState().name(),
                virtualMachine.getName());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, ex, "virtualMachine.applyStateError", stateTransition
                    .getEndState().name(), virtualMachine.getName());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();

            return null;
        }
    }

    /**
     * Reset the VirtualMachine to the state passed
     * 
     * @param vappId Virtual Appliance Id
     * @param vdcId VirtualDatacenter Id
     * @param state The state to which change
     * @throws Exception
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public String resetVirtualMachine(final Integer vmId, final Integer vappId,
        final Integer vdcId, final VirtualMachineStateTransition state)
    {
        VirtualMachine virtualMachine = getVirtualMachine(vdcId, vappId, vmId);
        userService.checkCurrentEnterpriseForPostMethods(virtualMachine.getEnterprise());

        VirtualAppliance virtualAppliance =
            getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        try
        {
            VirtualMachineDescriptionBuilder machineDescriptionBuilder =
                jobCreator.toTarantinoDto(virtualMachine, virtualAppliance);

            String location =
                tarantino
                    .applyVirtualMachineState(virtualMachine, machineDescriptionBuilder, state);
            LOGGER.info("Applying the reset of the virtual machine id {} in the virtual factory!",
                virtualMachine.getId());
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                "virtualMachine.resetVirtualMachineEnqueued", virtualMachine.getName());
            // For the Admin to know all errors
            tracer.systemLog(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, EventType.VM_STATE,
                "virtualMachine.resetVirtualMachineTarantinoEnqueued");

            // tasksService.
            // Here we add the url which contains the status
            return location;
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, EventType.VM_DEPLOY,
                "virtualMachine.resetVirtualMachineError", virtualMachine.getName());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE,
                EventType.VM_DEPLOY, ex, "virtualMachine.resetVirtualMachineError",
                virtualMachine.getName());

            addUnexpectedErrors(APIError.STATUS_INTERNAL_SERVER_ERROR);
            flushErrors();

            return null;
        }
    }

    /**
     * Checks one by one all {@link RemoteService} associated with the @{link Datacenter}.
     * 
     * @param datacenterId
     * @return ErrorsDto
     */
    public ErrorsDto checkRemoteServiceStatusByDatacenter(final Integer datacenterId)
    {

        List<RemoteService> remoteServicesByDatacenter =
            infRep.findRemoteServicesByDatacenter(infRep.findById(datacenterId));

        ErrorsDto errors = new ErrorsDto();
        for (RemoteService r : remoteServicesByDatacenter)
        {
            ErrorsDto checkRemoteServiceStatus =
                remoteServiceService.checkRemoteServiceStatus(r.getDatacenter(), r.getType(),
                    r.getUri());
            errors.addAll(checkRemoteServiceStatus);
        }

        return errors;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public NodeVirtualImage getNodeVirtualImage(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);
        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);
        VirtualDatacenter vdc = vdcRep.findById(vdcId);

        if (vm == null || !isAssignedTo(vmId, vapp.getId()))
        {
            LOGGER.error("Error retrieving the virtual machine: {} does not exist", vmId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        LOGGER.debug("virtual machine {} found", vmId);

        NodeVirtualImage nodeVirtualImage = findNodeVirtualImage(vm);
        if (nodeVirtualImage == null)
        {
            LOGGER.error("Error retrieving the node virtual image of machine: {} does not exist",
                vmId);
            addNotFoundErrors(APIError.NODE_VIRTUAL_MACHINE_IMAGE_NOT_EXISTS);
            flushErrors();
        }

        // if the ips are external, we need to set the limitID in order to return the
        // proper info.
        for (IpPoolManagement ip : vm.getIps())
        {
            if (ip.getVlanNetwork().getEnterprise() != null)
            {
                // needed for REST links.
                DatacenterLimits dl =
                    infRep.findDatacenterLimits(ip.getVlanNetwork().getEnterprise(),
                        vdc.getDatacenter());
                ip.getVlanNetwork().setLimitId(dl.getId());
            }
        }

        return nodeVirtualImage;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteNodeVirtualImage(final NodeVirtualImage nvi)
    {
        repo.deleteNodeVirtualImage(nvi);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public NodeVirtualImage findNodeVirtualImage(final VirtualMachine vm)
    {
        return repo.findNodeVirtualImageByVm(vm);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<NodeVirtualImage> getNodeVirtualImages(final Integer vdcId, final Integer vappId)
    {
        VirtualAppliance vapp = getVirtualApplianceAndCheckVirtualDatacenter(vdcId, vappId);

        if (vapp == null)
        {
            LOGGER.error("Error retrieving the virtual appliance: {} does not exist", vappId);
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        LOGGER.debug("virtual appliance {} found", vappId);

        return vapp.getNodes();
    }

    /**
     * Builds a {@link VirtualMachine} object from {@link VirtualMachineDto} object.
     * <p>
     * Allocated attributes (hypervisor/datastore) not set
     * 
     * @param dto transfer input object
     * @return output pojo object.
     */
    protected VirtualMachine buildVirtualMachineFromDto(final VirtualDatacenter vdc,
        final VirtualAppliance vapp, final VirtualMachineDto dto)
    {
        VirtualMachine vm = null;
        try
        {
            vm = ModelTransformer.persistenceFromTransport(VirtualMachine.class, dto);
        }
        catch (Exception e)
        {
            addUnexpectedErrors(APIError.STATUS_BAD_REQUEST);
            flushErrors();
        }

        // get the machine template and set the values of the virtual machine
        // according with the template and its overriden values.
        vm.setVirtualMachineTemplate(getVirtualMachineTemplateFromDto(dto));
        setVirtualMachineTemplateRequirementsIfNotAlreadyDefined(vm, vm.getVirtualMachineTemplate());

        // Get the resources from dto
        List<IpPoolManagement> ips = getNICsFromDto(vdc, dto);
        List<DiskManagement> disks = getHardDisksFromDto(vdc, dto);

        // Set the values for the virtualmachine
        vm.setIps(ips);
        vm.setDisks(disks);

        vm.setPassword(dto.getPassword());
        return vm;
    }

    /**
     * Allocate the POJOs of {@link DiskManagement} and {@link VolumeManagement} for the current
     * virtual datacenter, virtual appliance, virtual machine and the attachment order.
     * 
     * @param vapp {@link VirtualAppliance} object where the resource will be allocated.
     * @param vm {@link VirtualMachine} object where the resource will be allocated.
     * @param resources the list of resources that will be allocated.
     */
    protected void allocateNewStorages(final VirtualAppliance vapp, final VirtualMachine vm,
        final List< ? extends RasdManagement> resources, final List<Integer> blackList)
    {
        // When we allocate a resource, we need to set a unique attachment order for each one.
        // The function #getStorageFreeAttachmentSlot do the work. However, it only takes
        // the information from database, and we need to have a list of integers of the
        // already assigned slots before in the loop. 'blackList' stores them.

        for (RasdManagement resource : resources)
        {
            // Stateful volumes should not be trated as additional VM resources
            if (!isStatefulVolume(resource))
            {
                boolean allocated =
                    allocateResource(vm, vapp, resource, getFreeAttachmentSlot(blackList));
                if (allocated)
                {
                    // In Hyper-v only 2 attached volumes are allowed
                    if (vm.getHypervisor() != null
                        && vm.getHypervisor().getType() == HypervisorType.HYPERV_301
                        && blackList.size() >= 2)
                    {
                        addConflictErrors(APIError.VOLUME_TOO_MUCH_ATTACHMENTS);
                        flushErrors();
                    }

                    // if it is new allocated, we set the integer into the 'blacklisted' list.
                    Integer blacklisted = resource.getSequence();
                    blackList.add(blacklisted);

                    if (resource instanceof DiskManagement)
                    {
                        vdcRep.updateDisk((DiskManagement) resource);
                        tracer.log(SeverityType.INFO, ComponentType.STORAGE_DEVICE,
                            EventType.HARD_DISK_ASSIGN, "hardDisk.assigned", resource.getRasd()
                                .getLimit(), vm.getName());
                    }
                    else
                    {
                        storageRep.updateVolume((VolumeManagement) resource);
                        tracer.log(SeverityType.INFO, ComponentType.VOLUME,
                            EventType.VOLUME_ATTACH, "volume.attached", resource.getRasd()
                                .getElementName(), resource.getRasd().getLimit(), vm.getName());
                    }
                }
            }
        }
    }

    private boolean isStatefulVolume(final RasdManagement resource)
    {
        return resource instanceof VolumeManagement && ((VolumeManagement) resource).isStateful();
    }

    /**
     * Allocate the POJOs of {@link IpPoolManagement} for the current virtual datacenter, virtual
     * appliance, virtual machine and the attachment order.
     * 
     * @param vapp {@link VirtualAppliance} object where the resource will be allocated.
     * @param vm {@link VirtualMachine} object where the resource will be allocated.
     * @param resources the list of resources that will be allocated.
     */
    protected void allocateNewNICs(final VirtualAppliance vapp, final VirtualMachine vm,
        final List<IpPoolManagement> resources, final List<Integer> blackList)
    {
        // When we allocate a resource, we need to set a unique attachment order for each one.
        // The function #getStorageFreeAttachmentSlot do the work. However, it only takes
        // the information from database, and we need to have a list of integers of the
        // already assigned slots before in the loop. 'blackList' stores them.
        List<IpPoolManagement> ipPoolList = removeRepetedResources(resources);

        for (IpPoolManagement ip : resources)
        {
            boolean allocated = allocateResource(vm, vapp, ip, getFreeAttachmentSlot(blackList));
            if (allocated)
            {
                if (ip.getVlanNetwork().getType().equals(NetworkType.EXTERNAL)
                    || ip.getVlanNetwork().getType().equals(NetworkType.UNMANAGED))
                {
                    String mac =
                        IPNetworkRang.requestRandomMacAddress(vapp.getVirtualDatacenter()
                            .getHypervisorType());
                    String name = mac.replace(":", "") + "_host";
                    ip.setMac(mac);
                    ip.setName(name);
                    ip.setVirtualDatacenter(vapp.getVirtualDatacenter());
                }

                Rasd rasd = NetworkService.createRasdEntity(vm, ip);
                vdcRep.insertRasd(rasd);

                ip.setRasd(rasd);
                vdcRep.updateIpManagement(ip);

                // if it is new allocated, we set the integer into the 'blacklisted' list.
                blackList.add(ip.getSequence());
                tracer.log(SeverityType.INFO, ComponentType.NETWORK,
                    EventType.NIC_ASSIGNED_VIRTUAL_MACHINE, "nic.attached", vm.getName(),
                    ip.getIp(), ip.getVlanNetwork().getName());
            }
        }
    }

    private List<IpPoolManagement> removeRepetedResources(final List<IpPoolManagement> resources)
    {
        Map<String, IpPoolManagement> ipMap = new HashMap<String, IpPoolManagement>();

        int numberOfNewUnmanagedNICs = 0;
        for (IpPoolManagement ip : resources)
        {
            String mac = ip.getMac();
            if (mac.equalsIgnoreCase("?"))
            {
                mac = mac + numberOfNewUnmanagedNICs;
                numberOfNewUnmanagedNICs++;
            }
            ipMap.put(mac, ip);
        }

        if (resources.size() > ipMap.size())
        {
            String errorCode = APIError.RESOURCES_ALREADY_ASSIGNED.getCode();
            String message = APIError.RESOURCES_ALREADY_ASSIGNED.getMessage();
            CommonError error = new CommonError(errorCode, message);
            addNotFoundErrors(error);
            flushErrors();
        }

        return new ArrayList<IpPoolManagement>(ipMap.values());
    }

    /**
     * Dellocate the NICs of the {@link VirtualMachine} 'oldVm' parameter that are not anymore in
     * the new configuration 'newVm' parameter. Return the list of 'attachment orders' needed to
     * allocate
     * 
     * @param oldVm {@link VirtualMachine} with the 'old' configuration.
     * @param newVm {@link VirtualMachine} with the 'new' configuration.
     * @return the list of attachment order still in oldVm
     */
    protected List<Integer> dellocateOldNICs(final VirtualMachine oldVm, final VirtualMachine newVm)
    {
        List<Integer> oldNicsAttachments = new ArrayList<Integer>();

        // dellocate the old ips that are not in the new virtual machine.
        for (IpPoolManagement ip : oldVm.getIps())
        {
            if (!resourceIntoNewList(ip, newVm.getIps()))
            {
                // if the machine is NOT_ALLOCATED, the values here are definitive,
                // otherwise, it will be deleted in the handler
                if (oldVm.getState() == VirtualMachineState.NOT_ALLOCATED)
                {
                    if (ip.getVlanNetwork().getType().equals(NetworkType.UNMANAGED))
                    {
                        vdcRep.deleteRasd(ip.getRasd());
                        vdcRep.deleteIpPoolManagement(ip);
                    }
                    else if (ip.getVlanNetwork().getType().equals(NetworkType.EXTERNAL))
                    {
                        ip.setMac(null);
                        ip.setName(null);
                        ip.setVirtualDatacenter(null);
                        ip.detach();
                        vdcRep.deleteRasd(ip.getRasd());
                        vdcRep.updateIpManagement(ip);
                    }
                    else
                    {
                        ip.detach();
                        vdcRep.deleteRasd(ip.getRasd());
                        vdcRep.updateIpManagement(ip);
                    }
                }
                else
                {
                    ip.detach();
                    if (ip.getVlanNetwork().getType().equals(NetworkType.EXTERNAL))
                    {
                        ip.setMac(null);
                        ip.setName(null);
                        ip.setVirtualDatacenter(null);
                    }
                    vdcRep.updateIpManagement(ip);
                    tracer.log(SeverityType.INFO, ComponentType.NETWORK,
                        EventType.NIC_ASSIGNED_VIRTUAL_MACHINE, "nic.released", oldVm.getName(),
                        ip.getIp(), ip.getVlanNetwork().getName());
                }

                // if the dellocated ip is the one with the default configuration,
                // and it is the last one, set the default configuration to null
                if (ip.itHasTheDefaultConfiguration(oldVm)
                    && vdcRep.findIpsWithConfigurationIdInVirtualMachine(oldVm).size() == 0)
                {
                    oldVm.setNetworkConfiguration(null);
                }

            }
            else
            {
                oldNicsAttachments.add(ip.getSequence());
            }
        }
        return oldNicsAttachments;
    }

    /**
     * Dellocate the Disks of the {@link VirtualMachine} 'oldVm' parameter that are not anymore in
     * the new configuration 'newVm' parameter. Return the list of 'attachment orders' needed to
     * allocate after that.
     * 
     * @param oldVm {@link VirtualMachine} with the 'old' configuration.
     * @param newVm {@link VirtualMachine} with the 'new' configuration.
     * @return the list of attachment order still in oldVm
     */
    protected List<Integer> dellocateOldDisks(final VirtualMachine oldVm, final VirtualMachine newVm)
    {
        List<Integer> oldDisksAttachments = new ArrayList<Integer>();

        // dellocate the old disks that are not in the new virtual machine.
        for (DiskManagement disk : oldVm.getDisks())
        {
            if (!resourceIntoNewList(disk, newVm.getDisks()))
            {
                // if the machine is NOT_ALLOCATED, the values here are definitive,
                // otherwise, it will be deleted in the handler
                if (oldVm.getState() == VirtualMachineState.NOT_ALLOCATED)
                {
                    vdcRep.deleteRasd(disk.getRasd());
                    rasdDao.remove(disk);
                    tracer.log(SeverityType.INFO, ComponentType.STORAGE_DEVICE,
                        EventType.HARD_DISK_ASSIGN, "hardDisk.released", disk.getSizeInMb(),
                        oldVm.getName());
                }
                else
                {
                    disk.detach();
                    vdcRep.updateDisk(disk);
                }
            }
            else
            {
                oldDisksAttachments.add(disk.getSequence());
            }
        }
        return oldDisksAttachments;
    }

    /**
     * Dellocate the Volumes of the {@link VirtualMachine} 'oldVm' parameter that are not anymore in
     * the new configuration 'newVm' parameter. Return the list of 'attachment orders' needed to
     * allocate after that.
     * 
     * @param oldVm {@link VirtualMachine} with the 'old' configuration.
     * @param newVm {@link VirtualMachine} with the 'new' configuration.
     * @return the list of attachment order still in oldVm
     */
    protected List<Integer> dellocateOldVolumes(final VirtualMachine oldVm,
        final VirtualMachine newVm)
    {
        List<Integer> oldVolumesAttachments = new ArrayList<Integer>();

        // dellocate the old disks that are not in the new virtual machine.
        for (VolumeManagement vol : oldVm.getVolumes())
        {
            // Stateful volumes should not be trated as additional VM resources
            if (!vol.isStateful())
            {
                if (!resourceIntoNewList(vol, newVm.getVolumes()))
                {
                    if (!vol.isAttached())
                    {
                        addConflictErrors(APIError.VOLUME_NOT_ATTACHED);
                        flushErrors();
                    }
                    vol.detach();
                    storageRep.updateVolume(vol);
                    tracer.log(SeverityType.INFO, ComponentType.VOLUME, EventType.VOLUME_DETACH,
                        "volume.detached", vol.getName(), vol.getSizeInMB(), oldVm.getName());
                }
                else
                {
                    oldVolumesAttachments.add(vol.getSequence());
                }
            }
        }
        return oldVolumesAttachments;
    }

    /**
     * Get the next free attachment slot to be used to attach a disk, volume, or nic to the virtual
     * machine.
     * 
     * @param vm The virtual machine where the disk will be attached.
     * @return The free slot to use.
     */
    protected int getFreeAttachmentSlot(final List<Integer> blackList)
    {
        // Find the first free slot
        int i = RasdManagement.FIRST_ATTACHMENT_SEQUENCE;
        while (true)
        {
            if (!blackList.contains(i))
            {
                return i; // Found gap
            }

            i++;
        }
    }

    /**
     * Validates the given object with links to a hard disk and returns the referenced hard disk.
     * 
     * @param links The links to validate the hard disk.
     * @param expectedVirtualDatacenter The expected virtual datacenter to be found in the link.
     * @return The list of {@link DiskManagement} referenced by the link.
     * @throws Exception If the link is not valid.
     */
    public List<DiskManagement> getHardDisksFromDto(final VirtualDatacenter vdc,
        final SingleResourceTransportDto dto)
    {
        List<DiskManagement> disks = new LinkedList<DiskManagement>();

        // Validate and load each volume from the link list
        for (RESTLink link : dto.searchLinks(DiskResource.DISK))
        {
            String path =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM, DisksResource.DISKS_PATH,
                    DiskResource.DISK_PARAM);

            MultivaluedMap<String, String> pathValues =
                URIResolver.resolveFromURI(path, link.getHref());

            // URI needs to have an identifier to a VDC, and another one to the volume
            if (pathValues == null
                || !pathValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
                || !pathValues.containsKey(DiskResource.DISK))
            {
                throw new BadRequestException(APIError.HD_ATTACH_INVALID_LINK);
            }

            // Volume provided in link must belong to the same virtual datacenter
            Integer vdcId =
                Integer.parseInt(pathValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
            if (!vdcId.equals(vdc.getId()))
            {
                throw new BadRequestException(APIError.HD_ATTACH_INVALID_VDC_LINK);
            }

            Integer diskId = Integer.parseInt(pathValues.getFirst(DiskResource.DISK));

            DiskManagement disk = vdcRep.findHardDiskByVirtualDatacenter(vdc, diskId);
            if (disk == null)
            {
                String errorCode = APIError.HD_NON_EXISTENT_HARD_DISK.getCode();
                String message =
                    APIError.HD_NON_EXISTENT_HARD_DISK.getMessage() + ": Hard Disk id " + diskId;
                CommonError error = new CommonError(errorCode, message);
                addNotFoundErrors(error);
            }
            else
            {
                disks.add(disk);
            }
        }

        // Throw the exception with all the disks we have not found.
        flushErrors();

        return disks;
    }

    /**
     * Validates the given object with links to a NIC and returns the referenced list of
     * {@link IpPoolManagement}.
     * 
     * @param links The links to validate the hard disk.
     * @param expectedVirtualDatacenter The expected virtual datacenter to be found in the link.
     * @return The list of {@link IpPoolManagement} referenced by the link.
     * @throws Exception If the link is not valid.
     */
    public List<IpPoolManagement> getNICsFromDto(final VirtualDatacenter vdc,
        final SingleResourceTransportDto dto)
    {
        List<IpPoolManagement> ips = new LinkedList<IpPoolManagement>();

        // Validate and load each volume from the link list
        for (RESTLink link : dto.searchLinks(PrivateNetworkResource.PRIVATE_IP))
        {
            // Parse the URI with the expected parameters and extract the identifier values.
            String buildPath =
                buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                    VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                    PrivateNetworksResource.PRIVATE_NETWORKS_PATH,
                    PrivateNetworkResource.PRIVATE_NETWORK_PARAM, IpAddressesResource.IP_ADDRESSES,
                    IpAddressesResource.IP_ADDRESS_PARAM);
            MultivaluedMap<String, String> ipsValues =
                URIResolver.resolveFromURI(buildPath, link.getHref());

            // URI needs to have an identifier to a VDC, another one to a Private Network
            // and another one to Private IP
            if (ipsValues == null
                || !ipsValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
                || !ipsValues.containsKey(PrivateNetworkResource.PRIVATE_NETWORK)
                || !ipsValues.containsKey(IpAddressesResource.IP_ADDRESS))
            {
                throw new BadRequestException(APIError.VLANS_PRIVATE_IP_INVALID_LINK);
            }

            // Private IP must belong to the same Virtual Datacenter where the Virtual Machine
            // belongs to.
            Integer idVdc =
                Integer.parseInt(ipsValues.getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
            if (!idVdc.equals(vdc.getId()))
            {
                throw new BadRequestException(APIError.VLANS_IP_LINK_INVALID_VDC);
            }

            // Extract the vlanId and ipId values to execute the association.
            Integer vlanId =
                Integer.parseInt(ipsValues.getFirst(PrivateNetworkResource.PRIVATE_NETWORK));
            Integer ipId = Integer.parseInt(ipsValues.getFirst(IpAddressesResource.IP_ADDRESS));
            VLANNetwork vlan = vdcRep.findVlanByVirtualDatacenterId(vdc, vlanId);
            if (vlan == null)
            {
                String errorCode = APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK.getCode();
                String message =
                    APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK.getMessage() + ": Vlan id "
                        + vlanId;
                CommonError error = new CommonError(errorCode, message);
                addNotFoundErrors(error);
                continue;
            }
            IpPoolManagement ip = vdcRep.findIp(vlan, ipId);
            if (ip == null)
            {
                String errorCode = APIError.NON_EXISTENT_IP.getCode();
                String message =
                    APIError.NON_EXISTENT_IP.getMessage() + ": Vlan id " + vlan.getId();
                CommonError error = new CommonError(errorCode, message);
                addNotFoundErrors(error);
                continue;
            }

            ips.add(ip);
        }

        // Throw the exception with all the disks we have not found.
        flushErrors();

        return ips;
    }

    /**
     * Extracts the proper network configuration from the "network_configuration" link.
     * 
     * @param vapp VirtualAppliance We need it to check it the link os correct.
     * @param newvm VirtualMachine new. We need to check if the configuration is correct for the
     *            current virtualmachine's NICs.
     * @param configurationRef reference to configuration.
     * @return the {@link NetworkConfiguration} object to set to VirtualMachine.
     */
    public NetworkConfiguration getNetworkConfigurationFromDto(final VirtualAppliance vapp,
        final VirtualMachine newvm, final SingleResourceTransportDto dto)
    {
        RESTLink link =
            dto.searchLink(VirtualMachineNetworkConfigurationResource.DEFAULT_CONFIGURATION);
        if (link == null)
        {
            // we disable the default network configuration.
            return null;
        }
        String networkConfigurationTemplate =
            buildPath(VirtualDatacentersResource.VIRTUAL_DATACENTERS_PATH,
                VirtualDatacenterResource.VIRTUAL_DATACENTER_PARAM,
                VirtualAppliancesResource.VIRTUAL_APPLIANCES_PATH,
                VirtualApplianceResource.VIRTUAL_APPLIANCE_PARAM,
                VirtualMachinesResource.VIRTUAL_MACHINES_PATH,
                VirtualMachineResource.VIRTUAL_MACHINE_PARAM,
                VirtualMachineNetworkConfigurationResource.NETWORK,
                VirtualMachineNetworkConfigurationResource.CONFIGURATION_PATH,
                VirtualMachineNetworkConfigurationResource.CONFIGURATION_PARAM);

        MultivaluedMap<String, String> configurationValues =
            URIResolver.resolveFromURI(networkConfigurationTemplate, link.getHref());

        // URI needs to have an identifier to a VDC, another one to a Private Network
        // and another one to Private IP
        if (configurationValues == null
            || !configurationValues.containsKey(VirtualDatacenterResource.VIRTUAL_DATACENTER)
            || !configurationValues.containsKey(VirtualApplianceResource.VIRTUAL_APPLIANCE)
            || !configurationValues.containsKey(VirtualMachineResource.VIRTUAL_MACHINE)
            || !configurationValues
                .containsKey(VirtualMachineNetworkConfigurationResource.CONFIGURATION))
        {
            throw new BadRequestException(APIError.NETWORK_INVALID_CONFIGURATION_LINK);
        }

        // Get the identifiers of the link
        Integer vdcId =
            Integer.parseInt(configurationValues
                .getFirst(VirtualDatacenterResource.VIRTUAL_DATACENTER));
        Integer vappId =
            Integer.parseInt(configurationValues
                .getFirst(VirtualApplianceResource.VIRTUAL_APPLIANCE));
        Integer vmId =
            Integer.parseInt(configurationValues.getFirst(VirtualMachineResource.VIRTUAL_MACHINE));
        Integer configId =
            Integer.parseInt(configurationValues
                .getFirst(VirtualMachineNetworkConfigurationResource.CONFIGURATION));

        // Check the identifiers
        if (!vdcId.equals(vapp.getVirtualDatacenter().getId()))
        {
            throw new BadRequestException(APIError.NETWORK_LINK_INVALID_VDC);
        }
        if (!vappId.equals(vapp.getId()))
        {
            throw new BadRequestException(APIError.NETWORK_LINK_INVALID_VAPP);
        }
        if (!vmId.equals(newvm.getTemporal())) // it is the new resource, the id it is in the
        // 'temporal'
        {
            throw new BadRequestException(APIError.NETWORK_LINK_INVALID_VM);
        }

        List<IpPoolManagement> ips = newvm.getIps();
        for (IpPoolManagement ip : ips)
        {
            if (ip.getVlanNetwork().getConfiguration().getId().equals(configId))
            {
                return ip.getVlanNetwork().getConfiguration();
            }
        }

        // if we have reached this point, it means the configuratin id is not valid
        throw new BadRequestException(APIError.NETWORK_LINK_INVALID_CONFIG);
    }

    /**
     * Get the object {@link VirtualMachineTemplate} from the input dto.
     * 
     * @param dto the object that should have the link to a virtual machine template.
     * @return the found {@link virtualMachineTemplateObject}
     */
    public VirtualMachineTemplate getVirtualMachineTemplateFromDto(
        final SingleResourceTransportDto dto)
    {
        String vmTemplatePath =
            buildPath(
                EnterprisesResource.ENTERPRISES_PATH,
                EnterpriseResource.ENTERPRISE_PARAM, //
                DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM,
                VirtualMachineTemplatesResource.VIRTUAL_MACHINE_TEMPLATES_PATH,
                VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE_PARAM);

        RESTLink link = dto.searchLink(VIRTUAL_MACHINE_TEMPLATE);

        if (link == null)
        {
            addValidationErrors(APIError.LINKS_VIRTUAL_MACHINE_TEMPLATE_NOT_FOUND);
            flushErrors();
        }

        Integer entId = null;
        Integer dcId = null;
        Integer templId = null;
        try
        {
            MultivaluedMap<String, String> pathValues =
                URIResolver.resolveFromURI(vmTemplatePath, link.getHref());

            // URI needs to have an identifier to a ENTERPRISE, another to a DATACENTER_REPOSITORY
            // and another one to the TEMPLATE
            if (pathValues == null || !pathValues.containsKey(EnterpriseResource.ENTERPRISE)
                || !pathValues.containsKey(DatacenterRepositoryResource.DATACENTER_REPOSITORY)
                || !pathValues.containsKey(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE))
            {
                throw new BadRequestException(APIError.LINKS_VIRTUAL_MACHINE_TEMPLATE_INVALID_URI);
            }

            entId = Integer.valueOf(pathValues.getFirst(EnterpriseResource.ENTERPRISE));
            dcId =
                Integer.valueOf(pathValues
                    .getFirst(DatacenterRepositoryResource.DATACENTER_REPOSITORY));
            templId =
                Integer.valueOf(pathValues
                    .getFirst(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE));
        }
        catch (Exception e)
        {
            // unhandled exception parsing the uri
            addValidationErrors(APIError.LINKS_INVALID_LINK);
            flushErrors();
        }

        return getVirtualMachineTemplateAndValidateEnterpriseAndDatacenter(entId, dcId, templId);
    }

    /**
     * Gets a VirtualDatacenter. Raises an exception if it does not exist.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @return the found {@link VirtualDatacenter} instance.
     */
    protected VirtualDatacenter getVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = vdcRep.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        return vdc;
    }

    /**
     * Creates the backup object with a virtual machine.
     * 
     * @param vm original {@link VirtualMachine} obj.
     * @return the backup object.
     */
    public VirtualMachine duplicateVirtualMachineObject(final VirtualMachine vm)
    {
        VirtualMachine backUpVm = createBackUpMachine(vm);
        // set the 'real' name
        backUpVm.setName(vm.getName());
        createBackUpResources(vm, backUpVm);
        return backUpVm;
    }

    /**
     * Copy of the Virtual Machine object.
     * 
     * @param vm {@link VirtualMachine} object original
     * @return the copy of the input param.
     */
    protected VirtualMachine createBackUpMachine(final VirtualMachine vm)
    {
        VirtualMachine tmp = new VirtualMachine();

        // backup virtual machine properties
        tmp.setCpu(vm.getCpu());
        tmp.setDatastore(vm.getDatastore());
        tmp.setDescription(vm.getDescription());
        tmp.setEnterprise(vm.getEnterprise());
        tmp.setHdInBytes(vm.getHdInBytes());
        tmp.setHighDisponibility(vm.getHighDisponibility());
        tmp.setHypervisor(vm.getHypervisor());
        tmp.setIdType(vm.getIdType());
        tmp.setName("tmp_" + vm.getName());
        tmp.setPassword(vm.getPassword());
        tmp.setRam(vm.getRam());
        tmp.setState(VirtualMachineState.LOCKED);
        tmp.setSubState(vm.getSubState());
        tmp.setUser(vm.getUser());
        tmp.setUuid(vm.getUuid());
        tmp.setVdrpIP(vm.getVdrpIP());
        tmp.setVdrpPort(vm.getVdrpPort());
        tmp.setVirtualImageConversion(vm.getVirtualImageConversion());
        tmp.setVirtualMachineTemplate(vm.getVirtualMachineTemplate());
        tmp.setNetworkConfiguration(vm.getNetworkConfiguration());
        tmp.setTemporal(vm.getId());

        return tmp;
    }

    /**
     * Copy the resources of the Virtual Machine.
     * 
     * @param vm original virtualmachine
     * @param tmp backup virtualmachine where the resources will be copied.
     */
    protected void createBackUpResources(final VirtualMachine vm, final VirtualMachine tmp)
    {
        // Backup disks
        List<DiskManagement> disksTemp = new ArrayList<DiskManagement>();
        for (DiskManagement disk : vm.getDisks())
        {
            DiskManagement disktmp = new DiskManagement();
            disktmp.setSequence(disk.getSequence());
            disktmp.setDatastore(disk.getDatastore());
            disktmp.setTemporal(disk.getId());
            disktmp.setIdResourceType(disk.getIdResourceType());
            disktmp.setRasd(disk.getRasd());
            disktmp.setReadOnly(disk.getReadOnly());
            disktmp.setSizeInMb(disk.getSizeInMb());
            disktmp.setVirtualAppliance(disk.getVirtualAppliance());
            disktmp.setVirtualDatacenter(disk.getVirtualDatacenter());
            disktmp.setVirtualMachine(tmp);

            disksTemp.add(disktmp);
        }
        tmp.setDisks(disksTemp);

        // Backup NICs
        List<IpPoolManagement> ipsTemp = new ArrayList<IpPoolManagement>();
        for (IpPoolManagement ip : vm.getIps())
        {
            IpPoolManagement ipTmp = new IpPoolManagement();
            ipTmp.setSequence(ip.getSequence());
            ipTmp.setTemporal(ip.getId());
            ipTmp.setIdResourceType(ip.getIdResourceType());
            ipTmp.setRasd(ip.getRasd());
            ipTmp.setVirtualAppliance(ip.getVirtualAppliance());
            ipTmp.setVirtualDatacenter(ip.getVirtualDatacenter());
            ipTmp.setVirtualMachine(tmp);

            ipTmp.setName(ip.getName());
            ipTmp.setVlanNetwork(ip.getVlanNetwork());
            ipTmp.setMac(ip.getMac());
            ipTmp.setAvailable(ip.getAvailable());
            ipTmp.setNetworkName(ip.getNetworkName());
            ipTmp.setQuarantine(ip.getQuarantine());
            ipTmp.setIp(ip.getIp());

            ipsTemp.add(ipTmp);
        }
        tmp.setIps(ipsTemp);

        // Backup Volumes
        List<VolumeManagement> volsTemp = new ArrayList<VolumeManagement>();
        for (VolumeManagement vol : vm.getVolumes())
        {
            // Stateful volumes should not be trated as VM attached resources
            if (!vol.isStateful())
            {
                VolumeManagement volTmp = new VolumeManagement();
                volTmp.setSequence(vol.getSequence());
                volTmp.setTemporal(vol.getId());
                volTmp.setIdResourceType(vol.getIdResourceType());
                volTmp.setRasd(vol.getRasd());
                volTmp.setVirtualAppliance(vol.getVirtualAppliance());
                volTmp.setVirtualDatacenter(vol.getVirtualDatacenter());
                volTmp.setVirtualMachine(tmp);

                volTmp.setStoragePool(vol.getStoragePool());
                volTmp.setVirtualMachineTemplate(vol.getVirtualMachineTemplate());
                volTmp.setIdScsi(vol.getIdScsi());
                volTmp.setState(vol.getState());
                volTmp.setUsedSizeInMB(vol.getUsedSizeInMB());

                volsTemp.add(volTmp);
            }
        }
        tmp.setVolumes(volsTemp);
    }

    /**
     * Gets the {@link VirtualMachine} backup created to store reconfigure previous state.
     * 
     * @return the virtualmachine with ''temp'' == provided vm identifier
     */
    public VirtualMachine getBackupVirtualMachine(final VirtualMachine vmachine)
    {
        final VirtualMachine vmbackup = repo.findBackup(vmachine);

        if (vmbackup == null)
        {
            addNotFoundErrors(APIError.VIRTUAL_MACHINE_BACKUP_NOT_FOUND);
            flushErrors();
        }

        return vmbackup;
    }

    /**
     * Cleanup backup resources
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteBackupResources(final VirtualMachine backUpVm)
    {

        try
        {
            rasdDao.enableTemporalOnlyFilter();

            List<RasdManagement> rasds = backUpVm.getRasdManagements();

            // First of all, we have to release the VLAN tags if it is needed.
            // This is not a very optimal algorithm, since we traverse again the Rasds later,
            // but we need to know the id
            // of the virtual machine before to delete the entity to know if we can to release the
            // TAG.
            for (RasdManagement rollbackRasd : rasds)
            {
                if (rollbackRasd instanceof IpPoolManagement)
                {

                    IpPoolManagement originalRasd =
                        (IpPoolManagement) rasdDao.findById(rollbackRasd.getTemporal());

                    if (!originalRasd.isAttached())
                    {
                        // check if it was the last IP of the VLAN, and release the VLAN
                        // tag.
                        VLANNetwork vlanNetwork = originalRasd.getVlanNetwork();

                        final boolean assigned =
                            ipPoolManDao.isVlanAssignedToDifferentVM(backUpVm.getId(), vlanNetwork);

                        if (!assigned)
                        {
                            if (vlanNetwork.getType().equals(NetworkType.INTERNAL))
                            {
                                vlanNetwork.setTag(null);
                            }

                            NetworkAssignment na = netAssignDao.findByVlanNetwork(vlanNetwork);

                            if (na != null)
                            {
                                netAssignDao.remove(na);
                            }
                        }
                    }
                }
            }

            /*
             * CAUTION! We need this flush exactly here. Otherwise it tries to set teh
             * vlanNetwork.setTag(null) after to delete the related IpPoolManagement (that will be
             * deleted in the next loop) and it raises an UnknowEntityException.
             */
            vlanNetworkDao.flush();

            // we need to first delete the vm (as it updates the rasd_man)
            repo.deleteVirtualMachine(backUpVm);

            for (RasdManagement rollbackRasd : rasds)
            {
                if (rollbackRasd instanceof IpPoolManagement)
                {
                    IpPoolManagement originalRasd =
                        (IpPoolManagement) rasdDao.findById(rollbackRasd.getTemporal());

                    if (!originalRasd.isAttached())
                    {
                        // remove the rasd
                        vdcRep.deleteRasd(originalRasd.getRasd());

                        // unmanaged ips disappear when the are not assigned to a virtual machine.
                        if (originalRasd.isUnmanagedIp())
                        {
                            rasdDao.remove(originalRasd);
                        }

                        // external ips should remove its MAC and name
                        if (originalRasd.isExternalIp())
                        {
                            originalRasd.setMac(null);
                            originalRasd.setName(null);
                        }
                    }
                }
                // DiskManagements always are deleted
                if (rollbackRasd instanceof DiskManagement)
                {
                    DiskManagement originalRasd =
                        (DiskManagement) rasdDao.findById(rollbackRasd.getTemporal());
                    if (!originalRasd.isAttached())
                    {
                        vdcRep.deleteRasd(originalRasd.getRasd());
                        rasdDao.remove(originalRasd);
                    }
                }

                // refresh as the vm delete was updated the rasd
                rasdDao.remove(rasdDao.findById(rollbackRasd.getId()));
            }

            rasdDao.flush();
        }
        finally
        {
            rasdDao.restoreDefaultFilters();
        }

        // FIXME This is what we like
        // try
        // {
        // rasdDao.enableTemporalOnlyFilter();
        //
        // for (RasdManagement rasd : vm.getRasdManagements())
        // {
        // rasdDao.remove(rasd);
        // }
        //
        // repo.deleteVirtualMachine(repo.findVirtualMachineById(vm.getId()));
        //
        // rasdDao.flush();
        // }
        // finally
        // {
        // rasdDao.restoreDefaultFilters();
        // }
    }

    /**
     * Updates all the attributes and resource attachments of ''updatedVm'' from the backup
     * ''rollbackVm''.
     * 
     * @param updatedVm, current state of the virtual machine (not applied in the hypervisor)
     * @param rollbackVm, state of updaedVm previous to the reconfigure.
     * @return updatedVm with the attributes and resource attachments of rollbackVm.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachine restoreBackupVirtualMachine(final VirtualMachine updatedVm,
        final VirtualMachine rollbackVm)
    {

        // will use VsmServiceStub to force a refresh
        // updatedVm.setState(VirtualMachineState.LOCKED);

        // backup virtual machine properties
        updatedVm.setCpu(rollbackVm.getCpu());
        updatedVm.setDatastore(rollbackVm.getDatastore());
        updatedVm.setDescription(rollbackVm.getDescription());
        updatedVm.setEnterprise(rollbackVm.getEnterprise());
        updatedVm.setHdInBytes(rollbackVm.getHdInBytes());
        updatedVm.setHighDisponibility(rollbackVm.getHighDisponibility());
        updatedVm.setHypervisor(rollbackVm.getHypervisor());
        updatedVm.setIdType(rollbackVm.getIdType());
        updatedVm.setName(rollbackVm.getName().substring("tmp_".length()));
        updatedVm.setPassword(rollbackVm.getPassword());
        updatedVm.setRam(rollbackVm.getRam());
        updatedVm.setSubState(rollbackVm.getSubState());
        updatedVm.setUser(rollbackVm.getUser());
        updatedVm.setUuid(rollbackVm.getUuid());
        updatedVm.setVdrpIP(rollbackVm.getVdrpIP());
        updatedVm.setVdrpPort(rollbackVm.getVdrpPort());
        updatedVm.setVirtualImageConversion(rollbackVm.getVirtualImageConversion());
        updatedVm.setVirtualMachineTemplate(rollbackVm.getVirtualMachineTemplate());
        updatedVm.setNetworkConfiguration(rollbackVm.getNetworkConfiguration());

        List<RasdManagement> updatedResources = updatedVm.getRasdManagements();
        List<RasdManagement> rollbackResources = getBackupResources(rollbackVm);

        LOGGER.debug("removed backup virtual machine");

        for (RasdManagement updatedRasd : updatedResources)
        {
            RasdManagement rollbackRasd = getBackupResource(rollbackResources, updatedRasd.getId());

            if (rollbackRasd == null)
            {
                LOGGER.trace("restore: detach resource " + updatedRasd.getId());

                if (updatedRasd instanceof IpPoolManagement)
                {
                    IpPoolManagement originalRasd = (IpPoolManagement) updatedRasd;

                    // remove the rasd
                    vdcRep.deleteRasd(originalRasd.getRasd());

                    // unmanaged ips disappear when the are not assigned to a virtual machine.
                    if (originalRasd.isUnmanagedIp())
                    {
                        rasdDao.remove(originalRasd);
                    }
                }
                // DiskManagements always are deleted
                if (updatedRasd instanceof DiskManagement)
                {
                    DiskManagement originalRasd = (DiskManagement) updatedRasd;

                    vdcRep.deleteRasd(originalRasd.getRasd());
                    rasdDao.remove(originalRasd);
                }
                else
                {
                    // volumes only need to be dettached
                    if (!isStatefulVolume(updatedRasd))
                    {
                        updatedRasd.detach();
                    }
                }

            }
        }

        for (RasdManagement rollbackRasd : rollbackResources)
        {
            RasdManagement originalRasd = rasdDao.findById(rollbackRasd.getTemporal());

            if (!originalRasd.isAttached())
            {
                // Re attach the resource to the virtual machine
                LOGGER.trace("restore: attach resource " + originalRasd.getId());
                originalRasd.attach(originalRasd.getSequence(), updatedVm);
                // I dunno if it is necessary for the rest of resources,
                // but for IPs it is.
                if (originalRasd instanceof IpPoolManagement)
                {
                    IpPoolManagement ipman = (IpPoolManagement) originalRasd;
                    IpPoolManagement ipRoll = (IpPoolManagement) rollbackRasd;
                    VirtualAppliance vapp = vdcRep.findVirtualApplianceByVirtualMachine(updatedVm);
                    ipman.setVirtualAppliance(vapp);
                    ipman.setVirtualDatacenter(rollbackRasd.getVirtualDatacenter());
                    ipman.setIp(ipRoll.getIp());
                    ipman.setMac(ipRoll.getMac());
                }
            }

        }

        repo.deleteVirtualMachine(rollbackVm);
        repo.update(updatedVm);
        rasdDao.flush();
        // update virtual machine resources

        LOGGER.info("restored virtual machine {} from backup", updatedVm.getUuid());

        return updatedVm;
    }

    /**
     * Get the resources attached to the provided backup virtualmachine.
     */
    private List<RasdManagement> getBackupResources(final VirtualMachine rollbackVm)
    {
        try
        {
            rasdDao.enableTemporalOnlyFilter();
            return rollbackVm.getRasdManagements();
        }
        finally
        {
            rasdDao.restoreDefaultFilters();
        }
    }

    /**
     * Find the backup resources with temporal pointing to the provided resource identifier.
     * 
     * @return resource with temporal == provided resource id, null if not found
     */
    private RasdManagement getBackupResource(final List<RasdManagement> rollbackResources,
        final Integer tempRasdManId)
    {
        for (RasdManagement rasdman : rollbackResources)
        {
            if (tempRasdManId.equals(rasdman.getTemporal()))
            {
                return rasdman;
            }
        }
        return null;
    }

    /**
     * @param vmId to return
     * @return VirtualMachine with DC.
     */
    public VirtualMachine getVirtualMachineInitialized(final Integer vmId)
    {
        VirtualMachine virtualMachine = repo.findVirtualMachineById(vmId);

        if (virtualMachine == null)
        {
            return null;
        }

        if (virtualMachine.getHypervisor() != null)
        {
            Hibernate.initialize(virtualMachine.getHypervisor().getMachine().getDatacenter());
        }
        if (virtualMachine.getEnterprise() != null)
        {
            Hibernate.initialize(virtualMachine.getEnterprise());
        }
        if (virtualMachine.getDatastore() != null)
        {
            Hibernate.initialize(virtualMachine.getDatastore());
        }
        if (virtualMachine.getVirtualMachineTemplate() != null)
        {
            Hibernate.initialize(virtualMachine.getVirtualMachineTemplate());
        }

        return virtualMachine;
    }

    /**
     * Sets the {@link VirtualMachine#setState(VirtualMachineState)} to
     * {@link VirtualMachineState#UNKNOWN}.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setVirtualMachineToUnknown(final Integer vmId)
    {
        repo.setVirtualMachineToUnknown(vmId);
    }

    /**
     * Provides a standard method to allocate a resource and check if its already allocated.
     * 
     * @param vm {@link VirtualMachine} virtual machine where the resource will be allocated.
     * @param vapp {@link VirtualAppliance} virtual appiance where the resource will be allocated.
     * @param resource resource to allocate
     * @param attachOrder the number of allocation order for this resource.
     * @return true if the resource has been allocated, false if it was previously allocated.
     */
    protected boolean allocateResource(final VirtualMachine vm, final VirtualAppliance vapp,
        final RasdManagement resource, final Integer attachOrder)
    {

        if (resource.isAttached())
        {
            // FIXME BE AWARE OF IT:
            // the provided vm sometimes have ID (came form DDBB) and sometimes havent ID
            // (createBackup) but have the TemporalID. So it is not always called with the same type
            // of parameter.
            final Integer currentId =
                resource.getVirtualMachine().getId() != null ? resource.getVirtualMachine().getId()
                    : resource.getVirtualMachine().getTemporal();

            if (!currentId.equals(vm.getId()))
            {
                addConflictErrors(APIError.RESOURCE_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE);
                flushErrors();
            }

            return false;
        }

        if (resource.getVirtualMachine() != null
            && resource.getVirtualMachine().getTemporal() != null)
        {
            if (!resource.getVirtualMachine().getTemporal().equals(vm.getId()))
            {
                addConflictErrors(APIError.RESOURCE_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE);
                flushErrors();
            }

            // else do nothing, the resource is already asigned to this virtual machine.
            return false;
        }
        else
        {
            resource.attach(attachOrder, vm, vapp);
            return true;
        }
    }

    /**
     * This method writes without care for permissions.
     * 
     * @param vm void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVirtualMachineBySystem(final VirtualMachine vm)
    {
        repo.update(vm);
    }

    /**
     * This method writes without care for permissions.
     * 
     * @param vm void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertNodeVirtualImage(final NodeVirtualImage node)
    {
        repo.insertNodeVirtualImage(node);
    }

    /**
     * This method writes without care for permissions.
     * 
     * @param vm void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void insertVirtualMachine(final VirtualMachine virtualMachine)
    {
        repo.insert(virtualMachine);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<VirtualMachine> getManagedByHypervisor(final Hypervisor hypervisor)
    {
        return repo.findManagedByHypervisor(hypervisor);
    }
}
