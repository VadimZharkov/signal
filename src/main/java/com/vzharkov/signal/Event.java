package com.vzharkov.signal;

import java.util.Objects;
import java.util.function.Function;

import static com.vzharkov.signal.Event.Type.*;

/**
 * Represents a signal event.
 */
public class Event<V, E> {
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

    public static <V, E> Event<V, E> value(final V value) {
        return new Event<>(VALUE, value);
    }

    public static <V, E> Event<V, E> completed() {
        return new Event<>(COMPLETED, null);
    }

    public static <V, E> Event<V, E> error(final E error) {
        return new Event<>(ERROR, error);
    }

    public Type getType() {
        return type;
    }

    public boolean isValue() {
        return VALUE == type;
    }

    @SuppressWarnings("unchecked")
    public V getValue() {
        return isValue() ? (V)object : null;
    }

    public boolean isError() {
        return ERROR == type;
    }

    @SuppressWarnings("unchecked")
    public E getError() {
        return isError() ? (E)object : null;
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
                result = getValue().toString();
                break;
            case COMPLETED:
                result = "Completed";
                break;
            case ERROR:
                result = "Error: " + getError().toString();
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

        Event event = (Event<?,?>)o;

        if (isValue())
            return getValue().equals(event.getValue());

        if (isCompleted() && event.isCompleted())
            return true;

        return getError().equals(event.getError());
    }

    @Override
    public int hashCode() {
        if (isValue())
            return getValue().hashCode();

        if (isCompleted())
            return super.hashCode();

        return getError().hashCode();
    }

    public <U> Event<U, E> map(final Function<V, U> transform) {
        Objects.requireNonNull(transform);
        Event<U, E> event;
        switch (type) {
            case VALUE:
                event = Event.value(transform.apply(getValue()));
                break;
            case COMPLETED:
                event = Event.completed();
                break;
            case ERROR:
                event = Event.error(getError());
                break;
            default:
                throw new IllegalStateException("This should not have happened");
        }
        return event;
    }
}
