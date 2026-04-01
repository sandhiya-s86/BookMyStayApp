import java.util.ArrayList;

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
        System.out.println("   Hotel Booking Management System     ");
        System.out.println("             Version 1.0               ");
        System.out.println("=======================================");

        System.out.println("Application started successfully.");

        // ===== UC2 =====
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 0; // purposely 0 to test filtering

        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        System.out.println("\n===== Room Availability (UC2) =====");
        r1.displayDetails(singleAvailable);
        r2.displayDetails(doubleAvailable);
        r3.displayDetails(suiteAvailable);

        // ===== UC3 =====
        ArrayList<Room> inventory = new ArrayList<>();
        inventory.add(r1);
        inventory.add(r2);
        inventory.add(r3);

        int[] availability = {singleAvailable, doubleAvailable, suiteAvailable};

        System.out.println("\n===== Central Inventory (UC3) =====");
        for (int i = 0; i < inventory.size(); i++) {
            inventory.get(i).displayDetails(availability[i]);
        }

        // ===== UC4 =====
        System.out.println("\n===== Room Search (UC4) =====");

        // READ-ONLY SEARCH (no modification)
        for (int i = 0; i < inventory.size(); i++) {

            if (availability[i] > 0) { // filter available rooms only
                inventory.get(i).displayDetails(availability[i]);
            }
        }

        System.out.println("Search completed (No data modified).");
        System.out.println("Application Ended.");
    }
}