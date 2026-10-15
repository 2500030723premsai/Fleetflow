package fleetflow.service;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.Delivery;
import fleetflow.model.Vehicle;
import fleetflow.util.InputHelper;

/**
 * Feature 1 – Vehicle Management [Array/List Operations]
 * CRUD operations on the vehicles list in DataStore.
 */
public class VehicleService {

    private final DataStore data;

    public VehicleService(DataStore data) {
        this.data = data;
    }

    // ---------------------------------------------------------------- add
    public void add(Vehicle v) {
        if (data.vehicleMap.containsKey(v.getId())) {
            System.out.println("Vehicle ID " + v.getId() + " already exists. Skipping.");
            return;
        }
        data.addVehicle(v);
    }

    // ---------------------------------------------------------------- list
    public void list() {
        if (data.vehicles.isEmpty()) {
            System.out.println("No vehicles registered.");
        } else {
            System.out.println("\n--- Vehicle List (" + data.vehicles.size() + ") ---");
            data.vehicles.forEach(System.out::println);
        }
    }

    // ---------------------------------------------------------------- search
    public Vehicle findById(String id) {
        return data.vehicleMap.get(id);
    }

    // ---------------------------------------------------------------- update
    public void update(String id, String newName, String newType) {
        Vehicle v = data.vehicleMap.get(id);
        if (v == null) {
            System.out.println("Vehicle not found: " + id);
            return;
        }
        if (!newName.isBlank()) v.setName(newName);
        if (!newType.isBlank()) v.setType(newType);
        System.out.println("Vehicle updated: " + v);
    }

    // ---------------------------------------------------------------- remove
    public boolean remove(String id) {
        Vehicle v = data.vehicleMap.get(id);
        if (v == null) {
            System.out.println("Vehicle not found: " + id);
            return false;
        }
        // Do not remove if vehicle has an active (InTransit) delivery
        for (Delivery d : data.deliveries) {
            if (id.equals(d.getVehicleId()) && "InTransit".equalsIgnoreCase(d.getStatus())) {
                System.out.println("Cannot remove vehicle " + id + ": it has an active InTransit delivery (" + d.getId() + ").");
                return false;
            }
        }
        data.removeVehicle(id);
        System.out.println("Vehicle " + id + " removed.");
        return true;
    }

    // ---------------------------------------------------------------- interactive sub-menu
    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Vehicle Management [Array/List Operations] ---");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View All Vehicles");
            System.out.println("3. Search Vehicle by ID");
            System.out.println("4. Update Vehicle");
            System.out.println("5. Remove Vehicle");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> addInteractive(scanner);
                case 2 -> list();
                case 3 -> searchInteractive(scanner);
                case 4 -> updateInteractive(scanner);
                case 5 -> removeInteractive(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addInteractive(Scanner scanner) {
        String id   = InputHelper.readNonEmpty(scanner, "Vehicle ID: ");
        if (data.vehicleMap.containsKey(id)) {
            System.out.println("Vehicle ID already exists.");
            return;
        }
        String name = InputHelper.readNonEmpty(scanner, "Name: ");
        String type = InputHelper.readNonEmpty(scanner, "Type (Truck/Van/Bike): ");
        double cap  = readPositiveDouble(scanner, "Capacity (kg): ");
        Vehicle v   = new Vehicle(id, name, type, cap);
        data.addVehicle(v);
        System.out.println("Vehicle added: " + v);
    }

    private void searchInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Vehicle ID: ");
        Vehicle v = findById(id);
        if (v == null) System.out.println("Vehicle not found: " + id);
        else           System.out.println(v);
    }

    private void updateInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Vehicle ID to update: ");
        Vehicle v = data.vehicleMap.get(id);
        if (v == null) { System.out.println("Vehicle not found: " + id); return; }
        System.out.println("Current: " + v);
        System.out.print("New name (Enter to keep '" + v.getName() + "'): ");
        String name = scanner.nextLine().trim();
        System.out.print("New type (Enter to keep '" + v.getType() + "'): ");
        String type = scanner.nextLine().trim();
        update(id, name, type);
    }

    private void removeInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Vehicle ID to remove: ");
        remove(id);
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
