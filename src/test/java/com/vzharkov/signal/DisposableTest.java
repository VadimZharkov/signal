package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DisposableTest {
    private class Resource {};

    private Resource resource = new Resource();

    @Test
    public void testDisposable() {
        Disposable disposable = () -> resource = null;

        assertNotNull(resource);

        disposable.dispose();

        assertNull(resource);
    }
}