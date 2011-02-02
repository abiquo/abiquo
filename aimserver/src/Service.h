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

#ifndef SERVICE_H
#define SERVICE_H

#include <iniparser.h>
#include <dictionary.h>

#include <string>

using namespace std;

/*
 * Interface to implement server Services
 */
class Service
{
    protected:
        const char* name;

    public:
        Service(const char* name) : name(name) {}

	virtual ~Service() { };

        // Service name
        const char* getName() { return name; }

        // Control methods called by the server
        virtual bool initialize(dictionary * configuration) = 0;
        virtual bool cleanup() = 0;
        virtual bool start() = 0;
        virtual bool stop() = 0;
};

#endif
