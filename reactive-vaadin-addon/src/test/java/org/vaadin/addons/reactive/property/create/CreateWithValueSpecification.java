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

package org.vaadin.addons.reactive.property.create;

import javax.annotation.Nonnull;

import io.reactivex.subjects.ReplaySubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.reactive.ReactiveProperty;
import org.vaadin.addons.reactive.ReactivePropertyExtension;
import org.vaadin.addons.reactive.property.DelayActionSpecification;
import org.vaadin.addons.reactive.property.DelaySpecification;
import org.vaadin.addons.reactive.property.SetValueSpecification;
import org.vaadin.addons.reactive.property.SuppressActionSpecification;
import org.vaadin.addons.reactive.property.SuppressSpecification;
import org.vaadin.addons.reactive.property.UpdateValueSpecification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Specification for {@link ReactiveProperty} created by {@link ReactivePropertyExtension#createProperty(Object)}
 *
 * @author dohnal
 */
public interface CreateWithValueSpecification extends
        SetValueSpecification,
        UpdateValueSpecification,
        SuppressSpecification,
        SuppressActionSpecification,
        DelaySpecification,
        DelayActionSpecification
{
    abstract class AbstractCreateWithValueSpecification implements ReactivePropertyExtension
    {
        private final Integer DEFAULT_VALUE = 5;

        private ReplaySubject<ReactiveProperty<?>> capturedProperties;
        private ReactiveProperty<Integer> property;

        @BeforeEach
        void createWithValue()
        {
            capturedProperties = ReplaySubject.create();
            property = createProperty(DEFAULT_VALUE);
        }

        @Nonnull
        @Override
        public <T> ReactiveProperty<T> onCreateProperty(final @Nonnull ReactiveProperty<T> property)
        {
            final ReactiveProperty<T> created = ReactivePropertyExtension.super.onCreateProperty(property);

            capturedProperties.onNext(created);

            return created;
        }

        @Test
        @DisplayName("Created property should be captured")
        public void testCreatedProperty()
        {
            capturedProperties.test().assertValue(property);
        }

        @Test
        @DisplayName("HasValue should be true")
        public void testHasValue()
        {
            assertTrue(property.hasValue());
        }

        @Test
        @DisplayName("IsReadOnly should be false")
        public void testIsReadOnly()
        {
            assertFalse(property.isReadOnly());
        }

        @Test
        @DisplayName("Value should be correct")
        public void testValue()
        {
            assertEquals(DEFAULT_VALUE, property.getValue());
        }

        @Test
        @DisplayName("Observable should emit default value")
        public void testObservable()
        {
            property.asObservable().test().assertValue(DEFAULT_VALUE);
        }

        @Nested
        @DisplayName("Set value specification")
        class SetValue extends AbstractSetValueSpecification
        {
            @Nonnull
            @Override
            public ReactiveProperty<Integer> getProperty()
            {
                return property;
            }
        }

        @Nested
        @DisplayName("Update value specification")
        class UpdateValue extends AbstractUpdateValueSpecification
        {
            @Nonnull
            @Override
            public ReactiveProperty<Integer> getProperty()
            {
                return property;
            }
        }

        @Nested
        @DisplayName("Suppress specification")
        class Suppress extends AbstractSuppressSpecification
        {
            @Nonnull
            @Override
            public ReactiveProperty<Integer> getProperty()
            {
                return property;
            }
        }

        @Nested
        @DisplayName("Suppress action specification")
        class SuppressAction extends AbstractSuppressActionSpecification
        {
            @Nonnull
            @Override
            public ReactiveProperty<Integer> getProperty()
            {
                return property;
            }
        }

        @Nested
        @DisplayName("Delay specification")
        class Delay extends AbstractDelaySpecification
        {
            @Nonnull
            @Override
            public ReactiveProperty<Integer> getProperty()
            {
                return property;
            }
        }

        @Nested
        @DisplayName("Delay action specification")
        class DelayAction extends AbstractDelayActionSpecification
        {
            @Nonnull
            @Override
            public ReactiveProperty<Integer> getProperty()
            {
                return property;
            }
        }
    }
}
