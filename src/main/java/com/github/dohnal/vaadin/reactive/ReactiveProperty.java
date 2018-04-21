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

package com.github.dohnal.vaadin.reactive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

import com.github.dohnal.vaadin.reactive.property.BehaviorSubjectProperty;
import rx.Observable;

/**
 * Reactive property stores single editable value and also acts as observable
 *
 * @param <T> type of property
 * @author dohnal
 */
public interface ReactiveProperty<T> extends Property<T>, IsObservable<T>
{
    /**
     * Creates new property with no value
     *
     * @param <T> type of property
     * @return created property
     */
    @Nonnull
    static <T> ReactiveProperty<T> empty()
    {
        return new BehaviorSubjectProperty<>();
    }

    /**
     * Creates new property with given default value
     *
     * @param defaultValue default value
     * @param <T> type of property
     * @return created property
     */
    @Nonnull
    static <T> ReactiveProperty<T> withValue(final @Nullable T defaultValue)
    {
        return new BehaviorSubjectProperty<>(defaultValue);
    }

    /**
     * Creates new property from given observable
     *
     * @param observable observable
     * @param <T> type of property
     * @return created property
     */
    @Nonnull
    static <T> ReactiveProperty<T> fromObservable(final @Nonnull Observable<T> observable)
    {
        return new BehaviorSubjectProperty<>(observable);
    }

    /**
     * Creates new property from given property
     *
     * @param property property
     * @param <T> type of property
     * @return created property
     */
    @Nonnull
    static <T> ReactiveProperty<T> fromProperty(final @Nonnull ReactiveProperty<T> property)
    {
        return new BehaviorSubjectProperty<>(property);
    }

    /**
     * Returns current value
     * NOTE: if this property has not value, this method still return null
     *
     * @return current value
     * @see #hasValue()
     */
    @Nullable
    T getValue();

    /**
     * Sets new value and notifies all subscribers
     *
     * @param value value
     */
    @Override
    void setValue(final @Nullable T value);

    /**
     * Return if this property has any value
     *
     * @return if this property has any value
     */
    boolean hasValue();

    /**
     * Updates a value by given update function and notifies all subscribers
     *
     * @param update update function
     */
    void updateValue(final @Nonnull Function<T, T> update);

    /**
     * Return observable for this property which can be subscribed to
     *
     * @return observable for this property
     */
    @Nonnull
    @Override
    Observable<T> asObservable();
}
