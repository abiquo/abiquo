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
package com.abiquo.abiserver.business.hibernate.pojohb.infrastructure;

import java.util.List;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.infrastructure.Datastore;

/**
 * Represents a mounted point resource in the host machine
 * 
 * @author pnavarro
 */
public class DatastoreHB implements IPojoHB<Datastore>
{

    private Integer idDatastore;

    private String name;

    private String rootPath;

    private Boolean enabled;

    private String directory;

    private Long size;

    private Long usedSize;
    
    private String datastoreUUID;
    
    private Set<PhysicalmachineHB> pmList;
    
    /**
     * Sets the id
     * 
     * @param idDatastore the idDatastore to set
     */
    public void setIdDatastore(Integer idDatastore)
    {
        this.idDatastore = idDatastore;
    }

    /**
     * Gets the id
     * 
     * @return the idDatastore
     */
    public Integer getIdDatastore()
    {
        return idDatastore;
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
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Checks if is the default datastore
     * 
     * @return the defaulDatastore
     */
    public Boolean getEnabled()
    {
        return enabled;
    }

    /**
     * Sets the defaulDatastore flag
     * 
     * @param defaulDatastore the defaulDatastore to set
     */
    public void setEnabled(Boolean enabled)
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
    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    /**
     * Sets the datastore capacity
     * 
     * @param size the size to set
     */
    public void setSize(Long size)
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
    public void setUsedSize(Long usedSize)
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
     * @param datastoreRootPath the datastoreRootPath to set
     */
    public void setRootPath(String rootPath)
    {
        this.rootPath = rootPath;
    }

    /**
     * @return the datastoreRootPath
     */
    public String getRootPath()
    {
        return rootPath;
    }
    /**
     * @param pmList the pmList to set
     */
    public void setPmList(Set<PhysicalmachineHB> pmList)
    {
        this.pmList = pmList;
    }

    /**
     * @return the pmList
     */
    public Set<PhysicalmachineHB> getPmList()
    {
        return pmList;
    }
    
    public void setDatastoreUUID(String datastoreUUID)
    {
        this.datastoreUUID = datastoreUUID;
    }

    public String getDatastoreUUID()
    {
        return datastoreUUID;
    }
        
    @Override
    public Datastore toPojo()
    {
        Datastore ds = new Datastore();
        ds.setEnabled(enabled);
        ds.setDirectory(directory);
        ds.setId(getIdDatastore());
        ds.setName(name);
        ds.setUUID(getRootPath());
        ds.setSize(size);
        ds.setUsedSize(usedSize);
        ds.setDatastoreUUID(datastoreUUID);
        return ds;

    }

    

   
}
