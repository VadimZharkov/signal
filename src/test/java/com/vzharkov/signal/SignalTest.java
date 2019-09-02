package com.vzharkov.signal;

import org.junit.Test;
import static org.junit.Assert.*;

public class SignalTest {
    private Integer value = null;
    private Throwable error = null;

    @Test
    public void signalIsCreatedAndAlive() {
        final Pipe<String, Throwable> pipe = Signal.createPipe();

        assertNotNull(pipe.signal());
        assertTrue(pipe.signal().isAlive());
    }

    @Test
    public void valueIsSent() {
        value = null;
        final Integer expected = 10;

        final Pipe<Integer, Throwable> pipe = Signal.createPipe();
        pipe.signal().subscribe(e -> value = e.value());
        pipe.sink().sendValue(10);

        assertEquals(expected, value);
    }

    @Test
    public void signalIsCompleted() {
        final Pipe<Integer, Throwable> pipe = Signal.createPipe();
        pipe.sink().sendCompleted();

        assertTrue(pipe.signal().isCompleted());
    }

    @Test
    public void errorIsSentAndSignalIsFailed() {
        error = null;
        final Throwable expected = new Throwable();

        Pipe<Integer, Throwable> pipe = Signal.createPipe();
        pipe.signal().subscribe(e -> error = e.error());
        pipe.sink().sendError(expected);

        assertEquals(expected, error);
        assertTrue(pipe.signal().isFailed());
    }

    @Test
    public void observerIsDisposed() {
        final Integer expected = 10;
        value = expected;

        final Pipe<Integer, Throwable> pipe = Signal.createPipe();
        final Disposable disposable = pipe.signal().subscribe((e) -> value = e.value());
        disposable.dispose();
        pipe.sink().sendValue(5);

        assertEquals(value, expected);
    }

    @Test
    public void signalIsStoppedAfterCompletion() {
        value = null;
        final Integer expected = 10;

        final Pipe<Integer, Throwable> pipe = Signal.createPipe();
        pipe.signal().subscribe(e -> {
            if (e.isValue())
                value = e.value();
        });

        pipe.sink().sendValue(10);
        assertEquals(expected, value);

        pipe.sink().sendCompleted();
        assertTrue(pipe.signal().isCompleted());

        pipe.sink().sendValue(15);
        assertEquals(expected, value);
    }

    @Test
    public void signalIsStoppedAfterError() {
        value = null;
        error = null;
        final Integer expected = 10;

        final Pipe<Integer, Throwable> pipe = Signal.createPipe();
        pipe.signal().subscribe(e -> {
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
    public void signalIsMapped() {
        value = 0;
        final Integer expected = 10;

        final Pipe<String, Throwable> pipe = Signal.createPipe();
        pipe.signal()
                .map(Integer::valueOf)
                .subscribe(e -> value = e.value());
        pipe.sink().sendValue("10");

        assertEquals(expected, value);
    }
}