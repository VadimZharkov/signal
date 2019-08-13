package com.vzharkov.signal;

/**
 * Provides a mechanism for receiving push-based notifications.
 *
 * @param <V> Type of value being sent.
 * @param <E> Type of failure that can occur.
 */
public interface Observer<V, E> {
    void on(Event<V, E> event);
}
