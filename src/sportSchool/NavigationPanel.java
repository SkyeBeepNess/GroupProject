package sportSchool;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NavigationPanel extends JPanel {
    private JFrame frame;

    public NavigationPanel(JFrame frame) {
        this.frame = frame;
        setLayout(new FlowLayout());

        JButton homeButton = new JButton("Home");
        JButton searchButton = new JButton("Search Classes");
        JButton myBookingsButton = new JButton("My Bookings");
        JButton bookNowButton = new JButton("Book Now");

        homeButton.addActionListener(e -> showHomePage());
        searchButton.addActionListener(e -> showSearchPage());
        myBookingsButton.addActionListener(e -> showMyBookingsPage());
        bookNowButton.addActionListener(e -> showBookingPage());

        add(homeButton);
        add(searchButton);
        add(myBookingsButton);
        add(bookNowButton);
    }

    private void showHomePage() {
        HomePage homePage = new HomePage(frame);
        frame.setContentPane(homePage);
        frame.revalidate();
    }

    private void showSearchPage() {
        SearchPage searchPage = new SearchPage(frame);
        frame.setContentPane(searchPage);
        frame.revalidate();
    }

    private void showMyBookingsPage() {
        MyBookingsPage myBookingsPage = new MyBookingsPage(frame);
        frame.setContentPane(myBookingsPage);
        frame.revalidate();
    }

    private void showBookingPage() {
        BookingPage bookingPage = new BookingPage(frame, new MyBookingsPage(frame)); // Pass MyBookingsPage instance
        frame.setContentPane(bookingPage);
        frame.revalidate();
    }
}