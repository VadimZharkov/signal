package com.vzharkov.signal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
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
    private final AtomicReference<State> state = new AtomicReference<State>(State.ALIVE);
    private final Map<UUID, Observer<V, E>> observers = new ConcurrentHashMap<>();

    public static <E, V> Pipe<V, E> pipe() {
        final Signal<V, E> signal = new Signal<>();

        return new Pipe<V, E>(signal::send, signal);
    }

    private void send(final Event<V, E> event) {
        if (state.get() != State.ALIVE)
            return;

        observers.forEach((k, observer) -> observer.on(event));

        if (event.isTerminating()) {
            state.getAndUpdate((s) -> {
                return event.isError() ? State.FAILED : State.COMPLETED;
            });
        }
    }

    public Disposable subscribe(final Observer<V, E> observer) {
        final UUID key = UUID.randomUUID();
        observers.put(key, observer);

        return () -> observers.remove(key);
    }

    public <U> Signal<U, E> map(final Function<V, U> mapper) {
        final Pipe<U, E> pipe = Signal.pipe();
        subscribe(result -> pipe.input(result.map(mapper)));

        return pipe.signal();
    }
}
