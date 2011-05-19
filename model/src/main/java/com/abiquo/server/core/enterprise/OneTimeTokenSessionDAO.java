package com.abiquo.server.core.enterprise;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

/**
 * @author ssedano
 */
@Repository("jpaOneTimeTokeSessionDAO")
public class OneTimeTokenSessionDAO extends DefaultDAOBase<Integer, OneTimeTokenSession>
{
    public OneTimeTokenSessionDAO()
    {
        super(OneTimeTokenSession.class);
    }

    public OneTimeTokenSessionDAO(EntityManager entityManager)
    {
        super(OneTimeTokenSession.class, entityManager);
    }

    /**
     * HQL to consume tokens.
     */
    private static String CONSUME_TOKEN = "delete " + OneTimeTokenSession.class.getSimpleName()
        + " where token = :token";

    /**
     * Consume the given token. Which actually deletes the token. Returns the number of rows
     * affected by the update. Ideally only 1.
     * 
     * @param token token to be consumed.
     * @return number of rows affected.
     */
    public int consumeToken(String token)
    {
        Query query = this.getSession().createQuery(CONSUME_TOKEN);
        query.setString("token", token);
        return query.executeUpdate();
    }

}
