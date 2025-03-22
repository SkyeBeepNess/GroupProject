package models;

import java.util.List;

public class Admin {
    private String userID;
    private List<String> managedCourses;
    private boolean isSuper;
    public Admin(String userID, List<String> list, String role) {
        this.userID = userID;
        this.managedCourses = list;
        if (role.contentEquals("superadmin")) {
			this.isSuper = true;
		}
        else {
			this.isSuper = false;
		}
    }
    
    public String getUserID() {
		return userID;
	}
    public List<String> getManagedCourses() {
		return managedCourses;
	}
    public boolean getIsSuper() {
		// TODO Auto-generated method stub
    	return isSuper;
	}



}
