/**
 * 
 */
package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * @author jdevesa
 */
@XmlRootElement(name = "nics")
public class NicsDto extends WrapperDto<NicDto>
{
    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = 7750745888159232062L;

    @Override
    @XmlElement(name = "nic")
    public List<NicDto> getCollection()
    {
        return collection;
    }
}
