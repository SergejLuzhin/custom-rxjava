package com.customrxjava.tests;

import com.customrxjava.core.*;
import com.customrxjava.schedulers.*;
import org.junit.Test;

public class SchedulerTest {

    @Test
    public void testSchedulers() throws InterruptedException {
        Observable<String> observable = Observable.create(emitter -> {
            System.out.println("[create] Thread: " + Thread.currentThread().getName());
            emitter.onNext("A");
            emitter.onNext("B");
            emitter.onComplete();
        });

        observable
                .subscribeOn(new IOThreadScheduler())
                .observeOn(new SingleThreadScheduler())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) {
                        System.out.println("[onNext] " + item + " on thread " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("Error: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Completed on thread " + Thread.currentThread().getName());
                    }
                });

        // Завершеие фоновых потоков
        Thread.sleep(1000);
    }
}
