package sportSchool;
import javax.swing.*;
import java.awt.*;

public class SportsBookingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sports Booking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            HomePage homePage = new HomePage(frame);
            frame.add(homePage, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}