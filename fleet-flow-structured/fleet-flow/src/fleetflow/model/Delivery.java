package fleetflow.model;

public class Delivery {
    private final String id;
    private String packageId;
    private String driverId;
    private String vehicleId;
    private String routeId;
    private String priority;   // High / Medium / Low
    private String status;     // Pending / InTransit / Delivered

    public Delivery(String id, String packageId, String driverId,
                    String vehicleId, String routeId, String status) {
        this(id, packageId, driverId, vehicleId, routeId, "Medium", status);
    }

    public Delivery(String id, String packageId, String driverId,
                    String vehicleId, String routeId, String priority, String status) {
        this.id = id;
        this.packageId = packageId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.routeId = routeId;
        this.priority = (priority == null || priority.isBlank()) ? "Medium" : priority;
        this.status = (status == null || status.isBlank()) ? "Pending" : status;
    }

    public String getId()                       { return id; }
    public String getPackageId()                { return packageId; }
    public void   setPackageId(String pid)      { this.packageId = pid; }
    public String getDriverId()                 { return driverId; }
    public void   setDriverId(String did)       { this.driverId = did; }
    public String getVehicleId()                { return vehicleId; }
    public void   setVehicleId(String vid)      { this.vehicleId = vid; }
    public String getRouteId()                  { return routeId; }
    public void   setRouteId(String rid)        { this.routeId = rid; }
    public String getPriority()                 { return priority; }
    public void   setPriority(String priority)  { this.priority = priority; }
    public String getStatus()                   { return status; }
    public void   setStatus(String status)      { this.status = status; }

    @Override
    public String toString() {
        return id + " | Package: " + packageId + " | Driver: " + driverId
                + " | Vehicle: " + vehicleId + " | Route: " + routeId
                + " | Priority: " + priority + " | Status: " + status;
    }
}
