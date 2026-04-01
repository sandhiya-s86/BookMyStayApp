import java.util.*;

// ===== Abstract Room =====
abstract class Room {
    String type;
    int beds;
    double price;

    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }
}

// ===== Room Types =====
class SingleRoom extends Room {
    SingleRoom() { super("Single Room", 1, 1000); }
}
class DoubleRoom extends Room {
    DoubleRoom() { super("Double Room", 2, 2000); }
}
class SuiteRoom extends Room {
    SuiteRoom() { super("Suite Room", 3, 5000); }
}

// ===== UC9 Exception =====
class InvalidBookingException extends Exception {
    public InvalidBookingException(String msg) { super(msg); }
}

// ===== Reservation =====
class Reservation {
    String guestName;
    int roomType;
    String roomId;

    Reservation(String name, int type) {
        guestName = name;
        roomType = type;
    }
}

// ===== Validator =====
class ReservationValidator {
    public void validate(String name, int type, int[] availability)
            throws InvalidBookingException {

        if (name == null || name.isEmpty())
            throw new InvalidBookingException("Invalid guest name");

        if (type < 0 || type > 2)
            throw new InvalidBookingException("Invalid room type");

        if (availability[type] <= 0)
            throw new InvalidBookingException("Room not available");
    }
}

// ===== Allocation =====
class RoomAllocationService {
    private Map<String, Integer> counter = new HashMap<>();

    public RoomAllocationService() {
        counter.put("Single Room", 0);
        counter.put("Double Room", 0);
        counter.put("Suite Room", 0);
    }

    public String allocate(Reservation r, ArrayList<Room> rooms, int[] avail) {
        String type = rooms.get(r.roomType).type;

        int c = counter.get(type) + 1;
        counter.put(type, c);

        String id = type.split(" ")[0] + "-" + c;

        avail[r.roomType]--;
        r.roomId = id;

        System.out.println("Allocated Room ID: " + id);
        return id;
    }
}

// ===== UC7 Service =====
class Service {
    String name;
    double cost;

    Service(String n, double c) {
        name = n;
        cost = c;
    }
}

// ===== Add-On Manager =====
class AddOnServiceManager {
    Map<String, List<Service>> map = new HashMap<>();

    void addService(String id, Service s) {
        map.putIfAbsent(id, new ArrayList<>());
        map.get(id).add(s);
    }

    double totalCost(String id) {
        double total = 0;
        if (map.containsKey(id)) {
            for (Service s : map.get(id))
                total += s.cost;
        }
        return total;
    }
}

// ===== UC8 History =====
class BookingHistory {
    List<Reservation> list = new ArrayList<>();

    void add(Reservation r) { list.add(r); }

    List<Reservation> getAll() { return list; }
}

// ===== Report =====
class BookingReportService {
    void report(BookingHistory h) {
        System.out.println("\nBooking Report:");
        for (Reservation r : h.getAll()) {
            System.out.println(r.guestName + " -> " + r.roomId);
        }
    }
}

// ===== UC10 Cancellation =====
class CancellationService {

    private Map<String, Integer> reservationMap = new HashMap<>();
    private Stack<String> rollbackStack = new Stack<>();

    public void register(String id, int roomType) {
        reservationMap.put(id, roomType);
    }

    public void cancel(String id, int[] availability) {

        if (!reservationMap.containsKey(id)) {
            System.out.println("Invalid cancellation attempt");
            return;
        }

        int type = reservationMap.get(id);

        availability[type]++; // restore inventory
        rollbackStack.push(id);

        reservationMap.remove(id);

        System.out.println("Booking cancelled for ID: " + id);
    }

    public void showRollback() {
        System.out.println("\nRollback History:");
        while (!rollbackStack.isEmpty()) {
            System.out.println("Released ID: " + rollbackStack.pop());
        }
    }
}

// ===== MAIN =====
public class BookMyStayApp {

    public static void main(String[] args) {

        int[] availability = {2, 1, 0};

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        ReservationValidator validator = new ReservationValidator();
        RoomAllocationService allocator = new RoomAllocationService();
        AddOnServiceManager addOn = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService report = new BookingReportService();
        CancellationService cancelService = new CancellationService();

        try {
            Reservation r1 = new Reservation("Abhi", 0);
            validator.validate(r1.guestName, r1.roomType, availability);

            String id1 = allocator.allocate(r1, rooms, availability);

            history.add(r1);
            cancelService.register(id1, r1.roomType);

            addOn.addService(id1, new Service("Breakfast", 500));
            addOn.addService(id1, new Service("Spa", 1000));

            System.out.println("Add-on Cost: " + addOn.totalCost(id1));

            // Cancel booking
            cancelService.cancel(id1, availability);

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Report
        report.report(history);

        // Rollback history
        cancelService.showRollback();

        System.out.println("\nFinal Availability: " + availability[0]);
    }
}