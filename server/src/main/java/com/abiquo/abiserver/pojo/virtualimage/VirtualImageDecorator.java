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

package com.abiquo.abiserver.pojo.virtualimage;

import java.util.UUID;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.ovfmanager.ovf.section.DiskFormat;

public class VirtualImageDecorator extends VirtualImage
{

    private final UUID uuid;

    private String volumePath;

    public VirtualImageDecorator()
    {
        uuid = UUID.randomUUID();
    }

    public static VirtualImageDecorator createDecorator(final VirtualImage virtualImage)
    {
        VirtualImageDecorator decorator = new VirtualImageDecorator();

        decorator.setCategory(virtualImage.getCategory());
        decorator.setCpuRequired(virtualImage.getCpuRequired());
        decorator.setDeleted(virtualImage.getDeleted());
        decorator.setDescription(virtualImage.getDescription());
        decorator.setHdRequired(virtualImage.getHdRequired());

        if (virtualImage.getIcon() != null)
        {
            decorator.setIcon(virtualImage.getIcon());
        }

        decorator.setId(virtualImage.getId());
        decorator.setName(virtualImage.getName());
        decorator.setPath(virtualImage.getPath());
        decorator.setRamRequired(virtualImage.getRamRequired());
        if (virtualImage.getRepository() == null)
        {
            decorator.setRepository(null);
        }
        else
        {
            decorator.setRepository(virtualImage.getRepository());
        }

        decorator.setDiskFormatType(virtualImage.getDiskFormatType());
        if (virtualImage.getMaster() != null)
        {
            decorator.setMaster(virtualImage.getMaster());
        }
        decorator.setIdEnterprise(virtualImage.getIdEnterprise());
        decorator.setOvfId(virtualImage.getOvfId());
        decorator.setStateful(virtualImage.getStateful());
        decorator.setDiskFileSize(virtualImage.getDiskFileSize());

        return decorator;
    }

    @Override
    public String getPath()
    {
        String path = super.getPath();

        if (isManaged())
        {
            if (this.isImageStateful() && volumePath == null)
            {
                volumePath = path;
            }

            if (this.getMaster() != null && !this.getMaster().isImageStateful())
            {
                boolean vhd = isVhd(path);
                path = this.getMaster().getPath();
                if (vhd)
                {
                    path = vhdPath(path);
                }
            }

            path = getBundlePath(path);
        }

        return path;
    }

    @Override
    public String getName()
    {
        String name = super.getName();

        if (isManaged())
        {
            if (this.getMaster() != null)
            {
                name = this.getMaster().getName();
            }

            name = name.replaceAll(" ", "_");
        }

        return name;
    }

    /**
     * Gets the base path
     * 
     * @return the base path
     */
    public String getBasePath()
    {
        return super.getPath();
    }

    public String getPathByFormat(final DiskFormat format)
    {
        String directoryPath = this.toPojoHB().getDirectoryPath();

        final String viPath = getBasePath();
        final String viName = viPath.substring(viPath.lastIndexOf('/') + 1);

        if (this.getMaster() != null && !this.getMaster().isImageStateful())
        {
            directoryPath = this.getMaster().toPojoHB().getDirectoryPath();
        }

        // XXX String destination = directoryPath + "/formats/" + UUID.randomUUID() + "-" +
        // format.toString() + "-" + this.getName();
        String destination = directoryPath + "/formats/" + viName + "-" + format.toString();

        if (format.equals(DiskFormat.VHD_FLAT) || format.equals(DiskFormat.VHD_SPARSE))
        {
            // For hyper-v compatibility
            destination = vhdPath(destination);
        }

        return destination;
    }

    public String getNotManagedBundlePath()
    {
        return getIdEnterprise() + "/bundle/" + getName();
    }

    public String getNotManagedBundleName()
    {
        String name = getDecoratedName(getName(), "snapshot");
        if (isVhd(getPath()))
        {
            name = vhdPath(name);
        }
        return name;
    }

    private String getBundlePath(final String basePath)
    {
        return getDecoratedPath(basePath, "snapshot");
    }

    private String getDecoratedPath(final String basePath, final String identifier)
    {
        int index = basePath.lastIndexOf('/');
        String path = basePath.substring(0, index) + "/";
        String filename = basePath.substring(index + 1);

        return path + uuid + "-" + getDecoratedName(filename, identifier);
    }

    private String getDecoratedName(final String filename, final String identifier)
    {
        return identifier + "-" + filename;
    }

    @Override
    public VirtualimageHB toPojoHB()
    {
        VirtualimageHB image = super.toPojoHB();
        image.setVolumePath(volumePath);

        return image;
    }

    public void setVolumePath(final String volumePath)
    {
        this.volumePath = volumePath;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public String getVolumePath()
    {
        return volumePath;
    }

    private boolean isVhd(final String path)
    {
        return path.endsWith(".vhd");
    }

    private String vhdPath(final String path)
    {
        return path + ".vhd";
    }
}
