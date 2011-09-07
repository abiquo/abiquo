package com.abiquo.server.core.infrastructure.nodecollector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;

public class LogicServerPoliciy
{
    @XmlElement(required = true)
    protected String dn;

    // It can be template (update or initial) or instance
    @XmlElement(required = true)
    protected String name;

    @XmlElement(required = true)
    protected String priority;

    @XmlElement(required = false)
    protected String description;

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setType(final String value)
    {
        this.type = value;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setName(final String value)
    {
        this.name = value;
    }

    /**
     * Gets the value of the associated property.
     * 
     * @return possible object is {@link String }
     */
    public String getAssociated()
    {
        return associated;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the value of the associated property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setAssociated(final String value)
    {
        this.associated = value;
    }

    /**
     * Gets the value of the policies property.
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is
     * why there is not a <CODE>set</CODE> method for the content property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getContent().add(newItem);
     * </pre>
     * <p>
     * Objects of the following type(s) are allowed in the list {@link JAXBElement }{@code <}
     * {@link ConfigSet }{@code >} {@link String }
     */
    public List<Serializable> policies()
    {
        if (policies == null)
        {
            policies = new ArrayList<Serializable>();
        }
        return this.policies;
    }

    /**
     * Gets the value of the associatedTo property.
     * 
     * @return possible object is {@link String }
     */
    public String getAssociatedTo()
    {
        return associatedTo;
    }

    /**
     * Sets the value of the associatedTo property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setAssociatedTo(final String value)
    {
        this.associatedTo = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return possible object is {@link String }
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setDescription(final String value)
    {
        this.description = value;
    }

}
