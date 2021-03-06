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

package org.vaadin.addons.reactive.interaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.vaadin.addons.reactive.InteractionContext;
import org.vaadin.addons.reactive.ReactiveInteraction;
import org.vaadin.addons.reactive.exceptions.AlreadyHandledInteractionException;
import org.vaadin.addons.reactive.exceptions.UnhandledInteractionException;

/**
 * Basic implementation of {@link ReactiveInteraction} based on {@link PublishSubject}
 *
 * @param <T> type of input
 * @param <R> type of result
 * @author dohnal
 */
public final class PublishSubjectInteraction<T, R> implements ReactiveInteraction<T, R>
{
    private final PublishSubject<InteractionContext<T, R>> subject;

    /**
     * Creates new interaction
     */
    public PublishSubjectInteraction()
    {
        this.subject = PublishSubject.create();
    }

    @Override
    public void invoke(final @Nonnull Runnable action)
    {
        Objects.requireNonNull(action, "Action cannot be null");

        invokeInternal(new DefaultInteractionContext(null, result -> action.run()));
    }

    @Override
    public void invoke(final @Nonnull Consumer<? super R> action)
    {
        Objects.requireNonNull(action, "Action cannot be null");

        invokeInternal(new DefaultInteractionContext(null, result -> {
            Objects.requireNonNull(result, "Result cannot be null");

            action.accept(result);
        }));
    }

    @Override
    public void invoke(final @Nonnull T input, final @Nonnull Runnable action)
    {
        Objects.requireNonNull(input, "Input cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        invokeInternal(new DefaultInteractionContext(input, result -> action.run()));
    }

    @Override
    public void invoke(final @Nonnull T input, final @Nonnull Consumer<? super R> action)
    {
        Objects.requireNonNull(input, "Input cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        invokeInternal(new DefaultInteractionContext(input, result -> {
            Objects.requireNonNull(result, "Result cannot be null");

            action.accept(result);
        }));
    }

    @Nonnull
    @Override
    public Observable<InteractionContext<T, R>> asObservable()
    {
        return subject;
    }

    private void invokeInternal(final @Nonnull InteractionContext<T, R> interactionContext)
    {
        Objects.requireNonNull(interactionContext, "Interaction context cannot be null");

        if (!subject.hasObservers())
        {
            throw new UnhandledInteractionException(this);
        }

        subject.onNext(interactionContext);
    }

    private class DefaultInteractionContext implements InteractionContext<T, R>
    {
        private final T input;

        private final Consumer<? super R> action;

        private final AtomicReference<Boolean> isHandled;

        public DefaultInteractionContext(final @Nullable T input,
                                         final @Nonnull Consumer<? super R> action)
        {
            Objects.requireNonNull(action, "Action cannot be null");

            this.input = input;
            this.action = action;
            this.isHandled = new AtomicReference<>(false);
        }

        @Nullable
        @Override
        public T getInput()
        {
            return input;
        }

        @Override
        public boolean isHandled()
        {
            return Boolean.TRUE.equals(isHandled.get());
        }

        @Override
        public void handle()
        {
            handleInternal(null);
        }

        @Override
        public void handle(final @Nonnull R result)
        {
            Objects.requireNonNull(result, "Result cannot be null");

            handleInternal(result);
        }

        private void handleInternal(final @Nullable R result)
        {
            if (isHandled.compareAndSet(false, true))
            {
                action.accept(result);
            }
            else
            {
                throw new AlreadyHandledInteractionException(PublishSubjectInteraction.this);
            }
        }
    }
}
