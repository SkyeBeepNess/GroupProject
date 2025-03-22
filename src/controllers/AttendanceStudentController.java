package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//javaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

//java imports
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//methods imports
import dbhandlers.DataBaseHelper;
import models.Student;
import services.NavigationService;
import session.UserSession;

public class AttendanceStudentController {
	@FXML private Button homeButton;
	
	@FXML private ProgressIndicator studentAttendancePercentageIndicator;
	@FXML private TableView<StudentAttendance> sessionsTable;
	@FXML private TableColumn<StudentAttendance, String> sessionDateColumn;
	@FXML private TableColumn<StudentAttendance, String> attendanceColumn;
	
	@FXML private Text absentNum;
	@FXML private Text lateNum;
	@FXML private Text presentNum;
	
    private final ObservableList<StudentAttendance> data = FXCollections.observableArrayList();

	private static UserSession userSession;
	private static Student student;
	public final DataBaseHelper dbHelper = new DataBaseHelper();

	public AttendanceStudentController() {
		// TODO Auto-generated method stub
		this.userSession = UserSession.getInstance();
		this.student = dbHelper.getStudentByUserID(userSession.getUserId());
	}
	
	
	@FXML
	private void initialize() {
		double attPerc = student.getAttendancePercentage(null, null);
		int red = (int) (255 - (attPerc * 2.55));  
        int green = (int) (attPerc * 2.55);
        ArrayList<String> testing = new ArrayList<String>();
        testing.add("hello");
        
		studentAttendancePercentageIndicator.setProgress(attPerc/100.0);
		studentAttendancePercentageIndicator.setStyle("-fx-progress-color: rgb("+ red +"," + green +",0);");
		Map<String, Integer> frequencyMap = countFrequencies(student.getAttendanceRecords());
		
		absentNum.setText(frequencyMap.get("Absent").toString());
		lateNum.setText(frequencyMap.get("Late").toString());
		presentNum.setText(frequencyMap.get("Yes").toString());
		
        sessionDateColumn.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("attendance"));
        

        for (Map.Entry<LocalDate, String> entry : student.getAttendanceRecords().entrySet()) {
        	String key = entry.getKey().toString();
        	String val = entry.getValue();
        	
        	if (val.contentEquals("Yes")) {
				val = "Present";
        	}
        	addRow(key, val);
		}
        sessionsTable.setItems(data);
		System.out.println(sessionsTable.getColumns());
        
	}
	
	public void addRow(String key, String attendance) {
	        data.add(new StudentAttendance(key, attendance));
	    }
    public static Map<String, Integer> countFrequencies(Map<?, String> map) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String value : map.values()) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }

        return frequencyMap;
    }
	public class StudentAttendance {
	    private final SimpleStringProperty sessionDate;
	    private final SimpleStringProperty attendance;

	    public StudentAttendance(String key, String attendance) {
	        this.sessionDate = new SimpleStringProperty(key);
	        this.attendance = new SimpleStringProperty(attendance);
	    }

	    public String getSessionDate() {
	        return sessionDate.get();
	    }

	    public String getAttendance() {
	        return attendance.get();
	    }
	}

	


	@FXML
    private void onHomeClicked() {
    	NavigationService.navigateTo("HomePage.fxml", "Home");
    }
	
}