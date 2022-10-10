public class ArrayDeque<T> implements Deque<T> {
    private int head;
    private int tail;
    private T[] ts;
    private int arrSize;
    private int size;
    private final int INIT_SIZE = 8;
    private final int MIN_SHRINK_SIZE = 8;

    /** Creates an empty list. */
    public ArrayDeque() {
        this.head = 0;
        this.tail = 0;
        this.arrSize = INIT_SIZE;
        this.size = 0;
        //noinspection unchecked
        this.ts = (T[]) new Object[INIT_SIZE];
    }

    /** Resizes the underlying array to the target capacity.
     * condition: when FULL double the size
     * condition: when current arrSize is >= 16, usage ratio < 0.25, half the size*/
    private void resize(int capacity) {
        @SuppressWarnings("unchecked") T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            a[i] = get(i);
        }
        this.head = 0;
        this.tail = size;
        this.arrSize = capacity;
        ts = a;
    }

    /** return the index of arr prev to the given index*/
    private int plusOne(int index) {
        return Math.floorMod(index + 1, this.arrSize);
    }

    /** return the index of arr next to the given index*/
    private int minusOne(int index) {
        return Math.floorMod(index - 1, this.arrSize);
    }

    /** Insert the X into the front of the list*/
    @Override
    public void addFirst(T x) {
        if (this.size == this.arrSize) {
            resize(this.arrSize * 2);
        }
        ts[minusOne(head)] = x;
        head = minusOne(head);
        size++;
    }

    /** Inserts X into the back of the list. */
    @Override
    public void addLast(T x) {
        if (this.size == this.arrSize) {
            resize(this.arrSize * 2);
        }
        ts[tail] = x;
        tail = plusOne(tail);
        size++;
    }
    /** return if the list is empty*/
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    /** return the size of deque*/
    @Override
    public int size() {
        return this.size;
    }

    /** print the deque */
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
    }

    /** Remove the head element of the list and return it*/
    @Override
    public T removeFirst() {
        if (arrSize >= MIN_SHRINK_SIZE && (size << 2) < arrSize) {
            resize(arrSize / 2);
        }
        if (isEmpty()) {
            return null;
        }
        T x = ts[head];
        ts[head] = null;
        head = plusOne(head);
        size--;
        return x;
    }

    /** Remove the head element of the list and return it*/
    @Override
    public T removeLast() {
        if (arrSize >= MIN_SHRINK_SIZE && (size << 2) < arrSize) {
            resize(arrSize / 2);
        }
        if (isEmpty()) {
            return null;
        }
        T x = ts[minusOne(tail)];
        ts[minusOne(tail)] = null;
        tail = minusOne(tail);
        size--;
        return x;
    }

    /** Gets the ith item in the list (0 is the front). */
    @Override
    public T get(int i) {
        i = Math.floorMod(this.head + i, this.arrSize);
        return ts[i];
    }


}
