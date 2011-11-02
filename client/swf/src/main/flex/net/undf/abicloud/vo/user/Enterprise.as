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

package net.undf.abicloud.vo.user
{
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.vo.virtualhardware.ResourceAllocationLimit;

    [RemoteClass(alias="com.abiquo.abiserver.pojo.user.Enterprise")]
    [Bindable]
    public class Enterprise
    {
        public var id:int;

        public var name:String;
        
        public var isReservationRestricted:Boolean;

        public var deleted:Boolean;
        
        public var limits:ResourceAllocationLimit;

        /******
        * 
        * allowedDatacenter is an array containing all allowed datacenters for this enterprise
        * when a new DC is created (set tio 'allowed), it will be added automatically to this list 
        * 
        * ****/
        
        public var dcLimits:ArrayCollection;
        
        public var reservedMachines:ArrayCollection;

		//FIXME: this is hardcoded. Should be aquire from server         
        public var defaultTheme:String ="abicloudDefault";
        
        /*******
        * 
        * Related with Chef integration
        * 
        * ******/
        public var chefURL:String;
        
        public var chefValidatorCertificate:String;
        
        public var chefValidator:String;
        
        public var chefClient:String;
        
        public var chefClientCertificate:String;

        public function Enterprise()
        {
            id = 0;
            name = "";
            isReservationRestricted = false;
            deleted = false;
            limits = new ResourceAllocationLimit();
            dcLimits = new ArrayCollection();
            reservedMachines = new ArrayCollection();
	       isReservationRestricted = false;
        }

    }
}
