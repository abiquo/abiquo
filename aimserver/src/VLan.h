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

#ifndef VLAN_H
#define VLAN_H

#include <Service.h>

#include <string>

using namespace std;

class VLan: public Service
{
    protected:
        string ifconfig;
        string vconfig;
        string brctl;

        bool commandExist(string& command);
        int executeCommand(string command, bool redirect = false);

        void throwError(const string& message);

        void checkVlanRange(const int vlan);
        
        /*
         * Checks wether a VLAN interface exists
         */
        bool existsVlan(const int vlan, const string& vlanInterface);
        
        /*
         * Checks wether an interface exists
         */
        bool existsInterface(const string& interface);


        /*
         * Checks wether a bridge interface exists
         */
        bool existsBridge(const string& interface);

        /*
         * Returns 1 if there are 0 or 1 interfaces. Else it returns the number of interfaces.
         */
        int countBridgeInterfaces(const string& bridgeInterface);

    public:
        VLan();
        ~VLan();

        virtual bool initialize(dictionary * configuration);
        virtual bool cleanup();
        virtual bool start();
        virtual bool stop();

        void createVLAN(int vlan, const string& vlanInterface, const string& bridgeInterface);
        void deleteVLAN(int vlan, const string& vlanInterface, const string& bridgeInterface);

        void checkVLANConfiguration();
};

#endif
