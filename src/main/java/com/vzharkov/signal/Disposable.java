package com.vzharkov.signal;

/**
 * Represents something that can be “disposed”, usually associated with freeing
 * resources or canceling work.
 */
public interface Disposable {
    /**
     * Dispose the resource.
     */
    void dispose();
}