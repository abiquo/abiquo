package com.abiquo.server.core.task;

import static org.testng.Assert.assertEquals;

import java.util.UUID;

import com.abiquo.server.core.task.enums.TaskState;
import com.abiquo.server.core.task.enums.TaskType;

public class TaskGenerator
{
    public Task createUniqueInstance()
    {
        Task task = new Task();

        task.setOwnerId(UUID.randomUUID().toString());
        task.setTaskId(UUID.randomUUID().toString());
        task.setUserId(UUID.randomUUID().toString());
        task.setType(TaskType.POWER_ON);
        task.setState(TaskState.STARTED);

        return task;
    }

    public void assertSameTask(final Task one, final Task other)
    {
        assertEquals(one.getTaskId(), other.getTaskId());
        assertEquals(one.getOwnerId(), other.getOwnerId());
        assertEquals(one.getUserId(), other.getUserId());
        assertEquals(one.getType(), other.getType());
        assertEquals(one.getState(), other.getState());
    }
}
