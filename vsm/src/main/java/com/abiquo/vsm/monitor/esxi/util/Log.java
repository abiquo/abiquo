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

import java.io.FileWriter;
import java.util.Date;

/**
 * Logger to file if possible or console.
 */
public class Log
{

    private FileWriter _logger;

    private boolean _toConsole;

    private String _lineSep;

    public Log()
    {
    }

    public void init(String logfilepath, boolean appendlog, boolean toConsole)
    {
        try
        {
            String finlog = logfilepath;
            if (logfilepath.lastIndexOf(".") < 0)
            {
                finlog = logfilepath + ".txt";
            }
            _lineSep = System.getProperty("line.separator");

            // _logger = new FileWriter(finlog, appendlog);
            _logger = null;

            if (_lineSep == null || _lineSep.length() == 0)
            {
                _lineSep = "\n";
            }

            _toConsole = toConsole;
            Date dt = new Date();
            // internalLogLine(dt, "Begin Log.");
        }
        catch (Exception e)
        {
            System.out.println("Exception initializing log to : " + logfilepath
                + ". Using console. ");

            _logger = null;
            _toConsole = true;
        }
    }

    public synchronized void close()
    {
        if (_logger != null)
        {
            Date dt = new Date();
            // internalLogLine(dt, "End Log.");
            // internalLogLine(null, "");
            try
            {
                _logger.flush();
                _logger.close();
                _logger = null;
            }
            catch (Exception e)
            {
                System.out.println("Exception closing Log");
            }
        }
    }

    public synchronized void logLine(String strmsg)
    {
        if (_logger != null)
        {
            try
            {
                _logger.write(strmsg);
                _logger.write(_lineSep);
            }
            catch (Exception e)
            {
                System.out.println("Exception writing log message");
            }
        }
        if (_toConsole || _logger == null)
        {
            System.out.println(strmsg);
        }
    }

    public void internalLogLine(Date dt, String msg)
    {
        synchronized (this)
        {
            String strmsg = msg;
            if (dt != null)
            {
                strmsg = "[ " + dt.toString() + " ] " + msg;
            }
            if (_logger != null)
            {
                try
                {
                    _logger.write(strmsg);
                    _logger.write(_lineSep);
                }
                catch (Exception e)
                {
                    System.out.println("Exception writing log message");
                }
            }
            if (_toConsole || _logger == null)
            {
                System.out.println(strmsg);
            }
        }
    }
}
