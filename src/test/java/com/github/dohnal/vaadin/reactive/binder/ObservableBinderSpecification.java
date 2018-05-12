/*
 * Copyright (c) 2018-present, reactive-mvvm Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.github.dohnal.vaadin.reactive.binder;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import com.github.dohnal.vaadin.reactive.IsObservable;
import com.github.dohnal.vaadin.reactive.ObservableBinder;
import com.github.dohnal.vaadin.reactive.ReactiveBinderExtension;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Specification for binding observable by {@link ObservableBinder}
 *
 * @author dohnal
 */
public interface ObservableBinderSpecification
{
    abstract class WhenBindObservableToConsumerSpecification implements ReactiveBinderExtension
    {
        protected TestScheduler testScheduler;
        protected PublishSubject<Integer> observable;
        protected Consumer<Integer> consumer;
        protected PublishSubject<Throwable> errorSubject;
        protected TestObserver<Throwable> errorObserver;
        protected Disposable disposable;

        @BeforeEach
        @SuppressWarnings("unchecked")
        protected void bind()
        {
            testScheduler = new TestScheduler();
            observable = PublishSubject.create();
            observable.observeOn(testScheduler);
            consumer = Mockito.mock(Consumer.class);

            errorSubject = PublishSubject.create();
            errorSubject.observeOn(Schedulers.trampoline());
            errorObserver = errorSubject.test();

            disposable = when(observable).then(consumer);
        }

        @Override
        public void handleError(final @Nonnull Throwable error)
        {
            errorSubject.onNext(error);
        }

        @Test
        @DisplayName("Consumer should not be called")
        public void testConsumer()
        {
            Mockito.verify(consumer, Mockito.never()).accept(Mockito.any());
        }

        @Test
        @DisplayName("No error should be handled")
        public void testHandleError()
        {
            errorObserver.assertNoValues();
        }

        @Nested
        @DisplayName("When observable emits value")
        class WhenObservableEmitsValue
        {
            @Test
            @DisplayName("Consumer should be called with correct value")
            public void testConsumer()
            {
                observable.onNext(7);
                testScheduler.triggerActions();

                Mockito.verify(consumer).accept(7);
            }

            @Test
            @DisplayName("No error should be handled")
            public void testHandleError()
            {
                errorObserver.assertNoValues();
            }

            @Nested
            @DisplayName("When consumer throws error")
            class WhenConsumerThrowsError
            {
                private final Throwable ERROR = new RuntimeException("Error");

                @BeforeEach
                void before()
                {
                    Mockito.doThrow(ERROR).when(consumer).accept(7);
                }

                @Test
                @DisplayName("Correct error should be handled")
                public void testHandleError()
                {
                    observable.onNext(7);
                    testScheduler.triggerActions();

                    errorObserver.assertValue(ERROR);
                }
            }
        }

        @Nested
        @DisplayName("When observable emits error")
        class WhenObservableEmitsError
        {
            private final Throwable ERROR = new RuntimeException("Error");

            @Test
            @DisplayName("Consumer should not be called")
            public void testConsumer()
            {
                observable.onError(ERROR);
                testScheduler.triggerActions();

                Mockito.verify(consumer, Mockito.never()).accept(Mockito.any());
            }

            @Test
            @DisplayName("Correct error should be handled")
            public void testHandleError()
            {
                observable.onError(ERROR);
                testScheduler.triggerActions();

                errorObserver.assertValue(ERROR);
            }
        }

        @Nested
        @DisplayName("After observable is unbound from consumer")
        class AfterUnbind
        {
            @BeforeEach
            void before()
            {
                disposable.dispose();
            }

            @Nested
            @DisplayName("When observable emits value")
            class WhenObservableEmitsValue
            {
                @Test
                @DisplayName("Consumer should not be called")
                public void testConsumer()
                {
                    observable.onNext(7);
                    testScheduler.triggerActions();

                    Mockito.verify(consumer, Mockito.never()).accept(Mockito.any());
                }

                @Test
                @DisplayName("No error should be handled")
                public void testHandleError()
                {
                    errorObserver.assertNoValues();
                }
            }

            @Nested
            @DisplayName("When source observable emits error")
            class WhenSourceObservableEmitsError
            {
                private final Throwable ERROR = new RuntimeException("Error");

                @Test
                @DisplayName("Consumer should not be called")
                public void testConsumer()
                {
                    observable.onError(ERROR);
                    testScheduler.triggerActions();

                    Mockito.verify(consumer, Mockito.never()).accept(Mockito.any());
                }

                @Test
                @DisplayName("No error should be handled")
                public void testHandleError()
                {
                    observable.onError(ERROR);
                    testScheduler.triggerActions();

                    errorObserver.assertNoValues();
                }
            }
        }
    }

    abstract class WhenBindIsObservableToConsumerSpecification extends WhenBindObservableToConsumerSpecification
    {
        @BeforeEach
        @SuppressWarnings("unchecked")
        protected void bind()
        {
            testScheduler = new TestScheduler();
            observable = PublishSubject.create();
            observable.observeOn(testScheduler);
            consumer = Mockito.mock(Consumer.class);

            errorSubject = PublishSubject.create();
            errorSubject.observeOn(Schedulers.trampoline());
            errorObserver = errorSubject.test();

            disposable = when(new IsObservable<Integer>()
            {
                @Nonnull
                @Override
                public Observable<Integer> asObservable()
                {
                    return observable;
                }
            }).then(consumer);
        }
    }

    abstract class WhenBindObservableToRunnableSpecification implements ReactiveBinderExtension
    {
        protected TestScheduler testScheduler;
        protected PublishSubject<Integer> observable;
        protected Runnable runnable;
        protected PublishSubject<Throwable> errorSubject;
        protected TestObserver<Throwable> errorObserver;
        protected Disposable disposable;

        @BeforeEach
        @SuppressWarnings("unchecked")
        protected void bind()
        {
            testScheduler = new TestScheduler();
            observable = PublishSubject.create();
            observable.observeOn(testScheduler);
            runnable = Mockito.mock(Runnable.class);

            errorSubject = PublishSubject.create();
            errorSubject.observeOn(Schedulers.trampoline());
            errorObserver = errorSubject.test();

            disposable = when(observable).then(runnable);
        }

        @Override
        public void handleError(final @Nonnull Throwable error)
        {
            errorSubject.onNext(error);
        }

        @Test
        @DisplayName("Runnable should not be called")
        public void testRunnable()
        {
            Mockito.verify(runnable, Mockito.never()).run();
        }

        @Test
        @DisplayName("No error should be handled")
        public void testHandleError()
        {
            errorObserver.assertNoValues();
        }

        @Nested
        @DisplayName("When observable emits value")
        class WhenObservableEmitsValue
        {
            @Test
            @DisplayName("Runnable should be called")
            public void testRunnable()
            {
                observable.onNext(7);
                testScheduler.triggerActions();

                Mockito.verify(runnable).run();
            }

            @Test
            @DisplayName("No error should be handled")
            public void testHandleError()
            {
                errorObserver.assertNoValues();
            }

            @Nested
            @DisplayName("When runnable throws error")
            class WhenRunnableThrowsError
            {
                private final Throwable ERROR = new RuntimeException("Error");

                @BeforeEach
                void before()
                {
                    Mockito.doThrow(ERROR).when(runnable).run();
                }

                @Test
                @DisplayName("Correct error should be handled")
                public void testHandleError()
                {
                    observable.onNext(7);
                    testScheduler.triggerActions();

                    errorObserver.assertValue(ERROR);
                }
            }
        }

        @Nested
        @DisplayName("When observable emits error")
        class WhenObservableEmitsError
        {
            private final Throwable ERROR = new RuntimeException("Error");

            @Test
            @DisplayName("Runnable should not be called")
            public void testRunnable()
            {
                observable.onError(ERROR);
                testScheduler.triggerActions();

                Mockito.verify(runnable, Mockito.never()).run();
            }

            @Test
            @DisplayName("Correct error should be handled")
            public void testHandleError()
            {
                observable.onError(ERROR);
                testScheduler.triggerActions();

                errorObserver.assertValue(ERROR);
            }
        }

        @Nested
        @DisplayName("After observable is unbound from runnable")
        class AfterUnbind
        {
            @BeforeEach
            void before()
            {
                disposable.dispose();
            }

            @Nested
            @DisplayName("When observable emits value")
            class WhenObservableEmitsValue
            {
                @Test
                @DisplayName("Runnable should not be called")
                public void testRunnable()
                {
                    observable.onNext(7);
                    testScheduler.triggerActions();

                    Mockito.verify(runnable, Mockito.never()).run();
                }

                @Test
                @DisplayName("No error should be handled")
                public void testHandleError()
                {
                    errorObserver.assertNoValues();
                }
            }

            @Nested
            @DisplayName("When source observable emits error")
            class WhenSourceObservableEmitsError
            {
                private final Throwable ERROR = new RuntimeException("Error");

                @Test
                @DisplayName("Runnable should not be called")
                public void testRunnable()
                {
                    observable.onError(ERROR);
                    testScheduler.triggerActions();

                    Mockito.verify(runnable, Mockito.never()).run();
                }

                @Test
                @DisplayName("No error should be handled")
                public void testHandleError()
                {
                    observable.onError(ERROR);
                    testScheduler.triggerActions();

                    errorObserver.assertNoValues();
                }
            }
        }
    }

    abstract class WhenBindIsObservableToRunnableSpecification extends WhenBindObservableToRunnableSpecification
    {
        @BeforeEach
        @SuppressWarnings("unchecked")
        protected void bind()
        {
            testScheduler = new TestScheduler();
            observable = PublishSubject.create();
            observable.observeOn(testScheduler);
            runnable = Mockito.mock(Runnable.class);

            errorSubject = PublishSubject.create();
            errorSubject.observeOn(Schedulers.trampoline());
            errorObserver = errorSubject.test();

            disposable = when(new IsObservable<Integer>()
            {
                @Nonnull
                @Override
                public Observable<Integer> asObservable()
                {
                    return observable;
                }
            }).then(runnable);
        }
    }
}
