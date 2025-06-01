package com.customrxjava.tests;

import com.customrxjava.core.*;
import com.customrxjava.schedulers.*;
import org.junit.Test;

public class FullFlowTest {

    @Test
    public void fullReactiveFlow() throws InterruptedException {
        System.out.println("[Main] Запуск полного реактивного потока...");

        IOThreadScheduler ioScheduler = new IOThreadScheduler();
        SingleThreadScheduler singleScheduler = new SingleThreadScheduler();

        System.out.println("[Main] Используется subscribeOn(IOThreadScheduler) — генерация будет в пуле потоков.");
        System.out.println("[Main] Используется observeOn(SingleThreadScheduler) — обработка будет в одном потоке.");

        Observable<Integer> observable = Observable.create(emitter -> {
            System.out.println("[Generator] Генерация значений начата. Поток: " + Thread.currentThread().getName());
            for (int i = 1; i <= 10; i++) {
                if (emitter.isDisposed()) {
                    System.out.println("[Generator] Подписка отменена на значении: " + i);
                    break;
                }
                System.out.println("[Generator] Отправка значения: " + i);
                emitter.onNext(i);
                try {
                    Thread.sleep(200); // увеличили задержку для проверки отписки
                } catch (InterruptedException e) {
                    System.out.println("[Generator] Генерация прервана.");
                    break;
                }
            }
            if (!emitter.isDisposed()) {
                System.out.println("[Generator] Генерация завершена.");
                emitter.onComplete();
            }
        });

        Disposable disposable = observable
                .subscribeOn(ioScheduler)
                .filter(x -> {
                    System.out.println("[Filter] Фильтрация значения: " + x + " (пропускаем только чётные)");
                    return x % 2 == 0;
                })
                .map(x -> {
                    String result = "Число: " + x;
                    System.out.println("[Map] Преобразование " + x + " → '" + result + "'");
                    return result;
                })
                .flatMap(str ->
                        Observable.<String>create(em -> {
                            System.out.println("[FlatMap] Оборачивание значения: " + str);
                            em.onNext("[FlatMapped] " + str);
                            em.onComplete();
                        })
                )
                .observeOn(singleScheduler)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String item) {
                        System.out.println("[Observer] Получено значение: " + item + " (Поток: " + Thread.currentThread().getName() + ")");
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("[Observer] Ошибка: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("[Observer] Поток завершён.");
                    }
                });

        Thread.sleep(400); // выполняем отписку раньше окончания генерации
        System.out.println("[Main] Выполняется отписка от потока...");
        disposable.dispose();

        Thread.sleep(300); // ждём завершения фоновых потоков
        System.out.println("[Main] Тест завершён.");
    }
}
