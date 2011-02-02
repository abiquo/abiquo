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

package net.undf.abicloud.vo.service
{

    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.service.RemoteService")]
    public class RemoteService
    {
        /* ------------- Public atributes ------------- */

        public static const STATUS_KO:int = 0;

        public static const STATUS_OK:int = 1;

        public var idRemoteService:int;

        public var remoteServiceType:RemoteServiceType;

        public var idDataCenter:int;

        public var uri:String;

        public var uuid:String;

        public var name:String;

        public var status:int;

        public var protocol:String;

        public var domainName:String;

        public var port:int;

        public var serviceMapping:String;

        /* ------------- Constructor ------------- */
        public function RemoteService()
        {
            idRemoteService = 0;
            remoteServiceType = new RemoteServiceType();
            idDataCenter = 0;
            uri = "";
            uuid = "";
            name = "";
        }

        public function updateWithValues(remoteService:RemoteService):void
        {
            idRemoteService = remoteService.idRemoteService;
            remoteServiceType = remoteService.remoteServiceType;
            idDataCenter = remoteService.idDataCenter;
            uri = remoteService.uri;
            uuid = remoteService.uuid;
            name = remoteService.name;
            status = remoteService.status;
            protocol = remoteService.protocol;
            domainName = remoteService.domainName;
            port = remoteService.port;
            serviceMapping = remoteService.serviceMapping;
        }

    }
}