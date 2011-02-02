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

package com.abiquo.ovfmanager.ovf.section;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;

import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.SectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;

public class OVFDiskUtils
{

    private final static Logger log = LoggerFactory.getLogger(OVFDiskUtils.class);

    /*******************************************************************************
     * DISK SECTION
     *******************************************************************************/

    public enum DiskSizeUnit
    {
        Bytes, KiloBytes, MegaBytes, GigaBytes
    } // TODO Bits, KiloBits, MegaBits, GigaBits, Words, DoubleWords, QuadWords

    private static BigInteger toBytes(long size, DiskSizeUnit unit)
    {
        BigInteger b1024 = BigInteger.valueOf(1024);
        BigInteger bytes = BigInteger.valueOf(size);

        switch (unit)
        {
            case GigaBytes:
                bytes = bytes.multiply(b1024);
            case MegaBytes:
                bytes = bytes.multiply(b1024);
            case KiloBytes:
                bytes = bytes.multiply(b1024);
            case Bytes:
            default:
                break;
        }

        return bytes;
    }

    /**
     * Adds a virtual disk description to mount an existing virtual image file on the provided OVF
     * envelope. If the fileId is not provided an empty disk is added. Create the DiskSection if
     * there is any DiskDesc on the OVF envelope.
     * 
     * @param envelope , the envelope containing the fileId on its references section and where the
     *            virtual disk description will be added.
     * @param diskId , the required desired disk identifier for the new virtual disk description.
     * @param fileID , the optional file identifier on the references section to be mounted on the
     *            virtual disk. If any provided its an empty disk.
     * @param format , the required file image format (hypervisor type)
     * @param capacity , the required disk capacity.
     * @param capacityUnit , the optional capacity units. If none specified assumed bytes.
     * @param populatedSize , the optional expected used size of the disk.
     * @param parentRefDiskId , the optional parent disk, modified blocks in comparison to a parent
     *            image.
     * @throw IdNotFound, it the provided fileId is not on the References section or the
     *        parentRefDiskId is not on the Disk section.
     * @throw IdAlreadyExists, it the provided diskId is already on the Disk section. TODO capacity
     *        can also be property on the envelope "${some.property}" from the ProductSection
     */
    public static void addDiskDescription(EnvelopeType envelope, String diskId, String fileId,
        DiskFormat format, Long capacity, DiskSizeUnit capacityUnit, Long populatedSize,
        String parentRefDiskId) throws IdNotFoundException, IdAlreadyExistsException
    {
        VirtualDiskDescType disk;
        FileType file = null;
        BigInteger byteCapacity;

        // TODO assert diskId/capacity not null
        // TODO log info

        disk = new VirtualDiskDescType();

        disk.setDiskId(diskId);
        disk.setFormat(format.getDiskFormatUri());
        // XXX can the referenced file extension be used to infer the disk
        // format ???

        if (fileId != null)
        {
            file = OVFReferenceUtils.getReferencedFile(envelope, fileId);
            disk.setFileRef(fileId);
        }

        if (capacityUnit != null && capacityUnit != DiskSizeUnit.Bytes)
        {
            disk.setCapacityAllocationUnits(capacityUnit.name());

            byteCapacity = toBytes(capacity, capacityUnit);
        }
        else
        {
            byteCapacity = BigInteger.valueOf(capacity);
        }

        // TODO if(capacity.startsWith("$"))
        disk.setCapacity(String.valueOf(capacity));

        // TODO check there aren't any disk with the same fileId. Also assert
        // the same order with
        // fileId (references and Disk sections must match)

        if (populatedSize != null)
        {
            if (file == null)
            {
                log.error("An empty disk can not have expected used capacity");
            }

            if (populatedSize > byteCapacity.longValue())
            {
                // TODO fail
                log.error("The populate size ({}b) is higher than the capacity ({}b)",
                    populatedSize, byteCapacity.longValue());
            }

            disk.setPopulatedSize(populatedSize);
        }

        if (parentRefDiskId != null)
        {
            getDiskDescription(envelope, parentRefDiskId);

            disk.setParentRef(parentRefDiskId);
        }
        else if (file != null)
        {
            if (byteCapacity.longValue() < file.getSize().longValue())
            {
                log.error("File size ({}) higher than the specified capacity ({})", file.getSize()
                    .longValue(), byteCapacity.longValue());
            }
        }

        addDisk(envelope, disk);
    }

    /**
     * Get a specific virtual disk description.
     * 
     * @param envelope , the OVF envelope to be checked.
     * @param diskId , the desired disk identifier.
     * @throws IdNotFoundException if there is any disk with the required identifier on the envelope
     *             disk section.
     */
    public static VirtualDiskDescType getDiskDescription(EnvelopeType envelope, String diskId)
        throws IdNotFoundException
    {
        DiskSectionType sectionDisk;
        SectionType section;

        for (JAXBElement< ? extends SectionType> jxbSection : envelope.getSection())
        {
            section = jxbSection.getValue();

            if (section instanceof DiskSectionType)
            {
                sectionDisk = (DiskSectionType) section;

                for (VirtualDiskDescType vdisk : sectionDisk.getDisk())
                {
                    if (diskId.equals(vdisk.getDiskId()))
                    {
                        return vdisk;
                    }
                }// disks
            }// disk section
        }// sections

        throw new IdNotFoundException("Virtual disk description id :" + diskId);
    }

    /**
     * TODO use envelope Adds a VirtualDiskDescription to an existing VirtualSystem.
     * 
     * @throws IdAlreadyExists if the provided VirtualDiscDescription id is already on the
     *             VirtualSystem's DiskSection.
     */
    public static void addDisk(EnvelopeType envelope, VirtualDiskDescType vDisk)
        throws IdAlreadyExistsException
    {
        DiskSectionType sectionDisk = null;

        try
        {
            sectionDisk = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        }
        catch (SectionNotPresentException e)
        {
            try
            {
                sectionDisk = OVFEnvelopeUtils.createSection(DiskSectionType.class, null);
                OVFEnvelopeUtils.addSection(envelope, sectionDisk);
            }
            catch (SectionException e1)
            {
                // from a SectionNotPresentException
            }
        }
        catch (InvalidSectionException invalid)
        {
            // Envelope can have disk section
        }

        for (VirtualDiskDescType vdd : sectionDisk.getDisk())
        {
            if (vDisk.getDiskId().equalsIgnoreCase(vdd.getDiskId()))
            {
                final String msg = "The VirtualDiskDescription diskId" + vDisk.getDiskId();
                throw new IdAlreadyExistsException(msg);
            }
        }

        sectionDisk.getDisk().add(vDisk);
    }

    /**
     * TODO use envelope Adds a VirtualDiskDescription to an existing VirtualSystem.
     * 
     * @throws IdAlreadyExists if the provided VirtualDiscDescription id is already on the
     *             VirtualSystem's DiskSection.
     */
    public static void addDisk(DiskSectionType diskSection, VirtualDiskDescType vDisk)
        throws IdAlreadyExistsException
    {

        for (VirtualDiskDescType vdd : diskSection.getDisk())
        {
            if (vDisk.getDiskId().equalsIgnoreCase(vdd.getDiskId()))
            {
                final String msg = "The VirtualDiskDescription diskId" + vDisk.getDiskId();
                throw new IdAlreadyExistsException(msg);
            }
        }

        diskSection.getDisk().add(vDisk);
    }

    /**
     * Adds a virtual disk description to mount an existing virtual image file. If the fileId is not
     * provided an empty disk is added. Create the DiskSection if there is any DiskDesc on the OVF
     * envelope. This method do not assure fileId is on the EnvelopeReferenceSection (use
     * addDiskDescription instead)
     * 
     * @param diskId , the required desired disk identifier for the new virtual disk description.
     * @param fileID , the optional file identifier on the references section to be mounted on the
     *            virtual disk. If any provided its an empty disk.
     * @param format , the required file image format (hypervisor type)
     * @param capacity , the required disk capacity.
     * @param capacityUnit , the optional capacity units. If none specified assumed bytes.
     * @param populatedSize , the optional expected used size of the disk.
     * @param parentRefDiskId , the optional parent disk, modified blocks in comparison to a parent
     *            image.
     * @throw IdNotFound, it the provided fileId is not on the References section or the
     *        parentRefDiskId is not on the Disk section.
     * @throw IdAlreadyExists, it the provided diskId is already on the Disk section. TODO capacity
     *        can also be property on the envelope "${some.property}" from the ProductSection
     */
    public static VirtualDiskDescType createDiskDescription(String diskId, String fileId,
        DiskFormat format, Long capacity, DiskSizeUnit capacityUnit, Long populatedSize,
        String parentRefDiskId)
    {
        VirtualDiskDescType disk;
        BigInteger byteCapacity;

        // TODO assert diskId/capacity not null
        // TODO log info

        disk = new VirtualDiskDescType();

        disk.setDiskId(diskId);
        disk.setFormat(format.getDiskFormatUri());

        if (fileId != null)
        {
            // file = OVFReferenceUtils.getReferencedFile(envelope, fileId);
            disk.setFileRef(fileId);
        }

        if (capacityUnit != null && capacityUnit != DiskSizeUnit.Bytes)
        {
            disk.setCapacityAllocationUnits(capacityUnit.name());

            byteCapacity = toBytes(capacity, capacityUnit);
        }
        else
        {
            byteCapacity = BigInteger.valueOf(capacity);
        }

        // TODO if(capacity.startsWith("$"))
        disk.setCapacity(String.valueOf(capacity));

        // TODO check there aren't any disk with the same fileId. Also assert
        // the same order with
        // fileId (references and Disk sections must match)

        if (populatedSize != null)
        {
            if (fileId == null)
            {
                log.error("An empty disk can not have expected used capacity");
            }

            if (populatedSize > byteCapacity.longValue())
            {
                // TODO fail
                log.error("The populate size ({}b) is higher than the capacity ({}b)",
                    populatedSize, byteCapacity.longValue());
            }

            disk.setPopulatedSize(populatedSize);
        }

        if (parentRefDiskId != null)
        {
            // getDiskDescription(envelope, parentRefDiskId);

            disk.setParentRef(parentRefDiskId);
        }/*
          * else if (file != null) { if (byteCapacity.longValue() < file.getSize().longValue()) {
          * log.error("File size ({}) higher than the specified capacity ({})", file.getSize()
          * .longValue(), byteCapacity.longValue()); } }
          */

        return disk;
    }

}
