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

package com.abiquo.server.core.appslibrary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.abiquo.server.core.common.DefaultEntityBase;


/**
* @author apuig
 */
@Entity
@Table(name = "category")
public class Category extends DefaultEntityBase implements Serializable, PersistenceDto 
{   
    private static final long serialVersionUID = 305970249598840365L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idCategory")
    private Integer id;

    private String name;

    private Integer isErasable;
    
    private Integer isDefault;
    
    
    public Category()
    {
    }
    
    public Category(String name)
    {
        setName(name);
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getIsErasable()
    {
        return isErasable;
    }

    public void setIsErasable(Integer isErasable)
    {
        this.isErasable = isErasable;
    }

    public Integer getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault)
    {
        this.isDefault = isDefault;
    }

}
