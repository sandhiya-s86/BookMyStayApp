import java.util.*;

// ===== Abstract class =====
abstract class Room {
    String type;
    int beds;
    double price;

    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    void displayDetails(int availability) {
        System.out.println("Room Type  : " + type);
        System.out.println("Beds       : " + beds);
        System.out.println("Price      : " + price);
        System.out.println("Available  : " + availability);
        System.out.println("-----------------------------");
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

// ===== UC9: Custom Exception =====
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// ===== UC6: Reservation =====
class Reservation {
    String guestName;
    int roomType;
    String roomId;

    Reservation(String guestName, int roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// ===== UC9: Validator =====
class ReservationValidator {

    public void validate(String guestName, int roomType, int[] availability)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        if (roomType < 0 || roomType > 2) {
            throw new InvalidBookingException("Invalid room type selected");
        }

        if (availability[roomType] <= 0) {
            throw new InvalidBookingException("No rooms available for selected type");
        }
    }
}

// ===== UC6: Allocation =====
class RoomAllocationService {

    private Map<String, Integer> counters = new HashMap<>();

    public RoomAllocationService() {
        counters.put("Single Room", 0);
        counters.put("Double Room", 0);
        counters.put("Suite Room", 0);
    }

    public String allocateRoom(Reservation res, ArrayList<Room> inventory, int[] availability) {

        Room room = inventory.get(res.roomType);

        String roomId = generateRoomId(room.type);
        availability[res.roomType]--;

        res.roomId = roomId;

        System.out.println("Booking CONFIRMED | Room ID: " + roomId);
        return roomId;
    }

    private String generateRoomId(String type) {
        int count = counters.get(type) + 1;
        counters.put(type, count);
        return type.split(" ")[0] + "-" + count;
    }
}

// ===== UC7: Service =====
class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public double getCost() { return cost; }
}

// ===== UC7: Add-On =====
class AddOnServiceManager {

    private Map<String, List<Service>> servicesByReservation = new HashMap<>();

    public void addService(String reservationId, Service service) {
        servicesByReservation.putIfAbsent(reservationId, new ArrayList<>());
        servicesByReservation.get(reservationId).add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {
        double total = 0;
        List<Service> services = servicesByReservation.get(reservationId);

        if (services != null) {
            for (Service s : services) {
                total += s.getCost();
            }
        }
        return total;
    }
}

// ===== UC8: Booking History =====
class BookingHistory {
    private List<Reservation> confirmedReservations = new ArrayList<>();

    public void addReservation(Reservation r) {
        confirmedReservations.add(r);
    }

    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

// ===== UC8: Report =====
class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("\n===== Booking History Report =====");

        for (Reservation r : history.getConfirmedReservations()) {
            System.out.println("Guest: " + r.guestName +
                    " | Room ID: " + r.roomId);
        }
    }
}

// ===== MAIN =====
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App =====");

        int[] availability = {2, 1, 0};

        ArrayList<Room> inventory = new ArrayList<>();
        inventory.add(new SingleRoom());
        inventory.add(new DoubleRoom());
        inventory.add(new SuiteRoom());

        ReservationValidator validator = new ReservationValidator();
        RoomAllocationService allocator = new RoomAllocationService();
        AddOnServiceManager addOnManager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();

        try {
            // VALID booking
            Reservation r1 = new Reservation("Abhi", 0);
            validator.validate(r1.guestName, r1.roomType, availability);

            String id1 = allocator.allocateRoom(r1, inventory, availability);
            history.addReservation(r1);

            addOnManager.addService(id1, new Service("Breakfast", 500));
            addOnManager.addService(id1, new Service("Spa", 1000));

            System.out.println("Total Add-on Cost: " +
                    addOnManager.calculateTotalServiceCost(id1));

            // INVALID booking (will trigger error)
            Reservation r2 = new Reservation("", 5);
            validator.validate(r2.guestName, r2.roomType, availability);

        } catch (InvalidBookingException e) {
            System.out.println("Booking FAILED: " + e.getMessage());
        }

        // UC8 Report
        BookingReportService report = new BookingReportService();
        report.generateReport(history);

        System.out.println("\nApplication Ended.");
    }
}