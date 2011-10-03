package com.abiquo.api.resources.appslibrary;

import static com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource.createTransferObject;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualImageService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.DatacenterRepositoriesDto;
import com.abiquo.server.core.infrastructure.Repository;

@Parent(EnterpriseResource.class)
@Path(DatacenterRepositoriesResource.REMOTE_REPOSITORIES_PATH)
@Controller
public class DatacenterRepositoriesResource extends AbstractResource
{

    public static final String REMOTE_REPOSITORIES_PATH = "datacenterrepositories";

    @Autowired
    private VirtualImageService service;

    @Autowired
    private InfrastructureService infService;

    @GET
    public DatacenterRepositoriesDto getDatacenterRepositories(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @Context IRESTBuilder restBuilder) throws Exception
    {
        DatacenterRepositoriesDto reposDto = new DatacenterRepositoriesDto();

        List<Repository> all = service.getDatacenterRepositories(enterpId);

        if (all != null && !all.isEmpty())
        {
            for (Repository rep : all)
            {
                final String amUri =
                    infService.getRemoteService(rep.getDatacenter().getId(),
                        RemoteServiceType.APPLIANCE_MANAGER).getUri();

                reposDto.add(createTransferObject(rep, enterpId, amUri, restBuilder));
            }
        }

        return reposDto;
    }
}
