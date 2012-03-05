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

package com.abiquo.indexgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Reads all the OVF packages on a directory to create a RS XML document (''ovfindex.xml''). An
 * OVFPackage is defined as a directory containing an OVF envelope XML document and all its
 * referenced files. TODO include category on an OVF extension.
 * 
 * @author apuig
 */
public class OVFIndexGenerator
{
    private final static Logger log = LoggerFactory.getLogger(OVFIndexGenerator.class);

    /**
     * Creates a RS representation of all the OVF packages on the provided directory.
     * 
     * @param path, initial directory path to explore the OVF packages (SHULD be an absolute path)
     * @param repoExportUri, the RepositoryURI attribute (where the ''ovfindex'' will be exposed)
     * @param repoName, the RepositoryName attribute (RS friendly name)
     * @throws GenerationException, if the RS can not be created
     **/
    public RepositorySpace generateRepositorySpaceFrom(final URL path, String repoExportUri,
        final String repoName) throws GenerationException
    {
        RepositorySpace repo;

        repoExportUri = repoExportUri.replaceFirst("/$", "");

        log.info("Generating RepositorySpace from [{}]", path.toExternalForm());
        log.debug("Will be exported on [{}] with name [{}]", repoExportUri, repoName);

        repo = new RepositorySpace();
        repo.setRepositoryURI(repoExportUri);
        repo.setRepositoryName(repoName);

        if ("file".equalsIgnoreCase(path.getProtocol()))
        {
            File dir = new File(path.getPath());

            Set<OVFDescription> descriptions =
                getOVFPackageDescriptionsFromDirectory(dir, null, repo);

            repo.getOVFDescription().addAll(descriptions);
        }
        else
        {
            final String msg =
                "Only supports generation of OVFIndex from local paths, provided "
                    + path.toExternalForm();
            throw new GenerationException(msg);
        }

        return repo;
    }

    /**
     * Gets all the OVFDescriptions from the OVF envelope documents on the sub-directories pending
     * from the provided directory. XXX NOTE: none all the OVFDescriptions contains the
     * RepositoryURI attribute, all the OVFDescription's RepositoryURI from the RepositorySpace
     * RepositoryURI attribute.
     * 
     * @param f, if is a file find the ''.ovf'' to gets its OVFDescription, if is a directory call
     *            recursive for all its children.
     * @param path, the accumulated path during recursive calls.
     **/
    private Set<OVFDescription> getOVFPackageDescriptionsFromDirectory(final File f,
        final String path, final RepositorySpace repo)
    {
        Set<OVFDescription> packages = new HashSet<OVFDescription>();

        if (f.isDirectory())
        {
            for (File fs : f.listFiles())
            {
                String repoRelativePath;

                if (path == null)
                {
                    repoRelativePath = fs.getName();
                }
                else
                {
                    repoRelativePath = path + "/" + fs.getName(); // XXX
                }

                packages.addAll(getOVFPackageDescriptionsFromDirectory(fs, repoRelativePath, repo));
            }
        }// directory
        else
        {
            if (f.getName().endsWith(".ovf"))
            {
                log.debug("Reading OVF envelope file form [{}]", f.getAbsolutePath());

                try
                {
                    OVFDescription ovfDesc;

                    EnvelopeType envelope =
                        OVFSerializer.getInstance().readXMLEnvelope(new FileInputStream(f));

                    envelope =
                        fixOVfDocument(repo.getRepositoryURI() + "/" + path + "/" + f.getName(),
                            envelope);

                    checkEnvelopeIsValid(envelope);

                    ovfDesc = initDescription(envelope, repo, path + "/" + f.getName());

                    ovfDesc.setOVFFile(path);// f.getName
                    // ovfDesc.setRepositoryURI(repoURI);

                    packages.add(ovfDesc);

                }
                catch (Exception e) // FileNotFoundException XMLException
                {
                    log.error("Can not read the ovf envelope from [{}], caused by [{}]",
                        f.getAbsolutePath(), e.getMessage());

                }
            }// an OVF envelope file
             // else not an envelope file
        }

        return packages;
    }

    private static OVFDescription initDescription(final EnvelopeType envelope,
        final RepositorySpace repo, final String ovfFile) throws EmptyEnvelopeException,
        SectionNotPresentException, InvalidSectionException, RequiredAttributeException
    {

        String ovfid = repo.getRepositoryURI() + "/" + ovfFile;
        ContentType content = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);
        ProductSectionType product = OVFEnvelopeUtils.getSection(content, ProductSectionType.class);
        DiskSectionType diskSection = OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);

        String diskFormat = getDiskFormatAsString(envelope);

        OVFDescription desc = initFromProduct(product, ovfid, envelope);

        desc.setOVFFile(ovfFile);

        desc.setRepositoryName(repo.getRepositoryName());
        desc.setRepositoryURI(repo.getRepositoryURI());

        // TODO assume only one Disk on the envelope
        // TODO assume CapacityAllocationUnits ara bytes

        desc.setDiskSize(diskSection.getDisk().get(0).getCapacity());
        desc.setDiskFormat(diskFormat);

        return desc;
    }

    private static String createAbsolutePathIcon(final String ovfId, final EnvelopeType envelope)
        throws EmptyEnvelopeException, SectionException, IdNotFoundException
    {

        String iconPath;

        ContentType content = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);
        ProductSectionType product = OVFEnvelopeUtils.getSection(content, ProductSectionType.class);

        List<ProductSectionType.Icon> icons = product.getIcon();

        if (icons != null && icons.size() > 0)
        {
            String iconFileId = icons.get(0).getFileRef();

            // check if the fileRef is already an absolute path.
            if (iconFileId.startsWith("http:") || iconFileId.startsWith("https:"))
            {
                iconPath = iconFileId;
            }
            else
            {
                String iconRelPath;

                try
                {
                    iconRelPath =
                        OVFReferenceUtils.getReferencedFile(envelope, iconFileId).getHref();

                    if (iconRelPath.startsWith("http:") || iconRelPath.startsWith("https:"))
                    {
                        iconPath = iconRelPath;
                    }
                    else
                    {
                        iconPath = ovfId.substring(0, ovfId.lastIndexOf('/') + 1) + iconRelPath;
                    }

                }
                catch (IdNotFoundException e)
                {
                    final String msg =
                        "Can not get the Icon file reference [" + iconFileId
                            + "] at envelope OVFId [" + ovfId + "]";
                    throw new IdNotFoundException(msg, e);
                }
            }
        }
        else
        {
            final String msg =
                "There are any defined icon for the envelope with OVFId [" + ovfId + "]";
            throw new IdNotFoundException(msg);
        }

        return iconPath;
    }

    private static OVFDescription initFromProduct(final ProductSectionType product,
        final String ovfId, final EnvelopeType envelope)
    {

        OVFDescription desc = new OVFDescription();

        desc.setAppUrl(product.getAppUrl());
        desc.setClazz(product.getClazz());
        desc.setFullVersion(product.getFullVersion());
        desc.setInfo(product.getInfo());
        desc.setInstance(product.getInstance());
        desc.setProduct(product.getProduct());
        desc.setProductUrl(product.getProductUrl());
        desc.setVendor(product.getVendor());
        desc.setVendorUrl(product.getVendorUrl());
        desc.setVersion(product.getVersion());

        // desc.getCategoryOrProperty().addAll(product.getCategoryOrProperty());
        // desc.getIcon().addAll(product.getIcon());

        String iconPath;

        try
        {
            iconPath = createAbsolutePathIcon(ovfId, envelope);

            Icon icon = new Icon();
            icon.setFileRef(iconPath);
            desc.getIcon().add(icon);
            // XXX other icon attrs
        }
        catch (Exception e)
        {
            // log.error("Can not determine the icon for OVFid [{}] cause by [{}]", ovfId, e);
        }

        desc.getOtherAttributes().putAll(product.getOtherAttributes()); // XXX usefull

        return desc;
    }

    /**
     * Retrieves the information on the disk type of each Disk image referenced in the .ovf file
     * 
     * @throws RequiredAttributeException
     * @throws InvalidSectionException
     * @throws SectionNotPresentException
     */
    private static String getDiskFormatAsString(final EnvelopeType envelope)
        throws RequiredAttributeException, SectionNotPresentException, InvalidSectionException
    {

        DiskSectionType diskSectionType =
            OVFEnvelopeUtils.getSection(envelope, DiskSectionType.class);
        List<VirtualDiskDescType> virtualDiskDescTypes = diskSectionType.getDisk();

        String diskFormat = "";
        // Create a hash
        for (VirtualDiskDescType virtualDiskDescType : virtualDiskDescTypes)
        {

            // assure all the disks on the packages are of the same format.
            // If not the first disk format is taken.

            DiskFormat thisDiskFormat = DiskFormat.fromValue(virtualDiskDescType.getFormat());

            diskFormat += thisDiskFormat + ",";

            /**
             * TODO check all the disk formats are the same type <br>
             * if(diskFormat != null) { if(!
             * thisDiskFormat.toString().equalsIgnoreCase(diskFormat.toString())) {
             * log.error("Different disk formats in the same package. Prev[{}], acutal[{}]",
             * diskFormat, thisDiskFormat); // TODO must throw an exception ??? } } else {
             * diskFormat = thisDiskFormat; }
             */

        }

        diskFormat = diskFormat.replaceFirst(",$", "");

        return diskFormat;
    }

    /**
     * TODO in bytes private static Long getDiskSize(EnvelopeType envelope) throws
     * RequiredAttributeException, SectionNotPresentException, InvalidSectionException {
     * DiskSectionType diskSectionType = OVFEnvelopeUtils.getSection(envelope,
     * DiskSectionType.class); List<VirtualDiskDescType> virtualDiskDescTypes =
     * diskSectionType.getDisk(); if(virtualDiskDescTypes != null && !virtualDiskDescTypes.isEmpty()
     * && virtualDiskDescTypes.size() == 1) { return virtualDiskDescTypes.get(0).getCapacity().; } }
     **/

    /**
     * Called from ANT
     * 
     * @throws IOException
     */
    public static void main(final String[] args) throws GenerationException, XMLException,
        JAXBException, IOException
    {
        if (args == null || args.length != 4 || args[0] == null || args[1] == null
            || args[2] == null || args[3] == null)
        {
            throw new IllegalArgumentException("Required 4 arguments to be invoked \n"
                + "1:\"base folder containing the OVF description files\" \n"
                + "2:\"base URL where the generated index will be exposed\" \n"
                + "3:\"the name of the generated ovfindex\"\n"
                + "4:\"file name of the resulting ovfindex\"\n");
        }

        final String indexPath = args[0];
        final String indexUri = args[1];
        final String indexName = args[2];
        final String outputFile = args[3];

        OVFIndexGenerator gen = new OVFIndexGenerator();
        URL indexUrl = new URL("file:" + indexPath);
        RepositorySpace rs = gen.generateRepositorySpaceFrom(indexUrl, indexUri, indexName);

        File fileOut = new File(outputFile);
        FileOutputStream fout = new FileOutputStream(fileOut);

        try
        {
            RepositorySpaceSerializer.writeAsXML(rs, fout);
        }
        finally
        {
            fout.close();
        }

        log.info("Created [{}]", outputFile);
    }

    static class RepositorySpaceSerializer
    {
        /** Define the allowed objects to be binded form/into the OVFIndex schema definition. */
        private static JAXBContext contextIndex;

        static
        {
            try
            {
                contextIndex = JAXBContext.newInstance(new Class[] {RepositorySpace.class});
            }
            catch (JAXBException e)
            {
                log.error("[FATAL] Can not initialize the JAXB context, "
                    + "check ''ovfmanager.jar'' is present on the classpath.\n" + e.getMessage());

            }
        }

        /** Generated factory to create XML elements on OVFIndex name space. */
        private static final com.abiquo.appliancemanager.repositoryspace.ObjectFactory factoryIndex =
            new com.abiquo.appliancemanager.repositoryspace.ObjectFactory();

        /**
         * Creates an XML document representing the provided repository space based object and write
         * it to output stream. The provided stream is closed.
         * 
         * @param repoSpace, the object to be binded into an XML document.
         * @param os, the destination of the XML document.
         * @throws OVFSchemaException, any XML problem.
         */
        public static void writeAsXML(final RepositorySpace rs, final OutputStream os)
            throws XMLException
        {
            Marshaller marshall;

            try
            {
                marshall = contextIndex.createMarshaller();
                marshall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshall.marshal(factoryIndex.createRepositorySpace(rs), os);
            }
            catch (JAXBException ea)
            {
                throw new XMLException(ea);
            }
        }
    }

    class GenerationException extends Exception
    {
        private static final long serialVersionUID = -1064542503527290109L;

        public GenerationException(final String message, final Throwable cause)
        {
            super(message, cause);
        }

        public GenerationException(final String message)
        {
            super(message);
        }
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
            throw new GenerationException("Invalid OVF", e);
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

            if (diskSectionType.getDisk().size() != 1)
            {
                // more than one disk in disk section
                throw new GenerationException("Multiple disks");
            }
            else
            {
                int references = 0;
                for (FileType fileType : envelope.getReferences().getFile())
                {
                    fileIdToFileType.put(fileType.getId(), fileType);
                    if (diskSectionType.getDisk().get(0).getFileRef().equals(fileType.getId()))
                    {
                        references++;
                    }
                }
                if (references != 1)
                {
                    // file referenced in diskSection isn't found in file references or found more
                    // than one
                    throw new GenerationException("Multiple file in refs");
                }
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

        catch (GenerationException amException)
        {
            throw amException;
        }
        catch (Exception e)
        {
            throw new GenerationException("Invalid OVF", e);
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
