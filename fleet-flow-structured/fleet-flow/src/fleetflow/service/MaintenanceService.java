package fleetflow.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.Vehicle;
import fleetflow.util.InputHelper;

/**
 * Feature 6 – Vehicle Maintenance [Date Comparison / Sorting]
 * Records service dates and identifies overdue vehicles using LocalDate comparison.
 * Sorting is done via Comparator on LocalDate.
 */
public class MaintenanceService {

    private final DataStore data;

    public MaintenanceService(DataStore data) {
        this.data = data;
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Vehicle Maintenance [Date Comparison / Sorting] ---");
            System.out.println("1. Record Service Date for Vehicle");
            System.out.println("2. View Maintenance History (sorted by next service date)");
            System.out.println("3. Show Overdue / Due-Soon Vehicles");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> recordServiceDate(scanner);
                case 2 -> viewSorted();
                case 3 -> showOverdue();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /** Records last and next service dates for a vehicle. */
    private void recordServiceDate(Scanner scanner) {
        if (data.vehicles.isEmpty()) { System.out.println("No vehicles registered."); return; }
        String id = InputHelper.readNonEmpty(scanner, "Vehicle ID: ");
        Vehicle v = data.vehicleMap.get(id);
        if (v == null) { System.out.println("Vehicle not found: " + id); return; }

        LocalDate lastService = readDate(scanner, "Last service date (YYYY-MM-DD): ");
        LocalDate nextService = readDate(scanner, "Next service date (YYYY-MM-DD): ");

        if (!nextService.isAfter(lastService)) {
            System.out.println("Next service date must be after last service date.");
            return;
        }
        v.setLastServiceDate(lastService);
        v.setNextServiceDate(nextService);
        System.out.println("Service dates recorded for vehicle " + v.getId() + ":");
        System.out.println("  Last service: " + lastService + " | Next service: " + nextService);
    }

    /**
     * Lists all vehicles sorted by next service date (ascending).
     * Uses LocalDate natural ordering via Comparator – Date Comparison / Sorting.
     */
    public void viewSorted() {
        if (data.vehicles.isEmpty()) { System.out.println("No vehicles registered."); return; }

        List<Vehicle> sorted = new ArrayList<>(data.vehicles);
        // Sort by nextServiceDate; vehicles with null dates go to the end
        sorted.sort(Comparator.comparing(
                Vehicle::getNextServiceDate,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        System.out.println("\n--- Maintenance Schedule (sorted by Next Service Date) ---");
        for (Vehicle v : sorted) {
            System.out.println("  " + v);
        }
    }

    /**
     * Identifies vehicles whose next service date is today or in the past (overdue)
     * or within 30 days (due soon). Uses LocalDate.isBefore / isEqual for comparison.
     */
    public void showOverdue() {
        if (data.vehicles.isEmpty()) { System.out.println("No vehicles registered."); return; }

        LocalDate today     = LocalDate.now();
        LocalDate dueSoonBy = today.plusDays(30);

        System.out.println("\n--- Overdue / Due-Soon Vehicles (as of " + today + ") ---");
        boolean found = false;
        for (Vehicle v : data.vehicles) {
            LocalDate next = v.getNextServiceDate();
            if (next == null) continue;
            if (!next.isAfter(today)) {
                System.out.println("[OVERDUE]   " + v.getId() + " | " + v.getName()
                        + " | Next Service was: " + next);
                found = true;
            } else if (!next.isAfter(dueSoonBy)) {
                System.out.println("[DUE SOON]  " + v.getId() + " | " + v.getName()
                        + " | Next Service: " + next + " (in "
                        + (next.toEpochDay() - today.toEpochDay()) + " days)");
                found = true;
            }
        }
        if (!found) System.out.println("No vehicles are overdue or due within 30 days.");
    }

    private LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid date. Use format YYYY-MM-DD (e.g. 2026-06-15).");
            }
        }
    }
}
