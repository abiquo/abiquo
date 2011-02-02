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

package com.abiquo.vsm.monitor.esxi.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.util.OptionSpec;

public class AppUtil
{

    public Log log;

    private HashMap<String, String> optsEntered = new HashMap<String, String>();

    private HashMap userOpts = new HashMap();

    private HashMap builtInOpts = new HashMap();

    private String logfilepath = "";

    private String _cname;

    private ClientUtil _util;

    protected ServiceInstance serviceInstance;

    private ServiceUtil serviceUtil;

    public ServiceInstance getServiceInstance()
    {
        return serviceInstance;
    }

    public ServiceUtil getServiceUtil()
    {
        if (serviceUtil == null)
        {
            serviceUtil = ServiceUtil.CreateServiceUtil();
            serviceUtil.init(this);
        }
        return serviceUtil;
    }
    
    public ServiceUtil getServiceUtil3()
    {
        return getServiceUtil();
    }

    /**
     * If there is a global logger already available
     */
    private static Log gLog;

    public static AppUtil init(ServiceInstance serviceInstance, OptionSpec[] options,
        HashMap optsEntered) throws Exception
    {
        AppUtil cb = new AppUtil(serviceInstance);
        cb.addOptions(options);
        cb.setOptsEntered(optsEntered);
        return cb;
    }

    public static AppUtil initialize(ServiceInstance serviceInstance, OptionSpec[] userOptions,
        String[] args) throws Exception
    {
        AppUtil cb = new AppUtil(serviceInstance);
        if (userOptions != null)
        {
            cb.addOptions(userOptions);
            cb.parseInput(args);
            cb.validate();
        }
        else
        {
            cb.parseInput(args);
            cb.validate();
        }
        return cb;
    }

    public static AppUtil initialize(ServiceInstance serviceInstance, String[] args)
        throws Exception
    {
        AppUtil cb = initialize(serviceInstance, null, args);
        return cb;
    }

    public static void ALog(Log glog)
    {
        gLog = glog;
    }

    public AppUtil(ServiceInstance serviceInstance)
    {
        _util = new ClientUtil(this);
        init(serviceInstance);
    }

    public void init(ServiceInstance serviceInstance)
    {
        builtinOptions();
        if (gLog == null)
        {
            String logDir = System.getProperty("java.io.tmpdir", "/tmp/");
            if (logDir == null || logDir.length() == 0)
            {
                logDir = "";
            }
            logfilepath = logDir + "/" + serviceInstance.toString() + "_";
            log = new Log();
            log.init(logfilepath, false, false);
        }
        else
        {
            log = gLog;
        }
        _cname = serviceInstance.toString();
        this.serviceInstance = serviceInstance;
    }

    public void addOptions(OptionSpec[] userOptions) throws Exception
    {
        for (int i = 0; i < userOptions.length; i++)
        {
            if (userOptions[i].getOptionName() != null
                && userOptions[i].getOptionName().length() > 0
                && userOptions[i].getOptionDesc() != null
                && userOptions[i].getOptionDesc().length() > 0
                && userOptions[i].getOptionType() != null
                && userOptions[i].getOptionType().length() > 0
                && (userOptions[i].getOptionRequired() == 0 || userOptions[i].getOptionName()
                    .length() > 1))
            {
                userOpts.put(userOptions[i].getOptionName(), userOptions[i]);
            }
            else
            {
                System.out.println("Option " + userOptions[i].getOptionName()
                    + " definition is not valid");
                throw new ArgumentHandlingException("Option " + userOptions[i].getOptionName()
                    + " definition is not valid");
            }
        }
    }

    private void builtinOptions()
    {
        OptionSpec url = new OptionSpec("url", "String", 1, "VI SDK URL to connect to", null);
        OptionSpec userName =
            new OptionSpec("userName", "String", 1, "Username to connect to the host", null);
        OptionSpec password =
            new OptionSpec("password", "String", 1, "password of the corresponding user", null);
        OptionSpec config =
            new OptionSpec("config",
                "String",
                0,
                "Location of the VI perl configuration file",
                null);
        OptionSpec protocol =
            new OptionSpec("protocol", "String", 0, "Protocol used to connect to server", null);
        OptionSpec server = new OptionSpec("server", "String", 0, "VI server to connect to", null);
        OptionSpec portNumber =
            new OptionSpec("portNumber", "String", 0, "Port used to connect to server", "443");
        OptionSpec servicePath =
            new OptionSpec("servicePath",
                "String",
                0,
                "Service path used to connect to server",
                null);
        OptionSpec sessionFile =
            new OptionSpec("sessionFile",
                "String",
                0,
                "File containing session ID/cookie to utilize",
                null);
        OptionSpec help =
            new OptionSpec("help", "String", 0, "Display user information for the script", null);
        OptionSpec ignorecert =
            new OptionSpec("ignorecert",
                "String",
                0,
                "Ignore the server certificate validation",
                null);
        builtInOpts.put("url", url);
        builtInOpts.put("username", userName);
        builtInOpts.put("password", password);
        builtInOpts.put("password", password);
        builtInOpts.put("config", config);
        builtInOpts.put("protocol", protocol);
        builtInOpts.put("server", server);
        builtInOpts.put("portnumber", portNumber);
        builtInOpts.put("servicepath", servicePath);
        builtInOpts.put("sessionfile", sessionFile);
        builtInOpts.put("help", help);
        builtInOpts.put("ignorecert", ignorecert);
    }

    public void parseInput(String args[]) throws Exception
    {
        try
        {
            getCmdArguments(args);
        }
        catch (Exception e)
        {
            throw new ArgumentHandlingException("Exception running : " + e);
        }
        Iterator It = optsEntered.keySet().iterator();
        while (It.hasNext())
        {
            String keyValue = It.next().toString();
            String keyOptions = optsEntered.get(keyValue);
            boolean result = checkInputOptions(builtInOpts, keyValue);
            boolean valid = checkInputOptions(userOpts, keyValue);
            if (result == false && valid == false)
            {
                System.out.println("Invalid Input Option '" + keyValue + "'");
                displayUsage();
                throw new ArgumentHandlingException("Invalid Input Option '" + keyValue + "'");
            }
            result = checkDatatypes(builtInOpts, keyValue, keyOptions);
            valid = checkDatatypes(userOpts, keyValue, keyOptions);
            if (result == false && valid == false)
            {
                System.out.println("Invalid datatype for Input Option '" + keyValue + "'");
                displayUsage();
                throw new ArgumentHandlingException("Invalid Input Option '" + keyValue + "'");
            }
        }
    }

    private void getCmdArguments(String args[]) throws Exception
    {
        int len = args.length;
        int i = 0;
        boolean flag = false;
        if (len == 0)
        {
            displayUsage();
            throw new ArgumentHandlingException("usage");
        }
        while (i < args.length)
        {
            String val = "";
            String opt = args[i];
            if (opt.startsWith("--") && optsEntered.containsKey(opt.substring(2)))
            {
                System.out.println("key '" + opt.substring(2) + "' already exists ");
                displayUsage();
                throw new ArgumentHandlingException("key '" + opt.substring(2)
                    + "' already exists ");
            }
            if (args[i].startsWith("--"))
            {
                if (args.length > i + 1)
                {
                    if (!args[i + 1].startsWith("--"))
                    {
                        val = args[i + 1];
                        optsEntered.put(opt.substring(2), val);
                    }
                    else
                    {
                        optsEntered.put(opt.substring(2), null);
                    }
                }
                else
                {
                    optsEntered.put(opt.substring(2), null);
                }
            }
            i++;
        }
    }

    private boolean checkDatatypes(HashMap Opts, String keyValue, String keyOptions)
    {
        boolean valid = false;
        valid = Opts.containsKey(keyValue);
        if (valid)
        {
            OptionSpec oSpec = (OptionSpec) Opts.get(keyValue);
            String dataType = oSpec.getOptionType();
            boolean result = validateDataType(dataType, keyOptions);
            return result;
        }
        else
        {
            return false;
        }
    }

    private boolean validateDataType(String dataType, String keyValue)
    {
        try
        {
            if (dataType.equalsIgnoreCase("Boolean"))
            {
                if (keyValue.equalsIgnoreCase("true") || keyValue.equalsIgnoreCase("false"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else if (dataType.equalsIgnoreCase("Integer"))
            {
                int val = Integer.parseInt(keyValue);
                return true;
            }
            else if (dataType.equalsIgnoreCase("Float"))
            {
                Float.parseFloat(keyValue);
                return true;
            }
            else if (dataType.equalsIgnoreCase("Long"))
            {
                Long.parseLong(keyValue);
                return true;
            }
            else
            {
                // DO NOTHING
            }
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    private boolean checkInputOptions(HashMap checkOptions, String value)
    {
        boolean valid = false;
        valid = checkOptions.containsKey(value);
        return valid;
    }

    public void validate() throws Exception
    {
        validate(null, null);
    }

    public void validate(Object className, String functionName) throws Exception
    {
        boolean flag = false;
        if (optsEntered.isEmpty())
        {
            displayUsage();
            throw new ArgumentHandlingException("---help");
        }
        if (optsEntered.get("help") != null)
        {
            displayUsage();
            System.exit(1);
        }
        if (option_is_set("help"))
        {
            displayUsage();
            System.exit(1);
        }
        Vector<String> vec = getValue(builtInOpts);
        for (int i = 0; i < vec.size(); i++)
        {
            if (optsEntered.get(vec.get(i)) == null)
            {
                String missingArg = vec.get(i);
                if (missingArg.equalsIgnoreCase("password"))
                {
                    String password = readPassword("Enter password: ");
                    optsEntered.put("password", password);
                }
                else
                {
                    System.out.print("----ERROR: " + vec.get(i) + " not specified \n");
                    displayUsage();
                    throw new ArgumentHandlingException("----ERROR: " + vec.get(i)
                        + " not specified \n");
                }
            }
        }
        vec = getValue(userOpts);
        for (int i = 0; i < vec.size(); i++)
        {
            if (optsEntered.get(vec.get(i)) == null)
            {
                System.out.print("----ERROR: " + vec.get(i) + " not specified \n");
                displayUsage();
                throw new ArgumentHandlingException("----ERROR: " + vec.get(i)
                    + " not specified \n");
            }
        }
        if ((optsEntered.get("sessionfile") == null)
            && ((optsEntered.get("username") == null) && (optsEntered.get("password") == null)))
        {
            System.out
                .println("Must have one of command options 'sessionfile' or a 'username' and 'password' pair\n");
            displayUsage();
            throw new ArgumentHandlingException("Must have one of command options 'sessionfile' or a 'username' and 'password' pair\n");
        }
    }

    /*
     * taking out value of a particular key in the hashmapi.e checking for required =1 options
     */
    private Vector getValue(HashMap checkOptions)
    {
        Iterator It = checkOptions.keySet().iterator();
        Vector<String> vec = new Vector<String>();
        while (It.hasNext())
        {
            String str = It.next().toString();
            OptionSpec oSpec = (OptionSpec) checkOptions.get(str);
            if (oSpec.getOptionRequired() == 1)
            {
                vec.add(str);
            }
        }
        return vec;
    }

    public void displayUsage()
    {
        System.out.println("Common Java Options :");
        print_options(builtInOpts);
        System.out.println("\nCommand specific options: ");
        print_options(userOpts);
    }

    private void print_options(HashMap Opts)
    {
        String type = "";
        String defaultVal = "";
        Iterator It;
        String help = "";
        Set generalKeys = Opts.keySet();
        It = generalKeys.iterator();
        while (It.hasNext())
        {
            String keyValue = It.next().toString();
            OptionSpec oSpec = (OptionSpec) Opts.get(keyValue);
            if ((oSpec.getOptionType() != null) && (oSpec.getOptionDefault() != null))
            {
                type = oSpec.getOptionType();
                defaultVal = oSpec.getOptionDefault();
                System.out.println("   --" + keyValue + " < type " + type + ", default "
                    + defaultVal + ">");
            }
            if ((oSpec.getOptionDefault() != null) && (oSpec.getOptionType() == null))
            {
                defaultVal = oSpec.getOptionDefault();
                System.out.println("   --" + keyValue + " < default " + defaultVal + " >");
            }
            else if ((oSpec.getOptionType() != null) && (oSpec.getOptionDefault() == null))
            {
                type = oSpec.getOptionType();
                System.out.println("   --" + keyValue + " < type " + type + " >");
            }
            else if ((oSpec.getOptionType() == null) && (oSpec.getOptionDefault() == null))
            {
                System.out.println("   --" + keyValue + " ");
            }
            help = oSpec.getOptionDesc();
            System.out.println("      " + help);
        }
    }

    public boolean option_is_set(String option)
    {
        boolean valid = false;
        Iterator It = optsEntered.keySet().iterator();
        while (It.hasNext())
        {
            String keyVal = It.next().toString();
            if (option.equals(keyVal))
            {
                valid = true;
            }
        }
        return valid;
    }

    public String get_option(String key)
    {
        if (optsEntered.get(key) != null)
        {
            return optsEntered.get(key).toString();
        }
        else if (checkInputOptions(builtInOpts, key))
        {
            if (((OptionSpec) builtInOpts.get(key)).getOptionDefault() != null)
            {
                String str = ((OptionSpec) builtInOpts.get(key)).getOptionDefault();
                return str;
            }
            else
            {
                return null;
            }
        }
        else if (checkInputOptions(userOpts, key))
        {
            if (((OptionSpec) userOpts.get(key)).getOptionDefault() != null)
            {
                String str = ((OptionSpec) userOpts.get(key)).getOptionDefault();
                return str;
            }
            else
            {
                return null;
            }
        }
        else
        {
            System.out.println("undefined variable");
        }
        return null;
    }

    /**
     * @return name of the client application
     */
    public String getAppName()
    {
        return _cname;
    }

    /**
     * @return current log
     */
    public Log getLog()
    {
        return log;
    }

    /**
     * @return Client Util object
     */
    public ClientUtil getUtil()
    {
        return _util;
    }

    /**
     * @return web service url
     */
    public String getServiceUrl() throws Exception
    {
        // return _args[ARG_URL];
        return get_option("url");
    }

    /**
     * @return web service username
     */
    public String getUsername() throws Exception
    {
        // return _args[ARG_USER];
        return get_option("username");
    }

    /**
     * @return web service password
     */
    public String getPassword() throws Exception
    {
        /*
         * if (_args.length > ARG_PASSWD) { return _args[ARG_PASSWD]; } else { return ""; }
         */
        return get_option("password");
    }

    private String readPassword(String prompt)
    {
        try
        {
            PasswordMask consoleEraser = new PasswordMask();
            System.out.print(prompt);
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            consoleEraser.start();
            String pass = stdin.readLine();
            consoleEraser.halt();
            System.out.print("\b");
            return pass;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    class PasswordMask extends Thread
    {
        private boolean running = true;

        public void run()
        {
            while (running)
            {
                System.out.print("\b ");
            }
        }

        public synchronized void halt()
        {
            running = false;
        }
    }

    public HashMap getBuiltInOpts()
    {
        return builtInOpts;
    }

    public void setBuiltInOpts(HashMap builtInOpts)
    {
        this.builtInOpts = builtInOpts;
    }

    public HashMap<String, String> getOptsEntered()
    {
        return optsEntered;
    }

    public void setOptsEntered(HashMap<String, String> optsEntered)
    {
        this.optsEntered = optsEntered;
    }
}
