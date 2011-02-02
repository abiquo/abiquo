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

#ifndef EVENTS_MONITOR_H
#define EVENTS_MONITOR_H

#include <Service.h>
#include <pthread.h>
#include <aim_types.h>
#include <hiredis.h>
#include <string>

using namespace std;

/*
 * This class manage the subscriptions and monitorize the events produced
 * by the local virtual machines.
 */
class EventsMonitor : public Service
{
    protected:
        pthread_t threadId;
        
        // Redis host
        static string host;

        // Redis port
        static int port;

        // Server port
        static string machinePort;

        // Singleton instance
        static EventsMonitor* instance;

        // Hypervisor address
        static string machineAddress;

        EventsMonitor();

        string getIP(string& address, int port);

    public:
        ~EventsMonitor();

        // From Service.h interface
        virtual bool initialize(dictionary * configuration);
        virtual bool cleanup();
        virtual bool start();
        virtual bool stop();

        static void callback(const char* uuid, const char* event);

        static EventsMonitor* getInstance();
};

#endif
