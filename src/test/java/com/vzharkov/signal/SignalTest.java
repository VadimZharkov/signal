package com.vzharkov.signal;

import org.junit.Test;
import static org.junit.Assert.*;

public class SignalTest {

    private Integer value = null;
    private boolean compleated = false;
    private Throwable error = null;

    @Test
    public void signalIsCreated() {
        Pipe<String, Throwable> pipe = Signal.pipe();

        assertNotNull(pipe);
        assertNotNull(pipe.signal());
    }

    @Test
    public void signalSentValue() {
        value = null;
        Integer expected = 10;

        Pipe<Integer, Throwable> pipe = Signal.pipe();
        pipe.signal().subscribe((e) -> value = e.getValue());
        pipe.inputValue(expected);

        assertEquals(expected, value);
    }

    @Test
    public void signalSentCompleated() {
        compleated = false;
        boolean expected = true;

        Pipe<Integer, Throwable> pipe = Signal.pipe();
        pipe.signal().subscribe((e) -> compleated = e.isCompleted());
        pipe.inputCompleted();

        assertEquals(expected, compleated);
    }

    @Test
    public void signalSentError() {
        error = null;
        Throwable expected = new Throwable();

        Pipe<Integer, Throwable> pipe = Signal.pipe();
        pipe.signal().subscribe((e) -> error = e.getError());
        pipe.inputError(expected);

        assertEquals(expected, error);
    }

    @Test
    public void observerIsDisposed() {
        Integer expected = 10;
        value = expected;

        Pipe<Integer, Throwable> pipe = Signal.pipe();
        Disposable disposable = pipe.signal().subscribe((e) -> value = e.getValue());
        disposable.dispose();
        pipe.inputValue(5);

        assertEquals(value, expected);
    }

    @Test
    public void signalIsStoppedAfterCompletion() {
        value = null;
        compleated = false;

        Pipe<Integer, Throwable> pipe = Signal.pipe();
        pipe.signal().subscribe((e) -> {
            if (e.isCompleted())
                compleated = true;
            else if (e.isValue())
                value = e.getValue();
        });

        pipe.inputValue(10);
        assertEquals(new Integer(10), value);

        pipe.inputCompleted();
        assertEquals(true, compleated);

        pipe.inputValue(15);
        assertEquals(new Integer(10), value);
    }

    @Test
    public void signalIsStoppedAfterError() {
        value = null;
        error = null;

        Pipe<Integer, Throwable> pipe = Signal.pipe();
        pipe.signal().subscribe((e) -> {
            if (e.isError())
                error = e.getError();
            else if (e.isValue())
                value = e.getValue();
        });

        pipe.inputValue(10);
        assertEquals(new Integer(10), value);

        pipe.inputError(new Throwable());
        assertNotNull(error);

        pipe.inputValue(15);
        assertEquals(new Integer(10), value);
    }

    @Test
    public void signalIsMapped() {
        value = 0;
        Integer expected = 10;

        Pipe<String, Throwable> pipe = Signal.pipe();
        pipe.signal()
                .map(Integer::valueOf)
                .subscribe((r) -> value = r.getValue());
        pipe.inputValue("10");

        assertEquals(expected, value);
    }
}