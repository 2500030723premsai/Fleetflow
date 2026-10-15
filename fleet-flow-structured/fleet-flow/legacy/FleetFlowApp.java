package fleetflow;

import java.util.*;

/**
 * FleetFlow - Intelligent Logistics Management Platform
 * Single-file Java 17 DSA project.
 *
 * Compile: javac FleetFlowApp.java
 * Run:     java FleetFlowApp
 */
public class FleetFlowApp {

    // ========================= MODEL CLASSES =========================

    static class Vehicle {
        String vehicleId, vehicleType, currentLocation;
        double capacity;
        boolean available;

        Vehicle(String id, String type, double capacity, String location, boolean available) {
            this.vehicleId = id; this.vehicleType = type; this.capacity = capacity;
            this.currentLocation = location; this.available = available;
        }

        public String toString() {
            return vehicleId + " | " + vehicleType + " | " + capacity + "kg | " +
                    currentLocation + " | " + (available ? "Available" : "Unavailable");
        }
    }

    static class Driver {
        String driverId, driverName, phone;
        boolean available;

        Driver(String id, String name, String phone, boolean available) {
            this.driverId = id; this.driverName = name; this.phone = phone; this.available = available;
        }

        public String toString() {
            return driverId + " | " + driverName + " | " + phone + " | " +
                    (available ? "Available" : "Unavailable");
        }
    }

    static class Warehouse {
        String warehouseId, warehouseName, location;
        double capacity;

        Warehouse(String id, String name, String location, double capacity) {
            this.warehouseId = id; this.warehouseName = name;
            this.location = location; this.capacity = capacity;
        }

        public String toString() {
            return warehouseId + " | " + warehouseName + " | " + location + " | " + capacity;
        }
    }

    static class Package {
        String packageId, packageCode, customerName, pickupAddress, deliveryAddress, status;
        double weight;

        Package(String id, String code, String customer, String pickup, String delivery,
                double weight, String status) {
            this.packageId = id; this.packageCode = code; this.customerName = customer;
            this.pickupAddress = pickup; this.deliveryAddress = delivery;
            this.weight = weight; this.status = status;
        }

        public String toString() {
            return packageId + " | " + packageCode + " | " + customerName + " | " +
                    pickupAddress + " -> " + deliveryAddress + " | " + weight +
                    "kg | " + status;
        }
    }

    static class Shipment {
        String shipmentId, packageId, vehicleId, driverId, source, destination, status, shipmentDescription;

        Shipment(String id, String packageId, String vehicleId, String driverId,
                 String source, String destination, String status, String description) {
            this.shipmentId = id; this.packageId = packageId; this.vehicleId = vehicleId;
            this.driverId = driverId; this.source = source; this.destination = destination;
            this.status = status; this.shipmentDescription = description;
        }

        public String toString() {
            return shipmentId + " | Package:" + packageId + " | Vehicle:" + vehicleId +
                    " | Driver:" + driverId + " | " + source + " -> " + destination +
                    " | " + status + " | " + shipmentDescription;
        }
    }

    static class DeliveryDocument {
        String documentId, shipmentId, documentText;

        DeliveryDocument(String id, String shipmentId, String text) {
            this.documentId = id; this.shipmentId = shipmentId; this.documentText = text;
        }

        public String toString() {
            return documentId + " | Shipment:" + shipmentId + " | " + documentText;
        }
    }

    // ========================= STRING ALGORITHMS =========================

    static class KMP {
        static int[] lps(String p) {
            int[] lps = new int[p.length()];
            int len = 0, i = 1;
            while (i < p.length()) {
                if (p.charAt(i) == p.charAt(len)) lps[i++] = ++len;
                else if (len > 0) len = lps[len - 1];
                else lps[i++] = 0;
            }
            return lps;
        }

        static List<Integer> search(String text, String pattern) {
            List<Integer> ans = new ArrayList<>();
            if (pattern.isEmpty()) { ans.add(0); return ans; }
            int[] lps = lps(pattern);
            int i = 0, j = 0;
            while (i < text.length()) {
                if (text.charAt(i) == pattern.charAt(j)) {
                    i++; j++;
                    if (j == pattern.length()) {
                        ans.add(i - j);
                        j = lps[j - 1];
                    }
                } else if (j > 0) j = lps[j - 1];
                else i++;
            }
            return ans;
        }
    }

    static class ZFunction {
        static int[] zArray(String s) {
            int n = s.length(), l = 0, r = 0;
            int[] z = new int[n];
            for (int i = 1; i < n; i++) {
                if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);
                while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
                if (i + z[i] - 1 > r) {
                    l = i; r = i + z[i] - 1;
                }
            }
            return z;
        }

        static List<Integer> search(String text, String pattern) {
            List<Integer> result = new ArrayList<>();
            if (pattern.isEmpty()) { result.add(0); return result; }
            String combined = pattern + '\u0001' + text;
            int[] z = zArray(combined);
            for (int i = pattern.length() + 1; i < combined.length(); i++)
                if (z[i] >= pattern.length()) result.add(i - pattern.length() - 1);
            return result;
        }
    }

    static class RabinKarp {
        static final long BASE = 256, MOD = 1_000_000_007L;

        static List<Integer> search(String text, String pattern) {
            List<Integer> result = new ArrayList<>();
            int n = text.length(), m = pattern.length();
            if (m == 0) { result.add(0); return result; }
            if (m > n) return result;

            long high = 1, ph = 0, th = 0;
            for (int i = 0; i < m - 1; i++) high = high * BASE % MOD;
            for (int i = 0; i < m; i++) {
                ph = (ph * BASE + pattern.charAt(i)) % MOD;
                th = (th * BASE + text.charAt(i)) % MOD;
            }

            for (int i = 0; i <= n - m; i++) {
                if (ph == th && text.regionMatches(i, pattern, 0, m))
                    result.add(i);
                if (i < n - m) {
                    th = (BASE * (th - text.charAt(i) * high % MOD + MOD)
                            + text.charAt(i + m)) % MOD;
                }
            }
            return result;
        }

        static void searchCodes(List<Package> packages, String pattern) {
            System.out.println("\nRabin-Karp Package Search: " + pattern);
            boolean found = false;
            for (int i = 0; i < packages.size(); i++) {
                Package p = packages.get(i);
                if (!search(p.packageCode, pattern).isEmpty()) {
                    System.out.println("Index " + i + ": " + p.packageCode);
                    found = true;
                }
            }
            if (!found) System.out.println("No package code matched.");
        }
    }

    static class AhoCorasick {
        static class Node {
            Map<Character, Integer> next = new HashMap<>();
            int fail = 0;
            List<String> output = new ArrayList<>();
        }

        List<Node> nodes = new ArrayList<>();

        AhoCorasick(List<String> patterns) {
            nodes.add(new Node());
            for (String p : patterns) insert(p);
            build();
        }

        void insert(String p) {
            int cur = 0;
            for (char c : p.toLowerCase(Locale.ROOT).toCharArray())
                cur = nodes.get(cur).next.computeIfAbsent(c, k -> {
                    nodes.add(new Node()); return nodes.size() - 1;
                });
            nodes.get(cur).output.add(p);
        }

        void build() {
            Queue<Integer> q = new ArrayDeque<>();
            for (Map.Entry<Character,Integer> e : nodes.get(0).next.entrySet()) {
                nodes.get(e.getValue()).fail = 0;
                q.add(e.getValue());
            }
            while (!q.isEmpty()) {
                int u = q.poll();
                for (Map.Entry<Character,Integer> e : nodes.get(u).next.entrySet()) {
                    char c = e.getKey(); int v = e.getValue();
                    int f = nodes.get(u).fail;
                    while (f != 0 && !nodes.get(f).next.containsKey(c)) f = nodes.get(f).fail;
                    if (nodes.get(f).next.containsKey(c) && nodes.get(f).next.get(c) != v)
                        f = nodes.get(f).next.get(c);
                    nodes.get(v).fail = f;
                    nodes.get(v).output.addAll(nodes.get(f).output);
                    q.add(v);
                }
            }
        }

        void search(String text) {
            int state = 0;
            boolean found = false;
            String lower = text.toLowerCase(Locale.ROOT);
            for (int i = 0; i < lower.length(); i++) {
                char c = lower.charAt(i);
                while (state != 0 && !nodes.get(state).next.containsKey(c))
                    state = nodes.get(state).fail;
                if (nodes.get(state).next.containsKey(c)) state = nodes.get(state).next.get(c);
                for (String p : nodes.get(state).output) {
                    System.out.println("Found \"" + p + "\" at position " + (i - p.length() + 1));
                    found = true;
                }
            }
            if (!found) System.out.println("No status keyword found.");
        }
    }

    // ========================= SUFFIX ARRAY =========================

    static class SuffixArray {
        static int[] build(String s) {
            int n = s.length();
            Integer[] sa = new Integer[n];
            for (int i = 0; i < n; i++) sa[i] = i;

            // Educational doubling construction.
            for (int k = 1; k < n; k *= 2) {
                final int step = k;
                Arrays.sort(sa, (a, b) -> {
                    if (s.charAt(a) != s.charAt(b))
                        return Character.compare(s.charAt(a), s.charAt(b));
                    int ra = a + step < n ? s.charAt(a + step) : -1;
                    int rb = b + step < n ? s.charAt(b + step) : -1;
                    return Integer.compare(ra, rb);
                });
                if (k > n / 2) break;
            }
            int[] result = new int[n];
            for (int i = 0; i < n; i++) result[i] = sa[i];
            return result;
        }

        static void print(String s, int[] sa) {
            System.out.println("Suffix Array:");
            for (int i : sa)
                System.out.printf("%4d : %s%n", i, s.substring(i));
        }

        static List<Integer> search(String s, int[] sa, String pattern) {
            List<Integer> result = new ArrayList<>();
            for (int pos : sa) {
                if (s.startsWith(pattern, pos)) result.add(pos);
            }
            Collections.sort(result);
            return result;
        }
    }

    static class SAIS {
        // Working educational SA-IS implementation for integer alphabets.
        static int[] build(String str) {
            if (str.isEmpty()) return new int[0];
            int[] a = new int[str.length() + 1];
            for (int i = 0; i < str.length(); i++) a[i] = str.charAt(i) + 1;
            a[str.length()] = 0;
            int[] sa = sais(a, 65536 + 2);
            return Arrays.copyOf(sa, sa.length - 1);
        }

        static int[] sais(int[] s, int upper) {
            int n = s.length;
            if (n == 0) return new int[0];
            if (n == 1) return new int[]{0};
            if (n == 2) return s[0] < s[1] ? new int[]{0,1} : new int[]{1,0};

            boolean[] ls = new boolean[n];
            ls[n - 1] = true;
            for (int i = n - 2; i >= 0; i--)
                ls[i] = s[i] == s[i + 1] ? ls[i + 1] : s[i] < s[i + 1];

            int[] sumL = new int[upper + 1], sumS = new int[upper + 1];
            for (int i = 0; i < n; i++) {
                if (!ls[i]) sumS[s[i]]++;
                else sumL[s[i] + 1]++;
            }
            for (int i = 0; i <= upper; i++) {
                sumS[i] += sumL[i];
                if (i < upper) sumL[i + 1] += sumS[i];
            }

            int[] lms = new int[n], lmsMap = new int[n + 1];
            Arrays.fill(lmsMap, -1);
            int m = 0;
            for (int i = 1; i < n; i++)
                if (!ls[i - 1] && ls[i]) { lmsMap[i] = m; lms[m++] = i; }

            int[] sa = new int[n];
            induce(s, ls, lms, m, sumL, sumS, sa, upper);

            if (m > 0) {
                int[] sortedLms = new int[m];
                int p = 0;
                for (int x : sa) if (lmsMap[x] != -1) sortedLms[p++] = x;

                int recUpper = 0;
                int[] recS = new int[m];
                recS[lmsMap[sortedLms[0]]] = 0;

                for (int i = 1; i < m; i++) {
                    int l = sortedLms[i - 1], r = sortedLms[i];
                    int endL = (lmsMap[l] + 1 < m) ? lms[lmsMap[l] + 1] : n;
                    int endR = (lmsMap[r] + 1 < m) ? lms[lmsMap[r] + 1] : n;
                    boolean diff = endL - l != endR - r;
                    if (!diff) {
                        while (l < endL && r < endR && s[l] == s[r]) { l++; r++; }
                        if (l == n || r == n || s[l] != s[r]) diff = true;
                    }
                    if (diff) recUpper++;
                    recS[lmsMap[sortedLms[i]]] = recUpper;
                }

                int[] recSA = recUpper + 1 == m ? new int[m] : sais(recS, recUpper);
                if (recUpper + 1 == m) {
                    for (int i = 0; i < m; i++) recSA[recS[i]] = i;
                }
                for (int i = 0; i < m; i++) sortedLms[i] = lms[recSA[i]];
                induce(s, ls, sortedLms, m, sumL, sumS, sa, upper);
            }
            return sa;
        }

        static void induce(int[] s, boolean[] ls, int[] lms, int m,
                           int[] sumL, int[] sumS, int[] sa, int upper) {
            Arrays.fill(sa, -1);
            int[] buf = sumS.clone();
            for (int i = m - 1; i >= 0; i--) sa[--buf[s[lms[i]]]] = lms[i];

            buf = sumL.clone();
            sa[buf[s.length - 1]++] = s.length - 1;
            for (int i = 0; i < s.length; i++) {
                int v = sa[i];
                if (v >= 1 && !ls[v - 1]) sa[buf[s[v - 1]]++] = v - 1;
            }
            buf = sumL.clone();
            for (int i = s.length - 1; i >= 0; i--) {
                int v = sa[i];
                if (v >= 1 && ls[v - 1]) sa[--buf[s[v - 1] + 1]] = v - 1;
            }
        }
    }

    static class LCPKasai {
        static int[] build(String s, int[] sa) {
            int n = s.length();
            int[] rank = new int[n], lcp = new int[Math.max(0,n)];
            for (int i = 0; i < n; i++) rank[sa[i]] = i;
            int h = 0;
            for (int i = 0; i < n; i++) {
                int r = rank[i];
                if (r == 0) continue;
                int j = sa[r - 1];
                while (i + h < n && j + h < n && s.charAt(i+h) == s.charAt(j+h)) h++;
                lcp[r] = h;
                if (h > 0) h--;
            }
            return lcp;
        }

        static void printRepeated(String s, int[] sa, int[] lcp) {
            int count = 0;
            System.out.println("Repeated/common substrings:");
            for (int i = 1; i < lcp.length; i++) {
                if (lcp[i] > 1) {
                    System.out.println("\"" + s.substring(sa[i], sa[i] + lcp[i]) +
                            "\" (length " + lcp[i] + ")");
                    count++;
                }
            }
            System.out.println("Repeated patterns detected: " + count);
        }
    }

    // Simplified educational suffix tree/trie of all suffixes.
    static class SuffixTree {
        static class Node {
            Map<Character, Node> child = new TreeMap<>();
            List<Integer> positions = new ArrayList<>();
        }

        Node root = new Node();

        SuffixTree(String text) {
            for (int i = 0; i < text.length(); i++) {
                Node cur = root;
                for (int j = i; j < text.length(); j++) {
                    cur = cur.child.computeIfAbsent(text.charAt(j), k -> new Node());
                    cur.positions.add(i);
                }
            }
        }

        List<Integer> search(String pattern) {
            Node cur = root;
            for (char c : pattern.toCharArray()) {
                cur = cur.child.get(c);
                if (cur == null) return new ArrayList<>();
            }
            return cur.positions;
        }
    }

    // ========================= DYNAMIC PROGRAMMING =========================

    static class Levenshtein {
        static int distance(String a, String b) {
            int[][] dp = new int[a.length()+1][b.length()+1];
            for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
            for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

            for (int i = 1; i <= a.length(); i++)
                for (int j = 1; j <= b.length(); j++)
                    dp[i][j] = Math.min(
                            Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1),
                            dp[i-1][j-1] + (a.charAt(i-1) == b.charAt(j-1) ? 0 : 1));
            return dp[a.length()][b.length()];
        }
    }

    static class DamerauLevenshtein {
        static int distance(String a, String b) {
            int n = a.length(), m = b.length();
            int[][] d = new int[n+1][m+1];
            for (int i = 0; i <= n; i++) d[i][0] = i;
            for (int j = 0; j <= m; j++) d[0][j] = j;

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    int cost = a.charAt(i-1) == b.charAt(j-1) ? 0 : 1;
                    d[i][j] = Math.min(Math.min(d[i-1][j]+1, d[i][j-1]+1),
                            d[i-1][j-1]+cost);
                    if (i > 1 && j > 1 &&
                            a.charAt(i-1) == b.charAt(j-2) &&
                            a.charAt(i-2) == b.charAt(j-1))
                        d[i][j] = Math.min(d[i][j], d[i-2][j-2] + 1);
                }
            }
            return d[n][m];
        }
    }

    static class BitmaskTSP {
        static class Result {
            long cost; List<Integer> route;
            Result(long c, List<Integer> r) { cost = c; route = r; }
        }

        static Result solve(int[][] dist, int start) {
            int n = dist.length, size = 1 << n;
            long INF = Long.MAX_VALUE / 4;
            long[][] dp = new long[size][n];
            int[][] parent = new int[size][n];
            for (long[] row : dp) Arrays.fill(row, INF);
            for (int[] row : parent) Arrays.fill(row, -1);
            dp[1 << start][start] = 0;

            for (int mask = 0; mask < size; mask++) {
                for (int u = 0; u < n; u++) {
                    if ((mask & (1 << u)) == 0 || dp[mask][u] == INF) continue;
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1 << v)) != 0) continue;
                        int nm = mask | (1 << v);
                        long val = dp[mask][u] + dist[u][v];
                        if (val < dp[nm][v]) {
                            dp[nm][v] = val;
                            parent[nm][v] = u;
                        }
                    }
                }
            }

            int full = size - 1, last = -1;
            long best = INF;
            for (int i = 0; i < n; i++) {
                long val = dp[full][i] + dist[i][start];
                if (val < best) { best = val; last = i; }
            }

            LinkedList<Integer> route = new LinkedList<>();
            int mask = full, cur = last;
            while (cur != -1) {
                route.addFirst(cur);
                int prev = parent[mask][cur];
                mask ^= 1 << cur;
                cur = prev;
            }
            route.add(start);
            return new Result(best, route);
        }
    }

    static class MatrixChainMultiplication {
        static class Result {
            long cost; String order;
            Result(long c, String o) { cost=c; order=o; }
        }

        static Result solve(int[] p) {
            int n = p.length - 1;
            long[][] dp = new long[n][n];
            int[][] split = new int[n][n];

            for (int len = 2; len <= n; len++) {
                for (int i = 0; i + len - 1 < n; i++) {
                    int j = i + len - 1;
                    dp[i][j] = Long.MAX_VALUE / 4;
                    for (int k = i; k < j; k++) {
                        long q = dp[i][k] + dp[k+1][j] +
                                (long)p[i] * p[k+1] * p[j+1];
                        if (q < dp[i][j]) {
                            dp[i][j] = q; split[i][j] = k;
                        }
                    }
                }
            }
            return new Result(n == 0 ? 0 : dp[0][n-1],
                    n == 0 ? "" : order(split,0,n-1));
        }

        static String order(int[][] split, int i, int j) {
            if (i == j) return "A" + (i+1);
            int k = split[i][j];
            return "(" + order(split,i,k) + " x " + order(split,k+1,j) + ")";
        }
    }

    static class OptimalBST {
        static class Result {
            long cost; int[][] root; String structure;
            Result(long c, int[][] r, String s) { cost=c; root=r; structure=s; }
        }

        static Result solve(String[] keys, int[] freq) {
            int n = keys.length;
            if (n == 0) return new Result(0, new int[0][0], "");
            long[][] dp = new long[n][n];
            int[][] root = new int[n][n];
            long[] prefix = new long[n+1];
            for (int i=0;i<n;i++) prefix[i+1]=prefix[i]+freq[i];

            for (int i=0;i<n;i++) dp[i][i]=freq[i];

            for (int len=2;len<=n;len++) {
                for (int i=0;i+len-1<n;i++) {
                    int j=i+len-1;
                    dp[i][j]=Long.MAX_VALUE/4;
                    long sum=prefix[j+1]-prefix[i];
                    for (int r=i;r<=j;r++) {
                        long left=r>i?dp[i][r-1]:0;
                        long right=r<j?dp[r+1][j]:0;
                        long val=left+right+sum;
                        if(val<dp[i][j]) {dp[i][j]=val;root[i][j]=r;}
                    }
                }
            }
            return new Result(dp[0][n-1],root,tree(keys,root,0,n-1,"Root"));
        }

        static String tree(String[] keys,int[][] root,int i,int j,String label) {
            if(i>j) return "";
            int r=root[i][j];
            StringBuilder sb=new StringBuilder();
            sb.append(label).append(" = ").append(keys[r]).append("\n");
            String l=tree(keys,root,i,r-1,"  Left");
            String rr=tree(keys,root,r+1,j,"  Right");
            if(!l.isEmpty()) sb.append(l);
            if(!rr.isEmpty()) sb.append(rr);
            return sb.toString();
        }
    }

    // ========================= MANAGER =========================

    static class FleetManager {
        List<Vehicle> vehicles = new ArrayList<>();
        List<Driver> drivers = new ArrayList<>();
        List<Warehouse> warehouses = new ArrayList<>();
        List<Package> packages = new ArrayList<>();
        List<Shipment> shipments = new ArrayList<>();
        List<DeliveryDocument> documents = new ArrayList<>();

        int repeatedPatterns = 0;
        double totalCorrectionDistance = 0;
        int correctionCount = 0;
        long optimizedRouteCost = -1;

        void loadSampleData() {
            vehicles.add(new Vehicle("V001","Truck",1000,"Vijayawada",true));
            drivers.add(new Driver("D001","Ravi","9876543210",true));
            warehouses.add(new Warehouse("W001","Main Warehouse","Vijayawada",5000));
            packages.add(new Package("P001","PKG4589","Customer A",
                    "12 MG Road, Vijayawada","25 Benz Circle, Vijayawada",
                    10,"Out for Delivery"));
            shipments.add(new Shipment("SHP1025","P001","V001","D001",
                    "Vijayawada","Benz Circle","Out for Delivery",
                    "Package delivery from main warehouse to customer"));
            documents.add(new DeliveryDocument("DOC001","SHP1025",
                    "Package delivery from main warehouse to customer. Out for Delivery."));
        }

        boolean vehicleExists(String id) { return findVehicle(id)!=null; }
        boolean driverExists(String id) { return findDriver(id)!=null; }
        boolean warehouseExists(String id) { return findWarehouse(id)!=null; }
        boolean packageExists(String id) { return findPackage(id)!=null; }
        boolean shipmentExists(String id) { return findShipment(id)!=null; }

        Vehicle findVehicle(String id) {
            for(Vehicle x:vehicles) if(x.vehicleId.equalsIgnoreCase(id)) return x; return null;
        }
        Driver findDriver(String id) {
            for(Driver x:drivers) if(x.driverId.equalsIgnoreCase(id)) return x; return null;
        }
        Warehouse findWarehouse(String id) {
            for(Warehouse x:warehouses) if(x.warehouseId.equalsIgnoreCase(id)) return x; return null;
        }
        Package findPackage(String id) {
            for(Package x:packages) if(x.packageId.equalsIgnoreCase(id)) return x; return null;
        }
        Shipment findShipment(String id) {
            for(Shipment x:shipments) if(x.shipmentId.equalsIgnoreCase(id)) return x; return null;
        }

        void addVehicle(Vehicle v) {
            if(vehicleExists(v.vehicleId)) throw new IllegalArgumentException("Vehicle ID already exists.");
            vehicles.add(v);
        }
        void addDriver(Driver d) {
            if(driverExists(d.driverId)) throw new IllegalArgumentException("Driver ID already exists.");
            drivers.add(d);
        }
        void addWarehouse(Warehouse w) {
            if(warehouseExists(w.warehouseId)) throw new IllegalArgumentException("Warehouse ID already exists.");
            warehouses.add(w);
        }
        void addPackage(Package p) {
            if(packageExists(p.packageId)) throw new IllegalArgumentException("Package ID already exists.");
            packages.add(p);
        }
        void addShipment(Shipment s) {
            if(shipmentExists(s.shipmentId)) throw new IllegalArgumentException("Shipment ID already exists.");
            if(!packageExists(s.packageId)) throw new IllegalArgumentException("Package does not exist.");
            if(!vehicleExists(s.vehicleId)) throw new IllegalArgumentException("Vehicle does not exist.");
            if(!driverExists(s.driverId)) throw new IllegalArgumentException("Driver does not exist.");
            shipments.add(s);
        }
    }

    // ========================= INPUT / APP =========================

    static final Scanner sc = new Scanner(System.in);
    static final FleetManager manager = new FleetManager();

    static String read(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    static String required(String prompt) {
        while (true) {
            String s = read(prompt);
            if (!s.isEmpty()) return s;
            System.out.println("Value cannot be empty.");
        }
    }

    static int intInput(String prompt, int min, int max) {
        while (true) {
            try {
                int x = Integer.parseInt(read(prompt));
                if (x >= min && x <= max) return x;
            } catch (Exception ignored) {}
            System.out.println("Enter a valid integer from " + min + " to " + max + ".");
        }
    }

    static double doubleInput(String prompt, double min) {
        while (true) {
            try {
                double x = Double.parseDouble(read(prompt));
                if (x >= min) return x;
            } catch (Exception ignored) {}
            System.out.println("Enter a valid number >= " + min + ".");
        }
    }

    static boolean yesNo(String prompt) {
        while (true) {
            String s = read(prompt + " (Y/N): ");
            if (s.equalsIgnoreCase("Y")) return true;
            if (s.equalsIgnoreCase("N")) return false;
            System.out.println("Enter Y or N.");
        }
    }

    static void addVehicle() {
        try {
            manager.addVehicle(new Vehicle(required("Vehicle ID: "), required("Vehicle Type: "),
                    doubleInput("Capacity (kg): ",0), required("Current Location: "),
                    yesNo("Available")));
            System.out.println("Vehicle added.");
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
    }

    static void addDriver() {
        try {
            manager.addDriver(new Driver(required("Driver ID: "), required("Driver Name: "),
                    required("Phone: "), yesNo("Available")));
            System.out.println("Driver added.");
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
    }

    static void addWarehouse() {
        try {
            manager.addWarehouse(new Warehouse(required("Warehouse ID: "), required("Warehouse Name: "),
                    required("Location: "), doubleInput("Capacity: ",0)));
            System.out.println("Warehouse added.");
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
    }

    static void addPackage() {
        try {
            manager.addPackage(new Package(required("Package ID: "), required("Package Code: "),
                    required("Customer Name: "), required("Pickup Address: "),
                    required("Delivery Address: "), doubleInput("Weight: ",0),
                    required("Status: ")));
            System.out.println("Package added.");
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
    }

    static void addShipment() {
        try {
            manager.addShipment(new Shipment(required("Shipment ID: "), required("Package ID: "),
                    required("Vehicle ID: "), required("Driver ID: "), required("Source: "),
                    required("Destination: "), required("Status: "), required("Description: ")));
            System.out.println("Shipment added.");
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
    }

    static void addDocument() {
        String id=required("Document ID: "), sid=required("Shipment ID: ");
        if(!manager.shipmentExists(sid)) { System.out.println("Shipment does not exist."); return; }
        manager.documents.add(new DeliveryDocument(id,sid,required("Document Text: ")));
        System.out.println("Document added.");
    }

    static String allShipmentText() {
        StringBuilder sb=new StringBuilder();
        for(Shipment s:manager.shipments)
            sb.append(s.shipmentId).append(" ").append(s.shipmentDescription).append(" ");
        return sb.toString();
    }

    static void kmpSearch() {
        String text=required("Text or shipment data (leave as * for all shipment data): ");
        if(text.equals("*")) text=allShipmentText();
        String pattern=required("Pattern: ");
        List<Integer> r=KMP.search(text,pattern);
        System.out.println("Found: "+!r.isEmpty());
        System.out.println("Matching positions: "+r);
    }

    static void zSearch() {
        String text=required("Text: "), pattern=required("Pattern: ");
        List<Integer> r=ZFunction.search(text,pattern);
        System.out.println("Matching positions: "+r);
        System.out.println("Repeated pattern information: "+(r.size()>1 ? "Pattern occurs repeatedly." : "No repeated occurrence."));
    }

    static void rabinSearch() {
        manager.packages.forEach(p -> System.out.println(p.packageCode));
        if (manager.packages.isEmpty()) {
            System.out.println("No packages.");
        } else {
            RabinKarp.searchCodes(manager.packages, required("Package-code pattern: "));
        }
    }

    static void ahoSearch() {
        List<String> patterns=Arrays.asList("Pending","In Transit","Out for Delivery",
                "Delivered","Delayed","Cancelled");
        String text=required("Shipment/document text: ");
        new AhoCorasick(patterns).search(text);
    }

    static String getDocumentText() {
        if(manager.documents.isEmpty()) return "";
        StringBuilder s=new StringBuilder();
        for(DeliveryDocument d:manager.documents) s.append(d.documentText).append(" ");
        return s.toString();
    }

    static void suffixArray() {
        String text=required("Large logistics text (or * for stored documents): ");
        if(text.equals("*")) text=getDocumentText();
        int[] sa=SuffixArray.build(text);
        SuffixArray.print(text,sa);
        String p=read("Search substring (blank to skip): ");
        if(!p.isEmpty()) System.out.println("Search results: "+SuffixArray.search(text,sa,p));
    }

    static void sais() {
        String text=required("Logistics document: ");
        int[] sa=SAIS.build(text);
        System.out.println("SA-IS suffix array:");
        System.out.println(Arrays.toString(sa));
        for(int i:sa) System.out.println(i+" : "+text.substring(i));
    }

    static void lcp() {
        String text=required("Text (or * for stored documents): ");
        if(text.equals("*")) text=getDocumentText();
        int[] sa=SuffixArray.build(text);
        int[] lcp=LCPKasai.build(text,sa);
        System.out.println("Suffix Array: "+Arrays.toString(sa));
        System.out.println("LCP Array: "+Arrays.toString(lcp));
        LCPKasai.printRepeated(text,sa,lcp);
        manager.repeatedPatterns += countPositive(lcp);
    }

    static int countPositive(int[] a) {
        int c=0; for(int x:a) if(x>1)c++; return c;
    }

    static void suffixTree() {
        String text=required("Document text: "), p=required("Search substring: ");
        List<Integer> r=new SuffixTree(text).search(p);
        System.out.println("Exists: "+!r.isEmpty());
        System.out.println("Matching locations: "+r);
    }

    static List<String> storedAddresses() {
        List<String> a=new ArrayList<>();
        for(Package p:manager.packages) {
            a.add(p.pickupAddress); a.add(p.deliveryAddress);
        }
        return a;
    }

    static void addressCorrection(boolean damerau) {
        String input=required("Incorrect delivery address: ");
        List<String> addresses=storedAddresses();
        if(addresses.isEmpty()) { System.out.println("No stored addresses."); return; }
        String best=null; int bestD=Integer.MAX_VALUE;
        for(String candidate:addresses) {
            int d=damerau ? DamerauLevenshtein.distance(input,candidate) :
                    Levenshtein.distance(input,candidate);
            if(d<bestD) { bestD=d; best=candidate; }
        }
        System.out.println("Suggested: "+best);
        System.out.println((damerau ? "Damerau-Levenshtein" : "Levenshtein")+" Distance: "+bestD);
        manager.totalCorrectionDistance+=bestD; manager.correctionCount++;
    }

    static void tsp() {
        int n=intInput("Number of locations (2-12): ",2,12);
        int[][] d=new int[n][n];
        System.out.println("Enter distance matrix:");
        for(int i=0;i<n;i++) for(int j=0;j<n;j++)
            d[i][j]=intInput("Distance["+i+"]["+j+"]: ",0,Integer.MAX_VALUE/4);
        int start=intInput("Starting warehouse index (0-"+(n-1)+"): ",0,n-1);
        BitmaskTSP.Result r=BitmaskTSP.solve(d,start);
        System.out.println("Minimum route cost: "+r.cost);
        System.out.println("Optimal route: "+r.route);
        manager.optimizedRouteCost=r.cost;
    }

    static void matrixChain() {
        int n=intInput("Number of matrices: ",1,15);
        int[] p=new int[n+1];
        for(int i=0;i<=n;i++) p[i]=intInput("Dimension p["+i+"]: ",1,100000);
        MatrixChainMultiplication.Result r=MatrixChainMultiplication.solve(p);
        System.out.println("Minimum multiplication cost: "+r.cost);
        System.out.println("Optimal parenthesization: "+r.order);
    }

    static void optimalBST() {
        int n=intInput("Number of shipment/package keys: ",1,15);
        String[] keys=new String[n]; int[] freq=new int[n];
        System.out.println("Enter keys in sorted order:");
        for(int i=0;i<n;i++) {
            keys[i]=required("Key "+(i+1)+": ");
            freq[i]=intInput("Access frequency: ",1,1000000);
        }
        OptimalBST.Result r=OptimalBST.solve(keys,freq);
        System.out.println("Minimum search cost: "+r.cost);
        System.out.println("Optimal BST:");
        System.out.print(r.structure);
    }

    // ========================= DISPLAY / ANALYTICS =========================

    static void display(String title, List<?> list) {
        System.out.println("\n--- "+title+" ---");
        if(list.isEmpty()) System.out.println("No records.");
        else list.forEach(System.out::println);
    }

    static void analytics() {
        long delivered=manager.shipments.stream().filter(s->s.status.equalsIgnoreCase("Delivered")).count();
        long pending=manager.shipments.stream().filter(s->s.status.equalsIgnoreCase("Pending")).count();
        long delayed=manager.shipments.stream().filter(s->s.status.equalsIgnoreCase("Delayed")).count();
        long av=manager.vehicles.stream().filter(v->v.available).count();
        long ad=manager.drivers.stream().filter(d->d.available).count();

        System.out.println("\n=========================================");
        System.out.println("       FLEETFLOW ANALYTICS DASHBOARD");
        System.out.println("=========================================");
        System.out.println("Total vehicles                 : "+manager.vehicles.size());
        System.out.println("Available vehicles             : "+av);
        System.out.println("Total drivers                  : "+manager.drivers.size());
        System.out.println("Available drivers              : "+ad);
        System.out.println("Total warehouses               : "+manager.warehouses.size());
        System.out.println("Total packages                 : "+manager.packages.size());
        System.out.println("Total shipments                : "+manager.shipments.size());
        System.out.println("Delivered shipments            : "+delivered);
        System.out.println("Pending shipments              : "+pending);
        System.out.println("Delayed shipments              : "+delayed);
        System.out.println("Number of documents            : "+manager.documents.size());
        System.out.println("Repeated patterns detected    : "+manager.repeatedPatterns);
        System.out.printf("Average address distance       : %.2f%n",
                manager.correctionCount==0?0:manager.totalCorrectionDistance/manager.correctionCount);
        System.out.println("Optimized route cost           : "+
                (manager.optimizedRouteCost<0 ? "Not calculated" : manager.optimizedRouteCost));
        System.out.println("=========================================");
    }

    static void menu() {
        System.out.println("\n=========================================");
        System.out.println("       FLEETFLOW LOGISTICS SYSTEM");
        System.out.println("=========================================");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Add Driver");
        System.out.println("3. Add Warehouse");
        System.out.println("4. Add Package");
        System.out.println("5. Add Shipment");
        System.out.println("6. Add Delivery Document");
        System.out.println("\n------ STRING ALGORITHMS ------");
        System.out.println("7. KMP Shipment Search - Fast Shipment ID/Text Searching");
        System.out.println("8. Z-Function Pattern Search - Pattern Matching in Shipment Data");
        System.out.println("9. Rabin-Karp Package Search-Package Searching");
        System.out.println("10. Aho-Corasick Status Search -Searching Multiple Shipment Statuses");
        System.out.println("\n------ SUFFIX STRUCTURES ------");
        System.out.println("11. Build Suffix Array-Efficient Text Indexing");
        System.out.println("12. SA-IS Indexing-Advanced Suffix-Array Construction");
        System.out.println("13. LCP/Kasai Analysis -  Finding Common Prefixes Between Suffixes");
        System.out.println("14. Suffix Tree Search-Fast Substring Searching");
        System.out.println("\n------ DYNAMIC PROGRAMMING ------");
        System.out.println("15. Levenshtein Address Correction-Correcting Address Spelling Errors");
        System.out.println("16. Damerau-Levenshtein Address Correction-Address Correction Including Character Swaps");
        System.out.println("17. Bitmask Route Optimization-Finding an Optimized Delivery Route");
        System.out.println("18. Matrix-Chain Optimization-Optimizing Matrix Operations");
        System.out.println("19. Optimal BST-Optimizing Search Operations");
        System.out.println("\n20. Display All Shipments");
        System.out.println("21. Display All Packages");
        System.out.println("22. Display Vehicles");
        System.out.println("23. Display Drivers");
        System.out.println("24. Display Warehouses");
        System.out.println("25. Display Analytics");
        System.out.println("26. Load Sample Dataset");
        System.out.println("27. Exit");
    }

    static void run() {
        boolean running=true;
        while(running) {
            menu();
            int choice=intInput("Choose option: ",1,27);
            try {
                switch(choice) {
                    case 1 -> addVehicle();
                    case 2 -> addDriver();
                    case 3 -> addWarehouse();
                    case 4 -> addPackage();
                    case 5 -> addShipment();
                    case 6 -> addDocument();
                    case 7 -> kmpSearch();
                    case 8 -> zSearch();
                    case 9 -> rabinSearch();
                    case 10 -> ahoSearch();
                    case 11 -> suffixArray();
                    case 12 -> sais();
                    case 13 -> lcp();
                    case 14 -> suffixTree();
                    case 15 -> addressCorrection(false);
                    case 16 -> addressCorrection(true);
                    case 17 -> tsp();
                    case 18 -> matrixChain();
                    case 19 -> optimalBST();
                    case 20 -> display("ALL SHIPMENTS",manager.shipments);
                    case 21 -> display("ALL PACKAGES",manager.packages);
                    case 22 -> display("VEHICLES",manager.vehicles);
                    case 23 -> display("DRIVERS",manager.drivers);
                    case 24 -> display("WAREHOUSES",manager.warehouses);
                    case 25 -> analytics();
                    case 26 -> { manager.loadSampleData(); System.out.println("Sample dataset loaded."); }
                    case 27 -> running=false;
                }
            } catch(Exception e) {
                System.out.println("Operation error: "+e.getMessage());
            }
        }
        System.out.println("FleetFlow closed. Thank you.");
    }

    public static void main(String[] args) {
        System.out.println("FleetFlow - Intelligent Logistics Management Platform");
        
        System.out.println("Tip: choose option 26 first to load the sample dataset.");
        run();
    }
}
