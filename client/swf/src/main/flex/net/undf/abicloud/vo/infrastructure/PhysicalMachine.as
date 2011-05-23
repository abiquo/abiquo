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



    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine")]
    public class PhysicalMachine extends InfrastructureElement
    {

        /* ------------- Public atributes ------------- */
        public var dataCenter:DataCenter;

        public var description:String;

        public var ram:int;

        public var cpu:int;

        public var cpuRatio:int;

        public var hd:Number;

        public var ramUsed:int;

        public var cpuUsed:int;

        public var hdUsed:Number;

        public var idState:int;

        public var realRam:int;

        public var realCpu:int;

        public var realStorage:Number;

        public var vswitchName:String;

        public var datastores:ArrayCollection; //Of DataStore
        
        public var idEnterprise:int;
        
        public var initiatorIQN:String;
    
    	public var ipmiIp:String;
    	
    	public var ipmiPort:int;
    	
		public var ipmiUser:String;

    	public var ipmiPassword:String;
        

		public var hypervisor:HyperVisor;

        /* ------------- Constants ------------- */
        public static const STATE_STOPPED:int = 0;

        public static const STATE_PROVISIONED:int = 1;

        public static const STATE_NOT_MANAGED:int = 2;

        public static const STATE_MANAGED:int = 3;

        public static const STATE_HALTED:int = 4;
        
        public static const STATE_UNLICENSED:int = 5;
        
        public static const STATE_HA_IN_PROGRESS:int = 6;
        
        public static const STATE_DISABLED_BY_HA:int = 7;

        /* ------------- Constructor ------------- */
        public function PhysicalMachine()
        {
            super();

            dataCenter = new DataCenter();
            description = "";
            ram = 0;
            cpu = 0;
            cpuRatio = 0;
            hd = 0;
            ramUsed = 0;
            cpuUsed = 0;
            hdUsed = 0;
            idState = STATE_MANAGED;
            realRam = 0;
            realCpu = 0;
            realStorage = 0;
            vswitchName = "";
            initiatorIQN = "";
            datastores = new ArrayCollection();
            ipmiIp = "";
			ipmiPort = 0;
			ipmiUser = "";
			ipmiPassword = "";    
        }

        override public function set assignedTo(iE:InfrastructureElement):void
        {
            if (iE is Rack || iE == null)
                _assignedTo = iE;
            else
                throw Error("A physical machine can only be assigned to a rack");
        }
    }
}
