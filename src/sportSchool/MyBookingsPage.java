package sportSchool;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class MyBookingsPage extends JPanel {
    private JFrame frame;
    private JTextArea bookingsArea;
    private JTextField nameField;
    private JButton cancelBookingButton;

    public MyBookingsPage(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        // Navigation Panel at the top
        NavigationPanel navigationPanel = new NavigationPanel(frame);
        add(navigationPanel, BorderLayout.NORTH);

        nameField = new JTextField(20);
        JButton cancelBookingButton = new JButton("Cancel Booking");
        JButton searchButton = new JButton("Search"); // New search button

        bookingsArea = new JTextArea(15, 50);
        bookingsArea.setEditable(false);

        // Action listener for cancel booking button
        cancelBookingButton.addActionListener(e -> cancelBooking());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Name:"));
        inputPanel.add(nameField);
        inputPanel.add(searchButton); // Add search button to the panel
        inputPanel.add(cancelBookingButton);

        // Add hint label below the search bar
        JLabel hintLabel = new JLabel("Search by entering your name"); // New hint label
        inputPanel.add(hintLabel); // Add hint label to the panel

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookingsArea), BorderLayout.CENTER);

        // Navigation Panel at the bottom
        NavigationPanel bottomNavigationPanel = new NavigationPanel(frame);
        add(bottomNavigationPanel, BorderLayout.SOUTH);

        // Load bookings when the user enters their name
        nameField.addActionListener(e -> loadBookings());
        
        // Add tooltip for nameField
        nameField.setToolTipText("Enter your name to search.");

        // Action listener for search button
        searchButton.addActionListener(e -> loadBookings());
    }

    public void loadBookings() {
        String name = nameField.getText();
        bookingsArea.setText("");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sports.db")) {
            String sql = "SELECT id, class_name, booking_date FROM bookings WHERE user_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookingsArea.append("Booking ID: " + rs.getInt("id") + ", Class: " + rs.getString("class_name") +
                        ", Date: " + rs.getString("booking_date") + "\n");
            }
            if (bookingsArea.getText().isEmpty()) {
                bookingsArea.append("No bookings found for this user.\n");
            }
        } catch (SQLException e) {
            bookingsArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    private void cancelBooking() {
        String name = nameField.getText();
        String bookingId = JOptionPane.showInputDialog("Enter Booking ID to cancel:");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sports.db")) {
            String sql = "DELETE FROM bookings WHERE id = ? AND user_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(bookingId));
            pstmt.setString(2, name);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Booking cancelled successfully.");
                loadBookings(); // Refresh the bookings list after cancellation
            } else {
                JOptionPane.showMessageDialog(frame, "No booking found with this ID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid Booking ID.");
        }
    }
}