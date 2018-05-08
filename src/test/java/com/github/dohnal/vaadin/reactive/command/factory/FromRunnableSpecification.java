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

package com.github.dohnal.vaadin.reactive.command.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

import com.github.dohnal.vaadin.reactive.ReactiveCommand;
import com.github.dohnal.vaadin.reactive.ReactiveCommandFactory;
import com.github.dohnal.vaadin.reactive.command.CanExecuteEmitsValueSpecification;
import com.github.dohnal.vaadin.reactive.command.CreateSpecification;
import com.github.dohnal.vaadin.reactive.command.ExecuteSpecification;
import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Specification for {@link ReactiveCommand} created by
 * {@link ReactiveCommandFactory#createCommand(Runnable)}
 * {@link ReactiveCommandFactory#createCommand(Observable, Runnable)}
 * {@link ReactiveCommandFactory#createAsyncCommand(Runnable, Executor)}
 * {@link ReactiveCommandFactory#createAsyncCommand(Observable, Runnable, Executor)}
 *
 * @author dohnal
 */
public interface FromRunnableSpecification extends
        CreateSpecification,
        ExecuteSpecification,
        CanExecuteEmitsValueSpecification
{
    abstract class AbstractFromRunnableSpecification extends AbstractCreateSpecification<Void, Void>
            implements ReactiveCommandFactory
    {
        protected Runnable execution;
        protected ReactiveCommand<Void, Void> command;

        @BeforeEach
        void create()
        {
            execution = Mockito.mock(Runnable.class);
            command = createCommand(execution);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Void, Void> getCommand()
        {
            return command;
        }

        @Test
        @DisplayName("Execution should not be run")
        public void testExecution()
        {
            Mockito.verify(execution, Mockito.never()).run();
        }

        @Nested
        @DisplayName("Execute specification")
        class Execute extends AbstractExecuteSpecification<Void, Void>
        {
            private final Throwable ERROR = new RuntimeException("Error");

            @Nonnull
            @Override
            public ReactiveCommand<Void, Void> getCommand()
            {
                return command;
            }

            @Override
            protected void execute()
            {
                Mockito.doNothing().when(execution).run();
                command.execute();
            }

            @Override
            protected void executeWithError()
            {
                Mockito.doThrow(ERROR).when(execution).run();
                command.execute();
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
            protected void verifyExecution()
            {
                Mockito.verify(execution).run();
            }
        }
    }

    abstract class AbstractFromRunnableWithExecutorSpecification extends AbstractFromRunnableSpecification
    {
        @Override
        @BeforeEach
        void create()
        {
            execution = Mockito.mock(Runnable.class);
            command = createAsyncCommand(execution, new TestExecutor());
        }
    }

    abstract class AbstractFromRunnableWithCanExecuteSpecification
            extends AbstractCreateSpecification<Void, Void> implements ReactiveCommandFactory
    {
        protected Runnable execution;
        protected TestScheduler testScheduler;
        protected PublishSubject<Boolean> testSubject;
        protected ReactiveCommand<Void, Void> command;

        @BeforeEach
        void create()
        {
            execution = Mockito.mock(Runnable.class);
            testScheduler = new TestScheduler();
            testSubject = PublishSubject.create();
            testSubject.observeOn(testScheduler);
            command = createCommand(testSubject, execution);
        }

        @Nonnull
        @Override
        public ReactiveCommand<Void, Void> getCommand()
        {
            return command;
        }

        @Nested
        @DisplayName("CanExecute emits value specification")
        class CanExecuteEmitsValue extends AbstractCanExecuteEmitsValueSpecification<Void, Void>
        {
            @Nonnull
            @Override
            public ReactiveCommand<Void, Void> getCommand()
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
            protected void execute()
            {
                Mockito.doNothing().when(execution).run();
                command.execute();
            }
        }
    }

    abstract class AbstractFromRunnableWithCanExecuteAndExecutorSpecification
            extends AbstractFromRunnableWithCanExecuteSpecification
    {
        @BeforeEach
        void create()
        {
            execution = Mockito.mock(Runnable.class);
            testScheduler = new TestScheduler();
            testSubject = PublishSubject.create();
            testSubject.observeOn(testScheduler);
            command = createAsyncCommand(testSubject, execution, new TestExecutor());
        }
    }
}
