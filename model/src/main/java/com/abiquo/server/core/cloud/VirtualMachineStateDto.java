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
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * DTO to modify state parameters of the machine.
 * 
 * @author ssedano
 */
@XmlRootElement(name = "virtualmachinestate")
public class VirtualMachineStateDto extends SingleResourceTransportDto implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 7891496375111061310L;
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.virtualmachinestate+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    /**
     * Machine state state. <br>
     * Values
     * <ul>
     * <li><b>OFF</b></li>
     * <li><b>ON</b></li>
     * <li><b>PAUSED</b></li>
     * </ul>
     */
    private VirtualMachineState state;

    /**
     * Machine state state.
     * 
     * @return *
     *         <ul>
     *         <li><b>OFF</b></li>
     *         <li><b>ON</b></li>
     *         <li><b>PAUSED</b></li>
     *         </ul>
     */
    public VirtualMachineState getState()
    {
        return state;
    }

    /**
     * Machine state state. <br>
     * Values *
     * <ul>
     * <li><b>OFF</b></li>
     * <li><b>ON</b></li>
     * <li><b>PAUSED</b></li>
     * </ul>
     * 
     * @param state *
     *            <ul>
     *            <li><b>OFF</b></li>
     *            <li><b>ON</b></li>
     *            <li><b>PAUSED</b></li>
     *            </ul>
     *            void
     */
    public void setState(final VirtualMachineState state)
    {
        this.state = state;
    }
    
    @Override
    public String getMediaType()
    {
        return VirtualMachineStateDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
