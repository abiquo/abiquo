/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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

#ifndef AIM_HANDLER_H
#define AIM_HANDLER_H

#include <Service.h>
#include <EventsMonitor.h>
#include <Rimp.h>
#include <VLan.h>
#include <NodeConfig.h>

#include <vector>

#include <aim_types.h>

using namespace std;

class AimHandler: virtual public AimIf
{
protected:
    EventsMonitor* monitor;
    Rimp* rimp;
    VLan* vlan;
    NodeConfig* node;

public:
    AimHandler()
    {
        monitor = EventsMonitor::getInstance();
        rimp = new Rimp();
        vlan = new VLan();
        node = new NodeConfig();
    }

    vector<Service*> getServices()
    {
        vector<Service*> services;

        services.push_back(monitor);
        services.push_back(rimp);
        services.push_back(vlan);
        services.push_back(node);

        return services;
    }

    void checkRimpConfiguration()
    {
        rimp->checkRimpConfiguration();
    }

    int64_t getDatastoreSize()
    {
        return rimp->getDatastoreSize();
    }

    int64_t getDiskFileSize(const std::string& virtualImageDatastorePath)
    {
        return rimp->getDiskFileSize(virtualImageDatastorePath);
    }

    void getDatastores(std::vector<Datastore> & _return)
    {
        _return = rimp->getDatastores();
    }

    void getNetInterfaces(std::vector<NetInterface> & _return)
    {
        _return = rimp->getNetInterfaces();
    }

    void copyFromRepositoryToDatastore(const std::string& virtualImageRepositoryPath, const std::string& datastorePath,
            const std::string& virtualMachineUUID)
    {
        string datastoreIn = datastorePath;
        rimp->copyFromRepositoryToDatastore(virtualImageRepositoryPath, datastoreIn, virtualMachineUUID);
    }

    void deleteVirtualImageFromDatastore(const std::string& datastorePath, const std::string& virtualMachineUUID)
    {
        string datastoreIn = datastorePath;
        rimp->deleteVirtualImageFromDatastore(datastoreIn, virtualMachineUUID);
    }

    void copyFromDatastoreToRepository(const std::string& virtualMachineUUID, const std::string& snapshot,
            const std::string& destinationRepositoryPath, const std::string& sourceDatastorePath)
    {
        rimp->copyFromDatastoreToRepository(virtualMachineUUID, snapshot, destinationRepositoryPath,
                sourceDatastorePath);
    }

    void createVLAN(const int32_t vlanTag, const std::string& vlanInterface, const std::string& bridgeInterface)
    {
        vlan->createVLAN(vlanTag, vlanInterface, bridgeInterface);
    }

    void deleteVLAN(const int32_t vlanTag, const std::string& vlanInterface, const std::string& bridgeInterface)
    {
        vlan->deleteVLAN(vlanTag, vlanInterface, bridgeInterface);
    }

    void checkVLANConfiguration()
    {
        vlan->checkVLANConfiguration();
    }

    void getInitiatorIQN(std::string& _return)
    {
       node->getInitiatorIQN(_return);
    }
};

#endif
