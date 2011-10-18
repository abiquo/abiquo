package com.abiquo.server.core.cloud;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class VirtualImageConversionDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private DiskFormatType sourceType;

    public DiskFormatType getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(final DiskFormatType sourceType)
    {
        this.sourceType = sourceType;
    }

    private DiskFormatType targetType;

    public DiskFormatType getTargetType()
    {
        return targetType;
    }

    public void setTargetType(final DiskFormatType targetType)
    {
        this.targetType = targetType;
    }

    private String sourcePath;

    public String getSourcePath()
    {
        return sourcePath;
    }

    public void setSourcePath(final String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    private String targetPath;

    public String getTargetPath()
    {
        return targetPath;
    }

    public void setTargetPath(final String targetPath)
    {
        this.targetPath = targetPath;
    }

    private ConversionState state;

    public ConversionState getState()
    {
        return state;
    }

    public void setState(final ConversionState state)
    {
        this.state = state;
    }

    private long size;

    public long getSize()
    {
        return size;
    }

    public void setSize(final long size)
    {
        this.size = size;
    }

}
