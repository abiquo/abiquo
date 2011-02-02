/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * 
 */
package com.abiquo.ovfmanager.ovf.section;

import java.util.ArrayList;
import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.MsgType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType.Icon;
import org.dmtf.schemas.ovf.envelope._1.ProductSectionType.Property;

import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

/**
 * @author jdevesa
 */
public class OVFProductUtils
{

    /**
     * Sets the following tags inside the <ProductSection> · <Product> · <Vendor> · <Version> ·
     * <FullVersion> · <ProductUrl> · <VendorUrl>
     * 
     * @param product ProductSectionType where will insert the values
     * @param productName (optional) specifies the name of the product.
     * @param productVendorName (optional) specifies the name of the product vendor.
     * @param productVersion (optional) specifies the product version in short form
     * @param productFullVersion (optional) describes the product version in the long form
     * @param productUrl (optional) specifies a URL which shall resolve to a human readable
     *            description of the product
     * @param productVendorUrl (optinal)
     * @throws RequiredAttributeException if parameter 'product' is null.
     */
    public static void addProductOptionalInfo(ProductSectionType product, String productName,
        String productVendorName, String productVersion, String productFullVersion,
        String productUrl, String productVendorUrl) throws RequiredAttributeException
    {
        if (product == null)
        {
            throw new RequiredAttributeException("Parameter product can not be null");
        }

        if (productName != null)
        {
            MsgType msgProductName = new MsgType();
            msgProductName.setValue(productName);
            product.setProduct(msgProductName);
        }

        if (productVendorName != null)
        {
            MsgType msgProductVendorName = new MsgType();
            msgProductVendorName.setValue(productVendorName);
            product.setVendor(msgProductVendorName);
        }

        if (productVersion != null)
        {
            MsgType msgProductVersion = new MsgType();
            msgProductVersion.setValue(productVersion);
            product.setVendor(msgProductVersion);
        }

        if (productFullVersion != null)
        {
           
            product.setFullVersion( CIMTypesUtils.createString(productFullVersion));
        }

        if (productUrl != null)
        {
            product.setProductUrl( CIMTypesUtils.createString(productUrl));
        }

        if (productVendorUrl != null)
        {
            product.setVendorUrl(CIMTypesUtils.createString(productVendorUrl));
        }

    }

    /**
     * Creates a new <Icon> tag. All parameters are required and fileRef should exist in envelop
     * References.
     * 
     * @param height height of the picture/icon
     * @param width width of the picture/icon
     * @param mimeType mimeType of the picture/icon
     * @param fileRef file reference fo the icon
     * @throws RequiredAttributeException
     */
    public static Icon createIcon(Integer height, Integer width, String mimeType, String fileRef)
        throws RequiredAttributeException
    {
        if (height == null || width == null || mimeType == null || fileRef == null)
        {
            throw new RequiredAttributeException("Any parameter of this method can be null");
        }

        Icon newIcon = new Icon();
        newIcon.setHeight(height);
        newIcon.setWidth(width);
        newIcon.setMimeType(mimeType);
        newIcon.setFileRef(fileRef);

        return newIcon;
    }

    /**
     * Adds an <Icon> inside the <ProductSectionType> attribute.
     * 
     * @param productSection <ProductSectionType> to insert in.
     * @param iconToInsert <Icon> object to insert to.
     * @throws RequiredAttributeException throws if any attribute is inserted.
     */
    public static void addProductIcon(ProductSectionType productSection, Icon iconToInsert)
        throws RequiredAttributeException
    {
        if (productSection == null || iconToInsert == null)
        {
            throw new RequiredAttributeException("Any parameter of this method can be null");
        }

        productSection.getIcon().add(iconToInsert);
    }

    /**
     * Add a <Category> and a list of <Property> attributes inside <ProductSection> tag.
     * 
     * @param productSec <ProductSection> passed by Reference.
     * @param categoryKey key of the <Category>
     * @param categoryValue value of the <Category>
     * @param propertyList List of <Propery> attributes.
     * @throws RequiredAttributeException all parameters of this methods are mandatory. Throws and exception if any parameter is null
     * @throws IdAlreadyExistsException <Category> key already exists or <Property> key already exists
     * NOTE: CATEGORY KEY IS OPTIONAL IN STANDARD OVF. WE THINK IT'S BETTER TO ALWAYS INFORM THE CATEGORY KEY. SO
     * IN THIS METHOD YOU CAN NOT INSERT NOT-NULL CATEGORY KEYS. 
     * ( I NEED TO SHUT UP SOME MOUTHES)
     */
    public static void addProductProperties(ProductSectionType productSec, String categoryKey,
        String categoryValue, List<Property> propertyList) throws RequiredAttributeException, IdAlreadyExistsException
    {
        if (productSec == null || categoryKey == null || categoryValue == null || propertyList == null)
        {
            throw new RequiredAttributeException("Any parameter of this method can be null");
        }
        
        //if there is a category or property with this key, we can not insert this property list
        for (Object obj : productSec.getCategoryOrProperty())
        {
            if (obj instanceof MsgType)
            {
                if (((MsgType) obj).getMsgid().equalsIgnoreCase(categoryKey))
                {
                    throw new IdAlreadyExistsException("You are trying to insert a Category with an existing Key");
                }
            }
            //check inside the property list
            if (obj instanceof Property)
            {
                for (Property newProperty : propertyList)
                {
                   if (newProperty.getKey().equalsIgnoreCase(((Property) obj).getKey()))
                   {
                       throw new IdAlreadyExistsException("You are trying to insert a Property with an existing Key");
                   }
                }
            }
        }
        
        //insert category
        MsgType categoryMsg = new MsgType();
        categoryMsg.setMsgid(categoryKey);
        categoryMsg.setValue(categoryValue);
        productSec.getCategoryOrProperty().add(categoryMsg);
        
        for (Property newProperty : propertyList)
        {
            productSec.getCategoryOrProperty().add(newProperty);
        }
        
        
       
    }

    /**
     * Creates a new <Property> tag.
     * 
     * @param propertyKey identifier of the property. It should be unique.
     * @param propertyType type of the property
     * @param propertyValue value of the property (can be null if userConfigurable is true)
     * @param userConfigurable tells if propertyValue is configured in installation time or not.
     * @param label (optional) tags the property
     * @param description (partially optional) describes the label. it's mandatory with the label
     *            parameter
     * @return a New <Property> tag built.
     * @throws RequiredAttributeException thrown if propertyKey of propertyValue are null and other
     *             OVF requeriments.
     * TODO userConfigurable is always false for the moment. We need to fix this code.
     */
    public static Property createProperty(String propertyKey, String propertyType,
        String propertyValue, Boolean userConfigurable, String label, String description)
        throws RequiredAttributeException
    {

        Property prop = new Property();

        if (propertyKey == null || propertyType == null)
        {
            throw new RequiredAttributeException("Parameters propertyKey and propertyType cannot be null");
        }

        prop.setKey(propertyKey);
        prop.setType(propertyType);
        
        // TODO: userConfigurable is always false for the moment. Fix this code later.
        prop.setUserConfigurable(false);
        if (true)
        {
            if (propertyValue == null)
            {
                throw new RequiredAttributeException("Parameters propertyValue cannot be null");
            }
            prop.setValue(propertyValue);
        }

        if (label != null)
        {
            if (description != null)
            {
                MsgType msgLabel = new MsgType();
                msgLabel.setValue(label);
                prop.setLabel(msgLabel);

                MsgType msgDescription = new MsgType();
                msgDescription.setValue(description);
                prop.setLabel(msgDescription);
            }
            else
            {
                throw new RequiredAttributeException("If label is informed, description is mandatory!");
            }
        }

        return prop;
    }

    /**
     * Search a Property from a Product Section from a given property key.
     * 
     * @param prodSection <ProductSectionType> object which stores all the <Property> tags
     * @param propertyKey Key of the <Property> we look for.
     * @return a <Property> object which stores the given key.
     * @throws IdNotFoundException thrown if property key it doesn't exist.
     */
    public static Property getProperty(ProductSectionType prodSection, String propertyKey)
        throws IdNotFoundException
    {
        for (Object newProp : prodSection.getCategoryOrProperty())
        {
            if (newProp instanceof Property)
            {
                if (((Property) newProp).getKey().equalsIgnoreCase(propertyKey))
                {
                    return (Property) newProp;
                }
            }
        }

        throw new IdNotFoundException("No Property with PropertyKey");
    }

    /**
     * Get all the <Property> object stored in the given <ProductSectionType>
     * 
     * @param propSection <ProductSectionType> object.
     * @return List of <Property> objects.
     */
    public static List<Property> getAllProperties(ProductSectionType propSection)
    {
        List<Property> listAllProperties = new ArrayList<Property>();

        for (Object newProp : propSection.getCategoryOrProperty())
        {
            if (newProp instanceof Property)
            {
                listAllProperties.add((Property) newProp);
            }
        }

        return listAllProperties;
    }

}
