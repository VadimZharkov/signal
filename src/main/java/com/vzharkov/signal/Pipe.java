package com.vzharkov.signal;

import java.util.function.Consumer;

/**
 * Pipe is a tuple:
 * a function with which we can send new values, and the signal itself, which is read-only.
 * Pipe can be thought of as a physical pipe with two ends.
 * At one end you can insert new values (also called the "sink"),
 * and at the other end you can observe what's coming out.
 *
 * @param <V> Type of value being sent.
 * @param <E> Type of failure that can occur.
 */
public class Pipe<V, E> {
    private final Consumer<Event<V, E>> sink;
    private final Signal<V, E> signal;

    public Pipe(final Consumer<Event<V, E>> sink, final Signal<V, E> signal) {
        this.sink = sink;
        this.signal = signal;
    }

    public void input(final Event<V, E> value) {
        sink.accept(value);
    }

    public void inputValue(final V value) {
        sink.accept(Event.value(value));
    }

    public void inputCompleted() {
        sink.accept(Event.completed());
    }

    public void inputError(final E error) {
        sink.accept(Event.error(error));
    }

    public Signal<V, E> signal() {
        return signal;
    }
}
