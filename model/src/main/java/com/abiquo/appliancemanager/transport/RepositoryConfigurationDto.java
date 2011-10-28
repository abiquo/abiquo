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

package com.abiquo.appliancemanager.transport;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement
@XmlType(name = "repositoryConfiguration")
public class RepositoryConfigurationDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -912929254475352163L;

    /**
     * {@link abiquo.appliancemanager.repositoryLocation} property in the datacenter remote services
     * configuration. Remote repository NFS exported location ('nsf-devel:/opt/vm_repository')
     */
    protected String repositoryLocation;

    public String getRepositoryLocation()
    {
        return repositoryLocation;
    }

    public void setRepositoryLocation(String repositoryLocation)
    {
        this.repositoryLocation = repositoryLocation;
    }

}
