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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.05.31 at 04:53:04 PM CEST 
//

package com.abiquo.server.core.infrastructure.nodecollector;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the com.abiquo.server.core.infrastructure.nodecollector package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation
 * for XML content. The Java representation of XML content can consist of schema derived interfaces
 * and classes representing the binding of schema type definitions, element declarations and model
 * groups. Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory
{

    private final static QName _Hypervisor_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Hypervisor");

    private final static QName _Hosts_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Hosts");

    private final static QName _Host_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Host");

    private final static QName _VirtualSystemCollection_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector",
            "VirtualSystemCollection");

    private final static QName _VirtualSystem_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "VirtualSystem");

    private final static QName _LogicServer_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "LogicServer");

    private final static QName _LogicServers_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "LogicServers");

    private final static QName _Organization_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Organization");

    private final static QName _Organizations_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Organizations");

    private final static QName _Fsm_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Fsm");

    private final static QName _Fsms_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "Fsms");

    private final static QName _LogicServerPolicy_QNAME =
        new QName("http://abiquo.com/server/core/infrastructure/nodecollector", "LogicServerPolicy");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes
     * for package: com.abiquo.server.core.infrastructure.nodecollector
     */
    public ObjectFactory()
    {
    }

    /**
     * Create an instance of {@link ResourceType }
     */
    public ResourceType createResourceType()
    {
        return new ResourceType();
    }

    /**
     * Create an instance of {@link HostDto }
     */
    public HostDto createHostDto()
    {
        return new HostDto();
    }

    /**
     * Create an instance of {@link VirtualSystemCollectionDto }
     */
    public VirtualSystemCollectionDto createVirtualSystemCollectionDto()
    {
        return new VirtualSystemCollectionDto();
    }

    /**
     * Create an instance of {@link VirtualSystemDto }
     */
    public VirtualSystemDto createVirtualSystemDto()
    {
        return new VirtualSystemDto();
    }

    /**
     * Create an instance of {@link HostsDto }
     */
    public HostsDto createHostsDto()
    {
        return new HostsDto();
    }

    /**
     * <<<<<<< HEAD Create an instance of {@link LogicServerDto }
     */
    public LogicServerDto createLogicServerDto()
    {
        return new LogicServerDto();
    }

    /**
     * Create an instance of {@link LogicServersDto }
     */
    public LogicServersDto createLogicServersDto()
    {
        return new LogicServersDto();
    }

    /**
     * Create an instance of {@link OrganizationDto }
     */
    public OrganizationDto createOrganizationDto()
    {
        return new OrganizationDto();
    }

    /**
     * Create an instance of {@link OrganizationsDto }
     */
    public OrganizationsDto createOrganizationsDto()
    {
        return new OrganizationsDto();
    }

    /**
     * Create an instance of {@link FsmDto }
     */
    public FsmDto createFsmDto()
    {
        return new FsmDto();
    }

    /**
     * Create an instance of {@link FsmsDto }
     */
    public FsmsDto createFsmsDto()
    {
        return new FsmsDto();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HypervisorEnumTypeDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Hypervisor")
    public JAXBElement<HypervisorEnumTypeDto> createHypervisor(final HypervisorEnumTypeDto value)
    {
        return new JAXBElement<HypervisorEnumTypeDto>(_Hypervisor_QNAME,
            HypervisorEnumTypeDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HostsDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Hosts")
    public JAXBElement<HostsDto> createHosts(final HostsDto value)
    {
        return new JAXBElement<HostsDto>(_Hosts_QNAME, HostsDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HostDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Host")
    public JAXBElement<HostDto> createHost(final HostDto value)
    {
        return new JAXBElement<HostDto>(_Host_QNAME, HostDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VirtualSystemCollectionDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "VirtualSystemCollection")
    public JAXBElement<VirtualSystemCollectionDto> createVirtualSystemCollection(
        final VirtualSystemCollectionDto value)
    {
        return new JAXBElement<VirtualSystemCollectionDto>(_VirtualSystemCollection_QNAME,
            VirtualSystemCollectionDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VirtualSystemDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "VirtualSystem")
    public JAXBElement<VirtualSystemDto> createVirtualSystem(final VirtualSystemDto value)
    {
        return new JAXBElement<VirtualSystemDto>(_VirtualSystem_QNAME,
            VirtualSystemDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogicServerDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "LogicServer")
    public JAXBElement<LogicServerDto> createLogicServer(final LogicServerDto value)
    {
        return new JAXBElement<LogicServerDto>(_LogicServer_QNAME,
            LogicServerDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogicServersDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "LogicServers")
    public JAXBElement<LogicServersDto> createLogicServers(final LogicServersDto value)
    {
        return new JAXBElement<LogicServersDto>(_LogicServers_QNAME,
            LogicServersDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganizationDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Organization")
    public JAXBElement<OrganizationDto> createOrganization(final OrganizationDto value)
    {
        return new JAXBElement<OrganizationDto>(_Organization_QNAME,
            OrganizationDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganizationsDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Organizations")
    public JAXBElement<OrganizationsDto> createOrganizations(final OrganizationsDto value)
    {
        return new JAXBElement<OrganizationsDto>(_Organizations_QNAME,
            OrganizationsDto.class,
            null,
            value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FsmDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Fsm")
    public JAXBElement<FsmDto> createFsm(final FsmDto value)
    {
        return new JAXBElement<FsmDto>(_Fsm_QNAME, FsmDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FsmsDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "Fsms")
    public JAXBElement<FsmsDto> createFsms(final FsmsDto value)
    {
        return new JAXBElement<FsmsDto>(_Fsms_QNAME, FsmsDto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LogicServerPolicyDto }{@code >}
     */
    @XmlElementDecl(namespace = "http://abiquo.com/server/core/infrastructure/nodecollector", name = "LogicServerPolicy")
    public JAXBElement<LogicServerPolicyDto> createLogicServerPolicy(
        final LogicServerPolicyDto value)
    {
        return new JAXBElement<LogicServerPolicyDto>(_LogicServerPolicy_QNAME,
            LogicServerPolicyDto.class,
            null,
            value);
    }
}
