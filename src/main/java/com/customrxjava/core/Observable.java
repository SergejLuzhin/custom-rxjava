package com.customrxjava.core;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.customrxjava.schedulers.Scheduler;

public class Observable<T> {
    private final Consumer<Emitter<T>> onSubscribe;

    private Observable(Consumer<Emitter<T>> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <T> Observable<T> create(Consumer<Emitter<T>> emitterConsumer) {
        return new Observable<>(emitterConsumer);
    }

    public Disposable subscribe(Observer<T> observer) {
        SimpleDisposable disposable = new SimpleDisposable();
        try {
            onSubscribe.accept(new Emitter<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposable.isDisposed()) {
                        observer.onNext(item);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposable.isDisposed()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }

                @Override
                public boolean isDisposed() {
                    return disposable.isDisposed();
                }
            });
        } catch (Throwable t) {
            observer.onError(t);
        }
        return disposable;
    }

    public <R> Observable<R> map(Function<? super T, ? extends R> mapper) {
        return Observable.create(emitter ->
                Observable.this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        try {
                            R mapped = mapper.apply(item);
                            emitter.onNext(mapped);
                        } catch (Throwable t) {
                            emitter.onError(t);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        emitter.onComplete();
                    }
                })
        );
    }

    public Observable<T> filter(Predicate<? super T> predicate) {
        return Observable.create(emitter ->
                Observable.this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        try {
                            if (predicate.test(item)) {
                                emitter.onNext(item);
                            }
                        } catch (Throwable t) {
                            emitter.onError(t);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        emitter.onComplete();
                    }
                })
        );
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return Observable.create(emitter ->
                scheduler.execute(() -> Observable.this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        emitter.onNext(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        emitter.onComplete();
                    }
                }))
        );
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return Observable.create(emitter ->
                Observable.this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        scheduler.execute(() -> emitter.onNext(item));
                    }

                    @Override
                    public void onError(Throwable t) {
                        scheduler.execute(() -> emitter.onError(t));
                    }

                    @Override
                    public void onComplete() {
                        scheduler.execute(emitter::onComplete);
                    }
                })
        );
    }

    public <R> Observable<R> flatMap(Function<? super T, Observable<R>> mapper) {
        return Observable.create(emitter ->
                Observable.this.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        try {
                            Observable<R> inner = mapper.apply(item);
                            inner.subscribe(new Observer<R>() {
                                @Override
                                public void onNext(R r) {
                                    emitter.onNext(r);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    emitter.onError(t);
                                }

                                @Override
                                public void onComplete() {
                                    // optionally track completions
                                }
                            });
                        } catch (Throwable t) {
                            emitter.onError(t);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.onError(t);
                    }

                    @Override
                    public void onComplete() {
                        emitter.onComplete();
                    }
                })
        );
    }

}
