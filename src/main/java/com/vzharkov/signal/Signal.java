package com.vzharkov.signal;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Signal is a push-driven stream that sends Events over time.
 *
 * @param <V> Type of value being sent.
 */
@SuppressWarnings("UnusedReturnValue")
public class Signal<V> {
    public enum State {
        ALIVE,
        COMPLETED,
        FAILED
    }

    private static class Observer<T> {
        final Context context;
        final Consumer<Event<T>> consumer;

        private Observer(final Context context, final Consumer<Event<T>> consumer) {
            this.context = Objects.requireNonNull(context);
            this.consumer = Objects.requireNonNull(consumer);
        }

        static <T> Observer<T> of(final Context context, final Consumer<Event<T>> consumer) {
            return new Observer<>(context, consumer);
        }

        static <T> Observer<T> of(final Consumer<Event<T>> consumer) {
            return Observer.of(Context.DIRECT, consumer);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Observer<?> observer = (Observer<?>) o;

            if (!context.equals(observer.context)) return false;

            return consumer.equals(observer.consumer);
        }

        @Override
        public int hashCode() {
            int result = context.hashCode();
            result = 31 * result + consumer.hashCode();

            return result;
        }
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.ALIVE);
    private final Map<UUID, Observer<V>> observers = new ConcurrentHashMap<>();
    private final AtomicBoolean started = new AtomicBoolean(false);

    private Consumer<Emitter<V>> generator;

    public static <V> Channel<V> newChannel() {
        Signal<V> signal = new Signal<>();
        Emitter<V> emitter = signal::send;

        return new Channel<>(emitter, signal);
    }

    public static <V> Signal<V> generate(final Consumer<Emitter<V>> generator) {
        return new Signal<>(generator);
    }

    protected Signal() {
        this.started.set(true);
    }

    protected Signal(final Consumer<Emitter<V>> generator) {
        Objects.requireNonNull(generator);

        this.started.set(false);
        this.generator = generator;
    }

    public final boolean isAlive() {
        return state.get() == State.ALIVE;
    }

    public final boolean isCompleted() {
        return state.get() == State.COMPLETED;
    }

    public final boolean isFailed() {
        return state.get() == State.FAILED;
    }

    public final boolean isStarted() {
        return started.get();
    }

    protected void send(final Event<V> event) {
        Objects.requireNonNull(event);

        if (!isAlive())
            return;

        observers.forEach((k, observer) -> observer.context.invoke(() -> observer.consumer.accept(event)));

        if (event.isTerminating()) {
            state.getAndUpdate(s -> event.isError() ? State.FAILED : State.COMPLETED);
        }
    }

    protected final Disposable subscribe(final Observer<V> observer) {
        Objects.requireNonNull(observer.context);
        Objects.requireNonNull(observer.consumer);

        final UUID key = UUID.randomUUID();
        observers.put(key, observer);

        if (started.compareAndSet(false, true)) {
            generator.accept(this::send);
        }

        return () -> observers.remove(key);
    }

    public Disposable subscribe(final Context context, final Consumer<Event<V>> consumer) {
        return subscribe(Observer.of(context, consumer));
    }

    public Disposable subscribe(final Consumer<Event<V>> consumer) {
        return subscribe(Context.DIRECT, consumer);
    }

    public Disposable subscribeValues(final Context context, final Consumer<V> consumer) {
        return subscribe(context, e -> {
            if (e.isValue())
                consumer.accept(e.value());
        });
    }

    public Disposable subscribeValues(final Consumer<V> consumer) {
        return subscribeValues(Context.DIRECT, consumer);
    }

    public Disposable subscribeCompleted(final Context context, final Action action) {
        return subscribe(context, e -> {
            if (e.isCompleted())
                action.invoke();
        });
    }

    public Disposable subscribeCompleted(final Action action) {
        return subscribeCompleted(Context.DIRECT, action);
    }

    public Disposable subscribeErrors(final Context context, final Consumer<Throwable> consumer) {
        return subscribe(context, e -> {
            if (e.isError())
                consumer.accept(e.error());
        });
    }

    public Disposable subscribeErrors(final Consumer<Throwable> consumer) {
        return subscribeErrors(Context.DIRECT, consumer);
    }

    public <U> Signal<U> map(final Function<V, U> mapper) {
        Objects.requireNonNull(mapper);

        final Channel<U> channel = Signal.newChannel();
        subscribe(e -> channel.input.send(e.map(mapper)));

        return channel.output;
    }
}
