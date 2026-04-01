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

// ===== Subclasses =====
class SingleRoom extends Room {
    SingleRoom() { super("Single Room", 1, 1000); }
}

class DoubleRoom extends Room {
    DoubleRoom() { super("Double Room", 2, 2000); }
}

class SuiteRoom extends Room {
    SuiteRoom() { super("Suite Room", 3, 5000); }
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

// ===== UC6: Room Allocation =====
class RoomAllocationService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Integer> counters = new HashMap<>();

    public RoomAllocationService() {
        counters.put("Single Room", 0);
        counters.put("Double Room", 0);
        counters.put("Suite Room", 0);
    }

    public String allocateRoom(Reservation res, ArrayList<Room> inventory, int[] availability) {

        Room room = inventory.get(res.roomType);

        System.out.println("\nProcessing reservation for: " + res.guestName);

        if (availability[res.roomType] <= 0) {
            System.out.println("Booking FAILED");
            return null;
        }

        String roomId = generateRoomId(room.type);

        allocatedRoomIds.add(roomId);
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

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
}

// ===== UC7: Add-On Manager =====
class AddOnServiceManager {

    private Map<String, List<Service>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

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

// ===== MAIN =====
public class BookMyStayApp {

    public static void main(String[] args) {

        // ===== UC1 =====
        System.out.println("=======================================");
        System.out.println("      Welcome to Book My Stay App      ");
        System.out.println("=======================================");

        // ===== UC2 =====
        int[] availability = {2, 1, 0};

        // ===== UC3 =====
        ArrayList<Room> inventory = new ArrayList<>();
        inventory.add(new SingleRoom());
        inventory.add(new DoubleRoom());
        inventory.add(new SuiteRoom());

        // ===== UC4 =====
        System.out.println("\nAvailable Rooms:");
        for (int i = 0; i < inventory.size(); i++) {
            if (availability[i] > 0) {
                inventory.get(i).displayDetails(availability[i]);
            }
        }

        // ===== UC5 =====
        System.out.println("\n===== UC5 FIFO Booking =====");

        Queue<Integer> queue = new LinkedList<>();
        queue.add(0);
        queue.add(1);
        queue.add(2);

        while (!queue.isEmpty()) {
            int req = queue.poll();
            if (availability[req] > 0) {
                availability[req]--;
                System.out.println("Booking Success");
            } else {
                System.out.println("Booking Failed");
            }
        }

        // ===== UC6 =====
        System.out.println("\n===== UC6 Room Allocation =====");

        RoomAllocationService allocationService = new RoomAllocationService();

        Reservation r1 = new Reservation("Abhi", 0);
        Reservation r2 = new Reservation("Subha", 0);

        String id1 = allocationService.allocateRoom(r1, inventory, availability);
        String id2 = allocationService.allocateRoom(r2, inventory, availability);

        // ===== UC7 =====
        System.out.println("\n===== UC7 Add-On Services =====");

        AddOnServiceManager manager = new AddOnServiceManager();

        Service s1 = new Service("Breakfast", 500);
        Service s2 = new Service("Spa", 1000);

        manager.addService(id1, s1);
        manager.addService(id1, s2);

        double totalCost = manager.calculateTotalServiceCost(id1);

        System.out.println("Reservation ID: " + id1);
        System.out.println("Total Add-On Cost: " + totalCost);

        System.out.println("\nApplication Ended.");
    }
}