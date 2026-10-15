package fleetflow.engine.dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Exact route order for a small distance matrix using Dynamic Programming with Bitmask.
 * Computes the minimum distance tour and reconstructs the visiting sequence.
 */
public final class BitmaskTSP {
    public static final int INF = Integer.MAX_VALUE / 4;

    public static class Result {
        private final int cost;
        private final List<Integer> tour;

        public Result(int cost, List<Integer> tour) {
            this.cost = cost;
            this.tour = tour;
        }

        public int getCost() {
            return cost;
        }

        public List<Integer> getTour() {
            return tour;
        }

        public boolean hasValidTour() {
            return cost < INF && !tour.isEmpty();
        }
    }

    private BitmaskTSP() {}

    public static int minimumCost(int[][] dist) {
        if (dist == null) {
            throw new IllegalArgumentException("Distance matrix must not be null.");
        }
        int n = dist.length;
        if (n == 0) return 0;
        if (n == 1) return 0;
        Result result = solve(dist, 0);
        return result.hasValidTour() ? result.getCost() : INF;
    }

    /**
     * Finds the minimum cost Travelling Salesperson tour starting and ending at the given start index.
     *
     * @param dist distance matrix between locations
     * @param start starting location index (0-based)
     * @return Result containing total cost and sequence of visited node indices
     */
    public static Result solve(int[][] dist, int start) {
        if (dist == null) {
            throw new IllegalArgumentException("Distance matrix must not be null.");
        }
        int n = dist.length;
        for (int i = 0; i < n; i++) {
            if (dist[i] == null || dist[i].length != n) {
                throw new IllegalArgumentException("Distance matrix must be square.");
            }
            for (int j = 0; j < n; j++) {
                if (dist[i][j] < 0) {
                    throw new IllegalArgumentException("Distances must be non-negative.");
                }
            }
        }
        if (n == 0) {
            return new Result(0, Collections.emptyList());
        }
        if (start < 0 || start >= n) {
            throw new IllegalArgumentException("Starting location index is out of bounds: " + start);
        }
        if (n == 1) {
            List<Integer> single = new ArrayList<>();
            single.add(start);
            return new Result(0, single);
        }
        if (n > 20) {
            throw new IllegalArgumentException("Bitmask TSP is intended for small location counts (maximum 20).");
        }

        int size = 1 << n;
        int[][] dp = new int[size][n];
        int[][] parent = new int[size][n];
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }

        dp[1 << start][start] = 0;

        for (int mask = 1; mask < size; mask++) {
            if ((mask & (1 << start)) == 0) {
                continue;
            }
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] >= INF) {
                    continue;
                }
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 && dist[u][v] < INF) {
                        int nextMask = mask | (1 << v);
                        int nextCost = dp[mask][u] + dist[u][v];
                        if (nextCost < dp[nextMask][v]) {
                            dp[nextMask][v] = nextCost;
                            parent[nextMask][v] = u;
                        }
                    }
                }
            }
        }

        int fullMask = size - 1;
        int bestCost = INF;
        int lastNode = -1;

        for (int u = 0; u < n; u++) {
            if (u != start && dp[fullMask][u] < INF && dist[u][start] < INF) {
                int totalCost = dp[fullMask][u] + dist[u][start];
                if (totalCost < bestCost) {
                    bestCost = totalCost;
                    lastNode = u;
                }
            }
        }

        if (bestCost >= INF || lastNode == -1) {
            return new Result(INF, Collections.emptyList());
        }

        List<Integer> tour = new ArrayList<>();
        int curr = lastNode;
        int currMask = fullMask;
        while (curr != -1 && currMask > 0) {
            tour.add(curr);
            int prev = parent[currMask][curr];
            currMask ^= (1 << curr);
            curr = prev;
        }
        Collections.reverse(tour);
        tour.add(start);

        return new Result(bestCost, tour);
    }
}

