package org.vaadin.addons.reactive.demo.screen;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import io.reactivex.Observable;
import org.vaadin.addons.reactive.mvvm.ReactiveView;
import org.vaadin.addons.reactive.mvvm.ReactiveViewModel;

/**
 * @author dohnal
 */
@SpringView(name = ReactiveBinderScreen.SCREEN_NAME)
public class ReactiveBinderScreen extends AbstractDemoScreen
{
    public static final String SCREEN_NAME = "binder";

    public ReactiveBinderScreen()
    {
        super("demo/reactive_binder.yaml");

        addSection("section1", createSection1());
        addSection("section2", createSection2());
        addSection("section3", createSection3());
        addSection("section4", createSection4());
    }

    @Nonnull
    private ReactiveView createSection1()
    {
        class DemoViewModel extends ReactiveViewModel
        {
            private final Observable<String> observable;

            public DemoViewModel()
            {
                this.observable = Observable.just("Hello Reactive Vaadin");
            }

            @Nonnull
            public Observable<String> getObservable()
            {
                return observable;
            }
        }

        class DemoView extends ReactiveView<DemoViewModel>
        {
            @Override
            protected void initView(final @Nonnull DemoViewModel viewModel)
            {
                final Label label = new Label();

                bind(valueOf(label)).to(viewModel.getObservable());

                setCompositionRoot(label);
            }
        }

        return new DemoView().withViewModel(new DemoViewModel());
    }

    @Nonnull
    private ReactiveView createSection2()
    {
        class DemoViewModel extends ReactiveViewModel
        {
            private final Observable<LocalDateTime> observable;

            public DemoViewModel()
            {
                this.observable = Observable.interval(1, TimeUnit.SECONDS)
                        .map(value -> LocalDateTime.now());
            }

            @Nonnull
            public Observable<LocalDateTime> getObservable()
            {
                return observable;
            }
        }

        class DemoView extends ReactiveView<DemoViewModel>
        {
            @Override
            protected void initView(final @Nonnull DemoViewModel viewModel)
            {
                final DateTimeField dateTimeField = new DateTimeField();
                dateTimeField.setReadOnly(true);
                dateTimeField.setResolution(DateTimeResolution.SECOND);

                bind(valueOf(dateTimeField)).to(viewModel.getObservable());

                setCompositionRoot(dateTimeField);
            }
        }

        return new DemoView().withViewModel(new DemoViewModel());
    }

    @Nonnull
    private ReactiveView createSection3()
    {
        class DemoViewModel extends ReactiveViewModel
        {
            private final Observable<String> observable;

            public DemoViewModel()
            {
                this.observable = Observable.interval(1, TimeUnit.SECONDS)
                        .map(Object::toString);
            }

            @Nonnull
            public Observable<String> getObservable()
            {
                return observable;
            }
        }

        class DemoView extends ReactiveView<DemoViewModel>
        {
            @Override
            protected void initView(final @Nonnull DemoViewModel viewModel)
            {
                final Label label = new Label();

                bind(label::setValue).to(viewModel.getObservable());

                setCompositionRoot(label);
            }
        }

        return new DemoView().withViewModel(new DemoViewModel());
    }

    @Nonnull
    private ReactiveView createSection4()
    {
        class DemoViewModel extends ReactiveViewModel
        {
            private final Observable<String> observable;

            public DemoViewModel()
            {
                this.observable = Observable.error(
                        new RuntimeException("Error inside observable"));
            }

            @Nonnull
            public Observable<String> getObservable()
            {
                return observable;
            }
        }

        class DemoView extends ReactiveView<DemoViewModel>
        {
            @Override
            protected void initView(final @Nonnull DemoViewModel viewModel)
            {
                final Label label = new Label();
                final Button button = new Button("Bind", event ->
                        bind(valueOf(label)).to(viewModel.getObservable()));

                setCompositionRoot(new VerticalLayout(label, button));
            }

            @Override
            public void handleError(final @Nonnull Throwable error)
            {
                Notification.show("Unexpected error", error.getMessage(),
                        Notification.Type.ERROR_MESSAGE);
            }
        }

        return new DemoView().withViewModel(new DemoViewModel());
    }
}
