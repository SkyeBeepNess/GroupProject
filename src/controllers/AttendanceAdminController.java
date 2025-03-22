package controllers;

//javaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
//java imports
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//methods imports
import dbhandlers.DataBaseHelper;
import models.Admin;
import models.Student;
import services.NavigationService;
import session.UserSession;

public class AttendanceAdminController {
	@FXML private Button homeButton;
	@FXML private Button searchButton;
	@FXML private Button filterButton;
	
	@FXML private DatePicker startDate;
	@FXML private DatePicker endDate;
	
	@FXML private ScrollPane courseIDFilter;
	@FXML private ScrollPane scrollablePane;
	
	@FXML private VBox coursesContainer;
	@FXML private VBox selectedFileBox;
	@FXML private TextField searchTextField;
	
	@FXML private StackPane filterOverlay;
	@FXML private StackPane fileUploadOverlay;

	@FXML private Hyperlink browseLink;
	
	@FXML private Text fileName;
	@FXML private Text fileSize;
	GridPane coursesGrid = new GridPane();
	private List<String> courseIDs;
	private ArrayList<CheckBox> checkboxes = new ArrayList<>();
	private boolean filterOpen = false;
	private ArrayList<String> checkedList = new ArrayList<String>();
	private ArrayList<LocalDate> datesList = new ArrayList<LocalDate>();
	private File selectedFile;
	public final DataBaseHelper dbHelper = new DataBaseHelper();
	private static UserSession userSession;
	private Admin currentAdmin;
	
	@FXML
	private void initialize() {
		this.userSession = UserSession.getInstance();
		this.currentAdmin = new Admin(userSession.getUserId(), dbHelper.getManagedCourses(userSession.getUserId()), userSession.getRole());
		System.out.println(currentAdmin.getIsSuper());
		courseIDs = currentAdmin.getManagedCourses();
		endDate.setValue(LocalDate.now());    	
    	coursesGrid.setHgap(15); // Horizontal spacing
    	coursesGrid.setVgap(0); // Vertical spacing
    	
    	//2 -- 0.0921057
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
    		GridPane.setColumnIndex(courseCheckBox, i % 4);
            GridPane.setRowIndex(courseCheckBox, i / 4);
		}
    	coursesGrid.getChildren().addAll(checkboxes);
		courseIDFilter.setContent(coursesGrid);
	}
	
    @FXML
    private void handleSearch() { //Handles the press of the search button, checks if any filteres were applied and if any text has been inputed into the search field
        String searchInput = searchTextField.getText().trim();
        boolean filtersApplied = !checkedList.isEmpty() || !datesList.isEmpty();
        if (searchInput.isEmpty() && !filtersApplied) {
            coursesContainer.getChildren().clear();
        } else if (!searchInput.isEmpty() && !filtersApplied) {
            loadCourses(null, null, searchInput);
        } else if (!searchInput.isEmpty() && filtersApplied) {
            loadCourses(checkedList, datesList, searchInput);
        }
    }
	
    @FXML
    private void handleApplyFilters() { //Handles the press of the applied filters button, checks and stores applied filter, checks if search input was provided
    	datesList.clear();
    	coursesContainer.getChildren().clear();
    	System.out.println("test");
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
    	System.out.println(checkedList);
    	if (startDate.getValue() != null) {
			datesList.add(startDate.getValue());
			datesList.add(endDate.getValue());
		}
        boolean filtersApplied = !checkedList.isEmpty() || !datesList.isEmpty();
        boolean searchDone = !searchTextField.getText().trim().isEmpty();
        
        if (!filtersApplied && !searchDone) {
            coursesContainer.getChildren().clear();
        } else if (!datesList.isEmpty() && !checkedList.isEmpty() && !searchDone) {
            loadCourses(checkedList, datesList, null);
        } else if (!checkedList.isEmpty() && datesList.isEmpty() && !searchDone) {
            loadCourses(checkedList, null, null);
        } else if (searchDone && !checkedList.isEmpty() && datesList.isEmpty()) {
            loadCourses(checkedList, null, searchTextField.getText());
            System.out.println("HAHAHAHAHAH");
        } else if (searchDone) {
            loadCourses(checkedList, datesList, searchTextField.getText());
        }
        filterOverlay.setVisible(false);
		filterOpen = false;
    }
	
    private void loadCourses(List<String> coursesList, List<LocalDate> dateRange, String searchInput) { //Method that actually loads the attendance data, passes any filters/search to the dbHandler
        coursesContainer.getChildren().clear();
        System.out.println(coursesList.isEmpty());
        List<String> courseIDs = (coursesList.isEmpty()) ? currentAdmin.getManagedCourses() : coursesList;
        System.out.println("------");
        System.out.println(courseIDs);
        System.out.println("------");
        List<Student> students = dbHelper.getStudentsForCourse(courseIDs, dateRange, searchInput);
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
            coursePane.setPrefWidth(600);
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
			
			
	    	filterOverlay.setVisible(true);
	    	filterOpen = true;
		}
		else {
			filterOverlay.setVisible(false);
			filterOpen = false;
		}
		
    }	
	@FXML
    private void onHomeClicked() {
    	NavigationService.navigateTo("HomePage.fxml", "Home");
    }
    
    @FXML
    private void openImportCSVOverlay() {
    	selectedFile = null;
		fileUploadOverlay.setVisible(true);
    	
	}
    @FXML
    private void cancelCSVImport() {
    	selectedFile = null;
    	fileUploadOverlay.setVisible(false);
    	selectedFileBox.setVisible(false);
	}
    @FXML
    private void handleBrowseLink() {
        // Open the file chooser here
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File(s)");

        // Optionally add extension filters (e.g., CSV, XLS)
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("Excel Files", "*.xls", "*.xlsx")
        );

        // Show the file chooser
        // Use the hyperlink’s scene/window as the parent for the dialog
        Window window = browseLink.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            handleFileSelected(selectedFile);
        }
    }
    
    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }
    
    @FXML
    private void handleDragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
        	if (db.getFiles().size()>1) {
				System.out.println("TOO MUCH FILES AAAAAAAAA");
			}
        	else {
        		// Retrieve the first file from the list (you can modify this to handle multiple files)
            	selectedFile = db.getFiles().get(0);
            	String fileName = selectedFile.getName().toLowerCase();
                if (fileName.endsWith(".csv") || fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    handleFileSelected(selectedFile);
                } else {
                    System.out.println("Unsupported file format: " + fileName);
                }
			}
            
        }
        
        // Indicate whether the drop was successfully completed
        event.setDropCompleted(db.hasFiles());
        event.consume();
    }
    
    
    @FXML
    private void handleFileSelected(File selectedFile) {
    	selectedFileBox.setVisible(true);
		fileName.setText(selectedFile.getName());
		fileSize.setText("File size: " + selectedFile.length()/1000 + "KB");

	}
    
    @FXML
    private void handleSubmitFiles() {
    	if (selectedFile != null) {
    		System.out.println("hihihi");
    		//System.out.println(dbHelper.addAttendanceRecord(selectedFile));
		}
		

	}
}