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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import junit.framework.Assert;

import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.MemorySizeUnit;
import com.abiquo.appliancemanager.transport.OVFPackageDiskFormat;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;

public class ApplianceManagerStubTestUtils
{

    private ApplianceManagerResourceStubImpl stub;
   
    
    protected final static String idEnterprise = ApplianceManagerStubIT.idEnterprise;

    protected final static String baseUrl = ApplianceManagerStubIT.baseUrl;

    protected final static Long downloadProgressInterval = 5 * 1000l;

    private final static Long UPLOAD_FILE_SIZE_BYTES = (1024 * 1024) * 1l;

    public ApplianceManagerStubTestUtils(ApplianceManagerResourceStubImpl stub)
    {
        this.stub = stub;
    }
    
    public  void ovfStatus(final String ovfId,
        final OVFPackageInstanceStatusType expectedStatus) 
    {
        
        OVFPackageInstanceStatusDto prevStatus = stub.getOVFPackageInstanceStatus(idEnterprise, ovfId);
            //stub..getOVFPackageStatus(baseUrl, idEnterprise, ovfId);

        Assert.assertEquals(expectedStatus, prevStatus.getOvfPackageStatus());
        Assert.assertEquals(ovfId, prevStatus.getOvfId());

        if (expectedStatus == OVFPackageInstanceStatusType.ERROR)
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
    public  Integer ovfAvailable(final String ovfId, final Boolean isContained)
    {
        OVFPackageInstanceStatusListDto prevList =
            stub.getOVFPackagInstanceStatusList(idEnterprise);

        Assert.assertEquals(isContained, isContained(prevList, ovfId));

        return prevList.getOvfPackageInstancesStatus().size();
    }

    public  void installOvf(final String ovfId)
    {
        //TODO OVFPackageInstanceStatusDto statusInstall =
            stub.createOVFPackageInstance(idEnterprise, ovfId);

        // Assert.assertNull(statusInstall.getErrorCause());
        // Assert.assertEquals(statusInstall.getOvfId(), ovfId);
        // TODO download or downloading
//        Assert.assertEquals(statusInstall.getOvfPackageStatus(),
//            OVFPackageInstanceStatusType.DOWNLOADING);
    }

    public  void installOvfAndWaitCompletion(final String ovfId) throws Exception
    {
        installOvf(ovfId);

        waitUnitlDownloaded(ovfId);
    }

    /**
     * 
     * 
     * */

    protected static void createBundleDiskFile(final String ovfId, final String snapshot)
        throws Exception
    {
        EnterpriseRepositoryService er =
            EnterpriseRepositoryService.getRepo(idEnterprise);
        final String ovfpath = er.getRelativePackagePath(ovfId);
        final String diskFilePathRel = er.getDiskFilePath(ovfId);
        // final String diskFilePathRel = diskFilePath.substring(diskFilePath.lastIndexOf('/'));
        final String path = ovfpath + '/' + snapshot + "-snapshot-" + diskFilePathRel;
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
        final String bundleOVFid =
            ovfId.substring(0, ovfId.lastIndexOf('.')) + "-snapshot-" + snapshot + ".ovf";

        OVFPackageInstanceDto di = new OVFPackageInstanceDto();
        di.setName("theBundleDiskName");
        di.setDescription("theBundleDiskDescription");

        di.setCpu(1);
        di.setHd(Long.valueOf(1024 * 1024 * 10));
        di.setRam(Long.valueOf(512));
        di.setHdSizeUnit(MemorySizeUnit.BYTE);
        di.setRamSizeUnit(MemorySizeUnit.BYTE);

        di.setIconPath("thiIconPath");
        di.setDiskFileFormat(OVFPackageDiskFormat.VMDK_FLAT);

        // di.setImageSize(121212); // XXX not use
        di.setDiskFilePath("XXXXXXXXX do not used XXXXXXXXXXX"); // XXX not use
        di.setOvfUrl(bundleOVFid);

        di.setIdEnterprise(Integer.valueOf(idEnterprise));
        di.setIdUser(2);
        di.setCategoryName("Test others");

        return di;
    }

    protected static OVFPackageInstanceDto createTestDiskInfoUpload()
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
        di.setDiskFileFormat(OVFPackageDiskFormat.VHD_FLAT);

        // di.setImageSize(121212); // XXX not use
        di.setDiskFilePath("XXXXXXXXX do not used XXXXXXXXXXX"); // XXX not use
        di.setOvfUrl("upload/testUpload/envelope.ovf");

        di.setIdEnterprise(Integer.valueOf(idEnterprise));
        di.setIdUser(2);
        di.setCategoryName("Test others");

        return di;
    }

    protected static Boolean isContained(final OVFPackageInstanceStatusListDto list,
        final String ovfId)
    {
        for (OVFPackageInstanceStatusDto status : list.getOvfPackageInstancesStatus())
        {
            if (ovfId.equalsIgnoreCase(status.getOvfId()))
            {
                return true;
            }
        }
        return false;
    }

    protected  void waitUnitlDownloaded(final String ovfId) throws Exception
    {
        Thread.sleep(downloadProgressInterval);

        OVFPackageInstanceStatusDto status = stub.getOVFPackageInstanceStatus(idEnterprise, ovfId);
        switch (status.getOvfPackageStatus())
        {
            case DOWNLOAD:
                return;
            case DOWNLOADING:
                Thread.sleep(downloadProgressInterval);
                waitUnitlDownloaded(ovfId);
                break;
            default: // ERROR:
                throw new Exception("Error downloading ");
        }
    }

}
