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

package com.abiquo.abiserver.commands.stub;

import static com.abiquo.util.URIResolver.resolveURI;
import static java.lang.String.valueOf;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.ClientHandler;
import org.apache.wink.common.internal.utils.UriHelper;
import org.jclouds.abiquo.AbiquoContext;
import org.jclouds.abiquo.AbiquoContextFactory;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.AuthorizationException;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.business.authentication.TokenUtils;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.URIResolver;
import com.abiquo.util.resources.ResourceManager;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("unchecked")
public class AbstractAPIStub
{

    protected RestClient client;

    public static final String START_WITH = "startwith";

    public static final String BY = "by";

    public static final String FILTER = "has";

    public static final String LIMIT = "limit";

    public static final String ASC = "asc";

    public static final Integer DEFAULT_PAGE_LENGTH = 25;

    public static final String DEFAULT_PAGE_LENGTH_STRING = "25";

    protected final String apiUri;

    protected UserSession currentSession;

    private AbiquoContext context;

    public AbstractAPIStub()
    {
        this.apiUri = AbiConfigManager.getInstance().getAbiConfig().getApiLocation();

        // Do not follow redirects. We want to get 301 response codes
        ClientConfig restConfig = new ClientConfig();
        restConfig.followRedirects(false);
        client = new RestClient(restConfig);
    }

    public UserSession getCurrentSession()
    {
        return currentSession;
    }

    public void setCurrentSession(final UserSession currentSession)
    {
        this.currentSession = currentSession;
    }

    protected AbiquoContext getApiClient()
    {
        if (context == null)
        {
            UserHB user = getCurrentUserCredentials();
            String token = generateToken(user.getUser(), user.getPassword());

            Properties props = new Properties();
            props.put("abiquo.endpoint", apiUri);
            // Do not retry methods that fail with 5xx error codes
            props.put("jclouds.max-retries", "0");
            // Custom timeouts in ms
            // External storage operations take a while in some storage devices
            props.put("jclouds.timeouts.CloudClient.createVolume", "90000");
            props.put("jclouds.timeouts.CloudClient.updateVolume", "90000");
            props.put("jclouds.timeouts.CloudClient.replaceVolumes", "90000");
            props.put("jclouds.timeouts.CloudClient.deleteVolume", "90000");

            context =
                new AbiquoContextFactory().createContext(token,
                    ImmutableSet.<Module> of(new NullLoggingModule()), props);
        }

        return context;
    }

    protected void releaseApiClient()
    {
        if (context != null)
        {
            context.close();
            context = null;
        }
    }

    protected UserHB getCurrentUserCredentials()
    {
        DAOFactory factory = HibernateDAOFactory.instance();
        factory.beginConnection();

        Object[] credentials =
            factory.getUserDAO().getCurrentUserCredentials(currentSession.getUser(),
                currentSession.getAuthType());

        UserHB user = new UserHB();
        user.setUser((String) credentials[0]);
        user.setPassword((String) credentials[1]);
        user.setAuthType(currentSession.getAuthType());

        factory.endConnection();

        return user;
    }

    protected ClientResponse get(final String uri, final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        Resource resource;
        if (!StringUtils.isBlank(mediaType))
        {
            resource = resource(uri, user.getUser(), user.getPassword(), mediaType);
        }
        else
        {
            resource = resource(uri, user.getUser(), user.getPassword());
        }
        return resource.get();

    }

    protected ClientResponse get(final String uri, final String user, final String password,
        final String mediaType, final ClientHandler... handlers)
    {
        return resource(uri, user, password, mediaType, handlers).get();
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto)
    {
        UserHB user = getCurrentUserCredentials();
        Resource resource = resource(uri, user.getUser(), user.getPassword());
        if (dto != null)
        {
            resource.contentType(dto.getMediaType());
            resource.accept(dto.getMediaType());
        }
        return resource.post(dto);
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto,
        final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), mediaType).post(dto);
    }

    protected ClientResponse post(final String uri, final String acceptType,
        final String contentType, final SingleResourceTransportDto dto)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), acceptType).contentType(
            contentType).post(dto);
    }

    protected ClientResponse post(final String uri, final SingleResourceTransportDto dto,
        final String user, final String password)
    {
        Resource resource = resource(uri, user, password);
        if (dto != null)
        {
            resource.contentType(dto.getMediaType());
            resource.accept(dto.getMediaType());
        }
        return resource.post(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto)
    {
        UserHB user = getCurrentUserCredentials();
        Resource resource = resource(uri, user.getUser(), user.getPassword());
        if (dto != null)
        {
            resource.contentType(dto.getMediaType());
            resource.accept(dto.getMediaType());
        }
        return resource.put(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto,
        final String accept, final String content)
    {
        UserHB user = getCurrentUserCredentials();
        Resource resource = resource(uri, user.getUser(), user.getPassword());
        resource.contentType(content);
        resource.accept(accept);
        return resource.put(dto);
    }

    protected ClientResponse put(final String uri, final SingleResourceTransportDto dto,
        final String user, final String password, final String accept, final String content)
    {
        Resource resource = resource(uri, user, password);
        if (accept != null)
        {
            resource.accept(accept);
        }
        if (content != null)
        {
            resource.contentType(content);
        }

        return resource.put(dto);
    }

    protected ClientResponse delete(final String uri)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), MediaType.APPLICATION_XML)
            .delete();
    }

    protected String createLoginLink()
    {
        return URIResolver.resolveURI(apiUri, "/login", Collections.emptyMap());
    }

    protected Resource resource(final String uri, final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), mediaType);
    }

    private Resource resource(final String uri, final String user, final String password)
    {
        Resource resource = client.resource(uri).accept(MediaType.APPLICATION_XML);
        String cookieValue = generateToken(user, password);
        return resource.cookie(new Cookie("auth", cookieValue));
    }

    private Resource resource(final String uri, final String user, final String password,
        final String mediaType)
    {
        Resource resource = client.resource(uri).accept(mediaType);
        String cookieValue = generateToken(user, password);
        return resource.cookie(new Cookie("auth", cookieValue));
    }

    private String generateToken(final String user, final String password)
    {
        long tokenExpiration = System.currentTimeMillis() + 1000L * 1800;
        String signature = TokenUtils.makeTokenSignature(tokenExpiration, user, password);

        String[] tokens;
        if (this.currentSession != null && StringUtils.isNotBlank(currentSession.getAuthType()))
        {
            tokens =
                new String[] {user, valueOf(tokenExpiration), signature,
                currentSession.getAuthType()};
        }
        else
        {
            tokens =
                new String[] {user, valueOf(tokenExpiration), signature, AuthType.ABIQUO.name()};
        }

        String cookieValue = StringUtils.join(tokens, ":");
        return new String(Base64.encodeBase64(cookieValue.getBytes()));
    }

    protected UserHB getCurrentUser()
    {
        DAOFactory factory = HibernateDAOFactory.instance();
        factory.beginConnection();

        UserHB user =
            factory.getUserDAO().getUserByLoginAuth(currentSession.getUser(),
                currentSession.getAuthType());

        factory.endConnection();

        return user;
    }

    protected void populateErrors(final ClientResponse response, final BasicResult result,
        final String methodName)
    {
        populateErrors(response, result, methodName, null);
    }

    protected void populateErrors(final ClientResponse response, final BasicResult result,
        final String methodName, final String message)
    {
        result.setSuccess(false);
        if (response.getStatusCode() == 401 || response.getStatusCode() == 403)
        {
            ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX).reportError(
                new ResourceManager(BasicCommand.class), result,
                "onFaultAuthorization.noPermission", methodName);
            result.setMessage(StringUtils.isBlank(message) ? response.getMessage() : message);
            result.setResultCode(BasicResult.NOT_AUTHORIZED);
            throw new UserSessionException(result);
        }
        else if (response.getStatusCode() == 406)
        {
            ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX).reportError(
                new ResourceManager(BasicCommand.class), result,
                "onFaultAuthorization.noPermission", methodName);
            result.setMessage(StringUtils.isBlank(message) ? response.getMessage() : message);
            result.setResultCode(BasicResult.NOT_AUTHORIZED);
            throw new UserSessionException(result);
        }
        else if (response.getStatusCode() == 415)
        {
            ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX).reportError(
                new ResourceManager(BasicCommand.class), result,
                "onFaultAuthorization.noPermission", methodName);
            result.setMessage(StringUtils.isBlank(message) ? response.getMessage() : message);
            result.setResultCode(BasicResult.NOT_AUTHORIZED);
            throw new UserSessionException(result);
        }
        else
        {
            ErrorsDto errors = response.getEntity(ErrorsDto.class);
            result.setMessage(errors.toString());
            result.setErrorCode(errors.getCollection().get(0).getCode());
            if (errors.getCollection().get(0).getCode().equals("SOFT_LIMIT_EXCEEDED")
                || errors.getCollection().get(0).getCode().equals("LIMIT-2"))
            {
                result.setResultCode(BasicResult.SOFT_LIMT_EXCEEDED);
                // limit exceeded does not include the detail
                if (result.getMessage().length() < 254)
                {
                    result.setResultCode(0);
                }
            }
            else if (errors.getCollection().get(0).getCode().equals("LIMIT_EXCEEDED")
                || errors.getCollection().get(0).getCode().equals("LIMIT-1"))
            {
                result.setResultCode(BasicResult.HARD_LIMT_EXCEEDED);
                // limit exceeded does not include the detail
                if (result.getMessage().length() < 254)
                {
                    result.setResultCode(0);
                }
            }
            else if (errors.getCollection().get(0).getCode().equals("VM-44"))
            {
                result.setResultCode(BasicResult.NOT_MANAGED_VIRTUAL_IMAGE);
            }
        }
    }

    protected void populateErrors(final Exception ex, final BasicResult result,
        final String methodName, final String message)
    {
        if (ex instanceof AuthorizationException)
        {
            populateErrors((AuthorizationException) ex, result, methodName, message);
        }
        else if (ex instanceof AbiquoException)
        {
            populateErrors((AbiquoException) ex, result, methodName, message);
        }
        else if (ex instanceof UserSessionException)
        {
            throw (UserSessionException) ex;
        }
        else if (ex instanceof UndeclaredThrowableException)
        {
            UndeclaredThrowableException undeclared = (UndeclaredThrowableException) ex;
            if (undeclared.getCause() instanceof TimeoutException)
            {
                result.setSuccess(false);
                result.setMessage("Connection timed out during '" + methodName + "' invocation");
            }
        }
        else
        {
            result.setSuccess(false);
            result.setMessage(ex.getMessage());
        }
    }

    protected void populateErrors(final Exception ex, final BasicResult result,
        final String methodName)
    {
        populateErrors(ex, result, methodName, null);
    }

    protected void populateErrors(final AuthorizationException ex, final BasicResult result,
        final String methodName, final String message)
    {
        result.setSuccess(false);
        ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX).reportError(
            new ResourceManager(BasicCommand.class), result, "onFaultAuthorization.noPermission",
            methodName);
        result.setMessage(StringUtils.isBlank(message) ? ex.getMessage() : message);
        result.setResultCode(BasicResult.NOT_AUTHORIZED);
        throw new UserSessionException(result);
    }

    protected void populateErrors(final AbiquoException abiquoException, final BasicResult result,
        final String methodName, final String message)
    {

        result.setSuccess(false);
        result.setMessage(StringUtils.isBlank(message) ? abiquoException.getMessage() : message);
        result.setErrorCode(abiquoException.getErrors().get(0).getCode());

        if (abiquoException.hasError("SOFT_LIMIT_EXCEEDED"))
        {
            result.setResultCode(BasicResult.SOFT_LIMT_EXCEEDED);
            // limit exceeded does not include the detail
            if (result.getMessage().length() < 254)
            {
                result.setResultCode(0);
            }
        }
        else if (abiquoException.hasError("LIMIT_EXCEEDED"))
        {
            result.setResultCode(BasicResult.HARD_LIMT_EXCEEDED);
            // limit exceeded does not include the detail
            if (result.getMessage().length() < 254)
            {
                result.setResultCode(0);
            }
        }
    }

    protected String createEnterprisesLink(final String filter, final Integer firstElem,
        final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "admin/enterprises", Collections.emptyMap());
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (!StringUtils.isEmpty(filter))
        {
            queryParams.put(FILTER, new String[] {filter});
        }
        if (firstElem != null)
        {
            queryParams.put(START_WITH, new String[] {firstElem.toString()});
        }
        if (numResults != null)
        {
            queryParams.put(LIMIT, new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createEnterpriseLink(final int enterpriseId)
    {
        return URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}",
            Collections.singletonMap("enterprise", valueOf(enterpriseId)));
    }

    protected String createEnterpriseIPsLink(final int enterpriseId)
    {
        return URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/action/ips",
            Collections.singletonMap("enterprise", valueOf(enterpriseId)));
    }

    protected String createEnterpriseLimitsByDatacenterLink(final int enterpriseId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));

        return URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/limits", params);
    }

    protected String createEnterpriseLimitByDatacenterLink(final int enterpriseId, final int limitId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("limit", valueOf(limitId));

        return URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/limits/{limit}",
            params);
    }

    protected String createEnterpriseLimitByDatacenterVirtualAppliancesLink(final int enterpriseId,
        final int limitId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("limit", valueOf(limitId));

        return URIResolver.resolveURI(apiUri,
            "admin/enterprises/{enterprise}/limits/{limit}/action/virtualappliances", params);
    }

    protected String createExternalNetworkLink(final Integer entId, final Integer vlanId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(entId));
        params.put("externalvlan", valueOf(vlanId));

        return URIResolver.resolveURI(apiUri,
            "admin/enterprises/{enterprise}/action/externalnetworks/{externalvlan}", params);
    }

    protected String createExternalNetworkByDatacenterLink(final Integer entId,
        final Integer limitId, final Integer vlanId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(entId));
        params.put("limit", valueOf(limitId));
        params.put("externalvlan", valueOf(vlanId));

        return URIResolver
            .resolveURI(apiUri,
                "admin/enterprises/{enterprise}/limits/{limit}/externalnetworks/{externalvlan}",
                params);
    }

    protected String createExternalNetworkByDatacenterSetDefaultLink(final Integer entId,
        final Integer limitId, final Integer vlanId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(entId));
        params.put("limit", valueOf(limitId));
        params.put("externalvlan", valueOf(vlanId));

        return URIResolver
            .resolveURI(
                apiUri,
                "admin/enterprises/{enterprise}/limits/{limit}/externalnetworks/{externalvlan}/action/default",
                params);
    }

    protected String createExternalNetworksByDatacenterActionInternalDefaultLink(
        final Integer entId, final Integer limitId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(entId));
        params.put("limit", valueOf(limitId));

        return URIResolver
            .resolveURI(apiUri,
                "admin/enterprises/{enterprise}/limits/{limit}/externalnetworks/action/default",
                params);
    }

    protected String createExternalNetworkIPLink(final Integer enterpriseId,
        final Integer limitsId, final Integer networkId, final Integer ipId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId.toString());
        params.put("limit", limitsId.toString());
        params.put("network", networkId.toString());
        params.put("ip", ipId.toString());

        return resolveURI(apiUri,
            "admin/enterprises/{enterprise}/limits/{limit}/externalnetworks/{network}/ips/{ip}",
            params);
    }

    protected String getReservedMachinesUri(final Integer enterpriseId, final Integer machineId)
    {
        // String uri = createEnterpriseLink(enterpriseId);

        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("machineId", valueOf(machineId));

        return URIResolver.resolveURI(apiUri,
            "admin/enterprises/{enterprise}/reservedmachines/{machineId}", params);
    }

    protected String createRoleLink(final int roleId)
    {
        return URIResolver.resolveURI(apiUri, "admin/roles/{role}",
            Collections.singletonMap("role", valueOf(roleId)));
    }

    protected String createRolesLink()
    {
        return createRolesLink(null, null, null, null, false, null);
    }

    protected String createRolesLink(final Integer identerprise, final String filter,
        final String OrderBy, final Integer offset, final boolean asc, final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "admin/roles", Collections.emptyMap());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (identerprise != null)
        {
            queryParams.put("identerprise", new String[] {String.valueOf(identerprise)});
        }
        if (StringUtils.isNotEmpty(filter))
        {
            queryParams.put("has", new String[] {filter});
        }
        if (StringUtils.isNotEmpty(OrderBy))
        {
            queryParams.put("by", new String[] {OrderBy});
        }
        if (offset != null)
        {
            queryParams.put("startwith", new String[] {String.valueOf(offset)});
        }
        if (numResults != null)
        {
            queryParams.put("limit", new String[] {String.valueOf(numResults)});
        }
        queryParams.put("asc", new String[] {String.valueOf(asc)});

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createPrivilegeLink(final int privilegeId)
    {
        return URIResolver.resolveURI(apiUri, "config/privileges/{privilege}",
            Collections.singletonMap("privilege", valueOf(privilegeId)));
    }

    protected String createRoleActionGetPrivilegesURI(final Integer entId)
    {
        return createRoleLink(entId) + "/action/privileges";
    }

    protected String createRolesLdapLink()
    {
        return createRolesLdapLink(null, null);
    }

    protected String createRolesLdapLink(Integer offset, final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "admin/rolesldap", Collections.emptyMap());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createRoleLdapLink(final int roleLdapId)
    {
        return URIResolver.resolveURI(apiUri, "admin/rolesldap/{roleldap}",
            Collections.singletonMap("roleldap", valueOf(roleLdapId)));
    }

    protected String createUsersLink(final String enterpriseId)
    {
        return createUsersLink(enterpriseId, null, null);
    }

    protected String createUsersLink(final String enterpriseId, Integer offset,
        final Integer numResults)
    {
        String uri =
            URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/users",
                Collections.singletonMap("enterprise", enterpriseId));

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createVirtualMachineTemplatesLink(final Integer enterpriseId,
        final Integer datacenterId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("datacenterrepository", valueOf(datacenterId));

        String uri =
            URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/"
                + "datacenterrepositories/{datacenterrepository}/virtualmachinetemplates", params);

        return uri;
    }

    protected String createVirtualMachineTemplateLink(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("datacenterrepository", valueOf(datacenterId));
        params.put("virtualmachinetemplate", valueOf(virtualMachineTemplateId));

        String uri =
            URIResolver
                .resolveURI(
                    apiUri,
                    "admin/enterprises/{enterprise}/"
                        + "datacenterrepositories/{datacenterrepository}/virtualmachinetemplates/{virtualmachinetemplate}",
                    params);

        return uri;
    }

    protected String createDatacenterRepositoryLink(final Integer enterpriseId,
        final Integer datacenterId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("datacenterrepository", valueOf(datacenterId));

        String uri =
            URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/"
                + "datacenterrepositories/{datacenterrepository}", params);

        return uri;
    }

    protected String createDatacenterRepositoriesLink(final Integer enterpriseId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));

        String uri =
            URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/"
                + "datacenterrepositories", params);

        return uri;
    }

    protected String createTemplateDefinitionsLink(final String enterpriseId)
    {
        String uri =
            URIResolver.resolveURI(apiUri,
                "admin/enterprises/{enterprise}/appslib/templateDefinitions",
                Collections.singletonMap("enterprise", enterpriseId));
        return uri;
    }

    protected String createTemplateDefinitionListLink(final String enterpriseId,
        final String templateDefinitionListId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);
        params.put("templateDefinitionList", templateDefinitionListId);

        return resolveURI(
            apiUri,
            "admin/enterprises/{enterprise}/appslib/templateDefinitionLists/{templateDefinitionList}",
            params);
    }

    protected String createTemplateStateFromListLink(final String enterpriseId,
        final String templateDefinitionListId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);
        params.put("templateDefinitionList", templateDefinitionListId);

        return resolveURI(
            apiUri,
            "admin/enterprises/{enterprise}/appslib/templateDefinitionLists/{templateDefinitionList}/actions/repositoryStatus",
            params);
    }

    protected String createTemplateDefinitionListsLink(final String enterpriseId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);

        return resolveURI(apiUri, "admin/enterprises/{enterprise}/appslib/templateDefinitionLists",
            params);
    }

    protected String createTemplateDefinitionLink(final String enterpriseId,
        final String templateDefinitionId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);
        params.put("templateDefinition", templateDefinitionId);

        return resolveURI(apiUri,
            "admin/enterprises/{enterprise}/appslib/templateDefinitions/{templateDefinition}",
            params);
    }

    protected String createTemplateStateLink(final String enterpriseId,
        final String templateDefinitionId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);
        params.put("templateDefinition", templateDefinitionId);

        return resolveURI(
            apiUri,
            "admin/enterprises/{enterprise}/appslib/templateDefinitions/{templateDefinition}/actions/repositoryStatus",
            params);
    }

    protected String createTemplateDefinitionInstallLink(final String enterpriseId,
        final String templateDefinitionId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);
        params.put("templateDefinition", templateDefinitionId);

        return resolveURI(apiUri,
            "admin/enterprises/{enterprise}/appslib/templateDefinitions/{templateDefinition}/"
                + "actions/repositoryInstall", params);
    }

    protected String createTemplateDefinitionUninstallLink(final String enterpriseId,
        final String templateDefinitionId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseId);
        params.put("templateDefinition", templateDefinitionId);

        return resolveURI(apiUri,
            "admin/enterprises/{enterprise}/appslib/templateDefinitions/{templateDefinition}/"
                + "actions/repositoryUninstall", params);
    }

    protected String createDiskFormatTypeLink(final Integer diskFormatTypeId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("diskformattype", valueOf(diskFormatTypeId));

        return resolveURI(apiUri, "config/diskformattypes/{diskformattype}", params);
    }

    protected String createDiskFormatTypesLink()
    {
        Map<String, String> params = new HashMap<String, String>();

        return resolveURI(apiUri, "config/diskformattypes", params);
    }

    protected String createIconsLink(final Integer idEnterprise)
    {
        Map<String, String> params = new HashMap<String, String>();

        params.put("enterprise", idEnterprise.toString());

        return resolveURI(apiUri, "admin/enterprises/{enterprise}/icons", params);
    }

    protected String createCategoryLink(final Integer categoryId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("category", valueOf(categoryId));

        return resolveURI(apiUri, "config/categories/{category}", params);
    }

    protected String createCategoriesLink()
    {
        Map<String, String> params = new HashMap<String, String>();

        return resolveURI(apiUri, "config/categories", params);
    }

    protected String createUserLink(final int enterpriseId, final int userId)
    {
        return createUserLink(valueOf(enterpriseId), userId);
    }

    protected String createUserLink(final String enterpriseIdOrWildcard, final int userId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", enterpriseIdOrWildcard);
        params.put("user", valueOf(userId));

        return resolveURI(apiUri, "admin/enterprises/{enterprise}/users/{user}", params);
    }

    protected String createVirtualDatacentersLink()
    {
        return createVirtualDatacentersLink(null, null);
    }

    protected String createVirtualDatacentersLink(final Enterprise enterprise,
        final DataCenter datacenter)
    {
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (enterprise != null)
        {
            queryParams.put("enterprise", new String[] {valueOf(enterprise.getId())});
        }
        if (datacenter != null)
        {
            queryParams.put("datacenter", new String[] {valueOf(datacenter.getId())});
        }

        return URIResolver.resolveURI(apiUri, "cloud/virtualdatacenters",
            new HashMap<String, String>(), queryParams);
    }

    protected String createVirtualDatacenterLink(final Integer vdcId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());

        return URIResolver.resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}", params);
    }

    protected String createVirtualDatacentersFromEnterpriseLink(final Integer idEnterprise)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", idEnterprise.toString());

        return URIResolver.resolveURI(apiUri,
            "admin/enterprises/{enterprise}/action/virtualdatacenters", params);
    }

    protected String createVirtualDatacenterPrivateIPsLink(final Integer vdcId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/action/ips", params);
    }

    protected String createVirtualDatacenterPublicPurchasedIPsLink(final Integer vdcId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/publicips/purchased", params);
    }

    protected String createVirtualDatacenterPublicPurchasedIPLink(final Integer vdcId,
        final Integer ipId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("ip", ipId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/publicips/purchased/{ip}",
            params);
    }

    protected String createVirtualDatacenterPublicToPurchaseIPsLink(final Integer vdcId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/publicips/topurchase", params);
    }

    protected String createVirtualDatacenterPublicToPurchaseIPLink(final Integer vdcId,
        final Integer ipId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("ip", ipId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/publicips/topurchase/{ip}",
            params);
    }

    protected String createVirtualDatacenterActionDefaultVlan(final Integer vdcId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/action/defaultvlan", params);
    }

    protected String createVirtualAppliancesLink(final Integer vdcId)
    {
        return createVirtualDatacenterLink(vdcId) + "/virtualappliances";
    }

    protected String createVirtualApplianceLink(final Integer vdcId, final Integer vappId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vapp", vappId.toString());

        return URIResolver.resolveURI(apiUri,
            "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vapp}", params);
    }

    protected String createVirtualMachineConfigurationsLink(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vappid", vappId.toString());
        params.put("vmid", vmId.toString());

        return resolveURI(
            apiUri,
            "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappid}/virtualmachines/{vmid}/network/configurations",
            params);
    }

    protected String createVirtualMachineConfigurationLink(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vmConfigId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vappid", vappId.toString());
        params.put("vmid", vmId.toString());
        params.put("vmconfigid", vmConfigId.toString());

        return resolveURI(
            apiUri,
            "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappid}/virtualmachines/{vmid}/network/configurations/{vmconfigid}",
            params);
    }

    protected String createVirtualMachineNICsLink(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vappid", vappId.toString());
        params.put("vmid", vmId.toString());

        return resolveURI(
            apiUri,
            "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappid}/virtualmachines/{vmid}/network/nics",
            params);
    }

    protected String createInfrastructureVirtualMachineNICsLink(final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        params.put("vm", virtualMachineId.toString());

        return resolveURI(
            apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/virtualmachines/{vm}/action/nics",
            params);
    }

    protected String createVirtualMachineNICLink(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer nicOrder)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vappid", vappId.toString());
        params.put("vmid", vmId.toString());
        params.put("nicOrder", nicOrder.toString());

        return resolveURI(
            apiUri,
            "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappid}/virtualmachines/{vmid}/network/nics/{nicOrder}",
            params);
    }

    protected String createVirtualDatacenterDisksLink(final Integer vdcId)
    {

        return createVirtualDatacenterDisksLink(vdcId, null);
    }

    protected String createVirtualDatacenterDisksLink(final Integer vdcId,
        final Boolean forceSoftLimits)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());

        String uri = resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/disks", params);
        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (forceSoftLimits != null)
        {

            queryParams.put("force", new String[] {forceSoftLimits.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createVirtualDatacenterDiskLink(final Integer vdcId, final Integer diskId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("diskId", diskId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdcid}/disks/{diskId}", params);
    }

    protected String createVirtualMachineDisksLink(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {

        return createVirtualMachineDisksLink(vdcId, vappId, vmId, null);
    }

    protected String createVirtualMachineDisksLink(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Boolean forceSoftLimits)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vappid", vappId.toString());
        params.put("vmid", vmId.toString());

        String uri =
            resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappid}/virtualmachines/{vmid}/storage/disks/",
                params);
        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (forceSoftLimits != null)
        {

            queryParams.put("force", new String[] {forceSoftLimits.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createVirtualMachineDiskLink(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer diskId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdcid", vdcId.toString());
        params.put("vappid", vappId.toString());
        params.put("vmid", vmId.toString());
        params.put("diskId", diskId.toString());

        return resolveURI(
            apiUri,
            "cloud/virtualdatacenters/{vdcid}/virtualappliances/{vappid}/virtualmachines/{vmid}/storage/disks/{diskId}",
            params);
    }

    protected String createMachineLink(final PhysicalMachine machine)
    {
        Integer rackId = null;
        if (machine.getRack() != null)
        {
            rackId = machine.getRack().getId();
        }
        else
        {
            rackId = machine.getAssignedTo().getId();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", valueOf(machine.getDataCenter().getId()));
        params.put("rack", rackId.toString());
        params.put("machine", machine.getId().toString());

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}",
            params);
    }

    protected String createMachineLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}",
            params);
    }

    protected String createDatastoresLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        return createMachineLink(datacenterId, rackId, machineId) + "/datastores";
    }

    protected String createDatastoresRefreshLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        return createDatastoresLink(datacenterId, rackId, machineId) + "/action/refresh";
    }

    protected String createMachineLinkPowerOn(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/poweron", params);
    }

    protected String createMachineLinkPowerOff(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/poweroff",
            params);
    }

    protected String createMachineLinkVms(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/virtualmachines",
            params);
    }

    protected String createMachineLinkVm(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final Integer vmId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        params.put("vm", vmId.toString());

        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/virtualmachines/{vm}",
            params);
    }

    protected String createMachineLinkVmActionCapture(final Integer datacenterId,
        final Integer rackId, final Integer machineId, final Integer vmId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        params.put("vm", vmId.toString());

        return resolveURI(
            apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/virtualmachines/{vm}/action/capture",
            params);
    }

    protected String createMachineLinkCheckState(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final String ip, final String hypervisor, final String user,
        final String password, final Integer port)
    {
        boolean includeMachineId = false;
        if (machineId != null && machineId != 0)
        {
            includeMachineId = true;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        if (includeMachineId)
        {
            params.put("machine", machineId.toString());
        }
        params.put("ip", ip);
        params.put("hypervisor", hypervisor);
        params.put("user", user);
        params.put("password", password);
        params.put("port", port.toString());

        String uri = "admin/datacenters/{datacenter}/";
        if (includeMachineId)
        {
            uri += "racks/{rack}/machines/{machine}/action/checkstate?sync=true";
        }
        else
        {
            uri +=
                "action/checkmachinestate?ip={ip}&hypervisor={hypervisor}&user={user}&password={password}&port={port}";
        }

        return resolveURI(apiUri, uri, params);
    }

    protected String createMachineLinkCheckIpmi(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final String ip, final String user, final String password,
        final Integer port)
    {
        boolean includeMachineId = false;
        if (machineId != null && machineId != 0)
        {
            includeMachineId = true;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        if (includeMachineId)
        {
            params.put("machine", machineId.toString());
        }
        params.put("ip", ip);
        params.put("user", user);
        params.put("password", password);

        String uri = "admin/datacenters/{datacenter}/";
        if (includeMachineId)
        {
            uri += "racks/{rack}/machines/{machine}/action/checkipmi";
        }
        else
        {
            uri += "action/checkmachineipmi?ip={ip}&user={user}&password={password}";
            if (port != null)
            {
                params.put("port", port.toString());
                uri += "&port={port}";
            }
        }

        return resolveURI(apiUri, uri, params);
    }

    protected String createDatacenterLinkgetMachineInfo(final Integer datacenterId,
        final String ip, final String user, final String password, final String hypervisor,
        final Integer port)
    {

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("ip", ip);
        params.put("hypervisor", hypervisor);
        params.put("user", user);
        params.put("password", password);
        params.put("port", port.toString());

        String uri = "admin/datacenters/{datacenter}/";
        uri +=
            "action/discoversingle?ip={ip}&user={user}&password={password}&hypervisor={hypervisor}&port={port}";

        return resolveURI(apiUri, uri, params);
    }

    protected String createDatacenterLinkgetHypervisor(final Integer datacenterId, final String ip)
    {

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("ip", ip);

        String uri = "admin/datacenters/{datacenter}/action/hypervisor?ip={ip}";

        return resolveURI(apiUri, uri, params);
    }

    protected String createMachinesLinkMultiplePost(final Integer datacenterId, final Integer rackId)
    {

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());

        String uri = "admin/datacenters/{datacenter}/racks/{rack}/machines";

        return resolveURI(apiUri, uri, params);
    }

    protected String createRemoteServicesLink(final Integer datacenterId)
    {
        return UriHelper.appendPathToBaseUri(createDatacenterLink(datacenterId), "remoteservices");
    }

    protected String createRemoteServiceLink(final Integer datacenterId,
        final String remoteServiceType)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("remoteservice", remoteServiceType.toLowerCase().replace("_", ""));

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/remoteservices/{remoteservice}",
            params);
    }

    protected String createDatacenterLinkUsedResources(final Integer datacenterId)
    {
        return createDatacenterLink(datacenterId) + "/action/updateusedresources";
    }

    protected String createDatacenterLink()
    {
        return createDatacenterLink(null);
    }

    protected String createDatacenterLink(final Integer datacenterId)
    {
        String uri = "admin/datacenters";
        Map<String, String> params = new HashMap<String, String>();
        if (datacenterId != null)
        {
            params.put("datacenter", datacenterId.toString());
            uri += "/{datacenter}";
        }

        return resolveURI(apiUri, uri, params);
    }

    protected String createPrivateNetworksLink(final Integer vdcId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdc", vdcId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdc}/privatenetworks", params);
    }

    protected String createPrivateNetworkLink(final Integer vdcId, final Integer vlanId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdc", vdcId.toString());
        params.put("vlan", vlanId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdc}/privatenetworks/{vlan}", params);
    }

    protected String createPrivateNetworkIPLink(final Integer vdcId, final Integer vlanId,
        final Integer ipId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdc", vdcId.toString());
        params.put("vlan", vlanId.toString());
        params.put("ip", ipId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdc}/privatenetworks/{vlan}/ips/{ip}",
            params);
    }

    protected String createPrivateNetworkIPsLink(final Integer vdcId, final Integer vlanId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdc", vdcId.toString());
        params.put("vlan", vlanId.toString());

        return resolveURI(apiUri, "cloud/virtualdatacenters/{vdc}/privatenetworks/{vlan}/ips",
            params);
    }

    protected String createDatacenterPublicIPsLink(final Integer datacenterId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dc", datacenterId.toString());

        return resolveURI(apiUri, "admin/datacenters/{dc}/network/action/publicips", params);
    }

    protected String createDatacenterPublicTagCheck(final Integer datacenterId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dc", datacenterId.toString());

        return resolveURI(apiUri, "admin/datacenters/{dc}/network/action/checkavailability", params);
    }

    protected String createPublicNetworksLink(final Integer datacenterId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dc", datacenterId.toString());

        return resolveURI(apiUri, "admin/datacenters/{dc}/network", params);
    }

    protected String createPublicNetworkLink(final Integer datacenterId, final Integer networkId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dc", datacenterId.toString());
        params.put("network", networkId.toString());

        return resolveURI(apiUri, "admin/datacenters/{dc}/network/{network}", params);
    }

    protected String createPublicNetworkIPsLink(final Integer datacenterId, final Integer networkId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dc", datacenterId.toString());
        params.put("network", networkId.toString());

        return resolveURI(apiUri, "admin/datacenters/{dc}/network/{network}/ips", params);
    }

    protected String createPublicNetworkIPLink(final Integer datacenterId, final Integer networkId,
        final Integer ipId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dc", datacenterId.toString());
        params.put("network", networkId.toString());
        params.put("ip", ipId.toString());

        return resolveURI(apiUri, "admin/datacenters/{dc}/network/{network}/ips/{ip}", params);
    }

    // protected Resource resource(final String uri, final String user, final String password,
    // final ClientHandler... handlers)
    // {
    // return resource(uri, user, password, MediaType.APPLICATION_XML, handlers);
    // }

    protected Resource resource(final String uri, final String user, final String password,
        final String mediaType, final ClientHandler... handlers)
    {
        if (handlers == null || handlers.length == 0)
        {
            return resource(uri, user, password, mediaType);
        }
        ClientConfig config = new ClientConfig();
        config.handlers(handlers);

        Resource resource = new RestClient(config).resource(uri).accept(mediaType);
        long tokenExpiration = System.currentTimeMillis() + 1000L * 1800;

        String signature = TokenUtils.makeTokenSignature(tokenExpiration, user, password);

        String[] tokens;
        if (this.currentSession != null && StringUtils.isNotBlank(currentSession.getAuthType()))
        {
            tokens =
                new String[] {user, valueOf(tokenExpiration), signature,
                currentSession.getAuthType()};
        }
        else
        {
            tokens =
                new String[] {user, valueOf(tokenExpiration), signature, AuthType.ABIQUO.name()};
        }
        String cookieValue = StringUtils.join(tokens, ":");

        cookieValue = new String(Base64.encodeBase64(cookieValue.getBytes()));

        return resource.cookie(new Cookie("auth", cookieValue));
    }

    protected String createRacksLink(final Integer datacenterId)
    {
        return createRacksLink(datacenterId, null);
    }

    protected String createRacksLink(final Integer datacenterId, final Integer rackId)
    {
        Map<String, String> params = new HashMap<String, String>();
        String uri = "admin/datacenters/{datacenter}/racks";
        params.put("datacenter", datacenterId.toString());

        if (rackId != null)
        {
            uri += "/{rack}";
            params.put("rack", rackId.toString());
        }

        return resolveURI(apiUri, uri, params);
    }

    protected String createMachinesLink(final Rack rack)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", valueOf(rack.getDataCenter().getId()));
        params.put("rack", rack.getId().toString());

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/machines", params);
    }

    protected String createMachinesLink(final Integer datacenterId, final Integer rackId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/machines", params);
    }

    protected String createRackLink(final Integer datacenterId, final Integer rackId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}", params);
    }

    /**
     * @param virtualDatacenterId
     * @param virtualApplianceId
     * @return String
     */
    protected String createVirtualApplianceDeployLink(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualAppliances", String.valueOf(virtualApplianceId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualAppliances}/action/deploy",
                params);
    }

    /**
     * @param virtualDatacenterId
     * @param virtualApplianceId
     * @return String
     */
    protected String createVirtualApplianceUndeployLink(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualAppliances", String.valueOf(virtualApplianceId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualAppliances}/action/undeploy",
                params);
    }

    protected String createEditVirtualMachineStateUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));
        params.put("virtualMachine", String.valueOf(virtualMachineId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}/virtualmachines/{virtualMachine}/state",
                params);

    }

    protected String createRunlistLink(final Integer vdcId, final Integer vappId,
        final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vdc", vdcId.toString());
        params.put("vapp", vappId.toString());
        params.put("vm", virtualMachineId.toString());
        return resolveURI(
            apiUri,
            "cloud/virtualdatacenters/{vdc}/virtualappliances/{vapp}/virtualmachines/{vm}/config/runlist",
            params);
    }

    protected String createVirtualMachinesUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}/virtualmachines",
                params);
    }

    protected String createVirtualMachineUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));
        params.put("virtualMachineId", String.valueOf(virtualMachineId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}/virtualmachines/{virtualMachineId}",
                params);
    }

    protected String createVirtualMachineInstanceUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));
        params.put("virtualMachineId", String.valueOf(virtualMachineId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}/virtualmachines/{virtualMachineId}/action/instance",
                params);
    }

    protected String createVirtualMachineResetUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Integer virtualMachineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));
        params.put("virtualMachineId", String.valueOf(virtualMachineId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}/virtualmachines/{virtualMachineId}/action/reset",
                params);
    }

    protected String createVirtualApplianceUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));

        return URIResolver.resolveURI(apiUri,
            "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}",
            params);
    }

    protected String createVirtualApplianceMachinesUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));

        return URIResolver
            .resolveURI(
                apiUri,
                "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}/virtualmachines",
                params);
    }

    protected String createCurrencyLink(final int currencyId)
    {
        return URIResolver.resolveURI(apiUri, "config/currencies/{currency}",
            Collections.singletonMap("currency", valueOf(currencyId)));
    }

    protected String createPricingTemplateLink(final int templateId)
    {
        return URIResolver.resolveURI(apiUri, "config/pricingtemplates/{pricingtemplate}",
            Collections.singletonMap("pricingtemplate", valueOf(templateId)));
    }

    protected String createPricingTemplatesLink()
    {
        return createPricingTemplatesLink(null, null);
    }

    protected String createPricingTemplatesLink(Integer offset, final Integer numResults)
    {
        String uri =
            URIResolver.resolveURI(apiUri, "config/pricingtemplates", Collections.emptyMap());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (numResults != null)
        {
            queryParams.put("numResults", new String[] {numResults.toString()});
            if (offset != null)
            {
                offset = offset / numResults;

                queryParams.put("page", new String[] {offset.toString()});
            }
        }
        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createCostCodesLink()
    {
        return createCostCodesLink(null, null);
    }

    protected String createCostCodesLink(Integer offset, final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "config/costcodes", Collections.emptyMap());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (offset != null && numResults != null)
        {
            if (numResults != 0)
            {
                offset = offset / numResults;

                queryParams.put("page", new String[] {offset.toString()});
                queryParams.put("numResults", new String[] {numResults.toString()});
            }
            else if (numResults == 0 && offset == 0)
            {
                queryParams.put("page", new String[] {offset.toString()});
                queryParams.put("numResults", new String[] {numResults.toString()});
            }
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createCostCodeCurrenciesLink(final Integer costCodeId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("costcode", costCodeId.toString());

        return resolveURI(apiUri, "config/costcodes/{costcode}/currencies", params);
    }

    protected String createCostCodeLink(final int costCodeId)
    {
        return URIResolver.resolveURI(apiUri, "config/costcodes/{costcode}",
            Collections.singletonMap("costcode", valueOf(costCodeId)));
    }

    protected String createCostCodeCurrenciesLink(final String costCodeId, Integer offset,
        final Integer numResults)
    {
        String uri =
            URIResolver.resolveURI(apiUri, "config/costcodes/{costcode}/currencies",
                Collections.singletonMap("costcode", valueOf(costCodeId)));

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createCurrenciesLink()
    {
        return createCurrenciesLink(null, null);
    }

    protected String createCurrenciesLink(Integer offset, final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "config/currencies", Collections.emptyMap());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createPricingCostCodesLink(final Integer pricingId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pricingtemplate", pricingId.toString());

        return resolveURI(apiUri, "config/pricingtemplates/{pricingtemplate}/costcodes", params);
    }

    protected String createPricingCostCodeLink(final Integer pricingId,
        final Integer pricingCostCodeId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pricingtemplate", pricingId.toString());
        params.put("costcode", pricingCostCodeId.toString());
        return resolveURI(apiUri, "config/pricingtemplates/{pricingtemplate}/costcodes/{costcode}",
            params);
    }

    protected String createPricingTiersLink(final Integer pricingId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pricingtemplate", pricingId.toString());

        return resolveURI(apiUri, "config/pricingtemplates/{pricingtemplate}/tiers", params);
    }

    protected String createPricingTierLink(final Integer pricingId, final Integer pricingTierId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pricingtemplate", pricingId.toString());
        params.put("tier", pricingTierId.toString());
        return resolveURI(apiUri, "config/pricingtemplates/{pricingtemplate}/tiers/{tier}", params);
    }

    protected String createVirtualAppliancePriceLink(final int virtualDatacenterId,
        final int virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("vapp", String.valueOf(virtualApplianceId));

        return resolveURI(apiUri,
            "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{vapp}/action/price",
            params);
    }

    protected String createVirtualAppliancesByEnterpriseLink(final Integer entId)
    {
        return createEnterpriseLink(entId) + "/action/virtualappliances";
    }

    protected String createVirtualApplianceUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("virtualDatacenter", String.valueOf(virtualDatacenterId));
        params.put("virtualApplianceId", String.valueOf(virtualApplianceId));

        return URIResolver.resolveURI(apiUri,
            "cloud/virtualdatacenters/{virtualDatacenter}/virtualappliances/{virtualApplianceId}",
            params, queryParams);
    }

    protected String createVirtualAppliancesByVirtualDatacenterLink(final Integer vdcId)
    {
        return createVirtualDatacenterLink(vdcId) + "/virtualappliances";
    }

    /**
     * Returns the id, if exists, of a RESTLink. If not exists, returns {@code null}.
     * 
     * <pre>
     * {@code
     * http://localhost:80/api/admin/datacenters/2
     * }
     * Returns "2"
     * </pre>
     * 
     * @param link
     * @return string representing the id
     */
    protected String getIdFromLink(final RESTLink link)
    {
        if (link == null)
        {
            return null;
        }

        return link.getHref().substring(link.getHref().lastIndexOf("/") + 1);
    }

    protected String createRackOrganizationsLink(final Integer datacenterId, final Integer rackId,
        final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/organizations",
            params, queryParams);
    }

    protected String createRackLogicServersLink(final Integer datacenterId, final Integer rackId,
        final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/logicservers",
            params, queryParams);
    }

    protected String createRackLogicServerTemplatesLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/lstemplates",
            params, queryParams);
    }

    protected String createRackCloneLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/logicservers/clone",
            params, queryParams);
    }

    protected String createRackAssociateLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicservers/associate", params,
            queryParams);
    }

    protected String createRackDissociateLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicservers/dissociate", params,
            queryParams);
    }

    protected String createRackDeleteLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicservers/delete", params, queryParams);
    }

    protected String createMachineBladeLedOnLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/ledon", params);
    }

    protected String createMachineBladeLsLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/logicserver", params);
    }

    protected String createRackAssociateLogicServerTemplateLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicservers/assoctemplate", params,
            queryParams);
    }

    protected String createRackAssociateLogicServerCloneLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicservers/assocclone", params,
            queryParams);
    }

    protected String createObjectFsmLink(final Integer datacenterId, final Integer rackId,
        final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/fsm", params,
            queryParams);
    }

    protected String createMachineBladeLedOffLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/ledoff", params);
    }

    protected String createMachineBladeLedLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/led", params);
    }

    protected String createVirtualMachineHardDiskLink(final Integer datacenterId,
        final Integer rackId, final Integer pmId, final Integer vmId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", pmId.toString());
        params.put("vm", vmId.toString());

        return resolveURI(
            apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/virtualmachines/{vm}/action/disk",
            params);
    }

}
