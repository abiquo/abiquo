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
	public StorageRep() 
	{
		
	}
	
	public StorageRep(EntityManager entityManager)
	{
        assert entityManager != null;
        assert entityManager.isOpen();
        
        this.tierDAO = new TierDAO(entityManager);
        this.deviceDAO = new StorageDeviceDAO(entityManager);
	}
	
	@Autowired 
	private TierDAO tierDAO;
	
	@Autowired
	private StorageDeviceDAO deviceDAO;
	
	public List<Tier> getTiersByDatacenter(final Integer datacenterId)
	{
		return tierDAO.getTiersByDatacenter(datacenterId);
	}

	public List<StorageDevice> getDevicesByDatacenter(final Integer datacenterId) 
	{
		return deviceDAO.getDevicesByDatacenter(datacenterId);
	}

	public Tier findTierById(Integer datacenterId, Integer tierId) 
	{
		return tierDAO.getTierById(datacenterId, tierId);
	}

    public StorageDevice insertDevice(StorageDevice sd)
    {
        deviceDAO.persist(sd);
        deviceDAO.flush();
        
        return sd;
    }

}
