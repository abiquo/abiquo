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

import java.util.ArrayList;

/**
 * This class is used to return all the necessary information to manage Virtual Images
 * 
 * @author Oliver
 */

public class VirtualImageResult
{
    private ArrayList<Category> categories;

    private ArrayList<Repository> repositories;

    private ArrayList<Icon> icons;

    private ArrayList<DiskFormatType> virtualImageTypes;

    public VirtualImageResult()
    {
        categories = new ArrayList<Category>();
        repositories = new ArrayList<Repository>();
    }

    public ArrayList<Category> getCategories()
    {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories)
    {
        this.categories = categories;
    }

    public ArrayList<Repository> getRepositories()
    {
        return repositories;
    }

    public void setRepositories(ArrayList<Repository> repositories)
    {
        this.repositories = repositories;
    }

    public ArrayList<Icon> getIcons()
    {
        return icons;
    }

    public void setIcons(ArrayList<Icon> icons)
    {
        this.icons = icons;
    }

    public ArrayList<DiskFormatType> getVirtualImageTypes()
    {
        return virtualImageTypes;
    }

    public void setVirtualImageTypes(ArrayList<DiskFormatType> virtualImageTypes)
    {
        this.virtualImageTypes = virtualImageTypes;
    }

}
