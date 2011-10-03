package com.abiquo.api.resources.appslibrary;

import static com.abiquo.testng.TestConfig.AM_INTEGRATION_TESTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.RemoteServicesResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

@Test(groups = {AM_INTEGRATION_TESTS})
public class VirtualImageResourceIT extends AbstractJpaGeneratorIT
{
    private final static String AM_BASE_URI = "http://localhost:8888/am";// TODO test config

    // to add the am properly
    @Autowired
    private InfrastructureService service;

    private ApplianceManagerResourceStubImpl amclient;

    private RemoteServiceDto amDto()
    {
        RemoteServiceDto am = new RemoteServiceDto();
        am.setType(RemoteServiceType.APPLIANCE_MANAGER);
        am.setUri(AM_BASE_URI);

        return am;
    }

    private Enterprise ent;

    private Datacenter datacenter;

    @BeforeMethod
    public void setUpDatacenterRepository()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();

        setup(ent, datacenter);

        service.addRemoteService(amDto(), datacenter.getId());

        amclient = new ApplianceManagerResourceStubImpl(AM_BASE_URI);
    }

    @Test
    public void testCreateVirtualImage() throws InterruptedException
    {
        amclient.checkService();
        amclient.createOVFPackageInstance(ent.getId().toString(), "http://cccc.com/nore.ovf");

        Thread.sleep(5000);
    }
}
