package sportSchool;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class SearchPage extends JPanel {
    private JFrame frame;
    private JTextField searchField;
    private JTextArea resultArea;

    public SearchPage(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        NavigationPanel navigationPanel = new NavigationPanel(frame);
        add(navigationPanel, BorderLayout.SOUTH);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);

        searchButton.addActionListener(new SearchAction());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Classes:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private class SearchAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = searchField.getText();
            StringBuilder results = new StringBuilder();
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:sports.db")) {
                String sql = "SELECT * FROM sports_classes WHERE name LIKE ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "%" + searchTerm + "%");
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    results.append("ID: ").append(rs.getInt("id"))
                            .append(", Name: ").append(rs.getString("name"))
                            .append(", Date: ").append(rs.getString("date"))
                            .append(", Time: ").append(rs.getString("time"))
                            .append(", Price: ").append(rs.getDouble("price"))
                            .append("\n");
                }
            } catch (SQLException ex) {
                results.append("Error searching classes: ").append(ex.getMessage()).append("\n");
            }
            resultArea.setText(results.toString());
        }
    }
}