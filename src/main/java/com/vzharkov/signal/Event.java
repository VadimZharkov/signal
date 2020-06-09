package com.vzharkov.signal;

import java.util.Objects;
import java.util.function.Function;

import static com.vzharkov.signal.Event.Type.*;

/**
 * Represents a signal event.
 */
public class Event<V> {
    public enum Type {
        VALUE,
        COMPLETED,
        ERROR
    }

    private final Type type;
    private Object object;

    private Event(final Type type, final Object object) {
        this.type = Objects.requireNonNull(type);
        if (COMPLETED != type)
            this.object = Objects.requireNonNull(object);
    }

    public static <V> Event<V> value(final V value) {
        return new Event<>(VALUE, value);
    }

    public static <V> Event<V> completed() {
        return new Event<>(COMPLETED, null);
    }

    public static <V> Event<V> error(final Throwable error) {
        return new Event<>(ERROR, error);
    }

    public Type type() {
        return type;
    }

    public boolean isValue() {
        return VALUE == type;
    }

    @SuppressWarnings("unchecked")
    public V value() {
        return isValue() ? (V)object : null;
    }

    public boolean isError() {
        return ERROR == type;
    }

    @SuppressWarnings("unchecked")
    public Throwable error() {
        return isError() ? (Throwable) object : null;
    }

    public boolean isCompleted() {
        return COMPLETED == type;
    }

    public boolean isTerminating() {
        return  type != VALUE;
    }

    @Override
    public String toString() {
        String result;
        switch (type) {
            case VALUE:
                result = value().toString();
                break;
            case COMPLETED:
                result = "Completed";
                break;
            case ERROR:
                result = "Error: " + error().toString();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Event event = (Event<?>) o;

        if (isValue())
            return value().equals(event.value());

        if (isCompleted() && event.isCompleted())
            return true;

        return error().equals(event.error());
    }

    @Override
    public int hashCode() {
        if (isValue())
            return value().hashCode();

        if (isCompleted())
            return super.hashCode();

        return error().hashCode();
    }

    public <U> Event<U> map(final Function<V, U> transform) {
        Objects.requireNonNull(transform);

        Event<U> event;
        switch (type) {
            case VALUE:
                event = Event.value(transform.apply(value()));
                break;
            case COMPLETED:
                event = Event.completed();
                break;
            case ERROR:
                event = Event.error(error());
                break;
            default:
                throw new IllegalStateException("This should not have happened");
        }
        return event;
    }
}
