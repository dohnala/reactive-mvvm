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

package com.github.dohnal.vaadin.reactive.exceptions;

import javax.annotation.Nonnull;
import java.util.Objects;

import com.github.dohnal.vaadin.reactive.ReactiveInteraction;

/**
 * Indicates that an interaction is unhandled because no handler is subscribed to it
 *
 * @author dohnal
 */
public class UnhandledInteractionException extends RuntimeException
{
    private final ReactiveInteraction<?, ?> interaction;

    public UnhandledInteractionException(final @Nonnull ReactiveInteraction<?, ?> interaction)
    {
        Objects.requireNonNull(interaction, "Interaction cannot be null");

        this.interaction = interaction;
    }

    /**
     * Returns interaction which this exception was thrown for
     *
     * @return interaction
     */
    @Nonnull
    public ReactiveInteraction<?, ?> getInteraction()
    {
        return interaction;
    }
}
