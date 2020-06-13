package com.vzharkov.signal;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class SignalTest {
    @Test
    public void testCreatedSignal() {
        final Channel<String> channel = Signal.newChannel();

        assertNotNull(channel.output);
        assertTrue(channel.output.isAlive());
    }

    @Test
    public void testEventSend() {
        final Integer expected = 10;
        final Integer[] result = {0};

        final Channel<Integer> channel = Signal.newChannel();
        channel.output.subscribe(e -> result[0] = e.value());
        channel.input.send(Event.value(10));

        assertEquals(expected, result[0]);
    }

    @Test
    public void testValueSend() {
        final Integer expected = 10;
        final Integer[] result = {0};

        final Channel<Integer> channel = Signal.newChannel();
        channel.output.subscribeValues(v -> result[0] = v);
        channel.input.sendValue(10);

        assertEquals(expected, result[0]);
    }

    @Test
    public void testCompletedSend() {
        final Channel<Integer> channel = Signal.newChannel();
        channel.input.sendCompleted();

        assertTrue(channel.output.isCompleted());
    }

    @Test
    public void testErrorSend() {
        final Throwable expected = new Throwable();
        final Throwable[] error = {null};

        Channel<Integer> channel = Signal.newChannel();
        channel.output.subscribeErrors(e -> error[0] = e);
        channel.input.sendError(expected);

        assertEquals(expected, error[0]);
        assertTrue(channel.output.isFailed());
    }

    @Test
    public void testObserverDisposable() {
        final Integer expected = 10;
        final Integer[] result = {expected};

        final Channel<Integer> channel = Signal.newChannel();
        final Disposable disposable = channel.output.subscribe(e -> result[0] = e.value());
        disposable.dispose();
        channel.input.sendValue(5);

        assertEquals(result[0], expected);
    }

    @Test
    public void testCompletion() {
        final Integer expected = 10;
        final Integer[] result = {0};

        final Channel<Integer> channel = Signal.newChannel();
        channel.output.subscribeValues(v -> result[0] = v);

        channel.input.sendValue(10);
        assertEquals(expected, result[0]);

        channel.input.sendCompleted();
        assertTrue(channel.output.isCompleted());

        channel.input.sendValue(15);
        assertEquals(expected, result[0]);
    }

    @Test
    public void testStoppedAfterError() {
        final Integer expected = 10;
        final Throwable[] error = {null};
        final Integer[] result = {0};

        final Channel<Integer> channel = Signal.newChannel();
        channel.output.subscribe(e -> {
            if (e.isError())
                error[0] = e.error();
            else if (e.isValue())
                result[0] = e.value();
        });

        channel.input.sendValue(10);
        assertEquals(expected, result[0]);

        channel.input.sendError(new Throwable());
        assertNotNull(error[0]);
        assertTrue(channel.output.isFailed());

        channel.input.sendValue(15);
        assertEquals(expected, result[0]);
    }

    @Test
    public void testSignalMap() {
        final Integer expected = 10;
        final Integer[] result = new Integer[1];

        final Channel<String> channel = Signal.newChannel();
        channel.output
                .map(Integer::valueOf)
                .subscribeValues(v -> result[0] = v);
        channel.input.sendValue("10");

        assertEquals(expected, result[0]);
    }

    @Test
    public void testLazySignal() {
        final Integer expected = 10;
        final Integer[] result = new Integer[1];

        Signal<Integer> signal = Signal.generate(emitter -> {
            emitter.sendValue(10);
        });

        assertNotNull(signal);
        assertFalse(signal.isStarted());

        signal.subscribeValues(v -> result[0] = v);

        assertEquals(expected, result[0]);
    }

    @Test
    public void testExecutionContext() throws InterruptedException {
        final Context context = new Context() {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            @Override
            public void invoke(Action action) {
                executor.submit(action::invoke);
            }
        };

        final Integer expected = 10;
        final Integer[] result = {0};
        final String mainThreadName = Thread.currentThread().getName();
        final String[] contextThreadName = {mainThreadName};
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final Channel<Integer> channel = Signal.newChannel();
        channel.output.subscribeValues(context, v -> {
            contextThreadName[0] = Thread.currentThread().getName();
            result[0] = v;
            countDownLatch.countDown();
        });

        channel.input.sendValue(10);

        countDownLatch.await();

        assertEquals(expected, result[0]);
        assertNotEquals(mainThreadName, contextThreadName[0]);
    }
}