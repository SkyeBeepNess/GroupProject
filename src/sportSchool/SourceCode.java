package sportSchool;
import javax.swing.*;
import java.awt.*;


    public class SourceCode {
	public static void main(String[] args) {
		
	        SwingUtilities.invokeLater(() -> {  // invokeLater -> 
	        	
	            JFrame frame = new JFrame("Sports Booking System");
	            
	            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	            
	            frame.setSize(800, 600);
	            
	            frame.setLayout(new BorderLayout());
	            

	            HomePage homePage = new HomePage(frame);
	            frame.add(homePage, BorderLayout.CENTER);

	            frame.setVisible(true);
	        });
	    }


}
