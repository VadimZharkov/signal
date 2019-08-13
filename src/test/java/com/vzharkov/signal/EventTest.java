package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void isValueEvent() {
        String expected = "value";
        Event event = Event.value(expected);

        assertEquals(expected, event.getValue());
        assertFalse(event.isCompleted());
        assertFalse(event.isError());
        assertNull(event.getError());
    }

    @Test
    public void isCompletedEvent() {
        Event event = Event.completed();

        assertTrue(event.isCompleted());
        assertFalse(event.isValue());
        assertNull(event.getValue());
        assertFalse(event.isError());
        assertNull(event.getError());
    }

    @Test
    public void isFailedEvent() {
        Throwable expected = new Throwable();
        Event event = Event.error(expected);

        assertTrue(event.isError());
        assertEquals(expected, event.getError());
        assertFalse(event.isValue());
        assertNull(event.getValue());
        assertFalse(event.isCompleted());
    }

    @Test
    public void isValueMapped() {
        Integer value = 10;
        Event first = Event.value(value);
        Event second = first.map(v -> v.toString());

        assertTrue(second.isValue());
        assertFalse(second.isCompleted());
        assertFalse(second.isError());
        assertNull(second.getError());
        assertEquals("10", second.getValue());
        assertNotEquals(first, second);;
    }

    @Test
    public void isCompletedMapped() {
        Event first = Event.completed();
        Event second = first.map(v -> v.toString());

        assertTrue(second.isCompleted());
        assertFalse(second.isValue());
        assertNull(second.getValue());
        assertFalse(second.isError());
        assertNull(second.getError());
        assertEquals(first, second);
    }

    @Test
    public void isErrorMapped() {
        Throwable error = new Throwable();
        Event first = Event.error(error);
        Event second = first.map(v -> v.toString());

        assertTrue(second.isError());
        assertFalse(second.isValue());
        assertNull(second.getValue());
        assertFalse(second.isCompleted());
        assertEquals(second.getError(), error);
        assertEquals(first, second);;
    }
}