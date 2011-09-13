package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

@XmlRootElement(name = "approvals")
public class ApprovalsDto extends WrapperDto<ApprovalDto>
{
    private static final long serialVersionUID = 6167943072099251524L;

    @Override
    @XmlElement(name = "approval")
    public List<ApprovalDto> getCollection()
    {
        return collection;
    }

    public void setCollection(final List<ApprovalDto> approvals)
    {
        collection = approvals;
    }
}
