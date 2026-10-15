package fleetflow.service;

import java.util.ArrayList;
import java.util.List;
import fleetflow.data.DataStore;
import fleetflow.model.Delivery;
import fleetflow.model.Driver;
import fleetflow.model.Vehicle;

/**
 * Feature 7 – Driver Availability [Filtering / Searching]
 * Filters drivers based on their actual delivery status rather than just a flag.
 */
public class AvailabilityService {

    private final DataStore data;

    public AvailabilityService(DataStore data) {
        this.data = data;
    }

    public void show() {
        System.out.println("\n=== DRIVER AVAILABILITY [Filtering / Searching] ===");

        if (data.drivers.isEmpty()) {
            System.out.println("No drivers registered.");
            return;
        }

        // Filter: available drivers – no active InTransit delivery AND available flag true
        List<Driver> available = new ArrayList<>();
        List<Driver> busy      = new ArrayList<>();

        for (Driver d : data.drivers) {
            boolean hasActive = false;
            for (Delivery del : data.deliveries) {
                if (d.getId().equals(del.getDriverId()) && "InTransit".equalsIgnoreCase(del.getStatus())) {
                    hasActive = true;
                    break;
                }
            }
            if (d.isAvailable() && !hasActive) available.add(d);
            else                               busy.add(d);
        }

        System.out.println("\nAvailable Drivers (" + available.size() + "):");
        if (available.isEmpty()) System.out.println("  None.");
        else available.forEach(d -> System.out.println("  " + d));

        System.out.println("\nBusy / Unavailable Drivers (" + busy.size() + "):");
        if (busy.isEmpty()) System.out.println("  None.");
        else {
            for (Driver d : busy) {
                String activeDelivery = "None";
                for (Delivery del : data.deliveries) {
                    if (d.getId().equals(del.getDriverId()) && "InTransit".equalsIgnoreCase(del.getStatus())) {
                        activeDelivery = del.getId();
                        break;
                    }
                }
                System.out.println("  " + d + " | Active Delivery: " + activeDelivery);
            }
        }

        System.out.println("\nDrivers who can accept new work: " + available.size());
    }

    /**
     * Synchronises driver availability flags with the current state of deliveries.
     * Marks drivers as available once all their deliveries are Delivered.
     */
    public void syncAvailability() {
        for (Driver d : data.drivers) {
            boolean hasActive = false;
            for (Delivery del : data.deliveries) {
                if (d.getId().equals(del.getDriverId()) && "InTransit".equalsIgnoreCase(del.getStatus())) {
                    hasActive = true;
                    break;
                }
            }
            if (!hasActive) {
                d.setAvailable(true);
                if (!d.getAssignedVehicleId().isBlank()) {
                    Vehicle v = data.vehicleMap.get(d.getAssignedVehicleId());
                    if (v != null) v.setAvailable(true);
                    d.setAssignedVehicleId("");
                }
            }
        }
    }
}
