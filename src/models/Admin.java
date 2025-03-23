package models;

import java.util.List;

import session.UserSession;

public class Admin {	
    private String userID;
    private List<String> managedCourses;
    private List<String> managedUKPRN;
    private boolean isSuper;
    public Admin(String userID, List<String> courseList, String role) {
        this.userID = userID;
        this.managedCourses = courseList;
        if (role.contentEquals("superadmin")) {
			this.isSuper = true;
		}
        else {
			this.isSuper = false;
		}
    }
    
    public Admin(String userID, List<String> ukprnList) {
    	this.userID = userID;
    	this.managedUKPRN = ukprnList;
    	this.isSuper = false;
    }
    
    public Admin(String userID) {
    	this.userID = userID;
    	this.isSuper = true;
    }
    
    
    
    public String getUserID() {
		return userID;
	}
    public List<String> getManagedCourses() {
		return managedCourses;
	}
    public boolean getIsSuper() {
    	return isSuper;
	}
    public List<String> getManagedUKPRN(){
    	return managedUKPRN;
    }



}
