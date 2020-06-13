package com.vzharkov.signal;

/**
 * Channel can be thought of as a physical pipe with two ends.
 * At one end (input) you can insert new values,
 * and at the other end (output) you can observe what's coming out.
 *
 * @param <V> Type of value being sent.
 */
public class Channel<V> {
    public final Emitter<V> input;
    public final Signal<V> output;

    public Channel(final Emitter<V> input, final Signal<V> output) {
        this.input = input;
        this.output = output;
    }
}
