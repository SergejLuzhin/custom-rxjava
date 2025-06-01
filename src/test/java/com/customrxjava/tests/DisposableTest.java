package com.customrxjava.tests;

import com.customrxjava.core.*;
import org.junit.Test;

public class DisposableTest {

    @Test
    public void testDisposal() throws InterruptedException {
        Observable<Long> observable = Observable.create(emitter -> {
            new Thread(() -> {
                for (long i = 0; i < 100; i++) {
                    if (emitter.isDisposed()) {
                        System.out.println("Stopped by isDisposed()");
                        break;
                    }
                    emitter.onNext(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }).start();
        });

        Disposable disposable = observable.subscribe(new Observer<Long>() {
            @Override
            public void onNext(Long item) {
                System.out.println("Received: " + item);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Completed.");
            }
        });

        Thread.sleep(350); // получим 3-4 значения
        System.out.println("Disposing...");
        disposable.dispose();

        Thread.sleep(500); // ждём завершения фонового потока
    }
}
