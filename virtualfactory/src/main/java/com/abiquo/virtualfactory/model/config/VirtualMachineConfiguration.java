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

package com.abiquo.virtualfactory.model.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.network.VirtualNIC;

/**
 * The Class VirtualMachineConfiguration.
 */
public class VirtualMachineConfiguration
{
    public final static QName remoteDesktopPortQname = new QName("remoteDesktopPort");

    public final static QName remoteDesktopPasswordQname = new QName("remoteDesktopPassword");

    /** The id. */
    protected final UUID id;

    /** The remote desktop port */
    protected int rdPort;

    /**
     * The remote desktop password. A <code>null</code> value means no password used for remote
     * desktop.
     */
    protected String rdPassword;

    /** The ram in bytes */
    private long ramMemory;

    /**
     * True if the ram is set
     */
    private boolean ram_set;

    /** The CPU number */
    private int cpuNumber;

    /**
     * True if the CPU number is set
     */
    private boolean cpu_number_set;

    /** The hyper. */
    protected IHypervisor hyper;

    /** The name. */
    protected String name;

    /** The virtual disk base information */
    // protected VirtualDisk virtualDiskBase;

    /** The virtual disk base list */
    protected List<VirtualDisk> virtualDiskBaseList;

    /**
     * The Virtual NIC list
     */
    protected List<VirtualNIC> vnicList;

    /**
     * The virtual disk extended list
     */
    private List<VirtualDisk> extendedVirtualDisk;

    /**
     * The locatio of the Repository Manager Remote Service.
     */
    private String repositoryManagerAddress;

    /**
     * Creation by clone
     */
    public VirtualMachineConfiguration(final VirtualMachineConfiguration vmConfig)
    {
        this(vmConfig.getMachineId(), vmConfig.getMachineName(), vmConfig.getVirtualDiskBaseList(),
            vmConfig.getRdPort(), vmConfig.getRdPassword(), vmConfig.getMemoryRAM(), vmConfig
                .getCpuNumber(), vmConfig.getVnicList());
        this.getExtendedVirtualDiskList().addAll(vmConfig.getExtendedVirtualDiskList());
    }

    /**
     * The Constructor.
     * 
     * @param id the id
     * @param name the name
     * @param virtualDiskList the virtual disk
     * @param rdPort the rd port
     * @param rdPassword The rd password
     * @param ramAllocationUnits the ram allocation units
     * @param cpuNumber the cpu number
     * @param virtualNIClist
     */
    public VirtualMachineConfiguration(final UUID id, final String name,
        final List<VirtualDisk> virtualDiskList, final int rdPort, final String rdPassword,
        final long ramAllocationUnits, final int cpuNumber, final List<VirtualNIC> virtualNIClist)
    {
        this.id = id;
        this.name = name;
        this.virtualDiskBaseList = virtualDiskList;
        this.rdPort = rdPort;
        this.rdPassword = rdPassword;
        this.ramMemory = ramAllocationUnits;
        this.cpuNumber = cpuNumber;
        this.extendedVirtualDisk = new ArrayList<VirtualDisk>();
        this.vnicList = virtualNIClist;
    }

    /**
     * Gets the machine id.
     * 
     * @return the machine id
     */
    public UUID getMachineId()
    {
        return id;
    }

    /**
     * Gets the machine name.
     * 
     * @return the machine name
     */
    public String getMachineName()
    {
        return name;
    }

    /**
     * Gets the hyper.
     * 
     * @return the hyper
     */
    public IHypervisor getHyper()
    {
        return hyper;
    }

    // IS

    /**
     * Checks if is sets the hypervisor.
     * 
     * @return true, if is sets the hypervisor
     */
    public boolean isSetHypervisor()
    {
        return !(hyper == null);
    }

    // SETTERS

    /**
     * Sets the hypervisor.
     * 
     * @param hyper the new hypervisor
     */
    public void setHypervisor(final IHypervisor hyper)
    {
        this.hyper = hyper;
    }

    /**
     * Sets the machine name.
     * 
     * @param name the new machine name
     */
    public void setMachineName(final String name)
    {
        this.name = name;
    }

    /**
     * Gets the virtual Disk base
     * 
     * @return the virtual disk base
     */
    public VirtualDisk getVirtualDiskBase()
    {
        return virtualDiskBaseList.get(0);
    }

    /**
     * Sets the virtual Disk base
     * 
     * @param virtualDiskBase the virtual Disk base
     */
    // public void setVirtualDiskBase(VirtualDisk virtualDiskBase)
    // {
    // this.virtualDiskBase = virtualDiskBase;
    // }

    /**
     * Gets the remote desktop port
     * 
     * @return the rdPort
     */
    public int getRdPort()
    {
        return rdPort;
    }

    public String getRdPassword()
    {
        return rdPassword;
    }

    public void setRdPassword(final String rdPassword)
    {
        this.rdPassword = rdPassword;
    }

    /**
     * Sets the remote desktop port
     * 
     * @param rdPort the rdPort to set
     */
    public void setRdPort(final int rdPort)
    {
        this.rdPort = rdPort;
    }

    /**
     * Sets the memory RAM in bytes
     * 
     * @param ramMemory the memory ram
     */
    public void setMemoryRam(final long ramMemory)
    {
        this.ramMemory = ramMemory;
        this.ram_set = true;
    }

    /**
     * Gets the memory RAM allocation units in bytes
     * 
     * @return the ramAllocationUnits
     */
    public long getMemoryRAM()
    {
        return ramMemory;
    }

    /**
     * Sets the CPU number
     * 
     * @param cpuNumber the cpuNumber to set
     */
    public void setCpuNumber(final int cpuNumber)
    {
        this.cpuNumber = cpuNumber;
        this.cpu_number_set = true;
    }

    /**
     * Gets the CPU number
     * 
     * @return the cpuNumber
     */
    public int getCpuNumber()
    {
        return cpuNumber;
    }

    /**
     * Gets the Virtual Disk extended List. To add a virtual disk, just call this method and add the
     * disk with the {@link List#add(Object)} method
     * 
     * @return
     */
    public List<VirtualDisk> getExtendedVirtualDiskList()
    {
        return extendedVirtualDisk;
    }

    public boolean isRam_set()
    {
        return ram_set;
    }

    public boolean isCpu_number_set()
    {
        return cpu_number_set;
    }

    public List<VirtualDisk> getVirtualDiskBaseList()
    {
        return virtualDiskBaseList;
    }

    /**
     * Gets the Virtual NIC list
     * 
     * @return the vnicList
     */
    public List<VirtualNIC> getVnicList()
    {
        return vnicList;
    }

    /**
     * Gets the repositoryManagerAddress.
     * 
     * @return the repositoryManagerAddress
     */
    public String getRepositoryManagerAddress()
    {
        return repositoryManagerAddress;
    }

    /**
     * Sets the repositoryManagerAddress.
     * 
     * @param repositoryManagerAddress the repositoryManagerAddress to set
     */
    public void setRepositoryManagerAddress(final String repositoryManagerAddress)
    {
        this.repositoryManagerAddress = repositoryManagerAddress;
    }

}
