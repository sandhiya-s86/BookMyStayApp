import java.util.*;

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
    int[] availability = {2,2,1};

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
        System.out.println("\nRemaining Inventory:");
        System.out.println("Single: " + availability[0]);
        System.out.println("Double: " + availability[1]);
        System.out.println("Suite: " + availability[2]);
    }
}

// ===== ALLOCATION =====
class RoomAllocationService {
    Map<String,Integer> count = new HashMap<>();

    RoomAllocationService(){
        count.put("Single",0);
        count.put("Double",0);
        count.put("Suite",0);
    }

    synchronized String allocateRoom(Reservation r, RoomInventory inv) {
        String type = getType(r.type);

        if (!inv.allocate(r.type)) {
            System.out.println("No room available for " + r.guest);
            return null;
        }

        int c = count.get(type)+1;
        count.put(type,c);

        String id = type+"-"+c;
        r.id = id;

        System.out.println("Booking confirmed for " + r.guest + ", Room ID: " + id);
        return id;
    }

    String getType(int t){
        if(t==0) return "Single";
        if(t==1) return "Double";
        return "Suite";
    }
}

// ===== BOOKING QUEUE =====
class BookingRequestQueue {
    Queue<Reservation> queue = new LinkedList<>();

    synchronized void add(Reservation r) {
        queue.add(r);
    }

    synchronized Reservation get() {
        return queue.poll();
    }
}

// ===== UC11 THREAD CLASS =====
class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;
    private RoomAllocationService service;

    public ConcurrentBookingProcessor(
            BookingRequestQueue q,
            RoomInventory i,
            RoomAllocationService s) {

        queue = q;
        inventory = i;
        service = s;
    }

    @Override
    public void run() {
        while (true) {

            Reservation r;

            // 🔐 synchronized queue access
            synchronized (queue) {
                r = queue.get();
            }

            if (r == null) break;

            // 🔐 critical section
            synchronized (inventory) {
                service.allocateRoom(r, inventory);
            }
        }
    }
}

// ===== MAIN =====
public class BookMyStayApp {

    public static void main(String[] args) {

        BookingRequestQueue queue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService service = new RoomAllocationService();

        // Multiple booking requests
        queue.add(new Reservation("Abhi",0));
        queue.add(new Reservation("Vanmathi",1));
        queue.add(new Reservation("Kural",2));
        queue.add(new Reservation("Subha",0));

        // Threads
        Thread t1 = new Thread(new ConcurrentBookingProcessor(queue, inventory, service));
        Thread t2 = new Thread(new ConcurrentBookingProcessor(queue, inventory, service));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch(Exception e){}

        inventory.show();
    }
}