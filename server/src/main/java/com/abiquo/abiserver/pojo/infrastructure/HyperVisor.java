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

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.enumerator.HypervisorType;

/**
 * An Hypervisor controls the virtualization software in a Physical Machine We see it as another
 * infrastructure element, where Virtual Machines are attached An HyperVisor is attached to a
 * Physical Machine
 * 
 * @author Oliver
 */
public class HyperVisor extends InfrastructureElement implements IPojo<HypervisorHB>
{
    private String ip;

    private String ipService;

    private int port;

    private HyperVisorType type;

    private String user;

    private String password;

    public HyperVisor()
    {
        super();
        ip = "";
        ipService = "";
        port = 0;
        type = new HyperVisorType();
        user = "";
        password = "";
    }

    public String getIpService()
    {
        return ipService;
    }

    public void setIpService(String ipService)
    {
        this.ipService = ipService;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public HyperVisorType getType()
    {
        return type;
    }

    public void setType(HyperVisorType hyperVisorType)
    {
        type = hyperVisorType;
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
    
    public HypervisorHB toPojoHB()
    {
    	PhysicalMachine physicalMachine = (PhysicalMachine) super.getAssignedTo();
    	
    	return toPojoHB(((physicalMachine) == null) ? null : physicalMachine
                .toPojoHB());
    }

    public HypervisorHB toPojoHB(PhysicalmachineHB physicalMachine)
    {
        HypervisorHB hyperVisorHB = new HypervisorHB();

        hyperVisorHB.setIdHyper(super.getId());
        hyperVisorHB.setVirtualmachines(new HashSet<VirtualmachineHB>(0));
        hyperVisorHB.setIp(ip);
        hyperVisorHB.setIpService(ipService);
        hyperVisorHB.setPort(port);

        hyperVisorHB.setPhysicalMachine(physicalMachine);

        hyperVisorHB.setType(HypervisorType.fromValue(type.getName()));
        hyperVisorHB.setUser(user);
        hyperVisorHB.setPassword(password);

        return hyperVisorHB;
    }

}
