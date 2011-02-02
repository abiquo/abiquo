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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;

public class TestPopulate extends AbstractJpaGeneratorIT
{
    public static final String DATA_PROVIDER = "populateModel";

    private static final String DATA_FOLDER = "populateData";

    private static final String SINGLE_TEST_PROPERTY = "allocate.this";

    @Autowired
    PopulateReader populateReader;

    @DataProvider(name = DATA_PROVIDER)
    public Iterator<Object[]> populateModel(Method meth) throws URISyntaxException
    {
        final String inputFolder = DATA_FOLDER + '/' + meth.getName();

        URI populateFolderUri =
            Thread.currentThread().getContextClassLoader().getResource(inputFolder).toURI();

        File populateFolder = new File(populateFolderUri);
        if (!populateFolder.exists() || !populateFolder.isDirectory())
        {
            throw new PopulateException(String.format("%s is not a folder", populateFolderUri));
        }

        List<Object[]> models = new LinkedList<Object[]>();

        String singleTest = System.getProperty(SINGLE_TEST_PROPERTY);

        try
        {
            if (singleTest != null)
            {
                File singleTestFile = new File(populateFolder.getAbsolutePath() + '/' + singleTest);
                models.add(new Object[] {getModel(singleTestFile)});
            }
            else
            {
                for (File file : populateFolder.listFiles())
                {
                    if (file.isFile())
                    {
                        models.add(new Object[] {getModel(file)});
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new PopulateException("Can't read some populate data file"
                + e.getLocalizedMessage());
        }

        return models.iterator();
    }

    private List<String> getModel(File file) throws Exception
    {
        List<String> model = new LinkedList<String>();

        FileReader fReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fReader);
        boolean endFile = false;
        while (!endFile)
        {
            String part = br.readLine();

            if (part != null)
            {
                model.add(part);
            }
            else
            {
                endFile = true;
            }
        }

        return model;
    }

    // /
    public PopulateTestCase setUpModel(List<String> model)
    {
        tearDownModelTables(); // XXX be aware of other tests

        setup();
        
        return populateReader.readModel(model);
    }

    protected void removeVirtualMachine(Integer virtualMachineId)
    {
        populateReader.removeVirtualMachine(virtualMachineId);
    }
    
    protected void runningVirtualMachine(Integer virtualMachineId)
    {
        populateReader.runningVirtualMachine(virtualMachineId);
    }
    

    public void tearDownModelTables()
    {
        tearDown("workload_machine_load_rule", "workload_enterprise_exclusion_rule",
            "workload_fit_policy_rule", "datastore_assignment", "ip_pool_management",
            "rasd_management", "nodevirtualimage", "virtualapp", "vlan_network_assignment",
            "virtualdatacenter", "vlan_network", "network_configuration", "dhcp_service",
            "remote_service", "virtualmachine", "datastore", "hypervisor", "physicalmachine",
            "rack", "repository", "datacenter", "network", "virtualimage", "user", "role",
            "enterprise");
    }
}
