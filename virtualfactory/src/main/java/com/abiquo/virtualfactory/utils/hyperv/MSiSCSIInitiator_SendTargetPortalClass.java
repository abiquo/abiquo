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
package com.abiquo.virtualfactory.utils.hyperv;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import com.hyper9.jwbem.SWbemMethod;
import com.hyper9.jwbem.SWbemObject;
import com.hyper9.jwbem.SWbemServices;

/**
 * @author pnavarro
 */
public class MSiSCSIInitiator_SendTargetPortalClass extends SWbemObject
{
    private SWbemMethod refresh;

    public MSiSCSIInitiator_SendTargetPortalClass(IJIDispatch objectDispatcher,
        SWbemServices service)
    {
        super(objectDispatcher, service);
    }

    public void refresh(String portalAddress, int portalPort) throws JIException
    {
        if (this.refresh == null)
        {
            for (final SWbemMethod m : super.getMethods())
            {
                if (m.getName().equals("Refresh"))
                {
                    this.refresh = m;
                }
            }
        }

        SWbemObject inParams = this.refresh.getInParameters();

        inParams.getObjectDispatcher().put("PortalAddress", new JIVariant(portalAddress));
        inParams.getObjectDispatcher().put("PortalPort", new JIVariant(portalPort));

        Object[] methodParams =
            new Object[] {new JIString("Refresh"), new JIVariant(inParams.getObjectDispatcher()),
            new Integer(0), JIVariant.NULL(),};

        // Execute the method.
        JIVariant[] results = super.objectDispatcher.callMethodA("ExecMethod_", methodParams);

    }

}
