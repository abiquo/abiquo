/**
 * Abiquo premium edition
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

package com.abiquo.abiserver.pojo.pricing;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.pricing.CostCodeHB;
import com.abiquo.abiserver.pojo.IPojo;

public class CostCode implements IPojo<CostCodeHB>, Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    
    private String variable;
    

    
    
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }


    public String getVariable()
    {
        return variable;
    }

    public void setVariable(String variable)
    {
        this.variable = variable;
    }

    public CostCodeHB toPojoHB(){
        
        CostCodeHB costCodeHB = new CostCodeHB();
        costCodeHB.setId(id);
        costCodeHB.setVariable(variable);
        
        return costCodeHB;
    }

}
