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

/**
 * 
 */
package com.abiquo.server.core.infrastructure.network;

/**
 * Pojo class to work with to configure Virtual Machines. It is not a POJO nor a Dto object, only a
 * return object for the 'Service' layer.
 * 
 * @author jdevesa@abiquo.com
 */
public class VMNetworkConfiguration
{
    public VMNetworkConfiguration()
    {

    }

    private Integer id;

    private String gateway;

    private String primaryDNS;

    private String secondaryDNS;

    private String suffixDNS;

    private Boolean used;

    public Integer getId()
    {
        return this.id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    public void setPrimaryDNS(final String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    public void setSecondaryDNS(final String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    public String getSuffixDNS()
    {
        return suffixDNS;
    }

    public void setSuffixDNS(final String suffixDNS)
    {
        this.suffixDNS = suffixDNS;
    }

    public Boolean getUsed()
    {
        return used;
    }

    public void setUsed(final Boolean used)
    {
        this.used = used;
    }

    // HASHCODE AND EQUALS TO USE 'CONTAINS' OBJECT.
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (gateway == null ? 0 : gateway.hashCode());
        result = prime * result + (primaryDNS == null ? 0 : primaryDNS.hashCode());
        result = prime * result + (secondaryDNS == null ? 0 : secondaryDNS.hashCode());
        result = prime * result + (suffixDNS == null ? 0 : suffixDNS.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        VMNetworkConfiguration other = (VMNetworkConfiguration) obj;
        if (gateway == null)
        {
            if (other.gateway != null)
            {
                return false;
            }
        }
        else if (!gateway.equals(other.gateway))
        {
            return false;
        }
        if (primaryDNS == null)
        {
            if (other.primaryDNS != null)
            {
                return false;
            }
        }
        else if (!primaryDNS.equals(other.primaryDNS))
        {
            return false;
        }
        if (secondaryDNS == null)
        {
            if (other.secondaryDNS != null)
            {
                return false;
            }
        }
        else if (!secondaryDNS.equals(other.secondaryDNS))
        {
            return false;
        }
        if (suffixDNS == null)
        {
            if (other.suffixDNS != null)
            {
                return false;
            }
        }
        else if (!suffixDNS.equals(other.suffixDNS))
        {
            return false;
        }
        return true;
    }

}
