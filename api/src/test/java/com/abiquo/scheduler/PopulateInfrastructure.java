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

import static junit.framework.Assert.assertTrue;
import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.HypervisorGenerator;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.DatacenterLimitsDAO;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.DatastoreGenerator;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineGenerator;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackGenerator;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;
import com.abiquo.server.core.infrastructure.RepositoryGenerator;
import com.softwarementors.commons.test.SeedGenerator;

@Controller
@Transactional
public class PopulateInfrastructure extends PopulateConstants
{
    @Autowired
    private InfrastructureRep dcRep;

    @Autowired
    private DatacenterLimitsDAO dcLimitsDao;

    @Autowired
    private EnterpriseRep enterpriseRep;

    @Autowired
    private RepositoryDAO repoDao; // TODO on dcRep

    // Generators
    private SeedGenerator sgen = new SeedGenerator();

    private DatacenterGenerator dcGen = new DatacenterGenerator(sgen);

    private RackGenerator rackGen = new RackGenerator(sgen);

    private MachineGenerator machineGen = new MachineGenerator(sgen);

    private HypervisorGenerator hyperGen = new HypervisorGenerator(sgen);

    private RepositoryGenerator repoGen = new RepositoryGenerator(sgen);

    private DatastoreGenerator datastoreGen = new DatastoreGenerator(sgen);

    public PopulateInfrastructure()
    {
    }

    /**
     * <ul>
     * <li>d1
     * <li>d1.r1
     * <li>d1.r1.m1:HTYPE [ :cpu,used:ram,used:hd,used ]
     * </ul>
     */
    public void populateInfrastructure(final String declar)
    {
        //
        String datacenterName;
        String rackName;
        String machineDeclaration;

        String[] fragments = declar.split(DELIMITER_ENTITIES, 0);

        switch (fragments.length)
        {
            case 1: // create datacenter
                datacenterName = fragments[0];
                assertTrue("Expected datacenter declaration " + declar,
                    datacenterName.startsWith(DEC_DATACENTER));

                createDatacenter(datacenterName);
                break;
            case 2: // create rack
                datacenterName = fragments[0];
                rackName = fragments[1];
                assertTrue("Expected rack declaration " + declar,
                    datacenterName.startsWith(DEC_DATACENTER));
                assertTrue("Expected rack declaration " + declar, rackName.startsWith(DEC_RACK));

                createRack(datacenterName, rackName);
                break;

            case 3: // create machine
                datacenterName = fragments[0];
                rackName = fragments[1];
                machineDeclaration = fragments[2];
                assertTrue("Expected machine declaration " + declar,
                    datacenterName.startsWith(DEC_DATACENTER));
                assertTrue("Expected machine declaration " + declar, rackName.startsWith(DEC_RACK));
                assertTrue("Expected machine declaration " + declar,
                    machineDeclaration.startsWith(DEC_MACHINE));

                createMachine(datacenterName, rackName, machineDeclaration);
                break;

            default:
                throw new PopulateException("Invalid create infrastructure declaration : " + declar);
        }
    }

    public Datacenter createDatacenter(final String dcStr)
    {
        Datacenter dc = dcRep.findByName(dcStr);

        if (dc == null)
        {
            dc = dcGen.createInstance(dcStr);

            Repository repo = repoGen.createInstance(dc);
            dcRep.insert(dc);
            repoDao.persist(repo);

            // allowAllEnterpriseByDefault(dc);

            return dc;
        }
        else
        {
            throw new PopulateException(String.format("Datacenter [%s] already exist", dcStr));
        }
    }

    private void allowAllEnterpriseByDefault(final Datacenter dc)
    {
        for (Enterprise enterprise : enterpriseRep.findAll())
        {
            DatacenterLimits dcLimit = new DatacenterLimits(enterprise, dc);

            dcLimitsDao.persist(dcLimit);
        }

    }

    /**
     * @param rackStr, r1:2,1002,2,10,[3;4] -- minVlan, maxVlna, vlanxvdcexpected, NRSQ,
     *            vlansIdAvoided
     */
    public Rack createRack(final String dcStr, final String rackStr)
    {
        String[] frags = rackStr.split(DELIMITER_DEFINITION);

        assertTrue("Invalid rack declaration " + rackStr, frags.length == 1 || frags.length == 2);

        String rackName = frags[0];

        Rack rack = dcRep.findRackByName(rackName);

        if (rack == null)
        {
            Datacenter dc = dcRep.findByName(dcStr);

            Assert.assertNotNull("Datacenter not found " + dcStr, dc);

            rack = rackGen.createInstanceDefaultNetwork(dc, rackName);

            if (frags.length == 2)
            {
                frags = frags[1].split(DELIMITER_ATTRIBUTES);

                assertTrue(frags.length == 5);

                String minVlan = frags[0];
                String maxVlan = frags[1];
                String vlanxvdcExpected = frags[2];
                String nsqr = frags[3];
                String avoids = frags[4];

                rack.setVlanIdMin(Integer.valueOf(minVlan));
                rack.setVlanIdMax(Integer.valueOf(maxVlan));
                rack.setVlanPerVdcExpected(Integer.valueOf(vlanxvdcExpected));
                rack.setNrsq(Integer.valueOf(nsqr));

                avoids = avoids.substring(1, avoids.length() - 1);
                avoids = avoids.replace(';', ',');

                rack.setVlansIdAvoided(avoids);

            }// optional attributes

            dcRep.insertRack(rack);

            return rack;
        }
        else
        {
            throw new PopulateException(String.format("Rack [%s] already exist", rackStr));
        }
    }

    /**
     * @param mStr, m1:HTYPE [ :cpu,used:ram,used:hd,used ]
     */
    public Machine createMachine(final String dcStr, final String rackStr, final String machineDef)
    {
        String mFrg[] = machineDef.split(DELIMITER_DEFINITION);

        assertTrue(mFrg.length == 2);
        String mName = mFrg[0];

        Machine machine = dcRep.findMachineByName(mName);

        if (machine == null)
        {
            mFrg = mFrg[1].split(DELIMITER_ATTRIBUTES);

            assertTrue("Expected machine delcaration " + machineDef, mFrg.length == 1
                || mFrg.length == 4);

            HypervisorType htype = HypervisorType.valueOf(mFrg[0]);

            Rack rack = dcRep.findRackByName(rackStr);

            org.testng.Assert.assertNotNull(rack, "Rack not found " + rackStr);

            machine = machineGen.createMachine(rack.getDatacenter(), rack);
            machine.setState(MachineState.MANAGED);
            dcRep.insertMachine(machine);

            Hypervisor hyper = hyperGen.createInstance(machine, htype);
            dcRep.insertHypervisor(hyper);

            machine.setName(mName);
            machine.setHypervisor(hyper);

            long cpu = DEF_MACHINE_CPU, ram = DEF_MACHINE_RAM, hd = DEF_MACHINE_HD;

            if (mFrg.length == 4)
            {
                cpu = Long.valueOf(mFrg[1]);
                ram = Long.valueOf(mFrg[2]);
                hd = Long.valueOf(mFrg[3]);
            }

            Datastore ds = datastoreGen.createInstance(machine);
            ds.setEnabled(true);
            ds.setUsedSize(0);
            ds.setSize(hd * GB_TO_MB * 1014 * 1024); // TODO Datastore size is bytes

            dcRep.insertDatastore(ds);

            machine.setVirtualCpuCores((int) cpu);
            machine.setVirtualCpusUsed(0);

            machine.setVirtualRamInMb((int) (ram * GB_TO_MB));
            machine.setVirtualRamUsedInMb(0);

            machine.setVirtualHardDiskInBytes(hd * GB_TO_MB * 1014 * 1024);
            machine.setVirtualHardDiskUsedInBytes(0L);

            dcRep.updateMachine(machine);
        }
        else
        {
            throw new PopulateException(String.format("Machine [%s] already exist", machineDef));
        }

        return machine;
    }

}
