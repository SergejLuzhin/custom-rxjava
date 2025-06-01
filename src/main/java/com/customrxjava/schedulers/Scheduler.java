package com.customrxjava.schedulers;

public interface Scheduler {
    void execute(Runnable task);
}
