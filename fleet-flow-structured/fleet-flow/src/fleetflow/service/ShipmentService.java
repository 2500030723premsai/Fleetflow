package fleetflow.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.engine.randomized.RandomizedQuickSort;
import fleetflow.engine.string.KMP;
import fleetflow.model.PackageItem;
import fleetflow.util.InputHelper;

/**
 * Feature 11 – Shipment Search [KMP]
 * Feature 12 – Delivery Priority [Randomized QuickSort]
 */
public class ShipmentService {

    private final DataStore data;

    public ShipmentService(DataStore data) {
        this.data = data;
    }

    public void list() {
        if (data.packages.isEmpty()) {
            System.out.println("No shipments registered.");
        } else {
            System.out.println("\n--- Shipment List (" + data.packages.size() + ") ---");
            data.packages.forEach(System.out::println);
        }
    }

    /**
     * Feature 11 – Shipment Search using KMP string matching.
     * Calls KMP.contains() for every searchable field; does NOT use String.contains().
     */
    public void search(String query) {
        System.out.println("\n=== SHIPMENT SEARCH [KMP] ===");
        if (query == null || query.isBlank()) {
            System.out.println("Search query cannot be empty.");
            return;
        }
        String lq = query.toLowerCase();
        boolean found = false;
        for (PackageItem p : data.packages) {
            if (KMP.contains(p.getShipmentId().toLowerCase(), lq)
                    || KMP.contains(p.getId().toLowerCase(), lq)
                    || KMP.contains(p.getAddress().toLowerCase(), lq)) {
                System.out.println(p);
                found = true;
            }
        }
        if (!found) System.out.println("No matching shipment found for: " + query);
    }

    public void searchInteractive(Scanner scanner) {
        String query = InputHelper.readNonEmpty(scanner, "Search shipment (ID / address keyword): ");
        search(query);
    }

    /**
     * Feature 12 – Delivery Priority using Randomized QuickSort.
     * Sorts a copy of the list; preserves original DataStore order.
     */
    public void listByPriority() {
        System.out.println("\n=== DELIVERY PRIORITY [Randomized QuickSort] ===");
        if (data.packages.isEmpty()) {
            System.out.println("No shipments registered.");
            return;
        }

        List<PackageItem> sorted = new ArrayList<>(data.packages);

        Comparator<PackageItem> comp = (a, b) -> {
            int ra = priorityRank(a), rb = priorityRank(b);
            if (ra != rb) return Integer.compare(ra, rb);
            int idCmp = a.getId().compareTo(b.getId());
            return idCmp != 0 ? idCmp : a.getShipmentId().compareTo(b.getShipmentId());
        };

        // Genuine call to RandomizedQuickSort
        RandomizedQuickSort.sort(sorted, comp);

        System.out.println("\nShipments sorted by priority (High -> Medium -> Low -> Delivered):");
        sorted.forEach(p -> System.out.println("  " + p));
    }

    private int priorityRank(PackageItem p) {
        if ("Delivered".equalsIgnoreCase(p.getStatus())) return 100;
        String pr = p.getPriority() == null ? "" : p.getPriority().toUpperCase();
        return switch (pr) {
            case "HIGH", "URGENT" -> 1;
            case "MEDIUM", "NORMAL" -> 2;
            case "LOW" -> 3;
            default -> 4;
        };
    }
}
