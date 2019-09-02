package com.vzharkov.signal;

/**
 * Represents a signal input.
 *
 * @param <V> Type of value being sent.
 * @param <E> Type of failure that can occur.
 */
public interface Sink<V, E> {
    void send(final Event<V, E> event);

    default void sendValue(final V v) {
        send(Event.value(v));
    }

    default void sendCompleted() {
        send(Event.completed());
    }

    default void sendError(final E e)  {
        send(Event.error(e));
    }
}
