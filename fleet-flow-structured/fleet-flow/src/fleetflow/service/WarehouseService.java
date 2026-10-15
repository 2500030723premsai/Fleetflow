package fleetflow.service;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.Warehouse;
import fleetflow.util.InputHelper;

/**
 * Feature 4 – Warehouse Management [Array/List Operations]
 */
public class WarehouseService {

    private final DataStore data;

    public WarehouseService(DataStore data) {
        this.data = data;
    }

    public void add(Warehouse w) {
        if (data.warehouseMap.containsKey(w.getId())) {
            System.out.println("Warehouse ID " + w.getId() + " already exists.");
            return;
        }
        data.addWarehouse(w);
    }

    public void list() {
        if (data.warehouses.isEmpty()) {
            System.out.println("No warehouses registered.");
        } else {
            System.out.println("\n--- Warehouse List (" + data.warehouses.size() + ") ---");
            data.warehouses.forEach(System.out::println);
        }
    }

    public Warehouse findById(String id) {
        return data.warehouseMap.get(id);
    }

    public boolean remove(String id) {
        Warehouse w = data.warehouseMap.get(id);
        if (w == null) { System.out.println("Warehouse not found: " + id); return false; }
        data.removeWarehouse(id);
        System.out.println("Warehouse " + id + " removed.");
        return true;
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Warehouse Management [Array/List Operations] ---");
            System.out.println("1. Add Warehouse");
            System.out.println("2. View All Warehouses");
            System.out.println("3. Search Warehouse by ID");
            System.out.println("4. Update Warehouse");
            System.out.println("5. Remove Warehouse");
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
        String id = InputHelper.readNonEmpty(scanner, "Warehouse ID: ");
        if (data.warehouseMap.containsKey(id)) { System.out.println("Warehouse ID already exists."); return; }
        String name     = InputHelper.readNonEmpty(scanner, "Name: ");
        String location = InputHelper.readNonEmpty(scanner, "Location: ");
        int cap         = readPositiveInt(scanner, "Total capacity (units): ");
        Warehouse w     = new Warehouse(id, name, location, cap, 0);
        data.addWarehouse(w);
        System.out.println("Warehouse added: " + w);
    }

    private void searchInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Warehouse ID: ");
        Warehouse w = findById(id);
        if (w == null) System.out.println("Warehouse not found: " + id);
        else           System.out.println(w);
    }

    private void updateInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Warehouse ID to update: ");
        Warehouse w = data.warehouseMap.get(id);
        if (w == null) { System.out.println("Warehouse not found: " + id); return; }
        System.out.println("Current: " + w);
        System.out.print("New name (Enter to keep): ");
        String name = scanner.nextLine().trim();
        System.out.print("New location (Enter to keep): ");
        String loc = scanner.nextLine().trim();
        System.out.print("New capacity (Enter to keep): ");
        String capStr = scanner.nextLine().trim();
        if (!name.isBlank()) w.setName(name);
        if (!loc.isBlank())  w.setLocation(loc);
        if (!capStr.isBlank()) {
            try { w.setCapacity(Integer.parseInt(capStr)); } catch (NumberFormatException e) { System.out.println("Invalid capacity, keeping old value."); }
        }
        System.out.println("Warehouse updated: " + w);
    }

    private void removeInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Warehouse ID to remove: ");
        remove(id);
    }

    private int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val > 0) return val;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a positive integer.");
        }
    }
}
