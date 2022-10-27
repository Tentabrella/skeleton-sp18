package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void TestInitARB() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(10);
        assertEquals(10, arb.capacity());
    }

    @Test
    public void TestEnqueue() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(10);
        arb.enqueue("hello1");
        assertEquals(1, arb.fillCount());
    }

    @Test
    public void TestDeque() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(4);
        arb.enqueue("hello1");
        arb.enqueue("hello2");
        arb.enqueue("hello3");
        String dequeued = arb.dequeue();
        assertEquals("hello1", dequeued);
        assertEquals(2, arb.fillCount());
    }

    @Test
    public void TestPeek() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(10);
        arb.enqueue("hello1");
        arb.enqueue("hello2");
        arb.enqueue("hello3");
        assertEquals("hello1", arb.peek());
        assertEquals(3, arb.fillCount());
    }

    @Test
    public void TestIsFullIsEmpty() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            arb.enqueue("hello" + i);
        }
        assertTrue(arb.isFull());
        for (int i = 0; i < 10; i++) {
            arb.dequeue();
        }
        assertTrue(arb.isEmpty());
        arb.enqueue("hello");
        assertFalse(arb.isEmpty());
        assertFalse(arb.isFull());
    }

    @Test
    public void TestIntegrate() {
        ArrayRingBuffer<String> arb = new ArrayRingBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            arb.enqueue("hello" + i);
        }
        assertEquals("hello0", arb.dequeue());
        arb.enqueue("hello11");
        assertEquals("hello1", arb.dequeue());
        arb.dequeue();
        arb.dequeue();
        assertEquals("hello4", arb.dequeue());
        assertEquals("hello5", arb.peek());
        arb.enqueue("hello12");
        arb.enqueue("hello13");
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
