package pojos;

import java.util.LinkedList;
import java.util.List;

public class Trie {
    private static final int R = 27;
    private TNode root;

    public Trie() {
        root = new TNode();
    }

    public boolean search(String word) {
        word = word.toLowerCase();
        return search(word, 0, root);
    }

    private boolean search(String word, int i, TNode node) {
        if (node == null) {
            return false;
        }
        if (i == word.length()) {
            return node.exists;
        }
        return search(word, i + 1, node.links[index(word.charAt(i))]);
    }

    public void add(String word) {
        word = word.toLowerCase();
        add(root, word, 0);
    }

    private TNode add(TNode node, String word, int i) {
        if (node == null) {
            node = new TNode();
        }
        if (i == word.length()) {
            node.exists = true;
            return node;
        }
        int idx = index(word.charAt(i));
        node.links[idx] = add(node.links[idx], word, i + 1);
        return node;
    }

    private class TNode {
        private boolean exists;
        private TNode[] links;

        public TNode() {
            links = new TNode[R];
            exists = false;
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.add("hello");
        trie.add("hushihong");
        trie.add("apple");
        System.out.println(trie.search("hello"));
        System.out.println(trie.search("hell"));
        System.out.println(trie.search("hushihong"));
        System.out.println(trie.search("app"));
        trie.add("hell");
        List<String> res = trie.keysWithPrefix("hel");
        res.stream().forEach(System.out::println);
         res = trie.keysWithPrefix("h");
        res.stream().forEach(System.out::println);
    }

    public List<String> keysWithPrefix(String prefix) {
        List<String> res = new LinkedList<>();
        prefix = prefix.toLowerCase();
        TNode father = get(prefix, root, 0);
        collect(res, father, new StringBuilder(prefix));
        return res;
    }

    private void collect(List<String> collector, TNode node, StringBuilder stringBuilder) {
        if (node == null) {
            return;
        }
        if (node.exists) {
            collector.add(stringBuilder.toString());
        }
        for (int i = 0; i < R; i++) {
            collect(collector, node.links[i], stringBuilder.append(i == 26 ? ' ' : (char) ('a' + i)));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
    }

    private TNode get(String prefix, TNode node, int i) {
        if (node == null) {
            return null;
        }
        if (i == prefix.length()) {
            return node;
        }
        return get(prefix, node.links[index(prefix.charAt(i))], i + 1);
    }

    private int index(char c) {
        if (c == ' ') {
            return 26;
        }
        return c - 'a';
    }
}
