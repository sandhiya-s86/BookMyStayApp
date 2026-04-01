/**
 * Combined Use Case 1 + Use Case 2
 * Book My Stay Application
 */

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

        // ===== Use Case 1 =====
        System.out.println("=======================================");
        System.out.println("      Welcome to Book My Stay App      ");
        System.out.println("   Hotel Booking Management System     ");
        System.out.println("             Version 1.0               ");
        System.out.println("=======================================");

        System.out.println("Application started successfully.");
        System.out.println("Thank you for using Book My Stay.");

        // ===== Use Case 2 =====
        System.out.println("\n===== Room Availability =====");

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        r1.displayDetails(singleAvailable);
        r2.displayDetails(doubleAvailable);
        r3.displayDetails(suiteAvailable);

        System.out.println("Application Ended.");
    }
}