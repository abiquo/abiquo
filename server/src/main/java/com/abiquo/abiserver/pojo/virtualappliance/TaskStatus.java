/**
 * 
 */
package com.abiquo.abiserver.pojo.virtualappliance;

/**
 * @author jaume
 *
 */
public class TaskStatus
{
    private String uuid;
    
    private String statusName;
      
    private String message;

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setStatusName(String statusName)
    {
        this.statusName = statusName;
    }

    public String getStatusName()
    {
        return statusName;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getUuid()
    {
        return uuid;
    }
}
