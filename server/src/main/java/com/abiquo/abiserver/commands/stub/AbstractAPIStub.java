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

import java.util.HashMap;
import java.util.Map;

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
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.URIResolver;
import com.abiquo.util.resources.ResourceManager;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("unchecked")
public class AbstractAPIStub
{
    public static final String FLAT_MEDIA_TYPE = "application/flat+xml";

    public static final String LINK_MEDIA_TYPE = "application/link+xml";

    public static final String START_WITH = "startwith";

    public static final String BY = "by";

    public static final String FILTER = "has";

    public static final String LIMIT = "limit";

    public static final String ASC = "asc";

    public static final Integer DEFAULT_PAGE_LENGTH = 25;

    public static final String DEFAULT_PAGE_LENGTH_STRING = "25";

    protected RestClient client = new RestClient();

    protected final String apiUri;

    protected UserSession currentSession;

    public AbstractAPIStub()
    {
        this.apiUri = AbiConfigManager.getInstance().getAbiConfig().getApiLocation();
    }

    public UserSession getCurrentSession()
    {
        return currentSession;
    }

    public void setCurrentSession(final UserSession currentSession)
    {
        this.currentSession = currentSession;
    }

    private UserHB getCurrentUserCredentials()
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

    protected ClientResponse get(final String uri, final String user, final String password)
    {
        return resource(uri, user, password).get();
    }

    /**
     * Adds the content-type and accept headers with appropiate {@link MediaType}.
     * 
     * @param uri remote location.
     * @param user login.
     * @param password password.
     * @return ClientResponse
     */
    protected ClientResponse get(final String uri, final MediaType mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), mediaType).get();
    }

    protected ClientResponse getWithMediaType(final String uri, final String accept,
        final String contentType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), accept).contentType(contentType)
            .get();
    }

    protected ClientResponse post(final String uri, final Object dto, final String user,
        final String password)
    {
        return resource(uri, user, password).contentType(MediaType.APPLICATION_XML).post(dto);
    }

    protected ClientResponse put(final String uri, final Object dto, final String user,
        final String password)
    {
        return resource(uri, user, password).contentType(MediaType.APPLICATION_XML).put(dto);
    }

    protected ClientResponse put(final String uri, final Object dto, final String user,
        final String password, final String mediaType)
    {
        return resource(uri, user, password, mediaType).contentType(mediaType).put(dto);
    }

    protected ClientResponse delete(final String uri, final String user, final String password)
    {
        return resource(uri, user, password).delete();
    }

    protected ClientResponse delete(final String uri, final String user, final String password,
        final String mediaType)
    {
        return resource(uri, user, password, mediaType).delete();
    }

    protected ClientResponse get(final String uri)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).get();
    }

    protected ClientResponse get(final String uri, final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), mediaType).get();
    }

    protected ClientResponse post(final String uri, final Object dto)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).contentType(
            MediaType.APPLICATION_XML).post(dto);
    }

    protected ClientResponse post(final String uri, final Object dto, final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).contentType(mediaType)
            .accept(mediaType).post(dto);
    }

    protected Resource resource(final String uri)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).contentType(
            MediaType.APPLICATION_XML);
    }

    protected ClientResponse put(final String uri)
    {
        UserHB user = getCurrentUser();
        return resource(uri, user.getUser(), user.getPassword()).contentType(
            MediaType.APPLICATION_XML).put(null);
    }

    protected ClientResponse put(final String uri, final Object dto)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).contentType(
            MediaType.APPLICATION_XML).put(dto);
    }

    // protected ClientResponse put(final String uri, final Object dto, String mediaType)
    // {
    // UserHB user = getCurrentUser();
    // return resource(uri, user.getUser(), user.getPassword()).contentType(mediaType).put(dto);
    // }

    protected ClientResponse put(final String uri, final Object dto, final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword(), mediaType).contentType(mediaType)
            .put(dto);
    }

    protected ClientResponse delete(final String uri)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).delete();
    }

    protected String createLoginLink()
    {
        return URIResolver.resolveURI(apiUri, "/login", Collections.emptyMap());
    }

    protected ClientResponse delete(final String uri, final String mediaType)
    {
        UserHB user = getCurrentUserCredentials();
        return resource(uri, user.getUser(), user.getPassword()).accept(mediaType)
            .contentType(mediaType).delete();
    }

    private Resource resource(final String uri, final String user, final String password)
    {
        Resource resource = client.resource(uri).accept(MediaType.APPLICATION_XML);
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

    private Resource resource(final String uri, final String user, final String password,
        final String mediaType)
    {
        Resource resource = client.resource(uri).accept(mediaType);
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

    /**
     * Instantiate the {@link Resource} and not add the {@link MediaType.APPLICATION_XML} to the
     * request.
     * 
     * @param uri remote location.
     * @param user login.
     * @param password password.
     * @param mediaType content negotiation.
     * @return Resource
     */
    private Resource resource(final String uri, final String user, final String password,
        final MediaType mediaType)
    {
        Resource resource = client.resource(uri).contentType(mediaType);
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
        result.setSuccess(false);
        if (response.getStatusCode() == 401 || response.getStatusCode() == 403)
        {
            ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX).reportError(
                new ResourceManager(BasicCommand.class), result,
                "onFaultAuthorization.noPermission", methodName);
            result.setMessage(response.getMessage());
            result.setResultCode(BasicResult.NOT_AUTHORIZED);
            throw new UserSessionException(result);
        }
        else
        {
            ErrorsDto errors = response.getEntity(ErrorsDto.class);
            result.setMessage(errors.toString());
            if (errors.getCollection().get(0).getCode().equals("LIMIT_EXCEEDED"))
            {
                result.setResultCode(BasicResult.HARD_LIMT_EXCEEDED);
            }
        }
    }

    protected String createEnterprisesLink(final String filter, Integer offset,
        final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "admin/enterprises", Collections.emptyMap());
        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (!StringUtils.isEmpty(filter))
        {
            queryParams.put("filter", new String[] {filter});
        }
        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createEnterpriseLink(final int enterpriseId)
    {
        return URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}", Collections
            .singletonMap("enterprise", valueOf(enterpriseId)));
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

    protected String getReservedMachinesUri(final Integer enterpriseId, final Integer machineId)
    {
        String uri = createEnterpriseLink(enterpriseId);

        Map<String, String> params = new HashMap<String, String>();
        params.put("enterprise", valueOf(enterpriseId));
        params.put("machineId", valueOf(machineId));

        return URIResolver.resolveURI(apiUri,
            "admin/enterprises/{enterprise}/reservedmachines/{machineId}", params);
    }

    protected String createRoleLink(final int roleId)
    {
        return URIResolver.resolveURI(apiUri, "admin/roles/{role}", Collections.singletonMap(
            "role", valueOf(roleId)));
    }

    protected String createRolesLink()
    {
        return createRolesLink(null, null);
    }

    protected String createRolesLink(Integer offset, final Integer numResults)
    {
        String uri = URIResolver.resolveURI(apiUri, "admin/roles", Collections.emptyMap());

        Map<String, String[]> queryParams = new HashMap<String, String[]>();

        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
    }

    protected String createPrivilegeLink(final int privilegeId)
    {
        return URIResolver.resolveURI(apiUri, "config/privileges/{privilege}", Collections
            .singletonMap("privilege", valueOf(privilegeId)));
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
        return URIResolver.resolveURI(apiUri, "admin/rolesldap/{roleldap}", Collections
            .singletonMap("roleldap", valueOf(roleLdapId)));
    }

    protected String createUsersLink(final String enterpriseId)
    {
        return createUsersLink(enterpriseId, null, null);
    }

    protected String createUsersLink(final String enterpriseId, Integer offset,
        final Integer numResults)
    {
        String uri =
            URIResolver.resolveURI(apiUri, "admin/enterprises/{enterprise}/users", Collections
                .singletonMap("enterprise", enterpriseId));

        Map<String, String[]> queryParams = new HashMap<String, String[]>();
        if (offset != null && numResults != null)
        {
            offset = offset / numResults;

            queryParams.put("page", new String[] {offset.toString()});
            queryParams.put("numResults", new String[] {numResults.toString()});
        }

        return UriHelper.appendQueryParamsToPath(uri, queryParams, false);
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

    protected String createMachineLinkPowerOn(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/powerOn", params);
    }

    protected String createMachineLinkPowerOff(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/powerOff",
            params);
    }

    protected String createMachineLinkVms(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());

        return resolveURI(
            apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/virtualmachines",
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

        String uri = "admin/datacenters/{datacenter}/racks/{rack}/machines/";
        if (includeMachineId)
        {
            uri += "{machine}/";
        }
        uri +=
            "action/checkState?ip={ip}&hypervisor={hypervisor}&user={user}&password={password}&port={port}";

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
        params.put("port", port.toString());

        String uri = "admin/datacenters/{datacenter}/racks/{rack}/machines/";
        if (includeMachineId)
        {
            uri += "{machine}/";
        }
        uri += "action/checkIpmi?ip={ip}&user={user}&password={password}&port={port}";

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

    protected String createMachinesLinkMultiplePost(final Integer datacenterId,
        final Integer rackId, final String ipFrom, final String ipTo, final String hypervisor,
        final String user, final String password, final Integer port, final String vSwitch)
    {

        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("ipFrom", ipFrom);
        params.put("ipTo", ipTo);
        params.put("hypervisor", hypervisor);
        params.put("user", user);
        params.put("password", password);
        params.put("port", port.toString());
        params.put("vSwitch", vSwitch);

        String uri =
            "admin/datacenters/{datacenter}/racks/{rack}/machines"
                + "?ipFrom={ipFrom}&ipTo={ipTo}&hypervisor={hypervisor}&user={user}"
                + "&password={password}&port={port}&vSwitch={vSwitch}";

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
        return createDatacenterLink(datacenterId) + "action/updateUsedResources";
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

    protected Resource resource(final String uri, final String user, final String password,
        final ClientHandler... handlers)
    {
        if (handlers == null || handlers.length == 0)
        {
            return resource(uri, user, password);
        }
        ClientConfig config = new ClientConfig();
        config.handlers(handlers);

        Resource resource = new RestClient(config).resource(uri).accept(MediaType.APPLICATION_XML);
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

    protected ClientResponse get(final String uri, final String user, final String password,
        final ClientHandler... handlers)
    {
        return resource(uri, user, password, handlers).get();
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
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/logicServers",
            params, queryParams);
    }

    protected String createRackLogicServerTemplatesLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());

        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/lsTemplates",
            params, queryParams);
    }

    protected String createRackCloneLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/logicServers/clone",
            params, queryParams);
    }

    protected String createRackAssociateLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicServers/associate", params,
            queryParams);
    }

    protected String createRackDissociateLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicServers/dissociate", params,
            queryParams);
    }

    protected String createRackDeleteLogicServerLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicServers/delete", params, queryParams);
    }

    protected String createMachineBladeLedLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/action/ledOn", params);
    }

    protected String createMachineBladeLsLink(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        params.put("machine", machineId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/machines/{machine}/logicServer", params);
    }

    protected String createRackAssociateLogicServerTemplateLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicServers/assocTemplate", params,
            queryParams);
    }

    protected String createRackAssociateLogicServerCloneLink(final Integer datacenterId,
        final Integer rackId, final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri,
            "admin/datacenters/{datacenter}/racks/{rack}/logicServers/assocClone", params,
            queryParams);
    }

    protected String createCurrencyLink(final int currencyId)
    {
        return URIResolver.resolveURI(apiUri, "config/currencies/{currency}", Collections
            .singletonMap("currency", valueOf(currencyId)));
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
        return URIResolver.resolveURI(apiUri, "config/costcodes/{costcode}", Collections
            .singletonMap("costcode", valueOf(costCodeId)));
    }

    protected String createCostCodeCurrenciesLink(final String costCodeId, Integer offset,
        final Integer numResults)
    {
        String uri =
            URIResolver.resolveURI(apiUri, "config/costcodes/{costcode}/currencies", Collections
                .singletonMap("costcode", valueOf(costCodeId)));

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

    protected String createObjectFsmLink(final Integer datacenterId, final Integer rackId,
        final Map<String, String[]> queryParams)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("datacenter", datacenterId.toString());
        params.put("rack", rackId.toString());
        return resolveURI(apiUri, "admin/datacenters/{datacenter}/racks/{rack}/fsm", params,
            queryParams);
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
}
