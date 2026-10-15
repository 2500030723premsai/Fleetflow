package fleetflow;

import java.util.*;

/*
 * ============================================================
 * FLEETFLOW
 * Intelligent Logistics Management Platform
 * ============================================================
 *
 * Main DSA:
 *
 * 1. KMP                  -> Shipment ID Search
 * 2. Z-Function           -> Shipment Pattern Search
 * 3. Rabin-Karp           -> Package Code Search
 * 4. Aho-Corasick         -> Delivery Status Search
 *
 * 5. Suffix Array         -> Document Indexing
 * 6. Kasai LCP            -> Repeated Text Detection
 * 7. Suffix Tree          -> Document Search
 *
 * 8. Levenshtein          -> Address Correction
 * 9. Damerau-Levenshtein  -> Address Correction
 * 10. Bitmask DP          -> Route Optimization
 * 11. Matrix Chain        -> Operation Optimization
 * 12. Optimal BST         -> Shipment Key Search
 *
 * 13. Edmonds-Karp        -> Warehouse Flow
 * 14. Driver Matching     -> Driver-Task Assignment
 *
 * ============================================================
 */

public class FleetFlowAlgorithms {

    // ============================================================
    // DATABASE-LIKE STORAGE
    // ============================================================

    static ArrayList<String> shipmentIds = new ArrayList<>();
    static ArrayList<String> vehicleIds = new ArrayList<>();
    static ArrayList<String> driverIds = new ArrayList<>();
    static ArrayList<String> warehouseIds = new ArrayList<>();
    static ArrayList<String> packageIds = new ArrayList<>();


    // ============================================================
    // 1. KMP ALGORITHM
    // ============================================================

    static int[] buildLPS(String pattern) {

        int[] lps = new int[pattern.length()];

        int length = 0;
        int i = 1;

        while (i < pattern.length()) {

            if (pattern.charAt(i) == pattern.charAt(length)) {

                length++;
                lps[i] = length;
                i++;

            } else {

                if (length != 0) {

                    length = lps[length - 1];

                } else {

                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }


    static List<Integer> KMP(String text, String pattern) {

        List<Integer> result = new ArrayList<>();

        if (pattern.length() == 0) {
            return result;
        }

        int[] lps = buildLPS(pattern);

        int i = 0;
        int j = 0;

        while (i < text.length()) {

            if (text.charAt(i) == pattern.charAt(j)) {

                i++;
                j++;
            }

            if (j == pattern.length()) {

                result.add(i - j);

                j = lps[j - 1];

            } else if (i < text.length()
                    && text.charAt(i) != pattern.charAt(j)) {

                if (j != 0) {

                    j = lps[j - 1];

                } else {

                    i++;
                }
            }
        }

        return result;
    }


    // ============================================================
    // 2. Z FUNCTION
    // ============================================================

    static int[] zFunction(String s) {

        int n = s.length();

        int[] z = new int[n];

        int left = 0;
        int right = 0;

        for (int i = 1; i < n; i++) {

            if (i <= right) {

                z[i] = Math.min(
                        right - i + 1,
                        z[i - left]
                );
            }

            while (i + z[i] < n
                    && s.charAt(z[i]) == s.charAt(i + z[i])) {

                z[i]++;
            }

            if (i + z[i] - 1 > right) {

                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }


    // ============================================================
    // 3. RABIN-KARP
    // ============================================================

    static List<Integer> rabinKarp(
            String text,
            String pattern) {

        List<Integer> result = new ArrayList<>();

        if (pattern.length() == 0
                || pattern.length() > text.length()) {

            return result;
        }

        int d = 256;
        int q = 101;

        int m = pattern.length();
        int n = text.length();

        int patternHash = 0;
        int textHash = 0;

        int h = 1;

        for (int i = 0; i < m - 1; i++) {

            h = (h * d) % q;
        }

        for (int i = 0; i < m; i++) {

            patternHash =
                    (d * patternHash
                            + pattern.charAt(i)) % q;

            textHash =
                    (d * textHash
                            + text.charAt(i)) % q;
        }

        for (int i = 0; i <= n - m; i++) {

            if (patternHash == textHash) {

                boolean match = true;

                for (int j = 0; j < m; j++) {

                    if (text.charAt(i + j)
                            != pattern.charAt(j)) {

                        match = false;
                        break;
                    }
                }

                if (match) {

                    result.add(i);
                }
            }

            if (i < n - m) {

                textHash =
                        (d * (
                                textHash
                                        - text.charAt(i) * h
                        )
                                + text.charAt(i + m)) % q;

                if (textHash < 0) {

                    textHash += q;
                }
            }
        }

        return result;
    }


    // ============================================================
    // 4. AHO-CORASICK
    // ============================================================

    static class AhoCorasick {

        static class Node {

            int[] next = new int[26];

            int fail = 0;

            ArrayList<String> output =
                    new ArrayList<>();

            Node() {

                Arrays.fill(next, -1);
            }
        }

        ArrayList<Node> nodes =
                new ArrayList<>();


        AhoCorasick() {

            nodes.add(new Node());
        }


        void addPattern(String pattern) {

            int current = 0;

            for (char c : pattern.toLowerCase().toCharArray()) {

                if (c < 'a' || c > 'z') {
                    continue;
                }

                int index = c - 'a';

                if (nodes.get(current).next[index] == -1) {

                    nodes.get(current).next[index] =
                            nodes.size();

                    nodes.add(new Node());
                }

                current =
                        nodes.get(current).next[index];
            }

            nodes.get(current).output.add(pattern);
        }


        void build() {

            Queue<Integer> queue =
                    new LinkedList<>();

            for (int i = 0; i < 26; i++) {

                int child =
                        nodes.get(0).next[i];

                if (child == -1) {

                    nodes.get(0).next[i] = 0;

                } else {

                    nodes.get(child).fail = 0;

                    queue.add(child);
                }
            }


            while (!queue.isEmpty()) {

                int current = queue.poll();

                for (int i = 0; i < 26; i++) {

                    int child =
                            nodes.get(current).next[i];

                    if (child == -1) {

                        nodes.get(current).next[i] =
                                nodes.get(
                                        nodes.get(current).fail
                                ).next[i];

                    } else {

                        nodes.get(child).fail =
                                nodes.get(
                                        nodes.get(current).fail
                                ).next[i];

                        nodes.get(child).output.addAll(
                                nodes.get(
                                        nodes.get(child).fail
                                ).output
                        );

                        queue.add(child);
                    }
                }
            }
        }


        ArrayList<String> search(String text) {

            ArrayList<String> result =
                    new ArrayList<>();

            int current = 0;

            for (char c : text.toLowerCase().toCharArray()) {

                if (c < 'a' || c > 'z') {

                    current = 0;
                    continue;
                }

                current =
                        nodes.get(current)
                                .next[c - 'a'];

                result.addAll(
                        nodes.get(current).output
                );
            }

            return result;
        }
    }


    // ============================================================
    // 5. SUFFIX ARRAY
    // ============================================================

    static int[] suffixArray(String text) {

        int n = text.length();

        Integer[] suffixes =
                new Integer[n];

        for (int i = 0; i < n; i++) {

            suffixes[i] = i;
        }

        Arrays.sort(
                suffixes,
                (a, b) -> text.substring(a)
                        .compareTo(text.substring(b))
        );

        int[] result = new int[n];

        for (int i = 0; i < n; i++) {

            result[i] = suffixes[i];
        }

        return result;
    }


    // ============================================================
    // 6. KASAI LCP
    // ============================================================

    static int[] kasai(
            String text,
            int[] suffixArray) {

        int n = text.length();

        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {

            rank[suffixArray[i]] = i;
        }

        int[] lcp = new int[n];

        int k = 0;

        for (int i = 0; i < n; i++) {

            int r = rank[i];

            if (r == 0) {

                continue;
            }

            int j = suffixArray[r - 1];

            while (i + k < n
                    && j + k < n
                    && text.charAt(i + k)
                    == text.charAt(j + k)) {

                k++;
            }

            lcp[r] = k;

            if (k > 0) {

                k--;
            }
        }

        return lcp;
    }


    // ============================================================
    // 7. SUFFIX TREE / SUFFIX TRIE
    // ============================================================

    static class SuffixTree {

        static class Node {

            HashMap<Character, Node> children =
                    new HashMap<>();

            boolean end;
        }

        Node root = new Node();


        void insert(String word) {

            Node current = root;

            for (char c : word.toCharArray()) {

                current =
                        current.children.computeIfAbsent(
                                c,
                                k -> new Node()
                        );
            }

            current.end = true;
        }


        void build(String text) {

            for (int i = 0; i < text.length(); i++) {

                insert(text.substring(i));
            }
        }


        boolean search(String pattern) {

            Node current = root;

            for (char c : pattern.toCharArray()) {

                if (!current.children.containsKey(c)) {

                    return false;
                }

                current =
                        current.children.get(c);
            }

            return true;
        }
    }


    // ============================================================
    // 8. LEVENSHTEIN DISTANCE
    // ============================================================

    static int levenshtein(
            String a,
            String b) {

        int[][] dp =
                new int[a.length() + 1]
                        [b.length() + 1];


        for (int i = 0; i <= a.length(); i++) {

            dp[i][0] = i;
        }


        for (int j = 0; j <= b.length(); j++) {

            dp[0][j] = j;
        }


        for (int i = 1; i <= a.length(); i++) {

            for (int j = 1; j <= b.length(); j++) {

                int cost =
                        a.charAt(i - 1)
                                == b.charAt(j - 1)
                                ? 0 : 1;


                dp[i][j] =
                        Math.min(
                                Math.min(
                                        dp[i - 1][j] + 1,
                                        dp[i][j - 1] + 1
                                ),
                                dp[i - 1][j - 1]
                                        + cost
                        );
            }
        }

        return dp[a.length()][b.length()];
    }


    // ============================================================
    // 9. DAMERAU-LEVENSHTEIN
    // ============================================================

    static int damerauLevenshtein(
            String a,
            String b) {

        int[][] dp =
                new int[a.length() + 1]
                        [b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {

            dp[i][0] = i;
        }

        for (int j = 0; j <= b.length(); j++) {

            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {

            for (int j = 1; j <= b.length(); j++) {

                int cost =
                        a.charAt(i - 1)
                                == b.charAt(j - 1)
                                ? 0 : 1;


                dp[i][j] =
                        Math.min(
                                Math.min(
                                        dp[i - 1][j] + 1,
                                        dp[i][j - 1] + 1
                                ),
                                dp[i - 1][j - 1] + cost
                        );


                if (i > 1
                        && j > 1
                        && a.charAt(i - 1)
                        == b.charAt(j - 2)
                        && a.charAt(i - 2)
                        == b.charAt(j - 1)) {

                    dp[i][j] =
                            Math.min(
                                    dp[i][j],
                                    dp[i - 2][j - 2] + 1
                            );
                }
            }
        }

        return dp[a.length()][b.length()];
    }


    // ============================================================
    // 10. BITMASK DP - TSP
    // ============================================================

    static int tsp(int[][] graph) {

        int n = graph.length;

        int totalMasks = 1 << n;

        int[][] dp =
                new int[totalMasks][n];


        for (int[] row : dp) {

            Arrays.fill(
                    row,
                    Integer.MAX_VALUE / 2
            );
        }


        dp[1][0] = 0;


        for (int mask = 1;
             mask < totalMasks;
             mask++) {

            for (int u = 0; u < n; u++) {

                if ((mask & (1 << u)) == 0) {

                    continue;
                }


                for (int v = 0; v < n; v++) {

                    if ((mask & (1 << v)) != 0) {

                        continue;
                    }


                    int newMask =
                            mask | (1 << v);


                    dp[newMask][v] =
                            Math.min(
                                    dp[newMask][v],
                                    dp[mask][u]
                                            + graph[u][v]
                            );
                }
            }
        }


        int answer =
                Integer.MAX_VALUE;


        for (int i = 1; i < n; i++) {

            answer =
                    Math.min(
                            answer,
                            dp[totalMasks - 1][i]
                                    + graph[i][0]
                    );
        }

        return answer;
    }


    // ============================================================
    // 11. MATRIX CHAIN MULTIPLICATION
    // ============================================================

    static int matrixChain(int[] p) {

        int n = p.length - 1;

        int[][] dp =
                new int[n][n];


        for (int length = 2;
             length <= n;
             length++) {

            for (int i = 0;
                 i <= n - length;
                 i++) {

                int j =
                        i + length - 1;


                dp[i][j] =
                        Integer.MAX_VALUE;


                for (int k = i;
                     k < j;
                     k++) {

                    int cost =
                            dp[i][k]
                                    + dp[k + 1][j]
                                    + p[i]
                                    * p[k + 1]
                                    * p[j + 1];


                    dp[i][j] =
                            Math.min(
                                    dp[i][j],
                                    cost
                            );
                }
            }
        }

        return dp[0][n - 1];
    }


    // ============================================================
    // 12. OPTIMAL BST
    // ============================================================

    static int optimalBST(
            int[] keys,
            int[] frequency) {

        int n = keys.length;

        int[][] cost =
                new int[n][n];


        for (int i = 0; i < n; i++) {

            cost[i][i] =
                    frequency[i];
        }


        for (int length = 2;
             length <= n;
             length++) {

            for (int i = 0;
                 i <= n - length;
                 i++) {

                int j =
                        i + length - 1;


                cost[i][j] =
                        Integer.MAX_VALUE;


                int sum = 0;

                for (int k = i;
                     k <= j;
                     k++) {

                    sum += frequency[k];
                }


                for (int r = i;
                     r <= j;
                     r++) {

                    int left =
                            r > i
                                    ? cost[i][r - 1]
                                    : 0;


                    int right =
                            r < j
                                    ? cost[r + 1][j]
                                    : 0;


                    cost[i][j] =
                            Math.min(
                                    cost[i][j],
                                    left + right + sum
                            );
                }
            }
        }

        return cost[0][n - 1];
    }


    // ============================================================
    // 13. EDMONDS-KARP
    // ============================================================

    static int edmondsKarp(
            int[][] capacity,
            int source,
            int sink) {

        int n = capacity.length;

        int[][] residual =
                new int[n][n];


        for (int i = 0; i < n; i++) {

            residual[i] =
                    capacity[i].clone();
        }


        int maxFlow = 0;


        while (true) {

            int[] parent =
                    new int[n];

            Arrays.fill(parent, -1);

            parent[source] = source;


            Queue<Integer> queue =
                    new LinkedList<>();

            queue.add(source);


            while (!queue.isEmpty()
                    && parent[sink] == -1) {

                int u = queue.poll();


                for (int v = 0;
                     v < n;
                     v++) {

                    if (parent[v] == -1
                            && residual[u][v] > 0) {

                        parent[v] = u;

                        queue.add(v);
                    }
                }
            }


            if (parent[sink] == -1) {

                break;
            }


            int pathFlow =
                    Integer.MAX_VALUE;


            for (int v = sink;
                 v != source;
                 v = parent[v]) {

                int u = parent[v];

                pathFlow =
                        Math.min(
                                pathFlow,
                                residual[u][v]
                        );
            }


            for (int v = sink;
                 v != source;
                 v = parent[v]) {

                int u = parent[v];

                residual[u][v] -= pathFlow;

                residual[v][u] += pathFlow;
            }


            maxFlow += pathFlow;
        }

        return maxFlow;
    }


    // ============================================================
    // 14. BIPARTITE MATCHING
    // ============================================================

    static boolean matchDriver(
            int driver,
            boolean[][] graph,
            boolean[] visited,
            int[] assignedTask) {

        for (int task = 0;
             task < graph[driver].length;
             task++) {

            if (!graph[driver][task]
                    || visited[task]) {

                continue;
            }


            visited[task] = true;


            if (assignedTask[task] == -1
                    || matchDriver(
                            assignedTask[task],
                            graph,
                            visited,
                            assignedTask)) {

                assignedTask[task] = driver;

                return true;
            }
        }

        return false;
    }


    static int maxMatching(
            boolean[][] graph) {

        int drivers = graph.length;

        int tasks = graph[0].length;


        int[] assignedTask =
                new int[tasks];


        Arrays.fill(
                assignedTask,
                -1
        );


        int result = 0;


        for (int driver = 0;
             driver < drivers;
             driver++) {

            boolean[] visited =
                    new boolean[tasks];


            if (matchDriver(
                    driver,
                    graph,
                    visited,
                    assignedTask)) {

                result++;
            }
        }


        System.out.println("\nDriver Assignment:");

        for (int task = 0;
             task < tasks;
             task++) {

            if (assignedTask[task] != -1) {

                System.out.println(
                        "Task " + task
                                + " -> Driver "
                                + assignedTask[task]
                );
            }
        }


        return result;
    }


    // ============================================================
    // ADD SHIPMENT ID
    // ============================================================

    static void addShipmentID(Scanner sc) {

        System.out.println(
                "\n========== ADD SHIPMENT ID ==========");

        System.out.print("Enter Shipment ID: ");

        String id =
                sc.nextLine().trim();


        if (id.isEmpty()) {

            System.out.println(
                    "Shipment ID cannot be empty.");

            return;
        }


        if (shipmentIds.contains(id)) {

            System.out.println(
                    "Shipment ID already exists.");

            return;
        }


        shipmentIds.add(id);


        System.out.println(
                "Shipment ID added successfully!");

        System.out.println(
                "Added ID: " + id);
    }


    // ============================================================
    // ADD VEHICLE ID
    // ============================================================

    static void addVehicleID(Scanner sc) {

        System.out.println(
                "\n========== ADD VEHICLE ID ==========");

        System.out.print("Enter Vehicle ID: ");

        String id =
                sc.nextLine().trim();


        if (id.isEmpty()) {

            System.out.println(
                    "Vehicle ID cannot be empty.");

            return;
        }


        if (vehicleIds.contains(id)) {

            System.out.println(
                    "Vehicle ID already exists.");

            return;
        }


        vehicleIds.add(id);


        System.out.println(
                "Vehicle ID added successfully!");

        System.out.println(
                "Added ID: " + id);
    }


    // ============================================================
    // ADD DRIVER ID
    // ============================================================

    static void addDriverID(Scanner sc) {

        System.out.println(
                "\n========== ADD DRIVER ID ==========");

        System.out.print("Enter Driver ID: ");

        String id =
                sc.nextLine().trim();


        if (id.isEmpty()) {

            System.out.println(
                    "Driver ID cannot be empty.");

            return;
        }


        if (driverIds.contains(id)) {

            System.out.println(
                    "Driver ID already exists.");

            return;
        }


        driverIds.add(id);


        System.out.println(
                "Driver ID added successfully!");

        System.out.println(
                "Added ID: " + id);
    }


    // ============================================================
    // ADD WAREHOUSE ID
    // ============================================================

    static void addWarehouseID(Scanner sc) {

        System.out.println(
                "\n========== ADD WAREHOUSE ID ==========");

        System.out.print("Enter Warehouse ID: ");

        String id =
                sc.nextLine().trim();


        if (id.isEmpty()) {

            System.out.println(
                    "Warehouse ID cannot be empty.");

            return;
        }


        if (warehouseIds.contains(id)) {

            System.out.println(
                    "Warehouse ID already exists.");

            return;
        }


        warehouseIds.add(id);


        System.out.println(
                "Warehouse ID added successfully!");

        System.out.println(
                "Added ID: " + id);
    }


    // ============================================================
    // ADD PACKAGE ID
    // ============================================================

    static void addPackageID(Scanner sc) {

        System.out.println(
                "\n========== ADD PACKAGE ID ==========");

        System.out.print("Enter Package ID: ");

        String id =
                sc.nextLine().trim();


        if (id.isEmpty()) {

            System.out.println(
                    "Package ID cannot be empty.");

            return;
        }


        if (packageIds.contains(id)) {

            System.out.println(
                    "Package ID already exists.");

            return;
        }


        packageIds.add(id);


        System.out.println(
                "Package ID added successfully!");

        System.out.println(
                "Added ID: " + id);
    }


    // ============================================================
    // DISPLAY ALL IDs
    // ============================================================

    static void displayAllIDs() {

        System.out.println(
                "\n========== FLEETFLOW IDs ==========");


        System.out.println("\nShipment IDs:");

        if (shipmentIds.isEmpty()) {

            System.out.println("No Shipment IDs.");

        } else {

            for (String id : shipmentIds) {

                System.out.println("- " + id);
            }
        }


        System.out.println("\nVehicle IDs:");

        if (vehicleIds.isEmpty()) {

            System.out.println("No Vehicle IDs.");

        } else {

            for (String id : vehicleIds) {

                System.out.println("- " + id);
            }
        }


        System.out.println("\nDriver IDs:");

        if (driverIds.isEmpty()) {

            System.out.println("No Driver IDs.");

        } else {

            for (String id : driverIds) {

                System.out.println("- " + id);
            }
        }


        System.out.println("\nWarehouse IDs:");

        if (warehouseIds.isEmpty()) {

            System.out.println("No Warehouse IDs.");

        } else {

            for (String id : warehouseIds) {

                System.out.println("- " + id);
            }
        }


        System.out.println("\nPackage IDs:");

        if (packageIds.isEmpty()) {

            System.out.println("No Package IDs.");

        } else {

            for (String id : packageIds) {

                System.out.println("- " + id);
            }
        }
    }


    // ============================================================
    // SEARCH SHIPMENT ID
    // ============================================================

    static void searchShipmentID(Scanner sc) {

        System.out.println(
                "\n========== KMP SHIPMENT SEARCH ==========");


        if (shipmentIds.isEmpty()) {

            System.out.println(
                    "No Shipment IDs available.");

            return;
        }


        System.out.print(
                "Enter Shipment ID to search: ");

        String search =
                sc.nextLine().trim();


        boolean found = false;


        for (String id : shipmentIds) {

            List<Integer> positions =
                    KMP(id, search);


            if (!positions.isEmpty()) {

                System.out.println(
                        "\nShipment ID FOUND!");

                System.out.println(
                        "ID: " + id);

                System.out.println(
                        "Position: " + positions);

                found = true;
            }
        }


        if (!found) {

            System.out.println(
                    "\nShipment ID NOT FOUND.");
        }
    }


    // ============================================================
    // MAIN MENU
    // ============================================================

    public static void main(String[] args) {

        Scanner sc =
                new Scanner(System.in);


        while (true) {

            System.out.println();
            System.out.println(
                    "================================================");
            System.out.println(
                    "                 FLEETFLOW");
            System.out.println(
                    "       INTELLIGENT LOGISTICS PLATFORM");
            System.out.println(
                    "================================================");


            System.out.println(
                    "\nID MANAGEMENT");

            System.out.println(
                    "------------------------------------------------");

            System.out.println(
                    "1.  Add Shipment ID");

            System.out.println(
                    "2.  Add Vehicle ID");

            System.out.println(
                    "3.  Add Driver ID");

            System.out.println(
                    "4.  Add Warehouse ID");

            System.out.println(
                    "5.  Add Package ID");

            System.out.println(
                    "6.  View All IDs");


            System.out.println(
                    "\nSTRING ALGORITHMS");

            System.out.println(
                    "------------------------------------------------");

            System.out.println(
                    "7.  KMP - Shipment ID Search");

            System.out.println(
                    "8.  Z-Function - Shipment Pattern");

            System.out.println(
                    "9.  Rabin-Karp - Package Search");

            System.out.println(
                    "10. Aho-Corasick - Delivery Status");


            System.out.println(
                    "\nSUFFIX STRUCTURES");

            System.out.println(
                    "------------------------------------------------");

            System.out.println(
                    "11. Suffix Array - Document Indexing");

            System.out.println(
                    "12. Kasai LCP - Repeated Text");

            System.out.println(
                    "13. Suffix Tree - Document Search");


            System.out.println(
                    "\nDYNAMIC PROGRAMMING");

            System.out.println(
                    "------------------------------------------------");

            System.out.println(
                    "14. Levenshtein - Address Correction");

            System.out.println(
                    "15. Damerau-Levenshtein - Address Correction");

            System.out.println(
                    "16. Bitmask DP - Route Optimization");

            System.out.println(
                    "17. Matrix Chain - Operation Optimization");

            System.out.println(
                    "18. Optimal BST - Shipment Key Search");


            System.out.println(
                    "\nNETWORK FLOW");

            System.out.println(
                    "------------------------------------------------");

            System.out.println(
                    "19. Edmonds-Karp - Warehouse Flow");

            System.out.println(
                    "20. Driver Matching - Task Assignment");


            System.out.println(
                    "\n0. Exit");

            System.out.println(
                    "================================================");

            System.out.print(
                    "Enter your choice: ");


            int choice;


            try {

                choice =
                        sc.nextInt();

            } catch (InputMismatchException e) {

                System.out.println(
                        "Please enter a valid number.");

                sc.nextLine();

                continue;
            }


            sc.nextLine();


            // ====================================================
            // 1. ADD SHIPMENT
            // ====================================================

            if (choice == 1) {

                addShipmentID(sc);
            }


            // ====================================================
            // 2. ADD VEHICLE
            // ====================================================

            else if (choice == 2) {

                addVehicleID(sc);
            }


            // ====================================================
            // 3. ADD DRIVER
            // ====================================================

            else if (choice == 3) {

                addDriverID(sc);
            }


            // ====================================================
            // 4. ADD WAREHOUSE
            // ====================================================

            else if (choice == 4) {

                addWarehouseID(sc);
            }


            // ====================================================
            // 5. ADD PACKAGE
            // ====================================================

            else if (choice == 5) {

                addPackageID(sc);
            }


            // ====================================================
            // 6. VIEW IDs
            // ====================================================

            else if (choice == 6) {

                displayAllIDs();
            }


            // ====================================================
            // 7. KMP
            // ====================================================

            else if (choice == 7) {

                searchShipmentID(sc);
            }


            // ====================================================
            // 8. Z FUNCTION
            // ====================================================

            else if (choice == 8) {

                System.out.println(
                        "\n========== Z-FUNCTION ==========");

                System.out.print(
                        "Enter shipment text: ");

                String text =
                        sc.nextLine();


                System.out.print(
                        "Enter pattern: ");

                String pattern =
                        sc.nextLine();


                String combined =
                        pattern + "$" + text;


                int[] z =
                        zFunction(combined);


                ArrayList<Integer> positions =
                        new ArrayList<>();


                for (int i = pattern.length() + 1;
                     i < combined.length();
                     i++) {

                    if (z[i] == pattern.length()) {

                        positions.add(
                                i - pattern.length() - 1
                        );
                    }
                }


                System.out.println(
                        "Z Array: "
                                + Arrays.toString(z));


                if (positions.isEmpty()) {

                    System.out.println(
                            "Pattern NOT FOUND.");

                } else {

                    System.out.println(
                            "Pattern FOUND at positions: "
                                    + positions);
                }
            }


            // ====================================================
            // 9. RABIN KARP
            // ====================================================

            else if (choice == 9) {

                System.out.println(
                        "\n========== RABIN-KARP ==========");


                System.out.print(
                        "Enter package data: ");

                String text =
                        sc.nextLine();


                System.out.print(
                        "Enter package code: ");

                String pattern =
                        sc.nextLine();


                List<Integer> positions =
                        rabinKarp(text, pattern);


                if (positions.isEmpty()) {

                    System.out.println(
                            "Package code NOT FOUND.");

                } else {

                    System.out.println(
                            "Package code FOUND.");

                    System.out.println(
                            "Positions: "
                                    + positions);
                }
            }


            // ====================================================
            // 10. AHO CORASICK
            // ====================================================

            else if (choice == 10) {

                System.out.println(
                        "\n========== AHO-CORASICK ==========");


                AhoCorasick ac =
                        new AhoCorasick();


                System.out.print(
                        "Enter number of status patterns: ");

                int n =
                        sc.nextInt();

                sc.nextLine();


                for (int i = 0;
                     i < n;
                     i++) {

                    System.out.print(
                            "Enter status pattern "
                                    + (i + 1) + ": ");

                    ac.addPattern(
                            sc.nextLine()
                    );
                }


                ac.build();


                System.out.print(
                        "Enter delivery message: ");

                String message =
                        sc.nextLine();


                ArrayList<String> found =
                        ac.search(message);


                if (found.isEmpty()) {

                    System.out.println(
                            "No status pattern found.");

                } else {

                    System.out.println(
                            "Status patterns found:");

                    for (String s : found) {

                        System.out.println(
                                "- " + s);
                    }
                }
            }


            // ====================================================
            // 11. SUFFIX ARRAY
            // ====================================================

            else if (choice == 11) {

                System.out.println(
                        "\n========== SUFFIX ARRAY ==========");


                System.out.print(
                        "Enter logistics document text: ");

                String text =
                        sc.nextLine();


                int[] sa =
                        suffixArray(text);


                System.out.println(
                        "\nSuffix Array:");

                for (int i = 0;
                     i < sa.length;
                     i++) {

                    System.out.println(
                            sa[i]
                                    + " -> "
                                    + text.substring(sa[i]));
                }
            }


            // ====================================================
            // 12. KASAI
            // ====================================================

            else if (choice == 12) {

                System.out.println(
                        "\n========== KASAI LCP ==========");


                System.out.print(
                        "Enter logistics document: ");

                String text =
                        sc.nextLine();


                int[] sa =
                        suffixArray(text);


                int[] lcp =
                        kasai(text, sa);


                System.out.println(
                        "LCP Array:");

                System.out.println(
                        Arrays.toString(lcp));
            }


            // ====================================================
            // 13. SUFFIX TREE
            // ====================================================

            else if (choice == 13) {

                System.out.println(
                        "\n========== SUFFIX TREE ==========");


                System.out.print(
                        "Enter document text: ");

                String text =
                        sc.nextLine();


                SuffixTree tree =
                        new SuffixTree();


                tree.build(text);


                System.out.print(
                        "Enter substring to search: ");

                String pattern =
                        sc.nextLine();


                if (tree.search(pattern)) {

                    System.out.println(
                            "Substring FOUND.");

                } else {

                    System.out.println(
                            "Substring NOT FOUND.");
                }
            }


            // ====================================================
            // 14. LEVENSHTEIN
            // ====================================================

            else if (choice == 14) {

                System.out.println(
                        "\n========== LEVENSHTEIN ==========");


                System.out.print(
                        "Enter original address: ");

                String a =
                        sc.nextLine();


                System.out.print(
                        "Enter entered address: ");

                String b =
                        sc.nextLine();


                int distance =
                        levenshtein(a, b);


                System.out.println(
                        "Edit Distance: "
                                + distance);
            }


            // ====================================================
            // 15. DAMERAU
            // ====================================================

            else if (choice == 15) {

                System.out.println(
                        "\n========== DAMERAU-LEVENSHTEIN ==========");


                System.out.print(
                        "Enter original address: ");

                String a =
                        sc.nextLine();


                System.out.print(
                        "Enter entered address: ");

                String b =
                        sc.nextLine();


                int distance =
                        damerauLevenshtein(a, b);


                System.out.println(
                        "Damerau Edit Distance: "
                                + distance);
            }


            // ====================================================
            // 16. BITMASK DP
            // ====================================================

            else if (choice == 16) {

                System.out.println(
                        "\n========== BITMASK DP ==========");


                System.out.print(
                        "Enter number of locations (2-10): ");

                int n =
                        sc.nextInt();


                if (n < 2 || n > 10) {

                    System.out.println(
                            "Enter a number between 2 and 10.");

                    sc.nextLine();

                } else {

                    int[][] graph =
                            new int[n][n];


                    System.out.println(
                            "Enter distance matrix:");


                    for (int i = 0;
                         i < n;
                         i++) {

                        for (int j = 0;
                             j < n;
                             j++) {

                            graph[i][j] =
                                    sc.nextInt();
                        }
                    }


                    sc.nextLine();


                    int answer =
                            tsp(graph);


                    System.out.println(
                            "Minimum route cost: "
                                    + answer);
                }
            }


            // ====================================================
            // 17. MATRIX CHAIN
            // ====================================================

            else if (choice == 17) {

                System.out.println(
                        "\n========== MATRIX CHAIN ==========");


                System.out.print(
                        "Enter number of matrices: ");

                int n =
                        sc.nextInt();


                if (n <= 0) {

                    System.out.println(
                            "Number must be positive.");

                    sc.nextLine();

                } else {

                    int[] dimensions =
                            new int[n + 1];


                    System.out.println(
                            "Enter "
                                    + (n + 1)
                                    + " dimensions:");


                    for (int i = 0;
                         i <= n;
                         i++) {

                        dimensions[i] =
                                sc.nextInt();
                    }


                    sc.nextLine();


                    int answer =
                            matrixChain(dimensions);


                    System.out.println(
                            "Minimum multiplication cost: "
                                    + answer);
                }
            }


            // ====================================================
            // 18. OPTIMAL BST
            // ====================================================

            else if (choice == 18) {

                System.out.println(
                        "\n========== OPTIMAL BST ==========");


                System.out.print(
                        "Enter number of shipment keys: ");

                int n =
                        sc.nextInt();


                int[] keys =
                        new int[n];

                int[] frequency =
                        new int[n];


                System.out.println(
                        "Enter shipment keys:");

                for (int i = 0;
                     i < n;
                     i++) {

                    keys[i] =
                            sc.nextInt();
                }


                System.out.println(
                        "Enter access frequencies:");

                for (int i = 0;
                     i < n;
                     i++) {

                    frequency[i] =
                            sc.nextInt();
                }


                sc.nextLine();


                int answer =
                        optimalBST(
                                keys,
                                frequency
                        );


                System.out.println(
                        "Minimum Optimal BST Cost: "
                                + answer);
            }


            // ====================================================
            // 19. EDMONDS KARP
            // ====================================================

            else if (choice == 19) {

                System.out.println(
                        "\n========== EDMONDS-KARP ==========");


                System.out.print(
                        "Enter number of nodes: ");

                int n =
                        sc.nextInt();


                int[][] capacity =
                        new int[n][n];


                System.out.println(
                        "Enter capacity matrix:");


                for (int i = 0;
                     i < n;
                     i++) {

                    for (int j = 0;
                         j < n;
                         j++) {

                        capacity[i][j] =
                                sc.nextInt();
                    }
                }


                System.out.print(
                        "Enter source node: ");

                int source =
                        sc.nextInt();


                System.out.print(
                        "Enter destination node: ");

                int sink =
                        sc.nextInt();


                sc.nextLine();


                int flow =
                        edmondsKarp(
                                capacity,
                                source,
                                sink
                        );


                System.out.println(
                        "Maximum warehouse flow: "
                                + flow);
            }


            // ====================================================
            // 20. DRIVER MATCHING
            // ====================================================

            else if (choice == 20) {

                System.out.println(
                        "\n========== DRIVER-TASK MATCHING ==========");


                System.out.print(
                        "Enter number of drivers: ");

                int drivers =
                        sc.nextInt();


                System.out.print(
                        "Enter number of tasks: ");

                int tasks =
                        sc.nextInt();


                boolean[][] graph =
                        new boolean[drivers][tasks];


                System.out.println(
                        "\nEnter compatibility matrix.");

                System.out.println(
                        "Enter 1 if driver can perform task.");

                System.out.println(
                        "Enter 0 otherwise.");


                for (int i = 0;
                     i < drivers;
                     i++) {

                    for (int j = 0;
                         j < tasks;
                         j++) {

                        int value =
                                sc.nextInt();


                        graph[i][j] =
                                value == 1;
                    }
                }


                sc.nextLine();


                int matched =
                        maxMatching(graph);


                System.out.println(
                        "\nMaximum driver-task assignments: "
                                + matched);
            }


            // ====================================================
            // EXIT
            // ====================================================

            else if (choice == 0) {

                System.out.println();
                System.out.println(
                        "========================================");
                System.out.println(
                        " Thank you for using FleetFlow!");
                System.out.println(
                        "========================================");

                break;
            }


            // ====================================================
            // INVALID CHOICE
            // ====================================================

            else {

                System.out.println(
                        "Invalid choice.");
            }


            // ====================================================
            // RETURN TO MENU
            // ====================================================

            System.out.println();

            System.out.println(
                    "Press ENTER to return to main menu...");

            sc.nextLine();
        }


        sc.close();
    }
}