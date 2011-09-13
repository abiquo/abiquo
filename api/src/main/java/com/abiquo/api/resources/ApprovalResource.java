package com.abiquo.api.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.ApprovalDto;

@Parent(ApprovalsResource.class)
@Path(ApprovalResource.APPROVAL_PARAM)
@Controller
public class ApprovalResource extends AbstractResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovalResource.class);

    public static final String APPROVAL = "approval";

    public static final String APPROVAL_PARAM = "{" + APPROVAL + "}";

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ApprovalDto getApproval(@PathParam(APPROVAL) final Integer approvalId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        ApprovalDto approvalDto = new ApprovalDto();

        return approvalDto;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_XML)
    public void removeApproval(@PathParam(APPROVAL) final Integer approvalId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {

    }
}
