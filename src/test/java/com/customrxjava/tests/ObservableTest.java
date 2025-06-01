package com.customrxjava.tests;

import com.customrxjava.core.*;
import org.junit.Test;

public class ObservableTest {

    @Test
    public void testObservableBasic() {
        Observable<String> observable = Observable.create(emitter -> {
            emitter.onNext("Hello");
            emitter.onNext("Rx");
            emitter.onNext("Java");
            emitter.onComplete();
        });

        observable.subscribe(new Observer<String>() {
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
