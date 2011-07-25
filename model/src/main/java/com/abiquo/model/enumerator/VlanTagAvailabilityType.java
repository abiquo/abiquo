/**
 * 
 */
package com.abiquo.model.enumerator;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Enumerator to specify all the states of a VlanTag
 * 
 * @author jdevesa@abiquo.com
 */
@XmlEnum
public enum VlanTagAvailabilityType
{
    AVAILABLE("This tag is available."), USED("This tag is used by another VLAN in the Datacenter"), INVALID(
        "VLAN tag out of limits");

    private String message;

    /**
     * Private Constructor with a default message;
     * 
     * @param message
     */
    private VlanTagAvailabilityType(final String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
