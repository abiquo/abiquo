package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaDhcpOptionDAO")
public class DhcpOptionDAO extends DefaultDAOBase<Integer, DhcpOption>
{
    public DhcpOptionDAO()
    {
        super(DhcpOption.class);
    }

    public DhcpOptionDAO(final EntityManager entityManager)
    {
        super(DhcpOption.class, entityManager);
    }

    public List<DhcpOption> findDhcpOptionByOption(final int option)
    {
        return findByCriterions(Restrictions.eq(DhcpOption.OPTION_PROPERTY, option));
    }
}
