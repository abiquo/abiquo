/**
 * 
 */
package com.abiquo.abiserver.pojo.virtualhardware;

/**
 * @author jdevesa
 */
public class Disk
{
    private Long diskSizeInMb;

    private Boolean readOnly;

    public Long getDiskSizeInMb()
    {
        return diskSizeInMb;
    }

    public void setDiskSizeInMb(final Long diskSizeInMb)
    {
        this.diskSizeInMb = diskSizeInMb;
    }

    public Boolean getReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly(final Boolean readOnly)
    {
        this.readOnly = readOnly;
    }

}
