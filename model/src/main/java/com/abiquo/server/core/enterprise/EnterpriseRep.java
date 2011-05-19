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

package com.abiquo.server.core.enterprise;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.cloud.VirtualImageDAO;
import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDAO;

@Repository
@Transactional
public class EnterpriseRep extends DefaultRepBase
{

    /* package: test only */static final String BUG_INSERT_NAME_MUST_BE_UNIQUE =
        "ASSERT- insert: enterprise name must be unique";

    /* package: test only */static final String BUG_UPDATE_NAME_MUST_BE_UNIQUE =
        "ASSERT- update: enterprise name must be unique";

    @Autowired
    private EnterpriseDAO enterpriseDAO;

    @Autowired
    private VirtualImageDAO virtualImageDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MachineDAO machineDAO;

    @Autowired
    private DatacenterLimitsDAO limitsDAO;

    @Autowired
    private OneTimeTokenSessionDAO ottSessionDAO;

    public EnterpriseRep()
    {

    }

    public EnterpriseRep(EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = entityManager;
        this.enterpriseDAO = new EnterpriseDAO(entityManager);
        virtualImageDAO = new VirtualImageDAO(entityManager);
        userDAO = new UserDAO(entityManager);
        roleDAO = new RoleDAO(entityManager);
        ottSessionDAO = new OneTimeTokenSessionDAO(entityManager);
    }

    public void insert(Enterprise enterprise)
    {
        assert enterprise != null;
        assert !enterpriseDAO.isManaged(enterprise);
        assert !enterpriseDAO.existsAnyWithName(enterprise.getName()) : BUG_INSERT_NAME_MUST_BE_UNIQUE;

        enterpriseDAO.persist(enterprise);
        enterpriseDAO.flush();
    }

    public void update(Enterprise enterprise)
    {
        assert enterprise != null;
        assert enterpriseDAO.isManaged(enterprise);
        assert !enterpriseDAO.existsAnyOtherWithName(enterprise, enterprise.getName()) : BUG_UPDATE_NAME_MUST_BE_UNIQUE;

        enterpriseDAO.flush();
    }

    public Enterprise findById(Integer id)
    {
        assert id != null;

        return this.enterpriseDAO.findById(id);
    }

    public List<Enterprise> findAll()
    {
        return this.enterpriseDAO.findAll();
    }

    public List<Enterprise> findAll(Integer offset, Integer numResults)
    {
        return this.enterpriseDAO.findAll(offset, numResults);
    }

    public List<Enterprise> findByNameAnywhere(String name)
    {
        assert name != null;

        List<Enterprise> result = this.enterpriseDAO.findByNameAnywhere(name);
        return result;
    }

    public boolean existsAnyOtherWithName(Enterprise enterprise, String name)
    {
        assert enterprise != null;
        assert !StringUtils.isEmpty(name);

        return this.enterpriseDAO.existsAnyOtherWithName(enterprise, name);
    }

    public boolean existsAnyWithName(String name)
    {
        assert !StringUtils.isEmpty(name);

        return this.enterpriseDAO.existsAnyWithName(name);
    }

    public void delete(Enterprise enterprise)
    {
        assert enterprise != null;
        assert enterpriseDAO.isManaged(enterprise);

        enterpriseDAO.remove(enterprise);
        enterpriseDAO.flush();
    }

    public Collection<User> findAllUsers()
    {
        return userDAO.findAll();
    }

    public Collection<User> findUsersByEnterprise(Enterprise enterprise)
    {
        return userDAO.findByEnterprise(enterprise);
    }

    public Collection<User> findUsersByEnterprise(Enterprise enterprise, String filter,
        String order, boolean desc, boolean connected, Integer page, Integer numResults)
    {
        return userDAO.find(enterprise, filter, order, desc, connected, page, numResults);
    }

    public User findUserByEnterprise(Integer userId, Enterprise enterprise)
    {
        return userDAO.findByEnterprise(userId, enterprise);
    }

    public boolean existAnyUserWithNick(String nick)
    {
        return userDAO.existAnyUserWithNick(nick);
    }

    public boolean existAnyOtherUserWithNick(User user, String nick)
    {
        return userDAO.existAnyOtherUserWithNick(user, nick);
    }

    public void insertUser(User user)
    {
        userDAO.persist(user);
    }

    public void updateUser(User user)
    {
        userDAO.flush();
    }

    public void removeUser(User user)
    {
        userDAO.remove(user);
    }

    public User findUserById(Integer id)
    {
        return userDAO.findById(id);
    }

    public Role findRoleById(Integer id)
    {
        return roleDAO.findById(id);
    }

    public Collection<Role> findAllRoles()
    {
        return roleDAO.findAll();
    }

    public void insertRole(Role role)
    {
        roleDAO.persist(role);
    }

    public void updateRole(Role role)
    {
        roleDAO.flush();
    }

    public void deleteRole(Role role)
    {
        roleDAO.remove(role);
    }

    public DefaultEntityCurrentUsed getEnterpriseResourceUsage(int enterpriseId)
    {
        return enterpriseDAO.getEnterpriseResourceUsage(enterpriseId);
    }

    public Enterprise findByName(String name)
    {
        return enterpriseDAO.findUniqueByProperty(Enterprise.NAME_PROPERTY, name);
    }

    public User getUserByUserName(String nick)
    {
        return userDAO.findUniqueByProperty(User.NICK_PROPERTY, nick);
    }

    public List<Machine> findReservedMachines(Enterprise enterprise)
    {
        return machineDAO.findReservedMachines(enterprise);
    }

    public Machine findReservedMachine(Enterprise enterprise, Integer machineId)
    {
        return machineDAO.findReservedMachine(enterprise, machineId);
    }

    public void reserveMachine(Machine machine, Enterprise enterprise)
    {
        machineDAO.reserveMachine(machine, enterprise);
    }

    public void releaseMachine(Machine machine)
    {
        machineDAO.releaseMachine(machine);
    }

    public DatacenterLimits findLimitsByEnterpriseAndDatacenter(Enterprise enterprise,
        Datacenter datacenter)
    {
        return limitsDAO.findByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    public DatacenterLimits findLimitsByEnterpriseAndIdentifier(Enterprise enterprise,
        Integer limitId)
    {
        return limitsDAO.findByEnterpriseAndIdentifier(enterprise, limitId);
    }

    public Collection<DatacenterLimits> findLimitsByEnterprise(Enterprise enterprise)
    {
        return limitsDAO.findByEnterprise(enterprise);
    }

    public Collection<DatacenterLimits> findLimitsByDatacenter(Datacenter datacenter)
    {
        return limitsDAO.findByDatacenter(datacenter);
    }

    public void insertLimit(DatacenterLimits limit)
    {
        limitsDAO.persist(limit);
    }

    public void updateLimit(DatacenterLimits limit)
    {
        limitsDAO.flush();
    }

    public void deleteLimit(DatacenterLimits limit)
    {
        limitsDAO.remove(limit);
    }
    
    /**
     * Consumes the token. After a successful execution the token will be invalidated.
     * 
     * @param token token.
     * @return boolean true if there was token. False otherwise.
     */
    public boolean existOneTimeToken(String token)
    {
        return ottSessionDAO.consumeToken(token) > 0;
    }
    
    /**
     * The uniqueness of users is granted by Login + AuthType.
     * 
     * @param token token to persist.
     */
    public void persistToken(String token)
    {
        OneTimeTokenSession ottSession = new OneTimeTokenSession(token);
        ottSessionDAO.persist(ottSession);
    }
}
