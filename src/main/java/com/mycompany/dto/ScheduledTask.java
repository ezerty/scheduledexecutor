package com.mycompany.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledTask implements Delayed {

    private static final AtomicLong taskCounter = new AtomicLong(0);

    private final long taskId;

    public ScheduledTask() {
        taskId = taskCounter.incrementAndGet();
    }

    private LocalDateTime scheduledTime;

    private FutureTask task;

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public FutureTask getTask() {
        return task;
    }

    public void setTask(FutureTask task) {
        this.task = task;
    }

    public long getTaskId() {
        return taskId;
    }

    @Override
    public String toString() {
        return "ScheduledTask{" + "taskId=" + taskId + ", scheduledTime=" + scheduledTime + '}';
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delay = toLongByUTC(getScheduledTime()) - toLongByUTC(LocalDateTime.now());
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o instanceof ScheduledTask) {
            ScheduledTask anotherTask = (ScheduledTask) o;

            int compareResult = getScheduledTime().compareTo(anotherTask.getScheduledTime());
            return compareResult != 0 ? compareResult : Long.compare(getTaskId(), anotherTask.getTaskId());

        } else {
            throw new UnsupportedOperationException(String.format("Comparable only with %s, but %s has been passed",
                    getClass(), o.getClass()));
        }
    }

    private long toLongByUTC(LocalDateTime time) {
        return time.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }
}
