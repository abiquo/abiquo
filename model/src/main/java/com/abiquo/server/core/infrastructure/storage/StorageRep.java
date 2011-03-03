/**
 * 
 */
package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;

/**
 * @author jdevesa
 *
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
	
	public StorageRep() 
	{
		
	}
	
	public StorageRep(EntityManager entityManager)
	{
        assert entityManager != null;
        assert entityManager.isOpen();
        
        this.tierDAO = new TierDAO(entityManager);
        this.deviceDAO = new StorageDeviceDAO(entityManager);
        this.poolDAO = new StoragePoolDAO(entityManager);
	}
	
	public StorageDevice findDeviceById(final Integer datacenterId, final Integer deviceId)
    {
        return deviceDAO.getDeviceById(datacenterId, deviceId);
    }

    public StorageDevice findDeviceByManagementIP(final Integer datacenterId, final String managementIp)
    {
        return deviceDAO.findDeviceById(datacenterId, managementIp);
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

    public List<VolumeManagement> getVolumesByPool(final StoragePool pool)
    {
        return volumeDAO.getVolumesByPool(pool);
    }
    
    public List<Tier> getTiersByDatacenter(final Integer datacenterId)
	{
		return tierDAO.getTiersByDatacenter(datacenterId);
	}

    public StorageDevice insertDevice(StorageDevice sd)
    {
        deviceDAO.persist(sd);
        deviceDAO.flush();
        
        return sd;
    }
    
    public StoragePool insertPool(StoragePool sp)
    {
        poolDAO.persist(sp);
        poolDAO.flush();
        
        return sp;
    }
    
    public void removeDevice(StorageDevice sd)
    {   
        deviceDAO.remove(sd);
        deviceDAO.flush();
    }

    public void removePool(StoragePool sp)
    {
        poolDAO.remove(sp);
        poolDAO.flush();        
    }
    
    public void updateDevice(StorageDevice sd)
    {
        deviceDAO.flush();        
    }

    public void updateTier(Tier tier)
    {
        tierDAO.flush();        
    }

    public void updatePool(StoragePool pool)
    {
        poolDAO.flush();
    }

}
