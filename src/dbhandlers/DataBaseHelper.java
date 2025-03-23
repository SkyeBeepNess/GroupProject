package dbhandlers;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.AttendanceStudentController;
import controllers.AttendanceStudentController.StudentAttendance;
import javafx.collections.ObservableList;
import models.Admin;
import models.Student;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DataBaseHelper {
	private static Connection connection;

    public DataBaseHelper() {
        this.connection = DataBaseManager.getInstance().getConnection();
    }
	
    public static List<String> getManagedCourses(String userID) {
    	List<String> courseIDs = new ArrayList<>();
        String sql = "SELECT Courses FROM courses_access_permissions WHERE UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())){
			stmt.setObject(1, userID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
                courseIDs.addAll(Arrays.asList(rs.getString("Courses").split(",")));
			}
			
		} catch (Exception e) {
            e.printStackTrace();
		}
        return courseIDs;

	}

    public static List<String> getAllCourseIDs() {
        List<String> courseIDs = new ArrayList<>();
        String sql = "SELECT DISTINCT CourseID FROM attendance";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courseIDs.add(rs.getString("CourseID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseIDs;
    }    
    
    public static Student getStudentByUserID(String userID) {
    	StringBuilder sql = new StringBuilder("SELECT a.UserID, u.Name, a.CourseID, a.StudentID, a.Attendance, a.SessionDate FROM attendance a JOIN users u ON a.UserID = u.UserID WHERE a.UserID == ?");
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	Student student = null;
    	try (
                PreparedStatement stmt = connection.prepareStatement(sql.toString())) {  
                stmt.setObject(1, userID);
                
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String studentID = rs.getString("StudentID");
                    String courseID = rs.getString("CourseID");
                    String name = rs.getString("Name");
                    student = new Student(studentID, courseID, name);
				}
                do {
                	
                    String attendance = rs.getString("Attendance");
                    
                    LocalDate sessionDate = LocalDate.parse(rs.getString("SessionDate"), formatter);
                    student.addAttendance(sessionDate, attendance);
                    
                } while (rs.next());
            } catch (SQLException e) {
                e.printStackTrace();
            }
    	return student;
	}
    
    public static String getUserIDByStudentID(String StudentID) {
    	StringBuilder sql = new StringBuilder("SELECT DISTINCT UserID FROM attendance WHERE StudentID == ?");
    	String userID = null;
    	try (
                PreparedStatement stmt = connection.prepareStatement(sql.toString())) {  
                stmt.setObject(1, StudentID);
                
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    userID = rs.getString("UserID");

				}
            } catch (SQLException e) {
                e.printStackTrace();
            }

		return userID;
	}
    
    public static List<Student> getStudentsForCourse(List<String> courseIDs, List<LocalDate> dateRange, String searchInput) {
        Map<String, Student> studentMap = new HashMap<>();
        StringBuilder sql = new StringBuilder("SELECT a.UserID, u.Name, a.CourseID, a.StudentID, a.Attendance, a.SessionDate FROM attendance a JOIN users u ON a.UserID = u.UserID");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Object> parameters = new ArrayList<>();

        // ✅ Add CourseID filtering dynamically
        if (courseIDs != null && !courseIDs.isEmpty()) {
            sql.append(" WHERE CourseID IN (");
            sql.append(String.join(",", Collections.nCopies(courseIDs.size(), "?")));
            sql.append(")");
            parameters.addAll(courseIDs);
        }

        // ✅ Add searchInput condition if provided
        if (searchInput != null) {
            sql.append(parameters.isEmpty() ? " WHERE (" : " AND (").append("a.StudentID LIKE ? OR u.Name LIKE ?)");
            parameters.add("%" + searchInput + "%");
            parameters.add("%" + searchInput + "%");
        }

        // ✅ Add date range condition if provided
        if (dateRange != null && dateRange.size() == 2) {
        	
            sql.append(parameters.isEmpty() ? " WHERE " : " AND ").append("DATE(substr(SessionDate, 7, 4) || '-' || substr(SessionDate, 4, 2) || '-' || substr(SessionDate, 1, 2)) BETWEEN ? AND ?");
            parameters.add(String.valueOf(dateRange.get(0)));
            parameters.add(String.valueOf(dateRange.get(1)));
        }
        
        try (
            PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
            	String courseID = rs.getString("courseID");
                String studentID = rs.getString("StudentID");
                String attendance = rs.getString("Attendance");
                String name = rs.getString("Name");
                LocalDate sessionDate = LocalDate.parse(rs.getString("SessionDate"), formatter);

                studentMap.computeIfAbsent(studentID, k -> new Student(studentID, courseID, name)).addAttendance(sessionDate, attendance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(studentMap.values());
    }
    
  
    
    public static Map<LocalDate, String> getAttendanceRecords(String studentID) {
        Map<LocalDate, String> records = new HashMap<>();
        String sql = "SELECT SessionDate, Attendance FROM attendance WHERE StudentID = ?";

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.put(rs.getDate("SessionDate").toLocalDate(), rs.getString("Attendance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public static List<String> getManagedStudents(List<String> managedCourses) {
    	List<String> managedStudents = new ArrayList<>();
    	StringBuilder sql = new StringBuilder("SELECT StudentID FROM attendance");
    	List<Object> parameters = new ArrayList<>();
        sql.append(" WHERE CourseID IN (");
        sql.append(String.join(",", Collections.nCopies(managedCourses.size(), "?")));
        sql.append(")");
        parameters.addAll(managedCourses);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())){
        	for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
			ResultSet rs = stmt.executeQuery();
			
			
			while (rs.next()) {
				managedStudents.add(rs.getString("StudentID"));
			}
			
		} catch (Exception e) {
            e.printStackTrace();
		}
        return managedStudents;
	}

    public static int checkUploadedFile(File importedFile, List<String> managedCourses) {
    	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	List<String> managedStudents = getManagedStudents(managedCourses);
    	
        try (BufferedReader br = new BufferedReader(new FileReader(importedFile))) {
            String line;
            int lineNumber = 1;
            boolean foundData = false;
            
            // Process each line of the file
            while ((line = br.readLine()) != null) {
                // Skip blank lines
                if (line.trim().isEmpty()) {
                    lineNumber++;
                    continue;
                }
                
                foundData = true;
                
                

                String[] tokens = line.split(",");
                
                // Check that the line has exactly 4 columns
                if (tokens.length != 4) {
                    System.err.println("Line " + lineNumber + " has " + tokens.length + " columns; expected 4.");
                    return 100;
                }
                
                // Validate Student ID (first column): should not be empty
                String studentID = tokens[0].trim();
                
                
                if (studentID.isEmpty()) {
                    System.err.println("Line " + lineNumber + " has an empty Student ID.");
                    return 101;
                }
                else if (managedStudents.contains(studentID) == false) {
                	System.err.println("Line " + lineNumber + " has a wrong Student ID.");
                    return 101;
				}
                
                // Validate Course Code (second column): should not be empty
                String courseCode = tokens[1].trim();
                if (courseCode.isEmpty()) {
                    System.err.println("Line " + lineNumber + " has an empty Course Code.");
                    return 102;
                }
                else if (managedCourses.contains(courseCode) == false) {
                	System.err.println("Line " + lineNumber + " has an wrong Course Code.");
                    return 102;
				}
                
                // Validate Date (third column): must follow the specified date format
                String dateStr = tokens[2].trim();
                try {
                    LocalDate.parse(dateStr, dateFormatter);
                } catch (DateTimeParseException e) {
                    System.err.println("Line " + lineNumber + " has an invalid date: " + dateStr);
                    return 103;
                }
                
                // Validate Attendance Status (fourth column): must be either "Yes" or "Late"
                String attendanceStatus = tokens[3].trim();
                if (!attendanceStatus.equalsIgnoreCase("Yes") && !attendanceStatus.equalsIgnoreCase("Late") && !attendanceStatus.equalsIgnoreCase("Absent")) {
                    System.err.println("Line " + lineNumber + " has an invalid attendance status: " + attendanceStatus);
                    return 103;
                }
                
                lineNumber++;
            }
            
            if (!foundData) {
                System.err.println("The file is empty.");
                return 0;
            }
            
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }    	
    }    
    
    public static boolean uploadCSVtoDB(File importedFile) {

    	String sql = "INSERT INTO attendance (StudentID, CourseID, SessionDate, Attendance, UserID) VALUES (?, ?, ?, ?, ?)";
    	try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
    		connection.setAutoCommit(false);
    		try (BufferedReader lineReader = new BufferedReader(new FileReader(importedFile))) {
                String lineText;
                int count = 0;
                // Skip header if present

                while ((lineText = lineReader.readLine()) != null) {
                	if (lineText.trim().isEmpty() == false) {
                		String[] data = lineText.split(",");
                        // If the CSV has exactly 3 columns per row, otherwise adjust accordingly.
                       
                        stmt.setString(1, data[0]);
                        stmt.setString(2, data[1]);
                        stmt.setString(3, data[2]);
                        stmt.setString(4, data[3]);
                        stmt.setString(5, getUserIDByStudentID(data[0]));
                        stmt.addBatch();
                        count++;
					}
                	
                }
                stmt.executeBatch();
                connection.commit();
                System.out.println("Inserted " + count + " rows.");
            } catch (IOException ex) {
                System.err.println("Error reading CSV file");
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Database error");
            e.printStackTrace();
        }
		return true;

	}
    
    public static void updateAttendanceForStudent(Student student, ObservableList newAttendance) {
    	String deleteSQL = "DELETE FROM attendance WHERE StudentID = ?";
    	String insertSQL = "INSERT INTO attendance (StudentID, CourseID, SessionDate, Attendance, UserID) VALUES (?, ?, ?, ?, ?)";
    	DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    	String studentsUserID = DataBaseHelper.getUserIDByStudentID(student.getStudentID());
    	try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
            deleteStmt.setString(1, student.getStudentID());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
        	for (Object object : newAttendance) {
        		
                
                
                // Formatter for the input string
                
                // Parse the string into a LocalDate
                LocalDate date = LocalDate.parse(((StudentAttendance) object).getSessionDate(), inputFormatter);
                
                // Formatter for the desired output format
                
                // Format the LocalDate to the desired string format
                String output = date.format(outputFormatter);
        		insertStmt.setString(1, student.getStudentID());
        		insertStmt.setString(2, student.getCourseID());
        		insertStmt.setString(3, output);
        		if (((StudentAttendance) object).getAttendance().contentEquals("Present")) {
        			insertStmt.setString(4, "Yes");
				}
        		else {
            		insertStmt.setString(4, ((StudentAttendance) object).getAttendance());
				}
        		insertStmt.setString(5, studentsUserID);
                insertStmt.addBatch();
			}
        	
            
            insertStmt.executeBatch();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
