package com.customrxjava.tests;

import com.customrxjava.core.Observable;
import com.customrxjava.core.Observer;
import org.junit.Test;

public class FlatMapTest {

    @Test
    public void testFlatMap() {
        Observable<Integer> numbers = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        });

        Observable<String> result = numbers.flatMap(n ->
                Observable.<String>create(em -> {
                    em.onNext("Mapped: " + n);
                    em.onComplete();
                })
        );

        result.subscribe(new Observer<String>() {
            @Override
            public void onNext(String item) {
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
    }
}
