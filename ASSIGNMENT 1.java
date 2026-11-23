public class ParkingFacility {
    // slots[floor][slot] holds a vehicle number, or null if the slot is free
    private final String[][] slots;

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    /** Returns {floor, slot} of the first free slot, or null if it is full. */
    public int[] allocate(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) {
            return null;
        }
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicleNo;
                    return new int[]{f, s};
                }
            }
        }
        return null;
    }

    /** Frees a slot. Returns false if the position is invalid or already free. */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) {
            return false;
        }
        if (slot < 0 || slot >= slots[floor].length) {
            return false;
        }
        if (slots[floor][slot] == null) {
            return false;
        }
        slots[floor][slot] = null;
        return true;
    }

    public static void main(String[] args) {
        // floor 0 has 2 slots, floor 1 has 1 slot
        ParkingFacility p = new ParkingFacility(new int[]{2, 1});
        System.out.println(java.util.Arrays.toString(p.allocate("TN01AB1111")));
        System.out.println(java.util.Arrays.toString(p.allocate("TN02CD2222")));
        System.out.println(java.util.Arrays.toString(p.allocate("TN03EF3333")));
        System.out.println(java.util.Arrays.toString(p.allocate("TN04GH4444")));
        System.out.println(p.release(0, 1) + " " + p.release(0, 1) + " " + p.release(5, 0));
        System.out.println(java.util.Arrays.toString(p.allocate("TN04GH4444")));
    }
}
```[cite: 4]

---

### Task 8: `FeeDemo` (`Complete the Code`)[cite: 8]

```java
interface Chargeable {
    double chargingFee(int kWh);
}

abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    protected abstract double ratePerHour();

    /** Every started hour is charged in full. */
    public double calculateFee(int minutes) {
        if (minutes <= 0) {
            return 0.0;
        }
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 10;
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
    }

    @Override
    protected double ratePerHour() {
        return 30;
    }
}

class ElectricCar extends Car implements Chargeable {
    ElectricCar(String regNo) {
        super(regNo);
    }

    @Override
    public double chargingFee(int kWh) {
        return kWh * 12.0;
    }
}

public class FeeDemo {
    public static void main(String[] args) {
        Vehicle[] parked = {
            new TwoWheeler("TN01A1"),
            new Car("TN02B2"),
            new ElectricCar("TN03C3")
        };
        for (Vehicle v : parked) {
            double fee = v.calculateFee(130); // dynamic method dispatch
            if (v instanceof Chargeable c) {
                fee += c.chargingFee(10);
            }
            System.out.println(v.getClass().getSimpleName() + " " + v.getRegNo() + " -> Rs " + fee);
        }
        System.out.println(new Car("TN02B2").equals(new ElectricCar("TN02B2")));
    }
}
```[cite: 8]

---

### Task 11: Complete Campus Smart-Parking System Implementation[cite: 10]

```java
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

enum UserCategory {
    STUDENT, STAFF, VISITOR
}

enum VehicleCategory {
    TWO_WHEELER, CAR, ELECTRIC_VEHICLE
}

interface Payable {
    double computeFee(int durationMinutes, int chargingKWh);
}

abstract class ParkingVehicle implements Payable {
    private final String regNo;
    private final VehicleCategory category;
    private final UserCategory userCategory;
    private final boolean hasMonthlyPass;

    protected ParkingVehicle(String regNo, VehicleCategory category, UserCategory userCategory, boolean hasMonthlyPass) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("Registration number cannot be empty");
        }
        this.regNo = regNo.trim().toUpperCase();
        this.category = category;
        this.userCategory = userCategory;
        this.hasMonthlyPass = (userCategory == UserCategory.VISITOR) ? false : hasMonthlyPass;
    }

    public String getRegNo() {
        return regNo;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    public UserCategory getUserCategory() {
        return userCategory;
    }

    public boolean hasMonthlyPass() {
        return hasMonthlyPass;
    }

    protected abstract double hourlyRate();

    protected abstract double dailyMaxCap();

    @Override
    public double computeFee(int durationMinutes, int chargingKWh) {
        if (durationMinutes < 0 || chargingKWh < 0) {
            throw new IllegalArgumentException("Duration and charging units cannot be negative");
        }
        if (userCategory != UserCategory.VISITOR && hasMonthlyPass) {
            return 0.0;
        }
        if (durationMinutes == 0) {
            return 0.0;
        }
        int billableHours = (durationMinutes + 59) / 60;
        double parkingFee = Math.min(billableHours * hourlyRate(), dailyMaxCap());
        return parkingFee + extraCharge(chargingKWh);
    }

    protected double extraCharge(int chargingKWh) {
        return 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingVehicle other)) return false;
        return regNo.equals(other.regNo);
    }

    @Override
    public int hashCode() {
        return regNo.hashCode();
    }
}

class BikeVehicle extends ParkingVehicle {
    public BikeVehicle(String regNo, UserCategory userCategory, boolean hasMonthlyPass) {
        super(regNo, VehicleCategory.TWO_WHEELER, userCategory, hasMonthlyPass);
    }

    @Override
    protected double hourlyRate() {
        return 10.0;
    }

    @Override
    protected double dailyMaxCap() {
        return 80.0;
    }
}

class CarVehicle extends ParkingVehicle {
    public CarVehicle(String regNo, UserCategory userCategory, boolean hasMonthlyPass) {
        super(regNo, VehicleCategory.CAR, userCategory, hasMonthlyPass);
    }

    @Override
    protected double hourlyRate() {
        return 30.0;
    }

    @Override
    protected double dailyMaxCap() {
        return 240.0;
    }
}

class EvVehicle extends ParkingVehicle {
    private final boolean usesChargingSlot;

    public EvVehicle(String regNo, UserCategory userCategory, boolean hasMonthlyPass, boolean usesChargingSlot) {
        super(regNo, VehicleCategory.ELECTRIC_VEHICLE, userCategory, hasMonthlyPass);
        this.usesChargingSlot = usesChargingSlot;
    }

    public boolean isUsesChargingSlot() {
        return usesChargingSlot;
    }

    @Override
    protected double hourlyRate() {
        return 25.0;
    }

    @Override
    protected double dailyMaxCap() {
        return 200.0;
    }

    @Override
    protected double extraCharge(int chargingKWh) {
        return usesChargingSlot ? chargingKWh * 12.0 : 0.0;
    }
}

class ParkingTicket {
    private static int ticketSequence = 1000;
    private final String ticketId;
    private final ParkingVehicle vehicle;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;

    public ParkingTicket(ParkingVehicle vehicle, int floor, int slot, LocalDateTime entryTime) {
        this.ticketId = "TKT-" + (++ticketSequence);
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    public String getTicketId() {
        return ticketId;
    }

    public ParkingVehicle getVehicle() {
        return vehicle;
    }

    public int getFloor() {
        return floor;
    }

    public int getSlot() {
        return slot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}

class SmartParkingSystem {
    private final ParkingTicket[][] grid;
    private final Map<String, ParkingTicket> activeByRegNo = new HashMap<>();
    private final Set<String> issuedTicketIds = new HashSet<>();
    private double totalCollection = 0.0;

    public SmartParkingSystem(int[] slotsPerFloor) {
        this.grid = new ParkingTicket[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.grid[f] = new ParkingTicket[slotsPerFloor[f]];
        }
    }

    public ParkingTicket enter(ParkingVehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (activeByRegNo.containsKey(vehicle.getRegNo())) {
            throw new IllegalStateException("Duplicate registration number already parked: " + vehicle.getRegNo());
        }
        for (int f = 0; f < grid.length; f++) {
            for (int s = 0; s < grid[f].length; s++) {
                if (grid[f][s] == null) {
                    ParkingTicket ticket = new ParkingTicket(vehicle, f, s, entryTime);
                    grid[f][s] = ticket;
                    activeByRegNo.put(vehicle.getRegNo(), ticket);
                    issuedTicketIds.add(ticket.getTicketId());
                    return ticket;
                }
            }
        }
        throw new IllegalStateException("Parking facility is full");
    }

    public double exit(String regNo, LocalDateTime exitTime, int chargingKWh) {
        if (regNo == null) {
            throw new IllegalArgumentException("Invalid registration number");
        }
        String key = regNo.trim().toUpperCase();
        ParkingTicket ticket = activeByRegNo.remove(key);
        if (ticket == null) {
            throw new IllegalArgumentException("Vehicle not currently parked: " + key);
        }
        int f = ticket.getFloor();
        int s = ticket.getSlot();
        grid[f][s] = null;

        long mins = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        int durationMinutes = (int) Math.max(0, mins);
        double fee = ticket.getVehicle().computeFee(durationMinutes, chargingKWh);
        totalCollection += fee;
        return fee;
    }

    public double getTotalCollection() {
        return totalCollection;
    }
}

public class CampusParkingApp {
    public static void main(String[] args) {
        SmartParkingSystem system = new SmartParkingSystem(new int[]{2, 1}); // 3 slots total
        LocalDateTime base = LocalDateTime.of(2026, 11, 23, 9, 0);

        ParkingTicket t1 = system.enter(new BikeVehicle("TN01BK101", UserCategory.VISITOR, false), base);
        ParkingTicket t2 = system.enter(new CarVehicle("TN02CR202", UserCategory.STAFF, true), base.plusMinutes(10));
        ParkingTicket t3 = system.enter(new EvVehicle("TN03EV303", UserCategory.VISITOR, false, true), base.plusMinutes(20));

        System.out.println("Issued: " + t1.getTicketId() + ", " + t2.getTicketId() + ", " + t3.getTicketId());

        // Boundary & normal exits
        double f1 = system.exit("TN01BK101", base.plusMinutes(0), 0);     // 0 mins -> Rs 0.0
        double f2 = system.exit("TN02CR202", base.plusMinutes(130), 0);   // Staff pass -> Rs 0.0
        double f3 = system.exit("TN03EV303", base.plusMinutes(80), 5);    // 2 hrs * 25 + 5 * 12 = Rs 110.0

        System.out.println("Fees -> TN01BK101: " + f1 + ", TN02CR202: " + f2 + ", TN03EV303: " + f3);
        System.out.println("Total Collection: Rs " + system.getTotalCollection());

        // Invalid cases
        try {
            system.enter(new CarVehicle("TN02CR202", UserCategory.VISITOR, false), base);
        } catch (Exception e) {
            System.out.println("Expected duplicate error: " + e.getMessage());
        }

        try {
            system.exit("INVALID99", base.plusHours(1), 0);
        } catch (Exception e) {
            System.out.println("Expected invalid exit error: " + e.getMessage());
        }
    }
}
```[cite: 10]

---

### Task 12: Day-4 Design Challenge (E-Bikes, Delivery Vans, One-Day Pass & 5-Floor Jagged Layout)[cite: 11]

```java
import java.time.LocalTime;
import java.util.Arrays;

interface Pass {
    boolean coversFullDay();
    String passName();
}

class OneDayVisitorPass implements Pass {
    private final double flatPassFee;

    public OneDayVisitorPass(double flatPassFee) {
        this.flatPassFee = flatPassFee;
    }

    @Override
    public boolean coversFullDay() {
        return true;
    }

    @Override
    public String passName() {
        return "ONE_DAY_PASS";
    }

    public double getFlatPassFee() {
        return flatPassFee;
    }
}

@FunctionalInterface
interface PricingRule {
    double calculate(int minutes, LocalTime entryTime, Pass pass);
}

abstract class ExtendedVehicle {
    private final String regNo;
    private final PricingRule pricingRule;

    protected ExtendedVehicle(String regNo, PricingRule pricingRule) {
        this.regNo = regNo;
        this.pricingRule = pricingRule;
    }

    public String getRegNo() {
        return regNo;
    }

    public double computeCharge(int minutes, LocalTime entryTime, Pass pass) {
        return pricingRule.calculate(minutes, entryTime, pass);
    }
}

class EBike extends ExtendedVehicle {
    public EBike(String regNo, double flatFee) {
        super(regNo, (mins, time, pass) -> {
            if (pass != null && pass.coversFullDay()) {
                return ((OneDayVisitorPass) pass).getFlatPassFee();
            }
            return mins <= 0 ? 0.0 : flatFee;
        });
    }
}

class DeliveryVan extends ExtendedVehicle {
    private static final LocalTime PEAK_START = LocalTime.of(9, 0);
    private static final LocalTime PEAK_END = LocalTime.of(17, 0);

    public DeliveryVan(String regNo, double baseCarHourlyRate) {
        super(regNo, (mins, time, pass) -> {
            if (pass != null && pass.coversFullDay()) {
                return ((OneDayVisitorPass) pass).getFlatPassFee();
            }
            if (mins <= 0) {
                return 0.0;
            }
            int hours = (mins + 59) / 60;
            boolean peakWindow = !time.isBefore(PEAK_START) && !time.isAfter(PEAK_END);
            double effectiveRate = peakWindow ? baseCarHourlyRate * 2.0 : baseCarHourlyRate;
            return hours * effectiveRate;
        });
    }
}

class FiveFloorJaggedParking {
    private final String[][] floorSlots;

    public FiveFloorJaggedParking(int[] slotsPerFloor) {
        this.floorSlots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            this.floorSlots[f] = new String[slotsPerFloor[f]];
        }
    }

    public int[] park(String regNo) {
        for (int f = 0; f < floorSlots.length; f++) {
            for (int s = 0; s < floorSlots[f].length; s++) {
                if (floorSlots[f][s] == null) {
                    floorSlots[f][s] = regNo;
                    return new int[]{f, s};
                }
            }
        }
        return null;
    }

    public boolean leave(int floor, int slot) {
        if (floor < 0 || floor >= floorSlots.length || slot < 0 || slot >= floorSlots[floor].length) {
            return false;
        }
        if (floorSlots[floor][slot] == null) {
            return false;
        }
        floorSlots[floor][slot] = null;
        return true;
    }
}

public class Day4DesignChallengeDemo {
    public static void main(String[] args) {
        FiveFloorJaggedParking facility = new FiveFloorJaggedParking(new int[]{12, 18, 20, 15, 10});

        EBike eBike = new EBike("TN05EB501", 15.0);
        DeliveryVan vanPeak = new DeliveryVan("TN06DV601", 30.0);
        DeliveryVan vanOffPeak = new DeliveryVan("TN07DV701", 30.0);
        Pass oneDayPass = new OneDayVisitorPass(120.0);

        System.out.println("Slot eBike: " + Arrays.toString(facility.park(eBike.getRegNo())));
        System.out.println("Slot vanPeak: " + Arrays.toString(facility.park(vanPeak.getRegNo())));
        System.out.println("Slot vanOffPeak: " + Arrays.toString(facility.park(vanOffPeak.getRegNo())));

        System.out.println("E-Bike (90m, no pass): Rs " + eBike.computeCharge(90, LocalTime.of(10, 0), null));
        System.out.println("Van Peak (130m at 10:30, no pass): Rs " + vanPeak.computeCharge(130, LocalTime.of(10, 30), null));
        System.out.println("Van Off-Peak (130m at 19:00, no pass): Rs " + vanOffPeak.computeCharge(130, LocalTime.of(19, 0), null));
        System.out.println("Van with 1-Day Pass: Rs " + vanPeak.computeCharge(240, LocalTime.of(11, 0), oneDayPass));
    }
}

OUTPUT
Task 3: ParkingFacility
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]

Task 8: FeeDemo
TwoWheeler TN01A1 -> Rs 30.0
Car TN02B2 -> Rs 90.0
ElectricCar TN03C3 -> Rs 210.0
true

Task 11: CampusParkingApp
Issued: TKT-1001, TKT-1002, TKT-1003
Fees -> TN01BK101: 0.0, TN02CR202: 0.0, TN03EV303: 110.0
Total Collection: Rs 110.0
Expected duplicate error: Duplicate registration number already parked: TN02CR202
Expected invalid exit error: Vehicle not currently parked: INVALID99

Task 12: Day4DesignChallengeDemo
Slot eBike: [0, 0]
Slot vanPeak: [0, 1]
Slot vanOffPeak: [0, 2]
E-Bike (90m, no pass): Rs 15.0
Van Peak (130m at 10:30, no pass): Rs 180.0
Van Off-Peak (130m at 19:00, no pass): Rs 90.0
Van with 1-Day Pass: Rs 120.0
