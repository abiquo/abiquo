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

package net.undf.abicloud.vo.infrastructure
{
    import mx.collections.ArrayCollection;

    /**
     * Auxiliary class
     * This class is used to create a new Physical Machine, along with its Hypervisors
     * And to retrieve the result of a physical machine creation
     * @author Oliver
     *
     */
    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation")]
    public class PhysicalMachineCreation
    {
        public var physicalMachine:PhysicalMachine;

        public var hypervisors:ArrayCollection;

        public var resources:ArrayCollection;

        public function PhysicalMachineCreation()
        {
        }
    }
}