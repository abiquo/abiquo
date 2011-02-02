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

package com.abiquo.scheduler;

public class PopulateConstants
{

    final static String COMMNET = "#";

    final static String DELIMITER_ENTITIES = "\\.";

    final static String DELIMITER_DEFINITION = "\\:";

    final static String DELIMITER_ATTRIBUTES = "\\,";

    final static String DELIMITER_LIMIT = "\\;";

    final static String TEST_NAME = "test.name=";

    final static String TEST_DESCRIPTION = "test.description=";

    /*
     * *
     */
    final static String DEC_DATACENTER = "dc";

    final static String DEC_RACK = "r";

    final static String DEC_MACHINE = "m";

    //
    final static String DEC_ENTERPRISE = "e";

    final static String DEC_VIRTUAL_DATACENTER = "vdc";

    final static String DEC_VIRTUAL_IMAGE = "vi";

    final static String DEC_VIRTUAL_APPLIANCE = "va";

    final static String DEC_VIRTUAL_MACHINE = "vm";

    final static String DEC_VLAN = "vlan";

    //
    final static String DEC_ACTION = "action";

    final static String DEC_ALLOCATE = "allocate";

    final static String DEC_DEALLOCATE = "deallocate";

    final static String DEC_LIMIT = "limit";

    //
    final static String DEC_RULE = "rule";

    final static String RULE_FIT = "fit";

    final static String RULE_EXCLUSION = "exclusion";

    final static String RULE_RESERVED = "reserved";

    final static String RULE_LOAD = "load";

    /*
     * *
     */
    final static long DEF_MACHINE_CPU = 4;

    @Deprecated
    final static long DEF_MACHINE_CPU_USED = 1;

    final static long DEF_MACHINE_RAM = 8;

    @Deprecated
    final static long DEF_MACHINE_RAM_USED = 2;

    final static long DEF_MACHINE_HD = 100;

    @Deprecated
    final static long DEF_MACHINE_HD_USED = 4;

    final static int DEF_IMAGE_CPU = 1;

    final static int DEF_IMAGE_RAM = 2;

    final static long DEF_IMAGE_HD = 1;

    final static long GB_TO_MB = 1024;
}
