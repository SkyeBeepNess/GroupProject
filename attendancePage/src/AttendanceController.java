package attendancePage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.util.List;


public class AttendanceController {
	private VBox coursesContainer;
	private void loadAllCourses() {
        List<String> courseIDs = DataBaseHelper.getAllCourseIDs();

        for (String courseID : courseIDs) {
            TitledPane coursePane = new TitledPane();
            coursePane.setText("Course ID: " + courseID);

            // Create a container for student details
            AnchorPane contentPane = new AnchorPane();
            VBox studentList = new VBox(10); // Vertical list of students

            // Fetch students for this Course ID
            List<Student> students = DataBaseHelper.getStudentsForCourse(courseID);
            for (Student student : students) {
                Label studentLabel = new Label(student.getStudentID() + ") - " + student.getAttendanceStatus());
                studentLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #f0f0f0; -fx-border-color: gray;");
                studentList.getChildren().add(studentLabel);
            }

            // Add student list inside AnchorPane
            contentPane.getChildren().add(studentList);
            AnchorPane.setTopAnchor(studentList, 5.0);
            AnchorPane.setLeftAnchor(studentList, 5.0);

            // Set the content inside TitledPane
            coursePane.setContent(contentPane);

            // Add TitledPane to the VBox
            coursesContainer.getChildren().add(coursePane);
        }
    }
}