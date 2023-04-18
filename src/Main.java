import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static HashMap<Integer, String> readStops(String filename) {
        BufferedReader reader;
        HashMap<Integer, String> stops = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            // skip first line
            String line = reader.readLine();
            line = reader.readLine();
            while (line != null) {
                String[] fields = line.split(",");
                Integer stop_ID = Integer.valueOf(fields[0]);
                String stop_name = fields[2];
                stops.put(stop_ID, stop_name);
                // read next line
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stops;
    }

    public static HashMap<String, Integer> readTrips(String filename) {
        BufferedReader reader;
        HashMap<String, Integer> trips = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            // skip first line
            String line = reader.readLine();
            line = reader.readLine();
            while (line != null) {
                String[] fields = line.split(",");
                String trip_ID = fields[2];
                Integer route_ID = Integer.valueOf(fields[0]);
                trips.put(trip_ID, route_ID);
                // read next line
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return trips;
    }

    public static Vector<Integer> readCalendar(String filename) {
        BufferedReader reader;
        Vector<Integer> days = new Vector<>();
        try {
            reader = new BufferedReader(new FileReader(filename));
            // skip first line
            String line = reader.readLine();
            line = reader.readLine();
            String[] fields = line.split(",");
            for (int i = 1; i < 8; i++) {
                Integer day = Integer.parseInt(fields[i]);
                days.add(day);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return days;
    }


    public static void main(String[] args) throws ParseException {

        if (args.length != 3) {
            System.out.print("ERROR: 3 arguments required!");
            System.exit(1);
        }
        if (Integer.parseInt(args[0]) < 2 || Integer.parseInt(args[0]) > 13) {
            System.out.print("ERROR: incorrect stop ID!");
            System.exit(1);
        }
        if (!Objects.equals(args[2], "relative") && !Objects.equals(args[2], "absolute")) {
            System.out.print("ERROR: time type (3. argument) has to either be 'relative' or 'absolute'!");
            System.exit(1);
        }

        // if all arguments are properly provided
        // parse input arguments
        int stop_ID = Integer.parseInt(args[0]);
        int num = Integer.parseInt(args[1]);
        String time_type = args[2];

        // current device time
        LocalTime current_time = LocalTime.now();
        //LocalTime current_time = LocalTime.parse("12:00:00");
        System.out.println("current time: " + current_time);

        /*String date = "04/18/2023";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date d = format.parse(date);
        DateFormat format2 = new SimpleDateFormat("EEEE");
        String day = format2.format(d);
        System.out.println(day);*/

        Calendar cal = Calendar.getInstance();
        int weekday = cal.get(Calendar.DAY_OF_WEEK);
        //System.out.println(weekday);


        // get bus stops
        HashMap<Integer, String> stops = readStops("stops.txt");
        // get trips
        HashMap<String, Integer> trips = readTrips("trips.txt");
        // get days
        Vector<Integer> days = readCalendar("calendar.txt");
        //System.out.println("day: " + days);

        int counter = 0;

        Vector<BusRide> buses = new Vector<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("stop_times.txt"));
            // skip first line
            String line = reader.readLine();
            line = reader.readLine();

            String stop_name = stops.get(stop_ID);
            System.out.println("\n" + stop_name);

            Vector<Integer> routes = new Vector<>();

            while (line != null) {
                String[] fields = line.split(",");
                String trip_ID = fields[0];
                LocalTime arrival_time = LocalTime.parse(fields[1]);
                int id = Integer.parseInt(fields[3]);

                boolean isBefore = arrival_time.isBefore(current_time);
                boolean isAfter = arrival_time.isAfter(current_time.plusHours(2));

                // check time range, stop ID, num of arrivals & day of the week
                if (!isBefore && !isAfter && id == stop_ID && counter < num && days.get(weekday-2) == 1) {
                    for (String trip : trips.keySet()) {
                        if (Objects.equals(trip_ID, trip)) {
                            Integer route_ID = trips.get(trip_ID);
                            routes.add(route_ID);

                            // create new bus ride
                            BusRide busRide = new BusRide(arrival_time, stop_name, trip_ID, route_ID);
                            buses.add(busRide);

                            break;
                        }
                    }
                    counter++;
                }
                // go to next line
                line = reader.readLine();
            }

            // group bus rides by route IDs
            Map<Integer, List<BusRide>> grouped = buses.stream().collect(Collectors.groupingBy(BusRide::getRoute));

            // sort arrivals
            for (Map.Entry<Integer, List<BusRide>> entry : grouped.entrySet()) {
                Comparator<BusRide> comparator = Comparator.comparing(BusRide::getArrivalTime);
                entry.getValue().sort(comparator);
            }

            for (Map.Entry<Integer, List<BusRide>> entry : grouped.entrySet()) {
                Integer route_ID = entry.getKey();
                List<BusRide> bus_rides = entry.getValue();
                System.out.print(route_ID + ": ");
                for (BusRide ride : bus_rides) {
                    if (Objects.equals(time_type, "relative")) {
                        // get minutes (relative time)
                        Duration duration = Duration.between(current_time, ride.getArrivalTime());
                        long minutes = duration.toMinutes();
                        if (bus_rides.indexOf(ride) == bus_rides.size() - 1) {
                            System.out.print(minutes + "min");
                        } else {
                            System.out.print(minutes + "min, ");
                        }
                    } else {
                        if (bus_rides.indexOf(ride) == bus_rides.size() - 1) {
                            System.out.print(ride.getArrivalTime());
                        } else {
                            System.out.print(ride.getArrivalTime() + ", ");
                        }
                    }
                }
                System.out.println();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);
    }
}