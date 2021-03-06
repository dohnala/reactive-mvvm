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

package org.vaadin.addons.reactive.command;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.reactive.ReactiveCommand;
import org.vaadin.addons.reactive.exceptions.CannotExecuteCommandException;

/**
 * @author dohnal
 */
public interface CanExecuteEmitsValueSpecification extends BaseCommandSpecification
{
    /**
     * Specification that tests behavior of command after it is created with custom CanExecute
     * which controls command executability
     *
     * @param <T> type of command input
     * @param <R> type of command result
     */
    abstract class AbstractCanExecuteEmitsValueSpecification<T, R> implements RequireCommand<T, R>
    {
        protected abstract void emitValue(final @Nonnull Boolean value);

        protected abstract void emitError(final @Nonnull Throwable error);

        @Nonnull
        protected abstract Observable<R> execute();

        @Nested
        @DisplayName("When CanExecute observable emits same value")
        class WhenCanExecuteEmitsSameValue
        {
            @BeforeEach
            void before()
            {
                emitValue(true);
            }

            @Test
            @DisplayName("CanExecute observable should not emit any value")
            public void testCanExecute()
            {
                final TestObserver<Boolean> testObserver = getCommand().canExecute().test();

                testObserver.assertValue(true);

                emitValue(true);

                testObserver.assertValue(true);
            }
        }

        @Nested
        @DisplayName("When CanExecute observable emits true")
        class WhenCanExecuteEmitsTrue
        {
            @BeforeEach
            void before()
            {
                emitValue(false);
            }

            @Test
            @DisplayName("CanExecute observable should emit true")
            public void testCanExecute()
            {
                final TestObserver<Boolean> testObserver = getCommand().canExecute().test();

                testObserver.assertValue(false);

                emitValue(true);

                testObserver.assertValues(false, true);
            }
        }

        @Nested
        @DisplayName("When CanExecute observable emits false")
        class WhenCanExecuteEmitsFalse
        {
            @BeforeEach
            void before()
            {
                emitValue(true);
            }

            @Test
            @DisplayName("CanExecute observable should emit false")
            public void testCanExecute()
            {
                final TestObserver<Boolean> testObserver = getCommand().canExecute().test();

                testObserver.assertValue(true);

                emitValue(false);

                testObserver.assertValues(true, false);
            }

            @Nested
            @DisplayName("After CanExecute observable emits false")
            class AfterCanExecuteEmitsFalse
            {
                @BeforeEach
                void before()
                {
                    emitValue(false);

                    getCommand().getError().test();
                }

                @Nested
                @DisplayName("When command is executed")
                class WhenExecute extends WhenExecuteWhileDisabledSpecification<T, R>
                {
                    @Nonnull
                    @Override
                    public ReactiveCommand<T, R> getCommand()
                    {
                        return AbstractCanExecuteEmitsValueSpecification.this.getCommand();
                    }

                    @Nonnull
                    @Override
                    protected Observable<R> execute()
                    {
                        return AbstractCanExecuteEmitsValueSpecification.this.execute();
                    }
                }
            }
        }

        @Nested
        @DisplayName("When CanExecute observable emits error")
        class WhenCanExecuteEmitsError
        {
            protected final Throwable ERROR = new RuntimeException("Error");

            @BeforeEach
            void before()
            {
                emitValue(true);
            }

            @Test
            @DisplayName("CanExecute observable should emit error")
            public void testCanExecute()
            {
                final TestObserver<Boolean> testObserver = getCommand().canExecute().test();

                testObserver.assertValue(true);

                emitError(ERROR);

                testObserver.assertValue(true);
                testObserver.assertError(ERROR);
            }

            @Nested
            @DisplayName("After CanExecute observable emits error")
            class AfterCanExecuteEmitsError
            {
                @BeforeEach
                void before()
                {
                    emitError(ERROR);

                    getCommand().getError().test();
                }

                @Nested
                @DisplayName("When command is executed")
                class WhenExecute extends WhenExecuteWhileDisabledSpecification<T, R>
                {
                    @Nonnull
                    @Override
                    public ReactiveCommand<T, R> getCommand()
                    {
                        return AbstractCanExecuteEmitsValueSpecification.this.getCommand();
                    }

                    @Nonnull
                    @Override
                    protected Observable<R> execute()
                    {
                        return AbstractCanExecuteEmitsValueSpecification.this.execute();
                    }

                    @Test
                    @Override
                    @DisplayName("CanExecute observable should emit error")
                    public void testCanExecute()
                    {
                        final TestObserver<Boolean> testObserver = getCommand().canExecute().test();

                        testObserver.assertError(ERROR);
                    }
                }
            }
        }
    }

    /**
     * Specification that tests behavior of command when executed while disabled (CanExecute is not true)
     *
     * @param <T> type of command input
     * @param <R> type of command result
     */
    abstract class WhenExecuteWhileDisabledSpecification<T, R> implements RequireCommand<T, R>
    {
        @Nonnull
        protected abstract Observable<R> execute();

        @Test
        @DisplayName("Result observable should not emit any value")
        public void testResult()
        {
            final TestObserver<R> testObserver = getCommand().getResult().test();

            execute().subscribe();

            testObserver.assertNoValues();
        }

        @Test
        @DisplayName("Error observable should emit CannotExecuteCommandException")
        public void testError()
        {
            final TestObserver<Throwable> testObserver = getCommand().getError().test();

            execute().subscribe();

            testObserver.assertValue(error ->
                    error.getClass().equals(CannotExecuteCommandException.class) &&
                            ((CannotExecuteCommandException) error).getCommand().equals(getCommand()));
        }

        @Test
        @DisplayName("CanExecute observable should not emit any value")
        public void testCanExecute()
        {
            final TestObserver<Boolean> testObserver = getCommand().canExecute().test();

            testObserver.assertValue(false);

            execute().subscribe();

            testObserver.assertValue(false);
        }

        @Test
        @DisplayName("IsExecuting observable should not emit any value")
        public void testIsExecuting()
        {
            final TestObserver<Boolean> testObserver = getCommand().isExecuting().test();

            testObserver.assertValue(false);

            execute().subscribe();

            testObserver.assertValue(false);
        }

        @Test
        @DisplayName("ExecutionCount observable should not emit any value")
        public void testExecutionCount()
        {
            final TestObserver<Integer> testObserver = getCommand().getExecutionCount().test();

            testObserver.assertValue(0);

            execute().subscribe();

            testObserver.assertValue(0);
        }

        @Test
        @DisplayName("HasBeenExecuted observable should not emit any value")
        public void testHasBeenExecuted()
        {
            final TestObserver<Boolean> testObserver = getCommand().hasBeenExecuted().test();

            testObserver.assertValue(false);

            execute().subscribe();

            testObserver.assertValue(false);
        }

        @Test
        @DisplayName("Progress observable should not emit any value")
        public void testProgress()
        {
            final TestObserver<Float> testObserver = getCommand().getProgress().test();

            testObserver.assertValue(0.0f);

            execute().subscribe();

            testObserver.assertValue(0.0f);
        }
    }
}
