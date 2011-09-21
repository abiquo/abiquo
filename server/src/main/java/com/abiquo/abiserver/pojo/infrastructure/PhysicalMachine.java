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
import com.abiquo.server.core.infrastructure.DatastoreDto;
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

    private String ipmiIp;

    private Integer ipmiPort;

    private String ipmiUser;

    private String ipmiPassword;

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
        ipmiIp = null;
        ipmiPort = null;
        ipmiUser = null;
        ipmiPassword = null;
    }

    public DataCenter getDataCenter()
    {
        return dataCenter;
    }

    public void setDataCenter(final DataCenter dataCenter)
    {
        this.dataCenter = dataCenter;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public int getRam()
    {
        return ram;
    }

    public void setRam(final int ram)
    {
        this.ram = ram;
    }

    public int getCpu()
    {
        return cpu;
    }

    public void setCpu(final int cpu)
    {
        this.cpu = cpu;
    }

    public long getHd()
    {
        return hd;
    }

    public void setHd(final long hd)
    {
        this.hd = hd;
    }

    // used
    public int getRamUsed()
    {
        return ramUsed;
    }

    public void setRamUsed(final int ram)
    {
        ramUsed = ram;
    }

    public int getCpuUsed()
    {
        return cpuUsed;
    }

    public void setCpuUsed(final int cpu)
    {
        cpuUsed = cpu;
    }

    public long getHdUsed()
    {
        return hdUsed;
    }

    public void setHdUsed(final long hd)
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

    public void setIdState(final int idState)
    {
        this.idState = idState;
    }

    /**
     * @param cpuRatio the cpuRatio to set
     */
    public void setCpuRatio(final int cpuRatio)
    {
        this.cpuRatio = cpuRatio;
    }

    /**
     * @param vswitchName the vswitchName to set
     */
    public void setVswitchName(final String vswitchName)
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
    public void setInitiatorIQN(final String initiatorIQN)
    {
        this.initiatorIQN = initiatorIQN;
    }

    /**
     * @param datastores the datastores to set
     */
    public void setDatastores(final Set<Datastore> datastores)
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

    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public String getIpmiIp()
    {
        return ipmiIp;
    }

    public void setIpmiIp(final String ipmiIp)
    {
        this.ipmiIp = ipmiIp;
    }

    public Integer getIpmiPort()
    {
        return ipmiPort;
    }

    public void setIpmiPort(final Integer ipmiPort)
    {
        this.ipmiPort = ipmiPort;
    }

    public String getIpmiUser()
    {
        return ipmiUser;
    }

    public void setIpmiUser(final String ipmiUser)
    {
        this.ipmiUser = ipmiUser;
    }

    public String getIpmiPassword()
    {
        return ipmiPassword;
    }

    public void setIpmiPassword(final String ipmiPassword)
    {
        this.ipmiPassword = ipmiPassword;
    }

    public HyperVisor getHypervisor()
    {
        return hypervisor;
    }

    public void setHypervisor(final HyperVisor hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    @Override
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
        physicalMachineHB.setCpuUsed(getCpuUsed());
        physicalMachineHB.setRam(getRam());
        physicalMachineHB.setRamUsed(getRamUsed());
        Rack rack = (Rack) getAssignedTo();
        physicalMachineHB.setRack(rack.toPojoHB());
        physicalMachineHB.setIdState(getIdState());
        physicalMachineHB.setVswitchName(vswitchName);
        physicalMachineHB.setInitiatorIQN(initiatorIQN);
        physicalMachineHB.setIpmiIp(ipmiIp);
        physicalMachineHB.setIpmiPort(ipmiPort);
        physicalMachineHB.setIpmiUser(ipmiUser);
        physicalMachineHB.setIpmiPassword(ipmiPassword);
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
            physicalMachineHB.setIdEnterprise(idEnterprise.intValue() != 0 ? idEnterprise : null);
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

    public void setRack(final Rack rack)
    {
        this.rack = rack;
    }

    public static PhysicalMachine create(final MachineDto dto, final DataCenter datacenter,
        final Rack rack)
    {
        PhysicalMachine physicalMachine = new PhysicalMachine();

        physicalMachine.setDataCenter(datacenter);
        physicalMachine.setCpuRatio(dto.getVirtualCpusPerCore());
        physicalMachine.setCpu(dto.getVirtualCpuCores());
        physicalMachine.setCpuUsed(dto.getVirtualCpusUsed());
        physicalMachine.setDescription(dto.getDescription());
        physicalMachine.setId(dto.getId());
        physicalMachine.setName(dto.getName());
        physicalMachine.setAssignedTo(rack);
        physicalMachine.setRam(dto.getVirtualRamInMb());
        physicalMachine.setRamUsed(dto.getVirtualRamUsedInMb());
        physicalMachine.setIdState(dto.getState().ordinal());
        physicalMachine.setVswitchName(dto.getVirtualSwitch());
        physicalMachine.setIpmiIp(dto.getIpmiIp());
        physicalMachine.setIpmiPort(dto.getIpmiPort());
        physicalMachine.setIpmiUser(dto.getIpmiUser());
        physicalMachine.setIpmiPassword(dto.getIpmiPassword());

        // FIXME: where is initiatorIQN?
        // physicalMachine.setInitiatorIQN(dto.get);

        return physicalMachine;
    }

    public static PhysicalMachine create(final MachineDto dto, final Rack rack)
    {
        PhysicalMachine physicalMachine = new PhysicalMachine();

        // physicalMachine.setDataCenter(datacenter);
        physicalMachine.setCpuRatio(dto.getVirtualCpusPerCore());
        physicalMachine.setCpu(dto.getVirtualCpuCores());
        physicalMachine.setCpuUsed(dto.getVirtualCpusUsed());
        physicalMachine.setDescription(dto.getDescription());
        physicalMachine.setId(dto.getId());
        physicalMachine.setName(dto.getName());
        physicalMachine.setAssignedTo(rack);
        physicalMachine.setRam(dto.getVirtualRamInMb());
        physicalMachine.setRamUsed(dto.getVirtualRamUsedInMb());
        physicalMachine.setIdState(dto.getState().ordinal());
        physicalMachine.setVswitchName(dto.getVirtualSwitch());
        physicalMachine.setIpmiIp(dto.getIpmiIp());
        physicalMachine.setIpmiPort(dto.getIpmiPort());
        physicalMachine.setIpmiUser(dto.getIpmiUser());
        physicalMachine.setIpmiPassword(dto.getIpmiPassword());

        // FIXME: where is initiatorIQN?
        // physicalMachine.setInitiatorIQN(dto.get);

        return physicalMachine;
    }

    public void completeInfo(final MachineDto dto)
    {
        HyperVisor h = new HyperVisor();
        h.setIp(dto.getIp());
        h.setIpService(dto.getIpService());
        if (dto.getType() != null)
        {
            h.setType(new HyperVisorType(dto.getType()));
        }
        h.setUser(dto.getUser());
        h.setPassword(dto.getPassword());
        if (dto.getPort() != null)
        {
            h.setPort(dto.getPort());
        }
        this.setHypervisor(h);

        if (dto.getDatastores() != null && !dto.getDatastores().getCollection().isEmpty())
        {
            this.setDatastores(new HashSet<Datastore>());

            for (DatastoreDto dataDto : dto.getDatastores().getCollection())
            {
                Datastore datastore = new Datastore();
                datastore.setDatastoreUUID(dataDto.getDatastoreUUID());
                datastore.setDirectory(dataDto.getDirectory());
                datastore.setEnabled(dataDto.isEnabled());
                datastore.setId(dataDto.getId());
                datastore.setName(dataDto.getName());
                datastore.setSize(dataDto.getSize());
                datastore.setUsedSize(dataDto.getUsedSize());
                datastore.setUUID(dataDto.getRootPath());

                this.getDatastores().add(datastore);
            }
        }
    }

}
