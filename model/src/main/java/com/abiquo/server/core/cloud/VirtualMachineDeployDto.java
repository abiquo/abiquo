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

package com.abiquo.server.core.cloud;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * DTO to deploy parameters of the virtual machine.
 * 
 * @author ssedano
 */
@XmlRootElement(name = "virtualMachineDeploy")
public class VirtualMachineDeployDto extends SingleResourceTransportDto implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -272879466628574960L;

    /**
     * Force the soft limits defined for the enterprise in the deploy. <br>
     * Values
     * <ul>
     * <li><b>OFF</b></li>
     * <li><b>ON</b></li>
     * </ul>
     */
    private Boolean forceEnterpriseSoftLimits;

    /**
     * Force the soft limits defined for the enterprise in the deploy. <br>
     * Values
     * <ul>
     * <li><b>OFF</b></li>
     * <li><b>ON</b></li>
     * </ul>
     * 
     * @return <ul>
     *         <li><b>OFF</b></li>
     *         <li><b>ON</b></li>
     *         </ul>
     */
    public Boolean isForceEnterpriseSoftLimits()
    {
        return forceEnterpriseSoftLimits == null ? false : forceEnterpriseSoftLimits;
    }

    /**
     * Force the soft limits defined for the enterprise in the deploy. <br>
     * Values
     * <ul>
     * <li><b>OFF</b></li>
     * <li><b>ON</b></li>
     * </ul>
     * 
     * @param power <ul>
     *            <li><b>OFF</b></li>
     *            <li><b>ON</b></li>
     *            </ul>
     *            void
     */
    public void setForceEnterpriseSoftLimits(final Boolean forceEnterpriseSoftLimits)
    {
        this.forceEnterpriseSoftLimits = forceEnterpriseSoftLimits;
    }
}
