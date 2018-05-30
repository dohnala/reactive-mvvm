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

package org.vaadin.addons.reactive.mvvm.binder;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

import io.reactivex.disposables.Disposable;
import org.vaadin.addons.reactive.ObservableProperty;
import org.vaadin.addons.reactive.ObservablePropertyBinder;
import org.vaadin.addons.reactive.binder.ObservablePropertyBinderDecorator;

/**
 * Decorator which binds observable properties with UI access
 *
 * @param <T> type of value
 * @author dohnal
 */
public final class UIObservablePropertyBinder<T> extends ObservablePropertyBinderDecorator<T>
{
    private final Consumer<Runnable> withUIAccess;

    public UIObservablePropertyBinder(final @Nonnull Consumer<Runnable> withUIAccess,
                                      final @Nonnull ObservablePropertyBinder<T> binder)
    {
        super(binder);

        Objects.requireNonNull(withUIAccess, "With UI access cannot be null");

        this.withUIAccess = withUIAccess;
    }

    @Nonnull
    @Override
    public final ObservableProperty<T> getProperty()
    {
        return new UIObservableProperty<>(withUIAccess, super.getProperty());
    }

    @Nonnull
    @Override
    public Disposable to(final @Nonnull ObservableProperty<T> anotherProperty)
    {
        return super.to(new UIObservableProperty<>(withUIAccess, anotherProperty));
    }
}
