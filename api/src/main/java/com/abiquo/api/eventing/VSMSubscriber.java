package com.abiquo.api.eventing;

/**
 * Virtual System Monitor subscriber interface to allow proxying the implementation.
 * 
 * @author daniel.estevez
 */
public interface VSMSubscriber
{
    /**
     * Attempts an VSM subscription for each VA. If subscription succeeds the task is unsheduled.
     * Gets all VM Deployed and subscribes to VSM
     */
    public abstract void subscribe();
}
