package com.abiquo.server.core.task;

import static org.testng.Assert.assertEquals;

import java.util.UUID;

import com.abiquo.server.core.task.Job.JobType;

public class JobGenerator
{
    public Job createUniqueInstance()
    {
        Job job = new Job();

        job.setId(UUID.randomUUID().toString());
        job.setDescription("Random description");
        job.setType(JobType.CONFIGURE);
        job.setParentTaskId(UUID.randomUUID().toString());

        return job;
    }

    public void assertSameJob(final Job one, final Job other)
    {
        assertEquals(one.getId(), other.getId());
        assertEquals(one.getEntityKey(), other.getEntityKey());
        assertEquals(one.getType(), other.getType());
        assertEquals(one.getDescription(), other.getDescription());
        assertEquals(one.getState(), other.getState());
        assertEquals(one.getRollbackState(), other.getRollbackState());
        assertEquals(one.getParentTaskId(), other.getParentTaskId());
    }
}
