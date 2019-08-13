package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.*;

public class DisposableTest {
    class Resource {};

    private Resource resource = new Resource();

    @Test
    public void resourceIsDisposed() {
        Disposable disposable = () -> resource = null;
        assertNotNull(resource);
        disposable.dispose();
        assertNull(resource);
    }
}