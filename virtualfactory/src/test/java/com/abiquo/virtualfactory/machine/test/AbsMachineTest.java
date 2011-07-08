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

package com.abiquo.virtualfactory.machine.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;

public abstract class AbsMachineTest extends TestCase
{

    protected final static Logger log = LoggerFactory.getLogger(AbsMachineTest.class);

    protected IHypervisor hypervisor;

    AbsVirtualMachine vMachine;

    VirtualMachineConfiguration configurations;

    /*
     * HYPERVISOR configuration properties
     */
    protected String hvURL;

    protected String hvUser;

    protected String hvPassword;

    protected String targetDatastore;

    /*
     * MACHINE configuration properties
     */
    protected boolean deployVirtualMachine;

    protected UUID id;

    protected String name;

    protected int rdPort;

    protected long ramAllocationUnits;

    protected int cpuNumber;

    protected String macAddress;

    protected String macAddress2;

    protected String vswitchName;

    protected String vswitchName2;

    protected int vlanTag;

    protected int vlanTag2;

    protected String networkName;

    protected String networkName2;

    /*
     * DISK configuration properties
     */
    protected String diskRepository;

    protected String diskImagePath;

    protected String diskId;

    protected long diskCapacity;

    /*
     * iSCSI DISK configuration
     */

    protected String iscsiTestLocation;

    protected String iscsiUUID;
    
    /**
     * Sets test ready for deploy without copying disk (HA)
     */
    protected boolean isHA = false;

    public abstract IHypervisor instantiateHypervisor();

    protected VirtualMachineConfiguration createConfiguration()
    {
        /**
         * TODO shall create the VMConfiguration form an OVF document
         */

        String diskLocation = "[" + diskRepository + "]" + diskImagePath;

        VirtualDisk virtualDisk =
            new VirtualDisk(diskId,
                diskLocation,
                diskCapacity,
                targetDatastore,
                "",
                DiskFormat.UNKNOWN.getDiskFormatUri());
        List<VirtualDisk> disks = new LinkedList<VirtualDisk>();
        disks.add(virtualDisk);

        List<VirtualNIC> vnicList = new ArrayList<VirtualNIC>();
        // FIXME: Uncomment This!
//        vnicList.add(new VirtualNIC(vswitchName, macAddress, vlanTag, networkName, 0));
//        vnicList.add(new VirtualNIC(vswitchName2, macAddress2, vlanTag2, networkName2, 1));

        VirtualMachineConfiguration conf =
            new VirtualMachineConfiguration(id,
                name,
                disks,
                rdPort,
                null,
                ramAllocationUnits,
                cpuNumber,
                vnicList);

        conf.setHypervisor(hypervisor);

        return conf;
    }

    @Override
    @Before
    public void setUp()
    {
        try
        {
            hypervisor = instantiateHypervisor();

            hypervisor.init(new URL(hvURL), hvUser, hvPassword);

            hypervisor.connect(new URL(hvURL));
            // hypervisor.login(hvUser, hvPassword);

            configurations = createConfiguration();

            vMachine = hypervisor.createMachine(configurations);

            vMachine.deployMachine();

        }
        catch (Exception e)
        {
            logAndFail("Can not create the machine instance ", e);
        }

        log.debug("Created test machine");
    }

    @Override
    @After
    public void tearDown()
    {
        log.debug("deleting VM on state " + vMachine.getStateInHypervisor());

        try
        {
            if (vMachine.getStateInHypervisor() != State.POWER_OFF)
            {
                vMachine.applyState(State.POWER_OFF);
            }

            vMachine.deleteMachine();
        }
        catch (Exception e) // TODO VirtualMachineException
        {
            logAndFail("delete", e);
        }

        log.debug("deleting done");
    }

    @Test
    public void testPowerOnMachine()
    {
        log.debug("power on VM on state " + vMachine.getStateInHypervisor().name());

        try
        {
            if (vMachine.getStateInHypervisor() == State.POWER_UP)
            {
                log.debug("already power on");
                testPowerOffMachine();
            }

            vMachine.applyState(State.POWER_UP);
        }
        catch (Exception e) // TODO VirtualMachineException
        {
            logAndFail("power on", e);
        }

        log.debug("power on done");
    }

    @Test
    public void testPowerOffMachine()
    {
        log.debug("power off VM on state " + vMachine.getStateInHypervisor().name());

        try
        {
            if (vMachine.getStateInHypervisor() == State.POWER_OFF)
            {
                log.debug("already power off VM");
                testPowerOnMachine();
            }

            vMachine.applyState(State.POWER_OFF);
        }
        catch (Exception e) // TODO VirtualMachineException
        {
            logAndFail("power off", e);
        }

        log.debug("power off done");
    }

    @Test
    public void testPauseMachine()
    {
        log.debug("pause VM on state " + vMachine.getStateInHypervisor().name());

        try
        {
            if (vMachine.getStateInHypervisor() == State.PAUSE)
            {
                log.debug("already pause VM");
                testResumeMachine();
            }

            vMachine.applyState(State.PAUSE);
        }
        catch (Exception e) // TODO VirtualMachineException
        {
            logAndFail("pause", e);
        }

        log.debug("pause done");
    }

    @Test
    public void testResumeMachine()
    {
        log.debug("resume VM on state " + vMachine.getStateInHypervisor().name());

        try
        {
            if (vMachine.getStateInHypervisor() == State.RESUME)
            {
                log.debug("already resume VM");
                testPauseMachine();
            }

            vMachine.applyState(State.RESUME);
        }
        catch (Exception e) // TODO VirtualMachineException
        {
            logAndFail("resume", e);
        }

        log.debug("resume done");
    }

    @Test
    public void testResetMachine()
    {
        log.debug("reset VM on state " + vMachine.getStateInHypervisor().name());

        try
        {
            vMachine.resetMachine();
        }
        catch (VirtualMachineException e)
        {
            logAndFail("reset", e);
        }

        log.debug("reset done");
    }

    /*
     * public void testDeployMachine() throws VirtualMachineException { vmMachine.deployMachine(); }
     */
    @Test
    public void testAllMachine()
    {
        try
        {
            vMachine.applyState(State.POWER_UP);
            log.debug("power on done");
            vMachine.applyState(State.PAUSE);
            log.debug("pause done");
            vMachine.applyState(State.RESUME);
            log.debug("resume done");
            vMachine.applyState(State.POWER_OFF);
            log.debug("power off done");
        }
        catch (Exception e)
        {
            logAndFail("Complet process fail ", e);
        }
    }

    @Test
    public void testAddRemoveISCSI()
    {
        // TODO assert state power on
        VirtualMachineConfiguration vmConfigAdd = createConfiguration();

        VirtualDisk iscsiDisk = new VirtualDisk();
        iscsiDisk.setDiskType(VirtualDiskType.ISCSI);
        iscsiDisk.setId(iscsiUUID);
        iscsiDisk.setLocation(iscsiTestLocation);
        vmConfigAdd.getExtendedVirtualDiskList().add(iscsiDisk);

        // adds
        try
        {
            vMachine.reconfigVM(vmConfigAdd);
            log.debug("add iscsi success");
        }
        catch (Exception e)
        {
            logAndFail("iSCSI attachement fail", e);
        }

        /*
         * // remove VirtualMachineConfiguration vmConfigDel = createConfiguration(); try {
         * vMachine.reconfigVM(vmConfigDel); log.debug("remove iscsi success"); } catch(Exception e)
         * { logAndFail("iSCSI deattachement fail", e); }
         */
    }

    private void logAndFail(final String msg, final Exception e)
    {
        log.error(msg, e);
        fail(msg);
    }

}
