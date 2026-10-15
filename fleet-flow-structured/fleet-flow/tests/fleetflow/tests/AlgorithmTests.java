package fleetflow.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;
import fleetflow.engine.approximation.VertexCoverApprox;
import fleetflow.engine.dp.BitmaskTSP;
import fleetflow.engine.dp.DamerauLevenshtein;
import fleetflow.engine.dp.LevenshteinDistance;
import fleetflow.engine.flow.BipartiteMatching;
import fleetflow.engine.flow.Dinic;
import fleetflow.engine.flow.EdmondsKarp;
import fleetflow.engine.flow.MinCut;
import fleetflow.engine.randomized.ParallelReduce;
import fleetflow.engine.randomized.RandomizedQuickSort;
import fleetflow.engine.randomized.ReservoirSampling;
import fleetflow.engine.string.AhoCorasick;
import fleetflow.engine.string.KMP;
import fleetflow.engine.string.RabinKarp;
import fleetflow.engine.suffix.LCPKasai;
import fleetflow.engine.suffix.SuffixArray;

/** Lightweight dependency-free regression tests. Run with scripts/test.sh. */
public final class AlgorithmTests {
    private static int passed;
    private AlgorithmTests() {}

    public static void main(String[] args) {
        test("KMP", () -> check(KMP.contains("fleetflow shipment", "shipment") && !KMP.contains("abc", "abd"), "KMP match/miss"));
        test("Rabin-Karp", () -> check(RabinKarp.contains("aaaaab", "aaab") && !RabinKarp.contains("abc", "acb"), "Rabin-Karp match/miss"));
        test("Aho-Corasick overlapping patterns", () -> check(AhoCorasick.find("ushers", new String[]{"he", "she", "hers", "his", "she"}).equals(List.of("she", "he", "hers")), "Aho-Corasick output"));
        test("Levenshtein", () -> check(LevenshteinDistance.distance("kitten", "sitting") == 3, "Levenshtein distance"));
        test("Damerau-Levenshtein transposition", () -> check(DamerauLevenshtein.distance("ab", "ba") == 1, "Damerau transposition"));
        test("Aho-Corasick suffix/failure links", () -> check(AhoCorasick.find("ababa", new String[]{"aba", "ba", "bab", "a"}).equals(List.of("a", "aba", "ba", "bab")), "Aho-Corasick failure links"));
        test("Bitmask TSP", () -> check(BitmaskTSP.minimumCost(new int[][]{{0, 10, 15}, {10, 0, 20}, {15, 20, 0}}) == 45, "TSP tour cost"));
        test("Bitmask TSP single location", () -> check(BitmaskTSP.solve(new int[][]{{0}}, 0).getTour().equals(List.of(0)), "single-node tour"));
        test("Edmonds-Karp max flow", () -> check(EdmondsKarp.maxFlow(flowGraph(), 0, 3) == 5, "Edmonds-Karp flow"));
        test("Dinic max flow", () -> check(Dinic.maxFlow(flowGraph(), 0, 3) == 5, "Dinic flow"));
        test("Minimum cut source side", () -> check(MinCut.sourceSide(flowGraph(), 0, 3)[0] && !MinCut.sourceSide(flowGraph(), 0, 3)[3], "min-cut reachability"));
        test("Bipartite matching", () -> check(BipartiteMatching.maximumMatching(new boolean[][]{{true, true}, {true, false}}) == 2, "maximum matching"));
        test("Vertex cover approximation", () -> check(coversAll(new int[][]{{0,1},{1,2},{2,3}}, VertexCoverApprox.approximate(new int[][]{{0,1},{1,2},{2,3}})), "cover includes every edge endpoint"));
        test("Randomized QuickSort", () -> { int[] a = {5, -1, 3, 3, 0}; RandomizedQuickSort.sort(a); check(Arrays.equals(a, new int[]{-1,0,3,3,5}), "sorted values"); });
        test("Parallel reduce", () -> check(ParallelReduce.sum(List.of(1, 2, 3, 4)) == 10 && ParallelReduce.countMatching(List.of(1,2,3,4), x -> x % 2 == 0) == 2, "sum/count"));
        test("Reservoir sampling", () -> check(ReservoirSampling.sample(List.of(1,2,3,4), 2, new Random(7)).size() == 2, "sample size"));
        test("Suffix array and LCP", () -> { int[] sa = SuffixArray.build("banana"); check(Arrays.equals(sa, new int[]{5,3,1,0,4,2}), "suffix order"); check(Arrays.equals(LCPKasai.build("banana", sa), new int[]{0,1,3,0,0,2}), "LCP array"); });
        System.out.println("\nRESULT: " + passed + " test groups passed.");
    }

    private static int[][] flowGraph() { return new int[][]{{0,3,2,0},{0,0,1,2},{0,0,0,3},{0,0,0,0}}; }
    private static boolean coversAll(int[][] edges, Set<Integer> cover) { for (int[] e : edges) if (!cover.contains(e[0]) && !cover.contains(e[1])) return false; return true; }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private static void test(String name, Runnable action) { action.run(); passed++; System.out.println("PASS: " + name); }
}
