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
package com.abiquo.virtualfactory.hypervisor.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.virtualfactory.constants.MessageValues;
import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.machine.impl.XenServerMachine;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.config.Configuration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.model.config.XenServerHypervisorConfiguration;
import com.abiquo.virtualfactory.utils.xenserver.SRType;
import com.xensource.xenapi.APIVersion;
import com.xensource.xenapi.Connection;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.PBD;
import com.xensource.xenapi.SR;
import com.xensource.xenapi.Session;
import com.xensource.xenapi.VM;

/**
 * XenServer connector.
 * 
 * @author ibarrera
 */
public class XenServerHypervisor implements IHypervisor
{
    /** The logger */
    private final static Logger LOGGER = LoggerFactory.getLogger(XenServerHypervisor.class);

    /** The hypervisor configuration. */
    private XenServerHypervisorConfiguration config;

    /** The XML-RPC API connection URL */
    private URL url;

    /** The XML-RPC API connection */
    private Connection conn;

    /** The session identifier */
    private String sessionID;

    /** The location of the Abiquo Storage Repository. */
    private String abiquoRepositoryAddress;

    /** The path of the Abiquo Storage Repository. */
    private String abiquoRepositoryPath;

    /** The ID of the Abiquo Storage Repository */
    private String abiquoRepositoryID;

    /** The admin user */
    private String user;

    /** The admin password */
    private String password;

    @Override
    public void connect(final URL address) throws HypervisorException
    {
        try
        {
            // Initialize connection
            conn = new Connection(url);

            // Login to hypervisor
            Session session =
                Session.loginWithPassword(conn, user, password, APIVersion.latest().toString());
            sessionID = session.getUuid(conn);

            LOGGER.debug("Connected to XenServer at: " + url);
        }
        catch (Exception ex)
        {
            throw new HypervisorException(MessageValues.CONN_EXCP_I, ex);
        }
    }

    @Override
    public AbsVirtualMachine createMachine(final VirtualMachineConfiguration config)
        throws VirtualMachineException
    {
        // Create the XenServer machine
        return new XenServerMachine(config);
    }

    @Override
    public URL getAddress()
    {
        return url;
    }

    @Override
    public String getHypervisorType()
    {
        return HypervisorType.XENSERVER.getValue();
    }

    @Override
    public void init(final URL address, final String user, final String password)
        throws HypervisorException
    {
        // Load hypervisor configuration
        AbiCloudModel model = AbiCloudModel.getInstance();
        Configuration config = model.getConfigManager().getConfiguration();
        this.config = config.getXenServerConfig();
        this.user = user;
        this.password = password;

        try
        {
            url = new URL(address.getProtocol() + "://" + address.getHost());
        }
        catch (MalformedURLException ex)
        {
            throw new HypervisorException("Could not initialize Hypervisor URL", ex);
        }

        // Set Abiquo Storage Repository
        String[] repoLocation = this.config.getAbiquoRepository().split(":");
        abiquoRepositoryAddress = repoLocation[0];
        abiquoRepositoryPath = repoLocation[1];

        // Ignore last / in server path
        if (abiquoRepositoryPath.endsWith("/"))
        {
            abiquoRepositoryPath =
                abiquoRepositoryPath.substring(0, abiquoRepositoryPath.lastIndexOf('/'));
        }

        LOGGER.info("XenServer Hypervisor initialized");
    }

    /**
     * Tries to reconnect to hypervisor.
     * 
     * @throws HypervisorException If reconnection fails.
     */
    public void reconnect() throws HypervisorException
    {
        if (conn == null)
        {
            connect(url);
        }
        else
        {
            try
            {
                // Find if there is an active session
                Session session = Session.getByUuid(conn, sessionID);

                // If no active session is active, reconnect
                if (session == null)
                {
                    connect(url);
                }
            }
            catch (Exception ex)
            {
                throw new HypervisorException(ex);
            }
        }
    }

    @Override
    public void login(final String user, final String password)
    {
        // Not used
    }

    @Override
    public void logout()
    {
        try
        {
            // Logout from hypervisor and end connection
            Session.logout(conn);
            conn.dispose();
            conn = null;

            LOGGER.debug("Disconnected from XenServer at: " + url);
        }
        catch (Exception ex)
        {
            LOGGER.error("Could not log out from XenServer Hypervisor", ex);
        }
    }

    /**
     * Get the current XenServer host.
     * 
     * @return The current XenServer host.
     * @throws HypervisorException If the current XenServer host cannot be retrieved.
     */
    public Host getHost() throws HypervisorException
    {
        String address = url.getHost();

        try
        {
            for (Host host : Host.getAll(conn))
            {
                if (host.getAddress(conn).equals(address))
                {
                    return host;
                }
            }
        }
        catch (Exception ex)
        {
            throw new HypervisorException("Could not get the current host", ex);
        }

        throw new HypervisorException("Could not get the current host");
    }

    /**
     * Initializes the Abiquo Storage Repository.
     * <p>
     * This method checks if the repository already exist, and creates it if necessary.
     * 
     * @throws HypervisorException If the repository cannot be initialized.
     */
    public void initAbiquoRepository() throws HypervisorException
    {
        // Always should check if repository if present, since it could be deleted after this
        // Hypervisor has been instantiated. If that happens, the repository could have a different
        // ID than the previous abiquoRepositoryID.

        reconnect();

        try
        {
            SR sr = null;

            // Find the Abiquo Storage Repository
            Map<PBD, PBD.Record> pbds = PBD.getAllRecords(conn);

            for (Iterator<PBD.Record> it = pbds.values().iterator(); it.hasNext() && sr == null;)
            {
                PBD.Record pbd = it.next();
                SRType type = SRType.fromValue(pbd.SR.getType(conn));

                if (type == SRType.NFS)
                {
                    // Check if NFS repository points to Abiquo Repository
                    Map<String, String> deviceConfig = pbd.deviceConfig;

                    if (deviceConfig.get("server").equals(abiquoRepositoryAddress)
                        && deviceConfig.get("serverpath").equals(abiquoRepositoryPath))
                    {
                        sr = pbd.SR;

                        LOGGER.debug("Found Abiquo Repository at: " + config.getAbiquoRepository());
                    }
                }
            }

            // If not found, create it
            if (sr == null)
            {
                LOGGER.debug("Abiquo Repository not found."
                    + "Creating a new NFS Storage Repository at: " + config.getAbiquoRepository());

                // Configure Abiquo repository location
                Map<String, String> deviceConfig = new HashMap<String, String>();
                deviceConfig.put("server", abiquoRepositoryAddress);
                deviceConfig.put("serverpath", abiquoRepositoryPath);

                // Create SR. PhysicalSize will be computed by XenServer
                sr =
                    SR.create(conn, getHost(), deviceConfig, 0L, "Abiquo",
                        "Abiquo Image Repository (" + config.getAbiquoRepository() + ")", "nfs",
                        "user", Boolean.TRUE, new HashMap<String, String>());
            }

            abiquoRepositoryID = sr.getUuid(conn);
        }
        catch (Exception ex)
        {
            throw new HypervisorException("Could not initialize Abiquo Storage Repository", ex);
        }
    }

    /**
     * Gets the conn.
     * 
     * @return the conn
     */
    public Connection getConn()
    {
        return conn;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public URL getUrl()
    {
        return url;
    }

    /**
     * Gets the abiquoRepositoryID.
     * 
     * @return the abiquoRepositoryID
     */
    public String getAbiquoRepositoryID()
    {
        return abiquoRepositoryID;
    }

    @Override
    public void disconnect() throws HypervisorException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public AbsVirtualMachine getMachine(final VirtualMachineConfiguration virtualMachineConfig)
        throws HypervisorException
    {
        try
        {
            init(this.url, this.user, this.password);
            connect(this.url);
            Set<VM> vms = VM.getByNameLabel(getConn(), virtualMachineConfig.getMachineName());
            if (vms != null && !vms.isEmpty())
            {
                virtualMachineConfig.setHypervisor(this);
                AbsVirtualMachine vm = createMachine(virtualMachineConfig);
                vm.setState(State.DEPLOYED);
                return vm;
            }
            else
            {
                LOGGER.debug(MessageValues.VM_NOT_FOUND + virtualMachineConfig.getMachineName());
                return null;
            }
        }
        catch (Exception ex)
        {
            throw new HypervisorException(MessageValues.VM_NOT_FOUND, ex);
        }
        finally
        {
            logout();
        }
    }
}
