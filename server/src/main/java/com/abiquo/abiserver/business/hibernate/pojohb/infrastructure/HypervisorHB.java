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

package com.abiquo.abiserver.business.hibernate.pojohb.infrastructure;

// Generated 16-oct-2008 16:52:14 by Hibernate Tools 3.2.1.GA

import java.util.HashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisor;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.server.core.enumerator.HypervisorType;

/**
 * Hypervisor generated by hbm2java
 */
public class HypervisorHB implements java.io.Serializable, IPojoHB<HyperVisor>
{

    private static final long serialVersionUID = -7337606409880143290L;

    private Integer idHyper;

    private Set<VirtualmachineHB> virtualmachines = new HashSet<VirtualmachineHB>(0);

    private String ip;

    private String ipService;

    private Integer port;

    private PhysicalmachineHB physicalMachine;

    private HypervisorType type;

    private String user;

    private String password;

    public HypervisorHB()
    {
    }

    public String getIpService()
    {
        return ipService;
    }

    public void setIpService(String ipService)
    {
        this.ipService = ipService;
    }

    public Integer getIdHyper()
    {
        return idHyper;
    }

    public void setIdHyper(Integer idHyper)
    {
        this.idHyper = idHyper;
    }

    public Set<VirtualmachineHB> getVirtualmachines()
    {
        return virtualmachines;
    }

    public void setVirtualmachines(Set<VirtualmachineHB> virtualmachines)
    {
        this.virtualmachines = virtualmachines;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public PhysicalmachineHB getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(PhysicalmachineHB physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public HyperVisor toPojo()
    {
    	return toPojo(physicalMachine.toPojo());
    }
    
    public HyperVisor toPojo(PhysicalMachine physicalMachine)
    {
        HyperVisor hyperVisor = new HyperVisor();

        hyperVisor.setAssignedTo(physicalMachine);
        
        hyperVisor.setId(idHyper);
        hyperVisor.setName(getType().getValue());
        

        hyperVisor.setIp(ip);
        hyperVisor.setIpService(ipService);
        hyperVisor.setPort(port);

        hyperVisor.setType(new HyperVisorType(type));
        hyperVisor.setUser(user);
        hyperVisor.setPassword(password);

        return hyperVisor;
    }

    public HypervisorType getType()
    {
        return type;
    }

    public void setType(HypervisorType type)
    {
        this.type = type;
    }

}
