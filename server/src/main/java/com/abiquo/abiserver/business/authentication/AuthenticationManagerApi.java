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

package com.abiquo.abiserver.business.authentication;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.providers.encoding.Md5PasswordEncoder;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.stub.LoginResourceStub;
import com.abiquo.abiserver.commands.stub.impl.LoginResourceStubImpl;
import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.user.EnterpriseDAO;
import com.abiquo.abiserver.persistence.dao.user.RoleDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.user.UserSessionDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;

public class AuthenticationManagerApi implements IAuthenticationManager
{
    /**
     * Abiquo API URL.
     */
    private String apiUri;

    /**
     * Factory of DAOs and transaction manager.
     */
    private DAOFactory factory;

    private final AbiConfig abiConfig = AbiConfigManager.getInstance().getAbiConfig();

    private static final ResourceManager resourceManger =
        new ResourceManager(AuthenticationManagerDB.class);

    private final ErrorManager errorManager = ErrorManager
        .getInstance(AbiCloudConstants.ERROR_PREFIX);

    public AuthenticationManagerApi()
    {
        if (factory == null)
        {
            factory = HibernateDAOFactory.instance();
        }
    }

    /**
     * @see com.abiquo.abiserver.business.authentication.IAuthenticationManager#checkSession(com.abiquo.abiserver.pojo.authentication.UserSession)
     */
    @Override
    public BasicResult checkSession(final UserSession userSession)
    {
        BasicResult checkSessionResult = new BasicResult();
        getFactory().beginConnection();

        UserSession sessionToCheck = null;

        try
        {

            sessionToCheck =
                getUserSessionDAO().getCurrentUserSession(userSession.getUser(),
                    userSession.getKey());

            if (sessionToCheck == null)
            {
                // The session does not exist, so is not valid
                checkSessionResult.setResultCode(BasicResult.SESSION_INVALID);
                errorManager
                    .reportError(resourceManger, checkSessionResult, "checkSession.invalid");
            }
            else
            {
                // Checking if the session has expired
                Date currentDate = new Date();
                if (currentDate.before(sessionToCheck.getExpireDate()))
                {
                    extendSession(sessionToCheck);

                    checkSessionResult.setSuccess(true);
                    checkSessionResult.setMessage(AuthenticationManagerApi.resourceManger
                        .getMessage("checkSession.success"));
                }
                else
                {
                    // The session has time out. Deleting the session from Data Base
                    getUserSessionDAO().makeTransient(sessionToCheck);

                    checkSessionResult.setResultCode(BasicResult.SESSION_TIMEOUT);
                    errorManager.reportError(resourceManger, checkSessionResult,
                        "checkSession.expired");
                }

            }

        }
        catch (Exception e)
        {
            if (getFactory().isTransactionActive())
            {
                getFactory().rollbackConnection();
            }
            errorManager.reportError(resourceManger, checkSessionResult, "checkSession.exception",
                e);
        }
        finally
        {
            getFactory().endConnection();
        }

        return checkSessionResult;
    }

    /**
     * Authentication against the Abiquo API needs a Basic Authentication (Plain text).
     * 
     * @param login credentials.
     * @return BasicAuthSecurityHandler ready to be placed in the chain.
     */
    private BasicAuthSecurityHandler createAuthenticationToken(final Login login)
    {
        BasicAuthSecurityHandler basicAuthHandler = new BasicAuthSecurityHandler();
        basicAuthHandler.setUserName(login.getUser());

        basicAuthHandler.setPassword(login.getPassword());
        return basicAuthHandler;
    }

    /**
     * Authentication against DB expects a Md5 hash.
     * 
     * @param login credentials.
     * @return Md5 hash of the credentials.
     */
    private String createMd5encodedPassword(final Login login)
    {
        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        String passwordHash = encoder.encodePassword(login.getPassword(), null);
        return passwordHash;
    }

    /**
     * Creates the usersession data.
     * 
     * @param userHB DB user.
     * @return UserSession logged user.
     */
    private UserSession createUserSession(final UserHB userHB)
    {
        UserSession userSession = new UserSession();
        userSession.setUser(userHB.getUser());
        userSession.setKey(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        userSession.setLocale(userHB.getLocale());

        userSession.setUserIdDb(userHB.getIdUser());
        userSession.setEnterpriseName(userHB.getEnterpriseHB().getName());

        int sessionTimeout = abiConfig.getSessionTimeout();
        long expireMilis = new Date().getTime() + sessionTimeout * 60 * 1000;
        Date expireDate = new Date(expireMilis);
        userSession.setExpireDate(expireDate);

        userSession.setAuthType(userHB.getAuthType());

        // Saving in Data Base the created User Session
        getUserSessionDAO().makePersistent(userSession);
        return userSession;
    }

    /**
     * Delete old sessions from DB.
     * 
     * @param userHB
     * @param dataResult void
     */
    private void deleteOldSessions(final UserHB userHB, final DataResult<LoginResult> dataResult)
    {
        // Looking for all existing active sessions of this user, ordered by when were
        getUserSessionDAO().deleteUserSessionsOlderThan(userHB.getName(), new Date());
    }

    /**
     * This function should be deprecated since is an ad-hoc implementation (mostly a hack) to
     * delegates the login to the Abiquo API.
     * 
     * @see com.abiquo.abiserver.business.authentication.IAuthenticationManager#doLogin(com.abiquo.abiserver.pojo.authentication.Login)
     */
    @Override
    public DataResult<LoginResult> doLogin(final Login login)

    {
        DataResult<LoginResult> dataResult = new DataResult<LoginResult>();
        UserDto userDto = null;
        try
        {
            if (StringUtils.isBlank(login.getAuthToken()))
            {

                BasicAuthSecurityHandler basicAuthHandler = createAuthenticationToken(login);
                // We perform this call to a secure location. If success then the credentials are
                // valid
                DataResult<UserDto> dataResultDto = apiLoginCall(login, basicAuthHandler);
                userDto = dataResultDto.getData();
                // Old DB login needs a Md5 password
                String passwordHash = createMd5encodedPassword(login);
                basicAuthHandler.setPassword(passwordHash);
                login.setPassword(passwordHash);
            }

            dataResult = login(login, userDto);
        }

        catch (BadCredentialsException e)
        {
            if (getFactory().isTransactionActive())
            {
                getFactory().rollbackConnection();
            }
            errorManager.reportError(resourceManger, dataResult, "doLogin.passwordUserIncorrect");

            dataResult.setResultCode(BasicResult.USER_INVALID);
            throw e;
        }
        catch (Exception e)
        {
            if (getFactory().isTransactionActive())
            {
                getFactory().rollbackConnection();
            }
            errorManager.reportError(resourceManger, dataResult, "doLogin.passwordUserIncorrect");

            dataResult.setResultCode(BasicResult.USER_INVALID);
        }

        return dataResult;
    }

    /**
     * @param login login.
     * @param basicAuthHandler handler.
     * @return DataResult<UserDto>
     */
    private DataResult<UserDto> apiLoginCall(final Login login,
        final BasicAuthSecurityHandler basicAuthHandler)
    {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.handlers(basicAuthHandler);
        LoginResourceStub proxy = getLoginStubProxy();
        DataResult<UserDto> dataResultDto =
            proxy.getUserByName(login.getUser(), login.getPassword(), basicAuthHandler);
        return dataResultDto;
    }

    private DataResult<LoginResult> login(final Login login, final UserDto userDto)
        throws Exception
    {
        DataResult<LoginResult> dataResult;
        UserHB userHB;
        userHB = getUserToPersistSession(login, userDto);
        dataResult = persistLogin(userHB);
        return dataResult;
    }

    /**
     * @see com.abiquo.abiserver.business.authentication.IAuthenticationManager#doLogout(com.abiquo.abiserver.pojo.authentication.UserSession)
     */
    @Override
    public BasicResult doLogout(final UserSession userSession)
    {
        BasicResult basicResult = new BasicResult();

        getFactory().beginConnection();

        try
        {
            getUserSessionDAO().deleteAllUserSessions(userSession.getUser(), userSession.getKey());

            basicResult.setSuccess(true);
            basicResult.setMessage(AuthenticationManagerApi.resourceManger
                .getMessage("doLogout.success"));
            getFactory().endConnection();
        }
        catch (Exception e)
        {
            if (getFactory().isTransactionActive())
            {
                getFactory().rollbackConnection();
            }

            errorManager.reportError(resourceManger, basicResult, "doLogout", e);
        }
        finally
        {
            getFactory().endConnection();
        }

        return basicResult;
    }

    /**
     * Extends current session by a time configured.
     * 
     * @param sessionToCheck current session void.
     */
    private void extendSession(final UserSession sessionToCheck)
    {
        // The session is valid updating the expire Date
        int sessionTimeout = abiConfig.getSessionTimeout();
        long expireMilis = new Date().getTime() + sessionTimeout * 60 * 1000;
        Date expireDate = new Date(expireMilis);
        sessionToCheck.setExpireDate(expireDate);

        // Although in the manual doesnt says so, this method does saveOrUpdate.
        getUserSessionDAO().makePersistent(sessionToCheck);

    }

    /**
     * @see com.abiquo.abiserver.business.authentication.IAuthenticationManager#findAllSessions(java.lang.String)
     */
    @Override
    public List<UserSession> findAllSessions(final String username)
    {
        List<UserSession> sessions = null;

        try
        {
            sessions = getUserSessionDAO().findByProperty("user", username);

        }
        catch (Exception ex)
        {

            BasicResult checkSessionResult = new BasicResult();
            checkSessionResult.setSuccess(false);

            errorManager.reportError(resourceManger, checkSessionResult, "checkSession.exception",
                ex);
        }

        return sessions;
    }

    /**
     * Generates the result. This function is provided for encapsulation purposes.
     * 
     * @param dataResult
     * @param loginResult void
     */
    private void generate(final DataResult<LoginResult> dataResult, final LoginResult loginResult)
    {
        // Generating the DataResult
        dataResult.setSuccess(true);
        dataResult
            .setMessage(AuthenticationManagerApi.resourceManger.getMessage("doLogin.success"));
        dataResult.setData(loginResult);
    }

    /**
     * Generates the login.
     * 
     * @param userHB DB user.
     * @param userSession logged user.
     * @return LoginResult pojo containing the data to login.
     */
    private LoginResult generateLoginResult(final UserHB userHB, final UserSession userSession)
    {
        // Generating the login result, with the user who has logged in and his session
        LoginResult loginResult = new LoginResult();
        loginResult.setSession(userSession);
        loginResult.setUser(userHB.toPojo());
        return loginResult;
    }

    /**
     * If unset, sets the URI.
     * 
     * @return Abiquo API URL.
     */
    public String getApiUri()
    {
        if (apiUri == null)
        {
            apiUri = abiConfig.getApiLocation();
        }
        return apiUri;
    }

    public EnterpriseDAO getEnterpriseDAO()
    {
        return getFactory().getEnterpriseDAO();
    }

    /**
     * Retrieves the gateway to the api, focused on Enterprise resource. This method instantiates a
     * new LoginResourceStubImpl
     * 
     * @return EnterprisesResourceStub.
     */
    protected LoginResourceStub getLoginStubProxy()
    {
        LoginResourceStub proxy = new LoginResourceStubImpl();
        return proxy;
    }

    public DAOFactory getFactory()
    {
        if (factory == null)
        {
            factory = HibernateDAOFactory.instance();
        }
        return factory;
    }

    // DAO's
    public RoleDAO getRoleDAO()
    {
        return getFactory().getRoleDAO();
    }

    public UserDAO getUserHBDao()
    {
        return getFactory().getUserDAO();
    }

    public UserSessionDAO getUserSessionDAO()
    {
        return getFactory().getUserSessionDAO();
    }

    // DAO's
    /**
     * Searches at DB for the user.
     * 
     * @param login login data.
     * @param userDto pojo that contains data from the current user.
     * @return DB user.
     * @throws Exception UserHB.
     */
    private UserHB getUserToPersistSession(final Login login, final UserDto userDto)
        throws Exception
    {
        // Get the user from the appropiate source
        UserHB userHB = null;
        getFactory().beginConnection();
        if (StringUtils.isBlank(login.getAuthToken()))
        {
            userHB = userDtoToUserHB(userDto); // getUser(login, session);
        }
        else
        {
            userHB = getUserUsingToken(login);
        }
        getFactory().endConnection();
        return userHB;
    }

    /**
     * Gets the user from DB using the authentication Token information.
     * 
     * @param login The object with the token information.
     * @param session The Hibernate session.
     * @return The user.
     */
    private UserHB getUserUsingToken(final Login login) throws Exception
    {
        // Get token data
        String[] token = TokenUtils.getTokenFields(login.getAuthToken());
        String tokenUser = TokenUtils.getTokenUser(token);
        long tokenExpiration = TokenUtils.getTokenExpiration(token);
        String tokenSignature = TokenUtils.getTokenSignature(token);

        // Check token expiration
        if (TokenUtils.isTokenExpired(tokenExpiration))
        {
            throw new Exception("Authentication token has expired.");
        }

        // Get the token user from db
        UserHB userHB = getUserHBDao().findUniqueByProperty("user", tokenUser);

        if (userHB != null)
        {
            // Validate credentials with the token
            String signature =
                TokenUtils.makeTokenSignature(tokenExpiration, userHB.getUser(),
                    userHB.getPassword())
                    + userHB.getAuthType();

            if (!signature.equals(tokenSignature))
            {
                return null;
            }
            userHB.setEnterpriseHB(getFactory().getEnterpriseDAO().findById(
                userHB.getEnterpriseHB().getIdEnterprise()));
        }

        return userHB;
    }

    /**
     * @see com.abiquo.abiserver.business.authentication.IAuthenticationManager#isLoggedIn(java.lang.String)
     */
    @Override
    public boolean isLoggedIn(final String username)
    {
        List<UserSession> sessions = findAllSessions(username);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * Stores the information we need to keep.
     * 
     * @param userHB user.
     * @return DataResult<LoginResult>
     */
    public DataResult<LoginResult> persistLogin(final UserHB userHB)
    {
        DataResult<LoginResult> dataResult = new DataResult<LoginResult>();
        getFactory().beginConnection();
        try
        {

            if (userHB != null)
            {
                // User exists. Check if it is active
                if (userHB.getActive() == 1)
                {
                    // User exists in database and is active.

                    deleteOldSessions(userHB, dataResult);

                    // Creating the user session
                    UserSession userSession = createUserSession(userHB);

                    LoginResult loginResult = generateLoginResult(userHB, userSession);

                    generate(dataResult, loginResult);
                }
                else
                {
                    // User is not active. Generating the DataResult
                    errorManager.reportError(resourceManger, dataResult, "doLogin.userInActive");
                }
            }
            else
            {
                // User not exists in database or bad credentials. Generating the DataResult
                errorManager.reportError(resourceManger, dataResult,
                    "doLogin.passwordUserIncorrect");
                dataResult.setResultCode(BasicResult.USER_INVALID);
            }
        }
        catch (Exception e)
        {
            if (getFactory().isTransactionActive())
            {
                getFactory().rollbackConnection();
            }

            errorManager.reportError(resourceManger, dataResult, "doLogin.exception", e);
        }
        getFactory().endConnection();
        return dataResult;
    }

    /**
     * Parses a dto to a db info pojo. It does not access to db. Just a parser between pojos.
     * 
     * @param userDto user dto returned from the API.
     * @return db user, representation of an Hibernate pojo user.
     */
    private UserHB userDtoToUserHB(final UserDto userDto)
    {
        UserHB userHB = new UserHB();
        userHB.setActive(userDto.isActive() ? 1 : 0);
        userHB.setAvailableVirtualDatacenters(userDto.getAvailableVirtualDatacenters());
        userHB.setDescription(userDto.getDescription());
        userHB.setEmail(userDto.getEmail());
        userHB.setIdUser(userDto.getId());
        userHB.setLocale(userDto.getLocale());
        userHB.setName(userDto.getName());
        userHB.setPassword(userDto.getPassword());
        userHB.setSurname(userDto.getSurname());
        userHB.setUser(userDto.getNick());
        userHB.setAuthType(userDto.getAuthType());
        EnterpriseHB enterpriseHB = getEnterpriseDAO().findById(userDto.getIdEnterprise());

        RoleHB roleHB = getRoleDAO().findById(userDto.getIdRole());

        userHB.setEnterpriseHB(enterpriseHB);
        userHB.setRoleHB(roleHB);

        return userHB;
    }

}
