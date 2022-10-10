public class Palindrome {
    /** Convert String to deque*/
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> deque = new LinkedListDeque<>();
        int n = word.length();
        for (int i = 0; i < n; i++) {
            deque.addLast(word.charAt(i));
        }
        return deque;
    }

    /** Test if a String is Palindrome
     * return false for null or isn't palindrome
     * return true if string is palindrome*/
    public boolean isPalindrome(String word) {
        if (word == null) {
            return false;
        }
        Deque<Character> wordDeque = wordToDeque(word);
        return isPalindrome(wordDeque);
    }

    /** Another isPalindrome using CharComparator*/
    public boolean isPalindrome(String word, CharacterComparator cc) {
        if (word == null) {
            return false;
        }
        Deque<Character> wordDeque = wordToDeque(word);
        return isPalindrome(wordDeque, cc);
    }

    /** Helper method of isPalindrome*/
    private boolean isPalindrome(Deque<Character> wordDeque) {
        if (wordDeque.isEmpty() || wordDeque.size() == 1) {
            return true;
        }
        if (wordDeque.removeLast() != wordDeque.removeFirst()) {
            return false;
        }
        return isPalindrome(wordDeque);
    }

    private boolean isPalindrome(Deque<Character> wordDeque, CharacterComparator cc) {
        if (wordDeque.isEmpty() || wordDeque.size() == 1) {
            return true;
        }
        if (!cc.equalChars(wordDeque.removeLast(), wordDeque.removeFirst())) {
            return false;
        }
        return isPalindrome(wordDeque, cc);
    }





}
