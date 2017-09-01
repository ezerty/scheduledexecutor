package com.mycompany.service;
        
import com.mycompany.service.ScheduledExecutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScheduledExecutorTest {

    private static ScheduledExecutor scheduledExecutor = ScheduledExecutor.INSTANCE;

    @BeforeClass
    public static void startUp() {
        scheduledExecutor.start();
    }

    @AfterClass
    public static void tearDown() {
        scheduledExecutor.stop();
    }

    @Test
    public void testSimpleTaskExecution() throws Exception {

        int a = 3;
        int b = 2;

        FutureTask<Integer> resultFuture = scheduledExecutor.add(LocalDateTime.now(), (Callable<Integer>) () -> {
            return a + b;
        });

        Integer executionResult = resultFuture.get();
        Assert.assertEquals(executionResult.intValue(), a + b);
    }

    @Test
    public void testSequenceExecution() {
        List<Integer> results = new ArrayList<>();
        List<FutureTask> futureTasks = new ArrayList<>();

        LocalDateTime futureTime = LocalDateTime.now().plusSeconds(1);
        LocalDateTime now = LocalDateTime.now();

        futureTasks.add(addElementToList(results, 3, futureTime));
        futureTasks.add(addElementToList(results, 4, futureTime));
        futureTasks.add(addElementToList(results, 5, futureTime));
        futureTasks.add(addElementToList(results, 6, futureTime));
        futureTasks.add(addElementToList(results, 7, futureTime));
        futureTasks.add(addElementToList(results, 8, futureTime));
        futureTasks.add(addElementToList(results, 9, futureTime));
        futureTasks.add(addElementToList(results, 10, futureTime));
        futureTasks.add(addElementToList(results, 1, now));
        futureTasks.add(addElementToList(results, 2, now));

        futureTasks.forEach((FutureTask t) -> {
            try {
                t.get();
            } catch (Exception e) {
                throw new RuntimeException("Sequential execution test failed", e);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), results);
    }

    private <T> FutureTask addElementToList(List<T> list, T element, LocalDateTime time) {
        return scheduledExecutor.add(time, (Callable) () -> {
            list.add(element);
            return null;
        });
    }
}
