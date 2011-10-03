package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "datacenterRepositories")
public class DatacenterRepositoriesDto extends WrapperDto<DatacenterRepositoryDto>
{
    @XmlElement(name = "datacenterRepository")
    public List<DatacenterRepositoryDto> getCollection()
    {
        return collection;
    }
}
