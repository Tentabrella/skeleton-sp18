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
        assertFalse(palindrome.isPalindrome("cat"));
        assertFalse(palindrome.isPalindrome("afternoon"));
        assertFalse(palindrome.isPalindrome(null));
        assertFalse(palindrome.isPalindrome("Deified"));
        assertTrue(palindrome.isPalindrome("noon"));
        assertTrue(palindrome.isPalindrome("deified"));
        assertTrue(palindrome.isPalindrome("flake", new OffByOne()));
        assertTrue(palindrome.isPalindrome("climb", new OffByOne()));
        assertTrue(palindrome.isPalindrome("chid", new OffByOne()));
        assertFalse(palindrome.isPalindrome("noon", new OffByOne()));
        assertTrue(palindrome.isPalindrome("pink", new OffByN(5)));
        assertTrue(palindrome.isPalindrome("taffy", new OffByN(5)));
        assertTrue(palindrome.isPalindrome("sworn", new OffByN(5)));
        assertFalse(palindrome.isPalindrome("noon", new OffByN(5)));
        assertFalse(palindrome.isPalindrome("climb", new OffByN(5)));
    }
}
