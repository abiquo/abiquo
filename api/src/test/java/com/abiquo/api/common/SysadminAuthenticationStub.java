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
package com.abiquo.api.common;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;

/**
 * Stub to inject an authenticated user called `sysadmin` for service tests.
 * 
 * @author dcalavera
 */
public class SysadminAuthenticationStub implements Authentication
{

    @Override
    public String getName()
    {
        return "sysadmin";
    }

    @Override
    public boolean isAuthenticated()
    {
        return true;
    }

    @Override
    public GrantedAuthority[] getAuthorities()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getCredentials()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDetails()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAuthenticated(boolean arg0) throws IllegalArgumentException
    {

    }
}
