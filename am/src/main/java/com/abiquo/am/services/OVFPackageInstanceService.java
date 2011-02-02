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

package com.abiquo.am.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.abiquo.am.services.aux.OVFPackageInstanceFromOVFEnvelope;
import com.abiquo.am.services.aux.OVFPackageInstanceToOVFEnvelope;
import com.abiquo.am.services.notify.AMNotifierFactory;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.exceptions.EventException;
import com.abiquo.appliancemanager.exceptions.RepositoryException;
import com.abiquo.appliancemanager.transport.MemorySizeUnit;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionAlreadyPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.abiquo.server.core.enumerator.DiskFormatType;

@Component(value = "ovfPackageInstanceService")
public class OVFPackageInstanceService extends OVFPackageConventions
{

    // @Autowired
    static OVFPackageInstanceDownloader downloader;

    @Resource(name = "ovfPackageInstanceDownloader")
    public void setDownloader(OVFPackageInstanceDownloader downloader)
    {
        this.downloader = downloader;
    }

    /** The constant logger object. */
    private final static Logger logger = LoggerFactory
        .getLogger(OVFPackageInstanceDownloader.class);

    /** Timeout for all the HTTP connections. */
    private final static Integer httpTimeout = AMConfigurationManager.getInstance()
        .getAMConfiguration().getTimeout();

    public OVFPackageInstanceService()
    {
        super();
    }

    public void delete(String erId, String ovfId) throws RepositoryException, IdNotFoundException
    {
        OVFPackageInstanceStatusType status = getOVFStatus(erId, ovfId);

        switch (status)
        {
            case DOWNLOADING:
                downloader.cancelDeployOVFPackage(ovfId, erId);
                break;

            // TODO case error
            default:
                EnterpriseRepositoryService.getRepo(erId).deleteOVF(ovfId);
                break;
        }

        try
        {
            AMNotifierFactory.getInstance().setOVFStatus(erId, ovfId,
                OVFPackageInstanceStatusType.NOT_DOWNLOAD);
            // ?
        }
        catch (Exception e)
        {
            final String msg =
                String.format("Can not notify the NOT_DOWNLOAD (cancel) of [%s] caused by [%s]",
                    ovfId, e.getLocalizedMessage());
            logger.error(msg);
        }
    }

    public void startDownload(String erId, String ovfId) throws DownloadException,
        RepositoryException
    {
        // first create the folder in order to allow the creation of ERROR marks.
        EnterpriseRepositoryService erep = EnterpriseRepositoryService.getRepo(erId);
        erep.createOVFPackageFolder(ovfId);

        EnvelopeType envelope = obtainEnvelope(ovfId);

        // TODO check envelope is compatible

        downloader.deployOVFPackage(erId, ovfId, envelope);

        // sets the current state to start downloading
        try
        {
            AMNotifierFactory.getInstance().setOVFStatus(erId, ovfId,
                OVFPackageInstanceStatusType.DOWNLOADING);
        }
        catch (Exception e)
        {
            final String cause = String.format("Can not set DOWNLOADING state for OVF [%s]", ovfId);
            logger.error(cause, e);
        }

    }

    public void upload(OVFPackageInstanceDto diskinfo, File diskFile) throws RepositoryException,
        IOException, IdNotFoundException, EventException
    {
        downloader.uploadOVFPackage(diskinfo, diskFile);

        // sets the current state to start downloading
        AMNotifierFactory.getInstance().setOVFStatus(String.valueOf(diskinfo.getIdEnterprise()),
            diskinfo.getOvfUrl(), OVFPackageInstanceStatusType.DOWNLOAD);

    }

    public OVFPackageInstanceDto getOVFPackage(final String erId, final String ovfId)
        throws IdNotFoundException
    {
        EnterpriseRepositoryService erepo = EnterpriseRepositoryService.getRepo(erId);

        OVFPackageInstanceDto packDto;

        if (!isBundleOvfId(ovfId))
        {
            EnvelopeType envelope = erepo.getEnvelope(ovfId);

            try
            {
                String relativePackagePath = erepo.getRelativePackagePath(ovfId);
                relativePackagePath = erId + '/' + relativePackagePath; // FIXME use EnterpriseRepo

                packDto = OVFPackageInstanceFromOVFEnvelope.getDiskInfo(ovfId, envelope);
                packDto = fixFilePathWithRelativeOVFPackagePath(packDto, relativePackagePath);
                packDto.setIdEnterprise(Integer.valueOf(erId));
            }
            catch (Exception e)
            {

                try
                {
                    envelope = fixMissingProductSection(envelope);
                    envelope = fixDiskFormtatUriAndFileSizes(envelope, ovfId);

                    checkEnvelopeIsValid(envelope);

                    // TODO restore OVF Envelope on filesystem

                    String relativePackagePath = erepo.getRelativePackagePath(ovfId);
                    relativePackagePath = erId + '/' + relativePackagePath; // FIXME use
                                                                            // EnterpriseRepo

                    packDto = OVFPackageInstanceFromOVFEnvelope.getDiskInfo(ovfId, envelope);
                    packDto = fixFilePathWithRelativeOVFPackagePath(packDto, relativePackagePath);
                    packDto.setIdEnterprise(Integer.valueOf(erId));
                }
                catch (Exception envelopeInvalidForever)
                {
                    try
                    {
                        AMNotifierFactory.getInstance().setOVFStatusError(erId, ovfId,
                            envelopeInvalidForever.toString());
                    }
                    catch (Exception errorSettingError)
                    {
                        final String cause =
                            String.format("Can not set ERROR state for OVF [%s]", ovfId);
                        logger.error(cause, e);
                    }

                    throw new IdNotFoundException(String.format("Invalid OVF at %s", ovfId));
                }

            }// try to change the envelope in order to make abiquo compatible.
        }
        else
        {
            final String masterOvf = getBundleMasterOvfId(ovfId);
            final String snapshot = getBundleSnapshot(ovfId);

            EnvelopeType envelope = erepo.getEnvelope(masterOvf);

            String relativePackagePath = erepo.getRelativePackagePath(masterOvf);
            relativePackagePath = erId + '/' + relativePackagePath; // FIXME use EnterpriseRepo

            packDto = OVFPackageInstanceFromOVFEnvelope.getDiskInfo(masterOvf, envelope);
            packDto = fixFilePathWithRelativeOVFPackagePath(packDto, relativePackagePath);

            packDto.setIdEnterprise(Integer.valueOf(erId));

            packDto.setName(snapshot + OVF_BUNDLE_PATH_IDENTIFIER + packDto.getName());

            final String masterDiskPath = packDto.getDiskFilePath();
            final String bundleDiskPath = createBundleOvfId(masterDiskPath, snapshot);

            packDto.setMasterDiskFilePath(masterDiskPath);
            packDto.setDiskFilePath(bundleDiskPath);
            packDto.setOvfUrl(ovfId);
            // packDto.setDiskFileSize(diskFileSize); TODO change the disk size
        }

        return packDto;
    }

    private OVFPackageInstanceDto fixFilePathWithRelativeOVFPackagePath(
        OVFPackageInstanceDto ovfpi, String relativePackagePath)
    {
        ovfpi.setDiskFilePath(relativePackagePath + ovfpi.getDiskFilePath());

        return ovfpi;
    }

    /**
     * @throws RepositoryException, if some of the Disk files of the bundle do not exist on the
     *             repository.
     */
    public String createOVFBundle(OVFPackageInstanceDto diskInfo, final String snapshot)
        throws RepositoryException
    {

        final String erId = String.valueOf(diskInfo.getIdEnterprise());
        final String ovfIdSnapshot = diskInfo.getOvfUrl();

        EnterpriseRepositoryService erepo = EnterpriseRepositoryService.getRepo(erId);

        EnvelopeType envelopeBundle =
            OVFPackageInstanceToOVFEnvelope.createEnvelopeFromOVFPackageInstance(diskInfo);

        return erepo.createBundle(ovfIdSnapshot, snapshot, envelopeBundle);
    }

    /**
     * XXX
     * 
     * @throws DownloadException, if can not connect to the OVF location of if the envelope document
     *             is invalid.
     */
    public EnvelopeType obtainEnvelope(final String ovfId) throws DownloadException
    {
        InputStream evelopeStream = openHTTPConnection(ovfId);

        EnvelopeType envelope;
        try
        {
            envelope = OVFSerializer.getInstance().readXMLEnvelope(evelopeStream);

            envelope = fixDiskFormtatUriAndFileSizes(envelope, ovfId);
            envelope = fixMissingProductSection(envelope);

            checkEnvelopeIsValid(envelope);
        }
        catch (Exception e)
        {
            final String cause = String.format("Invalid OVF Envelope document at [%s]", ovfId);
            throw new DownloadException(cause, e);
        }
        finally
        {
            try
            {
                evelopeStream.close();
            }
            catch (IOException e)
            {
                final String cause =
                    String.format("Can not close stream to Envelope document at [%s]", ovfId);
                throw new DownloadException(cause, e);
                // XXX do not
            }
        }

        return envelope;
    }

    private void checkEnvelopeIsValid(EnvelopeType envelope) throws EmptyEnvelopeException,
        SectionNotPresentException, InvalidSectionException, RequiredAttributeException
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

        try
        {
            hardwareSectionType =
                OVFEnvelopeUtils.getSection(vsystem, VirtualHardwareSectionType.class);
        }
        catch (InvalidSectionException e)
        {
            throw new SectionNotPresentException("VirtualHardware on a virtualSystem", e);
        }

        Integer cpu = null;
        Long hd = null;
        Long ram = null;

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

    private EnvelopeType fixDiskFormtatUriAndFileSizes(EnvelopeType envelope, String ovfId)
        throws SectionNotPresentException, InvalidSectionException
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
                    URL fileUrl = getFileUrl(ftype.getHref(), ovfId);
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

    private Long getFileSizeFromHttpHead(URL fileUrl) throws DownloadException
    {
        try
        {
            URLConnection connection = fileUrl.openConnection();

            connection.setUseCaches(true);
            connection.setReadTimeout(httpTimeout);
            connection.setConnectTimeout(httpTimeout);

            String contentLenght = connection.getHeaderField(CONTENT_LENGTH);

            return Long.parseLong(contentLenght);
        }
        catch (Exception e)
        {
            throw new DownloadException(String.format("Can not obtain file [%s] size",
                fileUrl.toExternalForm()), e);
        }
    }

    /**
     * If product section is not present use the VirtualSystemType to set it.
     * 
     * @throws EmptyEnvelopeException
     */
    private EnvelopeType fixMissingProductSection(EnvelopeType envelope)
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
     * Gets the average download progress for all the files on the provided OVF package identifier.
     * If it is complete downloaded or some file fails, changes the OVFDescription status to
     * DOWNLOAD or ERROR on the OVFIndex.
     * 
     * @param ovfId, the URI representing the OVF package whose download progress has been
     *            solicited.
     * @return on DOWNLOADING state 'getDownloadProgress' returns a double between from 0 to 100
     *         indicating the percentage of the download for the OVF package.
     * @throws DownloadException if the state is DOWNLOADING (exist the file mark on the Enterprise
     *             Repository) but it isn't in the ''htCurrentTransfers'' structure.
     */
    public OVFPackageInstanceStatusDto getOVFPackageStatusIncludeProgress(final String ovfId,
        final String enterpriseId) throws DownloadException
    {

        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(enterpriseId);

        final OVFPackageInstanceStatusType status = enterpriseRepository.getOVFStatus(ovfId);

        OVFPackageInstanceStatusDto statusDto = new OVFPackageInstanceStatusDto();
        statusDto.setOvfId(ovfId);
        statusDto.setOvfPackageStatus(status);

        logger.debug("Status for [{}] : {}", ovfId, status.name());

        if (status == OVFPackageInstanceStatusType.DOWNLOADING)
        {
            statusDto.setProgress(downloader.getDownloadProgress(ovfId));
        }
        else if (status == OVFPackageInstanceStatusType.ERROR)
        {
            statusDto.setErrorCause(status.getErrorCause());
        }

        return statusDto;
    }

    public OVFPackageInstanceDto createBunlde(final OVFPackageInstanceDto master,
        final String snapshot)
    {

        final String ovfId = master.getOvfUrl();
        final String bundleOvfId =
            ovfId.substring(0, ovfId.lastIndexOf('/') + 1) + snapshot + "-snapshot-"
                + ovfId.substring(ovfId.lastIndexOf('/') + 1, ovfId.length());

        OVFPackageInstanceDto di = new OVFPackageInstanceDto();
        di.setName(snapshot + master.getName());
        di.setDescription("bundle of " + master.getDescription());

        di.setCpu(master.getCpu());
        di.setHd(master.getHd());
        di.setRam(master.getRam());
        di.setHdSizeUnit(master.getHdSizeUnit());
        di.setRamSizeUnit(master.getRamSizeUnit());

        di.setIconPath(master.getIconPath());
        di.setDiskFileFormat(master.getDiskFileFormat());

        // di.setImageSize(121212); // XXX not use
        // di.setDiskFilePath("XXXXXXXXX do not used XXXXXXXXXXX"); // XXX not use
        di.setMasterDiskFilePath(master.getDiskFilePath());
        di.setOvfUrl(bundleOvfId);

        di.setIdEnterprise(master.getIdEnterprise());
        // di.setIdUser(2);
        di.setCategoryName(master.getCategoryName());

        return di;
    }

    // /**
    // * Return the references not being used on the provided list of OVF packages.
    // */
    // private Set<String> getFilesNotBeingUsedOn(final String erId, Set<String> fileLocations,
    // final List<String> ovfIds)
    // {
    // EnterpriseRepositoryService erepo =
    // EnterpriseRepositoryService.getRepo(erId);
    //
    // for (String ovfId : ovfIds)
    // {
    // try
    // {
    // EnvelopeType envelope = erepo.getEnvelope(ovfId);
    // final Set<String> references =
    // OVFReferenceUtils.getAllReferencedFileLocations(envelope);
    // fileLocations.removeAll(references);
    // }
    // catch (IdNotFoundException e)
    // {
    // logger.error("Can not get the Envelope for the ovf [{}]", ovfId);
    // // XXX should do not occurs, just get the ovf from the filesystem
    // }
    // }
    //
    // return fileLocations;
    // }

    public OVFPackageInstanceStatusType getOVFStatus(final String erId, final String ovfId)
    {
        return EnterpriseRepositoryService.getRepo(erId).getOVFStatus(ovfId);
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
}
