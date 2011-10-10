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
package com.abiquo.abiserver.appslibrary.stub;

import static java.lang.String.valueOf;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

import com.abiquo.abiserver.business.authentication.TokenUtils;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackageListsDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;

public class AppsLibraryStubImpl extends AbstractAPIStub implements AppsLibraryStub
{

    private RestClient client;

    public static final String OVF_PACKAGE_LISTS_PATH = "appslib/ovfpackagelists";

    public static final String ENTERPRISES_PATH = "admin/enterprises";

    public String baseUri;

    final String user;

    final String password;

    final String authType;

    public static final String OVF_PACKAGE_PATH = "appslib/ovfpackages";

    public AppsLibraryStubImpl(final UserSession session)
    {
        client = new RestClient();
        baseUri = AbiConfigManager.getInstance().getAbiConfig().getApiLocation();

        DAOFactory factory = HibernateDAOFactory.instance();
        factory.beginConnection();
        UserHB user =
            factory.getUserDAO().getUserByLoginAuth(session.getUser(), session.getAuthType());
        factory.endConnection();

        this.user = user.getUser();
        this.password = user.getPassword();
        this.authType = user.getAuthType();
    }

    private void setAuthCookie(final Resource resource)
    {
        long tokenExpiration = System.currentTimeMillis() + 1000L * 1800;
        String signature = TokenUtils.makeTokenSignature(tokenExpiration, user, password);
        String authType = this.authType;
        String[] tokens;
        if (authType != null)
        {
            tokens = new String[] {user, valueOf(tokenExpiration), signature, authType};
        }
        else
        {
            tokens = new String[] {user, valueOf(tokenExpiration), signature};
        }
        String cookieValue = StringUtils.join(tokens, ":");

        cookieValue = new String(Base64.encodeBase64(cookieValue.getBytes()));

        resource.cookie(new Cookie("auth", cookieValue));
    }

    public Resource createResourceOVFPackageLists(final Integer idEnterprise)
    {
        final String path =
            ENTERPRISES_PATH + '/' + String.valueOf(idEnterprise) + '/' + OVF_PACKAGE_LISTS_PATH;
        Resource reso = client.resource(baseUri + "/" + path);

        setAuthCookie(reso);

        return reso;
    }

    public Resource createResourceOVFPackageList(final Integer idEnterprise,
        final Integer idOvfpackageList)
    {
        final String path =
            ENTERPRISES_PATH + '/' + String.valueOf(idEnterprise) + '/' + OVF_PACKAGE_LISTS_PATH
                + '/' + String.valueOf(idOvfpackageList);

        Resource reso = client.resource(baseUri + "/" + path);

        setAuthCookie(reso);

        return reso;
    }

    // //////////

    @Override
    public OVFPackageListDto createOVFPackageList(final Integer idEnterprise,
        final String ovfpackageListURL)
    {
        Resource resource = createResourceOVFPackageLists(idEnterprise);
        // resource.queryParam("ovfindexURL", ovfpackageListURL);

        ClientResponse response =
            resource.contentType(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_XML).post(
                ovfpackageListURL);

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus / 200 != 1)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(OVFPackageListDto.class);
    }

    @Override
    public void deleteOVFPackageList(final Integer idEnterprise, final String nameOvfpackageList)
    {
        final Integer idOvfPackageList =
            getOVFPackageListIdFromName(idEnterprise, nameOvfpackageList);

        Resource resource = createResourceOVFPackageList(idEnterprise, idOvfPackageList);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus / 200 != 1)
        {
            throw new WebApplicationException(response(response));
        }
    }

    @Override
    public OVFPackageListDto getOVFPackageList(final Integer idEnterprise,
        final String nameOVFPackageList)
    {
        final Integer idOvfPackageList =
            getOVFPackageListIdFromName(idEnterprise, nameOVFPackageList);
        String uri = createOVFPackageListLink(idEnterprise.toString(), idOvfPackageList.toString());

        ClientResponse response = get(uri);

        return response.getEntity(OVFPackageListDto.class);
    }

    private Integer getOVFPackageListIdFromName(final Integer idEnterprise, final String packageName)
    {
        OVFPackageListsDto packageLists = getOVFPackageLists(idEnterprise);

        for (OVFPackageListDto list : packageLists.getCollection())
        {
            final String listName = list.getName();
            if (packageName.equalsIgnoreCase(listName))
            {
                return list.getId();
            }
        }

        final String cause =
            String.format("Can not locat OVFPackageList named [%s] for enterprise [%s]",
                packageName, idEnterprise);
        final Response response = Response.status(Status.NOT_FOUND).entity(cause).build();
        throw new WebApplicationException(response);
    }

    @Override
    public List<String> getOVFPackageListName(final Integer idEnterprise)
    {
        List<String> packageNameList = new LinkedList<String>();

        OVFPackageListsDto packageLists = getOVFPackageLists(idEnterprise);

        for (OVFPackageListDto list : packageLists.getCollection())
        {
            packageNameList.add(list.getName());
        }

        return packageNameList;
    }

    // XXX not used on the AppsLibraryCommand
    private OVFPackageListsDto getOVFPackageLists(final Integer idEnterprise)
    {
        Resource resource = createResourceOVFPackageLists(idEnterprise);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        final Integer httpStatus = response.getStatusCode();
        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(OVFPackageListsDto.class);
    }

    @Override
    public OVFPackageListDto refreshOVFPackageList(final Integer idEnterprise,
        final String nameOvfpackageList)
    {
        final Integer idList = getOVFPackageListIdFromName(idEnterprise, nameOvfpackageList);

        return refreshOVFPackageList(idEnterprise, idList);
    }

    private OVFPackageListDto refreshOVFPackageList(final Integer idEnterprise, final Integer idList)
    {
        Resource resource = createResourceOVFPackageList(idEnterprise, idList);
        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.TEXT_PLAIN).put(null);

        final Integer httpStatus = response.getStatusCode();
        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(OVFPackageListDto.class);
    }

    private static Response response(final ClientResponse response)
    {
        String cause = new String();
        try
        {
            ErrorsDto errors = response.getEntity(ErrorsDto.class);
            for (ErrorDto e : errors.getCollection())
            {
                cause = cause.concat(e.getMessage());
            }
        }
        catch (Exception e)
        {
            cause = response.getEntity(String.class);
        }

        return Response.status(response.getStatusCode()).entity(cause).build();
    }

    @Override
    public OVFPackagesDto getOVFPackages(final Integer idEnterprise, final String nameOVFPackageList)
    {
        final Integer idOvfPackageList =
            getOVFPackageListIdFromName(idEnterprise, nameOVFPackageList);

        Resource resource = createResourceOVFPackages(idEnterprise, idOvfPackageList);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(OVFPackagesDto.class);
    }

    public Resource createResourceOVFPackages(final Integer idEnterprise,
        final Integer idOvfpackageList)
    {
        final String path =
            ENTERPRISES_PATH + '/' + String.valueOf(idEnterprise) + '/' + OVF_PACKAGE_PATH + '/'
                + String.valueOf(idOvfpackageList);

        Resource reso = client.resource(baseUri + "/" + path);

        setAuthCookie(reso);

        return reso;
    }
}
