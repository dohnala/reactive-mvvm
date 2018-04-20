package com.github.dohnal.vaadin.reactive.command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.dohnal.vaadin.reactive.ReactiveCommand;
import rx.Observable;
import rx.Subscription;

import static com.google.common.base.Preconditions.checkArgument;

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
                canExecute,
                Observable.combineLatest(
                        commands.stream()
                                .map(ReactiveCommand::canExecute)
                                .collect(Collectors.toList()),
                        values -> Arrays.stream(Arrays.copyOf(values, values.length, Boolean[].class))
                                .allMatch(Boolean.TRUE::equals)),
                (x, y) -> x && y));

        checkArgument(commands.size() > 0, "At least one command is required");

        this.commands = commands;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected final void executeInternal(final @Nullable T input)
    {
        handleStart();

        // Zip child results and subscribe them to result subject
        final Subscription resultSubscription = Observable
                .zip(commands.stream()
                                .map(command -> command.getResult().take(1))
                                .collect(Collectors.toList()),
                        results -> Arrays.asList((R[]) results))
                .subscribe(result);

        // Merge child errors and subscribe them to error subject
        final Subscription errorSubscription = Observable
                .merge(commands.stream()
                        .map(command -> command.getError().take(1))
                        .collect(Collectors.toList()))
                .subscribe(error);

        // Compute progress from child commands and subscribe them to progress property
        final Subscription progressSubscription = Observable
                .combineLatest(commands.stream()
                                .map(command -> command.getProgress()
                                        .withLatestFrom(command.isExecuting().take(3), AbstractMap.SimpleImmutableEntry::new)
                                        .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                                        .map(AbstractMap.SimpleImmutableEntry::getKey)
                                        .startWith(0.0f))
                                .collect(Collectors.toList()),
                        values -> computeProgress(Arrays.copyOf(values, values.length, Float[].class)))
                .subscribe(progress::setValue);

        // After all child commands finished execution, handle complete
        final Subscription isExecutingSubscription = Observable.zip(commands.stream()
                .map(command -> command.isExecuting()
                        .filter(Boolean.FALSE::equals)
                        .skip(1)
                        .take(1))
                .collect(Collectors.toList()), values -> values)
                .subscribe(values -> {}, error -> {}, () -> {
                    resultSubscription.unsubscribe();
                    errorSubscription.unsubscribe();
                    progressSubscription.unsubscribe();

                    this.handleComplete();
                });

        commands.forEach(command -> command.execute(input));
    }

    @Nonnull
    private Float computeProgress(final @Nonnull Float... values)
    {
        return Arrays.stream(values).reduce(0.0f, (x, y) -> x + y) / commands.size();
    }
}
