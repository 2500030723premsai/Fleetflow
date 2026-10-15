package fleetflow.service;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.Delivery;
import fleetflow.model.Driver;
import fleetflow.model.PackageItem;
import fleetflow.model.Vehicle;
import fleetflow.util.InputHelper;

/**
 * Feature 14 – Delivery Status Tracking [Finite-State Transitions]
 *
 * Valid transitions:
 *   Pending   -> InTransit
 *   InTransit -> Delivered
 *
 * All other transitions are rejected.
 * When a delivery reaches Delivered, the driver and vehicle are freed.
 */
public class StatusTrackingService {

    private final DataStore data;

    public StatusTrackingService(DataStore data) {
        this.data = data;
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Delivery Status Tracking [Finite-State Transitions] ---");
            System.out.println("1. View All Delivery Statuses");
            System.out.println("2. Update Delivery Status");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> listStatuses();
                case 2 -> updateStatusInteractive(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void listStatuses() {
        if (data.deliveries.isEmpty()) {
            System.out.println("No deliveries registered.");
            return;
        }
        System.out.println("\n--- Delivery Status Board ---");
        System.out.printf("%-10s %-10s %-10s %-10s %-10s%n",
                "Del.ID", "Package", "Driver", "Vehicle", "Status");
        System.out.println("-".repeat(55));
        for (Delivery d : data.deliveries) {
            System.out.printf("%-10s %-10s %-10s %-10s %-10s%n",
                    d.getId(), d.getPackageId(), d.getDriverId(),
                    d.getVehicleId(), d.getStatus());
        }
    }

    /**
     * Applies a finite-state transition to a delivery.
     * Allowed: Pending -> InTransit, InTransit -> Delivered
     */
    public void updateStatusInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Delivery ID: ");
        Delivery delivery = data.deliveryMap.get(id);
        if (delivery == null) {
            System.out.println("Delivery not found: " + id);
            return;
        }

        String current = delivery.getStatus();
        System.out.println("Current status: " + current);
        System.out.println("Allowed transitions:");

        if ("Pending".equalsIgnoreCase(current)) {
            System.out.println("  Pending -> InTransit");
        } else if ("InTransit".equalsIgnoreCase(current)) {
            System.out.println("  InTransit -> Delivered");
        } else if ("Delivered".equalsIgnoreCase(current)) {
            System.out.println("  Delivery is already in final state: Delivered. No further transitions.");
            return;
        } else {
            System.out.println("  Unknown current status '" + current + "'.");
            return;
        }

        String newStatus = InputHelper.readNonEmpty(scanner, "New status: ");
        applyTransition(delivery, newStatus.trim());
    }

    /**
     * Validates and applies the finite-state transition.
     */
    public boolean applyTransition(Delivery delivery, String newStatus) {
        String current = delivery.getStatus();

        boolean valid = isValidTransition(current, newStatus);
        if (!valid) {
            System.out.println("INVALID TRANSITION: '" + current + "' -> '" + newStatus
                    + "'. Allowed: Pending->InTransit, InTransit->Delivered.");
            return false;
        }

        delivery.setStatus(newStatus);
        System.out.println("Status updated: " + delivery.getId() + " -> " + newStatus);

        // Update package status to match
        PackageItem pkg = data.packageMap.get(delivery.getPackageId());
        if (pkg != null) pkg.setStatus(newStatus);

        // On delivery completion, free driver and vehicle
        if ("Delivered".equalsIgnoreCase(newStatus)) {
            Driver driver = data.driverMap.get(delivery.getDriverId());
            if (driver != null) {
                driver.setAvailable(true);
                driver.setAssignedVehicleId("");
                System.out.println("Driver " + driver.getId() + " is now available.");
            }
            Vehicle vehicle = data.vehicleMap.get(delivery.getVehicleId());
            if (vehicle != null) {
                vehicle.setAvailable(true);
                System.out.println("Vehicle " + vehicle.getId() + " is now available.");
            }
        }
        return true;
    }

    private boolean isValidTransition(String from, String to) {
        if ("Pending".equalsIgnoreCase(from)   && "InTransit".equalsIgnoreCase(to))  return true;
        if ("InTransit".equalsIgnoreCase(from) && "Delivered".equalsIgnoreCase(to))  return true;
        return false;
    }
}
