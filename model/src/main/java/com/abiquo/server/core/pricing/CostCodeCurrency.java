package com.abiquo.server.core.pricing;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = CostCodeCurrency.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = CostCodeCurrency.TABLE_NAME)
public class CostCodeCurrency extends DefaultEntityBase
{
    public static final String TABLE_NAME = "costeCodeCurrency";

    // DO NOT ACCESS: present due to needs of infrastructure support. *NEVER* call from business
    // code
    protected CostCodeCurrency()
    {
        // Just for JPA support
    }

    public CostCodeCurrency(final BigDecimal purchase, final CostCode costCode,
        final Currency currency)
    {
        super();
        setPurchase(purchase);
        setCostCode(costCode);
        setCurrency(currency);
    }

    private final static String ID_COLUMN = "idCosteCodeCurrency";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    @Override
    public Integer getId()
    {
        return this.id;
    }

    public final static String PURCHASE_PROPERTY = "purchase";

    private final static String PURCHASE_COLUMN = "purchase";

    @Column(name = PURCHASE_COLUMN, nullable = true)
    private BigDecimal purchase;

    public BigDecimal getPurchase()
    {
        return this.purchase;
    }

    public void setPurchase(final BigDecimal purchase)
    {
        this.purchase = purchase;
    }

    public final static String COST_CODE_PROPERTY = "costCode";

    private final static boolean COST_CODE_REQUIRED = true;

    private final static String COST_CODE_ID_COLUMN = "idCostCode";

    @JoinColumn(name = COST_CODE_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_costCode")
    private CostCode costCode;

    @Required(value = COST_CODE_REQUIRED)
    public CostCode getCostCode()
    {
        return this.costCode;
    }

    public void setCostCode(final CostCode costCode)
    {
        this.costCode = costCode;
    }

    public final static String CURRENCY_PROPERTY = "currency";

    private final static boolean CURRENCY_REQUIRED = true;

    private final static String CURRENCY_ID_COLUMN = "idCurrency";

    @JoinColumn(name = CURRENCY_ID_COLUMN)
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_" + TABLE_NAME + "_currency")
    private Currency currency;

    @Required(value = CURRENCY_REQUIRED)
    public Currency getCurrency()
    {
        return this.currency;
    }

    public void setCurrency(final Currency currency)
    {
        this.currency = currency;
    }

}
