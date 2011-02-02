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

package net.undf.abicloud.controller.metering
{
    import mx.collections.ArrayCollection;

    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;

    public class MeteringResultHandler extends ResultHandler
    {
        /* ------------- Constructor --------------- */
        public function MeteringResultHandler()
        {
            super();
        }

        public function getEventTypesResultHandler(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the event Types to model
                AbiCloudModel.getInstance().meteringManager.eventTypes = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem retrieving the list of event types
                super.handleResult(result);
            }
        }

        public function getSeverityTypesResultHandler(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the Severity Types to model
                AbiCloudModel.getInstance().meteringManager.severityTypes = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem retrieving the list of severity types
                super.handleResult(result);
            }
        }

        public function getEventsFilteredResultHandler(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the list of events filtered to model
                AbiCloudModel.getInstance().meteringManager.eventsFiltered = DataResult(result).data as ArrayCollection;
            }
            else
            {
                //There was a problem retrieving the events filtered list
                super.handleResult(result);
            }
        }
    }
}