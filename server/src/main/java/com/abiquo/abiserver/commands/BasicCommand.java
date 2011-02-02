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

package com.abiquo.abiserver.commands;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.Rack.rack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import com.abiquo.abiserver.abicloudws.AbiCloudConstants;
import com.abiquo.abiserver.business.AuthService;
import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.session.InvalidSessionException;
import com.abiquo.abiserver.exception.session.TimeoutSessionException;
import com.abiquo.abiserver.exception.session.TooManyUsersException;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Datacenter;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Machine;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.VirtualDatacenter;
import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.util.ErrorManager;
import com.abiquo.util.resources.ResourceManager;

/**
 * This class is the base class for all command classes in AbiServer. A command class in AbiServer
 * always extends BasicCommand, and contains methods where server's tasks are performed. There
 * methods implement business logic, Data Base access, and so on. All commands MUST declare all its
 * methods "protected", to ensure that all of them are accessed through the public method "execute".
 * Using the method "execute", we can check that commands are only used by users authorized to do
 * so. If a command declares a public method, that method could be used by an unauthenticated user,
 * and means a public access point to AbiServer
 * 
 * @author Oliver
 */

public abstract class BasicCommand
{

    protected final ErrorManager errorManager =
        ErrorManager.getInstance(AbiCloudConstants.ERROR_PREFIX);

    // final static private ResourceManager resourceManager = new
    // ResourceManager(BasicCommand.class);

    /**
     * ResourceManager for handling the RResourceBundles or/and properties files for this abstract
     * class
     */
    private static final ResourceManager basicCommandResoureManager =
        new ResourceManager(BasicCommand.class);

    /** The ResourceManager for handling the ResourceBundles or/and properties files for this object */
    protected ResourceManager resourceManager;

    protected final AbiConfig abiConfig = AbiConfigManager.getInstance().getAbiConfig();

    /**
     * Constructor -
     */
    public BasicCommand()
    {
        // Initialize resourceManager
        resourceManager = new ResourceManager(this.getClass());
    }

    protected void setResourceManager(final Class< ? > cl)
    {
        resourceManager = new ResourceManager(cl);
    }

    protected void setResourceManager(final ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
    }

    /**
     * Standard access way to a Command class, without asking for user's session. Proxy will
     * determine if the asked method can be used without a session.
     * 
     * @param resource A String array containing in the first position the resource name, for check
     *            authorization and in the second position the command's method name, to call it
     * @param args array with the arguments for the method
     * @return
     * @deprecated should provide a returnType
     */
    @Deprecated
    public BasicResult execute(final String[] resource, final Object[] args)
    {
        // TODO this has been here to long!!! What does it actually do???
        boolean isAuthorized = true; // proxy.doAuthorization(resource[0]); <-- AUTHORIZATION

        // TEMPORALY DISABLED!
        // Set the Locale of the ResourceManagers for BasicCommand and that of the current object of
        // this class
        Login login = (Login) args[0];
        setLocale(login.getLocale());

        if (isAuthorized)
        {
            try
            {
                return onExecute(resource[1], args, BasicResult.class);
            }
            catch (Exception e)
            {
                BasicResult result = new BasicResult();
                result.setMessage(e.getMessage());
                return result;
            }
        }
        else
        {
            return onFaultAuthorization(resource[1]);
        }
    }

    /**
     * Sets the Locale of the ResourceManagers for BasicCommand and that of the current object of
     * this class
     * 
     * @param localeString String representation of the <code>Locale</code> to set to
     */
    private void setLocale(final String localeString)
    {
        resourceManager.setLocale(localeString);
        basicCommandResoureManager.setLocale(localeString);
    }

    /**
     * Returns the locale of the current session
     * 
     * @return <code>Locale</code> object representing the current language and country in use for
     *         the current session.
     */
    protected final Locale getLocale()
    {
        return resourceManager.getLocale();
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T execute(final UserSession session, final String[] resource,
        final Object[] args, final Class<T> returnType) throws Exception
    {
        // Set the Locale of the Class variable resourceManager - this is the only place where the
        // Locale needs to be set since all commands are called via the execute method
        setLocale(session.getLocale());

        // First, we check that the session is still valid
        // checkSession should be Generic
        BasicResult checkSessionResult = AuthService.getInstance().checkSession(session);

        // If the session is valid, we check if this session is authorized to use the asked method
        boolean isAuthorized = false;

        if (checkSessionResult.getSuccess())
        {
            // The session is valid. Checking authorization
            isAuthorized = true; // TODO Proxy.getInstance().doAuthorization(session, resource[0]);
            // AUTHORIZATION ONLY ENABLED TO CHECK SESSION

            if (isAuthorized)
            {
                // Everything is OK. We call the method
                return (T) onExecute(resource[1], args, returnType);
            }
            else
            {
                // This session is not authorized to call the solicited method
                return onFaultAuthorization(session, resource[1], returnType);
            }
        }
        else
        {
            if (checkSessionResult.getResultCode() == BasicResult.SESSION_INVALID)
            {
                throw new InvalidSessionException("Invalid Session. Please Log In again");
            }
            else if (checkSessionResult.getResultCode() == BasicResult.SESSION_TIMEOUT)
            {
                throw new TimeoutSessionException("Session timeout. Please Log In again");
            }
            else if (checkSessionResult.getResultCode() == BasicResult.SESSION_MAX_NUM_REACHED)
            {
                throw new TooManyUsersException("Too many users logged in the same time. Please wait");
            }
            else if (checkSessionResult.getResultCode() == BasicResult.NOT_AUTHORIZED
                || checkSessionResult.getMessage().equals("Forbidden"))
            {
                throw new InvalidSessionException("You do not have enough permissions to perform this task.");
            }
            else
            {
                throw new Exception("Unhandled session exception");
            }
        }
    }

    /**
     * Standard access way to a Command class. After checking against Proxy that session is valid,
     * it will call the asked method, and will return its result.
     * 
     * @param session
     * @param resource A String array containing in the first position the resource name, for check
     *            authorization and in the second position the command's method name, to call it
     * @param args
     * @return
     */
    public BasicResult execute(final UserSession session, final String[] resource,
        final Object[] args)
    {
        try
        {
            return execute(session, resource, args, BasicResult.class);
        }
        catch (InvalidSessionException e)
        {
            BasicResult result = new BasicResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setResultCode(BasicResult.SESSION_INVALID);
            return result;
        }
        catch (TimeoutSessionException e)
        {
            BasicResult result = new BasicResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setResultCode(BasicResult.SESSION_TIMEOUT);
            return result;
        }
        catch (TooManyUsersException e)
        {
            BasicResult result = new BasicResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setResultCode(BasicResult.SESSION_MAX_NUM_REACHED);
            return result;
        }
        catch (Exception e)
        {
            BasicResult result = new BasicResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    /**
     * Standard fault response, when someone calls a Command without a session
     * 
     * @param methodName
     * @return
     */
    private BasicResult onFaultAuthorization(final String methodName)
    {
        BasicResult faultResult = new BasicResult();

        faultResult.setSuccess(false);

        errorManager.reportError(basicCommandResoureManager, faultResult,
            "onFaultAuthorization.needsAuthorization", methodName);

        return faultResult;
    }

    /**
     * Fault response when authorization fails when calling a method
     * 
     * @param session
     * @param methodName
     * @return
     */
    private <T extends Object> T onFaultAuthorization(final UserSession session,
        final String methodName, final Class<T> cl)
    {
        BasicResult faultResult = new BasicResult();

        faultResult.setSuccess(false);

        errorManager.reportError(basicCommandResoureManager, faultResult,
            "onFaultAuthorization.noPermission", methodName);

        if (cl == BasicResult.class)
        {

            return cl.cast(faultResult);
        }
        else
        {
            return cl.cast(new Object());
        }

    }

    /**
     * When this Command has checked that user's session is valid, the method asked by the user,
     * with the corresponding arguments, will be called here
     * 
     * @param methodName
     * @param args array with the parameters for the method asked by user. null if the method has no
     *            parameters
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T extends Object> T onExecute(final String methodName, final Object[] args,
        Class< ? extends Object> returnType) throws Exception
    {
        BasicResult result = new BasicResult();

        try
        {
            Class[] argsType;
            if (args != null)
            {
                argsType = new Class[args.length];

                for (int i = 0; i < args.length; i++)
                {
                    argsType[i] = args[i].getClass();
                }
            }
            else
            {
                argsType = new Class[0];
            }

            Method method = null;
            try
            {
                method = this.getClass().getDeclaredMethod(methodName, argsType);
            }
            catch (NoSuchMethodException e)
            {
                // Try to find method in superclasses

                Class currentClass = this.getClass();
                Class superClass = currentClass.getSuperclass();

                while (superClass != null && method == null)
                {
                    try
                    {
                        method = superClass.getDeclaredMethod(methodName, argsType);
                    }
                    catch (NoSuchMethodException ex)
                    {
                        // Continue trying with the superClass
                        currentClass = superClass;
                        superClass = currentClass.getSuperclass();
                    }
                }

                // If the method is not found in superclasses, rethrow the exception
                if (method == null)
                {
                    throw e;
                }
            }

            Object obj = method.invoke(this, args);

            return (T) returnType.cast(obj);
        }
        catch (IllegalAccessException e)
        {
            errorManager.reportError(basicCommandResoureManager, result,
                "onExecute.IllegalAccessException", e, methodName);
        }
        catch (IllegalArgumentException e)
        {
            errorManager.reportError(basicCommandResoureManager, result,
                "onExecute.IllegalArgumentException", e, methodName);
        }
        catch (InvocationTargetException e)
        {
            throw (Exception) e.getCause();
            // errorManager.reportError(basicCommandResoureManager, result,
            // "onExecute.InvocationTargetException", e, methodName);
        }
        catch (NoSuchMethodException e)
        {
            errorManager.reportError(basicCommandResoureManager, result,
                "onExecute.NoSuchMethodException", e, methodName);
        }
        // catch (Exception e)
        // {
        // errorManager.reportError(basicCommandResoureManager, result, "onExecute.Exception", e,
        // methodName);
        // }

        returnType = BasicResult.class;
        return (T) returnType.cast(result);
    }

    /**
     * Execute the {@link com.abiquo.tracer.TracerFactory} for the event.
     * 
     * @param severity Severity of the event
     * @param component Component who traces the event
     * @param event Event type
     * @param performedUser user who executes the event
     * @param dataCenter Data Center to inform
     * @param virtualDatacenterName Virtual Data Center to inform
     * @param description The event description
     * @param vapp Virtual Appliance to inform
     * @param rack Rack to inform
     * @param machine Physical Machine to inform
     * @param user User to inform
     * @param ent TODO
     */
    public static void traceLog(final SeverityType severity, final ComponentType component,
        final EventType event, final UserSession performedUser, final DataCenter dataCenter,
        final String virtualDatacenterName, final String description, final VirtualAppliance vapp,
        final Rack rack, final PhysicalMachine machine, final String user, final String ent)
    {
        UserInfo ui = new UserInfo();
        ui.setId(performedUser.getUserIdDb());
        ui.setUsername(performedUser.getUser());
        ui.setEnterprise(performedUser.getEnterpriseName());

        Platform platform = platform("abiquo");
        com.abiquo.tracer.Enterprise enterprise = null;
        if (ent != null)
        {
            enterprise = com.abiquo.tracer.Enterprise.enterprise(ent);
        }
        else
        {
            enterprise = enterprise(ui.getEnterprise());
        }

        if (user != null)
        {
            enterprise.setUser(com.abiquo.tracer.User.user(user));
        }

        // Set a datacenter object, with name
        if (dataCenter != null)
        {
            Datacenter datacenter = Datacenter.datacenter(dataCenter.getName());
            if (rack != null)
            {
                com.abiquo.tracer.Rack r = rack(rack.getName());
                if (machine != null)
                {
                    r.setMachine(Machine.machine(machine.getName()));
                }
                datacenter.setRack(r);
            }
            platform.setDatacenter(datacenter);
        }

        // Set a virtualdatacenter object, with name
        if (virtualDatacenterName != null)
        {
            VirtualDatacenter vdc = VirtualDatacenter.virtualDatacenter(virtualDatacenterName);
            if (vapp != null)
            {
                vdc.setVirtualAppliance(com.abiquo.tracer.VirtualAppliance.virtualAppliance(vapp
                    .getName()));
            }
            enterprise.setVirtualDatacenter(vdc);
        }

        platform.setEnterprise(enterprise);

        if (description != null)
        {
            TracerFactory.getTracer().log(severity, component, event, description, ui, platform);
        }
        else
        {
            TracerFactory.getTracer().log(severity, component, event, ui, platform);
        }

    }

    /**
     * Execute the {@link com.abiquo.tracer.TracerFactory} for the event.
     * 
     * @param severity Severity of the event
     * @param component Component who traces the event
     * @param event Event type
     * @param performedUser user who executes the event
     * @param dataCenter Data Center to inform
     * @param virtualDatacenterName Virtual Data Center to inform
     * @param description The event description
     * @param vapp Virtual Appliance to inform
     * @param rack Rack to inform
     * @param machine Physical Machine to inform
     * @param user User to inform
     * @param ent TODO
     */
    public static void traceSystemLog(final SeverityType severity, final ComponentType component,
        final EventType event, final DataCenter dataCenter, final String virtualDatacenterName,
        final String description, final VirtualAppliance vapp, final Rack rack,
        final PhysicalMachine machine)
    {
        Platform platform = Platform.SYSTEM_PLATFORM;

        // Set a datacenter object, with name
        if (dataCenter != null)
        {
            Datacenter datacenter = Datacenter.datacenter(dataCenter.getName());
            if (rack != null)
            {
                com.abiquo.tracer.Rack r = rack(rack.getName());
                if (machine != null)
                {
                    r.setMachine(Machine.machine(machine.getName()));
                }
                datacenter.setRack(r);
            }
            platform.setDatacenter(datacenter);
        }

        // Set a virtualdatacenter object, with name
        if (virtualDatacenterName != null)
        {
            VirtualDatacenter vdc = VirtualDatacenter.virtualDatacenter(virtualDatacenterName);
            if (vapp != null)
            {
                vdc.setVirtualAppliance(com.abiquo.tracer.VirtualAppliance.virtualAppliance(vapp
                    .getName()));
            }
            platform.getEnterprise().setVirtualDatacenter(vdc);
        }

        if (description != null)
        {
            TracerFactory.getTracer().log(severity, component, event, description, platform);
        }
        else
        {
            TracerFactory.getTracer().log(severity, component, event, platform);
        }

    }
}
