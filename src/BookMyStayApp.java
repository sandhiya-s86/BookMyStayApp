import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

// Abstract class
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

// Subclasses
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

// Main class
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

        // Queue for booking requests
        Queue<Integer> bookingQueue = new LinkedList<>();

        // Requests: 0=Single, 1=Double, 2=Suite
        bookingQueue.add(0);
        bookingQueue.add(1);
        bookingQueue.add(0);
        bookingQueue.add(2); // will fail (no suite)

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

        // Final availability
        System.out.println("\n===== Final Availability =====");
        for (int i = 0; i < inventory.size(); i++) {
            inventory.get(i).displayDetails(availability[i]);
        }

        System.out.println("Application Ended.");
    }
}