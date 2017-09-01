package com.mycompany.service;

import com.mycompany.dto.ScheduledTask;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import java.util.concurrent.RejectedExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ScheduledExecutor {

    INSTANCE;

    private final Logger logger = LogManager.getLogger(getClass());

    private BlockingQueue<ScheduledTask> queue = new DelayQueue<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public FutureTask add(LocalDateTime time, Callable callable) {

        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.setScheduledTime(time);
        FutureTask futureTask = new FutureTask(callable);
        scheduledTask.setTask(futureTask);

        logger.debug(String.format("Enqueuing task: %s", scheduledTask));
        try {
            queue.put(scheduledTask);
        } catch (InterruptedException e) {
            logger.error(String.format("Failed to enqueue task: %s", scheduledTask), e);
        }

        return futureTask;
    }

    public void start() {
        new Thread(() -> {
            while (!executorService.isShutdown()) {
                try {
                    ScheduledTask task = queue.take();
                    logger.debug(String.format("Dequeued task: %s", task));

                    try {
                        executorService.submit(() -> {
                            logger.debug(String.format("Executing task: %s", task));
                            task.getTask().run();
                        });

                    } catch (RejectedExecutionException e) {
                        logger.error(String.format("Failed to execute task at %s", task.getScheduledTime()), e);
                    }
                } catch (InterruptedException e) {
                    logger.error("Task queue polling has been interrupted", e);
                }
            }
        }).start();
    }

    public void stop() {
        executorService.shutdown();
    }

}
