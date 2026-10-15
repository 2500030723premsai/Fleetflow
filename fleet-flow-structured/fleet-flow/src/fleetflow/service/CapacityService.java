package fleetflow.service;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.Delivery;
import fleetflow.model.PackageItem;
import fleetflow.model.Vehicle;
import fleetflow.util.InputHelper;

/**
 * Feature 13 – Capacity Checking [Greedy Algorithm]
 *
 * Greedy decision: accept a delivery if remainingCapacity >= packageWeight.
 * This is a per-delivery feasibility check, not a global packing optimisation.
 */
public class CapacityService {

    private final DataStore data;

    public CapacityService(DataStore data) {
        this.data = data;
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Capacity Checking [Greedy Algorithm] ---");
            System.out.println("1. Check Capacity for a Vehicle + Package");
            System.out.println("2. View Capacity Status of All Vehicles");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> checkInteractive(scanner);
                case 2 -> viewAll();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Greedy check: calculate used capacity, determine remaining, accept/reject.
     */
    public boolean check(String vehicleId, String packageId) {
        Vehicle veh = data.vehicleMap.get(vehicleId);
        PackageItem pkg = data.packageMap.get(packageId);

        if (veh == null) { System.out.println("Vehicle not found: " + vehicleId); return false; }
        if (pkg == null) { System.out.println("Package not found: " + packageId); return false; }

        double usedCap = calculateUsedCapacity(vehicleId);
        double remaining = veh.getCapacity() - usedCap;

        System.out.println("\n--- Capacity Check (Greedy) ---");
        System.out.println("Vehicle   : " + veh.getId() + " | " + veh.getName()
                + " | Total capacity: " + veh.getCapacity() + " kg");
        System.out.println("In use    : " + usedCap + " kg");
        System.out.println("Remaining : " + remaining + " kg");
        System.out.println("Package   : " + pkg.getId() + " | Weight: " + pkg.getWeight() + " kg");

        // Greedy decision
        if (pkg.getWeight() <= remaining) {
            System.out.println("RESULT    : ACCEPTED - Package fits within vehicle capacity.");
            return true;
        } else {
            System.out.println("RESULT    : REJECTED - Package weight (" + pkg.getWeight()
                    + " kg) exceeds remaining capacity (" + remaining + " kg).");
            return false;
        }
    }

    private double calculateUsedCapacity(String vehicleId) {
        double used = 0;
        for (Delivery d : data.deliveries) {
            if (vehicleId.equals(d.getVehicleId()) && "InTransit".equalsIgnoreCase(d.getStatus())) {
                PackageItem p = data.packageMap.get(d.getPackageId());
                if (p != null) used += p.getWeight();
            }
        }
        return used;
    }

    private void checkInteractive(Scanner scanner) {
        String vid = InputHelper.readNonEmpty(scanner, "Vehicle ID: ");
        String pid = InputHelper.readNonEmpty(scanner, "Package ID: ");
        check(vid, pid);
    }

    private void viewAll() {
        if (data.vehicles.isEmpty()) { System.out.println("No vehicles registered."); return; }
        System.out.println("\n--- Vehicle Capacity Status ---");
        System.out.printf("%-8s %-20s %-10s %-10s %-10s%n",
                "ID", "Name", "Total(kg)", "Used(kg)", "Free(kg)");
        System.out.println("-".repeat(60));
        for (Vehicle v : data.vehicles) {
            double used = calculateUsedCapacity(v.getId());
            double free = v.getCapacity() - used;
            System.out.printf("%-8s %-20s %-10.1f %-10.1f %-10.1f%n",
                    v.getId(), v.getName(), v.getCapacity(), used, free);
        }
    }
}
