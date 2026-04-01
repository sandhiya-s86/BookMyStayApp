import java.util.*;
import java.io.*;

// ===== ROOM =====
abstract class Room {
    String type;
    Room(String type) { this.type = type; }
}
class SingleRoom extends Room { SingleRoom(){ super("Single"); } }
class DoubleRoom extends Room { DoubleRoom(){ super("Double"); } }
class SuiteRoom extends Room { SuiteRoom(){ super("Suite"); } }

// ===== RESERVATION =====
class Reservation {
    String guest;
    int type;
    String id;

    Reservation(String g, int t) {
        guest = g;
        type = t;
    }
}

// ===== INVENTORY =====
class RoomInventory {
    int[] availability = {5,3,2};

    synchronized boolean allocate(int type) {
        if (availability[type] > 0) {
            availability[type]--;
            return true;
        }
        return false;
    }

    synchronized void release(int type) {
        availability[type]++;
    }

    void show() {
        System.out.println("\nCurrent Inventory:");
        System.out.println("Single: " + availability[0]);
        System.out.println("Double: " + availability[1]);
        System.out.println("Suite: " + availability[2]);
    }
}

// ===== UC12: FILE PERSISTENCE =====
class FilePersistenceService {

    public void saveInventory(RoomInventory inv, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            bw.write("Single=" + inv.availability[0]);
            bw.newLine();
            bw.write("Double=" + inv.availability[1]);
            bw.newLine();
            bw.write("Suite=" + inv.availability[2]);

            System.out.println("Inventory saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving inventory.");
        }
    }

    public void loadInventory(RoomInventory inv, String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split("=");

                if (parts[0].equals("Single"))
                    inv.availability[0] = Integer.parseInt(parts[1]);
                else if (parts[0].equals("Double"))
                    inv.availability[1] = Integer.parseInt(parts[1]);
                else if (parts[0].equals("Suite"))
                    inv.availability[2] = Integer.parseInt(parts[1]);
            }

            System.out.println("Inventory restored successfully.");

        } catch (Exception e) {
            System.out.println("Error loading inventory. Starting fresh.");
        }
    }
}

// ===== MAIN =====
public class BookMyStayApp {

    public static void main(String[] args) {

        String filePath = "inventory.txt";

        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistence = new FilePersistenceService();

        // ===== LOAD (UC12) =====
        System.out.println("===== System Recovery =====");
        persistence.loadInventory(inventory, filePath);

        inventory.show();

        // Simulate booking
        inventory.allocate(0);
        inventory.allocate(1);

        System.out.println("\nAfter Booking:");
        inventory.show();

        // ===== SAVE (UC12) =====
        persistence.saveInventory(inventory, filePath);

        System.out.println("\nApplication Ended.");
    }
}