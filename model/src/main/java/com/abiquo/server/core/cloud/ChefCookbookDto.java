package com.abiquo.server.core.cloud;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "chefCookbook")
public class ChefCookbookDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String cookbook;

    public String getCookbook()
    {
        return cookbook;
    }

    public void setCookbook(String cookbook)
    {
        this.cookbook = cookbook;
    }

}
