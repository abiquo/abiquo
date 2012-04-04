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

import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDAO;
import com.abiquo.server.core.pricing.PricingTemplate;

@Repository
public class EnterpriseRep extends DefaultRepBase
{

    /* package: test only */static final String BUG_INSERT_NAME_MUST_BE_UNIQUE =
        "ASSERT- insert: enterprise name must be unique";

    /* package: test only */static final String BUG_UPDATE_NAME_MUST_BE_UNIQUE =
        "ASSERT- update: enterprise name must be unique";

    @Autowired
    private EnterpriseDAO enterpriseDAO;

    @Autowired
    private PrivilegeDAO privilegeDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private RoleLdapDAO roleLdapDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private MachineDAO machineDAO;

    @Autowired
    private EnterprisePropertiesDAO enterprisePropertiesDAO;

    @Autowired
    private DatacenterLimitsDAO limitsDAO;

    @Autowired
    private OneTimeTokenSessionDAO ottSessionDAO;

    public EnterpriseRep()
    {

    }

    public EnterpriseRep(final EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = entityManager;
        this.enterpriseDAO = new EnterpriseDAO(entityManager);
        userDAO = new UserDAO(entityManager);
        roleDAO = new RoleDAO(entityManager);
        ottSessionDAO = new OneTimeTokenSessionDAO(entityManager);
        privilegeDAO = new PrivilegeDAO(entityManager);
        roleLdapDAO = new RoleLdapDAO(entityManager);
    }

    public void insert(final Enterprise enterprise)
    {
        assert enterprise != null;
        assert !enterpriseDAO.isManaged(enterprise);
        assert !enterpriseDAO.existsAnyWithName(enterprise.getName()) : BUG_INSERT_NAME_MUST_BE_UNIQUE;

        enterpriseDAO.persist(enterprise);
        enterpriseDAO.flush();
    }

    public void update(final Enterprise enterprise)
    {
        assert enterprise != null;
        assert enterpriseDAO.isManaged(enterprise);
        assert !enterpriseDAO.existsAnyOtherWithName(enterprise, enterprise.getName()) : BUG_UPDATE_NAME_MUST_BE_UNIQUE;

        enterpriseDAO.flush();
    }

    public EnterpriseProperties findPropertiesByEnterprise(final Enterprise enterprise)
    {
        return enterprisePropertiesDAO.findByEnterprise(enterprise);
    }

    public void updateEnterpriseProperties(final EnterpriseProperties enterpriseProperties)
    {
        enterprisePropertiesDAO.flush();
    }

    public void removeEnterpriseProperties(final EnterpriseProperties ep)
    {
        enterprisePropertiesDAO.remove(ep);
    }

    public void createEnterpriseProperties(final Enterprise enterprise)
    {
        enterprisePropertiesDAO.persist(new EnterpriseProperties(enterprise));
    }

    public RoleLdap findLdapRoleByType(final String type)
    {
        return roleLdapDAO.findByType(type);
    }

    public Enterprise findById(final Integer id)
    {
        assert id != null;

        return this.enterpriseDAO.findById(id);
    }

    public List<Enterprise> findAll()
    {
        return this.enterpriseDAO.findAll();
    }

    public List<Enterprise> findAll(final Integer startwith, final Integer numResults)
    {
        return this.enterpriseDAO.findAll(startwith, numResults);
    }

    public List<Enterprise> findByPricingTemplate(final Integer firstElem,
        final PricingTemplate pricingTempl, final boolean included, final String filterName,
        final Integer numResults, final Integer idEnterprise)
    {
        return this.enterpriseDAO.findByPricingTemplate(firstElem, pricingTempl, included,
            filterName, numResults, idEnterprise);
    }

    public List<Enterprise> findByNameAnywhere(final String name)
    {
        assert name != null;

        List<Enterprise> result = this.enterpriseDAO.findByNameAnywhere(name);
        return result;
    }

    public boolean existsAnyOtherWithName(final Enterprise enterprise, final String name)
    {
        assert enterprise != null;
        assert !StringUtils.isEmpty(name);

        return this.enterpriseDAO.existsAnyOtherWithName(enterprise, name);
    }

    public boolean existsAnyWithName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return this.enterpriseDAO.existsAnyWithName(name);
    }

    public void delete(final Enterprise enterprise)
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

    public Collection<User> findUsersByRole(final Role role)
    {
        return userDAO.findByRole(role);
    }

    public Collection<User> findUsersByEnterprise(final Enterprise enterprise)
    {
        return userDAO.findByEnterprise(enterprise);
    }

    public Collection<User> findUsersByEnterprise(final Enterprise enterprise, final String filter,
        final String order, final boolean desc, final boolean connected, final Integer page,
        final Integer numResults)
    {
        return userDAO.find(enterprise, null, filter, order, desc, connected, page, numResults);
    }

    public User findUserByEnterprise(final Integer userId, final Enterprise enterprise)
    {
        return userDAO.findByEnterprise(userId, enterprise);
    }

    public boolean existAnyUserWithNick(final String nick)
    {
        return userDAO.existAnyUserWithNick(nick);
    }

    public boolean existAnyOtherUserWithNick(final User user, final String nick)
    {
        return userDAO.existAnyOtherUserWithNick(user, nick);
    }

    public boolean existAnyUserWithRole(final Role role)
    {
        return userDAO.existAnyUserWithRole(role);
    }

    public void insertUser(final User user)
    {
        userDAO.persist(user);
    }

    public void updateUser(final User user)
    {
        userDAO.flush();
    }

    public void removeUser(final User user)
    {
        userDAO.remove(user);
    }

    public User findUserById(final Integer id)
    {
        return userDAO.findById(id);
    }

    public Role findRoleById(final Integer id)
    {
        return roleDAO.findById(id);
    }

    public Collection<Role> findAllRoles()
    {
        return roleDAO.findAll();
    }

    public Collection<Role> findRolesByEnterprise(final Enterprise enterprise, final String filter,
        final String order, final boolean desc, final Integer page, final Integer numResults)
    {
        return roleDAO.find(enterprise, filter, order, desc, page, numResults);
    }

    public Collection<Role> findRolesByEnterpriseNotNull(final Enterprise enterprise,
        final String filter, final String order, final boolean desc, final Integer page,
        final Integer numResults)
    {
        return roleDAO.find(enterprise, filter, order, desc, page, numResults, true);
    }

    public Collection<Role> findRolesByEnterpriseAndName(final Enterprise enterprise,
        final String filter, final String order, final boolean desc, final Integer page,
        final Integer numResults, final boolean discardNullEnterprises)
    {
        return roleDAO.findExactly(enterprise, filter, order, desc, page, numResults,
            discardNullEnterprises);
    }

    public Collection<Role> findRolesByEnterpriseAndName(final Enterprise enterprise,
        final String filter, final String order, final boolean desc, final Integer page,
        final Integer numResults)
    {
        return findRolesByEnterpriseAndName(enterprise, filter, order, desc, page, numResults, true);
    }

    public void insertRole(final Role role)
    {
        roleDAO.persist(role);
    }

    public void updateRole(final Role role)
    {
        roleDAO.flush();
    }

    public void deleteRole(final Role role)
    {
        roleDAO.remove(role);
    }

    public List<Privilege> findPrivilegesByRole(final Role role)
    {
        return roleDAO.findPrivilegesByIdRole(role.getId());
    }

    public Collection<Privilege> findAllPrivileges()
    {
        return privilegeDAO.findAll();
    }

    public Privilege findPrivilegeById(final Integer id)
    {
        return privilegeDAO.findById(id);
    }

    public Collection<RoleLdap> findRolesLdap(final String filter, final String order,
        final boolean desc, final Integer page, final Integer numResults)
    {
        return roleLdapDAO.find(filter, order, desc, page, numResults);
    }

    public Collection<RoleLdap> findRoleLdapByRole(final Role role)
    {
        return roleLdapDAO.findByRole(role);
    }

    public RoleLdap findRoleLdapById(final Integer id)
    {
        return roleLdapDAO.findById(id);
    }

    public List<RoleLdap> findRoleLdapByRoleLdap(final String roleLdap)
    {
        return roleLdapDAO.findByRoleLdap(roleLdap);
    }

    public void insertRoleLdap(final RoleLdap roleLdap)
    {
        roleLdapDAO.persist(roleLdap);
    }

    public void updateRoleLdap(final RoleLdap roleLdap)
    {
        roleLdapDAO.flush();
    }

    public void deleteRoleLdap(final RoleLdap roleLdap)
    {
        roleLdapDAO.remove(roleLdap);
    }

    public boolean existAnyRoleLdapWithRole(final Role role)
    {
        return roleLdapDAO.existAnyRoleLdapWithRole(role);
    }

    public DefaultEntityCurrentUsed getEnterpriseResourceUsage(final int enterpriseId)
    {
        return enterpriseDAO.getEnterpriseResourceUsage(enterpriseId);
    }

    public Enterprise findByName(final String name)
    {
        return enterpriseDAO.findUniqueByProperty(Enterprise.NAME_PROPERTY, name);
    }

    /**
     * This function does not guarantee anymore returning a single record.
     * 
     * @param nick login.
     * @return User.
     * @deprecated Since 1.8 uniqueness of users is granted by Nick + AuthType. Use
     *             {@link #getUserByAuth(String, AuthType)} instead.
     */
    @Deprecated
    public User getUserByUserName(final String nick)
    {
        return userDAO.findUniqueByProperty(User.NICK_PROPERTY, nick);
    }

    public List<Machine> findReservedMachines(final Enterprise enterprise)
    {
        return machineDAO.findReservedMachines(enterprise);
    }

    public Machine findReservedMachine(final Enterprise enterprise, final Integer machineId)
    {
        return machineDAO.findReservedMachine(enterprise, machineId);
    }

    public void reserveMachine(final Machine machine, final Enterprise enterprise)
    {
        machineDAO.reserveMachine(machine, enterprise);
    }

    public void releaseMachine(final Machine machine)
    {
        machineDAO.releaseMachine(machine);
    }

    public DatacenterLimits findLimitsByEnterpriseAndDatacenter(final Enterprise enterprise,
        final Datacenter datacenter)
    {
        return limitsDAO.findByEnterpriseAndDatacenter(enterprise, datacenter);
    }

    public DatacenterLimits findLimitsByEnterpriseAndIdentifier(final Enterprise enterprise,
        final Integer limitId)
    {
        return limitsDAO.findByEnterpriseAndIdentifier(enterprise, limitId);
    }

    public Collection<DatacenterLimits> findLimitsByEnterprise(final Enterprise enterprise)
    {
        return limitsDAO.findByEnterprise(enterprise);
    }

    public Collection<DatacenterLimits> findLimitsByDatacenter(final Datacenter datacenter)
    {
        return limitsDAO.findByDatacenter(datacenter);
    }

    public void insertLimit(final DatacenterLimits limit)
    {
        limitsDAO.persist(limit);
    }

    public void updateLimit(final DatacenterLimits limit)
    {
        limitsDAO.flush();
    }

    public void deleteLimit(final DatacenterLimits limit)
    {
        limitsDAO.remove(limit);
    }

    /**
     * Consumes the token. After a successful execution the token will be invalidated.
     * 
     * @param token token.
     * @return boolean true if there was token. False otherwise.
     */
    public boolean existOneTimeToken(final String token)
    {
        return ottSessionDAO.consumeToken(token) > 0;
    }

    /**
     * The uniqueness of users is granted by Login + AuthType.
     * 
     * @param token token to persist.
     */
    public void persistToken(final String token)
    {
        OneTimeTokenSession ottSession = new OneTimeTokenSession(token);
        ottSessionDAO.persist(ottSession);
    }

    /**
     * {@see UserDAO#getAbiquoUserByLogin(String)}
     */
    public User getAbiquoUserByUserName(final String nick)
    {
        return userDAO.getAbiquoUserByLogin(nick);
    }

    /**
     * {@see UserDAO#getUserByAuth(String, authType)}
     */
    public User getUserByAuth(final String nick, final AuthType authType)
    {
        return userDAO.getUserByAuth(nick, authType);
    }

    /**
     * The uniqueness of users is granted by Login + AuthType.
     * 
     * @param nick login.
     * @param authType an {@link AuthType} value.
     * @return boolean false if there is no other user with same nick + authType. False otherwise.
     */
    public boolean existAnyUserWithNickAndAuth(final String nick, final AuthType authType)
    {
        return userDAO.existAnyUserWithNickAndAuth(nick, authType);
    }

    public boolean existAnyEnterpriseWithPricingTemplate(final PricingTemplate pricingTemplate)
    {
        return enterpriseDAO.existAnyEnterpriseWithPricingTemplate(pricingTemplate);
    }

    public boolean isUserAllowedToUseVirtualDatacenter(final String username,
        final String authtype, final String[] privileges, final Integer idVdc)
    {
        return userDAO.isUserAllowedToUseVirtualDatacenter(username, authtype, privileges, idVdc);
    }

    public boolean isUserAllowedToEnterprise(final String username, final String authtype,
        final String[] privileges, final Integer idEnterprise)
    {
        return userDAO.isUserAllowedToEnterprise(username, authtype, privileges, idEnterprise);
    }
}
