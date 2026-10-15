package fleetflow.engine.string;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedHashMap;
import java.util.Set;

/** Multi-pattern search using a trie, failure links, and output links (Aho-Corasick). */
public final class AhoCorasick {
    private AhoCorasick() {}

    private static final class Node {
        final Map<Character, Node> next = new LinkedHashMap<>();
        Node failure;
        final Set<String> outputs = new LinkedHashSet<>();
    }

    /** Returns each distinct non-empty pattern found in text, in first-discovery order. */
    public static List<String> find(String text, String[] patterns) {
        List<String> found = new ArrayList<>();
        if (text == null || patterns == null || patterns.length == 0) return found;

        Node root = new Node();
        root.failure = root;
        for (String pattern : patterns) {
            if (pattern == null || pattern.isEmpty()) continue;
            Node node = root;
            for (int i = 0; i < pattern.length(); i++) {
                node = node.next.computeIfAbsent(pattern.charAt(i), ignored -> new Node());
            }
            node.outputs.add(pattern);
        }

        Queue<Node> queue = new ArrayDeque<>();
        for (Node child : root.next.values()) {
            child.failure = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            for (Map.Entry<Character, Node> edge : current.next.entrySet()) {
                char ch = edge.getKey();
                Node child = edge.getValue();
                Node fallback = current.failure;
                while (fallback != root && !fallback.next.containsKey(ch)) fallback = fallback.failure;
                if (fallback.next.containsKey(ch) && fallback.next.get(ch) != child) {
                    child.failure = fallback.next.get(ch);
                } else {
                    child.failure = root;
                }
                child.outputs.addAll(child.failure.outputs);
                queue.add(child);
            }
        }

        Set<String> unique = new LinkedHashSet<>();
        Node state = root;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            while (state != root && !state.next.containsKey(ch)) state = state.failure;
            state = state.next.getOrDefault(ch, root);
            unique.addAll(state.outputs);
        }
        found.addAll(unique);
        return found;
    }
}
