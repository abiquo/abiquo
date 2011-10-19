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

package com.abiquo.api.spring.security.onetimetoken.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.spring.security.onetimetoken.service.OneTimeTokenDetailsService;
import com.abiquo.api.spring.security.onetimetoken.token.OneTimeTokenToken;
import com.abiquo.server.core.enterprise.EnterpriseRep;

/**
 * This class provides a mechanism to authenticate using a OneTimeTokenToken. When a successful
 * login the token is consumed.
 * 
 * @author ssedano
 */
@Service("oneTimeTokenDetailsService")
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class OneTimeTokenDetailsServiceImpl implements OneTimeTokenDetailsService
{
    // rep
    @Autowired
    protected EnterpriseRep enterpriseRep;

    /**
     * @see com.abiquo.api.spring.security.onetimetoken.service.OneTimeTokenDetailsService#checkToken(org.springframework.security.Authentication)
     *      .
     */
    @Override
    public boolean checkToken(Authentication authentication)
    {
        try
        {
            if (supports(authentication.getClass()))
            {
                return enterpriseRep.existOneTimeToken(((OneTimeTokenToken) authentication)
                    .getToken());
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    /**
     * This class only supports OneTimeTokenToken.
     * 
     * @param authentication OneTimeTokenToken.
     * @return false is no OneTimeTokenToken.
     */
    public boolean supports(Class authentication)
    {
        return OneTimeTokenToken.class.isAssignableFrom(authentication);
    }
}
