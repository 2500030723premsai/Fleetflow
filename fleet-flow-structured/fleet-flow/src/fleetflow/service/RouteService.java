package fleetflow.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.engine.dp.BitmaskTSP;
import fleetflow.model.Route;
import fleetflow.util.InputHelper;

/**
 * Feature 9 – Route Optimization [Bitmask DP / TSP]
 * Also provides CRUD for Route records.
 */
public class RouteService {

    private final DataStore data;

    public RouteService(DataStore data) {
        this.data = data;
    }

    public void list() {
        if (data.routes.isEmpty()) {
            System.out.println("No routes registered.");
        } else {
            System.out.println("\n--- Route List (" + data.routes.size() + ") ---");
            data.routes.forEach(System.out::println);
        }
    }

    public Route findById(String id) {
        return data.routeMap.get(id);
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Route Optimization [Bitmask DP / TSP] ---");
            System.out.println("1. Run Route Optimization (TSP)");
            System.out.println("2. View All Routes");
            System.out.println("3. Add Route");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> optimize(scanner);
                case 2 -> list();
                case 3 -> addInteractive(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addInteractive(Scanner scanner) {
        String id   = InputHelper.readNonEmpty(scanner, "Route ID: ");
        if (data.routeMap.containsKey(id)) { System.out.println("Route ID already exists."); return; }
        String name = InputHelper.readNonEmpty(scanner, "Name: ");
        String src  = InputHelper.readNonEmpty(scanner, "Source: ");
        String dst  = InputHelper.readNonEmpty(scanner, "Destination: ");
        double dist = readPositiveDouble(scanner, "Distance (km): ");
        Route r     = new Route(id, name, src, dst, dist);
        data.addRoute(r);
        System.out.println("Route added: " + r);
    }

    /**
     * Route Optimization using Bitmask TSP.
     * Builds a distance matrix from stored Route segments using Floyd-Warshall,
     * then finds the optimal visiting order with BitmaskTSP.
     *
     * Note: distances are from the in-memory DataStore, not live GPS data.
     */
    public void optimize(Scanner scanner) {
        System.out.println("\n=== ROUTE OPTIMIZATION [Bitmask DP / TSP] ===");
        System.out.println("Note: distances are from the DataStore sample network, not live GPS.");

        if (data.routes.isEmpty()) {
            System.out.println("No routes available. Load sample data or add routes first.");
            return;
        }

        System.out.println("\nAvailable Routes:");
        for (Route r : data.routes) System.out.println("  " + r);

        LinkedHashSet<String> locSet = new LinkedHashSet<>();
        for (Route r : data.routes) { locSet.add(r.getSource()); locSet.add(r.getDestination()); }
        List<String> locs = new ArrayList<>(locSet);

        System.out.println("\nAvailable Locations:");
        for (int i = 0; i < locs.size(); i++) System.out.println("  " + i + ". " + locs.get(i));

        int startIdx;
        try {
            System.out.print("\nSelect starting location index (0-" + (locs.size() - 1) + "): ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) { System.out.println("No selection. Returning."); return; }
            startIdx = Integer.parseInt(line);
            if (startIdx < 0 || startIdx >= locs.size()) {
                System.out.println("Invalid index."); return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number."); return;
        }

        System.out.print("Enter comma-separated location indices to visit (or Enter for all): ");
        String selLine = scanner.nextLine().trim();

        List<Integer> selected = new ArrayList<>();
        selected.add(startIdx);
        if (selLine.isEmpty()) {
            for (int i = 0; i < locs.size(); i++) if (i != startIdx) selected.add(i);
        } else {
            for (String part : selLine.split(",")) {
                String t = part.trim();
                if (t.isEmpty()) continue;
                try {
                    int idx = Integer.parseInt(t);
                    if (idx < 0 || idx >= locs.size()) { System.out.println("Invalid index: " + idx); return; }
                    if (!selected.contains(idx)) selected.add(idx);
                } catch (NumberFormatException e) { System.out.println("Invalid number: " + t); return; }
            }
        }

        int k = selected.size();
        if (k < 2) { System.out.println("Need at least 2 locations."); return; }
        if (k > 12) { System.out.println("Too many locations (" + k + "). Maximum is 12 for Bitmask TSP."); return; }

        // Build full graph and run Floyd-Warshall
        int n    = locs.size();
        int INF  = BitmaskTSP.INF;
        int[][] full = new int[n][n];
        for (int[] row : full) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) full[i][i] = 0;
        for (Route r : data.routes) {
            int u = locs.indexOf(r.getSource());
            int v = locs.indexOf(r.getDestination());
            int d = (int) Math.round(r.getDistanceKm());
            if (u >= 0 && v >= 0) {
                full[u][v] = Math.min(full[u][v], d);
                full[v][u] = Math.min(full[v][u], d);
            }
        }
        for (int m = 0; m < n; m++)
            for (int u = 0; u < n; u++)
                for (int v = 0; v < n; v++)
                    if (full[u][m] < INF && full[m][v] < INF)
                        full[u][v] = Math.min(full[u][v], full[u][m] + full[m][v]);

        int[][] dist = new int[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                dist[i][j] = full[selected.get(i)][selected.get(j)];
                if (i != j && dist[i][j] >= INF) {
                    System.out.println("Locations "
                            + locs.get(selected.get(i)) + " and "
                            + locs.get(selected.get(j)) + " are disconnected.");
                    return;
                }
            }
        }

        BitmaskTSP.Result result = BitmaskTSP.solve(dist, 0);
        if (!result.hasValidTour()) {
            System.out.println("No valid tour found with available route data.");
            return;
        }

        System.out.println("\n--- Optimised Visiting Order (Bitmask TSP) ---");
        List<Integer> tour = result.getTour();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tour.size(); i++) {
            sb.append(locs.get(selected.get(tour.get(i))));
            if (i < tour.size() - 1) sb.append(" -> ");
        }
        System.out.println("Sequence     : " + sb);
        System.out.println("Total Distance: " + result.getCost() + " km (round-trip back to "
                + locs.get(startIdx) + ")");
    }

    private double readPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(scanner.nextLine().trim());
                if (val > 0) return val;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a positive number.");
        }
    }
}
