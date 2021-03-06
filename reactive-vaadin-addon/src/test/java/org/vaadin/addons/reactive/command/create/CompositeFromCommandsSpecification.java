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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
import org.vaadin.addons.reactive.command.CompositeCanExecuteEmitsValueSpecification;
import org.vaadin.addons.reactive.command.CompositeExecutionSpecification;
import org.vaadin.addons.reactive.command.CreateSpecification;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Specification for {@link ReactiveCommand} created by
 * {@link ReactiveCommandExtension#createCompositeCommand(List)}
 * {@link ReactiveCommandExtension#createCompositeCommand(Observable, List)}
 * {@link ReactiveCommandExtension#createCompositeCommand(List, Scheduler)}
 * {@link ReactiveCommandExtension#createCompositeCommand(Observable, List, Scheduler)}
 *
 * @author dohnal
 */
public interface CompositeFromCommandsSpecification extends
        CreateSpecification,
        CompositeExecutionSpecification,
        CompositeCanExecuteEmitsValueSpecification
{
    abstract class AbstractCompositeFromNoCommandsSpecification implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        @BeforeEach
        void before()
        {
            capturedCommands = ReplaySubject.create();
        }

        @Test
        @DisplayName("IllegalArgumentException should be thrown")
        public void testCreate()
        {
            assertThrows(IllegalArgumentException.class, () -> createCompositeCommand(new ArrayList<>()));
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
        @DisplayName("Created command should not be captured")
        public void testCreatedCommand()
        {
            capturedCommands.test().assertNoValues();
        }
    }

    abstract class AbstractCompositeFromNoCommandsWithCanExecuteSpecification implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        @BeforeEach
        void before()
        {
            capturedCommands = ReplaySubject.create();
        }

        @Test
        @DisplayName("IllegalArgumentException should be thrown")
        public void testCreate()
        {
            assertThrows(IllegalArgumentException.class, () -> createCompositeCommand(
                    PublishSubject.create(), new ArrayList<>()));
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
        @DisplayName("Created command should not be captured")
        public void testCreatedCommand()
        {
            capturedCommands.test().assertNoValues();
        }
    }

    abstract class AbstractCompositeFromNoCommandsWithSchedulerSpecification implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        @BeforeEach
        void before()
        {
            capturedCommands = ReplaySubject.create();
        }

        @Test
        @DisplayName("IllegalArgumentException should be thrown")
        public void testCreate()
        {
            assertThrows(IllegalArgumentException.class, () ->
                    createCompositeCommand(new ArrayList<>(), Schedulers.from(Runnable::run)));
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
        @DisplayName("Created command should not be captured")
        public void testCreatedCommand()
        {
            capturedCommands.test().assertNoValues();
        }
    }

    abstract class AbstractCompositeFromNoCommandsWithCanExecuteAndSchedulerSpecification
            implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        @BeforeEach
        void before()
        {
            capturedCommands = ReplaySubject.create();
        }

        @Test
        @DisplayName("IllegalArgumentException should be thrown")
        public void testCreate()
        {
            assertThrows(IllegalArgumentException.class, () -> createCompositeCommand(
                    PublishSubject.create(), new ArrayList<>(), Schedulers.from(Runnable::run)));
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
        @DisplayName("Created command should not be captured")
        public void testCreatedCommand()
        {
            capturedCommands.test().assertNoValues();
        }
    }

    abstract class AbstractCompositeFromCommandsWithNoInputSpecification extends
            AbstractCreateSpecification<Void, List<Integer>> implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        protected Supplier<Integer> executionA;
        protected ReactiveCommand<Void, Integer> commandA;

        protected Supplier<Integer> executionB;
        protected ReactiveCommand<Void, Integer> commandB;

        protected ReactiveCommand<Void, List<Integer>> command;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();

            executionA = Mockito.mock(Supplier.class);
            commandA = createCommandFromSupplier(executionA);

            executionB = Mockito.mock(Supplier.class);
            commandB = createCommandFromSupplier(executionB);

            command = createCompositeCommand(Arrays.asList(commandA, commandB));
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
            capturedCommands.test().assertValues(commandA, commandB, command);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Void, List<Integer>> getCommand()
        {
            return command;
        }

        @Test
        @DisplayName("Child commands should not be executed")
        public void testChildCommands()
        {
            Mockito.verify(executionA, Mockito.never()).get();
            Mockito.verify(executionB, Mockito.never()).get();
        }

        @Nested
        @DisplayName("Execute specification")
        class Execute extends AbstractCompositeExecuteSpecification<Void, List<Integer>>
        {
            private final Integer RESULT_A = 7;
            private final Integer RESULT_B = 9;
            private final List<Integer> RESULT = Arrays.asList(RESULT_A, RESULT_B);
            private final Throwable ERROR_A = new RuntimeException("Error A");
            private final Throwable ERROR_B = new RuntimeException("Error B");

            @Nonnull
            @Override
            public ReactiveCommand<Void, List<Integer>> getCommand()
            {
                return command;
            }

            @Nonnull
            @Override
            protected Observable<List<Integer>> execute()
            {
                Mockito.when(executionA.get()).thenReturn(RESULT_A);
                Mockito.when(executionB.get()).thenReturn(RESULT_B);

                return command.execute();
            }

            @Nonnull
            @Override
            protected Observable<List<Integer>> executeWithError()
            {
                Mockito.when(executionA.get()).thenThrow(ERROR_A);
                Mockito.when(executionB.get()).thenReturn(RESULT_B);

                return command.execute();
            }

            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            protected Observable<Integer> executeChild()
            {
                Mockito.reset(executionA);
                Mockito.when(executionA.get()).thenReturn(RESULT_A);

                return commandA.execute();
            }

            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            protected Observable<Integer> executeChildWithError()
            {
                Mockito.reset(executionA);
                Mockito.when(executionA.get()).thenThrow(ERROR_A);

                commandA.getError().test();

                return commandA.execute();
            }

            @Nullable
            @Override
            protected List<Integer> getResult()
            {
                return RESULT;
            }

            @Nullable
            @Override
            protected Throwable getError()
            {
                return ERROR_A;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void clearExecution()
            {
                Mockito.clearInvocations(executionA, executionB);
            }

            @Override
            protected void verifyExecution()
            {
                Mockito.verify(executionA).get();
                Mockito.verify(executionB).get();
            }

            @Override
            protected void verifyErrorExecution()
            {
                Mockito.verify(executionA).get();
                Mockito.verify(executionB, Mockito.never()).get();
            }
        }
    }

    abstract class AbstractCompositeFromCommandsWithNoInputAndSchedulerSpecification extends
            AbstractCompositeFromCommandsWithNoInputSpecification
    {
        @Override
        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();

            executionA = Mockito.mock(Supplier.class);
            commandA = createCommandFromSupplier(executionA);

            executionB = Mockito.mock(Supplier.class);
            commandB = createCommandFromSupplier(executionB);

            command = createCompositeCommand(Arrays.asList(commandA, commandB), Schedulers.from(Runnable::run));
        }
    }

    abstract class AbstractCompositeFromCommandsWithNoResultSpecification extends
            AbstractCreateSpecification<Integer, List<Void>> implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        protected Consumer<Integer> executionA;
        protected ReactiveCommand<Integer, Void> commandA;

        protected Consumer<Integer> executionB;
        protected ReactiveCommand<Integer, Void> commandB;

        protected ReactiveCommand<Integer, List<Void>> command;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();

            executionA = Mockito.mock(Consumer.class);
            commandA = createCommandFromConsumer(executionA);

            executionB = Mockito.mock(Consumer.class);
            commandB = createCommandFromConsumer(executionB);

            command = createCompositeCommand(Arrays.asList(commandA, commandB));
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
            capturedCommands.test().assertValues(commandA, commandB, command);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Integer, List<Void>> getCommand()
        {
            return command;
        }

        @Test
        @DisplayName("Child commands should not be executed")
        public void testChildCommands()
        {
            Mockito.verify(executionA, Mockito.never()).accept(Mockito.anyInt());
            Mockito.verify(executionB, Mockito.never()).accept(Mockito.anyInt());
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
        class Execute extends AbstractCompositeExecuteSpecification<Integer, List<Void>>
        {
            private final Integer INPUT = 5;
            private final Throwable ERROR_A = new RuntimeException("Error A");
            private final Throwable ERROR_B = new RuntimeException("Error B");

            @Nonnull
            @Override
            public ReactiveCommand<Integer, List<Void>> getCommand()
            {
                return command;
            }

            @Nonnull
            @Override
            protected Observable<List<Void>> execute()
            {
                Mockito.doNothing().when(executionA).accept(INPUT);
                Mockito.doNothing().when(executionB).accept(INPUT);

                return command.execute(INPUT);
            }

            @Nonnull
            @Override
            protected Observable<List<Void>> executeWithError()
            {
                Mockito.doThrow(ERROR_A).when(executionA).accept(INPUT);
                Mockito.doNothing().when(executionB).accept(INPUT);

                return command.execute(INPUT);
            }

            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            protected Observable<Void> executeChild()
            {
                Mockito.reset(executionA);
                Mockito.doNothing().when(executionA).accept(INPUT);

                return commandA.execute(INPUT);
            }

            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            protected Observable<Void> executeChildWithError()
            {
                Mockito.reset(executionA);
                Mockito.doThrow(ERROR_A).when(executionA).accept(INPUT);

                commandA.getError().test();

                return commandA.execute(INPUT);
            }

            @Nullable
            @Override
            protected List<Void> getResult()
            {
                return null;
            }

            @Nullable
            @Override
            protected Throwable getError()
            {
                return ERROR_A;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void clearExecution()
            {
                Mockito.clearInvocations(executionA, executionB);
            }

            @Override
            protected void verifyExecution()
            {
                Mockito.verify(executionA).accept(INPUT);
                Mockito.verify(executionB).accept(INPUT);
            }

            @Override
            protected void verifyErrorExecution()
            {
                Mockito.verify(executionA).accept(INPUT);
                Mockito.verify(executionB, Mockito.never()).accept(INPUT);
            }
        }
    }

    abstract class AbstractCompositeFromCommandsWithNoResultAndSchedulerSpecification extends
            AbstractCompositeFromCommandsWithNoResultSpecification
    {
        @Override
        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();

            executionA = Mockito.mock(Consumer.class);
            commandA = createCommandFromConsumer(executionA);

            executionB = Mockito.mock(Consumer.class);
            commandB = createCommandFromConsumer(executionB);

            command = createCompositeCommand(Arrays.asList(commandA, commandB), Schedulers.from(Runnable::run));
        }
    }

    abstract class AbstractCompositeFromCommandsWithCanExecuteSpecification
            extends AbstractCreateSpecification<Void, List<Integer>> implements ReactiveCommandExtension
    {
        protected ReplaySubject<ReactiveCommand<?, ?>> capturedCommands;

        protected Supplier<Integer> executionA;
        protected ReactiveCommand<Void, Integer> commandA;

        protected Supplier<Integer> executionB;
        protected ReactiveCommand<Void, Integer> commandB;

        protected TestScheduler testScheduler;
        protected PublishSubject<Boolean> testSubject;
        protected ReactiveCommand<Void, List<Integer>> command;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();

            executionA = Mockito.mock(Supplier.class);
            commandA = createCommandFromSupplier(executionA);

            executionB = Mockito.mock(Supplier.class);
            commandB = createCommandFromSupplier(executionB);

            testScheduler = new TestScheduler();
            testSubject = PublishSubject.create();
            testSubject.observeOn(testScheduler);
            command = createCompositeCommand(testSubject, Arrays.asList(commandA, commandB));
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
            capturedCommands.test().assertValues(commandA, commandB, command);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Void, List<Integer>> getCommand()
        {
            return command;
        }

        @Nested
        @DisplayName("CanExecute emits value specification")
        class CanExecuteEmitsValue extends AbstractCompositeCanExecuteEmitsValueSpecification<Void, List<Integer>>
        {
            private final Integer RESULT_A = 7;
            private final Integer RESULT_B = 9;
            private final List<Integer> RESULT = Arrays.asList(RESULT_A, RESULT_B);

            @Nonnull
            @Override
            public ReactiveCommand<Void, List<Integer>> getCommand()
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
            protected Observable<List<Integer>> execute()
            {
                Mockito.when(executionA.get()).thenReturn(RESULT_A);
                Mockito.when(executionB.get()).thenReturn(RESULT_B);

                return command.execute();
            }

            @Nonnull
            @Override
            protected Observable<Integer> executeChild()
            {
                Mockito.when(executionA.get()).thenReturn(RESULT_A);

                return commandA.execute();
            }
        }
    }

    abstract class AbstractCompositeFromCommandsWithCanExecuteAndSchedulerSpecification extends
            AbstractCompositeFromCommandsWithCanExecuteSpecification
    {
        @Override
        @BeforeEach
        @SuppressWarnings("unchecked")
        void create()
        {
            capturedCommands = ReplaySubject.create();

            executionA = Mockito.mock(Supplier.class);
            commandA = createCommandFromSupplier(executionA);

            executionB = Mockito.mock(Supplier.class);
            commandB = createCommandFromSupplier(executionB);

            testScheduler = new TestScheduler();
            testSubject = PublishSubject.create();
            testSubject.observeOn(testScheduler);
            command = createCompositeCommand(testSubject, Arrays.asList(commandA, commandB),
                    Schedulers.from(Runnable::run));
        }
    }
}
