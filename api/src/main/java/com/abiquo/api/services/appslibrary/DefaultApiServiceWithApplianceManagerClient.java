package com.abiquo.api.services.appslibrary;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.User.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl.ApplianceManagerStubException;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;

@Service
public class DefaultApiServiceWithApplianceManagerClient extends DefaultApiService
{
    final private static Logger logger = LoggerFactory
        .getLogger(DefaultApiServiceWithApplianceManagerClient.class);

    @Autowired
    protected InfrastructureService infService;

    protected ApplianceManagerResourceStubImpl getApplianceManagerClient(final Integer dcId)
    {
        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();
        ApplianceManagerResourceStubImpl amStub = new ApplianceManagerResourceStubImpl(amUri);

        try
        {
            amStub.checkService();
        }
        catch (ApplianceManagerStubException e)
        {
            logger.error("ApplianceManager configuration error", e);

            // A user named "SYSTEM" is created
            UserInfo ui = new UserInfo("SYSTEM");
            Platform platform =
                platform("abicloud").enterprise(enterprise("abiCloud").user(user("SYSTEM")));
            TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.UNKNOWN, e.getLocalizedMessage(), ui, platform);

            addConflictErrors(APIError.VIMAGE_AM_DOWN);
            flushErrors();
        }

        return amStub;
    }
}
