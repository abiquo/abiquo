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
package com.abiquo.api.tracer;

/**
 * Holder for the tracer context.
 * <p>
 * This class stores the tracer context in a <code>thread-local</code> variable, in order to make it
 * safely accessible from any point of the application.
 * 
 * @author ibarrera
 */
public class TracerContextHolder
{
    /** Where the context will be stored. */
    private static ThreadLocal<TracerContext> contextHolder = new ThreadLocal<TracerContext>();

    /**
     * Initialises the tracer context.
     * 
     * @param context The current tracer context.
     */
    public static void initialize(final TracerContext context)
    {
        contextHolder.set(context);
    }

    /**
     * Gets the current tracer context.
     * 
     * @return The current tracer context.
     */
    public static TracerContext getContext()
    {
        TracerContext context = contextHolder.get();
        if (context == null)
        {
            throw new IllegalStateException("Tracer context has not been initialised");
        }
        return context;
    }

    /**
     * Clears the current tracer context.
     */
    public static void clearContext()
    {
        contextHolder.set(null);
    }
}
