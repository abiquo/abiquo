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

package com.abiquo.abiserver.pojo.metering; // NOPMD by xfernandez on 9/18/09 6:04 AM

import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.pojo.IPojo;

public class Meter implements IPojo<MeterHB>
{
    private Long idMeter;

    private Integer idDatacenter;

    private String datacenter;

    private Integer idRack;

    private String rack;

    private Integer idPhysicalMachine;

    private String physicalMachine;

    private Integer idStorageSystem;

    private String storageSystem;

    private String idStoragePool;

    private String storagePool;

    private String idVolume;

    private String volume;

    private Integer idNetwork;

    private String network;

    private Integer idSubnet;

    private String subnet;

    private Integer idEnterprise;

    private String enterprise;

    private Integer idUser;

    private String user;

    private Integer idVDC;

    private String virtualDataCenter;

    private Integer idVirtualApp;

    private String virtualApp;   

    private Integer idVirtualMachine;

    private String virtualmachine;

    private String severity;

    private String timestamp;

    private String performedby;

    private String actionperformed;  
    
	private String component;
	
	private String stacktrace;
    

    public final long getIdMeter()
    {
        return idMeter;
    }


    public final void setIdMeter(final long idMeter)
    {
        this.idMeter = idMeter;
    }


    public final Integer getIdDatacenter()
    {
        return idDatacenter;
    }


    public final void setIdDatacenter(final Integer idDatacenter)
    {
        this.idDatacenter = idDatacenter;
    }


    public final String getDatacenter()
    {
        return datacenter;
    }


    public final void setDatacenter(final String datacenter)
    {
        this.datacenter = datacenter;
    }


    public final Integer getIdRack()
    {
        return idRack;
    }


    public final void setIdRack(final Integer idRack)
    {
        this.idRack = idRack;
    }


    public final String getRack()
    {
        return rack;
    }


    public final void setRack(final String rack)
    {
        this.rack = rack;
    }


    public final Integer getIdPhysicalMachine()
    {
        return idPhysicalMachine;
    }


    public final void setIdPhysicalMachine(final Integer idPhysicalMachine)
    {
        this.idPhysicalMachine = idPhysicalMachine;
    }


    public final String getPhysicalMachine()
    {
        return physicalMachine;
    }


    public final void setPhysicalMachine(final String physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }


    public final Integer getIdStorageSystem()
    {
        return idStorageSystem;
    }


    public final void setIdStorageSystem(final Integer idStorageSystem)
    {
        this.idStorageSystem = idStorageSystem;
    }


    public final String getStorageSystem()
    {
        return storageSystem;
    }


    public final void setStorageSystem(final String storageSystem)
    {
        this.storageSystem = storageSystem;
    }


    public final String getIdStoragePool()
    {
        return idStoragePool;
    }


    public final void setIdStoragePool(final String idStoragePool)
    {
        this.idStoragePool = idStoragePool;
    }


    public final String getStoragePool()
    {
        return storagePool;
    }


    public final void setStoragePool(final String storagePool)
    {
        this.storagePool = storagePool;
    }


    public final String getIdVolume()
    {
        return idVolume;
    }


    public final void setIdVolume(final String idVolume)
    {
        this.idVolume = idVolume;
    }


    public final String getVolume()
    {
        return volume;
    }


    public final void setVolume(final String volume)
    {
        this.volume = volume;
    }


    public final Integer getIdNetwork()
    {
        return idNetwork;
    }


    public final void setIdNetwork(final Integer idNetwork)
    {
        this.idNetwork = idNetwork;
    }


    public final String getNetwork()
    {
        return network;
    }


    public final void setNetwork(final String network)
    {
        this.network = network;
    }


    public final Integer getIdSubnet()
    {
        return idSubnet;
    }


    public final void setIdSubnet(final Integer idSubnet)
    {
        this.idSubnet = idSubnet;
    }


    public final String getSubnet()
    {
        return subnet;
    }


    public final void setSubnet(final String subnet)
    {
        this.subnet = subnet;
    }


    public final Integer getIdEnterprise()
    {
        return idEnterprise;
    }


    public final void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }


    public final String getEnterprise()
    {
        return enterprise;
    }


    public final void setEnterprise(final String enterprise)
    {
        this.enterprise = enterprise;
    }


    public final Integer getIdUser()
    {
        return idUser;
    }


    public final void setIdUser(final Integer idUser)
    {
        this.idUser = idUser;
    }


    public final String getUser()
    {
        return user;
    }


    public final void setUser(final String user)
    {
        this.user = user;
    }


    public final Integer getIdVDC()
    {
        return idVDC;
    }


    public final void setIdVDC(final Integer idVDC)
    {
        this.idVDC = idVDC;
    }


    public final String getVirtualDataCenter()
    {
        return virtualDataCenter;
    }


    public final void setVirtualDataCenter(final String virtualDataCenter)
    {
        this.virtualDataCenter = virtualDataCenter;
    }


    public final Integer getIdVirtualApp()
    {
        return idVirtualApp;
    }


    public final void setIdVirtualApp(final Integer idVirtualApp)
    {
        this.idVirtualApp = idVirtualApp;
    }


    public final String getVirtualApp()
    {
        return virtualApp;
    }


    public final void setVirtualApp(final String virtualApp)
    {
        this.virtualApp = virtualApp;
    }


    public final Integer getIdVirtualMachine()
    {
        return idVirtualMachine;
    }


    public final void setIdVirtualMachine(final Integer idVirtualMachine)
    {
        this.idVirtualMachine = idVirtualMachine;
    }


    public final String getVirtualmachine()
    {
        return virtualmachine;
    }


    public final void setVirtualmachine(final String virtualmachine)
    {
        this.virtualmachine = virtualmachine;
    }


    public final String getSeverity()
    {
        return severity;
    }


    public final void setSeverity(final String severity)
    {
        this.severity = severity;
    }


    public final String getTimestamp()
    {
        return timestamp;
    }


    public final void setTimestamp(final String timestamp)
    {
        this.timestamp = timestamp;
    }


    public final String getPerformedby()
    {
        return performedby;
    }


    public final void setPerformedby(final String performedby)
    {
        this.performedby = performedby;
    }


    public final String getActionperformed()
    {
        return actionperformed;
    }


    public final void setActionperformed(final String actionperformed)
    {
        this.actionperformed = actionperformed;
    }
    
    public String getComponent() {
		return component;
	}


	public void setComponent(String component) {
		this.component = component;
	}


	public String getStacktrace() {
		return stacktrace;
	}


	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	
	@Override
    public final MeterHB toPojoHB() {
	    
		final MeterHB meter = new MeterHB();
		
		meter.setIdMeter(idMeter);
		meter.setIdDatacenter(idDatacenter);
		meter.setDatacenter(datacenter);
		meter.setIdRack(idRack);
		meter.setRack(rack);
		meter.setIdPhysicalMachine(idPhysicalMachine);
		meter.setPhysicalMachine(physicalMachine);
		meter.setIdStorageSystem(idStorageSystem);
		meter.setStorageSystem(storageSystem);
		meter.setIdStoragePool(idStoragePool);
		meter.setStoragePool(storagePool);
		meter.setIdVolume(idVolume);
		meter.setVolume(volume);
		meter.setIdNetwork(idNetwork);
		meter.setNetwork(network);
		meter.setIdSubnet(idSubnet);
		meter.setSubnet(subnet);
		meter.setIdEnterprise(idEnterprise);
		meter.setEnterprise(enterprise);
		meter.setIdUser(idUser);
		meter.setUser(user);
		meter.setIdVDC(idVDC);
		meter.setVirtualDataCenter(virtualDataCenter);
	    meter.setIdVirtualApp(idVirtualApp);
		meter.setVirtualApp(virtualApp);
		meter.setIdVirtualMachine(idVirtualMachine);
		meter.setVirtualmachine(virtualmachine);
		meter.setSeverity(severity);
		meter.setTimestamp(timestamp);
		meter.setPerformedby(performedby);
		meter.setActionperformed(actionperformed);
		meter.setComponent(component);
		meter.setStacktrace(stacktrace);
		
		
		return meter;
	}

}

