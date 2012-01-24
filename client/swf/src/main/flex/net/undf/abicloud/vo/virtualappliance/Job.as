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

    [RemoteClass(alias="com.abiquo.server.core.task.Job")]
    [Bindable]
    public class Job
    {

        //Job STATES
        public static const PENDING:String = "PENDING";
        public static const STARTED:String = "STARTED";
        public static const DONE:String = "DONE";
        public static const FAILED:String = "FAILED";
        public static const SKIPPED:String = "SKIPPED";
        public static const ROLLBACK_STARTED:String = "ROLLBACK_STARTED";
        public static const ROLLBACK_DONE:String = "ROLLBACK_DONE";
        public static const UNKNOWN:String = "UNKNOWN";
        
        //Job TYPES
        public static const CONFIGURE:String = "CONFIGURE";
        public static const DECONFIGURE:String = "DECONFIGURE";
        public static const RECONFIGURE:String = "RECONFIGURE";
        public static const POWER_ON:String = "POWER_ON";
        public static const POWER_OFF:String = "POWER_OFF";
        public static const PAUSE:String = "PAUSE";
        public static const RESUME:String = "RESUME";
        public static const RESET:String = "RESET";
        public static const SNAPSHOT:String = "SNAPSHOT";
        
        public var id:String;
	    public var type:String;
	    public var state:String;
	    public var rollbackState:String;
	    public var description:String;
	    public var parentTaskId:String;
	    public var timestamp:Number;
	    public var data:Object;
	
        public function Job()
        {
            id = "";
	        type = "";
	        state = "";
	        rollbackState = "";
	        description = "";
	        parentTaskId = "";
	        timestamp = 0;
	        data = new Object();
        }

    }
}