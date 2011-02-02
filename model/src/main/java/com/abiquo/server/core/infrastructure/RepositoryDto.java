/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

  package com.abiquo.server.core.infrastructure;

  import javax.xml.bind.annotation.XmlRootElement;

  import com.abiquo.model.transport.SingleResourceTransportDto;

  @XmlRootElement(name = "")
  public class RepositoryDto extends SingleResourceTransportDto
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

private long repositoryRemainingMb;

public long getRepositoryRemainingMb()
{
    return repositoryRemainingMb;
}

public void setRepositoryRemainingMb(long repositoryRemainingMb)
{
    this.repositoryRemainingMb = repositoryRemainingMb;
}

private String url;

public String getUrl()
{
    return url;
}

public void setUrl(String url)
{
    this.url = url;
}

private long repositoryCapacityMb;

public long getRepositoryCapacityMb()
{
    return repositoryCapacityMb;
}

public void setRepositoryCapacityMb(long repositoryCapacityMb)
{
    this.repositoryCapacityMb = repositoryCapacityMb;
}

  }
