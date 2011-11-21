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

package com.abiquo.am.services.download;

import static com.abiquo.am.services.OVFPackageConventions.getFileUrl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.dmtf.schemas.ovf.envelope._1.MsgType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.RASDType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.VirtualHardwareSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.dmtf.schemas.wbem.wscim._1.cim_schema._2.cim_resourceallocationsettingdata.ResourceType;
import org.dmtf.schemas.wbem.wscim._1.common.CimString;
import org.springframework.stereotype.Component;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.transport.MemorySizeUnit;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

@Component
public class OVFDocumentFetch
{
    /** Timeout for all the HTTP connections. */
    private final static Integer httpTimeout = AMConfigurationManager.getInstance()
        .getAMConfiguration().getTimeout();

    private InputStream openHTTPConnection(final String ovfid) throws DownloadException
    {
        URL target;

        try
        {
            target = new URL(ovfid);
            URLConnection connection = target.openConnection();

            connection.setUseCaches(true);
            // XXX auth related --- connection.setAllowUserInteraction(true);

            connection.setReadTimeout(httpTimeout);
            connection.setConnectTimeout(httpTimeout);

            return connection.getInputStream();
        }
        catch (MalformedURLException murl)
        {
            final String msg =
                String.format("The provided OVF identifier [%s] is not an URL", ovfid);
            throw new DownloadException(msg, murl);
        }
        catch (IOException e)
        {
            final String msg =
                String.format("Unable open an inputstream for the location [%s]", ovfid);
            throw new DownloadException(msg, e);

        }

    }

    /**
     * XXX
     * 
     * @throws DownloadException, if can not connect to the OVF location of if the envelope document
     *             is invalid.
     */
    public EnvelopeType obtainEnvelope(final String ovfId)
    {
        InputStream evelopeStream = openHTTPConnection(ovfId);

        EnvelopeType envelope;
        try
        {
            envelope = OVFSerializer.getInstance().readXMLEnvelope(evelopeStream);

            envelope = fixOVfDocument(ovfId, envelope);

            checkEnvelopeIsValid(envelope);
        }
        catch (Exception e)
        {
            if (e instanceof AMException)
            {
                throw (AMException) e;
            }

            throw new AMException(AMError.OVF_INVALID, e);
        }
        finally
        {
            try
            {
                evelopeStream.close();
            }
            catch (IOException e)
            {
                throw new AMException(AMError.OVF_INSTALL, //
                    "Can't close the http connection to " + ovfId,
                    e);
            }
        }

        return envelope;
    }

    public EnvelopeType fixOVfDocument(final String ovfId, EnvelopeType envelope)
    {
        try
        {
            envelope = fixDiskFormtatUriAndFileSizes(envelope, ovfId);
            envelope = fixMissingProductSection(envelope);
        }
        catch (Exception e)
        {
            throw new AMException(AMError.OVF_INVALID, e);
        }

        return envelope;
    }

    public EnvelopeType checkEnvelopeIsValid(final EnvelopeType envelope)
    {
        try
        {
            Map<String, VirtualDiskDescType> diskIdToDiskFormat =
                new HashMap<String, VirtualDiskDescType>();
            Map<String, FileType> fileIdToFileType = new HashMap<String, FileType>();

            DiskSectionType diskSectionType =
                OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);

            for (FileType fileType : envelope.getReferences().getFile())
            {
                fileIdToFileType.put(fileType.getId(), fileType);
            }
            // Create a hash
            for (VirtualDiskDescType virtualDiskDescType : diskSectionType.getDisk())
            {
                diskIdToDiskFormat.put(virtualDiskDescType.getDiskId(), virtualDiskDescType);
            }

            // /

            ContentType content = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

            if (content instanceof VirtualSystemCollectionType)
            {
                throw new EmptyEnvelopeException("Current OVF description document includes a VirtualSystemCollection, "
                    + "abicloud only deal with single virtual system based OVFs");
            }

            VirtualSystemType vsystem = (VirtualSystemType) content;

            VirtualHardwareSectionType hardwareSectionType;
            Integer cpu = null;
            Long hd = null;
            Long ram = null;

            try
            {
                hardwareSectionType =
                    OVFEnvelopeUtils.getSection(vsystem, VirtualHardwareSectionType.class);
            }
            catch (InvalidSectionException e)
            {
                throw new SectionNotPresentException("VirtualHardware on a virtualSystem", e);
            }

            for (RASDType rasdType : hardwareSectionType.getItem())
            {
                ResourceType resourceType = rasdType.getResourceType();
                int resTnumeric = Integer.parseInt(resourceType.getValue());

                // TODO use CIMResourceTypeEnum from value and then a SWITCH

                // Get the information on the ram
                if (CIMResourceTypeEnum.Processor.getNumericResourceType() == resTnumeric)
                {
                    String cpuVal = rasdType.getVirtualQuantity().getValue().toString();

                    cpu = Integer.parseInt(cpuVal);
                }
                else if (CIMResourceTypeEnum.Memory.getNumericResourceType() == resTnumeric)
                {
                    BigInteger ramVal = rasdType.getVirtualQuantity().getValue();

                    ram = ramVal.longValue();

                    if (rasdType.getAllocationUnits() != null
                        & rasdType.getAllocationUnits().getValue() != null)
                    {
                        final String allocationUnits = rasdType.getAllocationUnits().getValue();

                        final MemorySizeUnit ramSizeUnit = getMemoryUnitsFromOVF(allocationUnits);
                    }
                }
                else if (CIMResourceTypeEnum.Disk_Drive.getNumericResourceType() == resTnumeric)
                {
                    // HD requirements are extracted from the associated Disk on ''hostResource''
                    String diskId = getVirtualSystemDiskId(rasdType.getHostResource());

                    if (!diskIdToDiskFormat.containsKey(diskId))
                    {
                        throw new RequiredAttributeException("Virtual System make reference to an undeclared disk "
                            + diskId);
                    }

                    VirtualDiskDescType diskDescType = diskIdToDiskFormat.get(diskId);

                    String capacity = diskDescType.getCapacity();

                    hd = Long.parseLong(capacity);

                    final String allocationUnits = diskDescType.getCapacityAllocationUnits();
                    final MemorySizeUnit hdSizeUnit = getMemoryUnitsFromOVF(allocationUnits);
                }
            }// rasd

            if (cpu == null)
            {
                throw new RequiredAttributeException("Not CPU RASD element found on the envelope");
            }
            if (ram == null)
            {
                throw new RequiredAttributeException("Not RAM RASD element found on the envelope");
            }
            if (hd == null)
            {
                throw new RequiredAttributeException("Not HD RASD element found on the envelope");
            }

        }
        catch (Exception e)
        {
            throw new AMException(AMError.OVF_INVALID, e);
        }

        return envelope;
    }

    private EnvelopeType fixDiskFormtatUriAndFileSizes(final EnvelopeType envelope,
        final String ovfId) throws SectionNotPresentException, InvalidSectionException
    {

        DiskSectionType diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);

        if (diskSection.getDisk().size() != 1)
        {
            final String message =
                "abicloud only supports single disk definition on the OVF, the current envelope contains multiple disk";
            throw new InvalidSectionException(message);
        }

        VirtualDiskDescType vdisk = diskSection.getDisk().get(0);

        String formatUri = vdisk.getFormat();

        if (StringUtils.isEmpty(formatUri))
        {
            final String message = "Missing ''format'' attribute for the Disk element";
            throw new InvalidSectionException(message);
        }

        DiskFormatType format = DiskFormatType.fromURI(formatUri);

        if (format == null) // the format URI isn't on the abicloud enumeration. FIX it
        {
            // vbox/vmware
            // http://www.vmware.com/interfaces/specifications/vmdk.html#streamOptimized
            // abiquo
            // http://www.vmware.com/technical-resources/interfaces/vmdk_access.html#streamOptimized

            if (formatUri.contains("interfaces/specifications/vmdk.html"))
            {
                formatUri =
                    formatUri.replace("interfaces/specifications/vmdk.html",
                        "technical-resources/interfaces/vmdk_access.html");

                format = DiskFormatType.fromURI(formatUri);

                if (format == null)
                {
                    throw new InvalidSectionException(String.format(
                        "Invalid disk format type [%s]", formatUri));
                }

                vdisk.setFormat(formatUri);
            }

        }

        try
        {
            for (FileType ftype : envelope.getReferences().getFile())
            {
                if (ftype.getSize() == null)
                {
                    String fileUrl = getFileUrl(ftype.getHref(), ovfId);
                    Long size = getFileSizeFromHttpHead(fileUrl);

                    ftype.setSize(BigInteger.valueOf(size));
                }
            }
        }
        catch (Exception e)
        {
            throw new InvalidSectionException(String.format("Invalid File References section "
                + "(check all the files on the OVF document contains the ''size'' attribute):\n",
                e.toString()));
        }

        return envelope;
    }

    private final static String CONTENT_LENGTH = "Content-Length";

    private Long getFileSizeFromHttpHead(final String fileUrl) throws DownloadException
    {
        try
        {
            URLConnection connection = new URL(fileUrl).openConnection();

            connection.setUseCaches(true);
            connection.setReadTimeout(httpTimeout);
            connection.setConnectTimeout(httpTimeout);

            String contentLenght = connection.getHeaderField(CONTENT_LENGTH);

            return Long.parseLong(contentLenght);
        }
        catch (Exception e)
        {
            throw new DownloadException(String.format("Can not obtain file [%s] size", fileUrl), e);
        }
    }

    /**
     * If product section is not present use the VirtualSystemType to set it.
     * 
     * @throws EmptyEnvelopeException
     */
    private EnvelopeType fixMissingProductSection(final EnvelopeType envelope)
        throws InvalidSectionException, EmptyEnvelopeException
    {

        ContentType contentType = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        if (!(contentType instanceof VirtualSystemType))
        {
            throw new InvalidSectionException("abicloud only suport single virtual system definition,"
                + " current OVF envelope defines a VirtualSystemCollection");
        }

        VirtualSystemType vsystem = (VirtualSystemType) contentType;

        try
        {
            OVFEnvelopeUtils.getSection(vsystem, ProductSectionType.class);
        }
        catch (SectionNotPresentException e)
        {

            String vsystemName =
                vsystem.getName() != null && !StringUtils.isEmpty(vsystem.getName().getValue())
                    ? vsystem.getName().getValue() : vsystem.getId();

            MsgType prod = new MsgType();
            prod.setValue(vsystemName);

            ProductSectionType product = new ProductSectionType();
            product.setInfo(vsystem.getInfo());
            product.setProduct(prod);

            try
            {
                OVFEnvelopeUtils.addSection(vsystem, product);
            }
            catch (SectionAlreadyPresentException e1)
            {
            }
        }

        return envelope;
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
    private static String getVirtualSystemDiskId(final List<CimString> cimStrs)
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
}
