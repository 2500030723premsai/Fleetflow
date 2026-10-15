package fleetflow.engine.flow;
import java.util.Arrays;
public final class BipartiteMatching {
    private BipartiteMatching() {}

    /**
     * Finds maximum bipartite matching using augmenting paths.
     * @param edges edges[u][v] is true if left vertex u is compatible with right vertex v
     * @return array where match[v] is the left vertex index assigned to right vertex v, or -1 if unassigned
     */
    public static int[] findMatching(boolean[][] edges) {
        int left = edges.length;
        int right = left == 0 ? 0 : edges[0].length;
        int[] match = new int[right];
        Arrays.fill(match, -1);
        for (int u = 0; u < left; u++) {
            augment(u, edges, match, new boolean[right]);
        }
        return match;
    }

    public static int maximumMatching(boolean[][] edges) {
        int[] match = findMatching(edges);
        int count = 0;
        for (int m : match) {
            if (m >= 0) count++;
        }
        return count;
    }

    private static boolean augment(int u, boolean[][] e, int[] m, boolean[] seen) {
        for (int v = 0; v < m.length; v++) {
            if (e[u][v] && !seen[v]) {
                seen[v] = true;
                if (m[v] < 0 || augment(m[v], e, m, seen)) {
                    m[v] = u;
                    return true;
                }
            }
        }
        return false;
    }
}
