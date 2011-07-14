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

import org.apache.commons.lang.StringUtils;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImageConversions;

public class VirtualMachine extends InfrastructureElement implements IPojo<VirtualmachineHB>
{

    /* ------------- Public atributes ------------- */

    private VirtualImage virtualImage;

    private String UUID;

    private String description;

    private int ram;

    private int cpu;

    private long hd;

    private Integer vdrpPort;

    private String vdrpIP;

    private String vrdpPassword;

    private State state;

    private boolean highDisponibility;

    private int idType;

    private User user;

    private Enterprise enterprise;

    private VirtualImageConversions conversion;

    private Datastore datastore;
    
    private State subState;

    private String password;

    /* ------------- Constructor ------------- */
    public VirtualMachine()
    {
        super();
        virtualImage = new VirtualImage();
        UUID = "";
        description = "";
        ram = 0;
        cpu = 0;
        hd = 0;
        vdrpPort = 0;
        vdrpIP = "";
        highDisponibility = false;
        password = "";
    }

    public VirtualImage getVirtualImage()
    {
        return virtualImage;
    }

    public void setVirtualImage(final VirtualImage virtualImage)
    {
        this.virtualImage = virtualImage;
    }

    public String getUUID()
    {
        return UUID;
    }

    public void setUUID(final String uuid)
    {
        UUID = uuid;
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

    public Integer getVdrpPort()
    {
        return vdrpPort;
    }

    public void setVdrpPort(final Integer vdrpPort)
    {
        this.vdrpPort = vdrpPort;
    }

    public String getVdrpIP()
    {
        return vdrpIP;
    }

    public void setVdrpIP(final String vdrpIP)
    {
        this.vdrpIP = vdrpIP;
    }

    public String getVrdpPassword()
    {
        return vrdpPassword;
    }

    public void setVrdpPassword(final String vrdpPassword)
    {
        this.vrdpPassword = vrdpPassword;
    }

    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

    public State getSubState()
    {
        return subState;
    }

    public void setSubState(State subState)
    {
        this.subState = subState;
    }

    public boolean isHighDisponibility()
    {
        return highDisponibility;
    }

    public boolean getHighDisponibility()
    {
        return highDisponibility;
    }

    public void setHighDisponibility(final boolean highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    public int getIdType()
    {
        return idType;
    }

    public void setIdType(final int idType)
    {
        this.idType = idType;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(final User user)
    {
        this.user = user;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public VirtualmachineHB toPojoHB()
    {
        VirtualmachineHB virtualMachineHB = new VirtualmachineHB();

        virtualMachineHB.setIdVm(getId());
        HyperVisor hypervisor = (HyperVisor) getAssignedTo();

        if (hypervisor == null)
        {
            virtualMachineHB.setHypervisor(null);
        }
        else
        {
            virtualMachineHB.setHypervisor(hypervisor.toPojoHB());
        }

        if (conversion == null)
        {
            virtualMachineHB.setConversion(null);
        }
        else
        {
            virtualMachineHB.setConversion(conversion.toPojoHB());
        }

        // Client sends sometimes a description null or ""
        if (StringUtils.isEmpty(state.getDescription()))
        {
            virtualMachineHB.setState(StateEnum.fromId(state.getId()));
        }
        else
        {
            virtualMachineHB.setState(StateEnum.valueOf(state.getDescription()));
        }

        virtualMachineHB.setImage((virtualImage == null) ? null : virtualImage.toPojoHB());

        virtualMachineHB.setUuid(UUID);
        virtualMachineHB.setName(getName());
        virtualMachineHB.setDescription(description);
        virtualMachineHB.setRam(ram);
        virtualMachineHB.setCpu(cpu);
        virtualMachineHB.setHd(hd);
        virtualMachineHB.setVdrpIp(vdrpIP);
        virtualMachineHB.setVdrpPort(vdrpPort);
        virtualMachineHB.setHighDisponibility(highDisponibility ? 1 : 0);
        virtualMachineHB.setUserHB((user == null) ? null : user.toPojoHB());
        virtualMachineHB.setEnterpriseHB((enterprise == null) ? null : enterprise.toPojoHB());
        virtualMachineHB.setIdType(this.idType);
        virtualMachineHB.setDatastore((datastore == null) ? null : datastore.toPojoHB());
        virtualMachineHB.setSubState(subState);
        virtualMachineHB.setPassword(password);

        return virtualMachineHB;
    }

    /**
     * @return the conversion
     */
    public VirtualImageConversions getConversion()
    {
        return conversion;
    }

    /**
     * @param conversion the conversion to set
     */
    public void setConversion(final VirtualImageConversions conversion)
    {
        this.conversion = conversion;
    }

    public void setDatastore(final Datastore datastore)
    {
        this.datastore = datastore;
    }

    public Datastore getDatastore()
    {
        return datastore;
    }
}
