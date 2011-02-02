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

#ifndef DEBUG_H
#define DEBUG_H

#include <stdio.h>
#include <stdarg.h>
#include <syslog.h>
#include <string.h>
#include <time.h>

#define logtime() do { time_t raw; time(&raw); char * t = ctime(&raw); t[strlen(t)-1] = '\0'; ::fprintf(stderr, "[%s] ", t); } while (0)
#define stdlogerr(m, ...) do { logtime(); ::fprintf(stderr, m, ##__VA_ARGS__); ::fprintf(stderr, "\n"); } while (0)
#define syslogerr(m, ...) do { syslog(LOG_ERR, m, ##__VA_ARGS__); } while (0)

#define LOG(m, ...) do { stdlogerr(m, ##__VA_ARGS__); syslogerr(m, ##__VA_ARGS__); } while (0)

#endif

