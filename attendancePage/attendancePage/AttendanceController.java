package attendancePage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;




public class AttendanceController {
	@FXML private Button homeButton;
	@FXML protected VBox coursesContainer;
	@FXML protected ScrollPane scrollablePane;
	@FXML protected Button filterButton;
	@FXML protected StackPane filterOverlay;
	@FXML protected ScrollPane courseIDFilter;
	@FXML protected DatePicker startDate;
	@FXML protected DatePicker endDate;
	
	protected List<String> courseIDs;
	protected ArrayList<CheckBox> checkboxes = new ArrayList<>();
	private boolean filterOpen = false;
	
	
	private void loadCourses(ArrayList<String> coursesList, List<LocalDate> dateCouple) {
		ArrayList<String> courseIDs = new ArrayList<String>(coursesList);
		
	    coursesContainer.getChildren().clear();
		for (int i = 0; i < courseIDs.size(); i++) {
        	TitledPane coursePane = new TitledPane();
        	coursePane.setText("Course ID: " + courseIDs.get(i));
        	coursePane.setExpanded(false);
        	coursesContainer.getChildren().add(coursePane);
        	List<Student> students = DataBaseHelper.getStudentsForCourse(courseIDs.get(i), dateCouple);
        	GridPane studentsGrid = new GridPane();
            studentsGrid.setHgap(75); // Horizontal spacing
            studentsGrid.setVgap(100); // Vertical spacing
           
            // Fetch students for this Course ID
            
            for (int j = 0; j < students.size(); j++) {
            	VBox studentCard = new VBox();
            	Label studentID = new Label(students.get(j).getStudentID());
            	Label studentAttendance = new Label(Double.toString(students.get(j).getAttendancePercentageByDate(dateCouple)));
            	studentCard.getChildren().add(studentID);
            	studentCard.getChildren().add(studentAttendance);
            	studentsGrid.add(studentCard, j%5, j/5);
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
				endDate.setValue(LocalDate.now());
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
		ArrayList<LocalDate> datesList = new ArrayList<LocalDate>();
		ArrayList<String> checkedList = new ArrayList<String>();
		for (int i = 0; i < checkboxes.size(); i++) {
			if (checkboxes.get(i).isSelected()) {
				checkedList.add(checkboxes.get(i).getId());
			}
		}
		
		//System.out.println(startDate.getValue().getClass());
		if (startDate.getValue() != null ) {
			datesList.add(startDate.getValue());
		}

		if (endDate.getValue() != null) {
			datesList.add(endDate.getValue());
		}
		if (datesList.size() == 2) {
			loadCourses(checkedList, datesList);
		}
		else {
			loadCourses(checkedList, null);
		}
		filterOverlay.setVisible(false);
		filterOpen = false;
	}

}