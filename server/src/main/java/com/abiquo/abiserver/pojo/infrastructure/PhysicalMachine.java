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

package com.abiquo.abiserver.pojo.infrastructure;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.MachineDto;

public class PhysicalMachine extends InfrastructureElement implements IPojo<PhysicalmachineHB>
{

    /* ------------- Public atributes ------------- */
    private DataCenter dataCenter;

    private Rack rack;

    private String description;

    private int ram;

    private int cpu;

    private long hd;

    private int realRam;

    private int realCpu;

    private long realStorage;

    private int cpuRatio = AbiConfigManager.getInstance().getAbiConfig().getVirtualCpuPerCore();

    private int ramUsed;

    private int cpuUsed;

    private long hdUsed;

    /**
     * This parameter identifies the state of the physicalMachine. 0 - Stopped 1 - Not Provisioned 2
     * - Not managed 3 - Managed 4 - Halted
     */
    private int idState;

    private String vswitchName;

    private String initiatorIQN;

    private Set<Datastore> datastores;

    private Integer idEnterprise;
    
    private HyperVisor hypervisor;

    public PhysicalMachine()
    {
        super();

        dataCenter = new DataCenter();

        cpuRatio = AbiConfigManager.getInstance().getAbiConfig().getVirtualCpuPerCore();

        description = "";
        ram = 0;
        cpu = 0;
        hd = 0;

        ramUsed = 0;
        cpuUsed = 0;
        hdUsed = 0;
        setVswitchName("");
        idEnterprise = null;
    }

    public DataCenter getDataCenter()
    {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter)
    {
        this.dataCenter = dataCenter;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getRam()
    {
        return ram;
    }

    public void setRam(int ram)
    {
        this.ram = ram;
    }

    public int getCpu()
    {
        return cpu;
    }

    public void setCpu(int cpu)
    {
        this.cpu = cpu;
    }

    public long getHd()
    {
        return hd;
    }

    public void setHd(long hd)
    {
        this.hd = hd;
    }

    public int getRealRam()
    {
        return realRam;
    }

    public void setRealRam(int realRam)
    {
        this.realRam = realRam;
    }

    public int getRealCpu()
    {
        return realCpu;
    }

    public void setRealCpu(int realCpu)
    {
        this.realCpu = realCpu;
    }

    /**
     * @param realStorage the realStorage to set
     */
    public void setRealStorage(long realStorage)
    {
        this.realStorage = realStorage;
    }

    /**
     * @return the realStorage
     */
    public long getRealStorage()
    {
        return realStorage;
    }

    // used
    public int getRamUsed()
    {
        return ramUsed;
    }

    public void setRamUsed(int ram)
    {
        ramUsed = ram;
    }

    public int getCpuUsed()
    {
        return cpuUsed;
    }

    public void setCpuUsed(int cpu)
    {
        cpuUsed = cpu;
    }

    public long getHdUsed()
    {
        return hdUsed;
    }

    public void setHdUsed(long hd)
    {
        hdUsed = hd;
    }

    /**
     * @return the cpuRatio
     */
    public int getCpuRatio()
    {
        return cpuRatio;
    }

    public int getIdState()
    {
        return idState;
    }

    public void setIdState(int idState)
    {
        this.idState = idState;
    }

    /**
     * @param cpuRatio the cpuRatio to set
     */
    public void setCpuRatio(int cpuRatio)
    {
        this.cpuRatio = cpuRatio;
    }

    /**
     * @param vswitchName the vswitchName to set
     */
    public void setVswitchName(String vswitchName)
    {
        this.vswitchName = vswitchName;
    }

    /**
     * @return the vswitchName
     */
    public String getVswitchName()
    {
        return vswitchName;
    }

    /**
     * @return the initiatorIQN
     */
    public String getInitiatorIQN()
    {
        return initiatorIQN;
    }

    /**
     * @param initiatorIQN, the new initiatorIQN to set
     */
    public void setInitiatorIQN(String initiatorIQN)
    {
        this.initiatorIQN = initiatorIQN;
    }

    /**
     * @param datastores the datastores to set
     */
    public void setDatastores(Set<Datastore> datastores)
    {
        this.datastores = datastores;
    }

    /**
     * @return the datastores
     */
    public Set<Datastore> getDatastores()
    {
        return datastores;
    }

    public void setIdEnterprise(Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }
    
    

    public HyperVisor getHypervisor() {
		return hypervisor;
	}

	public void setHypervisor(HyperVisor hypervisor) {
		this.hypervisor = hypervisor;
	}

	public PhysicalmachineHB toPojoHB()
    {
        PhysicalmachineHB physicalMachineHB = new PhysicalmachineHB();

        physicalMachineHB.setDataCenter(getDataCenter().toPojoHB());

        if (cpuRatio == 0)
        {
            physicalMachineHB.setCpuRatio(AbiConfigManager.getInstance().getAbiConfig()
                .getVirtualCpuPerCore());
        }
        else
        {
            physicalMachineHB.setCpuRatio(getCpuRatio());
        }

        physicalMachineHB.setIdPhysicalMachine(getId());
        physicalMachineHB.setName(StringUtils.substring(getName(), 0, 255)); // a fully qualified
        // domain name (FQDN)
        // is 255 octets -
        // where any one label
        // can be 63 octets
        // long at most (RFC
        // 2181)
        physicalMachineHB.setDescription(getDescription());
        physicalMachineHB.setCpu(getCpu());
        physicalMachineHB.setRealCpu(realCpu);
        physicalMachineHB.setRealRam(realRam);
        physicalMachineHB.setRealStorage(realStorage);
        physicalMachineHB.setCpuUsed(getCpuUsed());
        physicalMachineHB.setRam(getRam());
        physicalMachineHB.setRamUsed(getRamUsed());
        physicalMachineHB.setHd(getHd());
        physicalMachineHB.setHdUsed(getHdUsed());
        Rack rack = (Rack) getAssignedTo();
        physicalMachineHB.setRack(rack.toPojoHB());
        physicalMachineHB.setIdState(getIdState());
        physicalMachineHB.setVswitchName(vswitchName);
        physicalMachineHB.setInitiatorIQN(initiatorIQN);
        Set<DatastoreHB> datastoresHB = new HashSet<DatastoreHB>();
        if (datastores != null)
        {
            for (Datastore datastore : datastores)
            {
                datastoresHB.add(datastore.toPojoHB());
            }
        }
        physicalMachineHB.setDatastoresHB(datastoresHB);
        if (idEnterprise != null)
        {
            physicalMachineHB.setIdEnterprise((idEnterprise.intValue() != 0) ? idEnterprise : null);
        }
        
        if (hypervisor != null)
        {
        	physicalMachineHB.setHypervisor(hypervisor.toPojoHB(physicalMachineHB));
        }

        return physicalMachineHB;
    }

    public Rack getRack()
    {
        return rack;
    }

    public void setRack(Rack rack)
    {
        this.rack = rack;
    }

    public static PhysicalMachine create(MachineDto dto, DataCenter datacenter, Rack rack)
    {
        PhysicalMachine physicalMachine = new PhysicalMachine();

        physicalMachine.setDataCenter(datacenter);
        physicalMachine.setCpuRatio(dto.getVirtualCpusPerCore());
        physicalMachine.setCpu(dto.getVirtualCpuCores());
        physicalMachine.setCpuUsed(dto.getVirtualCpusUsed());
        physicalMachine.setDescription(dto.getDescription());
        physicalMachine.setHd(dto.getVirtualHardDiskInMb());
        physicalMachine.setRealCpu(dto.getRealCpuCores());
        physicalMachine.setRealRam(dto.getRealRamInMb());
        physicalMachine.setRealStorage(dto.getRealHardDiskInMb());
        physicalMachine.setHdUsed(dto.getVirtualHardDiskUsedInMb());
        physicalMachine.setId(dto.getId());
        physicalMachine.setName(dto.getName());
        physicalMachine.setAssignedTo(rack);
        physicalMachine.setRam(dto.getVirtualRamInMb());
        physicalMachine.setRamUsed(dto.getVirtualRamUsedInMb());
        physicalMachine.setIdState(dto.getState().ordinal());
        physicalMachine.setVswitchName(dto.getVirtualSwitch());

        // FIXME: where is initiatorIQN?
        // physicalMachine.setInitiatorIQN(dto.get);

        return physicalMachine;
    }

}
