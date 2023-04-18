import java.time.LocalTime;

public class BusRide {
    private LocalTime arrival_time;
    private String bus_stop;
    private String trip_ID;
    private Integer route_ID;

    // primary constructor
    public BusRide(LocalTime arrival, String bus_stop, String trip, Integer route) {
        this.arrival_time = arrival;
        this.bus_stop = bus_stop;
        this.trip_ID = trip;
        this.route_ID = route;
    }

    // getters
    public LocalTime getArrivalTime() {
        return this.arrival_time;
    }
    public String getStop() {
        return this.bus_stop;
    }
    public String getTrip() {
        return this.trip_ID;
    }
    public Integer getRoute() {
        return this.route_ID;
    }

    // setters
    public void setArrivalTime(LocalTime time) {
        this.arrival_time = time;
    }
    public void setBusStop(String stop) {
        this.bus_stop = stop;
    }
    public void setTrip(String trip) {
        this.trip_ID = trip;
    }
    public void setRoute(Integer route) {
        this.route_ID = route;
    }

}
