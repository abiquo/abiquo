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

package net.undf.abicloud.vo.virtualappliance
{
	import mx.collections.ArrayCollection;
	

    [RemoteClass(alias="com.abiquo.server.core.task.Task")]
    [Bindable]
    public class Task
    {
        //Task STATES
        public static const FINISHED_SUCCESSFULLY:String = "FINISHED_SUCCESSFULLY";
        public static const FINISHED_UNSUCCESSFULLY:String = "FINISHED_UNSUCCESSFULLY";
        public static const PENDING:String = "PENDING";
        public static const STARTED:String = "STARTED";
        public static const ABORTED:String = "ABORTED";
        
        //Task TYPES
        public static const DEPLOY:String = "DEPLOY";
        public static const UNDEPLOY:String = "UNDEPLOY";
        public static const RECONFIGURE:String = "RECONFIGURE";
        public static const POWER_ON:String = "POWER_ON";
        public static const POWER_OFF:String = "POWER_OFF";
        public static const PAUSE:String = "PAUSE";
        public static const RESUME:String = "RESUME";
        public static const RESET:String = "RESET";
        public static const SNAPSHOT:String = "SNAPSHOT";
        public static const HIGH_AVAILABILITY:String = "HIGH_AVAILABILITY";
        
        public var ownerId:String;
	    public var taskId:String;
	    public var userId:String;
	    public var type:String;
	    public var timestamp:Number;
	    public var state:String;
	    public var jobs:ArrayCollection;

        public function Task()
        {
            ownerId = "";
	        taskId = "";
	        userId = "";
	        type = "";
	        timestamp = 0;
	        state = "";
	        jobs =  new ArrayCollection();
        }

    }
}