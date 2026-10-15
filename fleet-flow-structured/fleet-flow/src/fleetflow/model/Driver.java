package fleetflow.model;

public class Driver {
    private final String id;
    private String name;
    private String licenseNo;
    private boolean available = true;
    private String assignedVehicleId = "";

    public Driver(String id, String name, String licenseNo) {
        this.id = id;
        this.name = name;
        this.licenseNo = licenseNo;
    }

    public String getId()                             { return id; }
    public String getName()                           { return name; }
    public void   setName(String name)                { this.name = name; }
    public String getLicenseNo()                      { return licenseNo; }
    public void   setLicenseNo(String licenseNo)      { this.licenseNo = licenseNo; }
    public boolean isAvailable()                      { return available; }
    public void   setAvailable(boolean value)         { this.available = value; }
    public String getAssignedVehicleId()              { return assignedVehicleId; }
    public void   setAssignedVehicleId(String vid)    { this.assignedVehicleId = (vid == null ? "" : vid); }

    @Override
    public String toString() {
        return id + " | " + name + " | License: " + licenseNo
                + " | " + (available ? "Available" : "Unavailable")
                + " | Vehicle: " + (assignedVehicleId.isBlank() ? "None" : assignedVehicleId);
    }
}
