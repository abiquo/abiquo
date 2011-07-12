  package com.abiquo.server.core.pricing;

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class CosteCodeDto extends SingleResourceTransportDto
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

      private String variable;

public String getVariable()
{
    return variable;
}

public void setVariable(String variable)
{
    this.variable = variable;
}

  }
