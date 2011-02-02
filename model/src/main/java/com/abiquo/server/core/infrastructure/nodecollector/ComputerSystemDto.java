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

package com.abiquo.server.core.infrastructure.nodecollector;

import java.io.Serializable;

/**
 * Class computer system POJO contains all the shared capabilities for physical and virtual systems.
 * 
 * @author ibarrera
 */
public class ComputerSystemDto implements Serializable
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** The name of the node. */
    private String name;

    /** The number of cores that the machine store. */
    private Long cpu;

    /** The amount of memory RAM the machine contains. */
    private Long ram;

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the cpu.
     * 
     * @return the cpu
     */
    public Long getCpu()
    {
        return cpu;
    }

    /**
     * Sets the cpu.
     * 
     * @param cpu the cpu to set
     */
    public void setCpu(Long cpu)
    {
        this.cpu = cpu;
    }

    /**
     * Gets the ram.
     * 
     * @return the ram
     */
    public Long getRam()
    {
        return ram;
    }

    /**
     * Sets the ram.
     * 
     * @param ram the ram to set
     */
    public void setRam(Long ram)
    {
        this.ram = ram;
    }

}
