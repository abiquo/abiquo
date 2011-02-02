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
package com.abiquo.abicloud.taskservice.impl.quartz;

import static com.abiquo.abicloud.taskservice.utils.TaskUtils.getName;
import static com.abiquo.abicloud.taskservice.utils.TaskUtils.validateTask;

import java.text.ParseException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.TaskService;
import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.impl.AbstractTaskService;
import com.abiquo.abicloud.taskservice.impl.quartz.config.InMemoryConfiguration;
import com.abiquo.abicloud.taskservice.impl.quartz.config.QuartzConfiguration;
import com.abiquo.abicloud.taskservice.model.Task;

/**
 * Quartz implementation of the {@link TaskService}.
 * 
 * @author ibarrera
 */
public class QuartzTaskService extends AbstractTaskService
{
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzTaskService.class);

    /**
     * Default group name for task service triggers.
     */
    private static final String DEFAULT_GROUP_NAME = "abicloud";

    /**
     * The configuration for the service.
     */
    private QuartzConfiguration config;

    /**
     * The factory used to obtain {@link Scheduler}.
     */
    private SchedulerFactory schedulerFactory;

    /**
     * The current {@link Scheduler}.
     */
    private Scheduler scheduler;

    /**
     * Creates a new {@link TaskService} with the default configuration.
     * 
     * @throws TaskServiceException If the service cannot be initialized.
     */
    public QuartzTaskService() throws TaskServiceException
    {
        // By default, use a InMemoryConfiguration
        this(new InMemoryConfiguration());
    }

    /**
     * Creates a new {@link TaskService} with the specified configuration.
     * 
     * @param config The service configuration.
     * @throws TaskServiceException If the service cannot be initialized.
     */
    public QuartzTaskService(final QuartzConfiguration config) throws TaskServiceException
    {
        super();
        this.config = config;

        // Create scheduler
        try
        {
            schedulerFactory = new StdSchedulerFactory(config.getConfiguration());
            scheduler = schedulerFactory.getScheduler();
        }
        catch (SchedulerException ex)
        {
            throw new TaskServiceException("Could not initialize "
                + this.getClass().getSimpleName(), ex);
        }

        // Start the scheduler
        try
        {
            scheduler.start();
        }
        catch (SchedulerException ex)
        {
            throw new TaskServiceException("Could not start " + this.getClass().getSimpleName(), ex);
        }

        LOGGER.info("{} started", this.getClass().getSimpleName());
    }

    @Override
    public void schedule(final Class< ? > taskClass) throws TaskServiceException
    {
        // Check if is a valid task class
        validateTask(taskClass);

        // Read task configuration
        Task taskConfig = taskClass.getAnnotation(Task.class);
        String taskName = getName(taskClass, taskConfig.name(), "Job");
        String triggerName = getName(taskClass, taskConfig.name(), "Trigger");

        LOGGER.info("Adding task {} to {}", taskName, this.getClass().getSimpleName());

        // Create JobDetail Object
        JobDetail jobDetail = new JobDetail(taskName, DEFAULT_GROUP_NAME, QuartzTask.class);
        jobDetail.getJobDataMap().put(QuartzTask.TASK_CLASS_ATTRIBUTE, taskClass);

        // Create Trigger
        Trigger trigger = null;
        if (!"".equals(taskConfig.cron()))
        {
            // Create a new cron-based trigger
            trigger = createCronTrigger(triggerName, taskConfig.cron(), taskClass);
        }
        else
        {
            // Create a new period-based trigger
            trigger =
                createPeriodicTrigger(triggerName, taskConfig.interval(), taskConfig.startDelay(),
                    taskConfig.timeUnit());
        }

        // Schedule job
        try
        {
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (SchedulerException ex)
        {
            throw new TaskServiceException("Could not schedule task: " + taskClass.getName(), ex);
        }
    }

    @Override
    public void unschedule(final Class< ? > taskClass) throws TaskServiceException
    {
        // Check if is a valid task class
        validateTask(taskClass);

        // Read task configuration
        Task taskConfig = taskClass.getAnnotation(Task.class);
        String taskName = getName(taskClass, taskConfig.name(), "Job");
        String triggerName = getName(taskClass, taskConfig.name(), "Trigger");

        LOGGER.info("Removing task {} from {}", taskName, this.getClass().getSimpleName());

        try
        {
            scheduler.unscheduleJob(triggerName, DEFAULT_GROUP_NAME);
        }
        catch (SchedulerException ex)
        {
            throw new TaskServiceException("Could not unschedule task: " + taskClass.getName(), ex);
        }
    }

    @Override
    public void shutdown() throws TaskServiceException
    {
        LOGGER.info("Shutting down {}", this.getClass().getSimpleName());

        try
        {
            scheduler.shutdown();
        }
        catch (SchedulerException ex)
        {
            throw new TaskServiceException("Could not shutdown scheduler", ex);
        }
    }

    /**
     * Creates a new period-based trigger.
     * 
     * @param triggerName The name of the trigger.
     * @param repeatInterval The periodicity in <b>seconds</b>.
     * @param startDelay The start delay in <b>seconds</b>.
     * @param timeUnit The type of periodicyty.
     * @return The period-based trigger.
     * @throws TaskServiceException If task cannot be scheduled.
     */
    protected Trigger createPeriodicTrigger(final String triggerName, final Integer repeatInterval,
        final Integer startDelay, final TimeUnit timeUnit) throws TaskServiceException
    {
        // Compute start time
        Calendar startTime = Calendar.getInstance();
        int delay = (startDelay == null) ? 0 : startDelay;
        int repeatFactor = 0;

        if (timeUnit == TimeUnit.SECONDS)
        {
            repeatFactor = 1000;
            startTime.add(Calendar.SECOND, delay);
        }
        else
        {
            repeatFactor = 60 * 1000;
            startTime.add(Calendar.MINUTE, delay);
        }

        startTime.add((timeUnit == TimeUnit.MINUTES) ? Calendar.MINUTE : Calendar.SECOND, delay);

        // Create trigger
        SimpleTrigger trigger = new SimpleTrigger(triggerName, DEFAULT_GROUP_NAME);
        trigger.setStartTime(startTime.getTime());
        trigger.setRepeatInterval(repeatInterval * repeatFactor);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);

        return trigger;
    }

    /**
     * Creates a new cron-based trigger.
     * 
     * @param triggerName The name of the trigger.
     * @param cronExpression A cron expresion that configures the task execution.
     * @param taskClass The class of the target task.
     * @return Creates The cron-based trigger.
     * @throws TaskServiceException If task cannot be scheduled.
     */
    protected Trigger createCronTrigger(final String triggerName, final String cronExpression,
        final Class< ? > taskClass) throws TaskServiceException
    {
        try
        {
            return new CronTrigger(triggerName, DEFAULT_GROUP_NAME, cronExpression);
        }
        catch (ParseException ex)
        {
            throw new TaskServiceException("Invalid cron expression for task: "
                + taskClass.getName(), ex);
        }
    }

    /**
     * Gets the config.
     * 
     * @return the config
     */
    public QuartzConfiguration getConfig()
    {
        return config;
    }

    /**
     * Sets the config.
     * 
     * @param config the config to set
     */
    public void setConfig(final QuartzConfiguration config)
    {
        this.config = config;
    }

}
