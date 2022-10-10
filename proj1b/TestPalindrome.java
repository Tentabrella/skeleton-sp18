import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        OffByOne offByOne = new OffByOne();
        OffByN offBy5 = new OffByN(5);
        assertFalse(palindrome.isPalindrome("cat"));
        assertFalse(palindrome.isPalindrome("afternoon"));
        assertFalse(palindrome.isPalindrome(null));
        assertFalse(palindrome.isPalindrome("Deified"));
        assertTrue(palindrome.isPalindrome("noon"));
        assertTrue(palindrome.isPalindrome("deified"));
        assertTrue(palindrome.isPalindrome("flake", offByOne));
        assertTrue(palindrome.isPalindrome("climb", offByOne));
        assertTrue(palindrome.isPalindrome("chid", offByOne));
        assertFalse(palindrome.isPalindrome("noon", offByOne));
        assertTrue(palindrome.isPalindrome("pink", offBy5));
        assertTrue(palindrome.isPalindrome("taffy", offBy5));
        assertTrue(palindrome.isPalindrome("sworn", offBy5));
        assertFalse(palindrome.isPalindrome("noon", offBy5));
        assertFalse(palindrome.isPalindrome("climb", offBy5));
    }
}
