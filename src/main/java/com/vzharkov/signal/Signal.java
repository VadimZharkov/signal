package com.vzharkov.signal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Signal is a push-driven stream that sends Events over time.
 *
 * @param <V> Type of value being sent.
 * @param <E> Type of failure that can occur.
 */
public class Signal<V, E> {
    public enum State {
        ALIVE,
        COMPLETED,
        FAILED
    }

    private final AtomicReference<State> state = new AtomicReference<>(State.ALIVE);
    private final Map<UUID, Consumer<Event<V, E>>> observers = new ConcurrentHashMap<>();

    public static <V, E> Pipe<V, E> createPipe() {
        Signal<V, E> signal = new Signal<>();

        return new Pipe<V, E>() {
            public Sink<V, E> sink() {
                return signal::send;
            }

            public Signal<V, E> signal() {
                return signal;
            }
       };
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

    protected void send(final Event<V, E> event) {
        if (!isAlive())
            return;

        observers.forEach((k, observer) -> observer.accept(event));

        if (event.isTerminating()) {
            state.getAndUpdate((s) -> event.isError() ? State.FAILED : State.COMPLETED);
        }
    }

    public Disposable observe(final Consumer<Event<V, E>> observer) {
        final UUID key = UUID.randomUUID();
        observers.put(key, observer);

        return () -> observers.remove(key);
    }

    public Disposable observeValue(final Consumer<V> observer) {
        return observe(e -> {
            if (e.isValue())
                observer.accept(e.value());
        });
    }

    public Disposable observeCompleted(final Thunk observer) {
        return observe(e -> {
            if (e.isCompleted())
                observer.apply();
        });
    }

    public Disposable observeError(final Consumer<E> observer) {
        return observe(e -> {
            if (e.isError())
                observer.accept(e.error());
        });
    }

    public <U> Signal<U, E> map(final Function<V, U> mapper) {
        final Pipe<U, E> pipe = Signal.createPipe();
        observe(e -> pipe.sink().send(e.map(mapper)));

        return pipe.signal();
    }
}
