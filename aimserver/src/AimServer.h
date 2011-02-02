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

#ifndef AIMSERVER_H
#define AIMSERVER_H

#include <iniparser/dictionary.h>
#include <getopt.h>

#include <AimHandler.hpp>

#define DEFAULT_PORT 60606
#define DEFAULT_CONFIG "aim.ini"

using boost::shared_ptr;

// Server command line parsing structures
const char* const short_opt = "hc:p:u:dr:s:v";
const struct option long_opt[] =
{
    { "help",        0, NULL, 'h' },
    { "config-file", 1, NULL, 'c' },
    { "port",        1, NULL, 'p' },
    { "uri",         1, NULL, 'u' },
    { "daemon",      0, NULL, 'd' },
    { "repository",  1, NULL, 'r' },
    { "datastore",   1, NULL, 's' },
    { "version",     0, NULL, 'v' },
    { NULL,          0, NULL, 0   }
};

// Prototypes
const char * parseArguments(int argc, char **argv, dictionary *d);
void printUsage(const char* program);
void printConfiguration(dictionary * d);
bool checkConfiguration(dictionary * d);
void deinitialize(int param);
static void daemonize(void);

// Server main configuration
dictionary * configuration;

// AIM handler
shared_ptr<AimHandler> aimHandler(new AimHandler());

#endif
