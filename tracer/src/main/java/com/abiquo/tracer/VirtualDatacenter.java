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

package com.abiquo.tracer;

import java.io.Serializable;

public class VirtualDatacenter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -349334484003700495L;

	private String name;
	private VirtualAppliance virtualAppliance;
	private Volume volume;
	private Network network;

	private VirtualDatacenter(String virtualDatacenter) {
		this.setName(virtualDatacenter);
	}

	public static VirtualDatacenter virtualDatacenter(String virtualDatacenter) {
		return new VirtualDatacenter(virtualDatacenter);
	}

	public VirtualDatacenter virtualAppliance(VirtualAppliance virtualAppliance) {
		this.setVirtualAppliance(virtualAppliance);
		return this;
	}

	public VirtualDatacenter volume(Volume volume) {
		this.setVolume(volume);
		return this;
	}

	public VirtualDatacenter network(Network network) {
		this.setNetwork(network);
		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setVirtualAppliance(VirtualAppliance virtualAppliance) {
		this.virtualAppliance = virtualAppliance;
	}

	public VirtualAppliance getVirtualAppliance() {
		return virtualAppliance;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public Network getNetwork() {
		return network;
	}
}
