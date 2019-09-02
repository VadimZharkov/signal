package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void isValueEvent() {
        String expected = "value";
        Event event = Event.value(expected);

        assertEquals(expected, event.value());
        assertFalse(event.isCompleted());
        assertFalse(event.isError());
        assertNull(event.error());
    }

    @Test
    public void isCompletedEvent() {
        Event event = Event.completed();

        assertTrue(event.isCompleted());
        assertFalse(event.isValue());
        assertNull(event.value());
        assertFalse(event.isError());
        assertNull(event.error());
    }

    @Test
    public void isFailedEvent() {
        Throwable expected = new Throwable();
        Event event = Event.error(expected);

        assertTrue(event.isError());
        assertEquals(expected, event.error());
        assertFalse(event.isValue());
        assertNull(event.value());
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
        assertNull(second.error());
        assertEquals("10", second.value());
        assertNotEquals(first, second);;
    }

    @Test
    public void isCompletedMapped() {
        Event first = Event.completed();
        Event second = first.map(v -> v.toString());

        assertTrue(second.isCompleted());
        assertFalse(second.isValue());
        assertNull(second.value());
        assertFalse(second.isError());
        assertNull(second.error());
        assertEquals(first, second);
    }

    @Test
    public void isErrorMapped() {
        Throwable error = new Throwable();
        Event first = Event.error(error);
        Event second = first.map(v -> v.toString());

        assertTrue(second.isError());
        assertFalse(second.isValue());
        assertNull(second.value());
        assertFalse(second.isCompleted());
        assertEquals(second.error(), error);
        assertEquals(first, second);;
    }
}