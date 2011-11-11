/**
 * 
 */
package com.abiquo.abiserver.commands.stub.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.VirtualApplianceResourceStub;
import com.abiquo.abiserver.exception.VirtualApplianceCommandException;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.URIResolver;

/**
 * @author jdevesa
 */
public class VirtualApplianceResourceStubImpl extends AbstractAPIStub implements
    VirtualApplianceResourceStub
{

    @Override
    public BasicResult createVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        DataResult<VirtualAppliance> result = new DataResult<VirtualAppliance>();

        /** Prepare the request. */
        VirtualApplianceDto dto = createVirtualApplianceDtoObject(virtualAppliance);
        String uri = createVirtualAppliancesLink(virtualAppliance.getVirtualDataCenter().getId());

        ClientResponse response = post(uri, dto);

        if (response.getStatusCode() == 201)
        {
            VirtualApplianceDto networkDto = response.getEntity(VirtualApplianceDto.class);
            result.setData(createVirtualApplianceFlexObject(networkDto));
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "createVirtualAppliance");
        }

        return result;
    }

    protected VirtualApplianceDto createVirtualApplianceDtoObject(
        final VirtualAppliance virtualAppliance)
    {
        VirtualApplianceDto dto = new VirtualApplianceDto();

        dto.setName(virtualAppliance.getName());

        return dto;
    }

    protected VirtualAppliance createVirtualApplianceFlexObject(final VirtualApplianceDto dto)
    {
        VirtualAppliance vapp = new VirtualAppliance();

        vapp.setName(dto.getName());
        vapp.setId(dto.getId());
        vapp.setState(transformStates(dto.getState()));
        vapp.setNodes(new ArrayList<Node>());
        vapp.setSubState(vapp.getState());

        return vapp;
    }

    private State transformStates(final VirtualApplianceState state)
    {
        State flexState = new State();
        switch (state)
        {
            case DEPLOYED:
                flexState.setDescription(StateEnum.DEPLOYED.name());
                flexState.setId(StateEnum.DEPLOYED.id());
                break;
            case NOT_DEPLOYED:
                flexState.setDescription(StateEnum.NOT_DEPLOYED.name());
                flexState.setId(StateEnum.NOT_DEPLOYED.id());
                break;
            case NEEDS_SYNC:
                flexState.setDescription(StateEnum.NEEDS_SYNCHRONIZE.name());
                flexState.setId(StateEnum.NEEDS_SYNCHRONIZE.id());
                break;
            case LOCKED:
                flexState.setDescription(StateEnum.LOCKED.name());
                flexState.setId(StateEnum.LOCKED.id());
            case UNKNOWN:
                flexState.setDescription(StateEnum.UNKNOWN.name());
                flexState.setId(StateEnum.UNKNOWN.id());
        }

        return flexState;
    }

    @Override
    public VirtualmachineHB allocate(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualmachineHB vmachineRequ,
        final List<ResourceManagementHB> resMans, final boolean forceEnterpirseLimits,
        final ErrorManager errorManager) throws VirtualApplianceCommandException // TODO
    {
        // VirtualImageWithResourcesDto vimageReq =
        // createVirtualImageAndResourceDto(vmachineRequ, resMans, forceEnterpirseLimits);
        //
        // String vappUrl = resolveVirtualApplianceUrl(virtualDatacenterId, virtualApplianceId);
        //
        // ClientResponse response = post(vappUrl, vimageReq);
        //
        // if (response.getStatusCode() != 201)
        // {
        // onError(response);
        // }
        //
        // Integer vmachineId = response.getEntity(Integer.class);
        //
        // BasicCommand.traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
        // EventType.VAPP_CREATE, getCurrentUser(), null, "XXX", "Selected virtual machine", null,
        // null, null, null, null);
        //
        // DAOFactory dao = HibernateDAOFactory.instance();
        // dao.beginConnection();
        // VirtualmachineHB vmachine = dao.getVirtualMachineDAO().findById(vmachineId);
        //
        // /**
        // * TODO substitute vmachineRequ with vmachine on the current virutal appliance
        // */
        //
        // dao.endConnection();
        //
        // return vmachine;
        return null;
    }

    /**
     * vimage constains the modified cpu, ram and hd
     */
    // VirtualImageWithResourcesDto createVirtualImageAndResourceDto(VirtualmachineHB vmachine,
    // List<ResourceManagementHB> resMans, boolean forceEnterpirseLimits)
    // {
    // VirtualImageWithResourcesDto vimageReq = new VirtualImageWithResourcesDto();
    //
    // vimageReq.setVirtualImageId(vmachine.getImage().getIdImage());
    //
    // vimageReq.setRequiredCpu(vmachine.getCpu());
    // vimageReq.setRequiredRam(vmachine.getRam());
    // vimageReq.setRequiredHd(vmachine.getHd());
    //
    // vimageReq.setForeceEnterpriseSoftLimits(forceEnterpirseLimits);
    //
    // Collection<Integer> rasdIds = new HashSet<Integer>();
    // for (ResourceManagementHB rasdHb : resMans)
    // {
    // rasdIds.add(rasdHb.getIdManagement());
    // }
    //
    // vimageReq.setRasdIds(rasdIds);
    //
    // return vimageReq;
    // }

    private String resolveVirtualApplianceUrl(final Integer virtualDatacenterId,
        final Integer virtualApplianceId)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("{virtualDatacenter}", String.valueOf(virtualDatacenterId));
        params.put("{vapp}", String.valueOf(virtualApplianceId));

        return URIResolver.resolveURI(apiUri,
            "cloud/virtualdatacenters/{virtualDatacenter}/vapps/{vapp}", params);
    }

}
