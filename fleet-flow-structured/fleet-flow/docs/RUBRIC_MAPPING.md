# FleetFlow rubric and DSA mapping

This mapping is intended to help demonstrate the project honestly. A class existing in the repository does not, by itself, prove that the feature uses it at runtime; explain the implemented integrations separately.

| Rubric area | Project mapping | Complexity / discussion | Evidence to show |
|---|---|---|---|
| Problem, architecture and DSA-3 mapping | `fleetflow.main.Main`, `DataStore`, `model`, `service` packages | Explain in-memory model, service layer and terminal menu. | Run the app, load sample data, show vehicle/driver/delivery flows. |
| String algorithms | `KMP`, `RabinKarp`, `AhoCorasick`, `SuffixArray`, `LCPKasai` | KMP O(n+m); Rabin–Karp expected O(n+m), with character verification; Aho–Corasick O(total pattern length + text length + matches); suffix array here uses a simple comparison sort and is not optimized. | Run `scripts/test.sh`; show matching/missing and overlapping patterns. |
| Dynamic programming | `LevenshteinDistance`, `DamerauLevenshtein`, `BitmaskTSP` | Edit distance O(mn) time and O(mn) space; bitmask TSP O(n²2ⁿ) time and O(n2ⁿ) space. | Show address correction and route ordering on a small matrix. |
| Flow, matching and approximation | `EdmondsKarp`, `Dinic`, `MinCut`, `BipartiteMatching`, `VertexCoverApprox` | Edmonds–Karp O(VE²) in graph-list terms; matrix implementation scans V neighbors. Dinic is commonly O(V²E) general bound. Matching via augmenting paths O(VE). Vertex-cover algorithm is a 2-approximation for undirected edge lists. | Show the small graph tests and explain source/sink, capacity and cover. |
| Randomized and parallel | `RandomizedQuickSort`, `ReservoirSampling`, `ParallelReduce` | Randomized QuickSort expected O(n log n), worst-case O(n²); reservoir sampling O(N) time and O(k) memory; parallel reduction has parallel overhead and may not win for small data. | Run regression tests; compare repeated sizes if reporting performance. |
| Testing and performance | `tests/fleetflow/tests/AlgorithmTests.java`, `scripts/test.sh` | Dependency-free deterministic regression suite; randomized pivot means the sort order is tested, not pivot reproducibility. | Include the terminal test output in a demo/report. |
| Repository / contribution evidence | README, source, tests, docs and scripts | A ZIP cannot prove GitHub history, branch usage or individual contribution. | Push to your own repository and retain meaningful commits you actually make. |
| Demonstration and viva | Run instructions in README | Not gradeable from code alone. | Demonstrate the program and explain your own code and decisions. |

## Scope and limitations

- This is an educational, terminal-based prototype. Data is in memory and resets when the program exits.
- Route TSP is exact and exponential, so it is appropriate only for a small number of locations.
- The suffix array is intentionally a simple implementation suitable for small examples, not large datasets.
- The automated suite checks representative algorithm behavior; it is not exhaustive proof that every menu workflow or every edge case is correct.
- Do not claim a Git history, external deployment, or measured speedup unless you have independently produced that evidence.
