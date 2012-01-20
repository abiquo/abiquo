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
	/**
     * This class represents the status of a specifi task
     */

    [RemoteClass(alias="com.abiquo.abiserver.pojo.virtualappliance.TaskStatus")]
	[Bindable]
	public class TaskStatus
	{
		/*******
		 *  States
		 * 
		 *  FINISHED_SUCCESSFULLY,
		 * 
		 *  FINISHED_UNSUCCESSFULLY,
		 *
		 *  PENDING,
		 * 
		 *  STARTED,
		 * 
		 *  ABORTED
		 * 
		 * *****/
		 public static const FINISHED_SUCCESSFULLY:String = "FINISHED_SUCCESSFULLY";
		 public static const FINISHED_UNSUCCESSFULLY:String = "FINISHED_UNSUCCESSFULLY";
		 public static const PENDING:String = "PENDING";
		 public static const STARTED:String = "STARTED";
		 public static const ABORTED:String = "ABORTED";
		 
		 
        public var uuid:String;
        public var statusName:String;
        public var message:String;

		public function TaskStatus()
		{
			uuid = "";
			statusName = "";
			message = "";
		}

	}
}