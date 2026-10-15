package fleetflow.service;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.model.Delivery;
import fleetflow.model.Driver;
import fleetflow.model.PackageItem;
import fleetflow.model.Vehicle;
import fleetflow.util.InputHelper;

/**
 * Feature 5 – Delivery Management [Hashing / Record Lookup]
 * Uses deliveryMap (HashMap) for O(1) lookup.
 */
public class DeliveryService {

    private final DataStore data;

    public DeliveryService(DataStore data) {
        this.data = data;
    }

    public void list() {
        if (data.deliveries.isEmpty()) {
            System.out.println("No deliveries registered.");
        } else {
            System.out.println("\n--- Delivery List (" + data.deliveries.size() + ") ---");
            data.deliveries.forEach(System.out::println);
        }
    }

    public Delivery findById(String id) {
        // HashMap lookup – O(1)
        return data.deliveryMap.get(id);
    }

    public boolean remove(String id) {
        Delivery d = data.deliveryMap.get(id);
        if (d == null) { System.out.println("Delivery not found: " + id); return false; }
        if ("InTransit".equalsIgnoreCase(d.getStatus())) {
            System.out.println("Cannot remove delivery " + id + ": currently InTransit.");
            return false;
        }
        data.removeDelivery(id);
        System.out.println("Delivery " + id + " removed.");
        return true;
    }

    public void menu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Delivery Management [Hashing / Record Lookup] ---");
            System.out.println("1. Create Delivery");
            System.out.println("2. View All Deliveries");
            System.out.println("3. Search Delivery by ID  (HashMap lookup)");
            System.out.println("4. Update Delivery Assignment");
            System.out.println("5. Remove Delivery");
            System.out.println("0. Back");
            int choice = InputHelper.readInt(scanner, "Choose: ");
            switch (choice) {
                case 1 -> createInteractive(scanner);
                case 2 -> list();
                case 3 -> searchInteractive(scanner);
                case 4 -> updateInteractive(scanner);
                case 5 -> removeInteractive(scanner);
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Delivery ID: ");
        if (data.deliveryMap.containsKey(id)) { System.out.println("Delivery ID already exists."); return; }

        String pkgId = InputHelper.readNonEmpty(scanner, "Package ID: ");
        if (!data.packageMap.containsKey(pkgId)) { System.out.println("Package not found: " + pkgId); return; }

        String drvId = InputHelper.readNonEmpty(scanner, "Driver ID: ");
        if (!data.driverMap.containsKey(drvId)) { System.out.println("Driver not found: " + drvId); return; }

        String vehId = InputHelper.readNonEmpty(scanner, "Vehicle ID: ");
        if (!data.vehicleMap.containsKey(vehId)) { System.out.println("Vehicle not found: " + vehId); return; }

        // Capacity check (Feature 13 - Greedy)
        PackageItem pkg = data.packageMap.get(pkgId);
        Vehicle veh     = data.vehicleMap.get(vehId);
        double usedCap  = 0;
        for (Delivery d : data.deliveries) {
            if (vehId.equals(d.getVehicleId()) && "InTransit".equalsIgnoreCase(d.getStatus())) {
                PackageItem activePkg = data.packageMap.get(d.getPackageId());
                if (activePkg != null) usedCap += activePkg.getWeight();
            }
        }
        double remaining = veh.getCapacity() - usedCap;
        System.out.println("Vehicle capacity: " + veh.getCapacity() + " kg | In use: " + usedCap
                + " kg | Available: " + remaining + " kg | Package weight: " + pkg.getWeight() + " kg");
        if (pkg.getWeight() > remaining) {
            System.out.println("REJECTED: Package weight (" + pkg.getWeight()
                    + " kg) exceeds vehicle remaining capacity (" + remaining + " kg).");
            return;
        }

        String routeId = InputHelper.readNonEmpty(scanner, "Route ID: ");
        if (!data.routeMap.containsKey(routeId)) { System.out.println("Route not found: " + routeId); return; }

        String priority = InputHelper.readNonEmpty(scanner, "Priority (High/Medium/Low): ");

        Driver drv = data.driverMap.get(drvId);
        drv.setAvailable(false);
        drv.setAssignedVehicleId(vehId);
        veh.setAvailable(false);
        pkg.setStatus("InTransit");

        Delivery delivery = new Delivery(id, pkgId, drvId, vehId, routeId, priority, "Pending");
        data.addDelivery(delivery);
        System.out.println("Delivery created: " + delivery);
    }

    private void searchInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Delivery ID: ");
        Delivery d = findById(id);
        if (d == null) System.out.println("Delivery not found: " + id);
        else           System.out.println(d);
    }

    private void updateInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Delivery ID to update: ");
        Delivery d = data.deliveryMap.get(id);
        if (d == null) { System.out.println("Delivery not found: " + id); return; }
        System.out.println("Current: " + d);
        System.out.print("New Driver ID (Enter to keep): ");
        String did = scanner.nextLine().trim();
        System.out.print("New Vehicle ID (Enter to keep): ");
        String vid = scanner.nextLine().trim();
        System.out.print("New Priority (Enter to keep): ");
        String prio = scanner.nextLine().trim();
        if (!did.isBlank()) {
            if (!data.driverMap.containsKey(did)) { System.out.println("Driver not found: " + did); return; }
            d.setDriverId(did);
        }
        if (!vid.isBlank()) {
            if (!data.vehicleMap.containsKey(vid)) { System.out.println("Vehicle not found: " + vid); return; }
            d.setVehicleId(vid);
        }
        if (!prio.isBlank()) d.setPriority(prio);
        System.out.println("Delivery updated: " + d);
    }

    private void removeInteractive(Scanner scanner) {
        String id = InputHelper.readNonEmpty(scanner, "Delivery ID to remove: ");
        remove(id);
    }
}
