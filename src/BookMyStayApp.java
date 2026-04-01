import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

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
    SingleRoom() {
        super("Single Room", 1, 1000);
    }
}

class DoubleRoom extends Room {
    DoubleRoom() {
        super("Double Room", 2, 2000);
    }
}

class SuiteRoom extends Room {
    SuiteRoom() {
        super("Suite Room", 3, 5000);
    }
}

// ===== UC6: Reservation Class =====
class Reservation {
    String guestName;
    int roomType; // 0=Single, 1=Double, 2=Suite

    Reservation(String guestName, int roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// ===== UC6: Room Allocation Service =====
class RoomAllocationService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> assignedRoomsByType = new HashMap<>();
    private Map<String, Integer> counters = new HashMap<>();

    public RoomAllocationService() {
        counters.put("Single Room", 0);
        counters.put("Double Room", 0);
        counters.put("Suite Room", 0);
    }

    public void allocateRoom(Reservation reservation, ArrayList<Room> inventory, int[] availability) {

        Room room = inventory.get(reservation.roomType);

        System.out.println("\nProcessing reservation for: " + reservation.guestName +
                " (" + room.type + ")");

        if (availability[reservation.roomType] <= 0) {
            System.out.println("Booking FAILED - No rooms available");
            return;
        }

        // Generate unique room ID
        String roomId = generateRoomId(room.type);

        // Store unique IDs
        allocatedRoomIds.add(roomId);

        // Map room type → allocated IDs
        assignedRoomsByType.putIfAbsent(room.type, new HashSet<>());
        assignedRoomsByType.get(room.type).add(roomId);

        // Update inventory immediately
        availability[reservation.roomType]--;

        System.out.println("Booking CONFIRMED for " + reservation.guestName +
                " | Room ID: " + roomId);
    }

    private String generateRoomId(String roomType) {
        int count = counters.get(roomType) + 1;
        counters.put(roomType, count);

        return roomType.split(" ")[0] + "-" + count;
    }
}

// ===== MAIN CLASS =====
public class BookMyStayApp {

    public static void main(String[] args) {

        // ===== UC1 =====
        System.out.println("=======================================");
        System.out.println("      Welcome to Book My Stay App      ");
        System.out.println("=======================================");

        // ===== UC2 =====
        int singleAvailable = 2;
        int doubleAvailable = 1;
        int suiteAvailable = 0;

        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        // ===== UC3 =====
        ArrayList<Room> inventory = new ArrayList<>();
        inventory.add(r1);
        inventory.add(r2);
        inventory.add(r3);

        int[] availability = {singleAvailable, doubleAvailable, suiteAvailable};

        // ===== UC4 =====
        System.out.println("\nAvailable Rooms:");
        for (int i = 0; i < inventory.size(); i++) {
            if (availability[i] > 0) {
                inventory.get(i).displayDetails(availability[i]);
            }
        }

        // ===== UC5 =====
        System.out.println("\n===== Booking Requests (UC5 - FIFO) =====");

        Queue<Integer> bookingQueue = new LinkedList<>();

        bookingQueue.add(0);
        bookingQueue.add(1);
        bookingQueue.add(0);
        bookingQueue.add(2); // will fail

        while (!bookingQueue.isEmpty()) {
            int request = bookingQueue.poll();

            System.out.println("\nProcessing request for: " + inventory.get(request).type);

            if (availability[request] > 0) {
                availability[request]--;
                System.out.println("Booking SUCCESSFUL");
            } else {
                System.out.println("Booking FAILED (No rooms available)");
            }
        }

        // ===== UC6 =====
        System.out.println("\n===== UC6: Reservation Confirmation & Room Allocation =====");

        Queue<Reservation> reservationQueue = new LinkedList<>();

        reservationQueue.add(new Reservation("Abhi", 0));
        reservationQueue.add(new Reservation("Subha", 0));
        reservationQueue.add(new Reservation("Vanmathi", 2)); // fail

        RoomAllocationService service = new RoomAllocationService();

        while (!reservationQueue.isEmpty()) {
            Reservation res = reservationQueue.poll();
            service.allocateRoom(res, inventory, availability);
        }

        // ===== Final Availability =====
        System.out.println("\n===== Final Availability =====");
        for (int i = 0; i < inventory.size(); i++) {
            inventory.get(i).displayDetails(availability[i]);
        }

        System.out.println("Application Ended.");
    }
}