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

package com.abiquo.abiserver.scheduler;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;

import org.apache.wink.common.internal.providers.entity.csv.CsvReader;

/**
 * Test the scheduler behavior for a given IScheduler implementation and a TestDataSet (with a
 * VirtualImage and PhysicalMachine test set).
 */
public class SchedulerRestrictionsTest
{
    // /** The logger object */
    // private final static Logger log = LoggerFactory.getLogger(SchedulerRestrictionsTest.class);
    //
    // /** The scheduler behavior implementation to test */
    // private IScheduler scheduler;
    //
    // /**
    // * The input data set: initialize (and clean) physicalMachines (datacenter,rack and so) from
    // * DataBase and provide the VirtualImage set to test
    // */
    // private ISchedulerTestCase dataSet;
    //
    // /** Controls the DB model for the test. */
    // private PopulateModelTest populate;
    //
    // /**
    // * The virtual appliance Id, used on the IScheduler.select (know witch DC and report soft
    // limit
    // * exceeded)
    // */
    // private int virtualApplianceId;
    //
    // public SchedulerRestrictionsTest()
    // {
    // scheduler = new MinFitRankingScheduler();
    // try
    // {
    // dataSet = new SchedulerRestrictionsTestDataSet1();
    // }
    // catch (PersistenceException e)
    // {
    // assertFalse("Can not initialize the test", true);
    // }
    //
    // assertEquals("same images as expected physical machine association", dataSet
    // .getVirtualImages().size(), dataSet.getExpectedPhysicalMachineNameSequence().size());
    //
    // log.info("Testing scheduler " + scheduler.getClass().getCanonicalName()
    // + "\n using dataSet impl " + dataSet.getClass().getCanonicalName());
    // }
    //
    // /**
    // * Run the IScheduler.selectMachines for the given VirtualImage data set. Start the DB
    // * transaction before call the IScheduler. Commit at its end. TODO: sort the VirtualImages
    // * requirements ?
    // */
    // public void testScheduler()
    // {
    // List<VirtualMachine> machines = new ArrayList<VirtualMachine>();
    // Session session;
    // Transaction transaction;
    //
    // for (VirtualimageHB vImageHb : dataSet.getVirtualImages())
    // {
    // session = HibernateUtil.getSession();
    // transaction = session.beginTransaction();
    //
    // VirtualMachine vMachine = new VirtualMachine();
    //
    // VirtualImage vImage = vImageHb.toPojo(); // XXX getVirtualImage(vImageHb);
    //
    // try
    // {
    // // TODO test force=true
    // scheduler.selectMachine(session, virtualApplianceId, vMachine, vImage, true);
    //
    // // transaction.commit();
    // // session = HibernateUtil.getSession();
    // // transaction = session.beginTransaction();
    //
    // try
    // {
    // // Check where the virtual machine is instantiated.
    // PhysicalMachine pm =
    // (PhysicalMachine) ((HyperVisor) vMachine.getAssignedTo()).getAssignedTo();
    //
    // vMachine.setName(pm.getName());
    // State st = new State(StateEnum.IN_PROGRESS);
    // vMachine.setState(st);
    // log.info("vImage [{}] to pMachine[{}]", vImage.getName(), pm.getName());
    //
    // }
    // catch (Exception e)
    // {
    // assertNotNull("Can not obtain the virtual image's physical machine", e);
    // }
    //
    // transaction.commit();
    //
    // try
    // {
    // populate.createVirtualImageNode(vImageHb, vMachine);
    //
    // // populate.updateVirtualMachineState(vMachine);
    // // vMachine.setVirtualImage(vImageHb.toPojo());
    // }
    // catch (PersistenceException e)
    // {
    // assertNull(
    // "Can not create the virtual image node defining the relation vImage to vApp ",
    // e);
    // }
    // }
    // catch (HardLimitExceededException hle)
    // {
    // log.warn("VImage [" + vImage.getName() + "] Hard limit exceeded :"
    // + hle.getMessage());
    //
    // vMachine.setName(ISchedulerTestCase.HARD_LIMIT_REACH);
    // }
    // // TODO catch Softlimit and play with force
    // catch (SchedulerException e)
    // {
    // log.warn("VImage [" + vImage.getName() + "] Scheduler exception : "
    // + e.getMessage());
    //
    // // e.printStackTrace();
    //
    // vMachine.setName(ISchedulerTestCase.NOT_ENOUGH_RESOURCES);
    // }
    //
    // machines.add(vMachine);
    //
    // }// for each target virtual image
    //
    // assertEquals("as machines as required images", dataSet.getVirtualImages().size(), machines
    // .size());
    //
    // printSchedulerPlan(machines);
    //
    // }
    //
    // /**
    // * Prints where the VirtualImages will be deployed
    // */
    // private void printSchedulerPlan(final List<VirtualMachine> machines)
    // {
    // List<VirtualimageHB> images = dataSet.getVirtualImages();
    // List<String> expectedPMName = dataSet.getExpectedPhysicalMachineNameSequence();
    //
    // assertEquals("As machines as images", machines.size(), images.size());
    // assertEquals("As machines as expected", machines.size(), expectedPMName.size());
    //
    // log.debug(" ========== Scheduler Plan ========== ");
    //
    // for (int i = 0; i < images.size(); i++)
    // {
    // log.debug("image " + images.get(i).getName() + "\t at machine "
    // + machines.get(i).getName());
    //
    // assertEquals("Expected match", expectedPMName.get(i), machines.get(i).getName());
    //
    // }
    //
    // log.debug(" ========== ============== ========== ");
    // }
    //
    // /**
    // * Adds on Database the PhysicalMachines on the provided DataSet. Also a virtualAppliance.
    // */
    // @Override
    // protected void setUp()
    // {
    // try
    // {
    // populate = PopulateModelTest.getInstance();
    //
    // ResourceAllocationLimitHB ral = dataSet.getResourceAllocationLimit();
    //
    // AbiConfigManager.getInstance().getAbiConfig().setResourceReservationLimits(ral);
    //
    // virtualApplianceId = populate.getVirtualApp().getIdVirtualApp();
    //
    // populate.initDBPhysicalMachines(dataSet.getPhysicalMachines());
    // }
    // catch (PersistenceException e)
    // {
    // e.printStackTrace();
    // fail("Can not initialize the scheduler test");
    // }
    // }
    //
    // /**
    // * Removes from Database the PhysicalMachines and the virtualAppliance created during setUp()
    // */
    // @Override
    // protected void tearDown()
    // {
    // try
    // {
    // populate.clearCreatedDomainObjects();
    // }
    // catch (PersistenceException e)
    // {
    // e.printStackTrace();
    // fail("Can not clean up");
    // }
    // }
    //
    // public static void main(final String[] args)
    // {
    // SchedulerRestrictionsTest tst = new SchedulerRestrictionsTest();
    //
    // tst.setUp();
    //
    // tst.testScheduler();
    //
    // tst.tearDown();
    // }

    public static void main(final String[] args)
    {
        CsvReader reader = new CsvReader(new StringReader("a,2,3,4-10"));
        Collection<Integer> vlans_avoided_collection = new HashSet();
        String[] line = reader.readLine();
        try
        {
            for (String vlan_id : line)
            {
                if (vlan_id.split("-").length > 1)
                {
                    String[] interval = vlan_id.split("-");
                    Integer min = Integer.valueOf(interval[0]);
                    Integer max = Integer.valueOf(interval[1]);
                    if (min.compareTo(max) > 0)
                    {
                        Integer temp = max;
                        max = min;
                        min = temp;
                    }
                    else
                    {
                        for (int i = min; i <= max; i++)
                        {
                            vlans_avoided_collection.add(i);
                        }
                    }
                }
                else
                {
                    vlans_avoided_collection.add(Integer.valueOf(vlan_id));
                }
            }
        }
        catch (NumberFormatException e)
        {
            // Throws Exception
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
