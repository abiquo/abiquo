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

package com.abiquo.ovfmanager.cim;

import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.AutomaticRecoveryAction;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.AutomaticShutdownAction;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.AutomaticStartupAction;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.ChangeableType;
import org.dmtf.schemas.wbem.wscim._1.common.CimDateTime;

import com.abiquo.ovfmanager.cim.CIMTypesUtils.AutomaticRecoveryActionTypeEnum;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.AutomaticShutdownActionTypeEnum;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.AutomaticStartupActionTypeEnum;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.ChangeableTypeEnum;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

/**
 * TODO fill property definitions from ::: http
 * ://vmware.se/support/developer/cim-sdk/smash/u2/ga/apirefdoc/CIM_VirtualSystemSettingData.html
 * <li>arantxa.ii.uam.es/~networking/slides/VNE_ModeloGen�rico_WalterFuertes.pdf</li>
 */
public class CIMVirtualSystemSettingDataUtils
{
    public static VSSDType createVirtualSystemSettingData(String elementName, String instanceID,
        String virtualSystemIdentifier, String virtualSystemType) throws RequiredAttributeException
    {
        VSSDType vssd = new VSSDType();

        if (elementName == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData elementName");
        }
        if (instanceID == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData instanceID");
        }

        vssd.setElementName(CIMTypesUtils.createString(elementName));
        vssd.setInstanceID(CIMTypesUtils.createString(instanceID));

        vssd.setVirtualSystemIdentifier(CIMTypesUtils.createString(virtualSystemIdentifier));
        vssd.setVirtualSystemType(CIMTypesUtils.createString(virtualSystemType));

        return vssd;
    }

    public static VSSDType createVirtualSystemSettingData(String elementName, String instanceID,
        String description, Long generation, String caption, ChangeableTypeEnum changeableType)
        throws RequiredAttributeException
    {
        VSSDType vssd = new VSSDType();

        if (elementName == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData elementName");
        }
        if (instanceID == null)
        {
            throw new RequiredAttributeException("VirtualSystemSettingData instanceID");
        }

        vssd.setElementName(CIMTypesUtils.createString(elementName));
        vssd.setInstanceID(CIMTypesUtils.createString(instanceID));

        // Moving changeabletype from package resorceallocationSettingData to virtualsystem package
        ChangeableType rasdChangeable = CIMTypesUtils.createChangeableType(changeableType);
        if (rasdChangeable != null)
        {
            org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.ChangeableType vsChangeable =
                new org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_virtualsystemsettingdata.ChangeableType();
            vsChangeable.setValue(rasdChangeable.getValue());
            vssd.setChangeableType(vsChangeable);
        }
        else
        {
            vssd.setChangeableType(null);
        }

        vssd.setDescription(CIMTypesUtils.createString(description));
        vssd.setGeneration(CIMTypesUtils.createUnsignedLong(generation));
        vssd.setCaption(CIMTypesUtils.createCaptionVSSD(caption));

        return vssd;
    }

    public static void setRecoveryPropertiesToVSSettingData(VSSDType vssd, String recoveryFile,
        String snapshotDataRoot, String suspendDataRoot, String swapFileDataRoot)
        throws RequiredAttributeException
    {
        if (vssd == null)
        {
            throw new RequiredAttributeException("VSSDType");
        }
        vssd.setRecoveryFile(CIMTypesUtils.createString(recoveryFile));
        vssd.setSnapshotDataRoot(CIMTypesUtils.createString(snapshotDataRoot));
        vssd.setSuspendDataRoot(CIMTypesUtils.createString(suspendDataRoot));
        vssd.setSwapFileDataRoot(CIMTypesUtils.createString(swapFileDataRoot));
    }

    public static void setVirtualSystemToVSSettingData(VSSDType vssd,
        String virtualSystemIdentifier, String virtualSystemType) throws RequiredAttributeException
    {
        if (vssd == null)
        {
            throw new RequiredAttributeException("VSSDType");
        }
        vssd.setVirtualSystemIdentifier(CIMTypesUtils.createString(virtualSystemIdentifier));
        vssd.setVirtualSystemType(CIMTypesUtils.createString(virtualSystemType));

    }

    public static void setConfigurationPropertiesToVSSettingData(VSSDType vssd,
        String configurationID, String configurationName, String configurationFile,
        String logDataRoot, String configurationDataRoot) throws RequiredAttributeException
    {
        if (vssd == null)
        {
            throw new RequiredAttributeException("VSSDType");
        }
        vssd.setLogDataRoot(CIMTypesUtils.createString(logDataRoot));
        vssd.setConfigurationDataRoot(CIMTypesUtils.createString(configurationDataRoot));
        vssd.setConfigurationFile(CIMTypesUtils.createString(configurationFile));
        vssd.setConfigurationID(CIMTypesUtils.createString(configurationID));
        vssd.setConfigurationName(CIMTypesUtils.createString(configurationName));
    }

    /* TODO 
    public static void setCurrentCreationTimeToVSSettingData(VSSDType vssd)
        throws RequiredAttributeException
    {
        if (vssd == null)
        {
            throw new RequiredAttributeException("VSSDType");
        }
        vssd.setCreationTime(CIMTypesUtils.createCurrentTime());
    }*/

    // TODO CimDataTime to DataTiem
    public static void setLifeCicleActionPropertiesToVSSettingData(VSSDType vssd,
        AutomaticStartupActionTypeEnum automaticStartupAction,
        CimDateTime automaticStartupActionDelay, Short automaticStartupActionSequenceNumber,
        AutomaticRecoveryActionTypeEnum automaticRecoveryAction,
        AutomaticShutdownActionTypeEnum automaticShutdownAction) throws RequiredAttributeException
    {
        if (vssd == null)
        {
            throw new RequiredAttributeException("VSSDType");
        }

        if (automaticStartupAction != null)
        {
            AutomaticStartupAction startupAction = new AutomaticStartupAction();
            startupAction.setValue(String.valueOf(automaticStartupAction.getNumericARAType()));
            vssd.setAutomaticStartupAction(startupAction);
        }
        if (automaticShutdownAction != null)
        {
            AutomaticShutdownAction shutdownAction = new AutomaticShutdownAction();
            shutdownAction.setValue(String.valueOf(automaticShutdownAction.getNumericARAType()));
            vssd.setAutomaticShutdownAction(shutdownAction);
        }
        if (automaticRecoveryAction != null)
        {
            AutomaticRecoveryAction recoveryAction = new AutomaticRecoveryAction();
            recoveryAction.setValue(String.valueOf(automaticRecoveryAction.getNumericARAType()));
            vssd.setAutomaticRecoveryAction(recoveryAction);

        }

        vssd.setAutomaticStartupActionDelay(automaticStartupActionDelay);

        vssd.setAutomaticStartupActionSequenceNumber(CIMTypesUtils
            .createUnsignedShort(automaticStartupActionSequenceNumber));

    }

    public static void addNotesToVSSettingData(VSSDType vssd, List<String> notes)
        throws RequiredAttributeException
    {
        if (vssd == null)
        {
            throw new RequiredAttributeException("VSSDType");
        }

        for (String note : notes)
        {
            vssd.getNotes().add(CIMTypesUtils.createString(note));
        }
    }

}
