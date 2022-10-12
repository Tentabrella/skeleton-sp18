import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDequeGold {
    /** test if student's implementation is correct */
    @Test
    public void testArrayDeque() {
        StudentArrayDeque<Integer> hbArrayDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> stdArrayDeque = new ArrayDequeSolution<>();
        boolean isSamePerformance = true;
        int dequeLen = 0;
        String log = "";
        while (isSamePerformance) {
            int action = StdRandom.uniform(4);
            if (dequeLen == 0) {
                action %= 2;
            }
            Integer num;
            Integer actual = 0;
            Integer expected = 0;
            switch (action) {
                case 0:
                    num = StdRandom.uniform(10);
                    hbArrayDeque.addFirst(num);
                    stdArrayDeque.addFirst(num);
                    log += String.format("addFirst(%d)\n", num);
                    dequeLen++;
                    break;
                case 1:
                    num = StdRandom.uniform(10);
                    hbArrayDeque.addLast(num);
                    stdArrayDeque.addLast(num);
                    log += String.format("addLast(%d)\n", num);
                    dequeLen++;
                    break;
                case 2:
                    actual = hbArrayDeque.removeFirst();
                    expected = stdArrayDeque.removeFirst();
                    log += "removeFirst()\n";
                    break;
                case 3:
                    actual = hbArrayDeque.removeLast();
                    expected = stdArrayDeque.removeLast();
                    log += "removeLast()\n";
                    break;
                default:
            }
            assertEquals(log, expected, actual);
        }
    }
}
