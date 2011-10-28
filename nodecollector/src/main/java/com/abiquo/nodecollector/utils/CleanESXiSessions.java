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
package com.abiquo.nodecollector.utils;

import java.net.URL;

import com.vmware.vim25.ArrayOfUserSession;
import com.vmware.vim25.InvalidLogin;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * @author jdevesa
 *
 */
public class CleanESXiSessions
{
    private static ServiceInstance serviceInstance;
    
    
    public static void main(String args[])
    {
        String ipAddress = "10.60.1.80";
        String user = "root";
        String password = "temporal";
        
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory",
            "org.apache.axis.components.net.SunFakeTrustSocketFactory");
        
        try
        {
            serviceInstance =
                new ServiceInstance(new URL("https://" + ipAddress + "/sdk"),
                    user,
                    password,
                    true);
            
            ManagedObjectReference sessionManager = serviceInstance.getServiceContent().getSessionManager();
            // Get the session list
            ObjectContent properties = getObjectProperties(null, sessionManager, new String[] {"sessionList", "currentSession"})[0];
            
            UserSession currentSession;
            ArrayOfUserSession ses;
            if (properties.getPropSet()[0].getName().equals("currentSession"))
            {
                currentSession = (UserSession) properties.getPropSet()[0].getVal();
                ses = (ArrayOfUserSession) properties.getPropSet()[1].getVal();
            }
            else
            {
                currentSession = (UserSession) properties.getPropSet()[1].getVal();
                ses = (ArrayOfUserSession) properties.getPropSet()[0].getVal();
            }
            
            System.out.println("Current session key: " + currentSession.getKey() );
            System.out.println(ses.getUserSession().length);
            for (UserSession userSession : ses.getUserSession())
            {
                if (!currentSession.getKey().equalsIgnoreCase(userSession.getKey()))
                {
                    try
                    {
                        serviceInstance.getServerConnection().getVimService().terminateSession(sessionManager, new String[] { userSession.getKey() });
                        System.out.println("Session " + currentSession.getKey() + " has been terminated");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
            if (serviceInstance != null && serviceInstance.getServerConnection() != null)
            {
                serviceInstance.getServerConnection().logout();
            }
        }
        catch (InvalidLogin e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static ObjectContent[] getObjectProperties(final ManagedObjectReference collector,
        final ManagedObjectReference mobj, final String[] properties) throws Exception
    {
        if (mobj == null)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = serviceInstance.getServiceContent().getPropertyCollector();
        }

        PropertyFilterSpec filter = new PropertyFilterSpec();

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setAll(Boolean.valueOf(properties == null || properties.length == 0));
        propertySpec.setType(mobj.getType());
        propertySpec.setPathSet(properties);

        filter.setPropSet(new PropertySpec[] {propertySpec});

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(mobj);
        objectSpec.setSkip(Boolean.FALSE);
        filter.setObjectSet(new ObjectSpec[] {objectSpec});

        return serviceInstance.getServerConnection().getVimService().retrieveProperties(usecoll, new PropertyFilterSpec[] {filter});
    }
    
}
