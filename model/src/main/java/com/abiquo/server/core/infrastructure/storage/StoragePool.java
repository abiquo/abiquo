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

package com.abiquo.server.core.infrastructure.storage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.Length;

import com.abiquo.server.core.common.GenericEnityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = StoragePool.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = StoragePool.TABLE_NAME)
public class StoragePool extends GenericEnityBase<String> {
	public static final String TABLE_NAME = "storage_pool";

	protected StoragePool() {
	}
	
	public StoragePool(String id, String name)
	{
		this.setId(id);
		this.setName(name);
	}

    private final static String ID_COLUMN = "idStorage";

    /* package */final static int ID_LENGTH_MIN = 1;
    /* package */final static int ID_LENGTH_MAX = 40;
    private final static boolean ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    @Id
    @Column(name = ID_COLUMN, nullable = false, length = ID_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ID_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    private String id;

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

	public final static String NAME_PROPERTY = "name";
	private final static boolean NAME_REQUIRED = true;
	private final static int NAME_LENGTH_MIN = 0;
	private final static int NAME_LENGTH_MAX = 255;
	private final static boolean NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;
	private final static String NAME_COLUMN = "name";

	@Column(name = NAME_COLUMN, nullable = !NAME_REQUIRED, length = NAME_LENGTH_MAX)
	private String name;

	@Required(value = NAME_REQUIRED)
	@Length(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX)
	@LeadingOrTrailingWhitespace(allowed = NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public final static String TIER_PROPERTY = "tier";
	private final static boolean TIER_REQUIRED = true;
	private final static String TIER_ID_COLUMN = "idTier";

	@JoinColumn(name = TIER_ID_COLUMN)
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "FK_" + TABLE_NAME + "_tier")
	private Tier tier;

	@Required(value = TIER_REQUIRED)
	public Tier getTier() {
		return this.tier;
	}

	public void setTier(Tier tier) {
		this.tier = tier;
	}

	public final static String CABINET_PROPERTY = "cabinet";
	private final static boolean CABINET_REQUIRED = true;
	private final static String CABINET_ID_COLUMN = "idCabin";

	@JoinColumn(name = CABINET_ID_COLUMN)
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "FK_" + TABLE_NAME + "_cabinet")
	private Cabinet cabinet;

	@Required(value = CABINET_REQUIRED)
	public Cabinet getCabinet() {
		return this.cabinet;
	}

	public void setCabinet(Cabinet cabinet) {
		this.cabinet = cabinet;
	}

}
