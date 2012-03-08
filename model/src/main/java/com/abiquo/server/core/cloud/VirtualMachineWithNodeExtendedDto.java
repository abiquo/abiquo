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

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "virtualmachinewithnodeextended")
public class VirtualMachineWithNodeExtendedDto extends VirtualMachineWithNodeDto
{
    private static final long serialVersionUID = -4124890793051402691L;
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.virtualmachinewithnodeextended+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private String userName;

    private String userSurname;

    private String enterpriseName;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(final String userName)
    {
        this.userName = userName;
    }

    public String getUserSurname()
    {
        return userSurname;
    }

    public void setUserSurname(final String userSurname)
    {
        this.userSurname = userSurname;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(final String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    public VirtualMachineWithNodeExtendedDto()
    {
    }

    public VirtualMachineWithNodeExtendedDto(final VirtualMachineWithNodeDto dto,
        final String userName, final String userSurname, final String enterpriseName)
    {
        this.setLinks(dto.getLinks());
        this.setId(dto.getId());
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setRam(dto.getRam());
        this.setCpu(dto.getCpu());
        this.setHdInBytes(dto.getHdInBytes());
        this.setVdrpPort(dto.getVdrpPort());
        this.setVdrpIP(dto.getVdrpIP());
        this.setIdState(dto.getIdState());
        this.setState(dto.getState());
        this.setHighDisponibility(dto.getHighDisponibility());
        this.setIdType(dto.getIdType());
        this.setPassword(dto.getPassword());
        this.setUuid(dto.getUuid());
        this.setRunlist(dto.getRunlist());
        this.setNodeId(dto.getNodeId());
        this.setNodeName(dto.getNodeName());
        this.setX(dto.getX());
        this.setY(dto.getY());
        this.setUserName(userName);
        this.setUserSurname(userSurname);
        this.setEnterpriseName(enterpriseName);
    }
    
    @Override
    public String getMediaType()
    {
        return VirtualMachineWithNodeExtendedDto.MEDIA_TYPE;
    }
}
