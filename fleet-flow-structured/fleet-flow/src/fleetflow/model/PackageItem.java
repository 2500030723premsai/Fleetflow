package fleetflow.model;

public class PackageItem {
    private final String id;
    private String shipmentId;
    private String address;       // destination address
    private String status;
    private String priority;
    private double weight;        // kg

    public PackageItem(String id, String shipmentId, String address, String status) {
        this(id, shipmentId, address, status, "NORMAL", 0.0);
    }

    public PackageItem(String id, String shipmentId, String address, String status, String priority) {
        this(id, shipmentId, address, status, priority, 0.0);
    }

    public PackageItem(String id, String shipmentId, String address, String status, String priority, double weight) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.address = address;
        this.status = status;
        this.priority = (priority == null || priority.isBlank()) ? "NORMAL" : priority.toUpperCase();
        this.weight = weight;
    }

    public String getId()                       { return id; }
    public String getShipmentId()               { return shipmentId; }
    public void   setShipmentId(String sid)     { this.shipmentId = sid; }
    public String getAddress()                  { return address; }
    public void   setAddress(String address)    { this.address = address; }
    public String getStatus()                   { return status; }
    public void   setStatus(String status)      { this.status = status; }
    public String getPriority()                 { return priority; }
    public void   setPriority(String priority)  { this.priority = (priority == null || priority.isBlank()) ? "NORMAL" : priority.toUpperCase(); }
    public double getWeight()                   { return weight; }
    public void   setWeight(double weight)      { this.weight = weight; }

    @Override
    public String toString() {
        return id + " | Shipment: " + shipmentId + " | " + address
                + " | Status: " + status
                + " | Priority: " + priority
                + " | Weight: " + weight + " kg";
    }
}
