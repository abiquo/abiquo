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

package com.abiquo.abiserver.persistence.dao.user;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB} Exposes all the methods that
 * this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface UserDAO extends DAO<UserHB, Integer>
{

    /**
     * Get the user object giving the user name
     * 
     * @param username name of the user
     * @return UserHB object containing the result of the query
     */
    UserHB getUserByUserName(String username);

    /**
     * Gets the user email of the user with the provided name.
     * 
     * @param username name of the user
     * @param the email address of the current user
     */
    String getEmailByUserName(String username);

    /**
     * /** Return the list of users with less privileges than the user, filtering by enterprise
     * 
     * @param privileges privileges of the user
     * @return list of users
     */
    List<UserHB> getUsersByUserPrivileges(String privileges, Integer enterprise);

    /**
     * Return the list of users with the provided role (short description), filtering by enterprise
     * 
     * @param rol, short description of the rol (Public, Sys Admin, User, Enterpirse Admin)
     * @return list of users
     */
    List<UserHB> getUsersByUserRol(String rol, Integer enterprise);

    public UserHB findUserHBByName(String name);

}
