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

package net.undf.abicloud.events
{
    import flash.events.Event;
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.vo.infrastructure.DataCenter;
    import net.undf.abicloud.vo.networking.NetworkConfiguration;
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.virtualappliance.Log;
    import net.undf.abicloud.vo.virtualappliance.VirtualAppliance;
    import net.undf.abicloud.vo.virtualappliance.VirtualDataCenter;

    public class VirtualApplianceEvent extends Event
    {
        /* ------------- Constants------------- */
        public static const GET_VIRTUALAPPLIANCES_BY_ENTERPRISE:String = "getVirtualAppliancesByEnterpriseVirtualApplianceEvent";

        public static const GET_VIRTUALAPPLIANCE_NODES:String = "getVirtualApplianceNodes_VirtualApplianceEvent";

        public static const CREATE_VIRTUALAPPLIANCE:String = "createVirtualApplianceVirtualApplianceEvent";

        public static const EDIT_VIRTUALAPPLIANCE:String = "editVirtualApplianceVirtualApplianceEvent";

        public static const APPLY_CHANGES_VIRTUAL_APPLIANCE:String = "applyChangesVirtualApplianceVirtualApplianceEvent";

        public static const DELETE_VIRTUALAPPLIANCE:String = "deleteVirtualApplianceVirtualApplianceEvent";

        public static const DELETE_VIRTUALAPPLIANCE_NON_BLOCKING:String = "deleteVirtualApplianceNonBlockingVirtualApplianceEvent";

        public static const START_VIRTUALAPPLIANCE:String = "startVirtualApplianceVirtualApplianceEvent";

        public static const SHUTDOWN_VIRTUALAPPLIANCE:String = "shutdownVirtualApplianceVirtualApplianceEvent";

        public static const GET_VIRTUAL_APPLIANCE_UPDATED_LOGS:String = "getVirtualApplianceUpdatedLogsVirtualApplianceEvent"

        public static const MARK_LOG_AS_DELETED:String = "markLogAsDeletedVirtualApplianceEvent"

        public static const FORCE_REFRESH_VIRTUAL_APPLIANCE_STATE:String = "forceRefreshVirtualApplianceStateVirtualApplianceEvent";

        public static const CANCEL_VIRTUAL_APPLIANCE_DEPLOYMENT:String = "cancelVirtualApplianceDeploymentVirtualApplianceEvent";

        public static const CHECK_VIRTUAL_DATACENTERS_AND_APPLIANCES_BY_ENTERPRISE:String = "checkVirtualDatacentersAndAppliancesByEnterpriseVirtualApplianceEvent";

        public static const CHECK_VIRTUAL_APPLIANCE:String = "checkVirtualApplianceVirtualApplianceEvent"

        public static const VIRTUAL_DATACENTERS_AND_APPLIANCES_CHECKED:String = "virtualDatacentersAndAppliancesCheckedVirtualApplianceEvent";

        public static const CREATE_VIRTUAL_APPLIANCE_BUNDLE:String = "createVirtualApplianceBundleVirtualApplianceEvent";

        public static const CREATE_VIRTUAL_APPLIANCE_BUNDLE_AND_UPDATE:String = "createVirtualApplianceBundleAndUpdateVirtualApplianceEvent";

        public static const VIRTUAL_APPLIANCE_NODES_RETRIEVED:String = "virtualApplianceNodesRetrievedVirtualApplianceEvent";

        public static const VIRTUAL_APPLIANCE_CREATED:String = "virtualApplianceCreatedVirtualApplianceEvent"

        public static const VIRTUAL_APPLIANCE_EDITED:String = "virtualApplianceEditedVirtualApplianceEvent";

        public static const VIRTUAL_APPLIANCE_SELECTED:String = "virtualApplianceSelectedVirtualApplianceEvent";

        public static const VIRTUAL_APPLIANCE_CHECKED:String = "virtualApplianceCheckedVirtualApplianceEvent";

        public static const VIRTUAL_APPLIANCE_BUNDLE_CREATED:String = "virtualApplianceBundleCreatedVirtualApplianceEvent";

        public static const VIRTUAL_APPLIANCE_DELETED:String = "virtualApplianceDeletedVirtualApplianceEvent";

        public static const GET_VIRTUAL_DATACENTERS_BY_ENTERPRISE:String = "getVirtualDataCentersVirtualApplianceEvent";

        public static const CREATE_VIRTUAL_DATACENTER:String = "createVirtualDataCenterVirtualApplianceEvent";

        public static const DELETE_VIRTUAL_DATACENTER:String = "deleteVirtualDataCenterVirtualApplianceEvent";

        public static const EDIT_VIRTUAL_DATACENTER:String = "editVirtualDataCenterVirtualApplianceEvent";

        public static const GET_VIRTUAL_DATACENTER_DHCP_CONF:String = "getVirtualDataCenterDHCPConfVirtualApplianceEvent";

        public static const VIRTUAL_DATACENTER_ADDED:String = "virtualDataCenterAddedVirtualApplianceEvent";

        public static const VIRTUAL_DATACENTER_EDITED:String = "virtualDataCenterEditedVirtualApplianceEvent";

        public static const VIRTUAL_DATACENTER_DELETED:String = "virtualDataCenterDeletedVirtualApplianceEvent";

	public static const CHECK_VIRTUAL_DATACENTERS_AND_APPLIANCES_BY_ENTERPRISE_AND_DATACENTER:String = "checkVirtualDatacentersAndAppliancesByEnterpriseAndDatacenterEvent";
	
	   public static const GET_VIRTUAL_DATACENTERS_BY_ENTERPRISE_FASTER:String = "getVirtualDataCenterByEnterpriseFasterVirtualApplianceEvent";

        /* ------------- Public atributes ------------- */
        public var virtualAppliance:VirtualAppliance;

        public var nodes:ArrayCollection;

        public var log:Log;

        public var enterprise:Enterprise;

        public var virtualDataCenter:VirtualDataCenter;

        public var force:Boolean = false;

        public var networkName:String;

        public var networkConfiguration:NetworkConfiguration;

        public var datacenter:DataCenter;


        /* ------------- Constructor ------------- */
        public function VirtualApplianceEvent(type:String, bubbles:Boolean = true,
                                              cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}
