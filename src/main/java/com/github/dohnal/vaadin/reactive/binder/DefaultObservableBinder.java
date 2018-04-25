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

import com.github.dohnal.vaadin.reactive.Disposable;
import com.github.dohnal.vaadin.reactive.ObservableBinder;
import rx.Observable;
import rx.Subscription;

/**
 * Default implementation of {@link ObservableBinder)}
 *
 * @param <T> type of value
 * @author dohnal
 */
public final class DefaultObservableBinder<T> implements ObservableBinder<T>
{
    private final Observable<T> observable;

    public DefaultObservableBinder(final @Nonnull Observable<T> observable)
    {
        this.observable = observable;
    }

    @Nonnull
    @Override
    public final Disposable then(final @Nonnull Consumer<? super T> action)
    {
        final Subscription subscription = observable.subscribe(action::accept);

        return subscription::unsubscribe;
    }

    @Nonnull
    @Override
    public final Disposable then(final @Nonnull Runnable action)
    {
        final Subscription subscription = observable.subscribe(value -> action.run());

        return subscription::unsubscribe;
    }
}
