package fleetflow.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.engine.flow.BipartiteMatching;
import fleetflow.engine.randomized.ParallelReduce;
import fleetflow.model.Delivery;
import fleetflow.model.Driver;
import fleetflow.model.PackageItem;
import fleetflow.model.Vehicle;
import fleetflow.util.InputHelper;

/**
 * Feature 2 – Driver Management [Array/List Operations]
 * Feature 8 – Automatic Driver Assignment [Bipartite Matching]
 */
public class DriverService {

    private final DataStore data;

    public DriverService(DataStore data) {
        this.data = data;
    }

    // ---------------------------------------------------------------- add
    public void add(Driver d) {
        if (data.driverMap.containsKey(d.getId())) {
            System.out.println("Driver ID " + d.getId() + " already exists. Skipping.");
            return;
        }
        data.addDriver(d);
    }

    // ---------------------------------------------------------------- list
    public void list() {
        if (data.drivers.isEmpty()) {
            System.out.println("No drivers registered.");
        } else {
            System.out.println("\n--- Driver List (" + data.drivers.size() + ") ---");
            data.drivers.forEach(System.out::println);
        }
    }

    // ---------------------------------------------------------------- find
    public Driver findById(String id) {
        return data.driverMap.get(id);
    }

    // ---------------------------------------------------------------- update
    public void update(String id, String newName, String newLicense) {
        Driver d = data.driverMap.get(id);
        if (d == null) { System.out.println("Driver not found: " + id); return; }
        if (!newName.isBlank())    d.setName(newName);
        if (!newLicense.isBlank()) d.setLicenseNo(newLicense);
        System.out.println("Driver updated: " + d);
    }

    // ---------------------------------------------------------------- remove
    public boolean remove(String id) {
        Driver d = data.driverMap.get(id);
        if (d == null) { System.out.println("Driver not found: " + id); return false; }
        for (Delivery del : data.deliveries) {
            if (id.equals(del.getDriverId()) && "InTransit".equalsIgnoreCase(del.getStatus())) {
                System.out.println("Cannot remove driver " + id + ": active delivery " + del.getId() + " is InTransit.");
                return false;
            }
        }
        data.removeDriver(id);
        System.out.println("Driver " + id + " removed.");
        return true;
    }

    // ---------------------------------------------------------------- interactive sub-menu (Feature 2)
    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Driver Management [Array/List Operations] ---");
            System.out.println("1. Add Driver");
            System.out.println("2. View All Drivers");
            System.out.println("3. Search Driver by ID");
            System.out.println("4. Update Driver");
            System.out.println("5. Remove Driver");
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
        String id = InputHelper.readNonEmpty(scanner, "Driver ID: ");
        if (data.driverMap.containsKey(id)) { System.out.println("Driver ID already exists."); return; }
        String name    = InputHelper.readNonEmpty(scanner, "Name: ");
        String license = InputHelper.readNonEmpty(scanner, "License number: ");
        Driver d = new Driver(id, name, license);
        data.addDriver(d);
        System.out.println("Driver added: " + d);
    }

    private void searchInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Driver ID: ");
        Driver d  = findById(id);
        if (d == null) System.out.println("Driver not found: " + id);
        else           System.out.println(d);
    }

    private void updateInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Driver ID to update: ");
        Driver d  = data.driverMap.get(id);
        if (d == null) { System.out.println("Driver not found: " + id); return; }
        System.out.println("Current: " + d);
        System.out.print("New name (Enter to keep '" + d.getName() + "'): ");
        String name = scanner.nextLine().trim();
        System.out.print("New license (Enter to keep '" + d.getLicenseNo() + "'): ");
        String lic = scanner.nextLine().trim();
        update(id, name, lic);
    }

    private void removeInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Driver ID to remove: ");
        remove(id);
    }

    // ---------------------------------------------------------------- Feature 8 – Automatic Driver Assignment
    public void autoAssign(Scanner scanner) {
        System.out.println("\n=== AUTOMATIC DRIVER ASSIGNMENT [Bipartite Matching] ===");

        if (data.drivers.isEmpty())  { System.out.println("No drivers registered."); return; }
        if (data.packages.isEmpty()) { System.out.println("No packages registered."); return; }

        // Available drivers: available flag true AND no active InTransit delivery
        List<Driver> availDrivers = new ArrayList<>();
        for (Driver d : data.drivers) {
            boolean busy = false;
            for (Delivery del : data.deliveries) {
                if (d.getId().equals(del.getDriverId()) && "InTransit".equalsIgnoreCase(del.getStatus())) {
                    busy = true;
                    break;
                }
            }
            if (d.isAvailable() && !busy) availDrivers.add(d);
        }

        // Unassigned tasks: Pending packages with no existing InTransit or Delivered delivery
        List<PackageItem> unassignedTasks = new ArrayList<>();
        for (PackageItem p : data.packages) {
            boolean hasActive = false;
            for (Delivery del : data.deliveries) {
                if (p.getId().equals(del.getPackageId()) &&
                    ("InTransit".equalsIgnoreCase(del.getStatus()) || "Delivered".equalsIgnoreCase(del.getStatus()))) {
                    hasActive = true;
                    break;
                }
            }
            if (!hasActive && "Pending".equalsIgnoreCase(p.getStatus())) unassignedTasks.add(p);
        }

        System.out.println("\nAvailable Drivers (" + availDrivers.size() + "):");
        if (availDrivers.isEmpty()) System.out.println("  None.");
        else availDrivers.forEach(d -> System.out.println("  " + d));

        System.out.println("\nUnassigned Pending Tasks (" + unassignedTasks.size() + "):");
        if (unassignedTasks.isEmpty()) System.out.println("  None.");
        else unassignedTasks.forEach(p -> System.out.println("  " + p));

        if (availDrivers.isEmpty() || unassignedTasks.isEmpty()) {
            System.out.println("\nNo assignments possible.");
            return;
        }

        String confirm = InputHelper.readNonEmpty(scanner, "\nRun bipartite matching assignment? (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) { System.out.println("Assignment cancelled."); return; }

        int m = availDrivers.size();
        int n = unassignedTasks.size();
        boolean[][] edges = new boolean[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                edges[i][j] = availDrivers.get(i).getLicenseNo() != null &&
                               !availDrivers.get(i).getLicenseNo().isBlank();

        int[] match = BipartiteMatching.findMatching(edges);

        System.out.println("\n--- Assignment Results ---");
        int assigned = 0, unassigned = 0;

        for (int j = 0; j < n; j++) {
            PackageItem task = unassignedTasks.get(j);
            int dIdx = match[j];
            if (dIdx >= 0) {
                Driver dr = availDrivers.get(dIdx);
                Vehicle veh = null;
                for (Vehicle v : data.vehicles) {
                    if (v.isAvailable()) { veh = v; break; }
                }
                dr.setAvailable(false);
                if (veh != null) {
                    veh.setAvailable(false);
                    dr.setAssignedVehicleId(veh.getId());
                }
                task.setStatus("InTransit");
                String delId = "DEL" + (200 + data.deliveries.size());
                String routeId = data.routes.isEmpty() ? "R101" : data.routes.get(0).getId();
                Delivery newDel = new Delivery(delId, task.getId(), dr.getId(),
                        veh != null ? veh.getId() : "UNASSIGNED", routeId, "High", "InTransit");
                data.addDelivery(newDel);
                System.out.println("[ASSIGNED] " + task.getId() + " -> Driver: " + dr.getId()
                        + " (" + dr.getName() + ") | Vehicle: " + (veh != null ? veh.getId() : "None")
                        + " | Delivery: " + delId);
                assigned++;
            } else {
                System.out.println("[UNASSIGNED] " + task.getId() + " -> No eligible driver available.");
                unassigned++;
            }
        }
        System.out.println("\nSummary: " + assigned + " assigned, " + unassigned + " unassigned.");
    }

    // ---------------------------------------------------------------- Driver performance
    public void showPerformance() {
        System.out.println("\n=== DRIVER PERFORMANCE SUMMARY [Parallel Reduce] ===");
        if (data.drivers.isEmpty()) { System.out.println("No drivers registered."); return; }
        for (Driver driver : data.drivers) {
            long completed = ParallelReduce.countMatching(data.deliveries,
                    del -> driver.getId().equals(del.getDriverId()) &&
                           "Delivered".equalsIgnoreCase(del.getStatus()));
            System.out.println("  " + driver.getId() + " | " + driver.getName()
                    + " | Completed Deliveries: " + completed);
        }
        long total = ParallelReduce.countMatching(data.deliveries,
                del -> "Delivered".equalsIgnoreCase(del.getStatus()));
        System.out.println("Total completed deliveries: " + total);
    }
}
