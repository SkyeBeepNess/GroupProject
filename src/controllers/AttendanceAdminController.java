package controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
//javaFX imports
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.Desktop;
import java.io.File;
//java imports
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//methods imports
import dbhandlers.DataBaseHelper;
import dbhandlers.DataBaseManager;
import models.Admin;
import models.Student;
import services.NavigationService;
import services.UIServices;
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
	public final DataBaseHelper dbHelper = new DataBaseHelper();
	private static UserSession userSession;
	private Admin currentAdmin;
	
	private String csvFilePath;
    private ObjectProperty<File> selectedFile = new SimpleObjectProperty<>(null);
  
	@FXML private Button submitFileButton;
	
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
		
		submitFileButton.setDisable(true);

        
        //FILE
        selectedFile.addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
                handleFileSelected();
                submitFileButton.setDisable(false);
            } else {
                selectedFileBox.setVisible(false);
                submitFileButton.setDisable(true);
            }
        });
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
	
    
    private void loadCourses(List<String> coursesList, List<LocalDate> dateRange, String searchInput) {
        coursesContainer.getChildren().clear();
        
        List<String> courseIDs = (coursesList == null || coursesList.isEmpty()  ) ? currentAdmin.getManagedCourses() : coursesList;

        List<Student> students = dbHelper.getStudentsForCourse(courseIDs, dateRange, searchInput);
        Map<String, List<Student>> studentsByCourse = new HashMap<>();

        for (Student student : students) {
            studentsByCourse
                .computeIfAbsent(student.getCourseID(), k -> new ArrayList<>())
                .add(student);
        }

        for (String courseID : studentsByCourse.keySet()) {
            double courseAttendance = 0.0;

            // Create and configure titled pane
            TitledPane coursePane = new TitledPane();
            coursePane.setExpanded(false);
            //coursePane.setMaxWidth(Double.MAX_VALUE);
            
            // Create left side with course title
            Label titleLabel = new Label();
            HBox leftSide = new HBox(titleLabel);
            leftSide.setAlignment(Pos.CENTER_LEFT);
            
            // Create right side with attendance info
            Label overallAttText = new Label("Selected attendance:");
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(100);
            Label percentLabel = new Label();
            HBox rightSide = new HBox(10, overallAttText, progressBar, percentLabel);
            rightSide.setAlignment(Pos.CENTER_RIGHT);
            
            // Create main header container and add both sides
            HBox header = new HBox();
            header.setMaxWidth(Double.MAX_VALUE);
            header.getChildren().addAll(leftSide, rightSide);
            
            // Make left side take all available space to push right side to the end
            HBox.setHgrow(leftSide, Priority.ALWAYS);
            
            // Set the header as the graphic for the TitledPane
            coursePane.setGraphic(header);

            // Set title text
            String truncatedCourse = courseID.length() > 8 ? courseID.substring(0, 8) + "..." : courseID;
            titleLabel.setText("Course ID: " + truncatedCourse);

            // Create content grid
            
            GridPane studentsGrid = new GridPane();
            studentsGrid.setStyle("-fx-background-color: #D9D9D9;");
            studentsGrid.setHgap(15);
            studentsGrid.setVgap(15);
            
            studentsGrid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(studentsGrid, Priority.ALWAYS);
            
            List<Student> courseStudents = studentsByCourse.get(courseID);
            for (int j = 0; j < courseStudents.size(); j++) {
                Student student = courseStudents.get(j);

                VBox studentCard = new VBox();
                Label studentName = new Label(student.getName());
                
                studentCard.setStyle("-fx-background-radius: 20;"
                		+ "-fx-background-color: #FFFFFF;"
                		+ "-fx-padding: 20");
                String studentIDString = student.getStudentID();
                String truncatedID = studentIDString.length() > 12 ? studentIDString.substring(0, 12) + "..." : studentIDString;
                Label studentID = new Label(truncatedID);
                studentCard.setOnMouseClicked(event -> {
                    handleStudentClick(studentIDString); // or pass in whatever you need
                });
                double attendancePercentage = (dateRange != null && dateRange.size() == 2)
                        ? student.getAttendancePercentage(dateRange.get(0), dateRange.get(1))
                        : student.getAttendancePercentage(null, null);

                courseAttendance += attendancePercentage;

                Label attendance = new Label(Math.round(attendancePercentage * 10.0) / 10.0 + "%");
                studentCard.getChildren().addAll(studentName, studentID, attendance);

                studentsGrid.add(studentCard, j % 4, j / 4);
            }

            courseAttendance = courseStudents.isEmpty() ? 0 : courseAttendance / courseStudents.size();
            progressBar.setProgress(courseAttendance / 100);
            percentLabel.setText(Math.round(courseAttendance * 10.0) / 10.0 + "%");

            coursePane.setContent(studentsGrid);
            coursesContainer.getChildren().add(coursePane);

            // Scroll to bottom after each course is added
            scrollablePane.setVvalue(1.0);
        }
    }
	private void handleStudentClick(String studentIDString) {
		// TODO Auto-generated method stub
		userSession.setSelectedStudentId(studentIDString);
		NavigationService.navigateTo("attendancePageStudent.fxml", studentIDString);
		System.out.println(studentIDString);
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
    	selectedFile.set(null);
		fileUploadOverlay.setVisible(true);
    	
	}
    @FXML
    private void cancelCSVImport() {
    	selectedFile.set(null);
    	fileUploadOverlay.setVisible(false);
    	selectedFileBox.setVisible(false);
	}
   
    @FXML
    private void handleBrowseLink() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File(s)");

        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        Window window = browseLink.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            selectedFile.set(file);
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
            if (db.getFiles().size() > 1) {
                UIServices.showAlert(AlertType.ERROR, "File Import Error", "Please select only one file");
            } else {
                File file = db.getFiles().get(0);
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".csv")) {
                    selectedFile.set(file);
                } else {
                    System.out.println("Unsupported file format: " + fileName);
                }
            }
        }

        event.setDropCompleted(db.hasFiles());
        event.consume();
    }

    @FXML
    private void handleFileSelected() {
        selectedFileBox.setVisible(true);
        File file = selectedFile.get();
        fileName.setText(file.getName());
        fileSize.setText("File size: " + file.length() / 1000 + "KB");
    }

    @FXML
    private void handleSubmitFiles() {
        File file = selectedFile.get();
        if (file == null) {
            UIServices.showAlert(AlertType.ERROR, "File import error", "Please choose a file to upload");
            return;
        }
        if (dbHelper.checkUploadedFile(selectedFile.get(), currentAdmin.getManagedCourses()) ==1) {
			dbHelper.uploadCSVtoDB(selectedFile.get());
		}
        System.out.println(dbHelper.checkUploadedFile(selectedFile.get(), currentAdmin.getManagedCourses()));
        

    }

    
    @FXML
    private void onViewFileClicked() {
    	if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(selectedFile.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop not supported");
        }
    }
    
    @FXML
    private void onDeleteFileClicked() {
    	selectedFile.set(null);
    	selectedFileBox.setVisible(false);
    	
    }
}