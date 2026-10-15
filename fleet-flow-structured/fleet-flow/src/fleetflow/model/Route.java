package fleetflow.model;

public class Route {
    private final String id,name,source,destination;
    private final double distanceKm;
    public Route(String id,String name,String source,String destination,double distanceKm){this.id=id;this.name=name;this.source=source;this.destination=destination;this.distanceKm=distanceKm;}
    public String getId(){return id;} public String getName(){return name;} public String getSource(){return source;} public String getDestination(){return destination;} public double getDistanceKm(){return distanceKm;}
    @Override public String toString(){return id+" | "+source+" -> "+destination+" | "+distanceKm+" km";}
}
