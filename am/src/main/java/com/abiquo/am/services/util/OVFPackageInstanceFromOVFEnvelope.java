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

package com.abiquo.am.services.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType.Icon;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.ResourceType;
import org.dmtf.schemas.wbem.wscim._1.common.CimString;

import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.RepositoryException;
import com.abiquo.appliancemanager.transport.MemorySizeUnit;
import com.abiquo.appliancemanager.transport.OVFPackageDiskFormat;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;

public class OVFPackageInstanceFromOVFEnvelope
{

    /**
     * TODO re-DOC <br>
     * REQUIRE THE OVFID IS PLACED ON A REMOTE LOCATION (WARINING on generation)<BR>
     * Creates a list of VirtualInfo relative to the VirtualDisk (from the disk section) and
     * requirements (from the VirtualHardwareSection) contained on the provided OVF envelope. Used
     * to add VirtualImage on database once an OVFPackage completes its download. No duplicated Disk
     * are returned.
     * 
     * @param ovfDescription, the OVF description of the required OVF package.
     * @return an array containing all the DiskInfo properties and requirements.
     * @throws RepositoryException, if the envelope do not contain the required info to get
     *             VirtualDisk information.
     **/
    public static OVFPackageInstanceDto getDiskInfo(String ovfId, EnvelopeType envelope)
    // TODO String userID, String category
        throws AMException
    {

        OVFPackageInstanceDto diskInfo = null;

        Map<String, VirtualDiskDescType> diskIdToDiskFormat =
            new HashMap<String, VirtualDiskDescType>();
        Map<String, FileType> fileIdToFileType = new HashMap<String, FileType>();
        Map<String, List<String>> diskIdToVSs = new HashMap<String, List<String>>();
        Map<String, OVFPackageInstanceDto> requiredByVSs =
            new HashMap<String, OVFPackageInstanceDto>();
        DiskSectionType diskSectionType;

        try
        {
            ContentType contentType = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

            ProductSectionType product =
                OVFEnvelopeUtils.getSection(contentType, ProductSectionType.class);

            String description = null;
            if (product.getInfo() != null && product.getInfo().getValue() != null)
            {
                description = product.getInfo().getValue();
            }

            String iconPath = getIconPath(product, fileIdToFileType, ovfId);

            diskSectionType = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);

            for (FileType fileType : envelope.getReferences().getFile())
            {
                fileIdToFileType.put(fileType.getId(), fileType);
            }
            // Create a hash
            for (VirtualDiskDescType virtualDiskDescType : diskSectionType.getDisk())
            {
                diskIdToDiskFormat.put(virtualDiskDescType.getDiskId(), virtualDiskDescType);
            }

            if (contentType instanceof VirtualSystemType)
            {

                VirtualSystemType vs = (VirtualSystemType) contentType;
                OVFPackageInstanceDto req = getDiskInfo(vs, diskIdToDiskFormat, diskIdToVSs);

                requiredByVSs.put(vs.getId(), req);
            }
            else if (contentType instanceof VirtualSystemCollectionType)
            {
                List<VirtualSystemType> virtualSystems =
                    OVFEnvelopeUtils.getVirtualSystems((VirtualSystemCollectionType) contentType);

                for (VirtualSystemType virtualSystemType : virtualSystems)
                {
                    OVFPackageInstanceDto req =
                        getDiskInfo(virtualSystemType, diskIdToDiskFormat, diskIdToVSs);

                    requiredByVSs.put(virtualSystemType.getId(), req);
                }
            }

            for (VirtualDiskDescType diskDescType : diskIdToDiskFormat.values())
            {

                String diskId = diskDescType.getDiskId();
                String fileId = diskDescType.getFileRef();

                if (!fileIdToFileType.containsKey(fileId))
                {
                    String msg = "File Id [" + fileId + "] not found on the ReferencesSection";
                    throw new IdNotFoundException(msg);
                }

                FileType file = fileIdToFileType.get(fileId);
                final String filePath = file.getHref();
                final Long fileSize = file.getSize().longValue();

                String format = diskDescType.getFormat(); // XXX using the same format on the OVF
                // disk

                if (!diskIdToVSs.containsKey(diskId))
                {
                    throw new IdNotFoundException("Any virtualSystem is using diskId[" + diskId
                        + "]"); // XXX warning ??
                }

                if (diskIdToVSs.size() != 1)
                {
                    final String cause =
                        String.format(
                            "There are more than one virtual system on the OVF Envelope [%s]",
                            ovfId);
                    throw new AMException(Status.PRECONDITION_FAILED, cause);
                }

                for (String vssName : diskIdToVSs.get(diskId))
                {
                    diskInfo = new OVFPackageInstanceDto();
                    diskInfo.setName(vssName);
                    diskInfo.setOvfUrl(ovfId);

                    DiskFormat diskFormat = DiskFormat.fromValue(format);
                    OVFPackageDiskFormat ovfDiskFormat =
                        OVFPackageDiskFormat.valueOf(diskFormat.name());

                    diskInfo.setDiskFileFormat(ovfDiskFormat);
                    diskInfo.setDiskFileSize(fileSize);

                    // Note that getHRef() will now return the relative path
                    // of the file at the downloaded repository space
                    diskInfo.setDiskFilePath(filePath);

                    diskInfo.setIconPath(iconPath);
                    diskInfo.setDescription(description);
                    // XXX diskInfo.setSO(value);

                    if (!requiredByVSs.containsKey(vssName))
                    {
                        throw new IdNotFoundException("VirtualSystem id not found [" + vssName
                            + "]");
                    }

                    OVFPackageInstanceDto requirement = requiredByVSs.get(vssName);

                    // XXX disk format ::: diskInfo.setImageType(requirement.getImageType());

                    diskInfo.setCpu(requirement.getCpu());
                    diskInfo.setRam(requirement.getRam());
                    diskInfo.setHd(requirement.getHd());

                    diskInfo.setRamSizeUnit(requirement.getRamSizeUnit());
                    diskInfo.setHdSizeUnit(requirement.getHdSizeUnit());

                    // TODO diskInfo.setEnterpriseId(enterpriseId);
                    // diskInfo.setUserId(userId); TODO user ID
                    // diskInfo.getCategories().add(category); TODO category

                }// all vss
            }// all disks
        }
        catch (EmptyEnvelopeException e)
        {
            String msg = "No VirtualSystem or VirtualSystemCollection exists for this OVF package";

            throw new AMException(Status.NO_CONTENT, msg, e);
        }
        catch (Exception e)
        {
            throw new AMException(Status.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        if (diskInfo == null)
        {
            final String msg =
                "No VirtualSystem or VirtualSystemCollection exists for this OVF package";
            throw new AMException(Status.NO_CONTENT, msg);
        }

        return diskInfo;
    }

    /**
     * Only the first icon on product section is used
     */
    private static String getIconPath(final ProductSectionType product,
        final Map<String, FileType> fileIdToFileType, final String ovfId)
    {
        List<Icon> icons = product.getIcon();
        if (icons == null || icons.size() == 0)
        {
            return null;
        }

        final Icon icon = icons.get(0);
        final String iconRef = icon.getFileRef();

        if (iconRef.startsWith("http"))
        {
            return iconRef;
        }

        if (!fileIdToFileType.containsKey(iconRef))
        {
            return null; // XXX log cause
        }

        final String iconPathRelative = fileIdToFileType.get(iconRef).getHref();

        if (iconPathRelative.startsWith("http"))
        {
            return iconPathRelative;
        }

        final String ovfAbs = ovfId.substring(0, ovfId.lastIndexOf('/'));

        return ovfAbs + '/' + iconPathRelative;
    }

    /**
     * TODO TBD
     **/
    private static OVFPackageInstanceDto getDiskInfo(VirtualSystemType vsystem,
        Map<String, VirtualDiskDescType> diskDescByName, Map<String, List<String>> diskIdToVSs)
        throws IdAlreadyExistsException, RequiredAttributeException, SectionNotPresentException
    {
        OVFPackageInstanceDto dReq = new OVFPackageInstanceDto();
        VirtualHardwareSectionType hardwareSectionType;

        try
        {
            hardwareSectionType =
                OVFEnvelopeUtils.getSection(vsystem, VirtualHardwareSectionType.class);
        }
        catch (InvalidSectionException e)
        {
            throw new SectionNotPresentException("VirtualHardware on a virtualSystem", e);
        }

        dReq.setCpu(-1);
        dReq.setHd(Long.valueOf(-1));
        dReq.setRam(Long.valueOf(-1));

        // XXX now we are using ONLY the Disk format

        // XXX String vsType = hardwareSectionType.getSystem().getVirtualSystemType().getValue();
        // XXX dReq.setImageType(vsType);
        // XXX log.debug("Using ''virtualSystemType'' [{}]", vsType);

        for (RASDType rasdType : hardwareSectionType.getItem())
        {
            ResourceType resourceType = rasdType.getResourceType();
            int resTnumeric = Integer.parseInt(resourceType.getValue());

            // TODO use CIMResourceTypeEnum from value and then a SWITCH

            // Get the information on the ram
            if (CIMResourceTypeEnum.Processor.getNumericResourceType() == resTnumeric)
            {
                String cpuVal = rasdType.getVirtualQuantity().getValue().toString();

                dReq.setCpu(Integer.parseInt(cpuVal));

                // TODO if(rasdType.getAllocationUnits()!= null)
            }
            else if (CIMResourceTypeEnum.Memory.getNumericResourceType() == resTnumeric)
            {
                BigInteger ramVal = rasdType.getVirtualQuantity().getValue();

                dReq.setRam(ramVal.longValue());

                if (rasdType.getAllocationUnits() != null
                    & rasdType.getAllocationUnits().getValue() != null)
                {
                    final String allocationUnits = rasdType.getAllocationUnits().getValue();

                    final MemorySizeUnit ramSizeUnit = getMemoryUnitsFromOVF(allocationUnits);

                    dReq.setRamSizeUnit(ramSizeUnit);
                }
            }
            else if (CIMResourceTypeEnum.Disk_Drive.getNumericResourceType() == resTnumeric)
            {
                // HD requirements are extracted from the associated Disk on ''hostResource''
                String diskId = getVirtualSystemDiskId(rasdType.getHostResource());

                if (!diskDescByName.containsKey(diskId))
                {
                    String msg = "DiskId [" + diskId + "] not found on disk section";
                    throw new IdAlreadyExistsException(msg);
                }

                if (!diskIdToVSs.containsKey(diskId))
                {
                    List<String> vss = new LinkedList<String>();
                    vss.add(vsystem.getId()); // XXX

                    diskIdToVSs.put(diskId, vss);
                }
                else
                {
                    diskIdToVSs.get(diskId).add(vsystem.getId());
                }

                VirtualDiskDescType diskDescType = diskDescByName.get(diskId);

                String capacity = diskDescType.getCapacity();

                dReq.setHd(Long.parseLong(capacity));

                final String allocationUnits = diskDescType.getCapacityAllocationUnits();
                final MemorySizeUnit hdSizeUnit = getMemoryUnitsFromOVF(allocationUnits);

                dReq.setHdSizeUnit(hdSizeUnit);
                // dReq.setImageSize(diskDescType.get);
            }
        }// rasd

        if (dReq.getCpu() == -1)
        {
            throw new RequiredAttributeException("Not CPU RASD element found on the envelope");
        }
        if (dReq.getRam() == -1)
        {
            throw new RequiredAttributeException("Not RAM RASD element found on the envelope");
        }
        if (dReq.getHd() == -1)
        {
            throw new RequiredAttributeException("Not HD RASD element found on the envelope");
        }

        return dReq;
    }

    /**
     * default is byte
     * 
     * @throws RequiredAttributeException
     */
    private static MemorySizeUnit getMemoryUnitsFromOVF(final String allocationUnits)
        throws RequiredAttributeException
    {

        if (allocationUnits == null || "byte".equalsIgnoreCase(allocationUnits)
            || "bytes".equalsIgnoreCase(allocationUnits))
        {
            return MemorySizeUnit.BYTE;
        }
        else if ("byte * 2^10".equals(allocationUnits) || "KB".equalsIgnoreCase(allocationUnits)
            || "KILOBYTE".equalsIgnoreCase(allocationUnits)
            || "KILOBYTES".equalsIgnoreCase(allocationUnits)) // kb
        {
            return MemorySizeUnit.KILOBYTE;
        }
        else if ("byte * 2^20".equals(allocationUnits) || "MB".equalsIgnoreCase(allocationUnits)
            || "MEGABYTE".equalsIgnoreCase(allocationUnits)
            || "MEGABYTES".equalsIgnoreCase(allocationUnits)) // mb
        {
            return MemorySizeUnit.MEGABYTE;
        }
        else if ("byte * 2^30".equals(allocationUnits) || "GB".equalsIgnoreCase(allocationUnits)
            || "GIGABYTE".equalsIgnoreCase(allocationUnits)
            || "GIGABYTES".equalsIgnoreCase(allocationUnits)) // gb
        {
            return MemorySizeUnit.GIGABYTE;
        }
        else if ("byte * 2^40".equals(allocationUnits) || "TB".equalsIgnoreCase(allocationUnits)
            || "TERABYTE".equalsIgnoreCase(allocationUnits)
            || "TERABYTES".equalsIgnoreCase(allocationUnits)) // tb
        {
            return MemorySizeUnit.TERABYTE;
        }
        else
        {
            final String msg =
                "Unknow disk capacityAllocationUnits factor [" + allocationUnits + "]";

            throw new RequiredAttributeException(msg);
        }
    }

    /**
     * Decode CimStrings (on the OVF namespce) on the Disk RASD's HostResource attribute to delete
     * the ''ovf://disk/'' prefix
     **/
    private static String getVirtualSystemDiskId(List<CimString> cimStrs)
    {
        String cimStringVal = "";
        for (CimString cimString : cimStrs)
        {
            cimStringVal = cimString.getValue();

            if (cimStringVal.startsWith("ovf:/disk/"))
            {
                cimStringVal = cimStringVal.replace("ovf:/disk/", "");
                break;
            }
            else if (cimStringVal.startsWith("/disk/"))
            {
                cimStringVal = cimStringVal.replace("/disk/", "");
                break;
            }
        }

        return cimStringVal;
    }

    /**
     * Gets the disk capacity on bytes.
     * 
     * @param capacity, numeric value
     * @param alloctionUnit, bytes by default but can be Kb, Mb, Gb or Tb.
     * @return capacity on bytes private static BigInteger getBytes(String capacity, String
     *         allocationUnits) { BigInteger capa = new BigInteger(capacity); if(allocationUnits ==
     *         null) { return capa; } if("byte".equals(allocationUnits)) { return capa; } BigInteger
     *         factor = new BigInteger("2"); if("byte * 2^10".equals(allocationUnits)) // kb {
     *         factor.pow(10); } else if("byte * 2^20".equals(allocationUnits)) // mb {
     *         factor.pow(20); } else if("byte * 2^30".equals(allocationUnits)) // gb {
     *         factor.pow(30); } else if("byte * 2^40".equals(allocationUnits)) // tb {
     *         factor.pow(40); } else {
     *         System.err.println("Unknow capacityAllocationUnits factor ["+allocationUnits+"]");
     *         return capa; } return capa.multiply(factor); }
     */

    /**
     * Decode CimStrings (on the OVF namespce) on the Disk RASD's HostResource attribute to delete
     * the ''ovf://disk/'' prefix private static String getVirtualSystemDiskId(List<CimString>
     * cimStrs) { String cimStringVal = ""; for (CimString cimString : cimStrs) { cimStringVal =
     * cimString.getValue(); if (cimStringVal.startsWith("ovf:/disk/")) { cimStringVal =
     * cimStringVal.replace("ovf:/disk/", ""); break; } } return cimStringVal; }
     */

}
