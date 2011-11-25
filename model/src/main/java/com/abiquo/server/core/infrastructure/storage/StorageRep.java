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

<<<<<<< HEAD
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageDAO;
import com.abiquo.server.core.cloud.VirtualDatacenter;
=======
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageDAO;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
>>>>>>> stable
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.stateful.DiskStatefulConversion;
import com.abiquo.server.core.cloud.stateful.DiskStatefulConversionDAO;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.management.RasdDAO;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;
import com.abiquo.server.core.util.FilterOptions;

/**
 * @author jdevesa
 */
@Repository
public class StorageRep extends DefaultRepBase
{
    @Autowired
    private StorageDeviceDAO deviceDAO;

    @Autowired
    private DiskManagementDAO diskManagementDAO;

    @Autowired
    private DiskStatefulConversionDAO diskStatefulConversionDAO;

    @Autowired
    private InitiatorMappingDAO initiatorMappingDAO;

    @Autowired
    private NodeVirtualImageDAO nodeVirtualImageDAO;

    @Autowired
    private StoragePoolDAO poolDAO;

    @Autowired
    private RasdDAO rasdDAO;

    @Autowired
    private RasdManagementDAO rasdManagementDAO;

    @Autowired
    private TierDAO tierDAO;

    @Autowired
    private VolumeManagementDAO volumeDAO;

    public StorageRep()
    {

    }

    public StorageRep(final EntityManager entityManager)
    {
        this.deviceDAO = new StorageDeviceDAO(entityManager);
        this.diskManagementDAO = new DiskManagementDAO(entityManager);
        this.diskStatefulConversionDAO = new DiskStatefulConversionDAO(entityManager);
        this.initiatorMappingDAO = new InitiatorMappingDAO(entityManager);
        this.nodeVirtualImageDAO = new NodeVirtualImageDAO(entityManager);
        this.poolDAO = new StoragePoolDAO(entityManager);
        this.rasdDAO = new RasdDAO(entityManager);
        this.rasdManagementDAO = new RasdManagementDAO(entityManager);
        this.tierDAO = new TierDAO(entityManager);
        this.volumeDAO = new VolumeManagementDAO(entityManager);
    }

    public InitiatorMapping findByVolumeAndInitiator(final Integer idVolumeManagement,
        final String initiatorIqn)
    {
        return initiatorMappingDAO.findByVolumeAndInitiator(idVolumeManagement, initiatorIqn);
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

<<<<<<< HEAD
    public DiskManagement findHardDiskByVirtualMachine(final VirtualMachine vm,
        final Integer diskOrder)
=======
    public Tier findTierById(final Integer tierId)
    {
        return tierDAO.findById(tierId);
    }

    public Tier findTierById(final Integer datacenterId, final Integer tierId)
>>>>>>> stable
    {
        return diskManagementDAO.findHardDisksByVirtualMachine(vm, diskOrder);
    }

    public List<DiskManagement> findHardDisksByVirtualMachine(final VirtualMachine vm)
    {
        return diskManagementDAO.findHardDisksByVirtualMachine(vm);
    }

    public List<NodeVirtualImage> findNodeVirtualImageByVirtualImage(final VirtualImage virtualImage)
    {
        return nodeVirtualImageDAO.findByVirtualImage(virtualImage);
    }

    public StoragePool findPoolById(final Integer deviceId, final String poolId)
    {
        return poolDAO.findPoolById(deviceId, poolId);
    }

    public List<InitiatorMapping> getInitiatorMappings(final Integer idVolume)
    {
        return initiatorMappingDAO.findByVolumeId(idVolume);
    }

    public InitiatorMapping getInitiatorMapping(final Integer mappingId)
    {
        return initiatorMappingDAO.findById(mappingId);
    }

    public StoragePool findPoolByName(final Integer deviceId, final String name)
    {
        return poolDAO.findPoolByName(deviceId, name);
    }

    public List<StoragePool> findPoolsByTier(final Tier tier)
    {
        return poolDAO.findPoolsByTier(tier);
    }

    public Tier findTierById(final Integer datacenterId, final Integer tierId)
    {
        return tierDAO.getTierById(datacenterId, tierId);
    }

    public VolumeManagement findVolumeById(final Integer volumeId)
    {
        return volumeDAO.findById(volumeId);
    }

    public VolumeManagement findVolumeByRasd(final Rasd rasd)
    {
        return volumeDAO.getVolumeByRasd(rasd);
    }

    public List<VolumeManagement> findVolumesByEnterprise(final Integer id,
        final FilterOptions filters)
    {
        return volumeDAO.getVolumesByEnterprise(id, filters);
    }

    public List<VolumeManagement> findVolumesByPool(final StoragePool pool,
        final FilterOptions filters)
    {
        return volumeDAO.getVolumesByPool(pool, filters);
    }

    public List< ? extends RasdManagement> findDisksAndVolumesByVirtualMachine(
        final VirtualMachine vm)
    {
        return rasdManagementDAO.findDisksAndVolumesByVirtualMachine(vm);
    }

    public List<StorageDevice> getDevicesByDatacenter(final Integer datacenterId)
    {
        return deviceDAO.getDevicesByDatacenter(datacenterId);
    }

    public DiskStatefulConversion getDiskStatefulConversionByVolume(final VolumeManagement volume)
    {
        return diskStatefulConversionDAO.getByVolume(volume.getId());
    }

    public List<StoragePool> getPoolsByDevice(final Integer deviceId)
    {
        return poolDAO.getPoolsByStorageDevice(deviceId);
    }

    public List<VolumeManagement> getStatefulCandidates(final VirtualDatacenter vdc)
    {
        return volumeDAO.getStatefulCandidates(vdc);
    }

    public List<VolumeManagement> getAttachedVolumes(final VirtualDatacenter vdc)
    {
        return volumeDAO.getAttachedVolumes(vdc);
    }

    public List<VolumeManagement> getDetachedVolumes(final VirtualDatacenter vdc)
    {
        return volumeDAO.getDetachedVolumes(vdc);
    }

    public List<VolumeManagement> getAvailableVolumes(final VirtualDatacenter vdc,
        final FilterOptions filterOptions)
    {
        return volumeDAO.getAvailableVolumes(vdc, filterOptions);
    }

    public List<Tier> getTiersByDatacenter(final Integer datacenterId)
    {
        return tierDAO.getTiersByDatacenter(datacenterId);
    }

<<<<<<< HEAD
    public VolumeManagement getVolumeByVirtualDatacenter(final VirtualDatacenter vdc,
        final Integer volumeId)
=======
    public VolumeManagement getVolumeFromImage(final Integer idImage)
>>>>>>> stable
    {
        return volumeDAO.getVolumeByVirtualDatacenter(vdc, volumeId);
    }

    public VolumeManagement getVolumeFromImage(final Integer idImage)
    {
        return volumeDAO.getVolumeFromImage(idImage);
    }

    public List<VolumeManagement> getVolumesByEnterprise(final int idEnterprise)
    {
        return volumeDAO.getVolumesFromEnterprise(idEnterprise);
    }

    public List<VolumeManagement> getVolumesByPool(final StoragePool pool)
    {
        return volumeDAO.getVolumesByPool(pool);
    }

    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc)
    {
        return volumeDAO.getVolumesByVirtualDatacenter(vdc);
    }

    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc,
        final FilterOptions filterOptions)
    {
        return volumeDAO.getVolumesByVirtualDatacenter(vdc, filterOptions);
    }

    public List<VolumeManagement> getVolumesByVirtualMachine(final VirtualMachine vm)
    {
        return volumeDAO.getVolumesByVirtualMachine(vm);
    }

    public List<VolumeManagement> getVolumesByVirtualMachine(final VirtualMachine vm,
        final FilterOptions filters)
    {
        return volumeDAO.getVolumesByVirtualMachine(vm, filters);
    }

    public StorageDevice insertDevice(final StorageDevice sd)
    {
        deviceDAO.persist(sd);
        deviceDAO.flush();

        return sd;
    }

    public void insertHardDisk(final DiskManagement createdDisk)
    {
        if (createdDisk.getRasd() != null)
        {
            rasdDAO.persist(createdDisk.getRasd());
        }
        diskManagementDAO.persist(createdDisk);
    }

    public void insertInitiatorMapping(final InitiatorMapping imapping)
    {
        initiatorMappingDAO.persist(imapping);
    }

    public StoragePool insertPool(final StoragePool sp)
    {
        poolDAO.persist(sp);
        poolDAO.flush();

        return sp;
    }

    public Tier insertTier(final Tier tier)
    {
        tierDAO.persist(tier);
        tierDAO.flush();

        return tier;
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

    public void removeHardDisk(final DiskManagement diskToDelete)
    {
        Rasd rasd = diskToDelete.getRasd();
        diskManagementDAO.remove(diskToDelete);
        rasdDAO.remove(rasd);
        diskManagementDAO.flush();

    }

    public void removePool(final StoragePool sp)
    {
        poolDAO.remove(sp);
        poolDAO.flush();
    }

    public void removeVolume(final VolumeManagement volume)
    {
        Rasd rasd = volume.getRasd();
        volumeDAO.remove(volume);
        rasdDAO.remove(rasd);
        volumeDAO.flush();
    }

    public void updateDevice(final StorageDevice sd)
    {
        deviceDAO.flush();
    }

    public void updatePool(final StoragePool pool)
    {
        poolDAO.flush();
    }

<<<<<<< HEAD
    public void updateTier(final Tier tier)
=======
    public void updateVolume(final VolumeManagement volume)
    {
        volumeDAO.flush();
    }

    public List<VolumeManagement> getVolumesByVirtualMachine(final VirtualMachine vm)
>>>>>>> stable
    {
        tierDAO.flush();
    }

<<<<<<< HEAD
    public void updateVolume(final VolumeManagement volume)
=======
    public void insertInitiatorMapping(final InitiatorMapping imapping)
>>>>>>> stable
    {
        volumeDAO.flush();
    }
}
