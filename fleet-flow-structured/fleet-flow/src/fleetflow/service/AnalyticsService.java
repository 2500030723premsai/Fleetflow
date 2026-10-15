package fleetflow.service;

import fleetflow.data.DataStore;
import fleetflow.engine.randomized.ParallelReduce;

/**
 * Feature 15 – Analytics Summary [Parallel Reduce]
 * Uses ParallelReduce.countMatching() and ParallelReduce.count() for all aggregations.
 * ParallelReduce uses Java parallel streams internally.
 */
public class AnalyticsService {

    private final DataStore data;

    public AnalyticsService(DataStore data) {
        this.data = data;
    }

    public void show() {
        System.out.println("\n=== ANALYTICS SUMMARY [Parallel Reduce] ===");

        // Totals via ParallelReduce.count()
        long totalVehicles  = ParallelReduce.count(data.vehicles);
        long totalDrivers   = ParallelReduce.count(data.drivers);
        long totalPackages  = ParallelReduce.count(data.packages);
        long totalWarehouses= ParallelReduce.count(data.warehouses);
        long totalRoutes    = ParallelReduce.count(data.routes);
        long totalDeliveries= ParallelReduce.count(data.deliveries);

        // Delivery breakdowns via ParallelReduce.countMatching()
        long pending   = ParallelReduce.countMatching(data.deliveries,
                d -> "Pending".equalsIgnoreCase(d.getStatus()));
        long inTransit = ParallelReduce.countMatching(data.deliveries,
                d -> "InTransit".equalsIgnoreCase(d.getStatus()));
        long delivered = ParallelReduce.countMatching(data.deliveries,
                d -> "Delivered".equalsIgnoreCase(d.getStatus()));

        // Driver availability via ParallelReduce.countMatching()
        long availDrivers = ParallelReduce.countMatching(data.drivers, d -> {
            boolean busy = data.deliveries.stream().anyMatch(del ->
                    d.getId().equals(del.getDriverId()) && "InTransit".equalsIgnoreCase(del.getStatus()));
            return d.isAvailable() && !busy;
        });

        // Vehicle capacity utilisation
        double totalCap = 0, usedCap = 0;
        for (fleetflow.model.Vehicle v : data.vehicles) totalCap += v.getCapacity();
        for (fleetflow.model.Delivery del : data.deliveries) {
            if ("InTransit".equalsIgnoreCase(del.getStatus())) {
                fleetflow.model.PackageItem p = data.packageMap.get(del.getPackageId());
                if (p != null) usedCap += p.getWeight();
            }
        }
        double utilPct = totalCap > 0 ? (usedCap / totalCap) * 100.0 : 0.0;

        System.out.println("\n  Total Vehicles        : " + totalVehicles);
        System.out.println("  Total Drivers         : " + totalDrivers);
        System.out.println("  Total Packages        : " + totalPackages);
        System.out.println("  Total Warehouses      : " + totalWarehouses);
        System.out.println("  Total Routes          : " + totalRoutes);
        System.out.println("  Total Deliveries      : " + totalDeliveries);
        System.out.println("  -- Pending            : " + pending);
        System.out.println("  -- In Transit         : " + inTransit);
        System.out.println("  -- Delivered          : " + delivered);
        System.out.println("  Available Drivers     : " + availDrivers);
        System.out.printf("  Vehicle Capacity Use  : %.1f%% (%.1f / %.1f kg)%n",
                utilPct, usedCap, totalCap);
    }
}
