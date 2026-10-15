package fleetflow.main;

import java.util.Scanner;
import fleetflow.data.DataStore;
import fleetflow.service.AddressService;
import fleetflow.service.AnalyticsService;
import fleetflow.service.AvailabilityService;
import fleetflow.service.CapacityService;
import fleetflow.service.DeliveryService;
import fleetflow.service.DriverService;
import fleetflow.service.MaintenanceService;
import fleetflow.service.PackageService;
import fleetflow.service.RouteService;
import fleetflow.service.ShipmentService;
import fleetflow.service.StatusTrackingService;
import fleetflow.service.VehicleService;
import fleetflow.service.WarehouseService;
import fleetflow.util.InputHelper;

/**
 * Entry point for FleetFlow – Intelligent Logistics Management Platform.
 * Terminal-based application. Java 21.
 */
public class Main {

    public static void main(String[] args) {

        DataStore data = new DataStore();

        // Initialise all services
        VehicleService      vehicles      = new VehicleService(data);
        DriverService       drivers       = new DriverService(data);
        PackageService      packages      = new PackageService(data);
        WarehouseService    warehouses    = new WarehouseService(data);
        DeliveryService     deliveries    = new DeliveryService(data);
        MaintenanceService  maintenance   = new MaintenanceService(data);
        AvailabilityService availability  = new AvailabilityService(data);
        RouteService        routes        = new RouteService(data);
        AddressService      address       = new AddressService(data);
        ShipmentService     shipments     = new ShipmentService(data);
        CapacityService     capacity      = new CapacityService(data);
        StatusTrackingService statusTrack = new StatusTrackingService(data);
        AnalyticsService    analytics     = new AnalyticsService(data);

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                int choice = InputHelper.readInt(scanner, "Choose an option: ");
                switch (choice) {
                    case 1  -> vehicles.menu(scanner);
                    case 2  -> drivers.menu(scanner);
                    case 3  -> packages.menu(scanner);
                    case 4  -> warehouses.menu(scanner);
                    case 5  -> deliveries.menu(scanner);
                    case 6  -> maintenance.menu(scanner);
                    case 7  -> { availability.syncAvailability(); availability.show(); }
                    case 8  -> drivers.autoAssign(scanner);
                    case 9  -> routes.menu(scanner);
                    case 10 -> address.menu(scanner);
                    case 11 -> shipments.searchInteractive(scanner);
                    case 12 -> shipments.listByPriority();
                    case 13 -> capacity.menu(scanner);
                    case 14 -> statusTrack.menu(scanner);
                    case 15 -> analytics.show();
                    case 16 -> { data.loadSampleData(); }
                    case 0  -> running = false;
                    default -> System.out.println("Invalid option. Enter a number from the menu.");
                }
            }
            System.out.println("\nFleetFlow closed. Goodbye.");
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("========== FLEETFLOW: INTELLIGENT LOGISTICS MANAGEMENT ==========");
        System.out.println();
        System.out.println("1.  Vehicle Management              [Array/List Operations]");
        System.out.println("2.  Driver Management               [Array/List Operations]");
        System.out.println("3.  Package Management              [Array/List Operations]");
        System.out.println("4.  Warehouse Management            [Array/List Operations]");
        System.out.println("5.  Delivery Management             [Hashing / Record Lookup]");
        System.out.println("6.  Vehicle Maintenance             [Date Comparison / Sorting]");
        System.out.println("7.  Driver Availability             [Filtering / Searching]");
        System.out.println("8.  Automatic Driver Assignment     [Bipartite Matching]");
        System.out.println("9.  Route Optimization              [Bitmask DP / TSP]");
        System.out.println("10. Address Correction              [Levenshtein Distance]");
        System.out.println("11. Shipment Search                 [KMP]");
        System.out.println("12. Delivery Priority               [Randomized QuickSort]");
        System.out.println("13. Capacity Checking               [Greedy Algorithm]");
        System.out.println("14. Delivery Status Tracking        ");
        System.out.println("15. Analytics Summary               ");
        System.out.println("16. Load Sample Data");
        System.out.println("0.  Exit");
        System.out.println();
    }
}
