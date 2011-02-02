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

#ifndef CONFIG_CONSTANTS_H
#define CONFIG_CONSTANTS_H

// Server properties
#define serverPort (char*)"server:port\0"
#define daemonizeServer (char*)"server:daemonize\0"

// Monitor properties
#define monitorUri (char*)"monitor:uri\0"
#define redisHost (char*)"monitor:redisHost\0"
#define redisPort (char*)"monitor:redisPort\0"

// Rimp properties
#define rimpRepository (char*)"rimp:repository\0"
#define rimpDatastore (char*)"rimp:datastore\0"

// Vlan properties
#define vlanIfConfigCmd (char*)"vlan:ifconfigCmd\0"
#define vlanVconfigCmd (char*)"vlan:vconfigCmd\0"
#define vlanBrctlCmd (char*)"vlan:brctlCmd\0"

#endif
