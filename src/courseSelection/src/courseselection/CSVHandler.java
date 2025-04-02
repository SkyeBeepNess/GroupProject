package courseSelection.src.courseselection;

import java.io.*;
import java.util.*;

public class CSVHandler {

	public static List<Course> loadCourses(String path) {
	    List<Course> courses = new ArrayList<>();
	    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	        String line = br.readLine(); // Skip header

	        while ((line = br.readLine()) != null) {
	            String[] parts = line.split(",", -1);
	            if (parts.length > 12) {
	                String id = parts[9].trim();
	                String title = parts[12].trim();
	                courses.add(new Course(id, title));
	            }
	        }
	    } catch (IOException e) {
	        System.err.println("⚠ Error reading course file: " + e.getMessage());
	    }
	    return courses;
	}
}