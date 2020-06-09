package com.vzharkov.signal;

/**
 * Represents a signal input.
 *
 * @param <V> Type of value being sent.
 */
@FunctionalInterface
public interface Sink<V> {
    void send(final Event<V> event);

    default void sendValue(final V v) {
        send(Event.value(v));
    }

    default void sendCompleted() {
        send(Event.completed());
    }

    default void sendError(final Throwable e)  {
        send(Event.error(e));
    }
}
