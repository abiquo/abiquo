package com.abiquo.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.ApprovalDto;
import com.abiquo.server.core.enterprise.ApprovalsDto;

@Path(ApprovalsResource.APPROVALS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Approvals")
public class ApprovalsResource extends AbstractResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalsResource.class);

    public static final String APPROVALS_PATH = "approvals";

    public static final String APPROVALS_TOKEN = "token";

    public static final String APPROVALS_TOKEN_PARAM = "{" + APPROVALS_TOKEN + "}";

    public static final String APPROVALS_APPROVED_PATH = "/approved/" + APPROVALS_TOKEN_PARAM;

    public static final String APPROVALS_DENIED_PATH = "/denied/" + APPROVALS_TOKEN_PARAM;

    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ApprovalsDto getApprovals(@Context final IRESTBuilder restBuilder) throws Exception
    {
        ApprovalsDto approvalsDto = new ApprovalsDto();

        return approvalsDto;
    }

    @POST
    @Produces
    public ApprovalDto addApproval(@Context final IRESTBuilder restBuilder) throws Exception
    {
        ApprovalDto approvalDto = new ApprovalDto();

        return approvalDto;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(ApprovalsResource.APPROVALS_APPROVED_PATH)
    public void approveRequest(@PathParam(ApprovalsResource.APPROVALS_TOKEN) final Integer token,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        LOGGER.info("Approving request matching token: " + token);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(ApprovalsResource.APPROVALS_DENIED_PATH)
    public void denyRequest(@PathParam(ApprovalsResource.APPROVALS_TOKEN) final Integer token,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        LOGGER.info("Denying request matching token: " + token);
    }
}
