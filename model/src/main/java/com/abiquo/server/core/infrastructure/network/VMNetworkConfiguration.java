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
