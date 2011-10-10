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

import java.io.File;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.ovf.envelope._1.ReferencesType;
import org.dmtf.schemas.ovf.envelope._1.VSSDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType.Icon;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.CIMResourceAllocationSettingDataType;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.ovfmanager.cim.CIMResourceAllocationSettingDataUtils;
import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.ovfmanager.ovf.section.OVFDiskUtils;
import com.abiquo.ovfmanager.ovf.section.OVFProductUtils;
import com.abiquo.ovfmanager.ovf.section.OVFVirtualHadwareSectionUtils;

/**
 * Creates an OVF document from a DiskInfo. (Mono disk and Mono virtual system Envelopes)
 */
public class OVFPackageInstanceToOVFEnvelope
{

    /** All the virtual system are created using these type. */
    private final static String VIRTUAL_SYSTEM_TYPE = "abiquo";

    /**
     * Use the imageSize
     **/
    public static EnvelopeType createEnvelopeFromOVFPackageInstance(OVFPackageInstanceDto disk)
    {
        EnvelopeType envelope = new EnvelopeType();
        ReferencesType references = new ReferencesType();

        final String diskPath = disk.getDiskFilePath();
        // final String packRelPath = getRelativePackagePath(disk.getOvfUrl(),
        // String.valueOf(disk.getIdEnterprise()));

        final Long diskSize = disk.getDiskFileSize();

        // final String completPath = packRelPath +'/'+diskPath;
        // System.err.println(packRelPath);

        try
        {
            FileType fileRef =
                OVFReferenceUtils.createFileType("diskFile", diskPath,
                    BigInteger.valueOf(diskSize), null, null);
            OVFReferenceUtils.addFile(references, fileRef);

            DiskSectionType diskSection = createDiskSection(disk);
            VirtualSystemType vsystem = createVirtualSystem(disk);
            ProductSectionType product = createProductSection(disk);

            OVFEnvelopeUtils.addSection(vsystem, product);
            OVFEnvelopeUtils.addSection(envelope, diskSection);
            OVFEnvelopeUtils.addVirtualSystem(envelope, vsystem);
            envelope.setReferences(references);
        }
        catch (Exception e)
        {
            throw new AMException(AMError.OVF_INSTALL,
                "Can not create the OVF from the DiskInfo",
                e);
        }

        return envelope;
    }

    /***
     * Use ImageType and HD
     */
    private static DiskSectionType createDiskSection(OVFPackageInstanceDto disk) throws Exception
    {
        DiskFormat format = DiskFormat.fromName(disk.getDiskFileFormat().name());

        final Long diskSize = disk.getDiskFileSize();

        // TODO getHDUnits
        VirtualDiskDescType diskDesc =
            OVFDiskUtils.createDiskDescription("ovfdisk", "diskFile", format, diskSize, null, null,
                null);

        DiskSectionType diskSection = new DiskSectionType();
        OVFDiskUtils.addDisk(diskSection, diskDesc);

        return diskSection;
    }

    /**
     * Use Description and name.
     */
    private static VirtualSystemType createVirtualSystem(OVFPackageInstanceDto disk)
        throws Exception
    {
        VirtualSystemType vsystem =
            OVFEnvelopeUtils.createVirtualSystem(disk.getName(), disk.getName(),
                disk.getDescription());

        VirtualHardwareSectionType vhs = createVirtualHardwareSection(disk);

        OVFEnvelopeUtils.addSection(vsystem, vhs);

        return vsystem;
    }

    /**
     * Use Description, Name and IconPath.
     */
    private static ProductSectionType createProductSection(OVFPackageInstanceDto disk)
        throws Exception
    {
        ProductSectionType product = new ProductSectionType();

        product.setInfo(CIMTypesUtils.createMsg(disk.getDescription(), "0"));
        product.setProduct(CIMTypesUtils.createMsg(disk.getName(), "0"));

        final String diskIcon = disk.getIconPath();
        if (diskIcon != null)
        {
            Icon icon = OVFProductUtils.createIcon(50, 50, "jpg", diskIcon); // XXX icon
            // details
            OVFProductUtils.addProductIcon(product, icon);
        }
        // warn

        return product;
    }

    /**
     * Use RAM, CPU, HD (and its Units) of the DiskInfo to build a VirtualHadwareSection to be used
     * on the OVF Envelope.
     */
    private static VirtualHardwareSectionType createVirtualHardwareSection(
        OVFPackageInstanceDto disk) throws RequiredAttributeException
    {
        VirtualHardwareSectionType vhsection = new VirtualHardwareSectionType();

        VSSDType vsystem = new VSSDType();
        vsystem.setVirtualSystemType(CIMTypesUtils.createString(VIRTUAL_SYSTEM_TYPE));
        vhsection.setSystem(vsystem);

        CIMResourceAllocationSettingDataType cpu =
            CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData("Cpu", "1",
                CIMResourceTypeEnum.Processor, disk.getCpu(), null);

        CIMResourceAllocationSettingDataType ram =
            CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData("Ram", "1",
                CIMResourceTypeEnum.Memory, disk.getRam(), disk.getRamSizeUnit().name());

        CIMResourceAllocationSettingDataType hd =
            CIMResourceAllocationSettingDataUtils.createResourceAllocationSettingData("Hd", "1",
                CIMResourceTypeEnum.Disk_Drive, disk.getHd(), disk.getHdSizeUnit().name());

        RASDType rasdCpu = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(cpu);
        RASDType rasdRam = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(ram);
        RASDType rasdHd = CIMResourceAllocationSettingDataUtils.createRASDTypeFromCIMRASD(hd);

        rasdHd.getHostResource().add(CIMTypesUtils.createString("ovf:/disk/ovfdisk"));

        OVFVirtualHadwareSectionUtils.addRASD(vhsection, rasdCpu);
        OVFVirtualHadwareSectionUtils.addRASD(vhsection, rasdRam);
        OVFVirtualHadwareSectionUtils.addRASD(vhsection, rasdHd);

        return vhsection;
    }

    public static EnvelopeType fixFilePathsAndSize(EnvelopeType envelope, final String snapshot,
        final String packagePath)

    {
        Set<String> diskFileIds = new HashSet<String>();

        DiskSectionType diskSection;

        try
        {

            diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        }
        catch (Exception e)
        {
            throw new AMException(AMError.OVF_BOUNDLE, String.format(
                "The bundle [%s] can not be created "
                    + "because the original envelope do not exist or do not have Disk Section",
                snapshot), e);
        }

        List<VirtualDiskDescType> disks = diskSection.getDisk();

        if (disks.isEmpty())
        {

            throw new AMException(AMError.OVF_BOUNDLE, String.format(
                "The bundle [%s] can not be created  because the "
                    + "original envelope do not contains any Disk", snapshot));
        }
        else
        {
            for (VirtualDiskDescType disk : disks)
            {
                diskFileIds.add(disk.getFileRef());
            }
        }

        // all the disk files should be marked with "snapshot + OVF_BUNDLE_PATH_IDENTIFIER" + hRef
        // (packagePath relative)
        for (String diskFileId : diskFileIds)
        {
            FileType file;
            try
            {
                file = OVFReferenceUtils.getReferencedFile(envelope, diskFileId);
            }
            catch (IdNotFoundException e)
            {
                throw new AMException(AMError.OVF_BOUNDLE, String.format(
                    "The bundle [%s] can not be created because the "
                        + "referenced file Id [%s] is not found on the Envelope", snapshot,
                    diskFileId), e);
            }

            // TODO check hRef is 'packagePath' relative
            final String relativeBundleFileRef = snapshot + file.getHref();
            final String absoluteBundleFileRef = packagePath + relativeBundleFileRef;

            File bundleFile = new File(absoluteBundleFileRef);
            Long bundleFileSize;

            if (!bundleFile.exists() || bundleFile.isDirectory())
            {

                throw new AMException(AMError.OVF_BOUNDLE, String.format(
                    "The bundle [%s] can not be created because the "
                        + "referenced file on [%s] is not found", snapshot, absoluteBundleFileRef));
            }
            else
            {
                // actually exist
                bundleFileSize = bundleFile.length(); // Bytes

                file.setSize(BigInteger.valueOf(bundleFileSize));
                file.setHref(relativeBundleFileRef);
            }

            for (VirtualDiskDescType vDiskDesc : disks)
            {
                if (diskFileId.equalsIgnoreCase(vDiskDesc.getFileRef()))
                {
                    // TODO Capacity == Disk file
                    vDiskDesc.setCapacity(String.valueOf(bundleFileSize));
                }
            }

        }// each disk file

        return envelope;
    }

}
