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

import com.github.dohnal.vaadin.reactive.Disposable;
import com.github.dohnal.vaadin.reactive.IsObservable;
import com.github.dohnal.vaadin.reactive.ObservablePropertyBinder;
import com.github.dohnal.vaadin.reactive.ReactiveBinder;
import com.github.dohnal.vaadin.reactive.ReactiveProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

/**
 * Specification for binding observable property by {@link ObservablePropertyBinder}
 *
 * @author dohnal
 */
public interface ObservablePropertyBinderSpecification
{
    abstract class WhenBindObservablePropertyToObservableSpecification implements ReactiveBinder
    {
        private TestScheduler testScheduler;
        private TestSubject<Integer> sourceObservable;
        private ReactiveProperty<Integer> property;
        private Disposable disposable;

        @BeforeEach
        protected void bind()
        {
            testScheduler = Schedulers.test();
            sourceObservable = TestSubject.create(testScheduler);
            property = ReactiveProperty.empty();

            disposable = bind(property).to(sourceObservable);
        }

        @Test
        @DisplayName("Property value should not be set")
        public void testPropertyValue()
        {
            property.asObservable().test()
                    .assertNoValues();
        }

        @Nested
        @DisplayName("When source observable emits value")
        class WhenSourceObservableEmitsValue
        {
            @Test
            @DisplayName("Property value should be set with correct value")
            public void testPropertyValue()
            {
                property.asObservable().test()
                        .perform(() -> {
                            sourceObservable.onNext(7);
                            testScheduler.triggerActions();
                        })
                        .assertValue(7);
            }
        }

        @Nested
        @DisplayName("When source observable emits value after property is unbound from observable")
        class WhenSourceObservableEmitsValueAfterUnbind
        {
            @Test
            @DisplayName("Property value should not be set")
            public void testPropertyValue()
            {
                property.asObservable().test()
                        .perform(() -> {
                            disposable.dispose();
                            sourceObservable.onNext(7);
                            testScheduler.triggerActions();
                        })
                        .assertNoValues();
            }
        }
    }

    abstract class WhenBindObservablePropertyToIsObservableSpecification implements ReactiveBinder
    {
        private TestScheduler testScheduler;
        private TestSubject<Integer> sourceObservable;
        private ReactiveProperty<Integer> property;
        private Disposable disposable;

        @BeforeEach
        protected void bind()
        {
            testScheduler = Schedulers.test();
            sourceObservable = TestSubject.create(testScheduler);
            property = ReactiveProperty.empty();

            disposable = bind(property).to(new IsObservable<Integer>()
            {
                @Nonnull
                @Override
                public Observable<Integer> asObservable()
                {
                    return sourceObservable;
                }
            });
        }

        @Test
        @DisplayName("Property value should not be set")
        public void testPropertyValue()
        {
            property.asObservable().test()
                    .assertNoValues();
        }

        @Nested
        @DisplayName("When source observable emits value")
        class WhenSourceObservableEmitsValue
        {
            @Test
            @DisplayName("Property value should be set with correct value")
            public void testPropertyValue()
            {
                property.asObservable().test()
                        .perform(() -> {
                            sourceObservable.onNext(7);
                            testScheduler.triggerActions();
                        })
                        .assertValue(7);
            }
        }

        @Nested
        @DisplayName("When source observable emits value after property is unbound from observable")
        class WhenSourceObservableEmitsValueAfterUnbind
        {
            @Test
            @DisplayName("Property value should not be set")
            public void testPropertyValue()
            {
                property.asObservable().test()
                        .perform(() -> {
                            disposable.dispose();
                            sourceObservable.onNext(7);
                            testScheduler.triggerActions();
                        })
                        .assertNoValues();
            }
        }
    }

    abstract class WhenBindObservablePropertyToObservablePropertySpecification implements ReactiveBinder
    {
        private ReactiveProperty<Integer> sourceProperty;
        private ReactiveProperty<Integer> property;
        private Disposable disposable;

        @BeforeEach
        protected void bind()
        {
            sourceProperty = ReactiveProperty.empty();
            property = ReactiveProperty.empty();

            disposable = bind(property).to(sourceProperty);
        }

        @Test
        @DisplayName("Property value should not be set")
        public void testPropertyValue()
        {
            property.asObservable().test()
                    .assertNoValues();
        }

        @Test
        @DisplayName("Source property value should not be set")
        public void testSourcePropertyValue()
        {
            sourceProperty.asObservable().test()
                    .assertNoValues();
        }

        @Nested
        @DisplayName("When source property emits value")
        class WhenSourceObservableEmitsValue
        {
            @Test
            @DisplayName("Property value should be set with correct value")
            public void testPropertyValue()
            {
                property.asObservable().test()
                        .perform(() -> sourceProperty.setValue(7))
                        .assertValue(7);
            }
        }

        @Nested
        @DisplayName("When property emits value")
        class WhenObservableEmitsValue
        {
            @Test
            @DisplayName("Source property value should be set with correct value")
            public void testSourcePropertyValue()
            {
                sourceProperty.asObservable().test()
                        .perform(() -> property.setValue(7))
                        .assertValue(7);
            }
        }

        @Nested
        @DisplayName("When source property emits value after property is unbound from source property")
        class WhenSourceObservableEmitsValueAfterUnbind
        {
            @Test
            @DisplayName("Property value should not be set")
            public void testPropertyValue()
            {
                property.asObservable().test()
                        .perform(() -> {
                            disposable.dispose();
                            sourceProperty.setValue(7);
                        })
                        .assertNoValues();
            }
        }

        @Nested
        @DisplayName("When property emits value after property is unbound from source property")
        class WhenObservableEmitsValueAfterUnbind
        {
            @Test
            @DisplayName("Source property value should not be set")
            public void testPropertyValue()
            {
                sourceProperty.asObservable().test()
                        .perform(() -> {
                            disposable.dispose();
                            property.setValue(7);
                        })
                        .assertNoValues();
            }
        }
    }
}
