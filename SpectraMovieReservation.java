import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

// Spectra Movie Reservation System
public class SpectraMovieReservation extends JFrame {
    // Inner class: Movie
    static class Movie implements Serializable {
        String title;
        String time;
        double price;

        Movie(String title, String time, double price) {
            this.title = title;
            this.time = time;
            this.price = price;
        }

        @Override
        public String toString() {
            return title + " | " + time + " | ₹" + price;
        }
    }

    // Inner class: Booking
    static class Booking implements Serializable {
        Movie movie;
        String customerName;
        int seats;
        double totalCost;

        Booking(Movie movie, String name, int seats) {
            this.movie = movie;
            this.customerName = name;
            this.seats = seats;
            this.totalCost = seats * movie.price;
        }

        @Override
        public String toString() {
            return customerName + " booked " + seats + " seat(s) for "
                    + movie.title + " at " + movie.time + " | Total: ₹" + totalCost;
        }
    }

    // Fields
    private ArrayList<Movie> movies = new ArrayList<>();
    private ArrayList<Booking> bookings = new ArrayList<>();
    private DefaultListModel<String> movieListModel = new DefaultListModel<>();
    private JList<String> movieList = new JList<>(movieListModel);
    private final String FILE_NAME = "bookings.dat";

    // Constructor
    public SpectraMovieReservation() {
        setTitle("🎬 Spectra Movie Reservation System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load movies
        movies.add(new Movie("Avengers: Endgame", "10:00 AM", 250));
        movies.add(new Movie("Oppenheimer", "1:30 PM", 300));
        movies.add(new Movie("Inception", "5:00 PM", 220));
        movies.add(new Movie("Dune: Part Two", "8:00 PM", 280));

        for (Movie m : movies) movieListModel.addElement(m.toString());

        // Buttons
        JButton bookBtn = new JButton("🎟️ Book Ticket");
        JButton cancelBtn = new JButton("❌ Cancel Booking");
        JButton viewBtn = new JButton("📜 View All Bookings");
        JButton exitBtn = new JButton("🚪 Exit");

        JPanel btnPanel = new JPanel();
        btnPanel.add(bookBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(viewBtn);
        btnPanel.add(exitBtn);

        add(new JLabel("Select a Movie to Book", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(movieList), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Load previous bookings
        loadBookings();

        // Button Actions
        bookBtn.addActionListener(e -> bookTicket());
        cancelBtn.addActionListener(e -> cancelBooking());
        viewBtn.addActionListener(e -> viewBookings());
        exitBtn.addActionListener(e -> {
            saveBookings();
            System.exit(0);
        });
    }

    // Book Ticket
    private void bookTicket() {
        int index = movieList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie to book.");
            return;
        }
        Movie selected = movies.get(index);
        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.trim().isEmpty()) return;

        String seatStr = JOptionPane.showInputDialog(this, "Enter number of seats:");
        if (seatStr == null || seatStr.trim().isEmpty()) return;
        int seats;
        try {
            seats = Integer.parseInt(seatStr);
            if (seats <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid seat number.");
            return;
        }

        Booking b = new Booking(selected, name, seats);
        bookings.add(b);
        saveBookings();
        JOptionPane.showMessageDialog(this, "Booking Successful!\n" + b.toString());
    }

    // Cancel Booking
    private void cancelBooking() {
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings found.");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Enter your name to cancel booking:");
        if (name == null || name.trim().isEmpty()) return;

        boolean removed = bookings.removeIf(b -> b.customerName.equalsIgnoreCase(name));
        if (removed) {
            saveBookings();
            JOptionPane.showMessageDialog(this, "Booking cancelled successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No booking found under that name.");
        }
    }

    // View All Bookings
    private void viewBookings() {
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bookings available.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Booking b : bookings) sb.append(b.toString()).append("\n\n");
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "All Bookings", JOptionPane.INFORMATION_MESSAGE);
    }

    // Save & Load Bookings
    private void saveBookings() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(bookings);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving bookings: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadBookings() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            bookings = (ArrayList<Booking>) in.readObject();
        } catch (Exception e) {
            bookings = new ArrayList<>();
        }
    }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpectraMovieReservation().setVisible(true));
    }
}
