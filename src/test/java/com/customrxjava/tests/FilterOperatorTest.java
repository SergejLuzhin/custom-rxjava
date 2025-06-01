package com.customrxjava.tests;

import com.customrxjava.core.*;
import org.junit.Test;

public class FilterOperatorTest {

    @Test
    public void testFilterOperator() {
        Observable<Integer> numbers = Observable.create(emitter -> {
            for (int i = 1; i <= 5; i++) {
                emitter.onNext(i);
            }
            emitter.onComplete();
        });

        numbers
                .filter(i -> i % 2 == 0)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Filtered: " + item);
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
