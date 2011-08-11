package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "usersWithRoles")
public class UsersWithRolesDto extends WrapperDto<UserWithRoleDto>
{
    @Override
    @XmlElement(name = "userWithRole")
    public List<UserWithRoleDto> getCollection()
    {
        return collection;
    }
}
