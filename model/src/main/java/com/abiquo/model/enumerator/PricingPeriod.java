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
    HOUR,

    /* 2 */
    DAY,

    /* 3 */
    WEEK,

    /* 4 */
    MONTH,

    /* 5 */
    QUARTER,

    /* 6 */
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
