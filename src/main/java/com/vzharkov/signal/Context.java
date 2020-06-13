package com.vzharkov.signal;

/**
 * Execution context
 */
public interface Context {
    Context DIRECT = Action::invoke;

    void invoke(final Action action);
}
