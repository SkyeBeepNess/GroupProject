package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//javaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
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
import services.UIServices;
import session.UserSession;

public class AttendanceStudentController {
	@FXML private Button homeButton;
	@FXML private Button backToOverviewButton;
	@FXML private Button editAttendanceButton;
	@FXML private Button applyButton;
	@FXML private Button cancelButton;
	@FXML private Button editSelectionButton;
	@FXML private Button applyButtonNewRecord;
	@FXML private Button cancelButtonNewRecord;
	@FXML private Button deleteRecordButton;
	
	@FXML private ProgressIndicator studentAttendancePercentageIndicator;
			
	@FXML private TableView<StudentAttendance> sessionsTable;
	@FXML private TableColumn<StudentAttendance, String> sessionDateColumn;
	@FXML private TableColumn<StudentAttendance, String> attendanceColumn;
	
	@FXML private TableView<StudentAttendance> editableSessionsTable;
	@FXML private TableColumn<StudentAttendance, String> sessionDateColumnEditable;
	@FXML private TableColumn<StudentAttendance, String> attendanceColumnEditable;

	@FXML private Text absentNum;
	@FXML private Text lateNum;
	@FXML private Text presentNum;
	@FXML private Text studentNameField;
	@FXML private Text studentIDField;
	@FXML private Text courseIDField;
	@FXML private Text sessionDateText;
	
	@FXML private StackPane attendanceEditOverlay;
	@FXML private StackPane editRowOverlay;
	@FXML private StackPane addNewRecordOverlay;
	
	@FXML private RadioButton rbPresent;
	@FXML private RadioButton rbLate;
	@FXML private RadioButton rbAbsent;
	
	@FXML private RadioButton rbPresentNew;
	@FXML private RadioButton rbLateNew;
	@FXML private RadioButton rbAbsentNew;
	
	@FXML private DatePicker newSessionDate;
	
	
    private ObservableList<StudentAttendance> data = FXCollections.observableArrayList();
    private final ObservableList<StudentAttendance> editableData = FXCollections.observableArrayList();

	private static UserSession userSession;
	private static Student student;
	public final DataBaseHelper dbHelper = new DataBaseHelper();

	public AttendanceStudentController() {
		// TODO Auto-generated method stub
		this.userSession = UserSession.getInstance();
		if (userSession.getRole().contentEquals("admin")||userSession.getRole().contentEquals("superadmin")) {
			System.out.println("test");
			System.out.println(userSession.getSelectedStudentId());
			this.student = dbHelper.getStudentByUserID(dbHelper.getUserIDByStudentID(userSession.getSelectedStudentId()));
			System.out.println(student.getAttendancePercentage(null, null));
			
		}
		else {
			this.student = dbHelper.getStudentByUserID(userSession.getUserId());
		}
		
	}

	
	@FXML
	private void initialize() {
		data.clear();
		editableData.clear();
		if (userSession.getRole().contentEquals("admin")||userSession.getRole().contentEquals("superadmin")) {
			backToOverviewButton.setVisible(true);
			backToOverviewButton.setDisable(false);
			editAttendanceButton.setVisible(true);
			this.student = dbHelper.getStudentByUserID(dbHelper.getUserIDByStudentID(userSession.getSelectedStudentId()));
			
		}
		editableSessionsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedAttendance) -> {
	        if (selectedAttendance != null) {
	        	editSelectionButton.setDisable(false);
	        	deleteRecordButton.setDisable(false);
	            System.out.println("Selected session: " + selectedAttendance);
	            
	        }
	        else {
	        	deleteRecordButton.setDisable(true);

	        	editSelectionButton.setDisable(true);
			}
	    });
		
		newSessionDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				applyButtonNewRecord.setDisable(false);
			}
		});
		
		studentNameField.setText(student.getName());
		studentIDField.setText(" ("+student.getStudentID()+")");
		courseIDField.setText(student.getCourseID());
		
		double attPerc = student.getAttendancePercentage(null, null);
		int red = (int) (255 - (attPerc * 2.55));  
        int green = (int) (attPerc * 2.55);

		studentAttendancePercentageIndicator.setProgress(attPerc/100.0);
		studentAttendancePercentageIndicator.setStyle("-fx-progress-color: rgb("+ red +"," + green +",0);");
		Map<String, Integer> frequencyMap = countFrequencies(student.getAttendanceRecords());
		if (frequencyMap.get("Absent") != null) {
			absentNum.setText(frequencyMap.get("Absent").toString());
		}
		else {
			absentNum.setText("0");
		}
		if (frequencyMap.get("Late") != null) {
			lateNum.setText(frequencyMap.get("Late").toString());
		}
		else {
			lateNum.setText("0");
		}
		if (frequencyMap.get("Yes") != null) {
			presentNum.setText(frequencyMap.get("Yes").toString());
		}
		else {
			presentNum.setText("0");
		}
		
		
		
		
        sessionDateColumn.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("attendance"));
        sessionDateColumnEditable.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        attendanceColumnEditable.setCellValueFactory(new PropertyValueFactory<>("attendance"));

        for (Map.Entry<LocalDate, String> entry : student.getAttendanceRecords().entrySet()) {
        	String key = entry.getKey().toString();
        	String val = entry.getValue();
        	
        	if (val.contentEquals("Yes")) {
				val = "Present";
        	}
        	addRow(key, val);
		}
        sessionsTable.setItems(data);
        editableSessionsTable.setItems(editableData);
        
        
	}
	
	public void addRow(String key, String attendance) {
	        data.add(new StudentAttendance(key, attendance));
	        editableData.add(new StudentAttendance(key, attendance));
	        
	    }
	public void editRow(int key, StudentAttendance attendance) {
        
        editableData.set(key, attendance);
        
    }
	public void deleteRow(StudentAttendance selectedSession) {
        
        editableData.remove(selectedSession);
        
    }

    public static Map<String, Integer> countFrequencies(Map<?, String> map) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        for (String value : map.values()) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }

        return frequencyMap;
    }
	public class StudentAttendance {
	    private  SimpleStringProperty sessionDate;
	    private  SimpleStringProperty attendance;

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

		public void setAttendance(String text) {
			this.attendance = new SimpleStringProperty(text);
		}
	}
	
	@FXML
	private void openEditOverlay() {
		initialize();
		attendanceEditOverlay.setVisible(true);
	}
	@FXML
	private void closeEditOverlay() {
		attendanceEditOverlay.setVisible(false);

	}
	@FXML
	private void saveChanges() {
		dbHelper.updateAttendanceForStudent(student, editableData);

		initialize();
        UIServices.showAlert(AlertType.CONFIRMATION, "Success!", "Changes saved");

		System.out.println("changes saved");
		attendanceEditOverlay.setVisible(false);

	}
	@FXML
	private void navigateBackToOverview() {
		NavigationService.navigateTo("attendancePageAdmin.fxml", "Home");
	}


	@FXML
    private void onHomeClicked() {
    	NavigationService.navigateTo("HomePage.fxml", "Home");
    }
	
	
	@FXML
	private void handleEditSelectedAttendance() {
        // Get the currently selected row.
        editRowOverlay.setVisible(true);

		StudentAttendance selectedSession = editableSessionsTable.getSelectionModel().getSelectedItem();

    	sessionDateText.setText(selectedSession.getSessionDate());

        // Create radio buttons for attendance choices.
        ToggleGroup attendanceGroup = new ToggleGroup();

        rbPresent.setToggleGroup(attendanceGroup);
        rbLate.setToggleGroup(attendanceGroup);
        rbAbsent.setToggleGroup(attendanceGroup);

        // Set the radio button selection based on the selected row.
        String currentAttendance = selectedSession.getAttendance();
        if ("Present".equals(currentAttendance)) {
            rbPresent.setSelected(true);
        } else if ("Late".equals(currentAttendance)) {
            rbLate.setSelected(true);
        } else if ("Absent".equals(currentAttendance)) {
            rbAbsent.setSelected(true);
        }


        // Cancel button: just close the dialog.
        cancelButton.setOnAction(e -> editRowOverlay.setVisible(false));

        // Apply button: update the selected session and close the dialog.
        applyButton.setOnAction(e -> {
            // Update the session's date.
        	System.out.println("Test");
            // Update the session's attendance.
            RadioButton selectedRadio = (RadioButton) attendanceGroup.getSelectedToggle();
            if (selectedRadio != null) {
            	editRow(editableSessionsTable.getSelectionModel().getSelectedIndex(), new StudentAttendance(selectedSession.getSessionDate(), selectedRadio.getText()) );
                //selectedSession.setAttendance(selectedRadio.getText());
            }
            editRowOverlay.setVisible(false);
            System.out.println(editableData);
        });

    }
	@FXML
	private void handleAddRecord() {
        // Get the currently selected row.
		addNewRecordOverlay.setVisible(true);

        // Create radio buttons for attendance choices.
        ToggleGroup newAttendanceGroup = new ToggleGroup();

        rbPresentNew.setToggleGroup(newAttendanceGroup);
        rbLateNew.setToggleGroup(newAttendanceGroup);
        rbAbsentNew.setToggleGroup(newAttendanceGroup);

        // Set the radio button selection based on the selected row.
        rbPresentNew.setSelected(true);


        // Cancel button: just close the dialog.
        cancelButtonNewRecord.setOnAction(e -> addNewRecordOverlay.setVisible(false));
        if (newSessionDate.getValue() != null) {
			applyButtonNewRecord.setDisable(false);
		}
        
        applyButtonNewRecord.setOnAction(e -> {
            // Update the session's date.
        	System.out.println("Test");
            // Update the session's attendance.
            RadioButton selectedRadio = (RadioButton) newAttendanceGroup.getSelectedToggle();
            LocalDate newDate = newSessionDate.getValue();
            if (selectedRadio != null) {
            	addRow(newDate.toString(), selectedRadio.getText());
                //selectedSession.setAttendance(selectedRadio.getText());
            }
            addNewRecordOverlay.setVisible(false);
            System.out.println(editableData);
        });

    }
	
	@FXML
	private void handleDeleteRecord() {
		if (UIServices.showConfirmation("Are you sure?", "Are you sure you want to delete this record?", "Yes", "No").getText().contentEquals("Yes")) {
			StudentAttendance selectedSession = editableSessionsTable.getSelectionModel().getSelectedItem();
			deleteRow(selectedSession);
		}

		
	}
	
}