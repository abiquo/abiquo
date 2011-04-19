package com.abiquo.commons.amqp.impl.ha.domain;

import com.abiquo.commons.amqp.domain.Queuable;
import com.abiquo.commons.amqp.util.JSONUtils;

public class HATask implements Queuable
{
    protected int datacenterId;

    protected int machineId;

    protected int virtualMachineId;

    protected int virtualApplianceId;

    public int getDatacenterId()
    {
        return datacenterId;
    }

    public void setDatacenterId(int datacenterId)
    {
        this.datacenterId = datacenterId;
    }

    public int getMachineId()
    {
        return machineId;
    }

    public void setMachineId(int machineId)
    {
        this.machineId = machineId;
    }

    public int getVirtualMachineId()
    {
        return virtualMachineId;
    }

    public void setVirtualMachineId(int virtualMachineId)
    {
        this.virtualMachineId = virtualMachineId;
    }

    public int getVirtualApplianceId()
    {
        return virtualApplianceId;
    }

    public void setVirtualApplianceId(int virtualApplianceId)
    {
        this.virtualApplianceId = virtualApplianceId;
    }

    @Override
    public byte[] toByteArray()
    {
        return JSONUtils.serialize(this);
    }

    public static HATask fromByteArray(final byte[] bytes)
    {
        return JSONUtils.deserialize(bytes, HATask.class);
    }
}
