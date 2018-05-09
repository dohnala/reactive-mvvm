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

package com.github.dohnal.vaadin.reactive.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.dohnal.vaadin.reactive.ReactiveCommand;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Composite implementation of {@link ReactiveCommand}
 *
 * @param <T> type of command input parameter
 * @param <R> type of command result
 * @author dohnal
 */
public final class CompositeCommand<T, R> extends AbstractCommand<T, List<R>>
{
    private final List<ReactiveCommand<T, R>> commands;

    /**
     * Creates new composite reactive command from given child commands
     *
     * @param canExecute observable which controls command executability
     * @param commands child commands this command is composed from
     */
    public CompositeCommand(final @Nonnull Observable<Boolean> canExecute,
                            final @Nonnull List<ReactiveCommand<T, R>> commands)
    {
        super(Observable.combineLatest(
                canExecute.startWith(true).distinctUntilChanged(),
                Observable.combineLatest(
                        commands.stream()
                                .map(command -> command
                                        .canExecute()
                                        .startWith(true)
                                        .distinctUntilChanged())
                                .collect(Collectors.toList()),
                        values -> Arrays.stream(Arrays.copyOf(values, values.length, Boolean[].class))
                                .allMatch(Boolean.TRUE::equals)),
                (x, y) -> x && y));

        Objects.requireNonNull(canExecute, "CanExecute cannot be null");
        Objects.requireNonNull(commands, "Commands cannot be null");

        if (commands.size() == 0)
        {
            throw new IllegalArgumentException("At least one command is required");
        }

        this.commands = commands;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final void executeInternal(final @Nullable T input)
    {
        handleStart();

        // Zip child results and subscribe them to result subject
        final Disposable resultSubscription = Observable
                .zip(commands.stream()
                                .map(command -> command.getResult().take(1))
                                .collect(Collectors.toList()),
                        results -> Arrays.asList((R[]) results))
                .subscribe(result::onNext);

        // Merge child errors and subscribe them to error subject
        final Disposable errorSubscription = Observable
                .merge(commands.stream()
                        .map(command -> command.getError().take(1))
                        .collect(Collectors.toList()))
                .subscribe(error::onNext);

        // Compute progress from child commands and subscribe them to progress property
        final Disposable progressSubscription = Observable
                .combineLatest(commands.stream()
                                .map(command -> command.getProgress()
                                        .withLatestFrom(command.isExecuting().take(3), AbstractMap.SimpleImmutableEntry::new)
                                        .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                                        .map(AbstractMap.SimpleImmutableEntry::getKey)
                                        .startWith(0.0f))
                                .collect(Collectors.toList()),
                        values -> computeProgress(Arrays.copyOf(values, values.length, Float[].class)))
                .subscribe(progress::setValue);

        final Observable<?> execution = Observable.zip(commands.stream()
                .map(command -> command.isExecuting()
                        .filter(Boolean.FALSE::equals)
                        .skip(1)
                        .take(1))
                .collect(Collectors.toList()), values -> values);

        // After all child commands finished execution, handle complete
        final Disposable isExecutingSubscription = execution
                .subscribe(values -> {}, error -> {}, () -> {
                    resultSubscription.dispose();
                    errorSubscription.dispose();
                    progressSubscription.dispose();

                    this.handleComplete();
                });

        if (input == null)
        {
            commands.forEach(ReactiveCommand::execute);
        }
        else
        {
            commands.forEach(command -> command.execute(input));
        }
    }

    @Nonnull
    private Float computeProgress(final @Nonnull Float... values)
    {
        Objects.requireNonNull(values, "Values cannot be null");

        return Arrays.stream(values).reduce(0.0f, (x, y) -> x + y) / commands.size();
    }
}
