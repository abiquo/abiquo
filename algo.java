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



        BasicResult basicResult;
        basicResult = new BasicResult();

        DataCenterDAO datacenterDAO = factory.getDataCenterDAO();
        RackDAO rackDAO = factory.getRackDAO();
        PhysicalMachineDAO pmDAO = factory.getPhysicalMachineDAO();
        MachineLoadRuleDAO machineLoadRuleDAO = factory.getMachineLoadRuleDAO();
        FitPolicyRuleDAO fitPolicyRuleDAO = factory.getFitPolicyRuleDAO();

        try
        {
            factory.beginConnection();
            DatacenterHB datacenterPojo = datacenterDAO.findById(dataCenter.getId());

            // only delete the datacenter if it doesn't have any virtual
            // datacenter and any storage device associated
            if (datacenterDAO.getNumberVirtualDatacentersByDatacenter(datacenterPojo
                .getIdDataCenter()) == 0)
            {
                if (datacenterDAO.getNumberStorageDevicesByDatacenter(datacenterPojo
                    .getIdDataCenter()) == 0)
                {
                    // Delete datacenter allocation rules
                    fitPolicyRuleDAO.deleteRulesForDatacenter(dataCenter.getId());
                    machineLoadRuleDAO.deleteRulesForDatacenter(dataCenter.getId());

                    for (RackHB currentRack : datacenterPojo.getRacks())
                    {
                        for (PhysicalmachineHB pmToDelete : currentRack.getPhysicalmachines())
                        {
                            deleteNotManagedVMachines(pmToDelete.getIdPhysicalMachine());

                            pmDAO.makeTransient(pmToDelete);
                        }

                        // Once all physical machines are deleted, delete the rack
                        rackDAO.makeTransient(currentRack);
                    }

                    datacenterPojo.setEntLimits(new HashSet<DatacenterLimitHB>());

                    datacenterDAO.makeTransient(datacenterPojo);

                    // make the changes persistent
                    factory.endConnection();

                    traceLog(SeverityType.INFO, ComponentType.DATACENTER, EventType.DC_DELETE,
                        userSession, dataCenter, null, null, null, null, null, null, null);

                    basicResult.setSuccess(true);

                }
                else
                {
                    traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                        userSession, dataCenter, null, "there are storage devices associated",
                        null, null, null, null, null);

                    // This exception will be catch if you try to delete
                    // PhysicalDatacenters with
                    // AssociatedDatacenters
                    factory.rollbackConnection();

                    errorManager.reportError(InfrastructureCommandImpl.resourceManager,
                        basicResult, "deleteDataCenterConstraintSD");
                }
            }
            else
            {
                traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                    userSession, dataCenter, null, "there are virtual datacenters associated",
                    null, null, null, null, null);

                // This exception will be catch if you try to delete
                // PhysicalDatacenters with
                // AssociatedDatacenters
                factory.rollbackConnection();

                errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                    "deleteDataCenterConstraint");

            }

        }
        catch (Exception e)
        {
            factory.rollbackConnection();

            errorManager.reportError(InfrastructureCommandImpl.resourceManager, basicResult,
                "deleteDataCenter", e);

            traceLog(SeverityType.CRITICAL, ComponentType.DATACENTER, EventType.DC_DELETE,
                userSession, dataCenter, null, e.getMessage(), null, null, null, null, null);

        }

        return basicResult;
