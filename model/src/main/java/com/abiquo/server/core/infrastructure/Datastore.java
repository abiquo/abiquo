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

package com.abiquo.server.core.infrastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = Datastore.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = Datastore.TABLE_NAME)
public class Datastore extends DefaultEntityBase
{
    public static final String TABLE_NAME = "datastore";

    public Datastore()
    {
        // never call this!
    }

    private final static String ID_COLUMN = "idDatastore";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String NAME_PROPERTY = "name";

    private final static boolean NAME_REQUIRED = false;

    private final static int NAME_LENGTH_MIN = 0;

    private final static int NAME_LENGTH_MAX = 255;

    private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NAME_COLUMN = "name";

    @Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
    private String name;

    @Required(value = NAME_REQUIRED)
    @Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public final static String DIRECTORY_PROPERTY = "directory";

    private final static boolean DIRECTORY_REQUIRED = false;

    private final static int DIRECTORY_LENGTH_MIN = 0;

    private final static int DIRECTORY_LENGTH_MAX = 255;

    private final static boolean DIRECTORY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DIRECTORY_COLUMN = "directory";

    @Column(name = DIRECTORY_COLUMN, nullable = !DIRECTORY_REQUIRED, length = DIRECTORY_LENGTH_MAX)
    private String directory;

    @Required(value = DIRECTORY_REQUIRED)
    @Length(min = DIRECTORY_LENGTH_MIN, max = DIRECTORY_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DIRECTORY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDirectory()
    {
        return this.directory;
    }

    public void setDirectory(final String directory)
    {
        this.directory = directory;
    }

    public final static String ROOT_PATH_PROPERTY = "rootPath";

    private final static boolean ROOT_PATH_REQUIRED = true;

    private final static int ROOT_PATH_LENGTH_MIN = 1;

    public final static int ROOT_PATH_LENGTH_MAX = 42;

    private final static boolean ROOT_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ROOT_PATH_COLUMN = "rootPath";

    @Column(name = ROOT_PATH_COLUMN, nullable = !ROOT_PATH_REQUIRED, length = ROOT_PATH_LENGTH_MAX)
    private String rootPath;

    @Required(value = ROOT_PATH_REQUIRED)
    @Length(min = ROOT_PATH_LENGTH_MIN, max = ROOT_PATH_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ROOT_PATH_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getRootPath()
    {
        return this.rootPath;
    }

    public void setRootPath(final String rootPath)
    {
        this.rootPath = rootPath;
    }

    public final static String SIZE_PROPERTY = "size";

    /* package */final static String SIZE_COLUMN = "Size";

    /* package */final static long SIZE_MIN = Long.MIN_VALUE;

    /* package */final static long SIZE_MAX = Long.MAX_VALUE;

    /* package */final static boolean SIZE_REQUIRED = false;

    @Column(name = SIZE_COLUMN, nullable = true)
    @Range(min = SIZE_MIN, max = SIZE_MAX)
    private long size;

    @Required(value = SIZE_REQUIRED)
    public long getSize()
    {
        return this.size;
    }

    public void setSize(final long size)
    {
        this.size = size;
    }

    public final static String USED_SIZE_PROPERTY = "usedSize";

    /* package */final static String USED_SIZE_COLUMN = "UsedSize";

    /* package */final static long USED_SIZE_MIN = Long.MIN_VALUE;

    /* package */final static long USED_SIZE_MAX = Long.MAX_VALUE;

    /* package */final static boolean USED_SIZE_REQUIRED = false;

    @Column(name = USED_SIZE_COLUMN, nullable = true)
    @Range(min = USED_SIZE_MIN, max = USED_SIZE_MAX)
    private long usedSize;

    @Required(value = USED_SIZE_REQUIRED)
    public long getUsedSize()
    {
        return this.usedSize;
    }

    public void setUsedSize(final long usedSize)
    {
        this.usedSize = usedSize;
    }

    public final static String ENABLED_PROPERTY = "enabled";

    private final static String ENABLED_COLUMN = "enabled";

    private final static boolean ENABLED_REQUIRED = true;

    @Column(name = ENABLED_COLUMN, nullable = false)
    private boolean enabled = true;

    @Required(value = ENABLED_REQUIRED)
    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    public final static String DATASTORE_UUID_PROPERTY = "datastoreUUID";

    private final static boolean DATASTORE_UUID_REQUIRED = false;

    private final static int DATASTORE_UUID_LENGTH_MIN = 0;

    private final static int DATASTORE_UUID_LENGTH_MAX = 255;

    private final static boolean DATASTORE_UUID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String DATASTORE_UUID_COLUMN = "datastoreUuid";

    @Column(name = DATASTORE_UUID_COLUMN, nullable = !DATASTORE_UUID_REQUIRED, length = DATASTORE_UUID_LENGTH_MAX)
    private String datastoreUUID;

    @Required(value = DATASTORE_UUID_REQUIRED)
    @Length(min = DATASTORE_UUID_LENGTH_MIN, max = DATASTORE_UUID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = DATASTORE_UUID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getDatastoreUUID()
    {
        return this.datastoreUUID;
    }

    public void setDatastoreUUID(final String datastoreUUID)
    {
        this.datastoreUUID = datastoreUUID;
    }

    // code in Datastore
    public static final String MACHINES_PROPERTY = "machines";

    @ManyToMany(mappedBy = Machine.DATASTORES_PROPERTY, fetch = FetchType.LAZY)
    private List<Machine> machines = new ArrayList<Machine>();

    @Required(value = true)
    public List<Machine> getMachines()
    {
        return Collections.unmodifiableList(this.machines);
    }

    public void addToMachines(final Machine value)
    {
        assert value != null;
        assert !this.machines.contains(value);

        this.machines.add(value);
        value.addToDatastores(this);
    }

    public void removeFromMachines(final Machine value)
    {
        assert value != null;
        assert this.machines.contains(value);

        this.machines.remove(value);
        value.removeFromDatastores(this);
    }

    public Datastore(final Machine machine, final String name, final String rootPath,
        final String directory)
    {
        setRootPath(rootPath);
        setName(name);
        setDirectory(directory);
        addToMachines(machine);
    }

    /**
     * Two datastores are the same if their UUIDs are the same.
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj instanceof Datastore == false)
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }
        Datastore rhs = (Datastore) obj;
        return new EqualsBuilder().append(this.getDatastoreUUID(), rhs.getDatastoreUUID())
            .isEquals();
    }
}
