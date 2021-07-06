package com.github.darioajr.ringbuffer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class ProducerConsumerLiveTest {

    private final String[] messages = { "Message010", "Message011", "Message012", "Message013", "Message014", "Message015", "Message016", "Message017", "Message018", "Message019" };

    @Test
    public void givenARingBuffer_whenInterleavingProducerConsumer_thenElementsMatch() throws Exception {
        RingBuffer<String> buffer = new RingBuffer<String>(messages.length);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(new Producer<String>(buffer, messages));
        Future<String[]> consumed = executorService.submit(new Consumer<String>(buffer, messages.length));

        while(!consumed.isDone()) {
            System.out.println("Aguardando...");

        }

        String[] messagesConsumed = consumed.get(5L, TimeUnit.SECONDS);
        assertArrayEquals(messages, messagesConsumed);
    }

    static class Producer<T> implements Runnable {

        private RingBuffer<T> buffer;
        private T[] items;

        public Producer(RingBuffer<T> buffer, T[] items) {
            this.buffer = buffer;
            this.items = items;
        }

        @Override
        public void run() {

            for (int i = 0; i < items.length;) {
                if (buffer.offer(items[i])) {
                    System.out.println("Produced: " + items[i]);
                    i++;
                    LockSupport.parkNanos(5);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    static class Consumer<T> implements Callable<T[]> {

        private RingBuffer<T> buffer;
        private int expectedCount;

        public Consumer(RingBuffer<T> buffer, int expectedCount) {
            this.buffer = buffer;
            this.expectedCount = expectedCount;
        }

        @Override
        public T[] call() throws Exception {
            T[] items = (T[]) new String[expectedCount];
            for (int i = 0; i < items.length;) {
                T item = buffer.poll();
                if (item != null) {
                    items[i++] = item;

                    LockSupport.parkNanos(5);
                    System.out.println("Consumed: " + item);
                }
            }
            return items;
        }
    }
}
