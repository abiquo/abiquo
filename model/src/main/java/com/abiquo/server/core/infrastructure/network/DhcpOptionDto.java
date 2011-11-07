  package com.abiquo.server.core.infrastructure.network;

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class DhcpOptionDto extends SingleResourceTransportDto
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

      private int option;

public int getOption()
{
    return option;
}

public void setOption(int option)
{
    this.option = option;
}

private String description;

public String getDescription()
{
    return description;
}

public void setDescription(String description)
{
    this.description = description;
}

  }
