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
#ifndef VIRT_MONITOR_H
#define VIRT_MONITOR_H

#include <libvirt/libvirt.h>

#define TIMEOUT_MS 1000

#define CREATED     "CREATED\0"
#define POWEREDON   "POWER_ON\0"
#define POWEREDOFF  "POWER_OFF\0"
#define SUSPENDED   "PAUSED\0"
#define RESUMED     "RESUMED\0"
#define SAVED       "SAVED\0"
#define DESTROYED   "DESTROYED\0"
#define UNKNOWN     "UNKNOWN\0"

int connect(const char * url, void (*callback_routine)(const char*, const char*));
void * listen(void* opaque);
void cancel();

#endif

