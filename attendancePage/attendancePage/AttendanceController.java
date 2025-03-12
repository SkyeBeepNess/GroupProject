package attendancePage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.List;




public class AttendanceController {
	private boolean filterOpen = false;
	@FXML
	private Button homeButton;
	@FXML
	protected VBox coursesContainer;
	@FXML
	protected ScrollPane scrollablePane;
	@FXML
	protected Button filterButton;
	@FXML
	protected StackPane filterOverlay;
	@FXML
	protected ScrollPane courseIDFilter;
	
	@FXML
	protected ArrayList<CheckBox> checkboxes = new ArrayList<>();
	
	protected List<String> courseIDs;
	
	@FXML
	private void loadAllCourses() {
        List<String> courseIDs = DataBaseHelper.getAllCourseIDs();
        
        for (int i = 0; i < 20; i++) {
        	TitledPane coursePane = new TitledPane();
        	coursePane.setText("Course ID: " + courseIDs.get(i));
        	coursesContainer.getChildren().add(coursePane);
        	List<Student> students = DataBaseHelper.getStudentsForCourse(courseIDs.get(i));
        	GridPane studentsGrid = new GridPane();
            studentsGrid.setHgap(75); // Horizontal spacing
            studentsGrid.setVgap(100); // Vertical spacing
           
            // Fetch students for this Course ID
            
            for (int j = 0; j < students.size(); j++) {
            	VBox studentCard = new VBox();
            	Label studentID = new Label(students.get(j).getStudentID());
            	Label studentAttendance = new Label(Double.toString(students.get(j).getAttendancePercentage()));
            	studentCard.getChildren().add(studentID);
            	studentCard.getChildren().add(studentAttendance);
            	studentsGrid.add(studentCard, j%5, j/5);
            	System.out.println(students.get(j).getStudentID() + "-----" +students.get(j).getAttendancePercentage());
    		}
            
            coursePane.setContent(studentsGrid);
            scrollablePane.setVvalue(1.0);
		}
        
        
	}    
	
	
	
	@FXML	
	private void filterClicked() {
		
		System.out.println("filterclicked");
		System.out.println(filterOpen);
		if (filterOpen == false) {
			
			System.out.println(courseIDs);
			
			if (courseIDs == null) {
				System.out.println("new");
				courseIDs = DataBaseHelper.getAllCourseIDs();
		    	
		    	GridPane coursesGrid = new GridPane();
		    	coursesGrid.setHgap(15); // Horizontal spacing
		    	coursesGrid.setVgap(0); // Vertical spacing
		    	for (int i = 0; i < courseIDs.size(); i++) {
		    		
		    		CheckBox courseCheckBox = new CheckBox();
		    		
		    		if (courseIDs.get(i).length()>10) {
		    			
		    			courseCheckBox.setText(courseIDs.get(i).substring(0,10) + "...");
		    			courseCheckBox.setId(courseIDs.get(i));
					}
		    		else {
		    			courseCheckBox.setText(courseIDs.get(i));
		    			courseCheckBox.setId(courseIDs.get(i));
					}
		    		checkboxes.add(courseCheckBox);
		        	coursesGrid.add(courseCheckBox, i%3, i/3);
		       
				}
		    	courseIDFilter.setContent(coursesGrid);
			}
			
			
			
	    	
	    	filterOverlay.setVisible(true);
	    	filterOpen = true;
		}
		else {
			filterOverlay.setVisible(false);
			filterOpen = false;
		}
    }
	
	@FXML
	private void applyFilterClicked() {
		System.out.println(checkboxes);

	}
	
	
	
	
	
}