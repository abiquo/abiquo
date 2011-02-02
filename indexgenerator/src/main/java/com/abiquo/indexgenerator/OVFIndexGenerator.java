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
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.DiskSectionType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualDiskDescType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.appliancemanager.repositoryspace.OVFDescription;
import com.abiquo.appliancemanager.repositoryspace.RepositorySpace;
import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.OVFReferenceUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.InvalidSectionException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

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
    public RepositorySpace generateRepositorySpaceFrom(URL path, String repoExportUri,
        String repoName) throws GenerationException
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
    private Set<OVFDescription> getOVFPackageDescriptionsFromDirectory(File f, String path,
        RepositorySpace repo)
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

                    ovfDesc = initDescription(envelope, repo, path + "/" + f.getName());

                    ovfDesc.setOVFFile(path);// f.getName
                    // ovfDesc.setRepositoryURI(repoURI);

                    packages.add(ovfDesc);

                }
                catch (Exception e) // FileNotFoundException XMLException
                {
                    log.error("Can not read the ovf envelope from [{}], caused by [{}]", f
                        .getAbsolutePath(), e.getMessage());

                }
            }// an OVF envelope file
            // else not an envelope file
        }

        return packages;
    }

    private static OVFDescription initDescription(EnvelopeType envelope, RepositorySpace repo,
        String ovfFile) throws EmptyEnvelopeException, SectionNotPresentException,
        InvalidSectionException, RequiredAttributeException
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

    private static String createAbsolutePathIcon(String ovfId, EnvelopeType envelope)
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

    private static OVFDescription initFromProduct(ProductSectionType product, String ovfId,
        EnvelopeType envelope)
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
    private static String getDiskFormatAsString(EnvelopeType envelope)
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
     *TODO in bytes private static Long getDiskSize(EnvelopeType envelope) throws
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
    public static void main(String[] args) throws GenerationException, XMLException, JAXBException,
        IOException
    {
        if (args == null || args.length != 4 || args[0] == null || args[1] == null || args[2] == null || args[3] == null)
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
        public static void writeAsXML(RepositorySpace rs, OutputStream os) throws XMLException
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

        public GenerationException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public GenerationException(String message)
        {
            super(message);
        }
    }
}
