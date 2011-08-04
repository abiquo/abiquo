package com.abiquo.model.enumerator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PricingPeriod")
@XmlEnum
public enum PricingPeriod
{
    /* 0 */
    MINUTE,

    /* 1 */
    DAY,

    /* 2 */
    WEEK,

    /* 3 */
    MONTH,

    /* 4 */
    QUARTER,

    /* 5 */
    YEAR;

    public int id()
    {
        return ordinal();
    }

    public static PricingPeriod fromId(final int id)
    {
        return values()[id];
    }
}
