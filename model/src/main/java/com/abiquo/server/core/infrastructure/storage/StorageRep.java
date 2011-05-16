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
 * 
 */
package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.util.FilterOptions;

/**
 * @author jdevesa
 */
@Repository
public class StorageRep extends DefaultRepBase
{
    @Autowired
    private TierDAO tierDAO;

    @Autowired
    private StorageDeviceDAO deviceDAO;

    @Autowired
    private StoragePoolDAO poolDAO;

    @Autowired
    private VolumeManagementDAO volumeDAO;

    @Autowired
    private RasdDAO rasdDAO;

    public StorageRep()
    {

    }

    public StorageRep(final EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.tierDAO = new TierDAO(entityManager);
        this.deviceDAO = new StorageDeviceDAO(entityManager);
        this.poolDAO = new StoragePoolDAO(entityManager);
        this.volumeDAO = new VolumeManagementDAO(entityManager);
    }

    public StorageDevice findDeviceById(final Integer datacenterId, final Integer deviceId)
    {
        return deviceDAO.getDeviceById(datacenterId, deviceId);
    }

    public StorageDevice findDeviceByManagementIP(final Integer datacenterId,
        final String managementIp)
    {
        return deviceDAO.findDeviceByManagementIP(datacenterId, managementIp);
    }

    public Tier findTierById(final Integer datacenterId, final Integer tierId)
    {
        return tierDAO.getTierById(datacenterId, tierId);
    }

    public StoragePool findPoolById(final Integer deviceId, final String poolId)
    {
        return poolDAO.findPoolById(deviceId, poolId);
    }

    public StoragePool findPoolByName(final Integer deviceId, final String name)
    {
        return poolDAO.findPoolByName(deviceId, name);
    }

    public VolumeManagement findVolumeById(final Integer volumeId)
    {
        return volumeDAO.findById(volumeId);
    }

    public List<StoragePool> findPoolsByTier(final Tier tier)
    {
        return poolDAO.findPoolsByTier(tier);
    }

    public List<StorageDevice> getDevicesByDatacenter(final Integer datacenterId)
    {
        return deviceDAO.getDevicesByDatacenter(datacenterId);
    }

    public List<StoragePool> getPoolsByDevice(final Integer deviceId)
    {
        return poolDAO.getPoolsByStorageDevice(deviceId);
    }

    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc,
        final FilterOptions filterOptions) throws Exception
    {
        return volumeDAO.getVolumesByVirtualDatacenter(vdc, filterOptions);
    }

    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc)
    {
        return volumeDAO.getVolumesByVirtualDatacenter(vdc);
    }

    public VolumeManagement getVolumeByVirtualDatacenter(final VirtualDatacenter vdc,
        final Integer volumeId)
    {
        return volumeDAO.getVolumeByVirtualDatacenter(vdc, volumeId);
    }

    public List<VolumeManagement> getVolumesByPool(final StoragePool pool)
    {
        return volumeDAO.getVolumesByPool(pool);
    }

    public List<VolumeManagement> getVolumesByEnterprise(final int idEnterprise)
    {
        return volumeDAO.getVolumesFromEnterprise(idEnterprise);
    }

    public List<Tier> getTiersByDatacenter(final Integer datacenterId)
    {
        return tierDAO.getTiersByDatacenter(datacenterId);
    }

    public List<VolumeManagement> findVolumesByEnterprise(final Integer id,
        final FilterOptions filters)
    {
        return volumeDAO.getVolumesByEnterprise(id, filters);
    }

    public Tier insertTier(final Tier tier)
    {
        tierDAO.persist(tier);
        tierDAO.flush();

        return tier;
    }

    public StorageDevice insertDevice(final StorageDevice sd)
    {
        deviceDAO.persist(sd);
        deviceDAO.flush();

        return sd;
    }

    public StoragePool insertPool(final StoragePool sp)
    {
        poolDAO.persist(sp);
        poolDAO.flush();

        return sp;
    }

    public VolumeManagement insertVolume(final VolumeManagement volume)
    {
        rasdDAO.persist(volume.getRasd());
        volumeDAO.persist(volume);
        volumeDAO.flush();

        return volume;
    }

    public void removeDevice(final StorageDevice sd)
    {
        deviceDAO.remove(sd);
        deviceDAO.flush();
    }

    public void removePool(final StoragePool sp)
    {
        poolDAO.remove(sp);
        poolDAO.flush();
    }

    public void removeVolume(final VolumeManagement volume)
    {
        // volumeDAO.remove(volume);
        // volumeDAO.flush();
    }

    public void updateDevice(final StorageDevice sd)
    {
        deviceDAO.flush();
    }

    public void updateTier(final Tier tier)
    {
        tierDAO.flush();
    }

    public void updatePool(final StoragePool pool)
    {
        poolDAO.flush();
    }

}
