package com.vzharkov.signal;

/**
 * Pipe can be thought of as a physical pipe with two ends.
 * At one end (Sink) you can insert new values,
 * and at the other end (Signal) you can observe what's coming out.
 *
 * @param <V> Type of value being sent.
 * @param <E> Type of failure that can occur.
 */
public interface Pipe<V, E> {
    Sink<V, E> sink();
    Signal<V, E> signal();
}
