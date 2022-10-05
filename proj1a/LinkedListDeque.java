public class LinkedListDeque<T> {
    private ListNode sentinel;
    private int size;

    private static class ListNode<T> {
        private T value;
        private ListNode<T> prev;
        private ListNode<T> next;

        public ListNode(T value) {
            this.value = value;
        }
    }

    public LinkedListDeque() {
        this.sentinel = new ListNode(null);
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
        this.size = 0;
    }

    public void addFirst(T item) {
        ListNode node = new ListNode(item);
        node.next = this.sentinel.next;
        node.prev = this.sentinel;
        this.sentinel.next.prev = node;
        this.sentinel.next = node;
        this.size++;
    }

    public void addLast(T item) {
        ListNode node = new ListNode(item);
        node.next = this.sentinel;
        node.prev = this.sentinel.prev;
        this.sentinel.prev.next = node;
        this.sentinel.prev = node;
        this.size++;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public void printDeque() {
        ListNode node = this.sentinel.next;
        while (node != sentinel) {
            System.out.print(node.value + " ");
            node = node.next;
        }
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        ListNode node = this.sentinel.next;
        this.sentinel.next = node.next;
        node.next.prev = this.sentinel;
        this.size--;
        return (T) node.value;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        ListNode node = this.sentinel.prev;
        this.sentinel.prev = node.prev;
        node.prev.next = this.sentinel;
        this.size--;
        return (T) node.value;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        ListNode node = this.sentinel;
        while (index >= 0) {
            node = node.next;
            index--;
        }
        return (T) node.value;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return (T) getRecursive(index, this.sentinel);
    }

    private T getRecursive(int index, ListNode<T> node) {
        if (index < 0) {
            return node.value;
        }
        return getRecursive(--index, node.next);
    }
}
