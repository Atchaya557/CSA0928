import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

public class AtmWithdrawalDemo {

    private static void verifyCardReaderFile(String path) throws FileNotFoundException {
        File cardFile = new File(path);
        if (!cardFile.exists()) {
            throw new FileNotFoundException("Card reader config file not found: " + path);
        }
    }

    public static void withdraw(String simulatedInput, String cardFilePath, double balance) {
        boolean dispensesStarted = false;
        try (Scanner sc = new Scanner(simulatedInput)) {
            verifyCardReaderFile(cardFilePath);

            String accountNo = sc.next();
            if (!sc.hasNextInt()) {
                throw new InputMismatchException("Amount must be a valid integer");
            }
            int amount = sc.nextInt();

            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive");
            }
            if (amount > balance) {
                throw new InsufficientBalanceException("Insufficient balance for account " + accountNo);
            }

            dispensesStarted = true;
            System.out.println("Dispensing Rs " + amount + " from account " + accountNo);
        } catch (FileNotFoundException | InputMismatchException e) {
            System.out.println("Input/Hardware error: " + e.getMessage());
        } catch (IllegalArgumentException | InsufficientBalanceException e) {
            System.out.println("Transaction rejected: " + e.getMessage());
        } finally {
            System.out.println("Finally cleanup: session closed | cashDispensed=" + dispensesStarted);
        }
    }

    public static void main(String[] args) {
        withdraw("ACC101 500", "nonexistent_card_reader.cfg", 2000.0);
        withdraw("ACC101 abc", "pom.xml", 2000.0);
        withdraw("ACC101 -100", "pom.xml", 2000.0);
        withdraw("ACC101 5000", "pom.xml", 2000.0);
        withdraw("ACC101 500", "pom.xml", 2000.0);
    }
}
```[cite: 28]

---

### Q2: `StorageDemo` (`Complete the Code` — Exam Portal Storage & Exception Chaining)[cite: 30]

```java
import java.io.IOException;

class ExamException extends Exception {
    ExamException(String message) {
        super(message);
    }

    ExamException(String message, Throwable cause) {
        super(message, cause);
    }
}

class SubmissionStorageException extends ExamException {
    SubmissionStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

class Storage {
    void save(String fileName, long sizeKb) throws SubmissionStorageException {
        try {
            if (sizeKb > 500) {
                throw new IOException("Disk quota exceeded");
            }
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            // TODO 1: Wrap the IOException in a SubmissionStorageException, keeping the
            // IOException as its cause
            throw new SubmissionStorageException("Could not store " + fileName, e);
        }
    }
}

public class StorageDemo {
    public static void main(String[] args) {
        Storage storage = new Storage();
        try {
            storage.save("answer01.pdf", 120);
            storage.save("answer02.pdf", 800);
        } catch (ExamException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Cause: " + e.getCause().getMessage());
        }
    }
}
```[cite: 30]

---

### Q3: `SalesReportGenerator` (Manual `finally` vs `try-with-resources`)[cite: 31]

```java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SalesReportGenerator {

    public static void generateManual(List<String> storeFiles, String outputFile) throws IOException {
        BufferedWriter writer = null;
        double totalSales = 0.0;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            for (String file : storeFiles) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        totalSales += Double.parseDouble(line.trim());
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
            writer.write("Total Sales: " + totalSales);
            writer.newLine();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void generateWithTryResources(List<String> storeFiles, String outputFile) throws IOException {
        double totalSales = 0.0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (String file : storeFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        totalSales += Double.parseDouble(line.trim());
                    }
                }
            }
            writer.write("Total Sales: " + totalSales);
            writer.newLine();
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> files = List.of("store1.txt", "store2.txt");
        generateWithTryResources(files, "summary_twr.txt");
        System.out.println("Report generated using try-with-resources.");
    }
}
```[cite: 31]

---

### Q4: `ScannerStationsDemo` (`Thread`, `Runnable`, `start()`, `sleep()`, `join()`, `isAlive()`, Daemon)[cite: 32]

```java
public class ScannerStationsDemo {

    static class StationThread extends Thread {
        private final int scripts;
        private int scanned;

        StationThread(String name, int scripts) {
            super(name);
            this.scripts = scripts;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= scripts; i++) {
                    scanned++;
                    Thread.sleep(20);
                }
                System.out.println(getName() + " finished scanning " + scanned + " scripts");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public int getScanned() {
            return scanned;
        }
    }

    static class StationTask implements Runnable {
        private final String stationName;
        private final int scripts;
        private int scanned;

        StationTask(String stationName, int scripts) {
            this.stationName = stationName;
            this.scripts = scripts;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= scripts; i++) {
                    scanned++;
                    Thread.sleep(25);
                }
                System.out.println(stationName + " finished scanning " + scanned + " scripts");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public int getScanned() {
            return scanned;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StationThread s1 = new StationThread("Station-1", 5);
        StationThread s2 = new StationThread("Station-2", 5);
        StationTask task3 = new StationTask("Station-3", 5);
        StationTask task4 = new StationTask("Station-4", 5);
        Thread s3 = new Thread(task3, "Station-3");
        Thread s4 = new Thread(task4, "Station-4");

        s4.setDaemon(true);

        s1.start();
        s2.start();
        s3.start();
        s4.start();

        System.out.println("Station-1 alive right after start(): " + s1.isAlive());

        s1.join();
        s2.join();
        s3.join();

        int total = s1.getScanned() + s2.getScanned() + task3.getScanned() + task4.getScanned();
        System.out.println("Total scripts scanned: " + total);
    }
}
```[cite: 32]

---

### Q5: `SeatBooking` (`Complete the Code` — Atomic Check-Then-Book)[cite: 33]

```java
public class SeatBooking {
    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBooking(int seats) {
        booked = new boolean[seats];
    }

    // TODO 1: Make the check-then-book operation atomic so that a seat can never be
    // sold twice
    public synchronized boolean book(int seat) {
        if (seat < 0 || seat >= booked.length) {
            return false;
        }
        if (!booked[seat]) {
            booked[seat] = true;
            bookingsMade++;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws InterruptedException {
        SeatBooking screen = new SeatBooking(10);
        Thread[] users = new Thread[100];
        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10; // 10 users compete for every seat
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }
        for (Thread t : users) {
            t.join();
        }
        System.out.println("Seats sold: " + screen.bookingsMade);
    }
}
```[cite: 33]

---

### Q6: `TokenCounter` (`Complete the Code` — Producer-Consumer) & `WalletTransferDemo` (Deadlock vs Lock Ordering)[cite: 34]

#### `TokenCounter` (`Complete the Code`):
```java
public class TokenCounter {
    private final int[] buffer = new int[5];
    private int count = 0, in = 0, out = 0;

    // the counter holds at most 5 tokens
    public synchronized void put(int token) throws InterruptedException {
        // TODO 1: Wait while the counter is full, add the token at position in, then
        // wake waiting threads
        while (count == buffer.length) {
            wait();
        }
        buffer[in] = token;
        in = (in + 1) % buffer.length;
        count++;
        notifyAll();
    }

    public synchronized int take() throws InterruptedException {
        // TODO 2: Wait while the counter is empty, remove the token at position out,
        // wake waiting threads and return the token
        while (count == 0) {
            wait();
        }
        int token = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        notifyAll();
        return token;
    }

    public static void main(String[] args) throws InterruptedException {
        TokenCounter counter = new TokenCounter();
        int[] collected = new int[2];

        Thread cook = new Thread(() -> {
            try {
                for (int t = 1; t <= 20; t++) {
                    counter.put(t);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread[] students = new Thread[2];
        for (int s = 0; s < 2; s++) {
            final int id = s;
            students[s] = new Thread(() -> {
                try {
                    for (int k = 0; k < 10; k++) {
                        collected[id] += counter.take();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        cook.start();
        for (Thread t : students) t.start();
        cook.join();
        for (Thread t : students) t.join();
        System.out.println("Sum of tokens collected: " + (collected[0] + collected[1]) + " (expected 210)");
    }
}
```[cite: 34]

#### `WalletTransferDemo` (Consistent Lock Ordering):
```java
public class WalletTransferDemo {

    static class Wallet {
        private final int id;
        private int balance;

        Wallet(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }

        int getId() {
            return id;
        }

        int getBalance() {
            return balance;
        }

        void debit(int amt) {
            balance -= amt;
        }

        void credit(int amt) {
            balance += amt;
        }
    }

    public static void safeTransfer(Wallet from, Wallet to, int amount) {
        Wallet first = from.getId() < to.getId() ? from : to;
        Wallet second = from.getId() < to.getId() ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() >= amount) {
                    from.debit(amount);
                    to.credit(amount);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Wallet w1 = new Wallet(1, 1000);
        Wallet w2 = new Wallet(2, 1000);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) safeTransfer(w1, w2, 1);
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++) safeTransfer(w2, w1, 1);
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("W1 balance: " + w1.getBalance() + ", W2 balance: " + w2.getBalance());
    }
}
```[cite: 34]

---

### Q7: `ResultProcessorDemo` (`ExecutorService`, `Callable`, `Future`, `ConcurrentHashMap`, `AtomicInteger`)[cite: 35]

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultProcessorDemo {

    static class ScoreFileTask implements Callable<String> {
        private final Path filePath;

        ScoreFileTask(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public String call() throws Exception {
            if (!Files.exists(filePath)) {
                throw new IOException("Missing file: " + filePath);
            }
            String content = Files.readString(filePath).trim();
            int score = Integer.parseInt(content);
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("Out-of-range score in " + filePath + ": " + score);
            }
            return score >= 40 ? "PASS(" + score + ")" : "FAIL(" + score + ")";
        }
    }

    public static void processFiles(List<Path> files, int poolSize) {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Future<String>> futures = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        long start = System.nanoTime();
        for (Path p : files) {
            fileNames.add(p.getFileName().toString());
            futures.add(pool.submit(new ScoreFileTask(p)));
        }

        for (int i = 0; i < futures.size(); i++) {
            String name = fileNames.get(i);
            try {
                String status = futures.get(i).get();
                results.put(name, status);
                successCount.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                results.put(name, "ERROR: Interrupted");
                failureCount.incrementAndGet();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                results.put(name, "ERROR: " + cause.getMessage());
                failureCount.incrementAndGet();
            }
        }

        pool.shutdown();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Pool size: " + poolSize
                + " | Success: " + successCount.get()
                + " | Failed: " + failureCount.get()
                + " | Time: " + elapsedMs + " ms");
    }

    public static void main(String[] args) throws IOException {
        Path tempDir = Files.createTempDirectory("semester_scores");
        Path valid1 = tempDir.resolve("s1.txt");
        Path valid2 = tempDir.resolve("s2.txt");
        Path corrupt = tempDir.resolve("s3_corrupt.txt");
        Path missing = tempDir.resolve("s4_missing.txt");

        Files.writeString(valid1, "85");
        Files.writeString(valid2, "32");
        Files.writeString(corrupt, "NOT_A_NUMBER");

        List<Path> batch = List.of(valid1, valid2, corrupt, missing);
        for (int threads : new int[]{1, 2, 4, 8}) {
            processFiles(batch, threads);
        }
    }
}

//OUTPUT:
Q1: ETicketBenchmark
String '+' length: 300000 | time(ms): <varies by machine>
StringBuilder length: 300000 | time(ms): <varies by machine>
StringBuffer length: 300000 | time(ms): <varies by machine>

Q2: PlaylistComparison
ArrayList (mid=5000): <varies by machine> ms
LinkedList (mid=5000): <varies by machine> ms
ArrayDeque (polled=-1): <varies by machine> ms

Q3: Attendance
Unique students: 3

Q4: `PharmacyInventoryDemo
Lookup sum=252500 | 100k lookups(ms)=<varies by machine>
TreeMap (code order) total=252500 | time(ms)=<varies by machine>
LinkedHashMap (insertion order) total=252500 | time(ms)=<varies by machine>

Q5: LeaderboardDemo
Plaintext
[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]

Q6: `TokenCounter` & `WalletTransferDemo`
Sum of tokens collected: 210 (expected 210)
`WalletTransferDemo`:
W1 balance: 1000, W2 balance: 1000

Q7: ResultProcessorDemo
Pool size: 1 | Success: 2 | Failed: 2 | Time: <varies by machine> ms
Pool size: 2 | Success: 2 | Failed: 2 | Time: <varies by machine> ms
Pool size: 4 | Success: 2 | Failed: 2 | Time: <varies by machine> ms
Pool size: 8 | Success: 2 | Failed: 2 | Time: <varies by machine> ms
