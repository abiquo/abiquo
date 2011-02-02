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
package com.abiquo.vsm.monitor.executor;

/**
 * Base class for asynchronous tasks that need to handle execution results.
 * 
 * @author ibarrera
 */
public abstract class AbstractTask implements Runnable
{
    /** Callback handler. */
    private ExecutionCallback callback;

    /**
     * Creates a new {@lkink AbstractTask} whithout a callback handler.
     */
    public AbstractTask()
    {
        super();
    }

    /**
     * Creates a new {@lkink AbstractTask} with the specified callback handler.
     * 
     * @param callback The callback handler.
     */
    public AbstractTask(ExecutionCallback callback)
    {
        super();
        this.callback = callback;
    }

    /**
     * Executes the task and calls the {@link ExecutionCallback}, if defined.
     */
    @Override
    public void run()
    {
        try
        {
            execute();

            if (callback != null)
            {
                callback.executionComplete();
            }
        }
        catch (Throwable t)
        {
            if (callback != null)
            {
                callback.executionFailure(t);
            }
        }
    }

    /**
     * Executes the task and calls the {@link ExecutionCallback}, if defined.
     * 
     * @throws Exception If task execution fails.
     */
    public abstract void execute() throws Exception;

    /**
     * Gets the callback
     * 
     * @return the callback
     */
    public ExecutionCallback getCallback()
    {
        return callback;
    }

    /**
     * Sets the callback
     * 
     * @param callback the callback to set
     */
    public void setCallback(ExecutionCallback callback)
    {
        this.callback = callback;
    }

}
