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

package org.vaadin.addons.reactive.command.create;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vaadin.addons.reactive.ReactiveCommand;
import org.vaadin.addons.reactive.ReactiveCommandExtension;
import org.vaadin.addons.reactive.command.CanExecuteEmitsValueSpecification;
import org.vaadin.addons.reactive.command.CreateSpecification;
import org.vaadin.addons.reactive.command.ExecuteSpecification;

/**
 * Specification for {@link ReactiveCommand} created by
 * {@link ReactiveCommandExtension#createCommandFromConsumer(Consumer)}
 * {@link ReactiveCommandExtension#createCommandFromConsumer(Observable, Consumer)}
 * {@link ReactiveCommandExtension#createCommandFromConsumer(Consumer, Scheduler)}
 * {@link ReactiveCommandExtension#createCommandFromConsumer(Observable, Consumer, Scheduler)}
 *
 * @author dohnal
 */
public interface FromConsumerSpecification extends
        CreateSpecification,
        ExecuteSpecification,
        CanExecuteEmitsValueSpecification
{
    abstract class AbstractFromConsumerSpecification extends AbstractCreateSpecification<Integer, Void>
            implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;
        protected Consumer<Integer> execution;
        protected ReactiveCommand<Integer, Void> command;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();
            execution = Mockito.mock(Consumer.class);
            command = createCommandFromConsumer(execution);
        }

        @Nonnull
        @Override
        public <T, R> ReactiveCommand<T, R> onCreateCommand(final @Nonnull ReactiveCommand<T, R> command)
        {
            final ReactiveCommand<T, R> created = ReactiveCommandExtension.super.onCreateCommand(command);

            capturedCommands.onNext(created);

            return created;
        }

        @Test
        @DisplayName("Created command should be captured")
        public void testCreatedCommand()
        {
            capturedCommands.test().assertValue(command);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Integer, Void> getCommand()
        {
            return command;
        }

        @Test
        @DisplayName("Execution should not be run")
        public void testExecution()
        {
            Mockito.verify(execution, Mockito.never()).accept(Mockito.anyInt());
        }

        @Nested
        @DisplayName("When command is executed with no input")
        class ExecuteWithNoInput
        {
            @Test
            @DisplayName("Error observable shout emit NullPointerException")
            public void testExecute()
            {
                final TestObserver<Throwable> testObserver = command.getError().test();

                command.execute().subscribe();

                testObserver.assertValue(error -> error.getClass().equals(NullPointerException.class));
            }
        }

        @Nested
        @DisplayName("Execute specification")
        class Execute extends AbstractExecuteSpecification<Integer, Void>
        {
            private final Integer INPUT = 5;
            private final Throwable ERROR = new RuntimeException("Error");

            @Nonnull
            @Override
            public ReactiveCommand<Integer, Void> getCommand()
            {
                return command;
            }

            @Nonnull
            @Override
            protected Observable<Void> execute()
            {
                Mockito.doNothing().when(execution).accept(INPUT);

                return command.execute(INPUT);
            }

            @Nonnull
            @Override
            protected Observable<Void> executeWithError()
            {
                Mockito.doThrow(ERROR).when(execution).accept(INPUT);

                return command.execute(INPUT);
            }

            @Nullable
            @Override
            protected Void getResult()
            {
                return null;
            }

            @Nullable
            @Override
            protected Throwable getError()
            {
                return ERROR;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void clearExecution()
            {
                Mockito.clearInvocations(execution);
            }

            @Override
            protected void verifyExecution()
            {
                Mockito.verify(execution).accept(INPUT);
            }
        }
    }

    abstract class AbstractFromConsumerWithSchedulerSpecification extends AbstractFromConsumerSpecification
    {
        @Override
        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();
            execution = Mockito.mock(Consumer.class);
            command = createCommandFromConsumer(execution, Schedulers.from(Runnable::run));
        }
    }

    abstract class AbstractFromConsumerWithCanExecuteSpecification
            extends AbstractCreateSpecification<Integer, Void> implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;
        protected Consumer<Integer> execution;
        protected TestScheduler testScheduler;
        protected PublishSubject<Boolean> testSubject;
        protected ReactiveCommand<Integer, Void> command;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();
            execution = Mockito.mock(Consumer.class);
            testScheduler = new TestScheduler();
            testSubject = PublishSubject.create();
            testSubject.observeOn(testScheduler);
            command = createCommandFromConsumer(testSubject, execution);
        }

        @Nonnull
        @Override
        public <T, R> ReactiveCommand<T, R> onCreateCommand(final @Nonnull ReactiveCommand<T, R> command)
        {
            final ReactiveCommand<T, R> created = ReactiveCommandExtension.super.onCreateCommand(command);

            capturedCommands.onNext(created);

            return created;
        }

        @Test
        @DisplayName("Created command should be captured")
        public void testCreatedCommand()
        {
            capturedCommands.test().assertValue(command);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Integer, Void> getCommand()
        {
            return command;
        }

        @Nested
        @DisplayName("CanExecute emits value specification")
        class CanExecuteEmitsValue extends AbstractCanExecuteEmitsValueSpecification<Integer, Void>
        {
            private final Integer INPUT = 5;

            @Nonnull
            @Override
            public ReactiveCommand<Integer, Void> getCommand()
            {
                return command;
            }

            @Override
            protected void emitValue(final @Nonnull Boolean value)
            {
                testSubject.onNext(value);
                testScheduler.triggerActions();
            }

            @Override
            protected void emitError(final @Nonnull Throwable error)
            {
                testSubject.onError(error);
                testScheduler.triggerActions();
            }

            @Nonnull
            @Override
            protected Observable<Void> execute()
            {
                Mockito.doNothing().when(execution).accept(INPUT);

                return command.execute(INPUT);
            }
        }
    }

    abstract class AbstractFromConsumerWithCanExecuteAndSchedulerSpecification
            extends AbstractFromConsumerWithCanExecuteSpecification
    {
        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();
            execution = Mockito.mock(Consumer.class);
            testScheduler = new TestScheduler();
            testSubject = PublishSubject.create();
            testSubject.observeOn(testScheduler);
            command = createCommandFromConsumer(testSubject, execution, Schedulers.from(Runnable::run));
        }
    }
}

