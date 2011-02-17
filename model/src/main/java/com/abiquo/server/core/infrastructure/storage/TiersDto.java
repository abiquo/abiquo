/**
 * 
 */
package com.abiquo.server.core.infrastructure.storage;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.WrapperDto;

/**
 * @author jdevesa@abiquo.com
 *
 */
@XmlRootElement(name = "tiers")
public class TiersDto extends WrapperDto<TierDto>{

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -3711847757260940874L;

	@Override
	@XmlElement(name = "tier")
	public List<TierDto> getCollection() {
		
		if (collection == null)
		{
			collection = new ArrayList<TierDto>();
		}
		return collection;
	}

}
