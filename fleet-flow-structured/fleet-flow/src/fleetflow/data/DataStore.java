package fleetflow.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import fleetflow.model.Delivery;
import fleetflow.model.Driver;
import fleetflow.model.PackageItem;
import fleetflow.model.Route;
import fleetflow.model.Vehicle;
import fleetflow.model.Warehouse;

/**
 * In-memory storage for the FleetFlow platform.
 * Lists are the primary stores; Maps provide O(1) ID lookup (Features 3 and 5).
 * Data resets when the program closes.
 */
public final class DataStore {

    // Primary lists (for iteration, sorting, and algorithm input)
    public final List<Vehicle>     vehicles    = new ArrayList<>();
    public final List<Driver>      drivers     = new ArrayList<>();
    public final List<Warehouse>   warehouses  = new ArrayList<>();
    public final List<PackageItem> packages    = new ArrayList<>();
    public final List<Route>       routes      = new ArrayList<>();
    public final List<Delivery>    deliveries  = new ArrayList<>();

    // HashMap lookup tables for O(1) record retrieval (Hashing / Record Lookup)
    public final Map<String, Vehicle>     vehicleMap    = new LinkedHashMap<>();
    public final Map<String, Driver>      driverMap     = new LinkedHashMap<>();
    public final Map<String, Warehouse>   warehouseMap  = new LinkedHashMap<>();
    public final Map<String, PackageItem> packageMap    = new LinkedHashMap<>();
    public final Map<String, Route>       routeMap      = new LinkedHashMap<>();
    public final Map<String, Delivery>    deliveryMap   = new LinkedHashMap<>();

    // ---------------------------------------------------------------
    // Helpers – keep list and map in sync
    // ---------------------------------------------------------------

    public void addVehicle(Vehicle v) {
        if (!vehicleMap.containsKey(v.getId())) {
            vehicles.add(v);
            vehicleMap.put(v.getId(), v);
        }
    }

    public void addDriver(Driver d) {
        if (!driverMap.containsKey(d.getId())) {
            drivers.add(d);
            driverMap.put(d.getId(), d);
        }
    }

    public void addWarehouse(Warehouse w) {
        if (!warehouseMap.containsKey(w.getId())) {
            warehouses.add(w);
            warehouseMap.put(w.getId(), w);
        }
    }

    public void addPackage(PackageItem p) {
        if (!packageMap.containsKey(p.getId())) {
            packages.add(p);
            packageMap.put(p.getId(), p);
        }
    }

    public void addRoute(Route r) {
        if (!routeMap.containsKey(r.getId())) {
            routes.add(r);
            routeMap.put(r.getId(), r);
        }
    }

    public void addDelivery(Delivery d) {
        if (!deliveryMap.containsKey(d.getId())) {
            deliveries.add(d);
            deliveryMap.put(d.getId(), d);
        }
    }

    public boolean removeVehicle(String id) {
        Vehicle v = vehicleMap.remove(id);
        if (v != null) { vehicles.remove(v); return true; }
        return false;
    }

    public boolean removeDriver(String id) {
        Driver d = driverMap.remove(id);
        if (d != null) { drivers.remove(d); return true; }
        return false;
    }

    public boolean removeWarehouse(String id) {
        Warehouse w = warehouseMap.remove(id);
        if (w != null) { warehouses.remove(w); return true; }
        return false;
    }

    public boolean removePackage(String id) {
        PackageItem p = packageMap.remove(id);
        if (p != null) { packages.remove(p); return true; }
        return false;
    }

    public boolean removeRoute(String id) {
        Route r = routeMap.remove(id);
        if (r != null) { routes.remove(r); return true; }
        return false;
    }

    public boolean removeDelivery(String id) {
        Delivery d = deliveryMap.remove(id);
        if (d != null) { deliveries.remove(d); return true; }
        return false;
    }

    // ---------------------------------------------------------------
    // Sample data
    // ---------------------------------------------------------------

    public void loadSampleData() {
        if (!vehicles.isEmpty()) {
            System.out.println("Sample data already loaded.");
            return;
        }

        // Vehicles
        Vehicle v1 = new Vehicle("V101", "Heavy Truck One",  "Truck", 2500);
        v1.setLastServiceDate(LocalDate.of(2026, 1, 10));
        v1.setNextServiceDate(LocalDate.of(2026, 7, 10));
        v1.setAvailable(false);

        Vehicle v2 = new Vehicle("V102", "Express Van One",  "Van",   900);
        v2.setLastServiceDate(LocalDate.of(2026, 3, 15));
        v2.setNextServiceDate(LocalDate.of(2026, 9, 15));

        Vehicle v3 = new Vehicle("V103", "City Van Two",     "Van",   800);
        v3.setLastServiceDate(LocalDate.of(2025, 11, 1));
        v3.setNextServiceDate(LocalDate.of(2026, 5, 1));   // overdue

        addVehicle(v1);
        addVehicle(v2);
        addVehicle(v3);

        // Drivers
        Driver d1 = new Driver("D101", "Arun Kumar",    "DL-1001");
        d1.setAvailable(false);
        d1.setAssignedVehicleId("V101");

        Driver d2 = new Driver("D102", "Maya Rao",      "DL-1002");
        Driver d3 = new Driver("D103", "Rajesh Singh",  "DL-1003");

        addDriver(d1);
        addDriver(d2);
        addDriver(d3);

        // Warehouses
        Warehouse w1 = new Warehouse("W101", "Central Warehouse", "Chennai",   1000, 400);
        Warehouse w2 = new Warehouse("W102", "Western Hub",       "Bangalore", 800,  600);
        addWarehouse(w1);
        addWarehouse(w2);

        // Packages
        addPackage(new PackageItem("P101", "SHP1025", "12 Main Street",  "InTransit",  "High",   30.0));
        addPackage(new PackageItem("P102", "SHP1026", "8 Lake Road",     "Pending",    "High",   15.0));
        addPackage(new PackageItem("P103", "SHP1027", "45 Hill Top",     "Pending",    "Medium", 50.0));
        addPackage(new PackageItem("P104", "SHP1028", "9 Market Street", "Pending",    "Low",    8.0));
        addPackage(new PackageItem("P105", "SHP1020", "5 Gandhi Road",   "Delivered",  "Medium", 20.0));
        addPackage(new PackageItem("P106", "SHP1021", "22 North Street", "Delivered",  "High",   12.0));

        // Routes
        addRoute(new Route("R101", "Chennai-Vellore Corridor",   "Chennai",   "Vellore",   140));
        addRoute(new Route("R102", "Vellore-Bangalore Link",     "Vellore",   "Bangalore", 210));
        addRoute(new Route("R103", "Bangalore-Salem Highway",    "Bangalore", "Salem",     200));
        addRoute(new Route("R104", "Salem-Chennai Expressway",   "Salem",     "Chennai",   340));
        addRoute(new Route("R105", "Chennai-Bangalore Direct",   "Chennai",   "Bangalore", 350));
        addRoute(new Route("R106", "Vellore-Salem Connect",      "Vellore",   "Salem",     180));

        // Deliveries  (statuses: Pending / InTransit / Delivered)
        addDelivery(new Delivery("DEL100", "P105", "D101", "V101", "R101", "Medium", "Delivered"));
        addDelivery(new Delivery("DEL101", "P101", "D101", "V101", "R101", "High",   "InTransit"));
        addDelivery(new Delivery("DEL102", "P106", "D102", "V102", "R102", "High",   "Delivered"));

        System.out.println("Sample data loaded successfully.");
    }
}
