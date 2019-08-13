package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.*;

public class PipeTest {

    @Test
    public void pipeIsCreated() {
        Pipe<String, Throwable> pipe = Signal.pipe();
        assertNotNull(pipe);
    }
}