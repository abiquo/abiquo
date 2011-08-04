/**
 * 
 */
package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * Represents a collection of {@link VMNetworkConfigurationDto}.
 * 
 * @author jdevesa
 */
@XmlRootElement(name = "vmnetworkconfigurations")
public class VMNetworkConfigurationsDto extends WrapperDto<VMNetworkConfigurationDto>
{

    @Override
    @XmlElement(name = "vmnetworkconfiguration")
    public List<VMNetworkConfigurationDto> getCollection()
    {
        return collection;
    }

}
