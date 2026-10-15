package fleetflow.model;

public class Warehouse {
    private final String id;
    private String name;
    private String location;
    private int capacity;        // total storage units
    private int usedCapacity;    // currently used units

    public Warehouse(String id, String name, String location) {
        this(id, name, location, 0, 0);
    }

    public Warehouse(String id, String name, String location, int capacity, int usedCapacity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.usedCapacity = usedCapacity;
    }

    public String getId()                          { return id; }
    public String getName()                        { return name; }
    public void   setName(String name)             { this.name = name; }
    public String getLocation()                    { return location; }
    public void   setLocation(String location)     { this.location = location; }
    public int    getCapacity()                    { return capacity; }
    public void   setCapacity(int capacity)        { this.capacity = capacity; }
    public int    getUsedCapacity()                { return usedCapacity; }
    public void   setUsedCapacity(int used)        { this.usedCapacity = used; }
    public int    getRemainingCapacity()           { return capacity - usedCapacity; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + location
                + " | Capacity: " + capacity
                + " | Used: " + usedCapacity
                + " | Remaining: " + getRemainingCapacity();
    }
}
