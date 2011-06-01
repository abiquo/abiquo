package com.abiquo.server.core.infrastructure;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.validation.Ip;
import com.abiquo.model.validation.Port;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = UcsRack.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = UcsRack.TABLE_NAME)
public class UcsRack extends Rack
{
    public static final String TABLE_NAME = "ucs_rack";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER*
    // call from business code
    public UcsRack()
    {
        // Just for JPA support
    }

    /* package */UcsRack(final String name, final Datacenter datacenter, final Integer vlanIdMin,
        final Integer vlanIdMax, final Integer vlanPerVdcExpected, final Integer nrsq, final String ip,
        final Integer port, final String user, final String password)
    {
        setDatacenter(datacenter);
        setName(name);
        setVlanIdMin(vlanIdMin);
        setVlanIdMax(vlanIdMax);
        setVlanPerVdcExpected(vlanPerVdcExpected);
        setNrsq(nrsq);
        setIp(ip);
        setPort(port);
        setUser(user);
        setPassword(password);
    }

    public final static String PORT_PROPERTY = "port";

    private final static boolean PORT_REQUIRED = true;

    private final static String PORT_COLUMN = "port";

    private final static int PORT_MIN = 0;

    private final static int PORT_MAX = Integer.MAX_VALUE;

    @Column(name = PORT_COLUMN, nullable = !PORT_REQUIRED)
    @Range(min = PORT_MIN, max = PORT_MAX)
    @Required(value = PORT_REQUIRED)
    @Port
    private Integer port;

    public Integer getPort()
    {
        return this.port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public final static String IP_PROPERTY = "ip";

    private final static boolean IP_REQUIRED = true;

    private final static int IP_LENGTH_MIN = 0;

    private final static int IP_LENGTH_MAX = 255;

    private final static boolean IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String IP_COLUMN = "ip";

    @Column(name = IP_COLUMN, nullable = !IP_REQUIRED, length = IP_LENGTH_MAX)
    private String ip;

    @Required(value = IP_REQUIRED)
    @Length(min = IP_LENGTH_MIN, max = IP_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = IP_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getIp()
    {
        return this.ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public final static String PASSWORD_PROPERTY = "password";

    private final static boolean PASSWORD_REQUIRED = true;

    private final static int PASSWORD_LENGTH_MIN = 0;

    private final static int PASSWORD_LENGTH_MAX = 255;

    private final static boolean PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String PASSWORD_COLUMN = "password";

    @Column(name = PASSWORD_COLUMN, nullable = !PASSWORD_REQUIRED, length = PASSWORD_LENGTH_MAX)
    private String password;

    @Required(value = PASSWORD_REQUIRED)
    @Length(min = PASSWORD_LENGTH_MIN, max = PASSWORD_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = PASSWORD_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public final static String USER_PROPERTY = "user";

    private final static boolean USER_REQUIRED = true;

    private final static int USER_LENGTH_MIN = 0;

    private final static int USER_LENGTH_MAX = 255;

    private final static boolean USER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String USER_COLUMN = "user_rack";

    @Column(name = USER_COLUMN, nullable = !USER_REQUIRED, length = USER_LENGTH_MAX)
    private String user;

    @Required(value = USER_REQUIRED)
    @Length(min = USER_LENGTH_MIN, max = USER_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = USER_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getUser()
    {
        return this.user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

}
