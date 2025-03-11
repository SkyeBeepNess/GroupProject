package attendancePage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ScrollPane;

import java.util.List;




public class AttendanceController {
	@FXML
	private Button homeButton;
	@FXML
	protected AnchorPane coursesContainer;
	@FXML
	protected ScrollPane scrollablePane;

	
	@FXML
	private void loadAllCourses() {
        List<String> courseIDs = DataBaseHelper.getAllCourseIDs();

        TitledPane coursePane = new TitledPane();
        
        coursePane.setText("Course ID: " + "BMS");
     
        coursesContainer.getChildren().add(coursePane);
      
        List<Student> students = DataBaseHelper.getStudentsForCourse("BMS");
        
        GridPane studentsGrid = new GridPane();
        studentsGrid.setHgap(75); // Horizontal spacing
        studentsGrid.setVgap(100); // Vertical spacing
        


        // Fetch students for this Course ID
        
        for (int i = 0; i < students.size(); i++) {
        	Label test = new Label(students.get(i).getStudentID());
        	studentsGrid.add(test, i%5, i/5);
		}
        
        coursePane.setContent(studentsGrid);
        scrollablePane.setVvalue(1.0);



    }
}