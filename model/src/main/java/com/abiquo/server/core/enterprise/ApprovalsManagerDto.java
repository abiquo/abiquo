package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "approvalmanagers")
public class ApprovalsManagerDto extends WrapperDto<ApprovalManagerDto>
{
    private static final long serialVersionUID = 6167943072099251524L;

    @Override
    @XmlElement(name = "approvalmanager")
    public List<ApprovalManagerDto> getCollection()
    {
        return collection;
    }

    public void setCollection(final List<ApprovalManagerDto> approvalsmanager)
    {
        collection = approvalsmanager;
    }
}
