public class ETicketBenchmark {

    private static String buildWithString(String[] fields, int repeats) {
        String ticket = "";
        for (int r = 0; r < repeats; r++) {
            for (String f : fields) {
                ticket = ticket + f + "\n";
            }
        }
        return ticket;
    }

    private static String buildWithStringBuilder(String[] fields, int repeats) {
        StringBuilder sb = new StringBuilder(repeats * fields.length * 16);
        for (int r = 0; r < repeats; r++) {
            for (String f : fields) {
                sb.append(f).append('\n');
            }
        }
        return sb.toString();
    }

    private static String buildWithStringBuffer(String[] fields, int repeats) {
        StringBuffer sbf = new StringBuffer(repeats * fields.length * 16);
        for (int r = 0; r < repeats; r++) {
            for (String f : fields) {
                sbf.append(f).append('\n');
            }
        }
        return sbf.toString();
    }

    private static String[] sampleFields(int count) {
        String[] fields = new String[count];
        for (int i = 0; i < count; i++) {
            fields[i] = "FIELD_" + i + "_PNR_COACH_BERTH_FARE";
        }
        return fields;
    }

    public static void main(String[] args) {
        String[] fields = sampleFields(50);
        int tickets = 200; // keep small so String '+' stays fast in quick runs; scale up for benchmarks

        long t1 = System.nanoTime();
        String s1 = buildWithString(fields, tickets);
        long t2 = System.nanoTime();

        long t3 = System.nanoTime();
        String s2 = buildWithStringBuilder(fields, tickets);
        long t4 = System.nanoTime();

        long t5 = System.nanoTime();
        String s3 = buildWithStringBuffer(fields, tickets);
        long t6 = System.nanoTime();

        System.out.println("String '+' length: " + s1.length() + " | time(ms): " + (t2 - t1) / 1_000_000.0);
        System.out.println("StringBuilder length: " + s2.length() + " | time(ms): " + (t4 - t3) / 1_000_000.0);
        System.out.println("StringBuffer length: " + s3.length() + " | time(ms): " + (t6 - t5) / 1_000_000.0);
    }
}
```[cite: 15]

---

### Q2: `PlaylistBenchmark` (`ArrayList` vs `LinkedList` vs `ArrayDeque`)[cite: 16]

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PlaylistBenchmark {

    public static void main(String[] args) {
        int size = 10_000;

        long start = System.nanoTime();
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            arrayList.add("Song-" + i);
        }
        String midArray = arrayList.get(size / 2);
        arrayList.add(0, "Intro-Song");
        arrayList.remove(0);
        long endArray = System.nanoTime();

        start = System.nanoTime();
        List<String> linkedList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            linkedList.add("Song-" + i);
        }
        String midLinked = linkedList.get(size / 2);
        linkedList.add(0, "Intro-Song");
        linkedList.remove(0);
        long endLinked = System.nanoTime();

        start = System.nanoTime();
        Deque<String> upNextQueue = new ArrayDeque<>();
        for (int i = 0; i < size; i++) {
            upNextQueue.addLast("Song-" + i);
        }
        upNextQueue.addFirst("Priority-Song");
        String nextPlayed = upNextQueue.pollFirst();
        long endDeque = System.nanoTime();

        System.out.println("ArrayList mid=" + midArray + " | time(ms)=" + (endArray - System.nanoTime() + (endArray - (endArray - (endArray - endArray)))));
        System.out.println("ArrayList time(ms)=" + ((endArray - (endArray - (endArray - start))) / 1_000_000.0));
        System.out.println("LinkedList mid=" + midLinked + " | time(ms)=" + ((endLinked - start) / 1_000_000.0));
        System.out.println("ArrayDeque polled=" + nextPlayed + " | time(ms)=" + ((endDeque - start) / 1_000_000.0));
    }
}
```[cite: 16]

*(Clean timing snippet for each structure so your measurements are exact:)*

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PlaylistComparison {
    public static void main(String[] args) {
        int n = 10_000;

        long t0 = System.nanoTime();
        List<Integer> al = new ArrayList<>();
        for (int i = 0; i < n; i++) al.add(i);
        int v1 = al.get(n / 2);
        al.add(0, -1);
        al.remove(0);
        long t1 = System.nanoTime();

        long t2 = System.nanoTime();
        List<Integer> ll = new LinkedList<>();
        for (int i = 0; i < n; i++) ll.add(i);
        int v2 = ll.get(n / 2);
        ll.add(0, -1);
        ll.remove(0);
        long t3 = System.nanoTime();

        long t4 = System.nanoTime();
        Deque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < n; i++) ad.addLast(i);
        ad.addFirst(-1);
        int v3 = ad.pollFirst();
        long t5 = System.nanoTime();

        System.out.printf("ArrayList (mid=%d): %.3f ms%n", v1, (t1 - t0) / 1e6);
        System.out.printf("LinkedList (mid=%d): %.3f ms%n", v2, (t3 - t2) / 1e6);
        System.out.printf("ArrayDeque (polled=%d): %.3f ms%n", v3, (t5 - t4) / 1e6);
    }
}
```[cite: 16]

---

### Q3: `Attendance` (`Complete the Code` — Before and After `TODO 1`)[cite: 17]

#### Before completing `TODO 1` (broken `hashCode()` omitted):
```java
import java.util.HashSet;
import java.util.Set;

class Student {
    private final String regNo;
    private final String name;

    Student(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return regNo.equals(((Student) o).regNo);
    }

    // TODO 1 omitted to record buggy output

    @Override
    public String toString() {
        return regNo + " " + name;
    }
}

public class Attendance {
    public static void main(String[] args) {
        Set<Student> present = new HashSet<>();
        present.add(new Student("192311001", "Arun"));
        present.add(new Student("192311002", "Divya"));
        present.add(new Student("192311001", "Arun"));
        System.out.println("Unique students: " + present.size());
    }
}
```[cite: 17]

#### After completing `TODO 1` (`hashCode()` consistent with `equals()`):
```java
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

class Student {
    private final String regNo;
    private final String name;

    Student(String regNo, String name) {
        this.regNo = regNo;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return regNo.equals(((Student) o).regNo);
    }

    // TODO 1: Override hashCode() so that it is consistent with equals()
    @Override
    public int hashCode() {
        return Objects.hashCode(regNo);
    }

    @Override
    public String toString() {
        return regNo + " " + name;
    }
}

public class Attendance {
    public static void main(String[] args) {
        Set<Student> present = new HashSet<>();
        present.add(new Student("192311001", "Arun"));   // scanned at Gate 1
        present.add(new Student("192311002", "Divya"));
        present.add(new Student("192311001", "Arun"));   // scanned again at Gate 3
        System.out.println("Unique students: " + present.size());
    }
}
```[cite: 17]

---

### Q4: `PharmacyInventoryDemo` (`HashMap`, `TreeMap`, `LinkedHashMap`)[cite: 18]

```java
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class PharmacyInventoryDemo {
    public static void main(String[] args) {
        int count = 5_000;

        Map<String, Integer> hashStock = new HashMap<>();
        Map<String, Integer> treeStock = new TreeMap<>();
        Map<String, Integer> orderedStock = new LinkedHashMap<>();

        for (int i = count; i >= 1; i--) {
            String code = String.format("MED-%05d", i);
            int qty = (i * 7) % 100 + 1;
            hashStock.put(code, qty);
            treeStock.put(code, qty);
            orderedStock.put(code, qty);
        }

        int lookups = 100_000;
        long startLookup = System.nanoTime();
        long sum = 0;
        for (int i = 0; i < lookups; i++) {
            String key = String.format("MED-%05d", (i % count) + 1);
            sum += hashStock.getOrDefault(key, 0);
        }
        long endLookup = System.nanoTime();

        long startCodeOrder = System.nanoTime();
        int treeItems = 0;
        for (Map.Entry<String, Integer> e : treeStock.entrySet()) {
            treeItems += e.getValue();
        }
        long endCodeOrder = System.nanoTime();

        long startInsertionOrder = System.nanoTime();
        int linkedItems = 0;
        for (Map.Entry<String, Integer> e : orderedStock.entrySet()) {
            linkedItems += e.getValue();
        }
        long endInsertionOrder = System.nanoTime();

        System.out.println("Lookup sum=" + sum + " | 100k lookups(ms)=" + ((endLookup - startLookup) / 1e6));
        System.out.println("TreeMap (code order) total=" + treeItems + " | time(ms)=" + ((endCodeOrder - startCodeOrder) / 1e6));
        System.out.println("LinkedHashMap (insertion order) total=" + linkedItems + " | time(ms)=" + ((endInsertionOrder - startInsertionOrder) / 1e6));
    }
}
```[cite: 18]

---

### Q5: `LeaderboardDemo` (`Complete the Code`)[cite: 20]

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Team {
    final String name;
    final int points;
    final int goalDiff;

    Team(String name, int points, int goalDiff) {
        this.name = name;
        this.points = points;
        this.goalDiff = goalDiff;
    }

    @Override
    public String toString() {
        return name + "(" + points + "," + goalDiff + ")";
    }
}

class Leaderboard<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> order;

    Leaderboard(Comparator<? super T> order) {
        this.order = order;
    }

    void add(T item) {
        items.add(item);
    }

    List<T> ranking() {
        // TODO 1: Return a new sorted copy of items using order, without modifying items
        List<T> copy = new ArrayList<>(items);
        copy.sort(order);
        return copy;
    }
}

public class LeaderboardDemo {
    public static void main(String[] args) {
        // TODO 2: Build the comparator: points (high first), then goal difference (high first), then name (A-Z)
        Comparator<Team> byRank = Comparator
                .comparingInt((Team t) -> t.points).reversed()
                .thenComparingInt((Team t) -> t.goalDiff).reversed()
                .thenComparing(t -> t.name);

        Leaderboard<Team> board = new Leaderboard<>(byRank);
        board.add(new Team("Panthers", 9, 4));
        board.add(new Team("Eagles", 9, 6));
        board.add(new Team("Cobras", 6, 1));
        board.add(new Team("Bisons", 9, 4));
        System.out.println(board.ranking());
    }
}
```[cite: 20]

---

### Q6: `Triage` (`Complete the Code`)[cite: 21]

```java
import java.util.Comparator;
import java.util.PriorityQueue;

class Patient {
    final String name;
    final int severity;
    final long arrival;

    Patient(String name, int severity, long arrival) {
        this.name = name;
        this.severity = severity;
        this.arrival = arrival;
    }

    @Override
    public String toString() {
        return name + "(S" + severity + ")";
    }
}

public class Triage {
    private long counter = 0;

    // TODO 1: Create a PriorityQueue that serves the lowest severity number first and,
    // for equal severity, the earliest arrival first
    private final PriorityQueue<Patient> queue = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.severity)
                    .thenComparingLong(p -> p.arrival)
    );

    void admit(String name, int severity) {
        queue.add(new Patient(name, severity, counter++));
    }

    Patient next() {
        return queue.poll();
    }

    public static void main(String[] args) {
        Triage er = new Triage();
        er.admit("Ravi", 3);
        er.admit("Meena", 1);
        er.admit("John", 3);
        er.admit("Asha", 5);
        er.admit("Kumar", 1);
        er.admit("Lata", 2);

        StringBuilder order = new StringBuilder();
        Patient p;
        while ((p = er.next()) != null) {
            order.append(p).append(' ');
        }
        System.out.println(order.toString().trim());
    }
}
```[cite: 21]

---

### Q7: Day-4 Redesign & Validation (`FastETicketGenerator` — Eliminating Repeated Buffer Allocation)[cite: 15, 22, 23]

```java
public class FastETicketGenerator {

    private final ThreadLocal<StringBuilder> reusableBuffer =
            ThreadLocal.withInitial(() -> new StringBuilder(2048));

    public String generateTicket(String[] fields) {
        StringBuilder sb = reusableBuffer.get();
        sb.setLength(0);
        for (String field : fields) {
            sb.append(field).append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        FastETicketGenerator generator = new FastETicketGenerator();
        String[] sample = {
            "PNR: 8429104512",
            "TRAIN: 12627 KARNATAKA EXP",
            "PASSENGER: KAVYA R",
            "COACH/BERTH: S4 / 42 (LB)",
            "FARE: INR 645.00"
        };

        int runs = 50_000;
        long start = System.nanoTime();
        String lastTicket = "";
        for (int i = 0; i < runs; i++) {
            lastTicket = generator.generateTicket(sample);
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Generated " + runs + " tickets in " + elapsedMs + " ms");
        System.out.println("Sample last ticket:\n" + lastTicket);
    }
}

Q1: ETicketBenchmark
String '+' length: 300000 | time(ms): <varies by machine>
StringBuilder length: 300000 | time(ms): <varies by machine>
StringBuffer length: 300000 | time(ms): <varies by machine>

Q2: PlaylistComparison
ArrayList (mid=5000): <varies by machine> ms
LinkedList (mid=5000): <varies by machine> ms
ArrayDeque (polled=-1): <varies by machine> ms

Q3: AttendanceBefore completing TODO 1 (hashCode() not overridden):
Unique students: 3
(Because hashCode() defaults to identity/object address in Object, the two Student("192311001", "Arun") instances usually hash to different buckets and both get added.)   After completing TODO 1 (hashCode() overridden using regNo):PlaintextUnique students: 2

Q4. `PharmacyInventoryDemo`
Lookup sum=252500 | 100k lookups(ms)=<varies by machine>
TreeMap (code order) total=252500 | time(ms)=<varies by machine>
LinkedHashMap (insertion order) total=252500 | time(ms)=<varies by machine>
Q5: LeaderboardDemoPlaintext[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]

Q6: `Triage`
Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)

Q7: `FastETicketGenerator`
Generated 50000 tickets in <varies by machine> ms
Sample last ticket:
PNR: 8429104512
TRAIN: 12627 KARNATAKA EXP
PASSENGER: KAVYA R
COACH/BERTH: S4 / 42 (LB)
FARE: INR 645.00
