package com.vzharkov.signal;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {

    @Test
    public void testValueEvent() {
        String expected = "value";
        Event<String> event = Event.value(expected);

        assertEquals(expected, event.value());
        assertFalse(event.isCompleted());
        assertFalse(event.isError());
        assertNull(event.error());
    }

    @Test
    public void testCompletedEvent() {
        Event<Object> event = Event.completed();

        assertTrue(event.isCompleted());
        assertFalse(event.isValue());
        assertNull(event.value());
        assertFalse(event.isError());
        assertNull(event.error());
    }

    @Test
    public void testFailedEvent() {
        Throwable expected = new Throwable();
        Event<Object> event = Event.error(expected);

        assertTrue(event.isError());
        assertEquals(expected, event.error());
        assertFalse(event.isValue());
        assertNull(event.value());
        assertFalse(event.isCompleted());
    }

    @Test
    public void testValueMap() {
        Integer value = 10;
        Event<Integer> first = Event.value(value);
        Event<String> second = first.map(Object::toString);

        assertTrue(second.isValue());
        assertFalse(second.isCompleted());
        assertFalse(second.isError());
        assertNull(second.error());
        assertEquals("10", second.value());
        assertNotEquals(first, second);;
    }

    @Test
    public void testCompletedMap() {
        Event<Object> first = Event.completed();
        Event<String> second = first.map(Object::toString);

        assertTrue(second.isCompleted());
        assertFalse(second.isValue());
        assertNull(second.value());
        assertFalse(second.isError());
        assertNull(second.error());
        assertEquals(first, second);
    }

    @Test
    public void testErrorMap() {
        Throwable error = new Throwable();
        Event<Object> first = Event.error(error);
        Event<String> second = first.map(Object::toString);

        assertTrue(second.isError());
        assertFalse(second.isValue());
        assertNull(second.value());
        assertFalse(second.isCompleted());
        assertEquals(second.error(), error);
        assertEquals(first, second);;
    }
}