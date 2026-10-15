package fleetflow.model;

import java.time.LocalDate;

public class Vehicle {
    private final String id;
    private String name;
    private String type;
    private final double capacity;
    private boolean available = true;
    private LocalDate lastServiceDate;
    private LocalDate nextServiceDate;

    public Vehicle(String id, String name, String type, double capacity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    public String getId()                        { return id; }
    public String getName()                      { return name; }
    public void   setName(String name)           { this.name = name; }
    public String getType()                      { return type; }
    public void   setType(String type)           { this.type = type; }
    public double getCapacity()                  { return capacity; }
    public boolean isAvailable()                 { return available; }
    public void   setAvailable(boolean value)    { this.available = value; }
    public LocalDate getLastServiceDate()        { return lastServiceDate; }
    public void   setLastServiceDate(LocalDate d){ this.lastServiceDate = d; }
    public LocalDate getNextServiceDate()        { return nextServiceDate; }
    public void   setNextServiceDate(LocalDate d){ this.nextServiceDate = d; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + type + " | " + capacity + " kg | "
                + (available ? "Available" : "Unavailable")
                + " | Last Service: " + (lastServiceDate != null ? lastServiceDate : "N/A")
                + " | Next Service: " + (nextServiceDate != null ? nextServiceDate : "N/A");
    }
}
