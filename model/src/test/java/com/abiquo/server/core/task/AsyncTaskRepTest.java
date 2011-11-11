package com.abiquo.server.core.task;

import java.lang.reflect.Field;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AsyncTaskRepTest extends RedisAccessTestBase
{
    AsyncTaskRep repo = new AsyncTaskRep();

    @BeforeTest
    @Override
    public void testSetUp()
    {
        super.testSetUp();

        try
        {
            Field jobDaoField = AsyncTaskRep.class.getDeclaredField("jobDao");
            Field taskDaoField = AsyncTaskRep.class.getDeclaredField("taskDao");

            jobDaoField.setAccessible(true);
            taskDaoField.setAccessible(true);

            jobDaoField.set(repo, JobDAO.class.newInstance());
            taskDaoField.set(repo, TaskDAO.class.newInstance());
        }
        catch (Exception e)
        {
            // Ignore
        }
    }

    @Test
    public void test_save()
    {

    }

    // public Task save(Task task)
    // public Job save(Job job)
    // public void delete(Task task)
    // public Task findTask(String taskId)
    // public Job findJob(String jobId)
}
