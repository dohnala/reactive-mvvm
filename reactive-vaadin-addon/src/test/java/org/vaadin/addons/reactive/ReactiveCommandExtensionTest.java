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

package org.vaadin.addons.reactive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.vaadin.addons.reactive.command.create.CompositeFromCommandsSpecification;
import org.vaadin.addons.reactive.command.create.FromConsumerSpecification;
import org.vaadin.addons.reactive.command.create.FromFunctionSpecification;
import org.vaadin.addons.reactive.command.create.FromObservableFunctionSpecification;
import org.vaadin.addons.reactive.command.create.FromObservableSupplierSpecification;
import org.vaadin.addons.reactive.command.create.FromRunnableSpecification;
import org.vaadin.addons.reactive.command.create.FromSupplierSpecification;
import org.vaadin.addons.reactive.command.create.ProgressFromBiConsumerSpecification;
import org.vaadin.addons.reactive.command.create.ProgressFromBiFunctionSpecification;
import org.vaadin.addons.reactive.command.create.ProgressFromConsumerSpecification;
import org.vaadin.addons.reactive.command.create.ProgressFromFunctionSpecification;
import org.vaadin.addons.reactive.command.create.ProgressFromObservableBiFunctionSpecification;
import org.vaadin.addons.reactive.command.create.ProgressFromObservableFunctionSpecification;

/**
 * Tests for {@link ReactiveProperty}
 *
 * @author dohnal
 */
@DisplayName("Reactive command extension specification")
public class ReactiveCommandExtensionTest
{
    @Nested
    @DisplayName("When new reactive command is created")
    class WhenCreate implements
            FromRunnableSpecification,
            FromSupplierSpecification,
            FromConsumerSpecification,
            FromFunctionSpecification,
            FromObservableSupplierSpecification,
            FromObservableFunctionSpecification
    {
        @Nested
        @DisplayName("From runnable")
        class FromRunnable extends AbstractFromRunnableSpecification {}

        @Nested
        @DisplayName("From runnable with scheduler")
        class FromRunnableWithScheduler extends AbstractFromRunnableWithSchedulerSpecification {}

        @Nested
        @DisplayName("From runnable with CanExecute")
        class FromRunnableWithCanExecute extends AbstractFromRunnableWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From runnable with CanExecute and scheduler")
        class FromRunnableWithCanExecuteAndScheduler extends
                AbstractFromRunnableWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From supplier")
        class FromSupplier extends AbstractFromSupplierSpecification {}

        @Nested
        @DisplayName("From supplier with scheduler")
        class FromSupplierWithScheduler extends AbstractFromSupplierWithSchedulerSpecification {}

        @Nested
        @DisplayName("From supplier with CanExecute")
        class FromSupplierWithCanExecute extends AbstractFromSupplierWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From supplier with CanExecute and scheduler")
        class FromSupplierWithCanExecuteAndScheduler extends
                AbstractFromSupplierWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From consumer")
        class FromConsumer extends AbstractFromConsumerSpecification {}

        @Nested
        @DisplayName("From consumer with scheduler")
        class FromConsumerWithScheduler extends AbstractFromConsumerWithSchedulerSpecification {}

        @Nested
        @DisplayName("From consumer with CanExecute")
        class FromConsumerWithCanExecute extends AbstractFromConsumerWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From consumer with CanExecute and scheduler")
        class FromConsumerWithCanExecuteAndScheduler extends
                AbstractFromConsumerWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From function")
        class FromFunction extends AbstractFromFunctionSpecification {}

        @Nested
        @DisplayName("From function with scheduler")
        class FromFunctionWithScheduler extends AbstractFromFunctionWithSchedulerSpecification {}

        @Nested
        @DisplayName("From function with CanExecute")
        class FromFunctionWithCanExecute extends AbstractFromFunctionWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From function with CanExecute and scheduler")
        class FromFunctionWithCanExecuteAndScheduler extends
                AbstractFromFunctionWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From observable supplier")
        class FromObservableSupplier extends AbstractFromObservableSupplierSpecification {}

        @Nested
        @DisplayName("From observable supplier with scheduler")
        class FromObservableSupplierWithScheduler extends AbstractFromObservableSupplierWithSchedulerSpecification {}

        @Nested
        @DisplayName("From observable supplier with CanExecute")
        class FromObservableSupplierWithCanExecute extends AbstractFromObservableSupplierWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From observable supplier with CanExecute and scheduler")
        class FromObservableSupplierWithCanExecuteAndScheduler extends
                AbstractFromObservableSupplierWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From observable function")
        class FromObservableFunction extends AbstractFromObservableFunctionSpecification {}

        @Nested
        @DisplayName("From observable function with scheduler")
        class FromObservableFunctionWithScheduler extends AbstractFromObservableFunctionWithSchedulerSpecification {}

        @Nested
        @DisplayName("From observable function with CanExecute")
        class FromObservableFunctionWithCanExecute extends AbstractFromObservableFunctionWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From observable function with CanExecute and scheduler")
        class FromObservableFunctionWithCanExecuteAndScheduler extends
                AbstractFromObservableFunctionWithCanExecuteAndSchedulerSpecification {}
    }

    @Nested
    @DisplayName("When new reactive progress command is created")
    class WhenCreateProgress implements
            ProgressFromConsumerSpecification,
            ProgressFromFunctionSpecification,
            ProgressFromBiConsumerSpecification,
            ProgressFromBiFunctionSpecification,
            ProgressFromObservableFunctionSpecification,
            ProgressFromObservableBiFunctionSpecification
    {
        @Nested
        @DisplayName("From consumer with scheduler")
        class FromConsumerWithScheduler extends AbstractProgressFromConsumerWithSchedulerSpecification {}

        @Nested
        @DisplayName("From consumer with CanExecute and scheduler")
        class FromConsumerWithCanExecuteAndScheduler extends
                AbstractProgressFromConsumerWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From function with scheduler")
        class FromFunctionWithScheduler extends AbstractProgressFromFunctionWithSchedulerSpecification {}

        @Nested
        @DisplayName("From function with CanExecute and scheduler")
        class FromFunctionWithCanExecuteAndScheduler extends
                AbstractProgressFromFunctionWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From bi-consumer with scheduler")
        class FromBiConsumerWithScheduler extends AbstractProgressFromBiConsumerWithSchedulerSpecification {}

        @Nested
        @DisplayName("From bi-consumer with CanExecute and scheduler")
        class FromBiConsumerWithCanExecuteAndScheduler extends
                AbstractProgressFromBiConsumerWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From bi-function with scheduler")
        class FromBiFunctionWithScheduler extends AbstractProgressFromBiFunctionWithSchedulerSpecification {}

        @Nested
        @DisplayName("From bi-function with CanExecute and scheduler")
        class FromBiFunctionWithCanExecuteAndScheduler extends
                AbstractProgressFromBiFunctionWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From observable function with scheduler")
        class FromObservableFunctionWithScheduler extends
                AbstractProgressFromObservableFunctionWithSchedulerSpecification {}

        @Nested
        @DisplayName("From observable function with CanExecute and scheduler")
        class FromObservableFunctionWithCanExecuteAndScheduler extends
                AbstractProgressFromObservableFunctionWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From observable bi-function with scheduler")
        class FromObservableBiFunctionWithScheduler extends
                AbstractProgressFromObservableBiFunctionWithSchedulerSpecification {}

        @Nested
        @DisplayName("From observable bi-function with CanExecute and scheduler")
        class FromObservableBiFunctionWithCanExecuteAndScheduler extends
                AbstractProgressFromObservableBiFunctionWithCanExecuteAndSchedulerSpecification {}
    }

    @Nested
    @DisplayName("When new reactive composite command is created")
    class WhenCreateComposite implements CompositeFromCommandsSpecification
    {
        @Nested
        @DisplayName("From no commands")
        class FromNoCommands extends AbstractCompositeFromNoCommandsSpecification {}

        @Nested
        @DisplayName("From no commands with scheduler")
        class FromNoCommandsWithScheduler extends AbstractCompositeFromNoCommandsWithSchedulerSpecification {}

        @Nested
        @DisplayName("From no commands with CanExecute")
        class FromNoCommandsWithCanExecute extends AbstractCompositeFromNoCommandsWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From no commands with CanExecute and scheduler")
        class FromNoCommandsWithCanExecuteAndScheduler extends
                AbstractCompositeFromNoCommandsWithCanExecuteAndSchedulerSpecification {}

        @Nested
        @DisplayName("From commands with no input")
        class FromCommandsWithNoInput extends AbstractCompositeFromCommandsWithNoInputSpecification {}

        @Nested
        @DisplayName("From commands with no input and scheduler")
        class FromCommandsWithNoInputAndScheduler extends
                AbstractCompositeFromCommandsWithNoInputAndSchedulerSpecification {}

        @Nested
        @DisplayName("From commands with no result")
        class FromCommandsWithNoResult extends AbstractCompositeFromCommandsWithNoResultSpecification {}

        @Nested
        @DisplayName("From commands with no result and scheduler")
        class FromCommandsWithNoResultAndSchduler extends
                AbstractCompositeFromCommandsWithNoResultAndSchedulerSpecification {}

        @Nested
        @DisplayName("From commands with CanExecute")
        class FromCommandsWithCanExecute extends AbstractCompositeFromCommandsWithCanExecuteSpecification {}

        @Nested
        @DisplayName("From commands with CanExecute and scheduler")
        class FromCommandsWithCanExecuteAndScheduler extends
                AbstractCompositeFromCommandsWithCanExecuteAndSchedulerSpecification {}
    }
}
