package synthesizer;

import java.util.Iterator;

public class ArrayRingBuffer<T>  extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;

    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        first = 0;
        last = 0;
        this.capacity = capacity;
        rb = (T[]) new Object[capacity];
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException(). Exceptions
     * covered Monday.
     */
    public void enqueue(T x) {
        if (fillCount == capacity) {
            throw new RuntimeException("Ring buffer overflow");
        }
        rb[last] = x;
        fillCount++;
        last = addOne(last);
    }

    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    public T dequeue() {
        if (fillCount == 0) {
            throw new RuntimeException("Ring buffer underflow");
        }
        fillCount--;
        int oldFirst = first;
        first = addOne(first);
        return rb[oldFirst];
    }

    /**
     * Return oldest item, but don't remove it.
     */
    public T peek() {
        return rb[first];
    }

    private int addOne(int index) {
        if (index >= capacity - 1) {
            return 0;
        }
        return index + 1;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayRingBufferIterator<>();
    }

    private class ArrayRingBufferIterator<T> implements Iterator<T> {
        private int ptr;

        public ArrayRingBufferIterator() {
            this.ptr = first;
        }

        @Override
        public boolean hasNext() {
            return addOne(ptr) != last;
        }

        @Override
        public T next() {
            T item = (T) rb[ptr];
            ptr = addOne(ptr);
            return item;
        }
    }

}
