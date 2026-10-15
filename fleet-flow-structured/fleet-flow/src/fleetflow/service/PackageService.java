package fleetflow.service;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.PackageItem;
import fleetflow.util.InputHelper;

/**
 * Feature 3 – Package Management [Array/List Operations]
 * Uses HashMap (packageMap) for O(1) ID lookup.
 */
public class PackageService {

    private final DataStore data;

    public PackageService(DataStore data) {
        this.data = data;
    }

    public void add(PackageItem p) {
        if (data.packageMap.containsKey(p.getId())) {
            System.out.println("Package ID " + p.getId() + " already exists.");
            return;
        }
        data.addPackage(p);
    }

    public void list() {
        if (data.packages.isEmpty()) {
            System.out.println("No packages registered.");
        } else {
            System.out.println("\n--- Package List (" + data.packages.size() + ") ---");
            data.packages.forEach(System.out::println);
        }
    }

    public PackageItem findById(String id) {
        // HashMap lookup – O(1)
        return data.packageMap.get(id);
    }

    public boolean remove(String id) {
        PackageItem p = data.packageMap.get(id);
        if (p == null) { System.out.println("Package not found: " + id); return false; }
        data.removePackage(id);
        System.out.println("Package " + id + " removed.");
        return true;
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Package Management [Array/List Operations] ---");
            System.out.println("1. Add Package");
            System.out.println("2. View All Packages");
            System.out.println("3. Search Package by ID  (HashMap lookup)");
            System.out.println("4. Update Package");
            System.out.println("5. Remove Package");
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
        String id = InputHelper.readNonEmpty(scanner, "Package ID: ");
        if (data.packageMap.containsKey(id)) { System.out.println("Package ID already exists."); return; }
        String shipId  = InputHelper.readNonEmpty(scanner, "Shipment ID: ");
        String address = InputHelper.readNonEmpty(scanner, "Destination address: ");
        String priority = InputHelper.readNonEmpty(scanner, "Priority (High/Medium/Low): ");
        double weight  = readPositiveDouble(scanner, "Weight (kg): ");
        PackageItem p  = new PackageItem(id, shipId, address, "Pending", priority, weight);
        data.addPackage(p);
        System.out.println("Package added: " + p);
    }

    private void searchInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Package ID: ");
        PackageItem p = findById(id);   // uses HashMap
        if (p == null) System.out.println("Package not found: " + id);
        else           System.out.println(p);
    }

    private void updateInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Package ID to update: ");
        PackageItem p = data.packageMap.get(id);
        if (p == null) { System.out.println("Package not found: " + id); return; }
        System.out.println("Current: " + p);
        System.out.print("New address (Enter to keep): ");
        String addr = scanner.nextLine().trim();
        System.out.print("New priority (Enter to keep): ");
        String prio = scanner.nextLine().trim();
        System.out.print("New weight kg (Enter to keep): ");
        String wt = scanner.nextLine().trim();
        if (!addr.isBlank()) p.setAddress(addr);
        if (!prio.isBlank()) p.setPriority(prio);
        if (!wt.isBlank()) {
            try { p.setWeight(Double.parseDouble(wt)); } catch (NumberFormatException e) { System.out.println("Invalid weight, keeping old value."); }
        }
        System.out.println("Package updated: " + p);
    }

    private void removeInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Package ID to remove: ");
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
