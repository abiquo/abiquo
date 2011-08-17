  package com.abiquo.server.core.appslibrary;

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class CategoryDto extends SingleResourceTransportDto
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

      private String name;

public String getName()
{
    return name;
}

public void setName(String name)
{
    this.name = name;
}

private int isDefault;

public int getIsDefault()
{
    return isDefault;
}

public void setIsDefault(int isDefault)
{
    this.isDefault = isDefault;
}

private int isErasable;

public int getIsErasable()
{
    return isErasable;
}

public void setIsErasable(int isErasable)
{
    this.isErasable = isErasable;
}

  }
