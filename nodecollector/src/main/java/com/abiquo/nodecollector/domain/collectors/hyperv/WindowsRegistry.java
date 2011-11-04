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

package com.abiquo.nodecollector.domain.collectors.hyperv;

import java.net.UnknownHostException;

import org.jinterop.dcom.common.IJIAuthInfo;
import org.jinterop.dcom.common.JIDefaultAuthInfoImpl;
import org.jinterop.dcom.common.JIErrorCodes;
import org.jinterop.dcom.common.JIException;
import org.jinterop.winreg.IJIWinReg;
import org.jinterop.winreg.JIPolicyHandle;
import org.jinterop.winreg.JIWinRegFactory;

import com.abiquo.nodecollector.exception.CollectorException;

/**
 * Provides Windows Registry management functionallity.
 * 
 * @author ibarrera
 */
public class WindowsRegistry
{
    /** Auth infor to access the Windows Registry. */
    private IJIAuthInfo authInfo;

    /** The windows registry. */
    private IJIWinReg registry;

    /**
     * Connects to the Windows Registry in the given server.
     * 
     * @param address The server address.
     * @param username The user.
     * @param password The password.
     * @throws UnknownHostException If connection could not be established.
     */
    public void connect(final String address, final String username, final String password)
        throws CollectorException, UnknownHostException
    {
        authInfo = new JIDefaultAuthInfoImpl("", username, password);
        registry = JIWinRegFactory.getSingleTon().getWinreg(authInfo, address, true);
    }

    /**
     * Disconnects from the server.
     * 
     * @throws JIException If an error occurs.
     */
    public void disconnect() throws JIException
    {
        registry.closeConnection();
    }

    /**
     * Get the value for the given key in the registry.
     * 
     * @param root The key root.
     * @param path The key path.
     * @param key The name of the key.
     * @return The value.
     * @throws JIException If an error occurs.
     */
    public String getKeyValue(final Keys root, final String path, final String key)
        throws JIException
    {
        if (registry == null)
        {
            throw new IllegalStateException("Windows Registry is not connected to a server.");
        }

        JIPolicyHandle rootHandle = getPolicyHandle(root);
        JIPolicyHandle keyHandle = null;

        try
        {
            keyHandle = registry.winreg_OpenKey(rootHandle, path, IJIWinReg.KEY_QUERY_VALUE);

            // In the returned array, first param contains the class type as an Integer, second
            // param contains the value as a 1 dimensional byte array, if any. In case of
            // REG_MULTI_SZ a 2 dimensional byte array is returned as the second param.
            Object[] value = registry.winreg_QueryValue(keyHandle, key, 1024);
            return new String((byte[]) value[1]);
        }
        catch (JIException ex)
        {
            // If the key is not found, return null. Otherwise propagate the exception
            if (ex.getErrorCode() == JIErrorCodes.ERROR_FILE_NOT_FOUND)
            {
                return null;
            }

            throw ex;
        }
        finally
        {
            if (keyHandle != null)
            {
                registry.winreg_CloseKey(keyHandle);
            }
            if (rootHandle != null)
            {
                registry.winreg_CloseKey(rootHandle);
            }
        }
    }

    /**
     * Get the appropriate {@link JIPolicyHandle} for the given key.
     * 
     * @param key The root key.
     * @return The Policy Handle.
     * @throws JIException If an error occurs.
     */
    private JIPolicyHandle getPolicyHandle(final Keys key) throws JIException
    {
        switch (key)
        {
            case HKEY_CLASSES_ROOT:
                return registry.winreg_OpenHKCR();
            case HKEY_CURRENT_USER:
                return registry.winreg_OpenHKCU();
            case HKEY_LOCAL_MACHINE:
                return registry.winreg_OpenHKLM();
            case HKEY_USERS:
                return registry.winreg_OpenHKU();
            default:
                throw new UnsupportedOperationException("Cannot handle this root key.");
        }
    }

    /**
     * The Windows Registry Root keys.
     * <p>
     * The enum values correspond to the values of the Root key in the WMI registry classes.
     * 
     * @author ibarrera
     */
    public static enum Keys
    {
        HKEY_CLASSES_ROOT(0x80000000), HKEY_CURRENT_USER(0x80000001), HKEY_LOCAL_MACHINE(0x80000002), HKEY_USERS(
            0x80000003),

        // These two keys are not supported by the IJIWinReg class.
        HKEY_CURRENT_CONFIG(0x80000005), HKEY_DYN_DATA(0x80000006);

        private int code;

        private Keys(final int code)
        {
            this.code = code;
        }

        public int code()
        {
            return code;
        }
    }
}
