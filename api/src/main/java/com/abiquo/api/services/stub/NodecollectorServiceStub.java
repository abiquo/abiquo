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
 * Abiquo premium edition
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

package com.abiquo.api.services.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.nodecollector.client.NodeCollectorRESTClient;
import com.abiquo.nodecollector.exception.BadRequestException;
import com.abiquo.nodecollector.exception.CannotExecuteException;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.ServiceUnavailableException;
import com.abiquo.nodecollector.exception.UnprovisionedException;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualDiskEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.util.network.IPAddress;

/**
 * Wraps the calls nodecollector using {@link NodeCollectorPremiumRESTClient} providing an easy way
 * to mock it for test pruposes. Also transform NodeCollector's exceptions to any Abiquo API common
 * exception.
 * <p>
 * We pass the {@link RemoteService} object in every method. That means the services that call this
 * service need to get the {@link RemoteService} before to call it. That decouples this logic from
 * the persistent framework and also brings a more easy way to test it.
 * 
 * @author jdevesa@abiquo.com
 */
@Service
public class NodecollectorServiceStub extends DefaultApiService
{
    private final Logger logger = LoggerFactory.getLogger(NodecollectorServiceStub.class);

    @Autowired
    InfrastructureRep infrastructureRep;

    /**
     * Return kind of hypervisor API the remote machine through the
     * {@link NodeCollectorPremiumRESTClient} client.
     * 
     * @param nodecollector object nodecollector used in the datacenter.
     * @param hypervisorIp ip address of the hypervisor.
     * @return hypType kind of hypervisor API of remote machine.
     */
    public HypervisorType getRemoteHypervisorType(final RemoteService nodecollector,
        final String hypervisorIp)
    {
        NodeCollectorRESTClient restCli = initializeRESTClient(nodecollector);

        try
        {
            return restCli.getRemoteHypervisorType(hypervisorIp);
        }
        catch (BadRequestException e)
        {
            // InternalServerError -> A Bad Request NEVER should be thrown from here.
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (ConnectionException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_CONNECTION_EXCEPTION);
        }
        catch (UnprovisionedException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_NOT_FOUND_EXCEPTION);
        }
        catch (CollectorException e)
        {
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (ServiceUnavailableException e)
        {
            logger.error(e.getMessage());
            addServiceUnavailableErrors(APIError.NC_UNAVAILABLE_EXCEPTION);
        }
        catch (CannotExecuteException e)
        {
            addConflictErrors(new CommonError(APIError.STATUS_CONFLICT.getCode(), e.getMessage()));
            flushErrors();
        }

        flushErrors();

        return null;

    }

    /**
     * Return the list of machines in the range of ips from the parameter {ipFrom} to parameter
     * {ipTo} that matches with the hypervisortype, user, and password. For any error in the
     * retrieve, the machine will be ignored. Uses the method
     * {@link #getRemoteHypervisor(RemoteService, IPAddress, HypervisorType, String, String, Integer)}
     * for all the links.
     * 
     * @param nodecollector {@link RemoteService} object
     * @param ipFrom first address to search.
     * @param ipTo last address to search.
     * @param hypType {@link HypervisorType} object.
     * @param user user to login
     * @param password password to login
     * @param port aim port in case we want to retrieve KVM or XEN.
     * @return list of Machines.
     */
    public List<Machine> getRemoteHypervisors(final RemoteService nodecollector,
        final IPAddress ipFrom, final IPAddress ipTo, final HypervisorType hypType,
        final String user, final String password, final Integer port)
    {
        if (ipFrom.isBiggerThan(ipTo))
        {
            // TODO: Test here
            addConflictErrors(APIError.NETWORK_IP_FROM_BIGGER_THAN_IP_TO);
            flushErrors();
        }

        List<Machine> listOfMachines = new ArrayList<Machine>();
        IPAddress currentIp = ipFrom;
        while (!currentIp.isBiggerThan(ipTo))
        {
            try
            {

                Machine machine =
                    this.getRemoteHypervisor(nodecollector, currentIp, hypType, user, password,
                        port);
                listOfMachines.add(machine);
            }
            catch (ConflictException e)
            {
                // any conflict exception we found, we ignore it. We return only
                // the machines that are 'exactly' with the user, password and hypType we have
                // informed.
            }
            currentIp = currentIp.nextIPAddress();
        }

        return listOfMachines;
    }

    /**
     * Return the remote machine using its hypervisor APIs through the
     * {@link NodeCollectorPremiumRESTClient} client.
     * 
     * @param nodecollector object nodecollector used in the datacenter we want to add the machine.
     * @param hypervisorIp ip address of the hypervisor.
     * @param hypType kind of hypervisor API we want to use.
     * @param user use to log-in
     * @param password password
     * @param port (optional) AIM port we connect to if we use KVM or XEN.
     * @return the remote {@link Machine} entity, null if we don't find anything.
     */
    public Machine getRemoteHypervisor(final RemoteService nodecollector,
        final IPAddress hypervisorIp, final HypervisorType hypType, final String user,
        final String password, final Integer port)
    {
        NodeCollectorRESTClient restCli = initializeRESTClient(nodecollector);
        HostDto host = new HostDto();
        try
        {
            host =
                restCli.getRemoteHostInfo(hypervisorIp.toString(), hypType, user, password, port);
        }
        catch (BadRequestException e)
        {
            // InternalServerError -> A Bad Request NEVER should be thrown from here.
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (LoginException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_BAD_CREDENTIALS_TO_MACHINE);
        }
        catch (ConnectionException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_CONNECTION_EXCEPTION);
        }
        catch (UnprovisionedException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_NOT_FOUND_EXCEPTION);
        }
        catch (CollectorException e)
        {
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (ServiceUnavailableException e)
        {
            logger.error(e.getMessage());
            addServiceUnavailableErrors(APIError.NC_UNAVAILABLE_EXCEPTION);
        }
        catch (CannotExecuteException e)
        {
            addConflictErrors(new CommonError(APIError.STATUS_CONFLICT.getCode(), e.getMessage()));
            flushErrors();
        }

        flushErrors();

        Machine machine = hostToMachine(nodecollector.getDatacenter(), host);
        Hypervisor hypervisor =
            machine.createHypervisor(hypType, hypervisorIp.toString(), hypervisorIp.toString(),
                port, user, password);
        machine.setHypervisor(hypervisor);

        return machine;

    }

    /**
     * Return kind of hypervisor API the remote machine through the
     * {@link NodeCollectorPremiumRESTClient} client.
     * 
     * @param nodecollector object nodecollector used in the datacenter.
     * @param hypervisorIp ip address of the hypervisor.
     * @return hypType kind of hypervisor API of remote machine.
     * @throws LoginException
     */
    public List<VirtualMachine> getRemoteVirtualMachines(final RemoteService nodecollector,
        final String hypervisorIp, final HypervisorType hypervisorType, final String user,
        final String password, final Integer aimport)
    {
        NodeCollectorRESTClient restCli = initializeRESTClient(nodecollector);

        try
        {
            VirtualSystemCollectionDto vsc =
                restCli.getRemoteVirtualSystemCollection(hypervisorIp, hypervisorType, user,
                    password, aimport);

            List<VirtualMachine> vms = transportVSCollectionToVMs(vsc);

            return vms;
        }
        catch (BadRequestException e)
        {
            // InternalServerError -> A Bad Request NEVER should be thrown from here.
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (LoginException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_BAD_CREDENTIALS_TO_MACHINE);
        }
        catch (ConnectionException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_CONNECTION_EXCEPTION);
        }
        catch (UnprovisionedException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_NOT_FOUND_EXCEPTION);
        }
        catch (CollectorException e)
        {
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (CannotExecuteException e)
        {
            addConflictErrors(new CommonError(APIError.STATUS_CONFLICT.getCode(), e.getMessage()));
            flushErrors();
        }

        flushErrors();

        return null;

    }

    public Boolean isStonithUp(final RemoteService nodecollector, final String ip,
        final Integer port, final String username, final String password)
    {
        NodeCollectorRESTClient rest = initializeRESTClient(nodecollector);
        try
        {
            return rest.isStonithUp(ip, port, username, password);
        }
        catch (BadRequestException e)
        {
            // InternalServerError -> A Bad Request NEVER should be thrown from here.
            logger.error(e.getMessage());
            addUnexpectedErrors(APIError.NC_UNEXPECTED_EXCEPTION);
        }
        catch (ConnectionException e)
        {
            logger.debug(e.getMessage());
            addConflictErrors(APIError.NC_CONNECTION_EXCEPTION);
        }

        flushErrors();

        return null;
    }

    // private methods

    /**
     * Converts the type of {@link HostDto} from the nodecollector response to the {@link Machine}
     * type for the API model.
     * 
     * @param datacenter datacenter where the machine will be assigned.
     * @param host hostDto object.
     * @return a {@link Machine} entity.
     */
    protected Machine hostToMachine(final Datacenter datacenter, final HostDto host)
    {

        final Integer MEGABYTE = 1048576;

        int ram = (int) (host.getRam() / MEGABYTE);
        int cpus = (int) host.getCpu();

        Machine machine =
            new Machine(datacenter,
                host.getName(),
                "",
                ram,
                0,
                cpus,
                0,
                0,
                transfromToState(host.getStatus()),
                "");

        // Long totalStorage = 0L;
        String switches = "";
        for (ResourceType resource : host.getResources())
        {
            // TODO remove code
            if (resource.getResourceType().equals(ResourceEnumType.HARD_DISK))
            {
                Datastore datastore =
                    new Datastore(machine, resource.getElementName(), resource.getAddress(), "");
                datastore.setEnabled(Boolean.FALSE);
                datastore.setSize(resource.getUnits());
                datastore.setUsedSize(resource.getUnits() - resource.getAvailableUnits());
                if (resource.getConnection() == null)
                {
                    datastore.setDatastoreUUID(UUID.randomUUID().toString());
                }
                else
                {
                    datastore.setDatastoreUUID(resource.getConnection());
                }
            }
            else
            {
                if (resource.getResourceType().equals(ResourceEnumType.NETWORK_INTERFACE))
                {
                    switches = switches.concat(resource.getElementName()) + "/";
                    machine.getListOfMacs().add(resource.getAddress());
                }
            }

        }

        switches = switches.substring(0, switches.lastIndexOf('/'));
        machine.setVirtualSwitch(switches);
        return machine;
    }

    /**
     * Transform the NodeCollector's enum state {@link HostStatusEnumType} to Server enum state
     * {@link State}
     * 
     * @param status status of the nodecollector's retrieval.
     * @return
     */
    private MachineState transfromToState(final HostStatusEnumType status)
    {
        switch (status)
        {
            case MANAGED:
                return MachineState.MANAGED;
            case NOT_MANAGED:
                return MachineState.NOT_MANAGED;
            case PROVISIONED:
                return MachineState.PROVISIONED;
            default:
                return MachineState.STOPPED;
        }
    }

    protected NodeCollectorRESTClient initializeRESTClient(final RemoteService nodecollector)
    {
        return new NodeCollectorRESTClient(nodecollector.getUri());
    }

    /**
     * Returns a list of virtual machines builded from a virtual system list
     * 
     * @param vsc VirtualSystemCollectionDto list to transform
     * @return list of virtual machines builded from a virtual system list
     */
    private List<VirtualMachine> transportVSCollectionToVMs(final VirtualSystemCollectionDto vsc)
    {
        long MEGABYTE = 1024L * 1024L;

        List<VirtualMachine> vms = new ArrayList<VirtualMachine>();

        for (VirtualSystemDto vs : vsc.getVirtualSystems())
        {
            VirtualMachine vm =
                new VirtualMachine(vs.getName(), null, null, null, null, UUID.fromString(vs
                    .getUuid()), VirtualMachine.NOT_MANAGED);

            vm.setCpu(new Long(vs.getCpu()).intValue());
            vm.setRam(new Long(vs.getRam() / MEGABYTE).intValue());
            vm.setVdrpPort(new Long(vs.getVport()).intValue());
            vm.setState(VirtualMachineState.valueOf(vs.getStatus().value()));
            vm.setDisks(new ArrayList<DiskManagement>());
            for (ResourceType rt : vs.getResources())
            {
                if (rt.getLabel().equals("SYSTEM DISK"))
                {
                    long bytesHD = rt.getUnits();
                    vm.setHdInBytes(bytesHD);

                    if (StringUtils.hasText(rt.getAddress())
                        && StringUtils.hasText(rt.getConnection()))
                    {
                        Datastore ds = new Datastore();
                        ds.setDirectory(rt.getAddress());
                        ds.setRootPath(rt.getConnection());
                        ds.setName(rt.getElementName());
                        vm.setDatastore(ds);
                    }

                    VirtualImage vi = new VirtualImage(null);
                    VirtualDiskEnumType diskFormatType =
                        VirtualDiskEnumType.fromValue(rt.getResourceSubType().toString());
                    vi.setDiskFormatType(DiskFormatType.fromURI(diskFormatType.value()));
                    if (diskFormatType.equals(VirtualDiskEnumType.STATEFUL))
                    {
                        vi.setStateful(1);
                    }
                    vm.setVirtualImage(vi);
                    vm.setHdInBytes(rt.getUnits());
                }
                else
                {
                    DiskManagement disky =
                        new DiskManagement(null, null, null, rt.getUnits() * MEGABYTE, 0);
                    disky.setSizeInMb(rt.getUnits() * MEGABYTE);
                    vm.getDisks().add(disky);
                }
            }

            vms.add(vm);
        }

        return vms;
    }

}
