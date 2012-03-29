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

package com.abiquo.scheduler.workload;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jms.ResourceAllocationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.model.enumerator.FitPolicy;
import com.abiquo.scheduler.fit.AllocationFitMax;
import com.abiquo.scheduler.fit.AllocationFitMin;
import com.abiquo.scheduler.fit.IAllocationFit;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDAO;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.network.NetworkAssignment;
import com.abiquo.server.core.infrastructure.network.NetworkAssignmentDAO;
import com.abiquo.server.core.scheduler.MachineLoadRule;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;

/**
 * Provides a way to find out which is the best target to which to assign a resource.
 * <p>
 * This component provides a simple algorith to choose a target:
 * <ul>
 * <li><em>First pass</em>: get a consistent list of candidate targets.
 * <p>
 * This list must contain only potentially valid candidates, and must leave none of the potential
 * valid candidates out. But it can provide candidates that might be valid but will be pruned by the
 * second pass.
 * <p>
 * Typically, this first pass is going to be implemented via JPA-QL queries, to minimize the number
 * of loaded objects.
 * <p>
 * Implemented via <em>findFirstPassCandidates</em>.
 * <li><em>Second pass</em>: selects just one of the candidates obtained during the firt pass, by
 * applying several rules.
 * <p>
 * Why this second pass? While the ideal would be to get this done in the first pass, in reality you
 * have scenarios in which you might want to do a first cut via SQL, and then perform very
 * complicated checks that can be hellish in SQL via Java code.
 * <p>
 * Implemented via <em>findSecondPassCandidates</em>.</li>
 * </ul>
 * <p>
 * We can have different policies to choose one among a list of candidates: these policies are
 * defined via FitPolicy.
 * 
 * @author pedro.agullo
 * @param RESOURCE: the resource to be assigned to a target (for example, a VirtualMachineTemplate)
 * @param TARGET: the place where we want to assign a resource (for example, a Physicalmachine)
 * @param CONTEXT_DATA: optative additional data that might be convenient or needed to perform
 *            processing.
 */
/**
 * Notes for the current (single) implementation {@link VirtualimageAllocationService}
 * <p>
 * This class has implemented rule checking using some rules in the second pass (MachineLoadRuleHB),
 * and others in the fist pass (EnterpriseExclusionRuleHB).
 * <p>
 * Why is this so? In practice, we find that some rules are almost self contained, and hence will
 * not force unexpected load of objects (i.e., many hidden queries executed under the covers): these
 * are good candidates for being implemented as Rule<...>, as this provides us with the full power
 * of Java to implement complex logic.
 * <p>
 * Other rules can involve many entities, and it will be a good idea to make them part of the first
 * pass, which cuts down the number of entities to deal with and can perform fetching of those
 * entities that will need to be traversed to check the rules -via the appropriate JPA-QL.
 * <p>
 * In the future, it might make sense to create rules that exhibit both behaviors, making them
 * candidates to implement Rule<...> as well as participate in the first pass via some query or just
 * some JPA-QL fragment to be added to other queries in the first pass.
 * <p>
 * WARNING: while this sounds good and nice, some serious analysis of the generated SQL should be
 * performed once we get the final implementation, to minimize the number of queries and maximize
 * their speed.
 * <p>
 * NOTE: the current implementation only fetch once the physical machine rules.
 */
@Service
public class VirtualimageAllocationService
{

    private final static Logger log = LoggerFactory.getLogger(VirtualimageAllocationService.class);

    @Autowired
    private InfrastructureRep datacenterRepo;

    @Autowired
    private VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    private NetworkAssignmentDAO networkAssignmentDao;

    @Autowired
    private SecondPassRuleFinder<VirtualMachineTemplate, Machine, Integer> ruleFinder;

    public VirtualimageAllocationService()
    {

    }

    public VirtualimageAllocationService(final EntityManager em)
    {
        this.datacenterRepo = new InfrastructureRep(em);
        this.virtualApplianceDao = new VirtualApplianceDAO(em);
        this.networkAssignmentDao = new NetworkAssignmentDAO(em);
        this.ruleFinder = new PhysicalmachineRuleFinder(em);
    }

    /** Starting best fit. (none ''computeRank'' can be higher) */
    private static final long NO_FIT = Long.MIN_VALUE;

    /**
     * Finds the targets that best fits a given resource. If there is no target that can accept the
     * resource, then null will be returned.
     * 
     * @throws ResourceAllocationException, it there isn't enough resources to fulfilling the
     *             target.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Machine findBestTarget(final VirtualMachineRequirements requirements,
        final FitPolicy fitPolicy, final VirtualAppliance virtualAppliance)
    {
        final List<Integer> rackCandidates = getCandidateRacks(virtualAppliance);

        if (rackCandidates.isEmpty())
        {
            final String msg = "Any rack can be selected: all exceed the max VLAN allowed.";
            throw new NotEnoughResourcesException(msg);
        }

        StringBuilder sbErrorRacks = new StringBuilder("Caused by:");

        for (Integer idRack : rackCandidates)
        {

            final Rack rack = datacenterRepo.findRackById(idRack);

            try
            {
                final Collection<Machine> firstPassCandidates =
                    findFirstPassCandidates(requirements, virtualAppliance, rack);

                log.debug(String.format(
                    "All the virtual machines of the current virtual datacenter "
                        + "will be deployed on the rack id : %d", idRack));

                return findSecondPassCandidates(firstPassCandidates, requirements,
                    virtualAppliance, fitPolicy);
            }
            catch (Exception e) // NotEnoughResourcesException or PersistenceException
            {
                final String error = String.format("Rack [%s] can't be used : %s", //
                    rack.getName(), e.getMessage());

                sbErrorRacks.append("\n").append(error);

                log.error(error);
            }

        }// racks

        final String msg =
            "Any rack can be selected: There is no physical machine capacity to instantiate the required virtual appliance."
                + sbErrorRacks.toString();

        throw new NotEnoughResourcesException(msg);
    }

    /**
     * Finds the targets that best fits a given resource. If there is no target that can accept the
     * resource, then null will be returned.
     * 
     * @param requirements, specify the hardware needs of the desired virtual machine
     * @param datastoreUuid, the selected machine should have this datastore enabled.
     * @param originalHypervisorId, the selected machine IS NOT this provided hypervisor.
     * @param rackId, the rack is already defined.
     * @throws ResourceAllocationException, it there isn't enough resources to fulfilling the
     *             target.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Machine findBestTarget(final VirtualMachineRequirements requirements,
        final FitPolicy fitPolicy, final VirtualAppliance vapp, final String datastoreUuid,
        final Integer originalHypervisorId, final Integer rackId)
        throws ResourceAllocationException
    {

        final Integer virtualDatacenterId = vapp.getVirtualDatacenter().getId();

        final Collection<Machine> firstPassCandidates =
            datacenterRepo.findCandidateMachines(rackId, virtualDatacenterId, vapp.getEnterprise(),
                datastoreUuid, originalHypervisorId);

        return findSecondPassCandidates(firstPassCandidates, requirements, vapp, fitPolicy);

    }

    /**
     * Return a sorted list of racks (sorted by rack goodness based on network params). If some
     * network assigment on the datacenter then the rack is already defined.
     */
    protected List<Integer> getCandidateRacks(final VirtualAppliance vapp)
    {

        final VirtualDatacenter virtualDatacenter = vapp.getVirtualDatacenter();

        // Gets the network assignment to the virtualmachine
        final List<NetworkAssignment> networksAssignedList =
            networkAssignmentDao.findByVirtualDatacenter(virtualDatacenter);

        if (networksAssignedList.isEmpty())
        {
            log.debug("First virtual machine of the current virtual appliance "
                + "(no rack assigned to the network attached). "
                + "Selecting the rack to be used on the hole virtual appliance.");

            // Gets the rack candidates that would fit a new virtual datacenter
            return datacenterRepo
                .getRackIdByMinVLANCount(virtualDatacenter.getDatacenter().getId());

        }
        else
        // the rack is already selected
        {
            // As all the networks of the same virtualdata center are assigned to the same rack, we
            // get the first rack - vlan assignment
            final NetworkAssignment na = networksAssignedList.get(0);
            final Integer idRack = na.getRack().getId();

            return Collections.singletonList(idRack);
        }
    }

    protected Collection<Machine> findFirstPassCandidates(
        final VirtualMachineRequirements requirements, final VirtualAppliance vapp, final Rack rack)
        throws NotEnoughResourcesException
    {
        Collection<Machine> candidateMachines;

        final Enterprise enterprise = vapp.getEnterprise();
        final VirtualDatacenter virtualDatacenter = vapp.getVirtualDatacenter();

        final Integer idRack = rack.getId();

        final Long numberOfDeployedVLAN =
            datacenterRepo.getNumberOfDeployedVlanNetworksByRack(idRack);
        final Integer vlanPerSwitch = rack.getVlanIdMax() - rack.getVlanIdMin() + 1;

        log.debug("The number of deployed VLAN for the rack: {}, is: {}", idRack,
            numberOfDeployedVLAN);

        final int second_operator = Math.round(vlanPerSwitch * rack.getNrsq() / 100);
        final int vlan_soft_limit = vlanPerSwitch - second_operator;

        if (numberOfDeployedVLAN.intValue() >= vlan_soft_limit)
        {
            String warning =
                "The number of deployed VLAN has exceeded the networking resource security quotient";
            log.warn(warning);
            TracerFactory.getTracer().log(SeverityType.WARNING, ComponentType.NETWORK,
                EventType.RACK_VLAN_POOL, warning);
        }
        if (numberOfDeployedVLAN.compareTo(new Long(vlanPerSwitch)) >= 0)
        {
            throw new NotEnoughResourcesException(String.format(
                "Not enough VLAN resource on rack [%s] to instantiate the required virtual appliance.",
                rack.getName()));
        }

        // log.debug("The network assigned to the VM, VLAN network ID: {},  "
        // + "has already been assigned to rack : {}.", na.getVlanNetwork().getId(), idRack);

        try
        {
            candidateMachines =
                datacenterRepo.findCandidateMachines(idRack, virtualDatacenter.getId(),
                    requirements.getHd(), enterprise);
        }
        catch (PersistenceException e)
        {
            throw new NotEnoughResourcesException(e.getMessage());
        }

        return candidateMachines;
    }

    /**
     * Default rule check the actual utilization (load factor = 100%) for CPU, RAM and HD.
     */
    class DefaultLoadRule extends MachineLoadRule
    {
        @Override
        public boolean pass(final VirtualMachineRequirements requirements, final Machine machine,
            final Integer contextData)
        {

            final boolean passCPU =
                pass(Long.valueOf(machine.getVirtualCpusUsed()), requirements.getCpu(),
                    Long.valueOf(machine.getVirtualCpuCores()), 100);

            final boolean passRAM =
                pass(Long.valueOf(machine.getVirtualRamUsedInMb()), requirements.getRam(),
                    Long.valueOf(machine.getVirtualRamInMb()), 100);

            // BYTE to MB
            Long templateRequiredMb = requirements.getHd() / (1024 * 1024);

            Long machineAllowedMb = 0L;
            Long machineUsedMb = 0L;
            if (machine.getDatastores() != null && !machine.getDatastores().isEmpty())
            {
                for (Datastore d : machine.getDatastores())
                {
                    machineAllowedMb += d.getSize() / (1024 * 1024);
                    machineUsedMb += d.getUsedSize() / (1024 * 1024);
                }
            }

            // machine.getVirtualHardDiskInBytes() / (1024 * 1024);
            // machine.getVirtualHardDiskUsedInBytes() / (1024 * 1024);

            final boolean passHD = pass(machineUsedMb, templateRequiredMb, machineAllowedMb, 100);

            return passCPU && passRAM && passHD;
        }
    }

    private final MachineLoadRule DEFAULT_RULE = new DefaultLoadRule();

    /**
     * TODO TBD
     * 
     * @param requirements, specify the hardware needs of the desired virtual machine
     * @throws ResourceAllocationException, it there isn't enough resources to fulfilling the
     *             target.
     */
    protected final Machine findSecondPassCandidates(final Collection<Machine> firstPassCandidates,
        final VirtualMachineRequirements requirements, final VirtualAppliance virtualAppliance,
        final FitPolicy fitPolicy) throws NotEnoughResourcesException
    {
        IAllocationFit physicalMachineFit;

        // get all the rules of the candiate machines
        Map<Machine, List<MachineLoadRule>> machineRulesMap =
            ruleFinder.initializeMachineLoadRuleCache(firstPassCandidates);

        physicalMachineFit =
            fitPolicy == FitPolicy.PROGRESSIVE ? new AllocationFitMax() : new AllocationFitMin();

        Machine bestTarget = null;
        long bestFitTarget = NO_FIT;

        for (final Machine target : firstPassCandidates)
        {
            boolean pass = true;

            if (machineRulesMap != null) // community impl --> rules == null (so always pass)
            {
                List<MachineLoadRule> rules = machineRulesMap.get(target);

                if (rules == null || rules.isEmpty())
                {
                    // XXX unused vappid
                    pass = DEFAULT_RULE.pass(requirements, target, virtualAppliance.getId());
                }
                else
                {
                    for (final MachineLoadRule rule : rules)
                    {
                        // XXX unused vappid
                        if (!rule.pass(requirements, target, virtualAppliance.getId()))
                        {
                            pass = false;
                            break;
                        }
                    }
                }
            }
            else
            // default rule is to check the actual resource utilization (load = 100%)
            {
                // XXX unused vappid
                pass = DEFAULT_RULE.pass(requirements, target, virtualAppliance.getId());
            }

            if (pass)
            {
                final long fitTarget = physicalMachineFit.computeRanking(target);

                if (isGoodEnough(fitTarget))
                {
                    return target;
                }

                if (fitTarget > bestFitTarget)
                {
                    bestFitTarget = fitTarget;
                    bestTarget = target;
                }
            }
            else
            {
                log.error(String.format("Machine %s rejected by some load rule.", target.getName()));
            }
        }

        if (bestTarget == null)
        {
            final String cause =
                String.format("There are %d candidate machines but all are discarded by the "
                    + "current workload rules (RAM and CPU oversubscription "
                    + "or suitable Datastore with enought free size).\n"
                    + "Please check the workload rules or the physical machine resources "
                    + "available on the datacenter from the infrastructure view.\n"
                    + "Virtual machine requires %d Cpu -- %d Ram \n" + "Candidate machines : %s",
                    firstPassCandidates.size(), requirements.getCpu(), requirements.getRam(),
                    candidateNames(firstPassCandidates));

            throw new NotEnoughResourcesException(cause);
        }

        return bestTarget;
    }

    private String candidateNames(final Collection<Machine> firstPassCandidates)
    {
        StringBuilder sb = new StringBuilder();
        for (Machine candidate : firstPassCandidates)
        {
            sb.append(candidate.getName());
            sb.append(" ip - ");
            sb.append(candidate.getHypervisor().getIp());
            sb.append("\n");
        }

        return sb.toString();
    }

    protected boolean isGoodEnough(final long fitTarget)
    {
        // TODO never is good enough
        return false;
    }

    /**
     * When editing a virtual machine this method checks if the increases resources (setted at
     * vmtemplate) are allowed by the workload rules.
     */
    public boolean checkVirtualMachineResourceIncrease(final Machine machine,
        final VirtualMachineRequirements increaseRequirements, final Integer virtualApplianceId)
    {
        // get all the rules of the candiate machines
        Map<Machine, List<MachineLoadRule>> machineRulesMap =
            ruleFinder.initializeMachineLoadRuleCache(Collections.singletonList(machine));

        boolean pass = true;

        if (machineRulesMap != null) // community impl --> rules == null (so always pass)
        {
            List<MachineLoadRule> rules = machineRulesMap.get(machine);

            if (rules == null || rules.isEmpty())
            {
                pass = DEFAULT_RULE.pass(increaseRequirements, machine, virtualApplianceId);
            }
            else
            {
                for (final MachineLoadRule rule : rules)
                {
                    if (!rule.pass(increaseRequirements, machine, virtualApplianceId))
                    {
                        pass = false;
                        break;
                    }
                }
            }
        }
        else
        // default rule is to check the actual resource utilization (load = 100%)
        {
            pass = DEFAULT_RULE.pass(increaseRequirements, machine, virtualApplianceId);
        }

        return pass;
    }

    /**
     * Return all machines in a rack that are empty of VM.
     * 
     * @param rackId rack.
     * @return Integer
     */
    public Integer getEmptyOffMachines(final Integer rackId)
    {

        return datacenterRepo.getEmptyOffMachines(rackId);
    }

    /**
     * Return all machines in a rack that are empty of VM.
     * 
     * @param rackId rack.
     * @return Integer
     */
    public Integer getEmptyOnMachines(final Integer rackId)
    {

        return datacenterRepo.getEmptyOnMachines(rackId);
    }

    /**
     * Return all machines in a rack that are empty of VM.
     * 
     * @param rackId rack.
     * @return Integer
     */
    public List<Machine> getAllEmptyOnMachines(final Integer rackId)
    {

        return datacenterRepo.getAllMachinesToShutDownFromRack(rackId);
    }

    /**
     * Returns any machine that is in the rack in HALTED_FOR_SAVE.
     * 
     * @param rackId rack.
     * @return Machine
     */

    public List<Machine> getRandomMachinesToStartFromRack(final Integer rackId,
        final Integer howMany)
    {
        return datacenterRepo.getRandomMachinesToStartFromRack(rackId, howMany);
    }

    /**
     * Returns any machine that is in the rack in MANAGED.
     * 
     * @param rackId rack.
     * @return Machine
     */
    public List<Machine> getRandomMachinesToShutDownFromRack(final Integer rackId,
        final Integer howMany)
    {
        return datacenterRepo.getRandomMachinesToShutDownFromRack(rackId, howMany);
    }
}
