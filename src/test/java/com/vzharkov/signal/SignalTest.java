package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.*;

public class SignalTest {
    private Integer value = null;
    private Throwable error = null;

    @Test
    public void testCreatedSignal() {
        final Pipe<String> pipe = Signal.createPipe();

        assertNotNull(pipe.signal());
        assertTrue(pipe.signal().isAlive());
    }

    @Test
    public void testEventSend() {
        value = null;
        final Integer expected = 10;

        final Pipe<Integer> pipe = Signal.createPipe();
        pipe.signal().observe(e -> value = e.value());
        pipe.sink().send(Event.value(10));

        assertEquals(expected, value);
    }

    @Test
    public void testValueSend() {
        value = null;
        final Integer expected = 10;

        final Pipe<Integer> pipe = Signal.createPipe();
        pipe.signal().observeValue(v -> value = v);
        pipe.sink().sendValue(10);

        assertEquals(expected, value);
    }

    @Test
    public void testCompletedSend() {
        final Pipe<Integer> pipe = Signal.createPipe();
        pipe.sink().sendCompleted();

        assertTrue(pipe.signal().isCompleted());
    }

    @Test
    public void testErrorSend() {
        error = null;
        final Throwable expected = new Throwable();

        Pipe<Integer> pipe = Signal.createPipe();
        pipe.signal().observeError(e -> error = e);
        pipe.sink().sendError(expected);

        assertEquals(expected, error);
        assertTrue(pipe.signal().isFailed());
    }

    @Test
    public void testObserverDisposable() {
        final Integer expected = 10;
        value = expected;

        final Pipe<Integer> pipe = Signal.createPipe();
        final Disposable disposable = pipe.signal().observe(e -> value = e.value());
        disposable.dispose();
        pipe.sink().sendValue(5);

        assertEquals(value, expected);
    }

    @Test
    public void testCompletion() {
        value = null;
        final Integer expected = 10;

        final Pipe<Integer> pipe = Signal.createPipe();
        pipe.signal().observeValue(v -> value = v);

        pipe.sink().sendValue(10);
        assertEquals(expected, value);

        pipe.sink().sendCompleted();
        assertTrue(pipe.signal().isCompleted());

        pipe.sink().sendValue(15);
        assertEquals(expected, value);
    }

    @Test
    public void testStoppedAfterError() {
        value = null;
        error = null;
        final Integer expected = 10;

        final Pipe<Integer> pipe = Signal.createPipe();
        pipe.signal().observe(e -> {
            if (e.isError())
                error = e.error();
            else if (e.isValue())
                value = e.value();
        });

        pipe.sink().sendValue(10);
        assertEquals(expected, value);

        pipe.sink().sendError(new Throwable());
        assertNotNull(error);
        assertTrue(pipe.signal().isFailed());

        pipe.sink().sendValue(15);
        assertEquals(expected, value);
    }

    @Test
    public void testSignalMap() {
        value = 0;
        final Integer expected = 10;

        final Pipe<String> pipe = Signal.createPipe();
        pipe.signal()
                .map(Integer::valueOf)
                .observeValue(v -> value = v);
        pipe.sink().sendValue("10");

        assertEquals(expected, value);
    }
}