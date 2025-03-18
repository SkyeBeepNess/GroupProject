package controllers;

//javaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//java imports
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//methods imports
import dbhandlers.DataBaseHelper;
import models.Student;

public class AttendanceController {
	@FXML private Button homeButton;
	@FXML private Button searchButton;
	@FXML private Button filterButton;
	
	@FXML private DatePicker startDate;
	@FXML private DatePicker endDate;
	
	@FXML private ScrollPane courseIDFilter;
	@FXML private ScrollPane scrollablePane;
	
	@FXML private VBox coursesContainer;
	@FXML private StackPane filterOverlay;
	@FXML private TextField searchTextField;
	
	private List<String> courseIDs;
	private ArrayList<CheckBox> checkboxes = new ArrayList<>();
	private boolean filterOpen = false;
	private ArrayList<String> checkedList = new ArrayList<String>();
	private ArrayList<LocalDate> datesList = new ArrayList<LocalDate>();
	
    @FXML
    private void handleSearch() { //Handles the press of the search button, checks if any filteres were applied and if any text has been inputed into the search field
        String searchInput = searchTextField.getText().trim();
        boolean filtersApplied = !checkedList.isEmpty() || !datesList.isEmpty();
        System.out.println("test");
        if (searchInput.isEmpty() && !filtersApplied) {
            coursesContainer.getChildren().clear();
        } else if (!searchInput.isEmpty() && !filtersApplied) {
        	System.out.println("test");
            loadCourses(null, null, searchInput);
        } else if (!searchInput.isEmpty() && filtersApplied) {
            loadCourses(checkedList, datesList, searchInput);
        }
    }
	
    @FXML
    private void handleApplyFilters() { //Handles the press of the applied filters button, checks and stores applied filter, checks if search input was provided
    	datesList.clear();
    	for (int i = 0; i < checkboxes.size(); i++) {
			if (checkboxes.get(i).isSelected()) {
				if (!checkedList.contains(checkboxes.get(i).getId())) {
					checkedList.add(checkboxes.get(i).getId());
				}
			}
			else {
				if (checkedList.contains(checkboxes.get(i).getId())) {
					checkedList.remove(checkboxes.get(i).getId());
				}
			}
		}
    	if (startDate.getValue() != null) {
			datesList.add(startDate.getValue());
			datesList.add(endDate.getValue());
		}
        boolean filtersApplied = !checkedList.isEmpty() || !datesList.isEmpty();
        boolean searchDone = !searchTextField.getText().trim().isEmpty();
        
        if (!filtersApplied && !searchDone) {
            coursesContainer.getChildren().clear();
        } else if (!datesList.isEmpty() && !checkedList.isEmpty() && !searchDone) {
        	System.out.println("HAHAHAHA");
            loadCourses(checkedList, datesList, null);
        } else if (!checkedList.isEmpty() && datesList.isEmpty() && !searchDone) {
            loadCourses(checkedList, null, null);
        } else if (searchDone) {
            loadCourses(checkedList, datesList, searchTextField.getText());
        }
        filterOverlay.setVisible(false);
		filterOpen = false;
    }
	
    private void loadCourses(List<String> coursesList, List<LocalDate> dateRange, String searchInput) { //Method that actually loads the attendance data, passes any filters/search to the dbHandler
        coursesContainer.getChildren().clear();
        List<String> courseIDs = (coursesList == null) ? DataBaseHelper.getAllCourseIDs() : coursesList;
        List<Student> students = DataBaseHelper.getStudentsForCourse(courseIDs, dateRange, searchInput);
        Map<String, List<Student>> studentsByCourse = new HashMap<>();
                
        for (Student student : students) {
            studentsByCourse.computeIfAbsent(student.getCourseID(), k -> new ArrayList<>()).add(student);
        }

        for (String courseID : studentsByCourse.keySet()) {
            TitledPane coursePane = new TitledPane();
            GridPane studentsGrid = new GridPane();
            List<Student> courseStudents = studentsByCourse.get(courseID);
            
            studentsGrid.setHgap(35);
            studentsGrid.setVgap(40);
            
            coursePane.setText("Course ID: " + courseID);
            coursePane.setExpanded(false);
            
            coursesContainer.getChildren().add(coursePane);

            for (int j = 0; j < courseStudents.size(); j++) {
                VBox studentCard = new VBox();
                Label studentName = new Label(courseStudents.get(j).getName());
                Label studentID = new Label(courseStudents.get(j).getStudentID());
                double attendancePercentage = (dateRange != null && dateRange.size() == 2) ?
                        courseStudents.get(j).getAttendancePercentage(dateRange.get(0), dateRange.get(1)) :
                        courseStudents.get(j).getAttendancePercentage(null, null);
                Label attendance = new Label(Double.toString(attendancePercentage));
                studentCard.getChildren().addAll(studentName, studentID, attendance);
                studentsGrid.add(studentCard, j % 4, j / 4);
            }

            coursePane.setContent(studentsGrid);
            scrollablePane.setVvalue(1.0);
        }
    }
	
	@FXML	
	private void filterClicked() {
		if (filterOpen == false) {
			if (courseIDs == null) {
				endDate.setValue(LocalDate.now());
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
		        	coursesGrid.add(courseCheckBox, i%4, i/4);
		       
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
}