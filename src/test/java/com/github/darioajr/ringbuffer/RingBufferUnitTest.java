package com.github.darioajr.ringbuffer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RingBufferUnitTest {

    private final String[] messages = { "Message010", "Message011", "Message012", "Message013", "Message014", "Message015", "Message016", "Message017", "Message018", "Message019" };
    private final int defaultCapacity = messages.length;

    @Test
    public void givenRingBuffer_whenAnElementIsEnqueued_thenSizeIsOne() {
        RingBuffer<String> buffer = new RingBuffer<>(defaultCapacity);

        assertTrue(buffer.offer("Message013"));
        assertEquals(1, buffer.size());
    }

    @Test
    public void givenRingBuffer_whenAnElementIsDequeued_thenElementMatchesEnqueuedElement() {
        RingBuffer<String> buffer = new RingBuffer<>(defaultCapacity);

        buffer.offer("Message011");

        String shape = buffer.poll();
        assertEquals("Message011", shape);
    }

    @Test
    public void givenRingBuffer_whenAnElementIsEnqueuedAndDeququed_thenBufferIsEmpty() {

        RingBuffer<String> buffer = new RingBuffer<>(defaultCapacity);

        buffer.offer("Message012");

        assertFalse(buffer.isEmpty());
        assertEquals(1, buffer.size());

        buffer.poll();

        assertTrue(buffer.isEmpty());
    }

    @Test
    public void givenRingBuffer_whenFilledToCapacity_thenNoMoreElementsCanBeEnqueued() {

        int capacity = messages.length;
        RingBuffer<String> buffer = new RingBuffer<>(capacity);

        assertTrue(buffer.isEmpty());

        for (String shape : messages) {
            buffer.offer(shape);
        }

        assertTrue(buffer.isFull());
        assertFalse(buffer.offer("Message099"));
    }

    @Test
    public void givenRingBuffer_whenBufferIsEmpty_thenReturnsNull() {

        RingBuffer<String> buffer = new RingBuffer<>(1);

        assertTrue(buffer.isEmpty());
        assertNull(buffer.poll());
    }
}
