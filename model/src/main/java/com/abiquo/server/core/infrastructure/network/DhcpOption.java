package com.abiquo.server.core.infrastructure.network;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.model.validation.Ip;
import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = DhcpOption.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = DhcpOption.TABLE_NAME)
public class DhcpOption extends DefaultEntityBase
{
    public static final String TABLE_NAME = "dhcpOption";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected DhcpOption()
    {
        // Just for JPA support
    }

    public DhcpOption(final Integer option, final String address, final Integer mask,
        final String netmask, final String gateway)
    {
        setAddress(address);
        setMask(mask);
        setOption(option);
        setNetMask(netmask);
        setGateway(gateway);
    }

    private final static String ID_COLUMN = "idDhcpOption";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String OPTION_PROPERTY = "option";

    private final static boolean OPTION_REQUIRED = false;

    private final static String OPTION_COLUMN = "option";

    private final static int OPTION_MIN = Integer.MIN_VALUE;

    private final static int OPTION_MAX = Integer.MAX_VALUE;

    @Column(name = OPTION_COLUMN, nullable = !OPTION_REQUIRED)
    @Range(min = OPTION_MIN, max = OPTION_MAX)
    private Integer option;

    public Integer getOption()
    {
        return this.option;
    }

    private void setOption(final Integer option)
    {
        this.option = option;
    }

    // ******************************* Properties *******************************
    public final static String GATEWAY_PROPERTY = "gateway";

    private final static boolean GATEWAY_REQUIRED = true;

    private final static int GATEWAY_LENGTH_MIN = 1;

    private final static int GATEWAY_LENGTH_MAX = 40;

    private final static boolean GATEWAY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String GATEWAY_COLUMN = "gateway";

    @Column(name = GATEWAY_COLUMN, nullable = !GATEWAY_REQUIRED, length = GATEWAY_LENGTH_MAX)
    private String gateway;

    @Required(value = GATEWAY_REQUIRED)
    @Length(min = GATEWAY_LENGTH_MIN, max = GATEWAY_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = GATEWAY_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getGateway()
    {
        return this.gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public final static String ADDRESS_PROPERTY = "address";

    private final static boolean ADDRESS_REQUIRED = true;

    public final static int ADDRESS_LENGTH_MIN = 1;

    public final static int ADDRESS_LENGTH_MAX = 40;

    private final static boolean ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String ADDRESS_COLUMN = "network_address";

    @Column(name = ADDRESS_COLUMN, nullable = !ADDRESS_REQUIRED, length = ADDRESS_LENGTH_MAX)
    private String address;

    @Required(value = ADDRESS_REQUIRED)
    @Length(min = ADDRESS_LENGTH_MIN, max = ADDRESS_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = ADDRESS_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    @Ip
    public String getAddress()
    {
        return this.address;
    }

    public void setAddress(final String address)
    {
        this.address = address;
    }

    public final static String MASK_PROPERTY = "mask";

    private final static boolean MASK_REQUIRED = true;

    private final static String MASK_COLUMN = "mask";

    private final static long MASK_MIN_VALUE = 0L;

    private final static long MASK_MAX_VALUE = 31L;

    @Column(name = MASK_COLUMN, nullable = !MASK_REQUIRED)
    private Integer mask;

    @Required(value = MASK_REQUIRED)
    @Min(MASK_MIN_VALUE)
    @Max(MASK_MAX_VALUE)
    public Integer getMask()
    {
        return this.mask;
    }

    public void setMask(final Integer mask)
    {
        this.mask = mask;
    }

    public final static String NETMASK_PROPERTY = "netMask";

    private final static boolean NETMASK_REQUIRED = true;

    public final static int NETMASK_LENGTH_MIN = 0;

    public final static int NETMASK_LENGTH_MAX = 20;

    private final static boolean NETMASK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String NETMASK_COLUMN = "netmask";

    @Column(name = NETMASK_COLUMN, nullable = !NETMASK_REQUIRED, length = NETMASK_LENGTH_MAX)
    private String netMask;

    @Required(value = NETMASK_REQUIRED)
    @Length(min = NETMASK_LENGTH_MIN, max = NETMASK_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = NETMASK_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getNetMask()
    {
        return this.netMask;
    }

    private void setNetMask(final String netMask)
    {
        this.netMask = netMask;
    }

}
