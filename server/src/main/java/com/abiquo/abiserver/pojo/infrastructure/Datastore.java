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

/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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
package com.abiquo.abiserver.pojo.infrastructure;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatastoreHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.DatastoreDto;

/**
 * Represents a mounted point resource in the host machine
 * 
 * @author pnavarro
 */
public class Datastore implements IPojo<DatastoreHB>
{
    private Integer id;

    private String name;

    private String UUID;

    private Boolean enabled;

    private String directory;

    private String datastoreUUID;

    private Long size;

    private Long usedSize;

    public Datastore()
    {
        id = 0;
        name = "";
    }

    /**
     * Gets the id
     * 
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * Sets the id
     * 
     * @param id the id to set
     */
    public void setId(final Integer id)
    {
        this.id = id;
    }

    /**
     * Gets the name
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name
     * 
     * @param name the name to set
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Gets the UUID
     * 
     * @return the uUID
     */
    public String getUUID()
    {
        return UUID;
    }

    /**
     * Sets the UUID
     * 
     * @param uUID the uUID to set
     */
    public void setUUID(final String uUID)
    {
        UUID = uUID;
    }

    /**
     * Checks if is the datastore is enabled
     * 
     * @return the enabled value
     */
    public Boolean getEnabled()
    {
        return enabled;
    }

    /**
     * Sets the enabled flag
     * 
     * @param enabled the enabled to set
     */
    public void setEnabled(final Boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Gets the mounted point
     * 
     * @return the directory
     */
    public String getDirectory()
    {
        return directory;
    }

    /**
     * Sets the mounted point
     * 
     * @param directory the directory to set
     */
    public void setDirectory(final String directory)
    {
        this.directory = directory;
    }

    /**
     * Sets the datastore capacity
     * 
     * @param size the size to set
     */
    public void setSize(final Long size)
    {
        this.size = size;
    }

    /**
     * Gets the datastore capacity
     * 
     * @return the size
     */
    public Long getSize()
    {
        return size;
    }

    /**
     * @param usedSize the usedSize to set
     */
    public void setUsedSize(final Long usedSize)
    {
        this.usedSize = usedSize;
    }

    /**
     * @return the usedSize
     */
    public Long getUsedSize()
    {
        return usedSize;
    }

    /**
     * Sets UUID used to identify this Datastore in Abiquo
     * 
     * @param datastoreUUID
     */
    public void setDatastoreUUID(final String datastoreUUID)
    {
        this.datastoreUUID = datastoreUUID;
    }

    /**
     * Gets UUID used to identify this Datastore in Abiquo
     * 
     * @return
     */
    public String getDatastoreUUID()
    {
        return datastoreUUID;
    }

    @Override
    public DatastoreHB toPojoHB()
    {
        DatastoreHB datastore = new DatastoreHB();

        datastore.setDirectory(this.getDirectory());
        datastore.setEnabled(this.getEnabled());
        datastore.setIdDatastore(this.getId());
        datastore.setName(this.getName());
        datastore.setRootPath(this.getUUID());
        datastore.setSize(size);
        datastore.setUsedSize(usedSize);
        datastore.setDatastoreUUID(datastoreUUID);

        return datastore;
    }

    public DatastoreDto toDto()
    {
        DatastoreDto dto = new DatastoreDto();

        dto.setDatastoreUUID(this.datastoreUUID);
        dto.setDirectory(this.directory);
        dto.setEnabled(this.enabled);
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setRootPath(this.UUID);
        dto.setSize(this.size);
        dto.setUsedSize(this.usedSize);

        return dto;
    }

    public static Datastore fromDto(final DatastoreDto dto)
    {
        Datastore d = new Datastore();

        d.setDatastoreUUID(dto.getDatastoreUUID());
        d.setDirectory(dto.getDirectory());
        d.setEnabled(dto.isEnabled());
        d.setId(dto.getId());
        d.setName(dto.getName());
        d.setUUID(dto.getRootPath());
        d.setSize(dto.getSize());
        d.setUsedSize(dto.getUsedSize());

        return d;
    }

}
