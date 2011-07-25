package com.abiquo.server.core.infrastructure.network;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.VlanTagAvailabilityType;
import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Set the transfer object for the type VlanTagAvialiabilityType
 * 
 * @author jdevesa
 */
@XmlRootElement(name = "vlanTagAvailability")
public class VlanTagAvailabilityDto extends SingleResourceTransportDto implements Serializable
{
    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = 8354795972402115520L;

    private VlanTagAvailabilityType available;

    private String message;

    public VlanTagAvailabilityType getAvailable()
    {
        return available;
    }

    public void setAvailable(final VlanTagAvailabilityType available)
    {
        this.available = available;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }
}
