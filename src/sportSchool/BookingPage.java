package sportSchool;
import javax.swing.*;
import org.sqlite.SQLiteErrorCode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

class BookingPage extends JPanel {
    private JFrame frame;
    private JTextField nameField, semesterField, departmentField;
    private JComboBox<String> classDropdown;
    private JTextArea resultArea;
    private MyBookingsPage myBookingsPage; // Reference to MyBookingsPage

    public BookingPage(JFrame frame, MyBookingsPage myBookingsPage) {
        this.frame = frame;
        this.myBookingsPage = myBookingsPage; // Initialize reference

        // Set up the panel with BorderLayout
        setLayout(new BorderLayout());

        // Create a panel for the form with GridBagLayout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // padding

        // Set a smaller font for the entire panel
        setFont(new Font("Arial", Font.PLAIN, 12));

        // Create and set preferred sizes for fields
        nameField = new JTextField(10);
        semesterField = new JTextField(10);
        departmentField = new JTextField(10);
        resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setPreferredSize(new Dimension(150, 50));

        classDropdown = new JComboBox<>();
        loadClasses();

        JButton bookButton = new JButton("Book Class");
        bookButton.setPreferredSize(new Dimension(150, 30)); // button size
        bookButton.addActionListener(new BookClassAction());

        //components to the form panel using GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Semester:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(semesterField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Department:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(departmentField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Select Class:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(classDropdown, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; // Span across two columns
        formPanel.add(bookButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; 
        formPanel.add(new JScrollPane(resultArea), gbc);

        //better visual separation
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the form panel to the center 
        add(formPanel, BorderLayout.CENTER);

        // add the NavigationPanel 
        NavigationPanel navigationPanel = new NavigationPanel(frame);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private void loadClasses() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sports.db")) {
            String sql = "SELECT name FROM sports_classes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                classDropdown.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            resultArea.setText("Error loading classes: " + e.getMessage());
        }
    }

    private class BookClassAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String semesterStr = semesterField.getText().trim();
            String department = departmentField.getText().trim();
            String selectedClass = (String) classDropdown.getSelectedItem();

            // Validate fields
            StringBuilder errorMessage = new StringBuilder();

            // Validate name
            if (!isValidName(name)) {
                errorMessage.append("Name must be at least 3 letters long and contain only English letters.\n");
            }

            // Validate semester
            int semester = -1;
            if (semesterStr.isEmpty()) {
                errorMessage.append("Semester is required and must be a number between 0 and  12.\n");
            } else {
                try {
                    semester = Integer.parseInt(semesterStr);
                    if (semester < 0 || semester > 12) {
                        errorMessage.append("Semester must be a number between 0 and 12.\n");
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("Semester must be a valid number.\n");
                }
            }

            // If there are validation errors, show them and return
            if (errorMessage.length() > 0) {
                resultArea.setText(errorMessage.toString());
                return;
            }

            //  current date and time for booking_date
            String bookingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sports.db")) {
                String sql = "INSERT INTO bookings (user_name, semester, department, class_name, booking_date) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, semesterStr); // Set semester as a string
                pstmt.setString(3, department);
                pstmt.setString(4, selectedClass);
                pstmt.setString(5, bookingDate); // Set the booking date
                pstmt.executeUpdate();
                resultArea.setText("Successfully booked " + selectedClass);

                // Refresh bookings in MyBookingsPage
                myBookingsPage.loadBookings(); // Call the method to refresh bookings
            } catch (SQLException ex) {
                //  if the error is due to a unique constraint violation
                if (ex.getErrorCode() == 19) { // 19 is the error code for UNIQUE constraint violation in SQLite
                    resultArea.setText("You cannot register for the same class more than once for the same student.");
                } else if (ex.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_NOTNULL.code) {
                    resultArea.setText("Semester is required. Please provide a valid semester.");
                } else {
                    resultArea.setText("An error occurred while booking the class. Please try again.");
                }
            }
        }

        private boolean isValidName(String name) {
            return name.length() >= 3 && Pattern.matches("[a-zA-Z]+", name);
        }
    }
}