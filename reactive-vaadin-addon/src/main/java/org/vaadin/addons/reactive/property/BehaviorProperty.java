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

package org.vaadin.addons.reactive.property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.vaadin.addons.reactive.ReactiveProperty;
import org.vaadin.addons.reactive.exceptions.ReadOnlyPropertyException;

/**
 * Basic implementation of {@link ReactiveProperty} based on {@link BehaviorSubject}
 *
 * @param <T> type of property
 * @author dohnal
 */
public final class BehaviorProperty<T> implements ReactiveProperty<T>
{
    private final AtomicReference<T> value;

    private final PublishSubject<T> subject;

    private final Boolean readOnly;

    private final AtomicInteger suppressed;

    private final AtomicInteger delayed;

    private final BehaviorSubject<Boolean> delaySubject;

    /**
     * Creates new property with no value
     */
    public BehaviorProperty()
    {
        this.value = new AtomicReference<>();
        this.subject = PublishSubject.create();
        this.readOnly = false;
        this.suppressed = new AtomicInteger(0);
        this.delayed = new AtomicInteger(0);
        this.delaySubject = BehaviorSubject.createDefault(false);
    }

    /**
     * Creates new property with given default value
     *
     * @param defaultValue default value
     */
    public BehaviorProperty(final @Nonnull T defaultValue)
    {
        Objects.requireNonNull(defaultValue, "Default value cannot be null");

        this.value = new AtomicReference<>(defaultValue);
        this.subject = PublishSubject.create();
        this.readOnly = false;
        this.suppressed = new AtomicInteger(0);
        this.delayed = new AtomicInteger(0);
        this.delaySubject = BehaviorSubject.createDefault(false);
    }

    /**
     * Creates new property with observable bound to it
     *
     * @param observable observable
     */
    public BehaviorProperty(final @Nonnull Observable<? extends T> observable)
    {
        Objects.requireNonNull(observable, "Observable cannot be null");

        this.value = new AtomicReference<>();
        this.subject = PublishSubject.create();
        this.readOnly = true;
        this.suppressed = new AtomicInteger(0);
        this.delayed = new AtomicInteger(0);
        this.delaySubject = BehaviorSubject.createDefault(false);

        observable.subscribe(this::setValueInternal, this::setErrorInternal, this::setCompleteInternal);
    }

    @Override
    public final boolean hasValue()
    {
        return value.get() != null;
    }

    @Override
    public final boolean isReadOnly()
    {
        return Boolean.TRUE.equals(readOnly);
    }

    @Nullable
    @Override
    public final T getValue()
    {
        return value.get();
    }

    @Override
    public final void setValue(final @Nonnull T value)
    {
        Objects.requireNonNull(value, "Value cannot be null");

        if (isReadOnly())
        {
            throw new ReadOnlyPropertyException(this);
        }

        setValueInternal(value);
    }

    @Override
    public final void updateValue(final @Nonnull Function<? super T, ? extends T> update)
    {
        Objects.requireNonNull(update, "Update cannot be null");

        setValue(update.apply(getValue()));
    }

    @Nonnull
    @Override
    public final Observable<T> asObservable()
    {
        final Observable<T> observable = subject.publish(source -> {
            final PublishSubject<T> helper = PublishSubject.create();

            source.subscribe(helper::onNext, error -> {}, helper::onComplete);

            return helper
                    .filter(value -> !isSuppressed())
                    .buffer(Observable.merge(
                            source.filter(value -> !isDelayed()).map(value -> false),
                            delaySubject))
                    .filter(buffer -> buffer.size() > 0)
                    .map(buffer -> buffer.get(buffer.size() - 1));
        });

        return hasValue() ? observable.startWith(value.get()) : observable;
    }

    @Override
    public final boolean isSuppressed()
    {
        return suppressed.get() > 0;
    }

    @Nonnull
    @Override
    public final Disposable suppress()
    {
        suppressed.incrementAndGet();

        return Disposables.fromRunnable(suppressed::decrementAndGet);
    }

    @Override
    public final boolean isDelayed()
    {
        return delayed.get() > 0;
    }

    @Nonnull
    @Override
    public final Disposable delay()
    {
        if (delayed.incrementAndGet() == 1)
        {
            delaySubject.onNext(true);
        }

        return Disposables.fromRunnable(() -> {
            if (delayed.decrementAndGet() == 0)
            {
                delaySubject.onNext(false);
            }
        });
    }

    private void setValueInternal(final @Nonnull T value)
    {
        this.value.set(value);
        this.subject.onNext(value);
    }

    private void setErrorInternal(final @Nonnull Throwable error)
    {
        this.value.set(null);
        this.subject.onError(error);
    }

    private void setCompleteInternal()
    {
        this.value.set(null);
        this.subject.onComplete();
    }
}
