package sportSchool;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

class HomePage extends JPanel {
    private JFrame frame;
    private JTextArea classListArea;

    public HomePage(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        JLabel headingLabel = new JLabel("Available Sports Classes", SwingConstants.CENTER);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font size and style
        add(headingLabel, BorderLayout.NORTH); // Add heading to the top

        NavigationPanel navigationPanel = new NavigationPanel(frame);
        add(navigationPanel, BorderLayout.SOUTH);

        classListArea = new JTextArea(15, 50);
        classListArea.setEditable(false);
        loadClasses();

        add(new JScrollPane(classListArea), BorderLayout.CENTER);
    }

    private void loadClasses() {
        StringBuilder classes = new StringBuilder();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:src/resources/database/sports.db")) {
            String sql = "SELECT * FROM sports_classes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                classes.append("ID: ").append(rs.getInt("id"))
                        .append(", Name:    ").append(rs.getString("name")) //add space between 
                        .append("")
                        .append(", Date:    ").append(rs.getString("date"))
                        .append("")
                        .append(", Time:    ").append(rs.getString("time"))
                        .append("")
                        .append(", Price:   ").append(rs.getDouble("price"))
                        .append("")
                        .append("\n\n");
            }
        } catch (SQLException e) {
            classes.append("Error loading classes: ").append(e.getMessage()).append("\n");
        }
        classListArea.setText(classes.toString());
    }
}