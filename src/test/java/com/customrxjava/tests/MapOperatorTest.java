package com.customrxjava.tests;

import com.customrxjava.core.*;
import org.junit.Test;

public class MapOperatorTest {

    @Test
    public void testMapOperator() {
        Observable<Integer> source = Observable.create(emitter -> {
            emitter.onNext(1);
            emitter.onNext(2);
            emitter.onNext(3);
            emitter.onComplete();
        });

        source
                .map(i -> "Number: " + i)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) {
                        System.out.println("Mapped: " + item);
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
