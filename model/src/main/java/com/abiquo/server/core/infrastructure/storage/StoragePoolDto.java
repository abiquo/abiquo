package com.abiquo.server.core.infrastructure.storage;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "storagePool")
public class StoragePoolDto extends SingleResourceTransportDto
{
    
    private String idStorage;

    public String getIdStorage()
    {
        return idStorage;
    }

    public void setIdStorage(String idStorage)
    {
        this.idStorage = idStorage;
    }
    
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private long totalSizeInMb;

    public long getTotalSizeInMb()
    {
        return totalSizeInMb;
    }

    public void setTotalSizeInMb(long totalSizeInMb)
    {
        this.totalSizeInMb = totalSizeInMb;
    }

    private long usedSizeInMb;

    public long getUsedSizeInMb()
    {
        return usedSizeInMb;
    }

    public void setUsedSizeInMb(long usedSizeInMb)
    {
        this.usedSizeInMb = usedSizeInMb;
    }

    private long availableSizeInMb;

    public long getAvailableSizeInMb()
    {
        return availableSizeInMb;
    }

    public void setAvailableSizeInMb(long availableSizeInMb)
    {
        this.availableSizeInMb = availableSizeInMb;
    }

    private boolean enabled;

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
}
