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

package com.abiquo.appliancemanager;

import static com.abiquo.testng.AMRepositoryListener.REPO_PATH;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import junit.framework.Assert;

import org.apache.commons.io.FilenameUtils;

import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.OVFPackageConventions;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.MemorySizeUnit;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.testng.TestServerListener;

public class ApplianceManagerAsserts
{

    private final ApplianceManagerResourceStubImpl stub;

    protected final static String idEnterprise = ApplianceManagerIT.idEnterprise;

    protected final static String baseUrl = TestServerListener.BASE_URI;

    protected final static Long downloadProgressInterval = 5 * 1000l;

    private final static Long UPLOAD_FILE_SIZE_BYTES = (1024 * 1024) * 1l;

    public ApplianceManagerAsserts(final ApplianceManagerResourceStubImpl stub)
    {
        this.stub = stub;
    }

    public void ovfStatus(final String ovfId, final OVFStatusEnumType expectedStatus)
    {

        OVFPackageInstanceStateDto prevStatus =
            stub.getOVFPackageInstanceStatus(idEnterprise, ovfId);

        Assert.assertEquals(expectedStatus, prevStatus.getStatus());
        Assert.assertEquals(ovfId, prevStatus.getOvfId());

        if (expectedStatus == OVFStatusEnumType.ERROR)
        {
            Assert.assertNotNull(prevStatus.getErrorCause());
        }
        else
        {
            Assert.assertNull(prevStatus.getErrorCause());
        }

    }

    /**
     * @return the number of ovf availables
     */
    public Integer ovfAvailable(final String ovfId, final Boolean isContained)
    {
        OVFPackageInstancesStateDto prevList = stub.getOVFPackagInstanceStatusList(idEnterprise);

        Assert.assertEquals(isContained, isContained(prevList, ovfId));

        return prevList.getCollection().size();
    }

    public void installOvf(final String ovfId)
    {
        // TODO OVFPackageInstanceStatusDto statusInstall =
        stub.createOVFPackageInstance(idEnterprise, ovfId);

        // Assert.assertNull(statusInstall.getErrorCause());
        // Assert.assertEquals(statusInstall.getOvfId(), ovfId);
        // TODO download or downloading
        // Assert.assertEquals(statusInstall.getOvfPackageStatus(),
        // OVFPackageInstanceStatusType.DOWNLOADING);
    }

    public void installOvfAndWaitCompletion(final String ovfId) throws Exception
    {
        installOvf(ovfId);

        waitUnitlExpected(ovfId, OVFStatusEnumType.DOWNLOAD);
    }

    /**
     * Status DOWNLOAD, in the available list and the disk file in the repository fs.
     */
    public void ovfInstanceExist(final String ovfId)
    {
        ovfStatus(ovfId, OVFStatusEnumType.DOWNLOAD);

        ovfAvailable(ovfId, true);

        OVFPackageInstanceDto pi = stub.getOVFPackageInstance(idEnterprise, ovfId);
        File diskFile = new File(REPO_PATH + pi.getDiskFilePath());
        Assert.assertTrue(diskFile.exists());
    }

    /**
     * Status NOT_DOWNLOAD and not available in the list
     */
    public void ovfInstanceNoExist(final String ovfId)
    {
        // The OVF is NOT_DOWNLOAD
        ovfStatus(ovfId, OVFStatusEnumType.NOT_DOWNLOAD);

        // The OVF is not on the available list
        ovfAvailable(ovfId, false);
    }

    public void clean(final String ovfId)
    {
        // deletes the ovfs
        stub.delete(idEnterprise, ovfId);
        ovfAvailable(ovfId, false);
    }

    /**
     * 
     * 
     * */

    protected static void createBundleDiskFile(final String ovfId, final String snapshot)
        throws Exception
    {
        EnterpriseRepositoryService er = ErepoFactory.getRepo(idEnterprise);

        final String ovfpath = OVFPackageConventions.getRelativePackagePath(ovfId);
        final String diskFilePathRel = er.getDiskFilePath(ovfId);
        // final String diskFilePathRel = diskFilePath.substring(diskFilePath.lastIndexOf('/'));

        final String path =
            FilenameUtils.concat(FilenameUtils.concat(er.path(), ovfpath),
                (snapshot + "-snapshot-" + diskFilePathRel));

        // "/opt/testvmrepo/1/rs.bcn.abiquo.com/m0n0wall/000snap000-snapshot-m0n0wall-1.3b18-i386-flat.vmdk"

        File f = new File(path);
        f.createNewFile();
        f.deleteOnExit();

        FileWriter fileWriter = new FileWriter(f);
        for (int i = 0; i < 1000; i++)
        {
            fileWriter.write(i % 1);
        }
        fileWriter.close();
    }

    protected static File createUploadTempFile() throws IOException
    {
        Random rnd = new Random(System.currentTimeMillis());
        final String fileName = String.valueOf(rnd.nextLong());
        File file = File.createTempFile(fileName, ".uploadTest");

        RandomAccessFile f = new RandomAccessFile(file, "rw");
        f.setLength(UPLOAD_FILE_SIZE_BYTES);

        file.deleteOnExit();

        return file;
    }

    protected static OVFPackageInstanceDto createTestDiskInfoBundle(final String ovfId,
        final String snapshot)
    {

        final String name = ovfId.substring(ovfId.lastIndexOf('/') + 1);
        final String url = ovfId.substring(0, ovfId.lastIndexOf('/') + 1);
        final String bundleOVFid = url + snapshot + "-snapshot-" + name;
        // final String bundleOVFid =
        // ovfId.substring(0, ovfId.lastIndexOf('.')) + "-snapshot-" + snapshot + ".ovf";

        EnterpriseRepositoryService er = ErepoFactory.getRepo(idEnterprise);

        final String diskFilePathRel = er.getDiskFilePath(ovfId);
        final String diskPath = ("-snapshot-" + diskFilePathRel);

        OVFPackageInstanceDto di = new OVFPackageInstanceDto();
        di.setName("theBundleDiskName");
        di.setDescription("theBundleDiskDescription");

        di.setCpu(1);
        di.setHd(Long.valueOf(1024 * 1024 * 10));
        di.setRam(Long.valueOf(512));
        di.setHdSizeUnit(MemorySizeUnit.BYTE);
        di.setRamSizeUnit(MemorySizeUnit.BYTE);

        di.setIconPath("thiIconPath");
        di.setDiskFileFormat(DiskFormatType.VMDK_FLAT);

        // di.setImageSize(121212); // XXX not use
        di.setDiskFilePath(diskPath);
        di.setUrl(bundleOVFid);

        di.setEnterpriseRepositoryId(Integer.valueOf(idEnterprise));
        di.setCategoryName("Test others");

        return di;
    }

    protected static OVFPackageInstanceDto createTestDiskInfoUpload(final String ovfid)
    {
        OVFPackageInstanceDto di = new OVFPackageInstanceDto();
        di.setName("theDiskName");
        di.setDescription("theDiskDescription");

        di.setCpu(1);
        di.setHd(Long.valueOf(1024 * 1024 * 10));
        di.setRam(Long.valueOf(512));
        di.setHdSizeUnit(MemorySizeUnit.BYTE);
        di.setRamSizeUnit(MemorySizeUnit.BYTE);

        di.setIconPath("thiIconPath");
        di.setDiskFileFormat(DiskFormatType.VHD_FLAT);

        // di.setImageSize(121212); // XXX not use
        di.setDiskFilePath("XXXXXXXXX do not used XXXXXXXXXXX"); // XXX not use
        di.setUrl(ovfid);

        di.setEnterpriseRepositoryId(Integer.valueOf(idEnterprise));
        di.setCategoryName("Test others");

        return di;
    }

    protected static Boolean isContained(final OVFPackageInstancesStateDto list, final String ovfId)
    {
        for (OVFPackageInstanceStateDto status : list.getCollection())
        {
            if (ovfId.equalsIgnoreCase(status.getOvfId()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * TODO test timeout
     */
    public void waitUnitlExpected(final String ovfId, final OVFStatusEnumType expected)
        throws Exception
    {
        Thread.sleep(downloadProgressInterval); // FIXME

        OVFPackageInstanceStateDto status = stub.getOVFPackageInstanceStatus(idEnterprise, ovfId);
        if (status.getStatus() == expected)
        {
            return;
        }
        else if (status.getStatus() == OVFStatusEnumType.DOWNLOADING)
        {
            Thread.sleep(downloadProgressInterval);
            waitUnitlExpected(ovfId, expected);
        }
        else
        {
            throw new Exception(String.format("Expected %s get %s", expected.name(), status
                .getStatus().name()));
        }
    }

}
