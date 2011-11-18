package com.abiquo.api.services.stub;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.commons.amqp.impl.tarantino.TarantinoRequestProducer;
import com.abiquo.commons.amqp.impl.tarantino.domain.HypervisorConnection;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.commons.amqp.impl.tarantino.domain.dto.DatacenterTasks;
import com.abiquo.commons.amqp.impl.tarantino.domain.operations.ReconfigureVirtualMachineOp;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Utility methods to send jobs to Tarantino.
 * 
 * @author Ignasi Barrera
 */
@Service
public class TarantinoService extends DefaultApiService
{
    @Autowired
    private InfrastructureRep repo;

    @Autowired
    private VirtualMachineService vmService;

    @Autowired
    private VsmServiceStub vsm;

    public TarantinoService()
    {

    }

    public TarantinoService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        vmService = new VirtualMachineService(em);
        vsm = new VsmServiceStub();
    }

    /**
     * Creates and sends a reconfigure operation.
     * 
     * @param vm The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The identifier of the reconfigure task.
     */
    public String reconfigureVirtualMachine(final VirtualMachine vm,
        final VirtualMachineDescriptionBuilder originalConfig,
        final VirtualMachineDescriptionBuilder newConfig)
    {
        Datacenter datacenter = vm.getHypervisor().getMachine().getDatacenter();
        ignoreVSMEventsIfNecessary(datacenter, vm);

        DatacenterTasks reconfigureTask = reconfigureTask(vm, originalConfig, newConfig);
        send(datacenter, reconfigureTask, EventType.VM_RECONFIGURE);

        return reconfigureTask.getId();
    }

    /* ********************************** Helper methods ********************************** */

    /**
     * Creates a reconfigure task.
     * 
     * @param vm The virtual machine to reconfigure.
     * @param originalConfig The original configuration for the virtual machine.
     * @param newConfig The new configuration for the virtual machine.
     * @return The reconfigure task.
     */
    private DatacenterTasks reconfigureTask(final VirtualMachine vm,
        final VirtualMachineDescriptionBuilder originalConfig,
        final VirtualMachineDescriptionBuilder newConfig)
    {
        DatacenterTasks reconfigureTask = new DatacenterTasks();
        reconfigureTask.setId(vm.getUuid());
        reconfigureTask.setDependent(true);

        HypervisorConnection hypervisorConnection = hypervisorConnection(vm);

        ReconfigureVirtualMachineOp reconfigureJob = new ReconfigureVirtualMachineOp();
        reconfigureJob.setVirtualMachine(originalConfig.build(vm.getUuid()));
        reconfigureJob.setNewVirtualMachine(newConfig.build(vm.getUuid()));
        reconfigureJob.setHypervisorConnection(hypervisorConnection);
        reconfigureJob.setId(reconfigureTask.getId() + "." + vm.getUuid() + "reconfigure");

        reconfigureTask.getJobs().add(reconfigureJob);

        return reconfigureTask;
    }

    /**
     * Creates a new hypervisor connection configuration for the given virtual machine.
     * 
     * @param vm The virtual machine.
     * @return The hypervisor connection configuration.
     */
    private HypervisorConnection hypervisorConnection(final VirtualMachine vm)
    {
        return vmService.hypervisorConnectionConfiguration(vm);
    }

    /**
     * Send the given datacenter tasks.
     * 
     * @param datacenter The datacenter where the tasks will be sent to.
     * @param tasks The tasks to send.
     * @param event The event associated to the task (power on, reconfigure, etc).
     */
    private void send(final Datacenter datacenter, final DatacenterTasks tasks,
        final EventType event)
    {
        TarantinoRequestProducer producer = new TarantinoRequestProducer(datacenter.getName());

        try
        {
            producer.openChannel();
            producer.publish(tasks);
        }
        catch (Exception ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                "Failed to enqueue task in Tarantino. Rabbitmq might be "
                    + "down or not configured. The error message was " + ex.getMessage(), ex);

            addNotFoundErrors(APIError.GENERIC_OPERATION_ERROR);
            flushErrors();
        }
        finally
        {
            closeProducerChannel(producer, event);
        }

        tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE, event,
            "Task enqueued successfully to Tarantinio");
    }

    /**
     * Close the producer channel.
     * 
     * @param producer The channel to close.
     * @param event The event being processed (power on, reconfigure, etc).
     */
    private void closeProducerChannel(final TarantinoRequestProducer producer, final EventType event)
    {
        try
        {
            producer.closeChannel();
        }
        catch (IOException ex)
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                APIError.GENERIC_OPERATION_ERROR.getMessage());

            tracer.systemError(SeverityType.CRITICAL, ComponentType.VIRTUAL_MACHINE, event,
                "Error closing the producer channel with error: " + ex.getMessage(), ex);

        }
    }

    /**
     * Unsubscribe from the VSM if a reconfigure task is sent to a XEN or KVM.
     * <p>
     * Since reconfigure tasks in those hypervisors may undefine the domain and redefine it again,
     * the unsubscription is performed to ignore all DESTROY and CREATE events that may arrive
     * because of that process.
     * 
     * @param datacenter The datacenter where the tasks are performed.
     * @param vm The virtual machine to unsubscribe.
     */
    private void ignoreVSMEventsIfNecessary(final Datacenter datacenter, final VirtualMachine vm)
    {
        HypervisorType type = vm.getHypervisor().getType();

        if (type == HypervisorType.XEN_3 || type == HypervisorType.KVM)
        {
            List<RemoteService> services =
                repo.findRemoteServiceWithTypeInDatacenter(datacenter,
                    RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);

            if (services.isEmpty())
            {
                addNotFoundErrors(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
                flushErrors();
            }

            RemoteService vsmRS = services.get(0);
            // TODO: Serafin, uncomment this
            // vsm.unsubscribe(vsmRS, vm);
        }
    }
}
