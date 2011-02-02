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

package com.abiquo.virtualfactory.hypervisor.impl;

import java.net.URL;

public class KVMHypervisor extends AbsLibvirtHypervisor
{

    /**
     * Returns the connection string for the current hypervisor.
     * 
     * @param url Hypervisors address.
     * @return the hypervisor's connection string
     */
    @Override
    public String getHypervisorUrl(final URL url)
    {

        return "qemu+tcp://" + url.getHost() + "/system?no_tty=1";
    }

    /**
     * Returns the current hypervisor's type.
     * 
     * @return the hypervisor's type string
     */
    @Override
    public String getHypervisorType()
    {
        return "kvm";
    }
}
