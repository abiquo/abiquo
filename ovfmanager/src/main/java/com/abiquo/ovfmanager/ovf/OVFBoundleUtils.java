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

package com.abiquo.ovfmanager.ovf;

import org.dmtf.schemas.ovf.envelope._1.StringsType;

import com.abiquo.ovfmanager.cim.CIMTypesUtils;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

/**
 * unused
 * */
@Deprecated
public class OVFBoundleUtils
{
    
    
    public static StringsType createString(String lang, String fileRef) throws RequiredAttributeException
    {
        StringsType strings = new StringsType();
     
        if(lang == null)
        {
            throw new RequiredAttributeException("Lang for Strings");
        }
        
        // strings.setLang(lang);      // TODO from xml:lang namespace creates an enumeration ?¿?
        strings.setFileRef(fileRef); // TODO file exist ???
        
        return strings;
    }

    
    public static void addMessageToStrings(StringsType strings, String messageId, String value) throws RequiredAttributeException
    {
        // TODO id already exist 
        strings.getMsg().add(CIMTypesUtils.createMsgStrings(messageId, value));
    }
    
}
