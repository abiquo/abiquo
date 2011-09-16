package com.abiquo.server.core.enterprise;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class ApprovalManagerDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String approvalMail;

    public String getApprovalMail()
    {
        return approvalMail;
    }

    public void setApprovalMail(final String approvalMail)
    {
        this.approvalMail = approvalMail;
    }

}
