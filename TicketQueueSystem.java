import java.io.*;
import java.util.Scanner;

// One support ticket (a single record)
class Ticket {
    String id;
    String customer;
    String issue;

    Ticket(String id, String customer, String issue) {
        this.id = id;
        this.customer = customer;
        this.issue = issue;
    }

    public String toString() {
        return id + " | " + customer + " | " + issue;
    }
}

// One node of the linked list
class Node {
    Ticket data;
    Node next;

    Node(Ticket data) {
        this.data = data;
        this.next = null;
    }
}

// Queue built with a linked list (FIFO)
class TicketQueue {
    private Node front = null;   // next ticket to be served
    private Node rear = null;    // last ticket added
    private int size = 0;
    private int nextId = 101;    // first ticket is T101
    private final String FILE_NAME = "tickets.txt";

    // add a ticket at the rear
    private void enqueue(Ticket t) {
        Node n = new Node(t);
        if (rear == null) {
            front = n;
            rear = n;
        } else {
            rear.next = n;
            rear = n;
        }
        size++;
    }

    // remove a ticket from the front
    private Ticket dequeue() {
        if (front == null) {
            return null;
        }
        Ticket t = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return t;
    }

    // 1 - Raise ticket
    public void raiseTicket(String customer, String issue) {
        Ticket t = new Ticket("T" + nextId, customer, issue);
        nextId++;
        enqueue(t);
        System.out.println("Ticket raised. ID: " + t + " | Position: " + size);
    }

    // 2 - Serve next ticket
    public void serveNext() {
        Ticket t = dequeue();
        if (t == null) {
            System.out.println("No tickets in the queue.");
            return;
        }
        System.out.println("Ticket " + t.id + " served. Customer: " + t.customer);
        System.out.println("Tickets waiting: " + size);
    }

    // 3 - View queue
    public void viewQueue() {
        if (front == null) {
            System.out.println("No tickets in the queue.");
            return;
        }
        System.out.println("Waiting: " + size);
        Node current = front;
        int position = 1;
        while (current != null) {
            System.out.println(position + ". " + current.data);
            current = current.next;
            position++;
        }
    }

    // 4 - Search ticket by ID (manual linear search)
    public void searchTicket(String id) {
        Node current = front;
        int position = 1;
        while (current != null) {
            if (current.data.id.equalsIgnoreCase(id)) {
                System.out.println("Ticket found: " + current.data + " | Position: " + position);
                return;
            }
            current = current.next;
            position++;
        }
        System.out.println("Ticket " + id + " not found.");
    }

    // Load tickets from file when the program starts
    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", 3);
                if (parts.length < 3) {
                    continue;
                }
                String id = parts[0].trim();
                enqueue(new Ticket(id, parts[1].trim(), parts[2].trim()));

                int number = Integer.parseInt(id.substring(1));
                if (number >= nextId) {
                    nextId = number + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Save tickets to file
    public void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            Node current = front;
            while (current != null) {
                pw.println(current.data);
                current = current.next;
            }
        } catch (IOException e) {
            System.out.println("Could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }
}

public class TicketQueueSystem {
    public static void main(String[] args) {
        TicketQueue queue = new TicketQueue();
        queue.loadFromFile();

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("1 - Raise Ticket");
            System.out.println("2 - Serve Next Ticket");
            System.out.println("3 - View Queue");
            System.out.println("4 - Search Ticket by ID");
            System.out.println("5 - Exit");
            System.out.print("Enter option: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Customer: ");
                    String customer = sc.nextLine().trim().replace("|", "/");
                    System.out.print("Issue: ");
                    String issue = sc.nextLine().trim().replace("|", "/");
                    queue.raiseTicket(customer, issue);
                    queue.saveToFile();
                    break;
                case "2":
                    queue.serveNext();
                    queue.saveToFile();
                    break;
                case "3":
                    queue.viewQueue();
                    break;
                case "4":
                    System.out.print("Enter ticket ID: ");
                    queue.searchTicket(sc.nextLine().trim());
                    break;
                case "5":
                    queue.saveToFile();
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        sc.close();
    }
}
