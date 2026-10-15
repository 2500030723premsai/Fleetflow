# FleetFlow – Intelligent Logistics Management Platform

FleetFlow is a Java 21 terminal-based BTech project prototype for managing basic logistics records and demonstrating Data Structures and Algorithms (DSA) through shipment search, route planning, driver assignment, capacity checks, delivery status tracking and analytics.

## Requirements
- JDK 21 or later (a compatible recent JDK should also work)
- Terminal, or Eclipse IDE

## Run the application

From this folder, run:

```bash
./scripts/run.sh
```

On Windows, if using Git Bash, the same script can be run there. Alternatively compile the `.java` files in `src` into a build directory and run `fleetflow.main.Main`.

In Eclipse: import this folder as a Java project, ensure `src` is a source folder, and run `src/fleetflow/main/Main.java` as a Java Application.

## Run automated algorithm tests

```bash
./scripts/test.sh
```

The test suite uses only the JDK and checks representative cases for string matching, edit distance, bitmask TSP, max flow/min cut, bipartite matching, vertex cover, randomized QuickSort, parallel reduction, reservoir sampling, suffix array and LCP.

## Main terminal features
- Vehicle, driver, package, warehouse and delivery management
- Driver availability and automatic assignment
- Route listing and small-instance route optimization using bitmask dynamic programming
- Address suggestions using edit distance
- Shipment search using KMP
- Delivery priority sorting using randomized QuickSort
- Capacity checks, status transitions and summary analytics
- Sample-data loading

## Algorithm packages
- `engine/string`: KMP, Rabin–Karp, Aho–Corasick multi-pattern search
- `engine/suffix`: suffix array and Kasai LCP
- `engine/dp`: Levenshtein, Damerau–Levenshtein and bitmask TSP
- `engine/flow`: Edmonds–Karp, Dinic, minimum-cut source-side reachability and bipartite matching
- `engine/approximation`: 2-approximation for undirected vertex cover
- `engine/randomized`: randomized QuickSort, reservoir sampling and parallel reductions

See [`docs/RUBRIC_MAPPING.md`](docs/RUBRIC_MAPPING.md) for a criterion-by-criterion mapping, complexity notes, demo evidence and known limitations.

## Important limitations
- This is an educational prototype, not a production logistics platform.
- Records are stored in memory and reset when the program closes.
- Bitmask TSP has exponential complexity and is intended for a small number of locations.
- The suffix-array implementation uses a simple suffix comparison sort and is intended for learning/small inputs.
- The test suite covers representative algorithm cases, not every menu workflow or all possible inputs.
- The ZIP itself cannot establish GitHub history or individual contribution. Push the project to your own repository and make genuine, meaningful commits if the rubric requires development evidence.
