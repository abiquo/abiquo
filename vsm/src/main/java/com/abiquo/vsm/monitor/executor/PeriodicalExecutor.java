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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executes a task periodically.
 * 
 * @author ibarrera
 */
public class PeriodicalExecutor implements ExecutionCallback
{
    /** Target executor */
    private ScheduledExecutorService executor;

    /** Execution delay interval, in milliseconds */
    private int periodicity;

    /** The task to execute */
    private AbstractTask task;

    /**
     * Creates a new {@link PeriodicalExecutor} the execute the specified with the specified
     * periodicity.
     * 
     * @param task The task to execute.
     * @param periodicity Execution periodicity.
     */
    public PeriodicalExecutor(AbstractTask task, int periodicity)
    {
        super();
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.periodicity = periodicity;
        this.task = task;
        this.task.setCallback(this);
    }

    /**
     * Start the {@link PeriodicalExecutor}.
     */
    public void start()
    {
        executor.scheduleWithFixedDelay(task, 0, periodicity, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop the {@link PeriodicalExecutor}.
     */
    public void stop()
    {
        executor.shutdownNow();
    }

    /**
     * Do nothing after successful execution.
     * <p>
     * Subclasses can override this method to customize task completion handling.
     */
    @Override
    public void executionComplete()
    {
        // Override if necessary
    }

    /**
     * Do nothing after successful execution.
     * <p>
     * Subclasses can override this method to customize task execution failure handling.
     */
    @Override
    public void executionFailure(Throwable t)
    {
        // Override if necessary
    }

    /**
     * Gets the periodicity
     * 
     * @return the periodicity
     */
    public int getPeriodicity()
    {
        return periodicity;
    }

    /**
     * Sets the periodicity
     * 
     * @param periodicity the periodicity to set
     */
    public void setPeriodicity(int periodicity)
    {
        this.periodicity = periodicity;
    }

}
