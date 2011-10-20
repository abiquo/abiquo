/**
 * 
 */
package com.abiquo.server.core.infrastructure.storage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * @author jdevesa@abiquo.com
 */
@XmlRootElement(name = "disks")
public class DisksManagementDto extends WrapperDto<DiskManagementDto>
{
    @Override
    @XmlElement(name = "disk")
    public List<DiskManagementDto> getCollection()
    {
        if (collection == null)
        {
            collection = new ArrayList<DiskManagementDto>();
        }
        return collection;
    }
}
