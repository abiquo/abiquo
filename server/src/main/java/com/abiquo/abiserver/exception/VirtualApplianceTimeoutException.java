package com.abiquo.abiserver.exception;

/**
 * Exception thrown when the communication with the Virtual factory times out.
 * 
 * @author Ignasi Barrera
 */
public class VirtualApplianceTimeoutException extends VirtualApplianceFaultException
{
    private static final long serialVersionUID = 1L;

    public VirtualApplianceTimeoutException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public VirtualApplianceTimeoutException(final String message)
    {
        super(message);
    }

    public VirtualApplianceTimeoutException(final Throwable cause)
    {
        super(cause);
    }

}
